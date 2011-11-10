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


import java.math.BigDecimal;
import java.util.ArrayList;

import org.apache.log4j.Category;

import com.arsdigita.bebop.ActionLink;
import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.Container;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.GridPanel;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Link;
import com.arsdigita.bebop.List;
import com.arsdigita.bebop.ModalContainer;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Paginator;
import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.bebop.SegmentedPanel;
import com.arsdigita.bebop.SimpleComponent;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.TabbedPane;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.event.ChangeEvent;
import com.arsdigita.bebop.event.ChangeListener;
import com.arsdigita.bebop.event.RequestEvent;
import com.arsdigita.bebop.event.RequestListener;
import com.arsdigita.bebop.form.Date;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.list.ListCellRenderer;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.bebop.parameters.DateParameter;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.ContentSectionCollection;
import com.arsdigita.cms.docmgr.DocMgr;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.kernel.ui.ACSObjectSelectionModel;
import com.arsdigita.navigation.ui.admin.CategorySelectionModel;
import com.arsdigita.navigation.ui.admin.CategoryTree;
import com.arsdigita.xml.Element;

/**
 * User Interface component of the Document Manager application which
 * which serves as entry-point and navigation tool of the repositories
 * that a user has subscribed too.
 * The tree of all subscribed repositories is on the left side, the full
 * listing of the currently selected directory is on the right side.
 *
 * @author <mailto href="StefanDeusch@computer.org">Stefan Deusch</a>
 *
 */
class BrowseCatDocsPane extends ModalContainer
    implements DMConstants, ChangeListener, RequestListener {

    protected static Category s_log = Category.getInstance
        (CategoryDocsNavigatorPortlet.class.getName());

    private Component m_folderContent;
    private Component m_destinationFolderPanel;
    private Component m_newFileForm;
    private Component m_newFolderForm;
    private Component m_errorMsgPanel;
    private DestinationFolderForm m_destinationFolderForm;
    private ErrorMessageLabel m_errorMsgLabel;
    private Container m_mainBrowseContainer;

    private ContentSection m_docsContentSection;

    private TabbedPane m_mainTabPane;

    private CategoryTree m_tree;

    private BigDecimalParameter m_docCategoryTreeParam = new BigDecimalParameter(CAT_TREE_ID_PARAM_NAME);
    private BigDecimalParameter m_docInitCategoryTreeParam = new BigDecimalParameter(CAT_TREE_INIT_ID_PARAM_NAME);
    //private BigDecimalParameter m_docColumnIdParam = new BigDecimalParameter(DOC_COL_ID_PARAM_NAME);

    private BigDecimalParameter m_fileIdParam = new BigDecimalParameter(FILE_ID_PARAM_NAME);
    private StringParameter m_rootAddDocParam= new StringParameter(ROOT_ADD_DOC_PARAM_NAME);

    private final DateParameter m_startDateParam = new DateParameter(START_DATE_PARAM_NAME);
    private final DateParameter m_endDateParam = new DateParameter(END_DATE_PARAM_NAME);
                                                 
    private ACSObjectSelectionModel m_docCatSelectModel;

    /**
     * Default constructor
     */

    public BrowseCatDocsPane() {

        m_mainTabPane = new TabbedPane();

        m_mainBrowseContainer = new BoxPanel();
        m_mainTabPane.addTab(WS_BROWSE_TITLE,
                             m_mainBrowseContainer);
        m_mainTabPane.addTab("Search",
                             new SearchPane());

        m_mainBrowseContainer.setClassAttr("sidebarNavPanel");
        //m_mainBrowseContainer.setAttribute("navbar-title", "Folders");

        ContentSectionCollection csl = ContentSection.getAllSections();
        csl.addEqualsFilter("label",DocMgr.getConfig().getContentSection());
        if (!csl.next()) {
                csl.close(); return;
        }
        m_docsContentSection = csl.getContentSection();
        csl.close();
        s_log.debug("m_docs section is "+m_docsContentSection.getName());

        m_docCatSelectModel = 
            new ACSObjectSelectionModel(m_docCategoryTreeParam);

	// FIXME - don't think this will work but it does compile
	CategorySelectionModel m_categorySelectionModel = new CategorySelectionModel();
	m_tree = new CategoryTree(m_categorySelectionModel);

        // m_tree = new CategoryTree(m_docCatSelectModel,
//              new RequestLocal() {
//                  protected Object initialValue(PageState s) {
//                      return m_docsContentSection.getRootCategory();
//                  }}
//              );

        s_log.debug("added category tree");
        if(m_tree == null) {
            s_log.debug(" but the tree was null");
        }

        BoxPanel leftSide = new BoxPanel();
        leftSide.setClassAttr("navbar");
        m_tree.addChangeListener(this);
        leftSide.add(m_tree);

        m_mainBrowseContainer.add(leftSide);

        /*
         * Create all panels on the right side.
         */

        SegmentedPanel rightSide = new SegmentedPanel();
        rightSide.setClassAttr("main");

        m_folderContent = makeContentPanel(rightSide);

        m_mainBrowseContainer.add(rightSide);

        add(m_mainTabPane);

        //m_errorMsgPanel = makeErrorMsgPanel();
        //add(m_errorMsgPanel);
    }

    /**
     * Register the page the fist time
     */

    public void register(Page p) {
        //p.addGlobalStateParam(m_docColumnIdParam);
        p.addGlobalStateParam(m_rootAddDocParam);
        p.addGlobalStateParam(m_fileIdParam);
        p.addGlobalStateParam(m_docCategoryTreeParam);
        p.addGlobalStateParam(m_docInitCategoryTreeParam);
        p.addGlobalStateParam(m_endDateParam);
        p.addGlobalStateParam(m_startDateParam);
 
        p.addRequestListener(this);

        super.register(p);
    }

    /**
     * Checks if a folder is selected in the page state and consequently
     * hides or shows the Folder Contents or Folder Action panels.
     */
    public void pageRequested(RequestEvent e) {
        PageState state = e.getPageState();

        //BigDecimal fid = (BigDecimal) state.getValue(m_fileIdParam);

        if (m_tree == null) {
            s_log.debug("pR: tree is null");
            return;
        }

        BigDecimal key = (BigDecimal) m_tree.getSelectedKey(state);

        // start out with root folder selected and open
        if (key == null) {
            s_log.debug("key is null");
            BigDecimal selected = 
                (BigDecimal) m_docInitCategoryTreeParam.transformValue
                (state.getRequest());
            BigDecimal rootCategoryID = 
                m_docsContentSection.getRootCategory().getID();
            if (selected == null) {
                s_log.debug("selected is null");
                key =  rootCategoryID;
                m_tree.expand(rootCategoryID.toString(), state);
            } else {
                key = selected;
                m_tree.expand(rootCategoryID.toString(), state);
            }
            m_tree.setSelectedKey(state, key);
        }

    }

    /**
     * Helper method to communicate selected folder ID to subcomponents.
     * Return onle non-null after tree has been displayed at least once.
     */

    public BigDecimal getFolderID(PageState state) {
        return (BigDecimal) m_tree.getSelectedKey(state);
    }

    /**
     * Implementation of the change listener, clicking on the folder
     * loads the directory on the right side.
     */

    public void stateChanged(ChangeEvent e) {
        PageState state = e.getPageState();

        s_log.debug("stateChanged yo");

        // Display folder on the right side corresponding to the key
        displayFolderContentPanel(state);
    }

    /**
     * Build a panel to display the Folder content of the selected Folder
     * and add it as a segment to the passed in Segmented Panel.
     */

    private Component makeContentPanel(SegmentedPanel main) {

        Label info_header = new Label
            (new GlobalizedMessage("ui.cat.browse.content.header", 
                                   BUNDLE_NAME));
        //folder_info_header.addPrintListener(
        //                                    new FolderNamePrintListener(m_tree));

        GridPanel folderGrid = new GridPanel(1);
        SimpleContainer pane = new SimpleContainer();
        //pane.add(newFileLink);
        pane.add(new Label("        "));
        folderGrid.add(pane, GridPanel.RIGHT | GridPanel.BOTTOM);
        
        CategoryItemsBrowser documentsTable = 
            new CategoryItemsBrowser
            (new CategoryDocModelBuilder(m_docCatSelectModel,
                                         ContentItem.DRAFT),
             m_docCatSelectModel,
             ContentItem.DRAFT, 
             m_docsContentSection);
        Paginator paginator = new Paginator(documentsTable,10);
        documentsTable.setPaginator(paginator);
        
        folderGrid.add(paginator);
        folderGrid.add(documentsTable,
                       GridPanel.LEFT | GridPanel.BOTTOM);

        folderGrid.add(new Label("        "), GridPanel.RIGHT | GridPanel.BOTTOM);
        folderGrid.add(new DateSelectorWidget(new BoxPanel(BoxPanel.HORIZONTAL)),
                       GridPanel.CENTER | GridPanel.BOTTOM);

        return main.addSegment(info_header,
                               folderGrid);
    }

    /*
     * Build panel to display error message when copy/move failed.
     */

    private Component makeErrorMsgPanel() {
        ColumnPanel c = new ColumnPanel(1);
        m_errorMsgLabel = new ErrorMessageLabel();
        c.add(m_errorMsgLabel);

        ActionLink link = new ActionLink(ACTION_ERROR_CONTINUE);
        link.setClassAttr("actionLink");
        link.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    displayFolderContentPanel(e.getPageState());
                }
            });

        c.add(link);
        return c;
    }

    public void displayFolderContentPanel(PageState state) {
        setVisibleComponent(state, m_mainTabPane);
    }

    public void displayErrorMsgPanel(PageState state,
                                     String action,
                                     ArrayList list) {

        m_errorMsgLabel.setMessages(state, action, list);

        setVisibleComponent(state, m_errorMsgPanel);
    }


    /**
     * Error message panel that allows to set customized error
     * messages without showing a tomcat stacktrace
     */

    private static class ErrorMessageLabel extends SimpleComponent
        implements DMConstants {

        private RequestLocal m_msgs;
        private RequestLocal m_action;

        public ErrorMessageLabel() {
            m_msgs = new RequestLocal();
            m_action = new RequestLocal();

        }

        /**
         * Set list of file/folder that could not be delete/move/copy.
         *
         * @param action file operation (action, move, copy)
         */

        public void setMessages(PageState state,
                                String action,
                                ArrayList msgs) {
            m_action.set(state, action);
            m_msgs.set(state, msgs);
        }

        public void generateXML(PageState state, Element parent) {
            Element element = parent.newChildElement("docs:error-label",
                                                     DOCS_XML_NS);
            element.addAttribute("action", (String) m_action.get(state));

            ArrayList list = (ArrayList) m_msgs.get(state);

            if (list != null) {
                for (int i = 0; i < list.size(); i++) {
                    Element item = element.newChildElement("docs:item",
                                                           DOCS_XML_NS);
                    item.addAttribute("name", ((String) list.get(i)));
                }
            }
        }


    }

    /**
     * Table Cell Renderer that provides clickable Links to follow
     * directory links .
     */
    private static class DirLinkRenderer implements ListCellRenderer {

        public Component getComponent(List list, PageState state,
                                      Object value,  String key,
                                      int index, boolean isSelected) {

            Link link = new Link((String)value,
                                 "?" + SEL_FOLDER_ID_PARAM.getName() +
                                 "=" + key);
            return link;
        }
    }

    private class DateSelectorWidget extends Form {
        public DateSelectorWidget(Container panel) {
            super("dateSelector",panel);
            
            add(new Label("Modified After: "));
            add(new Date(m_startDateParam));
            add(new Label("Modified Before: "));
            add(new Date(m_endDateParam));
            add(new Submit("filter", "Filter"));
            
            //addInitListener(this);
        }
        
        //public void init(FormSectionEvent e)
        //    throws FormProcessException {
        //    PageState state = e.getPageState();
        //    
        //    FormData data = e.getFormData();
        //    data.put(START_DATE_PARAM_NAME, doc.getName());
        //}
    }
}
