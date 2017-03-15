/*
 * Copyright (C) 2015 LibreCCM Foundation.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package com.arsdigita.portation.modules.core.security;

import com.arsdigita.kernel.ACSObject;
import com.arsdigita.portation.AbstractMarshaller;
import com.arsdigita.portation.Portable;
import com.arsdigita.portation.conversion.NgCollection;
import com.arsdigita.portation.modules.core.core.CcmObject;
import com.arsdigita.portation.modules.core.security.util.PermissionIdMapper;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author <a href="mailto:tosmers@uni-bremen.de>Tobias Osmers<\a>
 * @version created on 6/15/16
 */
@JsonIdentityInfo(generator = PermissionIdGenerator.class,
                  resolver = PermissionIdResolver.class,
                  property = "customPermId")
public class Permission implements Portable {

    private long permissionId;
    private String grantedPrivilege;
    @JsonIdentityReference(alwaysAsId = true)
    private CcmObject object;
    @JsonIdentityReference(alwaysAsId = true)
    private Role grantee;
    private User creationUser;
    private Date creationDate;
    private String creationIp;

    public Permission(final com.arsdigita.kernel.permissions.Permission
                              trunkPermission) {
        long oldId = ((BigDecimal) trunkPermission.getACSObject().get("id"))
                .longValue()
                + ((BigDecimal) trunkPermission.getPartyOID().get("id"))
                .longValue();
        this.permissionId = ACSObject.generateID().longValue();
        PermissionIdMapper.map.put(oldId, this.permissionId);

        this.grantedPrivilege = trunkPermission.getPrivilege().getName();

        //this.object;
        //this.grantee;

        //this.creationUser
        this.creationDate = trunkPermission.getCreationDate();
        this.creationIp = trunkPermission.getCreationIP();

        NgCollection.permissions.put(this.permissionId, this);
    }

    /**
     * Constructor to copy a given Permission. Needed for purposes of
     * creating permissions for multiple grantees from the trunk object.
     *
     * @param ngPermission The Permission to be copied.
     */
    public Permission(final Permission ngPermission) {
        this.permissionId = ACSObject.generateID().longValue();
        this.grantedPrivilege = ngPermission.getGrantedPrivilege();

        this.object = ngPermission.getObject();
        this.grantee = ngPermission.getGrantee();

        this.creationUser = ngPermission.getCreationUser();
        this.creationDate = ngPermission.getCreationDate();
        this.creationIp = ngPermission.getCreationIp();

        NgCollection.permissions.put(this.permissionId, this);

    }

    @Override
    public AbstractMarshaller<? extends Portable> getMarshaller() {
        return new PermissionMarshaller();
    }

    public long getPermissionId() {
        return permissionId;
    }

    public void setPermissionId(final long permissionId) {
        this.permissionId = permissionId;
    }

    public String getGrantedPrivilege() {
        return grantedPrivilege;
    }

    public void setGrantedPrivilege(final String grantedPrivilege) {
        this.grantedPrivilege = grantedPrivilege;
    }

    public CcmObject getObject() {
        return object;
    }

    public void setObject(final CcmObject object) {
        this.object = object;
    }

    public Role getGrantee() {
        return grantee;
    }

    public void setGrantee(final Role grantee) {
        this.grantee = grantee;
    }

    public User getCreationUser() {
        return creationUser;
    }

    public void setCreationUser(final User creationUser) {
        this.creationUser = creationUser;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(final Date creationDate) {
        this.creationDate = creationDate;
    }

    public String getCreationIp() {
        return creationIp;
    }

    public void setCreationIp(final String creationIp) {
        this.creationIp = creationIp;
    }
}
