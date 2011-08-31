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
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
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

    /*private static final Logger logger =
                                Logger.getLogger(SciMemberPropertyForm.class);
    private SciMemberPropertiesStep m_step;
    private CheckboxGroup m_associated;
    private CheckboxGroup m_former;*/

    public SciMemberPropertyForm(ItemSelectionModel itemModel) {
        this(itemModel, null);
    }

    public SciMemberPropertyForm(ItemSelectionModel itemModel,
                                 SciMemberPropertiesStep step) {
        super(itemModel);
        //m_step = step;
    }

    @Override
    protected void addWidgets() {
        super.addWidgets();     
    }

    @Override
    public void init(FormSectionEvent fse) {
        super.init(fse);

        FormData data = fse.getFormData();
        SciMember member = (SciMember) super.initBasicWidgets(fse);        
    }

    @Override
    public void process(FormSectionEvent fse) {
        super.process(fse);

        PageState state = fse.getPageState();
        SciMember member = (SciMember) super.processBasicWidgets(fse);

        if ((member != null) && getSaveCancelSection().getSaveButton().
                isSelected(fse.getPageState())) {
           
            member.save();

            init(fse);
        }
    }
}
