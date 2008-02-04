package com.arsdigita.cms.docmgr;

import com.arsdigita.cms.contenttypes.AbstractContentTypeLoader;


public class DocumentLoader extends AbstractContentTypeLoader {

    private static final String[] TYPES = {
        "/WEB-INF/content-types/com/arsdigita/cms/docmgr/Document.xml"
    };

    public String[] getTypes() {
        return TYPES;
    }

}
