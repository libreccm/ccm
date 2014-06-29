/*
 * Copyright (C) 2001 - 2003 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the CCM Public
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of
 * the License at http://www.redhat.com/licenses/ccmpl.html
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 * Created on Dec 18, 2003
 *
 */
package com.arsdigita.cms.docmgr.ui;

import com.arsdigita.bebop.*;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormValidationListener;
import com.arsdigita.bebop.form.MultipleSelect;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.ArrayParameter;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.bebop.parameters.TrimmedStringParameter;
import com.arsdigita.bebop.parameters.URLParameter;
import com.arsdigita.bebop.util.Color;
import com.arsdigita.categorization.Category;
import com.arsdigita.categorization.CategoryCollection;
import com.arsdigita.cms.ContentBundle;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.ContentSectionCollection;
import com.arsdigita.cms.docmgr.DocFolder;
import com.arsdigita.cms.docmgr.DocLink;
import com.arsdigita.cms.docmgr.DocMgr;
import com.arsdigita.cms.docmgr.search.SearchResults;
import com.arsdigita.cms.docmgr.search.SearchUtils;
import com.arsdigita.dispatcher.ObjectNotFoundException;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.KernelExcursion;
import com.arsdigita.kernel.User;
import com.arsdigita.kernel.permissions.PermissionService;
import com.arsdigita.util.StringUtils;
import com.arsdigita.util.UncheckedWrapperException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import org.apache.log4j.Logger;

/**
 * A UI Container class used to create instances of <code>DocLink</code> object 
 * type.
 *
 * Contains a simple Search Form to search Documents by name. A table to display
 * the results the of the search. ActionLinks in the action column of
 * <code>CreateDocLinkSearchTable</code> are responsible to actually create the
 * Links to the Document represented in the corresponding row of the table.
 *
 * @author <a href="mailto:sshinde@redhat.com">Shashin Shinde</a>
 *
 * $Id:
 * //apps/docmgr-cms/dev/src/com/arsdigita/cms/docmgr/ui/CreateDocLinkPane.java#2
 * $
 *
 */
public class CreateDocLinkPane extends SimpleContainer
        implements DMConstants {

    private static final Logger s_log = Logger.getLogger(
                                               CreateDocLinkPane.class.getName());

    public static final String PARAM_EDIT_DOCLINK_ID = "editDoclinkID";
    private static final String SEARCH_DOCUMENT = "searchDocument";
    private static final String TITLE = "title";
    private static final String DESCRIPTION = "description";
    private static final String EXTERNAL_URL = "url";
    private static final String CATEGORIES = "file-edit-cats";
    private final BigDecimalParameter m_doclinkIDParam = 
                      new BigDecimalParameter(PARAM_EDIT_DOCLINK_ID);
    private final TrimmedStringParameter m_searchDocsParam = 
                      new TrimmedStringParameter(SEARCH_DOCUMENT);
    private Label m_emptyLabel;
    private Label m_errorMessages;
    private Tree m_tree;
    private Paginator m_paginator;

    /**
     * Constructor.
     *
     * @param parent to which this Table is added.Used to return after creating
     * the link and cleaning up the Table selection.
     * @param tree.Tree object to get the currently selected parent Folder under
     * which to create the link.
     * @param form.Search Form to get the terms to search for.
     */
    public CreateDocLinkPane(Component parent, Tree tree) {
        m_tree = tree;
        //Main Container.
        //ColumnPanel m_mainContainer = new ColumnPanel(ColumnPanel.VERTICAL, true);
        ColumnPanel m_mainContainer = new ColumnPanel(1);

        add(new Label(" "));
        add(new Label(" "));

        add(new Label(new GlobalizedMessage("ui.link.create.explanation", BUNDLE_NAME)),
                ColumnPanel.LEFT);

        URLEntryForm m_entryForm = new URLEntryForm(new GridPanel(2), parent);

        m_mainContainer.add(m_entryForm);
        m_mainContainer.add(new Label(" "));
        m_mainContainer.add(new Label(" "));

        //Add form at the top.
        SearchForm topSearchForm = new DocumentSearchForm(new GridPanel(2));
        m_mainContainer.add(topSearchForm);
        m_mainContainer.add(new Label(" &nbsp; &nbsp; ", false));

        //Bottom SegmentedPanel
        SegmentedPanel bottom = new SegmentedPanel();
        bottom.setClassAttr("main");

        //Header for the Bottom panel which displays the results.
        ModalContainer m_segmentHeader = new ModalContainer();
        m_emptyLabel = new Label("Search Results");
        m_segmentHeader.add(m_emptyLabel);
        m_segmentHeader.setDefaultComponent(m_emptyLabel);

        //Search Results Component.
        final CreateDocLinkSearchTable m_search = 
                  new CreateDocLinkSearchTable(this, parent, tree, topSearchForm);

        m_entryForm.addInitListener(new FormInitListener() {

            public void init(FormSectionEvent e) throws FormProcessException {
                m_search.clearSelection(e.getPageState());
                e.getPageState().setValue(m_searchDocsParam, null);
            }
        });

        m_paginator = new Paginator(
                (PaginationModelBuilder) m_search.getModelBuilder(),
                CreateDocLinkSearchTable.PAGE_SIZE);
        bottom.addSegment(m_segmentHeader, m_paginator);
        m_paginator.setHiddenIfSinglePage(false);
        m_mainContainer.add(bottom);
        m_errorMessages = new Label("");
        // DEPRECATED! The theme decides how to display a error message.
        // m_errorMessages.setColor(Color.red);
        m_mainContainer.add(m_errorMessages);

        //Add Search Results table
        m_mainContainer.add(m_search);
        add(m_mainContainer);
    }

    /*
     * Register state parameters
     */
    public void register(Page p) {
        super.register(p);
        p.addGlobalStateParam(m_doclinkIDParam);
        p.addGlobalStateParam(m_searchDocsParam);
    }

    Label getErrorLable() {
        return m_errorMessages;
    }

    public BigDecimalParameter getEditDoclinkIDParam() {
        return m_doclinkIDParam;
    }

    private class URLEntryForm extends Form
                            implements FormInitListener, 
                                       FormValidationListener, 
                                       FormProcessListener {

//	  public static final String PARAM_DOC_LINK_ID = "doclinkID";
        private Submit m_submit;
        TrimmedStringParameter m_title;
        TrimmedStringParameter m_description;
        URLParameter m_URL;
        ArrayParameter m_cats;
        Component m_parent;
//      BigDecimalParameter m_doclinkID = new BigDecimalParameter(PARAM_DOC_LINK_ID);

        public URLEntryForm(Container panel, Component parent) {
            super("urlEntry", panel);
            setMethod(Form.POST);
            m_parent = parent;
            m_title = new TrimmedStringParameter(TITLE);
            m_description = new TrimmedStringParameter(DESCRIPTION);
            m_URL = new URLParameter(EXTERNAL_URL);

            add(new Label("Title:"));
            TextField title = new TextField(m_title);
//          title.addValidationListener(new NotEmptyValidationListener());
            add(title);
            add(new Label("Description (optional):"));
//          TextField desc = new TextField(m_description);
//          desc.setSize(70);
            TextArea desc = new TextArea(m_description);
            desc.setRows(10);
            desc.setCols(40);
            add(desc);
            add(new Label("URL (must begin with \"http://\" or \"https://\"):"));
            TextField url = new TextField(m_URL);
            url.setSize(70);
//          url.addValidationListener(new NotEmptyValidationListener());
//          url.addValidationListener(new URLValidationListener());
            add(url);
            add(new Label(FILE_CATEGORIES));
            m_cats = new ArrayParameter(CATEGORIES);
            MultipleSelect catSelect = new MultipleSelect(CATEGORIES);
            catSelect.setSize(20);
            try {
                catSelect.addPrintListener(new CategoriesPrintListener(
                                                   getContentSection(), 
                                                   getEditDoclinkIDParam()));
            } catch (java.util.TooManyListenersException tmex) {
                throw new UncheckedWrapperException(tmex.getMessage());
            }
            add(catSelect);
            m_submit = new Submit("submitButton", "Save Link");
            add(m_submit, GridPanel.RIGHT);
            add(new Submit("cancelButton", "Cancel"));
//          add(new Hidden(m_doclinkID));
            addInitListener(this);
            addValidationListener(this);
            addProcessListener(this);
        }

        private ContentSection getContentSection() {
            ContentSectionCollection csc = ContentSection.getAllSections();
            csc.addEqualsFilter("label", DocMgr.getConfig().getContentSection());
            if (!csc.next()) {
                csc.close();
                return null;
            }
            ContentSection cs = csc.getContentSection();
            csc.close();
            return cs;
        }

        public void init(FormSectionEvent e) {
            PageState state = e.getPageState();
            BigDecimal id = (BigDecimal) state.getValue(m_doclinkIDParam);
            if (id != null) {
                DocLink doclink = new DocLink(id);
                FormData fd = e.getFormData();
//              fd.put(PARAM_DOC_LINK_ID, id);
                if (doclink.isExternal()) {
                    fd.put(TITLE, doclink.getTitle());
                    fd.put(DESCRIPTION, doclink.getDescription());
                    fd.put(EXTERNAL_URL, doclink.getExternalURL());

                    ArrayList assignedCats = new ArrayList();

                    CategoryCollection cats = doclink.getCategoryCollection();
                    Category cat;
                    if (cats.next()) {
                        cat = cats.getCategory();
                        String catID = cat.getID().toString();
                        assignedCats.add(catID);
                    }
                    fd.put(CATEGORIES, assignedCats.toArray());
                }
            }
        }

        public void validate(FormSectionEvent e) throws FormProcessException {
            PageState state = e.getPageState();
            if (m_submit.isSelected(state)) {
                FormData fd = e.getFormData();

                String title = (String) fd.get(TITLE);
                if (title == null || title.trim().length() == 0) {
                    fd.addError(TITLE, "This parameter is required");
                }
                //validate length of title
                if (title != null && title.length() > 1000) {
                    fd.addError(TITLE, "This parameter is too long. It must be fewer than 1000 characters.");
                }

                String url = (String) fd.get(EXTERNAL_URL);
                if (url == null || url.trim().length() == 0) {
                    fd.addError(EXTERNAL_URL, "This parameter is required");
                }
                if (url != null && url.length() > 4000) {
                    fd.addError(EXTERNAL_URL, "This parameter is too long. It must be fewer than 4000 characters.");
                }
                try {
                    new URL(url);
                } catch (MalformedURLException ex) {
                    fd.addError(EXTERNAL_URL, "Please enter a URL");
                }

                //validate length of description
                String desc = (String) fd.get(DESCRIPTION);
                if (desc != null && desc.length() > 4000) {
                    fd.addError(DESCRIPTION, "This parameter is too long. It must be fewer than 4000 characters.");
                }
            }
        }

        /**
         * Just a dummy run to cache the results in RequestLocal object.
         */
        public void process(FormSectionEvent e) {
            PageState state = e.getPageState();
            FormData fd = e.getFormData();
            if (m_submit.isSelected(state)) {

                final DocFolder parent = getSelectedFolder(state);

                ContentBundle bundle;

                //Reached here indicates we can now proceed with Link Creation.
                DocLink link;
                BigDecimal id = (BigDecimal) state.getValue(m_doclinkIDParam);
                if (id != null) {
                    link = new DocLink(id);
                    bundle = link.getContentBundle();
                } else {
                    link = new DocLink();
                    link.setName((String) fd.get(TITLE));

                    //FIXME: when our client decides upon what permissions 
                    // to adopt , change this one
                    //and permissions code accordingly.
                    link.setRepository(DocFolder.getRepository(parent));
                    link.setLanguage("en");

                    bundle = new ContentBundle(link);
                    bundle.setParent(parent);
                    bundle.setContentSection(parent.getContentSection());
                    bundle.save();

                    final ContentBundle bundleFinal = bundle;

                    new KernelExcursion() {

                        protected void excurse() {
                            setParty(Kernel.getSystemParty());
                            PermissionService.setContext(bundleFinal, parent);
                        }
                    }.run();
                }
                link.setName((String) fd.get(TITLE));
                link.setTitle((String) fd.get(TITLE));
                link.setDescription((String) fd.get(DESCRIPTION));
                link.setExternalURL((String) fd.get(EXTERNAL_URL));
                link.setCategories((String[]) fd.get(CATEGORIES));
                //((Table) e.getSource()).clearSelection(state);
            }
            ((BrowsePane) m_parent).displayFolderContentPanel(state);
        }
    }

    /**
     * A Form to Search Documents by name.Has one simple
     * <code>TextField</code> and a submit button.
     *
     * It caches the SearchResults object in the m_coin RequestLocal object.
     *
     */
    private class DocumentSearchForm extends Form
            implements SearchForm,
            FormValidationListener,
            FormProcessListener,
            FormInitListener {

        private Submit m_submit;
        private RequestLocal m_validated;
        private RequestLocal m_coln;

        public DocumentSearchForm(Container panel) {
            super("docSearch", panel);
            setMethod(Form.GET);
            add(new Label("Search text:"));
            TextField searchText = new TextField(SEARCH_DOCUMENT);
            add(searchText);
            m_submit = new Submit("search", "Search");
            add(m_submit);
            addValidationListener(this);
            addProcessListener(this);
            addInitListener(this);

            m_coln = new RequestLocal();
            m_validated = new RequestLocal();
        }

        public void init(FormSectionEvent e) throws FormProcessException {
            e.getFormData().setParameter(SEARCH_DOCUMENT, null);
        }

        public void validate(FormSectionEvent e) throws FormProcessException {
            PageState ps = e.getPageState();

            String docsSearch = (String) ps.getValue(m_searchDocsParam);

            if (StringUtils.emptyString(docsSearch)) {
                m_emptyLabel.setVisible(ps, true);
                m_validated.set(ps, new Boolean(false));
                throw new FormProcessException(
                          "At least one search parameter must be specified");
            }
            m_validated.set(ps, new Boolean(true));
            //Reset the paginator for new search.
            m_paginator.reset(ps);
        }

        /**
         * Just a dummy run to cache the results in RequestLocal object.
         */
        public void process(FormSectionEvent e) {
            PageState state = e.getPageState();
            if (m_submit.isSelected(state)
                    && ((Boolean) m_validated.get(state)).booleanValue()) {
                SearchResults results = getSearchHits(state);
                if (results != null
                        && results.getTotalSize() > 0) {
                    s_log.debug("results");
                } else {
                    s_log.debug("no results");
                }
            }
        }

        /**
         * Cache the results in the upper class's RequestLocal object.
         */
        public SearchResults getSearchHits(PageState state) {
            SearchResults searchResults = (SearchResults) m_coln.get(state);
            String terms = (String) state.getValue(m_searchDocsParam);
            s_log.debug("terms is " + terms);
            if (searchResults == null && !StringUtils.emptyString(terms)) {
                User user = (User) Kernel.getContext().getParty();
                searchResults = SearchUtils.getSimpleSearch(terms, user);
                m_coln.set(state, searchResults);
            }
            return searchResults;
        }

        @Override
        public boolean isVisible(PageState state) {
            return true;
        }

        public long getSearchResultCount(PageState state) {
            return m_coln.get(state) == null ? 0
                    : ((SearchResults) m_coln.get(state)).getTotalSize();
        }
    }

    /**
     * Helper method to return the currently Selected Parent Folder in the tree
     */
    private DocFolder getSelectedFolder(PageState state) {
        DocFolder p = null;
        String selKey = (String) m_tree.getSelectedKey(state);

        if (selKey == null) {
            p = DMUtils.getRootFolder(state);
        } else {
            BigDecimal folderID = new BigDecimal(selKey);
            try {
                p = new DocFolder(folderID);
            } catch (DataObjectNotFoundException nf) {
                throw new ObjectNotFoundException(
                        (String) FOLDER_PARENTNOTFOUND_ERROR.localize(state
                                                            .getRequest()));
            }
        }
        return p;
    }
}
