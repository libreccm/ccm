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

import com.arsdigita.cms.ContentType;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ContentPage;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.util.Assert;
import java.math.BigDecimal;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataAssociation;
import com.arsdigita.persistence.DataAssociationCursor;
import com.arsdigita.persistence.DataOperation;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.SessionManager;

import org.apache.log4j.Logger;


/**
 * An very generic type to represent an organization.
 *
 * @author Jens Pelzetter
 */
public class GenericOrganization extends ContentPage {

    public static final String ORGANIZATIONNAME = "organizationname";
    public static final String ORGANIZATIONNAMEADDENDUM = "organizationnameaddendum";
    public static final String DESCRIPTION = "description";
    public static final String ROLES = "roles";

    public static final String BASE_DATA_OBJECT_TYPE = "com.arsdigita.cms.contenttypes.GenericOrganization";

    private static final GenericOrganizationConfig s_config = new GenericOrganizationConfig();

    private static final Logger s_log = Logger.getLogger(GenericOrganization.class);

     static {
 	s_config.load();
     }

     public static final GenericOrganizationConfig getConfig () {
 	return s_config;
     }

    /**
     * Default constructor. This creates a new (empty) organization
     */
    public GenericOrganization() {
	this(BASE_DATA_OBJECT_TYPE);
    }

    public GenericOrganization(BigDecimal id) throws DataObjectNotFoundException {
	this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    public GenericOrganization(OID id) throws DataObjectNotFoundException {
	super(id);
    }

    public GenericOrganization(DataObject obj) {
	super(obj);
    }

    public GenericOrganization(String type) {
	super(type);
    }


    /* accessors *************************************************/
    public String getOrganizationName() {
	return (String)get(ORGANIZATIONNAME);
    }

    public void setOrganizationName(String name) {
	set(ORGANIZATIONNAME, name);
    }

    public String getOrganizationNameAddendum() {
	return (String)get(ORGANIZATIONNAMEADDENDUM);
    }

    public void setOrganizationNameAddendum(String addendum) {
	set(ORGANIZATIONNAMEADDENDUM, addendum);
    }

    public String getDescription() {
	return (String)get(DESCRIPTION);
    }

    public void setDescription(String description) {
	set(DESCRIPTION, description);
    }

    public OrganizationRoleCollection getOrganizationRoles() {
	return new OrganizationRoleCollection((DataCollection) get(ROLES));
    }

    public void addOrganizationRole(OrganizationRole organizationRole) {
	Assert.exists(organizationRole, OrganizationRole.class);
	add(ROLES, organizationRole);
    }

    public void removeOrganizationRole(OrganizationRole organizationRole) {
	Assert.exists(organizationRole, OrganizationRole.class);
	remove(ROLES, organizationRole);
    }
    
}