/*
 * Copyright (C) 2001, 2002, 2003 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the CCM Public
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of
 * the License at http://www.redhat.com/licenses/ccmpl.html
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */

package com.arsdigita.cms.docmgr.ui;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.Link;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.bebop.SegmentedPanel;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.cms.docmgr.Document;
import java.math.BigDecimal;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.web.Application;
import com.arsdigita.web.Web;

/**
 * This component shows the version history of a document. It allows
 * to download historical versions.
 *
 * @author <mailto href="StefanDeusch@computer.org">Stefan Deusch</a>
 *
 */
class FileInfoHistoryPane extends SimpleContainer
    implements DMConstants
{
    private Component m_history;

    // share file instance for all sub components
    private RequestLocal m_file;

    public FileInfoHistoryPane( final Page p ) {

        m_file = new RequestLocal() {
                protected Object initialValue(PageState state) {
                    BigDecimal id = (BigDecimal) state.getValue
			( ((DocmgrBasePage) p).getFileIDParam());
                    return new Document(id);
                }
            };

        SegmentedPanel main = new SegmentedPanel();
        main.setClassAttr("main");

        m_history = makeHistoryPane(main);

        add(main);
        
        Application app = Web.getContext().getApplication();
        if (app != null) {
        	add(new Link("Cancel", app.getPath()));
        }
    }

    private Component makeHistoryPane(SegmentedPanel panel) {

        return panel.addSegment(FILE_REVISION_HISTORY_HEADER,
                                new FileRevisionsTable(this));
    }

    Document getDocument(PageState state) {
        return (Document)m_file.get(state);
    }
}
