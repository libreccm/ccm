/*
 * Copyright (c) 2011 Jens Pelzetter
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 */
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

    private final static PublicPersonalProfileConfig config = new PublicPersonalProfileConfig();
    private final Parameter personType;
    private final Parameter embedded;
    private final Parameter showHomeNavEntry;
    private final Parameter homeNavItemLabels;
    private final Parameter showPersonInfoEverywhere;
    // private final Parameter contactType;
    
    /*static {
        config.load();
    }*/

    public PublicPersonalProfileConfig() {
        personType =
        new StringParameter(
                "com.arsdigita.cms.publicPersonalProfile.person_type",
                            Parameter.REQUIRED,
                            "com.arsdigita.cms.contenttypes.GenericPerson");
        
        embedded = new BooleanParameter(
                "com.arsdigita.cms.publicpersonalprofile.embedded",
                Parameter.REQUIRED,
                false);
                
        showHomeNavEntry = new BooleanParameter(
                "com.arsdigita.cms.publicpersonalprofile.showHomeNavEntry", 
                Parameter.REQUIRED, 
                true);
        
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
        
        register(personType);
        register(embedded);
        register(showHomeNavEntry);
        register(homeNavItemLabels);
        register(showPersonInfoEverywhere);
        // register(contactType);

        loadInfo();
    }

    public final String getPersonType() {
        return (String) get(personType);
    }
    
    public final Boolean getEmbedded() {
        return (Boolean) get(embedded);
    }
    
    public final boolean getShowHomeNavEntry() {
        return (Boolean) get(showHomeNavEntry);
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
    
    /*public static PublicPersonalProfileConfig getConfig() {
        return config;
    }*/
}
