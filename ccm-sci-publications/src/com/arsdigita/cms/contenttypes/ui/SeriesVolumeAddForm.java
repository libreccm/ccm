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
import com.arsdigita.bebop.SaveCancelSection;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.IntegerParameter;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.cms.ContentType;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.Publication;
import com.arsdigita.cms.contenttypes.Series;
import com.arsdigita.cms.contenttypes.VolumeInSeriesCollection;
import com.arsdigita.cms.ui.ItemSearchWidget;
import com.arsdigita.cms.ui.authoring.BasicItemForm;
import org.apache.log4j.Logger;

/**
 *
 * @author Jens Pelzetter
 */
public class SeriesVolumeAddForm extends BasicItemForm {

    private static final Logger s_log = Logger.getLogger(
            SeriesVolumeAddForm.class);
    private SeriesPropertiesStep m_step;
    private ItemSearchWidget m_itemSearch;
    private SaveCancelSection m_saveCancelSection;
    private final String ITEM_SEARCH = "volumes";
    private ItemSelectionModel m_itemModel;

    public SeriesVolumeAddForm(ItemSelectionModel itemModel) {
        super("VolumesEntryForm", itemModel);
        m_itemModel = itemModel;
    }

    @Override
    protected void addWidgets() {
        add(new Label((String) PublicationGlobalizationUtil.globalize(
                "publications.ui.series.volumes.select_publication").
                localize()));
        m_itemSearch = new ItemSearchWidget(
                ITEM_SEARCH,
                ContentType.findByAssociatedObjectType(
                Publication.class.getName()));
        add(m_itemSearch);

        add(new Label((String) PublicationGlobalizationUtil.globalize(
                "publications.ui.series.volume_of_series").localize()));
        ParameterModel volumeOfSeriesParam = new IntegerParameter(
                VolumeInSeriesCollection.VOLUME_OF_SERIES);
        TextField volumeOfSeries = new TextField(volumeOfSeriesParam);
        add(volumeOfSeries);
    }

    @Override
    public void init(FormSectionEvent fse) throws FormProcessException {
        FormData data = fse.getFormData();
        PageState state = fse.getPageState();

        setVisible(state, true);
    }

    @Override
    public void process(FormSectionEvent fse) throws FormProcessException {
        FormData data = fse.getFormData();
        PageState state = fse.getPageState();
        Series series = (Series) getItemSelectionModel().
                getSelectedObject(state);

        if (!(this.getSaveCancelSection().getCancelButton().
              isSelected(state))) {
            Publication volume = (Publication) data.get(ITEM_SEARCH);
            volume = (Publication) volume.getContentBundle().getInstance(series.
                    getLanguage());

            series.addVolume(volume,
                             (Integer) data.get(
                    VolumeInSeriesCollection.VOLUME_OF_SERIES));
        }

        init(fse);
    }

    @Override
    public void validate(FormSectionEvent fse) throws FormProcessException {
        final PageState state = fse.getPageState();
        final FormData data = fse.getFormData();

        if (data.get(ITEM_SEARCH) == null) {
            data.addError(
                    PublicationGlobalizationUtil.globalize(
                    "publications.ui.series.volume_of_series.no_volume_selected"));
            return;
        }

        Series series = (Series) getItemSelectionModel().
                getSelectedObject(state);
        Publication volume = (Publication) data.get(ITEM_SEARCH);
        if (!(volume.getContentBundle().hasInstance(series.getLanguage()))) {
            data.addError(
                    PublicationGlobalizationUtil.globalize(
                    "publications.ui.series.volume_of_series.no_suitable_language_variant"));
            return;
        }

        volume = (Publication) volume.getContentBundle().getInstance(series.
                getLanguage());
        VolumeInSeriesCollection volumes = series.getVolumes();
        volumes.addFilter(String.format("id = %s", volume.getID().toString()));
        if (volumes.size() > 0) {
            data.addError(PublicationGlobalizationUtil.globalize(
                    "publications.ui.series.volume_of_series.already_added"));
        }

        volumes.close();
    }
}
