/*
 * Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
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
 */

package com.arsdigita.portalworkspace.ui;

import java.math.BigDecimal;

import org.apache.log4j.Logger;

import com.arsdigita.bebop.ActionLink;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.RadioGroup;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.NotEmptyValidationListener;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.dispatcher.AccessDeniedException;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.Party;
import com.arsdigita.portalworkspace.Workspace;
import com.arsdigita.portalworkspace.WorkspacePage;
import com.arsdigita.portalworkspace.util.GlobalizationUtil;


/**
 * Another entry point into a standard portal workspace page where the
 * page is constructed in "edit" mode to allow configuration and modification
 * by an authorized participant.
 *
 * It is used via a jsp page which is invoked at the applications url.
 *
 * Example code stub:
 * <pre>
 * <define:component name="edit"
 *              classname="com.arsdigita.portalworkspace.ui.WorkspaceEditor" />
 * <jsp:scriptlet>
 *    ((AbstractWorkspaceComponent)edit).setWorkspaceModel(
 *                                           new DefaultWorkspaceSelectionModel());
 * </jsp:scriptlet>
 * </pre>
 *
 * Currently there is a jsp for the default url at
 * (web)/templates/ccm-portalworkspace/edit.jsp which is mapped via web.xml
 * to /ccm/portal/edit.jsp in the default, pre-configured configuration.
 */
public class WorkspaceEditor extends AbstractWorkspaceComponent {

    private static final Logger s_log = Logger.getLogger(WorkspaceEditor.class);

    private ActionLink m_add;

    private SelectThemeForm m_selectForm;

    private RadioGroup m_grp;

    private CreateThemeForm m_createForm;

    private ActionLink m_createThemeLink;

    private WorkspaceThemeCollection m_workspaceThemes;

    private BasicPropertiesForm m_basisPropertiesForm;

    private ActionLink m_editBasicPropertiesLink;


    /**
     * Default Constructor constructs a new, empty WorkspaceEditor object.
     */
    public WorkspaceEditor() {
        this(null);
    }

    /**
     * Constructs a WorkspaceViewer for a specific workspace object
     * and sets the xml tags accordingly.
     * 
     * @param workspace
     */
    public WorkspaceEditor(WorkspaceSelectionModel workspace) {

        super(workspace);
        s_log.debug("WorkspaceEditor constructed");

        m_add = new ActionLink("add pane");
        m_add.setClassAttr("actionLink");
        m_add.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				PageState state = e.getPageState();

				Workspace workspace = getSelectedWorkspace(state);
				Party party = Kernel.getContext().getParty();
				if (!PortalHelper.canCustomize(party, workspace)) {
					throw new AccessDeniedException(
							"no permissions to customize workspace");
				}

				WorkspacePage page = workspace.addPage("New page",
						"New portal page");
			}
		});

        add(m_add);

        m_createThemeLink = new ActionLink((String) GlobalizationUtil
				.globalize("portal.ui.admin.create_theme").localize());
        m_createThemeLink.setClassAttr("actionLink");
        m_createThemeLink.addActionListener(new CreateThemeLinkListener());
        m_grp = new RadioGroup("themes");
        m_grp.setClassAttr("vertical");
        populateOptionGroup();
        m_selectForm = new SelectThemeForm();
        m_createForm = new CreateThemeForm();

        add(m_selectForm);
        add(m_createForm);
        add(m_createThemeLink); // nb. this line is commented in the undp src

        // now add the basic properties controls
        m_editBasicPropertiesLink = new ActionLink((String) GlobalizationUtil
				.globalize("portal.ui.admin.edit_basic_properties").localize());
        m_editBasicPropertiesLink
				.addActionListener(new BasicPropertiesLinkListener());
        m_editBasicPropertiesLink.setClassAttr("actionLink");
        add(m_editBasicPropertiesLink);
        m_basisPropertiesForm = new BasicPropertiesForm();
        add(m_basisPropertiesForm);
    }

    /**
     * 
     * @param portal
     * @return
     */
    protected PortalList createPortalList(PortalSelectionModel portal) {
        return new PortalListEditor(portal);
    }


    public void register(Page page) {
		super.register(page);
		page.setVisibleDefault(m_selectForm, !m_workspaceThemes.isEmpty());
		page.setVisibleDefault(m_createForm, false);
		page.setVisibleDefault(m_basisPropertiesForm, false);
    }

    
    /**
     *
     * @param portal
     * @return
     */
    protected PersistentPortal createPortalDisplay(PortalSelectionModel portal) {
        return new PersistentPortal(portal, PortalConstants.MODE_EDITOR);
    }

    // TODO
    // there is a big problem with this model
    // the options are locked and therefore you can't see
    // the theme created until after a server restart
    // however this is as the code came from skylift so
    // I'm leaving it like this.
    private void populateOptionGroup() {
		m_grp.clearOptions();
		m_workspaceThemes = WorkspaceTheme.retrieveAllWorkspaceThemes();
		while (m_workspaceThemes.next()) {
			String id = m_workspaceThemes.getWorkspaceTheme().getID()
					.toString();
			String name = m_workspaceThemes.getWorkspaceTheme().getName();
			addOption(id, name);
		}
    }

    private void addOption(String id, String name) {
		Option opt = new Option(id, name);
		m_grp.addOption(opt);
    }

    /**
     * 
     */
    public class SelectThemeForm extends Form implements FormProcessListener,
                                                         FormInitListener {
        private Label instruction;
        private Submit button;
        RequestLocal prtlRL;

        /**
         *
         */
        public SelectThemeForm() {
			super("selectthemeform");
			s_log.debug("SelectThemeForm constructed");
			instruction = new Label(GlobalizationUtil
					.globalize("portal.ui.admin.select_theme_for_portal"));
			button = new Submit("selecttheme", GlobalizationUtil
					.globalize("portal.ui.select_theme"));
			button.setButtonLabel("Select Theme");

			add(instruction);
			add(button);
			m_grp.addValidationListener(new NotNullValidationListener(
					"Select a Theme"));
			add(m_grp);
			addProcessListener(this);
			addInitListener(this);
        }

        /**
         *
         * @param e
         */
        public void process(FormSectionEvent e) {
			s_log.debug("process called for SelectThemeForm");
			String selectedkey;
			PageState s = e.getPageState();

			if (button.isSelected(s)) {
				selectedkey = (String) m_grp.getValue(s);
				BigDecimal bd = new BigDecimal(selectedkey);

				WorkspaceTheme theme = WorkspaceTheme
						.retrieveWorkspaceTheme(bd);
				Workspace workspace = getSelectedWorkspace(s);
				workspace.setTheme(theme);
				workspace.save();
			}
        }

        public void init(FormSectionEvent e) throws FormProcessException {
			s_log.debug("init called for SelectThemeForm");
			PageState ps = e.getPageState();
			if (m_workspaceThemes.isEmpty())
				this.setVisible(ps, false);
			if (this.isVisible(ps)) {
				m_createThemeLink.setVisible(ps, true);
				m_createForm.setVisible(ps, false);
				m_basisPropertiesForm.setVisible(ps, false);
			}
			FormData fd = e.getFormData();
			Workspace workspace = getSelectedWorkspace(ps);
			WorkspaceTheme theme = workspace.getTheme();
			if (theme != null)
				fd.put("themes", theme.getID().toString());
        }

    }


    /** 
     * 
     */
    public class CreateThemeForm extends Form implements FormProcessListener,
                                                         FormInitListener {
        private Label instruction;
        private Submit savebutton;
        private TextField themename;
        private ColorPicker background;
        private ColorPicker text;
        private ColorPicker activetab;
        private ColorPicker inactivetab;
        private ColorPicker activetabtext;
        private ColorPicker inactivetabtext;
        private ColorPicker toprule;
        private ColorPicker bottomrule;
        private ColorPicker portletheader;
        private ColorPicker portletborder;
        private ColorPicker portletheadertext;
        private ColorPicker portletbodynarrow;
        RequestLocal prtlRL;

        /**
         *
         */
        public CreateThemeForm() {
			super("createthemeform");
			s_log.debug("CreateThemeForm constructed");
			setClassAttr("themecreator");
			savebutton = new Submit("savetheme");
			savebutton.setButtonLabel("Save Theme");
			instruction = new Label(GlobalizationUtil
					.globalize("portal.ui.admin.create_theme_instruction"));
			text = new ColorPicker("Page Text Color ", "#112233");
			background = new ColorPicker("Page Background Color ", "#112233");
			activetab = new ColorPicker("Selected Tab Color ", "#1F22B3");
			inactivetab = new ColorPicker("Unselected Tab Color ", "#112233");
			activetabtext = new ColorPicker("Selected Tab Text Color",
					"#FFFFFF");
			inactivetabtext = new ColorPicker("Unselected Tab Text Color ",
					"#11CC33");
			toprule = new ColorPicker("Top Rule Color ", "#FFFFFF");
			bottomrule = new ColorPicker("Bottom Rule Color ", "#FFFFFF");
			portletheader = new ColorPicker("Portlet Header Color ", "#FFFFFF");
			portletborder = new ColorPicker("Portlet Border Color ", "#FFFFFF");
			portletheadertext = new ColorPicker("Portlet Header Text Color ",
					"#FFFFFF");
			portletbodynarrow = new ColorPicker(
					"Narrow Column Portlet Body Color ", "#FFFFFF");

			themename = new TextField("themename");
			themename.addValidationListener(new NotNullValidationListener(
					"Please provide a name for this theme."));

			add(instruction);
			add(themename);
			add(background);
			add(text);
			add(activetab);
			add(inactivetab);
			add(activetabtext);
			add(inactivetabtext);
			add(toprule);
			add(bottomrule);
			add(portletheader);
			add(portletheadertext);
			add(portletborder);
			add(portletbodynarrow);
			add(savebutton);

			addProcessListener(this);
			addInitListener(this);
        }

		public void process(FormSectionEvent e) {
			s_log.debug("processing the create theme form");
			PageState ps = e.getPageState();
			if (savebutton.isSelected(ps)) {
				WorkspaceTheme theme = new WorkspaceTheme((String) themename
						.getValue(ps));
				theme.setActiveTabColor(activetab.getValue(ps));
				theme.setInactiveTabColor(inactivetab.getValue(ps));
				theme.setActiveTabTextColor(activetabtext.getValue(ps));
				theme.setInactiveTabTextColor(inactivetabtext.getValue(ps));
				theme.setTopRuleColor(toprule.getValue(ps));
				theme.setBottomRuleColor(bottomrule.getValue(ps));
				theme.setPortletHeaderColor(portletheader.getValue(ps));
				theme.setPortletIconColor(portletheader.getValue(ps));
				theme.setPortletBorderColor(portletborder.getValue(ps));
				theme.setPortletHeaderTextColor(portletheadertext.getValue(ps));
				theme.setPageBGColor(background.getValue(ps));
				theme.setBodyTextColor(text.getValue(ps));
				theme.setNarrowBGColor(portletbodynarrow.getValue(ps));
				s_log.debug("saving the theme");
				theme.save();
			}
			m_selectForm.setVisible(ps, true);
			m_createThemeLink.setVisible(ps, true);
			m_editBasicPropertiesLink.setVisible(ps, true);
			this.setVisible(ps, false);
		}

		public void init(FormSectionEvent e) throws FormProcessException {
			// PageState ps = e.getPageState();
			// this.setVisible(ps, false);
		}
	}

	private class CreateThemeLinkListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			PageState ps = event.getPageState();
			m_createForm.setVisible(ps, true);
			m_selectForm.setVisible(ps, false);
			m_createThemeLink.setVisible(ps, false);
			m_editBasicPropertiesLink.setVisible(ps, false);
		}
	}

	private class BasicPropertiesLinkListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			PageState ps = e.getPageState();
			m_createForm.setVisible(ps, false);
			m_selectForm.setVisible(ps, false);
			m_createThemeLink.setVisible(ps, false);
			m_editBasicPropertiesLink.setVisible(ps, false);
			m_basisPropertiesForm.setVisible(ps, true);
		}
	}

	public class BasicPropertiesForm extends Form implements
			FormProcessListener, FormInitListener {
		private Label title;

		private TextField m_title;

		private Label description;

		private Submit savebutton;

		private Submit cancelbutton;

		private TextArea m_description;

		public BasicPropertiesForm() {
			super("basicpropertiesform");
			setClassAttr("basicprops");

			title = new Label(GlobalizationUtil
					.globalize("portal.ui.admin.workspace_title"));

			m_title = new TextField("title");
			m_title.setSize(40);
			m_title.getParameterModel().addParameterListener(
					new NotEmptyValidationListener());

			m_description = new TextArea("description");
			m_description.setRows(10);
			m_description.setCols(40);

			description = new Label(GlobalizationUtil
					.globalize("portal.ui.admin.workspace_description"));

			savebutton = new Submit("save", "Save");
			cancelbutton = new Submit("cancel", "Cancel");

			add(title);
			add(m_title);
			add(description);
			add(m_description);
			add(cancelbutton);
			add(savebutton);
			addProcessListener(this);
			addInitListener(this);
		}

		public void process(FormSectionEvent e) {
			s_log.debug("processing the basic properties form");
			PageState ps = e.getPageState();
			if (savebutton.isSelected(ps)) {

				Workspace workspace = getSelectedWorkspace(ps);
				workspace.setTitle((String) m_title.getValue(ps));
				workspace.setDescription((String) m_description.getValue(ps));
				workspace.save();
			}
			m_selectForm.setVisible(ps, true);
			m_createThemeLink.setVisible(ps, true);
			m_editBasicPropertiesLink.setVisible(ps, true);
			this.setVisible(ps, false);
		}

		public void init(FormSectionEvent e) throws FormProcessException {
			// s_log.debug("initialising the basic properties form");
			PageState ps = e.getPageState();
			Workspace workspace = getSelectedWorkspace(ps);
			m_title.setValue(ps, workspace.getTitle());
			m_description.setValue(ps, workspace.getDescription());
			// s_log.debug("setting visibility to false");
			// this.setVisible(ps, false);
		}
	}
}
