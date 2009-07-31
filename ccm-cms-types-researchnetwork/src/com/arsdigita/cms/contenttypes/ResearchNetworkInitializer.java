package com.arsdigita.cms.contenttypes;

import com.arsdigita.runtime.LegacyInitEvent;
import org.apache.log4j.Logger;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 */
public class ResearchNetworkInitializer extends ContentTypeInitializer {

    private static final Logger s_log = Logger.getLogger(ResearchNetworkInitializer.class);

    public ResearchNetworkInitializer() {
        super("ccm-cms-types-researchnetwork.pdl.mf", ResearchNetwork.BASE_DATA_OBJECT_TYPE);
    }

    public String getStylesheet() {
        return "static/content-types/com/arsdigita/cms/contenttypes/ResearchNetwork.xsl";
    }

    @Override
    public void init(LegacyInitEvent evt) {
        super.init(evt);
    }
}
