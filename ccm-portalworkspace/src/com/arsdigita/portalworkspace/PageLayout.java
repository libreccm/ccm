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
 */

package com.arsdigita.portalworkspace;

import java.math.BigDecimal;
import java.sql.SQLException;

import com.arsdigita.db.Sequences;
import com.arsdigita.domain.DomainCollection;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.util.StringUtils;
import com.arsdigita.util.UncheckedWrapperException;

public class PageLayout extends DomainObject {

	public static final String ID = "id";

	public static final String TITLE = "title";

	public static final String DESCRIPTION = "description";

	public static final String FORMAT = "format";

	public static final String FORMAT_ONE_COLUMN = "100%";

	public static final String FORMAT_TWO_COLUMNS = "50%,50%";

	public static final String FORMAT_THREE_COLUMNS = "30%,40%,30%";

	public static final String FORMAT_FOUR_COLUMNS = "25%,25%,25%,25%";

	public static final String BASE_DATA_OBJECT_TYPE = 
                               "com.arsdigita.portalworkspace.PageLayout";

	public PageLayout() {
		this(BASE_DATA_OBJECT_TYPE);
	}

	public PageLayout(String type) {
		super(type);
	}

	public PageLayout(DataObject dobj) {
		super(dobj);
	}

	public PageLayout(OID oid) {
		super(oid);
	}

	public void initialize() {
		super.initialize();

		if (isNew()) {
			try {
				set(ID, Sequences.getNextValue());
			} catch (SQLException ex) {
				throw new UncheckedWrapperException("cannot set id", ex);
			}
		}
	}

	public static PageLayout getDefaultLayout() {
		return findLayoutByFormat(Workspace.getConfig().getDefaultLayout());
	}

	public static PageLayout findLayoutByFormat(String format) {
		DomainCollection layouts = retrieveAll();
		layouts.addEqualsFilter(FORMAT, format);

		if (layouts.next()) {
			PageLayout layout = (PageLayout) layouts.getDomainObject();
			layouts.close();
			return layout;
		}
		throw new RuntimeException("Cannot find page layout for " + format);
	}

	public static PageLayout create(String title, String description,
			String format) {
		PageLayout page = new PageLayout();
		page.setup(title, description, format);
		return page;
	}

	protected void setup(String title, String description, String format) {
		setTitle(title);
		setDescription(description);
		setFormat(format);
	}

	public static DomainCollection retrieveAll() {
		DataCollection layouts = SessionManager.getSession().retrieve(
				BASE_DATA_OBJECT_TYPE);
		return new DomainCollection(layouts);
	}

	public BigDecimal getID() {
		return (BigDecimal) get(ID);
	}

	public void setTitle(String title) {
		set(TITLE, title);
	}

	public String getTitle() {
		return (String) get(TITLE);
	}

	public void setDescription(String description) {
		set(DESCRIPTION, description);
	}

	public String getDescription() {
		return (String) get(DESCRIPTION);
	}

	public void setFormat(String format) {
		set(FORMAT, format);
	}

	public String getFormat() {
		return (String) get(FORMAT);
	}

	public int getColumns() {
		String[] bits = StringUtils.split(getFormat(), ',');
		return bits.length;
	}
}
