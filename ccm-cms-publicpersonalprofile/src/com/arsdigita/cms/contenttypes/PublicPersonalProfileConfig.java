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
