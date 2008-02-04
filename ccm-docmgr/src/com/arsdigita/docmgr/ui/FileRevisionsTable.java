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

package com.arsdigita.docmgr.ui;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;

import javax.servlet.http.HttpServletResponse;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.ControlLink;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.event.TableActionEvent;
import com.arsdigita.bebop.event.TableActionListener;
import com.arsdigita.bebop.table.TableCellRenderer;
import com.arsdigita.bebop.table.TableModel;
import com.arsdigita.bebop.table.TableModelBuilder;
import com.arsdigita.docmgr.File;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.util.LockableImpl;
import com.arsdigita.versioning.Tag;
import com.arsdigita.versioning.TagCollection;
import com.arsdigita.versioning.Transaction;
import com.arsdigita.versioning.TransactionCollection;

/**
 * This component lists all file revisions in tabular form.
 * The right-most column has a button to download that particular
 * version.
 *
 * @author <mailto href="StefanDeusch@computer.org">Stefan Deusch</a>
 */

class FileRevisionsTable extends Table
        implements TableActionListener, DMConstants {

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
        PageState state = e.getPageState();

        int col = e.getColumn().intValue();
        InputStream is = null;
        HttpServletResponse resp = state.getResponse();

        // download file revision blob on click onto 4th column
        if (col==4) {
           String downloadKey = (String)e.getRowKey();

          //If this row refers to the latest version...
          if(downloadKey.equals("current")) {
              File file = m_parent.getFile(state);
              String mimetype = file.getContentType();
                  if (mimetype == null) {
                      mimetype = File.DEFAULT_MIME_TYPE;
                  }

              resp.setContentType(mimetype);

              byte[] bites = file.getRawContent();
              is = new ByteArrayInputStream(bites);

          } else {
              BigDecimal transactionID = new BigDecimal(downloadKey);

              Session session = SessionManager.getSession();
              DataQuery query = session.retrieveQuery
                      ("com.arsdigita.docs.getFileRevisionBlob");
              query.setParameter("transactionID", transactionID);
              if (query.next()) {


                  String mimeType = m_parent.getFile(state).getContentType();
                  if (mimeType == null) {
                      mimeType = File.DEFAULT_MIME_TYPE;
                  }

                  resp.setContentType(mimeType);

                  Object blob = query.get("content");
                  is = new ByteArrayInputStream((byte[]) blob);
               }

           }//end else


           byte[] buf = new byte[8192]; // 8k buffer
           OutputStream os = null;
           try {
               os = resp.getOutputStream();
               int sz = 0;
               while ((sz = is.read(buf, 0 , 8192)) != -1) {
                   os.write(buf, 0, sz);
               }
           } catch (IOException iox) {
               throw new RuntimeException(iox.getMessage());
           } finally {
               if (null != os) {
                   try {
                       os.close();
                   } catch(IOException closeErr) {
                       // Ignore, since no log4j here.
                   }
               }
           }
        }
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
                ControlLink l = new ControlLink(value.toString());
                l.setClassAttr("downloadLink");
                return l;
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
        return new FileRevisionsTableModel(m_parent.getFile(state), state);
    }
}

class FileRevisionsTableModel implements TableModel, DMConstants {

    private FileInfoHistoryPane m_parent;
    private File m_file;
    private PageState m_state;
    private TransactionCollection m_tc;
    private Transaction m_transaction;
    private Transaction m_lastContentChange;
    private int m_row;
    private int m_last = 2;

    FileRevisionsTableModel(File file, PageState state) {
        m_file = file;
        m_state = state;

        m_tc = m_file.getTransactions();
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
                com.arsdigita.kernel.User user = m_file.getLastModifiedUser();
                if (null == user) {
                    return "Unknown";
                } else {
                    return user.getPersonName().toString();
                }
            }
            case 2:
                if(m_row == 0)
                 return DMUtils.DateFormat.format(m_file.getCreationDate());
                else
                 return DMUtils.DateFormat.format(m_file.getLastModifiedDate());
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
          if(m_row == m_last - 1) {
            return "current";
          } else {
            return m_transaction.getID();
          }
        } else {
            return m_file.getID() + "." + (m_row);
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
