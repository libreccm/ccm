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

package com.arsdigita.cms.webpage.ui;

import java.util.ArrayList;
import java.util.Iterator;

import org.apache.log4j.Logger;

import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.bebop.form.MultipleSelect;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.bebop.parameters.StringInRangeValidationListener;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.bebop.portal.PortletConfigFormSection;
import com.arsdigita.categorization.CategoryCollection;
import com.arsdigita.cms.ContentBundle;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.Folder;
import com.arsdigita.cms.dispatcher.ItemResolver;
import com.arsdigita.cms.dispatcher.SimpleItemResolver;
import com.arsdigita.cms.ui.CMSDHTMLEditor;
import com.arsdigita.cms.webpage.Webpage;
import com.arsdigita.cms.webpage.installer.Initializer;
import com.arsdigita.cms.workflow.CMSTask;
import com.arsdigita.web.Application;
import com.arsdigita.kernel.ResourceType;
import com.arsdigita.kernel.permissions.PermissionService;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.TransactionContext;
import com.arsdigita.portal.Portlet;
import com.arsdigita.util.Assert;
import com.arsdigita.util.StringUtils;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.web.Web;
import com.arsdigita.cms.webpage.util.WebpageGlobalizationUtil;
import com.arsdigita.workflow.simple.Engine;
import com.arsdigita.workflow.simple.Task;
import com.arsdigita.workflow.simple.TaskCollection;
import com.arsdigita.workflow.simple.Workflow;
import com.arsdigita.workflow.simple.WorkflowTemplate;

public class WebpagePortletEditor extends PortletConfigFormSection {

	private static final Logger s_log = Logger.getLogger(WebpagePortletEditor.class);

	TextField m_author, m_title;
	MultipleSelect m_categories;
	CMSDHTMLEditor m_desc, m_body;
	//private RequestLocal m_parentAppRL;

	public WebpagePortletEditor(ResourceType resType, RequestLocal parentAppRL) {
		super(resType, parentAppRL);
		//m_parentAppRL = parentAppRL;
	}

	public WebpagePortletEditor(RequestLocal application) {
		super(application);
	}

	public void addWidgets() {
		add(new Label("Body:"));
		StringParameter bodyParam = new StringParameter(Webpage.BODY);
		m_body = new CMSDHTMLEditor(bodyParam);
		m_body.setCols(80);
		m_body.setRows(20);
		add(m_body);
		
		m_title = new TextField(new StringParameter("title"));
		m_title.setSize(35);
		m_title.addValidationListener(new NotNullValidationListener());
		m_title.addValidationListener(new StringInRangeValidationListener(1, 200));
		add(new Label("Title:"));
		add(m_title);

		m_author = new TextField(Webpage.AUTHOR);
		add(new Label(new AuthorLabelPrinter()));
		add(m_author);

		add(new Label("Categories"));
		m_categories = new MultipleSelect("categories");
		m_categories.setSize(5);
		try {
			ContentSection section = Initializer.getConfig().getWebpageSection();

			m_categories.addPrintListener(new CategoriesPrintListener(section));
		}
		catch (java.util.TooManyListenersException tmex) {
			throw new UncheckedWrapperException(tmex.getMessage());
		}
		add(m_categories);

        // EE 20051125 - removed the configWithoutToolbar parameter
		//m_desc = new CMSDHTMLEditor("desc", CMSDHTMLEditor.m_configWithoutToolbar);
        m_desc = new CMSDHTMLEditor("desc");
		m_desc.setRows(20);
		m_desc.setCols(80);
		m_desc.addValidationListener(new StringInRangeValidationListener(1, 4000));

		add(new Label("Description:<br /><font size=-1>(this field is currently not displayed on the website)</font>", false));
		add(m_desc);
	}

	public void initWidgets(PageState state, Portlet portlet) throws FormProcessException {

		if (portlet != null) {
			WebpagePortlet myportlet = (WebpagePortlet) portlet;
			ItemResolver resolver = new SimpleItemResolver();
			Webpage webpage = myportlet.getWebpage();
			if (webpage != null) {
				m_title.setValue(state, portlet.getTitle());
				m_desc.setValue(state, portlet.getDescription());
				m_body.setValue(state, webpage.getBody());
				m_author.setValue(state, webpage.getAuthor());

				ArrayList assignedCats = new ArrayList();
				CategoryCollection cc = webpage.getCategoryCollection();
				while (cc.next()) {
					String catID = cc.getCategory().getID().toString();
					assignedCats.add(catID);
				}
				m_categories.setValue(state, assignedCats.toArray());
			}
		}
	}

	public void validateWidgets(PageState state, Portlet portlet) throws FormProcessException {
		super.validateWidgets(state, portlet);
		
	}

	public void processWidgets(PageState state, Portlet portlet) throws FormProcessException {
		portlet.setTitle((String) m_title.getValue(state));
		portlet.setDescription((String) m_desc.getValue(state));
		WebpagePortlet myportlet = (WebpagePortlet) portlet;
		Webpage webpage = myportlet.getWebpage();

		boolean commitTransaction = false;
		boolean isMyTransaction = false;
		TransactionContext txn = null;
		try {
			Session session = SessionManager.getSession();
			txn = session.getTransactionContext();
			if (!txn.inTxn()) {
				txn.beginTxn();
				isMyTransaction = true;
			}
			if (webpage == null) {
				ContentSection section = Initializer.getConfig().getWebpageSection();
				if (section == null) {
					throw new FormProcessException(WebpageGlobalizationUtil
                                    .globalize("webpage.ui.no_such_contentsection"));
				}
                
                // FR: move reference from PortalSite to Application (could be Workspace)
                Application app = Web.getWebContext().getApplication();
				String folderName = StringUtils.replace(app.getPath(), "/", "-");
				Folder rootFolder = section.getRootFolder();
				Folder folder = (Folder) rootFolder.getItem(folderName, true);
				if (folder == null) {
					folder = new Folder();
					folder.setName(folderName);
					folder.setLabel(folderName);
					folder.setParent(rootFolder);
					PermissionService.setContext(folder, rootFolder);
					folder.save();
				}

				webpage = new Webpage();
				webpage.setLanguage("en");
//				webpage.setName("webpage" + webpage.getID());
				webpage.setTitle(portlet.getTitle());
				webpage.setDescription(portlet.getDescription());
				webpage.setBody((String) m_body.getValue(state));
				webpage.setAuthor((String) m_author.getValue(state));
				webpage.save();

				ContentBundle bundle = new ContentBundle(webpage);
				bundle.setContentSection(section);
				bundle.setParent(folder);
				webpage.setCategories((String[]) m_categories.getValue(state));
				
				// set workflow
				TaskCollection taskColl = null;
				try {
					taskColl = section.getWorkflowTemplates();
					if (taskColl.next()) {
						Task task = taskColl.getTask();
						final WorkflowTemplate wfTemp = new WorkflowTemplate(task.getID());
						final Workflow flow = wfTemp.instantiateNewWorkflow();
		                flow.setObjectID(webpage.getID());
		                flow.start(Web.getWebContext().getUser());
		                flow.save();
					}
				}
				finally {
					if (taskColl != null) {
						taskColl.close();
					}
				}
				
				// finish all task
				Workflow workflow = Workflow.getObjectWorkflow(webpage);
				
				Engine engine = Engine.getInstance();
				Assert.exists(engine, Engine.class);
				Iterator i = engine.getEnabledTasks(Web.getWebContext().getUser(), workflow.getID()).iterator();
				CMSTask task;
				do {
					while (i.hasNext()) {
						task = (CMSTask) i.next();
						try {
							task.finish();
						}
						catch (Exception ex) {
							throw new FormProcessException(ex);
						}
					}
					
					i = engine.getEnabledTasks(Web.getWebContext().getUser(), workflow.getID()).iterator();
				}
				while (i.hasNext());
				
				myportlet.setWebpage(webpage);
			}
			else {
				webpage.setTitle(portlet.getTitle());
				webpage.setDescription(portlet.getDescription());
				webpage.setBody((String) m_body.getValue(state));
				webpage.setAuthor((String) m_author.getValue(state));
				webpage.setCategories((String[]) m_categories.getValue(state));
				webpage.save();
			}
			commitTransaction = true;
		}
		finally {
			if (txn != null && isMyTransaction) {
				if (commitTransaction) {
					txn.commitTxn();
				}
				else {
					txn.abortTxn();
				}
			}
		}
	}
}
