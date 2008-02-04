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







import com.arsdigita.domain.DataObjectNotFoundException;

import com.arsdigita.formbuilder.HiddenIDGenerator;


import java.math.BigDecimal;
import com.arsdigita.bebop.SingleSelectionModel;
import com.arsdigita.formbuilder.PersistentWidget;


public class HiddenIDGeneratorForm extends WidgetForm {
    public HiddenIDGeneratorForm(String name,
                                 SingleSelectionModel form,
                                 SingleSelectionModel control) {
        super(name, form, control);
    }


    protected PersistentWidget getWidget() {
        return new HiddenIDGenerator();
    }

    protected PersistentWidget getWidget(BigDecimal id)
        throws DataObjectNotFoundException {

        return new HiddenIDGenerator(id);
    }

    /**
     *  This determines whether or not the "required value" radio group
     *  is part of the form.  This returns true and should be overridden
     *  by fields where it does not make sense to ask.  For instance,
     *  when the widget is a hidden field then asking if it is required
     *  or not does not make any logical sense so those widgets should
     *  return false.
     *
     *  This will always return the same value for a given widget no matter
     *  what state the widget is in.
     */
    protected boolean includeRequiredRadioGroup() {
        return false;
    } 
}
