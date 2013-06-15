/*
 * GenericContactPersonPropertiesStep.java
 *
 * Created on 4. Juli 2009, 15:12
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.BasicPageForm;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;
import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.Label;
import com.arsdigita.cms.contenttypes.util.ContenttypesGlobalizationUtil;

/**
 *
 * @author quasi
 */
public class GenericContactPersonPropertiesStep extends SimpleEditStep {

    public static final String ADD_PERSON_SHEET_NAME = "addPerson";
    public static final String EDIT_PERSON_SHEET_NAME = "editPerson";
    public static final String CHANGE_PERSON_SHEET_NAME = "changePerson";
    public static final String DELETE_PERSON_SHEET_NAME = "deletePerson";
    private WorkflowLockedComponentAccess addPerson;
    // private WorkflowLockedComponentAccess editPerson;
    // private WorkflowLockedComponentAccess delPerson;

    /**
     * Creates a new instance of GenericContactPersonPropertiesStep
     */
    public GenericContactPersonPropertiesStep(ItemSelectionModel itemModel, 
                                              AuthoringKitWizard parent) {
        this(itemModel, parent, "");
    }

    public GenericContactPersonPropertiesStep(ItemSelectionModel itemModel, 
                                              AuthoringKitWizard parent, 
                                              String prefix) {
        super(itemModel, parent, prefix);

        BasicPageForm addPersonSheet = new 
                      GenericContactAttachPersonPropertyForm(itemModel, this);
        addPerson = new WorkflowLockedComponentAccess(addPersonSheet, itemModel);
        add(ADD_PERSON_SHEET_NAME, 
            ContenttypesGlobalizationUtil.globalize(
                        "cms.contenttypes.ui.contact.attach_person"), 
            addPerson, 
            addPersonSheet.getSaveCancelSection().getCancelButton());

        /* Set the displayComponent for this step */
        setDisplayComponent(getPersonPropertySheet(itemModel));

    }

    public static Component getPersonPropertySheet(ItemSelectionModel itemModel) {
        GenericContactPersonSheet sheet = new GenericContactPersonSheet(itemModel);
        return sheet;
    }

    public static Component getEmptyPersonPropertySheet(ItemSelectionModel itemModel) {
        return new Label((ContenttypesGlobalizationUtil.globalize(
                          "cms.contenttypes.ui.contact.emptyPerson") ));
    }
}
