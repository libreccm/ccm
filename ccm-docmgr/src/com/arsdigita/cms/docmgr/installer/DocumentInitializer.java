package com.arsdigita.cms.docmgr.installer;

import org.apache.log4j.Category;

import com.arsdigita.cms.contenttypes.ContentTypeInitializer;
import com.arsdigita.cms.docmgr.Document;

/*
 * Instantiates the Document content type
 *
 * @version $Id: PrescriptiveInitializer.java,v 1.3 2003/06/30 21:47:31 cwolfe Exp $
 */
public class DocumentInitializer extends ContentTypeInitializer {

    private static Category s_log = 
        Category.getInstance(DocumentInitializer.class);

    // using the whole module's mf
    public DocumentInitializer() {
        super("ccm-docmgr.pdl.mf", Document.BASE_DATA_OBJECT_TYPE);
    }

    public String getTraversalXML() {
        return "/WEB-INF/traversal-adapters/com/arsdigita/cms/docmgr/Document.xml";
    }

    public String[] getStylesheets() {
        return new String[] { "/static/content-types/com/arsdigita/cms/docmgr/Document.xsl" };
    }
}
