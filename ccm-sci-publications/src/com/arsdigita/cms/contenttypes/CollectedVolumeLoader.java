package com.arsdigita.cms.contenttypes;

/**
 *
 * @author Jens Pelzetter
 */
public class CollectedVolumeLoader extends AbstractContentTypeLoader {

    private static final String[] TYPES = {"/WEB-INF/content-types/com/arsdigita/cms/contenttypes/CollectedVolume.xml"};

    public CollectedVolumeLoader() {
        super();
    }

    public String[] getTypes() {
        return TYPES;
    }

}
