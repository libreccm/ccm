/*
 * Copyright (c) 2013 Jens Pelzetter
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
package com.arsdigita.bookmarks;

import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Link;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bookmarks.util.GlobalizationUtil;
import com.arsdigita.ui.admin.applications.AbstractApplicationManager;
import com.arsdigita.ui.admin.applications.ApplicationInstanceAwareContainer;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class BookmarksAppManager extends AbstractApplicationManager<Bookmarks> {

    public Class<Bookmarks> getApplication() {
        return Bookmarks.class;
    }

    public ApplicationInstanceAwareContainer getApplicationAdminForm() {
        return new ApplicationAdminForm();
    }

    private class ApplicationAdminForm extends ApplicationInstanceAwareContainer {

        public ApplicationAdminForm() {
            super();

            final BoxPanel panel = new BoxPanel();
            panel.add(new Label(GlobalizationUtil.globalize("bookmarks.ui.not_yet_included_into_app_tab")));
            final Link link = new Link(new PrintListener() {
                public void prepare(final PrintEvent event) {
                    final Link link = (Link) event.getTarget();

                    final String path = String.format("%s/admin", getAppInstance().getPath());
                    final String label = String.format("/ccm%s", path);

                    link.setTarget(path);
                    link.setChild(new Label(label));
                }

            });
            panel.add(link);

            add(panel);
        }

    }
    
     public boolean allowRoot() {
        return false;
    }
    
}
