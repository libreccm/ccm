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

import java.math.BigDecimal;
import java.net.URL;

/**
 * This interface describes the functionality common to operations
 * on files and folders in the document repository application.
 *
 * Concrete implementations of this interface may vary in the
 * implementation of the data store.  For example, persistent data for
 * resource might be stored in a file system or a database.
 *
 * @author Stefan Deusch (stefan@arsdigita.com)
 * @version $Id: Resource.java  pboy $
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
