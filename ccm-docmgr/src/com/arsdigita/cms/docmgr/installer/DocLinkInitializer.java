package com.arsdigita.cms.docmgr.installer;

import org.apache.log4j.Category;

import com.arsdigita.cms.contenttypes.ContentTypeInitializer;
import com.arsdigita.cms.docmgr.DocLink;

/*
 * Instantiates the DocLink content type
 *
 * @version $Id: PrescriptiveInitializer.java,v 1.3 2003/06/30 21:47:31 cwolfe Exp $
 */
public class DocLinkInitializer extends ContentTypeInitializer {

    private static Category s_log = 
        Category.getInstance(DocLinkInitializer.class);

    // using empty mf, everything is loaded by DocumentInitializer
    public DocLinkInitializer() {
        super("empty.pdl.mf", DocLink.BASE_DATA_OBJECT_TYPE);
    }

    public String getTraversalXML() {
        return "/WEB-INF/traversal-adapters/com/arsdigita/cms/docmgr/DocLink.xml";
    }

    public String[] getStylesheets() {
        return new String[] { "/static/content-types/com/arsdigita/cms/docmgr/DocLink.xsl" };
    }
}
