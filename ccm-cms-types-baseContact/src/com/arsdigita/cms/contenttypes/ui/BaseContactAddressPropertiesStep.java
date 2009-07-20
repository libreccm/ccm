/*
 * BaseContactAddressPropertiesStep.java
 *
 * Created on 4. Juli 2009, 15:15
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.PageState;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.BasicPageForm;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.toolbox.ui.DomainObjectPropertySheet;
import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.Label;
import com.arsdigita.cms.contenttypes.BaseAddress;
import com.arsdigita.cms.contenttypes.BaseContact;
import com.arsdigita.cms.contenttypes.util.BaseAddressGlobalizationUtil;
import com.arsdigita.cms.contenttypes.util.BaseContactGlobalizationUtil;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;

/**
 *
 * @author quasi
 */
public class BaseContactAddressPropertiesStep extends SimpleEditStep {
    
    public static final String ADD_ADDRESS_SHEET_NAME = "addAddress";
    public static final String EDIT_ADDRESS_SHEET_NAME = "editAddress";
    public static final String CHANGE_ADDRESS_SHEET_NAME = "changeAddress";
    public static final String DELETE_ADDRESS_SHEET_NAME = "deleteAddress";

    /** Creates a new instance of BaseContactAddressPropertiesStep */
    public BaseContactAddressPropertiesStep(ItemSelectionModel itemModel, AuthoringKitWizard parent) {
        this(itemModel, parent, "");
    }
    
    public BaseContactAddressPropertiesStep(ItemSelectionModel itemModel, AuthoringKitWizard parent, String prefix) {
        super(itemModel, parent, prefix);

//        BaseContact baseContact = (BaseContact)itemModel.getSelectedObject(state);
        
//XXX
//        if(/*baseContact.getAddress() == null*/ true) {
            BasicPageForm attachAddressSheet = new BaseContactAttachAddressPropertyForm(itemModel, this);
            add(ADD_ADDRESS_SHEET_NAME, "Attach Address", new WorkflowLockedComponentAccess(attachAddressSheet, itemModel), attachAddressSheet.getSaveCancelSection().getCancelButton());
        
            /* Set the displayComponent for this step */
//            setDisplayComponent(getEmptyBaseAddressPropertySheet(itemModel));

//        } else {
        
            // editAddress
            BasicPageForm editAddressSheet = new BaseContactEditAddressPropertyForm(itemModel, this);
            add(EDIT_ADDRESS_SHEET_NAME, "Edit Address", new WorkflowLockedComponentAccess(editAddressSheet, itemModel), editAddressSheet.getSaveCancelSection().getCancelButton());

//            BasicPageForm attachAddressSheet = new BaseContactAttachAddressPropertyForm(itemModel, this);
//            add(CHANGE_ADDRESS_SHEET_NAME, "Reattach Address", new WorkflowLockedComponentAccess(attachAddressSheet, itemModel), attachAddressSheet.getSaveCancelSection().getCancelButton());

            BasicPageForm deleteAddressSheet = new BaseContactDeleteAddressForm(itemModel, this);
            add(DELETE_ADDRESS_SHEET_NAME, "Delete Address", new WorkflowLockedComponentAccess(deleteAddressSheet, itemModel), deleteAddressSheet.getSaveCancelSection().getCancelButton());

            /* Set the displayComponent for this step */
            setDisplayComponent(getBaseAddressPropertySheet(itemModel));
//        }
        
    }
    
    public static Component getBaseAddressPropertySheet(ItemSelectionModel itemModel) {
        
        DomainObjectPropertySheet sheet = new DomainObjectPropertySheet(itemModel);
        
        sheet.add((String)BaseAddressGlobalizationUtil.globalize("cms.contenttypes.ui.baseAddress.address").localize(), "address." + BaseAddress.ADDRESS);
        if(!BaseContact.getConfig().getHideAddressPostalCode()) 
            sheet.add((String)BaseAddressGlobalizationUtil.globalize("cms.contenttypes.ui.baseAddress.postal_code").localize(), "address." + BaseAddress.POSTAL_CODE);
        sheet.add((String)BaseAddressGlobalizationUtil.globalize("cms.contenttypes.ui.baseAddress.city").localize(), "address." + BaseAddress.CITY);
        if(!BaseContact.getConfig().getHideAddressState()) 
            sheet.add((String)BaseAddressGlobalizationUtil.globalize("cms.contenttypes.ui.baseAddress.state").localize(), "address." + BaseAddress.STATE);
        
        if(!BaseContact.getConfig().getHideAddressCountry()) {
            sheet.add((String)BaseAddressGlobalizationUtil.globalize("cms.contenttypes.ui.baseAddress.iso_country_code").localize(),
                    "address." + BaseAddress.ISO_COUNTRY_CODE,
                    new DomainObjectPropertySheet.AttributeFormatter() {
                public String format(DomainObject item,
                        String attribute,
                        PageState state) {
                    BaseAddress baseAddress = ((BaseContact)item).getAddress();
                    if(baseAddress != null && baseAddress.getIsoCountryCode() != null) {
                        return BaseAddress.getCountryNameFromIsoCode(baseAddress.getIsoCountryCode());
                    } else {
                        return (String)BaseAddressGlobalizationUtil.globalize
                                ("cms.ui.unknown").localize();
                    }
                }
            });
        }
        
        return sheet;
        
    }

    public static Component getEmptyBaseAddressPropertySheet(ItemSelectionModel itemModel) {
        return new Label(((String)BaseContactGlobalizationUtil.globalize("cms.contenttypes.ui.baseContact.emptyAddress").localize()));
    }

}
