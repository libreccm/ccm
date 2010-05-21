/*
 * BaseContactEditAddressPropertyForm.java
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
import com.arsdigita.cms.contenttypes.BaseAddress;
import com.arsdigita.cms.contenttypes.BaseContact;
import com.arsdigita.cms.contenttypes.util.BaseAddressGlobalizationUtil;

import org.apache.log4j.Logger;

/**
 *
 * @author quasi
 */
public class BaseContactEditAddressPropertyForm extends BasicPageForm implements FormProcessListener, FormInitListener, FormSubmissionListener {
    
    private static final Logger logger = Logger.getLogger(BaseContactPropertyForm.class);

    private BaseContactAddressPropertiesStep m_step;

    public static final String ADDRESS = BaseAddress.ADDRESS;
    public static final String POSTAL_CODE = BaseAddress.POSTAL_CODE;
    public static final String CITY = BaseAddress.CITY;
    public static final String STATE = BaseAddress.STATE;
    public static final String ISO_COUNTRY_CODE = BaseAddress.ISO_COUNTRY_CODE;

    /**
     * ID of the form
     */
    public static final String ID = "BaseContactEditAddress";

    /**
     * Constrctor taking an ItemSelectionModel
     *
     * @param itemModel
     */
    public BaseContactEditAddressPropertyForm(ItemSelectionModel itemModel)    {
        this(itemModel, null);
    }

    /**
     * Constrctor taking an ItemSelectionModel and an instance of BaseContactPropertiesStep.
     * 
     * @param itemModel
     * @param step
     */
    public BaseContactEditAddressPropertyForm(ItemSelectionModel itemModel, BaseContactAddressPropertiesStep step) {
        super(ID, itemModel);
        m_step = step;
        addSubmissionListener(this);
    }

    @Override
    public void addWidgets() {
	add(new Label((String)BaseAddressGlobalizationUtil.globalize("cms.contenttypes.ui.baseAddress.address").localize()));
	ParameterModel addressParam = new StringParameter(ADDRESS);
        addressParam.addParameterListener( new NotNullValidationListener( ) );
	addressParam.addParameterListener( new StringInRangeValidationListener(0, 1000) );
	TextArea address = new TextArea(addressParam);
        address.setRows(5);
        address.setCols(30);
	add(address);

        if(!BaseContact.getConfig().getHideAddressPostalCode()) {
            add(new Label((String)BaseAddressGlobalizationUtil.globalize("cms.contenttypes.ui.baseAddress.postal_code").localize()));
            ParameterModel postalCodeParam = new StringParameter(POSTAL_CODE);
            TextField postalCode = new TextField(postalCodeParam);
            /* XXX NumberListener ?*/
            add(postalCode);
        }

	add(new Label((String)BaseAddressGlobalizationUtil.globalize("cms.contenttypes.ui.baseAddress.city").localize()));
	ParameterModel cityParam = new StringParameter(CITY);
	TextField city = new TextField(cityParam);
	add(city);

        if(!BaseContact.getConfig().getHideAddressState()) {
            add(new Label((String)BaseAddressGlobalizationUtil.globalize("cms.contenttypes.ui.baseAddress.state").localize()));
            ParameterModel stateParam = new StringParameter(STATE);
            TextField state = new TextField(stateParam);
            add(state);
        }
        
        if (!BaseContact.getConfig().getHideAddressCountry()) {
            add(new Label((String)BaseAddressGlobalizationUtil.globalize("cms.contenttypes.ui.baseAddress.iso_country_code").localize()));
            ParameterModel countryParam = new StringParameter(ISO_COUNTRY_CODE);
            countryParam.addParameterListener(new StringInRangeValidationListener(0, 2));
            
            SingleSelect country = new SingleSelect(countryParam);

            country.addOption(new Option("", new Label((String)BaseAddressGlobalizationUtil.globalize("cms.ui.select_one" ).localize())));

            Iterator countries = BaseAddress.getSortedListOfCountries(null).entrySet().iterator();
            while(countries.hasNext()) {
                Map.Entry<String,String> elem = (Map.Entry<String,String>)countries.next();
                country.addOption(new Option(elem.getValue().toString(), elem.getKey().toString()));
            }
            
            country.addValidationListener(
                new ParameterListener() {
                    public void validate(ParameterEvent e) throws FormProcessException {
                        ParameterData data = e.getParameterData();
                        String isoCode = (String) data.getValue() ;
                        if (isoCode == null || isoCode.length() == 0) {
                            data.addError((String)BaseAddressGlobalizationUtil.globalize("cms.contenttypes.ui.address.error_iso_country").localize());
                        }
                    }
                }
            );

            add(country);
        }

    }

    public void init(FormSectionEvent fse) {
	FormData data = fse.getFormData();
        PageState state = fse.getPageState();
        BaseContact baseContact = (BaseContact)getItemSelectionModel().getSelectedObject(state);
       
        if(baseContact.getAddress() != null) {
            data.put(ADDRESS, baseContact.getAddress().getAddress());
            data.put(POSTAL_CODE, baseContact.getAddress().getPostalCode());
            data.put(CITY, baseContact.getAddress().getCity());
            data.put(STATE, baseContact.getAddress().getState());
            if(!BaseAddress.getConfig().getHideCountryCodeSelection()) {
                data.put(ISO_COUNTRY_CODE, baseContact.getAddress().getIsoCountryCode());
            }
        }
    }

    public void submitted(FormSectionEvent fse) {
	if (m_step != null && 
	    getSaveCancelSection().getCancelButton().isSelected(fse.getPageState())) {
	    m_step.cancelStreamlinedCreation(fse.getPageState());
	}
    }

    public void process(FormSectionEvent fse) {
	FormData data = fse.getFormData();
        PageState state = fse.getPageState();
        BaseContact baseContact = (BaseContact)getItemSelectionModel().getSelectedObject(state);

	if (baseContact.getAddress() != null &&
	    getSaveCancelSection().getSaveButton().isSelected(fse.getPageState())) {
	    baseContact.getAddress().setAddress((String)data.get(ADDRESS));
	    baseContact.getAddress().setPostalCode((String)data.get(POSTAL_CODE));
	    baseContact.getAddress().setCity((String)data.get(CITY));
	    baseContact.getAddress().setState((String)data.get(STATE));
	    baseContact.getAddress().setIsoCountryCode((String)data.get(ISO_COUNTRY_CODE));
            
	    baseContact.getAddress().save();
	}
	
	if (m_step != null) {
	    m_step.maybeForwardToNextStep(fse.getPageState());
	}
    }
}