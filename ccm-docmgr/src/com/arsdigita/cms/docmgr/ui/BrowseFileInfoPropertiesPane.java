package com.arsdigita.cms.docmgr.ui;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TooManyListenersException;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.arsdigita.bebop.ActionLink;
import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.GridPanel;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Link;
import com.arsdigita.bebop.ModalContainer;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.bebop.SimpleComponent;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormValidationListener;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.event.RequestEvent;
import com.arsdigita.bebop.event.RequestListener;
import com.arsdigita.bebop.form.FileUpload;
import com.arsdigita.bebop.form.MultipleSelect;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.SingleSelect;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.ArrayParameter;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.bebop.parameters.EmailValidationListener;
import com.arsdigita.bebop.parameters.NotEmptyValidationListener;
import com.arsdigita.bebop.parameters.StringLengthValidationListener;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.bebop.parameters.TrimmedStringParameter;
import com.arsdigita.categorization.Category;
import com.arsdigita.categorization.CategoryCollection;
import com.arsdigita.cms.ContentBundle;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.ContentSectionCollection;
import com.arsdigita.cms.FileAsset;
import com.arsdigita.cms.docmgr.DocFolder;
import com.arsdigita.cms.docmgr.DocMgr;
import com.arsdigita.cms.docmgr.Document;
import com.arsdigita.dispatcher.DispatcherHelper;
import com.arsdigita.dispatcher.MultipartHttpServletRequest;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.kernel.Group;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.KernelExcursion;
import com.arsdigita.kernel.User;
import com.arsdigita.kernel.permissions.ObjectPermissionCollection;
import com.arsdigita.kernel.permissions.PermissionDescriptor;
import com.arsdigita.kernel.permissions.PermissionManager;
import com.arsdigita.kernel.permissions.PermissionService;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
import com.arsdigita.mail.Mail;
import com.arsdigita.mimetypes.MimeType;
import com.arsdigita.persistence.OID;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.versioning.TransactionCollection;
import com.arsdigita.versioning.Versions;
import com.arsdigita.web.Application;
import com.arsdigita.web.ParameterMap;
import com.arsdigita.web.URL;
import com.arsdigita.web.Web;
import com.arsdigita.xml.Element;

/**
 * @author Peter Kopunec
 */
public class BrowseFileInfoPropertiesPane extends ModalContainer implements DMConstants {
	
	private static final Logger s_log = Logger.getLogger(BrowseFileInfoPropertiesPane.class);
	
	private BrowsePane m_parent;
	private Page m_page;
	
	private Component m_properties;
	private Component m_edit;
	private Component m_upload;
	private FileSendColleaguePane m_sendColleague;
	private Component m_action;
	
	private FilePropertiesPanel m_propertiesPane;
	private BigDecimalParameter docIDParam = new BigDecimalParameter("doc_id");
	
	public BrowseFileInfoPropertiesPane(BrowsePane parent) {
		m_parent = parent;
		
		m_properties = makePropertiesPane();
		add(m_properties);
		setDefaultComponent(m_properties);
		
		m_edit = makeEditPane();
		add(m_edit);
		
		m_upload = makeUploadForm();
		add(m_upload);
		
		m_sendColleague = makeSendColleagueForm();
		add(m_sendColleague);
	}
	
	/**
	 * Register the page the fist time
	 */
	public void register(Page p) {
		m_page = p;
		p.addGlobalStateParam(docIDParam);
		
		super.register(p);
	}

    public BigDecimalParameter getFileIDParam() {
    	try {
    		return ((DocmgrBasePage) m_page).getFileIDParam();
		}
		catch (Throwable e) {
		}
        return null;
    }
	
    public void setDocID(PageState state, BigDecimal docID) {
    	state.setValue(docIDParam, docID);
    	m_page.getTitle().setLabel(DMUtils.getFile(docID).getTitle(), state);
	}
    
	private Component makePropertiesPane() {
		SimpleContainer container= new SimpleContainer();
		
		m_propertiesPane = new FilePropertiesPanel(this);
		container.add(m_propertiesPane);
		
		container.add(new FileActionPane());
		return container;
	}
	
    public void displayPropertiesPane(PageState state) {
    	setVisibleComponent(state, m_properties);
	}
    
    private Component makeEditPane() {
		return new FileEditForm(this);
	}
    
    public void displayEditForm(PageState state) {
    	setVisibleComponent(state, m_edit);
	}
    
    private Component makeUploadForm() {
		return new VersionUploadForm();
	}
    
    public void displayUploadForm(PageState state) {
    	setVisibleComponent(state, m_upload);
	}
    
    private FileSendColleaguePane makeSendColleagueForm() {
		return new FileSendColleaguePane();
	}
    
    public void displaySendColleagueForm(PageState state) {
    	m_sendColleague.initState(state);
    	setVisibleComponent(state, m_sendColleague);
	}
    
    class BackCancelActionLink extends ActionLink {
    	
    	public BackCancelActionLink(String label) {
    		super(label);
    		addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					m_parent.displayFolderContentPanel(e.getPageState());
				}
			});
    	}
    }
    
	class FilePropertiesPanel extends SimpleComponent {
		
		BrowseFileInfoPropertiesPane m_parent;
		
		public FilePropertiesPanel(BrowseFileInfoPropertiesPane parent) {
			m_parent = parent;
		}
		
		public void generateXML(PageState state, Element parent) {
			Element element = parent.newChildElement("docs:file-info", DOCS_XML_NS);
			BigDecimal docID = (BigDecimal) state.getValue(docIDParam);
			if (docID != null) {
				try {
					// Retrieve resource properties.
					Document doc = new Document(docID);
					if (s_log.isDebugEnabled()) {
						s_log.debug("doc raw content: " + doc.getSearchRawContent());
						//s_log.debug("doc xml content: " + doc.getSearchXMLContent());
					}

					Element titleElement = element.newChildElement("docs:title", DOCS_XML_NS);
					titleElement.setText(doc.getTitle());

					Element nameElement = element.newChildElement("docs:name", DOCS_XML_NS);
					nameElement.setText(URLDecoder.decode(doc.getName()));

					Element descriptionElement = element.newChildElement("docs:description", DOCS_XML_NS);
					String description = doc.getDescription();
					if (description != null) {
						descriptionElement.setText(description);
					}

					Element sizeElement = element.newChildElement("docs:size", DOCS_XML_NS);
					sizeElement.setText(DMUtils.FileSize.formatFileSize(doc.getSize()));

					Element typeElement = element.newChildElement("docs:type", DOCS_XML_NS);
					// Retrieve pretty name for a mime type.
					typeElement.setText(doc.getPrettyMimeType());

					Element lastModifiedElement = element.newChildElement("docs:last-modified", DOCS_XML_NS);
					lastModifiedElement.setText(null != doc.getLastModifiedDate() ? DMUtils.DateFormat.format(doc.getLastModifiedDate()) : "");

					Element revisionElement = element.newChildElement("docs:revision", DOCS_XML_NS);

					TransactionCollection tc = Versions.getTaggedTransactions(doc.getOID());
					long numRevs = tc.size();
					revisionElement.setText(numRevs + "");

					// Must allow for the possibility that not author is available.

					Element authorElement = element.newChildElement("docs:author", DOCS_XML_NS);
					authorElement.setText(doc.getImpliedAuthor());

					Element uriElement = element.newChildElement("docs:uri", DOCS_XML_NS);
					uriElement.setText(makeFileURL(doc, state));

					Element catsElement = element.newChildElement("docs:categories", DOCS_XML_NS);

					CategoryCollection cats = doc.getCategoryCollection();
					Category cat;
					Element catElement;
					while (cats.next()) {
					    cat = cats.getCategory();
					    catElement = catsElement.newChildElement("docs:category", DOCS_XML_NS);
					    catElement.addAttribute("id", cat.getID().toString());
					    catElement.addAttribute("name", cat.getName());
					}

				}
				catch (DataObjectNotFoundException exc) {
					Element notfoundElement = element.newChildElement("docs:notfound", DOCS_XML_NS);
				}
			}
			else {
				Element notfoundElement = element.newChildElement("docs:notfound", DOCS_XML_NS);
			}
		}

		private String makeFileURL(Document doc, PageState state) {
			final HttpServletRequest req = state.getRequest();

			final ParameterMap params = new ParameterMap();
			params.setParameter(FILE_ID_PARAM_NAME, doc.getID());

			return URL.here(req, "/download/", params).toString();
		}
	}
	
	class FileEditForm extends Form implements FormValidationListener, FormProcessListener, FormInitListener {
		
		private final static String FILE_EDIT = "file-edit";
		private final static String FILE_EDIT_CATS = "file-edit-cats";
		private final static String FILE_EDIT_FNAME = "file-edit-name";
		private final static String FILE_EDIT_AUDIENCE = "file-edit-audience";
		private final static String FILE_EDIT_AUTHOR = "file-edit-author";
		private final static String FILE_EDIT_DESCRIPTION = "file-edit-description";
		private final static String FILE_EDIT_TITLE = "file-edit-title";
		
		private BrowseFileInfoPropertiesPane m_parent;

		private StringParameter m_FileAudience;
		private StringParameter m_FileName;
		private TrimmedStringParameter m_FileAuthor;
		private StringParameter m_FileDesc;
		private ArrayParameter m_FileCats;
		private StringParameter m_FileTitle;
		
		private FileUpload m_fileUpload;
		private final static String FILE_UPLOAD = "file-upload";
		private Submit m_submit;
		
		public FileEditForm(BrowseFileInfoPropertiesPane parent) {
			super(FILE_EDIT, new ColumnPanel(2));

			m_parent = parent;

			m_FileAudience = new StringParameter(FILE_EDIT_AUDIENCE);
			m_FileAuthor = new TrimmedStringParameter(FILE_EDIT_AUTHOR);
			m_FileDesc = new StringParameter(FILE_EDIT_DESCRIPTION);
			m_FileCats = new ArrayParameter(FILE_EDIT_CATS);

			add(new Label(FILE_NAME_REQUIRED));
			m_FileName = new StringParameter(FILE_EDIT_FNAME);
			TextField fnameEntry = new TextField(m_FileName);
			add(fnameEntry);

			add(new Label("Title"));//TODO
			m_FileTitle = new StringParameter(FILE_EDIT_TITLE);
			TextField fTitleEntry = new TextField(m_FileTitle);
			add(fTitleEntry);

			add(new Label(FILE_INTENDED_AUDIENCE));
			SingleSelect audienceEntry = new SingleSelect(m_FileAudience);
			try {
				audienceEntry.addPrintListener(new IntendedAudienceSelectPrinter());
			}
			catch (TooManyListenersException tmlex) {
				s_log.error(tmlex.getMessage());
				tmlex.printStackTrace();
			}
			add(audienceEntry);

			add(new Label(new AuthorLabelPrinter()));
			TextField authorEntry = new TextField(m_FileAuthor);
			authorEntry.addValidationListener(new StringLengthValidationListener(200));
			add(authorEntry);

			add(new Label(FILE_CATEGORIES));
			MultipleSelect catSelect = new MultipleSelect(FILE_EDIT_CATS);
			catSelect.setSize(20);
			try {
				BigDecimalParameter fileIDParam = null;
				fileIDParam = getFileIDParam();
				catSelect.addPrintListener(new CategoriesPrintListener(getContentSection(), fileIDParam));
			}
			catch (java.util.TooManyListenersException tmex) {
				throw new UncheckedWrapperException(tmex.getMessage());
			}
			add(catSelect);

			add(new Label(FILE_DESCRIPTION));
			TextArea descArea = new TextArea(m_FileDesc);
			descArea.setRows(10);
			descArea.setCols(40);
			add(descArea);

			m_submit = new Submit("file-edit-save");
			m_submit.setButtonLabel(FILE_SAVE);
			add(new Label()); // spacer

			SimpleContainer sc = new SimpleContainer();
			sc.add(m_submit);
			sc.add(new Submit(CANCEL));

			add(sc);

			addInitListener(this);
			addProcessListener(this);
			addValidationListener(this);
		}
		
	    /**
		 * Initializer to pre-fill name and description
		 */
		public void init(FormSectionEvent e) throws FormProcessException {
			PageState state = e.getPageState();

			FormData data = e.getFormData();

			BigDecimal id = (BigDecimal) state.getValue(docIDParam);
			Document doc = DMUtils.getFile(id);

			ArrayList assignedCats = new ArrayList();

			CategoryCollection cats = doc.getCategoryCollection();
			Category cat;
			while (cats.next()) {
			    cat = cats.getCategory();
			    String catID = cat.getID().toString();
			    assignedCats.add(catID);
			    s_log.debug("init: "+catID);
			}

			data.put(FILE_EDIT_FNAME, URLDecoder.decode(doc.getName()));
			data.put(FILE_EDIT_TITLE, doc.getTitle());
			data.put(FILE_EDIT_AUTHOR, doc.getAuthor());
			data.put(FILE_EDIT_DESCRIPTION, doc.getDescription());
			data.put(FILE_EDIT_CATS, assignedCats.toArray());
			initAudienceFormData(data, doc);
		}

		/**
		 * read form and update
		 */
		public void process(FormSectionEvent e) throws FormProcessException {
			PageState state = e.getPageState();
			if (m_submit.isSelected(state)) {
				processEdit(e);
			}

			displayPropertiesPane(state);
		}

		private void processEdit(FormSectionEvent e) throws FormProcessException {
			PageState state = e.getPageState();
			FormData data = e.getFormData();

			Document doc = DMUtils.getFile((BigDecimal) state.getValue(docIDParam));

			setDocumentAttributes(data, doc);
			doc.setCategories((String[]) data.get(FILE_EDIT_CATS));
			doc.setLastModifiedLocal(new java.util.Date());
			doc.save(); // creates a new revision

			setDocumentPermission(data, doc);
		}

		private void setDocumentAttributes(FormData data, Document doc) {
			String ftitle = (String) data.get(FILE_EDIT_TITLE);
			String fname = (String) data.get(FILE_EDIT_FNAME);
			// title must be set before name
			doc.setTitle((ftitle != null && ftitle.length() > 0) ? ftitle : fname);
			fname = fname.replace('"', ' ');
			doc.setName(URLEncoder.encode(fname));

			String fauthor = (String) data.get(FILE_EDIT_AUTHOR);
			String fdesc = (String) data.get(FILE_EDIT_DESCRIPTION);

			if (fauthor != null) {
				doc.setAuthor(fauthor);
			}
			doc.setDescription(fdesc);
		}

		protected String getUploadFileName(FormSectionEvent e) {
			FormData data = e.getFormData();
			String filename = (String) data.get(FILE_UPLOAD);
			return DMUtils.extractFileName(filename, e.getPageState());
		}

		/**
		 * Test if the new name already exists in the current folder
		 */

		public void validate(FormSectionEvent event) throws FormProcessException {
			PageState state = event.getPageState();
			if (m_submit.isSelected(state)) {
				validateEdit(event);
			}
		}

		private void validateEdit(FormSectionEvent event) throws FormProcessException {
			PageState state = event.getPageState();
			FormData data = event.getFormData();

			String fname = (String) data.get(FILE_EDIT_FNAME);
			if (fname == null || fname.trim().length() == 0) {
				data.addError(FILE_EDIT_FNAME, "This parameter is required");
			}

			String title = (String) data.get(FILE_EDIT_TITLE);
			if (title == null || title.length() == 0) {
				data.addError(FILE_EDIT_TITLE, "This parameter is required.");
			}
			else if (title.length() > 200) {
				data.addError(FILE_EDIT_TITLE, "This title is too long. It must be fewer than 200 characters.");
			}
		}

		/*
		 * Initialize form data for document's intended audience.
		 */
		private void initAudienceFormData(FormData data, Document doc) {
			ObjectPermissionCollection opc = PermissionService.getDirectGrantedPermissions(doc.getOID());
			long numPermissions = opc.size();
			if (numPermissions > 1) {
				s_log.error("there should only be 1 direct permission for " + "a document");
			}
			if (numPermissions == 0) {
				data.put(FILE_EDIT_AUDIENCE, "workspace");
				opc.close();
				return;
			}
			boolean isPublic = false;
			while (opc.next()) {
				if (opc.getGranteeID().intValue() == PermissionManager.VIRTUAL_PUBLIC_ID) {
					isPublic = true;
				}
			}
			opc.close();
			if (isPublic) {
				data.put(FILE_EDIT_AUDIENCE, "public");
			}
			else {
				data.put(FILE_EDIT_AUDIENCE, "internal");
			}
		}

		/*
		 * Grant/revoke direct permissions as needed. Note that workspace
		 * permissions is always implied.
		 */
		private void setDocumentPermission(FormData data, Document doc) throws FormProcessException {
			final String intendedAudience = (String) data.get(FILE_EDIT_AUDIENCE);
			if (intendedAudience == null) {
				throw new FormProcessException("Intended Audience cannot be null");
			}
			final PermissionDescriptor publicDescriptor = new PermissionDescriptor(PrivilegeDescriptor.READ, doc.getOID(), new OID(
					User.BASE_DATA_OBJECT_TYPE, PermissionManager.VIRTUAL_PUBLIC_ID));
			final PermissionDescriptor internalDescriptor = new PermissionDescriptor(PrivilegeDescriptor.READ, doc.getOID(), new OID(
					Group.BASE_DATA_OBJECT_TYPE, DocMgr.getConfig().getInternalGroupID()));
			new KernelExcursion() {
				protected void excurse() {
					//Party currentParty = Kernel.getContext().getParty();

					setParty(Kernel.getSystemParty());
					if ("public".equals(intendedAudience)) {
						PermissionService.grantPermission(publicDescriptor);
						PermissionService.revokePermission(internalDescriptor);
					}
					else if ("internal".equals(intendedAudience)) {
						PermissionService.revokePermission(publicDescriptor);
						PermissionService.grantPermission(internalDescriptor);
					}
					else {
						// workspace only
						PermissionService.revokePermission(publicDescriptor);
						PermissionService.revokePermission(internalDescriptor);
					}
				}
			}.run();
		}

		protected class AuthorLabelPrinter implements PrintListener {
			public AuthorLabelPrinter() {
				// Empty
			}

			public void prepare(PrintEvent e) {
				Label label = (Label) e.getTarget();
				PageState pageState = e.getPageState();

				String name = Web.getContext().getUser().getName();

				label.setLabel("Author: (if not " + name + ")");
			}
		}

		protected class IntendedAudienceSelectPrinter implements PrintListener {
			public IntendedAudienceSelectPrinter() {
			}

			public void prepare(PrintEvent e) {
				SingleSelect select = (SingleSelect) e.getTarget();

				select.addOption(new Option("workspace", Web.getContext().getApplication().getParentApplication().getDisplayName() + " members"));
				select.addOption(new Option("internal", new Label(FILE_INTENDED_AUDIENCE_INTERNAL)));
				select.addOption(new Option("public", new Label(FILE_INTENDED_AUDIENCE_PUBLIC)));
			}
		}
		
	    private ContentSection getContentSection() {
			ContentSectionCollection csl = ContentSection.getAllSections();
			csl.addEqualsFilter("label", DocMgr.getConfig().getContentSection());
			if (!csl.next()) {
				csl.close();
				return null;
			}
			ContentSection docsContentSection = csl.getContentSection();
			csl.close();
			return docsContentSection;
		}
	}
	
	class VersionUploadForm extends Form implements FormInitListener, FormProcessListener, FormValidationListener {
		
		private static final String VERSION_UPLOAD_FORM = "file-version";
		private static final String VERSION_TRANSACTION_DESCRIPTION = "file-transaction-description";
		private static final String VERSION_FILE_UPLOAD = "file-version-upload";
		
		private FileUpload m_fileUpload;
		private StringParameter m_versionDesc;
		private RequestLocal m_fileData;
		private Submit m_submit;
		
		//This  const allows for less than 4k bytes of 2byte unicode chars, plus
		// a little wiggle room...
		private int FOUR_K_CHAR_LIMIT = 1994;
		
		public VersionUploadForm() {
			super(VERSION_UPLOAD_FORM, new ColumnPanel(2));
			setMethod(Form.POST);
			setEncType("multipart/form-data");

			// initialize the file
			m_fileData = new RequestLocal() {
				protected Object initialValue(PageState state) {
					BigDecimal id = (BigDecimal) state.getValue(docIDParam);
					return new Document(id);
				}
			};

			m_fileUpload = new FileUpload(VERSION_FILE_UPLOAD);

			m_versionDesc = new StringParameter(VERSION_TRANSACTION_DESCRIPTION);

			add(new Label("Title"));//TODO
	        Label labelTitle = new Label();
	        labelTitle.addPrintListener(new PrintListener() {
				public void prepare(PrintEvent e) {
					PageState state = e.getPageState();
					Label t = (Label) e.getTarget();
					t.setLabel(getFile(state).getTitle());
				}
			});
			add(labelTitle);

			add(new Label(FILE_NAME));
	        Label label = new Label();
			label.addPrintListener(new PrintListener() {
				public void prepare(PrintEvent e) {
					PageState state = e.getPageState();
					Label t = (Label) e.getTarget();
					t.setLabel(URLDecoder.decode(getFile(state).getName()));
				}
			});
			add(label);

			add(new Label(FILE_SOURCE));
			add(m_fileUpload);

			add(new Label(FILE_VERSION_DESCRIPTION));
			TextArea fversionDesc = new TextArea(m_versionDesc);
			fversionDesc.setRows(10);
			fversionDesc.setCols(40);
			add(fversionDesc);

			m_submit = new Submit("file-version-save");
			m_submit.setButtonLabel(FILE_SAVE);
			add(new Label()); // spacer

			SimpleContainer sc = new SimpleContainer();
			sc.add(m_submit);
			sc.add(new Submit(CANCEL));

			add(sc, ColumnPanel.LEFT);

			addInitListener(this);
			addValidationListener(this);
			addProcessListener(this);
		}
		
	    private Document getFile(PageState s) {
			return (Document) m_fileData.get(s);
		}

		public void init(FormSectionEvent e) {
			PageState state = e.getPageState();
		}

		/**
		 * Receive uploaded file and reset file content, mime type, and
		 * description. Return to File properties screen.
		 */
		public void process(FormSectionEvent e) throws FormProcessException {

			PageState state = e.getPageState();
			
			if (m_submit.isSelected(state)) {
				FormData data = e.getFormData();
				MultipartHttpServletRequest req = (MultipartHttpServletRequest) state.getRequest();

				String fpath = (String) data.get(VERSION_FILE_UPLOAD);
				String fname = DMUtils.extractFileName(fpath, state);
				Document doc = getFile(state);
				java.io.File src = null;

				try {

					if (fpath != null && fpath.length() > 0) {
						src = req.getFile(VERSION_FILE_UPLOAD);
					}

					// Try to update the file in the database

					FileAsset fa = doc.getFile();
					fa.loadFromFile(fname, src, "txt");

				}
				catch (java.io.IOException ex) {
					ex.printStackTrace();
					throw new FormProcessException(ex.getMessage());
				}

				// Annotate transaction description
				String vdesc = (String) data.get(VERSION_TRANSACTION_DESCRIPTION);
				//If version description string is over 4K in size, truncate...
				if (vdesc.length() > FOUR_K_CHAR_LIMIT)
					vdesc = vdesc.substring(0, FOUR_K_CHAR_LIMIT);
				Versions.tag(doc.getOID(), vdesc);
				doc.setLastModifiedLocal(doc.getLastModifiedDate());
				doc.save();
			}

			displayPropertiesPane(state);
		}

		/**
		 * Validate the length of name of the new file being uploaded. Validate
		 * if user tries to upload a file with a different Mime type than the
		 * original. This is not supported.
		 *  
		 */
		public void validate(FormSectionEvent e) throws FormProcessException {
			PageState state = e.getPageState();
			if (!m_submit.isSelected(state)) {
				return;
			}
			FormData data = e.getFormData();
			String fpath = (String) data.get(VERSION_FILE_UPLOAD);
			if (fpath == null || fpath.length() == 0) {
				data.addError(VERSION_FILE_UPLOAD, "This parameter is required.");
			}

			String fname = DMUtils.extractFileName(fpath, state);

			int nameLength = 500;
			try {
				nameLength = fname.getBytes("UTF-8").length;
			}
			catch (UnsupportedEncodingException uee) {
				throw new RuntimeException("No UTF-8 support: " + uee);
			}
			// XXX Not localized as the other errors are.
			if (nameLength > 200) {
				data.addError(VERSION_FILE_UPLOAD, "This filename is too long. It must be fewer than 200 characters.");
			}
			
			String desc = (String) data.get(VERSION_TRANSACTION_DESCRIPTION);
            // trim it to really check it's not empty
            if (desc != null) {
                desc = desc.trim();
            }
			if (desc == null || desc.length() == 0) {
				data.addError(VERSION_TRANSACTION_DESCRIPTION, "This parameter is required.");
			}
			//Default to more than required so that it will fail.
			/*
			 * PageState state = e.getPageState(); FormData data =
			 * e.getFormData(); HttpServletRequest req = state.getRequest();
			 * 
			 * String uploadedFileName = (String) data.get(VERSION_FILE_UPLOAD);
			 * String newType = File.guessContentType(uploadedFileName,req);
			 * String oldType = getFile(state).getContentType();
			 * 
			 * if (!newType.equalsIgnoreCase(oldType)) {
			 * data.addError(VERSION_FILE_UPLOAD,
			 * DIFFERENT_MIMETYPE_ERROR.localize(req).toString()); }
			 */
		}
	}
	
	class FileSendColleaguePane extends SimpleContainer {
		
		private Component m_sendForm;
		private Component m_thankYou;

		public FileSendColleaguePane() {
			GridPanel spacer = new GridPanel(1);
			spacer.add(new Label("   "));
			add(spacer);

			m_sendForm = new SendForm(this);
			add(m_sendForm);

			m_thankYou = buildThankYou();
			add(m_thankYou);
		}
			
		public void initState(PageState ps) {
			s_log.debug("FileSendColleaguePane.initState");
			m_sendForm.setVisible(ps, true);
			m_thankYou.setVisible(ps, false);
		}
		
		private Component buildThankYou() {
			BoxPanel panel = new BoxPanel();

			panel.add(FILE_SEND_COLLEAGUE_THANKS);
			ActionLink backLink = new ActionLink(FILE_SEND_COLLEAGUE_THANKS_RETURN_LINK.localize().toString());
			backLink.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					PageState state = e.getPageState();
					displayPropertiesPane(state);
				}
			});
			panel.add(backLink);

			return panel;
		}
		
		public void displayThankYou(PageState ps) {
			s_log.debug("FileSendColleaguePane.displayThankYou");
			m_thankYou.setVisible(ps, true);
		}
	}
	
	class SendForm extends Form implements FormProcessListener {
		
		private static final String EMAIL_PARAM_NAME = "scEmail";
		
		private final TrimmedStringParameter m_emailParam = new TrimmedStringParameter(EMAIL_PARAM_NAME);
		private FileSendColleaguePane m_parent;
		
		public SendForm(FileSendColleaguePane parent) {
			super("sendEmail",new BoxPanel());
			
			m_parent = parent;
			
			GridPanel panel = new GridPanel(2);
			
			panel.add(FILE_SEND_COLLEAGUE_FORM_EMAIL);
			TextField emailField = new TextField(m_emailParam);
			emailField.addValidationListener(new EmailValidationListener());
			panel.add(emailField, GridPanel.LEFT);
			panel.add(new Submit(FILE_SEND_COLLEAGUE_SUBMIT));

			add(panel);

			addProcessListener(this);
		}

		public void process(FormSectionEvent e) throws FormProcessException {

			PageState state = e.getPageState();
			FormData data = e.getFormData();
			this.setVisible(state, false);
			m_parent.displayThankYou(state);

			// send the mail

			User u = Web.getContext().getUser();

			s_log.debug(FILE_SEND_COLLEAGUE_RETURN_ADDRESS.localize().toString());

			Mail mail = new Mail((String) data.get(EMAIL_PARAM_NAME), FILE_SEND_COLLEAGUE_RETURN_ADDRESS.localize().toString(),
					FILE_SEND_COLLEAGUE_SUBJECT.localize().toString(), FILE_SEND_COLLEAGUE_MESSAGE.localize().toString() + " " + u.getName());
			Document doc = new Document((BigDecimal) state.getValue(docIDParam));

			try {
				FileAsset fa = doc.getFile();
				if (fa == null) {
					return;
				}

				ByteArrayOutputStream assetStream = new ByteArrayOutputStream();
				long readBytes = fa.writeBytes(assetStream);
				if (readBytes == 0) {
					return;
				}

				String mimeTypeString = "application/octet-stream";
				MimeType mimeType = fa.getMimeType();
				if (mimeType != null) {
					mimeTypeString = mimeType.getMimeType();
				}
				s_log.debug("mimeTypeString: " + mimeTypeString);
				mail.attach(assetStream.toByteArray(), mimeTypeString, doc.getTitle());
				mail.send();
			}
			catch (java.io.IOException iox) {
				s_log.error("SendForm.process", iox);
//				iox.printStackTrace();
				throw new FormProcessException("An error occurred while trying to send document");
			}
			catch (javax.mail.MessagingException mex) {
				s_log.error("SendForm.process", mex);
				//mex.printStackTrace();
				throw new FormProcessException("An error occurred while trying to send document");
			}
		}
	}
	
	class FileActionPane extends ColumnPanel implements RequestListener {
		
		private ActionLink m_newVersion;
		private Link m_download;
		private ActionLink m_email;
		private ActionLink m_delete;
		private ActionLink m_editLink;
		private ActionLink m_fileRevisions;
		
		public FileActionPane() {
			super(1);
			
			add(new BackCancelActionLink("back"));

			m_editLink = new ActionLink(new Label(FILE_EDIT_LINK));
			m_editLink.setClassAttr("actionLink");
			m_editLink.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					PageState state = e.getPageState();
					displayEditForm(state);
				}
			});
			add(m_editLink);
			
			m_fileRevisions = new ActionLink("file revisions");
			m_fileRevisions.setClassAttr("actionLink");
			m_fileRevisions.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					PageState state = e.getPageState();
					m_parent.displayFileVersions(state, (BigDecimal) state.getValue(docIDParam));
				}
			});
			add(m_fileRevisions);
			
			m_newVersion = addActionLink(FILE_NEW_VERSION_LINK);

			PrintListener printListener = new PrintListener() {
				public void prepare(PrintEvent e) {
					Link l = (Link) e.getTarget();
					PageState state = e.getPageState();
					Document f = new Document((BigDecimal) state.getValue(docIDParam));
					l.setTarget("download/?" + FILE_ID_PARAM_NAME + "=" + f.getID());
				}
			};

			m_download = new Link(new Label(FILE_DOWNLOAD_LINK), printListener);
			m_download.setClassAttr("actionLink");
			add(m_download);

			m_email = addActionLink(FILE_SEND_COLLEAGUE_LINK);
			m_delete = addActionLink(FILE_DELETE_LINK);

			m_newVersion.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					displayUploadForm(e.getPageState());
				}
			});

			m_email.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					displaySendColleagueForm(e.getPageState());

				}
			});

			m_delete.addActionListener(new DeleteListener());
			m_delete.setConfirmation(FILE_DELETE_CONFIRM.localize().toString());
		}
		
		public void register(Page p) {
			super.register(p);
			p.addRequestListener(this);
		}
		
		private ActionLink addActionLink(GlobalizedMessage msg) {
			ActionLink ln = new ActionLink(new Label(msg));
			ln.setClassAttr("actionLink");
			this.add(ln);
			return ln;
		}

		public void pageRequested(RequestEvent event) {
			PageState state = event.getPageState();
			s_log.debug("pageRequested");

			if (!isVisible(state)) {
				// no point in hiding links
				return;
			}

			User user = Web.getContext().getUser();
			Application app = Web.getContext().getApplication();
//			Document doc = new Document((BigDecimal) state.getValue(docIDParam));
			if (PermissionService.checkPermission(new PermissionDescriptor(PrivilegeDescriptor.ADMIN, app, user))) {
				return;
			}

			if (!PermissionService.checkPermission(new PermissionDescriptor(PrivilegeDescriptor.EDIT, app, user))) {
				m_editLink.setVisible(state, false);
				m_delete.setVisible(state, false);
				m_newVersion.setVisible(state, false);
			}
		}
	}
	
    /**
	 * Delete Listener of a file.
	 */
	private final class DeleteListener implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			PageState state = e.getPageState();
			Document doc = new Document((BigDecimal) state.getValue(docIDParam));
			ContentBundle cb = (ContentBundle) doc.getParent();
			String parentFolderID = cb.getParent().getID().toString();
			cb.delete();

			try {
				String appURI = getRedirectURI(state);

				DispatcherHelper.sendRedirect(state.getRequest(), state.getResponse(), appURI + "?" + SEL_FOLDER_ID_PARAM.getName() + "="
						+ parentFolderID);
			}
			catch (IOException iox) {
				throw new UncheckedWrapperException(iox);
			}
		}

		private String getRedirectURI(PageState state) {
			String appURI = state.getRequestURI();
			s_log.debug("Original app URI: " + appURI);
			int idx = appURI.indexOf("/file/");
			if (idx > -1) {
				appURI = appURI.substring(0, idx);
			}

			final String servletPath = Web.getConfig().getDispatcherServletPath();
			if (appURI.startsWith(servletPath)) {
				appURI = appURI.substring(servletPath.length());
			}

			s_log.debug("New URI: " + appURI);
			return appURI;
		}
	}
}
