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

import com.arsdigita.portation.AbstractMarshaller;
import com.arsdigita.portation.Identifiable;
import com.arsdigita.portation.modules.core.core.CcmObject;

import java.util.Date;

/**
 * @author <a href="mailto:tosmers@uni-bremen.de>Tobias Osmers<\a>
 * @version created the 6/15/16
 */
public class Permission implements Identifiable {

    private long permissionId;
    private String grantedPrivilege;
    private CcmObject object;
    private Role grantee;
    private User creationUser;
    private Date creationDate;
    private String creationIp;

    public Permission(final com.arsdigita.kernel.permissions.Permission permission) {

    }

    @Override
    public AbstractMarshaller<? extends Identifiable> getMarshaller() {
        return new PermissionMarshaller();
    }

    public long getPermissionId() {
        return permissionId;
    }

    public void setPermissionId(long permissionId) {
        this.permissionId = permissionId;
    }

    public String getGrantedPrivilege() {
        return grantedPrivilege;
    }

    public void setGrantedPrivilege(String grantedPrivilege) {
        this.grantedPrivilege = grantedPrivilege;
    }

    public CcmObject getObject() {
        return object;
    }

    public void setObject(CcmObject object) {
        this.object = object;
    }

    public Role getGrantee() {
        return grantee;
    }

    public void setGrantee(Role grantee) {
        this.grantee = grantee;
    }

    public User getCreationUser() {
        return creationUser;
    }

    public void setCreationUser(User creationUser) {
        this.creationUser = creationUser;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public String getCreationIp() {
        return creationIp;
    }

    public void setCreationIp(String creationIp) {
        this.creationIp = creationIp;
    }
}
