/*
 * Copyright (C) 2003-2005 Red Hat Inc. All Rights Reserved.
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

import com.arsdigita.categorization.Category;
import com.arsdigita.categorization.RootCategoryCollection;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.ContentType;
import com.arsdigita.cms.ContentTypeLifecycleDefinition;
import com.arsdigita.cms.ContentTypeWorkflowTemplate;
import com.arsdigita.cms.SecurityManager;
import com.arsdigita.cms.Template;
import com.arsdigita.cms.TemplateManagerFactory;
import com.arsdigita.cms.lifecycle.LifecycleDefinition;
import com.arsdigita.cms.lifecycle.PhaseDefinition;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.cms.workflow.CMSTask;
import com.arsdigita.cms.workflow.CMSTaskType;
import com.arsdigita.cms.workflow.UnfinishedTaskNotifier;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.initializer.InitializationException;
import com.arsdigita.kernel.ACSObjectInstantiator;
import com.arsdigita.kernel.Party;
import com.arsdigita.kernel.PartyCollection;
import com.arsdigita.kernel.Role;
import com.arsdigita.kernel.permissions.PermissionService;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.util.Assert;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.web.ApplicationSetup;
import com.arsdigita.workflow.simple.WorkflowTemplate;
import com.arsdigita.xml.XML;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.Timer;

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Sets up the content section.
 *
 * @author Jon Orris (jorris@redhat.com)
 * @version $Revision: #17 $ $DateTime: 2004/08/17 23:15:09 $
 */

public final class ContentSectionSetup {


    private final static String STYLESHEET = "/packages/content-section/xsl/cms.xsl";

    private static Logger s_log = Logger.getLogger(ContentSectionSetup.class);
    private HashMap m_tasks = new HashMap();
    private LifecycleDefinition m_lcd;
    private WorkflowTemplate m_wf;
    final ContentSection m_section;

    public ContentSectionSetup(ContentSection section) {
        Assert.exists(section, ContentSection.class);
        m_section = section;
    }

    public void run() {

        // 4) Mount content sections.

    }
    /**
     * setup content section app type
     */
    public static void setupContentSectionAppType() {
        ApplicationSetup setup = new ApplicationSetup(s_log);
        setup.setApplicationObjectType(ContentSection.BASE_DATA_OBJECT_TYPE);
        setup.setKey(ContentSection.PACKAGE_TYPE);
        setup.setTitle("CMS Content Section");
        setup.setDescription("A CMS Content Section");
        setup.setPortalApplication(false);
        //setup.setDispatcherClass(ContentItemDispatcher.class.getName());
        setup.setStylesheet(STYLESHEET);
        setup.setInstantiator(new ACSObjectInstantiator() {
            public DomainObject doNewInstance(DataObject dataObject) {
                return new ContentSection(dataObject);
            }
        });

        setup.run();

    }

    public void registerCategories(String filename) {

        if (filename == null) {
            s_log.info("not loading any categories");
            return;
        }

        s_log.info("loading categories from " + filename);

        XML.parseResource(filename, new CategoryHandler(m_section));
    }


    public void registerRoles(List roles) {

        Iterator i = roles.iterator();
        while (i.hasNext()) {
            List role = (List)i.next();

            String name = (String)role.get(0);
            String desc = (String)role.get(1);
            List privileges = (List)role.get(2);
            String task = (role.size() > 3 ? (String)role.get(3) : null);

            s_log.info("Creating role " + name);


            Role group = registerRole(
                    name,
                    desc,
                    privileges);

            if (task != null)
                m_tasks.put(task, group);
        }

    }

    Role registerRole(String name,
                              String desc,
                              List privileges) {
        Role role = m_section.getStaffGroup().createRole(name);
        role.setDescription(desc);
        role.save();

        Iterator i = privileges.iterator();
        while (i.hasNext()) {
            String priv = (String)i.next();
            s_log.info("Granting privilege cms_" + priv);

            role.grantPermission(m_section,
                    PrivilegeDescriptor.get("cms_" + priv));

            if (priv.equals(SecurityManager.CATEGORY_ADMIN) ||
                priv.equals(SecurityManager.CATEGORIZE_ITEMS)) {
                RootCategoryCollection coll = Category.getRootCategories(m_section);
                while (coll.next()) {
                    if (priv.equals(SecurityManager.CATEGORY_ADMIN)) {
                        role.grantPermission(coll.getCategory(),
                                             PrivilegeDescriptor.ADMIN);
                    } else {
                        role.grantPermission(coll.getCategory(),
                                             Category.MAP_DESCRIPTOR);
                    }
                }
            }
        }

        return role;
    }

    public void registerResolvers(String itemResolverClass, String templateResolverClass) {

        if (itemResolverClass != null && itemResolverClass.length()>0) {
            m_section.setItemResolverClass(itemResolverClass);
            s_log.info("Registering " + itemResolverClass + " as the item resolver class");
        } else {
            m_section.setItemResolverClass
                (ContentSection.getConfig().getDefaultItemResolverClass().getName());
            s_log.info("Registering " + itemResolverClass + " as the item resolver class");            
        }
        if (templateResolverClass != null && templateResolverClass.length()>0) {
            m_section.setTemplateResolverClass(templateResolverClass);
            s_log.info("Registering " + templateResolverClass +
                       " as the template resolver class");
        } else {
            m_section.setTemplateResolverClass
                (ContentSection.getConfig().getDefaultTemplateResolverClass().getName());
            s_log.info("Registering " + templateResolverClass +
                       " as the template resolver class");
        }

        m_section.save();
    }

    public void registerWorkflowTemplates()
            throws InitializationException {

        // The 3-step production workflow.
        WorkflowTemplate wf = new WorkflowTemplate();
        wf.setLabel( (String) GlobalizationUtil.globalize(
                     "cms.installer.production_workflow").localize());
        wf.setDescription("A process that involves creating and approving content.");
        wf.save();

        CMSTask authoring = new CMSTask();
        authoring.setLabel((String) GlobalizationUtil.globalize(
                           "cms.installer.authoring").localize());
        authoring.setDescription("Create content.");
        authoring.save();


        Role author = (Role)m_tasks.get("Authoring");
        if (author != null)
            authoring.assignGroup(author.getGroup());

        authoring.setTaskType(CMSTaskType.retrieve(CMSTaskType.AUTHOR));
        authoring.save();

        CMSTask approval = new CMSTask();
        approval.setLabel( (String) GlobalizationUtil.globalize(
                           "cms.installer.approval").localize());
        approval.setDescription("Approve content.");
        approval.save();
        approval.addDependency(authoring);
        approval.save();

        Role approver = (Role)m_tasks.get("Approval");
        if (approver != null)
            approval.assignGroup(approver.getGroup());

        approval.setTaskType(CMSTaskType.retrieve(CMSTaskType.EDIT));
        approval.save();


        CMSTask deploy = new CMSTask();
        deploy.setLabel((String) GlobalizationUtil.globalize(
                                 "cms.installer.deploy").localize());
        deploy.setDescription("Deploy content.");
        deploy.save();
        deploy.addDependency(approval);
        deploy.save();

        Role publisher = (Role)m_tasks.get("Publishing");
        if (publisher != null)
            deploy.assignGroup(publisher.getGroup());

        deploy.setTaskType(CMSTaskType.retrieve(CMSTaskType.DEPLOY));
        deploy.save();

        wf.addTask(authoring);
        wf.addTask(approval);
        wf.addTask(deploy);
        wf.save();

        m_section.addWorkflowTemplate(wf);
        m_section.save();

        m_wf = wf;
    }

    public void registerViewers(Boolean pub) {
        Role viewers = m_section.getViewersGroup().createRole("Content Reader");
        viewers.setDescription("Can view published pages within this section");
        viewers.save();

        viewers.grantPermission(m_section,
                PrivilegeDescriptor.get("cms_read_item"));


        String email = Boolean.TRUE.equals(pub) ? "public@nullhost"
                                                : "registered@nullhost";

        Party viewer = retrieveParty(email);
        if (viewer == null)
            throw new InitializationException( 
                (String) GlobalizationUtil.globalize(
                "cms.installer.cannot_find_group_for_email").localize() + email);

        s_log.info("Adding " + email + " to viewers role");
        viewers.getGroup().addMemberOrSubgroup(viewer);
        viewers.save();
    }

    Party retrieveParty(String email) {
        PartyCollection parties = Party.retrieveAllParties();
        parties.filter(email);
        if (parties.next()) {
            Party party = parties.getParty();
            parties.close();
            return party;
        }
        return null;
    }

    public void registerAlerts() {
        Role alert = m_section.getStaffGroup().createRole("Alert Recipient");
        alert.setDescription("Receive alerts regarding expiration of pubished content");
        alert.save();
    }

    public void registerContentTypes(List types) {

        Iterator i = types.iterator();

        while (i.hasNext()) {
            Object obj = i.next();
            if (obj instanceof String) {
                registerContentType((String)obj);
            } else {
                List list = (List)obj;
                String name = (String)list.get(0);
                String file = (String)list.get(1);

                ContentType type = registerContentType(name);
                registerTemplate(type, file);
            }
        }
    }

    ContentType registerContentType(String name) {
        ContentType type = null;
        try {
            type = ContentType.findByAssociatedObjectType(name);
        } catch (DataObjectNotFoundException ex) {
            throw new UncheckedWrapperException( 
                (String) GlobalizationUtil.globalize(
                "cms.installer.cannot_find_content_type").localize() + name, ex);
        }

        s_log.info("Adding type " + name + " to " + m_section.getDisplayName());
        m_section.addContentType(type);

        s_log.info("Setting the default lifecycle for " +
                   name + " to " + m_lcd.getLabel());
        ContentTypeLifecycleDefinition.
                updateLifecycleDefinition(m_section, type, m_lcd);
        m_lcd.save();

        s_log.info("Setting the default workflow template for " + name +
                   " to " + m_wf.getLabel());
        ContentTypeWorkflowTemplate.updateWorkflowTemplate(m_section, type, m_wf);
        m_wf.save();

        return type;
    }

    void registerTemplate(ContentType type, String filename) {
        // Use the base of the file name (ie without path & extension)
        // as the template name
        int pos1 = filename.lastIndexOf("/");
        int pos2 = filename.lastIndexOf(".");

        if (pos2 == -1)
            pos2 = filename.length();

        String label = filename.substring(pos1+1,pos2);

        String typename = type.getClassName();
        int pos3 = typename.lastIndexOf(".");
        String name = typename.substring(pos3+1, typename.length()) + "-" + label;

        Template temp = new Template();
        temp.setContentSection(m_section);
        temp.setName(name);
        temp.setLabel(label);
        temp.setParent(m_section.getTemplatesFolder());

        final ClassLoader loader = Thread.currentThread
            ().getContextClassLoader();
        final InputStream stream = loader.getResourceAsStream
            (filename.substring(1));

        if (stream == null) {
            throw new IllegalStateException
                ((String) GlobalizationUtil.globalize
                 ("cms.installer.cannot_find_file").localize() + filename);
        }

        final BufferedReader input = new BufferedReader
            (new InputStreamReader(stream));

        StringBuffer body = new StringBuffer();
        String line;
        for (;;) {
            try {
                line = input.readLine();
            } catch (IOException ex) {
                throw new UncheckedWrapperException( 
                    (String) GlobalizationUtil.globalize(
                    "cms.installer.cannot_read_line_of_data").localize(),  ex);
            }
            if (line == null)
                break;

            body.append(line);
            body.append("\n");
        }

        temp.setText(body.toString());

        temp.save();

        TemplateManagerFactory.getInstance()
                .addTemplate(m_section, type, temp, "public");

        temp.publish(m_lcd, new Date());
        // Dan said to comment this out
        // temp.getLifecycle().start();
    }

    public void registerPublicationCycles()
            throws InitializationException {

        // The feature lifecycle.
        LifecycleDefinition lcd = new LifecycleDefinition();
        lcd.setLabel( (String) GlobalizationUtil.globalize(
                      "cms.installer.simple_publication").localize());
        lcd.setDescription("A one-phase lifecycle for items.");
        lcd.save();

        PhaseDefinition pd = lcd.addPhaseDefinition(
                "Live", "The first phase. It lasts forever.",
                new Integer(0), null, null);
        pd.save();

        lcd.save();

        m_lcd = lcd;

        m_section.addLifecycleDefinition(lcd);
        m_section.save();
    }

    /**
     *  SAX Handler for category lists.  Creates the categories as they are
     *  defined, with structure, in the xml document.
     */
    private class CategoryHandler extends DefaultHandler {
        private Stack m_cats = new Stack();
        private ContentSection m_section;

        public CategoryHandler(ContentSection section) {
            m_section = section;
        }

        public void startElement ( String uri, String local,
                                   String qName, Attributes attrs ) {
            if ("categories".equals(qName)) {
                String name = attrs.getValue("name");
                if (name == null) {
                    name = "Root";
                }
                String description = attrs.getValue("description");
                String context = attrs.getValue("context");
                
                Category root = Category.getRootForObject(m_section, 
                                                          context);
                if (root == null) {
                    root = new Category();
                }
                root.setName(name);
                root.setDescription(description);

                if (root.isNew()) {
                    Category.setRootForObject(m_section,
                                              root,
                                              context);
                }
                m_cats.push(root);
                PermissionService.setContext(root, m_section);
            } else if ( "category".equals(qName) ) {
                String name = attrs.getValue("name");
                String description = attrs.getValue("description");
                String url = attrs.getValue("url");

                // set the default description to the name of the category
                if ( description == null ) {
                    description = name;
                }

                s_log.debug("creating category '" + name + "'");
                Category cat = new Category(name, description, url);
                cat.save();

                Category parent = null;
                try {
                    parent = (Category)m_cats.peek();
                } catch ( EmptyStackException ex ) {
                    throw new UncheckedWrapperException("no root category", ex);
                }

                parent.addChild(cat);
                parent.save();
                cat.setDefaultParentCategory(parent);
                cat.save();

                m_cats.push(cat);
            }
        }

        public void endElement ( String uri, String local, String qName ) {
            if ( "category".equals(qName) ) {
                m_cats.pop();
            } else if ( "categories".equals(qName)) {
                m_cats.pop();
            }
        }
    }

    public void loadAlertPrefs(List tasks) {
        if (tasks == null) {
            // fallback to default com.arsdigita.cms.default_task_alerts registry parameter
            String[] defaultTaskAlerts = ContentSection.getConfig().getDefaultTaskAlerts();
            if (defaultTaskAlerts != null) {
                for (int i=0,n=defaultTaskAlerts.length; i<n; i++) {
                    StringTokenizer tok = new StringTokenizer(defaultTaskAlerts[i],":");
                    try {
                        String taskName = tok.nextToken();
                        while (tok.hasMoreTokens()) {
                            String operation = tok.nextToken();
                            CMSTask.addAlert(m_section, taskName, operation);
                        }
                    } catch (NoSuchElementException nsee) {
                        s_log.warn("Invalid task alerts definition");
                    }
                }
            }
        } else {
            Iterator taskIter = tasks.iterator();
            while (taskIter.hasNext()) {
                List oneTask = (List) taskIter.next();
                String taskName = (String) oneTask.get(0);
                List operationList = (List) oneTask.get(1);
                Iterator operationIter = null;
                if (operationList != null) {
                    operationIter = operationList.iterator();
                }
                if (operationIter != null) {
                    while (operationIter.hasNext()) {
                        String operation = (String) operationIter.next();
                        CMSTask.addAlert(m_section, taskName, operation);
                    }
                }
            }
        }
    }

    public Timer startNotifierTask(Boolean sendOverdue, Integer duration,
                                   Integer alertInterval, Integer max) {
        Timer unfinished = null;
        if (sendOverdue.booleanValue()) {
            if (duration == null || alertInterval == null || max == null) {
                s_log.info("Not sending overdue task alerts, " +
                           "required initialization parameters were not specified");
                return null;
            }
            // start the Timer as a daemon, so it doesn't keep the JVM from exiting
            unfinished = new Timer(true);
            UnfinishedTaskNotifier notifier = new UnfinishedTaskNotifier(
                                                  m_section, duration.intValue(),
                    alertInterval.intValue(), max.intValue());
            // schedule the Task to start in 5 minutes, at 1 hour intervals
            unfinished.schedule(notifier, 5L * 60 * 1000, 60L * 60 * 1000);
            s_log.info("Sending overdue alerts for tasks greater than " +
                       duration + " hours old");
        } else {
            s_log.info("Not sending overdue task alerts");
        }

        return unfinished;
    }

}
