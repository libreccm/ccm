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
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.ContentType;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.Publication;
import com.arsdigita.cms.contenttypes.Series;
import com.arsdigita.cms.contenttypes.VolumeInSeriesCollection;
import com.arsdigita.cms.ui.ItemSearchWidget;
import com.arsdigita.cms.ui.authoring.BasicItemForm;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;

/**
 *
 * @author Jens Pelzetter
 * @version $Id$
 */
@SuppressWarnings("PMD.LongVariable")
public class SeriesVolumeAddForm extends BasicItemForm {

    private static final String ITEM_SEARCH = "volumes";
    //private final SeriesPropertiesStep seriesStep;
    private ItemSearchWidget itemSearch;
    //private final SaveCancelSection saveCancelSection;    
    //private final ItemSelectionModel itemModel;
    private final SimpleEditStep editStep;
    private Label selectedVolumeLabel;
    private TextField volumeOfSeries;

    public SeriesVolumeAddForm(final ItemSelectionModel itemModel, final SimpleEditStep editStep) {
        super("VolumesEntryForm", itemModel);
        //itemModel = itemModel;
        this.editStep = editStep;
    }

    @Override
    protected void addWidgets() {
        add(new Label(PublicationGlobalizationUtil.globalize(
                "publications.ui.series.volumes.select_publication")));
        itemSearch = new ItemSearchWidget(
                ITEM_SEARCH,
                ContentType.findByAssociatedObjectType(
                Publication.class.getName()));
        itemSearch.setDisableCreatePane(true);
        add(itemSearch);

        selectedVolumeLabel = new Label("");
        selectedVolumeLabel.addPrintListener(new PrintListener() {

            @Override
            public void prepare(final PrintEvent event) {
                final Publication publication = ((SeriesVolumesStep) editStep).
                getSelectedPublication();
                final Label target = (Label) event.getTarget();
                target.setLabel(publication.getTitle());
            }
        });
        add(selectedVolumeLabel);

        add(new Label(PublicationGlobalizationUtil.globalize("publications.ui.series.volume_of_series")));
        ParameterModel volumeOfSeriesParam = new StringParameter(VolumeInSeriesCollection.VOLUME_OF_SERIES);
        volumeOfSeries = new TextField(volumeOfSeriesParam);
        add(volumeOfSeries);
    }

    @Override
    public void init(final FormSectionEvent fse) throws FormProcessException {
        final FormData data = fse.getFormData();
        final PageState state = fse.getPageState();

        final Publication publication = ((SeriesVolumesStep) editStep).
                getSelectedPublication();
        final String volume = ((SeriesVolumesStep) editStep).getSelectedVolume();

        if (publication == null) {
            itemSearch.setVisible(state, true);
            selectedVolumeLabel.setVisible(state, false);
        } else {
            data.put(ITEM_SEARCH, publication);
            if ((volume == null)) {
                volumeOfSeries.setValue(state, 0);
            } else {
                volumeOfSeries.setValue(state, volume);
            }

            itemSearch.setVisible(state, false);
            //selectedVolumeLabel.setLabel(publication.getTitle());
            selectedVolumeLabel.setVisible(state, true);
        }

        setVisible(state, true);
    }

    @Override
    public void process(final FormSectionEvent fse) throws FormProcessException {
        final FormData data = fse.getFormData();
        final PageState state = fse.getPageState();
        final Series series = (Series) getItemSelectionModel().getSelectedObject(state);

        if (!(this.getSaveCancelSection().getCancelButton().
              isSelected(state))) {
            Publication volume = ((SeriesVolumesStep) editStep).
                    getSelectedPublication();

            final String volOfSeries;
            if (this.volumeOfSeries.getValue(state) == null) {
                volOfSeries = null;
            } else {
                volOfSeries = data.getString(VolumeInSeriesCollection.VOLUME_OF_SERIES);
            }

            if (volume == null) {
                volume = (Publication) data.get(ITEM_SEARCH);
                volume = (Publication) volume.getContentBundle().getInstance(series.
                        getLanguage());

                series.addVolume(volume, (String) data.get(VolumeInSeriesCollection.VOLUME_OF_SERIES));
            } else {
                final VolumeInSeriesCollection volumes = series.getVolumes();

                while (volumes.next()) {
                    if (volumes.getPublication().equals(volume)) {
                        break;
                    }
                }

                volumes.setVolumeOfSeries(volOfSeries);

                ((SeriesVolumesStep) editStep).setSelectedPublication(null);
                ((SeriesVolumesStep) editStep).setSelectedVolume(null);

                volumes.close();
            }
        }

        init(fse);
    }

    @Override
    public void validate(final FormSectionEvent fse) throws FormProcessException {
        final PageState state = fse.getPageState();
        final FormData data = fse.getFormData();
        boolean editing = false;

        if ((((SeriesVolumesStep) editStep).getSelectedPublication() == null)
            && (data.get(ITEM_SEARCH) == null)) {
            data.addError(
                    PublicationGlobalizationUtil.globalize(
                    "publications.ui.series.volume_of_series.no_volume_selected"));
            return;
        }

        final Series series = (Series) getItemSelectionModel().getSelectedObject(state);
        Publication volume = (Publication) data.get(ITEM_SEARCH);
        if (volume == null) {
            volume = ((SeriesVolumesStep) editStep).getSelectedPublication();
            editing = true;
        }


        if (!editing) {
            final VolumeInSeriesCollection volumes = series.getVolumes();
            volumes.addFilter(String.format("id = %s", volume.getID().toString()));
            if (volumes.size() > 0) {
                data.addError(PublicationGlobalizationUtil.globalize(
                        "publications.ui.series.volume_of_series.already_added"));
            }
            volumes.close();
        }
    }

}
