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

import com.arsdigita.cms.contenttypes.GenericPerson;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.BasicItemForm;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;
import java.util.Date;

/**
 *
 * @author Jens Pelzetter
 */
public class SeriesEditshipStep extends SimpleEditStep {

    public static final String ADD_EDITOR_SHEET_NAME = "addEditor";
    private GenericPerson selectedEditor;
    private Date selectedEditorDateFrom;
    private Date selectedEditorDateTo;

    public SeriesEditshipStep(
            ItemSelectionModel itemModel, AuthoringKitWizard parent) {
        this(itemModel, parent, null);
    }

    public SeriesEditshipStep(
            ItemSelectionModel itemModel,
            AuthoringKitWizard parent,
            String prefix) {
        super(itemModel, parent, prefix);

        BasicItemForm addEditorSheet = new SeriesEditshipAddForm(itemModel,
                                                                 this);
        add(ADD_EDITOR_SHEET_NAME,
            (String) PublicationGlobalizationUtil.globalize(
                "publications.ui.series.add_editship").localize(),
            new WorkflowLockedComponentAccess(addEditorSheet, itemModel),
            addEditorSheet.getSaveCancelSection().getCancelButton());

        SeriesEditshipTable editorsTable = new SeriesEditshipTable(itemModel,
                                                                   this);
        setDisplayComponent(editorsTable);
    }

    public GenericPerson getSelectedEditor() {
        return selectedEditor;
    }

    public void setSelectedEditor(GenericPerson selectedEditor) {
        this.selectedEditor = selectedEditor;
    }

    public Date getSelectedEditorDateFrom() {
        return selectedEditorDateFrom;
    }

    public void setSelectedEditorDateFrom(Date selectedEditorDateFrom) {
        this.selectedEditorDateFrom = selectedEditorDateFrom;
    }

    public Date getSelectedEditorDateTo() {
        return selectedEditorDateTo;
    }

    public void setSelectedEditorDateTo(Date selectedEditorDateTo) {
        this.selectedEditorDateTo = selectedEditorDateTo;
    }
}
