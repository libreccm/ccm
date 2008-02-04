package com.arsdigita.cms.docmgr;

import com.arsdigita.cms.contenttypes.AbstractContentTypeLoader;


public class DocFolderLoader extends AbstractContentTypeLoader {

    private static final String[] TYPES = {
        "/WEB-INF/content-types/com/arsdigita/cms/docmgr/DocFolder.xml"
    };

    public String[] getTypes() {
        return TYPES;
    }

}
