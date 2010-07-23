/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the CCM Public
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the
 * License at http://www.redhat.com/licenses/ccmpl.html.
 *
 * Software distributed under the License is distributed on an
 * "AS IS" basis, WITHOUT WARRANTY OF ANY KIND, either express
 * or implied. See the License for the specific language
 * governing rights and limitations under the License.
 *
 */
package com.arsdigita.cms.ui.portlet;


import com.arsdigita.bebop.GridPanel;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Link;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.portal.AbstractPortletRenderer;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.dispatcher.Utilities;
import com.arsdigita.cms.portlet.TaskPortlet;
import com.arsdigita.cms.ui.ContentItemPage;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.cms.workflow.CMSEngine;
import com.arsdigita.cms.workflow.CMSTask;
import com.arsdigita.kernel.User;
import com.arsdigita.web.URL;
import com.arsdigita.web.Web;
import com.arsdigita.workflow.simple.Engine;
import com.arsdigita.xml.Element;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 *
 * Represents as XML the most recent user tasks.
 *
 * @author Richard Li
 * @author Jim Parsons
 * @version $Id: TaskPortletRenderer.java 1280 2006-07-27 09:12:09Z cgyg9330 $
 */
public class TaskPortletRenderer extends AbstractPortletRenderer {

    private static final Logger s_log = Logger.getLogger
            (TaskPortletRenderer.class);

    private TaskPortlet m_portlet;

    public TaskPortletRenderer(TaskPortlet portlet) {
        m_portlet = portlet;
    }

    protected void generateBodyXML(PageState pageState, Element parentElement) {


        final String contentCenter = Utilities.getWorkspaceURL();


        Link link = new Link("Content Center", contentCenter);
        SimpleContainer container = new SimpleContainer();
        container.add(new Label("Go to "));
        container.add(link);
        container.generateXML(pageState, parentElement);

        //link.generateXML(pageState, parentElement);
        GridPanel taskDisplay = new GridPanel(3);
        int i = 0;
        CMSTask currentTask;
        ContentSection section;
        ContentItem item;
        Label dueday;
        Date dday;

        User currentUser = Web.getContext().getUser();
        Date currentDate = new Date();

        final int numTasks = m_portlet.getMaxNumTasks();

        if ( currentUser == null ) {
            taskDisplay.add
                    (new Label(GlobalizationUtil.globalize
                    ("cms.ui.portlet.tasks.no_user_logged_in")));

            taskDisplay.generateXML(pageState, parentElement);

            return;
        }

        List alist = Engine.getInstance(CMSEngine.CMS_ENGINE_TYPE).getEnabledTasks(currentUser);

        Iterator it = alist.iterator();

        if(!it.hasNext()) {
            taskDisplay.add
                    (new Label(GlobalizationUtil.globalize
                    ("cms.ui.portlet.tasks.no_assigned_tasks")));
            return;
        }

        while(it.hasNext()) {
            currentTask = (CMSTask)it.next();
            item = currentTask.getItem();
            section = currentTask.getContentSection();
            i++;
            //We wish to place the following in a table:
            //Name of Content Item
            //Description of task
            //Due date (rendered in RED if beyond)

            //s_log.warn("getpath is: " + cpath.get)
            final String itemURL = ContentItemPage.getItemURL(section.getPath() + "/", item.getID(), 
                                                              ContentItemPage.AUTHORING_TAB);
            URL fullURL = URL.there(pageState.getRequest(), itemURL, null);

            taskDisplay.add(new Link(item.getDisplayName(), fullURL));
            taskDisplay.add(new Label(currentTask.getDisplayName()));
            dueday = new Label(currentTask.getDueDate().toString());
            dueday.setIdAttr("duedate");
            dday = currentTask.getDueDate();
            if(currentDate.before(dday))
                dueday.setClassAttr("overdue");
            else
                dueday.setClassAttr("ok");
            taskDisplay.add(dueday);

            if(i == numTasks)
                break;
        }


        taskDisplay.generateXML(pageState, parentElement);
    }

   /*
    * Make certain that this Portlet is not cached.
    *
    */
    public boolean isDirty() {
        return true;
    }

}
