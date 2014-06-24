/*
 * Copyright (C) 2009 Sören Bernstein, Universität Bremen
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
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.event.ParameterEvent;
import com.arsdigita.bebop.event.ParameterListener;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.SingleSelect;
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.bebop.parameters.StringInRangeValidationListener;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.bebop.parameters.ParameterData;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.ui.authoring.BasicPageForm;
import java.util.Iterator;
import java.util.Map;
import com.arsdigita.cms.contenttypes.GenericAddress;
import com.arsdigita.cms.contenttypes.GenericContact;
import com.arsdigita.cms.contenttypes.util.ContenttypesGlobalizationUtil;
import com.arsdigita.util.UncheckedWrapperException;
import java.util.TooManyListenersException;
import java.util.logging.Level;

import org.apache.log4j.Logger;

/**
 *
 * @author quasi, Created on 8. Juli 2009, 10:27
 */
public class GenericContactEditAddressPropertyForm extends BasicPageForm
    implements FormProcessListener,
               FormInitListener,
               FormSubmissionListener {

    private static final Logger logger = Logger.getLogger(
        GenericContactPropertyForm.class);

    public static final String ADDRESS = GenericAddress.ADDRESS;
    public static final String POSTAL_CODE = GenericAddress.POSTAL_CODE;
    public static final String CITY = GenericAddress.CITY;
    public static final String STATE = GenericAddress.STATE;
    public static final String ISO_COUNTRY_CODE = GenericAddress.ISO_COUNTRY_CODE;

    private GenericContactAddressPropertiesStep m_step;

    /**
     * ID of the form
     */
    public static final String ID = "ContactEditAddress";

    /**
     * Constrctor taking an ItemSelectionModel
     *
     * @param itemModel
     */
    public GenericContactEditAddressPropertyForm(ItemSelectionModel itemModel) {
        this(itemModel, null);
    }

    /**
     * Constrctor taking an ItemSelectionModel and an instance of ContactPropertiesStep.
     *
     * @param itemModel
     * @param step
     */
    public GenericContactEditAddressPropertyForm(ItemSelectionModel itemModel,
                                                 GenericContactAddressPropertiesStep step) {
        super(ID, itemModel);
        m_step = step;
        addSubmissionListener(this);
    }

    @Override
    public void addWidgets() {
        ParameterModel addressParam = new StringParameter(ADDRESS);
        addressParam.addParameterListener(new NotNullValidationListener());
        addressParam.addParameterListener(new StringInRangeValidationListener(0, 1000));
        TextArea address = new TextArea(addressParam);
        address.setLabel(ContenttypesGlobalizationUtil
            .globalize("cms.contenttypes.ui.address.address"));
        address.setRows(5);
        address.setCols(30);
        add(address);

        if (!GenericContact.getConfig().getHideAddressPostalCode()) {
            ParameterModel postalCodeParam = new StringParameter(POSTAL_CODE);
            TextField postalCode = new TextField(postalCodeParam);
            postalCode.setLabel(ContenttypesGlobalizationUtil
                .globalize("cms.contenttypes.ui.address.postal_code"));
            /* XXX NumberListener ?*/
            add(postalCode);
        }

        ParameterModel cityParam = new StringParameter(CITY);
        TextField city = new TextField(cityParam);
        city.setLabel(ContenttypesGlobalizationUtil
            .globalize("cms.contenttypes.ui.address.city"));
        add(city);

        if (!GenericContact.getConfig().getHideAddressState()) {
            ParameterModel stateParam = new StringParameter(STATE);
            TextField state = new TextField(stateParam);
            state.setLabel(ContenttypesGlobalizationUtil
                .globalize("cms.contenttypes.ui.address.state"));
            add(state);
        }

        if (!GenericContact.getConfig().getHideAddressCountry()) {
            ParameterModel countryParam = new StringParameter(ISO_COUNTRY_CODE);
            countryParam.addParameterListener(new StringInRangeValidationListener(0, 2));

            SingleSelect country = new SingleSelect(countryParam);
            country.setLabel(ContenttypesGlobalizationUtil.globalize(
                "cms.contenttypes.ui.address.iso_country_code"));

            country.addOption(new Option("",
                                         new Label(ContenttypesGlobalizationUtil
                                             .globalize("cms.ui.select_one"))));

            try {
                country.addPrintListener(new PrintListener() {

                    @Override
                    public void prepare(final PrintEvent event) {
                        final SingleSelect target = (SingleSelect) event.getTarget();

                        final Iterator countries = GenericAddress.getSortedListOfCountries(null)
                            .entrySet().iterator();
                        while (countries.hasNext()) {
                            Map.Entry<String, String> elem = (Map.Entry<String, String>) countries
                                .next();
                            target.addOption(new Option(elem.getValue().toString(),
                                                         elem.getKey().toString()));
                        }
                    }

                });
            } catch (TooManyListenersException ex) {
                throw new UncheckedWrapperException("Something has gone terribly wrong", ex);
            }

            country.addValidationListener(
                new ParameterListener() {

                    @Override
                    public void validate(ParameterEvent e) throws FormProcessException {
                        ParameterData data = e.getParameterData();
                        String isoCode = (String) data.getValue();
                        if (isoCode == null || isoCode.length() == 0) {
                            data.addError((String) ContenttypesGlobalizationUtil
                                .globalize(
                                    "cms.contenttypes.ui.address.error_iso_country")
                                .localize());
                        }
                    }

                });

            add(country);
        }

    }

    @Override
    public void init(FormSectionEvent fse) {
        FormData data = fse.getFormData();
        PageState state = fse.getPageState();
        GenericContact contact = (GenericContact) getItemSelectionModel().getSelectedObject(state);

        if (contact.getAddress() != null) {
            data.put(ADDRESS, contact.getAddress().getAddress());
            data.put(POSTAL_CODE, contact.getAddress().getPostalCode());
            data.put(CITY, contact.getAddress().getCity());
            data.put(STATE, contact.getAddress().getState());
            if (!GenericAddress.getConfig().getHideCountryCodeSelection()) {
                data.put(ISO_COUNTRY_CODE, contact.getAddress().getIsoCountryCode());
            }
        }
    }

    @Override
    public void submitted(FormSectionEvent fse) {
        if (m_step != null
                && getSaveCancelSection().getCancelButton().isSelected(fse.getPageState())) {
            m_step.cancelStreamlinedCreation(fse.getPageState());
        }
    }

    @Override
    public void process(FormSectionEvent fse) {
        FormData data = fse.getFormData();
        PageState state = fse.getPageState();
        GenericContact contact = (GenericContact) getItemSelectionModel().getSelectedObject(state);

        if (getSaveCancelSection().getSaveButton().isSelected(fse.getPageState())) {
            contact.getAddress().setAddress((String) data.get(ADDRESS));
            contact.getAddress().setPostalCode((String) data.get(POSTAL_CODE));
            contact.getAddress().setCity((String) data.get(CITY));
            contact.getAddress().setState((String) data.get(STATE));
            contact.getAddress().setIsoCountryCode((String) data.get(ISO_COUNTRY_CODE));

            contact.getAddress().save();
        }

        if (m_step != null) {
            m_step.maybeForwardToNextStep(fse.getPageState());
        }
    }

}
