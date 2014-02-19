/*
 * Copyright (c) 2013 Jens Pelzetter
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
package com.arsdigita.ui.admin;

import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.Tree;
import com.arsdigita.bebop.event.ChangeEvent;
import com.arsdigita.bebop.event.ChangeListener;
import com.arsdigita.toolbox.ui.LayoutPanel;
import com.arsdigita.ui.admin.applications.ApplicationInstanceAwareContainer;
import com.arsdigita.ui.admin.applications.ApplicationInstancePane;
import com.arsdigita.ui.admin.applications.ApplicationManager;
import com.arsdigita.ui.admin.applications.BaseApplicationPane;
import com.arsdigita.ui.admin.applications.MultiInstanceApplicationPane;
import com.arsdigita.ui.admin.applications.SingletonApplicationPane;
import com.arsdigita.ui.admin.applications.tree.ApplicationTreeModelBuilder;
import com.arsdigita.web.Application;
import com.arsdigita.web.ApplicationCollection;
import com.arsdigita.web.ApplicationType;
import com.arsdigita.web.ApplicationTypeCollection;
import java.util.HashMap;
import java.util.Map;

/**
 * A tab for managing Application and application instances.
 *
 * @author pb
 * @author Jens Pelzetter
 */
public class ApplicationsAdministrationTab extends LayoutPanel implements AdminConstants {

    private final Tree applicationTree;
    private final Map<String, BaseApplicationPane> appPanes
                                                   = new HashMap<String, BaseApplicationPane>();
    private final Map<String, ApplicationInstancePane> instancePanes
                                                       = new HashMap<String, ApplicationInstancePane>();
    private final BoxPanel appPanel;

    /**
     * Constructor
     */
    public ApplicationsAdministrationTab() {

        super();

        setClassAttr("sidebarNavPanel");
        setAttribute("navbar-title", "Sitemap");

        applicationTree = new Tree(new ApplicationTreeModelBuilder());
        applicationTree.addChangeListener(new TreeStateChangeListener());

        setClassAttr("navbar");

        setLeft(applicationTree);

        final ApplicationTypeCollection applicationTypes = ApplicationType.
            retrieveAllApplicationTypes();

        final Map<String, ApplicationManager<?>> appManagers = ApplicationManagers.getInstance().
            getApplicationManagers();

        while (applicationTypes.next()) {
            if (applicationTypes.getApplicationType().isSingleton()) {
                createSingletonAppPane(applicationTypes.getApplicationType(), appManagers);
            } else {
                createAppPane(applicationTypes.getApplicationType(), appManagers);
            }
        }

        appPanel = new BoxPanel();
        appPanel.setClassAttr("main");
        for (Map.Entry<String, BaseApplicationPane> entry : appPanes.entrySet()) {
            appPanel.add(entry.getValue());
        }

        for (Map.Entry<String, ApplicationInstancePane> entry : instancePanes.entrySet()) {
            appPanel.add(entry.getValue());
        }

        //setRight(appPanel);
        setBody(appPanel);
    }

    private void createSingletonAppPane(final ApplicationType applicationType,
                                        final Map<String, ApplicationManager<?>> appManagers) {
        final String appObjectType = applicationType.getApplicationObjectType();

        final ApplicationManager<?> manager = appManagers.get(appObjectType);
        final SingletonApplicationPane pane;
        if (manager == null) {
            pane = new SingletonApplicationPane(applicationType, null);
        } else {
            pane = new SingletonApplicationPane(
                applicationType, appManagers.get(appObjectType).getApplicationAdminForm());
        }
        appPanes.put(appObjectType, pane);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void createAppPane(final ApplicationType applicationType,
                               final Map<String, ApplicationManager<?>> appManagers) {
        final ApplicationManager<?> appManager = appManagers.get(applicationType.
            getApplicationObjectType());
        final Form createForm;
        if (appManager == null) {
            createForm = null;
        } else {
            createForm = appManager.getApplicationCreateForm();
        }

        final MultiInstanceApplicationPane<?> appPane = new MultiInstanceApplicationPane(
            applicationType, createForm);
        appPanes.put(applicationType.getApplicationObjectType(), appPane);
        createInstancePane(applicationType, appManagers);
    }

    private void createInstancePane(final ApplicationType applicationType,
                                    final Map<String, ApplicationManager<?>> managementForms) {
        final ApplicationManager<?> manager = managementForms.get(applicationType.
            getApplicationObjectType());
        final ApplicationInstancePane instPane;
        if (manager == null) {
            instPane = new ApplicationInstancePane(new Placeholder());
        } else {
            instPane = new ApplicationInstancePane(managementForms.get(applicationType.
                getApplicationObjectType()).
                getApplicationAdminForm());
        }

        instancePanes.put(applicationType.getApplicationObjectType(), instPane);
    }

    @Override
    public void register(final Page page) {
        super.register(page);

        for (Map.Entry<String, BaseApplicationPane> entry : appPanes.entrySet()) {
            page.setVisibleDefault(entry.getValue(), false);
        }
        for (Map.Entry<String, ApplicationInstancePane> entry : instancePanes.entrySet()) {
            page.setVisibleDefault(entry.getValue(), false);
        }
    }

    private void setPaneVisible(final SimpleContainer pane, final PageState state) {
        for (Map.Entry<String, BaseApplicationPane> entry : appPanes.entrySet()) {
            entry.getValue().setVisible(state, false);
        }
        for (Map.Entry<String, ApplicationInstancePane> entry : instancePanes.entrySet()) {
            entry.getValue().setVisible(state, false);
        }

        pane.setVisible(state, true);
    }

    private class TreeStateChangeListener implements ChangeListener {

        public TreeStateChangeListener() {
            //Nothing
        }

        @Override
        public void stateChanged(final ChangeEvent event) {
            final PageState state = event.getPageState();

            final String selectedKey = (String) applicationTree.getSelectedKey(state);
            if (selectedKey != null) {
                if (selectedKey.contains(".")) {
                    // Selected key is a classname and therefore the key of an ApplicationPane
                    final BaseApplicationPane pane = appPanes.get(selectedKey);
                    if (pane != null) {
                        setPaneVisible(pane, state);
                    }
                } else {
                    // Selected key is the name of a instance pane
                    final ApplicationCollection applications = Application.retrieveAllApplications();
                    applications.addEqualsFilter(Application.PRIMARY_URL, selectedKey + "/");
                    final ApplicationInstancePane pane;
                    if (applications.next()) {
                        final Application application = applications.getApplication();
                        pane = instancePanes.get(application.getClass().getName());
                        if (pane != null) {
                            pane.setApplication(application);
                        }
                    } else {
                        pane = null;
                    }

                    if (pane != null) {
                        setPaneVisible(pane, state);
                    }
                }
            }
        }

    }

    private class Placeholder extends ApplicationInstanceAwareContainer {

        public Placeholder() {
            super();
            final Label label = new Label(GlobalizationUtil.globalize(
                "ui.admin.applications.placeholder"));
            add(label);
        }

    }
}
