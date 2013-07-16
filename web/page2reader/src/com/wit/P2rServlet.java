package com.wit;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.appengine.api.datastore.EntityNotFoundException;
import com.wit.base.BaseConstants;
import com.wit.base.BaseServlet;
import com.wit.base.Log;
import com.wit.base.NotLoggedInException;
import com.wit.base.UserManager;
import com.wit.base.model.User;
import com.wit.page2reader.P2rConstants;
import com.wit.page2reader.P2rManager;
import com.wit.page2reader.P2rManager.PageUrlList;
import com.wit.page2reader.model.PageUrl;

@SuppressWarnings("serial")
public class P2rServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(P2rServlet.class
            .getName());

    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        BaseServlet.response(resp, "No pain. No gain.");
    }

    public void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        // Check logging in from sessionID in cookies
        String sessionKeyString = BaseServlet.getSSIDCookie(req).getValue();
        String sessionID = BaseServlet.getSIDCookie(req).getValue();

        // Get contents in request parameter.
        String methodName = req.getParameter(BaseServlet.METHOD);
        String content = req.getParameter(BaseServlet.CONTENT);

        if (sessionKeyString == null || sessionID == null || methodName == null
                || content == null) {
            logger.severe("Request parameters missing: sessionKeyString = "
                    + sessionKeyString + ", sessionID = " + sessionID
                    + ", methodName = " + methodName + ", content = " + content);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }

        User user = null;
        try {
            user = UserManager.checkLoggedInAndGetUser(sessionKeyString,
                    sessionID);
        } catch (NotLoggedInException e) {
            try {
                Log log = new Log();
                log.addLogInfo(BaseConstants.DID_LOG_IN, false, null, null);
                BaseServlet.response(resp, log.getJSONString());
            } catch (JSONException e1) {
                BaseServlet.writeExceptionToLogger(logger, e);
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
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
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } catch (IllegalAccessException e) {
            BaseServlet.writeExceptionToLogger(logger, e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } catch (InvocationTargetException e) {
            // All exceptions thrown by invoked methods will be wrapped
            // in this exception.
            BaseServlet.writeExceptionToLogger(logger, e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    public void addPageUrl(HttpServletResponse resp, User user,
            String content) throws IOException, JSONException, EntityNotFoundException {
        Log log = new Log();
        PageUrl pageUrl = P2rManager.addPageUrl(user, content, log);
        if (pageUrl != null) {
            // Create a task to fetch the url, cleanse it, embed images, and send to reader
            P2rManager.queuePageToReader(BServlet.FROM_EMAIL, BServlet.FROM_NAME, user, pageUrl,
                    log);
        }
        BaseServlet.response(resp, log.getJSONString());
    }

    public void deletePageUrl(HttpServletResponse resp, User user,
            String content) throws IOException, JSONException {
        Log log = new Log();
        P2rManager.deletePageUrl(content, log);
        BaseServlet.response(resp, log.getJSONString());
    }

    public void resendToReader(HttpServletResponse resp, User user,
            String content) throws IOException, JSONException, EntityNotFoundException {

        Log log = new Log();
        PageUrl pageUrl = P2rManager.getPageUrl(content);
        P2rManager.queuePageToReader(BServlet.FROM_EMAIL, BServlet.FROM_NAME, user, pageUrl,
                log);
        BaseServlet.response(resp, log.getJSONString());
    }

    public void getPagingPageUrls(HttpServletResponse resp, User user, String content)
            throws IOException {
        
        PageUrlList pageUrlList = P2rManager.getPagingPageUrls(user, content);

        JSONArray jsonPageUrls = new JSONArray();
        for (PageUrl pageUrl : pageUrlList) {
            jsonPageUrls.put(pageUrl.getJSONObject());
        }

        JSONObject jsonResult = new JSONObject();
        jsonResult.put(P2rConstants.PAGE_URLS, jsonPageUrls);
        jsonResult.put(P2rConstants.CURSOR_STRING, pageUrlList.getCursorString());
        
        Log log = new Log();
        log.addLogInfo(P2rConstants.GET_PAGING_PAGE_URLS, true, jsonResult.toString(), null);

        BaseServlet.response(resp, log.getJSONString());
    }

    public void updateReaderEmail(HttpServletResponse resp, User user,
            String content) throws IOException, JSONException {
        Log log = new Log();
        P2rManager.updateReaderEmail(user, content, log);
        BaseServlet.response(resp, log.getJSONString());
    }
}
