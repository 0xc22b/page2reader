package com.wit.page2reader;

import static org.junit.Assert.assertEquals;

import java.io.UnsupportedEncodingException;

import javax.mail.MessagingException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalMailServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalMemcacheServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.wit.LogHelper;
import com.wit.base.Log;
import com.wit.base.UserManager;
import com.wit.base.model.User;
import com.wit.page2reader.P2rManager.PageUrlList;
import com.wit.page2reader.model.PageUrl;
import com.wit.page2reader.model.ReaderEmail;

public class P2rManagerTest {

    // By setting the unapplied job percentage to 100, we are instructing
    // the local datastore to operate with the maximum amount of eventual
    // consistency. Maximum eventual consistency means writes will commit
    // but always fail to apply, so global (non-ancestor) queries
    // will consistently fail to see changes.
    private final LocalServiceTestHelper helper = new LocalServiceTestHelper(
            new LocalDatastoreServiceTestConfig()
                    .setDefaultHighRepJobPolicyUnappliedJobPercentage(100),
            new LocalMemcacheServiceTestConfig(),
            new LocalMailServiceTestConfig());

    private User user;

    @Before
    public void setUp() throws UnsupportedEncodingException, MessagingException {
        helper.setUp();

        Log log = new Log();
        user = UserManager.signUp("wit", "wit@gmail.c", "raknaja2",
                "raknaja2", log);
    }

    @After
    public void tearDown() {
        helper.tearDown();
    }

    @Test
    public void testUpdatePageUrl() {
        Log log = new Log();
        P2rManager.addPageUrl(user, "http://www.google.com/ncr", log);

        PageUrlList pageUrls = P2rManager.getPagingPageUrls(user, null);
        assertEquals(1, pageUrls.size());

        PageUrl pageUrl = pageUrls.get(0);
        assertEquals("http://www.google.com/ncr", pageUrl.getPUrl());

        P2rManager.addPageUrl(user, "http://www.yahoo.com", log);

        pageUrls = P2rManager.getPagingPageUrls(user, null);
        assertEquals(2, pageUrls.size());

        pageUrl = pageUrls.get(0);
        assertEquals("http://www.yahoo.com", pageUrl.getPUrl());

        pageUrl = pageUrls.get(1);
        assertEquals("http://www.google.com/ncr", pageUrl.getPUrl());
    }

    @Test
    public void testUpdateReaderEmail() {
        Log log = new Log();
        P2rManager.updateReaderEmail(user, "test@mail.com", log);

        ReaderEmail readerEmail = P2rManager.getReaderEmail(user);
        assertEquals("test@mail.com", readerEmail.getREmail());
        assertEquals(true, log.isValid());
        assertEquals(true, LogHelper.isLogInfoTypeValid(log, P2rConstants.UPDATE_READER_EMAIL));

        P2rManager.updateReaderEmail(user, "testagain@mail.com", log);

        readerEmail = P2rManager.getReaderEmail(user);
        assertEquals("testagain@mail.com", readerEmail.getREmail());
        assertEquals(true, log.isValid());
        assertEquals(true, LogHelper.isLogInfoTypeValid(log, P2rConstants.UPDATE_READER_EMAIL));
    }
}
