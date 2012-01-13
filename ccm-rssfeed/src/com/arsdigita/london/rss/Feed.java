package com.arsdigita.london.rss;

import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.kernel.ACSObject;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;

import java.math.BigDecimal;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.DataCollection;


/**
 * Domain object for an RSS channel.
 * @author Simon Buckle (sbuckle@arsdigita.com)
 */
public class Feed extends ACSObject {

    public static final String BASE_DATA_OBJECT_TYPE
        = "com.arsdigita.london.rss.Feed";

    // Attributes
    public static final String TITLE = "title";
    public static final String URL = "url";
    public static final String DESCRIPTION = "description";
    public static final String IS_PROVIDER = "isProvider";

    public Feed() {
        super(BASE_DATA_OBJECT_TYPE);
    }

    public Feed(BigDecimal id)
        throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    public Feed(DataObject obj) {
        super(obj);
    }

    public Feed(OID oid)
        throws DataObjectNotFoundException {
        super(oid);
    }

    public static Feed create(String url,
                              String title,
                              String desc,
                              boolean provider) {
        Feed feed = new Feed();

        feed.setURL(url);
        feed.setTitle(title);
        feed.setDescription(desc);
        feed.setProvider(provider);

        return feed;
    }

    public static Feed retrieve(BigDecimal id)
        throws DataObjectNotFoundException {

        Session session = SessionManager.getSession();
        DataCollection feed = session.retrieve(BASE_DATA_OBJECT_TYPE);

        feed.addEqualsFilter(ACSObject.ID, id);

        if (feed.next()) {
            DataObject obj = feed.getDataObject();
            feed.close();
            return new Feed(obj);
        }

        throw new DataObjectNotFoundException("cannot find feed " + id);
    }

    public static Feed retrieve(String url)
        throws DataObjectNotFoundException {

        Session session = SessionManager.getSession();
        DataCollection feed = session.retrieve(BASE_DATA_OBJECT_TYPE);

        feed.addEqualsFilter(URL, url);

        if (feed.next()) {
            DataObject obj = feed.getDataObject();
            feed.close();
            return new Feed(obj);
        }

        throw new DataObjectNotFoundException("cannot find feed" + url);
    }

    public static FeedCollection retrieveAll() {
        Session session = SessionManager.getSession();
        DataCollection feeds = session.retrieve(BASE_DATA_OBJECT_TYPE);
        feeds.addOrder(TITLE);
        return new FeedCollection(feeds);
    }

    /*
     * Accessor to the base object type.
     * @return The fully qualified name of the supporting data object.
     */
    public String getBaseDataObjectType() {
        return BASE_DATA_OBJECT_TYPE;
    }

    /*
     * @param title The title of the channel (RSS feed).
     */
    public void setTitle(String title) {
        set(TITLE, title);
    }

    public String getTitle() {
        return (String)get(TITLE);
    }

    /*
     * @param url The url to the main site containing the channel.
     */
    public void setURL(String url) {
        set(URL, url);
    }

    public String getURL() {
        return (String)get(URL);
    }

    /*
     * @param desc The description of what the channel contains.
     */
    public void setDescription(String desc) {
        set(DESCRIPTION, desc);
    }

    public String getDescription() {
        return (String)get(DESCRIPTION);
    }

    public void setProvider(boolean provider) {
        set(IS_PROVIDER, new Boolean(provider));
    }

    public boolean isProvider() {
        return ((Boolean)get(IS_PROVIDER)).booleanValue();
    }
}
