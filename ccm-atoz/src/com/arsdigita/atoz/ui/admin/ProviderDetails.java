/*
 * Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
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
 */
package com.arsdigita.atoz.ui.admin;

import com.arsdigita.kernel.ui.ACSObjectSelectionModel;

import com.arsdigita.atoz.AtoZ;
import com.arsdigita.atoz.AtoZProvider;
import com.arsdigita.atoz.ui.AtoZGlobalizationUtil;
import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.Label;

import com.arsdigita.bebop.SimpleComponent;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;

import com.arsdigita.domain.DomainObjectXMLRenderer;

import com.arsdigita.xml.Element;

public class ProviderDetails extends SimpleComponent {

    private final ACSObjectSelectionModel m_provider;
    private final ColumnPanel panel;

    public ProviderDetails(final ACSObjectSelectionModel provider) {
        m_provider = provider;

        panel = new ColumnPanel(2);

        panel.add(new Label(AtoZGlobalizationUtil.globalize("atoz.ui.provider_details.title")));
        final Label titleLabel = new Label();
        titleLabel.addPrintListener(new PrintListener() {
            public void prepare(final PrintEvent event) {
                final PageState state = event.getPageState();

                final AtoZProvider provider = (AtoZProvider) m_provider.getSelectedObject(state);

                final Label target = (Label) event.getTarget();
                target.setLabel(provider.getTitle());
            }

        });
        panel.add(titleLabel);

        panel.add(new Label(AtoZGlobalizationUtil.globalize("atoz.ui.provider_details.description")));
        final Label descLabel = new Label();
        descLabel.addPrintListener(new PrintListener() {
            public void prepare(final PrintEvent event) {
                final PageState state = event.getPageState();

                final AtoZProvider provider = (AtoZProvider) m_provider.getSelectedObject(state);

                final Label target = (Label) event.getTarget();
                target.setLabel(provider.getDescription());
            }

        });
        panel.add(descLabel);
    }

    @Override
    public void generateXML(final PageState state, final Element parent) {

        panel.generateXML(state, parent);

//        Element content = AtoZ.newElement("providerDetails");
//        exportAttributes(content);
//
//        AtoZProvider provider = (AtoZProvider)
//            m_provider.getSelectedObject(state);
//        
//        DomainObjectXMLRenderer xr = new DomainObjectXMLRenderer(content);
//        xr.setWrapRoot(false);
//        xr.setWrapAttributes(true);
//        xr.setWrapObjects(false);
//        
//        xr.walk(provider, ProviderDetails.class.getName());
//
//        parent.addContent(content);
    }

}
