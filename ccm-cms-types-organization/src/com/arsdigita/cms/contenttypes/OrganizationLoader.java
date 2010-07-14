
package com.arsdigita.cms.contenttypes;

/**
 * Loader.
 *
 * @author Jens Pelzetter
 */
public class OrganizationLoader extends AbstractContentTypeLoader {

    private static final String[] TYPES = {
        "/WEB-INF/content-types/com/arsdigita/cms/contenttypes/Organization.xml"
    };

    public String[] getTypes() {
        return TYPES;
    }

}
