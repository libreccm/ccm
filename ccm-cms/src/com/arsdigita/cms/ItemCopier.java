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
package com.arsdigita.cms;

import com.arsdigita.domain.DomainObject;
import com.arsdigita.persistence.OID;

import com.arsdigita.persistence.metadata.Property;

/**
 * <p>This is a supplementary class which should only be used in the
 * <code>copyProperty</code> method of {@link CustomCopy}
 * implementations. For a more detailed description, see the javadoc
 * on {@link CustomCopy#copyProperty(CustomCopy, Property,
 * ItemCopier)}.</p>
 *
 * <p>Note that the <code>copyProperty</code> method must <b>only</b> call
 * <code>copier.copy</code> in order to correctly copy some
 * content item inside the method body. An attempt to call any other method
 * to copy an item may cause disastrous results.</p>
 *
 * <p>For a correct implementation of the <code>copyProperty</code> method,
 * see {@link ContentItem#copyProperty(CustomCopy, Property, ItemCopier)}.</p>
 *
 * <p>There are not, and should not be, any public implementations of
 * this class.</p>
 *
 * @author Stanislav Freidin (sfreidin@arsdigita.com)
 * @version $Id: ItemCopier.java 754 2005-09-02 13:26:17Z sskracic $
 * @see CustomCopy#copyProperty(CustomCopy, Property, ItemCopier)
 */
public interface ItemCopier {

    /**
     * Denotes that this instance item copier is used for a regular
     * copy operation
     */
    public static final int PLAIN_COPY = 0;
    public static final int VERSION_COPY = 1;

    /**
     * Creates a copy, by reference or by value, of the property
     * represented in <code>object</code>.
     *
     * When called from within <code>CustomCopy.copyProperty</code>,
     * source will be the source object passed into
     * <code>copyProperty</code>, target will be <code>this</code>,
     * and <code>object</code> will be the object which is to be copied.
     *
     * @param source the <code>DomainObject</code> source (original)
     * object to which this property belongs
     * @param target the new <code>DomainObject</code> copy to which
     * the return value of this method will be attached
     * @param object the <code>DomainObject</code> property being
     * copied
     * @param prop a <code>Property</code> representing
     * <code>object</code>
     * @return <code>object</code> if <code>prop</code> is not a
     * component or a copy of <code>object</code> it is a component
     */
    DomainObject copy(final CustomCopy source,
		      final CustomCopy target,
		      final DomainObject object,
		      final Property prop);

    /**
     * Return a copy of the object identified by the specified OID.
     * The copy may not exist if the specified OID has not been copied yet;
     * in this case, the method will return <code>null</code>.
     * <p>
     * This method will typically be used in the
     * {@link ContentItem#copyProperty(ContentItem, String, ItemCopier)}
     * method.
     *
     * @param oid the OID of the object whose copy should be retrieved
     * @return the copy of the specified object, or null if the object has
     *   not been copied yet, or if the object is not convertible to a
     *  <code>DomainObject</code>.
     */
    DomainObject getCopy(OID oid);

    /**
     * Return PLAIN_COPY if the particular instance of the item
     * copier is used for making plain copies of the item. Return
     * VERSION_COPY id the copier is used to create live versions
     * for draft items.
     */
    public int getCopyType();
}
