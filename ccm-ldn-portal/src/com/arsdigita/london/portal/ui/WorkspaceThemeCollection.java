package com.arsdigita.london.portal.ui;

import java.math.BigDecimal;

import com.arsdigita.domain.DomainCollection;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.util.Assert;

public class WorkspaceThemeCollection extends DomainCollection {

	public WorkspaceThemeCollection(DataCollection dataCollection) {
		super(dataCollection);
	}

	/**
	 * Get the ID for the WorkspaceTheme for the current row.
	 * 
	 * @return the id of this WorkspaceTheme.
	 */
	public BigDecimal getID() {
		BigDecimal id = (BigDecimal) m_dataCollection.get("id");

		Assert.exists(id);

		return id;
	}

	/**
	 * Get the current item as a domain object.
	 * 
	 * @return the domain object for the current row.
	 */
	public DomainObject getDomainObject() {
		DomainObject domainObject = getWorkspaceTheme();

		Assert.exists(domainObject);

		return domainObject;
	}

	/**
	 * Get the current item as a WorkspaceTheme domain object.
	 * 
	 * @return a WorkspaceTheme domain object.
	 */
	public WorkspaceTheme getWorkspaceTheme() {
		DataObject dataObject = m_dataCollection.getDataObject();

		WorkspaceTheme workspaceTheme = WorkspaceTheme
				.retrieveWorkspaceTheme(dataObject);

		Assert.exists(workspaceTheme);

		return workspaceTheme;
	}

	/**
	 * Get the name for the WorkspaceTheme for the current row.
	 * 
	 * @return the name of this WorkspaceTheme.
	 */
	public String getName() {
		String name = (String) m_dataCollection.get("theme_name");

		Assert.exists(name);

		return name;
	}

}
