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
import com.arsdigita.portation.conversion.NgCoreCollection;
import com.arsdigita.portation.modules.core.categorization.Categorization;
import com.arsdigita.portation.modules.core.categorization.Category;
import com.arsdigita.portation.modules.core.categorization.util.CategoryInformation;
import com.arsdigita.portation.modules.core.security.Permission;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
 * @version created on 6/15/16
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class,
                  resolver = CcmObjectIdResolver.class,
                  property = "uuid")
public class CcmObject {

    private long objectId;
    private String uuid;
    private String displayName;
    @JsonIgnore
    private List<Permission> permissions;
    @JsonIgnore
    private List<Categorization> categories;


    public CcmObject(final ACSObject trunkObject) {
        this.objectId = trunkObject.getID().longValue();

        this.uuid = UUID.randomUUID().toString();
        this.displayName = trunkObject.getDisplayName();

        this.permissions = new ArrayList<>();
        this.categories = new ArrayList<>();

        NgCoreCollection.ccmObjects.put(this.objectId, this);
    }

    // specific constructor for ldn-terms' Domain and LegalMetadata
    public CcmObject(final String displayName) {
        this(ACSObject.generateID(), displayName);
    }

    // specific constructor for sideNote asset and folders
    public CcmObject(final BigDecimal objectId, final String displayName) {
        this.objectId = objectId.longValue();

        this.uuid = UUID.randomUUID().toString();
        this.displayName = displayName;

        this.permissions = new ArrayList<>();
        this.categories = new ArrayList<>();

        NgCoreCollection.ccmObjects.put(this.objectId, this);
    }

    public long getObjectId() {
        return objectId;
    }

    public void setObjectId(final long objectId) {
        this.objectId = objectId;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(final String uuid) {
        this.uuid = uuid;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(final String displayName) {
        this.displayName = displayName;
    }

    public List<Permission> getPermissions() {
        return permissions;
    }

    public void setPermissions(final List<Permission> permissions) {
        this.permissions = permissions;
    }

    public void addPermission(final Permission permission) {
        permissions.add(permission);
    }

    public void removePermission(final Permission permission) {
        permissions.remove(permission);
    }

    public List<Categorization> getCategories() {
        return categories;
    }

    public void setCategories(final List<Categorization> categories) {
        this.categories = categories;
    }

    public void addCategory(final Categorization category) {
        categories.add(category);
    }

    public void removeCategory(final Categorization category) {
        categories.remove(category);
    }
}
