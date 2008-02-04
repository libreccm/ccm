
package com.arsdigita.london.importer.cms;

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;

import com.arsdigita.cms.ContentSection;
import com.arsdigita.util.Assert;

import java.util.List;


/**
 *   An enhanced version of &lt;folder&gt; XML subblock handler,
 * that also takes care of including other XML files via &lt;external&gt; tag.
 *
 *  @see com.arsdigita.london.importer
 */
public class FolderItemParser extends FolderParser {

    private static final Logger s_log =
        Logger.getLogger(FolderItemParser.class);

    private List m_items;

    public FolderItemParser(ContentSection section,
                            List items) {
        super(section);

        m_items = items;
    }

    public FolderItemParser(String tagName,
                            String tagURI,
                            ContentSection section,
                            List items) {
        super(tagName, tagURI, section);

        m_items = items;
    }


    protected void startTag(String tagName,
                            String uri,
                            Attributes atts) {
        if ("external".equals(tagName)) {
            String file = atts.getValue("source");
            if (s_log.isDebugEnabled()) {
                s_log.debug("Got item " + file + " in " + getFolder());
            }
            Assert.exists(file, String.class);
            m_items.add(new Object[] { getFolder(), file });
        } else {
            super.startTag(tagName, uri, atts);
        }
    }

    protected void endTag(String tagName,
                          String uri) {
        if ("external".equals(tagName)) {
            // nada
        } else {
            super.endTag(tagName, uri);
        }
    }


}

