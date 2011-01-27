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
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.IntegerParameter;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.Publication;
import com.arsdigita.cms.ui.authoring.BasicPageForm;
import org.apache.log4j.Logger;

/**
 *
 * @author Jens Pelzetter
 */
public class PublicationPropertyForm
        extends BasicPageForm
        implements FormProcessListener,
                   FormInitListener,
                   FormSubmissionListener {

    private static final Logger s_log = Logger.getLogger(
            PublicationPropertyForm.class);
    private PublicationPropertiesStep m_step;
    public static final String ID = "Publication_edit";

    public PublicationPropertyForm(ItemSelectionModel itemModel) {
        this(itemModel, null);
    }

    public PublicationPropertyForm(ItemSelectionModel itemModel,
                                   PublicationPropertiesStep step) {
        super(ID, itemModel);
        m_step = step;
        addSubmissionListener(this);
    }

    @Override
    protected void addWidgets() {
        super.addWidgets();

        add(new Label((String) PublicationGlobalizationUtil.globalize(
                "publications.ui.publication.title").localize()));
        ParameterModel titleParam = new StringParameter(Publication.TITLE);
        TextField title = new TextField(titleParam);
        add(title);

        add(new Label((String) PublicationGlobalizationUtil.globalize(
                "publications.ui.publication.year_of_publication").localize()));
        ParameterModel yearOfPublicationParam = new IntegerParameter(
                Publication.YEAR_OF_PUBLICATION);
        TextField yearOfPublication = new TextField(yearOfPublicationParam);
        yearOfPublication.setMaxLength(4);
        add(yearOfPublication);

        add(new Label((String) PublicationGlobalizationUtil.globalize(
                "publications.ui.publication.abstract").localize()));
        ParameterModel abstractParam = new StringParameter(Publication.ABSTRACT);
        TextArea abstractArea = new TextArea(abstractParam);
        abstractArea.setCols(60);
        abstractArea.setRows(18);
        add(abstractArea);

        add(new Label((String) PublicationGlobalizationUtil.globalize(
                "publications.ui.publication.misc").localize()));
        ParameterModel miscParam = new StringParameter(Publication.MISC);
        TextArea misc = new TextArea(miscParam);
        misc.setCols(60);
        misc.setRows(18);
        add(misc);
    }

    @Override
    public void init(FormSectionEvent fse) throws FormProcessException {
        FormData data = fse.getFormData();
        Publication publication = (Publication) super.initBasicWidgets(fse);

        data.put(Publication.TITLE, publication.getTitle());
        data.put(Publication.YEAR_OF_PUBLICATION, publication.
                getYearOfPublication());
        data.put(Publication.ABSTRACT, publication.getAbstract());
        data.put(Publication.MISC, publication.getMisc());
    }

    @Override
    public void process(FormSectionEvent fse) throws FormProcessException {
        FormData data = fse.getFormData();
        Publication publication = (Publication) super.processBasicWidgets(fse);

        if ((publication != null) && getSaveCancelSection().getSaveButton().
                isSelected(fse.getPageState())) {
            publication.setTitle((String) data.get(Publication.TITLE));
            publication.setYearOfPublication((Integer) data.get(
                    Publication.YEAR_OF_PUBLICATION));
            publication.setAbstract((String) data.get(Publication.ABSTRACT));
            publication.setMisc((String) data.get(Publication.MISC));

            publication.save();
        }       
    }

    @Override
    public void submitted(FormSectionEvent fse) throws FormProcessException {
        if ((m_step != null) && getSaveCancelSection().getCancelButton().
                isSelected(fse.getPageState())) {
            m_step.cancelStreamlinedCreation(fse.getPageState());
        }
    }
}
