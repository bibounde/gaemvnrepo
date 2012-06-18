package com.bibounde.gaemvnrepo.web.queue;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bibounde.gaemvnrepo.shared.exception.BusinessException;
import com.bibounde.gaemvnrepo.shared.exception.TechnicalException;
import com.bibounde.gaemvnrepo.web.AbstractSpringServlet;

public abstract class AbstractQueueServlet extends AbstractSpringServlet{

    private static final String GAE_HEADER_QUEUE_NAME = "X-AppEngine-QueueName";
    private static final String GAE_HEADER_TASK_NAME = "X-AppEngine-TaskName";
    private static final String GAE_HEADER_RETRY_COUNT = "X-AppEngine-TaskRetryCount";
    
    @SuppressWarnings("unused")
    private static final Logger logger = LoggerFactory.getLogger(AbstractQueueServlet.class);

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String queueName = req.getHeader(GAE_HEADER_QUEUE_NAME);
        String taskName = req.getHeader(GAE_HEADER_TASK_NAME);
        String retryCount = req.getHeader(GAE_HEADER_RETRY_COUNT);
        
        logger.debug("{} calls task {} for the {} time", new Object[]{queueName, taskName, retryCount});
        
        if (queueName != null && taskName != null && retryCount != null) {
            try {
                this.execute(req.getParameterMap());
            } catch (Exception e) {
                logger.error("Unable to perform queue operation", e);
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        } else {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }

    /**
     * Performs execution
     * @param parameterMap
     * @throws TechnicalException
     * @throws BusinessException 
     */
    protected abstract void execute(Map parameterMap) throws TechnicalException, BusinessException;
    
    
}
