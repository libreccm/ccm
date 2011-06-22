/*
 * Copyright (c) 2011 Jens Pelzetter,
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
import com.arsdigita.cms.contenttypes.Publication;
import com.arsdigita.cms.contenttypes.PublicationSciDepartmentCollection;
import com.arsdigita.cms.contenttypes.SciDepartmentPublicationsCollection;
import com.arsdigita.cms.contenttypes.SciDepartmentWithPublications;
import com.arsdigita.cms.ui.ItemSearchWidget;
import com.arsdigita.cms.ui.authoring.BasicItemForm;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;

/**
 *
 * @author Jens Pelzetter 
 */
public class PublicationSciDepartmentAddForm
        extends BasicItemForm
        implements FormProcessListener,
                   FormInitListener {

    private ItemSearchWidget itemSearch;
    private final String ITEM_SEARCH = "departments";

    public PublicationSciDepartmentAddForm(ItemSelectionModel itemModel) {
        super("PublicationSciDepartmentAddForm", itemModel);
    }

    @Override
    public void addWidgets() {
        add(new Label((String) SciOrganizationWithPublicationsGlobalizationUtil.
                globalize("sciorganization.ui.selectDepartment").localize()));
        itemSearch = new ItemSearchWidget(ITEM_SEARCH,
                                          ContentType.findByAssociatedObjectType(SciDepartmentWithPublications.class.
                getName()));
        add(itemSearch);
    }

    @Override
    public void init(final FormSectionEvent fse) throws FormProcessException {
        PageState state = fse.getPageState();

        setVisible(state, true);
    }

    @Override
    public void process(final FormSectionEvent fse) throws FormProcessException {
        FormData data = fse.getFormData();
        PageState state = fse.getPageState();

        Publication publication = (Publication) getItemSelectionModel().
                getSelectedObject(state);

        if (this.getSaveCancelSection().getSaveButton().isSelected(state)) {
            SciDepartmentWithPublications department =
                                          (SciDepartmentWithPublications) data.
                    get(ITEM_SEARCH);
            department = (SciDepartmentWithPublications) department.
                    getContentBundle().getInstance(publication.getLanguage());
            DataObject link = publication.add("departments", department);
            link.set("publicationOrder", Integer.valueOf((int) department.
                    getPublications().size()));

            link.save();
        }

        init(fse);
    }

    @Override
    public void validate(FormSectionEvent fse) throws FormProcessException {
        final PageState state = fse.getPageState();
        final FormData data = fse.getFormData();

        if (data.get(ITEM_SEARCH) == null) {
            data.addError(
                    SciOrganizationWithPublicationsGlobalizationUtil.globalize(
                    "sciorganization.ui.selectDepartment.no_department_selected"));
            return;
        }

        Publication publication = (Publication) getItemSelectionModel().
                getSelectedObject(state);
        SciDepartmentWithPublications department =
                                      (SciDepartmentWithPublications) data.get(
                ITEM_SEARCH);
        if (!(department.getContentBundle().hasInstance(publication.getLanguage()))) {
            data.addError(
                    SciOrganizationWithPublicationsGlobalizationUtil.globalize(
                    "sciorganization.ui.selectDepartment.no_suitable_language_variant"));
            return;
        }
        
        department = (SciDepartmentWithPublications) department.getContentBundle().getInstance(publication.getLanguage());
        PublicationSciDepartmentCollection departments = new PublicationSciDepartmentCollection((DataCollection) publication.
                get("departments"));        
        departments.addFilter(String.format("id = %s", department.getID().toString()));
        if (departments.size() > 0) {
            data.addError(
                    SciOrganizationWithPublicationsGlobalizationUtil.globalize(
                    "sciorganization.ui.selectDepartment.already_added"));
        }
        
        departments.close();
    }
}
