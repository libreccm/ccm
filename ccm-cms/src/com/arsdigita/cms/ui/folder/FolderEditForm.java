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
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.cms.Folder;

import org.apache.log4j.Logger;

class FolderEditForm extends FolderBaseForm {

    private static Logger s_log = Logger.getLogger(FolderEditForm.class);

    private final FolderRequestLocal m_folder;

    public FolderEditForm(final FolderRequestLocal folder) {
        super("folder-edit");

        m_folder = folder;

        // XXX need to do name uniqueness valdation on m_fragment here
        // as well.

        addInitListener(new InitListener());
        addProcessListener(new ProcessListener());
    }

    private static FolderRequestLocal parent(final FolderRequestLocal folder) {
        final FolderRequestLocal parent = new FolderRequestLocal(null) {
                protected final Object initialValue(final PageState state) {
                    return (Folder) folder.getFolder(state).getParent();
                }
            };

        return parent;
    }

    private class InitListener implements FormInitListener {
        public final void init(final FormSectionEvent e) {
            final PageState state = e.getPageState();

            final Folder folder = m_folder.getFolder(state);

            m_title.setValue(state, folder.getLabel());
            m_fragment.setValue(state, folder.getName());
        }
    }

    private class ProcessListener implements FormProcessListener {
        public final void process(final FormSectionEvent e)
                throws FormProcessException {
            final PageState state = e.getPageState();

            final Folder folder = m_folder.getFolder(state);

            folder.setLabel((String) m_title.getValue(state));
            folder.setName((String) m_fragment.getValue(state));
            folder.save();

            // also modify the live version of the folder,
            // otherwise items within this folder will keep
            // using the old URL, for example
            Folder live = (Folder) folder.getLiveVersion();
            if (live != null) {
                live.setLabel((String) m_title.getValue(state));
                live.setName((String) m_fragment.getValue(state));
                live.save();
            }
        }
    }
}
