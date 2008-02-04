/*
 * Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.cms.installer;

import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.initializer.Configuration;
import com.arsdigita.initializer.InitializationException;
import com.arsdigita.kernel.SiteNode;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.TransactionContext;
import com.arsdigita.util.Assert;
import com.arsdigita.web.Application;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;

/**
 * <p>Initializes a content section, registering a default
 * workflow, lifecycle & roles and adding the content types
 *
 * <p>The initialization process takes several configuration
 * parameters. The <code>name</code> is the name of the content
 * section, the <code>types</code> is a list of content types
 * to register
 *
 * @author Daniel Berrange (berrange@redhat.com)
 * @author Michael Pih
 * @version $Revision: #43 $ $DateTime: 2004/08/17 23:15:09 $
 */
public class SectionInitializer extends com.arsdigita.kernel.BaseInitializer {

    public static final String versionId = "$Id: SectionInitializer.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/17 23:15:09 $";

    private static org.apache.log4j.Logger s_log =
            org.apache.log4j.Logger.getLogger(SectionInitializer.class);

    private static final String NAME = "name";
    private static final String TYPES = "types";
    private static final String ROLES = "roles";
    private static final String PUBLIC = "public";
    private static final String CATEGORIES = "categories";
    private static final String TASK_ALERTS = "taskAlerts";
    private static final String SEND_OVERDUE_ALERTS = "sendOverdueAlerts";
    private static final String TASK_DURATION = "taskDuration";
    private static final String OVERDUE_ALERT_INTERVAL = "alertInterval";
    private static final String MAX_ALERTS = "maxAlerts";
    private static final String ITEM_RESOLVER_CLASS = "itemResolverClass";
    private static final String TEMPLATE_RESOLVER_CLASS = "templateResolverClass";

    private Configuration m_conf = new Configuration();

    // the Timer used to send Unfinished notifications
    private static Timer s_unfinishedTimer;

    public SectionInitializer() throws InitializationException {
        m_conf.initParameter(NAME,
                "The name of the content section",
                String.class);
        m_conf.initParameter(TYPES,
                "The content types to register",
                List.class);
        m_conf.initParameter(ROLES,
                "The roles to create",
                List.class);
        m_conf.initParameter(PUBLIC,
                "Whether to make published content available to non-registered users",
                Boolean.class);
        m_conf.initParameter(CATEGORIES,
                "XML file containing the category tree",
                List.class,
                Collections.EMPTY_LIST);
        m_conf.initParameter(TASK_ALERTS,
                "A list of workflow tasks, and the events for which alerts are sent",
                List.class);
        m_conf.initParameter(SEND_OVERDUE_ALERTS,
                "Send alerts when a task is overdue (has remained in the \"enabled\" state for a long time)",
                Boolean.class,
                Boolean.FALSE);
        // XXX Once the Duration of a Task can actually be maintained (in the UI, or initialization parameters),
        // we should use the value in the DB, and get rid of this
        m_conf.initParameter(TASK_DURATION,
                "How long a task can remain \"enabled\" before it is considered overdue (in hours)",
                Integer.class);
        m_conf.initParameter(OVERDUE_ALERT_INTERVAL,
                "Time to wait between sending overdue notifications on the same task (in hours)",
                Integer.class);
        m_conf.initParameter(MAX_ALERTS,
                "The maximum number of alerts to send that a single task is overdue",
                Integer.class);
        m_conf.initParameter(ITEM_RESOLVER_CLASS,
                "The ItemResolver class to use for the section (defaults to MultilingualItemResolver)",
                String.class);
        m_conf.initParameter(TEMPLATE_RESOLVER_CLASS,
                "The TemplateResolver class to use for the section (defaults to DefaultTemplateResolver)",
                String.class);
    }

    public Configuration getConfiguration() {
        return m_conf;
    }


    protected void doStartup() {

        // Create and mount the demo content section if it does not exist.
        String name = (String) m_conf.getParameter(NAME);

        TransactionContext txn =
                SessionManager.getSession().getTransactionContext();
        txn.beginTxn();
        ContentSectionSetup.setupContentSectionAppType();

        Util.validateURLParameter("name", name);

        String sitemapEntry = "/" +  name + "/";
        if (Application.isInstalled(ContentSection.BASE_DATA_OBJECT_TYPE,
                sitemapEntry)) {
            s_log.info("skipping " + name +
                    " because it is already installed");
        } else {
            s_log.info("Installing " + name + " at " +
                    sitemapEntry);
            createSection(name);
        }

        ContentSection section = retrieveContentSection(name);
        Assert.exists(section, ContentSection.class);
        ContentSectionSetup setup = new ContentSectionSetup(section);

        setup.loadAlertPrefs((List) m_conf.getParameter(TASK_ALERTS));

        s_unfinishedTimer = setup.startNotifierTask
            ((Boolean) m_conf.getParameter(SEND_OVERDUE_ALERTS),
             (Integer) m_conf.getParameter(TASK_DURATION),
             (Integer) m_conf.getParameter(OVERDUE_ALERT_INTERVAL),
             (Integer) m_conf.getParameter(MAX_ALERTS));

        txn.commitTxn();
    }

    private ContentSection retrieveContentSection(String name) {
        BigDecimal rootNodeID = SiteNode.getRootSiteNode().getID();
        SiteNode node = null;
        try {
            node = SiteNode.getSiteNode("/" + name);
        } catch (DataObjectNotFoundException ex) {
            throw new InitializationException( (String) GlobalizationUtil.globalize("cms.installer.root_site_node_missing").localize(),  ex);
        }
        ContentSection section = null;
        if ( rootNodeID.equals(node.getID()) ) {
            // This instance does not exist yet.
            section = createSection(name);
        } else {
            try {
                section = ContentSection.getSectionFromNode(node);
            } catch (DataObjectNotFoundException de) {
                throw new InitializationException( (String) GlobalizationUtil.globalize("cms.installer.could_not_load_section", new Object[] {name}).localize(), de);
            }
        }
        return section;
    }

    protected void doShutdown() {
        if (s_unfinishedTimer != null) {
            s_unfinishedTimer.cancel();
            s_unfinishedTimer = null;
        }
    }

    /**
     * Install the CMS Demo.
     */
    private ContentSection createSection(String name) {

        s_log.info("Creating content section on /" + name);


        ContentSection section = ContentSection.create(name);

        ContentSectionSetup setup = new ContentSectionSetup(section);

        // Setup the access controls
        
        if (ContentSection.getConfig().getUseSectionCategories()) {
            Iterator files = ((List) m_conf.getParameter(CATEGORIES)).iterator();
            while ( files.hasNext() ) {
                setup.registerCategories((String) files.next());
            }
        }

        setup.registerRoles((List)m_conf.getParameter(ROLES));
        setup.registerViewers((Boolean)m_conf.getParameter(PUBLIC));
        setup.registerAlerts();
        setup.registerPublicationCycles();
        setup.registerWorkflowTemplates();
        setup.registerContentTypes((List)m_conf.getParameter(TYPES));
        setup.registerResolvers
            ((String) m_conf.getParameter(ITEM_RESOLVER_CLASS),
             (String) m_conf.getParameter(TEMPLATE_RESOLVER_CLASS));
        section.save();

        return section;
    }
}
