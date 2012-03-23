package com.arsdigita.cms.docmgr.installer;

import org.apache.log4j.Category;

import com.arsdigita.cms.contenttypes.ContentTypeInitializer;
import com.arsdigita.cms.docmgr.Document;

/*
 * Instantiates the Document content type
 *
 * @version $Id: DocumentInitializer.java $
 */
public class DocumentInitializer extends ContentTypeInitializer {

    private static Category s_log = 
        Category.getInstance(DocumentInitializer.class);

    /**
     * Constructor, sets the PDL manifest file and object type string.
     * Using the whole module's mf here
     */
    public DocumentInitializer() {
        super("ccm-docmgr.pdl.mf", Document.BASE_DATA_OBJECT_TYPE);
    }

    @Override
    public String getTraversalXML() {
        return "/WEB-INF/traversal-adapters/com/arsdigita/cms/docmgr/Document.xml";
    }

    /**
     * Retrieve location of this content types stylesheet. Overwrites parent
     * method with FormItem specific value for use by the parent class worker
     * methods.
     *
     * @return fully qualified path info string reltive to document (context) root
     */
    @Override
    public String[] getStylesheets() {
        return new String[] {
            "/themes/heirloom/contenttypes/DMDocument.xsl" };
        //  "/static/content-types/com/arsdigita/cms/docmgr/Document.xsl" };
    }
}
