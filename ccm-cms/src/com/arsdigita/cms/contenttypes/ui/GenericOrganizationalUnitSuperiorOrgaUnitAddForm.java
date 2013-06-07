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
import com.arsdigita.cms.contenttypes.GenericOrganizationalUnitSuperiorCollection;
import com.arsdigita.cms.ui.ItemSearchWidget;
import com.arsdigita.cms.ui.authoring.BasicItemForm;
import com.arsdigita.kernel.Kernel;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class GenericOrganizationalUnitSuperiorOrgaUnitAddForm
        extends BasicItemForm
        implements FormProcessListener,
                   FormInitListener {

    private ItemSearchWidget itemSearch;
    private final String ITEM_SEARCH = "superiorOrgaUnits";
    private final GenericOrgaUnitSuperiorOrgaUnitAddFormCustomizer customizer;

    public GenericOrganizationalUnitSuperiorOrgaUnitAddForm(
            final ItemSelectionModel itemModel,
            final GenericOrgaUnitSuperiorOrgaUnitAddFormCustomizer customizer) {
        super("SuperiorOrgaUnitsAddForm", itemModel);
        this.customizer = customizer;
        add(new Label(customizer.getSelectSuperiorOrgaUnitLabel()));
        itemSearch = new ItemSearchWidget(
                ITEM_SEARCH,
                ContentType.findByAssociatedObjectType(
                customizer.getSuperiorOrgaUnitType()));
        itemSearch.setDisableCreatePane(true);
        add(itemSearch);
    }

    @Override
    protected void addWidgets() {
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

        if (getSaveCancelSection().getSaveButton().isSelected(state)) {
            GenericOrganizationalUnit supOrgaUnit =
                                      (GenericOrganizationalUnit) data.get(
                    ITEM_SEARCH);
            supOrgaUnit = (GenericOrganizationalUnit) supOrgaUnit.
                    getContentBundle().getInstance(orgaunit.getLanguage(), true);

            orgaunit.addSuperiorOrgaUnit(supOrgaUnit, customizer.getAssocType());
        }

        init(fse);
    }

    @Override
    public void validate(final FormSectionEvent fse) throws FormProcessException {
        final PageState state = fse.getPageState();
        final FormData data = fse.getFormData();

        if (data.get(ITEM_SEARCH) == null) {
            data.addError(customizer.getNothingSelectedMessage());
            return;
        }

        final GenericOrganizationalUnit orgaunit =
                                        (GenericOrganizationalUnit) getItemSelectionModel().
                getSelectedObject(state);
        GenericOrganizationalUnit supOrgaUnit =
                                  (GenericOrganizationalUnit) data.get(
                ITEM_SEARCH);
        if (!(supOrgaUnit.getContentBundle().hasInstance(orgaunit.getLanguage(),
                                                         Kernel.getConfig().
              languageIndependentItems()))) {
            data.addError(customizer.getNoSuitableLanguageVariantMessage());
            return;
        }

        supOrgaUnit = (GenericOrganizationalUnit) supOrgaUnit.getContentBundle().
                getInstance(orgaunit.getLanguage(), true);

        if (orgaunit.getID().equals(supOrgaUnit.getID())) {
            data.addError(customizer.getAddingToItselfMessage());
            return;
        }

        final GenericOrganizationalUnitSuperiorCollection supOrgaUnits =
                                                          orgaunit.
                getSuperiorOrgaUnits();
        supOrgaUnits.addFilter(String.format("id = %s", supOrgaUnit.getID().
                toString()));
        if (supOrgaUnits.size() > 0) {
            data.addError(customizer.getAlreadyAddedMessage());
        }
        supOrgaUnits.close();
    }
}
