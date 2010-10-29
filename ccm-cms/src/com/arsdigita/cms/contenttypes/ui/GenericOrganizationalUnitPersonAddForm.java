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
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.SingleSelect;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.ContentType;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.RelationAttribute;
import com.arsdigita.cms.RelationAttributeCollection;
import com.arsdigita.cms.contenttypes.GenericOrganizationalUnit;
import com.arsdigita.cms.contenttypes.GenericOrganizationalUnitPersonCollection;
import com.arsdigita.cms.contenttypes.GenericPerson;
import com.arsdigita.cms.contenttypes.util.ContenttypesGlobalizationUtil;
import com.arsdigita.cms.ui.ItemSearchWidget;
import com.arsdigita.cms.ui.authoring.BasicItemForm;
import com.arsdigita.dispatcher.DispatcherHelper;
import org.apache.log4j.Logger;

/**
 * Form for adding related persons the an organization.
 *
 * @author Jens Pelzetter
 */
public class GenericOrganizationalUnitPersonAddForm extends BasicItemForm {

    private static final Logger logger = Logger.getLogger(
            GenericOrganizationalUnitPersonAddForm.class);
    private GenericOrganizationalUnitPersonPropertiesStep m_step;
    private ItemSearchWidget m_itemSearch;
    private final String ITEM_SEARCH = "orgaunitPerson";

    public GenericOrganizationalUnitPersonAddForm(ItemSelectionModel itemModel) {
        super("PersonAddForm", itemModel);
    }

    @Override
    protected void addWidgets() {
        add(new Label((String) ContenttypesGlobalizationUtil.globalize(
                "cms.contenttypes.ui.genericorgaunit.select_person").localize()));
        m_itemSearch = new ItemSearchWidget(ITEM_SEARCH, ContentType.
                findByAssociatedObjectType(getPersonType()));
        m_itemSearch.getItemField().addValidationListener(
                new NotNullValidationListener());
        add(this.m_itemSearch);

        add(new Label(ContenttypesGlobalizationUtil.globalize(
                "cms.contenttypes.ui.genericorgaunit.person.role")));
        ParameterModel roleParam =
        new StringParameter(
        GenericOrganizationalUnitPersonCollection.PERSON_ROLE);        
        SingleSelect roleSelect = new SingleSelect(roleParam);
        roleSelect.addValidationListener(new NotNullValidationListener());
        roleSelect.addOption(
                new Option("",
                           new Label((String) ContenttypesGlobalizationUtil.
                globalize("cms.ui.select_one").localize())));
        RelationAttributeCollection roles = new RelationAttributeCollection(
                getRoleAttributeName());
        roles.addLanguageFilter(DispatcherHelper.getNegotiatedLocale().
                getLanguage());
        while (roles.next()) {
            RelationAttribute role;
            role = roles.getRelationAttribute();
            roleSelect.addOption(new Option(role.getKey(), role.getName()));
        }

        add(roleSelect);

        /*TextField role = new TextField(roleParam);
        role.addValidationListener(new NotNullValidationListener());
        add(role);*/
    }

    @Override
    public void init(FormSectionEvent fse) {
        PageState state = fse.getPageState();

        setVisible(state, true);
    }

    @Override
    public void process(FormSectionEvent fse) throws FormProcessException {
        FormData data = fse.getFormData();
        PageState state = fse.getPageState();
        GenericOrganizationalUnit orga = (GenericOrganizationalUnit) getItemSelectionModel().
                getSelectedObject(state);

        if (this.getSaveCancelSection().getSaveButton().isSelected(state)) {
            if (data.get(ITEM_SEARCH) == null) {
                logger.warn("Person to add is null!!!");
            } else {
                logger.debug(String.format("Adding person %s",
                                           ((GenericPerson) data.get(ITEM_SEARCH)).
                        getFullName()));
            }
            orga.addPerson((GenericPerson) data.get(ITEM_SEARCH),
            (String) data.get(
            GenericOrganizationalUnitPersonCollection.PERSON_ROLE));            
        }

        init(fse);
    }

    protected String getPersonType() {
        return GenericPerson.class.getName();
    }

    protected String getRoleAttributeName() {
        return "GenericOrganizationRole";
    }
}
