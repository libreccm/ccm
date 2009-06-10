/*
 * Copyright (C) 2009 Jens Pelzetter, for the Center of Social Politics of the University of Bremen
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
package com.arsdigita.cms.contenttypes;

import com.arsdigita.cms.ContentPage;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.util.Assert;
import java.math.BigDecimal;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.domain.DomainObjectFactory;

import org.apache.log4j.Logger;

/**
 * This represents an organization. It is designed to be suitable
 * for most organizations. Currently, it offers the following properties:
 * - name of the organization
 * - an addendum for the name
 * - a short description of the organization.
 *
 * It is also possible to add roles to the organization, e.g. CEO, mayor or others.
 * The following features are planned to implement in one of the next commits:
 * - Ability to add persons (ccm-cms-types-person) to a role
 * - Adding OrganizationUnits
 *
 * The current version of this contenttype is modeled on base on the MultipartArticle
 * contenttype.
 *
 * @author Jens Pelzetter
 */
public class GenericOrganization extends ContentPage {

    /**
     * The name of the organization (name can't be used as shorter name here
     * because name is already used in one of parent classes, and is used for
     * internal purposes also.
     */
    public static final String ORGANIZATIONNAME = "organizationname";
    /**
     * Addendum for the name of the organization.
     */
    public static final String ORGANIZATIONNAMEADDENDUM = "organizationnameaddendum";
    /**
     * A short description of the organization.
     */
    public static final String ORGANIZATIONDESCRIPTION = "description";
    /**
     * Roles associated with the organization.
     */
    public static final String ROLES = "roles";
    /**
     * Type of this class (used for internal purposed).
     */
    public static final String BASE_DATA_OBJECT_TYPE = "com.arsdigita.cms.contenttypes.GenericOrganization";
    private static final GenericOrganizationConfig s_config = new GenericOrganizationConfig();
    private static final Logger s_log = Logger.getLogger(GenericOrganization.class);

    /**
     * Called when the class is loaded by the Java class loader.
     */
    static {
        s_config.load();
    }

    /**
     * Returns a possibly existing configuration object for the class.
     *
     * @return config object
     */
    public static final GenericOrganizationConfig getConfig() {
        return s_config;
    }

    /**
     * Default constructor. This creates a new (empty) organization
     */
    public GenericOrganization() {
        this(BASE_DATA_OBJECT_TYPE);
    }

    /**
     * Trys to find an organization in the database by its id.
     *
     * @param id ID of the object to (re-)create
     * @throws DataObjectNotFoundException if no object with the given id is found in the database.
     */
    public GenericOrganization(BigDecimal id) throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    /**
     * Trys to find an organization in the database by its OID.
     *
     * @param id ID of the object to (re-)create
     * @throws DataObjectNotFoundException if no object with the given OID is found in the database.
     */
    public GenericOrganization(OID id) throws DataObjectNotFoundException {
        super(id);
    }

    /**
     * Create an new GenericOrganization object from a DataObject
     *
     * @param obj The data object
     */
    public GenericOrganization(DataObject obj) {
        super(obj);
    }

    /**
     * Not sure for what this constructor is.
     *
     * @param type The type of the object to create (?)
     */
    public GenericOrganization(String type) {
        super(type);
    }


    /* accessors *************************************************/

    /**
     * Gets the name of the organization.
     *
     * @return The name of the organization.
     */
    public String getOrganizationName() {
        return (String) get(ORGANIZATIONNAME);
    }

    /**
     *  Sets the name of the organization.
     *
     * @param name The (new) name of the organization.
     */
    public void setOrganizationName(String name) {
        set(ORGANIZATIONNAME, name);
    }

    /**
     *
     * @return Addendum for the name of the organization.
     */
    public String getOrganizationNameAddendum() {
        return (String) get(ORGANIZATIONNAMEADDENDUM);
    }

    /**
     * Sets the the addenum property of the organization.
     *
     * @param addendum The new value for the addendum property.
     */
    public void setOrganizationNameAddendum(String addendum) {
        set(ORGANIZATIONNAMEADDENDUM, addendum);
    }

    /**
     *
     * @return Description of the organization, if any.
     */
    public String getOrganizationDescription() {
        return (String) get(DESCRIPTION);
    }

    /**
     * Sets the description of the organization.
     *
     * @param description The (new) description.
     */
    public void setOrganizationDescription(String description) {
        set(DESCRIPTION, description);
    }

    /**
     *
     * @return All roles associated with this organization.
     */
    public OrganizationRoleCollection getOrganizationRoles() {
        return new OrganizationRoleCollection((DataCollection) get(ROLES));
    }

    /**
     * Adds a role to a organization.
     *
     * @param organizationRole The role to add.
     */
    public void addOrganizationRole(OrganizationRole organizationRole) {
        Assert.exists(organizationRole, OrganizationRole.class);
        add(ROLES, organizationRole);
    }

    /**
     * Removes a role from a organization.
     *
     * @param organizationRole The role to remove.
     */
    public void removeOrganizationRole(OrganizationRole organizationRole) {
        Assert.exists(organizationRole, OrganizationRole.class);
        remove(ROLES, organizationRole);
    }
}