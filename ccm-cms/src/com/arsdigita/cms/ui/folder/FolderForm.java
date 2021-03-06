/*
 * Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.cms.ui.folder;

import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.cms.Folder;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.ui.authoring.BasicItemForm;
import com.arsdigita.util.Assert;

/**
 * Class FolderForm implements the basic form for creating or renaming folders.
 *
 * @author Jon Orris (jorris@arsdigita.com)
 *
 * @version $Revision #1 $DateTime: 2004/08/17 23:15:09 $
 * @version $Id: FolderForm.java 774 2005-09-12 14:53:47Z fabrice $
 */
public abstract class FolderForm extends BasicItemForm{

    FolderSelectionModel m_currentFolder;

    /**
     * Create a new folder form.
     *
     * @param name Name of the form
     * @param folder SelectionModel containing the current folder being operated on.
     *
     * @pre name != null && folder != null
     */
    public FolderForm(String name, FolderSelectionModel folder) {
        super(name, new ItemSelectionModel(Folder.BASE_DATA_OBJECT_TYPE, Folder.BASE_DATA_OBJECT_TYPE, "fldr"));
        m_currentFolder = folder;
    }

    public void register(Page p) {
        super.register(p);
        p.addComponentStateParam(this, getItemSelectionModel().getStateParameter());
    }

    /**
     * Returns true if the form submission was cancelled.
     */
    public boolean isCancelled(PageState s) {
        return getSaveCancelSection().getCancelButton().isSelected(s);
    }

    /**
     * Validates the form. Checks for name uniqueness.
     */
    public void validate(FormSectionEvent e) throws FormProcessException {
        Folder folder = (Folder) m_currentFolder.getSelectedObject(e.getPageState());
        Assert.exists(folder);
        validateNameUniqueness(folder, e);
    }

    /**
     * Updates a folder with a new parent, name, and label.
     *
     * @param folder The folder to update
     * @param parent The new parent folder. May be null.
     * @param name The new name of the folder
     * @param label The new label for the folder
     */
    final protected void updateFolder(Folder folder, Folder parent, String name, String label) {
        folder.setParent(parent);
        updateFolder(folder, name, label);
    }

    /**
     * Updates a folder with a new name and label.
     *
     * @param folder The folder to update
     * @param name The new name of the folder
     * @param label The new label for the folder
     */
    final protected void updateFolder(Folder folder, String name, String label) {
        folder.setName(name);
        folder.setLabel(label);
        folder.save();

        // also modify the live version of the folder,
        // otherwise items within this folder will keep
        // using the old URL, for example
        Folder live = (Folder) folder.getLiveVersion();
        if (live != null) {
            live.setName(name);
            live.setLabel(label);
            live.save();
        }
    }

    /**
     * Returns the current folder being operated on.
     *
     * @return The current folder
     *
     * @pre state != null
     * @post return != null
     */
    final protected Folder getCurrentFolder(PageState state) {
        Folder folder = (Folder) m_currentFolder.getSelectedObject(state);
        return folder;
    }
}
