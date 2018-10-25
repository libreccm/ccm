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
import com.arsdigita.kernel.ResourceType;
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
import com.arsdigita.search.converter.Converter;
import com.arsdigita.search.converter.ConverterRegistry;
import com.arsdigita.search.converter.ExcelConverter;
import com.arsdigita.search.converter.OOConverter;
import com.arsdigita.search.converter.PDFConverter;
import com.arsdigita.search.converter.TextConverter;
import com.arsdigita.search.converter.WordConverter;
import com.arsdigita.toolbox.CharsetEncodingProvider;
import com.arsdigita.ui.admin.Admin;
import com.arsdigita.ui.login.Login;
import com.arsdigita.ui.permissions.Permissions;
import com.arsdigita.util.URLRewriter;
import com.arsdigita.web.ApplicationType;
import com.arsdigita.web.Host;
import com.arsdigita.webdevsupport.WebDevSupport;
import com.arsdigita.workflow.simple.TaskComment;

import org.apache.log4j.Logger;
import org.libreccm.categorization.CategoriesExporter;
import org.libreccm.categorization.CategorizationsExporter;
import org.libreccm.core.ResourceTypesExporter;
import org.libreccm.export.ExportManager;
import org.libreccm.security.GroupMembershipsExporter;
import org.libreccm.security.GroupsExporter;
import org.libreccm.security.PermissionsExporter;
import org.libreccm.security.RoleMembershipsExporter;
import org.libreccm.security.RolesExporter;
import org.libreccm.security.UsersExporter;
import org.libreccm.workflow.AssignableTasksExporter;
import org.libreccm.workflow.TaskAssignmentsExporter;
import org.libreccm.workflow.TaskCommentsExporter;
import org.libreccm.workflow.TaskDependenciesExporter;
import org.libreccm.workflow.TasksExporter;
import org.libreccm.workflow.WorkflowsExporter;

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

        add(new PDLInitializer(new ManifestSource("ccm-core.pdl.mf",
                                                  new NameFilter(DbHelper
                                                      .getDatabaseSuffix(
                                                          database), "pdl"))));

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

    @Override
    public final void init(final DomainInitEvent event) {
        super.init(event);

        s_log.debug("Running core init(DomainInitEvent) ...");

        event.getFactory().registerInstantiator(
            Host.BASE_DATA_OBJECT_TYPE,
            new DomainObjectInstantiator() {

            public DomainObject doNewInstance(final DataObject dobj) {
                return new Host(dobj);
            }

        });

        event.getFactory().registerInstantiator(
            ApplicationType.BASE_DATA_OBJECT_TYPE,
            new DomainObjectInstantiator() {

            public DomainObject doNewInstance(final DataObject dobj) {
                return new ApplicationType(dobj);
            }

        });

        event.getFactory().registerInstantiator(
            TaskComment.BASE_DATA_OBJECT_TYPE,
            new DomainObjectInstantiator() {

            public DomainObject doNewInstance(final DataObject data) {
                return new TaskComment(data);
            }

        });

        /* domain.ReflectionInstantiator instantiator for 
         * dataObject com.arsdigita.webdevsupport.WebDevSupport              */
        event.getFactory().registerInstantiator(
            WebDevSupport.BASE_DATA_OBJECT_TYPE,
            new ACSObjectInstantiator() {

            @Override
            public DomainObject doNewInstance(final DataObject data) {
                return new WebDevSupport(data);
            }

        });

        event.getFactory().registerInstantiator(
            Login.BASE_DATA_OBJECT_TYPE,
            new ACSObjectInstantiator() {

            @Override
            public DomainObject doNewInstance(final DataObject data) {
                return new Login(data);
            }

        });

        event.getFactory().registerInstantiator(
            Admin.BASE_DATA_OBJECT_TYPE,
            new ACSObjectInstantiator() {

            @Override
            public DomainObject doNewInstance(
                final DataObject data) {
                return new Admin(data);
            }

        });

        event.getFactory().registerInstantiator(
            Permissions.BASE_DATA_OBJECT_TYPE,
            new ACSObjectInstantiator() {

            @Override
            public DomainObject doNewInstance(final DataObject data) {
                return new Permissions(data);
            }

        });

        event.getFactory().registerInstantiator(
            BasicAuditTrail.BASE_DATA_OBJECT_TYPE,
            new DomainObjectInstantiator() {

            public DomainObject doNewInstance(final DataObject data) {
                return new BasicAuditTrail(data);
            }

        });

        event.getFactory().registerInstantiator(
            MimeType.BASE_DATA_OBJECT_TYPE,
            new DomainObjectInstantiator() {

            public DomainObject doNewInstance(final DataObject dataObject) {
                return new MimeType(
                    dataObject);
            }

            @Override
            public DomainObjectInstantiator
                resolveInstantiator(final DataObject obj) {
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

        final ExportManager exportManager = ExportManager.getInstance();
        exportManager.registerExporter(new CategoriesExporter());
        exportManager.registerExporter(new CategorizationsExporter());
        exportManager.registerExporter(new ResourceTypesExporter());
        exportManager.registerExporter(new GroupMembershipsExporter());
        exportManager.registerExporter(new GroupsExporter());
        exportManager.registerExporter(new PermissionsExporter());
        exportManager.registerExporter(new RoleMembershipsExporter());
        exportManager.registerExporter(new RolesExporter());
        exportManager.registerExporter(new UsersExporter());
        exportManager.registerExporter(new AssignableTasksExporter());
        exportManager.registerExporter(new TaskAssignmentsExporter());
        exportManager.registerExporter(new TaskCommentsExporter());
        exportManager.registerExporter(new TaskDependenciesExporter());
        exportManager.registerExporter(new TasksExporter());
        exportManager.registerExporter(new WorkflowsExporter());

        // Creates an entry in table web_hosts. Might be considered a loader
        // task (and is already handled there). But configuration may be
        // changed so we have to recheck here.
        Session session = SessionManager.getSession();
        TransactionContext txn = session.getTransactionContext();
        txn.beginTxn();
        Loader.loadHost();
        txn.commitTxn();

        s_log.info("Core init(DomainInitEvent) done");
    }

}
