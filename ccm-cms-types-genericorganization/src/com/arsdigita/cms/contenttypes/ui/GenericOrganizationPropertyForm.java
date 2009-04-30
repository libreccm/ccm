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

package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.event.ParameterEvent;
import com.arsdigita.bebop.event.ParameterListener;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.SingleSelect;
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.bebop.parameters.StringInRangeValidationListener;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.bebop.parameters.URLValidationListener;
import com.arsdigita.bebop.parameters.ParameterData;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.GenericOrganization;
import com.arsdigita.cms.ui.authoring.BasicPageForm;
import com.arsdigita.domain.DomainCollection;
import org.apache.log4j.Logger;
import com.arsdigita.cms.util.GlobalizationUtil;


/**
 * Form to edit the properties of an organization.
 *
 * @author: Jens Pelzetter
 */
public class GenericOrganizationPropertyForm extends BasicPageForm implements FormProcessListener, FormInitListener, FormSubmissionListener {
    
    private static final Logger s_log = Logger.getLogger(GenericOrganizationPropertyForm.class);

    private GenericOrganizationPropertiesStep m_step;

    public static final String ORGANIZATIONNAME = GenericOrganization.ORGANIZATIONNAME;

    public static final String ID = "GenericOrganization_edit";

    public GenericOrganizationPropertyForm(ItemSelectionModel itemModel) {
	this(itemModel, null);
    }
    
    public GenericOrganizationPropertyForm(ItemSelectionModel itemModel, GenericOrganizationPropertiesStep step) {
	super(ID, itemModel);
	m_step = step;
	addSubmissionListener(this);
    }

    protected void addWidgets() {
	super.addWidgets();

	add(new Label(GlobalizationUtil.globalize("cms.contenttypes.ui.genericorganization.organizationname")));
	ParameterModel nameParam = new StringParameter(ORGANIZATIONNAME);
	TextField name = new TextField(nameParam);
	add(name);	   		      
    }

    public void init(FormSectionEvent fse) {
	FormData data = fse.getFormData();
	GenericOrganization orga = (GenericOrganization)super.initBasicWidgets(fse);

	data.put(ORGANIZATIONNAME, orga.getOrganizationName());	
    }

    public void submitted(FormSectionEvent fse) {
	if (m_step != null 
	    && getSaveCancelSection().getCancelButton().isSelected(fse.getPageState())) {
	    m_step.cancelStreamlinedCreation(fse.getPageState());
	}
    }

    public void process(FormSectionEvent fse) {
	FormData data = fse.getFormData();

	GenericOrganization orga = (GenericOrganization)super.processBasicWidgets(fse);

	if (orga != null
	    && getSaveCancelSection().getSaveButton().isSelected(fse.getPageState())) {
	    orga.setName((String)data.get(ORGANIZATIONNAME));

	    orga.save();
	}

	if (m_step != null) {
	    m_step.maybeForwardToNextStep(fse.getPageState());
	}
    }
}


