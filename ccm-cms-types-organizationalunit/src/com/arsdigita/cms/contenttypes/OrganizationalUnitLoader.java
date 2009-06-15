package com.arsdigita.cms.contenttypes;

import org.apache.log4j.Logger;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 */
public class OrganizationalUnitLoader extends AbstractContentTypeLoader {

    private final static Logger logger = Logger.getLogger(OrganizationalUnitLoader.class);
    private final static String[] TYPES = {"/WEB-INF/content-types/com/arsdigita/cms/contentypes/OrganizationalUnit.xml"};    

    public String[] getTypes() {
        return TYPES;
    }

    public OrganizationalUnitLoader() {
        super();
    }    
}
