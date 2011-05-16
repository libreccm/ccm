package com.arsdigita.cms.contentassets;

/**
 *
 * @author Jens Pelzetter
 */
public class SciProjectPublicationLinkInitializer extends RelatedLinkInitializer {

    public SciProjectPublicationLinkInitializer() {
        super("empty.pdl.mf");
    }

    @Override
    public String getTraversalXML() {
        return "/WEB-INF/traversal-adapters/com/arsdigita/cms/contentassets/SciProjectPublicationLink.xml";
    }
}
