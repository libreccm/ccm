package com.arsdigita.cms.contenttypes;

import org.apache.log4j.Logger;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class SciPublicPersonalProfileInitializer extends ContentTypeInitializer {
    
    private static final Logger logger = Logger.getLogger(SciPublicPersonalProfileInitializer.class);
    
    public SciPublicPersonalProfileInitializer() {
        super("ccm-sci-publicpersonalprofile.pdl.mf",
              SciPublicPersonalProfile.BASE_DATA_OBJECT_TYPE);
    }
    
     @Override
    public String[] getStylesheets() {
        return new String[]{
                    "/static/content-types/com/arsdigita/cms/contenttypes/SciPublicPersonalProfile.xsl"
                };
    }

    @Override
    public String getTraversalXML() {
        return "/WEB-INF/traversal-adapters/com/arsdigita/cms/contenttypes/SciPublicPersonalProfile.xml";

    }
}
