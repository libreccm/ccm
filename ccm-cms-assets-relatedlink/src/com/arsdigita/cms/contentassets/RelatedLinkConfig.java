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

    private static final RelatedLinkConfig INSTANCE = new RelatedLinkConfig();

    static {
        INSTANCE.load();
    }

    private final Parameter assetStepSortKey = new IntegerParameter(
            "com.arsdigita.cms.relatedlink.contentassets.asset_step_sortkey",
            Parameter.REQUIRED,
            1);
    
    protected RelatedLinkConfig() {
        
        super();
        
        register(assetStepSortKey);
        
        loadInfo();
    }
    
    public static final RelatedLinkConfig getInstance() {
        return INSTANCE;
    }
    
    public Integer getAssetStepSortKey() {
        return (Integer) get(assetStepSortKey);
    }
    
}
