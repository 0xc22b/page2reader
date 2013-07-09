package com.wit.page2reader;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

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
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.urlfetch.HTTPHeader;
import com.google.appengine.api.urlfetch.HTTPMethod;
import com.google.appengine.api.urlfetch.HTTPRequest;
import com.google.appengine.api.urlfetch.HTTPResponse;
import com.google.appengine.api.urlfetch.URLFetchServiceFactory;
import com.wit.base.BaseConstants;
import com.wit.base.Log;
import com.wit.base.UserManager;
import com.wit.base.UserVerifier;
import com.wit.base.model.User;
import com.wit.base.model.UserUname;
import com.wit.page2reader.model.P2rGrp;
import com.wit.page2reader.model.PageUrl;
import com.wit.page2reader.model.ReaderEmail;

import de.jetwick.snacktory.ArticleTextExtractor;
import de.jetwick.snacktory.JResult;

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

    public static PageUrl addPageUrl(User user, String pUrl, Log log) {

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

                    log.addLogInfo(P2rConstants.ADD_PAGE_URL, true,
                            pageUrl.getJSONObject().toString(), null);
                    return pageUrl;
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
        log.addLogInfo(P2rConstants.ADD_PAGE_URL, false, pageUrl.getJSONObject().toString(),
                BaseConstants.CONCURRENT_MODIFICATION_EXCEPTION);
        return null;
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

                    log.addLogInfo(P2rConstants.DELETE_PAGE_URL, true, keyString, null);
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
        log.addLogInfo(P2rConstants.DELETE_PAGE_URL, false, keyString,
                BaseConstants.CONCURRENT_MODIFICATION_EXCEPTION);
    }

    private static PageUrl getPageUrl(String keyString) {

        Key pageUrlKey = KeyFactory.stringToKey(keyString);

        DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
        try {
            Entity entity = ds.get(pageUrlKey);
            return new PageUrl(entity);
        } catch (EntityNotFoundException e) {
            return null;
        }
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

                    log.addLogInfo(P2rConstants.UPDATE_READER_EMAIL, true, readerEmail.getREmail(),
                            null);
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
        log.addLogInfo(P2rConstants.UPDATE_READER_EMAIL, false, readerEmail.getREmail(),
                BaseConstants.CONCURRENT_MODIFICATION_EXCEPTION);
    }

    public static void queuePageToReader(String fromEmail, String fromName, User user,
            PageUrl pageUrl, Log log) throws EntityNotFoundException {

        ReaderEmail readerEmail = P2rManager.getReaderEmail(user);
        if (readerEmail != null) {

            UserUname userUname = UserManager.getUserUname(user.getUserUnameGrpKey(),
                    user.getKey());

            String toEmail = readerEmail.getREmail();
            String toName = userUname.getUsername();

            Queue queue = QueueFactory.getDefaultQueue();
            queue.add(TaskOptions.Builder.withUrl("/worker/p2r")
                    .param(P2rConstants.FROM_EMAIL, fromEmail)
                    .param(P2rConstants.FROM_NAME, fromName)
                    .param(P2rConstants.TO_EMAIL, toEmail)
                    .param(P2rConstants.TO_NAME, toName)
                    .param(P2rConstants.PAGE_URL_KEY_STRING, pageUrl.getKeyString()));

            // Log background task created successfully
            log.addLogInfo(P2rConstants.SEND_TO_READER, true, null, null);
        } else {
            log.addLogInfo(P2rConstants.SEND_TO_READER, false, null, null);
        }
    }

    public static void pageToReader(String fromEmail, String fromName, String toEmail,
            String toName, String pageUrlKeyString) throws Exception {

        // As it's in background, no USER log! so if errors occurred, put them in SERVER log!

        PageUrl pageUrl = getPageUrl(pageUrlKeyString);
        if (pageUrl == null) {
            // Users might already delete it, do nothing.
            return;
        }

        // 1. Fetch URL
        String urlContent = fetchPageUrl(pageUrl.getPUrl());
        if (urlContent != null && !urlContent.isEmpty()) {
            // 2. Cleanse
            JResult result = new JResult();
            result.setUrl(pageUrl.getPUrl());

            ArticleTextExtractor extractor = new ArticleTextExtractor();
            extractor.extractContent(result, urlContent);

            // TODO: Solve relative links

            // 3. Embeded images

            String text = lessText(result.getText(), 350);
            String cleansedPage = htmlTemplate(result.getTitle(), result.getCleansedHtml());

            // 4. Send to reader email
            sendEmailCleansedPage(fromEmail, fromName, toEmail, toName,
                    result.getTitle(), cleansedPage);

            // 5. Update to DB
            editPageUrl(pageUrl, result.getTitle(), text, cleansedPage);
        }
    }

    private static String fetchPageUrl(String pUrl) throws IOException {
        HTTPResponse res = null;
        com.google.appengine.api.urlfetch.FetchOptions fetchOptions =
                com.google.appengine.api.urlfetch.FetchOptions.Builder
                        .allowTruncate()
                        .followRedirects()
                        .setDeadline(60.0);

        URL url = new URL(pUrl);
        HTTPRequest req = new HTTPRequest(url, HTTPMethod.GET, fetchOptions);
        res = URLFetchServiceFactory.getURLFetchService().fetch(req);

        // Try to get charset, default to utf-8
        // TODO: Better way to get charset from Content-Type in response header?
        String charset = "utf-8";
        List<HTTPHeader> httpHeaders = res.getHeadersUncombined();
        for (HTTPHeader httpHeader : httpHeaders) {
            if (httpHeader.getName().equals("Content-Type")) {
                String[] values = httpHeader.getValue().split(";");
                for (String value : values) {
                    if (value.toLowerCase().contains("charset")) {
                        values = value.toLowerCase().split("=");
                        if (values[0].trim().equals("charset")) {
                            charset = values[1].trim();
                            break;
                        }
                    }
                }
                break;
            }
        }
        return new String(res.getContent(), Charset.forName(charset));
    }

    private static void sendEmailCleansedPage(String fromEmail, String fromName, String toEmail,
            String toName, String subject, String msgBody) throws UnsupportedEncodingException,
            MessagingException {

        Properties props = new Properties();
        javax.mail.Session mailSession = javax.mail.Session.getDefaultInstance(
                props, null);

        MimeMessage msg = new MimeMessage(mailSession);
        msg.setFrom(new InternetAddress(fromEmail, fromName));
        msg.addRecipient(Message.RecipientType.TO,
                new InternetAddress(toEmail, toName));
        msg.setSubject("convert");

        Multipart mp = new MimeMultipart();

        MimeBodyPart htmlPart = new MimeBodyPart();
        String sentBy = "<html><head><title>Page2Reader</title></head><body><p>"
                + "Sent by Page2Reader</p></body></html>";
        htmlPart.setContent(sentBy, "text/html");
        mp.addBodyPart(htmlPart);

        MimeBodyPart attachment = new MimeBodyPart();
        attachment.setFileName(subject + ".html");

        // Kindle supports only ANSI or ASCII
        // https://kdp.amazon.com/self-publishing/help?topicId=A3G4LY8RGZ9SP6
        String asciiString = escapeForAscii(msgBody);
        DataSource ds = new ByteArrayDataSource(asciiString.getBytes("us-ascii"),
                "application/octet-stream");
        attachment.setDataHandler(new DataHandler(ds));

        mp.addBodyPart(attachment);

        msg.setContent(mp);

        Transport.send(msg);
    }

    private static String escapeForAscii(String originalString) {
        // Escape some characters for encoding in Ascii
        // http://numberformat.wordpress.com/2013/02/09/convert-utf-8-unicode-to-ascii-latin-1/
        // https://github.com/numberformat/20130209/

        String str = Normalizer.normalize(originalString, Normalizer.Form.NFKD);
 
        str = str.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");

        str = str.replaceAll("[\u00AB\u2034\u2037\u00BB\u02BA\u030B\u030E\u201C\u201D\u201E"
                + "\u201F\u2033\u2036\u3003\u301D\u301E]", "\"");
        str = str.replaceAll("[\u02CB\u0300\u2035]", "`");
        str = str.replaceAll("[\u02C4\u02C6\u0302\u2038\u2303]", "^");
        str = str.replaceAll("[\u02CD\u0331\u0332\u2017]", "_");
        str = str.replaceAll("[\u00AD\u2010\u2011\u2012\u2013\u2014\u2212\u2015]", "-");
        str = str.replaceAll("[\u201A]", ",");
        str = str.replaceAll("[\u0589\u05C3\u2236]", ":");
        str = str.replaceAll("[\u01C3\u2762]", "!");
        str = str.replaceAll("[\u203D]", "?");
        str = str.replaceAll("[\u00B4\u02B9\u02BC\u02C8\u0301\u200B\u2018\u2019\u201B\u2032]", "'");
        str = str.replaceAll("[\u27E6]", "[");
        str = str.replaceAll("[\u301B]", "]");
        str = str.replaceAll("[\u2983]", "{");
        str = str.replaceAll("[\u2984]", "}");
        str = str.replaceAll("[\u066D\u204E\u2217\u2731]", "*");
        str = str.replaceAll("[\u00F7\u0338\u2044\u2060\u2215]", "/");
        str = str.replaceAll("[\u20E5\u2216]", "\\");
        str = str.replaceAll("[\u266F]", "#");
        str = str.replaceAll("[\u066A\u2052]", "%");
        str = str.replaceAll("[\u2039\u2329\u27E8\u3008]", "<");
        str = str.replaceAll("[\u203A\u232A\u27E9\u3009]", ">");
        str = str.replaceAll("[\u01C0\u05C0\u2223\u2758]", "|");
        str = str.replaceAll("[\u02DC\u0303\u2053\u223C\u301C]", "~");

        return str;
    }

    private static void editPageUrl(PageUrl pageUrl, String title, String text,
            String cleansedPage) {

        // Check if still existing
        DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
        try {
            ds.get(pageUrl.getKey());
        } catch (EntityNotFoundException e) {
            // Already deleted, do nothing
            return;
        }

        pageUrl.setTitle(title);
        pageUrl.setText(text);
        pageUrl.setCleansedPage(cleansedPage);
        pageUrl.setSentCount(pageUrl.getSentCount() + 1);

        // Save to DB
        for (int i = 0; i < 10; i++) {
            try {
                Transaction txn = ds.beginTransaction();
                try {
                    ds.put(txn, pageUrl.getEntity());

                    // Throws java.util.ConcurrentModificationException
                    // if entity group was modified by other thread
                    txn.commit();

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
        throw new ConcurrentModificationException();
    }

    private static String htmlTemplate(String title, String text) {
        String template = "<!DOCTYPE html>"
                + "<head>"
                + "<meta charset='utf-8'>"
                + "<title>" + title + "</title>"
                + "<style></style>"
                + "</head>"
                + "<body>" + text + "</body>"
                + "</html>";
        return template;
    }

    private static String lessText(String text, int length) {
        if (text == null)
            return "";

        if (length >= 0 && text.length() > length)
            return text.substring(0, length);

        return text;
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
