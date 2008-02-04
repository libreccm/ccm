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

package com.arsdigita.london.terms.ui.admin;


import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.Resettable;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.event.ActionEvent;

import com.arsdigita.domain.DomainCollection;

import com.arsdigita.london.util.ui.event.DomainObjectActionEvent;
import com.arsdigita.london.util.ui.event.DomainObjectActionListener;
import com.arsdigita.london.util.ui.parameters.DomainObjectParameter;

import com.arsdigita.london.terms.Terms;


public class TermPicker extends SimpleContainer implements Resettable {

    private DomainObjectParameter m_domain;
    private DomainObjectParameter m_term;

    private TermNameSearchForm m_search;
    private TermFilteredListing m_results;
    
    
    public TermPicker(DomainObjectParameter domain,
                      DomainObjectParameter term) {
        super(Terms.XML_PREFIX + 
              ":termPicker",
              Terms.XML_NS);
        m_domain = domain;
        m_term = term;
        
        m_search = new TermNameSearchForm();
        m_search.addCompletionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    PageState state = e.getPageState();
                    if (m_search.getQuery(state) != null) {
                        m_results.setVisible(state, true);
                    } else {
                        state.setValue(m_term, null);
                        fireCompletionEvent(state);
                    }
                }
            });
        add(m_search);
        
        m_results = new TermFilteredListing(domain) {
                protected void applyFilters(PageState state,
                                            DomainCollection terms) {
                    String name = m_search.getQuery(state);
                    terms.addFilter("model.name like :name")
                        .set("name", "%" + name + "%");
                }
            };
        m_results.addDomainObjectActionListener(
            TermFilteredListing.ACTION_VIEW,
            new DomainObjectActionListener() {
                public void actionPerformed(DomainObjectActionEvent e) {
                    PageState state = e.getPageState();
                    
                    state.setValue(m_term, e.getObject());
                    fireCompletionEvent(state);
                }
            });
        
        add(m_results);
    }
    
    public void reset(PageState state) {
        m_results.setVisible(state, false);
    }
    
    public void register(Page p) {
        super.register(p);
        
        p.setVisibleDefault(m_results, false);
    }

}
