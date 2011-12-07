package com.arsdigita.cms.publicpersonalprofile;

import com.arsdigita.runtime.AbstractConfig;
import com.arsdigita.util.parameter.IntegerParameter;
import com.arsdigita.util.parameter.Parameter;
import com.arsdigita.util.parameter.StringParameter;

/**
 *
 * @author Jens Pelzetter (jensp)
 * @version $Id$
 */
public class PersonalProjectsConfig extends AbstractConfig {

    private final Parameter groupSplit;
    private final Parameter sortBy;

    public PersonalProjectsConfig() {
        groupSplit = new IntegerParameter(
                "com.arsdigita.cms.publicpersonalprofile.projects.groupSplit",
                Parameter.REQUIRED,
                16);

        sortBy = new StringParameter(
                "com.arsdigita.cms.publicpersonalprofile.projects.sortBy",
                                     Parameter.REQUIRED,
                                     "date");

        register(groupSplit);
        register(sortBy);

        loadInfo();
    }

    public final Integer getGroupSplit() {
        return (Integer) get(groupSplit);
    }
    
    public final String getSortBy() {
        return (String) get(sortBy);
    }
}
