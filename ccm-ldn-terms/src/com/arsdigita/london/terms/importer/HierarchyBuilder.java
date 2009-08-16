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

import org.apache.log4j.Logger;

import com.arsdigita.domain.DomainCollection;
import com.arsdigita.london.terms.Domain;
import com.arsdigita.london.terms.Term;
import com.arsdigita.util.Assert;

public class HierarchyBuilder {
    
    private static final Logger s_log = 
        Logger.getLogger(HierarchyBuilder.class);

    private Domain m_domain;    
        
    public void findDomain(String url) 
        throws Exception {
    	s_log.debug("Entering find domain");
        m_domain = Domain.find(new URL(url));
        s_log.debug("Domain " + m_domain);
        removeRootTerms();
        removeNarrowerTerms();
    }
    
    /**
	 *  Find all the terms and remove all 
	 *  the  narrower associations in each
	 */
	private void removeNarrowerTerms() {
		s_log.debug("Removing narrower terms from domain "+m_domain);
		DomainCollection allTerms = m_domain.getTerms();
		while(allTerms.next())
		{ 
			Term t = (Term)allTerms.getDomainObject();
			DomainCollection narrowerTerms = t.getNarrowerTerms();
			//s_log.debug("Term "+t+" has narrower terms "+narrowerTerms);
			while(narrowerTerms.next())
			{
				Term narrow = (Term)narrowerTerms.getDomainObject();
				//s_log.debug("removing term "+narrow+" from narrower association of term "+t);				
				t.removeNarrowerTerm(narrow);
				
			}
		}		
		
	}

	/**
	 *  Find the existing root terms in the current domain
	 * and remove them all
	 */
	private void removeRootTerms() {
		//s_log.debug("remvoing root terms of domain "+m_domain);
		DomainCollection dc = m_domain.getRootTerms();	
		while(dc.next())
		{ 
			Term t = (Term)dc.getDomainObject();
			//s_log.debug("removing root term "+t);
			m_domain.removeRootTerm(t);
		
		}		
	}

	public void addNarrowerTerm(Integer id,
                                Integer narrowerID,
                                Boolean isDefault,
                                Boolean isPreferred) {
        Assert.exists(m_domain, Domain.class);
        Assert.exists(id, Integer.class);
        Assert.exists(narrowerID, Integer.class);
        s_log.debug("addNarrowerTerm id" + id 
        		+ " narrowerID " + narrowerID
				+ " isDefault "+ isDefault
				+ " isPreferred "+isPreferred);

        Term term = m_domain.getTerm(id);
        Term narrowerTerm = m_domain.getTerm(narrowerID);
        term.addNarrowerTerm(narrowerTerm,
                             isDefault == null ? 
                             false : isDefault.booleanValue(),
                             isPreferred == null ? 
                             true : isPreferred.booleanValue());

        s_log.debug("addNarrowerTerm " + term.getName() + " Narrower " + narrowerTerm.getName());
        term.save();
    }
    
    public void addRootTerm(Integer id) {
        Assert.exists(m_domain, Domain.class);
        Assert.exists(id, Integer.class);
        s_log.debug("addRootTerm "+id);
        Term term = m_domain.getTerm(id);
        m_domain.addRootTerm(term);
        term.save();
    }
}
