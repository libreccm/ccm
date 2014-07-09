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
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.IntegerParameter;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.InternetArticle;
import com.arsdigita.cms.contenttypes.UnPublished;
import com.arsdigita.cms.ui.ItemSearchWidget;

/**
 *
 * @author Jens Pelzetter
 */
public class UnPublishedPropertyForm
        extends PublicationPropertyForm
        implements FormInitListener,
                   FormProcessListener,
                   FormSubmissionListener {

    private UnPublishedPropertiesStep m_step;
    private ItemSearchWidget m_itemSearch;
    private final String ITEM_SEARCH = "organization";
    public static final String ID = "UnPublishedEdit";

    public UnPublishedPropertyForm(ItemSelectionModel itemModel) {
        this(itemModel, null);
    }

    public UnPublishedPropertyForm(ItemSelectionModel itemModel,
                                   UnPublishedPropertiesStep step) {
        super(itemModel, step);
        m_step = step;
        addSubmissionListener(this);
    }

    @Override
    protected void addWidgets() {

        super.addWidgets();

        ParameterModel placeParam =
                       new StringParameter(InternetArticle.PLACE);
        TextField place = new TextField(placeParam);
        place.setLabel(PublicationGlobalizationUtil.globalize(
                       "publications.ui.unpublished.place"));
        add(place);
       
        ParameterModel numberParam =
                       new StringParameter(UnPublished.NUMBER);
        TextField number = new TextField(numberParam);
        number.setLabel(PublicationGlobalizationUtil.globalize(
                        "publications.ui.unpublished.number"));
        add(number);

        ParameterModel numberOfPagesParam =
                       new IntegerParameter(UnPublished.NUMBER_OF_PAGES);
        TextField numberOfPages = new TextField(numberOfPagesParam);
        numberOfPages.setLabel(PublicationGlobalizationUtil.globalize(
                               "publications.ui.unpublished.number_of_pages"));
        add(numberOfPages);

    }

    @Override
    public void init(FormSectionEvent fse) throws FormProcessException {
        super.init(fse);

        FormData data = fse.getFormData();
        UnPublished unpublished = (UnPublished) initBasicWidgets(fse);

        data.put(UnPublished.PLACE, unpublished.getPlace());       
        data.put(UnPublished.NUMBER, unpublished.getNumber());
        data.put(UnPublished.NUMBER_OF_PAGES, unpublished.getNumberOfPages());
    }

    @Override
    public void process(FormSectionEvent fse) throws FormProcessException {
        super.process(fse);

        FormData data = fse.getFormData();
        UnPublished unpublished = (UnPublished) processBasicWidgets(fse);

        if ((unpublished != null) && getSaveCancelSection().getSaveButton().
                isSelected(fse.getPageState())) {
            unpublished.setPlace((String) data.get(UnPublished.PLACE));       
            unpublished.setNumber((String) data.get(UnPublished.NUMBER));
            unpublished.setNumberOfPages(
                    (Integer) data.get(UnPublished.NUMBER_OF_PAGES));

            unpublished.save();        
        }
    }
}
