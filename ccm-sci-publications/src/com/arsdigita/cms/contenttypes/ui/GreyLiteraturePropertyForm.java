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
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.GreyLiterature;

/**
 *
 * @author Jens Pelzetter
 */
public class GreyLiteraturePropertyForm
        extends UnPublishedPropertyForm
        implements FormInitListener,
                   FormProcessListener,
                   FormSubmissionListener {

    private GreyLiteraturePropertiesStep m_step;
    public static final String ID = "GreyLiteratureEdit";

    public GreyLiteraturePropertyForm(ItemSelectionModel itemModel) {
        this(itemModel, null);
    }

    public GreyLiteraturePropertyForm(ItemSelectionModel itemModel,
                                      GreyLiteraturePropertiesStep step) {
        super(itemModel, step);
        m_step = step;
        addSubmissionListener(this);
    }

    @Override
    protected void addWidgets() {
        super.addWidgets();

        add(new Label((String) PublicationGlobalizationUtil.globalize(
                "publications.ui.greyliterature.pages_from").
                localize()));
        ParameterModel fromParam = new IntegerParameter(
                GreyLiterature.PAGES_FROM);
        TextField pagesFrom = new TextField(fromParam);
        add(pagesFrom);

        add(new Label((String) PublicationGlobalizationUtil.globalize(
                "publications.ui.greyliterature.pages_to").
                localize()));
        ParameterModel toParam = new IntegerParameter(
                GreyLiterature.PAGES_TO);
        TextField pagesTo = new TextField(toParam);
        add(pagesTo);    
    }

    @Override
    public void init(FormSectionEvent fse) throws FormProcessException {
        super.init(fse);

        FormData data = fse.getFormData();
        GreyLiterature grey = (GreyLiterature) initBasicWidgets(fse);

        data.put(GreyLiterature.PAGES_FROM, grey.getPagesFrom());
        data.put(GreyLiterature.PAGES_TO, grey.getPagesTo());       
    }

    @Override
    public void process(FormSectionEvent fse) throws FormProcessException {
        super.process(fse);

        FormData data = fse.getFormData();
        GreyLiterature grey = (GreyLiterature) processBasicWidgets(fse);

        if ((grey != null) && getSaveCancelSection().getSaveButton().isSelected(fse.
                getPageState())) {
            grey.setPagesFrom((Integer) data.get(GreyLiterature.PAGES_FROM));
            grey.setPagesTo((Integer) data.get(GreyLiterature.PAGES_TO));
            
            grey.save();
        }       
    }
}
