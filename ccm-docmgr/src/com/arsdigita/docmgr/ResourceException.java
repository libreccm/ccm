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
 * Base class for resource exceptions.
 *
 * @author Ron Henderson (ron@arsdigita.com)
 * @version $Id: //apps/docmgr/dev/src/com/arsdigita/docmgr/ResourceException.java#2 $
 */

public class ResourceException extends RuntimeException {

    /**
     * Creates a new exception with a given error message.
     * @param message the error message
     */

    public ResourceException (String message) {
        super(message);
    }

    /**
     * Creates a new exception by wrapping an existing one.
     * @param e the exception to wrap
     */

    public ResourceException (Exception e) {
        this(e.getMessage());
    }
}
