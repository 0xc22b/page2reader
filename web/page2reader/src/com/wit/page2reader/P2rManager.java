package com.wit.page2reader;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;

import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.QueryResultList;
import com.google.appengine.api.datastore.Transaction;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.wit.base.BaseConstants;
import com.wit.base.Log;
import com.wit.base.UserVerifier;
import com.wit.base.model.User;
import com.wit.page2reader.model.P2rGrp;
import com.wit.page2reader.model.PageUrl;
import com.wit.page2reader.model.ReaderEmail;

public class P2rManager {

    @SuppressWarnings("serial")
    public static class PageUrlList extends ArrayList<PageUrl> {

        private String cursorString;

        public String getCursorString() {
            return cursorString;
        }

        public void setCursorString(String cursorString) {
            this.cursorString = cursorString;
        }
    }

    public static final int PAGE_SIZE = 30;

    public static PageUrlList getPagingPageUrls(User user, String cursorString) {

        PageUrlList pageUrls = new PageUrlList();

        DatastoreService ds = DatastoreServiceFactory.getDatastoreService();

        FetchOptions fetchOptions = FetchOptions.Builder.withLimit(PAGE_SIZE);
        if (cursorString != null) {
            fetchOptions.startCursor(Cursor.fromWebSafeString(cursorString));
        }

        Query query = new Query(PageUrl.class.getSimpleName());
        query.setAncestor(getP2rGrpKey(user));
        query.addSort(BaseConstants.SERVER_CREATE_DATE, SortDirection.DESCENDING);

        PreparedQuery preparedQuery = ds.prepare(query);
        QueryResultList<Entity> results = preparedQuery.asQueryResultList(fetchOptions);
        for (Entity entity : results) {
            PageUrl pageUrl = new PageUrl(entity);
            pageUrls.add(pageUrl);
        }

        String nextCursorString = results.getCursor().toWebSafeString();
        pageUrls.setCursorString(nextCursorString);

        return pageUrls;
    }

    public static void addPageUrl(User user, String pUrl, Log log) {

        // TODO: Validate page url


        Key p2rGrpKey = getP2rGrpKey(user);
        PageUrl pageUrl = new PageUrl(p2rGrpKey, pUrl);

        // Save to DB
        DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
        for (int i = 0; i < 10; i++) {
            try {
                Transaction txn = ds.beginTransaction();
                try {
                    ds.put(txn, pageUrl.getEntity());

                    // Throws java.util.ConcurrentModificationException
                    // if entity group was modified by other thread
                    txn.commit();

                    // TODO: Create a job to retrive the page, cleanse it, embed images, and send to reader

                    log.addLogInfo(P2rConstants.ADD_PAGE_URL, true,
                            pageUrl.getJSONObject().toString(), null);
                    return;
                } finally {
                    if (txn.isActive()) {
                        txn.rollback();
                    }
                }
            } catch (ConcurrentModificationException e) {
                // stay in the loop and try again.
            }
            // you could use another backoff algorithm here rather than 100ms each time.
            try { Thread.sleep(100); } catch (InterruptedException e) {}
        }
        log.addLogInfo(P2rConstants.ADD_PAGE_URL, false, null, null);
    }

    public static void editPageUrl(PageUrl pageUrl) {
        
    }

    public static void deletePageUrl(String keyString, Log log) {
        Key pageUrlKey = KeyFactory.stringToKey(keyString);
        DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
        for (int i = 0; i < 10; i++) {
            try {
                Transaction txn = ds.beginTransaction();
                try {
                    ds.delete(txn, pageUrlKey);

                    // Throws java.util.ConcurrentModificationException
                    // if entity group was modified by other thread
                    txn.commit();

                    log.addLogInfo(P2rConstants.DELETE_PAGE_URL, true, null, null);
                    return;
                } finally {
                    if (txn.isActive()) {
                        txn.rollback();
                    }
                }
            } catch (ConcurrentModificationException e) {
                // stay in the loop and try again.
            }
            // you could use another backoff algorithm here rather than 100ms each time.
            try { Thread.sleep(100); } catch (InterruptedException e) {}
        }
        log.addLogInfo(P2rConstants.DELETE_PAGE_URL, false, null, null);
    }

    public static ReaderEmail getReaderEmail(User user) {

        Key p2rGrpKey = getP2rGrpKey(user);
        Key readerEmailKey = ReaderEmail.createKey(p2rGrpKey);

        DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
        try {
            Entity entity = ds.get(readerEmailKey);
            return new ReaderEmail(entity);
        } catch (EntityNotFoundException e) {
            return null;
        }
    }

    public static void updateReaderEmail(User user, String newREmail, Log log) {
        // Validate new reader email
        UserVerifier.isEmailValid(newREmail, false, log);
        if (!log.isValid()) {
            return;
        }

        ReaderEmail readerEmail = getReaderEmail(user);
        if (readerEmail == null) {
            Key p2rGrpKey = getP2rGrpKey(user);
            Key readerEmailKey = ReaderEmail.createKey(p2rGrpKey);

            readerEmail = new ReaderEmail(readerEmailKey);
        }

        readerEmail.setREmail(newREmail);

        // Save to DB
        DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
        for (int i = 0; i < 10; i++) {
            try {
                Transaction txn = ds.beginTransaction();
                try {
                    ds.put(txn, readerEmail.getEntity());

                    // Throws java.util.ConcurrentModificationException
                    // if entity group was modified by other thread
                    txn.commit();

                    log.addLogInfo(P2rConstants.UPDATE_READER_EMAIL, true, null, null);
                    return;
                } finally {
                    if (txn.isActive()) {
                        txn.rollback();
                    }
                }
            } catch (ConcurrentModificationException e) {
                // stay in the loop and try again.
            }
            // you could use another backoff algorithm here rather than 100ms each time.
            try { Thread.sleep(100); } catch (InterruptedException e) {}
        }
        log.addLogInfo(P2rConstants.UPDATE_READER_EMAIL, false, null, null);
    }

    protected static Key getP2rGrpKey(User user) {

        Key p2rGrpKey = P2rGrp.createKey(user);
        String p2rGrpKeyString = KeyFactory.keyToString(p2rGrpKey);

        // Try to get from memcache first.
        MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
        if (!syncCache.contains(p2rGrpKeyString)) {
            // Not found in memcache, try to get from DB.
            DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
            try {
                ds.get(p2rGrpKey);
            } catch (EntityNotFoundException e) {
                // Not found in DB, create a new one.
                P2rGrp p2rGrp = new P2rGrp(p2rGrpKey);
                ds.put(p2rGrp.getEntity());
            }
            syncCache.put(p2rGrpKeyString, null);
        }
        return p2rGrpKey;
    }
}
