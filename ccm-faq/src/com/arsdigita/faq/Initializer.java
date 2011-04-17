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
 *
 */
package com.arsdigita.faq;

import com.arsdigita.db.*;
import com.arsdigita.dispatcher.ObjectNotFoundException;
import com.arsdigita.domain.*;
import com.arsdigita.faq.ui.FaqQuestionsPortlet;
import com.arsdigita.kernel.ACSObjectInstantiator;
import com.arsdigita.kernel.NoValidURLException;
import com.arsdigita.kernel.URLFinder;
import com.arsdigita.kernel.URLService;
import com.arsdigita.persistence.*;
import com.arsdigita.persistence.pdl.*;
import com.arsdigita.runtime.*;

/**
 * FAQ Initializer
 *
 * @author Jim Parsons &lt;jparsons@redhat.com&gt;
 * @author Peter Boy &lt;pboy@barkhof.uni-bremen.de&gt;
 * @version $Revision: #19 $ $Date: 2004/08/17 $
 **/

public class Initializer extends CompoundInitializer {


    public Initializer() {
        final String url = RuntimeConfig.getConfig().getJDBCURL();
        final int database = DbHelper.getDatabaseFromURL(url);

        add(new PDLInitializer
            (new ManifestSource
             ("ccm-faq.pdl.mf",
              new NameFilter(DbHelper.getDatabaseSuffix(database), "pdl"))));

    }

    /**
     *
     * @param evt
     */
    @Override
    public void init(DomainInitEvent evt) {
        super.init(evt);

        // Prerequisite to access a faq instance
        DomainObjectFactory.registerInstantiator(
               Faq.BASE_DATA_OBJECT_TYPE, new ACSObjectInstantiator() {
                   @Override
                   public DomainObject doNewInstance(DataObject dataObject) {
                       return new Faq(dataObject);
                   }
               }
        );


    //    Registering internal portlets

        // Prerequisite to access FaqQuestionsPortlet
        DomainObjectFactory.registerInstantiator(
               FaqQuestionsPortlet.BASE_DATA_OBJECT_TYPE,
               new ACSObjectInstantiator() {
                   @Override
                    public DomainObject doNewInstance(DataObject dataObject) {
                         return new FaqQuestionsPortlet(dataObject);
                    }
               }
        );
        
        // Enshure URL's are resolved
        URLFinder faqFinder = new URLFinder() {
                public String find(OID oid, String context)
                              throws NoValidURLException {
		    return find(oid);
		}
                public String find(OID oid) throws NoValidURLException {
                    QAPair pair;
                    try {
                        pair = (QAPair) DomainObjectFactory.newInstance(oid);
                    } catch (DataObjectNotFoundException e) {
                        throw new ObjectNotFoundException("No such FAQ item: " +
                                oid + ". May have been deleted.");
                    }

                    String url = pair.getFaq().getPrimaryURL() + "#"
                                                               + pair.getID();
                    return url;

                }
            };
        URLService.registerFinder(QAPair.BASE_DATA_OBJECT_TYPE, faqFinder);

    }

}
