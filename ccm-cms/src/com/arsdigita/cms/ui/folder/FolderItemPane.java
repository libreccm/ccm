/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
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

import com.arsdigita.bebop.*;
import com.arsdigita.bebop.event.*;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.cms.CMS;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.Folder;
import com.arsdigita.cms.SecurityManager;
import com.arsdigita.cms.dispatcher.Utilities;
import com.arsdigita.cms.ui.authoring.CreationSelector;
import com.arsdigita.cms.ui.authoring.NewItemForm;
import com.arsdigita.cms.ui.permissions.CMSPermissionsPane;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.kernel.ACSObject;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.User;
import com.arsdigita.kernel.permissions.ObjectPermissionCollection;
import com.arsdigita.kernel.permissions.PermissionDescriptor;
import com.arsdigita.kernel.permissions.PermissionService;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.toolbox.ui.ActionGroup;
import com.arsdigita.util.Assert;

import java.math.BigDecimal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Encapsulates a {@link FolderManipulator} in order to create a flat
 * item listing. Also contains a new item form.
 *
 * @author <a href="mailto:sfreidin@arsdigita.com">Stanislav Freidin</a>
 * @version $Revision: #6 $ $DateTime: 2004/08/17 23:15:09 $
 * @version Id: FolderItemPane.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class FolderItemPane extends SegmentedPanel
        implements FormProcessListener, ChangeListener, FormSubmissionListener,
                   Resettable, ActionListener {

    private static final String CONTENT_TYPE_ID = "ct";
    private static final String CMS_PRIVILEGES = "com.arsdigita.cms.getPrivileges";
    private static final String PRIVILEGE = "privilege";
    private static final String PRIVILEGE_NAME = "prettyName";

    //maximum number of items displayed in the table at a time
    public static int LIST_SIZE = 20;

    private NewItemForm m_newItem;
    private SingleSelectionModel m_typeSel;
    private FolderSelectionModel m_folderSel;
    private CreationSelector m_selector;
    private FolderManipulator m_folderManip;

    private FolderCreator m_folderCreator;
    private ActionLink m_createFolderAction;
    private ActionLink m_togglePrivateAction;

    private Segment m_browseSeg;
    private Segment m_newItemSeg;
    private Segment m_newFolderSeg;
    private Segment m_editFolderSeg;
    private Segment m_permissionsSeg;
    private CMSPermissionsPane m_permPane;

    // Folder edit/rename functionality.
    private ActionLink m_editFolderAction;
    private FolderEditor m_folderEditor;


    private Label m_contentLabel;
    private Label m_itemPath;
    private Label m_chooseLabel;

    /**
     * Create a new item listing pane. The listing creates its own {@link
     * FolderSelectionModel} to keep track of the currently selected
     * folder. This requires that this list is used within a content
     * section. The section's root folder is used as the starting point for
     * browsing.
     */
    public FolderItemPane() {
        this(new FolderSelectionModel("folder"));
    }

    /**
     * Construct a new item listing pane. The provided folder selection model
     * is used to keep track of the currently displayed folder.
     *
     * @param folderSel maintains the currently displayed folder.
     */
    public FolderItemPane(FolderSelectionModel folderSel) {
        super();
        setIdAttr("flat-item-list");

        m_folderSel = folderSel;

        m_newItemSeg = addSegment();
        m_newItemSeg.setIdAttr("folder-new-item");

        m_newFolderSeg = addSegment();
        m_newFolderSeg.setIdAttr("folder-new-folder");

        m_editFolderSeg = addSegment();
        m_editFolderSeg.setIdAttr("folder-edit-folder");

        m_browseSeg = addSegment();
        m_browseSeg.setIdAttr("folder-browse");

        final ActionGroup browseActions = new ActionGroup();
        m_browseSeg.add(browseActions);

        // The top 'browse' segment
        m_contentLabel = new Label(globalize("cms.ui.contents_of"), false);
        m_browseSeg.addHeader(m_contentLabel);
        m_chooseLabel = new Label(globalize("cms.ui.choose_target_folder"), false);
        m_browseSeg.addHeader(m_chooseLabel);
        m_itemPath = new Label(new PrintListener() {
                public final void prepare(final PrintEvent e) {
                    final Folder f = (Folder) m_folderSel.getSelectedObject
                        (e.getPageState());
                    ((Label) e.getTarget()).setLabel(f.getDisplayName());
                }
            });

        m_browseSeg.addHeader(m_itemPath);
        m_folderManip = new FolderManipulator(m_folderSel);
        m_folderManip.getItemView().addProcessListener(this);
        m_folderManip.getTargetSelector().addProcessListener(this);
        m_folderManip.getTargetSelector().addSubmissionListener(this);

        browseActions.setSubject(m_folderManip);

        // The actions

        m_createFolderAction = new ActionLink(new Label(globalize("cms.ui.new_folder")));
        m_createFolderAction.addActionListener(this);
        browseActions.addAction(m_createFolderAction);

        m_editFolderAction = new ActionLink(new Label(globalize("cms.ui.edit_folder")));
        m_editFolderAction.addActionListener(this);
        browseActions.addAction(m_editFolderAction);

        m_newItem = new SectionNewItemForm("newItem");
        m_newItem.addProcessListener(this);
        browseActions.addAction(m_newItem);

        m_permissionsSeg = addSegment();
        m_permissionsSeg.setIdAttr("folder-permissions");

        final ActionGroup permActions = new ActionGroup();
        m_permissionsSeg.add(permActions);

        // The permissions segment
        m_permissionsSeg.addHeader(new Label(GlobalizationUtil.globalize("cms.ui.permissions")));

        List privs = new ArrayList();
        Map privNameMap = new HashMap();
        DataQuery query = SessionManager.getSession().retrieveQuery(CMS_PRIVILEGES);
        query.addFilter("scope != 'section'");
        query.addOrder("sortOrder");
        while (query.next()) {
            String privilege = (String) query.get(PRIVILEGE);
            String privilegeName = (String) query.get(PRIVILEGE_NAME);
            privs.add(PrivilegeDescriptor.get(privilege));
            privNameMap.put(privilege, privilegeName);
        }
        query.close();

        m_permPane = new CMSPermissionsPane
            ((PrivilegeDescriptor[]) privs.toArray(new PrivilegeDescriptor[privs.size()]),
             privNameMap, m_folderSel);
        permActions.setSubject(m_permPane);

        // An action

        m_togglePrivateAction = new ActionLink(new Label(new PrintListener() {
                public void prepare(PrintEvent e) {
                    PageState state = e.getPageState();
                    Label target = (Label) e.getTarget();
                    Folder currentFolder = (Folder) m_folderSel.getSelectedObject(state);
                    // ACSObject parent = currentFolder.getParent();
                    DataObject context = PermissionService.getContext(currentFolder);
                    if (context == null) {
                        target.setLabel( (String) GlobalizationUtil.globalize("cms.ui.restore_default_permissions").localize());
                    } else {
                        target.setLabel( (String) GlobalizationUtil.globalize("cms.ui.use_custom_permissions").localize());
                    }
                }
            }));
        m_togglePrivateAction.addActionListener(this);
        permActions.addAction(m_togglePrivateAction);

        // The 'new item' segment
        m_newItemSeg.addHeader(new Label(globalize("cms.ui.new_item")));
        m_typeSel = new ParameterSingleSelectionModel(new BigDecimalParameter(CONTENT_TYPE_ID));
        m_typeSel.addChangeListener(this);

        m_selector = new CreationSelector(m_typeSel, m_folderSel);
        m_newItemSeg.add(m_selector);
        m_newItemSeg.add(new Label("<br/>", false));

        // The 'new folder' segment
        m_newFolderSeg.addHeader(new Label(globalize("cms.ui.new_folder")));
        Form folderCreate = new Form("fcreat");
        m_folderCreator = new FolderCreator("fcreat", m_folderSel);
        m_folderCreator.addSubmissionListener(this);
        m_folderCreator.addProcessListener(this);
        folderCreate.add(m_folderCreator);
        m_newFolderSeg.add(folderCreate);
        m_newFolderSeg.add(new Label("<br/>", false));

        m_editFolderSeg.addHeader(new Label(globalize("cms.ui.edit_folder")));
        m_folderEditor = new FolderEditor("fedit", m_folderSel);
        m_folderEditor.addSubmissionListener(this);
        m_folderEditor.addProcessListener(this);

        Form folderEditorForm = new Form( "fedit_form" );
        folderEditorForm.add( m_folderEditor );
        m_editFolderSeg.add(folderEditorForm);
        m_editFolderSeg.add(new Label("<br/>", false));
    }

    public void register(Page p) {
        super.register(p);

        p.setVisibleDefault(m_chooseLabel, false);
        p.setVisibleDefault(m_newItemSeg, false);
        p.setVisibleDefault(m_newFolderSeg, false);
        p.setVisibleDefault(m_editFolderSeg, false);

        p.addComponentStateParam(this, m_typeSel.getStateParameter());
        p.addComponentStateParam(this, m_folderSel.getStateParameter());

        p.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    if (!event.getPageState().isVisibleOnPage(FolderItemPane.this)) {
                        return;
                    }

                    showHideSegments(event.getPageState());
                }
            });
    }

    /**
     * Show/hide segments based on access checks.
     *
     * @param state The page state
     * @pre ( state != null )
     */
    private void showHideSegments(PageState state) {
        SecurityManager sm = Utilities.getSecurityManager(state);
        Folder folder = (Folder) m_folderSel.getSelectedObject(state);
        Assert.exists(folder);

        // MP: This should be checked on the current folder instead of just
        //     the content section.
        boolean newItem =
            sm.canAccess(state.getRequest(),
                         SecurityManager.NEW_ITEM,
                         folder);

        if (!newItem) {
            browseMode(state);
        }
        m_createFolderAction.setVisible(state, newItem);
        m_newItem.setVisible(state, newItem);

        final boolean editItem = sm.canAccess(state.getRequest(),
                                              SecurityManager.EDIT_ITEM,
                                              folder);

        m_editFolderAction.setVisible(state, editItem);

        User user = (User)Kernel.getContext().getParty();
        PermissionDescriptor perm =
            new PermissionDescriptor(PrivilegeDescriptor.ADMIN,
                                     folder,
                                     user);

        if (PermissionService.checkPermission(perm)) {
            m_permissionsSeg.setVisible(state, true);
        } else {
            m_permissionsSeg.setVisible(state, false);
        }
    }

    private void browseMode(PageState s) {
        m_browseSeg.setVisible(s, true);
        m_permissionsSeg.setVisible(s, true);
        m_chooseLabel.setVisible(s, false);
        m_contentLabel.setVisible(s, true);
        m_itemPath.setVisible(s, true);
        m_newItemSeg.setVisible(s, false);
        m_newFolderSeg.setVisible(s, false);
        m_editFolderSeg.setVisible(s, false);

        m_typeSel.clearSelection(s);
    }

    private void newItemMode(PageState s) {
        m_permissionsSeg.setVisible(s, false);
        m_newItemSeg.setVisible(s, true);
    }

    private void newFolderMode(PageState s) {
        m_permissionsSeg.setVisible(s, false);
        m_newFolderSeg.setVisible(s, true);
    }

    public void submitted(FormSectionEvent e)
        throws FormProcessException {
        PageState s = e.getPageState();
        if ( e.getSource() == m_folderCreator
             && m_folderCreator.isCancelled(s) ) {
            browseMode(s);
            throw new FormProcessException(GlobalizationUtil.globalize("cms.ui.cancelled"));
        } else if (e.getSource() == m_folderEditor && m_folderEditor.isCancelled(s)) {
            browseMode(s);
            throw new FormProcessException(GlobalizationUtil.globalize("cms.ui.cancelled"));
        } else if ( e.getSource() == m_folderManip.getTargetSelector() ) {
            // This only works if this submission listener is run
            // after the target selector's one
            if ( ! m_folderManip.getTargetSelector().isVisible(s) ) {
                browseMode(s);
            }
        }
    }

    public void process(FormSectionEvent e) {
        PageState s = e.getPageState();
        final Object source = e.getSource();
        if (source == m_newItem) {
            BigDecimal typeID = m_newItem.getTypeID(s);
            m_typeSel.setSelectedKey(s, typeID);
            newItemMode(s);
        } else if (source == m_folderCreator || source == m_folderEditor) {
            browseMode(s);
        } else if (source == m_folderManip.getItemView()) {
            // Hide everything except for the browseSeg
            m_permissionsSeg.setVisible(s, false);
            m_chooseLabel.setVisible(s, true);
            m_contentLabel.setVisible(s, false);
            m_itemPath.setVisible(s, false);
        } else if (source == m_folderManip.getTargetSelector()) {
            browseMode(s);
        }
    }

    public void stateChanged(ChangeEvent e) {
        PageState s = e.getPageState();

        if ( e.getSource() == m_typeSel ) {
            if ( ! m_typeSel.isSelected(s) ) {
                browseMode(s);
            }
        }
    }

    public void actionPerformed(ActionEvent e) {
        PageState s = e.getPageState();
        Object source = e.getSource();
        if ( source == m_createFolderAction ) {
            newFolderMode(s);
        } else if (source == m_editFolderAction) {
            m_permissionsSeg.setVisible(s, false);
            m_editFolderSeg.setVisible(s, true);
        }
        else if (source == m_togglePrivateAction) {
            togglePermissions(s);
        }
    }

    private void togglePermissions(PageState state) {
        Folder currentFolder = (Folder) m_folderSel.getSelectedObject(state);
        if (!Utilities.getSecurityManager(state).canAccess(state.getRequest(), SecurityManager.STAFF_ADMIN)) {
            throw new com.arsdigita.cms.dispatcher.AccessDeniedException();
        }
        DataObject context = PermissionService.getContext(currentFolder);
        if (context != null) {
            PermissionService.clonePermissions(currentFolder);
            Folder liveFolder = (Folder) currentFolder.getLiveVersion();
            if (liveFolder != null) {
                PermissionService.clonePermissions(liveFolder);
            }
        } else {
            ACSObject parent = currentFolder.getParent();
            if (parent != null) {
                PermissionService.setContext(currentFolder, parent);
            } else {
                // if the folder has no parent, it must be a root folder or template folder
                // in this case, set it's context to the ContentSection
                ContentSection section = currentFolder.getContentSection();
                if (section != null) {
                    PermissionService.setContext(currentFolder, section);
                } else {
                    throw new IllegalStateException("Cannot set the context for a folder with " +
                                                    "no parent and no Content Section");
                }
            }

            Folder liveVersion = (Folder) currentFolder.getLiveVersion();
            if (liveVersion != null) {
                ACSObject liveParent = liveVersion.getParent();
                if (liveParent != null) {
                    PermissionService.setContext(liveVersion, liveParent);
                } else {
                    ContentSection liveSection = liveVersion.getContentSection();
                    if (liveSection != null) {
                        PermissionService.setContext(liveVersion, liveSection);
                    } else {
                        throw new IllegalStateException("Cannot set the context for a folder with " +
                                                        "no parent and no Content Section");
                    }
                }
            }

            // revoke all direct permissions so folder will only have inherited permissions
            ObjectPermissionCollection perms = PermissionService.getGrantedPermissions(currentFolder.getOID());
            while (perms.next()) {
                if (!perms.isInherited() && !perms.getPrivilege().equals(PrivilegeDescriptor.ADMIN)) {
                    PermissionDescriptor desc = new PermissionDescriptor(perms.getPrivilege(), currentFolder.getOID(),
                                                                         perms.getGranteeOID());
                    PermissionService.revokePermission(desc);
                }
            }

            if (liveVersion != null) {
                ObjectPermissionCollection livePerms = PermissionService.getGrantedPermissions(liveVersion.getOID());
                while (livePerms.next()) {
                    if (!livePerms.isInherited()) {
                        PermissionDescriptor desc2 = new PermissionDescriptor(livePerms.getPrivilege(), liveVersion.getOID(),
                                                                              livePerms.getGranteeOID());
                        PermissionService.revokePermission(desc2);
                    }
                }
            }

        }
        m_permPane.reset(state);
    }

    public void reset(PageState s) {
        browseMode(s);
        m_folderSel.clearSelection(s);
        m_folderManip.reset(s);
    }

    public final FolderManipulator getManipulator() {
        return m_folderManip;
    }

    public final CMSPermissionsPane getPermissionsPane() {
        return m_permPane;
    }

    public void setPermissionLinkVis(PageState state) {
        if (!Utilities.getSecurityManager(state).
            canAccess(state.getRequest(), SecurityManager.STAFF_ADMIN)) {
            m_togglePrivateAction.setVisible(state, false);
        }
    }

    private static class SectionNewItemForm extends NewItemForm {

        public SectionNewItemForm(String name) {
            super(name);
        }

        public ContentSection getContentSection(PageState s) {
            return CMS.getContext().getContentSection();
        }
    }

    /**
     * Getting the GlobalizedMessage using a CMS Class targetBundle.
     *
     * @param key The resource key
     * @pre ( key != null )
     */
    private static GlobalizedMessage globalize(String key) {
        return com.arsdigita.cms.ui.ContentSectionPage.globalize(key);
    }
}
