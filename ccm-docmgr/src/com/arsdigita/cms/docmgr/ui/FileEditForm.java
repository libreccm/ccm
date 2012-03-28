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

import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.Tree;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormValidationListener;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.form.FileUpload;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.SingleSelect;
import com.arsdigita.bebop.form.MultipleSelect;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.ArrayParameter;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.bebop.parameters.TrimmedStringParameter;
import com.arsdigita.categorization.Category;
import com.arsdigita.categorization.CategoryCollection;
import com.arsdigita.cms.ContentBundle;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.ContentSectionCollection;
import com.arsdigita.cms.FileAsset;
import com.arsdigita.cms.docmgr.Initializer;
import com.arsdigita.cms.docmgr.DocFolder;
import com.arsdigita.cms.docmgr.DocMgr;
import com.arsdigita.cms.docmgr.Document;
import com.arsdigita.dispatcher.ObjectNotFoundException;
import com.arsdigita.dispatcher.MultipartHttpServletRequest;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.kernel.Group;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.KernelExcursion;
import com.arsdigita.kernel.User;
import com.arsdigita.kernel.permissions.ObjectPermissionCollection;
import com.arsdigita.kernel.permissions.PermissionDescriptor;
import com.arsdigita.kernel.permissions.PermissionManager;
import com.arsdigita.kernel.permissions.PermissionService;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
import com.arsdigita.mimetypes.MimeType;
import com.arsdigita.persistence.OID;
import com.arsdigita.util.Assert;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.versioning.Versions;
import com.arsdigita.web.Web;

import javax.servlet.http.HttpServletRequest;

import java.math.BigDecimal;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TooManyListenersException;

/**
 * This component allows to change the file name and the
 * description of a file. It also serves to associate
 * keywords to a file (knowledge object).
 *
 *  @author Stefan Deusch
 *  @author Crag Wolfe
 */
class FileEditForm extends Form
    implements FormValidationListener,
               FormProcessListener,
               FormInitListener,
               DMConstants
{
    private final static org.apache.log4j.Logger s_log =
        org.apache.log4j.Logger.getLogger(FileEditForm.class);

    private final static String FILE_EDIT = "file-edit";
    private final static String FILE_EDIT_CATS = "file-edit-cats";
    private final static String FILE_EDIT_FNAME = "file-edit-name";
    private final static String FILE_EDIT_AUDIENCE = "file-edit-audience";
    private final static String FILE_EDIT_AUTHOR = "file-edit-author";
    private final static String FILE_EDIT_DESCRIPTION = "file-edit-description";
    private final static String FILE_EDIT_TITLE = "file-edit-title";

    private StringParameter m_FileAudience;
    private StringParameter m_FileName;
    private TrimmedStringParameter m_FileAuthor;
    private StringParameter m_FileDesc;
    private ArrayParameter m_FileCats;
    private StringParameter m_FileTitle;

    // m_parent is FileInfoPropertiesPane in the edit case,
    // and BrowsePane in the create case
    private Component m_parent;

    boolean m_creation = false;

    // only used for creation form
    private FileUpload m_fileUpload;
    private Tree m_tree;
    private final static String FILE_UPLOAD = "file-upload";
    private final static String FILE_UPLOAD_FORM = "file-upload-form";
    //private final static String FILE_UPLOAD_INPUT_DESCRIPTION = "file-description";
    
    private Submit m_submit;

    public FileEditForm(Component parent) {
        this(parent,false, null);
    }
    /**
     * Constructor
     */

    public FileEditForm(Component parent, 
                        boolean creation, Tree tree) {
        super(FILE_EDIT, new ColumnPanel(2));

        m_parent = parent;
        m_creation = creation;
        m_tree = tree;

        m_FileAudience = new StringParameter(FILE_EDIT_AUDIENCE);
        m_FileAuthor = new TrimmedStringParameter(FILE_EDIT_AUTHOR);
        m_FileDesc = new StringParameter(FILE_EDIT_DESCRIPTION);
        m_FileCats = new ArrayParameter(FILE_EDIT_CATS);
        
        if (m_creation) {
            setMethod(Form.POST);
            setEncType("multipart/form-data");

            add(new Label(FILE_UPLOAD_ADD_FILE));
            m_fileUpload = new FileUpload(FILE_UPLOAD);
            add(m_fileUpload);
        }
        else {
        	add(new Label(FILE_NAME_REQUIRED));
            m_FileName = new StringParameter(FILE_EDIT_FNAME);
            TextField fnameEntry = new TextField(m_FileName);
            add(fnameEntry);
        }
        
        add(new Label("Title"));//TODO
        m_FileTitle = new StringParameter(FILE_EDIT_TITLE);
        TextField fTitleEntry = new TextField(m_FileTitle);
        add(fTitleEntry);


        add(new Label(FILE_INTENDED_AUDIENCE));
        SingleSelect audienceEntry = new SingleSelect(m_FileAudience);
        try {
            audienceEntry.addPrintListener(new IntendedAudienceSelectPrinter());
        } catch (TooManyListenersException tmlex) {
            s_log.error(tmlex.getMessage());
            tmlex.printStackTrace();
        }
        add(audienceEntry);

        add(new Label(new AuthorLabelPrinter()));
        TextField authorEntry = new TextField(m_FileAuthor);
        add(authorEntry);

        add(new Label(FILE_CATEGORIES));
        MultipleSelect catSelect
            = new MultipleSelect(FILE_EDIT_CATS);
        catSelect.setSize(20);
        try {
            BigDecimalParameter fileIDParam = null;
            if (!m_creation) {
                fileIDParam = getFileIDParam();
            }
            catSelect.addPrintListener
                (new CategoriesPrintListener
                 (getContentSection(),
                  fileIDParam));
        } catch (java.util.TooManyListenersException tmex) {
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
    public void init(FormSectionEvent e)
        throws FormProcessException {

    	if (m_creation) {
    		initCreate(e);
        }
        else {
        	initEdit(e);
        }
    }
    public void initEdit(FormSectionEvent e)
        throws FormProcessException {

        PageState state = e.getPageState();
        
        FormData data = e.getFormData();
        
    	BigDecimal id = getSelectedDocID(state);
        Document doc = DMUtils.getFile(id);

        ArrayList assignedCats = new ArrayList();
        // Iterator i = doc.getCategories();
	CategoryCollection cats = doc.getCategoryCollection();
	Category cat;
	if (cats.next()) {
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

    private void initCreate(FormSectionEvent e) 
        throws FormProcessException {

        if (m_parent instanceof BrowsePane) {
            PageState state = e.getPageState();
            FormData data = e.getFormData();
            HttpServletRequest request = state.getRequest();
            ArrayParameter catIDParam = new ArrayParameter("catID");
            String[] catIDs = (String[]) catIDParam.transformValue(request);
            if (catIDs != null) {
                data.put(FILE_EDIT_CATS, catIDs);
            }
        }
    }

    /**
     * read form and update
     */
    public void process(FormSectionEvent e)
        throws FormProcessException {
        
    	Document doc = null;
        if (m_submit.isSelected(e.getPageState())) {
            if (m_creation) {
                doc = processCreate(e);
            } else {
                doc = processEdit(e);
            }
        }
        backCancel(e.getPageState(), (doc != null ? doc.getID() : null));
    }

    private Document processEdit(FormSectionEvent e)
        throws FormProcessException {
        PageState state = e.getPageState();
        FormData data = e.getFormData();

        Document doc = DMUtils.getFile(getSelectedDocID(state));

        setDocumentAttributes(data,doc);
        doc.setCategories((String[]) data.get(FILE_EDIT_CATS));
        doc.setLastModifiedLocal(new java.util.Date());
        doc.save(); // creates a new revision

        setDocumentPermission(data,doc);

        return doc;
    }

    private Document processCreate(FormSectionEvent e)
        throws FormProcessException {

        PageState state = e.getPageState();
        final FormData data = e.getFormData();
        final HttpServletRequest req = state.getRequest();

        final String fname = getUploadFileName(e);
        String titleTmp = (String) data.get(FILE_EDIT_TITLE);
        if (titleTmp == null || titleTmp.trim().length() == 0) {
        	titleTmp = fname;
        	MimeType mime = MimeType.guessMimeTypeFromFile(fname);
        	int index;
        	if (mime != null && (index = fname.lastIndexOf('.')) > -1) {
        		titleTmp = fname.substring(0, index);
        	}
        	else {
        		titleTmp = fname;
        	}
        }
        final String title = titleTmp;
        String fpath = (String) data.get(FILE_UPLOAD);

        java.io.File src = null;

        if (fpath != null && fpath.length() > 0) {
            HttpServletRequest mreq = e.getPageState().getRequest();

            Assert.isTrue(mreq instanceof MultipartHttpServletRequest,
                              "I got a " + mreq + " when I was " +
                              "expecting a MultipartHttpServletRequest");

            src = ((MultipartHttpServletRequest) mreq).getFile(FILE_UPLOAD);
        }

        if (s_log.isDebugEnabled()) {
            s_log.debug("file == '" + src + "'");
        }

        DocFolder p = null;
        String selKey = (String) m_tree.getSelectedKey(state);

        if (selKey == null) {
            p = DMUtils.getRootFolder(state);
        } else {
            BigDecimal folderID = new BigDecimal(selKey);
            try {
                p = new DocFolder(folderID);
            } catch(DataObjectNotFoundException nf) {
                throw new ObjectNotFoundException
                    ((String) FOLDER_PARENTNOTFOUND_ERROR.localize(req));
            }
        }
        final DocFolder parent = p;

        // insert the file in the data base below parent

        final Document f1 = new Document();

        // FR: define the bundle here
        //final ContentBundle bundle = new ContentBundle(ContentBundle.BASE_DATA_OBJECT_TYPE);
        f1.setTitle(title);
        f1.setName(URLEncoder.encode(fname));
        f1.setLanguage("en");
        final ContentBundle bundle = new ContentBundle(f1);

        final FileAsset fa = new FileAsset();
        
        new KernelExcursion() {
            	protected void excurse() {
            		// Create all the objects inside the kernel excursion
            		// so persistence doesn't barf...
            		setParty(Kernel.getSystemParty());
            		//f1.setTitle(title);
            		//f1.setName(URLEncoder.encode(fname));
            		//f1.setLanguage("en");
            		fa.setName("temp");
            		f1.setFile(fa);
            		bundle.setDefaultLanguage(f1.getLanguage());
            		//bundle.addInstance(f1);
            		bundle.setName(fname);
            		bundle.setParent(parent);
            		bundle.setContentSection(parent.getContentSection());
            		PermissionService.setContext(bundle,parent);
            		PermissionService.setContext(f1,bundle);
            		PermissionService.setContext(fa,f1);
            		bundle.save();
            		f1.save();
            	}}.run();
            	
        try {
            fa.loadFromFile(fname,src,"txt");
        } catch (java.io.IOException ex) {
            ex.printStackTrace();
            throw new FormProcessException(ex.getMessage());
        }
        
        Versions.tag(f1.getOID(),(FILE_UPLOAD_INITIAL_TRANSACTION_DESCRIPTION 
                                  .localize(req) 
                                  .toString()));
        
        setDocumentAttributes(data,f1);
        // title must be set before name
        f1.setTitle(title);
        f1.setName(URLEncoder.encode(fname));
        f1.setRepository(DocFolder.getRepository(p));
        
        f1.setLastModifiedLocal(f1.getLastModifiedDate());
        
        //f1.save();
        // context has been set, now add additional permissions
        setDocumentPermission(data,f1);
        
        f1.setCategories((String[]) data.get(FILE_EDIT_CATS));
        bundle.save();

            return f1;
    }

    private void setDocumentAttributes(FormData data,
                                       Document doc) {
    	
        
        if (!m_creation) {
            String ftitle = (String) data.get(FILE_EDIT_TITLE);
            String fname = (String) data.get(FILE_EDIT_FNAME);
            // title must be set before name
            doc.setTitle((ftitle != null && ftitle.length() > 0) ? ftitle : fname);
            doc.setName(URLEncoder.encode(fname));
        }

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

    public void validate(FormSectionEvent event)
        throws FormProcessException {

        if (m_submit.isSelected(event.getPageState())) {
            FormData data = event.getFormData();
            //validate length of author
            String author = (String) data.get(FILE_EDIT_AUTHOR);
            if (author != null && author.length() > 200) {
            	data.addError(FILE_EDIT_AUTHOR, "This parameter is too long. It must be fewer than 200 characters.");
            }
            //validate length of description
            String desc = (String) data.get(FILE_EDIT_DESCRIPTION);
            if (desc != null && desc.length() > 4000) {
                data.addError(FILE_EDIT_DESCRIPTION, "This parameter is too long. It must be fewer than 4000 characters.");
            }

            if (m_creation) {
                validateCreate(event);
            } else {
                validateEdit(event);
            }
        }
    }

    private void validateCreate(FormSectionEvent e)
        throws FormProcessException {
        PageState state = e.getPageState();
        FormData data = e.getFormData();
        HttpServletRequest req = state.getRequest();

        String fname = (String) data.get(FILE_UPLOAD);
        if (fname == null || fname.length() == 0) {
        	data.addError(FILE_UPLOAD, "This parameter is required.");
        }
        
        fname = DMUtils.extractFileName(getUploadFileName(e), state);

        // XXX Not localized as the other errors are.
        if (fname.length() > 200) {
            data.addError
                (FILE_UPLOAD,
                 "This filename is too long. It must be fewer than 200 characters.");
        }

        String title = (String) data.get(FILE_EDIT_TITLE);
        if (title != null && title.length() > 200) {
            data.addError(FILE_EDIT_TITLE, 
                    "This title is too long. It must be fewer than 200 characters.");
        }

        DocFolder parent = null;
        String selKey = (String) m_tree.getSelectedKey(state);

        if (selKey == null) {
            parent = DMUtils.getRootFolder(state);
        } else {
            BigDecimal folderID = new BigDecimal(selKey);
            try {
                parent = new DocFolder(folderID);
            } catch(DataObjectNotFoundException nf) {
                throw new ObjectNotFoundException(FOLDER_PARENTNOTFOUND_ERROR
                                                  .localize(req).toString());
            }
        }

        // Now we have the parent, make sure the user is allowed to create here
        parent.assertPrivilege(PrivilegeDescriptor.CREATE);
        
        try {
            parent.retrieveSubResource(fname);
            data.addError(FILE_UPLOAD,
                          RESOURCE_EXISTS_ERROR
                          .localize(req).toString());
        } catch(DataObjectNotFoundException nf) {
            // ok here
        }// catch(InvalidNameException ex) {
    }

    private void validateEdit(FormSectionEvent event)
        throws FormProcessException {
//        PageState state = event.getPageState();
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
            data.addError(FILE_EDIT_TITLE, 
                    "This title is too long. It must be fewer than 200 characters.");
        }
        
//        HttpServletRequest req = state.getRequest();

//        Document doc = DMUtils.getFile(getSelectedDocID(state));

//         // Construct a name with the optional extension

//         String name = doc.getName();

//         if (!doc.isValidNewName(name)) {
//             data.addError(FILE_EDIT_FNAME,
//                           "Not a valid new name for this file");
//         }

        // Verify that the new name does not correspond to an existing
        // resource (file or folder)

	// XXX we need this, but leaving it broken for now... --hbrock

	/*
        if (!name.equals(file.getName())) {
            try {
                Folder parent = (Folder) file.getParent();
                parent.getResourceID(name);
                data.addError(FILE_EDIT_FNAME,
                              (String)RESOURCE_EXISTS_ERROR.localize(req));
            } catch(DataObjectNotFoundException nfe) {
                // good, so we can rename it
            } catch (InvalidNameException ex) {
                data.addError(FILE_EDIT_FNAME,
                              ex.getMessage());
            }
        }
	*/
    }


    /* Initialize form data for document's intended audience.
     */
    private void initAudienceFormData(FormData data,
                                        Document doc) {
        ObjectPermissionCollection opc =
            PermissionService.getDirectGrantedPermissions(doc.getOID());
        long numPermissions = opc.size();
        if (numPermissions > 1) {
            s_log.error("there should only be 1 direct permission for "+
                        "a document");
        }
        if (numPermissions == 0) {
            data.put(FILE_EDIT_AUDIENCE,"workspace");
            opc.close();
            return;
        }
        boolean isPublic = false;
        while (opc.next()) {
            if (opc.getGranteeID().intValue() == 
                PermissionManager.VIRTUAL_PUBLIC_ID) {
                isPublic = true;
            }
        }
        opc.close();
        if (isPublic) {
            data.put(FILE_EDIT_AUDIENCE, "public");
        } else {
            data.put(FILE_EDIT_AUDIENCE, "internal");
        }
    }

    /* Grant/revoke direct permissions as needed.
       Note that workspace permissions is always implied.
    */
    private void setDocumentPermission(FormData data,
                                       Document doc) 
    throws FormProcessException {
        final String intendedAudience = (String) data.get(FILE_EDIT_AUDIENCE);
        if (intendedAudience == null) {
            throw new FormProcessException
                ("Intended Audience cannot be null");
        }
        final PermissionDescriptor publicDescriptor =
            new PermissionDescriptor
            (PrivilegeDescriptor.READ,
             doc.getOID(),
             new OID(User.BASE_DATA_OBJECT_TYPE, 
                     PermissionManager.VIRTUAL_PUBLIC_ID));
        final PermissionDescriptor internalDescriptor =
            new PermissionDescriptor
            (PrivilegeDescriptor.READ,
             doc.getOID(),
             new OID(Group.BASE_DATA_OBJECT_TYPE, 
                     DocMgr.getConfig().getInternalGroupID()));
        new KernelExcursion() {
            protected void excurse() {
                //Party currentParty = Kernel.getContext().getParty();

                
                setParty(Kernel.getSystemParty());
                if("public".equals(intendedAudience)) {
                    PermissionService.grantPermission(publicDescriptor);
                    PermissionService.revokePermission(internalDescriptor);
                } else if("internal".equals(intendedAudience)) {
                    PermissionService.revokePermission(publicDescriptor);
                    PermissionService.grantPermission(internalDescriptor);
                } else {
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

            label.setLabel("Author: (if not "+name+")");
        }
    }

    protected class IntendedAudienceSelectPrinter implements PrintListener {
        public IntendedAudienceSelectPrinter() {
        }

        public void prepare(PrintEvent e) {
            SingleSelect select = (SingleSelect) e.getTarget();
            
            select.addOption
                (new Option("workspace",
                            Web.getContext().getApplication()
                            .getParentApplication().getDisplayName()+
                            " members"));
            select.addOption
                (new Option("internal",
                            new Label(FILE_INTENDED_AUDIENCE_INTERNAL)));
            select.addOption
                (new Option("public",
                            new Label(FILE_INTENDED_AUDIENCE_PUBLIC)));
        }
    }

    private ContentSection getContentSection() {
        ContentSectionCollection csl = ContentSection.getAllSections();
        csl.addEqualsFilter("label",DocMgr.getConfig().getContentSection());
        if (!csl.next()) {
                csl.close(); return null;
        }
        ContentSection docsContentSection = csl.getContentSection();
        csl.close();
        return docsContentSection;
    }

    private BigDecimal getSelectedDocID(PageState state) {
        if (m_creation) {
            s_log.warn("doc create step shouldn't need selected id");
            return null;
        }
        return (BigDecimal) state.getValue(getFileIDParam());
    }
    
    private BigDecimalParameter getFileIDParam() {
    	if (m_parent instanceof FileInfoPropertiesPane) {
    		return ((FileInfoPropertiesPane) m_parent).getFileIDParam();
    	}
    	else {
    		if (m_parent instanceof BrowseFileInfoPropertiesPane) {
				((BrowseFileInfoPropertiesPane) m_parent).getFileIDParam();
			}
    	}
    	return null;
    }
    
    private void backCancel(PageState state, BigDecimal docID) {
    	if (m_parent instanceof BrowsePane) {
    		if (docID != null) {
        		((BrowsePane) m_parent).displayFilePropPanel(state, docID);
    		}
    		else {
    		((BrowsePane) m_parent).displayFolderContentPanel(state);
    		}
    	}
    	else {
    		if (m_parent instanceof FileInfoPropertiesPane) {
    			((FileInfoPropertiesPane) m_parent).displayPropertiesAndActions(state);
    		}
    		else {
    			if (m_parent instanceof BrowseFileInfoPropertiesPane) {
    				((BrowseFileInfoPropertiesPane) m_parent).displayPropertiesPane(state);
    			}
    		}
    	}
    }
}

