package com.arsdigita.cms.contentassets;

import com.arsdigita.runtime.AbstractConfig;
import com.arsdigita.util.parameter.IntegerParameter;
import com.arsdigita.util.parameter.Parameter;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class RelatedLinkConfig extends AbstractConfig {

    private static RelatedLinkConfig INSTANCE;
    
    private final Parameter assetStepSortKey = new IntegerParameter(
            "com.arsdigita.cms.relatedlink.contentassets.asset_step_sortkey",
            Parameter.REQUIRED,
            1);
    
    public RelatedLinkConfig() {
        
        register(assetStepSortKey);
        
        loadInfo();
    }
    
    public static final RelatedLinkConfig getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new RelatedLinkConfig();
            INSTANCE.load();
        }
        return INSTANCE;
    }
    
    public Integer getAssetStepSortKey() {
        return (Integer) get(assetStepSortKey);
    }
    
}
