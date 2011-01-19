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
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.event.ParameterEvent;
import com.arsdigita.bebop.event.ParameterListener;
// import com.arsdigita.bebop.event.ParameterListener;
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
import com.arsdigita.cms.contenttypes.SimpleAddress;
import com.arsdigita.cms.contenttypes.util.SimpleAddressGlobalizationUtil;
import com.arsdigita.cms.contenttypes.IsoCountry;
import com.arsdigita.cms.ui.authoring.BasicPageForm;
import com.arsdigita.domain.DomainCollection;
import org.apache.log4j.Logger;

/**
 * Form to edit the basic properties of an address. These are address body, 
 * country, postal code, phone, mobile phone, fax, email address, and notes.
 * Used by <code>SimpleAddressPropertiesStep</code> authoring kit step.
 * <br />
 * This form can be extended to create forms for SimpleAddress subclasses.
 *
 * @author <a href="mailto:dominik@redhat.com">Dominik Kacprzak</a>
 * @version $Revision: $ $Date: $
 **/
public class SimpleAddressPropertyForm extends BasicPageForm
    implements FormProcessListener, FormInitListener, FormSubmissionListener {
    
    private static final Logger s_log = Logger.getLogger(SimpleAddressPropertyForm.class);

    private SimpleAddressPropertiesStep m_step;

    /** SimpleAddress body parameter name */
    public static final String ADDRESS = SimpleAddress.ADDRESS;
    /** Country iso code parameter name */
    public static final String ISO_COUNTRY_CODE = SimpleAddress.ISO_COUNTRY_CODE;
    /** Postal code parameter name */
    public static final String POSTAL_CODE = SimpleAddress.POSTAL_CODE;
    /** Phone number parameter name */
    public static final String PHONE = SimpleAddress.PHONE;
    /** Mobile phone number parameter name */
    public static final String MOBILE = SimpleAddress.MOBILE;
    /** Fax number parameter name */
    public static final String FAX = SimpleAddress.FAX;
    /** Email address parameter name */
    public static final String EMAIL = SimpleAddress.EMAIL;
    /** SimpleAddress notes parameter name */
    public static final String NOTES = SimpleAddress.NOTES;
    /** SimpleAddress URI parameter name*/
    public static final String URI = SimpleAddress.URI;
    
    /** Name of this form */
    public static final String ID = "Address_edit";

    /**
     * Creates a new form to edit the SimpleAddress object specified by the item
     * selection model passed in.
     *
     * @param itemModel The ItemSelectionModel to use to obtain the SimpleAddress to
     * work on
     **/
    public SimpleAddressPropertyForm( ItemSelectionModel itemModel ) {
        this(itemModel,null);
    }
    /**
     * Creates a new form to edit the SimpleAddress object specified by the item
     * selection model passed in.
     *
     * @param itemModel The ItemSelectionModel to use to obtain the SimpleAddress to
     * work on
     * @param step The SimpleAddressPropertiesStep which controls this form.
     **/
    public SimpleAddressPropertyForm( ItemSelectionModel itemModel, SimpleAddressPropertiesStep step ) {
        super( ID, itemModel );
        m_step = step;
        addSubmissionListener(this);
    }

    /**
     * Adds widgets to the form.
     **/
    protected void addWidgets() {
        super.addWidgets();
        
        add( new Label( (String)SimpleAddressGlobalizationUtil.globalize
                 ( "cms.contenttypes.ui.address.address" ).localize() ) );
        ParameterModel addressBodyParam = new StringParameter( ADDRESS );
        addressBodyParam.addParameterListener( new NotNullValidationListener( ) );
	addressBodyParam.addParameterListener( new StringInRangeValidationListener(0, 1000) );
        TextArea addressBody = new TextArea( addressBodyParam );
        addressBody.setRows(5);
        addressBody.setCols(30);
        add( addressBody );
        
        if (!SimpleAddress.getConfig().getHideCountryCodeSelection()) {
            add( new Label( (String)SimpleAddressGlobalizationUtil.globalize
                     ( "cms.contenttypes.ui.address.iso_country_code" ).localize() ) );
            ParameterModel isoCountryCodeParam = new StringParameter( ISO_COUNTRY_CODE );
            //isoCountryCodeParam.addParameterListener( new NotNullValidationListener( ) );
	        // Don't assume submission via drop-down menu isoCountryCode
	        isoCountryCodeParam.addParameterListener( new StringInRangeValidationListener(0, 2) );
            SingleSelect isoCountryCode = new SingleSelect( isoCountryCodeParam );

            isoCountryCode.addOption( new Option( "", 
                                      new Label( (String)SimpleAddressGlobalizationUtil.globalize
                                          ("cms.ui.select_one" ).localize() ) ) );


            // retrieve country iso codes
            DomainCollection countries = IsoCountry.retrieveAll();
            while (countries.next()) {
                IsoCountry country = (IsoCountry)countries.getDomainObject();
                isoCountryCode.addOption( new Option( country.getIsoCode(),
                                                  country.getCountryName()));
            }

            isoCountryCode.addValidationListener(
                new ParameterListener() {
                    public void validate(ParameterEvent e) throws FormProcessException {
                        // the --select one-- option is not allowed
                        ParameterData data = e.getParameterData();
                        String isoCode = (String) data.getValue() ;
                        s_log.debug("ISO code is : " + isoCode);
                        if (isoCode == null || isoCode.length() == 0) {
                            data.addError(
                                (String)SimpleAddressGlobalizationUtil.globalize(
                                    "cms.contenttypes.ui.address.error_iso_country").localize());
                        }
                    }});

            add( isoCountryCode );
        }
                
        if (!SimpleAddress.getConfig().getHidePostalCode()) {
            add( new Label( (String)SimpleAddressGlobalizationUtil.globalize
                     ( "cms.contenttypes.ui.address.postal_code" ).localize() ) );
            ParameterModel postalCodeParam = new StringParameter( POSTAL_CODE );
            //  postalCodeParam.addParameterListener( new NotNullValidationListener( ) );
     	        postalCodeParam.addParameterListener( new StringInRangeValidationListener(0, 20) );
            TextField postalCode = new TextField( postalCodeParam );
            postalCode.setMaxLength( 20 );
            add( postalCode );        
        }
        
        add( new Label( SimpleAddressGlobalizationUtil.globalize( "cms.contenttypes.ui.address.phone" ) ) );
        ParameterModel phoneParam = new StringParameter( PHONE );
	    phoneParam.addParameterListener( new StringInRangeValidationListener(0, 20) );
        TextField phone = new TextField( phoneParam );
        phone.setSize(20);
        phone.setMaxLength( 20 );
        add( phone );        

        add( new Label( (String)SimpleAddressGlobalizationUtil.globalize
                 ( "cms.contenttypes.ui.address.mobile" ).localize() ) );
        ParameterModel mobileParam = new StringParameter( MOBILE );
	    mobileParam.addParameterListener( new StringInRangeValidationListener(0, 20) );
        TextField mobile = new TextField( mobileParam );
        mobile.setSize(20);
        mobile.setMaxLength( 20 );
        add( mobile );
        
        add( new Label( (String)SimpleAddressGlobalizationUtil.globalize
                 ( "cms.contenttypes.ui.address.fax" ).localize() ) );
        ParameterModel faxParam = new StringParameter( FAX );
	    faxParam.addParameterListener( new StringInRangeValidationListener(0, 20) );
        TextField fax = new TextField( faxParam );
        fax.setSize(20);
        fax.setMaxLength( 20 );
        add( fax );
        
        add( new Label( (String)SimpleAddressGlobalizationUtil.globalize
                 ( "cms.contenttypes.ui.address.email" ).localize() ) );
        ParameterModel emailParam = new StringParameter( EMAIL );
	    emailParam.addParameterListener( new StringInRangeValidationListener(0, 75) );
        TextField email = new TextField( emailParam );
        email.setSize(25);
        email.setMaxLength( 75 );
        email.setHint(SimpleAddressGlobalizationUtil.globalize
                     ( "cms.contenttypes.ui.address.email_hint").localize().toString());
        add( email );
        add(new Label(""));
        add(new Label( (String)SimpleAddressGlobalizationUtil.globalize
                     ( "cms.contenttypes.ui.address.email_desc" ).localize() ) );
        
        add( new Label( (String)SimpleAddressGlobalizationUtil.globalize
                 ( "cms.contenttypes.ui.address.uri" ).localize() ) );
        ParameterModel uriParam = new StringParameter( URI );
	    uriParam.addParameterListener( new StringInRangeValidationListener(0, 250) );
        TextField uri = new TextField( uriParam );
        uri.addValidationListener(new URLValidationListener(){
			public void validate(ParameterEvent e)throws FormProcessException{
				ParameterData d = e.getParameterData();
				String value = (String)d.getValue();
				if(value.indexOf("http")<0)
					value = "http://" + value;
					d.setValue(value);
					super.validate(e);
				}
			});
        uri.setSize(30);
        uri.setMaxLength( 250 );
        uri.setHint(SimpleAddressGlobalizationUtil.globalize
                   ( "cms.contenttypes.ui.address.uri_hint").localize().toString());
        add( uri );
        add(new Label(""));
        add(new Label((String)SimpleAddressGlobalizationUtil.globalize
                ( "cms.contenttypes.ui.address.uri_desc" ).localize() ) );

        add( new Label( (String)SimpleAddressGlobalizationUtil.globalize
                 ( "cms.contenttypes.ui.address.notes" ).localize() ) );
        ParameterModel notesParam = new StringParameter( NOTES );
        TextArea notes = new TextArea( notesParam );
        notes.setRows(8);
        notes.setCols(30);
        add( notes );
    }

    /**
     * Form initialization hook. Fills widgets with data.
     **/
    public void init(FormSectionEvent fse) {
        FormData data = fse.getFormData();
        SimpleAddress address = ( SimpleAddress ) super.initBasicWidgets(fse);
	
        //remind the user to prefix the URI with "http://"
        String uri = address.getURI();
        if(uri != null){
            // this will accept https
            if(uri.toLowerCase().indexOf("http") < 0)
            address.setURI("http://" + uri);
        } else {
            address.setURI("http://");
        }

        data.put( ADDRESS, address.getAddress() );
        if (!SimpleAddress.getConfig().getHideCountryCodeSelection()) {
            data.put( ISO_COUNTRY_CODE, address.getCountryIsoCode() );
        }
        if (!SimpleAddress.getConfig().getHidePostalCode() ) {
            data.put( POSTAL_CODE, address.getPostalCode() );
        }
        data.put( PHONE, address.getPhone() );
        data.put( MOBILE, address.getMobile() );
        data.put( FAX, address.getFax() );
        data.put( EMAIL, address.getEmail() );
        data.put( URI, address.getURI() );
        data.put( NOTES, address.getNotes() );

    }

    /** Cancels streamlined editing. */
    public void submitted( FormSectionEvent fse ) {
        if (m_step != null &&
            getSaveCancelSection().getCancelButton()
            .isSelected( fse.getPageState())) {
            m_step.cancelStreamlinedCreation(fse.getPageState());
        }
    }
 
    /**
     * Form processing hook. Saves SimpleAddress object.
     **/
    public void process(FormSectionEvent fse) {
        FormData data = fse.getFormData();

        SimpleAddress address = ( SimpleAddress ) super.processBasicWidgets( fse );

        // save only if save button was pressed
        if ( address != null && 
             getSaveCancelSection().getSaveButton().isSelected(fse.getPageState())) {

            address.setAddress( ( String ) data.get( ADDRESS ) );
            if (!SimpleAddress.getConfig().getHideCountryCodeSelection()) {
                address.setCountryIsoCode( ( String ) data.get( ISO_COUNTRY_CODE ) );
            }
            // if (!SimpleAddress.getConfig().getHidePostalCode()) {
            //     address.setPostalCode( ( String ) data.get( POSTAL_CODE ) );
            // }
            address.setPostalCode( !SimpleAddress.getConfig().getHidePostalCode() ?
                                   ( String ) data.get( POSTAL_CODE )       :
                                   ( String ) ""                             );
            address.setPhone( ( String ) data.get( PHONE ) );
            address.setMobile( ( String ) data.get( MOBILE ) );
            address.setFax( ( String ) data.get( FAX ) );
            address.setEmail( ( String ) data.get( EMAIL ) );
            address.setURI( ( String ) data.get( URI ) );
            address.setNotes( ( String ) data.get( NOTES ) );
            // address.setNotes( data.get( NOTES )!= "" ? ( String )data.get( NOTES ) : ( String )"dummy" );
            address.save();
        }
        if (m_step != null) {
            m_step.maybeForwardToNextStep(fse.getPageState());
        }
    }
}
