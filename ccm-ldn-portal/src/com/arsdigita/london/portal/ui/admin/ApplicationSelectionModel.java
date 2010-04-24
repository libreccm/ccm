/*
 * Copyright (C) 2001 ArsDigita Corporation. All Rights Reserved.
 *
 * The contents of this file are subject to the ArsDigita Public 
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of
 * the License at http://www.arsdigita.com/ADPL.txt
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */

package com.arsdigita.london.portal.ui.admin;

import org.apache.log4j.Logger;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.ui.ACSObjectSelectionModel;
import com.arsdigita.web.Application;
import com.arsdigita.web.Web;

/**
 * This selection model has a default value which will be return when nothing is
 * selected.
 */

public class ApplicationSelectionModel extends ACSObjectSelectionModel {

	// public static final BigDecimalParameter PARAM =
	// (BigDecimalParameter)PermissionsPane.getObjectIDParam();

	public static final String DEFAULT_PARAM_NAME = "app_id";

	private boolean m_hasDefaultValue;

	private static final Logger s_log = Logger
			.getLogger(ApplicationSelectionModel.class);

	/**
     * Constructor 
     * 
     * @param param
     * @param hasDefaultValue
     */
    public ApplicationSelectionModel(BigDecimalParameter param,
			                                                     boolean hasDefaultValue) {
		super(param);

		m_hasDefaultValue = hasDefaultValue;
	}

	/**
     * 
     * @param hasDefaultValue
     */
    public ApplicationSelectionModel(boolean hasDefaultValue) {
		this(new BigDecimalParameter(DEFAULT_PARAM_NAME), hasDefaultValue);
	}

	/**
     * 
     * @param param
     */
    public ApplicationSelectionModel(String param) {
		this(new BigDecimalParameter(param), false);
	}

	/**
     * 
     * @param param
     * @param hasDefaultValue
     */
    public ApplicationSelectionModel(String param, boolean hasDefaultValue) {
		this(new BigDecimalParameter(param), hasDefaultValue);
	}

	/**
     * 
     */
    public ApplicationSelectionModel() {
		this(DEFAULT_PARAM_NAME, false);
	}

	/**
     * 
     * @return
     */
    public Application getDefaultApplication() {
		Application app = Web.getContext().getApplication();

		// XXX just in case
		if (app == null) {
			s_log.debug("Using kernel.getContext().getResource() instead");
			app = (Application) Kernel.getContext().getResource();
		}

		if (app != null) {
			s_log.debug("Default Application: " + app.getID());
		} else {
			s_log.debug("No application found");
		}
		return app;
	}

	/**
	 * Override ACSObjectSelectionModel methods to default to the default
	 * Application
	 */
	public boolean isSelected(PageState state) {
		if (m_hasDefaultValue && !super.isSelected(state)) {
			return (getDefaultApplication() != null);
		}
		return super.isSelected(state);
	}

	/**
     * 
     * @param state
     * @return
     */
    public DomainObject getSelectedObject(PageState state) {
		if (m_hasDefaultValue && !super.isSelected(state)) {
			return getDefaultApplication();
		}

		return super.getSelectedObject(state);
	}

	/**
     * 
     * @param state
     * @return
     */
    public Object getSelectedKey(PageState state) {
		if (m_hasDefaultValue && !super.isSelected(state)) {
			return getDefaultApplication();
		}
		return super.getSelectedKey(state);
	}

	/** Utility methods */
	public Application getSelectedApplication(PageState state) {
		return (Application) getSelectedObject(state);
	}

	/**
     * Utility method
     * 
     * @param state
     * @param Application
     */
    public void setSelectedApplication(PageState state, Application Application) {
		setSelectedObject(state, Application);
	}
}
