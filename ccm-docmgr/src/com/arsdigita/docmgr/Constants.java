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

package com.arsdigita.docmgr;


/**
 * Constants used throughout the document manager application.
 *
 * @version $Id: //apps/docmgr/dev/src/com/arsdigita/docmgr/Constants.java#3 $
 */

public interface Constants {

    // PDL constants
    String ACTION      = "action";
    String CONTENT     = "content";
    String DESCRIPTION = "description";
    String DURATION    = "duration";
    String FOLDER_ID   = "folderID";
    String IS_FOLDER   = "isFolder";
    String LAST_MODIFIED_DATE = "lastModifiedDate";
    String MIME_TYPE_LABEL = "mimeTypeDescription";
    String NAME        = "name";
    String OBJECT_ID   = "objectID";
    String PARENT      = "parent";
    String PARTY_ID    = "partyID";
    String PATH        = "path";
    String SIZE        = "size";
    String TYPE        = "mimeType";
    String USER_ID     = "userID";

    String REPOSITORIES_MOUNTED = "subscribedRepositories";

    // MIME type constants

    String TEXT_PLAIN  = com.arsdigita.mail.Mail.TEXT_PLAIN;
    String TEXT_HTML   = com.arsdigita.mail.Mail.TEXT_HTML;
}
