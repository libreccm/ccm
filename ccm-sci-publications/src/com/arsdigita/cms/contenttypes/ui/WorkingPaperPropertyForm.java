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

import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.cms.ItemSelectionModel;

/**
 *
 * @author Jens Pelzetter
 */
public class WorkingPaperPropertyForm
        extends UnPublishedPropertyForm
        implements FormInitListener,
                   FormProcessListener,
                   FormSubmissionListener {

    private WorkingPaperPropertiesStep m_step;
    public static final String ID = "WorkingPaperEdit";

    public WorkingPaperPropertyForm(ItemSelectionModel itemModel) {
        this(itemModel, null);
    }

    public WorkingPaperPropertyForm(ItemSelectionModel itemModel,
                                    WorkingPaperPropertiesStep step) {
        super(itemModel, step);
        m_step = step;
        addSubmissionListener(this);
    }

    @Override
    protected void addWidgets() {
        super.addWidgets();
    }

    @Override
    public void init(FormSectionEvent fse) throws FormProcessException {
        super.init(fse);
    }

    @Override
    public void process(FormSectionEvent fse) throws FormProcessException {
        super.process(fse);
    }
}
