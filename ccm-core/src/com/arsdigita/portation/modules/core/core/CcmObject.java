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
package com.arsdigita.portation.modules.core.core;

import com.arsdigita.kernel.ACSObject;
import com.arsdigita.portation.AbstractMarshaller;
import com.arsdigita.portation.Identifiable;
import com.arsdigita.portation.modules.core.categorization.Categorization;
import com.arsdigita.portation.modules.core.categorization.Category;
import com.arsdigita.portation.modules.core.security.Permission;

import java.util.List;

/**
 * Root class of all entities in LibreCCM which need categorisation and
 * permission services.
 *
 * This class defines several basic properties including associations to
 * {@link Category} (via the {@link Categorization} class and permissions.
 *
 * In the old hierarchy the equivalent of this class was the {@code ACSObject}
 * entity.
 *
 * We are using the {@code JOINED} inheritance strategy for the inheritance
 * hierarchy of this class to achieve modularity and to minimise duplicate data
 * in the database.
 *
 * @author <a href="mailto:tosmers@uni-bremen.de>Tobias Osmers<\a>
 * @version created the 6/15/16
 */
public class CcmObject implements Identifiable {

    private long objectId;
    private String uuid;
    private String displayName;
    private List<Permission> permissions;
    private List<Categorization> categories;


    public CcmObject(final ACSObject trunkObject) {
        this.objectId = trunkObject.getID().longValue();
        this.uuid = null;
        this.displayName = trunkObject.getDisplayName();
        this.permissions = null;// Todo: mapping
        this.categories = null;// Todo: mapping
    }

    @Override
    public AbstractMarshaller<? extends Identifiable> getMarshaller() {
        return new CcmObjectMarshaller();
    }

    public long getObjectId() {
        return objectId;
    }

    public void setObjectId(long objectId) {
        this.objectId = objectId;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public List<Permission> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<Permission> permissions) {
        this.permissions = permissions;
    }

    public List<Categorization> getCategories() {
        return categories;
    }

    public void setCategories(List<Categorization> categories) {
        this.categories = categories;
    }
}
