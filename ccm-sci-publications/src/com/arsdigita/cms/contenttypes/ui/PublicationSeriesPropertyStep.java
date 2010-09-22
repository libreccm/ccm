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
public class PublicationSeriesPropertyStep extends SimpleEditStep {

    private static final String ADD_SERIES_SHEET_NAME = "addSeries";

    public PublicationSeriesPropertyStep(ItemSelectionModel itemModel,
                                         AuthoringKitWizard parent) {
        this(itemModel, parent, null);
    }

    public PublicationSeriesPropertyStep(ItemSelectionModel itemModel,
                                       AuthoringKitWizard parent,
                                       String prefix) {
        super(itemModel, parent, prefix);

        BasicItemForm addSeriesSheet = new PublicationSeriesAddForm(itemModel);
        add(ADD_SERIES_SHEET_NAME,
            (String) PublicationGlobalizationUtil.globalize(
                "publications.ui.series.add_series").localize(),
            new WorkflowLockedComponentAccess(addSeriesSheet, itemModel),
            addSeriesSheet.getSaveCancelSection().getCancelButton());

        PublicationSeriesTable seriesTable = new PublicationSeriesTable(
                itemModel);
        setDisplayComponent(seriesTable);
    }
}
