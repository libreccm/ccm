
package com.arsdigita.cms.contenttypes;

import com.arsdigita.cms.contenttypes.AbstractContentTypeLoader;


/**
 * Loader.
 *
 * @author Sören Bernstein
 */
public class BaseContactLoader extends AbstractContentTypeLoader {
    public final static String versionId =
        "$Id: BaseContactLoader.java $" +
        "$Author: quasi $" +
        "$DateTime: 2009/03/15 $";


    private static final String[] TYPES = {
        "/WEB-INF/content-types/com/arsdigita/cms/contenttypes/BaseContact.xml"
    };

    public String[] getTypes() {
        return TYPES;
    }

}
