/*
 * Copyright (c) 2013 Jens Pelzetter
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
package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.runtime.AbstractConfig;
import com.arsdigita.util.parameter.BooleanParameter;
import com.arsdigita.util.parameter.Parameter;

/**
 * Configuration for the summary tab of a SciDepartment.
 *
 * @author Jens Pelzetter
 * @version $Id$
 */
public class SciDepartmentSummaryTabConfig extends AbstractConfig {

    private final Parameter showHeads;
    private final Parameter showViceHeads;
    private final Parameter showSecretariat;
    private final Parameter showRoleContacts;
    private final Parameter showSubDepartments;
    private final Parameter showContacts;

    public SciDepartmentSummaryTabConfig() {
        super();

        showHeads = new BooleanParameter(
                "com.arsdigita.cms.contenttypes.scidepartment.summarytab.heads.show",
                Parameter.REQUIRED,
                true);

        showViceHeads = new BooleanParameter(
                "com.arsdigita.cms.contenttypes.scidepartment.summarytab.viceheads.show",
                Parameter.REQUIRED,
                true);

        showSecretariat = new BooleanParameter(
                "com.arsdigita.cms.contenttypes.scidepartment.summarytab.secretariat.show",
                Parameter.REQUIRED,
                true);

        showRoleContacts = new BooleanParameter(
                "com.arsdigita.cms.contenttypes.scidepartment.summarytab.role_contacts.show",
                Parameter.REQUIRED,
                true);

        showSubDepartments = new BooleanParameter(
                "com.arsdigita.cms.contenttypes.scidepartment.summarytab.subdepartments.show",
                Parameter.REQUIRED,
                true);

        showContacts = new BooleanParameter(
                "com.arsdigita.cms.contenttypes.scidepartment.summarytab.contacts.show",
                Parameter.REQUIRED,
                true);

        register(showHeads);
        register(showViceHeads);
        register(showSecretariat);
        register(showRoleContacts);
        register(showSubDepartments);
        register(showContacts);

        loadInfo();
    }

    public final boolean isShowingHead() {
        return (Boolean) get(showHeads);
    }

    public final boolean isShowingViceHead() {
        return (Boolean) get(showViceHeads);
    }

    public final boolean isShowingSecretriat() {
        return (Boolean) get(showSecretariat);
    }
    
    public final boolean isShowingRoleContacts() {
        return (Boolean) get(showRoleContacts);
    }

    public final boolean isShowingSubDepartment() {
        return (Boolean) get(showSubDepartments);
    }

    public final boolean isShowingContacts() {
        return (Boolean) get(showContacts);
    }

}
