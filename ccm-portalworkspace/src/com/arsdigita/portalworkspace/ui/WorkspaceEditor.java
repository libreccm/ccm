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

import com.arsdigita.bebop.ActionLink;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.NotEmptyValidationListener;
import com.arsdigita.dispatcher.AccessDeniedException;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.Party;
import com.arsdigita.portalworkspace.Workspace;
import com.arsdigita.portalworkspace.WorkspacePage;
import com.arsdigita.portalworkspace.util.GlobalizationUtil;

import org.apache.log4j.Logger;

/**
 * Entry point into a standard (public or access restricted) portal workspace page where the page is
 * constructed in "edit" mode to allow configuration and modification by an authorized participant.
 *
 * It is used via a jsp page which is invoked at the applications url.
 *
 * Example code stub:
 * <pre>
 * <define:component name="edit"
 *          classname="com.arsdigita.portalworkspace.ui.WorkspaceEditor" />
 * <jsp:scriptlet>
 *    ((AbstractWorkspaceComponent)edit).setWorkspaceModel(
 *                                       new DefaultWorkspaceSelectionModel());
 * </jsp:scriptlet>
 * </pre>
 *
 * Currently there is a jsp for the default url at (web)/templates/ccm-portalworkspace/edit.jsp
 * which is mapped via web.xml to /ccm/portal/edit.jsp in the default, pre-configured configuration.
 */
public class WorkspaceEditor extends AbstractWorkspaceComponent {

    private static final Logger s_log = Logger.getLogger(WorkspaceEditor.class);

    private ActionLink m_add;

    private BasicPropertiesForm m_basisPropertiesForm;

    private ActionLink m_editBasicPropertiesLink;

    /**
     * Default Constructor constructs a new, empty WorkspaceEditor object.
     */
    public WorkspaceEditor() {
        this(null);
    }

    /**
     * Constructs a WorkspaceViewer for a specific workspace object and sets the xml tags
     * accordingly.
     *
     * @param workspace
     */
    public WorkspaceEditor(WorkspaceSelectionAbstractModel workspace) {

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

    @Override
    public void register(Page page) {
        super.register(page);
        // Modigyable themes removed, cf. above
        // page.setVisibleDefault(m_selectForm, !m_workspaceThemes.isEmpty());
        // page.setVisibleDefault(m_createForm, false);
        page.setVisibleDefault(m_basisPropertiesForm, false);
    }

    /**
     *
     * @param portal
     * @return
     */
    protected PersistentPortal createPortalDisplay(PortalSelectionModel portal) {
        return new PersistentPortal(portal, WorkspacePage.MODE_EDITOR);
    }

    /**
     *
     */
    private class BasicPropertiesLinkListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            PageState ps = e.getPageState();
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
