package com.arsdigita.london.search.spider;

import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.search.intermedia.SearchableACSObject;
import com.arsdigita.util.Assert;

import java.math.BigDecimal;
import java.net.URL;
import java.util.Date;

/**
 * Domain object for searchable content obtained from remote sites.
 * Instances of this domain object are created by the spider for the
 * content it is downloading.
 *@author <a href="mailto:mhanisch@redhat.com">Michael Hanisch</a>
 *@version $Id: SpideredContent.java 755 2005-09-02 13:42:47Z sskracic $
 */
public class SpideredContent extends SearchableACSObject {
    
    private static org.apache.log4j.Logger s_log =
        org.apache.log4j.Logger.getLogger(SpideredContent.class);

    // constants for attribute names

    public static final String URL = "URL";
    public static final String CONTENT = "content";
    public static final String LAST_MODIFIED = "lastModified";
    public static final String LAST_ACCESSED = "lastAccessed";
    public static final String MIME_TYPE = "mimeType";
    public static final String CONTENT_SIZE = "content_size";
    public static final String STATUS = "status";
    public static final String CONTENT_SECTION = "content_section";

    public static final String BASE_DATA_OBJECT_TYPE = 
        "com.arsdigita.london.search.spider.SpideredContent";


    public SpideredContent() {
        super(BASE_DATA_OBJECT_TYPE);
    }

    public SpideredContent(BigDecimal id) throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    public SpideredContent(DataObject obj) {
        super(obj);
    }

    public SpideredContent(OID id) throws DataObjectNotFoundException {
        super(id);
    }



    public String getURL() {
        return (String)get(URL);
    }

    public void setURL(String url) {
        set(URL, url);
    }

    public void setURL(URL url) {
        setURL(url.toString());
    }

    public Date getLastModified() {
        return (Date) get(LAST_MODIFIED);
    }

    public void setLastModified(Date lastModified) {
        set(LAST_MODIFIED, lastModified);
    }

    public Date getLastAccessed() {
        return (Date) get(LAST_ACCESSED);
    }

    public void setLastAccessed(Date lastAccessed) {
        set(LAST_ACCESSED, lastAccessed);
    }

    public BigDecimal getSize() {
        return (BigDecimal) get(CONTENT_SIZE);
    }

    protected void setSize(BigDecimal size) {
        // TO DO: check if size matches the size of the content?
        set(CONTENT_SIZE, size);
    }
    
    public String getStatus() {
        return (String) get(STATUS);
    }

    public void setStatus(String status) {
        // TO DO: check
        set(STATUS, status);
    }

    public String getMimeType() {
        return (String) get(MIME_TYPE);
    }

    public void setMimeType(String mimeType) {
        set(MIME_TYPE, mimeType);
    }

    public byte[] getContent() {
        return (byte[]) get(CONTENT);
    }

    public void setContent(byte[] content) {
        // TO DO: check for emptiness? Set size?
        set(CONTENT, content);
        s_log.debug("setting CONTENT");
        if (content != null) {
            s_log.debug("CONTENT size is " + content.length);
            setSize(new BigDecimal(content.length));
        }
    }

    // methods from Searchable interface

    /**
     *@return Always returns an empty string!
     */
    public String getSearchXMLContent() {
        return "";
    }

    /**
     *@return the actual content retrieved from a remote site
     */
    public byte[] getSearchRawContent() {
        return getContent();
    }
    
    /**
     *@return always returns an empty string; no URL stub is needed
     * since there is a custom URLFinder for this type
     *@see com.arsdigita.kernel.URLService;
     *@see com.arsdigita.kernel.URLFinder;
     */
    public String getSearchUrlStub() {
        return "";
    }

    /**
     *@return the URL of this spidered content; 
     *@see #getURL()
     */
    public String getSearchLinkText() {
        return getURL();
    }

    /**
     *@returns Always returns an empty String.
     */
    public String getSearchSummary() {
        /* TO DO: In the future, we might want to show something useful here,
         * like the site this is from, when it has been downloaded the last time
         * etc.; or we might even try to extract some useful info from the content
         */
        return "";
    }

    /**
     * Retrieve spidered content by its URL (i.e., the URL from which it
     * had been retrieved.
     *@param url a URL; the SpideredContent object returned by this
     *  method had originally been retrieved from this URL.
     *  This argument must not be null!
     *@return the SpideredContent retrieved from the given <tt>url</tt>,
     * or <tt>null</tt> if no content has been retrieved from that URL.
     */
    public static SpideredContent retrieve(URL url) {
        Assert.exists(url, URL.class);
        return retrieve(url.toString());
    }

    /**
     * Retrieve spidered content by its URL (i.e., the URL from which it
     * had been retrieved.
     *@param url a (<tt>String</tt> representation of a) URL; the
     *  SpideredContent object returned by this
     *  method had originally been retrieved from this URL.
     *  This argument must not be null!
     *@return the SpideredContent retrieved from the given <tt>url</tt>,
     * or <tt>null</tt> if no content has been retrieved from that URL.
     */
    public static SpideredContent retrieve(String url) {
        Assert.exists(url, String.class);
        DataCollection coll = SessionManager.getSession()
            .retrieve(BASE_DATA_OBJECT_TYPE);
        coll.addEqualsFilter(URL, url);
        if (coll.next()) {
            return (SpideredContent) DomainObjectFactory.
                newInstance(coll.getDataObject());
        }
        else {
            return null;
        }
    }

    /* SpideredContent is not assigned to a particular content section. */
    public String getContentSection() {
	return "";
    }
}

