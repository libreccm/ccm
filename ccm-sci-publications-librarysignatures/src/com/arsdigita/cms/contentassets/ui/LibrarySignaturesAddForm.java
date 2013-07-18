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
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.NotEmptyValidationListener;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contentassets.LibrarySignature;
import com.arsdigita.cms.contenttypes.Publication;
import com.arsdigita.cms.ui.authoring.BasicItemForm;
import com.arsdigita.kernel.ui.ACSObjectSelectionModel;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class LibrarySignaturesAddForm extends BasicItemForm {

    private final static String LIBRARY = "library";
    private final static String SIGNATURE = "signature";
    private final static String LINK = "link";
    private final ItemSelectionModel itemModel;
    private final ACSObjectSelectionModel signatureModel;

    public LibrarySignaturesAddForm(final ItemSelectionModel itemModel, final ACSObjectSelectionModel signatureModel) {
        super("LibrarySignaturesAddForm", itemModel);

        this.itemModel = itemModel;
        this.signatureModel = signatureModel;
    }

    @Override
    public void addWidgets() {
        add(new Label(LibrarySignaturesGlobalizationUtil.globalize("scipublications.librarysignatures.form.library")));
        final TextField library = new TextField(LIBRARY);
        library.addValidationListener(new NotNullValidationListener(LibrarySignaturesGlobalizationUtil.globalize(
                "scipublications.librarysignatures.form.library.not_null")));
        library.addValidationListener(new NotEmptyValidationListener(LibrarySignaturesGlobalizationUtil.globalize(
                "scipublications.librarysignatures.form.library.not_empty")));
        add(library);

        add(new Label(LibrarySignaturesGlobalizationUtil.globalize("scipublications.librarysignatures.form.signature")));
        final TextField signature = new TextField(SIGNATURE);
        signature.addValidationListener(new NotNullValidationListener(LibrarySignaturesGlobalizationUtil.globalize(
                "scipublications.librarysignatures.form.signature.not_null")));
        library.addValidationListener(new NotEmptyValidationListener(LibrarySignaturesGlobalizationUtil.globalize(
                "scipublications.librarysignatures.form.signature.not_empty")));
        add(signature);

        add(new Label(LibrarySignaturesGlobalizationUtil.globalize("scipublications.librarysignatures.form.link")));
        add(new TextField(LINK));
    }

    @Override
    public void init(final FormSectionEvent event) throws FormProcessException {
        final PageState state = event.getPageState();

        if (signatureModel.getSelectedObject(state) != null) {
            final LibrarySignature signature = (LibrarySignature) signatureModel.getSelectedObject(state);
            final FormData data = event.getFormData();

            data.put(LIBRARY, signature.getLibrary());
            data.put(SIGNATURE, signature.getSignature());
            data.put(LINK, signature.getLibraryLink());
        }

        setVisible(state, true);
    }

    @Override
    public void process(final FormSectionEvent event) throws FormProcessException {
        final FormData data = event.getFormData();
        final PageState state = event.getPageState();

        final LibrarySignature signature;
        if (signatureModel.getSelectedObject(state) == null) {
            final Publication publication = (Publication) itemModel.getSelectedItem(state);

            signature = LibrarySignature.create(publication);
        } else {
            signature = (LibrarySignature) signatureModel.getSelectedObject(state);

            signatureModel.setSelectedObject(state, null);
        }

        signature.setLibrary(data.getString(LIBRARY));
        signature.setSignature(data.getString(SIGNATURE));
        signature.setLibraryLink(data.getString(LINK));

        signature.save();
    }

}
