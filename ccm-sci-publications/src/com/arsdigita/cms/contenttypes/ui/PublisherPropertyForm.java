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
import com.arsdigita.bebop.FormSection;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.NotEmptyValidationListener;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.Publisher;

/**
 *
 * @author Jens Pelzetter
 * @version $Id$
 */
public class PublisherPropertyForm
        extends GenericOrganizationalUnitPropertyForm
        implements FormProcessListener,
                   FormInitListener,
                   FormSubmissionListener {

    private PublisherPropertiesStep m_step;
    public static final String PUBLISHER_NAME = Publisher.PUBLISHER_NAME;
    public static final String PLACE = Publisher.PLACE;
    public static final String ID = "Publisher_edit";

    public PublisherPropertyForm(ItemSelectionModel itemModel) {
        this(itemModel, null);
    }

    public PublisherPropertyForm(ItemSelectionModel itemModel,
                                 PublisherPropertiesStep step) {
        super(itemModel, step);
        m_step = step;
        addSubmissionListener(this);
    }

    @Override
    protected void addWidgets() {
        super.addWidgets();

        addMandatoryFieldWidgets(this);
    }

    public static void addMandatoryFieldWidgets(FormSection form) {
        form.add(new Label((String) PublicationGlobalizationUtil.globalize(
                "publications.ui.publisher.name").localize()));
        ParameterModel nameParam = new StringParameter(PUBLISHER_NAME);
        TextField name = new TextField(nameParam);
        name.addValidationListener(new NotNullValidationListener());
        name.addValidationListener(new NotEmptyValidationListener());
        form.add(name);

        form.add(new Label((String) PublicationGlobalizationUtil.globalize(
                "publications.ui.publisher.place").localize()));
        ParameterModel placeParam = new StringParameter(PLACE);
        TextField place = new TextField(placeParam);
        form.add(place);
    }

    @Override
    public void init(FormSectionEvent fse) throws FormProcessException {
        super.init(fse);

        FormData data = fse.getFormData();
        Publisher publisher = (Publisher) super.initBasicWidgets(fse);

        data.put(PUBLISHER_NAME, publisher.getPublisherName());
        data.put(PLACE, publisher.getPlace());
    }

    @Override
    public void process(FormSectionEvent fse) throws FormProcessException {
        super.process(fse);

        FormData data = fse.getFormData();
        Publisher publisher = (Publisher) super.processBasicWidgets(fse);

        if ((publisher != null) && getSaveCancelSection().getSaveButton().
                isSelected(fse.getPageState())) {
            publisher.setPublisherName((String) data.get(PUBLISHER_NAME));
            publisher.setPlace((String) data.get(PLACE));
        }

        publisher.save();
    }
}
