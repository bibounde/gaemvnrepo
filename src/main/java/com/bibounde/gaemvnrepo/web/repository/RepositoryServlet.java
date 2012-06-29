package com.bibounde.gaemvnrepo.web.repository;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tika.Tika;
import org.apache.tika.mime.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import com.bibounde.gaemvnrepo.model.File;
import com.bibounde.gaemvnrepo.server.service.util.ConfigurationUtil;
import com.bibounde.gaemvnrepo.shared.domain.repository.FileNavigationNode;
import com.bibounde.gaemvnrepo.shared.exception.BusinessException;
import com.bibounde.gaemvnrepo.shared.exception.TechnicalException;
import com.bibounde.gaemvnrepo.shared.service.RepositoryService;
import com.bibounde.gaemvnrepo.web.AbstractSpringServlet;

import freemarker.template.Configuration;
import freemarker.template.Template;

public class RepositoryServlet extends AbstractSpringServlet {

    private final static String REPO_PREFIX = "/repo/";
    
    @SuppressWarnings("unused")
    private static final Logger logger = LoggerFactory.getLogger(RepositoryServlet.class);

    @Autowired
    private RepositoryService repositoryService;
    
    private Configuration freeMarkerConfiguration;
    
    //Tools to detect content type because maven does not send it
    private Tika tika;
    
    @Override
    public void init() throws ServletException {
        this.freeMarkerConfiguration = new Configuration();
        this.freeMarkerConfiguration.setServletContextForTemplateLoading(getServletContext(), "WEB-INF/templates");
        
        this.tika = new Tika();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String filePath = this.getFilePath(req);
        
        logger.debug("Maven or web user attempt to retrieve {}", filePath);
        
        try {
            
            if ("".equals(filePath)) {
                logger.debug("Repository list is asked");
                List<String> repositories = this.repositoryService.getReposiroryNames();
                List<NavigationItem> items = new ArrayList<NavigationItem>();
                for (String name : repositories) {
                    NavigationItem item = new NavigationItem();
                    item.link = req.getRequestURI() + name + "/";
                    item.name = name;
                    items.add(item);
                }
                this.computeTemplate(items, resp);
                resp.setStatus(HttpServletResponse.SC_OK);
            } else {
                String repoName = this.getRepositoryName(filePath);
                
                if (filePath.equals(repoName)) {
                    logger.debug("Root of {} repository asked. Returns list", repoName);
                    List<FileNavigationNode> nodes = this.repositoryService.getFileNavigationNodes(repoName, 0);
                    this.computeTemplate(this.getNavigationItem(filePath, nodes), resp);
                } else {
                    File file = this.repositoryService.findFileByPath(this.getRepositoryName(filePath), filePath);
                    
                    if (file == null) {
                        logger.debug("{} not found", filePath);
                        resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    } else {
                        if (file.isFile()) {
                            logger.debug("{} file found. Returns content", filePath);
                            byte[] bytes = file.getContent();
                            if (bytes == null) {
                                logger.error("Unable to find content of {} with blobkey", filePath, file.getContentKey().getKeyString());
                                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                            } else {
                                resp.setContentLength(file.getContent().length);
                                resp.getOutputStream().write(file.getContent());
                                resp.setStatus(HttpServletResponse.SC_OK);
                            }
                            
                            
                        } else {
                            logger.debug("{} directory found. Returns list", filePath);
                            List<FileNavigationNode> nodes = this.repositoryService.getFileNavigationNodes(filePath, StringUtils.countOccurrencesOf(filePath, "/"));
                            this.computeTemplate(this.getNavigationItem(filePath, nodes), resp);
                            resp.setStatus(HttpServletResponse.SC_OK);
                            
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Unable to process file", e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
    
    private List<NavigationItem> getNavigationItem(String filePath, List<FileNavigationNode> nodes) {
        List<NavigationItem> ret = new ArrayList<NavigationItem>();
        NavigationItem backItem = new NavigationItem();
        backItem.name = "..";
        String backPath = filePath.contains("/") ? filePath.substring(0, filePath.lastIndexOf("/")) + "/" : "";
        backItem.link = REPO_PREFIX + backPath;
        ret.add(backItem);
        
        for (FileNavigationNode node : nodes) {
            NavigationItem item = new NavigationItem();
            item.name = node.name;
            item.link = REPO_PREFIX + node.path + (node.file ? "" : "/");
            ret.add(item);
        }
        return ret;
    }
    
    private void computeTemplate(List<NavigationItem> items, HttpServletResponse response) throws Exception {
        Template template = this.freeMarkerConfiguration.getTemplate("navigation.ftl");
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("items", items);
        model.put("caption", ConfigurationUtil.INSTANCE.getCaption());
        template.process(model, response.getWriter());
        response.getWriter().flush();
    }

    @Override
    protected void doHead(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        logger.info("Do head");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        logger.info("Do post");
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String reqFilePath = this.getFilePath(req);
        logger.debug("Maven attempt to put {}", reqFilePath);
        
        if (req.getContentLength() > 0) {
            ByteArrayOutputStream output = null;
            try {
                //Creates content
                output = new ByteArrayOutputStream();
                InputStream input = req.getInputStream();
                
                byte[] buffer = new byte[1024];
                for (int length = 0; (length = input.read(buffer)) > -1;){
                    output.write(buffer, 0, length);
                }
                output.flush();
                byte[] content = output.toByteArray();
                
                String contentType = tika.detect(Arrays.copyOf(content, content.length < 1024 ? content.length : 1024), reqFilePath.substring(reqFilePath.lastIndexOf("/")));
                
                this.repositoryService.uploadFile(this.getRepositoryName(reqFilePath), reqFilePath, content, contentType);
                
                logger.debug("{} successfully uploaded", reqFilePath);
                resp.setStatus(HttpServletResponse.SC_OK);
            } catch (TechnicalException e) {
                logger.error("Unable to execute upload operation", e);
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            } catch (BusinessException e) {
                logger.error("Unable to execute upload operation", e);
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            } finally {
                if (output != null) {
                    output.close();
                }
            }
        } else {
            logger.error("No content found");
            resp.setStatus(HttpServletResponse.SC_LENGTH_REQUIRED);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        logger.info("Do delete");
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        logger.info("Do options");
    }

    @Override
    protected void doTrace(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        logger.info("Do trace");
    }
    
    /**
     * Retrieves file path form request
     * @param req http request
     * @return file path
     */
    private String getFilePath(HttpServletRequest req) {
        String filePath = req.getRequestURI().replace(REPO_PREFIX, ""); 
        return filePath.endsWith("/") ? filePath.substring(0, filePath.length() - 1) : filePath;
    }
    
    /**
     * Retrieves repository name from file path
     * @param filePath file path
     * @return repository name
     */
    private String getRepositoryName(String filePath) {
        String[] splitted = filePath.split("/");
        return splitted[0];
    }
    
    public static class NavigationItem implements Serializable {

        public String link, name;

        /**
         * @return the link
         */
        public String getLink() {
            return link;
        }

        /**
         * @return the name
         */
        public String getName() {
            return name;
        }
    }

}
