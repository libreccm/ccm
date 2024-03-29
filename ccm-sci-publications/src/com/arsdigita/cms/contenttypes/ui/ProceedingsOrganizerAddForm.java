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
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.cms.ContentType;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.GenericOrganizationalUnit;
import com.arsdigita.cms.contenttypes.Proceedings;
import com.arsdigita.cms.contenttypes.ProceedingsOrganizerCollection;
import com.arsdigita.cms.contenttypes.PublicationsConfig;
import com.arsdigita.cms.ui.ItemSearchWidget;
import com.arsdigita.cms.ui.authoring.BasicItemForm;
import com.arsdigita.kernel.Kernel;

/**
 *
 * @author Jens Pelzetter
 * @version $Id$
 */
public class ProceedingsOrganizerAddForm
    extends BasicItemForm
    implements FormProcessListener,
               FormInitListener {

    private ItemSearchWidget itemSearch;
    private final String ITEM_SEARCH = "departmentOrga";
    private ItemSelectionModel itemModel;
    private final static PublicationsConfig config = new PublicationsConfig();

    static {
        config.load();
    }

    public ProceedingsOrganizerAddForm(final ItemSelectionModel itemModel) {
        super("ProceedingsOrganizerForm", itemModel);
        this.itemModel = itemModel;
    }

    @Override
    protected void addWidgets() {

        itemSearch = new ItemSearchWidget(ITEM_SEARCH,
                                          ContentType
                                          .findByAssociatedObjectType(
                                              GenericOrganizationalUnit.class
                                              .getName()));
        itemSearch.setDefaultCreationFolder(config
            .getDefaultOrganizationsFolder());
        itemSearch.setEditAfterCreate(false);
        itemSearch.setLabel(PublicationGlobalizationUtil.globalize(
            "publications.ui.proceedings.organizer"));
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
        Proceedings proceedings = (Proceedings) getItemSelectionModel().
            getSelectedObject(state);

        if (this.getSaveCancelSection().getSaveButton().isSelected(state)) {
            GenericOrganizationalUnit organizer
                                          = (GenericOrganizationalUnit) data
                .get(
                    ITEM_SEARCH);
            organizer = (GenericOrganizationalUnit) organizer.getContentBundle()
                .getInstance(proceedings.getLanguage());

            proceedings.addOrganizer(organizer);
            itemSearch.publishCreatedItem(data, organizer);

        }

        init(fse);
    }

    @Override
    public void validate(final FormSectionEvent fse) throws FormProcessException {
        final PageState state = fse.getPageState();
        final FormData data = fse.getFormData();

        if (data.get(ITEM_SEARCH) == null) {
            data.addError(PublicationGlobalizationUtil.globalize(
                "publications.ui.proceedings.organizer.no_orga_selected"));
            return;
        }

        Proceedings proceedings = (Proceedings) getItemSelectionModel().
            getSelectedObject(state);
        GenericOrganizationalUnit organizer = (GenericOrganizationalUnit) data
            .get(ITEM_SEARCH);
        if (!(organizer.getContentBundle()
              .hasInstance(proceedings.getLanguage(),
                           Kernel.getConfig().
                           languageIndependentItems()))) {
            data.addError(
                PublicationGlobalizationUtil.globalize(
                    "publications.ui.proceedings.organizer.no_suitable_language_variant"));
            return;
        }

        organizer = (GenericOrganizationalUnit) organizer.getContentBundle()
            .getInstance(proceedings.getLanguage());
        ProceedingsOrganizerCollection organizers = proceedings.getOrganizers();
        organizers.addFilter(String.format("id = %s",
                                           organizer
                                           .getContentBundle()
                                           .getID()
                                           .toString()));
        if (organizers.size() > 0) {
            data.addError(PublicationGlobalizationUtil.globalize(
                "publications.ui.proceedings.select_organizer.already_added"));
        }
    }

}
