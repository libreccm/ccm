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
package com.arsdigita.cms.ui;

import com.arsdigita.bebop.PageState;
import com.arsdigita.cms.ContentCenter;
import com.arsdigita.toolbox.ui.ContextBar;
import com.arsdigita.web.URL;

import org.apache.log4j.Logger;

import java.util.List;

/**
 * <p>The context bar of the content center UI.</p>
 *
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: WorkspaceContextBar.java 2286 2012-03-11 09:14:14Z pboy $
 */
// Made public (instead of unspecified) in 6.6.8
public class WorkspaceContextBar extends ContextBar {

    /** A logger instance, primarily to assist debugging .                    */
    private static final Logger s_log = Logger.getLogger
                                               (WorkspaceContextBar.class);

    /**
     * 
     * @param state
     * @return 
     */
    @Override
    protected List entries(final PageState state) {

        final List entries = super.entries(state);

        final String centerTitle = lz("cms.ui.content_center");
        // final String centerPath = ContentCenter.getURL();
        final String centerPath = ContentCenter.getURL();
        if (s_log.isDebugEnabled()) {
            s_log.debug("Got Url: " + centerPath);
        }
        final URL url = URL.there(state.getRequest(), centerPath);

        entries.add(new Entry(centerTitle, url));

        return entries;
    }

    private static String lz(final String key) {
        return (String) ContentSectionPage.globalize(key).localize();
    }
}
