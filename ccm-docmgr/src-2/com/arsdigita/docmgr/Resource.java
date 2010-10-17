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

import java.math.BigDecimal;
import java.net.URL;

/**
 * This interface describes the functionality common to operations
 * on files and folders in the document manager application.
 *
 * Concrete implementations of this interface may vary in the
 * implementation of the data store.  For example, persistent data for
 * resource might be stored in a file system or a database.
 *
 * @author Stefan Deusch (stefan@arsdigita.com)
 * @version $Id: //apps/docmgr/dev/src/com/arsdigita/docmgr/Resource.java#4 $
 */

public interface Resource {

    /**
     * The path-separator character, represented as a string for convenience.
     */

    final static String SEPARATOR = "/";

    /**
     * The path-separator character.
     */

    final static char SEPARATOR_CHAR = SEPARATOR.charAt(0);


    /**
     * Returns the name of the file or folder corresponding to this
     * resource.
     */
    String getName();

    /**
     * Returns a description of the resource, or null if no
     * description has been provided.
     */
    String getDescription();

    /**
     * Returns the path name of this resource's parent, or null if
     * this resource does not have a parent.
     */
    Resource getParent();

    /**
     * Converts this resource into a pathname string.
     */
    String getPath();

    /**
     * Tests whether this resource is a folder.
     */
    boolean isFolder();

    /**
     * Tests whether this resource is a file.
     */
    boolean isFile();

    /**
     * Copies the resource into another location.  Preserves the
     * original name of the resource but places the copy inside a new
     * parent resource.
     *
     * @param parent the parent of the copy
     * @return a copy of the original resource
     */
    Resource copyTo(Resource parent) throws ResourceExistsException;

    /**
     * Copies the resource into another location with a new name.
     *
     * @param name the name of the copy
     * @param parent the parent of the copy
     * @return a copy of the original resource.
     */
    Resource copyTo(String name, Resource parent) throws ResourceExistsException;

    /**
     * Copies the resource into the same location (same parent) with a
     * new name.
     *
     * @param name the name of the copy
     * @return a copy of the original resource.
     */
    Resource copyTo(String name) throws ResourceExistsException;

    /**
     * Returns the pathname string of this resource.
     */
    String toString();

    /**
     * Returns the URL corresponding to this resource.
     */
    URL toURL();

    /**
     * Returns a unique identifier for this resource.
     */
    BigDecimal getID();
}
