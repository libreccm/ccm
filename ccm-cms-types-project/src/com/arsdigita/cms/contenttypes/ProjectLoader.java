package com.arsdigita.cms.contenttypes;

import org.apache.log4j.Logger;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 */
public class ProjectLoader extends AbstractContentTypeLoader {


    private final static Logger s_log = Logger.getLogger(ProjectLoader.class);
    private final static String[] TYPES = {"/WEB-INF/content-types/com/arsdigita/cms/contenttypes/Project.xml"};

    public String[] getTypes() {
        return TYPES;
    }

    public ProjectLoader() {
        super();
    }
}
