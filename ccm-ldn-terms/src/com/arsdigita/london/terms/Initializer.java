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

package com.arsdigita.london.terms;

import com.arsdigita.domain.DomainObject;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.domain.DomainObjectInstantiator;
import com.arsdigita.domain.xml.TraversalHandler;

import com.arsdigita.runtime.CompoundInitializer;
import com.arsdigita.runtime.DomainInitEvent;
import com.arsdigita.runtime.PDLInitializer;
import com.arsdigita.runtime.RuntimeConfig;

import com.arsdigita.db.DbHelper;

import com.arsdigita.persistence.pdl.ManifestSource;
import com.arsdigita.persistence.pdl.NameFilter;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;

import com.arsdigita.categorization.Category;

import com.arsdigita.kernel.ACSObjectInstantiator;
import com.arsdigita.kernel.URLService;
import com.arsdigita.kernel.URLFinder;
import com.arsdigita.kernel.NoValidURLException;

import com.arsdigita.xml.XML;

/**
 *
 *
 */
public class Initializer extends CompoundInitializer {

    public Initializer() {
        final String url = RuntimeConfig.getConfig().getJDBCURL();
        final int database = DbHelper.getDatabaseFromURL(url);

        add(new PDLInitializer
            (new ManifestSource
             ("ccm-ldn-terms.pdl.mf",
              new NameFilter(DbHelper.getDatabaseSuffix(database), "pdl"))));
    }

    public void init(DomainInitEvent e) {
        DomainObjectFactory f = e.getFactory();

        f.registerInstantiator
            (Domain.BASE_DATA_OBJECT_TYPE,
             new DomainObjectInstantiator() {
                 public DomainObject doNewInstance(DataObject dataObject) {
                     return new Domain(dataObject);
                 }
             });

        f.registerInstantiator
            (Term.BASE_DATA_OBJECT_TYPE,
             new ACSObjectInstantiator() {
                 public DomainObject doNewInstance(DataObject dataObject) {
                     return new Term(dataObject);
                 }
             });

        f.registerInstantiator
            (Terms.BASE_DATA_OBJECT_TYPE,
             new ACSObjectInstantiator() {
                 public DomainObject doNewInstance(DataObject dataObject) {
                     return new Terms(dataObject);
                 }
             });

        f.registerInstantiator
            ("com.arsdigita.categorization.UseContext",
             new DomainObjectInstantiator() {
                 public DomainObject doNewInstance(DataObject dataObject) {
                     return new DomainUseContext(dataObject);
                 }
             });

        URLService.registerFinder(
            Term.BASE_DATA_OBJECT_TYPE,
            new URLFinder() {
                public String find(OID oid) 
                    throws NoValidURLException {
                    return find(oid, null);
                }

                public String find(OID oid, String context) 
                    throws NoValidURLException {
                    Term term = (Term)DomainObjectFactory.newInstance(oid);
                    Category model = term.getModel();
                    return URLService.locate(model.getOID());
                }
            });

        URLService.registerFinder(
            Domain.BASE_DATA_OBJECT_TYPE,
            new URLFinder() {
                public String find(OID oid) 
                    throws NoValidURLException {
                    return find(oid, null);
                }

                public String find(OID oid, String context) 
                    throws NoValidURLException {
                    Domain domain = (Domain)DomainObjectFactory.newInstance(oid);
                    Category model = domain.getModel();
                    return URLService.locate(model.getOID());
                }
            });

        XML.parse(Terms.getConfig().getTraversalAdapters(),
                  new TraversalHandler());
    }
}
