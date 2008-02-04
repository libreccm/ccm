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

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.arsdigita.bebop.ActionLink;
import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.Container;
import com.arsdigita.bebop.GridPanel;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.ModalContainer;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.bebop.SegmentedPanel;
import com.arsdigita.bebop.SimpleComponent;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.TabbedPane;
import com.arsdigita.bebop.Tree;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.event.ChangeEvent;
import com.arsdigita.bebop.event.ChangeListener;
import com.arsdigita.bebop.event.RequestEvent;
import com.arsdigita.bebop.event.RequestListener;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.ContentBundle;
import com.arsdigita.cms.docmgr.DocFolder;
import com.arsdigita.cms.docmgr.DocLink;
import com.arsdigita.cms.docmgr.Document;
import com.arsdigita.cms.docmgr.ui.tree.DocFolderAppCellRenderer;
import com.arsdigita.cms.docmgr.ui.tree.RepositoryTreeModel;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.kernel.User;
import com.arsdigita.kernel.permissions.PermissionDescriptor;
import com.arsdigita.kernel.permissions.PermissionService;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
import com.arsdigita.web.Application;
import com.arsdigita.web.Web;
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
class BrowsePane extends ModalContainer
    implements DMConstants, ChangeListener, RequestListener, ActionListener {
	
	private static final Logger s_log = Logger.getLogger(BrowsePane.class);

	public static final String PARAM_ADD_DOC_TO_FID = "add_doc_to_fid";
	public static final String PARAM_ADD_FOLDER_TO_FID = "add_folder_to_fid";

    private Component m_folderContent;
    private FolderContentsTableForm m_folderContentsTableForm;
    private Component m_destinationFolderPanel;
    private Component m_newFileForm;
    private Component m_newFolderForm;
    private Component m_newDocLinkForm;
    private Component m_editDocLinkForm;
    private Component m_errorMsgPanel;
    private Component m_fileInfoComponent;
    private Component m_folderEditComponent;
    private Component m_fileVersionsComponent;
    private BrowseFileInfoPropertiesPane m_fileInfoPane;
    private BrowseFileRevisionsTable m_fileVersionsTable;
    private DestinationFolderForm m_destinationFolderForm;
    private ErrorMessageLabel m_errorMsgLabel;
    private Container m_mainBrowseContainer;
    private CreateDocLinkPane m_docLinkPane;

    private ActionLink m_newFileLink;
    private ActionLink m_createDoclinkLink;
    private ActionLink m_newFolderLink;

    private TabbedPane m_mainTabPane;

    protected Tree m_tree;
    private RequestLocal m_createdFolderID = new RequestLocal();

    private BigDecimalParameter m_rootFolderIdParam = new BigDecimalParameter(ROOTFOLDER_ID_PARAM_NAME);
    private BigDecimalParameter m_selFolderIdParam  = new BigDecimalParameter(SEL_FOLDER_ID_PARAM_NAME);
    private BigDecimalParameter m_fileIdParam = new BigDecimalParameter(FILE_ID_PARAM_NAME);
    private StringParameter m_rootAddDocParam= new StringParameter(ROOT_ADD_DOC_PARAM_NAME);
    private BigDecimalParameter m_openFolderIdParam  = new BigDecimalParameter(OPEN_FOLDER_ID_PARAM_NAME);
    private BigDecimalParameter m_folderAddDocParam = new BigDecimalParameter(FOLDER_ADD_DOC_PARAM_NAME);

    /**
     * Default constructor
     */

    public BrowsePane() {

        m_mainTabPane = new TabbedPane();

        m_mainBrowseContainer = new BoxPanel();
        m_mainTabPane.addTab(WS_BROWSE_TITLE,
                             m_mainBrowseContainer);

        m_mainBrowseContainer.setClassAttr("sidebarNavPanel");
        //m_mainBrowseContainer.setAttribute("navbar-title", "Folders");

        BoxPanel leftSide = new BoxPanel();
        leftSide.setClassAttr("navbar");
        m_tree = new Tree(new RepositoryTreeModel());
        m_tree.setCellRenderer(new DocFolderAppCellRenderer());
        m_tree.addActionListener(this);
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

        m_newFileForm = makeFileUploadForm();
        add(m_newFileForm);

        m_newDocLinkForm = makeDocLinkCreateForm();
        add(m_newDocLinkForm);

        m_editDocLinkForm = makeDocLinkEditForm();
        add(m_editDocLinkForm);

        m_newFolderForm = makeFolderCreateForm();
        add(m_newFolderForm);

        m_destinationFolderPanel = makeExpandFolderPanel();
        add(m_destinationFolderPanel);

        m_errorMsgPanel = makeErrorMsgPanel();
        add(m_errorMsgPanel);
        
        m_fileInfoComponent = makeFilePropPanel();
        add(m_fileInfoComponent);
        
        m_folderEditComponent = makeFolderEditForm();
        add(m_folderEditComponent);
        
        m_fileVersionsComponent = makeFileVersionsPanel();
        add(m_fileVersionsComponent);
    }

    /**
     * Register the page the fist time
     */

    public void register(Page p) {
        p.addGlobalStateParam(m_rootFolderIdParam);
        p.addGlobalStateParam(m_selFolderIdParam);
        p.addGlobalStateParam(m_rootAddDocParam);
        p.addGlobalStateParam(m_fileIdParam);
        p.addGlobalStateParam(m_openFolderIdParam);
        p.addGlobalStateParam(m_folderAddDocParam);
        
        p.addRequestListener(this);

        super.register(p);
    }

    /**
     * Checks if a folder is selected in the page state and consequently
     * hides or shows the Folder Contents or Folder Action panels.
     */
    public void pageRequested(RequestEvent e) {
        PageState state = e.getPageState();

        BigDecimal fid = (BigDecimal) state.getValue(m_fileIdParam);

        boolean display = false;
        
        BigDecimal openFolder = (BigDecimal) state.getValue(m_openFolderIdParam);
        HttpServletRequest request = state.getRequest();

        BigDecimal addDocToFolderID = null;
        String addDocToFolderIDTxt = request.getParameter(PARAM_ADD_DOC_TO_FID);
        if (addDocToFolderIDTxt != null) {
        	try {
        		addDocToFolderID = new BigDecimal(addDocToFolderIDTxt);
        		//open folder for upload new file
        		if (openFolder == null) {
        			openFolder = addDocToFolderID;
        		}
        	}
        	catch (Throwable t) {
        		s_log.warn("url param:" + PARAM_ADD_DOC_TO_FID + " doesn't contain an integer (" + addDocToFolderIDTxt + ")");
        	}
        }
        
        BigDecimal addFolderToFolderID = null;
        String addFolderToFolderIDTxt = request.getParameter(PARAM_ADD_FOLDER_TO_FID);
        if (addFolderToFolderIDTxt != null) {
        	try {
        		addFolderToFolderID = new BigDecimal(addFolderToFolderIDTxt);
        		//open folder for create new folder
        		if (openFolder == null) {
        			openFolder = addFolderToFolderID;
        		}
        	}
        	catch (Throwable t) {
        		s_log.warn("url param:" + PARAM_ADD_FOLDER_TO_FID + " doesn't contain an integer (" + addFolderToFolderIDTxt + ")");
        	}
        }

        if (openFolder != null) {
        	m_tree.setSelectedKey(state, openFolder.toString());
            DocFolder parent = new DocFolder(openFolder);
            while (!parent.isRoot()) {
                parent = (DocFolder) parent.getParent();
                m_tree.expand(parent.getID().toString(), state);
            }
        }
        
        String key = (String) m_tree.getSelectedKey(state);

        // start out with root folder selected and open
        if (key == null) {
            key =  DMUtils.getRootFolder(state).getID().toString();
            m_tree.setSelectedKey(state, key);
            display = true;
        }

            // set creation links as not visible for non admins
            User user = Web.getContext().getUser();
            Application app = Web.getContext().getApplication();
        if (!PermissionService.checkPermission(new PermissionDescriptor(PrivilegeDescriptor.CREATE, app, user))) {
                m_newFileLink.setVisible(state, false);
                m_newFolderLink.setVisible(state, false);
                m_createDoclinkLink.setVisible(state, false);
            }

        // need this only when coming from 1-file page
        if (fid != null) {
            try {
                Document file = new Document(fid);

                DocFolder parent = (DocFolder) ((ContentBundle)file.getParent()).getParent();

                key = parent.getID().toString();

                while (!parent.isRoot()) {
                    parent = (DocFolder) parent.getParent();
                    m_tree.expand(parent.getID().toString(), state);
                }

                displayFilePropPanel(state, fid);
                display = false;
            } catch (DataObjectNotFoundException exc) {
                s_log.error("document not found", exc);
            }

            // to display this file's folder in the table
            m_tree.setSelectedKey(state, key);

            // now wipe out file param to avoid trouble elsewhere
            state.setValue(m_fileIdParam, null);
        }

        // need this only when coming from 1-docLink page
        String dlIDtxt = request.getParameter("dl_id");
        BigDecimal dlID = null;
        if (dlIDtxt != null) {
            try {
                dlID = new BigDecimal(dlIDtxt);
            }
            catch (Exception exc) {
                s_log.error("parse docLinkID", exc);
            }
        }
        if (dlID != null) {
            try {
                DocLink docLink = new DocLink(dlID);

                DocFolder parent = (DocFolder) ((ContentBundle) docLink.getParent()).getParent();

                key = parent.getID().toString();

                while (!parent.isRoot()) {
                    parent = (DocFolder) parent.getParent();
                    m_tree.expand(parent.getID().toString(), state);
                }
                
                displayDocLinkEditForm(state, dlID);
                display = false;
            } catch (DataObjectNotFoundException exc) {
                s_log.error("docLink not found", exc);
            }

            // to display this file's folder in the table
            m_tree.setSelectedKey(state, key);

            // now wipe out file param to avoid trouble elsewhere
            state.setValue(m_fileIdParam, null);
        }

        // finally expand selected folder
        m_tree.expand(key, state);

        if (display) {
            if( "t".equalsIgnoreCase(((String)state.getValue(m_rootAddDocParam)))) {
                // Entry hook to display FileUpload Form for Root folder
                displayFileUpload(state);
            }
            else if (request.getParameter(PARAM_ROOT_ADD_DOC_LINK) != null) {
            	// display create doc link pane
         	   state.setValue(m_docLinkPane.getEditDoclinkIDParam(), null);
               displayLinkCreate(state);
            }
            else {
                BigDecimal folderID = (BigDecimal) state.getValue(m_folderAddDocParam);
                if (folderID != null) {
                    m_tree.setSelectedKey(state, folderID.toString());
                    // Entry hook to display FileUpload Form for folder
                    displayFileUpload(state);
                }
                else {
                displayFolderContentPanel(state);
            }
            }
        }
        else {
        	//diplay FileUpload Form for specified folder
        	if (addDocToFolderID != null) {
        		displayFileUpload(state);
        	}
        	//diplay FolderCreate Form for specified folder
        	else if (addFolderToFolderID != null) {
        		displayFolderCreate(state);
        	}
        }
    }

    public void generateXML(PageState state, Element parent) {
    	super.generateXML(state, parent);
    	Object createdFolderID = m_createdFolderID.get(state);
    	if (createdFolderID != null) {
    		parent.addAttribute("createdFolderID", createdFolderID.toString());
    	}
    }
    
    public void setCreatedFolderID(PageState state, BigDecimal id) {
    	m_createdFolderID.set(state, id);
    }

    /**
     * Helper method to communicate selected folder ID to subcomponents.
     * Return onle non-null after tree has been displayed at least once.
     */

    public BigDecimal getFolderID(PageState state) {
        return new BigDecimal((String) m_tree.getSelectedKey(state));
    }

    /**
     * Implementation of the change listener, clicking on the folder
     * loads the directory on the right side.
     */
    public void stateChanged(ChangeEvent e) {
        PageState state = e.getPageState();

        // Display folder on the right side corresponding to the key
        displayFolderContentPanel(state);
    }
    
    public void actionPerformed(ActionEvent e) {
        PageState state = e.getPageState();

        // clear paging
        state.setValue(m_folderContentsTableForm.getPageNoParameter(), new Integer(0));
        
        // Display folder on the right side corresponding to the key
        displayFolderContentPanel(state);
    }

    /**
     * Build a panel to display the Folder content of the selected Folder
     * and add it as a segment to the passed in Segmented Panel.
     */

    private Component makeContentPanel(SegmentedPanel main) {

        Label folder_info_header = new Label
            (new GlobalizedMessage("ui.folder.content.header", BUNDLE_NAME));
        folder_info_header.addPrintListener(
                                            new FolderNamePrintListener(m_tree));

        m_newFileLink = new ActionLink(new Label(FOLDER_NEW_FILE_LINK));
        m_newFileLink.setClassAttr("actionLink");
        
        m_createDoclinkLink = new ActionLink(new Label(FOLDER_NEW_DOCLINK_LINK));
        m_createDoclinkLink.setClassAttr("actionLink");
        
        m_newFolderLink =
            new ActionLink(new Label(FOLDER_NEW_FOLDER_LINK));
        m_newFolderLink.setClassAttr("actionLink");

        m_newFileLink.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    displayFileUpload(e.getPageState());
                }
            });

        m_createDoclinkLink.addActionListener(new ActionListener() {
               public void actionPerformed(ActionEvent e) {
            	   PageState state = e.getPageState();
            	   state.setValue(m_docLinkPane.getEditDoclinkIDParam(), null);
                   displayLinkCreate(state);
               }
        });

        m_newFolderLink.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    displayFolderCreate(e.getPageState());
                }
            });

        GridPanel folderGrid = new GridPanel(1);
        SimpleContainer pane = new SimpleContainer();
        pane.add(m_newFileLink);
        pane.add(new Label("        "));
        pane.add(m_createDoclinkLink);
        pane.add(new Label("        "));
        pane.add(m_newFolderLink);
        folderGrid.add(pane, GridPanel.RIGHT | GridPanel.BOTTOM);
        m_folderContentsTableForm = new FolderContentsTableForm(this, m_tree);
        folderGrid.add(m_folderContentsTableForm,
                       GridPanel.LEFT | GridPanel.BOTTOM);

        return main.addSegment(folder_info_header,
                               folderGrid);
    }

    /**
     * Build File upload form
     */
    private Component makeFileUploadForm() {
        Label fileUploadFormHeaderLabel = new Label
            (new GlobalizedMessage("ui.file.upload.header", BUNDLE_NAME));
        fileUploadFormHeaderLabel.addPrintListener
            (new FolderNamePrintListener(m_tree));
        
    	TabbedPane tabbedPane = new TabbedPane();
    	
    	BoxPanel browseContainer = new BoxPanel();
    	tabbedPane.addTab(WS_BROWSE_TITLE, browseContainer);
    	browseContainer.setClassAttr("sidebarNavPanel");
    	
    	BoxPanel leftSide = new BoxPanel();
    	leftSide.setClassAttr("navbar");
    	leftSide.add(m_tree);
    	
    	browseContainer.add(leftSide);
    	
    	SegmentedPanel rightSide = new SegmentedPanel();
    	rightSide.setClassAttr("main");
    	
    	rightSide.addSegment(fileUploadFormHeaderLabel, new FileEditForm(this, true, m_tree));
    	browseContainer.add(rightSide);
    	
    	return tabbedPane;
    }

    /**
     * Build Folder create form
     */
    private Component makeFolderCreateForm() {
        Label folderCreateFormHeaderLabel = new Label
            (new GlobalizedMessage("ui.folder.create.header", BUNDLE_NAME));
        folderCreateFormHeaderLabel.addPrintListener(
                                                     new FolderNamePrintListener(m_tree));

    	TabbedPane tabbedPane = new TabbedPane();
    	
    	BoxPanel browseContainer = new BoxPanel();
    	tabbedPane.addTab(WS_BROWSE_TITLE, browseContainer);
    	browseContainer.setClassAttr("sidebarNavPanel");
    	
    	BoxPanel leftSide = new BoxPanel();
    	leftSide.setClassAttr("navbar");
    	leftSide.add(m_tree);
    	
    	browseContainer.add(leftSide);
    	
    	SegmentedPanel rightSide = new SegmentedPanel();
    	rightSide.setClassAttr("main");
    	
    	rightSide.addSegment(folderCreateFormHeaderLabel, new FolderCreateForm(this, m_tree));
    	browseContainer.add(rightSide);
    	
    	return tabbedPane;
    }

   private Component makeDocLinkCreateForm(){
     TabbedPane docLinkTabPane = new TabbedPane();
     
     GridPanel gridPanel = new GridPanel(1);
     Label docLinkCreateFormHeaderLabel = new Label(new GlobalizedMessage("ui.link.create.header", BUNDLE_NAME));
     docLinkCreateFormHeaderLabel.addPrintListener(
                                                  new FolderNamePrintListener(m_tree));
     gridPanel.add(docLinkCreateFormHeaderLabel);
     m_docLinkPane = new CreateDocLinkPane(this, m_tree);
     gridPanel.add(m_docLinkPane);
     
     BoxPanel browseContainer = new BoxPanel();
     docLinkTabPane.addTab(WS_BROWSE_TITLE, browseContainer);
     browseContainer.setClassAttr("sidebarNavPanel");
     
     BoxPanel leftSide = new BoxPanel();
     leftSide.setClassAttr("navbar");
     leftSide.add(m_tree);
     
     browseContainer.add(leftSide);
     
     SegmentedPanel rightSide = new SegmentedPanel();
     rightSide.setClassAttr("main");
     
     rightSide.addSegment(new Label("Doc Link"), gridPanel);
     browseContainer.add(rightSide);
     return docLinkTabPane;
   }

   private Component makeDocLinkEditForm(){
     TabbedPane docLinkTabPane = new TabbedPane();
     
     GridPanel gridPanel = new GridPanel(1);
     Label docLinkCreateFormHeaderLabel = new Label("Edit Link in");
     docLinkCreateFormHeaderLabel.addPrintListener(
                                                  new FolderNamePrintListener(m_tree));
     gridPanel.add(docLinkCreateFormHeaderLabel);
     m_docLinkPane = new CreateDocLinkPane(this, m_tree);
     gridPanel.add(m_docLinkPane);
     
     BoxPanel browseContainer = new BoxPanel();
     docLinkTabPane.addTab(WS_BROWSE_TITLE, browseContainer);
     browseContainer.setClassAttr("sidebarNavPanel");
     
     BoxPanel leftSide = new BoxPanel();
     leftSide.setClassAttr("navbar");
     leftSide.add(m_tree);
     
     browseContainer.add(leftSide);
     
     SegmentedPanel rightSide = new SegmentedPanel();
     rightSide.setClassAttr("main");
     
     rightSide.addSegment(new Label("Edit New Link"), gridPanel);
     browseContainer.add(rightSide);
     return docLinkTabPane;
   }

    /**
     * Build a destination folders tree.
     */
    private Component makeExpandFolderPanel() {
//        GridPanel gridPanel = new GridPanel(1);
//        gridPanel.add(DESTINATION_FOLDER_PANEL_HEADER);
        m_destinationFolderForm = new DestinationFolderForm(this);
//        gridPanel.add(m_destinationFolderForm);
//        return gridPanel;
    	TabbedPane tabbedPane = new TabbedPane();
    	
    	BoxPanel browseContainer = new BoxPanel();
    	tabbedPane.addTab(WS_BROWSE_TITLE, browseContainer);
    	browseContainer.setClassAttr("sidebarNavPanel");
    	
    	BoxPanel leftSide = new BoxPanel();
    	leftSide.setClassAttr("navbar");
    	leftSide.add(m_tree);
    	
    	browseContainer.add(leftSide);
    	
    	SegmentedPanel rightSide = new SegmentedPanel();
    	rightSide.setClassAttr("main");
    	
    	rightSide.addSegment(DESTINATION_FOLDER_PANEL_HEADER, m_destinationFolderForm);
    	browseContainer.add(rightSide);
    	
    	return tabbedPane;
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

    /*
     * Build panel to display file properies.
     */
    private Component makeFilePropPanel() {
        m_fileInfoPane = new BrowseFileInfoPropertiesPane(this);
    	
    	TabbedPane filePropTabPane = new TabbedPane();
    	
    	BoxPanel filePropBrowseContainer = new BoxPanel();
    	filePropTabPane.addTab(WS_BROWSE_TITLE, filePropBrowseContainer);
    	filePropBrowseContainer.setClassAttr("sidebarNavPanel");
    	
    	BoxPanel leftSide = new BoxPanel();
    	leftSide.setClassAttr("navbar");
    	leftSide.add(m_tree);
    	
    	filePropBrowseContainer.add(leftSide);
    	
    	SegmentedPanel rightSide = new SegmentedPanel();
    	rightSide.setClassAttr("main");
    	
    	rightSide.addSegment(new Label("Document"), m_fileInfoPane);
    	filePropBrowseContainer.add(rightSide);
    	
    	return filePropTabPane;
    }
    
    private Component makeFolderEditForm() {
    	TabbedPane tabbedPane = new TabbedPane();
    	
    	BoxPanel browseContainer = new BoxPanel();
    	tabbedPane.addTab(WS_BROWSE_TITLE, browseContainer);
    	browseContainer.setClassAttr("sidebarNavPanel");
    	
    	BoxPanel leftSide = new BoxPanel();
    	leftSide.setClassAttr("navbar");
    	leftSide.add(m_tree);
    	
    	browseContainer.add(leftSide);
    	
    	SegmentedPanel rightSide = new SegmentedPanel();
    	rightSide.setClassAttr("main");
    	
    	rightSide.addSegment(new Label("Folder edit"), new BrowseFolderEdit(this, m_selFolderIdParam));
    	browseContainer.add(rightSide);
    	
    	return tabbedPane;
    }
    
    private Component makeFileVersionsPanel() {
    	m_fileVersionsTable = new BrowseFileRevisionsTable(this);
    	
    	TabbedPane tabbedPane = new TabbedPane();
    	
    	BoxPanel browseContainer = new BoxPanel();
    	tabbedPane.addTab(WS_BROWSE_TITLE, browseContainer);
    	browseContainer.setClassAttr("sidebarNavPanel");
    	
    	BoxPanel leftSide = new BoxPanel();
    	leftSide.setClassAttr("navbar");
    	leftSide.add(m_tree);
    	
    	browseContainer.add(leftSide);
    	
    	SegmentedPanel rightSide = new SegmentedPanel();
    	rightSide.setClassAttr("main");
    	
    	rightSide.addSegment(new Label("File revisions"), m_fileVersionsTable);
    	browseContainer.add(rightSide);
    	
    	return tabbedPane;
    }
    
    public void displayFileUpload(PageState state) {
        setVisibleComponent(state, m_newFileForm);
    }

    public void displayFolderCreate(PageState state) {
        setVisibleComponent(state, m_newFolderForm);
    }

    public void displayLinkCreate(PageState state){
        setVisibleComponent(state,m_newDocLinkForm);
    }

    public void displayLinkEdit(PageState state){
        setVisibleComponent(state,m_editDocLinkForm);
    }

    public void displayFolderContentPanel(PageState state) {
        setVisibleComponent(state, m_mainTabPane);
    }

    public void displayDestinationFolderPanel(PageState state,
                                              Object[] resourceList,
                                              boolean isMove) {

        setVisibleComponent(state, m_destinationFolderPanel);
        m_destinationFolderForm.setResourceList(state, resourceList);
        if (isMove) {
            m_destinationFolderForm.setMove(state);
        } else {
            m_destinationFolderForm.setCopy(state);
        }
    }

    public void displayErrorMsgPanel(PageState state,
                                     String action,
                                     ArrayList list) {

        m_errorMsgLabel.setMessages(state, action, list);

        setVisibleComponent(state, m_errorMsgPanel);
    }

    public void displayFilePropPanel(PageState state, BigDecimal docID) {
    	if (s_log.isDebugEnabled()) {
    		s_log.debug("displayFilePropPanel, docID="+docID.toString());
    	}
	
		m_fileInfoPane.setDocID(state, docID);
		m_fileInfoPane.displayPropertiesPane(state);
        setVisibleComponent(state, m_fileInfoComponent);
    }
    
    public void displayFilePropEditForm(PageState state, BigDecimal docID) {
    	if (s_log.isDebugEnabled()) {
    		s_log.debug("displayFilePropEditForm, docID="+docID.toString());
    	}
	
		m_fileInfoPane.setDocID(state, docID);
		m_fileInfoPane.displayEditForm(state);
        setVisibleComponent(state, m_fileInfoComponent);
    }
    
    public void displayDocLinkEditForm(PageState state, BigDecimal doclinkID) {
    	if (s_log.isDebugEnabled()) {
    		s_log.debug("displayDocLinkEditForm, doclinkID="+doclinkID.toString());
    	}
    	state.setValue(m_docLinkPane.getEditDoclinkIDParam(), doclinkID);
        displayLinkEdit(state);
    }
    
    public void displayFilePropSendColleagueForm(PageState state, BigDecimal docID) {
    	if (s_log.isDebugEnabled()) {
    		s_log.debug("displayFilePropSendColleagueForm, docID="+docID.toString());
    	}
	
		m_fileInfoPane.setDocID(state, docID);
		m_fileInfoPane.displaySendColleagueForm(state);
        setVisibleComponent(state, m_fileInfoComponent);
    }
    
    public void displayFolderEditForm(PageState state, BigDecimal folderID) {
    	state.setValue(m_selFolderIdParam, folderID);
        setVisibleComponent(state, m_folderEditComponent);
    }
    
    public void displayFileVersions(PageState state, BigDecimal docID) {
    	if (s_log.isDebugEnabled()) {
    		s_log.debug("displayFileVersions, docID="+docID);
    	}
    	
        m_fileVersionsTable.setDocID(state, docID);
        setVisibleComponent(state, m_fileVersionsComponent);
    }
    
    public BigDecimalParameter getFileIDparam() {
    	return m_fileIdParam;
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
}
