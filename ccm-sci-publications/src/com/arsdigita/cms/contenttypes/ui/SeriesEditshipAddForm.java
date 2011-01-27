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
import com.arsdigita.bebop.parameters.DateParameter;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.cms.ContentType;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.EditshipCollection;
import com.arsdigita.cms.contenttypes.GenericPerson;
import com.arsdigita.cms.contenttypes.Series;
import com.arsdigita.cms.ui.ItemSearchWidget;
import com.arsdigita.cms.ui.authoring.BasicItemForm;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import org.apache.log4j.Logger;

/**
 *
 * @author Jens Pelzetter
 */
public class SeriesEditshipAddForm extends BasicItemForm {

    private static final Logger s_log =
                                Logger.getLogger(SeriesEditshipAddForm.class);
    private SeriesPropertiesStep m_step;
    private ItemSearchWidget m_itemSearch;
    private SaveCancelSection m_saveCancelSection;
    private final String ITEM_SEARCH = "editors";
    private ItemSelectionModel m_itemModel;

    public SeriesEditshipAddForm(ItemSelectionModel itemModel) {
        super("EditorsEntryForm", itemModel);
        m_itemModel = itemModel;
    }

    @Override
    protected void addWidgets() {
        add(new Label((String) PublicationGlobalizationUtil.globalize(
                "publications.ui.series.editship.selectEditors").localize()));
        m_itemSearch = new ItemSearchWidget(
                ITEM_SEARCH,
                ContentType.findByAssociatedObjectType(GenericPerson.class.
                getName()));
        add(m_itemSearch);

        add(new Label((String) PublicationGlobalizationUtil.globalize(
                "publications.ui.series.editship.from").localize()));
        ParameterModel fromParam = new DateParameter(EditshipCollection.FROM);
        com.arsdigita.bebop.form.Date from = new com.arsdigita.bebop.form.Date(
                fromParam);
        Calendar today = new GregorianCalendar();
        from.setYearRange(1900, today.get(Calendar.YEAR));
        add(from);

        add(new Label((String) PublicationGlobalizationUtil.globalize(
                "publications.ui.series.editship.to").localize()));
        ParameterModel toParam = new DateParameter(EditshipCollection.TO);
        com.arsdigita.bebop.form.Date to = new com.arsdigita.bebop.form.Date(
                toParam);
        to.setYearRange(1900, today.get(Calendar.YEAR));
        add(to);
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
        Series series =
               (Series) getItemSelectionModel().getSelectedObject(state);

        if (!(this.getSaveCancelSection().
                getCancelButton().isSelected(state))) {
            series.addEditor((GenericPerson) data.get(ITEM_SEARCH),
                    (Date) data.get(EditshipCollection.FROM),
                    (Date) data.get(EditshipCollection.TO));
        }

        init(fse);

    }
}
