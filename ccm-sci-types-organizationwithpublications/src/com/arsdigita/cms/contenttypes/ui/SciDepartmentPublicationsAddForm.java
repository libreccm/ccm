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
import com.arsdigita.cms.contenttypes.SciDepartmentPublicationsCollection;
import com.arsdigita.cms.contenttypes.SciDepartmentWithPublications;
import com.arsdigita.cms.ui.ItemSearchWidget;
import com.arsdigita.cms.ui.authoring.BasicItemForm;

/**
 *
 * @author Jens Pelzetter 
 */
public class SciDepartmentPublicationsAddForm
        extends BasicItemForm
        implements FormProcessListener,
                   FormInitListener {

    private ItemSearchWidget itemSearch;
    private final String ITEM_SEARCH = "publications";

    public SciDepartmentPublicationsAddForm(final ItemSelectionModel itemModel) {
        super("SciDepartmentPublicationsAddForm", itemModel);
    }

    @Override
    public void addWidgets() {
        add(new Label((String) SciOrganizationWithPublicationsGlobalizationUtil.
                globalize("sciorganization.ui.selectPublication").localize()));
        itemSearch = new ItemSearchWidget(ITEM_SEARCH, ContentType.
                findByAssociatedObjectType(Publication.class.getName()));
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
        SciDepartmentWithPublications department =
                                      (SciDepartmentWithPublications) getItemSelectionModel().
                getSelectedObject(state);

        if (this.getSaveCancelSection().getSaveButton().isSelected(state)) {
            Publication publication = (Publication) data.get(ITEM_SEARCH);
            publication = (Publication) publication.getContentBundle().
                    getInstance(department.getLanguage());

            department.addPublication(publication);
        }

        init(fse);
    }

    @Override
    public void validate(final FormSectionEvent fse) throws FormProcessException {
        final PageState state = fse.getPageState();
        final FormData data = fse.getFormData();

        if (data.get(ITEM_SEARCH) == null) {
            data.addError(
                    SciOrganizationWithPublicationsGlobalizationUtil.globalize(
                    "sciorganization.ui.selectPublication.no_publication_selected"));
            return;
        }

        SciDepartmentWithPublications department =
                                      (SciDepartmentWithPublications) getItemSelectionModel().
                getSelectedObject(state);
        Publication publication = (Publication) data.get(ITEM_SEARCH);
        if (!(publication.getContentBundle().hasInstance(
              department.getLanguage()))) {
            data.addError(
                    SciOrganizationWithPublicationsGlobalizationUtil.globalize(
                    "sciorganization.ui.selectPublication.no_suitable_language_variant"));
            return;
        }

        publication = (Publication) publication.getContentBundle().getInstance(
                department.getLanguage());
        SciDepartmentPublicationsCollection publications = department.
                getPublications();
        publications.addFilter(String.format("id = %s", publication.getID().
                toString()));
        if (publications.size() > 0) {
            data.addError(SciOrganizationWithPublicationsGlobalizationUtil.
                    globalize(
                    "sciorganization.ui.selectPublication.already_added"));
        }

        publications.close();
    }
}
