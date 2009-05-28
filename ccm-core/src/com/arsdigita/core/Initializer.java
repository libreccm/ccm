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
package com.arsdigita.core;

import com.arsdigita.auditing.BasicAuditTrail;
import com.arsdigita.db.DbHelper;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.domain.DomainObjectInstantiator;
import com.arsdigita.kernel.ACSObjectInstantiator;
import com.arsdigita.loader.CoreLoader;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.TransactionContext;
import com.arsdigita.persistence.pdl.ManifestSource;
import com.arsdigita.persistence.pdl.NameFilter;
import com.arsdigita.runtime.CompoundInitializer;
import com.arsdigita.runtime.DomainInitEvent;
import com.arsdigita.runtime.LegacyInitEvent;
import com.arsdigita.runtime.LegacyInitializer;
import com.arsdigita.runtime.OptionalLegacyInitializer;
import com.arsdigita.runtime.PDLInitializer;
import com.arsdigita.runtime.RuntimeConfig;
import com.arsdigita.ui.admin.Admin;
import com.arsdigita.ui.sitemap.SiteMap;
import com.arsdigita.xml.FactoriesSetup;
import com.arsdigita.web.Host;
import com.arsdigita.web.WebApp;
import com.arsdigita.web.ApplicationType;
import com.arsdigita.workflow.simple.TaskComment;
import com.arsdigita.search.converter.Converter;
import com.arsdigita.search.converter.ConverterRegistry;
import com.arsdigita.search.converter.PDFConverter;
import com.arsdigita.search.converter.ExcelConverter;
import com.arsdigita.search.converter.OOConverter;
import com.arsdigita.search.converter.WordConverter;
import com.arsdigita.search.converter.TextConverter;


/**
 * CoreInitializer
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #15 $ $Date: 2004/08/16 $
 **/

public class Initializer extends CompoundInitializer {

    public final static String versionId = "$Id: Initializer.java 1547 2007-03-29 14:24:57Z chrisgilbert23 $ by $Author: chrisgilbert23 $, $DateTime: 2004/08/16 18:10:38 $";

    public Initializer() {
        final String url = RuntimeConfig.getConfig().getJDBCURL();
        final int database = DbHelper.getDatabaseFromURL(url);

        add(new com.arsdigita.persistence.Initializer());

        add(new PDLInitializer
            (new ManifestSource
             ("ccm-core.pdl.mf",
              new NameFilter(DbHelper.getDatabaseSuffix(database), "pdl"))));

        add(new LegacyInitializer("com/arsdigita/core/enterprise.init"));
        add(new OptionalLegacyInitializer("enterprise.init"));
    }

    public final void init(final DomainInitEvent e) {
        super.init(e);

        e.getFactory().registerInstantiator
            (Host.BASE_DATA_OBJECT_TYPE,
             new DomainObjectInstantiator() {
                 public DomainObject doNewInstance(DataObject dobj) {
                     return new Host(dobj);
                 }
             });

        e.getFactory().registerInstantiator
            (ApplicationType.BASE_DATA_OBJECT_TYPE,
             new DomainObjectInstantiator() {
                 public DomainObject doNewInstance(DataObject dobj) {
                     return new ApplicationType(dobj);
                 }
             });

        e.getFactory().registerInstantiator
            (WebApp.BASE_DATA_OBJECT_TYPE,
             new DomainObjectInstantiator() {
                 public DomainObject doNewInstance(final DataObject data) {
                     return new WebApp(data);
                  }
             });

        e.getFactory().registerInstantiator
            (TaskComment.BASE_DATA_OBJECT_TYPE,
             new DomainObjectInstantiator() {
                 public DomainObject doNewInstance(final DataObject data) {
                     return new TaskComment(data);
                 }
             });

        e.getFactory().registerInstantiator
            (Admin.BASE_DATA_OBJECT_TYPE,
             new ACSObjectInstantiator() {
                 public DomainObject doNewInstance(final DataObject data) {
                     return new Admin(data);
                 }
             });

        e.getFactory().registerInstantiator
            (SiteMap.BASE_DATA_OBJECT_TYPE,
             new ACSObjectInstantiator() {
                 public DomainObject doNewInstance(final DataObject data) {
                     return new SiteMap(data);
                 }
             });

	    e.getFactory().registerInstantiator
            (BasicAuditTrail.BASE_DATA_OBJECT_TYPE,
	         new DomainObjectInstantiator() {
		         public DomainObject doNewInstance(final DataObject data) {
		                return new BasicAuditTrail(data);
	             }
	         });

        // register the document converters
        Converter converter = new PDFConverter();
        ConverterRegistry.registerConverter(converter, 
                                            converter.getMimeTypes());

        converter = new ExcelConverter();
        ConverterRegistry.registerConverter(converter, 
                                            converter.getMimeTypes());

        converter = new WordConverter();
        ConverterRegistry.registerConverter(converter, 
                                            converter.getMimeTypes());

        converter = new OOConverter();
        ConverterRegistry.registerConverter(converter, 
                                            converter.getMimeTypes());

        converter = new TextConverter();
        ConverterRegistry.registerConverter(converter, 
                                            converter.getMimeTypes());
    }

    public final void init(final LegacyInitEvent e) {
        super.init(e);

        Session session = SessionManager.getSession();
        TransactionContext txn = session.getTransactionContext();
        txn.beginTxn();
        CoreLoader.loadHost();
        txn.commitTxn();

        FactoriesSetup.setupFactories();
    }
}
