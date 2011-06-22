/*
 * Copyright (c) 2010 Jens Pelzetter,
 * for the Center of Social Politics of the University of Bremen
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
package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.cms.ContentType;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.SciOrganization;
import com.arsdigita.cms.contenttypes.SciOrganizationProjectsCollection;
import com.arsdigita.cms.contenttypes.SciProject;
import com.arsdigita.cms.ui.ItemSearchWidget;
import com.arsdigita.cms.ui.authoring.BasicItemForm;

/**
 * Form for linking projects with a {@link SciOrganization}.
 *
 * @author Jens Pelzetter
 * @see SciOrganizationProjectAddForm
 * @see SciOrganization
 * @see SciProject
 */
public class SciOrganizationProjectAddForm
        extends BasicItemForm
        implements FormProcessListener,
                   FormInitListener {

    private ItemSearchWidget m_itemSearch;
    private final String ITEM_SEARCH = "projects";

    public SciOrganizationProjectAddForm(ItemSelectionModel itemModel) {
        super("ProjectsAddForm", itemModel);
    }

    @Override
    protected void addWidgets() {
        add(new Label((String) SciOrganizationGlobalizationUtil.globalize(
                "sciorganization.ui.organization.select_project").localize()));
        m_itemSearch = new ItemSearchWidget(
                ITEM_SEARCH,
                ContentType.findByAssociatedObjectType(
                SciProject.class.getName()));
        add(m_itemSearch);
    }

    @Override
    public void init(FormSectionEvent fse) throws FormProcessException {
        PageState state = fse.getPageState();

        setVisible(state, true);
    }

    @Override
    public void process(FormSectionEvent fse) throws FormProcessException {
        FormData data = fse.getFormData();
        PageState state = fse.getPageState();
        SciOrganization orga = (SciOrganization) getItemSelectionModel().
                getSelectedObject(state);

        if (!(this.getSaveCancelSection().getCancelButton().
              isSelected(state))) {
            SciProject project = (SciProject) data.get(ITEM_SEARCH);
            project = (SciProject) project.getContentBundle().getInstance(orga.
                    getLanguage());

            orga.addProject(project);
        }

        init(fse);
    }

    @Override
    public void validate(FormSectionEvent fse) throws FormProcessException {
        final PageState state = fse.getPageState();
        final FormData data = fse.getFormData();

        if (data.get(ITEM_SEARCH) == null) {
            data.addError(
                    SciOrganizationGlobalizationUtil.globalize(
                    "sciorganization.ui.organization.select_project.no_project_selected"));
            return;
        }

        SciOrganization orga = (SciOrganization) getItemSelectionModel().
                getSelectedObject(state);
        SciProject project = (SciProject) data.get(ITEM_SEARCH);
        if (!(project.getContentBundle().hasInstance(orga.getLanguage()))) {
            data.addError(
                    SciOrganizationGlobalizationUtil.globalize(
                    "sciorganization.ui.organization.select_project.no_suitable_language_variant"));
            return;
        }

        project = (SciProject) project.getContentBundle().getInstance(orga.
                getLanguage());
        SciOrganizationProjectsCollection projects = orga.getProjects();
        projects.addFilter(String.format("id = %s", project.getID().toString()));
        if (projects.size() > 0) {
            data.addError(
                    SciOrganizationGlobalizationUtil.globalize(
                    "sciorganization.ui.organization.select_project.already_added"));
        }

        projects.close();
    }
}
