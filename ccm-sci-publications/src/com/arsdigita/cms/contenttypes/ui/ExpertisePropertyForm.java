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
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.IntegerParameter;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.ContentType;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.Expertise;
import com.arsdigita.cms.contenttypes.GenericOrganizationalUnit;
import com.arsdigita.cms.ui.ItemSearchWidget;

/**
 *
 * @author Jens Pelzetter
 */
public class ExpertisePropertyForm
        extends PublicationPropertyForm
        implements FormInitListener,
                   FormProcessListener,
                   FormSubmissionListener {

    private ExpertisePropertiesStep m_step;
    private ItemSearchWidget m_itemSearchOrga;
    private final String ITEM_SEARCH_ORGA = "organization";
    private ItemSearchWidget m_itemSearchOrderer;
    private final String ITEM_SEARCH_ORDERER = "orderer";
    public static final String ID = "ExpertiseEdit";

    public ExpertisePropertyForm(ItemSelectionModel itemModel) {
        this(itemModel, null);
    }

    public ExpertisePropertyForm(ItemSelectionModel itemModel,
                                 ExpertisePropertiesStep step) {
        super(itemModel, step);
        m_step = step;
        addSubmissionListener(this);
    }

    @Override
    protected void addWidgets() {
        super.addWidgets();

        add(new Label((String) PublicationGlobalizationUtil.globalize(
                "publications.ui.expertise.place").localize()));
        ParameterModel placeParam = new StringParameter(Expertise.PLACE);
        TextField place = new TextField(placeParam);
        add(place);

        add(new Label((String) PublicationGlobalizationUtil.globalize(
                "publications.ui.expertise.organization").localize()));
        m_itemSearchOrga = new ItemSearchWidget(ITEM_SEARCH_ORGA,
                                                ContentType.
                findByAssociatedObjectType(
                GenericOrganizationalUnit.class.getName()));
        add(m_itemSearchOrga);

        add(new Label((String) PublicationGlobalizationUtil.globalize(
                "publications.ui.expertise.number_of_pages").localize()));
        ParameterModel numberOfPagesParam =
                       new IntegerParameter(Expertise.NUMBER_OF_PAGES);
        TextField numberOfPages = new TextField(numberOfPagesParam);
        add(numberOfPages);     

        add(new Label((String) PublicationGlobalizationUtil.globalize(
                "publications.ui.expertise.orderer").localize()));
        m_itemSearchOrderer = new ItemSearchWidget(ITEM_SEARCH_ORDERER,
                                                   ContentType.
                findByAssociatedObjectType(
                GenericOrganizationalUnit.class.getName()));
        add(m_itemSearchOrderer);
    }

    @Override
    public void init(FormSectionEvent fse) throws FormProcessException {
        super.init(fse);

        FormData data = fse.getFormData();
        Expertise expertise = (Expertise) initBasicWidgets(fse);

        data.put(Expertise.PLACE, expertise.getPlace());
        data.put(ITEM_SEARCH_ORGA, expertise.getOrganization());
        data.put(Expertise.NUMBER_OF_PAGES, expertise.getNumberOfPages());       
        data.put(ITEM_SEARCH_ORDERER, expertise.getOrderer());
    }

    @Override
    public void process(FormSectionEvent fse) throws FormProcessException {
        super.process(fse);

        FormData data = fse.getFormData();
        Expertise expertise = (Expertise) processBasicWidgets(fse);

        if ((expertise != null) && getSaveCancelSection().getSaveButton().
                isSelected(fse.getPageState())) {
            expertise.setPlace((String) data.get(Expertise.PLACE));
            expertise.setOrganization(
                    (GenericOrganizationalUnit) data.get(ITEM_SEARCH_ORGA));
            expertise.setNumberOfPages(
                    (Integer) data.get(Expertise.NUMBER_OF_PAGES));         
            expertise.setOrderer(
                    (GenericOrganizationalUnit) data.get(ITEM_SEARCH_ORDERER));

            expertise.save();
           
        }
    }
}
