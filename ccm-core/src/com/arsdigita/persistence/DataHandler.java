/*
 * Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.persistence;

/**
 * The DataHandler can be used to override the behavior of the persistence
 * layer when it performs certain operations. Currently only delete is
 * supported since the primary use for this class is to override hard deletes
 * and turn them into soft deletes under certain circumstances.
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #8 $ $Date: 2004/08/16 $
 **/

public abstract class DataHandler {

    public final static String versionId = "$Id: DataHandler.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";


    /**
     * This method is called in order to delete a data object. By default this
     * executes the SQL defined in the object type definition for the given
     * data object. In the common case this does a hard delete.
     **/

    public void doDelete(DataObject data) {
        throw new Error("not implemented");
    }

}
