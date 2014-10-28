/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.arsdigita.portalworkspace.ui;

import com.arsdigita.bebop.ActionLink;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.ParameterSingleSelectionModel;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.bebop.portal.PortletRenderer;
import com.arsdigita.categorization.Category;
import com.arsdigita.dispatcher.AccessDeniedException;
import com.arsdigita.kernel.Group;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.Party;
import com.arsdigita.kernel.PartyCollection;
import com.arsdigita.navigation.Navigation;
import com.arsdigita.navigation.NavigationModel;
import com.arsdigita.portal.Portlet;
import com.arsdigita.portal.PortletCollection;
import com.arsdigita.portalworkspace.Workspace;
import com.arsdigita.portalworkspace.WorkspacePage;
import com.arsdigita.portalworkspace.WorkspacePageCollection;
import com.arsdigita.portalworkspace.util.GlobalizationUtil;
import com.arsdigita.web.ParameterMap;
import com.arsdigita.web.URL;
import com.arsdigita.xml.Element;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author jensp
 */
public class WorkspaceGridViewer extends SimpleContainer {

    private WorkspaceSelectionAbstractModel workspaceModel;
    private ParameterSingleSelectionModel rowAction;
    private ParameterSingleSelectionModel rowModel;

    private WorkspaceDetails workspaceDetails;
    private WorkspaceCreator workspaceCreator;
    private WorkspaceEditor workspaceEditor;
    private WorkspaceGridRowEditor workspaceGridRowEditor;

    public WorkspaceGridViewer() {
        this(null);
    }

    public WorkspaceGridViewer(final WorkspaceSelectionAbstractModel workspaceModel) {
        super("portal:gridWorkspace", WorkspacePage.PORTAL_XML_NS);

        this.workspaceModel = workspaceModel;

        rowAction = new ParameterSingleSelectionModel(new StringParameter("rowAction"));
        rowModel = new ParameterSingleSelectionModel(new StringParameter("selectedRow"));

        workspaceDetails = new WorkspaceDetails(workspaceModel);
        workspaceEditor = new WorkspaceEditor();
        workspaceGridRowEditor = new WorkspaceGridRowEditor();

        workspaceCreator = new WorkspaceCreator();
        add(workspaceCreator);

        add(workspaceDetails);

        add(workspaceEditor);

        final SimpleContainer gridRowEditorContainer = new SimpleContainer(
            "portal:workspaceGridRowEditor", WorkspacePage.PORTAL_XML_NS) {

                @Override
                public boolean isVisible(final PageState state) {
                    return super.isVisible(state) && "edit".equals(rowAction.getSelectedKey(state));
                }

            };
        add(gridRowEditorContainer);
        gridRowEditorContainer.add(workspaceGridRowEditor);

    }

    public WorkspaceSelectionAbstractModel getWorkspaceModel() {
        return workspaceModel;
    }

    public void setWorkspaceModel(final WorkspaceSelectionAbstractModel workspaceModel) {
        this.workspaceModel = workspaceModel;
        workspaceDetails.setWorkspaceModel(workspaceModel);
    }

    @Override
    public void register(final Page page) {
        super.register(page);

        page.addComponentStateParam(this, rowAction.getStateParameter());
        page.addComponentStateParam(this, rowModel.getStateParameter());
    }

    @Override
    public void generateXML(final PageState state, final Element parent) {
        if (isVisible(state)) {
            final Element content = generateParent(parent);

            if (workspaceModel.getSelectedWorkspace(state) == null) {
                workspaceDetails.setVisible(state, false);
                workspaceCreator.setVisible(state, true);
            } else {
                workspaceDetails.setVisible(state, true);
                workspaceCreator.setVisible(state, false);
            }

            if (rowModel.isSelected(state)) {
                content.addAttribute("selectedRow", (String) rowModel.getSelectedKey(state));
            }

            generateChildrenXML(state, content);

            if (workspaceModel.getSelectedWorkspace(state) != null) {

                final WorkspacePageCollection pages = workspaceModel.getSelectedWorkspace(state).
                    getPages();

                final Element pagesElem = content.newChildElement("portal:rows",
                                                                  WorkspacePage.PORTAL_XML_NS);

                while (pages.next()) {
                    generateRowXML(pagesElem, state, pages.getPage());
                }
            }

        }

    }

    private void generateRowXML(final Element pagesElem,
                                final PageState state,
                                final WorkspacePage row) {
        final Element rowElem = pagesElem.newChildElement("portal:row",
                                                          WorkspacePage.PORTAL_XML_NS);

        rowElem.addAttribute("id", row.getID().toString());
        rowElem.addAttribute("layout", row.getLayout().getFormat());
        rowElem.addAttribute("style", row.getLayout().getTitle());
        rowElem.addAttribute("title", row.getTitle());
        rowElem.addAttribute("description", row.getDescription());
        final HttpServletRequest request = state.getRequest();
        final String pathInfo = request.getPathInfo();
        final ParameterMap parameterMap = new ParameterMap(request);
        parameterMap.setParameter(rowAction.getStateParameter().getName(), "edit");
        parameterMap.setParameter(rowModel.getStateParameter().getName(), row.getID().toString());
        rowElem.addAttribute("editLink", URL.here(request, pathInfo, parameterMap).toString());

        final PortletCollection portlets = row.getPortlets();
        final Element portletsElem = rowElem.newChildElement("portal:portlets",
                                                             WorkspacePage.PORTAL_XML_NS);

        while (portlets.next()) {
            generatePortletXML(portletsElem, state, portlets.getPortlet());
        }
    }

    private void generatePortletXML(final Element portletsElem,
                                    final PageState state,
                                    final Portlet portlet) {
        final PortletRenderer renderer = portlet.getPortletRenderer();

        renderer.generateXML(state, portletsElem);
    }

    private class WorkspaceCreator extends SimpleContainer {

        public WorkspaceCreator() {
            super("portal:workspaceCreator", WorkspacePage.PORTAL_XML_NS);

            final Label info = new Label(GlobalizationUtil.globalize(
                "cw.workspace.ui.categorized.create_workspace.info"));
            add(info);

            final ActionLink link = new ActionLink(new Label(GlobalizationUtil.globalize(
                "cw.workspace.ui.categorized.create_workspace.link")));
            link.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(final ActionEvent event) {
                    if (!canCreate()) {
                        throw new AccessDeniedException(
                            "Current party is not authorized to create a new portal workspace");
                    }

                    final NavigationModel navModel = Navigation.getConfig().getDefaultModel();
                    final Category category = navModel.getCategory();

                    final String workspaceUrl = String.format("workspace-%s-%s",
                                                              category.getID().toString(),
                                                              category.getURL());
                    final String workspaceTitle = String.format("Workspace-%s-%s",
                                                                category.getID().toString(),
                                                                category.getName());
                    final String workspaceDesc = String.format(
                        "Portal Workspace for category %s (ID: %s).",
                        category.getID().toString(),
                        category.getName());

                    final Workspace workspace = Workspace.createWorkspace(workspaceUrl,
                                                                          workspaceTitle,
                                                                          null,
                                                                          true);
                    workspace.setDescription(workspaceDesc);
                    workspace.save();

                    category.addChild(workspace);
                }

            });
            add(link);

        }

        @Override
        public boolean isVisible(final PageState state) {
//            final boolean visible = super.isVisible(state);
//
//            return visible && canCreate();
            return workspaceModel.getSelectedWorkspace(state) == null && canCreate();
        }

        public boolean canCreate() {
            final PartyCollection parties = Party.retrieveAllParties();
            parties.filter("Site-wide Administrators");
            parties.next();
            final Group admins = (Group) parties.getParty();
            parties.close();

            final Party currentParty = Kernel.getContext().getParty();
            return currentParty != null && admins.hasMember(currentParty);
        }

    }

    private class WorkspaceEditor extends SimpleContainer {

        public WorkspaceEditor() {
            super("portal:workspaceGridEditor", WorkspacePage.PORTAL_XML_NS);

            final ActionLink addRowLink = new ActionLink(GlobalizationUtil.globalize(
                "cw.workspace.ui.categorized.workspace.add_row.link"));
            addRowLink.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(final ActionEvent event) {
                    final PageState state = event.getPageState();
                    final Workspace workspace = workspaceModel.getSelectedWorkspace(state);
                    final Party currentParty = Kernel.getContext().getParty();
                    if (!PortalHelper.canCustomize(currentParty, workspace)) {
                        throw new AccessDeniedException(
                            "Current party has now permission to edit this workspace");
                    }

                    final WorkspacePageCollection pages = workspace.getPages();
                    final long size = pages.size();
                    pages.close();

                    workspace.addPage(String.format("workspace-grid-row-%d", size + 1), "");

                }

            });

            add(addRowLink);
        }

        @Override
        public boolean isVisible(final PageState state) {
            return workspaceModel.getSelectedWorkspace(state) != null && canEdit(state);
        }

        public boolean canEdit(final PageState state) {
            return PortalHelper.canCustomize(Kernel.getContext().getParty(),
                                             workspaceModel.getSelectedWorkspace(state));
        }

    }

//    private class GridRowSelectionModel extends AbstractSingleSelectionModel {
//
//        private final RequestLocal selected;
//
//        public GridRowSelectionModel() {
//            selected = new RequestLocal();
//        }
//
//        @Override
//        public Object getSelectedKey(final PageState state) {
//            return selected.get(state);
//        }
//
//        @Override
//        public void setSelectedKey(final PageState state, final Object key) {
//            selected.set(state, key);
//        }
//
//        @Override
//        public ParameterModel getStateParameter() {
//            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//        }
//
//    }
    private class WorkspaceGridRowEditor extends Form {

        private final TextField key;

        public WorkspaceGridRowEditor() {
            super("WordspaceGridRowEditor", new SimpleContainer());

            final Label label = new Label();
            label.addPrintListener(new PrintListener() {

                @Override
                public void prepare(final PrintEvent event) {
                    final Label target = (Label) event.getTarget();

                    target.setLabel(rowModel.getStateParameter().getName());
                }

            });
            add(label);

            key = new TextField("key");
            add(key);

            addInitListener(new FormInitListener() {

                @Override
                public void init(final FormSectionEvent event) throws FormProcessException {
                    key.setValue(event.getPageState(),
                                 rowModel.getSelectedKey(event.getPageState()));
                }

            });
        }

        @Override
        public void generateXML(final PageState state, final Element parent) {
            super.generateXML(state, parent);

            if (rowModel.isSelected(state)) {
                parent.addAttribute("selectedRow",
                                    (String) rowModel.getSelectedKey(state));
            }
        }

    }

}
