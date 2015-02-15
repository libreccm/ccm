/*
 * Copyright (C) 2001, 2002 Red Hat Inc. All Rights Reserved.
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
 */
package com.arsdigita.cms.docmgr.ui;

import com.arsdigita.bebop.*;
import com.arsdigita.bebop.event.*;
import com.arsdigita.bebop.form.*;
import com.arsdigita.bebop.parameters.*;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.ContentSectionCollection;
import com.arsdigita.cms.docmgr.DocMgr;
import com.arsdigita.cms.docmgr.Document;
import com.arsdigita.cms.docmgr.search.LuceneSearcher;
import com.arsdigita.cms.docmgr.search.SearchResults;
import com.arsdigita.cms.docmgr.search.SearchUtils;
import com.arsdigita.cms.docmgr.util.GlobalizationUtil;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.User;
import com.arsdigita.kernel.permissions.PermissionDescriptor;
import com.arsdigita.kernel.permissions.PermissionService;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.util.StringUtils;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.web.Web;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.TooManyListenersException;

/**
 * A Search Pane to search documents.
 *
 * @author Crag Wolfe
 */
class SearchPane extends SimpleContainer implements DMConstants {

    private static final org.apache.log4j.Logger s_log
                                                 = org.apache.log4j.Logger.getLogger(
                    SearchPane.class);

    public static final String SEARCH_AUTHOR = "searchAuthor";
    public static final String SEARCH_TERMS = "searchTerms";
    public static final String SEARCH_MIME_TYPE = "searchMimeType";
    public static final String CATEGORY_SELECT = "searchCategorySelect";
    public static final String WORKSPACE_SELECT = "workspaceSelect";

    private final BigDecimalParameter m_categoriesParam = new BigDecimalParameter(CATEGORY_SELECT);
    private final BigDecimalParameter m_workspaceParam = new BigDecimalParameter(WORKSPACE_SELECT);
    private final TrimmedStringParameter m_termsParam = new TrimmedStringParameter(SEARCH_TERMS);
    private final TrimmedStringParameter m_authorParam = new TrimmedStringParameter(SEARCH_AUTHOR);
    private final StringParameter m_mimeTypeParam = new StringParameter(SEARCH_MIME_TYPE);
    private final DateParameter m_startDateParam = new DateParameter(START_DATE_PARAM_NAME);
    private final DateParameter m_endDateParam = new DateParameter(END_DATE_PARAM_NAME);

    private ContentSection m_docsContentSection;

    private RequestLocal m_coln;
    private RequestLocal m_validated;

    private BoxPanel m_mainBrowseContainer;
    //private TabbedPane m_mainTabPane;
    private Table m_searchResultsTable;
    private SearchList m_searchList;
    private SearchListModelBuilder m_model;
    private Label m_noResultsLabel;
    private Label m_hasResultsLabel;
    private Label m_emptyLabel;
    private Submit m_submit;
    private ModalContainer m_segmentHeader;

    public SearchPane() {

        // set component's content section
        ContentSectionCollection csl = ContentSection.getAllSections();
        csl.addEqualsFilter("label", DocMgr.getConfig().getContentSection());
        if (!csl.next()) {
            csl.close();
            return;
        }
        m_docsContentSection = csl.getContentSection();
        csl.close();

        // bebop components
        //m_mainTabPane = new SimpleContainer();
        m_mainBrowseContainer = new BoxPanel(BoxPanel.HORIZONTAL, true);
        //m_mainTabPane.addTab(WS_SEARCH_TITLE,
        //                     m_mainBrowseContainer);

        //m_mainTabPane.add(new Label("yo"));
        //m_mainBrowseContainer.setClassAttr("sidebarNavPanel");
        DocsSearchForm leftSide = new DocsSearchForm(new GridPanel(2));
        m_mainBrowseContainer.add(leftSide);
        m_mainBrowseContainer.add(new Label(" &nbsp; &nbsp; ", false));

        m_model = new SearchListModelBuilder(leftSide);

        SegmentedPanel rightSide = new SegmentedPanel();
        rightSide.setClassAttr("main");
        m_searchList = new SearchList(m_model);
        //rightSide.add(m_searchList);

        m_segmentHeader = new ModalContainer();
        m_hasResultsLabel = new Label("Results");
        m_noResultsLabel = new Label("No results matched your search");
        m_emptyLabel = new Label("");
        m_segmentHeader.add(m_hasResultsLabel);
        m_segmentHeader.add(m_noResultsLabel);
        m_segmentHeader.add(m_emptyLabel);
        m_segmentHeader.setDefaultComponent(m_emptyLabel);

        rightSide.addSegment(
                //            (new Label(new EmptySearchPrintListener(leftSide)),
                m_segmentHeader,
                m_searchList);

        m_mainBrowseContainer.add(rightSide);

        //m_searchResultsTable = new Table(new SearchTableModelBuilder(),
        //    {"searchResults"});
        m_coln = new RequestLocal();
        m_validated = new RequestLocal();

        add(m_mainBrowseContainer);

    }

    /* Register state parameters */
    public void register(Page p) {
        super.register(p);
        p.addGlobalStateParam(m_categoriesParam);
        p.addGlobalStateParam(m_workspaceParam);
        p.addGlobalStateParam(m_termsParam);
        p.addGlobalStateParam(m_authorParam);
        p.addGlobalStateParam(m_mimeTypeParam);
        p.addGlobalStateParam(m_endDateParam);
        p.addGlobalStateParam(m_startDateParam);
    }

    private class DocsSearchForm extends Form
            implements SearchForm, FormValidationListener,
                       FormProcessListener, FormInitListener {

        public DocsSearchForm(Container panel) {
            super("docSearch", panel);

            add(new Label("Search text:"));
            TextField searchText = new TextField(SEARCH_TERMS);
            //searchText.addValidationListener(new NotEmptyValidationListener());
            add(searchText);
            add(new Label("Author:"));
            add(new TextField(SEARCH_AUTHOR));
            add(new Label("Document Type:"));
            add(new MimeTypesWidget(m_mimeTypeParam));

            add(new Label("Modified After: "));
            add(new com.arsdigita.bebop.form.Date(m_startDateParam));
            add(new Label("Modified Before: "));
            add(new com.arsdigita.bebop.form.Date(m_endDateParam));

            add(new Label("Category"));
            SingleSelect categoriesWidget
                         = new SingleSelect(m_categoriesParam);
            try {
                categoriesWidget.addOption(new Option("", ""));
                categoriesWidget.addPrintListener(new CategoriesPrintListener(m_docsContentSection));
            } catch (TooManyListenersException e) {
                UncheckedWrapperException.throwLoggedException(getClass(), "Too many listeners", e);
            }
            add(categoriesWidget);

            //categoriesWidget.setSize(SELECT_HEIGHT);
            add(new Label("Workspace"));
            SingleSelect workspaceWidget
                         = new SingleSelect(m_workspaceParam);
            try {
                workspaceWidget.addPrintListener(new WorkspacesPrintListener());
            } catch (TooManyListenersException e) {
                UncheckedWrapperException.throwLoggedException(getClass(), "Too many listeners", e);
            }
            add(workspaceWidget);

            m_submit = new Submit("search", "Search");
            add(m_submit);
            addInitListener(this);
            addValidationListener(this);
            addProcessListener(this);
        }

        public void validate(FormSectionEvent e)
                throws FormProcessException {
            PageState state = e.getPageState();

            String terms = (String) state.getValue(m_termsParam);
            String author = (String) state.getValue(m_authorParam);
            String mimeType = (String) state.getValue(m_mimeTypeParam);
            Date endDate = (Date) state.getValue(m_endDateParam);
            Date startDate = (Date) state.getValue(m_startDateParam);
            BigDecimal workspaceID = (BigDecimal) state.getValue(m_workspaceParam);
            BigDecimal categoryID
                       = (BigDecimal) state.getValue(m_categoriesParam);

            if (StringUtils.emptyString(terms) && StringUtils.emptyString(author) && StringUtils.
                    emptyString(mimeType) && endDate == null && startDate == null && categoryID
                                                                                     == null
                && workspaceID == null) {
                m_emptyLabel.setVisible(state, true);
                m_validated.set(state, new Boolean(false));
                throw new FormProcessException(GlobalizationUtil.globalize(
                        "ui.unspecified_search_parameters"));
            }
            m_validated.set(state, new Boolean(true));
        }

        public void init(FormSectionEvent e) {
            m_segmentHeader.setVisibleComponent(e.getPageState(), m_emptyLabel);
        }

        public void process(FormSectionEvent e) {
            PageState state = e.getPageState();

            SearchResults results = getSearchHits(state);
            if (results != null && results.getTotalSize() > 0) {
                m_segmentHeader.setVisibleComponent(state, m_hasResultsLabel);
                s_log.debug("results");
            } else {
                m_segmentHeader.setVisibleComponent(state, m_noResultsLabel);
                s_log.debug("no results");
            }
        }

        public SearchResults getSearchHits(PageState state) {

            SearchResults coln = (SearchResults) m_coln.get(state);
            if (coln == null && m_submit.isSelected(state) && ((Boolean) m_validated.get(state)).
                    booleanValue()) {
                String terms = (String) state.getValue(m_termsParam);
                String author = (String) state.getValue(m_authorParam);
                String mimeType = (String) state.getValue(m_mimeTypeParam);
                Date endDate = (Date) state.getValue(m_endDateParam);
                Date startDate = (Date) state.getValue(m_startDateParam);
                BigDecimal workspaceID = (BigDecimal) state.getValue(m_workspaceParam);

                String[] sections = null;
                if (!LuceneSearcher.class.equals(SearchUtils.getSearcher().getClass())) {
                    sections = (String[]) new String[]{m_docsContentSection.getID().toString()};
                }

                // don't need this since all types in this section are documents
                //String[] types = (String[])state.getValue(m_typesParam);
                String[] types = null;

                BigDecimal rootCategoryID
                           = m_docsContentSection.getRootCategory().getID();
                ArrayList categoryIDs = new ArrayList();
                if (!LuceneSearcher.class.equals(SearchUtils.getSearcher().getClass())) {
                    BigDecimal categoryID
                               = (BigDecimal) state.getValue(m_categoriesParam);
                    if (categoryID != null && !categoryID.equals(rootCategoryID)) {
                        categoryIDs.add(categoryID.toString());
                    }

                    // this would be used instead for multiple select
                    //String[] cats = (String[])state.getValue(m_categoriesParam);
                    //
                    //if (cats != null) {
                    //    for (int i = 0 ; i < cats.length ; i++) {
                    //        BigDecimal curCat = new BigDecimal(cats[i]);
                    //        if(!rootCategoryID.equals(curCat)) {
                    //            categoryIDs.add(curCat.toString());
                    //        }
                    //    }
                    //}
                }

                //if (terms != null && !"".equals(terms)) {
                // if form has been submitted, m_emptyLabel will not be visible
                User user = (User) Kernel.getContext().getParty();
                coln = SearchUtils.getAdvancedSearch(terms,
                                                     author,
                                                     mimeType,
                                                     workspaceID,
                                                     startDate,
                                                     endDate,
                                                     types,
                                                     sections,
                                                     user,
                                                     categoryIDs);
                //}
                m_coln.set(state, coln);
            }
            return coln;
        }

        public boolean isVisible(PageState state) {
            return true;
        }

        public long getSearchResultCount(PageState state) {
            return m_coln.get(state) == null ? 0
                   : ((SearchResults) m_coln.get(state)).getTotalSize();
        }

    }

    private class MimeTypesWidget extends SingleSelect {

        public MimeTypesWidget(ParameterModel pm) {
            super(pm);
            addOption(new Option("", ""));
            addOption(new Option("excel", "Excel document"));
            addOption(new Option("html", "HTML"));
            addOption(new Option("acrobat", "PDF"));
            addOption(new Option("plain text", "Plain text"));
            addOption(new Option("powerpoint", "Powerpoint file"));
            addOption(new Option("rich text", "Rich Text Format (rtf)"));
            addOption(new Option("word", "Word document"));
        }
    }

    private class WorkspacesPrintListener implements PrintListener {

        public WorkspacesPrintListener() {
        }

        public void prepare(PrintEvent e) {
            OptionGroup o = (OptionGroup) e.getTarget();
            o.clearOptions();
            PageState state = e.getPageState();
            User user = Web.getWebContext().getUser();

            o.addOption(new Option("", ""));
            DataQuery dq = SessionManager.getSession().retrieveQuery(
                    "com.arsdigita.cms.docmgr.workspacesWithRepositories");
            while (dq.next()) {
                if (PermissionService.checkPermission(new PermissionDescriptor(
                        PrivilegeDescriptor.READ,
                        new OID(Document.BASE_DATA_OBJECT_TYPE,
                                dq.get("workspaceID")),
                        user.getOID()))) {
                    o.addOption(new Option(((BigDecimal) dq.get("workspaceID")).toString(),
                                           (String) dq.get("title")));
                }

                dq.close();
            }
        }
    }

}
