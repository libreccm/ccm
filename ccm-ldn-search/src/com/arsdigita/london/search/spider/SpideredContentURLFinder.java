package com.arsdigita.london.search.spider;


import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.kernel.NoValidURLException;
import com.arsdigita.kernel.URLFinder;
import com.arsdigita.persistence.OID;

/**
 * URLFinder implementation for {@link SpideredContent}, i.e.
 * content for remote sites that has been indexed for searching.
 *
 * This URLFinder just returns the URL from which the 
 * <tt>SpideredContent</tt> with the given <tt>OID</tt>
 * was retrieved.
 *@author <a href="mailto:mhanisch@redhat.com">Michael Hanisch</a>
 *@version $Id: SpideredContentURLFinder.java 755 2005-09-02 13:42:47Z sskracic $
 */
public class SpideredContentURLFinder implements URLFinder {

    public String find(OID oid, String context) {
        return find(oid);
    }

    public String find(OID oid) {
        
        String contentURL = null;
        try {
            SpideredContent content = new SpideredContent(oid);

            contentURL = content.getURL();
        }
        catch (DataObjectNotFoundException done) {
            throw new NoValidURLException(done.getMessage());
        }
        if (contentURL == null || contentURL.trim().length() == 0) {
            throw new NoValidURLException(contentURL);
        }
        return contentURL;
    }
}
