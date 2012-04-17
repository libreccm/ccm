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

package com.arsdigita.portalworkspace.ui.portlet;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.Map;
import java.util.TooManyListenersException;

import org.apache.log4j.Logger;

import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.portal.PortletConfigFormSection;
import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.SingleSelect;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.bebop.parameters.IntegerParameter;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.categorization.Category;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.kernel.ResourceType;
import com.arsdigita.portalworkspace.portlet.ContentDirectoryPortlet;
// @deprecated use com.arsdigita.bebop.portal.PortletConfigFormSection
// import com.arsdigita.london.portal.ui.PortletConfigFormSection;
import com.arsdigita.categorization.ui.CategorizationTree;
import com.arsdigita.persistence.OID;
import com.arsdigita.portal.Portlet;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.web.Application;
import com.arsdigita.web.Web;

/**
 * 
 * 
 */
public class ContentDirectoryPortletEditor extends PortletConfigFormSection {

	private static final Logger s_log = Logger
			.getLogger(ContentDirectoryPortletEditor.class);

	private SingleSelect m_root;

	private SingleSelect m_layout;

	private SingleSelect m_depth;

	/**
     * Constructor
     * @param resType
     * @param parentAppRL
     */
    public ContentDirectoryPortletEditor(ResourceType resType,
			RequestLocal parentAppRL) {
		super(resType, parentAppRL);
	}

	/**
     * 
     * @param application
     */
    public ContentDirectoryPortletEditor(RequestLocal application) {
		super(application);
	}

	/**
     * 
     */
    public void addWidgets() {
		super.addWidgets();

		m_root = new SingleSelect(new BigDecimalParameter("root"));
		try {
			m_root.addPrintListener(new CategoryPrintListener());
		} catch (TooManyListenersException ex) {
			throw new UncheckedWrapperException("this cannot happen", ex);
		}

		m_layout = new SingleSelect(new StringParameter("layout"));
		m_layout.addOption(new Option("grid", "Grid"));
		m_layout.addOption(new Option("panel", "Panel"));

		m_depth = new SingleSelect(new IntegerParameter("depth"));
		m_depth.addOption(new Option("1", "1 Level"));
		m_depth.addOption(new Option("2", "2 Levels"));

		add(new Label("Root category:", Label.BOLD), ColumnPanel.RIGHT);
		add(m_root);

		add(new Label("Layout:", Label.BOLD), ColumnPanel.RIGHT);
		add(m_layout);

		add(new Label("Depth:", Label.BOLD), ColumnPanel.RIGHT);
		add(m_depth);
	}

	/**
     * 
     * @param state
     * @param portlet
     * @throws FormProcessException
     */
    public void initWidgets(PageState state, Portlet portlet)
			throws FormProcessException {
		super.initWidgets(state, portlet);

		if (portlet != null) {
			ContentDirectoryPortlet myportlet = (ContentDirectoryPortlet) portlet;

			m_root.setValue(state, myportlet.getRoot().getID());
			m_layout.setValue(state, myportlet.getLayout());
			m_depth.setValue(state, new Integer(myportlet.getDepth()));
		}
	}

	/**
     * 
     * @param state
     * @param portlet
     * @throws FormProcessException
     */
    public void processWidgets(PageState state, Portlet portlet)
			throws FormProcessException {
		super.processWidgets(state, portlet);

		ContentDirectoryPortlet myportlet = (ContentDirectoryPortlet) portlet;
		myportlet.setLayout((String) m_layout.getValue(state));
		myportlet.setDepth(((Integer) m_depth.getValue(state)).intValue());

		BigDecimal id = (BigDecimal) m_root.getValue(state);
		Category root;
		try {
			root = (Category) DomainObjectFactory.newInstance(new OID(
					Category.BASE_DATA_OBJECT_TYPE, id));
		} catch (DataObjectNotFoundException ex) {
			throw new UncheckedWrapperException("cannot find category", ex);
		}
		myportlet.setRoot(root);
	}

	/**
     * 
     * @return
     */
    protected String getUseContext() {
		return null;
	}

	/**
     * 
     */
    private class CategoryPrintListener implements PrintListener {
		public void prepare(PrintEvent e) {
			SingleSelect target = (SingleSelect) e.getTarget();

			Application app = Web.getContext().getApplication();
			Category root = Category.getRootForObject(app, getUseContext());

			Map cats = CategorizationTree.getSubtreePath(root, " > ");
			Iterator i = cats.keySet().iterator();
			while (i.hasNext()) {
				String path = (String) i.next();
				Category cat = (Category) cats.get(path);

				target.addOption(new Option(cat.getID().toString(), path));
			}
		}
	}
}
