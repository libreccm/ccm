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

package com.arsdigita.aplaws.ui;


import com.arsdigita.aplaws.Aplaws;
// import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
// import com.arsdigita.bebop.form.Widget;
// import com.arsdigita.categorization.ui.ACSObjectCategoryForm;
// import com.arsdigita.persistence.SessionManager;
// import com.arsdigita.persistence.DataCollection;
// import com.arsdigita.domain.DomainCollection;
// import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.kernel.ACSObject;
import com.arsdigita.london.terms.Term;
import com.arsdigita.london.terms.Domain;

import java.util.Collection;
import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;

import org.apache.log4j.Logger;

/**
 * Extends com.arsdigita.london.terms.ui.ACSObjectCategoryPicker and overwrites
 * the private class ItemCategoryFormCompletion to add functionality to its
 * lgclSelected() method.
 * 
 */
public abstract class ACSObjectCategoryPicker
                extends com.arsdigita.london.terms.ui.ACSObjectCategoryPicker{

    private static final Logger s_log = Logger.getLogger(ACSObjectCategoryPicker.class);

    /**
     * Constructor
     * 
     * @param root
     * @param mode
     */
    public ACSObjectCategoryPicker(BigDecimalParameter root,
            StringParameter mode) {
        super(root,mode);
    }


    /**
     * Overwrites private class of the parent class to add functionality for
     * processing or mapping from LGCL to APLAWS-NAV in method lgclSelected().
     */
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

        /**
         * Adds processing or mapping from LGCL to APLAWS-NAV too.
         *
         * ANav and the corresponding configuration parameter in module
         * ccm-ldn-aplaws are highly specific to the needs of British Local
         * Authorities and should not be used in more general parts of the
         * code.
         * 
         * @param domain
         * @param object
         */
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
            boolean lgclOverrideAnav = Aplaws.getAplawsConfig().
                                       getOverrideAnavFromLGCLMappings().booleanValue();
            if (lgclOverrideAnav) {
                Domain aplawsNav = Domain.retrieve("APLAWS-NAV");
                Collection aplawsNavTerms = getRelatedTerms(lgclTerms, aplawsNav);
				clearTerms(aplawsNav, object);
				assignTerms(aplawsNavTerms, object);
			}
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


}
