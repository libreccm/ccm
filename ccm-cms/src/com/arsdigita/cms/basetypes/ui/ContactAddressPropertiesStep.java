/*
 * ContactAddressPropertiesStep.java
 *
 * Created on 4. Juli 2009, 15:15
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package com.arsdigita.cms.basetypes.ui;

import com.arsdigita.bebop.PageState;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.BasicPageForm;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.toolbox.ui.DomainObjectPropertySheet;
import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.Label;
import com.arsdigita.cms.basetypes.Address;
import com.arsdigita.cms.basetypes.Contact;
import com.arsdigita.cms.basetypes.util.BasetypesGlobalizationUtil;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;

/**
 *
 * @author quasi
 */
public class ContactAddressPropertiesStep extends SimpleEditStep {

    public static final String ADD_ADDRESS_SHEET_NAME = "addAddress";
    public static final String EDIT_ADDRESS_SHEET_NAME = "editAddress";
    public static final String CHANGE_ADDRESS_SHEET_NAME = "changeAddress";
    public static final String DELETE_ADDRESS_SHEET_NAME = "deleteAddress";

    /** Creates a new instance of ContactAddressPropertiesStep */
    public ContactAddressPropertiesStep(ItemSelectionModel itemModel, AuthoringKitWizard parent) {
        this(itemModel, parent, "");
    }

    public ContactAddressPropertiesStep(ItemSelectionModel itemModel, AuthoringKitWizard parent, String prefix) {
        super(itemModel, parent, prefix);

//        Contact contact = (Contact)itemModel.getSelectedObject(state);

//XXX
//        if(/*contact.getAddress() == null*/ true) {
        BasicPageForm attachAddressSheet = new ContactAttachAddressPropertyForm(itemModel, this);
        add(ADD_ADDRESS_SHEET_NAME, (String) BasetypesGlobalizationUtil.globalize("cms.basetypes.ui.contact.attach_address").localize(), new WorkflowLockedComponentAccess(attachAddressSheet, itemModel), attachAddressSheet.getSaveCancelSection().getCancelButton());

        /* Set the displayComponent for this step */
//            setDisplayComponent(getEmptyBaseAddressPropertySheet(itemModel));

//        } else {

        // editAddress
        BasicPageForm editAddressSheet = new ContactEditAddressPropertyForm(itemModel, this);
        add(EDIT_ADDRESS_SHEET_NAME, (String) BasetypesGlobalizationUtil.globalize("cms.basetypes.ui.contact.edit_address").localize(), new WorkflowLockedComponentAccess(editAddressSheet, itemModel), editAddressSheet.getSaveCancelSection().getCancelButton());

//            BasicPageForm attachAddressSheet = new ContactAttachAddressPropertyForm(itemModel, this);
//            add(CHANGE_ADDRESS_SHEET_NAME, (String)BasetypesGlobalizationUtil.globalize("cms.basetypes.ui.contact.reattach_address").localize(), new WorkflowLockedComponentAccess(attachAddressSheet, itemModel), attachAddressSheet.getSaveCancelSection().getCancelButton());

        BasicPageForm deleteAddressSheet = new ContactDeleteAddressForm(itemModel, this);
        add(DELETE_ADDRESS_SHEET_NAME, (String) BasetypesGlobalizationUtil.globalize("cms.basetypes.ui.contact.delete_address").localize(), new WorkflowLockedComponentAccess(deleteAddressSheet, itemModel), deleteAddressSheet.getSaveCancelSection().getCancelButton());

        /* Set the displayComponent for this step */
        setDisplayComponent(getAddressPropertySheet(itemModel));
//        }

    }

    public static Component getAddressPropertySheet(ItemSelectionModel itemModel) {

        DomainObjectPropertySheet sheet = new DomainObjectPropertySheet(itemModel);

        sheet.add((String) BasetypesGlobalizationUtil.globalize("cms.basetypes.ui.baseAddress.address").localize(), "address." + Address.ADDRESS);
        if (!Contact.getConfig().getHideAddressPostalCode()) {
            sheet.add((String) BasetypesGlobalizationUtil.globalize("cms.basetypes.ui.baseAddress.postal_code").localize(), "address." + Address.POSTAL_CODE);
        }
        sheet.add((String) BasetypesGlobalizationUtil.globalize("cms.basetypes.ui.baseAddress.city").localize(), "address." + Address.CITY);
        if (!Contact.getConfig().getHideAddressState()) {
            sheet.add((String) BasetypesGlobalizationUtil.globalize("cms.basetypes.ui.baseAddress.state").localize(), "address." + Address.STATE);
        }

        if (!Contact.getConfig().getHideAddressCountry()) {
            sheet.add((String) BasetypesGlobalizationUtil.globalize("cms.basetypes.ui.baseAddress.iso_country_code").localize(),
                    "address." + Address.ISO_COUNTRY_CODE,
                    new DomainObjectPropertySheet.AttributeFormatter() {

                        public String format(DomainObject item,
                                String attribute,
                                PageState state) {
                            Address Address = ((Contact) item).getAddress();
                            if (Address != null && Address.getIsoCountryCode() != null) {
                                return Address.getCountryNameFromIsoCode(Address.getIsoCountryCode());
                            } else {
                                return (String) BasetypesGlobalizationUtil.globalize("cms.ui.unknown").localize();
                            }
                        }
                    });
        }

        return sheet;

    }

    public static Component getEmptyBaseAddressPropertySheet(ItemSelectionModel itemModel) {
        return new Label(((String) BasetypesGlobalizationUtil.globalize("cms.basetypes.ui.contact.emptyAddress").localize()));
    }
}
