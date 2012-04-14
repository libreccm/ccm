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

package com.arsdigita.london.atoz.ui.terms;

import com.arsdigita.atoz.ui.admin.ProviderAdmin;
import com.arsdigita.atoz.ui.admin.ProviderDetails;

import com.arsdigita.bebop.ActionLink;
import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.kernel.ui.ACSObjectSelectionModel;

/**
 * 
 */
public class DomainProviderAdmin extends ProviderAdmin {
    
    private DomainProviderForm m_detailsForm;
    private ActionLink m_editDetails;
    private ProviderDetails m_details;

    /**
     * Constructor
     */
    public DomainProviderAdmin(ACSObjectSelectionModel provider) {
        super("domainProviderAdmin", 
              provider);
               
        m_details = new ProviderDetails(provider);
        m_detailsForm = new DomainProviderForm(provider);
        m_editDetails = new ActionLink("Edit details");
        m_editDetails.setIdAttr("edit");

        add(m_details);
        add(m_detailsForm);
        add(m_editDetails);

        m_editDetails.addActionListener(new DomainProviderEditStart());
        m_detailsForm.addCompletionListener(new DomainProviderEditComplete());
    }

    public void register(Page p) {
        super.register(p);
        
        p.setVisibleDefault(m_detailsForm, false);
    }

    private void switchMode(PageState state,
                            Component form,
                            boolean active) {
        form.setVisible(state, active);
                
        m_details.setVisible(state, !active);
        m_editDetails.setVisible(state, !active);
    }

    private class DomainProviderEditStart implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            PageState state = e.getPageState();
            
            switchMode(state, m_detailsForm, true);
        }
    }

    private class DomainProviderEditComplete implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            PageState state = e.getPageState();

            switchMode(state, m_detailsForm, false);
        }
    }

}
