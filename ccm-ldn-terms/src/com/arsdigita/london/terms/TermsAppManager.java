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
package com.arsdigita.london.terms;

import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.SegmentedPanel;
import com.arsdigita.london.terms.ui.TermGlobalizationUtil;
import com.arsdigita.london.terms.ui.admin.DomainEditForm;
import com.arsdigita.london.terms.ui.admin.DomainMappingAddForm;
import com.arsdigita.london.terms.ui.admin.DomainMappingTable;
import com.arsdigita.london.terms.ui.admin.DomainTable;
import com.arsdigita.london.util.ui.parameters.DomainObjectParameter;
import com.arsdigita.ui.admin.applications.AbstractSingletonApplicationManager;
import com.arsdigita.ui.admin.applications.ApplicationInstanceAwareContainer;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class TermsAppManager extends AbstractSingletonApplicationManager<Terms> {

    private DomainObjectParameter selected = new DomainObjectParameter("selectedDomain");

    ;
    
    @Override
    public Class<Terms> getApplication() {
        return Terms.class;
    }

    @Override
    public ApplicationInstanceAwareContainer getApplicationAdminForm() {
        //final ApplicationInstanceAwareContainer container = new ApplicationInstanceAwareContainer();
        final TermAdminContainer container = new TermAdminContainer();

//        final BoxPanel panel = new BoxPanel(BoxPanel.VERTICAL);
//        final Label warnLabel = new Label(GlobalizationUtil.globalize("ui.admin.applications.form_not_compatible_now"));
//        warnLabel.setClassAttr("warning");
//        panel.add(warnLabel);
//        panel.add(new Link("Terms Admin", "/admin/terms"));
//
//        panel.add(container);

        final DomainTable domains = new DomainTable(selected);
        final DomainEditForm editForm = new DomainEditForm("domainEditForm", selected);
        final DomainMappingTable mappings = new DomainMappingTable(selected);
        final DomainMappingAddForm mappingAddForm = new DomainMappingAddForm(selected);
        final BoxPanel mappingPanel = new BoxPanel(BoxPanel.VERTICAL);
        
        mappingPanel.add(mappings);
        mappingPanel.add(mappingAddForm);
        
        container.add(domains);
        
        final SegmentedPanel panel = new SegmentedPanel();
        panel.addSegment(new Label(TermGlobalizationUtil.globalize("term.domain.ui.basic_properties")), 
                         editForm);
        panel.addSegment(new Label(TermGlobalizationUtil.globalize("term.domain.ui.mappings")),
                         mappingPanel);
        
//        container.add(editForm);
        
        container.add(panel);

        return container;
    }

    private class TermAdminContainer extends ApplicationInstanceAwareContainer {
    
        @Override
        public void register(final Page page) {
            super.register(page);

            page.addGlobalStateParam(selected);
        }

    }
}
