package com.arsdigita.cms.contenttypes;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class SimpleOrganizationInitializer extends ContentTypeInitializer {

    public SimpleOrganizationInitializer() {
        super("ccm-cms-types-simpleorganization.pdl.mf",
              SimpleOrganization.BASE_DATA_OBJECT_TYPE);
    }
    
   @Override 
   public String[] getStylesheets() {
       return new String[]{INTERNAL_THEME_TYPES_DIR + "SimpleOrganization.xsl"};
   }
   
    @Override
    public String getTraversalXML() {
        return "/WEB-INF/traversal-adapters/com/arsdigita/cms/contenttypes/SimpleOrganization.xml";
    }
}
