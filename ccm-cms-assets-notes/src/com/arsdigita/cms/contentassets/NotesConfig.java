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

    private static NotesConfig INSTANCE;
    
    private final Parameter assetStepSortKey = new IntegerParameter(
            "com.arsdigita.cms.contentassets.notes.asset_step_sortkey",
            Parameter.REQUIRED,
            3);

    public NotesConfig() {

        super();

        register(assetStepSortKey);

        loadInfo();

    }

    public static final NotesConfig getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new NotesConfig();
            INSTANCE.load();
        }
        return INSTANCE;
    }

    public Integer getAssetStepSortKey() {
        return (Integer) get(assetStepSortKey);
    }
}
