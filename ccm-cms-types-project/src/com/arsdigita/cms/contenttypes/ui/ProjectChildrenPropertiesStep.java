/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.util.ContenttypesGlobalizationUtil;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.BasicItemForm;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;
import org.apache.log4j.Logger;

/**
 *
 * @author Jens Pelzetter
 */
public class ProjectChildrenPropertiesStep extends SimpleEditStep {

    private final static Logger s_log = Logger.getLogger(
            ProjectChildrenPropertiesStep.class);
    private String ADD_CHILD_SHEET_NAME = "addChild";

    public ProjectChildrenPropertiesStep(ItemSelectionModel itemModel,
                                         AuthoringKitWizard parent) {
        this(itemModel, parent, null);
    }

    public ProjectChildrenPropertiesStep(
            ItemSelectionModel itemModel,
            AuthoringKitWizard parent,
            String prefix) {
        super(itemModel, parent, prefix);

        BasicItemForm addChildSheet = new ProjectAddChildForm(itemModel);
        add(ADD_CHILD_SHEET_NAME,
            (String) ContenttypesGlobalizationUtil.globalize(
                "cms.contenttypes.ui.genericorgaunit.add_child").localize(),
            new WorkflowLockedComponentAccess(addChildSheet, itemModel),
            addChildSheet.getSaveCancelSection().getCancelButton());

        GenericOrganizationalUnitChildTable childrenTable = new GenericOrganizationalUnitChildTable(
                itemModel);
        setDisplayComponent(childrenTable);
    }
}
