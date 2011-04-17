/*
 * Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.docrepo;


/**
 * Constants used throughout the document manager application.
 *
 * @version $Id: Constants.java  pboy $
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
