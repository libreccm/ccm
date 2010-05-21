package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.bebop.Component;

import org.apache.log4j.Logger;

/**
 * AuthoringStep for the basic properties of a basic contact
 */
public class BaseContactPropertiesStep extends com.arsdigita.cms.basetypes.ui.ContactPropertiesStep {

    private static final Logger logger = Logger.getLogger(BaseContactPropertiesStep.class);
    
    /**
     * Name of the this edit sheet (Don't know if this this really needed.
     * It has the same value in almost all PropertiesStep classes)
     */
    public static final String EDIT_BASIC_SHEET_NAME = "editBasic";

    /**
     * Constructor for the PropertiesStep.
     *
     * @param itemModel
     * @param parent
     */
    public BaseContactPropertiesStep(ItemSelectionModel itemModel, AuthoringKitWizard parent) {
        super(itemModel, parent);
    }

    /**
     * Creates and returns the sheet for editing the basic properties
     * of an organization. (@see BaseContactPropertyForm).
     * 
     * @param itemModel
     * @return The sheet for editing the properties of the organization.
     */
    public static Component getBaseContactPropertySheet(ItemSelectionModel itemModel) {
        return com.arsdigita.cms.basetypes.ui.ContactPropertiesStep.getContactPropertySheet(itemModel);
    }
    
}
