/*
 * Copyright (c) 2014 Jens Pelzetter
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
package com.arsdigita.cms.contentassets.ui;

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
import com.arsdigita.cms.contentassets.SciPublicationsPersonsService;
import com.arsdigita.cms.contenttypes.GenericPerson;
import com.arsdigita.cms.contenttypes.Publication;
import com.arsdigita.cms.contenttypes.util.ContenttypesGlobalizationUtil;
import com.arsdigita.cms.ui.ItemSearchWidget;
import com.arsdigita.cms.ui.authoring.BasicItemForm;
import com.arsdigita.globalization.GlobalizationHelper;
import com.arsdigita.toolbox.GlobalisationUtil;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class SciPublicationsPersonsPersonForm extends BasicItemForm {

    private ItemSearchWidget itemSearch;
    private final static String ITEM_SEARCH = "publicationRelatedPersons";
    private final static String RELATION = "relation";
    private final ItemSelectionModel itemModel;

    public SciPublicationsPersonsPersonForm(final ItemSelectionModel itemModel) {

        super("SciPublicationsPersonsPersonForm", itemModel);
        this.itemModel = itemModel;

    }

    @Override
    public void addWidgets() {

        final GlobalisationUtil globalisationUtil = new SciPublicationsPersonsGlobalisationUtil();
        add(new Label(globalisationUtil.globalise(
            "com.arsdigita.cms.contentassets.publicationspersons.select_person")));
        itemSearch = new ItemSearchWidget(
            ITEM_SEARCH,
            ContentType.findByAssociatedObjectType(GenericPerson.class.getName()));
        itemSearch.setDisableCreatePane(true);
        add(itemSearch);

        add(new Label(globalisationUtil.globalise(
            "com.arsdigita.cms.contentassets.publicationspersons.select_person_relation")));
        final ParameterModel relationParam = new StringParameter(RELATION);
        final SingleSelect relationSelect = new SingleSelect(relationParam);
        relationSelect.addValidationListener(new NotNullValidationListener());
        relationSelect.addOption(new Option("", new Label(ContenttypesGlobalizationUtil.globalize(
                                            "cms.ui.select_one"))));
        final RelationAttributeCollection relations = new RelationAttributeCollection(
            "publications_persons_relations");
        relations.addLanguageFilter(GlobalizationHelper.getNegotiatedLocale().getLanguage());
        while (relations.next()) {
            RelationAttribute relation;
            relation = relations.getRelationAttribute();
            relationSelect.addOption(new Option(relation.getKey(), relation.getName()));
        }
        add(relationSelect);
    }

    @Override
    public void init(final FormSectionEvent event) throws FormProcessException {

        setVisible(event.getPageState(), true);
    }

    @Override
    public void process(final FormSectionEvent event) throws FormProcessException {

        final FormData data = event.getFormData();
        final PageState state = event.getPageState();
        final Publication publication = (Publication) itemModel.getSelectedObject(state);
        final SciPublicationsPersonsService service = new SciPublicationsPersonsService();

        if (!getSaveCancelSection().getCancelButton().isSelected(state)) {
            final GenericPerson person = (GenericPerson) data.get(ITEM_SEARCH);
            service.addPublication(person, publication, (String) data.get(RELATION));
        }

    }

    @Override
    public void validate(final FormSectionEvent event) throws FormProcessException {

        final FormData data = event.getFormData();

        final GlobalisationUtil globalisationUtil = new SciPublicationsPersonsGlobalisationUtil();
        if (data.get(ITEM_SEARCH) == null) {
            data.addError(globalisationUtil.globalise(
                "com.arsdigita.cms.contentasset.publications_persons.none_selected"));
        }

        if ((data.get(RELATION) == null) || ((String) data.get(RELATION)).isEmpty()) {
            data.addError(globalisationUtil.globalise(
                "com.arsdigita.cms.contentasset.publications_persons.none_relation_selected"));
        }

    }

}
