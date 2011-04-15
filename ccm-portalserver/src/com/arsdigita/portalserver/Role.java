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
package com.arsdigita.portalserver;


import com.arsdigita.portalserver.util.GlobalizationUtil; 

import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.kernel.Group;
import com.arsdigita.kernel.permissions.PermissionService;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import java.math.BigDecimal;

import org.apache.log4j.Logger;

public class Role extends Group {
    public static final String versionId = "$Id: //portalserver/dev/src/com/arsdigita/portalserver/Role.java#6 $";

    public static final String BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.workspace.Role";
    public static final String MEMBER_TYPE = "members";

    private static final Logger s_log = Logger.getLogger(Role.class);
    private PortalSite m_portalsite;

    protected String getBaseDataObjectType() {
        return BASE_DATA_OBJECT_TYPE;
    }

    public Role(DataObject obj) {
        super(obj);
    }

    public Role(OID oid) throws DataObjectNotFoundException {
        super(oid);
    }

    public Role(BigDecimal id) throws DataObjectNotFoundException {
        super(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    protected Role(PortalSite psite, String roleName, String assigneeTitle,
                   String description, boolean isSystem) {
        super(BASE_DATA_OBJECT_TYPE);
        setPortalSite(psite);
        setSystem(isSystem);
        setRoleNameInternal(roleName);
        setAssigneeTitleInternal(assigneeTitle);
        setDescriptionInternal(description);
    }

    /**
     * Creates a user defined role, as opposed to a system role.
     **/
    public static Role createRole(PortalSite psite, String roleName,
                                  String assigneeTitle, String description) {
      return createRole(psite, roleName, assigneeTitle, description, false);
    }

    /**
     * Creates a role in the specified portal.
     *
     * @param portalsite the portal site in which to create the role
     * @param roleName the name of the role to be created
     * @param isSystem whether or not the role should be created as a system
     * role.
     **/
    public static Role createRole(PortalSite psite, String roleName,
                                  String assigneeTitle, String description,
                                  boolean isSystem) {
        return new Role(psite, roleName, assigneeTitle, description, isSystem);
    }

    public static Role createRole(PortalSite psite, String roleName,
                                  String assigneeTitle, String description,
                                  boolean isSystem, String roleType) {
        Role role = new Role(psite, roleName, assigneeTitle, description, isSystem);

        if ( s_log.isDebugEnabled() ) {
            s_log.debug("creating role ", new Throwable());
        }

        role.setRoleTypeInternal(roleType);

        return role;
    }

    public String getRoleName() {
        return (String) get("roleName");
    }

    /**
     * Modify the role name. This will also change the group name to
     * appropriately reflect the new role name and the workspace title.
     * The role name of a system role can not be modified.
     *
     * @throws RuntimeException when this is a system role
     **/
    public void setRoleName(String roleName) {
        systemCheck();
        setRoleNameInternal(roleName);
    }

    private void setRoleNameInternal(String roleName) {
        set("roleName", roleName);
        setName(getPortalSite().getTitle() + ": " + roleName);
    }

    public String getAssigneeTitle() {
        return (String) get("assigneeTitle");
    }

    public void setAssigneeTitle(String title) {
        systemCheck();
        setAssigneeTitleInternal(title);
    }

    private void setAssigneeTitleInternal(String title) {
        set("assigneeTitle", title);
    }

    public String getDescription() {
        return (String) get("description");
    }

    public void setDescription(String description) {
        systemCheck();
        setDescriptionInternal(description);
    }

    private void setDescriptionInternal(String description) {
        set("description", description);
    }

    private void setRoleTypeInternal(String type) {
        set("type", type);
    }

    public PortalSite getPortalSite() {
        if (m_portalsite == null) {
            m_portalsite = (PortalSite)
                DomainObjectFactory.newInstance((DataObject) get("workspace"));
        }

        return m_portalsite;
    }

    private void setPortalSite(PortalSite portalsite) {
        setAssociation("workspace", portalsite);
        m_portalsite = portalsite;
    }

    private void setSystem(boolean isSystem) {
        set("isSystem", isSystem ? Boolean.TRUE : Boolean.FALSE);
    }

    /**
     * Returns whether or not this is a system role. A system role can not
     * have its name changed, and can not be deleted.
     *
     * @return whether or not this is a system role
     **/
    public boolean isSystem() {
        return Boolean.TRUE.equals(get("isSystem"));
    }

    private void systemCheck() {
        if (isSystem()) {
            throw new RuntimeException("Can not modify system roles.");
        }
    }

    /**
     * <p>Delete the Role.  This method removes all members and
     * subgroups of the Role, and revokes all permission grants.</p>
     **/
    public void delete() {
        if (isSystem()) {
            throw new RuntimeException("Can't delete system roles");
        }

        clearMembers();
        clearSubgroups();
        PermissionService.revokePartyPermissions(getOID());

        super.delete();
    }

    protected void afterSave() {
        PermissionService.setContext(this, m_portalsite);
        super.afterSave();
    }
}
