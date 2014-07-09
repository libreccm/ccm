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
import com.arsdigita.cms.contenttypes.PublicationsConfig;
import com.arsdigita.cms.contenttypes.UnPublished;
import com.arsdigita.cms.ui.ItemSearchWidget;
import com.arsdigita.cms.ui.authoring.BasicItemForm;
import com.arsdigita.kernel.Kernel;


/**
 *
 * @author Jens Pelzetter
 * @version $Id$
 */
public class UnPublishedOrganizationForm
        extends BasicItemForm
        implements FormProcessListener,
                   FormInitListener {

    private ItemSearchWidget itemSearch;
    private final String ITEM_SEARCH = "unPublishedOrga";
    private final static PublicationsConfig config = new PublicationsConfig();

    static {
        config.load();
    }

    public UnPublishedOrganizationForm(final ItemSelectionModel itemModel) {
        super("UnPublishedOrganizationForm", itemModel);
    }

    @Override
    public void addWidgets() {

        itemSearch = new ItemSearchWidget(ITEM_SEARCH,
                                          ContentType.findByAssociatedObjectType(
                GenericOrganizationalUnit.class.getName()));
        itemSearch.setDefaultCreationFolder(config.getDefaultOrganizationsFolder());
        itemSearch.setEditAfterCreate(false);
        itemSearch.setLabel(PublicationGlobalizationUtil.globalize(
                            "publications.ui.unpublished.organization"));
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
        UnPublished unPublished = (UnPublished) getItemSelectionModel().
                getSelectedObject(state);

        if (this.getSaveCancelSection().getSaveButton().isSelected(state)) {
            GenericOrganizationalUnit orga = (GenericOrganizationalUnit) data.get(ITEM_SEARCH);
            orga = (GenericOrganizationalUnit) orga.getContentBundle().
                    getInstance(unPublished.getLanguage());

            unPublished.setOrganization(orga);
            itemSearch.publishCreatedItem(data, orga);

        }

        init(fse);
    }

    @Override
    public void validate(FormSectionEvent fse) throws FormProcessException {
        final PageState state = fse.getPageState();
        final FormData data = fse.getFormData();

        if (data.get(ITEM_SEARCH) == null) {
            data.addError(PublicationGlobalizationUtil.globalize(
                    "publications.ui.unpublished.organization.no_orga_selected"));
            return;
        }

        UnPublished unPublished = (UnPublished) getItemSelectionModel().
                getSelectedObject(state);
        GenericOrganizationalUnit orga = (GenericOrganizationalUnit) data.get(
                ITEM_SEARCH);
        if (!(orga.getContentBundle().hasInstance(unPublished.getLanguage(),
                                                  Kernel.getConfig().
              languageIndependentItems()))) {
            data.addError(
                    PublicationGlobalizationUtil.globalize(
                    "publications.ui.unpublished.organization.no_suitable_language_variant"));
            return;
        }
    }

}
