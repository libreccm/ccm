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

import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.SecurityManager;
import com.arsdigita.cms.dispatcher.Utilities;
import com.arsdigita.cms.ui.item.ContentItemRequestLocal;
import com.arsdigita.dispatcher.AccessDeniedException;
import com.arsdigita.kernel.User;
import com.arsdigita.util.Assert;
import com.arsdigita.web.Web;
import org.apache.log4j.Logger;

/**
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: FormSecurityListener.java 1942 2009-05-29 07:53:23Z terry $
 */
public class FormSecurityListener implements FormSubmissionListener {

    private static Logger s_log = Logger.getLogger(FormSecurityListener.class);

    private final String m_action;
    private final ContentItemRequestLocal m_item;

    public FormSecurityListener(final String action,
                                final ContentItemRequestLocal item) {
        Assert.exists(action, String.class);

        m_action = action;
        m_item = item;
    }

    public FormSecurityListener(final String action) {
        this(action, null);
    }

    public final void submitted(final FormSectionEvent e)
            throws FormProcessException {
        final PageState state = e.getPageState();
        final User user = Web.getContext().getUser();
        final SecurityManager sm = Utilities.getSecurityManager(state);
        
        if (m_item == null) {
            if(sm.canAccess(user, m_action)) {
                return;
            }
        } else {

            final ContentItem item = m_item.getContentItem(state);
            
            if (sm.canAccess(user, m_action, item)) {
                return;
            }
        }

        throw new AccessDeniedException();
    }
}
