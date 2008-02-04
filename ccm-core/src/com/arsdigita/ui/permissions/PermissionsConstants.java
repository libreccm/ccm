/*
 * Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.ui.permissions;

import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
import com.arsdigita.globalization.GlobalizedMessage;

/**
 * This interface is used to centralize constants and labels used in
 * the Permissions UI package.
 *
 * @author Stefan Deusch 
 * @version $Id: PermissionsConstants.java 287 2005-02-22 00:29:02Z sskracic $
 */

interface PermissionsConstants {

    /**
     * These are our five default privileges.
     */

    PrivilegeDescriptor[] DEFAULT_PRIVILEGES = new PrivilegeDescriptor[] {
        PrivilegeDescriptor.READ,
        PrivilegeDescriptor.WRITE,
        PrivilegeDescriptor.CREATE,
        PrivilegeDescriptor.DELETE,
        PrivilegeDescriptor.ADMIN
    };


    String BUNDLE_NAME = "com.arsdigita.ui.permissions.PermissionsResources";

    GlobalizedMessage SEARCH_LABEL =  new GlobalizedMessage
        ("permissions.userSearchForm.label", BUNDLE_NAME);

    GlobalizedMessage SEARCH_BUTTON = new GlobalizedMessage
        ("permissions.button.search", BUNDLE_NAME);

    GlobalizedMessage SAVE_BUTTON = new GlobalizedMessage
        ("permissions.button.save", BUNDLE_NAME);

    GlobalizedMessage NO_RESULTS = new GlobalizedMessage
        ("permissions.userSearchForm.noResults", BUNDLE_NAME);


    // Direct / Indirect permissions

    GlobalizedMessage PERM_TABLE_DIRECT_HEADING = new GlobalizedMessage
        ("permissions.directPermissions.heading", BUNDLE_NAME);

    GlobalizedMessage PERM_TABLE_DIRECT_EXPLANATION = new GlobalizedMessage
        ("permissions.directPermissions.explanation", BUNDLE_NAME);

    GlobalizedMessage PERM_TABLE_INDIRECT_HEADING = new GlobalizedMessage
        ("permissions.indirectPermissions.heading", BUNDLE_NAME);

    GlobalizedMessage PERM_TABLE_INDIRECT_EXPLANATION = new GlobalizedMessage
        ("permissions.indirectPermissions.explanation", BUNDLE_NAME);

    GlobalizedMessage PERM_TABLE_INDIRECT_CONTEXT = new GlobalizedMessage
        ("permissions.indirectPermissions.context", BUNDLE_NAME);

    GlobalizedMessage PERM_TABLE_GRANTEE =  new GlobalizedMessage
        ("permissions.table.grantee", BUNDLE_NAME);

    GlobalizedMessage PERM_TABLE_ACTIONS = new GlobalizedMessage
        ("permissions.table.actions",  BUNDLE_NAME);

    GlobalizedMessage REMOVE_ALL_CONFIRM = new GlobalizedMessage
        ("permissions.table.actions.removeAll",  BUNDLE_NAME);

    GlobalizedMessage PERM_TABLE_INHERITED = new GlobalizedMessage
        ("permissions.table.inherited",  BUNDLE_NAME);

    GlobalizedMessage PERM_TABLE_NO_PARENT_CONTEXT = new GlobalizedMessage
        ("permissions.table.parent.context.null",  BUNDLE_NAME);


    // Permissions header

    GlobalizedMessage PAGE_TITLE = new GlobalizedMessage
        ("permissions.one.title", BUNDLE_NAME);

    GlobalizedMessage MAIN_SITE = new GlobalizedMessage
        ("permissions.main.site", BUNDLE_NAME);

    GlobalizedMessage PERSONAL_SITE = new GlobalizedMessage
        ("permissions.personal.site", BUNDLE_NAME);

    GlobalizedMessage PERMISSIONS_INDEX = new GlobalizedMessage
        ("permissions.index.title", BUNDLE_NAME);

    GlobalizedMessage PERMISSIONS_INDEX_NAVBAR = new GlobalizedMessage
        ("permissions.index.navbarItem", BUNDLE_NAME);

    // Permissions grant form

    GlobalizedMessage PAGE_GRANT_TITLE = new GlobalizedMessage
        ("permissions.one.grant.title",BUNDLE_NAME);

    GlobalizedMessage PAGE_GRANT_LEFT = new GlobalizedMessage
        ("permissions.one.grant.explanation.left", BUNDLE_NAME);

    GlobalizedMessage PAGE_GRANT_RIGHT = new GlobalizedMessage
        ("permissions.one.grant.explanation.right", BUNDLE_NAME);

    // Access denied page

    GlobalizedMessage PAGE_DENIED_TITLE =  new GlobalizedMessage
        ("permissions.denied.title", BUNDLE_NAME);

    // Index page

    GlobalizedMessage PAGE_OBJECT_INDEX =  new GlobalizedMessage
        ("permissions.index.adminObjects", BUNDLE_NAME);

    GlobalizedMessage PAGE_OBJECT_PANEL_TITLE = new GlobalizedMessage
        ("permissions.index.panelTitle", BUNDLE_NAME);

    GlobalizedMessage PAGE_OBJECT_NONE =  new GlobalizedMessage
        ("permissions.index.noAdminObjects", BUNDLE_NAME);

    // Flats for permission types

    int DIRECT    = 0;
    int INHERITED = 1;

    // Form constants

    String OBJECT_ID = "po_id";
    String DIRECT_PERMISSIONS = "direct";
    String INDIRECT_PERMISSIONS = "indirect";
    String SEARCH_QUERY = "query";
    String PRIV_SET = "privs_set";


    // shared query
    String RETRIEVE_USERS = "com.arsdigita.kernel.RetrieveUsers";
}
