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
import com.arsdigita.cms.contenttypes.SciProject;
import com.arsdigita.cms.contenttypes.SciProjectSubProjectsCollection;
import com.arsdigita.cms.ui.ItemSearchWidget;
import com.arsdigita.cms.ui.authoring.BasicItemForm;

/**
 * Form for adding a subproject to a {@link SciProject}.
 *
 * @author Jens Pelzetter
 */
public class SciProjectSubprojectAddForm
        extends BasicItemForm
        implements FormProcessListener,
                   FormInitListener {

    private ItemSearchWidget m_itemSearch;
    private final String ITEM_SEARCH = "projects";

    public SciProjectSubprojectAddForm(ItemSelectionModel itemModel) {
        super("SubprojectAddForm", itemModel);
    }

    @Override
    protected void addWidgets() {
        add(new Label((String) SciOrganizationGlobalizationUtil.globalize(
                "sciorganization.ui.project.select_subproject").localize()));
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
        SciProject project = (SciProject) getItemSelectionModel().
                getSelectedObject(state);

        if (!(this.getSaveCancelSection().getCancelButton().
              isSelected(state))) {
            SciProject subProject = (SciProject) data.get(ITEM_SEARCH);
            subProject = (SciProject) subProject.getContentBundle().
                    getInstance(project.getLanguage());

            project.addSubProject(subProject);
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
                    "sciorganization.ui.project.select_subproject.no_project_selected"));
            return;
        }

        SciProject project = (SciProject) getItemSelectionModel().
                getSelectedObject(state);
        SciProject subProject = (SciProject) data.get(ITEM_SEARCH);
        if (!(subProject.getContentBundle().hasInstance(project.getLanguage()))) {
            data.addError(
                    SciOrganizationGlobalizationUtil.globalize(
                    "sciorganization.ui.project.select_subproject.no_suitable_language_variant"));
            return;
        }

        subProject = (SciProject) subProject.getContentBundle().getInstance(project.
                getLanguage());
        if (subProject.getID().equals(project.getID())) {
            data.addError(SciOrganizationGlobalizationUtil.globalize(
                    "sciorganization.ui.project.select_subproject.same_project"));
            return;
        }

        SciProjectSubProjectsCollection subProjects = project.getSubProjects();
        subProjects.addFilter(String.format("id = %s", subProject.getID().
                toString()));
        if(subProjects.size() > 0) {
             data.addError(SciOrganizationGlobalizationUtil.globalize(
                    "sciorganization.ui.project.select_subproject.already_added"));
        }
        
        subProjects.close();
    }
}
