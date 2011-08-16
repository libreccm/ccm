package com.arsdigita.cms.publicpersonalprofile;

import com.arsdigita.runtime.AbstractConfig;
import com.arsdigita.util.parameter.BooleanParameter;
import com.arsdigita.util.parameter.Parameter;
import com.arsdigita.util.parameter.StringParameter;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class PublicPersonalProfileConfig extends AbstractConfig {

    private final Parameter homeNavItemLabels;
    private final Parameter showPersonInfoEverywhere;
    // private final Parameter contactType;

    public PublicPersonalProfileConfig() {
        homeNavItemLabels = new StringParameter(
                "com.arsdigita.cms.publicpersonalprofile.navitem.home.labels",
                Parameter.REQUIRED,
                "en:Home, de:Allgemein");

        showPersonInfoEverywhere =
        new BooleanParameter(
                "com.arsdigita.cms.publicpersonalprofile.show_person_info_everywhere",
                Parameter.REQUIRED,
                false);

        /*   contactType = new StringParameter(
        "com.arsdigita.cms.publicpersonalprofile.contactType",
        Parameter.REQUIRED,
        "commonContact");*/

        register(homeNavItemLabels);
        register(showPersonInfoEverywhere);
        // register(contactType);

        loadInfo();
    }

    public final String getHomeNavItemLabels() {
        return (String) get(homeNavItemLabels);
    }

    public final Boolean getShowPersonInfoEverywhere() {
        return (Boolean) get(showPersonInfoEverywhere);
    }
    /*  public final String getContactType() {
    return (String) get(contactType);
    }*/
}
