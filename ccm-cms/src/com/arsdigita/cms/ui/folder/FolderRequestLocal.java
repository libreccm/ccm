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

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SingleSelectionModel;
import com.arsdigita.cms.Folder;
import com.arsdigita.cms.ui.ACSObjectRequestLocal;
import org.apache.log4j.Logger;

import java.math.BigDecimal;

public class FolderRequestLocal extends ACSObjectRequestLocal {
    public static final String versionId =
        "$Id: FolderRequestLocal.java 754 2005-09-02 13:26:17Z sskracic $" +
        "$Author: sskracic $" +
        "$DateTime: 2004/08/17 23:15:09 $";

    private static final Logger s_log = Logger.getLogger
        (FolderRequestLocal.class);

    private final SingleSelectionModel m_model;

    public FolderRequestLocal(final SingleSelectionModel model) {
        m_model = model;
    }

    public FolderRequestLocal() {
        m_model = null;
    }

    protected Object initialValue(final PageState state) {
        if (m_model == null) {
            return null;
        } else {
            final String id = m_model.getSelectedKey(state).toString();

            return new Folder(new BigDecimal(id));
        }
    }

    public final Folder getFolder(final PageState state) {
        return (Folder) get(state);
    }
}
