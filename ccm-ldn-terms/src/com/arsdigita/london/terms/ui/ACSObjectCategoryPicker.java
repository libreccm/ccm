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

package com.arsdigita.london.terms.ui;

import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.categorization.ui.ACSObjectCategoryForm;
import com.arsdigita.domain.DomainCollection;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.kernel.ACSObject;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.london.terms.Term;
import com.arsdigita.london.terms.Domain;

import java.util.Collection;
import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;

import org.apache.log4j.Logger;

/**
 * abstracted from original version of 
 * c.ad.aplaws.ui.ItemCategoryPicker r1297
 * 
 * @author Chris Gilbert
 */
// NON JavaDoc:
// copied from c.ad.aplaws.ui (module ccm-ldn-aplaws) in order to avoid a
// dependency from the integration layer for forum-categorised. Otherwise you
// have had to specify the specific integration layer (i.e. ccm-???-aplaws) in
// application.xml for compiling, which may be different for each installation.
public abstract class ACSObjectCategoryPicker extends SimpleContainer {

    private static final Logger s_log = Logger.getLogger(ACSObjectCategoryPicker.class);

    private ACSObjectCategoryForm m_form;
    private BigDecimalParameter m_root;

    public ACSObjectCategoryPicker(BigDecimalParameter root,
                                   StringParameter mode)     {
        
		m_form = getForm(root, mode);
        m_root = root;

        add(m_form);
        m_form.addCompletionListener(new ItemCategoryFormCompletion());
    }
    
    protected abstract ACSObjectCategoryForm getForm(BigDecimalParameter root,
                                                     StringParameter mode);
     
    protected abstract ACSObject getObject(PageState state);
  
   
 
    
    private class ItemCategoryFormCompletion implements ActionListener {
        public void actionPerformed(ActionEvent ev) {
	    
            PageState state = ev.getPageState();
            Domain domain = getDomain(state);
            String domainKey = domain.getKey();

            if (s_log.isDebugEnabled()) {
                s_log.debug("Saving categories in: " + domainKey);
            }

			ACSObject object = getObject(state);
            
            if ("LGCL".equals(domainKey)) {
                lgclSelected(domain, object);
            }

            else if ("LGDL".equals(domainKey)) {
                lgdlSelected(domain, object);
            }

            fireCompletionEvent(state);                                   
        }

        private void lgclSelected(Domain domain, ACSObject object) {
            List lgclTerms = getCurrentCategories(domain, object);

            Domain gcl = Domain.retrieve("GCL");
            Collection gclTerms = getRelatedTerms(lgclTerms, gcl);
            clearTerms(gcl, object);
            assignTerms(gclTerms, object);

            // The assignment below is removed to satisfy requirement 4.1,
            // use case 1 of the document "Metadata Improvements" version 1
            // by Camden, dated 23/01/05.
            //Domain lgsl = Domain.retrieve("LGSL");
            //Collection lgslTerms = getRelatedTerms(lgclTerms, lgsl);
			//clearTerms(lgsl, object);
			//assignTerms(lgslTerms, object);

            // adding processing or mapping from LGCL to APLAWS-NAV too
//  Deactivated here.
//  If needed 
//          boolean lgclOverrideAnav = Aplaws.getAplawsConfig().getOverrideAnavFromLGCLMappings().booleanValue();
//          if (lgclOverrideAnav) {
//              Domain aplawsNav = Domain.retrieve("APLAWS-NAV");
//              Collection aplawsNavTerms = getRelatedTerms(lgclTerms, aplawsNav);
//              clearTerms(aplawsNav, object);
//              assignTerms(aplawsNavTerms, object);
//          }
        }

        // User has selected a term in the LGDL hierarchy, which includes
        // terms from the LGSL. We're only interested in LGSL terms here.
        private void lgdlSelected(Domain domain, ACSObject object) {
            Domain lgsl = Domain.retrieve("LGSL");
            Domain gcl = Domain.retrieve("GCL");
            Domain lgcl = Domain.retrieve("LGCL");

            // We have a mapping LGSL -> LGCL based on the reverse of a
            // published mapping. We don't have a mapping LGSL -> GCL, so we
            // do LGSL -> LGCL -> GCL instead.

            List lgslTerms = getCurrentCategories(lgsl, object);
            Collection lgclTerms = getRelatedTerms(lgslTerms, lgcl);

            LinkedList lgclIDs = new LinkedList();
            Iterator i = lgclTerms.iterator();
            while (i.hasNext()) {
                Term term = (Term) i.next();
                lgclIDs.add(term.getModel().getID());
            }

            Collection gclTerms = getRelatedTerms(lgclIDs, gcl);

            clearTerms(lgcl, object);
            assignTerms(lgclTerms, object);

            clearTerms(gcl, object);
            assignTerms(gclTerms, object);
        }
    }
    
    protected List getCurrentCategories(Domain domain,
                                        ACSObject object) {
        if (s_log.isDebugEnabled()) {
            s_log.debug("Getting terms from " + domain + " to " + object);
        }
        DomainCollection terms = domain.getTerms();
        terms.addEqualsFilter("model.childObjects.id", object.getID());
        terms.addPath("model.id");

        List current = new LinkedList();
        while (terms.next()) {
            if (s_log.isDebugEnabled()) {
                s_log.debug("Got term " + terms.get("model.id"));
            }
            current.add(terms.get("model.id"));
        }
        return current;
    }

    // TODO move out of UI code
    public static Collection getCurrentTerms(Domain domain,
                                   ACSObject object) {
        if (s_log.isDebugEnabled()) {
            s_log.debug("Getting terms from " + domain + " to " + object);
        }
        Collection current = new LinkedList();
        DomainCollection terms = domain.getTerms();
        terms.addEqualsFilter("model.childObjects.id", object.getID());
        terms.addPath("model.id");
        while (terms.next()) {
            if (s_log.isDebugEnabled()) {
                s_log.debug("Got term " + terms.get("model.id"));
            }
            current.add(terms.getDomainObject());
        }
        return current;
    }

    // TODO move out of UI code
    public static Collection getRelatedTerms(Collection src,
                                   Domain domain) {
        if (s_log.isDebugEnabled()) {
            s_log.debug("Getting related terms to " + domain);
	    
        }
		if (src.isEmpty()) {
			// this is a hack, it would be better not to use a completion event listener as 
			// this is called even when the form is cancelled...
			return new LinkedList();
		}
        DomainCollection terms = domain.getTerms();
		// these next two lines build the query 
        terms.addEqualsFilter("model.parents.link.relationType", "related");
		terms.addFilter("model.parents.id in :ids").set("ids", src);
	
        Collection related = new LinkedList();
        while (terms.next()) {
            if (s_log.isDebugEnabled()) {
                s_log.debug("Got term " + terms.getDomainObject());
            }
            related.add(terms.getDomainObject());
        }
        return related;
    }
    
    protected void clearTerms(Domain domain,
                              ACSObject object) {
        if (s_log.isDebugEnabled()) {
            s_log.debug("Removing terms from " + domain + " to " + object);
        }
        Iterator terms = getCurrentTerms(domain, object).iterator();
        while (terms.hasNext()) {
            Term term = (Term)terms.next();
            if (s_log.isDebugEnabled()) {
                s_log.debug("Removing term " + term + " from " + object);
            }
            term.removeObject(object);
        }
    }


    // TODO move out of UI code
    public static void assignTerms(Collection terms,
                               ACSObject object) {
        if (s_log.isDebugEnabled()) {
            s_log.debug("Assigning terms to " + object);
        }
        Iterator i = terms.iterator();
        while (i.hasNext()) {
            Term term = (Term)i.next();
            if (s_log.isDebugEnabled()) {
                s_log.debug("Assigning term " + term + " to " + object);
            }
            term.addObject(object);
        }
    }

    protected Domain getDomain(PageState state) {
        if (s_log.isDebugEnabled()) {
            s_log.debug("Getting domain for " + state.getValue(m_root));
        }

        DataCollection domains = SessionManager.getSession()
            .retrieve(Domain.BASE_DATA_OBJECT_TYPE);
        domains.addEqualsFilter("model.id",
                                state.getValue(m_root));
        
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
