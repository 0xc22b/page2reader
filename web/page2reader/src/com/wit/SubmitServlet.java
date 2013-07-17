package com.wit;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Logger;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.EntityNotFoundException;
import com.wit.base.BaseServlet;
import com.wit.base.Log;
import com.wit.base.NotLoggedInException;
import com.wit.base.UserManager;
import com.wit.base.model.User;
import com.wit.page2reader.P2rManager;
import com.wit.page2reader.model.PageUrl;

@SuppressWarnings("serial")
public class SubmitServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(SubmitServlet.class
            .getName());

    public static final String NOT_LOGGED_IN = "page2reader:Please sign in first";
    public static final String SENT = "page2reader:Sent";
    public static final String ERROR = "page2reader:Error! Please try again";

    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        BaseServlet.response(resp, "No pain. No gain.");
    }
    
    public void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        // Check logging in from sessionID in cookies
        Cookie sSIDCookie = BaseServlet.getSSIDCookie(req);
        Cookie sIDCookie = BaseServlet.getSIDCookie(req);

        // Get contents in request parameter.
        String methodName = req.getParameter(BaseServlet.METHOD);
        String content = req.getParameter(BaseServlet.CONTENT);

        if (sSIDCookie == null || sIDCookie == null) {
            responseWithPostMessage(resp, NOT_LOGGED_IN);
            return;
        }

        String sessionKeyString = sSIDCookie.getValue();
        String sessionID = sIDCookie.getValue();

        if (sessionKeyString == null || sessionKeyString.isEmpty()
                || sessionID == null || sessionID.isEmpty()
                || methodName == null || methodName.isEmpty()
                || content == null || content.isEmpty()) {
            logger.severe("Request parameters missing: sessionKeyString = "
                    + sessionKeyString + ", sessionID = " + sessionID
                    + ", methodName = " + methodName + ", content = " + content);
            responseWithPostMessage(resp, ERROR);
            return;
        }

        User user = null;
        try {
            user = UserManager.checkLoggedInAndGetUser(sessionKeyString,
                    sessionID);
        } catch (NotLoggedInException e) {
            responseWithPostMessage(resp, NOT_LOGGED_IN);
            return;
        }

        try {
            @SuppressWarnings("rawtypes")
            Class[] argClasses = { HttpServletResponse.class, User.class,
                    String.class };
            Object[] argObjects = {resp, user, content};

            Method method = getClass().getMethod(methodName, argClasses);
            method.invoke(this, argObjects);
        } catch (NoSuchMethodException e) {
            BaseServlet.writeExceptionToLogger(logger, e);
            responseWithPostMessage(resp, ERROR);
        } catch (IllegalAccessException e) {
            BaseServlet.writeExceptionToLogger(logger, e);
            responseWithPostMessage(resp, ERROR);
        } catch (InvocationTargetException e) {
            // All exceptions thrown by invoked methods will be wrapped
            // in this exception.
            BaseServlet.writeExceptionToLogger(logger, e);
            responseWithPostMessage(resp, ERROR);
        }
    }

    /**
     * Add a page URL with form element (targetting to iFrame) used by bookmarklet, extensions.
     * The POST result must call window.postMessage to let the caller in the same window
     *     but not in the iFrame act upon submitting completed.
     */
    public void submitPageUrl(HttpServletResponse resp, User user,
            String content) throws EntityNotFoundException, IOException {
        Log log = new Log();
        PageUrl pageUrl = P2rManager.addPageUrl(user, content, log);
        if (pageUrl != null) {
            // Create a task to fetch the url, cleanse it, embed images, and send to reader
            P2rManager.queuePageToReader(BServlet.FROM_EMAIL, BServlet.FROM_NAME, user, pageUrl,
                    log);
            responseWithPostMessage(resp, SENT);
        } else {
            responseWithPostMessage(resp, ERROR);
        }
    }

    private void responseWithPostMessage(HttpServletResponse resp, String msg) throws IOException {
        String result = "<script>"
                + "(function() {"
                + "var result = '" + msg + "';"
                + "window.parent.postMessage(result, '*');"
                + "}());"
                + "</script>";

        // This result is in iFrame so couldn't call BaseServlet.response();
        resp.setContentType("text/html; charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();
        out.print(result);
        out.flush();
        out.close();
    }
}
