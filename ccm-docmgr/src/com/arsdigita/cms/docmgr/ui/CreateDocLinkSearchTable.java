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
 * Created on Dec 19, 2003
 *
 */

package com.arsdigita.cms.docmgr.ui;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.ControlLink;
import com.arsdigita.bebop.ExternalLink;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.PaginationModelBuilder;
import com.arsdigita.bebop.Paginator;
import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.Tree;
import com.arsdigita.bebop.event.TableActionAdapter;
import com.arsdigita.bebop.event.TableActionEvent;
import com.arsdigita.bebop.table.AbstractTableModelBuilder;
import com.arsdigita.bebop.table.DefaultTableCellRenderer;
import com.arsdigita.bebop.table.TableCellRenderer;
import com.arsdigita.bebop.table.TableColumn;
import com.arsdigita.bebop.table.TableModel;
import com.arsdigita.cms.ContentBundle;
import com.arsdigita.cms.docmgr.DocFolder;
import com.arsdigita.cms.docmgr.DocLink;
import com.arsdigita.cms.docmgr.Document;
import com.arsdigita.cms.docmgr.search.SearchResult;
import com.arsdigita.cms.docmgr.search.SearchResults;
import com.arsdigita.dispatcher.ObjectNotFoundException;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.KernelExcursion;
import com.arsdigita.kernel.Party;
import com.arsdigita.kernel.permissions.PermissionService;
import com.arsdigita.persistence.OID;
import com.arsdigita.versioning.Versions;

import java.math.BigDecimal;
import java.util.Iterator;

import org.apache.log4j.Logger;

/**
 *
 * A UI class used to create the objects of type <code>DocLink</code>.
 * This class displays a table which contains the results of Documents searched
 * by the user.
 * 
 * Action column displays ActionLinks to create a Link to the Document
 * displayed in the current row of the table.
 *
 * @author <a href="mailto:sshinde@redhat.com">Shashin Shinde</a>
 * 
 * $Id: //apps/docmgr-cms/dev/src/com/arsdigita/cms/docmgr/ui/CreateDocLinkSearchTable.java#2 $
 *
 */

public class CreateDocLinkSearchTable extends Table implements DMConstants{

    private static final Logger s_log = Logger.getLogger(CreateDocLinkSearchTable.class);

    public static final int PAGE_SIZE = 10;

    /** Table Headers. */
    private static String[] s_headers = {"Name", "Workspace/Application", "Action"};

    private TableColumn m_createColumn;
    
    private Component m_parent;
    private Tree m_tree;
    private CreateDocLinkPane m_linkPane;

    /**
     * Constructor.
     * @param linkPane to which this Table is added.Used to display some error messages.
     * @param parent. Used to return to after creating the link and cleaning up the Table selection.
     * @param tree.Tree object to get the currently selected parent Folder under
     *        which to create the link.
     * @param form.Search Form to get the terms to search for. 
     */
    CreateDocLinkSearchTable(CreateDocLinkPane linkPane, Component parent,Tree tree,SearchForm form){
        super(new CreateDocLinkTableModelBuilder(form), s_headers);
        m_parent = parent;
        m_tree = tree;
        m_linkPane = linkPane;

        setWidth("100%");
        setRenderers();

        addTableActionListener(new LinkCreator());
    }

    /**Just private helper method to group adding of all renderers in one place.*/
    private void setRenderers(){
        getColumn(0).setCellRenderer(new NameCellRenderer());
        getColumn(1).setCellRenderer(new WorkspaceCellRenderer());

        m_createColumn = getColumn(2);
        m_createColumn.setCellRenderer(new ActionCellRenderer());
        m_createColumn.setAlign("center");
    }

    /**
     * Extension of TableActionAdapter class only to work on clicks generated 
     * by ActionCellRenderer. 
     */
    private class LinkCreator extends TableActionAdapter {
        public void cellSelected(TableActionEvent e) {
            int col = e.getColumn().intValue();

            if ( m_createColumn != getColumn(col) ) {
                return;
            }

            PageState s = e.getPageState();
            BigDecimal id = new BigDecimal(e.getRowKey().toString());
            boolean valid = true;

            OID oid = new OID(Document.TYPE, id.abs());
            final Document doc = (Document) DomainObjectFactory.newInstance(oid);
            
            final DocFolder parent = getSelectedFolder(s);
            s_log.debug("Document Item: " + doc + " Parent Folder: " + parent);

            //Performe some validation and update the error messages label.
            if(doc.getParentResource().getID() == parent.getID()){
                m_linkPane.getErrorLable().setLabel("Document in Same Folder",s);
                valid = false;
            }

            try{
                parent.retrieveSubResource(doc.getName());
                m_linkPane.getErrorLable().setLabel(RESOURCE_EXISTS_ERROR,s);
                valid = false;
            }catch (DataObjectNotFoundException donf){
                //Do nothing.
            }

            if(! valid){
                ((Table) e.getSource()).clearSelection(s);
                return;
            }

            //Reached here indicates we can now proceed with Link Creation.
            DocLink newLink;
            BigDecimal doclinkID = (BigDecimal) s.getValue(m_linkPane.getEditDoclinkIDParam());
            if (doclinkID != null) {
            	newLink = new DocLink(doclinkID);
            	newLink.setExternalURL(null);
            }
            else {
            	newLink = new DocLink();
            }
            newLink.setName(doc.getName());
            newLink.setTitle(doc.getTitle());
            newLink.setDescription(doc.getDescription());
            
            //FIXME: when our client decides upon what permissions to adopt , change this one
            //and permissions code accordingly.
            newLink.setRepository(DocFolder.getRepository(parent));
            newLink.setLanguage("en");
            newLink.setTarget(doc);

            Versions.tag(
                newLink.getOID(),
                (FILE_UPLOAD_INITIAL_TRANSACTION_DESCRIPTION
                    .localize(s.getRequest())
                    .toString()));

            final ContentBundle bundle = new ContentBundle(newLink);
            bundle.setParent(parent);
            bundle.setContentSection(parent.getContentSection());
            bundle.save();

            new KernelExcursion() {
                protected void excurse() {
                    Party currentParty = Kernel.getContext().getParty();
                    setParty(Kernel.getSystemParty());
                    PermissionService.setContext(bundle, parent);
                }
            }.run();
            ((Table) e.getSource()).clearSelection(s);
            ((BrowsePane) m_parent).displayFolderContentPanel(s);
        }
    }

    /**
     * Produce Links to the create a Link to the given Document in the current row.
     */
    private static class ActionCellRenderer implements TableCellRenderer {

        private final static Logger logger = Logger.getLogger(ActionCellRenderer.class);
        private static ControlLink s_link;
        
        static {
            logger.debug("Static initalizer starting...");
            s_link = new ControlLink(new Label(DMConstants.FOLDER_NEW_CREATE_LINK));
            s_link.setConfirmation("Create Link to this Document ?");
            logger.debug("Static initalizer finished.");
        }

        public Component getComponent(Table table, PageState state, Object value,
                                      boolean isSelected, Object key,
                                      int row, int column) {
          return s_link;                
        }
    }

    /**
     * Compute the Workspace for the Document in the current row.We have to
     * instantiate it in order to get it's repository and the workspace which
     * is parent application of the repository.
     */
    private static class WorkspaceCellRenderer implements TableCellRenderer {
        public Component getComponent(Table table, PageState state, Object value,
                                      boolean isSelected, Object key,
                                      int row, int column) {
            SearchResult result = (SearchResult) value;
            BigDecimal id = result.getID();
            OID oid = new OID(Document.TYPE, id.abs());
            final Document doc = (Document) DomainObjectFactory.newInstance(oid);
            String workspace = doc.getRepository().getParentApplication().getDisplayName() + "/" + doc.getRepository().getDisplayName();
            return new Label(workspace);
        }
    }
    
    /**
     * Produce External Links to view a Document displayed in the current row.
     * Link is around it's name.
     */
    private class NameCellRenderer extends DefaultTableCellRenderer {
        public NameCellRenderer() {
            super(true);
        }

        public Component getComponent(Table table, PageState state, Object value,
                                      boolean isSelected, Object key,
                                      int row, int column) {
            SearchResult result = (SearchResult) value;
            String url = result.getUrlStub();
            ExternalLink m_title = new ExternalLink(result.getLink(), url);
            return m_title;
        }
    }

    /**Helper method to return the currently Selected Parent Folder in the tree */
    private DocFolder getSelectedFolder(PageState state){
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
                    (String) FOLDER_PARENTNOTFOUND_ERROR.localize(state.getRequest()));
            }
        }
        return p;
    }

    /**
     * Table model build around SearchResults.getResults iterator.
     * The row count is the no. of rows returned as a result of search.
     * Every row indicates one SearchResult object.
     */
    private static class CreateDocLinkTableModel implements TableModel {
        private static final int NAME = 0;
        private static final int WORKSPACE = 1;
        private static final int CREATABLE = 2;

        private PageState m_state;
        private CreateDocLinkSearchTable m_table;
        private Iterator m_results;
        private SearchResult m_result;

        public CreateDocLinkTableModel
            (CreateDocLinkSearchTable table, PageState state, SearchResults results) {
            m_state = state;
            m_table = table;
            if ( results != null ) {
                m_results = results.getResults();
                s_log.debug("In CreateDocLinkTableModel - Iterator ID: " + m_results.toString());
            }
        }

        public int getColumnCount() {
            return 3;
        }

        public boolean nextRow() {
            if ( m_results != null && m_results.hasNext() ) {
                m_result = (SearchResult) m_results.next();
                return true;
            }
            return false;
        }

        /**
         * Just return the corresponding SearchResult object associated with the
         * current row for all the columns.
         */
        public Object getElementAt(int columnIndex) {
            return m_result;
        }

        /**The ID of the Document in the current row is the key.*/
        public Object getKeyAt(int columnIndex) {
            return m_result.getID();
        }
    }

    /**
     * Search Table Model Builder.
     * Also set's up the pagination for the Table.
     */
    private static class CreateDocLinkTableModelBuilder
        extends AbstractTableModelBuilder implements PaginationModelBuilder {

        private RequestLocal m_resultsLocal;
        private SearchForm m_form;

        public CreateDocLinkTableModelBuilder(SearchForm form) {
            super();
            m_resultsLocal = new RequestLocal();
            m_form = form;
        }

        /**
         * Provide implementation for
		 * @see com.arsdigita.bebop.table.TableModelBuilder#makeModel(com.arsdigita.bebop.Table, com.arsdigita.bebop.PageState)
         * Construct the table model from our <code>SearchResults</code> object
         * which is provided by SearchForm.
		 */
		public TableModel makeModel(Table t, PageState ps) {
            SearchResults results = (SearchResults) m_resultsLocal.get(ps);
            if ( results == null ) {
                return Table.EMPTY_MODEL;
            } else {
                t.getRowSelectionModel().clearSelection(ps);
                return new CreateDocLinkTableModel
                    ((CreateDocLinkSearchTable) t, ps, (SearchResults) m_resultsLocal.get(ps));
            }
        }

        /**
         * Provide implementation for 
		 * @see com.arsdigita.bebop.PaginationModelBuilder#getTotalSize(com.arsdigita.bebop.Paginator, com.arsdigita.bebop.PageState)
         * Return the size of <code>SearchResults</code> objects and set it into the
         * RequestLocal cache.
		 */
        public int getTotalSize(Paginator p, PageState ps) {
            SearchResults results = null;
            if(m_resultsLocal.get(ps) == null){
                results = m_form.getSearchHits(ps);
                m_resultsLocal.set(ps,results);
            }
            if (results == null) {
                return 0;
            } else {
                int totalSize = (int) results.getTotalSize();
                s_log.debug("Setting paginator: size = " + results.getTotalSize() +
                    " first = " + p.getFirst(ps) + " last = " + p.getLast(ps));
                results.setRange(new Integer(p.getFirst(ps)),new Integer(p.getLast(ps) + 1));
                s_log.debug("totoalsize is "+ totalSize);
                return totalSize;
            }
        }

        public boolean isVisible(PageState state) {
            return m_form.isVisible(state);
        }
    }

}
