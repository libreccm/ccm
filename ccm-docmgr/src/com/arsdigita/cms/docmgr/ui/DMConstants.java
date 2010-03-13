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

import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.globalization.GlobalizedMessage;

/**
 * Variously used constant objects used in Document Manager UI
 *
 * @author <mailto href="StefanDeusch@computer.org">Stefan Deusch</a>
 */

public interface DMConstants {

    // PDL vars
    String FOLDER_ID = "folderID";
    String IS_LOCKED   = "isLocked";
    String IS_MOUNTED   = "isMounted";
    String LAST_MODIFIED = "lastModified";
    String MODIFYING_USER = "modifyingUser";
    String MIME_TYPE_LABEL = "mimeTypeDescription";
    String NAME        = "name";
    String ABS_PATH    = "absPath";
    String NUM_FILES   = "numFiles";
    String REPOSITORY_ID = "repositoryID";
    String RESOURCE_ID  = "resourceID";
    String SIZE        = "size";
    String TYPE        = "mimeType";
    String IS_FOLDER   = "isFolder";

    // PDL queries
    String GET_ALL_TREES = "com.arsdigita.docs.getAllTreesView";
    String GET_REPOSITORIES = "com.arsdigita.docs.getRepositoriesView";
    String GET_REPOSITORIES_ROOTS = "com.arsdigita.docs.getRepositoryRoots";
    String GET_CHILDREN = "com.arsdigita.docs.getChildren";

    // PDL associations
    String FILES   = "files";
    String FOLDERS = "folders";

    /**
     * The XML namespace.
     */
    String DOCS_XML_NS = "http://www.redhat.com/docs/1.0";

    /**
     * Globalization resource
     */
    String BUNDLE_NAME = "com.arsdigita.cms.docmgr.ui.DMResources";

    /**
     * Global state parameters.
     */
    String CAT_TREE_ID_PARAM_NAME = "dct_id";
    String CAT_TREE_INIT_ID_PARAM_NAME = "dct_init_id";
    
    String DOC_COL_ID_PARAM_NAME = "doc_col_id";

    String ROOTFOLDER_ID_PARAM_NAME = "r_id";
    BigDecimalParameter ROOTFOLDER_ID_PARAM  = new BigDecimalParameter(ROOTFOLDER_ID_PARAM_NAME);

    String SEL_FOLDER_ID_PARAM_NAME = "f_id";
    BigDecimalParameter SEL_FOLDER_ID_PARAM  = new BigDecimalParameter(SEL_FOLDER_ID_PARAM_NAME);

    String OPEN_FOLDER_ID_PARAM_NAME = "of_id";
    BigDecimalParameter OPEN_FOLDER_ID_PARAM  = new BigDecimalParameter(OPEN_FOLDER_ID_PARAM_NAME);

    String FILE_ID_PARAM_NAME = "d_id";
    // param constants are evil!  --ccw
    //BigDecimalParameter FILE_ID_PARAM = new BigDecimalParameter(FILE_ID_PARAM_NAME);

    String START_DATE_PARAM_NAME = "startDate";
    String END_DATE_PARAM_NAME = "endDate";

    /**
     * DM Index page title
     */
    Label PAGE_TITLE_LABEL  = new Label
        (new GlobalizedMessage("ui.title", BUNDLE_NAME));

    /**
     * DM File Info Page
     */
    Label FILE_INFO_LABEL = new Label
        (new GlobalizedMessage("ui.fileinfo.title", BUNDLE_NAME));

    // File Info Navigational Tabs
    Label FILE_INFO_PROPERTIES_TITLE = new Label
        (new GlobalizedMessage("ui.fileinfo.properties.title", BUNDLE_NAME));

    Label FILE_INFO_HISTORY_TITLE = new Label
        (new GlobalizedMessage("ui.fileinfo.history.title", BUNDLE_NAME));

    Label FILE_INFO_COMMENTS_TITLE = new Label
        (new GlobalizedMessage("ui.fileinfo.comments.title", BUNDLE_NAME));

    Label FILE_INFO_LINKS_TITLE = new Label
        (new GlobalizedMessage("ui.fileinfo.links.title", BUNDLE_NAME));

    Label GO_BACK_LABEL = new Label
        (new GlobalizedMessage("ui.fileinfo.goback.label", BUNDLE_NAME));

    /**
     * Navigational dimensional bar
     */
    Label MY_WORKSPACE_LABEL = new Label
        (new GlobalizedMessage("ui.workspace.title", BUNDLE_NAME));

    Label SIGN_OUT_LABEL = new Label
        (new GlobalizedMessage("ui.nav.signout", BUNDLE_NAME));

    Label HELP_LABEL = new Label
        (new GlobalizedMessage("ui.nav.help", BUNDLE_NAME));


    /**
     * Page navigational tabs
     */
    Label WS_BROWSE_TITLE = new Label
        (new GlobalizedMessage("ui.workspace.browse.title", BUNDLE_NAME));

    Label WS_SEARCH_TITLE = new Label
        (new GlobalizedMessage("ui.workspace.search.title", BUNDLE_NAME));

    Label WS_REPOSITORIES_TITLE = new Label
        (new GlobalizedMessage("ui.workspace.repositories.title", BUNDLE_NAME));

    /**
     * One Folder content
     */
    Label FOLDER_INFORMATION_HEADER = new Label
        (new GlobalizedMessage("ui.folder.content.header", BUNDLE_NAME));

    /**
     * Repositories
     */
    Label REPOSITORIES_INFORMATION_HEADER = new Label
        (new GlobalizedMessage("ui.repositories.content.header", BUNDLE_NAME));

    GlobalizedMessage REPOSITORY_RECENTDOCS_EMPTY
        = new GlobalizedMessage("ui.repositories.recentDocs.empty", BUNDLE_NAME);

    /**
     * File Uplaod Form
     */
    Label FILE_UPLOAD_FORM_HEADER = new Label
        (new GlobalizedMessage("ui.file.upload.header", BUNDLE_NAME));

    /**
     *  Folder Create Form
     */
    Label FOLDER_CREATE_FORM_HEADER = new Label
        (new GlobalizedMessage("ui.folder.create.header", BUNDLE_NAME));

    /**
     *  File Properties
     */
    Label FILE_PROPERTIES_HEADER = new Label
        (new GlobalizedMessage("ui.fileinfo.properties.header", BUNDLE_NAME));

    /**
     * File Edit Panel
     */
    Label FILE_EDIT_HEADER = new Label
        (new GlobalizedMessage("ui.fileinfo.edit.header", BUNDLE_NAME));

    GlobalizedMessage FILE_EDIT_ACTION_DESCRIPTION =
        new GlobalizedMessage("ui.fileinfo.edit.action.description", BUNDLE_NAME);

    /**
     * File Upload Panel
     */
    Label FILE_UPLOAD_HEADER = new Label
        (new GlobalizedMessage("ui.fileinfo.upload.header", BUNDLE_NAME));

    GlobalizedMessage FILE_UPLOAD_INITIAL_TRANSACTION_DESCRIPTION =
        new GlobalizedMessage("ui.fileinfo.upload.initialversion.description", BUNDLE_NAME);

    /**
     * File Download Panel
     */
    Label FILE_DOWNLOAD_HEADER = new Label
        (new GlobalizedMessage("ui.fileinfo.download.header", BUNDLE_NAME));

    /**
     * File-Send-to-Colleague Form
     */
    Label FILE_SEND_COLLEAGUE_HEADER = new Label
        (new GlobalizedMessage("ui.fileinfo.sendcolleague.header", BUNDLE_NAME));
    Label FILE_SEND_COLLEAGUE_FORM_EMAIL = new Label
        (new GlobalizedMessage("ui.fileinfo.sendcolleague.form.email", BUNDLE_NAME));
    GlobalizedMessage FILE_SEND_COLLEAGUE_MESSAGE = 
        (new GlobalizedMessage("ui.fileinfo.sendcolleague.message", BUNDLE_NAME));
    Label FILE_SEND_COLLEAGUE_THANKS = new Label
        (new GlobalizedMessage("ui.fileinfo.sendcolleague.thanks", BUNDLE_NAME));
    GlobalizedMessage FILE_SEND_COLLEAGUE_THANKS_RETURN_LINK =
        new GlobalizedMessage("ui.fileinfo.sendcolleague.thanks.return.link", BUNDLE_NAME);
    GlobalizedMessage FILE_SEND_COLLEAGUE_RETURN_ADDRESS =
        new GlobalizedMessage("ui.fileinfo.sendcolleague.return.address", BUNDLE_NAME);
    GlobalizedMessage FILE_SEND_COLLEAGUE_SUBJECT =
        new GlobalizedMessage("ui.fileinfo.sendcolleague.subject", BUNDLE_NAME);
    GlobalizedMessage FILE_SEND_COLLEAGUE_SUBMIT =
        new GlobalizedMessage("ui.fileinfo.sendcolleague.submit", BUNDLE_NAME);
        

    /**
     * File-Delete Form
     */
    Label FILE_DELETE_HEADER = new Label
        (new GlobalizedMessage("ui.fileinfo.delete.header", BUNDLE_NAME));

    /**
     * File Action Panel
     */
    Label FILE_ACTION_HEADER = new Label
        (new GlobalizedMessage("ui.fileinfo.actions.header", BUNDLE_NAME));

    /**
     * File Revision History Panel
     */

    Label FILE_REVISION_HISTORY_HEADER = new Label
        (new GlobalizedMessage("ui.fileinfo.history.header", BUNDLE_NAME));


    /**
     * File Feedback Panel
     */

    Label FILE_FEEDBACK_HEADER = new Label
        (new GlobalizedMessage("ui.fileinfo.feedback.header", BUNDLE_NAME));


    /**
     * Action Panel Constants
     */

    Label DESTINATION_FOLDER_PANEL_HEADER = new Label(
          new GlobalizedMessage("ui.folder.destination.list.header", BUNDLE_NAME));

    Label FOLDER_EMPTY_LABEL = new Label(
          new GlobalizedMessage("ui.folder.empty", BUNDLE_NAME));

    GlobalizedMessage  FOLDER_NEW_FOLDER_LINK =
        new GlobalizedMessage("ui.action.newfolder", BUNDLE_NAME);
    GlobalizedMessage  FOLDER_NEW_CREATE_LINK =
        new GlobalizedMessage("ui.link.create.link", BUNDLE_NAME);
    
    GlobalizedMessage  FOLDER_NEW_FILE_LINK =
        new GlobalizedMessage("ui.action.newfile", BUNDLE_NAME);

    GlobalizedMessage  FOLDER_NEW_DOCLINK_LINK =
        new GlobalizedMessage("ui.link.action.newdoclink", BUNDLE_NAME);

    Label ACTION_CUT_LABEL = new Label(
          new GlobalizedMessage("ui.action.edit.cut",  BUNDLE_NAME));

    Label ACTION_COPY_LABEL = new Label(
          new GlobalizedMessage("ui.action.edit.copy",  BUNDLE_NAME));

    Label ACTION_DELETE_LABEL = new Label(
          new GlobalizedMessage("ui.action.edit.delete", BUNDLE_NAME));

    GlobalizedMessage ACTION_DELETE_CONFIRM =
        new GlobalizedMessage("ui.action.delete.confirm", BUNDLE_NAME);

    Label ACTION_ERROR_LABEL = new Label(
          new GlobalizedMessage("ui.action.error", BUNDLE_NAME));

    Label ACTION_ERROR_CONTINUE = new Label(
          new GlobalizedMessage("ui.action.error.continue", BUNDLE_NAME));

    String ACTION_CUT_VALUE = "resource-cut";
    String ACTION_COPY_VALUE = "resource-copy";
    String ACTION_DELETE_VALUE = "resource-delete";

    GlobalizedMessage ACTION_DELETE_SUBMIT =
        new GlobalizedMessage("ui.action.delete.submit", BUNDLE_NAME);

    GlobalizedMessage ACTION_COPY_SUBMIT =
        new GlobalizedMessage("ui.action.copy.submit", BUNDLE_NAME);

    GlobalizedMessage ACTION_MOVE_SUBMIT =
        new GlobalizedMessage("ui.action.move.submit", BUNDLE_NAME);


    /**
     * Portlet Panel Constants
     */
    GlobalizedMessage  ROOT_ADD_RESOURCE_LINK =
        new GlobalizedMessage("ui.portlet.action.newresource", BUNDLE_NAME);

    String ROOT_ADD_DOC_PARAM_NAME = "root_add_doc";
    StringParameter ROOT_ADD_DOC_PARAM =
        new StringParameter(ROOT_ADD_DOC_PARAM_NAME);

    String FOLDER_ADD_DOC_PARAM_NAME = "folder_add_doc";

    GlobalizedMessage  ROOT_ADD_DOCLINK_LINK =
        new GlobalizedMessage("ui.portlet.action.new.doclink", BUNDLE_NAME);

    String PARAM_ROOT_ADD_DOC_LINK = "root_add_doc_link";


    /**
     * File Action Panel Constants
     */

    GlobalizedMessage  FILE_EDIT_LINK  =
        new GlobalizedMessage("ui.fileinfo.edit.link", BUNDLE_NAME);

    GlobalizedMessage  FILE_NEW_VERSION_LINK  =
        new GlobalizedMessage("ui.fileinfo.newversion.link", BUNDLE_NAME);

    GlobalizedMessage  FILE_DOWNLOAD_LINK  =
        new GlobalizedMessage("ui.fileinfo.download.link", BUNDLE_NAME);

    GlobalizedMessage  FILE_SEND_COLLEAGUE_LINK  =
        new GlobalizedMessage("ui.fileinfo.sendcolleague.link", BUNDLE_NAME);

    GlobalizedMessage  FILE_DELETE_LINK  =
        new GlobalizedMessage("ui.fileinfo.delete.link", BUNDLE_NAME);


    /**
     * Error messages
     */

    GlobalizedMessage FOLDER_PARENTNOTFOUND_ERROR =
        new GlobalizedMessage("ui.error.parentnotfound", BUNDLE_NAME);

    GlobalizedMessage RESOURCE_EXISTS_ERROR =
        new GlobalizedMessage("ui.error.resourceexists", BUNDLE_NAME);

    GlobalizedMessage EMAIL_INVALID_ERROR =
        new GlobalizedMessage("ui.email.formatinvalid", BUNDLE_NAME);

    GlobalizedMessage DIFFERENT_MIMETYPE_ERROR =
        new GlobalizedMessage("ui.error.mimetype", BUNDLE_NAME);


    /**
     * FILE DELETE link
     */

    GlobalizedMessage  FILE_DELETE_CONFIRM =
        new GlobalizedMessage("ui.filedelete.confirm", BUNDLE_NAME);

    // Labels for Files
    GlobalizedMessage FILE_NAME =
        new GlobalizedMessage("ui.file.name", BUNDLE_NAME);

    GlobalizedMessage FILE_CATEGORIES =
        new GlobalizedMessage("ui.file.categories", BUNDLE_NAME);

    GlobalizedMessage FILE_NAME_REQUIRED =
        new GlobalizedMessage("ui.file.name.required", BUNDLE_NAME);

   GlobalizedMessage TITLE_REQUIRED =
      new GlobalizedMessage("ui.file.name.required", BUNDLE_NAME);

    GlobalizedMessage TARGET_REQUIRED =
       new GlobalizedMessage("ui.link.target.required", BUNDLE_NAME);

    GlobalizedMessage FILE_INTENDED_AUDIENCE =
        new GlobalizedMessage("ui.file.intended.audience", BUNDLE_NAME);

    GlobalizedMessage FILE_UPLOAD_ADD_FILE =
        new GlobalizedMessage("ui.file.upload", BUNDLE_NAME);

    GlobalizedMessage FILE_SOURCE =
        new GlobalizedMessage("ui.file.source", BUNDLE_NAME);

    GlobalizedMessage FILE_DESCRIPTION =
        new GlobalizedMessage("ui.file.description", BUNDLE_NAME);

    GlobalizedMessage FILE_VERSION_DESCRIPTION =
        new GlobalizedMessage("ui.file.version.description", BUNDLE_NAME);

    GlobalizedMessage FILE_KEYWORDS =
        new GlobalizedMessage("ui.file.keywords", BUNDLE_NAME);

    GlobalizedMessage FILE_SAVE =
        new GlobalizedMessage("ui.file.save", BUNDLE_NAME);

    GlobalizedMessage FILE_SUBMIT =
        new GlobalizedMessage("ui.file.submit", BUNDLE_NAME);

    GlobalizedMessage CANCEL =
        new GlobalizedMessage("ui.cancel", BUNDLE_NAME);

    // Labels for Intended Audience Options
    GlobalizedMessage FILE_INTENDED_AUDIENCE_PUBLIC =
        new GlobalizedMessage("ui.file.intended.audience.public", BUNDLE_NAME);
    GlobalizedMessage FILE_INTENDED_AUDIENCE_WORKSPACE =
        new GlobalizedMessage("ui.file.intended.audience.workspace", BUNDLE_NAME);
    GlobalizedMessage FILE_INTENDED_AUDIENCE_INTERNAL =
        new GlobalizedMessage("ui.file.intended.audience.internal", BUNDLE_NAME);
    

    /**
     * Folder parameters
     */
    String FOLDER_NAME = "folder-name";
    String FOLDER_DESCRIPTION = "folder-description";

    Label FOLDER_NAME_LABEL = new  Label(
          new GlobalizedMessage("ui.folder.name", BUNDLE_NAME));

    Label FOLDER_DESCRIPTION_LABEL = new  Label(
          new GlobalizedMessage("ui.folder.description", BUNDLE_NAME));

    GlobalizedMessage FOLDER_SAVE =
        new GlobalizedMessage("ui.folder.save", BUNDLE_NAME);

    /**
     * Repsitories Selection Form
     */
    GlobalizedMessage REPOSITORIES_MOUNTED_SAVE =
        new GlobalizedMessage("ui.repositories.mounted.save", BUNDLE_NAME);

    /**
     * Send to colleague form variables.
     */

    Label SEND_FRIEND_FORM_EMAIL_SUBJECT = new Label(
          new GlobalizedMessage("ui.send.friend.email.subject", BUNDLE_NAME));

    Label SEND_FRIEND_FORM_EMAIL_LIST = new Label(
          new GlobalizedMessage("ui.send.friend.email.list", BUNDLE_NAME));

    Label SEND_FRIEND_FORM_DESCRIPTION = new Label(
          new GlobalizedMessage("ui.send.friend.description", BUNDLE_NAME));

    GlobalizedMessage SEND_FRIEND_FORM_SUBMIT =
        new GlobalizedMessage("ui.send.friend.submit", BUNDLE_NAME);
}
