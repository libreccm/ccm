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
import com.arsdigita.persistence.Filter;
import com.arsdigita.util.Assert;

public class MixedHierarchyBuilder {
    
    private static final Logger s_log = 
        Logger.getLogger(MixedHierarchyBuilder.class);

    private Domain m_src_domain = null;    
    private Domain m_dst_domain = null;    
        
    public void findSourceDomain(String url) 
        throws Exception {
        m_src_domain = Domain.find(new URL(url));
        s_log.debug("Src Domain " + m_src_domain);
    }
    
    public void findDestinationDomain(String url) 
        throws Exception {
        m_dst_domain = Domain.find(new URL(url));
        s_log.debug("Dest Domain " + m_dst_domain);
        removeNarrowerTerms();
    }
    
    /**
	 *  Find all the terms and remove all 
	 *  the  narrower associations in each
	 */
	private void removeNarrowerTerms() {
		s_log.debug("Removing narrower terms from domain "+m_src_domain);
		DomainCollection allTerms = m_src_domain.getTerms();
		while(allTerms.next())
		{ 
			Term t = (Term)allTerms.getDomainObject();
			DomainCollection narrowerTerms = t.getNarrowerTerms();
			s_log.debug("Term "+t+" has narrower terms "+narrowerTerms);
			s_log.debug(" narrower terms have "+narrowerTerms.size()+" terms");

			// ok, we have all the narrower terms
			// but really we only want the ones that are in the destination 
			// domain, so lets try and filter them.
			Filter filter = narrowerTerms.getFilterFactory().equals(Term.DOMAIN,m_dst_domain.getURL().toExternalForm());			
			narrowerTerms.addFilter(filter);
			
			// ok, now we have filtered them
			
			s_log.debug("Term "+t+" has filtered narrower terms "+narrowerTerms+" from domain "+m_dst_domain);
			s_log.debug("filtered narrower terms have "+narrowerTerms.size()+" terms");

			while(narrowerTerms.next())
			{
				Term narrow = (Term)narrowerTerms.getDomainObject();
				t.removeNarrowerTerm(narrow);
				
			}
		}		
		
	}

    public void addNarrowerTerm(Integer id,
                                Integer narrowerID,
                                Boolean isDefault,
                                Boolean isPreferred) {
        Assert.exists(m_src_domain, Domain.class);
        Assert.exists(m_dst_domain, Domain.class);
        Assert.exists(id, Integer.class);
        Assert.exists(narrowerID, Integer.class);        

        Term term = m_src_domain.getTerm(id);
        Term narrowerTerm = m_dst_domain.getTerm(narrowerID);
        term.addNarrowerTerm(narrowerTerm,
                             isDefault == null ? 
                             false : isDefault.booleanValue(),
                             isPreferred == null ? 
                             true : isPreferred.booleanValue());

        s_log.debug("addNarrowerTerm " + term + " Narrower " + narrowerTerm);        
        term.save();
    }
}
