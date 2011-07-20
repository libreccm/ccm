package com.arsdigita.cms.contenttypes;

import com.arsdigita.runtime.AbstractConfig;
import com.arsdigita.util.parameter.BooleanParameter;
import com.arsdigita.util.parameter.StringParameter;
import com.arsdigita.util.parameter.Parameter;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class PublicPersonalProfileConfig extends AbstractConfig {

    private final Parameter showUnfinishedParts;
    private final Parameter personType;

    public PublicPersonalProfileConfig() {
        showUnfinishedParts =
        new BooleanParameter(
                "com.arsdigita.cms.contenttypes.PublicPersonalProfile.show_unfinished_parts",
                Parameter.REQUIRED,
                Boolean.FALSE);
        personType =
        new StringParameter(
                "com.arsdigita.cms.contenttypes.PublicPersonalProfile.person_type",
                            Parameter.REQUIRED,
                            "com.arsdigita.cms.contenttypes.GenericPerson");

        register(showUnfinishedParts);
        register(personType);

        loadInfo();
    }

    public final boolean getShowUnFinishedParts() {
        return (Boolean) get(showUnfinishedParts);
    }

    public final String getPersonType() {
        return (String) get(personType);
    }
}
