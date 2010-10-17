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

import com.arsdigita.persistence.PersistenceException;

/**
 * Exception thrown to indicate that resource contains children and
 * cannot be deleted.
 *
 * @version $Id: //apps/docmgr/dev/src/com/arsdigita/docmgr/ResourceNotEmptyException.java#2 $
 */

public class ResourceNotEmptyException extends PersistenceException {

    /**
     * Creates a new exception with a given error message.
     * @param message the error message
     */

    public ResourceNotEmptyException(String message) {
        super(message,null);
    }
}
