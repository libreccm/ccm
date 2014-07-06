/*
 * HealthCareFacilityEditAddressPropertyForm.java
 *
 * Created on 8. Juli 2009, 10:27
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
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
import com.arsdigita.cms.contenttypes.HealthCareFacility;
import com.arsdigita.cms.contenttypes.util.ContenttypesGlobalizationUtil;

import org.apache.log4j.Logger;

/**
 *
 * @author quasi
 */
public class HealthCareFacilityEditAddressPropertyForm extends BasicPageForm implements FormProcessListener, FormInitListener, FormSubmissionListener {

    private static final Logger logger = Logger.getLogger(HealthCareFacilityPropertyForm.class);
    private HealthCareFacilityAddressPropertiesStep m_step;
    public static final String ADDRESS = com.arsdigita.cms.contenttypes.GenericAddress.ADDRESS;
    public static final String POSTAL_CODE = com.arsdigita.cms.contenttypes.GenericAddress.POSTAL_CODE;
    public static final String CITY = com.arsdigita.cms.contenttypes.GenericAddress.CITY;
    public static final String STATE = com.arsdigita.cms.contenttypes.GenericAddress.STATE;
    public static final String ISO_COUNTRY_CODE = com.arsdigita.cms.contenttypes.GenericAddress.ISO_COUNTRY_CODE;
    /**
     * ID of the form
     */
    public static final String ID = "HealthCareFacilityEditAddress";

    /**
     * Constrctor taking an ItemSelectionModel
     *
     * @param itemModel
     */
    public HealthCareFacilityEditAddressPropertyForm(ItemSelectionModel itemModel) {
        this(itemModel, null);
    }

    /**
     * Constrctor taking an ItemSelectionModel and an instance of HealthCareFacilityPropertiesStep.
     * 
     * @param itemModel
     * @param step
     */
    public HealthCareFacilityEditAddressPropertyForm(ItemSelectionModel itemModel, HealthCareFacilityAddressPropertiesStep step) {
        super(ID, itemModel);
        m_step = step;
        addSubmissionListener(this);
    }

    @Override
    public void addWidgets() {

    //  add(new Label(ContenttypesGlobalizationUtil.globalize(
    //          "cms.contenttypes.ui.address.address")));
        ParameterModel addressParam = new StringParameter(ADDRESS);
        addressParam.addParameterListener(new NotNullValidationListener());
        addressParam.addParameterListener(new StringInRangeValidationListener(0, 1000));
        TextArea address = new TextArea(addressParam);
        address.setLabel(ContenttypesGlobalizationUtil.globalize(
                "cms.contenttypes.ui.address.address"));
        address.setRows(5);
        address.setCols(30);
        add(address);

        if (!HealthCareFacility.getConfig().getHideAddressPostalCode()) {
            ParameterModel postalCodeParam = new StringParameter(POSTAL_CODE);
            TextField postalCode = new TextField(postalCodeParam);
            /* XXX NumberListener ?*/
            postalCode.setLabel(ContenttypesGlobalizationUtil.globalize(
                    "cms.contenttypes.ui.address.postal_code"));
            add(postalCode);
        }

        ParameterModel cityParam = new StringParameter(CITY);
        TextField city = new TextField(cityParam);
        city.setLabel(ContenttypesGlobalizationUtil.globalize(
                "cms.contenttypes.ui.address.city"));
        add(city);

        if (!HealthCareFacility.getConfig().getHideAddressState()) {
            ParameterModel stateParam = new StringParameter(STATE);
            TextField state = new TextField(stateParam);
            state.setLabel(ContenttypesGlobalizationUtil.globalize(
                    "cms.contenttypes.ui.address.state"));
            add(state);
        }

        if (!HealthCareFacility.getConfig().getHideAddressCountry()) {
         // add(new Label(ContenttypesGlobalizationUtil.globalize(
         //         "cms.contenttypes.ui.address.iso_country_code") ));
            ParameterModel countryParam = new StringParameter(ISO_COUNTRY_CODE);
            countryParam.addParameterListener(new StringInRangeValidationListener(0, 2));

            SingleSelect country = new SingleSelect(countryParam);

            country.addOption(new Option(
                    "", new Label(ContenttypesGlobalizationUtil
                                  .globalize("cms.ui.select_one") )));

            Iterator countries = com.arsdigita.cms.contenttypes.GenericAddress.getSortedListOfCountries(null).entrySet().iterator();
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
                                data.addError((String) ContenttypesGlobalizationUtil.globalize("cms.contenttypes.ui.address.error_iso_country").localize());
                            }
                        }
                    });

            country.setLabel(ContenttypesGlobalizationUtil.globalize(
                    "cms.contenttypes.ui.address.iso_country_code"));
            add(country);
        }

    }

    public void init(FormSectionEvent fse) {
        FormData data = fse.getFormData();
        PageState state = fse.getPageState();
        HealthCareFacility healthCareFacility = (HealthCareFacility) getItemSelectionModel().getSelectedObject(state);

        if (healthCareFacility.getAddress() != null) {
            data.put(ADDRESS, healthCareFacility.getAddress().getAddress());
            data.put(POSTAL_CODE, healthCareFacility.getAddress().getPostalCode());
            data.put(CITY, healthCareFacility.getAddress().getCity());
            data.put(STATE, healthCareFacility.getAddress().getState());
            if (!com.arsdigita.cms.contenttypes.GenericAddress.getConfig().getHideCountryCodeSelection()) {
                data.put(ISO_COUNTRY_CODE, healthCareFacility.getAddress().getIsoCountryCode());
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
        HealthCareFacility healthCareFacility = (HealthCareFacility) getItemSelectionModel().getSelectedObject(state);

        if (healthCareFacility.getAddress() != null
                && getSaveCancelSection().getSaveButton().isSelected(fse.getPageState())) {
            healthCareFacility.getAddress().setAddress((String) data.get(ADDRESS));
            healthCareFacility.getAddress().setPostalCode((String) data.get(POSTAL_CODE));
            healthCareFacility.getAddress().setCity((String) data.get(CITY));
            healthCareFacility.getAddress().setState((String) data.get(STATE));
            healthCareFacility.getAddress().setIsoCountryCode((String) data.get(ISO_COUNTRY_CODE));

            healthCareFacility.getAddress().save();
        }

        if (m_step != null) {
            m_step.maybeForwardToNextStep(fse.getPageState());
        }
    }
}
