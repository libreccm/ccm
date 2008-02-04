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
 */
package com.arsdigita.london.util;

import com.arsdigita.categorization.Category;

import com.arsdigita.persistence.SessionManager;
import com.arsdigita.domain.DomainServiceInterfaceExposer;
import com.arsdigita.domain.DomainCollection;

import java.util.Map;
import java.util.HashMap;
import java.util.TreeMap;
import java.math.BigDecimal;

import org.apache.log4j.Logger;

public class Categorization {
    
    private static final Logger s_log = Logger.getLogger(Categorization.class);
    
    /**
     * Retrieves the category subtree for the given root.
     * The returned map has String objects for full path
     * name as keys, and Category objects as values
     */
    public static Map categorySubtreePath(Category root) {
        return categorySubtreePath(root, " > ");
    }

    /**
     */
    public static Map categorySubtreePath(Category root, String join) {
        DomainCollection cats = new DomainCollection(
            SessionManager.getSession().retrieve(Category.BASE_DATA_OBJECT_TYPE)
        );
        cats.addFilter("defaultAncestors like :ancestors")
            .set("ancestors", 
                 ((String)DomainServiceInterfaceExposer
                  .get(root, "defaultAncestors")) + "%");
        cats.addEqualsFilter("parents.link.isDefault", Boolean.TRUE);
        cats.addOrder("defaultAncestors");
        cats.addPath("parents.id");

        Map path2cat = new TreeMap();
        Map cat2path = new HashMap();
        
        path2cat.put(root.getName(), root);
        cat2path.put(root.getID(), root.getName());

        while (cats.next()) {
            Category cat = (Category)cats.getDomainObject();
            BigDecimal parent = (BigDecimal)cats.get("parents.id");

            if (parent == null) {
                path2cat.put(cat.getName(), cat);
                cat2path.put(cat.getID(), cat.getName());
            } else {
                String parentPath = (String)cat2path.get(parent);
                String path = parentPath + join + cat.getName();
                path2cat.put(path, cat);
                cat2path.put(cat.getID(), path);
            }
        }

        return path2cat;
    }
}
