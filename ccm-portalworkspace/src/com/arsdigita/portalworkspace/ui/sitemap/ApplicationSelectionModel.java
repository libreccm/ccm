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

package com.arsdigita.portalworkspace.ui.sitemap;

import org.apache.log4j.Logger;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.ui.ACSObjectSelectionModel;
import com.arsdigita.web.Application;
import com.arsdigita.web.Web;

/**
 * Selection model for applications, specifically used by portalworkspace
 * admin task to select an application instance to delete or to assign a 
 * category.
 * 
 * This is a generic task as part of application management. A workspace is a 
 * special case of this generic task. 
 * 
 * This selection model adds an (optional?) default value to the parent class 
 * which will be returned when nothing is selected.
 * 
 * Used by packages admin and sitemap 
 */
public class ApplicationSelectionModel extends ACSObjectSelectionModel {

    /** Creates a s_logging category with name = full name of class */
    private static final Logger s_log = Logger
                                    .getLogger(ApplicationSelectionModel.class);

    public static final String DEFAULT_PARAM_NAME = "app_id";

    private boolean m_hasDefaultValue;

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
        Application app = Web.getWebContext().getApplication();

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
    @Override
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
    @Override
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
    @Override
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
