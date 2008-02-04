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

package com.arsdigita.bookmarks;

import com.arsdigita.bookmarks.ui.BookmarkEditPane;
import com.arsdigita.bookmarks.ui.BookmarkBasePage;

import com.arsdigita.bebop.BebopMapDispatcher;
import com.arsdigita.bebop.Page;

import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 * Bookmark dispatcher for both Bebop-backed and other URLs.
 *
 * @author Jim Parsons
 */

public class BookmarkDispatcher extends BebopMapDispatcher {


    private static final Logger s_log =
        Logger.getLogger(BookmarkDispatcher.class);

    public BookmarkDispatcher() {
        super();

        Map m = new HashMap();

        Page index = buildIndexPage();

        m.put("", index);

        setMap(m);
    }

    private Page buildIndexPage() {
        BookmarkBasePage p = new BookmarkBasePage();

        p.addRequestListener(new ApplicationAuthenticationListener("admin"));
        p.getBody().add(new BookmarkEditPane());

        p.lock();
        return p;
    }


}
