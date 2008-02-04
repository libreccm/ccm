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

package com.arsdigita.cms.docmgr;



/**
 * Exception thrown to indicate that another resource exists with the
 * same parent and name.
 *
 * @version $Id: //apps/docmgr-cms/dev/src/com/arsdigita/cms/docmgr/ResourceExistsException.java#1 $
 */

public class ResourceExistsException extends RuntimeException {

    /**
     * Creates a new exception with a given error message.
     * @param message the error message
     */

    public ResourceExistsException(String message) {
        super(message);
    }
}
