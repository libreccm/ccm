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
package com.arsdigita.cms.publicpersonalprofile;

import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormSection;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.ParameterSingleSelectionModel;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.publicpersonalprofile.ui.PublicPersonalProfileNavItemsAddForm;
import com.arsdigita.cms.publicpersonalprofile.ui.PublicPersonalProfileNavItemsTable;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class PublicPersonalProfilesAdminPanel extends BoxPanel {

    private final StringParameter navItemKeyParam;    

    public PublicPersonalProfilesAdminPanel() {
        super(BoxPanel.VERTICAL);

        navItemKeyParam = new StringParameter("selectedNavItem");
        final ParameterSingleSelectionModel navItemSelect = new ParameterSingleSelectionModel(navItemKeyParam);
        
        final Form form = new Form("PublicPersonalProfileAdmin");        
        final BoxPanel panel = new BoxPanel(BoxPanel.VERTICAL);
        final FormSection tableSection = new FormSection(panel);        
        final PublicPersonalProfileNavItemsAddForm addForm = new PublicPersonalProfileNavItemsAddForm(navItemSelect);
        final PublicPersonalProfileNavItemsTable table = new PublicPersonalProfileNavItemsTable(navItemSelect);
        
        panel.add(table);
        form.add(tableSection);        
        panel.add(addForm);        
        add(form);
    }

    @Override
    public void register(final Page page) {
        page.addGlobalStateParam(navItemKeyParam);
    }

}
