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
package com.arsdigita.london.terms.ui;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.form.Widget;
import com.arsdigita.bebop.parameters.ArrayParameter;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.categorization.Category;
import com.arsdigita.cms.CMS;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.domain.DomainCollection;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.london.terms.Domain;
import com.arsdigita.london.terms.Term;
import com.arsdigita.london.terms.Terms;
import com.arsdigita.london.terms.indexing.Indexer;
import com.arsdigita.london.terms.indexing.RankedTerm;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.xml.Element;
import com.arsdigita.xml.XML;

/**
 * A Widget for selecting Terms. Based heavily on CategoryWidget.
 *
 * @author mbooth@redhat.com
 * 
 * Chris Gilbert - updated to identify each node uniquely (correct behaviour
 * for polyhierarchical trees) - also, allow ajax update on all branches or
 * just top level branch
 * 
 * nb - widget applies to allocation of categories to any ACSObject hence
 * xml prefix should be more generic eg bebop rather than cms. cms retained
 * for compatibility with existing stylesheets
 */
// NON Javadoc comment:
// Copied from c.ad.aplaws.ui in order to make forum-categorised independend from
// a specific ccm-???-aplaws, i.e. a specific integration layer.
public class TermWidget extends Widget {

    private StringParameter m_mode;
    private ACSObjectCategoryPicker m_picker;

    public TermWidget(StringParameter mode, ACSObjectCategoryPicker picker) {
        super(new ArrayParameter(new BigDecimalParameter("category")));

        m_mode = mode;
        m_picker = picker;

    }

    @Override
    protected String getType() {
        return "category";
    }

    @Override
    public boolean isCompound() {
        return false;
    }

    @Override
    protected void generateWidget(PageState state, Element parent) {
        Domain domain = m_picker.getDomain(state);

        Element widget = parent.newChildElement("cms:categoryWidget",
                CMS.CMS_XML_NS);
        exportAttributes(widget);

        widget.addAttribute("mode", (String) state.getValue(m_mode));
        widget.addAttribute("name", getName());

        Set ids = new HashSet();
        BigDecimal[] values = (BigDecimal[]) getValue(state);
        if (values != null) {
            ids.addAll(Arrays.asList(values));
        }

        Element selEl = widget.newChildElement("cms:selectedCategories", CMS.CMS_XML_NS);
        selEl.addAttribute("name", this.getName());
        Iterator selCats = ids.iterator();
        while (selCats.hasNext()) {
            Element selCat = selEl.newChildElement("cms:category", CMS.CMS_XML_NS);
            selCat.addAttribute("id", selCats.next().toString());
        }

        // only root terms at first, the rest is loaded on-demand via AJAX
        DomainCollection terms = domain.getRootTerms();
//        DomainCollection terms = domain.getTerms();
        terms.addPath("model.parents.link.sortKey");
        terms.addPath("model.parents.id");
        terms.addPath("domain.key");

        // Pull out everything related to the category, otherwise
        // another query per row is executed when doing term.getModel();
        terms.addPath("model.objectType");
        terms.addPath("model.displayName");
        terms.addPath("model.defaultDomainClass");
        terms.addPath("model.name");
        terms.addPath("model.description");
        terms.addPath("model.url");
        terms.addPath("model.isEnabled");
        terms.addPath("model.isAbstract");
        terms.addPath("model.defaultAncestors");

        List roots = new LinkedList();
        while (terms.next()) {
            Term term = (Term) terms.getDomainObject();
            roots.add(new TermSortKeyPair(term, (BigDecimal) terms.get("model.parents.link.sortKey")));
        }

        Element el = generateCategory(widget, domain.getModel(), ids, null);

        /**
         * Used by kea based keyphrase extraction facility.
         * (Added r1885) 
         * 
         * @Author: terry_permeance
         */
        Indexer indexer = Indexer.retrieve(domain);
        if (indexer != null) {
            ContentItem item = CMS.getContext().getContentItem();
            List<RankedTerm> autoTerms = indexer.index(item, 16);
            Element autoCategories = widget.newChildElement("cms:autoCategories", CMS.CMS_XML_NS);
            for (Iterator<RankedTerm> i = autoTerms.iterator(); i.hasNext();) {
                RankedTerm nextRankedTerm = i.next();
                Category cat = nextRankedTerm.getTerm().getModel();
                if (!ids.contains(cat.getID())) {
                    String fullname = cat.getQualifiedName(" > ", false);
                    if (fullname != null) {
                        Element catEl = autoCategories.newChildElement("cms:category", CMS.CMS_XML_NS);
                        catEl.addAttribute("id", XML.format(cat.getID()));
                        catEl.addAttribute("name", cat.getName());
                        catEl.addAttribute("description", cat.getDescription());
                        catEl.addAttribute("isAbstract", cat.isAbstract() ? "1" : "0");
                        catEl.addAttribute("isEnabled", cat.isEnabled() ? "1" : "0");
                        catEl.addAttribute("sortKey", nextRankedTerm.getRanking().toString());
                        catEl.addAttribute("fullname", fullname);
                    }
                }
            }
        }

        if (Terms.getConfig().ajaxExpandAllBranches()) {
        // add attribute to the parent node, so that in stylesheet
        // we can look for any ancestor with this attribute (can't
        // add attribute to categoryWidget element as that is not
        // visible when subbranches are transformed)
            el.addAttribute("expand",  "all" );
        }

        for (Iterator i = roots.iterator(); i.hasNext();) {
            TermSortKeyPair pair = (TermSortKeyPair) i.next();
            Term term = pair.getTerm();
            BigDecimal sortKey = pair.getSortKey();

            generateRootTerm(el, term, ids, sortKey);
        }
    }

    public static Element generateCategory(Element parent,
            Category cat,
            Set selected,
            BigDecimal sortKey) {
        Element el = parent.newChildElement("cms:category", CMS.CMS_XML_NS);

        el.addAttribute("id", XML.format(cat.getID()));
        el.addAttribute("name", cat.getName());
        el.addAttribute("description", cat.getDescription());
        el.addAttribute("isSelected", selected.contains(cat.getID()) ? "1" : "0");
        el.addAttribute("isAbstract", cat.isAbstract() ? "1" : "0");
        el.addAttribute("isEnabled", cat.isEnabled() ? "1" : "0");
        if (sortKey != null) {
            el.addAttribute("sortKey", sortKey.toString());
        }
        // sort order attribute added to every node so that we can 
        // correctly transform xml fragments returned by ajax 
        el.addAttribute("order", ContentSection.getConfig().getCategoryTreeOrder());
        el.addAttribute("genCat", "true");

        StringBuilder path = new StringBuilder(parent.getAttribute("fullname"));
        if (path.length() > 0) {
            path.append(" > ");
            
        }
        path.append(cat.getName());
        el.addAttribute("fullname", path.toString());

        // need to uniquely identify each node in polyhierarchical trees
        // so that expand/contract is applied to the correct node by
        // javascript getElementByID function
        StringBuilder nodeID = new StringBuilder(parent.getAttribute("node-id"));
        if (nodeID.length() > 0) {
            nodeID.append("-");
            
        }
        nodeID.append(cat.getID());
        el.addAttribute("node-id", nodeID.toString());

        return el;
    }

    public static Element generateTerm(Element parent,
            Term term,
            Set selected,
            BigDecimal sortKey) {
        Category cat = term.getModel();
        Element el = generateCategory(parent, cat, selected, sortKey);

        el.addAttribute("pid", term.getUniqueID().toString());
        el.addAttribute("domain", term.getDomain().getKey());
        return el;
    }

    private static void generateRootTerm(Element parent,
            Term term,
            Set selected,
            BigDecimal sortKey) {
        Element el = generateTerm(parent, term, selected, sortKey);
        el.addAttribute("root", "1");
    }

    public static void generateSubtree(Element parent, Category root, Set ids) {
        DataCollection terms = SessionManager.getSession().retrieve(
                Term.BASE_DATA_OBJECT_TYPE);
        terms.addEqualsFilter("model.roTransParents.id", root.getID());
        terms.addEqualsFilter("model.parents.link.relationType", "child");

        Map children = new HashMap();
        while (terms.next()) {
            Term term = (Term) DomainObjectFactory.newInstance(terms.getDataObject());
            BigDecimal parentID = (BigDecimal) terms.get("model.parents.id");

            List childList = (List) children.get(parentID);
            if (childList == null) {
                childList = new LinkedList();
                children.put(parentID, childList);
            }

            childList.add(new TermSortKeyPair(term, (BigDecimal) terms.get("model.parents.link.sortKey")));
        }

        Element el = generateCategory(parent, root, ids, null);
        el.addAttribute("fullname", root.getName());
        el.addAttribute("node-id", root.getID().toString());
        el.addAttribute("order", ContentSection.getConfig().getCategoryTreeOrder());
	if (Terms.getConfig().ajaxExpandAllBranches()) {
        //	recognisable attribute has to be in the XML for each snippet that is transformed,
        // hence add it to the parent
	    el.addAttribute("expand",  "all" );
	}

        List roots = (List) children.get(root.getID());
        if (null != roots) {
            Iterator i = roots.iterator();
            while (i.hasNext()) {
                TermSortKeyPair pair = (TermSortKeyPair) i.next();
                Term term = pair.getTerm();
                BigDecimal sortKey = pair.getSortKey();

                generateTermWithChildren(el, term, ids, sortKey, children);
            }
        }
    }

    private static void generateTermWithChildren(Element parent,
            Term term,
            Set selected,
            BigDecimal sortKey,
            Map children) {
        Category cat = term.getModel();
        Element el = generateCategory(parent, cat, selected, sortKey);

        el.addAttribute("pid", term.getUniqueID().toString());
        el.addAttribute("domain", term.getDomain().getKey());

        List c = (List) children.get(cat.getID());
        if (c != null) {
            Iterator i = c.iterator();
            while (i.hasNext()) {
                TermSortKeyPair pair = (TermSortKeyPair) i.next();
                Term child = pair.getTerm();
                BigDecimal childSortKey = pair.getSortKey();

                // either generate next level down, or get all levels below current

                if (Terms.getConfig().ajaxExpandAllBranches()) {
		    generateTerm(el, child, selected, childSortKey);

                } else {
		    generateTermWithChildren(el, child, selected, childSortKey,
		         children);
                }



            }
        }
    }

    private static class TermSortKeyPair {

        private Term m_term;
        private BigDecimal m_sortKey;

        public TermSortKeyPair(Term term, BigDecimal sortKey) {
            m_term = term;
            m_sortKey = sortKey;
        }

        public Term getTerm() {
            return m_term;
        }

        public BigDecimal getSortKey() {
            return m_sortKey;
        }
    }
}
