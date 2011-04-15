/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.portalserver.permissions;

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
