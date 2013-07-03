package com.wit;

import java.io.IOException;

import org.json.JSONException;

import com.google.template.soy.data.SoyMapData;
import com.wit.base.BaseServlet;
import com.wit.base.model.User;
import com.wit.page2reader.P2rConstants;
import com.wit.page2reader.P2rManager;
import com.wit.page2reader.P2rManager.PageUrlList;
import com.wit.page2reader.model.PageUrl;

@SuppressWarnings("serial")
public class BServlet extends BaseServlet {

    @Override
    public String getWebUrl() {
        return "http://page2reader.appspot.com";
    }

    @Override
    public String getWebName() {
        return "Page2Reader";
    }

    @Override
    public String getFromEmail() {
        return "admin@page2reader.appspot.com";
    }

    @Override
    public String getFromName() {
        return "Page2Reader admin";
    }

    @Override
    public String getWorkContents(User user, int mode, int device) throws JSONException,
            IOException {

        String[] soyFileList = {getBaseSoyPath(), getWorkSoyPath()};
        SoyMapData soyMapData = null;

        String pageUrlViews = "";
        PageUrlList pageUrls = P2rManager.getPagingPageUrls(user, null);
        if (pageUrls.size() == 0) {
            pageUrlViews = BaseServlet.getTemplate(
                    soyFileList,
                    "wit.page2reader.soy.p2r.noEntry",
                    getWorkCssRenamingMapFile(device, mode),
                    soyMapData);
        } else {
            for (PageUrl pageUrl : pageUrls) {
                soyMapData = new SoyMapData(
                        P2rConstants.KEY_STRING, pageUrl.getKeyString(),
                        P2rConstants.P_URL, pageUrl.getPUrl(),
                        P2rConstants.TITLE, pageUrl.getTitle(),
                        P2rConstants.TEXT, pageUrl.getText());
                pageUrlViews += BaseServlet.getTemplate(
                        soyFileList,
                        "wit.page2reader.soy.p2r.pageUrlView",
                        getWorkCssRenamingMapFile(device, mode),
                        soyMapData);
            }
        }

        soyMapData = new SoyMapData(
                        P2rConstants.PAGE_URL_VIEWS, pageUrlViews,
                        P2rConstants.CURSOR_STRING, pageUrls.getCursorString());
        String content = BaseServlet.getTemplate(
                soyFileList,
                "wit.page2reader.soy.p2r.content",
                getWorkCssRenamingMapFile(device, mode),
                soyMapData);

        return content;
    }

    @Override
    public void deleteAllUserContents(User user) {

    }

    @Override
    public String getResetPasswordEmailSubject() {
        return super.getResetPasswordEmailSubject();
    }

    @Override
    public String getResetPasswordEmailMsgBody() {
        return super.getResetPasswordEmailMsgBody();
    }

    @Override
    public String getConfirmEmailSubject() {
        return super.getConfirmEmailSubject();
    }

    @Override
    public String getConfirmEmailMsgBody() {
        return super.getConfirmEmailMsgBody();
    }

    @Override
    public String getBaseSoyPath() {
        return "closure/wit/base/soy/base.soy";
    }

    @Override
    public String getHomeSoyPath() {
        return "closure/wit/home/soy/home.soy";
    }

    @Override
    public String getHomeSoyMethod(int device, int mode) {
        if (mode == 1) {
            return "wit.home.soy.home.initDebug";
        } else {
            return "wit.home.soy.home.init";
        }
    }

    @Override
    public String getHomeCssRenamingMapFile(int device, int mode) {
        if (mode == 1) {
            return "cssrenamingmap/home-debug.properties";
        } else {
            return "cssrenamingmap/home-compiled.properties";
        }
    }

    @Override
    public String getWorkSoyPath() {
        return "closure/wit/page2reader/soy/p2r.soy";
    }

    @Override
    public String getWorkSoyMethod(int device, int mode) {
        if (mode == 1) {
            return "wit.page2reader.soy.p2r.initDebug";
        } else {
            return "wit.page2reader.soy.p2r.init";
        }
    }

    @Override
    public String getWorkCssRenamingMapFile(int device, int mode) {
        if (mode == 1) {
            return "cssrenamingmap/p2r-debug.properties";
        } else {
            return "cssrenamingmap/p2r-compiled.properties";
        }
    }

    @Override
    public String getAboutSoyPath() {
        return "closure/wit/home/soy/about.soy";
    }

    @Override
    public String getAboutSoyMethod(int device, int mode) {
        if (mode == 1) {
            return "wit.home.soy.about.initDebug";
        } else {
            return "wit.home.soy.about.init";
        }
    }

    @Override
    public String getTermsSoyPath() {
        return "closure/wit/home/soy/terms.soy";
    }

    @Override
    public String getTermsSoyMethod(int device, int mode) {
        if (mode == 1) {
            return "wit.home.soy.terms.initDebug";
        } else {
            return "wit.home.soy.terms.init";
        }
    }

    @Override
    public String getFeedbackSoyPath() {
        return "closure/wit/home/soy/feedback.soy";
    }

    @Override
    public String getFeedbackSoyMethod(int device, int mode) {
        if (mode == 1) {
            return "wit.home.soy.feedback.initDebug";
        } else {
            return "wit.home.soy.feedback.init";
        }
    }

    @Override
    public String getConfirmEmailSoyPath() {
        return "closure/wit/home/soy/confirmemail.soy";
    }

    @Override
    public String getConfirmEmailSoyMethod(int device, int mode) {
        if (mode == 1) {
            return "wit.home.soy.confirmEmail.initDebug";
        } else {
            return "wit.home.soy.confirmEmail.init";
        }
    }

    @Override
    public String getResetPasswordSoyPath() {
        return "closure/wit/home/soy/resetpassword.soy";
    }

    @Override
    public String getResetPasswordSoyMethod(int device, int mode) {
        if (mode == 1) {
            return "wit.home.soy.resetPassword.initDebug";
        } else {
            return "wit.home.soy.resetPassword.init";
        }
    }

    @Override
    public String getUserSoyPath() {
        return "closure/wit/user/soy/user.soy";
    }

    @Override
    public String getUserSoyMethod(int device, int mode) {
        if (mode == 1) {
            return "wit.user.soy.user.initDebug";
        } else {
            return "wit.user.soy.user.init";
        }
    }

    @Override
    public String getUserCssRenamingMapFile(int device, int mode) {
        if (mode == 1) {
            return "cssrenamingmap/user-debug.properties";
        } else {
            return "cssrenamingmap/user-compiled.properties";
        }
    }
}
