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
package com.arsdigita.simplesurvey.ui.widgets;

import com.arsdigita.formbuilder.ui.editors.OptionGroupEditor;

import com.arsdigita.bebop.SingleSelectionModel;
import com.arsdigita.formbuilder.PersistentOptionGroup;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.formbuilder.PersistentCheckboxGroup;
import java.math.BigDecimal;

public class CheckboxEditor extends OptionGroupEditor {

    public CheckboxEditor(String name,
			       SingleSelectionModel form,
			       SingleSelectionModel control) {
        super(name, form, control, 
              new CheckboxWidgetForm(name, form, control),
              new OptionEditor(control) {
            protected PersistentOptionGroup getOptionGroup(BigDecimal id)
                throws DataObjectNotFoundException {
                return new PersistentCheckboxGroup(id);
            }
        });

    }
}

