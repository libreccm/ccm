/*
 * Copyright (C) 2001 ArsDigita Corporation. All Rights Reserved.
 *
 * The contents of this file are subject to the ArsDigita Public 
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of
 * the License at http://www.arsdigita.com/ADPL.txt
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */
package com.arsdigita.cms.webpage.installer;

import com.arsdigita.cms.webpage.Webpage;
import com.arsdigita.cms.webpage.WebpageConfig;
import com.arsdigita.cms.webpage.ui.WebpagePortlet;
import com.arsdigita.cms.webpage.ui.WebpagePortletEditor;

import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.TransactionContext;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.pdl.ManifestSource;
import com.arsdigita.persistence.pdl.NameFilter;

import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.domain.DomainObjectFactory;

import com.arsdigita.bebop.RequestLocal;

import com.arsdigita.portal.Portlet;
import com.arsdigita.portal.PortletSetup;
import com.arsdigita.portal.PortletType;

import com.arsdigita.kernel.ACSObjectInstantiator;
import com.arsdigita.kernel.ResourceType;
import com.arsdigita.kernel.BaseInitializer;
import com.arsdigita.kernel.ResourceTypeConfig;
import com.arsdigita.kernel.ui.ResourceConfigComponent;
import com.arsdigita.kernel.ui.ResourceConfigFormSection;

import com.arsdigita.runtime.CompoundInitializer;
import com.arsdigita.runtime.DomainInitEvent;
import com.arsdigita.runtime.PDLInitializer;
import com.arsdigita.runtime.RuntimeConfig;

import org.apache.log4j.Logger;
import com.arsdigita.util.UncheckedWrapperException;
import java.util.Iterator;
import java.util.List;
import com.arsdigita.util.Assert;
import com.arsdigita.db.DbHelper;

public class Initializer extends CompoundInitializer {

    //public static final String CONTENT_SECTION = "contentSection";
    private static Logger s_log = Logger.getLogger(Initializer.class.getName());

    //private Configuration m_conf = new Configuration();
    public Initializer() {
        final String url = RuntimeConfig.getConfig().getJDBCURL();
        final int database = DbHelper.getDatabaseFromURL(url);

        // moved to WebpageInitializer
//         add(new PDLInitializer
//             (new ManifestSource
//              ("ccm-webpage.pdl.mf",
//               new NameFilter(DbHelper.getDatabaseSuffix(database), "pdl"))));

    }

    //public Configuration getConfiguration() {
    //    return m_conf;
    //}
    @Override
    public void init(DomainInitEvent e) {
        super.init(e);

        e.getFactory().registerInstantiator(Webpage.BASE_DATA_OBJECT_TYPE,
                                            new ACSObjectInstantiator() {

            @Override
            public DomainObject doNewInstance(DataObject dataObject) {
                return new Webpage(dataObject);
            }
        });

        startup();
    }
    // ============================================================
    private static final WebpageConfig s_config = new WebpageConfig();

    // FR: load the parameter values
    static {
        s_log.debug("Static initalizer starting...");
        s_config.load();
        s_log.debug("Static initalizer finished.");
    }

    public static WebpageConfig getConfig() {
        return s_config;
    }

    public void startup() {
        final WebpageConfig config = getConfig();

        //String section = (String) m_conf.getParameter(CONTENT_SECTION);
        String section = config.getContentSection();
        Assert.isTrue(section != null, "contentSection is null");
        config.setContentSection(section);


        TransactionContext txn = SessionManager.getSession().
                getTransactionContext();
        txn.beginTxn();

        loadWebpagePortlet();

        txn.commitTxn();
    }

    public void shutdown() {
        /* Empty */
    }

    private void loadWebpagePortlet() {
        PortletSetup setup = new PortletSetup(s_log);
        setup.setPortletObjectType(WebpagePortlet.BASE_DATA_OBJECT_TYPE);
        setup.setTitle("Webpage");
        setup.setDescription("Displays a webpage");
        setup.setProfile(PortletType.WIDE_PROFILE);
        setup.setInstantiator(new ACSObjectInstantiator() {

            protected DomainObject doNewInstance(DataObject dataObject) {
                return new WebpagePortlet(dataObject);
            }
        });
        setup.run();

        new ResourceTypeConfig(WebpagePortlet.BASE_DATA_OBJECT_TYPE) {

            public ResourceConfigFormSection getCreateFormSection(
                    final ResourceType resType, final RequestLocal parentAppRL) {
                final ResourceConfigFormSection config =
                                                new WebpagePortletEditor(resType,
                                                                         parentAppRL);

                return config;
            }

            public ResourceConfigFormSection getModifyFormSection(
                    final RequestLocal application) {
                final WebpagePortletEditor config =
                                           new WebpagePortletEditor(application);

                return config;
            }
        };

    }
}
