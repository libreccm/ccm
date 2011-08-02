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
import com.arsdigita.persistence.metadata.Property;
import org.apache.log4j.Logger;

/**
 * Recursively copies a DataObject, and all of its composite sub-objects.
 * This class implements <code>ItemCopier</code> by maintaining a Map of
 * OID-&gt;DataObject of all objects copied so far. This ensures that no
 * infinite recursion loops ever occur.
 * <p>
 * The {@link #copyItem(ContentItem)} method attempts to copy an item by
 * using the following steps:
 * <ol>
 *   <li>Copy the scalar attributes of the item</li>
 *   <li>Copy the 1..1 associations of the item</li>
 *   <li>Save the item</li>
 *   <li>Copy the 0..n associations of the item</li>
 *   <li>Save the item</li>
 *   <li>Transfer all services (such as categorization) from the original item to
 *     the copy</li>
 * </ol>
 * At each copying step, this class will call
 * {@link ContentItem#copyProperty(ContentItem, String, ItemCopier)}
 * in order to give the item a chance to override the default copying behavior.
 * This class will pass itself as the third argument.
 * <p>
 * <code>ObjectCopier</code> has package-scope since it will only be accessed from
 * {@link ContentItem}, as part of the internal implementation of the
 * {@link ContentItem#copy()} method. Classes outside the package should not
 * attempt to call methods on this class directly, since the results may
 * prove dangerous.
 * <p>
 *
 * @author <a href="mailto:sfreidin@arsdigita.com">Stanislav Freidin</a>
 * @version $Id: ObjectCopier.java 2090 2010-04-17 08:04:14Z pboy $
 * @see ContentItem
 * @see ContentItem#copyProperty(ContentItem, String, ItemCopier)
 */
class ObjectCopier extends DomainCopier implements ItemCopier {

    private static Logger s_log = Logger.getLogger(ObjectCopier.class);

    /**
     * This copier is used to create plain copies for items
     */
    public int getCopyType() {
        return ItemCopier.PLAIN_COPY;
    }

    /**
     * Copy a {@link ContentItem}, along with all of its component
     * sub-objects, and return the copy. Note that the categories to
     * which the source item belongs are not automatically transferred
     * to the copy; the user must call {@link #copyServices(ContentItem,
     * ContentItem)} in order to transfer the categories and other
     * services.
     *
     * @param item the item to be copied
     * @return a copy of the item
     */
    public ContentItem copyItem(ContentItem item) {
        final ContentItem copy = (ContentItem) copy(item);
        return copy;
    }

    /**
     * Copies properties.  This method is called from {@link
     * #copy(DomainObject)} for each property of the object being
     * copied.
     *
     * This implementation calls {@link CustomCopy#copyProperty(CustomCopy,
     * Property, itemCopier)} if <code>source</code> and
     * <code>target</code> implement <code>CustomCopy</code>, and only
     * call <code>super.copyProperty</code> if the above returns false.
     *
     *
     * @param source the <code>DomainObject</code> being copied
     * @param target the new copy
     * @param prop the <code>Property</code> currently under
     * consideration
     */
    protected void copyProperty(final DomainObject source,
                                final DomainObject target,
                                final Property prop) {
        m_trace.enter("copyProperty", source, target, prop);

        if (target instanceof CustomCopy) {

            final CustomCopy sitem = (CustomCopy) source;
            final CustomCopy titem = (CustomCopy) target;

            if (titem.copyProperty(sitem, prop, this)) {
                s_log.debug("The target item overrides the default " +
                            "behavior to propogate the property; " +
                            "skipping it");

                m_trace.exit("copyProperty");

                return;
            }
        }

        super.copyProperty(source, target, prop);

        m_trace.exit("copyProperty");
    }

    /**
     * Creates a copy, by reference or by value, of the property
     * represented in <code>object</code>.
     *
     * This implementation returns the result of {@link
     * #copy(DomainObject)} if the property is a component and simply
     * returns <code>object</code> if it is not.
     *
     * @param source the <code>CustomCopy</code> source (original)
     * object to which this property belongs
     * @param target the new <code>CustomCopy</code> copy to which
     * the return value of this method will be attached
     * @param object the <code>DomainObject</code> property being
     * copied
     * @param prop a <code>Property</code> representing
     * <code>object</code>
     * @return <code>object</code> if <code>prop</code> is not a
     * component or a copy of <code>object</code> it is a component
     * @throws UnsupportedOperationException if either source or
     * target are not <code>DomainObjects</code>  
     */
    public DomainObject copy(final CustomCopy source,
			     final CustomCopy target,
			     final DomainObject object,
			     final Property prop) {
	if ((source instanceof DomainObject) && 
	    (target instanceof DomainObject)) {
	    return copy((DomainObject) source, (DomainObject) target, 
			object, prop);
	} else {
	    throw new UnsupportedOperationException
		("CustomCopy implementations should be DomainObjects");
	}
    }

    /**
     * Transfer services from one {@link ContentItem} to another. For
     * example, this method will be called to transfer the categories
     * from the draft version of the item to the live version of the
     * item.
     *
     * Note that the target item must have a valid item ID before this
     * method is called. If the target item is new, it should be saved
     * prior to calling <code>copyServices</code>.
     *
     * @param target the target item
     * @param source the source item
     */
    public static void copyServices(ContentItem target, ContentItem source) {
        if (target.copyServices(source)) {
            s_log.debug("The target item has opted to do its own " +
                        "service propogation");
        } else {
            // What goes here?
        }
    }
}
