/*
 * ContactEditAddressPropertyForm.java
 *
 * Created on 8. Juli 2009, 10:27
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package com.arsdigita.cms.basetypes.ui;

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
import com.arsdigita.cms.basetypes.Address;
import com.arsdigita.cms.basetypes.Contact;
import com.arsdigita.cms.basetypes.util.BasetypesGlobalizationUtil;

import org.apache.log4j.Logger;

/**
 *
 * @author quasi
 */
public class ContactEditAddressPropertyForm extends BasicPageForm implements FormProcessListener, FormInitListener, FormSubmissionListener {

    private static final Logger logger = Logger.getLogger(ContactPropertyForm.class);
    private ContactAddressPropertiesStep m_step;
    public static final String ADDRESS = Address.ADDRESS;
    public static final String POSTAL_CODE = Address.POSTAL_CODE;
    public static final String CITY = Address.CITY;
    public static final String STATE = Address.STATE;
    public static final String ISO_COUNTRY_CODE = Address.ISO_COUNTRY_CODE;
    /**
     * ID of the form
     */
    public static final String ID = "ContactEditAddress";

    /**
     * Constrctor taking an ItemSelectionModel
     *
     * @param itemModel
     */
    public ContactEditAddressPropertyForm(ItemSelectionModel itemModel) {
        this(itemModel, null);
    }

    /**
     * Constrctor taking an ItemSelectionModel and an instance of ContactPropertiesStep.
     * 
     * @param itemModel
     * @param step
     */
    public ContactEditAddressPropertyForm(ItemSelectionModel itemModel, ContactAddressPropertiesStep step) {
        super(ID, itemModel);
        m_step = step;
        addSubmissionListener(this);
    }

    @Override
    public void addWidgets() {
        add(new Label((String) BasetypesGlobalizationUtil.globalize("cms.basetypes.ui.baseAddress.address").localize()));
        ParameterModel addressParam = new StringParameter(ADDRESS);
        addressParam.addParameterListener(new NotNullValidationListener());
        addressParam.addParameterListener(new StringInRangeValidationListener(0, 1000));
        TextArea address = new TextArea(addressParam);
        address.setRows(5);
        address.setCols(30);
        add(address);

        if (!Contact.getConfig().getHideAddressPostalCode()) {
            add(new Label((String) BasetypesGlobalizationUtil.globalize("cms.basetypes.ui.baseAddress.postal_code").localize()));
            ParameterModel postalCodeParam = new StringParameter(POSTAL_CODE);
            TextField postalCode = new TextField(postalCodeParam);
            /* XXX NumberListener ?*/
            add(postalCode);
        }

        add(new Label((String) BasetypesGlobalizationUtil.globalize("cms.basetypes.ui.baseAddress.city").localize()));
        ParameterModel cityParam = new StringParameter(CITY);
        TextField city = new TextField(cityParam);
        add(city);

        if (!Contact.getConfig().getHideAddressState()) {
            add(new Label((String) BasetypesGlobalizationUtil.globalize("cms.basetypes.ui.baseAddress.state").localize()));
            ParameterModel stateParam = new StringParameter(STATE);
            TextField state = new TextField(stateParam);
            add(state);
        }

        if (!Contact.getConfig().getHideAddressCountry()) {
            add(new Label((String) BasetypesGlobalizationUtil.globalize("cms.basetypes.ui.baseAddress.iso_country_code").localize()));
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
        PageState state = fse.getPageState();
        Contact contact = (Contact) getItemSelectionModel().getSelectedObject(state);

        if (contact.getAddress() != null) {
            data.put(ADDRESS, contact.getAddress().getAddress());
            data.put(POSTAL_CODE, contact.getAddress().getPostalCode());
            data.put(CITY, contact.getAddress().getCity());
            data.put(STATE, contact.getAddress().getState());
            if (!Address.getConfig().getHideCountryCodeSelection()) {
                data.put(ISO_COUNTRY_CODE, contact.getAddress().getIsoCountryCode());
            }
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
        PageState state = fse.getPageState();
        Contact contact = (Contact) getItemSelectionModel().getSelectedObject(state);

        if (getSaveCancelSection().getSaveButton().isSelected(fse.getPageState())) {
            if (contact.getAddress() == null) {
                contact.setAddress(new Address());
                contact.getAddress().setName("Address for " + contact.getName() + "(" + contact.getID() + ")");
                contact.getAddress().setTitle("Address for " + contact.getName() + "(" + contact.getID() + ")");
            }

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
