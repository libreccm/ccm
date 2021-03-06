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
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.cms.ContentType;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.Publication;
import com.arsdigita.cms.contenttypes.PublicationsConfig;
import com.arsdigita.cms.contenttypes.Series;
import com.arsdigita.cms.contenttypes.SeriesCollection;
import com.arsdigita.cms.contenttypes.VolumeInSeriesCollection;
import com.arsdigita.cms.ui.ItemSearchWidget;
import com.arsdigita.cms.ui.authoring.BasicItemForm;
import com.arsdigita.kernel.Kernel;
import java.math.BigDecimal;
import org.apache.log4j.Logger;

/**
 *
 * @author Jens Pelzetter
 * @version $Id$
 */
public class PublicationSeriesAddForm
        extends BasicItemForm
        implements FormProcessListener,
                   FormInitListener {

    private static final Logger s_log =
                                Logger.getLogger(PublicationSeriesAddForm.class);
    private PublicationPropertiesStep m_step;
    private ItemSearchWidget m_itemSearch;
    private final String ITEM_SEARCH = "series";
    private ItemSelectionModel m_itemModel;
    private TextField volumeOfSeries;
    private final static PublicationsConfig config = new PublicationsConfig();

    static {
        config.load();
    }

    public PublicationSeriesAddForm(ItemSelectionModel itemModel) {
        super("SeriesEntryForm", itemModel);
        m_itemModel = itemModel;
    }

    @Override
    protected void addWidgets() {

        m_itemSearch = new ItemSearchWidget(
                ITEM_SEARCH,
                ContentType.findByAssociatedObjectType(Series.class.getName()));
        m_itemSearch.setDefaultCreationFolder(config.getDefaultSeriesFolder());
        m_itemSearch.setLabel(PublicationGlobalizationUtil.globalize(
                              "publications.ui.series.select_series"));
        add(m_itemSearch);

        ParameterModel volumeOfSeriesParam = new StringParameter(
                VolumeInSeriesCollection.VOLUME_OF_SERIES);
        volumeOfSeries = new TextField(volumeOfSeriesParam);
        volumeOfSeries.setLabel(PublicationGlobalizationUtil.globalize(
                       "publications.ui.series.volume_of_series"));
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
        Publication publication = (Publication) getItemSelectionModel().
                getSelectedObject(state);

        if (!(this.getSaveCancelSection().getCancelButton().
              isSelected(state))) {
            Series series = (Series) data.get(ITEM_SEARCH);
            series = (Series) series.getContentBundle().getInstance(publication.
                    getLanguage());

            publication.addSeries(series, (String) data.get(VolumeInSeriesCollection.VOLUME_OF_SERIES));
            m_itemSearch.publishCreatedItem(data, series);
        }

        init(fse);
    }

    @Override
    public void validate(FormSectionEvent fse) throws FormProcessException {
        final PageState state = fse.getPageState();
        final FormData data = fse.getFormData();

        if (data.get(ITEM_SEARCH) == null) {
            data.addError(PublicationGlobalizationUtil.globalize(
                    "publications.ui.series.select_series.no_series_selected"));
            return;
        }

        Publication publication = (Publication) getItemSelectionModel().
                getSelectedObject(state);
        Series series = (Series) data.get(ITEM_SEARCH);
        if (!(series.getContentBundle().hasInstance(publication.getLanguage(),
                                                    Kernel.getConfig().
              languageIndependentItems()))) {
            data.addError(
                    PublicationGlobalizationUtil.globalize(
                    "publications.ui.series.select_series.no_suitable_language_variant"));
            return;
        }

        series = (Series) series.getContentBundle().getInstance(publication.
                getLanguage());
        SeriesCollection seriesColl = publication.getSeries();
        seriesColl.addFilter(String.format("id = %s", series.getID().toString()));
        if (seriesColl.size() > 0) {
            data.addError(PublicationGlobalizationUtil.globalize(
                    "publications.ui.series.select_series.already_added"));
        }

        seriesColl.close();
    }

}
