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
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.ControlLink;
import com.arsdigita.bebop.ExternalLink;
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
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ContentType;
import com.arsdigita.cms.Folder;
import com.arsdigita.cms.docmgr.DocFolder;
import com.arsdigita.cms.docmgr.DocLink;
import com.arsdigita.cms.docmgr.DocMgr;
import com.arsdigita.cms.docmgr.Document;
import com.arsdigita.cms.docmgr.Repository;
import com.arsdigita.cms.docmgr.Resource;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.kernel.User;
import com.arsdigita.kernel.permissions.PermissionDescriptor;
import com.arsdigita.kernel.permissions.PermissionService;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.util.LockableImpl;
import com.arsdigita.web.Application;
import com.arsdigita.web.Web;
import com.arsdigita.xml.Element;

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
 * @author Peter Kopunec
 */
class FolderTable extends Table implements TableActionListener, DMConstants {

	private final static Logger s_log = Logger.getLogger(FolderTable.class);

	private final static String FOLDER_LIST_CONTENT_IDS = "folder-listing-ids";

	static String[] s_tableHeaders = { "", "Name", "Size", "Type", "Modified", "Download", "Send", "Edit", "Version history" };

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

		m_sources = new ArrayParameter(new BigDecimalParameter(FOLDER_LIST_CONTENT_IDS));
		m_checkboxGroup = new CheckboxGroup(FOLDER_LIST_CONTENT_IDS);
		m_parent.add(m_checkboxGroup);

//		setClassAttr("AlternateTable");
		setWidth("100%");
		setCellRenderers();
		addTableActionListener(this);
	}
	
	public void generateXML(PageState s, Element p) {
		long start = System.currentTimeMillis();
		super.generateXML(s, p);
		if (s_log.isInfoEnabled()) {
			s_log.info("generateXML:" + (System.currentTimeMillis() - start) + "ms");
		}
	}

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
		String rowkey = (String) e.getRowKey();

		int j = rowkey.indexOf(".");
		String id = rowkey.substring(0, j);

		char type = rowkey.charAt(j + 1); // either '1' or 'n' from "null"

		// set new Folder ID
		if (type == 'f') {
			switch (col) {
				case 7: // Edit
					BigDecimal folderID = new BigDecimal(id);
					m_parent.getParent().displayFolderEditForm(state, folderID);
					break;
				default:
					String oldKey = (String) m_tree.getSelectedKey(state);
					m_tree.setSelectedKey(state, id);
					m_tree.expand(oldKey, state);
					
					// clear paging
					state.setValue(m_parent.getPageNoParameter(), new Integer(0));

					// wipe out selected file in state or we get lost in BrowsePane
					//state.setValue(FILE_ID_PARAM, null);
					break;
			}

		}
		else {
			BigDecimal docID = new BigDecimal(id);
			switch (col) {
				case 6: // Send
					m_parent.getParent().displayFilePropSendColleagueForm(state, docID);
					break;
				case 7: // Edit
					if (type == 'l') {
						m_parent.getParent().displayDocLinkEditForm(state, docID);
					}
					else {
					m_parent.getParent().displayFilePropEditForm(state, docID);
					}
					break;
				case 8: // Version history
					m_parent.getParent().displayFileVersions(state, docID);
					break;
				default:
					m_parent.getParent().displayFilePropPanel(state, docID);
					break;
			}

			// redirect to file-info
//			ParameterMap params = new ParameterMap();
//			params.setParameter(FILE_ID_PARAM_NAME, id);
//			final URL url = URL.here(state.getRequest(), "/file", params);
//			throw new RedirectSignal(url,true);
		}

	}

	public void headSelected(TableActionEvent e) {
		throw new UnsupportedOperationException();
	}

	private void setCellRenderers() {
		getColumn(0).setCellRenderer(new CheckBoxRenderer());
//		getColumn(1).setCellRenderer(new LinkRenderer());
//		getColumn(5).setCellRenderer(new DownloadLinkRenderer());
//		getColumn(6).setCellRenderer(new SimpleLinkRenderer());
//		getColumn(7).setCellRenderer(new SimpleLinkRenderer());
//		getColumn(8).setCellRenderer(new SimpleLinkRenderer());
	}

//	private final class DownloadLinkRenderer implements TableCellRenderer {
//		public Component getComponent(Table table, PageState state, Object value, boolean isSelected, Object key, int row, int column) {
//			if (value == null) {
//				return new Label();
//			}
//			boolean isExternalLink = false;
//			try {
//				new BigDecimal((String) key);
//			}
//			catch (java.lang.NumberFormatException nfex) {
//				isExternalLink = true;
//			}
//			if (isExternalLink) {
//				ExternalLink link = new ExternalLink("Download", (String) key);
//				link.setClassAttr("downloadLink");
//				return link;
//			}
//			Link link = new Link("Download", "download/" + value + "?" + FILE_ID_PARAM_NAME + "=" + key);
//			link.setClassAttr("downloadLink");
//			return link;
//		}
//	}

//	private final class LinkRenderer implements TableCellRenderer {
//		public Component getComponent(Table table, PageState state, Object value, boolean isSelected, Object key, int row, int column) {
//
//			Resource resource = (Resource) value;
//			String classAttr;
//			boolean isLink = false;
//			boolean isExternalLink = false;
//			if (resource.isFolder()) {
//				classAttr = "isFolder";
//			}
//			else if (resource instanceof DocLink) {
//				classAttr = "isLink";
//				isLink = true;
//				isExternalLink = ((DocLink) resource).isExternal();
//			}
//			else {
//				classAttr = "isFile";
//			}
//			if (isLink && !isExternalLink) {
//				resource = ((DocLink) resource).getTarget();
//			}
//
//			// mimeTypes not supported yet
//			Label iconLabel = new Label();
//			if (classAttr != null) {
//				iconLabel.setClassAttr(classAttr);
//			}
//
//			// return container
//			SimpleContainer link = new SimpleContainer();
//			link.add(iconLabel);
//
//			if (isExternalLink) {
//				link.add(new ExternalLink((String) resource.getTitle(), ((DocLink) resource).getExternalURL()));
//			}
//			else if (isLink) {
//				Repository rep = ((Document) resource).getRepository();
//				s_log.debug(rep.getPath());
//				String path = Web.getConfig().getDispatcherContextPath() + rep.getPath() + "/file?" + FILE_ID_PARAM_NAME + "=" + resource.getID();
//				Link l = new Link((String) resource.getTitle(), path);
//				//fileURL+
//				link.add(l);
//			}
//			else {
//				link.add(new ControlLink(resource.getTitle()));
//			}
//			String descrip = resource.getDescription();
//			if (descrip != null && descrip.length() > 0) {
//				link.add(new Label("<br />", false));
//				link.add(new Label(descrip));
//			}
//			return link;
//		}
//	}

	private final class CheckBoxRenderer implements TableCellRenderer {
		public Component getComponent(Table table, PageState state, Object value, boolean isSelected, Object key, int row, int column) {

			String encodedKey = (String) key;
			int j = encodedKey.indexOf(".");
			BigDecimal id = new BigDecimal(encodedKey.substring(0, j));

			Option result = new Option(m_sources.marshalElement(id.abs()), "");
			result.setGroup(m_checkboxGroup);
			return result;
		}
	}

//	private final class SimpleLinkRenderer implements TableCellRenderer {
//		public Component getComponent(Table table, PageState state, Object value, boolean isSelected, Object key, int row, int column) {
//
//			if (value != null) {
//				final Resource resource = (Resource) value;
//				return new ControlLink(resource.getTitle());
//			}
//
//			return new Label();
//		}
//	}
}

class FolderTableModelBuilder extends LockableImpl implements TableModelBuilder {

	private final static Logger s_log = Logger.getLogger(FolderTableModelBuilder.class);

	public static final SimpleDateFormat dateFormatter = new SimpleDateFormat("MMMMMMMMMMM d, yyyy");

	private Tree m_tree;
	private FolderContentsTableForm m_parent;

	FolderTableModelBuilder(Tree tree, FolderContentsTableForm parent) {
		m_tree = tree;
		m_parent = parent;
	}

	public TableModel makeModel(Table t, PageState state) {
		// get parent folderID
		BigDecimal fid = DMUtils.getSelectedFolderID(state, m_tree);

		// create and return a FolderTableModel
		return new FolderTableModel(fid, state);
	}

	class FolderTableModel implements TableModel, DMConstants {

		private final BigDecimal m_typeIdDocument;
		private final BigDecimal m_typeIdDocLink;
		private final BigDecimal m_typeIdDocFolder;
		
		private BigDecimal m_parentFolderID;
		private PageState m_state;
		private boolean m_more;
		private DataQuery m_collection;
		private boolean m_isDocument;
		private boolean m_isDocLink;
		private boolean m_isDocFolder;
		private boolean m_isFolder;
		private ContentItem m_item;
		
		private BigDecimal m_primaryInstID;
		private BigDecimal m_targetDocId;
		private String m_url;
		private int m_rowsPerPage = DocMgr.getConfig().getRowsPerPage();

		private User m_user;
		private boolean m_isOwner;
		private boolean m_isManager;

		/**
		 * Constructor takes folder ID
		 */
		FolderTableModel(BigDecimal folderID, PageState state) {
			m_parentFolderID = folderID;
			m_state = state;

			Folder parentFolder = new Folder(folderID);

			m_collection = SessionManager.getSession().retrieveQuery("com.arsdigita.cms.docmgr.ui.ItemsInFolder");
			m_collection.setParameter(Folder.PARENT, folderID);
			m_collection.setParameter(Folder.VERSION, parentFolder.getVersion());
			m_collection.addOrder("isFolder desc");
			m_collection.addOrder("title");

			long size = m_collection.size();
			if (size == 0) {
				m_parent.hideActionLinks(state);
			}
			else {
				m_parent.hideEmptyLabel(state);
			}
			
			int maxPages = (int) (size / m_rowsPerPage) + (size % m_rowsPerPage > 0 ? 1 : 0);
			Integer pn = (Integer) state.getValue(m_parent.getPageNoParameter());
			int pageNo = (pn == null ? 0 : pn.intValue());
			int firstRowNo = ((int) (pageNo * m_rowsPerPage)) + 1;
			int lastRowNo = firstRowNo + m_rowsPerPage;
			if (s_log.isDebugEnabled()) {
				s_log.debug("items count:" + size + "; items per page:" + m_rowsPerPage + "; max pages:" + maxPages + 
						"; pageNo:" + pageNo + "; first row no:" + firstRowNo + "; last row no:" + lastRowNo);
			}
			m_collection.setRange(new Integer(firstRowNo), new Integer(lastRowNo));

			m_typeIdDocument = ContentType.findByAssociatedObjectType(Document.TYPE).getID();
			m_typeIdDocLink = ContentType.findByAssociatedObjectType(DocLink.TYPE).getID();
			m_typeIdDocFolder = ContentType.findByAssociatedObjectType(DocFolder.TYPE).getID();

			m_user = Web.getContext().getUser();
			Application app = Web.getContext().getApplication();
			m_isManager = PermissionService.checkPermission(new PermissionDescriptor(PrivilegeDescriptor.ADMIN, app, m_user));
		}

		public int getColumnCount() {
			return FolderTable.s_tableHeaders.length;
		}

		public Object getElementAt(int columnIndex) {
			switch (columnIndex) {
				case 0:
					return Boolean.FALSE;
				case 1: {
					Resource resource = null;
					String classAttr;
					boolean isLink = false;
					boolean isExternalLink = false;

					if (m_isFolder) {
						classAttr = "isFolder";
					}
					else if (m_isDocLink) {
						classAttr = "isLink";
						isLink = true;
						isExternalLink = m_url != null && m_url.length() > 0;
					}
					else {
						classAttr = "isFile";
					}
					if (isLink && !isExternalLink) {
						resource = new Document(m_targetDocId);
					}

					// mimeTypes not supported yet
					Label iconLabel = new Label();
					if (classAttr != null) {
						iconLabel.setClassAttr(classAttr);
					}

					// return container
					SimpleContainer link = new SimpleContainer();
					link.add(iconLabel);

					if (isExternalLink) {
						link.add(new ExternalLink((String) m_collection.get("title"), m_url));
					}
					else if (isLink) {
						Repository rep = ((Document) resource).getRepository();
						String path = Web.getConfig().getDispatcherContextPath() + rep.getPath() + "/file?" + FILE_ID_PARAM_NAME + "="
								+ resource.getID();
						Link l = new Link((String) resource.getTitle(), path);
						link.add(l);
					}
					else {
						link.add(new ControlLink((String) m_collection.get("title")));
					}
					String descrip = (String) m_collection.get("description");
					if (descrip != null && descrip.length() > 0) {
						link.add(new Label("<br />", false));
						link.add(new Label(descrip));
					}
					return link;
				}
				case 2:
					if (m_isDocument) {
						Long s = (Long) m_collection.get("length");
						long size = s == null ? 0 : s.longValue();
						return DMUtils.FileSize.formatFileSize(size);
					}
					else {
						if (m_isFolder) {
							int foldersCount = ((Integer) m_collection.get("foldersCount")).intValue();
							int itemsCount = ((Integer) m_collection.get("itemsCount")).intValue();
							return new GlobalizedMessage("ui.folderTableModelBuilder.folderItemCounts", BUNDLE_NAME, new Object[] {
									String.valueOf(foldersCount), String.valueOf(itemsCount) }).localize();
						}
						return null;
					}
				case 3:
					if (m_isFolder) {
						return "Folder";
					}
					else if (m_isDocument) {
						return m_collection.get("mimeType");
					}
					else {
						return "Doc Link";
					}
				case 4:
					if (m_isDocument) {
						Date d = (Date) m_collection.get("modified");
						if (d != null) {
							return dateFormatter.format(d);
						}
					}
					return null;
				case 5:
					if (m_isDocument || (m_isDocLink && m_targetDocId != null)) {
						Link link = new Link("Download", "download/?" + FILE_ID_PARAM_NAME + "="
								+ (m_isDocument ? m_primaryInstID : m_targetDocId));
						link.setClassAttr("downloadLink");
						return link;
					}
					if (m_isDocLink) {
						ExternalLink link = new ExternalLink("Download", m_url);
						link.setClassAttr("downloadLink");
						return link;
					}
					break;
				case 6:
					if (m_isDocument) {
						return new ControlLink(m_item.getDisplayName());
					}
					break;
				case 8:
					if (m_isOwner || m_isManager) {
						if (m_isDocument) {
							return new ControlLink(m_item.getDisplayName());
						}
					}
					break;
				case 7:
					if (m_isOwner || m_isManager) {
						return new ControlLink(m_item.getDisplayName());
					}
					break;
				default:
					break;
			}
			return null;
		}

		public Object getKeyAt(int columnIndex) {
			if (columnIndex == 5 && !m_isFolder) {
				if (m_isDocLink) {
					if (m_targetDocId != null) {
						return m_targetDocId.toString();
					}
					else {
						return m_url;
					}
				}
				if (m_isDocument) {
					return m_primaryInstID.toString();
				}
			}
			if ((columnIndex == 1 || columnIndex == 4) && !m_isFolder) {
				BigDecimal retID = m_targetDocId;
				if (retID == null) {
					retID = m_primaryInstID;
				}
				return String.valueOf(retID) + '.' + getType();
			}
			return String.valueOf(m_isFolder ? m_item.getID() : m_primaryInstID) + '.' + getType();
		}

		private char getType() {
			if (m_isFolder) {
				return 'f';
			}
			if (m_isDocLink) {
				return 'l';
			}
			if (m_isDocument) {
				return 'd';
			}
			return 'u';
		}

		public boolean nextRow() {
			boolean hasNext = m_collection.next();
			if (hasNext) {
				BigDecimal typeId = (BigDecimal) m_collection.get("typeId");
				m_isDocument = m_typeIdDocument.equals(typeId);
				m_isDocLink = m_typeIdDocLink.equals(typeId);
				m_isDocFolder = m_typeIdDocFolder.equals(typeId);
				m_isFolder = ((Boolean) m_collection.get("isFolder")).booleanValue();
				m_item = (ContentItem) DomainObjectFactory.newInstance((DataObject) m_collection.get("item"));
				
				m_primaryInstID = (BigDecimal) m_collection.get("primaryInstID");
				m_targetDocId = (BigDecimal) m_collection.get("targetDocId");
				m_url = (String) m_collection.get("url");

				//m_isOwner = m_user.getID().equals(m_collection.get("creatorID"));
				m_isOwner = false;
			}
			else {
				m_collection.close();
			}

			return hasNext;
		}

	}
}
