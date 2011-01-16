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

package com.arsdigita.london.atoz.ui.admin;

import com.arsdigita.kernel.ui.ACSObjectSelectionModel;
import com.arsdigita.london.atoz.AtoZCategoryProvider;
import com.arsdigita.london.terms.Domain;
import com.arsdigita.london.terms.Term;
import com.arsdigita.london.terms.Terms;

import com.arsdigita.aplaws.Aplaws;
import com.arsdigita.bebop.form.Widget;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.parameters.ArrayParameter;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.categorization.Category;
import com.arsdigita.domain.DomainCollection;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.xml.Element;
import com.arsdigita.xml.XML;

import com.arsdigita.cms.CMS;
import com.arsdigita.cms.ContentSection;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

/**
 * Copied from com.arsdigita.aplaws.ui.TermWidget.
 * ( com.arsdigita.aplaws.ui.TermWidget relocated to
 *   com.arsdigita.london.terms.ui.TermWidget   )
 *
 * @author clasohm@redhat.com
 */
public class TermWidget extends com.arsdigita.london.terms.ui.TermWidget {
	private static final Logger s_log = Logger.getLogger(TermWidget.class);
	
	private ACSObjectSelectionModel m_provider;

    public TermWidget(ACSObjectSelectionModel provider) {
		super(null, null);

		m_provider = provider;
	}
    
    @Override
    protected void generateWidget(PageState state,
                                  Element parent) {
        Domain domain = getDomain(state);

        Element widget = parent.newChildElement("cms:categoryWidget",
                                                CMS.CMS_XML_NS);
        exportAttributes(widget);

        widget.addAttribute("mode", "javascript");
        widget.addAttribute("name", getName());
        
        Set ids = new HashSet();

        BigDecimal[] values = (BigDecimal[])getValue(state);
        if (values != null) {
            for (int i = 0 ; i < values.length ; i++) {
                ids.add(values[i]);
            }
        }

        // only root terms at first, the rest is loaded on-demand via AJAX
        DomainCollection terms = domain.getRootTerms();  
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
            roots.add(new TermSortKeyPair
                          (term,(BigDecimal)terms.get("model.parents.link.sortKey")));
        }

        Element el = generateCategory(widget, domain.getModel(), ids, null);
        
        if (Terms.getConfig().ajaxExpandAllBranches()) {
        	// add attribute to the parent node, so that in stylesheet 
        	// we can look for any ancestor with this attribute (can't 
        	// add attribute to categoryWidget element as that is not 
        	// visible when subbranches are transformed)
        	el.addAttribute("expand",  "all" );
        }	
	
        for (Iterator i=roots.iterator(); i.hasNext(); ) {
            TermSortKeyPair pair = (TermSortKeyPair) i.next();
            Term term = pair.getTerm();
            BigDecimal sortKey = pair.getSortKey();

            generateRootTerm(el, term, ids, sortKey);
        }
    }

    private static void generateRootTerm(Element parent,
            Term term,
            Set selected,
            BigDecimal sortKey) {
    	Element el = generateTerm(parent, term, selected, sortKey);
    	el.addAttribute("root","1");
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

    /**
     *  copied from com.arsdigita.aplaws.ui.ACSObjectCategoryPicker 
     */
    protected Domain getDomain(PageState state) {
    	AtoZCategoryProvider provider = (AtoZCategoryProvider)m_provider.getSelectedObject(state);
    	Category root = Category.getRootForObject(provider);
    	
        if (s_log.isDebugEnabled()) {
            s_log.debug("Getting domain for " + root.getID());
        }

        DataCollection domains = SessionManager.getSession()
            .retrieve(Domain.BASE_DATA_OBJECT_TYPE);
        domains.addEqualsFilter("model.id", root.getID());
        
        if (domains.next()) {
            Domain domain = (Domain)DomainObjectFactory
                .newInstance(domains.getDataObject());
            if (s_log.isDebugEnabled()) {
                s_log.debug("Got domain " + domain);
            }
            domains.close();
            return domain;
        }
        if (s_log.isDebugEnabled()) {
            s_log.debug("No domain found");
        }
        return null;
    }
}
