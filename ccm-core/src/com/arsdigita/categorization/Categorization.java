/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.categorization;

import com.arsdigita.kernel.ACSObject;
import com.arsdigita.kernel.SiteNode;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * Provides static helper methods.
 *
 * @version $Revision: #5 $ $DateTime: 2004/08/16 18:10:38 $
 */
public class Categorization {
    
    private static List s_categoryListeners = new ArrayList();

    public static final String CONTEXT_NAVIGATION = "navigation";

    /**
     * A wrapper for {@link Categorization#getGlobalRootCategory(String)
     * getGlobalRootCategory(Categorization.CONTEXT_NAVIGATION)}.
     * @see #getGlobalRootCategory()
     **/
    public static Category getGlobalRootCategory() {
        return getGlobalRootCategory(CONTEXT_NAVIGATION);
    }

    /**
     * @see Category#getRootForObject(com.arsdigita.kernel.ACSObject)
     **/
    public static Category getGlobalRootCategory(String context) {
        // XXX change this impl.
        // XXX support context
        Category root = Category.getRootForObject(
            SiteNode.getRootSiteNode()
        );
        return root;
    }

    public static void addCategoryListener(CategoryListener cl) {
        s_categoryListeners.add(cl);
    }

    static void triggerDeletionEvent(Category cat) {
        Iterator it = s_categoryListeners.iterator();
        while (it.hasNext()) {
            CategoryListener cl = (CategoryListener) it.next();
            cl.onDelete(cat);
        }
    }

    static void triggerSetDefaultParentEvent(Category cat, Category parent) {
        Iterator it = s_categoryListeners.iterator();
        while (it.hasNext()) {
            CategoryListener cl = (CategoryListener) it.next();
            cl.onSetDefaultParent(cat, parent);
        }
    }

    static void triggerAddChildEvent(Category cat, Category child) {
        Iterator it = s_categoryListeners.iterator();
        while (it.hasNext()) {
            CategoryListener cl = (CategoryListener) it.next();
            cl.onAddChild(cat, child);
        }
    }

    static void triggerRemoveChildEvent(Category cat, Category child) {
        Iterator it = s_categoryListeners.iterator();
        while (it.hasNext()) {
            CategoryListener cl = (CategoryListener) it.next();
            cl.onRemoveChild(cat, child);
        }
    }

    static void triggerMapEvent(Category cat, ACSObject obj) {
        Iterator it = s_categoryListeners.iterator();
        while (it.hasNext()) {
            CategoryListener cl = (CategoryListener) it.next();
            cl.onMap(cat, obj);
        }
    }

    static void triggerUnmapEvent(Category cat, ACSObject obj) {
        Iterator it = s_categoryListeners.iterator();
        while (it.hasNext()) {
            CategoryListener cl = (CategoryListener) it.next();
            cl.onUnmap(cat, obj);
        }
    }

}
