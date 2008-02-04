/*
 * Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.Service;
import com.arsdigita.cms.ui.authoring.BasicPageForm;
import com.arsdigita.cms.util.GlobalizationUtil;

/**
 * Form to edit the basic properties of an service. This form can be extended to
 * create forms for Service subclasses.
 **/
public class ServicePropertyForm extends BasicPageForm
    implements FormProcessListener, FormInitListener, FormSubmissionListener {

    private ServicePropertiesStep m_step;

    /**  summary parameter name */
    public static final String SUMMARY = "summary";
    /**  services provided parameter name */
    public static final String SERVICES_PROVIDED = "servicesProvided";
    /**  opening times parameter name */
    public static final String OPENING_TIMES = "openingTimes";
    /**  address parameter name */
    public static final String ADDRESS = "address";
    /**  contacts parameter name */
    public static final String CONTACTS = "contacts";

    /** Name of this form */
    public static final String ID = "service_edit";

    /**
     * Creates a new form to edit the Service object specified
     * by the item selection model passed in.
     * @param itemModel The ItemSelectionModel to use to obtain the
     *    Service to work on
     */
    public ServicePropertyForm( ItemSelectionModel itemModel ) {
        this( itemModel, null );
    }

    /**
     * Creates a new form to edit the Service object specified
     * by the item selection model passed in.
     * @param itemModel The ItemSelectionModel to use to obtain the
     *    Service to work on
     * @param step The ServicePropertiesStep which controls this form.
     */
    public ServicePropertyForm( ItemSelectionModel itemModel, ServicePropertiesStep step ) {
        super( ID, itemModel );
        m_step = step;
        addSubmissionListener(this);
    }

    /**
     * Adds widgets to the form.
     */
    protected void addWidgets() {
        super.addWidgets();

        add(new Label(GlobalizationUtil.globalize("cms.contenttypes.ui.summary")));
        ParameterModel summaryParam = new StringParameter(SUMMARY);
        TextArea summary = new TextArea(summaryParam);
        summary.setCols(40);
        summary.setRows(5);
        add(summary);

        add(new Label(GlobalizationUtil.globalize("cms.contenttypes.ui.services_provided")));
        ParameterModel servicesProvidedParam =
            new StringParameter(SERVICES_PROVIDED);
        TextArea servicesProvided = new TextArea(servicesProvidedParam);
        servicesProvided.setCols(40);
        servicesProvided.setRows(5);
        add(servicesProvided);

        add(new Label(GlobalizationUtil.globalize("cms.contenttypes.ui.opening_times")));
        ParameterModel openingTimesParam = new StringParameter(OPENING_TIMES);
        TextArea openingTimes = new TextArea(openingTimesParam);
        openingTimes.setCols(40);
        openingTimes.setRows(5);
        add(openingTimes);

        add(new Label(GlobalizationUtil.globalize("cms.contenttypes.ui.address")));
        ParameterModel addressParam = new StringParameter(ADDRESS);
        TextArea address = new TextArea(addressParam);
        address.setCols(40);
        address.setRows(5);
        add(address);


        add(new Label(GlobalizationUtil.globalize("cms.contenttypes.ui.contacts")));
        ParameterModel contactsParam = new StringParameter(CONTACTS);
        TextArea contacts = new TextArea(contactsParam);
        contacts.setCols(40);
        contacts.setRows(5);
        add(contacts);

    }

    /** Form initialisation hook. Fills widgets with data. */
    public void init(FormSectionEvent fse) {
        FormData data = fse.getFormData();
        Service service = (Service) super.initBasicWidgets(fse);

        data.put(SUMMARY,           service.getSummary());
        data.put(SERVICES_PROVIDED, service.getServicesProvided());
        data.put(OPENING_TIMES,     service.getOpeningTimes());
        data.put(ADDRESS,           service.getAddress());
        data.put(CONTACTS,          service.getContacts());
    }

    /** Cancels streamlined editing. */
    public void submitted( FormSectionEvent fse ) {
        if (m_step != null &&
            getSaveCancelSection().getCancelButton()
            .isSelected( fse.getPageState())) {
            m_step.cancelStreamlinedCreation(fse.getPageState());
        }
    }

    /** Form processing hook. Saves Service object. */
    public void process(FormSectionEvent fse) {
        FormData data = fse.getFormData();

        Service service = (Service) super.processBasicWidgets(fse);

        // save only if save button was pressed
        if (service != null
            && getSaveCancelSection().getSaveButton()
            .isSelected(fse.getPageState())) {

            service.setSummary((String) data.get(SUMMARY));
            service.setServicesProvided((String) data.get(SERVICES_PROVIDED));
            service.setOpeningTimes((String) data.get(OPENING_TIMES));
            service.setAddress((String) data.get(ADDRESS));
            service.setContacts((String) data.get(CONTACTS));
            service.save();
        }
        if (m_step != null) {
            m_step.maybeForwardToNextStep(fse.getPageState());
        }
    }
}
