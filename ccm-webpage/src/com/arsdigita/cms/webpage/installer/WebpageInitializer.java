package com.arsdigita.cms.webpage.installer;

import com.arsdigita.cms.contenttypes.ContentTypeInitializer;

import com.arsdigita.cms.webpage.Webpage;

import org.apache.log4j.Category;

/*
 * Instantiates the Webpage content type
 *
 * @version $Id: PrescriptiveInitializer.java,v 1.3 2003/06/30 21:47:31 cwolfe Exp $
 */
public class WebpageInitializer extends ContentTypeInitializer {

    private static Category s_log = 
        Category.getInstance(WebpageInitializer.class);

    public WebpageInitializer() {
        super("ccm-webpage.pdl.mf", Webpage.BASE_DATA_OBJECT_TYPE);
    }

    public String[] getStylesheets() {
        return new String[] { "/static/content-types/com/arsdigita/cms/Webpage.xsl" };
    }
}
