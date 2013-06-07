/*
 * Copyright (c) 2010 Jens Pelzetter
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
import com.arsdigita.cms.contenttypes.GenericOrganizationalUnit;
import com.arsdigita.cms.contenttypes.GenericOrganizationalUnitPublicationsCollection;
import com.arsdigita.cms.contenttypes.Publication;
import com.arsdigita.cms.ui.ItemSearchWidget;
import com.arsdigita.cms.ui.authoring.BasicItemForm;
import com.arsdigita.kernel.Kernel;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class GenericOrganizationalUnitPublicationAddForm
        extends BasicItemForm
        implements FormInitListener,
                   FormProcessListener {

    private ItemSearchWidget itemSearch;
    private final static String ITEM_SEARCH = "publications";

    public GenericOrganizationalUnitPublicationAddForm(
            final ItemSelectionModel itemModel) {
        super("GenericOrganizationalUnitPublicationAddForm", itemModel);
    }

    @Override
    public void addWidgets() {
        add(new Label((String) PublicationGlobalizationUtil.globalize(
                "genericorganizationalunit.ui.publication.select").localize()));
        itemSearch = new ItemSearchWidget(ITEM_SEARCH,
                                          ContentType.findByAssociatedObjectType(Publication.class.
                getName()));
        itemSearch.setDisableCreatePane(true);
        add(itemSearch);
    }

    @Override
    public void init(final FormSectionEvent fse) throws FormProcessException {
        final PageState state = fse.getPageState();

        setVisible(state, true);
    }

    @Override
    public void process(final FormSectionEvent fse) throws FormProcessException {
        final FormData data = fse.getFormData();
        final PageState state = fse.getPageState();

        final GenericOrganizationalUnit orgaunit =
                                        (GenericOrganizationalUnit) getItemSelectionModel().
                getSelectedObject(state);

        if ((orgaunit != null)
            && getSaveCancelSection().getSaveButton().isSelected(state)) {
            Publication publication = (Publication) data.get(ITEM_SEARCH);
            publication = (Publication) publication.getContentBundle().
                    getInstance(orgaunit.getLanguage(), true);
            Publication.addPublication(orgaunit, publication);

            orgaunit.save();
        }

        init(fse);
    }

    @Override
    public void validate(final FormSectionEvent fse) throws FormProcessException {
        final PageState state = fse.getPageState();
        final FormData data = fse.getFormData();

        if (data.get(ITEM_SEARCH) == null) {
            data.addError(PublicationGlobalizationUtil.globalize(
                    "genericorganizationalunit.ui.publication.select.nothing"));
        }

        final GenericOrganizationalUnit orgaunit =
                                        (GenericOrganizationalUnit) getItemSelectionModel().
                getSelectedObject(state);
        Publication publication = (Publication) data.get(ITEM_SEARCH);
        if (!(publication.getContentBundle().hasInstance(orgaunit.getLanguage(),
                                                         Kernel.getConfig().
              languageIndependentItems()))) {
            data.addError(
                    PublicationGlobalizationUtil.globalize(
                    "genericorganizationalunit.ui.publication.no_suitable_language_variant"));
            return;
        }

        publication = (Publication) publication.getContentBundle().getInstance(orgaunit.
                getLanguage(), true);
        final GenericOrganizationalUnitPublicationsCollection publications =
                                                              Publication.
                getPublications(orgaunit);
        publications.addFilter(String.format("id = %s",
                                             publication.getID().toString()));
        if (publications.size() > 0) {
            data.addError(PublicationGlobalizationUtil.globalize(
                    "genericorganizationalunit.ui.publication.already_added"));
        }

        publications.close();

    }
}
