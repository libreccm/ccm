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

import com.arsdigita.bebop.SingleSelectionModel;
import com.arsdigita.bebop.FormData;
import com.arsdigita.formbuilder.PersistentOption;


/**
 * We are overriding the
 * OptionEditor to remove the HTML name that we don't want to appear in
 * the Simple Survey application.
 *
 * @author <a href="mailto:pmarklun@arsdigita.com">Peter Marklund</a>
 * @version $Id: OptionEditor.java 287 2005-02-22 00:29:02Z sskracic $
 */
public abstract class OptionEditor extends com.arsdigita.formbuilder.ui.editors.OptionEditor {

    public OptionEditor(SingleSelectionModel control) {
        super(control);
    }

    protected boolean showOptionValue() {
        return false;
    }

    protected String getOptionName(FormData formData, PersistentOption option) {

        String label = formData.getString("opt_label").trim();
        label.replace(' ', '_');

        return label;
    }
}

