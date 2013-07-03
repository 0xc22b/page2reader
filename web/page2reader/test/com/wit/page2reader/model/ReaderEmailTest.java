package com.wit.page2reader.model;

import static org.junit.Assert.assertEquals;

import java.io.UnsupportedEncodingException;

import javax.mail.MessagingException;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalMailServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalMemcacheServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.wit.Reflector;
import com.wit.base.Log;
import com.wit.base.UserManager;
import com.wit.base.model.User;
import com.wit.page2reader.P2rConstants;
import com.wit.page2reader.P2rManager;

public class ReaderEmailTest {

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
    private Key p2rGrpKey;

    @Before
    public void setUp() throws UnsupportedEncodingException,
                               MessagingException {
        helper.setUp();

        Log log = new Log();
        user = UserManager.signUp("wit", "wit@gmail.c", "raknaja2",
                "raknaja2", log);

        @SuppressWarnings("rawtypes")
        Class[] argClasses = {User.class};
        Object[] argObjects = {user};
        p2rGrpKey = (Key) Reflector.invokePrivateMethod(P2rManager.class,
                "getP2rGrpKey", argClasses, null, argObjects);
    }

    @After
    public void tearDown() {
        helper.tearDown();
    }

    @Test
    public void testReaderEmail() throws JSONException {

        Key readerEmailKey = ReaderEmail.createKey(p2rGrpKey);
        ReaderEmail readerEmail = new ReaderEmail(readerEmailKey);

        readerEmail.setREmail("test@mail.com");

        DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
        ds.put(readerEmail.getEntity());

        assertEquals(readerEmailKey, readerEmail.getKey());
        assertEquals("test@mail.com", readerEmail.getREmail());

        JSONObject jsonObj1 = readerEmail.getJSONObject();
        assertEquals(KeyFactory.keyToString(readerEmailKey),
                jsonObj1.getString(P2rConstants.KEY_STRING));
        assertEquals("test@mail.com", jsonObj1.getString(P2rConstants.R_EMAIL));
    }
}
