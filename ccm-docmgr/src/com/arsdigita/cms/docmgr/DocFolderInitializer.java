package com.arsdigita.cms.docmgr;

import com.arsdigita.cms.ContentType;
import com.arsdigita.cms.docmgr.DocFolder;

/**
 * Loader class for the DocFolder content type. Registers the content
 * type, and creates an authoring kit for it.
 *
 * @author Crag Wolfe
 */
public class DocFolderInitializer {

    private final static org.apache.log4j.Logger s_log =
        org.apache.log4j.Logger.getLogger(DocFolderInitializer.class);

    public static final String LABEL = "DocFolder";
    public static final String DESCRIPTION = "A document type.";

    public void load() {
        ContentType type;

        type = new ContentType();
        type.setName( LABEL );
        type.setDescription( DESCRIPTION );
        type.setClassName( DocFolder.class.getName() );
        type.setAssociatedObjectType( DocFolder.TYPE );
        type.save();

        s_log.debug("loaded DocFolder content type");
    }
}
