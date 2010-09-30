package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.BasicItemForm;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;

/**
 *
 * @author Jens Pelzetter
 */
public class ProceedingsPapersStep extends SimpleEditStep {

    private static final String ADD_PAPER_SHEET_NAME = "addPaper";

    public ProceedingsPapersStep(ItemSelectionModel itemModel,
                                 AuthoringKitWizard parent) {
        this(itemModel, parent, null);
    }

    public ProceedingsPapersStep(ItemSelectionModel itemModel,
                                 AuthoringKitWizard parent,
                                 String prefix) {
        super(itemModel, parent, prefix);

        BasicItemForm addPaperSheet =
                new ProceedingsPapersAddForm(itemModel);
        add(ADD_PAPER_SHEET_NAME,
                (String) PublicationGlobalizationUtil.globalize(
                "publications.ui.proceedings.add_paper").localize(),
                new WorkflowLockedComponentAccess(addPaperSheet, itemModel),
                addPaperSheet.getSaveCancelSection().getCancelButton());

        ProceedingsPapersTable papersTable = new ProceedingsPapersTable(
                itemModel);
        setDisplayComponent(papersTable);


    }
}
