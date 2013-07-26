/*
 * Copyright (c) 2010 Jens Pelzetter
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 */
package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.Publication;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.BasicItemForm;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;

/**
 *
 * @author Jens Pelzetter
 * @version $Id4
 */
public class SeriesVolumesStep extends SimpleEditStep {

    @SuppressWarnings("PMD.LongVariable")
    protected static final String ADD_VOLUME_SHEET_NAME = "addVolume";
    @SuppressWarnings("PMD.LongVariable")
    private Publication selectedPublication;
    private String selectedVolume;

    public SeriesVolumesStep(final ItemSelectionModel itemModel,
                             final AuthoringKitWizard parent) {
        this(itemModel, parent, null);
    }

    public SeriesVolumesStep(final ItemSelectionModel itemModel,
                             final AuthoringKitWizard parent,
                             final String prefix) {
        super(itemModel, parent, prefix);

        final BasicItemForm addVolumeSheet =
                            new SeriesVolumeAddForm(itemModel, this);
        add(ADD_VOLUME_SHEET_NAME,
            PublicationGlobalizationUtil.globalize("publications.ui.series.add_volume"),
            new WorkflowLockedComponentAccess(addVolumeSheet, itemModel),
            addVolumeSheet.getSaveCancelSection().getCancelButton());

        final SeriesVolumesTable volumesTable = new SeriesVolumesTable(itemModel, this);
        setDisplayComponent(volumesTable);
    }

    public Publication getSelectedPublication() {
        return selectedPublication;
    }

    @SuppressWarnings("PMD.LongVariable")
    public void setSelectedPublication(final Publication selectedPublication) {
        this.selectedPublication = selectedPublication;
    }

    public String getSelectedVolume() {
        return selectedVolume;
    }

    public void setSelectedVolume(final String selectedVolume) {
        this.selectedVolume = selectedVolume;
    }

}
