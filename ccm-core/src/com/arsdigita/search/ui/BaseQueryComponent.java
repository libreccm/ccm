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
package com.arsdigita.search.ui;

import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.util.Traversal;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormModel;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.parameters.ParameterData;
import com.arsdigita.xml.Element;

import com.arsdigita.globalization.GlobalizedMessage;

import com.arsdigita.search.FilterSpecification;
import com.arsdigita.search.Search;

import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;

import org.apache.log4j.Logger;


/**
 * This is a simple extension of the QueryComponent that
 * provides management of the 'terms' parameter and uses 
 * FilterGenerators to populate a query specification
 * <p>
 * Typical use would be as follows:
 *<pre>
 * Form f = new Form("search");
 * BaseQueryComponent q = new BaseQueryComponent();
 * q.add(new ObjectTypeFilterComponent("com.arsdigita.kernel.User");
 * q.add(new PermissionGenerator(PrivilegeDescriptor.READ));
 * q.add(new Submit("Go"));
 * f.add(q);
 *</pre>
 */
public class BaseQueryComponent extends QueryComponent {

    private static final Logger s_log =
        Logger.getLogger(BaseQueryComponent.class);
    
    private Set m_filters;
    private Form m_form;
    private StringParameter m_terms = new StringParameter("terms");

    /**
     * Creates a new query component
     */
    public BaseQueryComponent() {
        super("query");
        m_filters = new HashSet();
    }
    
    @Override
    public void register(Page p) {
        super.register(p);
        
        findFilters(m_filters);
    }
    
    @Override
    public void register(Form form, FormModel model) {
        if (s_log.isDebugEnabled()) {
            s_log.debug("Adding " + m_terms.getName() + " to form model");
        }
	s_log.debug("Adding " + m_terms.getName() + " to form model");
        m_terms.setPassIn(true);
        model.addFormParam(m_terms);
        m_form = form;
    }
        
    /**
     * Gets the current search terms
     */
    protected String getTerms(PageState state) {
        FormData fd = m_form.getFormData(state);
        if (fd != null) {
            ParameterData data = fd.getParameter(m_terms.getName());
	    s_log.debug("Search terms were : " + (String)data.getValue());
            return (String)data.getValue();
        }
        return null;
    }


    protected FilterSpecification[] getFilters(PageState state) {
        FilterSpecification[] filters = new FilterSpecification[m_filters.size()];
        
        Iterator i = m_filters.iterator();
        int c = 0;
        while (i.hasNext()) {
            FilterGenerator filter = (FilterGenerator)i.next();
            filters[c++] = filter.getFilter(state);
        }

        return filters;
    }


    @Override
    public void generateXML(PageState state,
                            Element parent) {
        Element content = generateParent(parent);
        
        Element terms = Search.newElement("terms");
        terms.addAttribute("param", m_terms.getName());
        terms.addAttribute("value", getTerms(state));
        generateErrorXML(state, terms);
        content.addContent(terms);

        generateChildrenXML(state, content);
    }

    protected void generateErrorXML(PageState state,
                                    Element parent) {
        FormData f = m_form.getFormData(state);
        if (f == null) {
            return;
        }
        Iterator i = f.getErrors(m_terms.getName());

        while (i.hasNext()) {
            Element error = Search.newElement("error");
            error.setText(
                (String) ((GlobalizedMessage) i.next()).localize(state.getRequest())
            );
            parent.addContent(error);
        }
    }

    protected void findFilters(Set filters) {
        FilterTraversal trav = new FilterTraversal(filters);
        trav.preorder(this);
    }
    
    private class FilterTraversal extends Traversal {
        private Set m_filters;
        
        public FilterTraversal(Set filters) {
            m_filters = filters;
        }

        public void act(Component c) {
            if (c instanceof FilterGenerator) {
                m_filters.add(c);
            }
        }
    }
}
