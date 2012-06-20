package com.bibounde.gaemvnrepo.web.repository;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.bibounde.gaemvnrepo.model.File;
import com.bibounde.gaemvnrepo.shared.exception.BusinessException;
import com.bibounde.gaemvnrepo.shared.exception.TechnicalException;
import com.bibounde.gaemvnrepo.shared.service.RepositoryService;
import com.bibounde.gaemvnrepo.web.AbstractSpringServlet;

public class RepositoryServlet extends AbstractSpringServlet {

    private final static String REPO_PREFIX = "/repo/";

    @SuppressWarnings("unused")
    private static final Logger logger = LoggerFactory.getLogger(RepositoryServlet.class);

    
    @Autowired
    private RepositoryService repositoryService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String filePath = this.getFilePath(req);
        
        logger.debug("Maven attempt to retrieve {}", filePath);
        
        try {
            File file = this.repositoryService.findFileByPath(this.getRepositoryName(filePath), filePath);
            
            if (file == null) {
                logger.debug("{} not found", filePath);
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            } else {
                if (file.getContent() == null) {
                    logger.warn("File content is null for {}", filePath);
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                } else {
                    resp.setContentType(file.getMime());
                    resp.setContentLength(file.getContent().length);
                    resp.getOutputStream().write(file.getContent());
                    resp.setStatus(HttpServletResponse.SC_OK);
                    
                    logger.debug("{} with content found", filePath);
                }
            }
        } catch (TechnicalException e) {
            logger.error("Unable to find file", e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
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
                byte[] content = output.toByteArray();

                this.repositoryService.uploadFile(this.getRepositoryName(reqFilePath), reqFilePath, content, req.getContentType());
                
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
        return req.getRequestURI().replace(REPO_PREFIX, "");
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

}
