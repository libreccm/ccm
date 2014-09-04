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
 *
 */
package com.arsdigita.kernel.ui;

import com.arsdigita.bebop.BlockStylable;
import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.CompoundComponent;
import com.arsdigita.bebop.ExcursionComponent;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.FormSection;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.ModalContainer;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.form.FormErrorDisplay;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.SingleSelect;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.kernel.Party;
import com.arsdigita.kernel.PartyCollection;
import com.arsdigita.persistence.OID;
import com.arsdigita.util.Assert;
import com.arsdigita.util.UncheckedWrapperException;

import java.math.BigDecimal;
import java.util.TooManyListenersException;

/**
 * Find a party in 2 steps: keyword search query followed by selecting
 * from among the search results.
 *
 * Use PartySearchSelect.Search if you only want step 1 (the search
 * functionality) and not step 2 (the select form).
 *
 * @author Oumi Mehrota
 */
public class PartySearchSelect
    extends CompoundComponent
    implements ExcursionComponent, Globalized
{
    private Search m_partySearch;
    private PartySelect m_partySelect;
    private RequestLocal m_searchResults;

    /**
     * Construct a search/select component for finding a party among
     * all the parties in the system.
     **/
    public PartySearchSelect() {
        m_partySearch = new Search();
        init();
    }

    /**
     * Construct a search/select component for finding a party among
     * those in the specified party collection.
     *
     * @pre basePartyCollection.get() instanceof PartyCollection
     **/
    public PartySearchSelect(RequestLocal basePartyCollection) {
        m_partySearch = new Search(basePartyCollection);
        init();
    }

    private void init() {
        m_searchResults = new RequestLocal() {
                public Object initialValue(PageState ps) {
                    return getSearchQuery(ps);
                }
            };

        m_partySelect = new PartySelect(m_searchResults);

        m_partySearch.addCompletionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    PageState ps = e.getPageState();
                    m_partySelect.setVisible(ps, true);
                    m_partySearch.setVisible(ps, false);
                }
            });
        m_partySelect.addCompletionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    PageState ps = e.getPageState();
                    m_partySelect.setVisible(ps, false);
                    m_partySearch.setVisible(ps, true);
                    PartySearchSelect.this.fireCompletionEvent(ps);
                }
            });

        add(m_partySearch);
        add(m_partySelect);
    }

    /**
     * Specifies the set of parties from which the user will search and select.
     * Mainly useful in conjunction with addSearchFormSection(FormSection).
     **/
    public void setBaseQuery(PageState ps, PartyCollection parties) {
        m_partySearch.setBaseQuery(ps, parties);
    }

    /**
     * Returns the collection of parties mathing the user's search criteria.
     * Mainly useful in conjunction with addSearchFormSection(FormSection).
     **/
    public PartyCollection getSearchQuery(PageState ps) {
        return m_partySearch.getSearchQuery(ps);
    }

    /**
     * Adds a form section to the search form, used to provide
     * additional UI for filtering/controlling the search results.
     * The process listener of the form section can use
     * getSearchQuery(PageState) in order to get the base search query
     * and add filters to it.  Alternatively, the process listener can
     * use setSearchQuery(PageState, PartyCollection) to set the base
     * query from scratch.
     **/
    public void addSearchFormSection(FormSection formSection) {
        m_partySearch.addFormSection(formSection);
    }

    /**
     * Returns which party was selected by the user.  This will be null
     * if the user cancels or does not complete the search-and-select process.
     **/
    public Party getSelectedParty(PageState ps) {
        return m_partySelect.getSelectedParty(ps);
    }

    public void register(Page p) {
        super.register(p);
        p.setVisibleDefault(m_partySelect, false);
        p.setVisibleDefault(m_partySearch, true);
    }

    /**
     * A search form for searching a set of parties by name/email fragment.
     **/
    public static class Search extends CompoundComponent {
        private RequestLocal m_partyQuery;
        private SimpleContainer m_formSections;

        /**
         * Construct a search component for searching all parties in
         * the system.
         **/
        public Search() {
            this(new RequestLocal() {
                    public Object initialValue(PageState ps) {
                        return Party.retrieveAllParties();
                    }
                });
        }

        /**
         * Construct a search component for searching within the specified
         * party collection.
         *
         * @pre basePartyCollection.get() instanceof PartyCollection
         **/
        public Search (RequestLocal basePartyCollection) {
            Assert.isTrue(basePartyCollection != null);

            m_partyQuery = basePartyCollection;

            final Form partySearch = new Form("partysearch");
            partySearch.setMethod(Form.POST);

            add(partySearch);

            partySearch.add(new FormErrorDisplay(partySearch),
                            ColumnPanel.FULL_WIDTH);
            partySearch.add(new Label("Search users and groups " +
                                      "whose names contain:"));
            final TextField query = new TextField("query");
            partySearch.add(query);
            partySearch.add(new Label(""));

            m_formSections = new SimpleContainer();
            partySearch.add(m_formSections);

            partySearch.add(new Submit("Search"));

            partySearch.addProcessListener(new FormProcessListener() {
                    public void process(FormSectionEvent e)
                        throws FormProcessException {
                        PageState ps = e.getPageState();

                        PartyCollection parties = Party.retrieveAllParties();
                        parties.filter((String) query.getValue(ps));

                        m_partyQuery.set(ps, parties);
                        Search.this.fireCompletionEvent(ps);
                    }
                });

        }

        /**
         * Specifies the set of parties within which the user will search.
         * Mainly useful in conjunction with addFormSection(FormSection).
         **/
        public void setBaseQuery(PageState ps,
                                 PartyCollection parties) {
            m_partyQuery.set(ps, parties);
        }

        /**
         * Returns the collection of parties mathing the user's search
         * criteria.
         **/
        public PartyCollection getSearchQuery(PageState ps) {
            return (PartyCollection) m_partyQuery.get(ps);
        }

        /**
         * Adds a form section to the search form, used to provide
         * additional UI for filtering/controlling the search results.
         * The process listener of the form section can use
         * getSearchQuery(PageState) in order to get the base search query
         * and add filters to it.  Alternatively, the process listener can
         * use setBaseQuery(PageState, PartyCollection) to set the base
         * query from scratch.
         **/
        public void addFormSection(FormSection formSection) {
            m_formSections.add(formSection);
        }
    }

    private static class PartySelect extends CompoundComponent {
        private Label m_emptyView;
        private Form m_form;
        private SingleSelect m_select;
        private ModalContainer m_selectContainer;
        private RequestLocal m_partyQuery;
        private Submit m_submit;
        private Submit m_cancel;

        public PartySelect(RequestLocal parties) {
            m_partyQuery = parties;
            m_form = new Form("partyselect");
            add(m_form);

            m_selectContainer = new ModalContainer();
            m_form.add(m_selectContainer);

            m_select = new SingleSelect(
                                        new BigDecimalParameter("partySearchResult")
                                        );
            m_selectContainer.add(m_select);
            m_selectContainer.setDefaultComponent(m_select);
            try {
                m_select.addPrintListener(new PrintListener() {
                        public void prepare(PrintEvent evt) {
                            PartySelect.this.initPartyChoices(evt.getPageState());
                        }
                    });
            } catch (TooManyListenersException e) {
                Assert.fail(e.getMessage());
            }

            m_emptyView = new Label(
                new GlobalizedMessage("kernel.ui.no_users_or_groups_found", 
                                      BUNDLE_NAME));
            m_emptyView.setFontWeight(Label.ITALIC);
            m_selectContainer.add(m_emptyView);

            m_form.add(new Label());
            m_cancel = new Submit("Cancel");
            m_form.add(m_cancel, BlockStylable.RIGHT);
            m_submit = new Submit("OK");
            m_form.add(m_submit);

            m_form.addInitListener(new FormInitListener() {
                    public void init(FormSectionEvent evt) {
                        PartySelect.this.init(evt.getPageState());
                    }
                });
            m_form.addProcessListener(new FormProcessListener() {
                    public void process(FormSectionEvent evt)
                        throws FormProcessException {
                        PartySelect.this.process(evt.getPageState());
                    }
                });
        }

        protected void initPartyChoices(PageState ps) {
            m_select.clearOptions();
            
            PartyCollection parties = (PartyCollection) m_partyQuery.get(ps);

            boolean isEmpty = true;

            try {
                while (parties.next()) {
                    isEmpty = false;
                    m_select
                        .addOption(new Option(parties.getID().toString(),
                                              parties.getDisplayName()),
                                   ps);
                }
            } finally {
                parties.close();
            }
        }

        protected void init(PageState ps) {
            PartyCollection parties = (PartyCollection) m_partyQuery.get(ps);
            if (parties.size()==0) {
                m_selectContainer.setVisibleComponent(ps, m_emptyView);
            } else {
                m_selectContainer.setVisibleComponent(ps, m_select);
            }
        }

        protected void process(PageState ps) {
            fireCompletionEvent(ps);
        }

        public Party getSelectedParty(PageState ps) {
            if (m_emptyView.isVisible(ps)) {
                return null;
            }
            if (! m_submit.isSelected(ps)) {
                return null;
            }

            BigDecimal partyID = (BigDecimal) m_select.getValue(ps);
            if (partyID==null) {
                return null;
            }
            try {
                return (Party) DomainObjectFactory
                    .newInstance(new OID(Party.BASE_DATA_OBJECT_TYPE,
                                         partyID));
            } catch (DataObjectNotFoundException ex) {
                throw new UncheckedWrapperException(ex.getMessage());
            }
        }
    }
}
