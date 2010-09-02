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
import com.arsdigita.bebop.SaveCancelSection;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.cms.ContentType;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.GenericOrganizationalUnit;
import com.arsdigita.cms.contenttypes.util.ContenttypesGlobalizationUtil;
import com.arsdigita.cms.ui.ItemSearchWidget;
import com.arsdigita.cms.ui.authoring.BasicItemForm;
import org.apache.log4j.Logger;

/**
 * Form for adding a child to an organization. To change the type of childs
 * allowed, overwrite the {@link getChildDataObjectType()} method.
 *
 * @author Jens Pelzetter
 */
public class GenericOrganizationalUnitChildAddForm extends BasicItemForm {

    private static final Logger s_log = Logger.getLogger(
            GenericOrganizationalUnitChildAddForm.class);
    private GenericOrganizationalUnitChildrenPropertiesStep m_step;
    private ItemSearchWidget m_itemSearch;
    private SaveCancelSection m_saveCancelSection;
    private final String ITEM_SEARCH = "orgaunitChild";
    private ItemSelectionModel m_itemModel;

    public GenericOrganizationalUnitChildAddForm(ItemSelectionModel itemModel) {
        this("ChildAddForm", itemModel);
    }

    public GenericOrganizationalUnitChildAddForm(String formName,
                                                 ItemSelectionModel itemModel) {
        super(formName, itemModel);
    }

    @Override
    protected void addWidgets() {
        add(new Label(
                (String) ContenttypesGlobalizationUtil.globalize(
                "cms.contenttypes.ui.genericorgaunit.select_child").localize()));

        this.m_itemSearch = new ItemSearchWidget(ITEM_SEARCH, ContentType.
                findByAssociatedObjectType(
                getChildDataObjectType()));
        add(this.m_itemSearch);

    }

    @Override
    public void init(FormSectionEvent fse) throws FormProcessException {
        FormData data = fse.getFormData();
        PageState state = fse.getPageState();

        setVisible(state, true);
    }

    @Override
    public void process(FormSectionEvent fse) throws FormProcessException {
        FormData data = fse.getFormData();
        PageState state = fse.getPageState();
        GenericOrganizationalUnit parent = (GenericOrganizationalUnit) getItemSelectionModel().
                getSelectedObject(state);

        if (!(this.getSaveCancelSection().getCancelButton().isSelected(state))) {
            parent.addOrgaUnitChildren((GenericOrganizationalUnit) data.get(
                    ITEM_SEARCH));
        }

        init(fse);
    }

    /**
     *
     * @return The BASE_DATA_OBJECT_TYPE of the childs allowed.
     */
    protected String getChildDataObjectType() {
        return GenericOrganizationalUnit.BASE_DATA_OBJECT_TYPE;
    }
}
