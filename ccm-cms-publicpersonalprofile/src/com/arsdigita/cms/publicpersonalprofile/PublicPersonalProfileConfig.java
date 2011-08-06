package com.arsdigita.cms.publicpersonalprofile;

import com.arsdigita.runtime.AbstractConfig;
import com.arsdigita.util.parameter.Parameter;
import com.arsdigita.util.parameter.StringParameter;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class PublicPersonalProfileConfig extends AbstractConfig {

    private final Parameter homeNavItemLabels;

    public PublicPersonalProfileConfig() {
        homeNavItemLabels = new StringParameter(
                "com.arsdigita.cms.publicpersonalprofile.navitem.home.labels",
                                                Parameter.REQUIRED,
                                                "en:Home, de:Allgemein");
        
        register(homeNavItemLabels);
        
        loadInfo();
    }
    
    public final String getHomeNavItemLabels() {
        return (String) get(homeNavItemLabels);
    }
}
