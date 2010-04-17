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

import com.arsdigita.cms.lifecycle.Lifecycle;
import com.arsdigita.cms.lifecycle.LifecycleService;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.persistence.metadata.Property;
import com.arsdigita.persistence.OID;
import com.arsdigita.util.Assert;
import org.apache.log4j.Logger;

import java.util.HashSet;

/**
 * Extends <code>ObjectCopier</code> to create a live version for an
 * item. Clones the item, making sure that live versions of all subitems
 * point only to other live versions.
 *
 * @author <a href="mailto:sfreidin@arsdigita.com">Stanislav Freidin</a>
 * @version $Id: VersionCopier.java 1942 2009-05-29 07:53:23Z terry $
 */
class VersionCopier extends ObjectCopier {

    private static Logger s_log = Logger.getLogger(VersionCopier.class);

    private final Lifecycle m_lifecycle;
    private boolean m_once = false;
    private long m_start = 0;
    private final HashSet m_traversedComponents;
    private OID m_topLevelSourceOID = null;

    /**
     * Creates a new <code>VersionCopier</code> with a given Lifecycle
     *
     * @param lifecycle The Lifecycle
     */
    VersionCopier(final Lifecycle lifecycle) {
        m_traversedComponents = new HashSet();
        m_lifecycle = lifecycle;
    }

    /**
     * Creates a new <code>VersionCopier</code> with no Lifecycle
     */
    VersionCopier() {
        m_traversedComponents = new HashSet();
        m_lifecycle = null;
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
    public ContentItem copyItem(final ContentItem item) {
        m_trace.enter("copyItem", item);

        if (s_log.isInfoEnabled()) {
            s_log.info("Publishing " + item);
            m_start = System.currentTimeMillis();
        }

        if (Assert.isEnabled()) {
            //Assert.isFalse(item instanceof ContentBundle);
            //Assert.isFalse(item instanceof Folder);
            Assert.isFalse(m_once);
            m_once = true;
        }

	m_topLevelSourceOID = item.getOID();
        final ContentItem version = createVersion(item);
	//Assert.isTrue(m_topLevelSourceOID == null,
	//	     "CopyItem should be called only once for a given copier instance");

        if (m_lifecycle != null) {
            LifecycleService.setLifecycle(version, m_lifecycle);
        }

        m_trace.exit("copyItem", version);

        if (Assert.isEnabled()) {
            // Reset the reentrance detection.
            m_once = false;
        }

        if (s_log.isInfoEnabled()) {
            s_log.info("Done publishing " + item + " (" +
                       (System.currentTimeMillis() - m_start) +
                       " millis)");
        }

        return version;
    }

    /**
     * This copier is used to create published copies of items
     */
    public int getCopyType() {
        return ItemCopier.VERSION_COPY;
    }

    /**
     * Kicks off the copying process.  Creates a copy by value of
     * <code>source</code> and then traverses its associations and
     * repeats the process.
     *
     * @param source the <code>DomainObject</code> from which to copy
     */
    public DomainObject copy(final DomainObject object) {
	if (object != null) {
	    m_traversedComponents.add(object);
	}
	return super.copy(object);
    }

    /**
     * Copies properties.  This method is called from {@link
     * #copy(DomainObject)} for each property of the object being
     * copied.
     *
     * Copying behavior depends on the following:
     * <dl>
     *  <dt> <code>object instanceof ContentItem && prop.isComponent()</code></dt>
     *  <dd> Copy ContentItem recursively, setting the version attribute, etc.</dd>
     *  <dt> <code>object instanceof ContentItem &&
     *       !prop.isComponent() (and target is an already-traversed
     *       component of the top-level item )</code></dt>
     *  <dd> Return reference to already-created copy of this item</dd>
     *  <dt> <code>object instanceof ContentItem &&
     *       !prop.isComponent() && prop.isRequired (and target is not an already-traversed
     *       component of the top-level item )</code></dt>
     *  <dd> Assert failure: required top-level item associations are
     *       not allowed here.</dd>
     *  <dt> <code>object instanceof ContentItem &&
     *       !prop.isComponent() && !prop.isRequired (and target is not an already-traversed
     *       component of the top-level item )</code></dt>
     *  <dd> Create PublishedLink for association; return null</dd>
     *  <dt> <code>!object instanceof ContentItem </code></dt>
     *  <dd> object is not a ContentItem, rely on default DomainCopier
     *       behavior</dd>
     * </dl>
     *
     * @param source the <code>DomainObject</code> being copied
     * @param target the new copy
     * @param prop the <code>Property</code> currently under
     * consideration
     */
    protected DomainObject copy(final DomainObject source,
				final DomainObject target,
				final DomainObject object,
				final Property prop) {
        m_trace.enter("copy", object, prop);

        if (s_log.isDebugEnabled()) {
            s_log.debug("Copying property " + prop + " of " + object);
        }

        Assert.exists(source, DomainObject.class);
        Assert.exists(target, DomainObject.class);
        Assert.exists(object, DomainObject.class);
        Assert.exists(prop, Property.class);

        if (object instanceof ContentItem) {
            s_log.debug("The property is a content item");

            final ContentItem item = (ContentItem) object;

            if (Assert.isEnabled()) {
                item.assertDraft();
            }

            if (prop.isComponent()) {
                s_log.debug("The property is a component; creating a " +
                            "live or pending version");

                final ContentItem copy = createVersion(item);

                m_trace.exit("copy", copy);

                return copy;
            } else if (m_traversedComponents.contains(object)) {
		final DomainObject copy = copy(object);
		
		m_trace.exit("copy", copy);
		
		return copy;
            } else if (prop.isRequired()) {
                Assert.fail("1..1 associations to non-component top-level ContentItems are not allowed");
		return null;
            } else {
                s_log.debug("The property is not a component; creating " +
                            "PublishedLink for the item");

		PublishedLink.create((ContentItem) getCopy(m_topLevelSourceOID), target, prop.getName(), item);

                m_trace.exit("copy", null);

                return null;
            }
        } else {
            s_log.debug("The property is not a content item; using " +
                        "domain object copier");

            final DomainObject copy = super.copy(source, target, object, prop);

            m_trace.exit("copy", copy);

            return copy;
        }
    }

    private ContentItem createVersion(final ContentItem item) {
        m_trace.enter("createVersion", item);

        if (Assert.isEnabled()) {
            item.assertDraft();
        }

        final ContentItem version = (ContentItem) copy(item);

	s_log.debug("The copy is pending; associating it with " +
		    "the draft item");
	
	version.setVersion(ContentItem.PENDING);
	item.addPendingVersion(version);

        version.copyServicesFrom(item);

        m_trace.exit("createVersion", version);

        return version;
    }
}
