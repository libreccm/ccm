package com.arsdigita.cms.contentassets;

/**
 *
 * @author Jens Pelzetter
 */
public class SciDepartmentPublicationLinkInitializer extends RelatedLinkInitializer {
    
    public SciDepartmentPublicationLinkInitializer() {
        super("empty.pdl.mf");
    }
    
    @Override
    public String getTraversalXML() {
         return "/WEB-INF/traversal-adapters/com/arsdigita/cms/contentassets/SciDepartmentPublicationLink.xml";
    }
    
}
