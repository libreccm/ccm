package com.arsdigita.cms.docmgr.ui;

import java.math.BigDecimal;

import org.apache.log4j.Logger;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Link;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.event.TableActionEvent;
import com.arsdigita.bebop.event.TableActionListener;
import com.arsdigita.bebop.table.TableCellRenderer;
import com.arsdigita.bebop.table.TableModel;
import com.arsdigita.bebop.table.TableModelBuilder;
import com.arsdigita.cms.docmgr.Document;
import com.arsdigita.kernel.User;
import com.arsdigita.util.LockableImpl;
import com.arsdigita.versioning.Tag;
import com.arsdigita.versioning.TagCollection;
import com.arsdigita.versioning.Transaction;
import com.arsdigita.versioning.TransactionCollection;
import com.arsdigita.versioning.Versions;

/**
 * @author Peter Kopunec
 */
public class BrowseFileRevisionsTable extends Table implements TableActionListener, DMConstants {
	
	protected static final Logger s_log = Logger.getLogger(BrowseFileRevisionsTable.class);
	
	public static final String[] s_tableHeaders = { "", "Author", "Date", "Comments", "" };
	
	private final BrowsePane m_parent;
	private Page m_page;
	
	public BrowseFileRevisionsTable(BrowsePane parent) {
		super(new BrowseFileRevisionsTableModelBuilder(parent), s_tableHeaders);
		m_parent = parent;
		setClassAttr("AlternateTable");
		setWidth("100%");
		getColumn(4).setCellRenderer(new LinkRenderer());
		addTableActionListener(this);
	}
	
	public void cellSelected(TableActionEvent e) {
	}
	
	public void headSelected(TableActionEvent e) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Register the page the fist time
	 */
	public void register(Page p) {
		m_page = p;
		super.register(p);
	}
	
//	public BigDecimalParameter getFileIDParam() {
//		try {
//			return ((DocmgrBasePage) m_page).getFileIDParam();
//		}
//		catch (Throwable e) {
//		}
//		return null;
//	}
	
	public void setDocID(PageState ps, BigDecimal docID) {
		Document doc = new Document(docID);
		Label title = m_page.getTitle();
		title.setLabel(doc.getTitle(), ps);
		BrowseFileRevisionsTableModelBuilder builder = (BrowseFileRevisionsTableModelBuilder) getModelBuilder();
		builder.setDocument(doc);
	}
	
	private final class LinkRenderer implements TableCellRenderer {
		public Component getComponent(Table table, PageState state, Object value, boolean isSelected, Object key, int row, int column) {
            String keyTxt = String.valueOf(key);
            int index = keyTxt.indexOf('.');
			if (value != null && index > -1) {
				Document doc = new Document(new BigDecimal(keyTxt.substring(0, index)));
				Link link = new Link("Download", "download/");
				link.setVar(DMConstants.FILE_ID_PARAM_NAME, doc.getID().toString());
				link.setVar("transID", keyTxt.substring(index + 1));
				link.setClassAttr("downloadLink");
				return link;
			}
			return new Label();
		}
	}
}

class BrowseFileRevisionsTableModelBuilder extends LockableImpl implements TableModelBuilder {
	
	private final BrowsePane m_parent;
	private BrowseFileRevisionsTableModel m_model;
	private Document m_doc;
	
	public BrowseFileRevisionsTableModelBuilder(BrowsePane parent) {
		m_parent = parent;
	}
	
	public TableModel makeModel(Table t, PageState ps) {
		m_model = new BrowseFileRevisionsTableModel(m_doc);
		return m_model;
	}
	
	public void setDocument(Document doc) {
		m_doc = doc;
	}
}

class BrowseFileRevisionsTableModel implements TableModel, DMConstants {
	
	private Document m_document;
	private Transaction m_transaction;
	private int m_row;
	private TransactionCollection m_tc;
	
	public BrowseFileRevisionsTableModel(Document doc) {
		m_document = doc;
		
		m_tc = Versions.getTaggedTransactions(m_document.getOID());
		m_row = (int) m_tc.size() + 1;
	}
	
	public int getColumnCount() {
		return BrowseFileRevisionsTable.s_tableHeaders.length;
	}
	
	public Object getElementAt(int columnIndex) {
		switch (columnIndex) {
			case 0:
				return new BigDecimal(m_row);
			case 1: {
				User user = m_transaction.getUser();
				if (null == user) {
					return "Unknown";
				}
				else {
					return user.getPersonName().toString();
				}
			}
			case 2:
				if (m_row == 0)
					return DMUtils.DateFormat.format(m_document.getCreationDate());
				else
					return DMUtils.DateFormat.format(m_transaction.getTimestamp());
			case 3: {
				StringBuffer sb = new StringBuffer();
				TagCollection tc = m_transaction.getTags();
				int counter = 0;
				while (tc.next()) {
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
	
	public boolean nextRow() {
		if (m_tc == null) {
			return false;
		}
		m_row--;
		if (m_tc.next()) {
			m_transaction = m_tc.getTransaction();
			return true;
		}
		else {
			m_tc.close();
			return false;
		}
	}
	
	public Object getKeyAt(int columnIndex) {
		if (columnIndex == 4) {
			return m_document.getID() + "." + m_transaction.getID();
		}
		else {
			return m_document.getID() + "." + m_row;
		}
	}
}
