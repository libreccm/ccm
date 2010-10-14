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
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.form.CheckboxGroup;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.parameters.ArrayParameter;
import com.arsdigita.bebop.parameters.BooleanParameter;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.GenericPerson;
import com.arsdigita.cms.contenttypes.SciMember;

/**
 * Form for editing the basic properties of a {@link SciMember}. This form
 * extends the form for editing the basic properties of {@link GenericPerson}.
 *
 * @author Jens Pelzetter
 * @see SciMember
 * @see GenericPerson
 * @see GenericPersonPropertyForm
 */
public class SciMemberPropertyForm
        extends GenericPersonPropertyForm
        implements FormInitListener,
                   FormSubmissionListener {

    private SciMemberPropertiesStep m_step;

    public SciMemberPropertyForm(ItemSelectionModel itemModel) {
        this(itemModel, null);
    }

    public SciMemberPropertyForm(ItemSelectionModel itemModel,
                                 SciMemberPropertiesStep step) {
        super(itemModel);
        m_step = step;
    }

    @Override
    protected void addWidgets() {
        super.addWidgets();

        add(new Label(SciOrganizationGlobalizationUtil.globalize(
                "sciorganization.ui.member.associatedMember")));
        ParameterModel isAssociated = new BooleanParameter(
                SciMember.ASSOCIATED_MEBER);
        ArrayParameter associatedParam = new ArrayParameter(isAssociated);
        CheckboxGroup associated = new CheckboxGroup(associatedParam);
        associated.addOption(new Option(""));
        add(associated);

        add(new Label(SciOrganizationGlobalizationUtil.globalize(
                "sciorganization.ui.member.formerMember")));
        ParameterModel isFormer = new BooleanParameter(SciMember.FORMER_MEMBER);
        ArrayParameter formerParam = new ArrayParameter(isFormer);
        CheckboxGroup former = new CheckboxGroup(formerParam);
        former.addOption(new Option(""));
        add(former);
    }

    @Override
    public void init(FormSectionEvent fse) {
        super.init(fse);

        FormData data = fse.getFormData();
        SciMember member = (SciMember) super.initBasicWidgets(fse);

        data.put(SciMember.ASSOCIATED_MEBER, member.isAssociatedMember());
        data.put(SciMember.FORMER_MEMBER, member.isFormerMember());
    }

    @Override
    public void process(FormSectionEvent fse) {
        super.process(fse);

        FormData data = fse.getFormData();
        SciMember member = (SciMember) super.processBasicWidgets(fse);

        if ((member != null) && getSaveCancelSection().getSaveButton().
                isSelected(fse.getPageState())) {
            member.setAssoicatedMember((Boolean) data.get(SciMember.ASSOCIATED_MEBER));
            member.setFormerMember((Boolean) data.get(SciMember.FORMER_MEMBER));

            member.save();

            init(fse);
        }
    }
}
