package com.arsdigita.cms.webpage;

import com.arsdigita.cms.contenttypes.AbstractContentTypeLoader;


public class WebpageLoader extends AbstractContentTypeLoader {

    private static final String[] TYPES = {
        "/WEB-INF/content-types/com/arsdigita/cms/webpage/Webpage.xml"
    };

    public String[] getTypes() {
        return TYPES;
    }

}
