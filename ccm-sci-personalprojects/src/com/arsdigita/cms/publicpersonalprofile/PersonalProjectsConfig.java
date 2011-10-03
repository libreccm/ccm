
package com.arsdigita.cms.publicpersonalprofile;

import com.arsdigita.runtime.AbstractConfig;
import com.arsdigita.util.parameter.IntegerParameter;
import com.arsdigita.util.parameter.Parameter;

/**
 *
 * @author Jens Pelzetter (jensp)
 * @version $Id$
 */
public class PersonalProjectsConfig extends AbstractConfig {
    
    private final Parameter groupSplit;
    
    public PersonalProjectsConfig() {
        groupSplit = new IntegerParameter(
                "com.arsdigita.cms.publicpersonalprofile.projects.groupSplit", 
                Parameter.REQUIRED, 
                16);
        
        register(groupSplit);
        
        loadInfo();
    }
    
    public final Integer getGroupSplit() {
        return (Integer) get(groupSplit);
    }
    
}
