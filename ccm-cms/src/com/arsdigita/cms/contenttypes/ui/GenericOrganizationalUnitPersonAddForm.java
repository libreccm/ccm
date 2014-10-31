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
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.SingleSelect;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.ContentBundle;
import com.arsdigita.cms.ContentType;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.RelationAttribute;
import com.arsdigita.cms.RelationAttributeCollection;
import com.arsdigita.cms.RelationAttributeResourceBundleControl;
import com.arsdigita.cms.contenttypes.GenericOrganizationalUnit;
import com.arsdigita.cms.contenttypes.GenericOrganizationalUnitPersonCollection;
import com.arsdigita.cms.contenttypes.GenericPerson;
import com.arsdigita.cms.contenttypes.util.ContenttypesGlobalizationUtil;
import com.arsdigita.cms.ui.ItemSearchWidget;
import com.arsdigita.cms.ui.authoring.BasicItemForm;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.util.UncheckedWrapperException;
import java.util.TooManyListenersException;
import org.apache.log4j.Logger;

/**
 * Form for adding related persons the an organization.
 *
 * @author Jens Pelzetter
 * @version $Id$
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
        // add(new Label(ContenttypesGlobalizationUtil.globalize(
        //     "cms.contenttypes.ui.genericorgaunit.select_person")));
        m_itemSearch = new ItemSearchWidget(ITEM_SEARCH, ContentType.
                                            findByAssociatedObjectType(getPersonType()));
        /*m_itemSearch.getItemField().addValidationListener(
         new NotNullValidationListener());*/
        m_itemSearch.setLabel(ContenttypesGlobalizationUtil.globalize(
            "cms.contenttypes.ui.genericorgaunit.select_person"));
        m_itemSearch.setDisableCreatePane(false);
        add(this.m_itemSearch);

        selectedPersonNameLabel = new Label();
        add(selectedPersonNameLabel);

        ParameterModel roleParam = new StringParameter(
            GenericOrganizationalUnitPersonCollection.PERSON_ROLE);
        SingleSelect roleSelect = new SingleSelect(roleParam);
        roleSelect.setLabel(ContenttypesGlobalizationUtil.globalize(
            "cms.contenttypes.ui.genericorgaunit.person.role"));
        roleSelect.addValidationListener(new NotNullValidationListener());

        try {
            roleSelect.addPrintListener(new PrintListener() {

                @Override
                public void prepare(final PrintEvent event) {
                    final SingleSelect target = (SingleSelect) event.getTarget();
                    target.clearOptions();

                    target.addOption(
                        new Option("",
                                   new Label(ContenttypesGlobalizationUtil
                                       .globalize("cms.ui.select_one"))));

                    final RelationAttributeCollection roles = new RelationAttributeCollection(
                        getRoleAttributeName());
                    roles.addLanguageFilter(Kernel.getConfig().getDefaultLanguage());
                    while (roles.next()) {
                        RelationAttribute role;
                        role = roles.getRelationAttribute();
                        //target.addOption(new Option(role.getKey(), role.getName()));
                        target.addOption(new Option(
                            role.getKey(),
                            new Label(new GlobalizedMessage(
                                    role.getKey(),
                                    getRoleAttributeName(),
                                    new RelationAttributeResourceBundleControl()))));
                    }
                }

            });
        } catch (TooManyListenersException ex) {
            throw new UncheckedWrapperException("Something has gone terribly wrong...", ex);
        }
        add(roleSelect);

        final ParameterModel statusModel = new StringParameter(
            GenericOrganizationalUnitPersonCollection.STATUS);
        final SingleSelect statusSelect = new SingleSelect(statusModel);
        statusSelect.setLabel(ContenttypesGlobalizationUtil.globalize(
            "cms.contenttypes.ui.genericorgaunit.person.status"));
        statusSelect.addValidationListener(new NotNullValidationListener());
        try {
            statusSelect.addPrintListener(new PrintListener() {

                @Override
                public void prepare(final PrintEvent event) {
                    final SingleSelect target = (SingleSelect) event.getTarget();
                    target.clearOptions();

                    target.addOption(new Option("",
                                                new Label(ContenttypesGlobalizationUtil.
                                                    globalize("cms.ui.select_one"))));

                    RelationAttributeCollection statusColl = new RelationAttributeCollection(
                        getStatusAttributeName());
                    statusColl.addLanguageFilter(Kernel.getConfig().getDefaultLanguage());
                    while (statusColl.next()) {
                        RelationAttribute status;
                        status = statusColl.getRelationAttribute();
                        //target.addOption(new Option(status.getKey(), status.getName()));
                        target.addOption(new Option(
                            status.getKey(),
                            new Label(new GlobalizedMessage(
                                    status.getKey(),
                                    getStatusAttributeName(),
                                    new RelationAttributeResourceBundleControl()))));
                    }
                }

            });
        } catch (TooManyListenersException ex) {
            throw new UncheckedWrapperException("Somethin has gone terribly wrong", ex);
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
        GenericOrganizationalUnit orga = (GenericOrganizationalUnit) getItemSelectionModel().
            getSelectedObject(state);

        if (this.getSaveCancelSection().getSaveButton().isSelected(state)) {

            GenericPerson person;
            person = selector.getSelectedPerson();

            if (person == null) {
                GenericPerson personToAdd = (GenericPerson) data.get(ITEM_SEARCH);
                logger.debug(String.format("Adding person %s",
                                           personToAdd.getFullName()));

                orga.addPerson(personToAdd,
                               (String) data.get(
                                   GenericOrganizationalUnitPersonCollection.PERSON_ROLE),
                               (String) data.get(
                                   GenericOrganizationalUnitPersonCollection.STATUS));
                m_itemSearch.publishCreatedItem(data, personToAdd);
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
            GenericOrganizationalUnit orga = (GenericOrganizationalUnit) getItemSelectionModel().
                getSelectedObject(state);

            GenericPerson person = (GenericPerson) data.get(ITEM_SEARCH);

            /*if (!(person.getContentBundle().hasInstance(orga.getLanguage(),
             Kernel.getConfig().
             languageIndependentItems()))) {
             data.addError(
             ContenttypesGlobalizationUtil.globalize(
             "cms.contenttypes.ui.genericorgaunit.person.no_suitable_language_variant"));

             return;
             }*/
            final ContentBundle bundle = person.getContentBundle();
            final GenericOrganizationalUnitPersonCollection persons = orga.getPersons();
            persons.addFilter(String.format("id = %s",
                                            bundle.getID().toString()));

            /*person = (GenericPerson) person.getContentBundle().getInstance(orga.
             getLanguage());
             GenericOrganizationalUnitPersonCollection persons =
             orga.getPersons();

             persons.addFilter(String.format("id = %s",
             person.getID().toString()));*/
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
