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
package com.arsdigita.subsite.ui;

import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Link;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.subsite.Subsite;
import com.arsdigita.ui.admin.GlobalizationUtil;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class AppManagerPanel extends SimpleContainer {

//    private final SiteSelectionModel selectionModel = new SiteSelectionModel(new BigDecimalParameter("site"));

    public AppManagerPanel() {
        super(Subsite.SUBSITE_XML_PREFIX + "controlCenter",
              Subsite.SUBSITE_XML_NS);

        final BoxPanel panel = new BoxPanel(BoxPanel.VERTICAL);
        final Label warnLabel = new Label(GlobalizationUtil.globalize("ui.admin.applications.form_not_compatible_now"));
        warnLabel.setClassAttr("warning");
        add(warnLabel);
        panel.add(warnLabel);
        panel.add(new Link("", "/ccm/admin/subsite"));
//        add(new SiteListing(selectionModel));
//        add(new SiteForm("site", selectionModel));
        
        add(panel);
    }
    
    @Override
    public void register(final Page page) {
        super.register(page);
//        page.addGlobalStateParam(selectionModel.getStateParameter());
    }

}
