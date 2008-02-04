package com.arsdigita.london.search.spider;

import com.arsdigita.persistence.PersistenceException;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.TransactionContext;
import com.arsdigita.util.Assert;

import net.matuschek.http.HttpDoc;
import net.matuschek.http.HttpDocManager;
import net.matuschek.http.HttpHeader;

import java.math.BigDecimal;
import java.net.URL;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

/**
 * Implementation of JoBo's HttpDocManager interface, the interface
 * that is responsible for processing the content retrieved from
 * the spider.
 * This implementation stores the content as {@link SpideredContent}
 *  domain objects.
 *
 *@author <a href="mailto:mhanisch@redhat.com">Michael Hanisch</a>
 *@version $Id: DocumentManager.java 287 2005-02-22 00:29:02Z sskracic $
 *@see SpideredContent
 */
public class DocumentManager implements HttpDocManager {

    // HTTP headers
    private static final String LAST_MODIFIED_HEADER = "Last-Modified";
    private static final String CONTENT_TYPE_HEADER  = "Content-Type";
    private static final String CONTENT_LENGTH_HEADER = "Content-Length";

    // HTTP status codes
    private static final String HTTP_OK = "200";
    private static final String HTTP_NOT_FOUND = "404";
    private static final String HTTP_INTERNAL_ERROR = "500";

    /**
     * DateFormat formatter/parser which is able to parse date from HTTP headers.
     * "Borrowed" from com.arsdigita.dispatcher.DispatcherHelper.
     * TO DO: Move this into an appropriate utility class!
     */
    public static final SimpleDateFormat rfc1123_formatter = new SimpleDateFormat("EEE, dd MMM yyyy hh:mm:ss z");

    private static final org.apache.log4j.Logger s_log =
        org.apache.log4j.Logger.getLogger(DocumentManager.class);

    public void processDocument(HttpDoc doc) {
        /* TO DO: Create SpideredContent domain object,
         * set the properties and save() it
         */
        if (doc == null) {
            s_log.error("retrieved document is null! SKIPPING");
            return;
        }
        URL retrievedURL = doc.getURL();
        Date lastAccessed = new Date();
        String status;          // TO DO: check status

        if (s_log.isDebugEnabled()) {
            s_log.debug("URL: " + doc.getURL());
            s_log.debug("HTML? " + doc.isHTML());
            s_log.debug("type: " + getMIMEType(doc));
            s_log.debug("size: " + getContentLength(doc));
            
            // output all headers
            Vector headerLines = doc.getHttpHeaders();
            for (int i = 0; i < headerLines.size(); i++) {
                HttpHeader header = (HttpHeader) headerLines.elementAt(i);
                s_log.debug( header.getName() + " ==> " + header.getValue());
            }
        }
        
        s_log.debug("checking DB for an older version of this file"); // !!

        // Do we already have a copy of this in our database?
        setupTxn();             // need to start a new transaction
        SpideredContent existingContent = SpideredContent.retrieve(retrievedURL);
        if (existingContent != null) {
            // Has the content been modified since then?  (also handle
            // the case when we do not now when the doc had been
            // modified
            Date lastModified = getLastModifiedDate(doc);
            Date lastModifiedOfExisting = existingContent.getLastModified();
            if (s_log.isDebugEnabled() && lastModifiedOfExisting != null) {
                s_log.debug("existing version of this document last modified at "
                            + rfc1123_formatter.format(lastModifiedOfExisting));
            }
            if (lastModified == null || 
                lastModified.compareTo(lastModifiedOfExisting) > 0) {
                // The doc we retrieved right now if newer than what we have
                // in the DB ==> update it!
                updateContent(existingContent, doc, lastAccessed, HTTP_OK);
                s_log.info("retrieved newer version of existing content"
                            + " => updating DB");
            }
            else {
                // The content in the DB hasn't been modified since; just remember
                // that we've accessed it in the meantime
                existingContent.setLastAccessed( lastAccessed );
                s_log.info("retrieved UNMODIFIED version of existing content");
            }
            existingContent.save();
        }
        else {
            // This content has been retrieved for the first time
            SpideredContent obj = new SpideredContent();
            updateContent(obj, doc, lastAccessed, HTTP_OK);
            obj.save();
            s_log.info("storing new content");
        }
        commitTxn();
    }

    /**
     * Retrieve the HttpDoc for the given URL from the cache;
     * this operation is <strong>not supported</strong>.
     *
     *@return always returns <tt>null</tt>
     */
    public HttpDoc retrieveFromCache(URL url) {
        return null;
    }

    /* Implementation note:
     * The getLastModifiedDate(HttpDoc), getContentLength(HttpDoc) and
     * getMIMEType(HttpDoc) methods really belong in a custom subclass
     *  of HttpDoc,
     * but as of now, I'd really like to avoid customizing the JoBo
     * spider.
     */
    
    /**
     * Get the last modified date for a document (by looking at the
     * "Last-Modified" header.
     *@param doc the document from which the date should be taken
     *@return the content of the <tt>doc</tt>'s "Last-Modified" header
     * as a <tt>Date</tt> object; or <tt>null</tt> if no such header was found
     */
    protected static Date getLastModifiedDate(HttpDoc doc) {
        HttpHeader lastModifiedHeader = doc.getHeader(LAST_MODIFIED_HEADER);
        if (lastModifiedHeader == null) {
            s_log.debug("no " + LAST_MODIFIED_HEADER + " found");
            return null;
        }
        
        // Format for Last-Modified header: "Sun, 21 Oct 2001 16:15:43 GMT"
        rfc1123_formatter.setLenient(false);
        return rfc1123_formatter.parse(lastModifiedHeader.getValue(),
                                       new ParsePosition(0));
    }

    /**
     * Get the size of the content, as returned by the 
     * CONTENT_LENGTH_HEADER.
     *@param doc the HttpDoc that has been retrieved
     *@return the size of <tt>doc</tt> as indicated by the CONTENT_LENGTH_HEADER
     */
    protected static BigDecimal getContentLength(HttpDoc doc) {
        // TO DO: should we use getContent().length instead?!
        HttpHeader size = doc.getHeader(CONTENT_LENGTH_HEADER);
        if (size == null) {
            s_log.debug(" no " + CONTENT_LENGTH_HEADER + " found");
            return null;
        }
        return new BigDecimal(size.getValue());
    }

    protected static String getMIMEType(HttpDoc doc) {
        HttpHeader header = doc.getHeader(CONTENT_TYPE_HEADER);
        if (header != null) {
            return header.getValue();
        }
        else {
            // TO DO: Better idea?
            return null;
        }
    }

    /**
     * Takes the provided arguments and values from <tt>newContent</tt>
     * and updates the Domain object <tt>oldContent</tt>. This operation
     * only updates the domain object in memory but does not
       <code>save()</code> it.
     *@param oldContent the domain object that should be updated.
     *  This should be a SpideredContent that had previously been retrieved
     *  from the same URL as <tt>newContent</tt>.
     *@param newContent HttpDoc that has just been retrieved, from the
     *  same location/URL as <tt>oldContent</tt>
     *@param lastAccessed time of last access (to <tt>newContent</tt>)
     *@param status string representation of HTTP status code for the operation
     *  that retrieved <tt>newContent</tt>
     *
     */
    protected void updateContent(SpideredContent oldContent,
                                 HttpDoc newContent,
                                 Date lastAccessed,
                                 String status) {
        Assert.exists(oldContent, SpideredContent.class);
        Assert.exists(newContent, HttpDoc.class);
        
        oldContent.setURL( newContent.getURL() );
        oldContent.setContent( newContent.getContent() );
        oldContent.setSize( getContentLength(newContent));
        oldContent.setMimeType( getMIMEType(newContent) );
        oldContent.setLastModified( getLastModifiedDate(newContent) );
        oldContent.setLastAccessed( lastAccessed );
        oldContent.setStatus( status );
    }

    private void setupTxn() {
        TransactionContext txn = 
            SessionManager.getSession().getTransactionContext();

        /* the following code has been borrowed from 
         *  //ps/proj/dp-vtr/dev/src/de/dp/search/ProcessScheduler
         */
        // rollback first, because nested txn was reported here
        if (txn.inTxn()) {
            try {
                txn.abortTxn();
            }
            catch (PersistenceException ex) {
                // ignore!
            }
        }
        txn.beginTxn();
    }

    private void commitTxn() {
        TransactionContext txn = 
            SessionManager.getSession().getTransactionContext();
        txn.commitTxn();
    }
        
            
}
