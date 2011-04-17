/*
 * Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.docrepo.ui;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.bebop.SegmentedPanel;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.docrepo.File;
import java.math.BigDecimal;
import com.arsdigita.domain.DataObjectNotFoundException;

/**
 * This component shows the version history of a document. It allows
 * to download historical versions.
 *
 * @author <mailto href="StefanDeusch@computer.org">Stefan Deusch</a>
 *
 */
class FileInfoHistoryPane extends SimpleContainer
    implements DRConstants
{
    private Component m_history;

    // share file instance for all sub components
    private RequestLocal m_file;

    public FileInfoHistoryPane() {

        m_file = new RequestLocal() {
                @Override
                protected Object initialValue(PageState state) {
                    BigDecimal id = (BigDecimal) state.getValue(FILE_ID_PARAM);
                    File file = null;
                    try {
                        file = new File(id);
                    } catch(DataObjectNotFoundException nfe) {
                        // ...
                    }
                    return file;
                }
            };

        SegmentedPanel main = new SegmentedPanel();
        main.setClassAttr("main");

        m_history = makeHistoryPane(main);

        add(main);
    }

    private Component makeHistoryPane(SegmentedPanel panel) {

        return panel.addSegment(FILE_REVISION_HISTORY_HEADER,
                                new FileRevisionsTable(this));
    }

    @Override
    public void register(Page p) {
        p.addGlobalStateParam(FILE_ID_PARAM);
        super.register(p);
    }

    File getFile(PageState state) {
        return (File)m_file.get(state);
    }
}
