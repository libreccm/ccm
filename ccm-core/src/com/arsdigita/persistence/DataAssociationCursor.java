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
package com.arsdigita.persistence;

/**
 *  DataAssociationCursor -
 * This is used to allow developers to iterate through the objects
 * within an association and get properties for those objects.
 * This does not implement java.util.Iterator because it is a cursor,
 * not an iterator.  That is, each row has properties but is not
 * actually an object
 * <p>
 *
 * This is typically used when the developer wants to iterator through
 * the objects within an association.  In the sample of code below,
 * the method gets the cursor from the association, filters the cursor
 * so that it only returns the first N articles and then puts those N
 * articles, into a list to be returned.  </p>
 *
 * <pre><code>
 * public Collection getArticles(int numberOfArticles) {
 *     LinkedList articles = new LinkedList();
 *     DataAssociationCursor cursor = ((DataAssociation) get("articles")).cursor();
 *     cursor.addFilter(cursor.getFilterFactory().lessThan("rownum",
 *                                                         numberOfArticles, true));
 *     while (cursor.next()) {
 *         articles.addLast(cursor.getDataObject());
 *     }
 *
 *     cursor.close();
 *     return children;
 * }
 *</code></pre>
 * <p>
 * Note that it is important to close the cursor explicitly to return
 * the proper database resources as soon as possible.
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @author <a href="mailto:randyg@alum.mit.edu">randyg@alum.mit.edu</a>
 * @version $Revision: #8 $ $Date: 2004/08/16 $
 */

public interface DataAssociationCursor extends DataCollection {

    public static final String versionId = "$Id: DataAssociationCursor.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    /**
     * Returns a data association that created this iterator
     **/
    DataAssociation getDataAssociation();


    /**
     * Returns the link associated with the current row.
     *
     * @return The link.
     **/
    DataObject getLink();


    /**
     * Calls get("link." + name).
     *
     * @param name The name of the link property.
     *
     * @return The property value.
     */
    Object getLinkProperty(String name);


    /**
     * Removes the object associated with the current position in the
     * collection. Note that this has NO EFFECT on the underlying
     * DataAssociation until save() is called on the association's parent
     * DataObject
     */
    void remove();

}
