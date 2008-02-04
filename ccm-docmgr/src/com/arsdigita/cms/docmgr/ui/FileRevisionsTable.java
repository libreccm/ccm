/*
* Copyright (C) 2001, 2002, 2003 Red Hat Inc. All Rights Reserved.
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

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Link;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.event.TableActionEvent;
import com.arsdigita.bebop.event.TableActionListener;
import com.arsdigita.bebop.table.TableCellRenderer;
import com.arsdigita.bebop.table.TableModel;
import com.arsdigita.bebop.table.TableModelBuilder;
import com.arsdigita.cms.docmgr.Document;
import com.arsdigita.util.LockableImpl;
import com.arsdigita.versioning.Tag;
import com.arsdigita.versioning.TagCollection;
import com.arsdigita.versioning.Transaction;
import com.arsdigita.versioning.TransactionCollection;
import com.arsdigita.versioning.Versions;
import org.apache.log4j.Logger;
import java.math.BigDecimal;

/**
 * This component lists all file revisions in tabular form.
 * The right-most column has a button to download that particular
 * version.
 *
 * @author <mailto href="StefanDeusch@computer.org">Stefan Deusch</a>
 */

class FileRevisionsTable extends Table
        implements TableActionListener, DMConstants {

    private static Logger s_log = Logger.getLogger(FileRevisionsTable.class);
    private FileInfoHistoryPane m_parent;
    static String[] s_tableHeaders = {
        "",
        "Author",
        "Date",
        "Comments",
        ""
    };


    /**
     * Constructor
     */

    public FileRevisionsTable(FileInfoHistoryPane parent) {

        super(new FileRevisionsTableModelBuilder(parent),  s_tableHeaders);
        m_parent = parent;

        setClassAttr("AlternateTable");
        setWidth("100%");
        setCellRenderers();
        addTableActionListener(this);
    }
    public void cellSelected(TableActionEvent e) {

    }
    public void headSelected(TableActionEvent e) {
        throw new UnsupportedOperationException();
    }

    private void setCellRenderers() {
        getColumn(4).setCellRenderer(new LinkRenderer());
    }

    private final class LinkRenderer implements TableCellRenderer {
        public Component getComponent(Table table,
                                      PageState state,
                                      Object value,
                                      boolean isSelected,
                                      Object key,
                                      int row,
                                      int column) {
            if (value != null) {

                Document doc = m_parent.getDocument(state);
                Link link = new Link("Download",
                                     "download/" + doc.getName() + "?" +
                                     DMConstants.FILE_ID_PARAM_NAME + "=" + doc.getID() + "&transID=" + key);
                link.setClassAttr("downloadLink");
                return link;

            } else {
                return new Label();
            }
        }
    }

}

class  FileRevisionsTableModelBuilder
        extends LockableImpl implements TableModelBuilder {

    private FileInfoHistoryPane m_parent;

    FileRevisionsTableModelBuilder(FileInfoHistoryPane parent) {
        m_parent =  parent;
    }

    public TableModel makeModel(Table t, PageState state) {
        return new FileRevisionsTableModel(m_parent.getDocument(state), state);
    }
}

class FileRevisionsTableModel implements TableModel, DMConstants {

    private FileInfoHistoryPane m_parent;
    private Document m_document;
    private PageState m_state;
    private TransactionCollection m_tc;
    private Transaction m_transaction;
    private Transaction m_lastContentChange;
    private int m_row;
    private int m_last = 2;

    FileRevisionsTableModel(Document doc, PageState state) {
        m_document = doc;
        m_state = state;

        m_tc = Versions.getTaggedTransactions(m_document.getOID());
        m_row = (int)m_tc.size()+1;
        m_last = m_row;

        // separate collection from last content changes
    }

    public int getColumnCount() {
        return 5;
    }

    public Object getElementAt(int columnIndex) {
        switch (columnIndex) {
            case 0 :
                return new BigDecimal(m_row);
            case 1: {
                com.arsdigita.kernel.User user = m_document.getLastModifiedUser();
                if (null == user) {
                    return "Unknown";
                } else {
                    return user.getPersonName().toString();
                }
            }
            case 2:
                if(m_row == 0)
                 return DMUtils.DateFormat.format(m_document.getCreationDate());
                else
                 return DMUtils.DateFormat.format(m_transaction.getTimestamp());
            case 3: {
                  StringBuffer sb = new StringBuffer();
                  TagCollection tc = m_transaction.getTags();
                  int counter = 0;
                  while(tc.next()) {
                      counter++;
                      Tag t = tc.getTag();
                      sb.append(counter + ") " + t.getDescription() + "  ");
                  }
                return sb.toString();
            }
            case 4:
                return "download";
            default:
                break;
        }
        return null;
    }

    public Object getKeyAt(int columnIndex) {
        if (columnIndex == 4) {
	    //          if(m_row == m_last - 1) {
            //return "current";
	    //} else {
            return m_transaction.getID();
	    //}
        } else {
            return m_document.getID() + "." + (m_row);
        }
    }

    public boolean nextRow() {
            m_row--;
        if (m_tc.next() ) {
          m_transaction = m_tc.getTransaction();
          return true;
        } else {
          m_tc.close();
          return false;
        }
    }


}
