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
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.event.ParameterEvent;
import com.arsdigita.bebop.event.ParameterListener;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.IntegerParameter;
import com.arsdigita.bebop.parameters.ParameterData;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.ContentType;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.PublicationWithPublisher;
import com.arsdigita.cms.contenttypes.Publisher;
import com.arsdigita.cms.ui.ItemSearchWidget;
import org.apache.log4j.Logger;

/**
 *
 * @author Jens Pelzetter
 */
public class PublicationWithPublisherPropertyForm
        extends PublicationPropertyForm
        implements FormProcessListener,
                   FormInitListener,
                   FormSubmissionListener {

    private static final Logger s_log = Logger.getLogger(
            PublicationWithPublisherPropertyForm.class);
    private PublicationWithPublisherPropertiesStep m_step;
    private ItemSearchWidget m_itemSearch;
    private final String ITEM_SEARCH = "publisher";
    public static final String ID = "PublicationWithPublisherEdit";
    private ItemSelectionModel m_itemModel;

    public PublicationWithPublisherPropertyForm(ItemSelectionModel itemModel) {
        this(itemModel, null);
    }

    public PublicationWithPublisherPropertyForm(
            ItemSelectionModel itemModel,
            PublicationWithPublisherPropertiesStep step) {
        super(itemModel, step);
        m_step = step;
        addSubmissionListener(this);
    }

    @Override
    protected void addWidgets() {
        super.addWidgets();

        add(new Label((String) PublicationGlobalizationUtil.globalize(
                "publications.ui.with_publisher.isbn").localize()));
        ParameterModel isbnParam = new StringParameter(
                PublicationWithPublisher.ISBN);
        TextField isbn = new TextField(isbnParam);
        isbn.setMaxLength(17);
        isbn.addValidationListener(new ParameterListener() {

            public void validate(ParameterEvent event)
                    throws FormProcessException {
                ParameterData data = event.getParameterData();
                String value = (String) data.getValue();

                if (value.isEmpty()) {
                    return;
                }

                value = value.replace("-", "");
             
                if (value.length() != 13) {
                    data.invalidate();
                    data.addError(PublicationGlobalizationUtil.globalize(
                            "publications.ui.invalid_isbn"));
                }

                try {
                    Long num = Long.parseLong(value);
                } catch (NumberFormatException ex) {
                    data.invalidate();
                    data.addError(PublicationGlobalizationUtil.globalize(
                            "publications.ui.invalid_isbn"));
                }
            }
        });
        add(isbn);

        add(new Label((String) PublicationGlobalizationUtil.globalize(
                "publications.ui.collected_volume.volume").localize()));
        ParameterModel volumeParam = new IntegerParameter(
                PublicationWithPublisher.VOLUME);
        TextField volume = new TextField(volumeParam);
        add(volume);

        add(new Label((String) PublicationGlobalizationUtil.globalize(
                "publications.ui.collected_volume.number_of_volumes").
                localize()));
        ParameterModel numberOfVolumesParam =
                       new IntegerParameter(
                PublicationWithPublisher.NUMBER_OF_VOLUMES);
        TextField numberOfVolumes = new TextField(numberOfVolumesParam);
        add(numberOfVolumes);

        add(new Label((String) PublicationGlobalizationUtil.globalize(
                "publications.ui.collected_volume.number_of_pages").
                localize()));
        ParameterModel numberOfPagesParam = new IntegerParameter(
                PublicationWithPublisher.NUMBER_OF_PAGES);
        TextField numberOfPages = new TextField(numberOfPagesParam);
        add(numberOfPages);

        add(new Label((String) PublicationGlobalizationUtil.globalize(
                "publications.ui.collected_volume.edition").
                localize()));
        ParameterModel editionModel = new StringParameter(
                PublicationWithPublisher.EDITION);
        TextField edition = new TextField(editionModel);
        add(edition);
    }

    @Override
    public void init(FormSectionEvent fse) throws FormProcessException {
        FormData data = fse.getFormData();

        super.init(fse);

        PublicationWithPublisher publication =
                                 (PublicationWithPublisher) super.
                initBasicWidgets(fse);

        data.put(PublicationWithPublisher.ISBN, publication.getISBN());
        data.put(PublicationWithPublisher.VOLUME, publication.getVolume());
        data.put(PublicationWithPublisher.NUMBER_OF_VOLUMES,
                 publication.getNumberOfVolumes());
        data.put(PublicationWithPublisher.NUMBER_OF_PAGES,
                 publication.getNumberOfPages());
        data.put(PublicationWithPublisher.EDITION, publication.getEdition());

    }

    @Override
    public void process(FormSectionEvent fse) throws FormProcessException {
        FormData data = fse.getFormData();
        super.process(fse);

        PublicationWithPublisher publication =
                                 (PublicationWithPublisher) super.
                processBasicWidgets(fse);

        if ((publication != null) && getSaveCancelSection().getSaveButton().
                isSelected(fse.getPageState())) {
            String isbn = (String) data.get(PublicationWithPublisher.ISBN);
            isbn = isbn.replace("-", "");
            publication.setISBN(isbn);

            publication.setVolume((Integer) data.get(
                    PublicationWithPublisher.VOLUME));
            publication.setNumberOfVolumes((Integer) data.get(
                    PublicationWithPublisher.NUMBER_OF_VOLUMES));
            publication.setNumberOfPages((Integer) data.get(
                    PublicationWithPublisher.NUMBER_OF_PAGES));
            publication.setEdition((String) data.get(
                    PublicationWithPublisher.EDITION));

            publication.save();
        }
    }
}
