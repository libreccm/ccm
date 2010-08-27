package com.arsdigita.cms.contenttypes;

/**
 *
 * @author Jens Pelzetter
 */
public class PublicationWithPublisherInitializer extends ContentTypeInitializer {

    public PublicationWithPublisherInitializer() {
        super("ccm-sci-publications.pdl.mf", PublicationWithPublisher.BASE_DATA_OBJECT_TYPE);
    }

    @Override
    public String[] getStylesheets() {
        return new String[]{"/static/content-types/com/arsdigita/cms/contenttypes/PublicationWithPublisher.xsl"};
    }

    @Override
    public String getTraversalXML() {
        return "/WEB-INF/traversal-adapters/com/arsdigita/cms/contenttypes/PublicationWithPublisher.xml";
    }

}
