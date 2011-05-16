package com.arsdigita.cms.contentassets;

/**
 *
 * @author Jens Pelzetter
 */
public class SciOrganizationPublicationLinkInitializer extends RelatedLinkInitializer {
    
    public SciOrganizationPublicationLinkInitializer() {
        super("empty.pdl.mf");
    }
    
    @Override
    public String getTraversalXML() {
        return "/WEB-INF/traversal-adapters/com/arsdigita/cms/contentassets/SciOrganizationPublicationLink.xml";
    }           
}
