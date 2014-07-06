/*
 * GenericContactAddressPropertiesStep.java
 *
 * Created on 4. Juli 2009, 15:15
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.Component;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.util.ContenttypesGlobalizationUtil;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.BasicPageForm;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;

/**
 *
 * @author quasi
 */
public class GenericContactAddressPropertiesStep extends SimpleEditStep {

    public static final String ADD_ADDRESS_SHEET_NAME = "addAddress";
    public static final String EDIT_ADDRESS_SHEET_NAME = "editAddress";
    public static final String CHANGE_ADDRESS_SHEET_NAME = "changeAddress";
    public static final String DELETE_ADDRESS_SHEET_NAME = "deleteAddress";
    //private ItemSelectionModel itemModel;
    private final WorkflowLockedComponentAccess addAddress;

    /**
     * Creates a new instance of GenericContactAddressPropertiesStep.
     * 
     * @param itemModel
     * @param parent
     */
    public GenericContactAddressPropertiesStep(final ItemSelectionModel itemModel,
                                               final AuthoringKitWizard parent) {
        this(itemModel, parent, "");
    }

    public GenericContactAddressPropertiesStep(final ItemSelectionModel itemModel,
                                               final AuthoringKitWizard parent,
                                               final String prefix) {
        super(itemModel, parent, prefix);

        final BasicPageForm addAddressSheet = new GenericContactAttachAddressPropertyForm(itemModel,
                                                                                          this);
        addAddress = new WorkflowLockedComponentAccess(addAddressSheet, itemModel);
        add(ADD_ADDRESS_SHEET_NAME, 
            ContenttypesGlobalizationUtil.globalize("cms.contenttypes.ui.contact.attach_address"),
            addAddress,
            addAddressSheet.getSaveCancelSection().getCancelButton());
            
        
        setDisplayComponent(getAddressPropertySheet(itemModel));

    }

    public static Component getAddressPropertySheet(final ItemSelectionModel itemModel) {
        final GenericContactAddressSheet sheet = new GenericContactAddressSheet(itemModel);
        return sheet;
    }
    
}
