package com.arsdigita.cms.contenttypes;

import com.arsdigita.runtime.AbstractConfig;
import com.arsdigita.util.parameter.BooleanParameter;
import com.arsdigita.util.parameter.StringParameter;
import com.arsdigita.util.parameter.Parameter;
import javax.swing.text.StyledEditorKit.BoldAction;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class SciPublicPersonalProfileConfig extends AbstractConfig {

    private final Parameter showUnfinishedParts;

    public SciPublicPersonalProfileConfig() {
        showUnfinishedParts =
        new BooleanParameter(
                "com.arsdigita.cms.contenttypes.SciPublicPersonalProfile.show_unfinished_parts",
                             Parameter.REQUIRED,
                             Boolean.FALSE);
        
        register(showUnfinishedParts);
        
        loadInfo();
    }
    
    public final boolean getShowUnFinishedParts() {
        return (Boolean) get(showUnfinishedParts);
    }
}
