package com.arsdigita.cms.contenttypes;

import org.apache.log4j.Logger;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class PublicPersonalProfileInitializer extends ContentTypeInitializer {
    
    private static final Logger logger = Logger.getLogger(PublicPersonalProfileInitializer.class);
    
    public PublicPersonalProfileInitializer() {
        super("ccm-cms-publicpersonalprofile.pdl.mf",
              PublicPersonalProfile.BASE_DATA_OBJECT_TYPE);
    }
    
     @Override
    public String[] getStylesheets() {
        return new String[]{
                    "/static/content-types/com/arsdigita/cms/contenttypes/PublicPersonalProfile.xsl"
                };
    }

    @Override
    public String getTraversalXML() {
        return "/WEB-INF/traversal-adapters/com/arsdigita/cms/contenttypes/PublicPersonalProfile.xml";

    }
}
