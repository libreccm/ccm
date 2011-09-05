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
import com.arsdigita.bebop.event.FormSubmissionListener;
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
import com.arsdigita.globalization.GlobalizationHelper;
import org.apache.log4j.Logger;

/**
 * Form for adding related persons the an organization.
 *
 * @author Jens Pelzetter
 */
public class GenericOrganizationalUnitPersonAddForm
        extends BasicItemForm
        implements FormProcessListener,
                   FormInitListener,
                   FormSubmissionListener {

    private static final Logger logger = Logger.getLogger(
            GenericOrganizationalUnitPersonAddForm.class);
    private GenericOrganizationalUnitPersonPropertiesStep m_step;
    private ItemSearchWidget m_itemSearch;
    private final String ITEM_SEARCH = "orgaunitPerson";
    private GenericOrganizationalUnitPersonSelector selector;
    private Label selectedPersonNameLabel;

    public GenericOrganizationalUnitPersonAddForm(ItemSelectionModel itemModel,
                                                  GenericOrganizationalUnitPersonSelector selector) {
        super("PersonAddForm", itemModel);
        this.selector = selector;
        addSubmissionListener(this);
    }

    @Override
    protected void addWidgets() {
        add(new Label((String) ContenttypesGlobalizationUtil.globalize(
                "cms.contenttypes.ui.genericorgaunit.select_person").localize()));
        m_itemSearch = new ItemSearchWidget(ITEM_SEARCH, ContentType.
                findByAssociatedObjectType(getPersonType()));
        /*m_itemSearch.getItemField().addValidationListener(
        new NotNullValidationListener());*/
        add(this.m_itemSearch);

        selectedPersonNameLabel = new Label("");
        add(selectedPersonNameLabel);

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
        roles.addLanguageFilter(GlobalizationHelper.getNegotiatedLocale().
                getLanguage());
        while (roles.next()) {
            RelationAttribute role;
            role = roles.getRelationAttribute();
            roleSelect.addOption(new Option(role.getKey(), role.getName()));
        }
        add(roleSelect);

        add(new Label(ContenttypesGlobalizationUtil.globalize(
                "cms.contenttypes.ui.genericorgaunit.person.status")));
        ParameterModel statusModel =
                       new StringParameter(
                GenericOrganizationalUnitPersonCollection.STATUS);
        SingleSelect statusSelect = new SingleSelect(statusModel);
        statusSelect.addValidationListener(new NotNullValidationListener());
        statusSelect.addOption(new Option("",
                                          new Label((String) ContenttypesGlobalizationUtil.
                globalize("cms.ui.select_one").localize())));
        RelationAttributeCollection statusColl =
                                    new RelationAttributeCollection(
                getStatusAttributeName());
        statusColl.addLanguageFilter(GlobalizationHelper.getNegotiatedLocale().
                getLanguage());
        while (statusColl.next()) {
            RelationAttribute status;
            status = statusColl.getRelationAttribute();
            statusSelect.addOption(new Option(status.getKey(), status.getName()));
        }
        add(statusSelect);

    }

    @Override
    public void init(FormSectionEvent fse) throws FormProcessException {
        FormData data = fse.getFormData();
        PageState state = fse.getPageState();

        GenericPerson person;
        String role;
        String status;

        person = selector.getSelectedPerson();
        role = selector.getSelectedPersonRole();
        status = selector.getSelectedPersonStatus();

        if (person == null) {
            m_itemSearch.setVisible(state, true);
            selectedPersonNameLabel.setVisible(state, false);
        } else {
            data.put(ITEM_SEARCH, person);
            data.put(GenericOrganizationalUnitPersonCollection.PERSON_ROLE,
                     role);
            data.put(GenericOrganizationalUnitPersonCollection.STATUS,
                     status);

            m_itemSearch.setVisible(state, false);
            selectedPersonNameLabel.setVisible(state, true);
            selectedPersonNameLabel.setLabel(person.getFullName(), state);
        }

        setVisible(state, true);
    }

    @Override
    public void process(FormSectionEvent fse) throws FormProcessException {
        FormData data = fse.getFormData();
        PageState state = fse.getPageState();
        GenericOrganizationalUnit orga =
                                  (GenericOrganizationalUnit) getItemSelectionModel().
                getSelectedObject(state);

        if (this.getSaveCancelSection().getSaveButton().isSelected(state)) {

            GenericPerson person;
            person = selector.getSelectedPerson();

            if (person == null) {
                GenericPerson personToAdd =
                              (GenericPerson) data.get(ITEM_SEARCH);
                personToAdd.getContentBundle().getInstance(orga.getLanguage());

                logger.debug(String.format("Adding person %s",
                                           personToAdd.getFullName()));

                orga.addPerson(personToAdd,
                               (String) data.get(
                        GenericOrganizationalUnitPersonCollection.PERSON_ROLE),
                               (String) data.get(
                        GenericOrganizationalUnitPersonCollection.STATUS));
            } else {
                GenericOrganizationalUnitPersonCollection persons;

                persons = orga.getPersons();

                while (persons.next()) {
                    if (persons.getPerson().equals(person)) {
                        break;
                    }
                }

                persons.setRoleName((String) data.get(
                        GenericOrganizationalUnitPersonCollection.PERSON_ROLE));
                persons.setStatus((String) data.get(
                        GenericOrganizationalUnitPersonCollection.STATUS));

                selector.setSelectedPerson(null);
                selector.setSelectedPersonRole(null);
                selector.setSelectedPersonStatus(null);

                persons.close();
            }
        }

        init(fse);
    }

    public void submitted(FormSectionEvent fse) throws FormProcessException {
        if (this.getSaveCancelSection().getCancelButton().isSelected(
                fse.getPageState())) {
            selector.setSelectedPerson(null);
            selector.setSelectedPersonRole(null);
            selector.setSelectedPersonStatus(null);

            init(fse);
        }
    }

    @Override
    public void validate(FormSectionEvent fse) throws FormProcessException {
        final PageState state = fse.getPageState();
        final FormData data = fse.getFormData();

        if ((selector.getSelectedPerson() == null)
            && (data.get(ITEM_SEARCH) == null)) {
            data.addError(
                    ContenttypesGlobalizationUtil.globalize(
                    "cms.contenttypes.ui.genericorgaunit.person.no_person_selected"));
            return;
        }

        if (selector.getSelectedPerson() == null) {
            GenericOrganizationalUnit orga =
                                      (GenericOrganizationalUnit) getItemSelectionModel().
                    getSelectedObject(state);

            GenericPerson person = (GenericPerson) data.get(ITEM_SEARCH);

            if (!(person.getContentBundle().hasInstance(orga.getLanguage()))) {
                data.addError(
                        ContenttypesGlobalizationUtil.globalize(
                        "cms.contenttypes.ui.genericorgaunit.person.no_suitable_language_variant"));
                
                return;
            }

            person = (GenericPerson) person.getContentBundle().getInstance(orga.
                    getLanguage());
            GenericOrganizationalUnitPersonCollection persons =
                                                      orga.getPersons();

            persons.addFilter(String.format("id = %s",
                                            person.getID().toString()));
            if (persons.size() > 0) {
                data.addError(
                        ContenttypesGlobalizationUtil.globalize(
                        "cms.contenttypes.ui.genericorgaunit.person.already_added"));
            }
            
            persons.close();
        }
    }

    protected String getPersonType() {
        return GenericPerson.class.getName();
    }

    protected String getRoleAttributeName() {
        return "GenericOrganizationalUnitRole";
    }

    protected String getStatusAttributeName() {
        return "GenericOrganizationalUnitMemberStatus";
    }
}
