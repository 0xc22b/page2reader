package com.wit.base;

import com.wit.base.UserManager;

import java.io.IOException;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class MailHandlerServlet extends HttpServlet { 

    public void doPost(HttpServletRequest req, HttpServletResponse resp) 
            throws IOException { 
        Properties props = new Properties(); 
        Session session = Session.getDefaultInstance(props, null); 
        try {
            MimeMessage msg = new MimeMessage(session, req.getInputStream());
            UserManager.receiveEmail(msg);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
