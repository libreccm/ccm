/*
 * Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.formbuilder.ui.editors;





import com.arsdigita.bebop.form.SingleSelect;

import com.arsdigita.bebop.parameters.ArrayParameter;

import com.arsdigita.domain.DataObjectNotFoundException;

import com.arsdigita.formbuilder.PersistentSingleSelect;



import java.math.BigDecimal;

import com.arsdigita.bebop.SingleSelectionModel;
import com.arsdigita.bebop.form.OptionGroup;
import com.arsdigita.formbuilder.PersistentWidget;


public class SingleSelectForm extends OptionGroupForm {
    public SingleSelectForm(String name,
                            SingleSelectionModel form,
                            SingleSelectionModel control) {
        super(name, form, control);
    }

    protected OptionGroup getOptionGroup(String name) {
        return new SingleSelect(new ArrayParameter(name));
    }

    protected PersistentWidget getWidget() {
        return new PersistentSingleSelect();
    }

    protected PersistentWidget getWidget(BigDecimal id)
        throws DataObjectNotFoundException {
        return new PersistentSingleSelect(id);
    }
}
