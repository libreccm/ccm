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
package com.arsdigita.cms.ui.folder;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.ControlLink;
import com.arsdigita.bebop.Image;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Link;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.PaginationModelBuilder;
import com.arsdigita.bebop.Paginator;
import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.event.ChangeEvent;
import com.arsdigita.bebop.event.ChangeListener;
import com.arsdigita.bebop.event.TableActionAdapter;
import com.arsdigita.bebop.event.TableActionEvent;
import com.arsdigita.bebop.event.TableActionListener;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.bebop.table.AbstractTableModelBuilder;
import com.arsdigita.bebop.table.DefaultTableCellRenderer;
import com.arsdigita.bebop.table.DefaultTableColumnModel;
import com.arsdigita.bebop.table.TableCellRenderer;
import com.arsdigita.bebop.table.TableColumn;
import com.arsdigita.bebop.table.TableHeader;
import com.arsdigita.bebop.table.TableModel;
import com.arsdigita.cms.CMS;
import com.arsdigita.cms.ContentBundle;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.Folder;
import com.arsdigita.cms.ItemCollection;
import com.arsdigita.cms.SecurityManager;
import com.arsdigita.cms.dispatcher.ItemResolver;
import com.arsdigita.cms.dispatcher.Utilities;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.permissions.PermissionService;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
import com.arsdigita.persistence.OID;
import com.arsdigita.toolbox.ui.FormatStandards;
import com.arsdigita.util.Assert;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import javax.servlet.ServletException;

/**
 * Browse folders and items. If the user clicks on a folder, the folder
 * selection model is updated. If the user clicks on any other item, an
 * separate item selection model is updated.
 *
 * @author <a href="mailto:lutter@arsdigita.com">David Lutterkort</a>
 * @version $Revision: #22 $ $DateTime: 2004/08/17 23:15:09 $
 */
public class FolderBrowser extends Table {
    public static final String versionId =
            "$Id: FolderBrowser.java 1540 2007-03-26 20:44:49Z apevec $" +
            "$Author: apevec $" +
            "$DateTime: 2004/08/17 23:15:09 $";
    
    private static final Logger s_log = Logger.getLogger(FolderBrowser.class);
    
    private static GlobalizedMessage[] s_headers = {
        globalize("cms.ui.folder.name"), globalize("cms.ui.folder.title"),
        globalize("cms.ui.folder.type"), globalize("cms.ui.folder.creation_date"),
        globalize("cms.ui.folder.last_modified"), globalize("cms.ui.folder.action"),
        globalize("cms.ui.folder.index") };
    
    private static GlobalizedMessage[] s_noIndexHeaders = {
        globalize("cms.ui.folder.name"), globalize("cms.ui.folder.title"),
        globalize("cms.ui.folder.type"), globalize("cms.ui.folder.creation_date"),
        globalize("cms.ui.folder.last_modified"), globalize("cms.ui.folder.action") };
    
    private static final String SORT_ACTION_UP = "sortActionUp";
    private static final String SORT_ACTION_DOWN = "sortActionDown";
    
    private FolderSelectionModel m_currentFolder;
    
    private TableActionListener m_folderChanger;
    
    private TableActionListener m_deleter;
    
    private TableActionListener m_indexChanger;
    
    private TableColumn m_nameColumn;
    private TableColumn m_deleteColumn;
    private TableColumn m_indexColumn;
    
    private final static String SORT_KEY_NAME = "name";
    private final static String SORT_KEY_TITLE = "title";
    private final static String SORT_KEY_LAST_MODIFIED_DATE = "lastModified";
    private final static String SORT_KEY_CREATION_DATE = "creationDate";
    
    private StringParameter m_sortType = new StringParameter("sortType");
    private StringParameter m_sortDirection = new StringParameter("sortDirn");
    
    public FolderBrowser(FolderSelectionModel currentFolder) {
        //super(new FolderTableModelBuilder(), s_headers);
        super();
        m_sortType.setDefaultValue(SORT_KEY_NAME);
        m_sortDirection.setDefaultValue(SORT_ACTION_UP);
        
        setModelBuilder(new FolderTableModelBuilder(currentFolder));
        setColumnModel(new DefaultTableColumnModel(hideIndexColumn() ? s_noIndexHeaders : s_headers));
        setHeader(new TableHeader(getColumnModel()));
        // DEE 1/18/02: the folder table model builder needs to know about
        // 'this' in order to set visibility, but 'this' isn't available
        // until after the super class' constructor has run, so we can't
        // just pass it into the constructor.
        // Also, we can't do anything before the super class call, so we
        // can't make a local copy of the foldertablemodelbuilder variable...
        // so we break the model builder construction up into two parts,
        // and pull the foldertablemodelbuilder back with the super class'
        // accessor.
        ((FolderTableModelBuilder)getModelBuilder()).setFolderBrowser(this);
        
        m_currentFolder = currentFolder;
        
        /*
         
        This code should be uncommented if the desired behaviour is for a change
        of folder to cause reversion to default ordering of contained items
        (by name ascending). Our feeling is that the user selected ordering
        should be retained for the duration of the folder browsing session. If
        anyone wants this alternative behaviour it should be brought in under
        the control of a config parameter.
         
        m_currentFolder.addChangeListener(new ChangeListener() {
         
                public void stateChanged(ChangeEvent e) {
                        PageState state = e.getPageState();
                        state.setValue(m_sortType, m_sortType.getDefaultValue());
                        state.setValue(m_sortDirection, m_sortDirection.getDefaultValue());
         
                }});
         */
        setClassAttr("dataTable");
        
        getHeader().setDefaultRenderer(new com.arsdigita.cms.ui.util.DefaultTableCellRenderer());
        
        m_nameColumn = getColumn(0);
        m_nameColumn.setCellRenderer(new NameCellRenderer());
        m_nameColumn.setHeaderRenderer(new HeaderCellRenderer(SORT_KEY_NAME));
        getColumn(1).setHeaderRenderer(new HeaderCellRenderer(SORT_KEY_TITLE));
        getColumn(3).setHeaderRenderer(new HeaderCellRenderer(SORT_KEY_CREATION_DATE));
        getColumn(4).setHeaderRenderer(new HeaderCellRenderer(SORT_KEY_LAST_MODIFIED_DATE));
        m_deleteColumn = getColumn(5);
        m_deleteColumn.setCellRenderer(new ActionCellRenderer());
        m_deleteColumn.setAlign("center");
        if (!hideIndexColumn()) {
            m_indexColumn = getColumn(6);
            m_indexColumn.setCellRenderer(new IndexToggleRenderer());
            m_indexColumn.setAlign("center");
            
            m_indexChanger = new IndexChanger(m_currentFolder);
            addTableActionListener(m_indexChanger);
        }
        m_folderChanger = new FolderChanger();
        addTableActionListener(m_folderChanger);
        
        m_deleter = new ItemDeleter();
        addTableActionListener(m_deleter);
        
        
        setEmptyView(new Label(globalize("cms.ui.folder.no_items")));
        
        Assert.assertNotNull( m_currentFolder.getStateParameter() );
    }
    
    public void register(Page p) {
        super.register(p);
        
        p.addComponentStateParam(this, m_currentFolder.getStateParameter());
        p.addComponentStateParam(this, m_sortType);
        p.addComponentStateParam(this, m_sortDirection);
        p.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                final PageState state = e.getPageState();
                
                if (state.isVisibleOnPage(FolderBrowser.this)) {
                    showHideFolderActions(state);
                }
            }
        });
    }
    
    private void showHideFolderActions(PageState state) {
        SecurityManager sm = Utilities.getSecurityManager(state);
        Folder folder = (Folder) m_currentFolder.getSelectedObject(state);
        Assert.assertNotNull(folder);
        
        boolean canDelete =
                sm.canAccess(state.getRequest(), SecurityManager.DELETE_ITEM, folder);
        
        m_deleteColumn.setVisible(state, canDelete);
    }
    
    public void respond(PageState state) throws ServletException {
        String key = state.getControlEventName();
        String value = state.getControlEventValue();
        if ( SORT_ACTION_UP.equals(key) ) {
            state.setValue(m_sortType, value);
            state.setValue(m_sortDirection, SORT_ACTION_UP);
        } else if ( SORT_ACTION_DOWN.equals(key) ) {
            state.setValue(m_sortType, value);
            state.setValue(m_sortDirection, SORT_ACTION_DOWN);
        } else {
            super.respond(state);
            //throw new ServletException("Unknown control event: " + key);
        }
    }
    
    public FolderSelectionModel getFolderSelectionModel() {
        return m_currentFolder;
    }
    
    private class FolderTableModelBuilder
            extends AbstractTableModelBuilder implements PaginationModelBuilder {
        
        private FolderSelectionModel m_folder;
        private RequestLocal m_size;
        private RequestLocal m_itemColl;
        private FolderBrowser m_fb;
        
        public FolderTableModelBuilder(FolderSelectionModel sel) {
            this(sel, null);
        }
        
        public FolderTableModelBuilder(FolderSelectionModel sel, FolderBrowser fb) {
            super();
            m_folder = sel;
            m_size = new RequestLocal();
            m_itemColl = new RequestLocal();
            m_fb = fb;
        }
        
        public TableModel makeModel(Table t, PageState s) {
            FolderSelectionModel sel = ((FolderBrowser) t).getFolderSelectionModel();
            Folder f = (Folder) sel.getSelectedObject(s);
            if ( f == null ) {
                return Table.EMPTY_MODEL;
            } else {
                t.getRowSelectionModel().clearSelection(s);
                return new FolderTableModel
                        ((FolderBrowser) t, s, (Folder.ItemCollection) m_itemColl.get(s));
            }
        }
        
        public int getTotalSize(Paginator paginator, PageState state) {
            
            Integer size = (Integer) m_size.get(state);
            
            if (size == null) {
                
                Folder f = (Folder) m_folder.getSelectedObject(state);
                
                Folder.ItemCollection itemColl = f.getPrimaryInstances();
                
                if (itemColl == null) {
                    return 0;
                }
                
                PermissionService.filterQuery(
                        itemColl,
                        "id",
                        PrivilegeDescriptor.READ,
                        Kernel.getContext().getParty().getOID()
                        );
                
                size = new Integer( (int) f.getPrimaryInstances().size());
                
                itemColl.setRange(new Integer(paginator.getFirst(state)),
                        new Integer(paginator.getLast(state) + 1));
                
                String sortKey = (String)state.getValue(m_sortType);
                String direction = "asc";
                if (SORT_ACTION_DOWN.equals((String)state.getValue(m_sortDirection))) {
                    direction = "desc";
                }
                
                if ( sortKey.equals(SORT_KEY_TITLE) ) {
                    itemColl.setOrder("lower(item." +
                            ContentItem.DISPLAY_NAME + ") " +
                            direction);
                } else if ( sortKey.equals(SORT_KEY_NAME) ) {
                    itemColl.setOrder("lower(item." +
                            ContentItem.NAME + ") " + direction);
                } else if ( sortKey.equals(SORT_KEY_LAST_MODIFIED_DATE)) {
                    itemColl.setOrder("item.auditing.lastModifiedDate " +
                            direction);
                } else if ( sortKey.equals(SORT_KEY_CREATION_DATE)) {
                    itemColl.setOrder("item.auditing.creationDate " +
                            direction);
                }
                
                m_size.set(state, size);
                m_itemColl.set(state, itemColl);
            }
            
            return size.intValue();
        }
        
        public void setFolderBrowser(FolderBrowser fb) {
            m_fb = fb;
        }
        
        /**
         * Indicates whether the paginator should be visible,
         * based on the visibility of the folder browser itself.
         *
         * @return true if folder browser is visible, or if the
         *         associated folder browser is unknown.
         */
        public boolean isVisible(PageState state) {
            return (m_fb != null)?m_fb.isVisible(state):true;
        }
    }
    
    
    private class HeaderCellRenderer
            extends com.arsdigita.cms.ui.util.DefaultTableCellRenderer {
        
        private String m_key;
        
        public HeaderCellRenderer(String key) {
            super(true);
            m_key = key;
        }
        
        public Component getComponent(final Table table, final PageState state,
                Object value,
                boolean isSelected, Object key,
                int row, int column) {
            String headerName = (String)((GlobalizedMessage)value).localize();
            String sortKey = (String)state.getValue(m_sortType);
            final boolean isCurrentKey = sortKey.equals(m_key);
            final String currentSortDirection = (String)state.getValue(m_sortDirection);
            String imageURLStub = null;
            
            if (SORT_ACTION_UP.equals(currentSortDirection)) {
                imageURLStub = "gray-triangle-up.gif";
            } else {
                imageURLStub = "gray-triangle-down.gif";
            }
            
            ControlLink cl =  new ControlLink(headerName) {
                public void setControlEvent(PageState ps) {
                    String sortDirectionAction = null;
                    // by default, everything sorts "up" unless it
                    // is the current key and it is already pointing up
                    if (SORT_ACTION_UP.equals(currentSortDirection) &&
                            isCurrentKey) {
                        sortDirectionAction = SORT_ACTION_DOWN;
                    } else {
                        sortDirectionAction = SORT_ACTION_UP;
                    }
                    ps.setControlEvent(table,
                            sortDirectionAction,
                            m_key);
                }
            };
            Label l = new Label();
            l.setLabel("<font color=\"blue\">" + headerName + "</font>");
            l.setOutputEscaping(false);
            l.setFontWeight(Label.BOLD);
            
            SimpleContainer container = new SimpleContainer();
            container.add(l);
            if ( isCurrentKey ) {
                Image image = new Image("/assets/" + imageURLStub);
                image.setBorder("0");
                container.add(image);
            }
            cl.setChild(container);
            return cl;
        }
    }
    
    /**
     * Produce links to view an item or control links for folders
     * to change into the folder.
     */
    private class NameCellRenderer extends DefaultTableCellRenderer {
        public NameCellRenderer() {
            super(true);
        }
        
        public Component getComponent(Table table, PageState state, Object value,
                boolean isSelected, Object key,
                int row, int column) {
            
            Folder.ItemCollection coll = (Folder.ItemCollection) value;
            String name = coll.getName();
            if ( coll.isFolder() ) {
                return super.getComponent(table, state, name,
                        isSelected, key, row, column);
            } else {
                ContentSection section = CMS.getContext().getContentSection();
                BigDecimal id = coll.getID();
                
                if (section == null) {
                    return new Label(name);
                } else {
                    ItemResolver resolver = section.getItemResolver();
                    return new Link(name, resolver.generateItemURL
                            (state, id, name, section, coll.getVersion()));
                }
            }
        }
    }
    
    
    /**
     * Produce delete links for items and non-empty folders.
     */
    private static class ActionCellRenderer implements TableCellRenderer {
        private static Label s_noAction;
        private static ControlLink s_link;
        
        static {
            s_noAction = new Label("&nbsp;", false);
            s_noAction.lock();
            s_link = new ControlLink(new Label(globalize("cms.ui.folder.delete")));
            s_link.setConfirmation("Permanently delete this item?"); // XXX G11N ?
        }
        
        public Component getComponent(Table table, PageState state, Object value,
                boolean isSelected, Object key,
                int row, int column) {
            if ( ((Boolean) value).booleanValue() ) {
                return s_link;
            } else {
                return s_noAction;
            }
        }
    }
    
    private final class IndexToggleRenderer implements TableCellRenderer {
        
        public Component getComponent(Table table, PageState state, Object value, boolean isSelected, Object key, int row, int column) {
            
            if ( value == null ) {
                return new Label(GlobalizationUtil.globalize("cms.ui.folder.na"));
            }
            ControlLink link = new ControlLink("");
            
            if (((Boolean) value).booleanValue()) {
                link.setClassAttr("checkBoxChecked");
            } else {
                link.setClassAttr("checkBoxUnchecked");
            }
            
            return link;
        }
    }
    
    // Deletes an item
    private class ItemDeleter extends TableActionAdapter {
        public void cellSelected(TableActionEvent e) {
            int col = e.getColumn().intValue();
            
            if ( m_deleteColumn != getColumn(col) ) {
                return;
            }
            
            PageState s = e.getPageState();
            BigDecimal id = new BigDecimal(e.getRowKey().toString());
            
            OID oid = new OID(ContentItem.BASE_DATA_OBJECT_TYPE, id.abs());
            
            final DomainObject item = DomainObjectFactory.newInstance(oid);
            
            if (item != null) {
                if (item instanceof ContentBundle) {
                    ContentBundle bundle = (ContentBundle) item;
                    ItemCollection instances = bundle.getInstances();
                    while (instances.next()) {
                        instances.getContentItem().delete();
                    }
                }
                item.delete();
            }
            
            ((Table) e.getSource()).clearSelection(s);
        }
    }
    
    /**
     * Table model around ItemCollection
     */
    private static class FolderTableModel implements TableModel {
        private static final int NAME = 0;
        private static final int TITLE = 1;
        private static final int TYPE = 2;
        private static final int CREATION_DATE = 3;
        private static final int LAST_MODIFIED = 4;
        private static final int DELETABLE = 5;
        private static final int IS_INDEX = 6;
        
        private PageState m_state;
        private FolderBrowser m_table;
        private Folder.ItemCollection m_itemColl;
        private Folder m_fol;
        private BigDecimal m_folIndexID;
        
        //old constructor before using paginator
        //public FolderTableModel(Folder folder) {
        //m_itemColl = folder.getItems();
        //}
        
        public FolderTableModel
                (FolderBrowser table, PageState state, Folder.ItemCollection itemColl) {
            m_state = state;
            m_table = table;
            m_itemColl = itemColl;
            
            m_fol = (Folder) table.getFolderSelectionModel().getSelectedObject(state);
            if (!hideIndexColumn()) {
                ContentBundle indexItem = m_fol.getIndexItem();
                if ( indexItem == null ) {
                    m_folIndexID = null;
                } else {
                    m_folIndexID = indexItem.getID();
                }
            }
        }
        
        public int getColumnCount() {
            return hideIndexColumn() ? 6 : 7;
        }
        
        public boolean nextRow() {
            return m_itemColl.next();
        }
        
        public Object getElementAt(int columnIndex) {
            switch (columnIndex) {
                case NAME:
                    return m_itemColl;
                case TITLE:
                    return m_itemColl.getDisplayName();
                case TYPE:
                    return m_itemColl.getTypeLabel();
                case CREATION_DATE: {
                    java.util.Date creationDate = m_itemColl.getCreationDate();
                    if ( creationDate == null ) {
                        return "--";
                    }
                    return FormatStandards.formatDate(creationDate);
                }
                case LAST_MODIFIED: {
                    java.util.Date lastModified = m_itemColl.getLastModifiedDate();
                    if ( lastModified == null ) {
                        return "--";
                    }
                    return FormatStandards.formatDate(lastModified);
                }
                case DELETABLE:
                    return new Boolean(isDeletable());
                case IS_INDEX: {
                    if (hideIndexColumn()) {
                        throw new IndexOutOfBoundsException("Column index " + columnIndex +
                                " not in table model.");
                    }
                    if ( m_itemColl.isFolder() ) {
                        return null;
                    }
                    if ( m_folIndexID == null ) {
                        return new Boolean(false);
                    }
                    return new Boolean(m_folIndexID
                            .compareTo(m_itemColl.getBundleID()) == 0);
                }
                default:
                    throw new IndexOutOfBoundsException("Column index " + columnIndex +
                            " not in table model.");
            }
        }
        
        public boolean isDeletable() {
            if (s_log.isDebugEnabled()) {
                s_log.debug("Checking to see if " + this + " is deletable");
            }
            
            if (m_itemColl.isLive()) {
                if (s_log.isDebugEnabled()) {
                    s_log.debug("The item is live; it cannot be deleted");
                }
                
                return false;
            }
            
            if (m_itemColl.isFolder()) {
                if (!m_itemColl.hasChildren()) {
                    if (s_log.isDebugEnabled()) {
                        s_log.debug("The item is an empty folder; it may be " +
                                "deleted");
                    }
                    return true;
                } else {
                    if (s_log.isDebugEnabled()) {
                        s_log.debug("The folder is not empty; it cannot be " +
                                "deleted");
                    }
                    return false;
                }
            } else {
                if (s_log.isDebugEnabled()) {
                    s_log.debug("The item is not a folder; it may be deleted");
                }
                
                return true;
            }
        }
        
        public Object getKeyAt(int columnIndex) {
            // Mark folders by using their negative ID (dirty, dirty)
            return ( m_itemColl.isFolder() ) ?  m_itemColl.getID().negate()
            : m_itemColl.getBundleID();
        }
    }
    
    private class FolderChanger extends TableActionAdapter {
        public void cellSelected(TableActionEvent e) {
            PageState s = e.getPageState();
            int col = e.getColumn().intValue();
            
            if ( m_nameColumn != getColumn(col) ) {
                return;
            }
            String key = (String) e.getRowKey();
            if ( key.startsWith("-") ) { // XXX dirty dirty
                clearSelection(s);
                getFolderSelectionModel().setSelectedKey(s, key.substring(1));
            }
        }
    }
    
    private class IndexChanger extends TableActionAdapter {
        
        private FolderSelectionModel m_fol;
        
        public IndexChanger(FolderSelectionModel fol) {
            super();
            m_fol = fol;
        }
        
        public void cellSelected(TableActionEvent e) {
            PageState state = e.getPageState();
            
            BigDecimal rowkey = new BigDecimal((String)e.getRowKey());
            if (rowkey == null){
                return;
            }
            
            try {
                ContentBundle contentItem = new ContentBundle(rowkey);
                
                Folder folder = (Folder) m_fol.getSelectedObject(state);
                
                ContentBundle currentIndexItem = (ContentBundle) folder.getIndexItem();
                if ( currentIndexItem == null || (currentIndexItem.getID().compareTo(contentItem.getID()) != 0)) {
                    folder.setIndexItem(contentItem);
                } else {
                    folder.removeIndexItem();
                }
                folder.save();
            } catch (DataObjectNotFoundException donfe) {
                // Do nothing
            }
        }
    }
    
    /**
     * Getting the GlobalizedMessage using a CMS Class targetBundle.
     *
     * @param key The resource key
     * @pre ( key != null )
     */
    private static GlobalizedMessage globalize(String key) {
        return FolderManipulator.globalize(key);
    }
    
    private static boolean hideIndexColumn() {
        return ContentSection.getConfig().getHideFolderIndexCheckbox();
    }
}
