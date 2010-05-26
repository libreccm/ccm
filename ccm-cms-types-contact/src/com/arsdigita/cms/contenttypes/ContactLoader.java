
package com.arsdigita.cms.contenttypes;

/**
 * Loader.
 *
 * @author Sören Bernstein
 */
public class ContactLoader extends AbstractContentTypeLoader {

    private static final String[] TYPES = {
        "/WEB-INF/content-types/com/arsdigita/cms/contenttypes/Contact.xml"
    };

    public String[] getTypes() {
        return TYPES;
    }

}
