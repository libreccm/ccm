package com.arsdigita.cms.contenttypes;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class OrganizationInitializer extends ContentTypeInitializer {

    public OrganizationInitializer() {
        super("ccm-cms-types-organization.pdl.mf",
              Organization.BASE_DATA_OBJECT_TYPE);
    }
    
   @Override 
   public String[] getStylesheets() {
       return new String[]{INTERNAL_THEME_TYPES_DIR + "Organization.xsl"};
   }
   
    @Override
    public String getTraversalXML() {
        return "/WEB-INF/traversal-adapters/com/arsdigita/cms/contenttypes/Organization.xml";
    }
}
