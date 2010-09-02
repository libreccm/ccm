/*
 * Copyright (c) 2010 Jens Pelzetter,
for the Center of Social Politics of the University of Bremen
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
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.GenericOrganizationalUnit;
import com.arsdigita.cms.contenttypes.util.ContenttypesGlobalizationUtil;
import com.arsdigita.cms.ui.authoring.BasicPageForm;
import org.apache.log4j.Logger;

/**
 * Form for editing the basic properties of an GenericOrganizationalUnit.
 *
 * @author Jens Pelzetter
 */
public class GenericOrganizationalUnitPropertyForm
        extends BasicPageForm
        implements FormProcessListener,
                   FormInitListener,
                   FormSubmissionListener {

    private final static Logger s_log = Logger.getLogger(
            GenericOrganizationalUnitPropertyForm.class);
    private GenericOrganizationalUnitPropertiesStep m_step;
    //public static final String NAME = GenericOrganizationalUnit.NAME;
    //public static final String ORGAUNIT_NAME = GenericOrganizationalUnit.ORGAUNIT_NAME;
    public static final String ADDENDUM = GenericOrganizationalUnit.ADDENDUM;
    public static final String ID = "GenericOrgaUnit_edit";

    public GenericOrganizationalUnitPropertyForm(ItemSelectionModel itemModel) {
        this(itemModel, null);
    }

    public GenericOrganizationalUnitPropertyForm(ItemSelectionModel itemModel,
                                                 GenericOrganizationalUnitPropertiesStep step) {
        super(ID, itemModel);
        m_step = step;
        addSubmissionListener(this);
    }

    @Override
    protected void addWidgets() {
        super.addWidgets();

        add(new Label(
                (String) ContenttypesGlobalizationUtil.globalize(
                "cms.contenttypes.ui.genericorganunit.name").localize()));
        ParameterModel nameParam = new StringParameter(NAME);
        TextField name = new TextField(nameParam);
        add(name);

        add(new Label(
                (String) ContenttypesGlobalizationUtil.globalize(
                "cms.contenttypes.ui.genericorgaunit.addendum").localize()));
        ParameterModel addendumParam = new StringParameter(ADDENDUM);
        TextField addendum = new TextField(addendumParam);
        add(addendum);

    }

    @Override
    public void submitted(FormSectionEvent fse) throws FormProcessException {
        if ((m_step != null)
            && getSaveCancelSection().getCancelButton().isSelected(fse.
                getPageState())) {
            m_step.cancelStreamlinedCreation(fse.getPageState());
        }
    }

    @Override
    public void init(FormSectionEvent fse) throws FormProcessException {
        FormData data = fse.getFormData();
        GenericOrganizationalUnit orgaunit =
                                  (GenericOrganizationalUnit) super.
                initBasicWidgets(fse);

        data.put(NAME, orgaunit.getName());
        data.put(ADDENDUM, orgaunit.getAddendum());
    }

    @Override
    public void process(FormSectionEvent fse) throws FormProcessException {
        FormData data = fse.getFormData();

        GenericOrganizationalUnit orgaunit =
                                  (GenericOrganizationalUnit) super.
                processBasicWidgets(fse);

        if ((orgaunit != null)
            && getSaveCancelSection().getSaveButton().isSelected(fse.
                getPageState())) {
            orgaunit.setName((String) data.get(NAME));
            orgaunit.setAddendum((String) data.get(ADDENDUM));

            orgaunit.save();
        }

        if (m_step != null) {
            m_step.maybeForwardToNextStep(fse.getPageState());
        }
    }
}
