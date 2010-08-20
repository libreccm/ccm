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
public class SeriesVolumesStep extends SimpleEditStep {

    private static final String ADD_VOLUME_SHEET_NAME = "addVolume";

    public SeriesVolumesStep(
            ItemSelectionModel itemModel,
            AuthoringKitWizard parent) {
        this(itemModel, parent, null);
    }

    public SeriesVolumesStep(
            ItemSelectionModel itemModel,
            AuthoringKitWizard parent,
            String prefix) {
        super(itemModel, parent, prefix);

        BasicItemForm addVolumeSheet =
                new SeriesVolumeAddForm(itemModel);
        add(ADD_VOLUME_SHEET_NAME,
                (String) PublicationGlobalizationUtil.globalize(
                "publications.ui.series.add_volume").localize(),
                new WorkflowLockedComponentAccess(addVolumeSheet, itemModel),
                addVolumeSheet.getSaveCancelSection().getCancelButton());

        SeriesVolumesTable volumesTable = new SeriesVolumesTable(
                itemModel);
        setDisplayComponent(volumesTable);
    }

}
