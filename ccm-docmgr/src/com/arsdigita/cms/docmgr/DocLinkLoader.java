package com.arsdigita.cms.docmgr;

import com.arsdigita.cms.contenttypes.AbstractContentTypeLoader;


public class DocLinkLoader extends AbstractContentTypeLoader {

    private static final String[] TYPES = {
        "/WEB-INF/content-types/com/arsdigita/cms/docmgr/DocLink.xml"
    };

    public String[] getTypes() {
        return TYPES;
    }

}
