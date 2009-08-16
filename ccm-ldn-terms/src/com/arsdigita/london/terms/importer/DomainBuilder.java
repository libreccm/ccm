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

package com.arsdigita.london.terms.importer;

import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.london.terms.Domain;
import com.arsdigita.london.terms.Term;
import com.arsdigita.util.Assert;

public class DomainBuilder {
    
    private static final Logger s_log = 
        Logger.getLogger(DomainBuilder.class);
    
    private Domain m_domain;
    private List m_terms;

    public DomainBuilder() {
        m_terms = new ArrayList();
    }
        
    public void createDomain(String key,
                             String url,
                             String title,
                             String description,
                             String version,
                             String versionDate)
        throws Exception {
    	s_log.debug("key: "+key+" url: "+url+" title: "+title
    			+" description: "+description+" version: "+version
				+" versionDate: "+versionDate);

    	DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date released = format.parse(versionDate);

        Domain domain = null;
        try {
            domain = Domain.retrieve(key);
            s_log.debug("Retrieved Domain: "+domain);
            
            // Now update this.
            domain.setURL(new URL(url));
            domain.setTitle(title);
            domain.setDescription(description);
            domain.setVersion(version);
            domain.setReleased(released);
            
          	
        } catch (DataObjectNotFoundException ex) {

            domain = Domain.create(key,
                                   new URL(url),
                                   title,
                                   null,
                                   version,
                                   released);
            s_log.debug("created Domain "+domain);
        }
        m_domain = domain;
        s_log.debug("Domain " + m_domain);

        Iterator terms = m_terms.iterator();
        while (terms.hasNext()) {
            Object[] term = (Object[])terms.next();
            doAddTerm((Integer)term[0],
                      (String)term[1],
                      (Boolean)term[2],
                      (String)term[3]);
        }

        domain.save();
    }
    
    public void addTerm(Integer id,
                        String name,
                        Boolean inAtoZ,
                        String shortcut) 
        throws Exception {

        Assert.exists(id, Integer.class);
        Assert.exists(name, String.class);
        Assert.exists(inAtoZ, Boolean.class);
        
        m_terms.add(new Object[] {
            id, name, inAtoZ, shortcut
        });
    }
    
    
    public void doAddTerm(Integer id,
                          String name,
                          Boolean inAtoZ,
                          String shortcut) {
        Assert.exists(m_domain, Domain.class);

        Term term = null;
        try {
            term = m_domain.getTerm(id);
            term.setName(name);
            term.setInAtoZ(Boolean.TRUE.equals(inAtoZ));
            term.setShortcut(shortcut);
        } catch (DataObjectNotFoundException ex) {
            term = Term.create(id,
                               name,
                               Boolean.TRUE.equals(inAtoZ),
                               shortcut,
                               m_domain);
        }
        s_log.debug("doAddTerm " + term);
    }
}
