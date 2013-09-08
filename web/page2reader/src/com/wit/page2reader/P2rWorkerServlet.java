package com.wit.page2reader;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.wit.base.BaseServlet;

@SuppressWarnings("serial")
public class P2rWorkerServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(P2rWorkerServlet.class
            .getName());

    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        BaseServlet.response(resp, "No pain. No gain.");
    }

    public void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        // Get contents in request parameter.
        String fromEmail = req.getParameter(P2rConstants.FROM_EMAIL);
        String fromName = req.getParameter(P2rConstants.FROM_NAME);
        String toEmail = req.getParameter(P2rConstants.TO_EMAIL);
        String toName = req.getParameter(P2rConstants.TO_NAME);
        String pageUrlKeyString = req.getParameter(P2rConstants.PAGE_URL_KEY_STRING);

        P2rManager.pageToReader(fromEmail, fromName, toEmail, toName, pageUrlKeyString,
                    logger);

        resp.setStatus(HttpServletResponse.SC_OK);
    }
}
