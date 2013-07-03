package com.arsdigita.cms.contentassets;

import com.arsdigita.runtime.AbstractConfig;
import com.arsdigita.util.parameter.BooleanParameter;
import com.arsdigita.util.parameter.IntegerParameter;
import com.arsdigita.util.parameter.Parameter;

/**
 * A record containing server-session scoped configuration properties.
 *
 * Accessors of this class may return null. Developers should take care to trap
 * null return values in their code.
 * 
 * Don't instantiate using constructor!
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class RelatedLinkConfig extends AbstractConfig {

    private static RelatedLinkConfig INSTANCE;
    

    // /////////////////////////////////////////////////////////
    // Parameter Section
    // /////////////////////////////////////////////////////////
    /**
     * 
     */
    private final Parameter assetStepSortKey = new IntegerParameter(
            "com.arsdigita.cms.contentassets.relatedlink.asset_step_sortkey",
            Parameter.REQUIRED,
            1);
    /**
     * Hide Additional Resource Fields on RelatedLinkPropertyForm anf
     * RelatedLink table
     */
    private final Parameter hideAdditionalResourceFields =
                            new BooleanParameter(
            "com.arsdigita.cms.contentassets.relatedlink.hide_additional_resource_fields",
            Parameter.REQUIRED,
            Boolean.FALSE);
    /**
     * Hide Additional Resource Fields on RelatedLinkPropertyForm anf
     * RelatedLink table
     */
    private final Parameter hideNewTargetWindow =
                            new BooleanParameter(
            "com.arsdigita.cms.contentassets.relatedlink.hide_new_target_window",
            Parameter.REQUIRED,
            Boolean.FALSE);
    
    
    /**
     * Constructor, don't use to instantiate. 
     * Use getInstance() instead!
     */
    public RelatedLinkConfig() {
        
        register(assetStepSortKey);
        register(hideAdditionalResourceFields);
        register(hideNewTargetWindow);
        
        loadInfo();
    }
    
    /**
     * Returns the singleton configuration record for the content section
     * environment.
     *
     * @return The <code>RelatedLinkConfig</code> record; it cannot be null
     */
    public static final RelatedLinkConfig getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new RelatedLinkConfig();
            INSTANCE.load();
        }
        return INSTANCE;
    }

    // /////////////////////////////////////////////////////////
    // Getter Section
    // /////////////////////////////////////////////////////////
    
    public Integer getAssetStepSortKey() {
        return (Integer) get(assetStepSortKey);
    }

    public final boolean isHideAdditionalResourceFields() {
        return ((Boolean) get(hideAdditionalResourceFields)).booleanValue();
    }

    public final boolean isHideNewTargetWindow() {
        return ((Boolean) get(hideNewTargetWindow)).booleanValue();
    }
    
}
