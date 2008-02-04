/*
 * Copyright (C) 2001, 2002, 2003 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the CCM Public
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of
 * the License at http://www.redhat.com/licenses/ccmpl.html
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */

package com.arsdigita.london.portal.ui.admin;

import com.arsdigita.bebop.form.*;
import com.arsdigita.bebop.parameters.*;
import com.arsdigita.kernel.permissions.*;

class PrivilegeSingleSelect extends SingleSelect {
    public PrivilegeSingleSelect(ParameterModel param) {
        super(param);
        for (int i = 0; i < Grant.s_interestingPrivileges.length; i++) {
            PrivilegeDescriptor priv = Grant.s_interestingPrivileges[i];
            String display = Grant.s_privilegePrettyNames[i];
            addOption(new Option(priv.getName(), display));
        }
    }
}
