/*
 * Copyright (C) 2001 ArsDigita Corporation. All Rights Reserved.
 *
 * The contents of this file are subject to the ArsDigita Public 
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of
 * the License at http://www.arsdigita.com/ADPL.txt
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */

package com.arsdigita.portalworkspace.ui;

import com.arsdigita.kernel.ACSObject;
import com.arsdigita.kernel.Party;
import com.arsdigita.kernel.permissions.PermissionDescriptor;
import com.arsdigita.kernel.permissions.PermissionService;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;

public class PortalHelper {

	public static boolean canBrowse(Party party, ACSObject object) {
		PermissionDescriptor perm = new PermissionDescriptor(
				PrivilegeDescriptor.READ, object, party);
		return PermissionService.checkPermission(perm);
	}

	public static boolean canCustomize(Party party, ACSObject object) {
		PermissionDescriptor perm = new PermissionDescriptor(
				PrivilegeDescriptor.ADMIN, object, party);
		return PermissionService.checkPermission(perm);
	}
}
