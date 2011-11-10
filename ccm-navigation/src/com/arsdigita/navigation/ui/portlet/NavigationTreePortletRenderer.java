/*
 * Copyright (C) 2008 Permeance Technologies Pty Ltd. All Rights Reserved.
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 */

package com.arsdigita.navigation.ui.portlet;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeSet;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.portal.AbstractPortletRenderer;
import com.arsdigita.categorization.Category;
import com.arsdigita.categorization.CategoryCollection;
import com.arsdigita.cms.TemplateContext;
import com.arsdigita.navigation.Navigation;
import com.arsdigita.navigation.portlet.NavigationTreePortlet;
// import com.arsdigita.london.portal.ui.PortalConstants;
import com.arsdigita.persistence.OID;
import com.arsdigita.web.ParameterMap;
import com.arsdigita.web.URL;
import com.arsdigita.web.Web;
import com.arsdigita.xml.Element;
import com.arsdigita.xml.XML;

/**
 * Renders a {@link NavigationTreePortlet}.
 * 
 * @author <a href="https://sourceforge.net/users/terry_permeance/">terry_permeance</a>
 */
public class NavigationTreePortletRenderer extends AbstractPortletRenderer
{
    // XXX quick hack: keep in Sync with com.arsdigita.london.portal.ui.PortalConstants!
    private static final String PORTLET_XML_NS = "http://www.uk.arsdigita.com/portlet/1.0";
    private final NavigationTreePortlet m_portlet;

    /**
     * 
     * @param portlet
     */
    public NavigationTreePortletRenderer(NavigationTreePortlet portlet)
    {
        m_portlet = portlet;
    }

    /**
     * 
     * @param state
     * @param parent
     */
    public void generateBodyXML(PageState state, Element parent)
    {
        Element element = parent.newChildElement("portlet:contentDirectory", PORTLET_XML_NS);
        element.addAttribute("id", getIdAttr());

        TemplateContext ctx = Navigation.getContext().getTemplateContext();
        String context = (ctx == null ? null : ctx.getContext());
        Category root = Category.getRootForObject(m_portlet.getNavigation(), context);
        if (root == null)
        {
            root = Category.getRootForObject(m_portlet.getNavigation());
        }
        if (!root.isEnabled())
        {
            return;
        }

        CategoryCollection cats = root.getDescendants();
        cats.addEqualsFilter("parents.link.relationType", "child");
        cats.addPath("parents.link.sortKey");
        cats.addPath("parents.id");
        cats.addOrder("parents.link.sortKey");

        Map children = new HashMap();
        while (cats.next())
        {
            Category cat = cats.getCategory();
            BigDecimal parentID = (BigDecimal) cats.get("parents.id");

            TreeSet childList = (TreeSet) children.get(parentID);
            if (childList == null)
            {
                childList = new TreeSet();
                children.put(parentID, childList);
            }

            childList.add(new CategorySortKeyPair(cat, (BigDecimal) cats.get("parents.link.sortKey")));
        }

        processChildren(element, root, children, 1, m_portlet.getDepth());
    }

    /**
     * 
     * @param parent
     * @param cat
     * @param children
     * @param depth
     * @param maxDepth
     */
    public void processChildren(Element parent, Category cat, Map children, int depth, int maxDepth)
    {
        if (depth <= maxDepth)
        {
            TreeSet c = (TreeSet) children.get(cat.getID());
            if (c != null)
            {
                Iterator i = c.iterator();
                while (i.hasNext())
                {
                    CategorySortKeyPair pair = (CategorySortKeyPair) i.next();
                    Category child = pair.getCategory();
                    BigDecimal childSortKey = pair.getSortKey();
                    if (child.isEnabled())
                    {
                        Element el = generateCategory(child, depth, childSortKey);
                        parent.addContent(el);

                        processChildren(el, child, children, depth + 1, maxDepth);
                    }
                }
            }
        }
    }

    /**
     * 
     * @param cat
     * @param depth
     * @param childSortKey
     * @return
     */
    public Element generateCategory(Category cat, int depth, BigDecimal childSortKey)
    {
        Element el = new Element(depth == 1 ? "portlet:contentDirectoryEntry" : "portlet:contentDirectorySubentry",
                PORTLET_XML_NS);

        el.addAttribute("id", XML.format(cat.getID()));
        el.addAttribute("name", cat.getName());
        el.addAttribute("description", cat.getDescription());
        el.addAttribute("isAbstract", cat.isAbstract() ? "1" : "0");
        el.addAttribute("url", redirectURL(cat.getOID()));
        el.addAttribute("sortKey", XML.format(childSortKey));
        return el;
    }

    /**
     * 
     * @param oid
     * @return
     */
    public static String redirectURL(OID oid)
    {
        ParameterMap map = new ParameterMap();
        map.setParameter("oid", oid.toString());

        URL here = Web.getContext().getRequestURL();

        return (new URL(here.getScheme(), here.getServerName(), here.getServerPort(), "", "", "/redirect/", map)).toString();
    }

    /**
     * 
     */
    private class CategorySortKeyPair implements Comparable
    {
        private Category m_category;

        private BigDecimal m_sortKey;

        public CategorySortKeyPair(Category category, BigDecimal sortKey)
        {
            m_category = category;
            m_sortKey = sortKey;
        }

        public Category getCategory()
        {
            return m_category;
        }

        public BigDecimal getSortKey()
        {
            return m_sortKey;
        }

        public int compareTo(Object o)
        {
            return m_sortKey.compareTo(((CategorySortKeyPair) o).m_sortKey);
        }
    }
}
