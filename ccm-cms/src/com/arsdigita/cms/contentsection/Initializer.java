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


package com.arsdigita.cms.contentsection;

import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.LoaderConfig;
import com.arsdigita.cms.installer.ContentSectionSetup;
import com.arsdigita.cms.installer.Util;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.kernel.SiteNode;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.TransactionContext;
import com.arsdigita.runtime.CompoundInitializer;
import com.arsdigita.runtime.ConfigError;
// import com.arsdigita.runtime.DataInitEvent;
import com.arsdigita.runtime.DomainInitEvent;
import com.arsdigita.util.Assert;
import com.arsdigita.web.Application;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.Timer;

import org.apache.log4j.Logger;


// CURRENT STATUS:
// (Simple) Migration of the Old Initializer code of this package to the new
// initializer system. Current goal is a pure replacement with as less code
// changes as possible.
// In a second step a restructure of the code will be done.


/**
 * XXX Reformulate according to the code development!
 * <p>Initializes a content section, registering a default workflow, lifecycle &
 * roles and adding the content types.
 *
 * <p>The initialization process takes several configuration
 * parameters. The <code>name</code> is the name of the content
 * section, the <code>types</code> is a list of content types
 * to register
 *
 * @author Daniel Berrange (berrange@redhat.com)
 * @author Michael Pih
 * @author pb
 * @version $Id: $
 */
public class Initializer extends CompoundInitializer {


    /** Creates a s_logging category with name = to the full name of class */
    private static Logger s_log = Logger.getLogger(Initializer.class);

    /** Local configuration object LoaderConfig containing immutable parameters 
        after installation.  */
    //  private static final LoaderConfig s_conf = LoaderConfig.getConfig();
    private static final LoaderConfig s_conf = new LoaderConfig();

    /** The Timer used to send Unfinished notifications  */
    private static Timer s_unfinishedTimer;


    public Initializer() {
      //final String url = RuntimeConfig.getConfig().getJDBCURL();
      //final int database = DbHelper.getDatabaseFromURL(url);
    }

//  /**
//   * An empty implementation of {@link Initializer#init(DataInitEvent)}.
//   *
//   * @param evt The data init event.
//   */
//  public void init(DataInitEvent evt) {
//  }
    /**
     * Initializes domain-coupling machinery, usually consisting of
     * registering object instantiators and observers.
     *
     */
    public void init(DomainInitEvent evt) {
        s_log.debug("CMS.installer.Initializer.init(DomainInitEvent) invoked");

        // Recursive invokation of init, is it really necessary??
        // On the other hand:
        // An empty implementations prevents this initializer from being executed.
        // A missing implementations causes the super class method to be executed,
        // which invokes the above added LegacyInitializer.
        // If super is not invoked, various other cms sub-initializer may not run.
        super.init(evt);

        // Create and mount the demo content section if it does not exist.
        // String name = (String) m_conf.getParameter(NAME);
        String name = s_conf.getContentSectionName();

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

        setup.loadAlertPrefs( s_conf.getTaskAlerts());

        s_unfinishedTimer = setup.startNotifierTask
            (s_conf.getSendOverdueAlerts(),
             s_conf.getTaskDuration(),
             s_conf.getOverdueAlertInterval(),
             s_conf.getMaxAlerts());

        txn.commitTxn();

        s_log.debug("CMS.installer.Initializer.init(DomainInitEvent) completed");
    }

    
    /**
     *
     * @param name
     * @return
     */
    private ContentSection retrieveContentSection(String name) {
        BigDecimal rootNodeID = SiteNode.getRootSiteNode().getID();
        SiteNode node = null;
        try {
            node = SiteNode.getSiteNode("/" + name);
        } catch (DataObjectNotFoundException ex) {
            throw new ConfigError(
                (String) GlobalizationUtil.globalize(
                    "cms.installer.root_site_node_missing").localize() + ex );
        }
        ContentSection section = null;
        if ( rootNodeID.equals(node.getID()) ) {
            // This instance does not exist yet.
            section = createSection(name);
        } else {
            try {
                section = ContentSection.getSectionFromNode(node);
            } catch (DataObjectNotFoundException de) {
                throw new ConfigError(
                    (String) GlobalizationUtil.globalize(
                        "cms.installer.could_not_load_section",
                        new Object[] {name}).localize() + de );
            }
        }
        return section;
    }
    /**
     * Install the CMS Demo.
     */
    private ContentSection createSection(String name) {

        s_log.info("Creating content section on /" + name);


        ContentSection section = ContentSection.create(name);

        ContentSectionSetup setup = new ContentSectionSetup(section);

        // Setup the access controls

        // section specific categories, usually not used.
        // During initial load at install time nor used at all!
        if (ContentSection.getConfig().getUseSectionCategories()) {
            // Iterator files = ((List) m_conf.getParameter(CATEGORIES)).iterator();
            Iterator files = s_conf.getCategoryFileList().iterator();
            while ( files.hasNext() ) {
                setup.registerCategories((String) files.next());
            }
        }

        // setup.registerRoles((List)m_conf.getParameter(ROLES));
        setup.registerRoles(s_conf.getStuffGroup());
        // setup.registerViewers((Boolean)m_conf.getParameter(PUBLIC));
        setup.registerViewers(s_conf.isPubliclyViewable());
        setup.registerAlerts();
        setup.registerPublicationCycles();
        setup.registerWorkflowTemplates();
        // setup.registerContentTypes((List)m_conf.getParameter(TYPES));
        setup.registerContentTypes(s_conf.getContentSectionsContentTypes());
        // setup.registerResolvers
        //     ((String) m_conf.getParameter(ITEM_RESOLVER_CLASS),
        //      (String) m_conf.getParameter(TEMPLATE_RESOLVER_CLASS));
        setup.registerResolvers
            (s_conf.getItemResolverClass(),
             s_conf.getTemplateResolverClass());
        section.save();

        return section;
    }

}
