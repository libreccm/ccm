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
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.SciAuthor;

/**
 * Form for editing the basic properties of a {@link SciAuthor}. This form
 * extends the form for editing the basic properties of {@link GenericPerson}.
 *
 * @author Jens Pelzetter
 * @see SciAuthor
 * @see GenericPerson
 * @see GenericPersonPropertyForm
 */
public class SciAuthorPropertyForm
        extends GenericPersonPropertyForm
        implements FormInitListener,
                   FormSubmissionListener {

    public SciAuthorPropertyForm(ItemSelectionModel itemModel) {
        this(itemModel, null);
    }

    public SciAuthorPropertyForm(ItemSelectionModel itemModel,
                                 SciAuthorPropertiesStep step) {
        super(itemModel);
    }

    @Override
    protected void addWidgets() {
        super.addWidgets();
    }

    @Override
    public void init(FormSectionEvent fse) {
        super.init(fse);

        super.initBasicWidgets(fse);
    }

    @Override
    public void process(FormSectionEvent fse) {
        super.process(fse);

        SciAuthor author = (SciAuthor) super.processBasicWidgets(fse);
        if ((author != null) && getSaveCancelSection().getSaveButton().
                isSelected(fse.getPageState())) {
            author.save();

            init(fse);
        }
    }
}
