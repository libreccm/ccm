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

package com.arsdigita.london.util.ui;

import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.parameters.IntegerParameter;

import com.arsdigita.domain.DomainObject;
import com.arsdigita.domain.DomainCollection;
import com.arsdigita.domain.DomainObjectXMLRenderer;

import com.arsdigita.xml.Element;
import com.arsdigita.web.URL;
import com.arsdigita.web.ParameterMap;
import com.arsdigita.web.Web;

import java.util.Iterator;

public abstract class AbstractDomainObjectList 
    extends AbstractDomainObjectComponent {
    
    private String m_prefix;
    private int m_pageSize;
    private IntegerParameter m_pageNumber;

    public AbstractDomainObjectList(String name,
                                    String prefix,
                                    String xmlns) {
        super(prefix + ":" + name, xmlns);
        m_prefix = prefix;
        m_pageNumber = new IntegerParameter("pageNumber");
    }

    public void register(Page p) {
        super.register(p);
        
        p.addComponentStateParam(this, m_pageNumber);
    }
    
    public void setPageSize(int pageSize) {
        m_pageSize = pageSize;
    }

    protected abstract DomainCollection getDomainObjects(PageState state);
    
    public void generateXML(PageState state,
                            Element parent) {
        Element content = generateParent(parent);
        
        DomainCollection objs = getDomainObjects(state);

        if (m_pageSize != 0) {
            Element pagEl = generatePaginatorXML(state, 
                                             objs);
            content.addContent(pagEl);
        }

        while (objs.next()) {
            Element el = generateObjectXML(state, objs.getDomainObject());
            content.addContent(el);
        }
    }
    
    protected Element generateObjectXML(PageState state,
                                        DomainObject dobj) {
        Element objEl = new Element(m_prefix + ":object",
                                    getNamespace());
        
        DomainObjectXMLRenderer xr = new DomainObjectXMLRenderer(objEl);
        xr.setNamespace(m_prefix, getNamespace());
        xr.setWrapRoot(false);
        xr.setWrapAttributes(true);
        xr.setWrapObjects(false);
        
        xr.walk(dobj,
                getClass().getName());
        
        Iterator actions = getDomainObjectActions();
        while (actions.hasNext()) {
            String action = (String)actions.next();
            Element el = generateActionXML(state, dobj, action);
            objEl.addContent(el);
        }
        return objEl;
    }
    
    protected Element generatePaginatorXML(PageState state,
                                           DomainCollection objs) {
        Integer pageNumberVal = (Integer)state.getValue(m_pageNumber);
        int pageNumber = pageNumberVal == null ? 1 : pageNumberVal.intValue();
        
        long objectCount = objs.size();
        int pageCount = (int)Math.ceil((double)objectCount / (double)m_pageSize);
        
        if (pageNumber < 1) {
            pageNumber = 1;
        }

        if (pageNumber > pageCount) {
            pageNumber = (pageCount == 0 ? 1 : pageCount);
        }
        
        long begin = ((pageNumber-1) * m_pageSize);
        int count = (int)Math.min(m_pageSize, (objectCount - begin));
        long end = begin + count;
        
        if (count != 0) {
            objs.setRange(new Integer((int)begin+1), new Integer((int)end+1));
        }
        

        URL url = Web.getContext().getRequestURL();
        ParameterMap map = new ParameterMap();
        Iterator current = url.getParameterMap().keySet().iterator();
        while (current.hasNext()) {
            String key = (String)current.next();
            if (key.equals(m_pageNumber.getName())) {
                continue;
            }
            map.setParameterValues(key, url.getParameterValues(key));
        }


        Element paginator = new Element(m_prefix + ":paginator",
                                        getNamespace());
        paginator.addAttribute("pageNumber", new Long(pageNumber).toString());
        paginator.addAttribute("pageCount", new Long(pageCount).toString());
        paginator.addAttribute("pageSize", new Long(m_pageSize).toString());
        paginator.addAttribute("objectBegin", new Long(begin+1).toString());
        paginator.addAttribute("objectEnd", new Long(end).toString());
        paginator.addAttribute("objectCount", new Long(objectCount).toString());
        paginator.addAttribute("pageParam", m_pageNumber.getName());
        paginator.addAttribute("baseURL", URL.there(url.getPathInfo(), map)
                               .toString());

        return paginator;
    }
                                        

    protected Element generateActionXML(PageState state,
                                        DomainObject dobj,
                                        String action) {
        Element actionEl = new Element(m_prefix + ":action",
                                       getNamespace());
        actionEl.addAttribute("name", action);
        actionEl.addAttribute("url", 
                              getDomainObjectActionLink(state, dobj, action));
        return actionEl;
    }
}
