/*
 * Copyright (C) 2001, 2002 Red Hat Inc. All Rights Reserved.
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

import org.apache.log4j.Category;

import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.TabbedPane;

/**
 * Dispatcher for document category navigator.
 *
 * @author Crag Wolfe
 */

public class DCNDispatcher extends DMDispatcher implements DMConstants {
    public static final String versionId =
        "$Id: //apps/docmgr-cms/dev/src/com/arsdigita/cms/docmgr/ui/DCNDispatcher.java#2 $" +
        "$Author: cwolfe $" +
        "$DateTime: 2003/08/14 12:28:36 $";

    private static Category s_log = Category.getInstance
        (DCNDispatcher.class.getName());

    /**
     * Default constructor instantiating the URL-page map.
     */
    public DCNDispatcher() {
        addPage("", buildDCNIndexPage(), true);
        addPage("file", buildFileInfoPage());
    }

    /**
     * Build index page to browse documents by category
    */

    protected Page buildDCNIndexPage() {
        Page p = new DocmgrBasePage();

        /**
         * Create main administration tab.
         */
        TabbedPane tb = new TabbedPane();
        tb.setIdAttr("page-body");

        p.add(new BrowseCatDocsPane());
        p.lock();

        return p;
    }
}
