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

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.domain.DomainCollection;

import com.arsdigita.london.util.ui.ModalContainer;
import com.arsdigita.london.util.ui.ActionLink;
import com.arsdigita.london.util.ui.parameters.DomainObjectParameter;
import com.arsdigita.london.util.ui.event.DomainObjectActionEvent;
import com.arsdigita.london.util.ui.event.DomainObjectActionListener;
import com.arsdigita.london.terms.Domain;
import com.arsdigita.london.terms.Term;
import com.arsdigita.london.terms.Terms;
import com.arsdigita.xml.Element;

import org.apache.log4j.Logger;

public class TermPanel extends ModalContainer {

    private static final Logger s_log = Logger.getLogger(TermPanel.class);

    private TermListing m_termListing;
    private RelatedTermListing m_relatedTermListing;
    private NarrowerTermListing m_narrowerTermListing;
    private ActionLink m_addTerm;
    private TermForm m_termForm;
    private TermDetails m_termDetails;
    private ActionLink m_addNarrowerTerm;
    private ActionLink m_addRootTerm;
    private ActionLink m_removeRootTerm;

    private TermPicker m_termPicker;

    private DomainObjectParameter m_domain;
    private DomainObjectParameter m_term;
    private DomainObjectParameter m_otherTerm;

    public static final String MODE_NO_TERM = "noTerm";
    public static final String MODE_CREATE_TERM = "createTerm";
    public static final String MODE_EDIT_TERM = "editTerm";
    public static final String MODE_VIEW_TERM = "viewTerm";
    public static final String MODE_ADD_NARROWER = "addNarrower";

    public TermPanel(DomainObjectParameter domain) {
        super(Terms.XML_PREFIX + ":termPanel",
              Terms.XML_NS);
        
        m_domain = domain;
        m_term = new DomainObjectParameter("term",
                                           Term.BASE_DATA_OBJECT_TYPE);
        m_otherTerm = new DomainObjectParameter("otherTerm",
                                                Term.BASE_DATA_OBJECT_TYPE);
        
        m_termListing = new TermListing(m_domain, m_term);
        m_narrowerTermListing = new NarrowerTermListing(m_domain, m_term);        
        m_addNarrowerTerm = new ActionLink("Add narrower term");
        m_addNarrowerTerm.setIdAttr("addNarrowerTerm");
        m_addRootTerm = new ActionLink("Set as root term");
        m_addRootTerm.setIdAttr("addRootTerm");
        m_removeRootTerm = new ActionLink("Remove as root term");
        m_removeRootTerm.setIdAttr("removeRootTerm");
        m_termPicker = new TermPicker(m_domain, m_otherTerm);
        m_relatedTermListing = new RelatedTermListing(m_domain, m_term);
        m_addTerm = new ActionLink("Create term");
        m_addTerm.setIdAttr("createTerm");
        m_termDetails = new TermDetails(m_term);
        m_termForm = new TermForm("termForm", m_domain, m_term);

        m_termListing.addDomainObjectActionListener(
            AllTermListing.ACTION_VIEW,
            new ModeChangeListener(MODE_VIEW_TERM));

        m_narrowerTermListing.addDomainObjectActionListener(
            NarrowerTermListing.ACTION_VIEW,
            new ModeChangeListener(MODE_VIEW_TERM));

        m_addNarrowerTerm.addActionListener(
            new ModeChangeListener(MODE_ADD_NARROWER));
        m_addNarrowerTerm.addActionListener(
            new ModeResetListener(m_termPicker));
        m_addRootTerm.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    PageState state = e.getPageState();
                    Domain domain = (Domain)state.getValue(m_domain);
                    Term term = (Term)state.getValue(m_term);
                    domain.addRootTerm(term);
                }
            });
        m_removeRootTerm.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    PageState state = e.getPageState();
                    Domain domain = (Domain)state.getValue(m_domain);
                    Term term = (Term)state.getValue(m_term);
                    domain.removeRootTerm(term);
                }
            });
        m_termPicker.addCompletionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    PageState state = e.getPageState();
                    Term term = (Term)state.getValue(m_term);
                    Term otherTerm = (Term)state.getValue(m_otherTerm);
                    
                    if (otherTerm != null) {
                        term.addNarrowerTerm(otherTerm, true, true);
                        state.setValue(m_otherTerm, null);
                    }
                }
            });
        m_termPicker.addCompletionListener(
            new ModeChangeListener(MODE_VIEW_TERM));            
        m_relatedTermListing.addDomainObjectActionListener(
            RelatedTermListing.ACTION_VIEW,
            new ModeChangeListener(MODE_VIEW_TERM));
        m_relatedTermListing.addDomainObjectActionListener(
            RelatedTermListing.ACTION_VIEW,
            new DomainObjectActionListener() {
                public void actionPerformed(DomainObjectActionEvent e) {
                    PageState state = e.getPageState();
                    Term term = (Term)state.getValue(m_term);
                    state.setValue(m_domain, term.getDomain());
                }
            });

        m_addTerm.addActionListener(
            new ModeChangeListener(MODE_CREATE_TERM));
        m_addTerm.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    e.getPageState().setValue(m_term, null);
                }
            });

        m_termDetails.addDomainObjectActionListener(
            TermDetails.ACTION_DELETE,
            new ModeChangeListener(MODE_NO_TERM));

        m_termDetails.addDomainObjectActionListener(
            TermDetails.ACTION_EDIT,
            new ModeChangeListener(MODE_EDIT_TERM));

        m_termForm.addCompletionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    PageState state = e.getPageState();
                    if (state.getValue(m_term) != null) {
                        setMode(state, MODE_VIEW_TERM);
                    } else {
                        setMode(state, MODE_NO_TERM);
                    }
                }
            });
        
        add(m_termListing);
        add(m_narrowerTermListing);

        SimpleContainer rootLinks = new SimpleContainer();
        add(rootLinks);
        rootLinks.add(m_addNarrowerTerm);
        rootLinks.add(m_addRootTerm);

        add(m_removeRootTerm);
        add(m_termPicker);
        add(m_relatedTermListing);
        add(m_addTerm);
        add(m_termDetails);
        add(m_termForm);

        registerMode(MODE_NO_TERM,
                     new Component[] { m_termListing, 
                                       m_addTerm });
        registerMode(MODE_VIEW_TERM,
                     new Component[] { m_termListing, 
                                       m_addTerm, 
                                       m_termDetails,
                                       m_narrowerTermListing,
                                       rootLinks,
                                       m_removeRootTerm,
                                       m_relatedTermListing });
        registerMode(MODE_EDIT_TERM,
                     new Component[] { m_termListing, 
                                       m_termForm });
        registerMode(MODE_CREATE_TERM,
                     new Component[] { m_termListing, 
                                       m_termForm });

        registerMode(MODE_ADD_NARROWER,
                     new Component[] { m_termListing, 
                                       m_termPicker });

        setDefaultMode(MODE_NO_TERM);
    }
    
    public void register(Page p) {
        super.register(p);
        
        p.addGlobalStateParam(m_term);
        p.addGlobalStateParam(m_otherTerm);
    }
    
    
    public void generateXML(PageState state,
                            Element parent) {
        
        Domain domain = (Domain)state.getValue(m_domain);
        Term term = (Term)state.getValue(m_term);
        
        if (term != null) {
            DomainCollection rootTerms = domain.getRootTerms();
            rootTerms.addEqualsFilter(Term.ID, 
                                      term.getID());
            boolean isRoot = rootTerms.next();
            rootTerms.close();

            // The isChild test is bogus. Any term term can be a root term.
            //DomainCollection broaderTerms = term.getBroaderTerms();
            //boolean isChild = broaderTerms.next();
            //broaderTerms.close();
            
            if (isRoot) {
                s_log.debug("Is Root");
                // Already a root term, so only show the remove
                // link
                m_addRootTerm.setVisible(state, false);
                m_removeRootTerm.setVisible(state, true);
//            } else if (isChild) {
//                s_log.debug("Is Child");
//                // In hierarchy, but not a root term, so don't
//                // let them futz with it
//                m_addRootTerm.setVisible(state, false);
//                m_removeRootTerm.setVisible(state, false);
            } else {
                s_log.debug("Nothing");
                // Not in hierarchy at all, so allow it be 
                // assigned as a root
                m_addRootTerm.setVisible(state, true);
                m_removeRootTerm.setVisible(state, false);
            }
        }

        super.generateXML(state, parent);
    }
}
