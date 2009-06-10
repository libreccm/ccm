package com.arsdigita.cms.contenttypes;

import org.apache.log4j.Logger;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 */
public class OrganizationRoleLoader extends AbstractContentTypeLoader {
    private static final Logger logger = Logger.getLogger(OrganizationRoleLoader.class);

    private static final String TYPES[] = { "/WEB-INF/content-types/com/arsdigita/cms/contenttypes/OrganizationRole.xml" };

    @Override
    protected String[] getTypes() {
        return TYPES;
    }


}
