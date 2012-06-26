package com.bibounde.gaemvnrepo.web.migration;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.bibounde.gaemvnrepo.shared.exception.TechnicalException;
import com.bibounde.gaemvnrepo.shared.service.MigrationService;

public class MigrationFilter implements Filter {

    private static final String MIGRATION_EXCEPTION = "/migration";
    private static final String VAADIN_EXCEPTION = "/VAADIN";
    private static final String TASKS_EXCEPTION = "/tasks";
    private static final String AH_EXCEPTION = "/_ah";
    
    private static final String REPO_URI = "/repo";

    @SuppressWarnings("unused")
    private static final Logger logger = LoggerFactory.getLogger(MigrationFilter.class);

    @Autowired
    private MigrationService migrationService;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        logger.debug("Init filter");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = ((HttpServletRequest) request);
        HttpServletResponse httpResponse = ((HttpServletResponse) response);
        String requestUri = httpRequest.getRequestURI();
        logger.debug("Perform filtering operation on {} [{}]", requestUri, httpRequest.getMethod());

        try {
            if (migrationService.isLocked()) {
                if (!requestUri.startsWith(MIGRATION_EXCEPTION) && !requestUri.startsWith(VAADIN_EXCEPTION) && !requestUri.startsWith(TASKS_EXCEPTION)
                        && !requestUri.startsWith(AH_EXCEPTION)) {
                    logger.debug("Application locked");
                    if (requestUri.startsWith(REPO_URI)) {
                        logger.debug("Return 503 for maven");
                        httpResponse.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
                    } else {
                        logger.debug("Redirect to migration page");
                        ((HttpServletResponse) response).sendRedirect("/migration");
                    }
                } else {
                    logger.debug("Exception uri. Continue...");
                    chain.doFilter(request, response);
                }
            } else {
                logger.debug("No migration. Continue...");
                chain.doFilter(request, response);
            }
        } catch (TechnicalException e) {
            logger.error("Unable to perform filter", e);
            httpResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public void destroy() {
        logger.debug("Destroy filter");
    }

}
