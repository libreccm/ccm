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
package com.arsdigita.docrepo.ui;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.ControlLink;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Link;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.Tree;
import com.arsdigita.bebop.event.TableActionEvent;
import com.arsdigita.bebop.event.TableActionListener;
import com.arsdigita.bebop.form.CheckboxGroup;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.parameters.ArrayParameter;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.bebop.table.TableCellRenderer;
import com.arsdigita.bebop.table.TableModel;
import com.arsdigita.bebop.table.TableModelBuilder;
import com.arsdigita.docrepo.File;
import com.arsdigita.docrepo.Folder;
import com.arsdigita.docrepo.ResourceImpl;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.util.LockableImpl;
import com.arsdigita.web.ParameterMap;
import com.arsdigita.web.RedirectSignal;
import com.arsdigita.web.URL;

import java.math.BigDecimal;

/**
 * This class has dual functionality as the name implies.
 * Firstly, it contains a table that lists the contents of
 * a given directory whose unique Folder ID is described
 * in the m_folderID model parameter that is passed in
 * the constructor or changed after construction at runtime.
 * (currently retrieved by the folder ID of the
 * the global state parameter SEL_FOLDER_ID_PARAM)
 * The table contains a checkbox for each item for bulk operations.
 *
 * @author <mailto href="StefanDeusch@computer.org">Stefan Deusch</a>
 */

class FolderTable extends Table
    implements TableActionListener, DRConstants {

    private final static String FOLDER_LIST_CONTENT_IDS = "folder-listing-ids";

    static String[] s_tableHeaders = {
        "",
        "Name",
        "Size",
        "Type",
        "Modified",
        ""
    };

    private CheckboxGroup m_checkboxGroup;
    private ArrayParameter m_sources;
    private FolderContentsTableForm m_parent;
    private Tree m_tree;


    /**
     * Constructor
     * @param tree to get the selected folder
     * @param parent corresponding form to this table
     */

    public FolderTable(Tree tree, FolderContentsTableForm parent) {

        super(new FolderTableModelBuilder(tree, parent), s_tableHeaders);
        m_parent = parent;
        m_tree = tree;

        m_sources = new ArrayParameter(new BigDecimalParameter
                                       (FOLDER_LIST_CONTENT_IDS));
        m_checkboxGroup = new CheckboxGroup(FOLDER_LIST_CONTENT_IDS);
        m_parent.add(m_checkboxGroup);

        //  setClassAttr("AlternateTable");
        setWidth("100%");
        setCellRenderers();
        addTableActionListener(this);
    }

    @Override
    public void register(Page p) {
        super.register(p);
        p.addComponentStateParam(this, m_sources);
    }

    public CheckboxGroup getCheckboxGroup() {
        return m_checkboxGroup;
    }

    public void cellSelected(TableActionEvent e) {
        PageState state = e.getPageState();
        int col = e.getColumn().intValue();
        String rowkey = (String)e.getRowKey();

        int j = rowkey.indexOf(".");
        String id = rowkey.substring(0, j);

        char isFolder = rowkey.charAt(j+1);  // either '1' or 'n' from "null"

        // set new Folder ID
        if (isFolder == 't') {
            String oldKey = (String) m_tree.getSelectedKey(state);
            m_tree.setSelectedKey(state, id);
            m_tree.expand(oldKey, state);

            // wipe out selected file in state or we get lost in BrowsePane
            state.setValue(FILE_ID_PARAM, null);

        } else {
            // redirect to file-info
            ParameterMap params = new ParameterMap();
            params.setParameter(FILE_ID_PARAM.getName(), id);
            final URL url = URL.here(state.getRequest(),"/file", params);
            throw new RedirectSignal(url,true);
        }

    }

    public void headSelected(TableActionEvent e) {
        throw new UnsupportedOperationException();
    }

    private void setCellRenderers() {
        getColumn(0).setCellRenderer(new CheckBoxRenderer());
        getColumn(1).setCellRenderer(new LinkRenderer());
        getColumn(5).setCellRenderer(new DownloadLinkRenderer());
    }

    private final class DownloadLinkRenderer implements TableCellRenderer {
        public Component getComponent(Table table,
                                      PageState state,
                                      Object value,
                                      boolean isSelected,
                                      Object key,
                                      int row,
                                      int column) {
            if (value==null) {
                return new Label();
            } else {
                Link link = new Link("Download",
                                     "download/" + value + "?" +
                                     FILE_ID_PARAM.getName() + "=" + key);
                link.setClassAttr("downloadLink");
                return link;
            }
        }
    }

    private final class LinkRenderer implements TableCellRenderer {
        public Component getComponent(Table table,
                                      PageState state,
                                      Object value,
                                      boolean isSelected,
                                      Object key,
                                      int row,
                                      int column) {


            ResourceImpl resource = (ResourceImpl) value;

            String classAttr;
            if (resource.isFolder()) {
                classAttr = "isFolder";
            } else {
                classAttr = "isFile";
            }

            // mimeTypes not supported yet

            Label iconLabel = new Label();
            if (classAttr != null) {
                iconLabel.setClassAttr(classAttr);
            }

            // return container
            SimpleContainer link = new SimpleContainer();
            link.add(iconLabel);

            link.add(new ControlLink(resource.getName()));

            return link;
        }
    }

    private final class  CheckBoxRenderer implements TableCellRenderer {
        public Component getComponent(Table table,
                                      PageState state,
                                      Object value,
                                      boolean isSelected,
                                      Object key,
                                      int row,
                                      int column) {

            String encodedKey = (String) key;
            int j = encodedKey.indexOf(".");
            BigDecimal id = new BigDecimal(encodedKey.substring(0, j));

            Option result = new Option(m_sources.marshalElement(id.abs()), "");
            result.setGroup(m_checkboxGroup);
            return result;
        }
    }


}

class  FolderTableModelBuilder
    extends LockableImpl implements TableModelBuilder {

    private Tree m_tree;
    private FolderContentsTableForm m_parent;

    FolderTableModelBuilder(Tree tree, FolderContentsTableForm parent) {
        m_tree = tree;
        m_parent = parent;
    }

    public TableModel makeModel(Table t, PageState state) {

        // get parent folderID
        BigDecimal fid = DRUtils.getSelectedFolderID(state, m_tree);

        // create and return a FolderTableModel
        return new FolderTableModel(fid, state);
    }


    class FolderTableModel implements TableModel, DRConstants {

        private BigDecimal m_parentFolderID;
        private PageState m_state;
        private boolean m_more;
        private DataQuery m_query;

        /**
         * Constructor takes folder ID
         */
        FolderTableModel(BigDecimal folderID, PageState state) {
            m_parentFolderID = folderID;
            m_state = state;

            Session session = SessionManager.getSession();
            m_query = session.retrieveQuery(GET_CHILDREN);
            m_query.setParameter(FOLDER_ID, m_parentFolderID);

            if (m_query.size() == 0) {
                m_parent.hideActionLinks(state);
            } else {
                m_parent.hideEmptyLabel(state);
            }

        }

        public int getColumnCount() {
            return 6; // same length as header String[]
        }

        public Object getElementAt(int columnIndex) {
            final boolean isFolder =  ((Boolean)m_query.get(IS_FOLDER)).booleanValue();

            switch (columnIndex) {
            case 0 :
                return Boolean.FALSE;
            case 1: {
                String name = (String) m_query.get(NAME);
                String type = (String) m_query.get(TYPE);


                if (isFolder) {
                    Folder folder = new Folder((BigDecimal) m_query.get("id") );
                    return folder;
                } else {
                    File file = new File((BigDecimal) m_query.get("id"));
                    return file;
                }
            }
            case 2:

                if ( isFolder ) {
                    return null;
                } else {
                    File file = new File((BigDecimal) m_query.get("id"));
                    long size = ((BigDecimal)file.getSize()).longValue();
                    return DRUtils.FileSize.formatFileSize(size, m_state);
                }
            case 3:
                if (isFolder) {
                    return "Folder";
                } else {
                    return m_query.get(MIME_TYPE_LABEL);
                }
            case 4:
                return m_query.get(LAST_MODIFIED);
            case 5:
                if (isFolder) {
                    return null;
                } else {
                    return m_query.get(NAME);
                }
            default:
                break;
            }
            return null;
        }

        public Object getKeyAt(int columnIndex) {
            if (columnIndex == 5) {
                return m_query.get("id");
            } else {
                return m_query.get("id") + "." + m_query.get(IS_FOLDER);
            }
        }

        public boolean nextRow() {
            return m_query.next();
        }

    }
}
