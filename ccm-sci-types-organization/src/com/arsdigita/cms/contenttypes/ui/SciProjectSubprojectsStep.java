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
public class SciProjectSubprojectsStep extends SimpleEditStep {

    private final static Logger s_log = Logger.getLogger(
            SciProjectSubprojectsStep.class);
    private String ADD_CHILD_SHEET_NAME = "addChild";

    public SciProjectSubprojectsStep(ItemSelectionModel itemModel,
                                              AuthoringKitWizard parent) {
        this(itemModel, parent, null);
    }

    public SciProjectSubprojectsStep(
            ItemSelectionModel itemModel,
            AuthoringKitWizard parent,
            String prefix) {
        super(itemModel, parent, prefix);

        BasicItemForm addSubProjectSheet =
                new SciProjectSubprojectAddForm(itemModel);
        add(ADD_CHILD_SHEET_NAME,
            (String) ContenttypesGlobalizationUtil.globalize(
                "cms.contenttypes.ui.genericorgaunit.add_child").localize(),
            new WorkflowLockedComponentAccess(addSubProjectSheet, itemModel),
            addSubProjectSheet.getSaveCancelSection().getCancelButton());

        SciProjectSubProjectsTable subProjectsTable = new SciProjectSubProjectsTable(
                itemModel);
        setDisplayComponent(subProjectsTable);
    }
}
