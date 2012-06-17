package com.bibounde.gaemvnrepo.web.cron;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bibounde.gaemvnrepo.shared.exception.BusinessException;
import com.bibounde.gaemvnrepo.shared.exception.TechnicalException;
import com.bibounde.gaemvnrepo.web.AbstractSpringServlet;

public abstract class AbstractCronServlet extends AbstractSpringServlet{

    private static final String GAE_CRON_HEADER = "X-AppEngine-Cron";
    
    @SuppressWarnings("unused")
    private static final Logger logger = LoggerFactory.getLogger(AbstractCronServlet.class);

    
    /* (non-Javadoc)
     * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String gaeCronHeaderValue = req.getHeader(GAE_CRON_HEADER);
        if (gaeCronHeaderValue != null && Boolean.valueOf(gaeCronHeaderValue).booleanValue()) {
            try {
                this.execute();
            } catch (Exception e) {
                logger.error("Unable to perform cron operation", e);
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        } else {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }

    /**
     * Performs execution
     * @throws TechnicalException
     * @throws BusinessException 
     */
    protected abstract void execute() throws TechnicalException, BusinessException;
    
    
}
