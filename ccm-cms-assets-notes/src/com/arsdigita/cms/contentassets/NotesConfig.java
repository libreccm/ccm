package com.arsdigita.cms.contentassets;

import com.arsdigita.runtime.AbstractConfig;
import com.arsdigita.util.parameter.IntegerParameter;
import com.arsdigita.util.parameter.Parameter;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class NotesConfig extends AbstractConfig {

    private static final NotesConfig INSTANCE = new NotesConfig();

    static {
        INSTANCE.load();
    }

    private final Parameter assetStepSortKey = new IntegerParameter(
            "com.arsdigita.cms.contentassets.notes.asset_step_sortkey",
            Parameter.REQUIRED,
            3);

    protected NotesConfig() {
        
        super();
        
        register(assetStepSortKey);
        
        loadInfo();
        
    }
    
    public static final NotesConfig getInstance() {
        return INSTANCE;
    }
    
    public Integer getAssetStepSortKey() {
        return (Integer) get(assetStepSortKey);
    }

}
