package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.BasicItemForm;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.cms.ui.authoring.WorkflowLockedComponentAccess;

/**
 *
 * @author Jens Pelzetter
 */
public class SeriesEditshipStep extends SimpleEditStep {

    private static final String ADD_EDITOR_SHEET_NAME = "addEditor";

    public SeriesEditshipStep(
            ItemSelectionModel itemModel, AuthoringKitWizard parent) {
        this(itemModel, parent, null);
    }

    public SeriesEditshipStep(
            ItemSelectionModel itemModel,
            AuthoringKitWizard parent,
            String prefix) {
        super(itemModel, parent, prefix);

        BasicItemForm addEditorSheet = new SeriesEditshipAddForm(itemModel);
        add(ADD_EDITOR_SHEET_NAME,
            (String) PublicationGlobalizationUtil.globalize(
                "publications.ui.series.add_editship").localize(),
            new WorkflowLockedComponentAccess(addEditorSheet, itemModel),
            addEditorSheet.getSaveCancelSection().getCancelButton());

        SeriesEditshipTable editorsTable = new SeriesEditshipTable(itemModel);
        setDisplayComponent(editorsTable);
    }
}
