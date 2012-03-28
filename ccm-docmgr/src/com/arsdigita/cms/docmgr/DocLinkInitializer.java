package com.arsdigita.cms.docmgr;

import org.apache.log4j.Category;

import com.arsdigita.cms.contenttypes.ContentTypeInitializer;
import com.arsdigita.cms.docmgr.DocLink;

/*
 * Instantiates the DocLink content type
 * Initializes the DocLink content type.
 *
 * Defines the content type specific properties and just uses the super class
 * methods to register the content type with the (transient) content type store
 * (map). This is done by runtimeRuntime startup method which runs the init()
 * methods of all initializers (this one just using the parent implementation).
 *
 * @version $Id: DocLinkInitializer.java,v 1.3 2003/06/30 21:47:31 cwolfe Exp $
 */
public class DocLinkInitializer extends ContentTypeInitializer {

    private static Category s_log = 
        Category.getInstance(DocLinkInitializer.class);

    // 
    /**
     * Constructor, sets the PDL manifest file and object type string.
     * Using empty mf here, everything is loaded by DocumentInitializer
     */
    public DocLinkInitializer() {
        super("empty.pdl.mf", DocLink.BASE_DATA_OBJECT_TYPE);
    }

    @Override
    public String getTraversalXML() {
        return "/WEB-INF/traversal-adapters/com/arsdigita/cms/docmgr/DocLink.xml";
    }

    /**
     * Retrieve location of this content types stylesheet. Overwrites parent
     * method with FormItem specific value for use by the parent class worker
     * methods.
     *
     * @return fully qualified path info string relative to document root
     */
    @Override
    public String[] getStylesheets() {
        return new String[] {
            "/themes/heirloom/contenttypes/DMDocLink.xsl" };
        //  "/static/content-types/com/arsdigita/cms/docmgr/DocLink.xsl" };
    }
}
