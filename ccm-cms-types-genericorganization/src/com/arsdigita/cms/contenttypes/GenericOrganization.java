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

/**
 * An very generic type to represent an organization.
 *
 * @author Jens Pelzetter
 */
public class GenericOrganization extends ContentPage {

    public static final String ORGANIZATIONNAME = "organizationname";

    public static final String BASE_DATA_OBJECT_TYPE = "com.arsdigita.cms.contenttypes.GenericOrganization";
    private static final GenericOrganizationConfig s_config = new GenericOrganizationConfig();

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

    public void beforeSave() {
	super.beforeSave();

	Assert.exists(getContentType(), ContentType.class);
    }

    /* accessors *************************************************/
    public String getOrganizationName() {
	return (String)get(ORGANIZATIONNAME);
    }

    public void setOrganizationName(String name) {
	set(ORGANIZATIONNAME, name);
    }
}