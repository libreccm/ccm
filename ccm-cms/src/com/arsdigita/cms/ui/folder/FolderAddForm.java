/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
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
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SingleSelectionModel;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.cms.Folder;
import org.apache.log4j.Logger;

class FolderAddForm extends FolderBaseForm {
    public static final String versionId =
        "$Id: FolderAddForm.java 287 2005-02-22 00:29:02Z sskracic $" +
        "$Author: sskracic $" +
        "$DateTime: 2004/08/17 23:15:09 $";

    private static Logger s_log = Logger.getLogger(FolderAddForm.class);

    private final SingleSelectionModel m_model;
    private final FolderRequestLocal m_parent;

    public FolderAddForm(final SingleSelectionModel model,
                         final FolderRequestLocal parent) {
        super("folder-add");
        
        m_model = model;
        m_parent = parent;

        m_fragment.addValidationListener
            (new ChildUniqueValidationListener(parent));

        addProcessListener(new ProcessListener());
    }

    private class ProcessListener implements FormProcessListener {
        public final void process(final FormSectionEvent e)
                throws FormProcessException {
            final PageState state = e.getPageState();

            final Folder folder = new Folder();

            folder.setParent(m_parent.getFolder(state));
            folder.setLabel((String) m_title.getValue(state));
            folder.setName((String) m_fragment.getValue(state));

            folder.save();

            m_model.setSelectedKey(state, folder.getID().toString());
        }
    }
}
