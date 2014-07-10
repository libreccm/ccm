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
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.form.DHTMLEditor;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.SingleSelect;
import com.arsdigita.bebop.parameters.NotEmptyValidationListener;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.RelationAttributeCollection;
import com.arsdigita.cms.RelationAttributeResourceBundleControl;
import com.arsdigita.cms.contentassets.GenericOrgaUnitTextAsset;
import com.arsdigita.cms.contentassets.GenericOrgaUnitTextAssetGlobalizationUtil;
import com.arsdigita.cms.contenttypes.GenericOrganizationalUnit;
import com.arsdigita.cms.ui.authoring.BasicItemForm;
import com.arsdigita.kernel.ui.ACSObjectSelectionModel;
import com.arsdigita.util.UncheckedWrapperException;
import java.util.TooManyListenersException;
import com.arsdigita.globalization.GlobalizedMessage;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class GenericOrgaUnitTextAssetEdit extends BasicItemForm implements FormSubmissionListener {

    private final static String TEXT_ASSET_NAME = "textAssetName";
    private final static String CONTENT = "content";
    private final ItemSelectionModel itemModel;
    private final ACSObjectSelectionModel selectionModel;

    public GenericOrgaUnitTextAssetEdit(final ItemSelectionModel itemModel,
                                        final ACSObjectSelectionModel selectionModel) {
        super("GenericOrgaUnitTextAssetEditForm", itemModel);

        this.itemModel = itemModel;
        this.selectionModel = selectionModel;
        addSubmissionListener(this);
    }

    @Override
    public void addWidgets() {
        add(new Label(GenericOrgaUnitTextAssetGlobalizationUtil.globalize(
                "cms.orgaunit.textasset.text_asset_name.label")));
        final SingleSelect nameSelect = new SingleSelect(TEXT_ASSET_NAME);
        nameSelect.addOption(new Option("", ""));
        try {
            nameSelect.addPrintListener(new PrintListener() {

                @Override
                public void prepare(final PrintEvent event) {
                    final SingleSelect target = (SingleSelect) event.getTarget();

                    final RelationAttributeCollection names = new RelationAttributeCollection(
                            "GenericOrgaUnitTextAssetName");
                    //names.addLanguageFilter(GlobalizationHelper.getNegotiatedLocale().getLanguage());
                    while (names.next()) {
                        //target.addOption(new Option(names.getKey(), names.getName()));
                        target.addOption(new Option(
                                names.getKey(),
                                new Label(new GlobalizedMessage(
                                                names.getKey(),
                                                "GenericOrgaUnitTextAssetName",
                                                new RelationAttributeResourceBundleControl()))));
                    }
                }

            });
        } catch (TooManyListenersException ex) {
            throw new UncheckedWrapperException(ex);
        }

        nameSelect.addValidationListener(new NotEmptyValidationListener());
        add(nameSelect);

        add(new Label(GenericOrgaUnitTextAssetGlobalizationUtil.globalize(
                "cms.orgaunit.textasset.content.label")));
        final DHTMLEditor contentEditor = new DHTMLEditor(
                new StringParameter(CONTENT), ContentSection.getConfig().getDHTMLEditorConfig());
        contentEditor.setRows(40);
        add(contentEditor);

    }

    @Override
    public void init(final FormSectionEvent event) throws FormProcessException {
        final PageState state = event.getPageState();

        if (selectionModel.getSelectedObject(state) != null) {
            final GenericOrgaUnitTextAsset textAsset = (GenericOrgaUnitTextAsset) selectionModel.
                    getSelectedObject(state);
            final FormData data = event.getFormData();

            data.put(TEXT_ASSET_NAME, textAsset.getTextAssetName());
            data.put(CONTENT, textAsset.getContent());
        }

        setVisible(state, true);
    }

    @Override
    public void process(final FormSectionEvent event) throws FormProcessException {
        final FormData data = event.getFormData();
        final PageState state = event.getPageState();

        final GenericOrgaUnitTextAsset textAsset;
        if (selectionModel.getSelectedObject(state) == null) {
            final GenericOrganizationalUnit orgaunit = (GenericOrganizationalUnit) itemModel.
                    getSelectedObject(state);

            textAsset = GenericOrgaUnitTextAsset.create(orgaunit, data.getString(TEXT_ASSET_NAME));
        } else {
            textAsset = (GenericOrgaUnitTextAsset) selectionModel.getSelectedObject(state);

            selectionModel.clearSelection(state);

            textAsset.setTextAssetName(data.getString(TEXT_ASSET_NAME));
        }

        textAsset.setContent(data.getString(CONTENT));

        textAsset.save();
    }

    @Override
    public void submitted(final FormSectionEvent event) throws FormProcessException {
        final PageState state = event.getPageState();
        if (getSaveCancelSection().getCancelButton().isSelected(state)) {
            selectionModel.clearSelection(state);
        }
    }

}
