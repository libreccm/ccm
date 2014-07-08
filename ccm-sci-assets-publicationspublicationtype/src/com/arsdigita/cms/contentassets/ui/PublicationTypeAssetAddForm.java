/*
 * Copyright (c) 2013 Jens Pelzetter
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
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.NotEmptyValidationListener;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.RelationAttribute;
import com.arsdigita.cms.contentassets.PublicationTypeAsset;
import com.arsdigita.cms.contentassets.PublicationTypeValuesCollection;
import com.arsdigita.cms.contenttypes.Publication;
import com.arsdigita.cms.ui.authoring.BasicItemForm;
import com.arsdigita.globalization.GlobalizationHelper;
import com.arsdigita.kernel.ui.ACSObjectSelectionModel;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class PublicationTypeAssetAddForm extends BasicItemForm {

    private final static String PUB_TYPE = "publicationType";
    private final static String ISBN = "isbn";
    private final static String MISC = "misc";
    private final ItemSelectionModel itemModel;
    private final ACSObjectSelectionModel typeModel;

    public PublicationTypeAssetAddForm(final ItemSelectionModel itemModel, final ACSObjectSelectionModel typeModel) {
        super("PublicationTypeAssetAddForm", itemModel);

        this.itemModel = itemModel;
        this.typeModel = typeModel;
    }

    @Override
    public void addWidgets() {

        //add(new Label(PublicationTypeAssetGlobalizationUtil.
        //        globalize("scipublications.publication_type_asset.form.type")));
        final SingleSelect type = new SingleSelect(PUB_TYPE);
        type.addValidationListener(new NotNullValidationListener());
        type.addValidationListener(new NotEmptyValidationListener());
        type.addOption(new Option("", new Label(PublicationTypeAssetGlobalizationUtil.globalize(
                "scipublications.publication_type_asset.form.type.select_one"))));
        final PublicationTypeValuesCollection values = new PublicationTypeValuesCollection();
        values.addLanguageFilter(GlobalizationHelper.getNegotiatedLocale().getLanguage());

        while (values.next()) {
            final RelationAttribute value = values.getRelationAttribute();
            type.addOption(new Option(value.getKey(), value.getName()));
        }
        type.setLabel(PublicationTypeAssetGlobalizationUtil.
                globalize("scipublications.publication_type_asset.form.type"));
        add(type);

        //add(new Label(PublicationTypeAssetGlobalizationUtil.
        //        globalize("scipublications.publication_type_asset.form.isbn")));
        final TextField isbn = new TextField(ISBN);
        isbn.setLabel(PublicationTypeAssetGlobalizationUtil.
                globalize("scipublications.publication_type_asset.form.isbn"));
        add(isbn);

        //add(new Label(PublicationTypeAssetGlobalizationUtil.
        //        globalize("scipublications.publication_type_asset.form.misc")));
        final TextArea misc = new TextArea(MISC);
        misc.setLabel(PublicationTypeAssetGlobalizationUtil.
                globalize("scipublications.publication_type_asset.form.misc"));
        misc.setRows(12);
        misc.setCols(60);
        add(misc);
    }

    @Override
    public void init(final FormSectionEvent event) throws FormProcessException {
        final PageState state = event.getPageState();

        if (typeModel.getSelectedObject(state) != null) {
            final PublicationTypeAsset typeAsset = (PublicationTypeAsset) typeModel.getSelectedObject(state);
            final FormData data = event.getFormData();

            data.put(PUB_TYPE, typeAsset.getPublicationType());
            data.put(ISBN, typeAsset.getIsbn());
            data.put(MISC, typeAsset.getMisc());
        }

        setVisible(state, true);
    }

    @Override
    public void process(final FormSectionEvent event) throws FormProcessException {
        final FormData data = event.getFormData();
        final PageState state = event.getPageState();

        final PublicationTypeAsset typeAsset;
        if (typeModel.getSelectedObject(state) == null) {
            final Publication publication = (Publication) itemModel.getSelectedItem(state);

            typeAsset = PublicationTypeAsset.create(publication);
        } else {
            typeAsset = (PublicationTypeAsset) typeModel.getSelectedObject(state);

            typeModel.setSelectedObject(state, null);
        }

        typeAsset.setPublicationType(data.getString(PUB_TYPE));
        typeAsset.setIsbn(data.getString(ISBN));
        typeAsset.setMisc(data.getString(MISC));

        typeAsset.save();
    }

}
