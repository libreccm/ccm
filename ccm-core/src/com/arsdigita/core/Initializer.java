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
import com.arsdigita.mimetypes.MimeType;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.TransactionContext;
import com.arsdigita.persistence.pdl.ManifestSource;
import com.arsdigita.persistence.pdl.NameFilter;
import com.arsdigita.runtime.CompoundInitializer;
import com.arsdigita.runtime.DomainInitEvent;
import com.arsdigita.runtime.PDLInitializer;
import com.arsdigita.runtime.RuntimeConfig;
import com.arsdigita.toolbox.CharsetEncodingProvider;
import com.arsdigita.ui.admin.Admin;
import com.arsdigita.ui.sitemap.SiteMap;
import com.arsdigita.util.URLRewriter;
import com.arsdigita.xml.FactoriesSetup;
import com.arsdigita.web.Host;
// import com.arsdigita.web.WebApp;
import com.arsdigita.web.ApplicationType;
import com.arsdigita.workflow.simple.TaskComment;
import com.arsdigita.search.converter.Converter;
import com.arsdigita.search.converter.ConverterRegistry;
import com.arsdigita.search.converter.PDFConverter;
import com.arsdigita.search.converter.ExcelConverter;
import com.arsdigita.search.converter.OOConverter;
import com.arsdigita.search.converter.WordConverter;
import com.arsdigita.search.converter.TextConverter;

import com.arsdigita.ui.permissions.Permissions;
import com.arsdigita.webdevsupport.WebDevSupport;
import org.apache.log4j.Logger;


/**
 * CoreInitializer
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Id: Initializer.java 1547 2007-03-29 14:24:57Z chrisgilbert23 $
 */
public class Initializer extends CompoundInitializer {

    private static Logger s_log = Logger.getLogger(Initializer.class);

    /**
     * Constructor
     */
    public Initializer() {
        s_log.info("Instantiating Core Initilizer ...");
        final String url = RuntimeConfig.getConfig().getJDBCURL();
        final int database = DbHelper.getDatabaseFromURL(url);

        s_log.info("Ading Sub-Initilizers ...");
        add(new com.arsdigita.persistence.Initializer());

        add(new PDLInitializer
            (new ManifestSource
             ("ccm-core.pdl.mf",
              new NameFilter(DbHelper.getDatabaseSuffix(database), "pdl"))));

        add(new com.arsdigita.ui.Initializer());
        add(new com.arsdigita.kernel.Initializer());
        add(new com.arsdigita.kernel.security.Initializer());
        add(new com.arsdigita.globalization.Initializer());
        add(new com.arsdigita.portal.Initializer());
        add(new com.arsdigita.search.Initializer());
        add(new com.arsdigita.search.lucene.Initializer());
        add(new com.arsdigita.search.intermedia.Initializer());
        add(new com.arsdigita.notification.Initializer());

    }

    /**
     * 
     * @param e
     */
    @Override
    public final void init(final DomainInitEvent e) {
        super.init(e);

        s_log.debug("Running core init(DomainInitEvent) ...");

        FactoriesSetup.setupFactories();

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

/*      Status Experimental - not used anywhere in code base.
        e.getFactory().registerInstantiator
            (WebApp.BASE_DATA_OBJECT_TYPE,
             new DomainObjectInstantiator() {
                 public DomainObject doNewInstance(final DataObject data) {
                     return new WebApp(data);
                  }
             });
*/
        e.getFactory().registerInstantiator
            (TaskComment.BASE_DATA_OBJECT_TYPE,
             new DomainObjectInstantiator() {
                 public DomainObject doNewInstance(final DataObject data) {
                     return new TaskComment(data);
                 }
             });

        /* domain.ReflectionInstantiator instantiator for 
         * dataObject com.arsdigita.webdevsupport.WebDevSupport              */
        e.getFactory().registerInstantiator
            (WebDevSupport.BASE_DATA_OBJECT_TYPE,
             new ACSObjectInstantiator() {
                 @Override
                 public DomainObject doNewInstance(final DataObject data) {
                     return new WebDevSupport(data);
                 }
             });

        e.getFactory().registerInstantiator
            (Admin.BASE_DATA_OBJECT_TYPE,
             new ACSObjectInstantiator() {
                 @Override
                 public DomainObject doNewInstance(final DataObject data) {
                     return new Admin(data);
                 }
             });

        e.getFactory().registerInstantiator
            (SiteMap.BASE_DATA_OBJECT_TYPE,
             new ACSObjectInstantiator() {
                 @Override
                 public DomainObject doNewInstance(final DataObject data) {
                     return new SiteMap(data);
                 }
             });

        e.getFactory().registerInstantiator
            (Permissions.BASE_DATA_OBJECT_TYPE,
             new ACSObjectInstantiator() {
                 @Override
                 public DomainObject doNewInstance(final DataObject data) {
                     return new Permissions(data);
                 }
             });

	    e.getFactory().registerInstantiator
            (BasicAuditTrail.BASE_DATA_OBJECT_TYPE,
	         new DomainObjectInstantiator() {
		       public DomainObject doNewInstance(final DataObject data) {
		              return new BasicAuditTrail(data);
	             }
	         });

/*      MimeType used to have its own initializer in the old initialize system
 *      based on enterprise.ini. This initializer performed a DomainObjectFactgory
 *      instantiation and some handling of INSO filter, an Oracle db related
 *      filter for intermedia textsearch. INSO filter handling is moved to
 *      intermedia search engine so no configuration was left in the domain
 *      instantiation could be moved to the central core initializer
        From old Initializer system:

        if (DomainObjectFactory.getInstantiator
                (MimeType.BASE_DATA_OBJECT_TYPE) == null) {
            DomainObjectInstantiator instMimeType = new DomainObjectInstantiator() {
                    public DomainObject doNewInstance(DataObject dataObject) {
                        return new MimeType(dataObject);
                    }
                };
            DomainObjectFactory.registerInstantiator
                (MimeType.BASE_DATA_OBJECT_TYPE, instMimeType);
        }

*/
        e.getFactory().registerInstantiator
            (MimeType.BASE_DATA_OBJECT_TYPE,
             new DomainObjectInstantiator() {
                 public DomainObject doNewInstance(DataObject dataObject) {
                     return new MimeType(dataObject);
                 }
                 @Override
                 public DomainObjectInstantiator
                     resolveInstantiator(DataObject obj) {
                     return this;
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

        // Initialize the the CharsetEncodingProvider internal data structure
        URLRewriter.addParameterProvider(new CharsetEncodingProvider());


        // Creates an entry in table web_hosts. Might be considered a loader
        // task (and is already handled there). But configuration may be
        // changed so we have to recheck here.
        Session session = SessionManager.getSession();
        TransactionContext txn = session.getTransactionContext();
        txn.beginTxn();
        CoreLoader.loadHost();
        txn.commitTxn();

        s_log.info("Core init(DomainInitEvent) done");
    }

    /**
     * 
     * @param e
     */
//  public final void init(final LegacyInitEvent e) {
//      super.init(e);

//      s_log.info("Running core init(LegacyInitEvent) ...");

//      Session session = SessionManager.getSession();
//      TransactionContext txn = session.getTransactionContext();
//      txn.beginTxn();
//      CoreLoader.loadHost();
//      txn.commitTxn();

//      FactoriesSetup.setupFactories();

//      s_log.info("Core init(LegacyInitEvent) done");
//  }
}
