/*
 * Copyright (c) 2015 Jens Pelzetter
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
package com.arsdigita.cms.formbuilder;

import com.arsdigita.runtime.AbstractConfig;
import com.arsdigita.util.parameter.BooleanParameter;
import com.arsdigita.util.parameter.IntegerParameter;
import com.arsdigita.util.parameter.StringParameter;
import com.arsdigita.util.parameter.Parameter;

public class FormItemConfig extends AbstractConfig {

    private static FormItemConfig config;

    /**
     * Enable honeypot field for spam protection?
     */
    private final Parameter honeypotEnabled;

    /**
     * Name of the honeypot field
     */
    private final Parameter honeypotName;

    /**
     * Enable mininium time check. If the form is filled out in
     * a very small time span the user is considered to be a bot.
     */
    private final Parameter minTimeCheckEnabled;

    /**
     * Minimum time for the min time check in milliseconds.
     */
    private final Parameter minTimeCheckPeriod;

    public static FormItemConfig getConfig() {
        if (config == null) {
            config = new FormItemConfig();
            config.load();
        } 

        return config;
    }

    public FormItemConfig() {
        honeypotEnabled = new BooleanParameter(
            "com.arsdigita.cms.formbuilder.formitem.honeypot_enabled",
            Parameter.REQUIRED,
            Boolean.FALSE);

        honeypotName = new StringParameter(
            "com.arsdigita.cms.formbuilder.formitem.honeypot_name",
            Parameter.REQUIRED,
            "your-homepage");

        minTimeCheckEnabled = new BooleanParameter(
            "com.arsdigita.cms.formbuilder.formitem.min_time_check_enabled",
            Parameter.REQUIRED,
            Boolean.FALSE);

        minTimeCheckPeriod = new IntegerParameter(
            "com.arsdigita.cms.formbuilder.formitem_min_time_check_period",
            Parameter.REQUIRED,
            1500);
        
        register(honeypotEnabled);
        register(honeypotName);
        register(minTimeCheckEnabled);
        register(minTimeCheckPeriod);

        loadInfo();
    }

    public Boolean isHoneypotEnabled() {
        return (Boolean) get(honeypotEnabled);
    }

    public String getHoneypotName() {
        return (String) get(honeypotName);
    }

    public Boolean isMinTimeCheckEnabled() {
        return (Boolean) get(minTimeCheckEnabled);
    }

    public Integer getMinTimeCheckPeriod() {
        return (Integer) get(minTimeCheckPeriod);
    }
}
