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

import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainCollection;
import com.arsdigita.london.terms.Domain;
import com.arsdigita.london.terms.Term;
import com.arsdigita.util.Assert;

public class RelatedBuilder {
    
    private static final Logger s_log = 
        Logger.getLogger(RelatedBuilder.class);

    private Domain m_src_domain;    
    private Domain m_dst_domain;

    private boolean m_removed_related_terms = false;    
        
    public void findSourceDomain(String url) 
        throws Exception {
        m_src_domain = Domain.find(new URL(url));
        s_log.debug("Src Domain " + m_src_domain);
        
    }
    
    public void findDestinationDomain(String url) 
        throws Exception {
        m_dst_domain = Domain.find(new URL(url));
        s_log.debug("Dest Domain " + m_dst_domain);
    }
    
    /**
     * 
     * remove all the related terms from the
     * terms in the source domain to the destination domain, 
     * or within the source domain if the destination domain 
     * is null. 
     * This only happens on the first call.
     *
     */
    private void removeRelatedTerms()    {
        Assert.exists(m_src_domain);
        s_log.debug("removing related terms from domain "+m_src_domain);
        if(m_removed_related_terms)  {
            //done this!!
            s_log.warn("This method has been called twice. This is unexpected.");
            return;
            }
        m_removed_related_terms = true;
        DomainCollection srcTerms = m_src_domain.getTerms();
        s_log.debug("got terms "+srcTerms);
        Domain domain = m_dst_domain;
        if(m_dst_domain == null) {
            s_log.debug("destination domain is null. Assume related terms within a single domain");
            domain = m_src_domain;
            
            }
        while(srcTerms.next())        {
            Term srcTerm = (Term)srcTerms.getDomainObject();
            DomainCollection relatedTerms = srcTerm.getRelatedTerms(domain);
            while(relatedTerms.next()){
                Term related = (Term)relatedTerms.getDomainObject();                
                srcTerm.removeRelatedTerm(related);
            }
        }
    }
    

    public void addRelatedTerm(Integer srcID,
                               Integer dstID) {
        Assert.exists(m_src_domain, Domain.class);
        Assert.exists(srcID, Integer.class);
        Assert.exists(dstID, Integer.class);   

        if(!m_removed_related_terms) {
            removeRelatedTerms();
        }

        Term srcTerm;
        try {
            srcTerm = m_src_domain.getTerm(srcID);
        } catch (DataObjectNotFoundException ex) {
            s_log.warn("Not mapping from non-existent term " + srcID +
                       " in domain " + m_src_domain.getKey());
            return;
        }

        Domain dst_domain = m_dst_domain == null ? m_src_domain : m_dst_domain;
        Term dstTerm;
        try {
            dstTerm = dst_domain.getTerm(dstID);
        } catch (DataObjectNotFoundException ex) {
            s_log.warn("Not mapping to non-existent term " + dstID +
                       " in domain " + dst_domain.getKey());
            return;
        }

        srcTerm.addRelatedTerm(dstTerm);
        s_log.debug("Term " + srcTerm + " related " + dstTerm);
        srcTerm.save();
    }
}
