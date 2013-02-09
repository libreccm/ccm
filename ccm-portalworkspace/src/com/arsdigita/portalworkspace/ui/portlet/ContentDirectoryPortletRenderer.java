/*
 * Copyright (C) 2001 ArsDigita Corporation. All Rights Reserved.
 *
 * The contents of this file are subject to the ArsDigita Public 
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of
 * the License at http://www.arsdigita.com/ADPL.txt
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */

package com.arsdigita.portalworkspace.ui.portlet;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.portal.AbstractPortletRenderer;
import com.arsdigita.categorization.Category;
import com.arsdigita.categorization.CategoryCollection;
import com.arsdigita.portalworkspace.portlet.ContentDirectoryPortlet;
import com.arsdigita.persistence.OID;
import com.arsdigita.portalworkspace.WorkspacePage;
import com.arsdigita.web.ParameterMap;
import com.arsdigita.web.URL;
import com.arsdigita.web.Web;
import com.arsdigita.xml.Element;
import com.arsdigita.xml.XML;

public class ContentDirectoryPortletRenderer extends AbstractPortletRenderer {
	private static Logger s_log = Logger
			.getLogger(ContentDirectoryPortletRenderer.class.getName());

	private ContentDirectoryPortlet m_portlet;

	/**
     * Constructor.
     * 
     * @param portlet
     */
    public ContentDirectoryPortletRenderer(ContentDirectoryPortlet portlet) {
		m_portlet = portlet;
	}

	/**
     * 
     * @param state
     * @param parent
     */
    public void generateBodyXML(PageState state, Element parent) {
		Element element = parent.newChildElement("portlet:contentDirectory",
				WorkspacePage.PORTLET_XML_NS);
		element.addAttribute("id", getIdAttr());
		element.addAttribute("layout", m_portlet.getLayout());

		Category root = m_portlet.getRoot();

		if (!root.isEnabled()) {
			return;
		}

		CategoryCollection cats = root.getDescendants();
		cats.addEqualsFilter("parents.link.relationType", "child");
		cats.addPath("parents.link.sortKey");
		cats.addPath("parents.id");
		cats.addOrder("parents.link.sortKey");

		Map children = new HashMap();
		while (cats.next()) {
			Category cat = cats.getCategory();
			BigDecimal parentID = (BigDecimal) cats.get("parents.id");

			TreeSet childList = (TreeSet) children.get(parentID);
			if (childList == null) {
				childList = new TreeSet();
				children.put(parentID, childList);
			}

			childList.add(new CategorySortKeyPair(cat, (BigDecimal) cats
					.get("parents.link.sortKey")));
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
    public void processChildren(Element parent, Category cat, Map children,
			int depth, int maxDepth) {
		if (depth <= maxDepth) {
			TreeSet c = (TreeSet) children.get(cat.getID());
			if (c != null) {
				Iterator i = c.iterator();
				while (i.hasNext()) {
					CategorySortKeyPair pair = (CategorySortKeyPair) i.next();
					Category child = pair.getCategory();
					BigDecimal childSortKey = pair.getSortKey();
					if (child.isEnabled()) {
						Element el = generateCategory(child, depth,
								childSortKey);
						parent.addContent(el);

						processChildren(el, child, children, depth + 1,
								maxDepth);
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
    public Element generateCategory(Category cat, int depth,
			BigDecimal childSortKey) {
		Element el = new Element(depth == 1 ? "portlet:contentDirectoryEntry"
				: "portlet:contentDirectorySubentry",
				WorkspacePage.PORTLET_XML_NS);

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
    public static String redirectURL(OID oid) {
		ParameterMap map = new ParameterMap();
		map.setParameter("oid", oid.toString());

		URL here = Web.getContext().getRequestURL();

		return (new URL(here.getScheme(), here.getServerName(), here
				.getServerPort(), "", "", "/redirect/", map)).toString();
	}

	/**
     * 
     */
    private class CategorySortKeyPair implements Comparable {
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

		public int compareTo(Object o) {
			return m_sortKey.compareTo(((CategorySortKeyPair) o).m_sortKey);
		}
	}
}
