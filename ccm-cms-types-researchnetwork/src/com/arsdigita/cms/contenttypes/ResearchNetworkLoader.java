package com.arsdigita.cms.contenttypes;

import org.apache.log4j.Logger;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 */
public class ResearchNetworkLoader extends AbstractContentTypeLoader {

    private final static Logger s_log = Logger.getLogger(ResearchNetworkLoader.class);
    private final static String[] TYPES = {"/WEB-INF/content-types/com/arsdigita/cms/contenttypes/ResearchNetwork.xml"};

    @Override
    protected String[] getTypes() {
        return TYPES;
    }

    public ResearchNetworkLoader() {
        super();
    }

}
