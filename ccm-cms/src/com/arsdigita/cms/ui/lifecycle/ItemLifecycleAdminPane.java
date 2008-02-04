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
package com.arsdigita.cms.ui.lifecycle;

import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.cms.CMS;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.lifecycle.Lifecycle;
import com.arsdigita.cms.ui.BaseItemPane;
import com.arsdigita.cms.ui.item.ContentItemRequestLocal;
import com.arsdigita.toolbox.ui.LayoutPanel;
import org.apache.log4j.Logger;

/**
 * @author Michael Pih
 * @author Jack Chung
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: ItemLifecycleAdminPane.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class ItemLifecycleAdminPane extends BaseItemPane {
    public static final String versionId =
        "$Id: ItemLifecycleAdminPane.java 287 2005-02-22 00:29:02Z sskracic $" +
        "$Author: sskracic $" +
        "$DateTime: 2004/08/17 23:15:09 $";

    private static final Logger s_log = Logger.getLogger
        (ItemLifecycleAdminPane.class);

    private final ContentItemRequestLocal m_item;
    private final LifecycleRequestLocal m_lifecycle;

    private final LayoutPanel m_introPane;
    private final LayoutPanel m_detailPane;
    private final LayoutPanel m_selectPane;

    public ItemLifecycleAdminPane(final ContentItemRequestLocal item) {
        m_item = item;
        m_lifecycle = new ItemLifecycleRequestLocal();

        m_introPane = new LayoutPanel();
        add(m_introPane);

        final Label message = new Label(gz("cms.ui.item.lifecycle.intro"));
        m_introPane.setBody(message);

        m_detailPane = new LayoutPanel();
        add(m_detailPane);

        final ItemLifecycleItemPane itemPane =
            new ItemLifecycleItemPane(m_item, m_lifecycle);
        m_detailPane.setBody(itemPane);

        m_selectPane = new LayoutPanel();
        add(m_selectPane);

        final ItemLifecycleSelectForm selectForm =
            new ItemLifecycleSelectForm(m_item);
        m_selectPane.setBody(selectForm);

        connect(selectForm, m_detailPane);
    }

    private class ItemLifecycleRequestLocal extends LifecycleRequestLocal {
        protected final Object initialValue(final PageState state) {
            final ContentItem item = m_item.getContentItem(state);
            final Lifecycle lifecycle = item.getLifecycle();

            s_log.debug("Returning lifecycle " + lifecycle);

            return lifecycle;
        }
    }

    public final void register(final Page page) {
        super.register(page);

        page.addActionListener(new VisibilityListener());
    }

    private class VisibilityListener implements ActionListener {
        public final void actionPerformed(final ActionEvent e) {
            s_log.debug("Determining which pane to show");

            final PageState state = e.getPageState();

            if (state.isVisibleOnPage(ItemLifecycleAdminPane.this)) {
                if (m_lifecycle.getLifecycle(state) == null) {
                    if (hasPermission(state)) {
                        push(state, m_selectPane);
                    } else {
                        push(state, m_introPane);
                    }
                } else {
                    push(state, m_detailPane);
                }
            }
        }
    }

    private boolean hasPermission(final PageState state) {
        final ContentItem item = m_item.getContentItem(state);

        return CMS.getContext().getSecurityManager().canAccess
            (state.getRequest(), SCHEDULE_PUBLICATION, item);
    }
}
