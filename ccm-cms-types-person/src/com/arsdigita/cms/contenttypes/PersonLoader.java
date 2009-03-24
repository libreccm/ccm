/*
 * Copyright (C) 2009 University Bremen, Center for Social Politics, Parkallee 39, 28209 Bremen.
 *
 */
package com.arsdigita.cms.contenttypes;

import com.arsdigita.cms.contenttypes.AbstractContentTypeLoader;

import org.apache.log4j.Logger;

/**
 *
 * @author Jens Pelzetter
 */
public class PersonLoader extends AbstractContentTypeLoader {

    private static final Logger s_log = Logger.getLogger(PersonLoader.class);
    private static final String[] TYPES = {
        "/WEB-INF/content-types/com/arsdigita/cms/contenttypes/Person.xml"
    };

    public String[] getTypes() {
        return TYPES;
    }
}
