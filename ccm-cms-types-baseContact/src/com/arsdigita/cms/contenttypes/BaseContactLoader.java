
package com.arsdigita.cms.contenttypes;

/**
 * Loader.
 *
 * @author Sören Bernstein
 */
public class BaseContactLoader extends AbstractContentTypeLoader {

    private static final String[] TYPES = {
        "/WEB-INF/content-types/com/arsdigita/cms/contenttypes/BaseContact.xml"
    };

    public String[] getTypes() {
        return TYPES;
    }

}
