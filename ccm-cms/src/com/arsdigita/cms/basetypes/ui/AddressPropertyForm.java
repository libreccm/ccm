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
package com.arsdigita.cms.basetypes.ui;

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
import com.arsdigita.bebop.parameters.ParameterData;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.basetypes.Address;
import com.arsdigita.cms.basetypes.util.BasetypesGlobalizationUtil;
import com.arsdigita.cms.ui.authoring.BasicPageForm;
import java.util.Iterator;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 * Form to edit the properties of a address.
 *
 * @author: Jens Pelzetter
 * @author: Sören Bernstein
 */
public class AddressPropertyForm extends BasicPageForm implements FormProcessListener, FormInitListener, FormSubmissionListener {

    private static final Logger s_log = Logger.getLogger(AddressPropertyForm.class);
    private AddressPropertiesStep m_step;
    public static final String ADDRESS = Address.ADDRESS;
    public static final String POSTAL_CODE = Address.POSTAL_CODE;
    public static final String CITY = Address.CITY;
    public static final String STATE = Address.STATE;
    public static final String ISO_COUNTRY_CODE = Address.ISO_COUNTRY_CODE;
    public static final String ID = "Address_edit";

    public AddressPropertyForm(ItemSelectionModel itemModel) {
        this(itemModel, null);
    }

    public AddressPropertyForm(ItemSelectionModel itemModel, AddressPropertiesStep step) {
        super(ID, itemModel);
        m_step = step;
        addSubmissionListener(this);
    }

    @Override
    protected void addWidgets() {
        super.addWidgets();

        add(new Label((String) BasetypesGlobalizationUtil.globalize("cms.basetypes.ui.address.address").localize()));
        ParameterModel addressParam = new StringParameter(ADDRESS);
        addressParam.addParameterListener(new NotNullValidationListener());
        addressParam.addParameterListener(new StringInRangeValidationListener(0, 1000));
        TextArea address = new TextArea(addressParam);
        address.setRows(5);
        address.setCols(30);
        add(address);

        add(new Label((String) BasetypesGlobalizationUtil.globalize("cms.basetypes.ui.address.postal_code").localize()));
        ParameterModel postalCodeParam = new StringParameter(POSTAL_CODE);
        TextField postalCode = new TextField(postalCodeParam);
        /* XXX NumberListener ?*/
        add(postalCode);

        add(new Label((String) BasetypesGlobalizationUtil.globalize("cms.basetypes.ui.address.city").localize()));
        ParameterModel cityParam = new StringParameter(CITY);
        TextField city = new TextField(cityParam);
        add(city);

        add(new Label((String) BasetypesGlobalizationUtil.globalize("cms.basetypes.ui.address.state").localize()));
        ParameterModel stateParam = new StringParameter(STATE);
        TextField state = new TextField(stateParam);
        add(state);

        if (!Address.getConfig().getHideCountryCodeSelection()) {
            add(new Label((String) BasetypesGlobalizationUtil.globalize("cms.basetypes.ui.address.iso_country_code").localize()));
            ParameterModel countryParam = new StringParameter(ISO_COUNTRY_CODE);
            countryParam.addParameterListener(new StringInRangeValidationListener(0, 2));

            SingleSelect country = new SingleSelect(countryParam);

            country.addOption(new Option("", new Label((String) BasetypesGlobalizationUtil.globalize("cms.ui.select_one").localize())));

            Iterator countries = Address.getSortedListOfCountries(null).entrySet().iterator();
            while (countries.hasNext()) {
                Map.Entry<String, String> elem = (Map.Entry<String, String>) countries.next();
                country.addOption(new Option(elem.getValue().toString(), elem.getKey().toString()));
            }

            country.addValidationListener(
                    new ParameterListener() {

                        public void validate(ParameterEvent e) throws FormProcessException {
                            ParameterData data = e.getParameterData();
                            String isoCode = (String) data.getValue();
                            s_log.debug("ISO code is : " + isoCode);
                            if (isoCode == null || isoCode.length() == 0) {
                                data.addError((String) BasetypesGlobalizationUtil.globalize("cms.basetypes.ui.address.error_iso_country").localize());
                            }
                        }
                    });

            add(country);
        }

    }

    public void init(FormSectionEvent fse) {
        FormData data = fse.getFormData();
        Address address = (Address) super.initBasicWidgets(fse);

        data.put(ADDRESS, address.getAddress());
        data.put(POSTAL_CODE, address.getPostalCode());
        data.put(CITY, address.getCity());
        data.put(STATE, address.getState());
        if (!Address.getConfig().getHideCountryCodeSelection()) {
            data.put(ISO_COUNTRY_CODE, address.getIsoCountryCode());
        }
    }

    public void submitted(FormSectionEvent fse) {
        if (m_step != null
                && getSaveCancelSection().getCancelButton().isSelected(fse.getPageState())) {
            m_step.cancelStreamlinedCreation(fse.getPageState());
        }
    }

    public void process(FormSectionEvent fse) {
        FormData data = fse.getFormData();

        Address address = (Address) super.processBasicWidgets(fse);

        if (address != null
                && getSaveCancelSection().getSaveButton().isSelected(fse.getPageState())) {
            address.setAddress((String) data.get(ADDRESS));
            address.setPostalCode((String) data.get(POSTAL_CODE));
            address.setCity((String) data.get(CITY));
            address.setState((String) data.get(STATE));
            address.setIsoCountryCode((String) data.get(ISO_COUNTRY_CODE));

            address.save();
        }

        if (m_step != null) {
            m_step.maybeForwardToNextStep(fse.getPageState());
        }
    }
}
