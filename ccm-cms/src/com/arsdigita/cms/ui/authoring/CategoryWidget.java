/*
 * Copyright (C) 2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.cms.ui.authoring;

import com.arsdigita.bebop.form.Widget;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.parameters.ArrayParameter;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.xml.Element;
import com.arsdigita.xml.XML;
import com.arsdigita.persistence.OID;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.categorization.Category;
import com.arsdigita.categorization.CategoryCollection;
import com.arsdigita.cms.CMS;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.math.BigDecimal;

public class CategoryWidget extends Widget {
    
    private BigDecimalParameter m_root;
    private StringParameter m_mode;

    public CategoryWidget(String name,
                          BigDecimalParameter root,
                          StringParameter mode) {
        super(new ArrayParameter(new BigDecimalParameter(name)));
        
        m_root = root;
        m_mode = mode;
    }

    protected String getType() {
        return "category";
    }
    public boolean isCompound() {
        return false;
    }
    
    protected void generateWidget(PageState state, 
                                  Element parent) {
        Element widget = parent.newChildElement("cms:categoryWidget",
                                                CMS.CMS_XML_NS);
        exportAttributes(widget);

        widget.addAttribute("mode", (String)state.getValue(m_mode));
        widget.addAttribute("name", getName());

        Set ids = new HashSet();

        BigDecimal[] values = (BigDecimal[])getValue(state);
        if (values != null) {
            for (int i = 0 ; i < values.length ; i++) {
                ids.add(values[i]);
            }
        }
        
        Category root = (Category)DomainObjectFactory.newInstance(
            new OID(Category.BASE_DATA_OBJECT_TYPE,
                    (BigDecimal)state.getValue(m_root))
        );

        CategoryCollection cats = root.getDescendants();
        cats.addEqualsFilter("parents.link.relationType", "child");
        cats.addPath("parents.link.sortKey");
        cats.addPath("parents.id");
        
        Map children = new HashMap();
        while (cats.next()) {
            Category cat = cats.getCategory();
            BigDecimal parentID = (BigDecimal)cats.get("parents.id");
            
            List childList = (List)children.get(parentID);
            if (childList == null) {
                childList = new ArrayList();
                children.put(parentID, childList);
            }
            
            childList.add(new CategorySortKeyPair
                          (cat,(BigDecimal)cats.get("parents.link.sortKey")));
        }
        
        generateCategory(widget, null, root, null, ids, children);
    }

    public void generateCategory(Element parent,
                                 String path,
                                 Category cat,
                                 BigDecimal sortKey,
                                 Set selected,
                                 Map children) {
        Element el = new Element("cms:category",
                                 CMS.CMS_XML_NS);
        
        el.addAttribute("id", XML.format(cat.getID()));
        el.addAttribute("name", cat.getName());
        el.addAttribute("description", cat.getDescription());
        el.addAttribute("isSelected", selected.contains(cat.getID()) ? "1" : "0");
        el.addAttribute("isAbstract", cat.isAbstract() ? "1" : "0");
        el.addAttribute("isEnabled", cat.isEnabled() ? "1" : "0");
        if (sortKey != null) {
            el.addAttribute("sortKey", sortKey.toString());
        }
        
        String fullname = path == null ? "/" : path + " > " + cat.getName();
        el.addAttribute("fullname", fullname);
	StringBuffer nodeID = new StringBuffer(parent.getAttribute("node-id"));
	if (nodeID.length() > 0) nodeID.append("-");
	nodeID.append(cat.getID());
	el.addAttribute("node-id", nodeID.toString());        
        parent.addContent(el);

        List c = (List)children.get(cat.getID());
        if (c != null) {
            Iterator i = c.iterator();
            while (i.hasNext()) {
                CategorySortKeyPair pair = (CategorySortKeyPair) i.next();
                Category child = pair.getCategory();
                BigDecimal childSortKey = pair.getSortKey();
                generateCategory(el, fullname, child, 
                                 childSortKey, selected, children);
            }
        }        
    }
    private class CategorySortKeyPair {
        private Category m_category;
        private BigDecimal m_sortKey;

        public CategorySortKeyPair(Category category, BigDecimal sortKey) {
            m_category = category;
            m_sortKey = sortKey;
        }
        public Category getCategory() {
            return m_category;
        }
        public BigDecimal getSortKey() {
            return m_sortKey;
        }
    }
}
