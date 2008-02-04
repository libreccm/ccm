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


import com.arsdigita.formbuilder.ui.editors.CheckboxGroupForm;

import com.arsdigita.bebop.SingleSelectionModel;


import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.FormData;

/**
 * The page for editing properties of surveys. We are overriding the
 * CheckboxGroupEditor to remove the HTML name that we don't want to appear in
 * the Simple Survey application.
 *
 * @author <a href="mailto:pmarklun@arsdigita.com">Peter Marklund</a>
 * @version $Id: CheckboxWidgetForm.java 755 2005-09-02 13:42:47Z sskracic $
 */
public class CheckboxWidgetForm extends CheckboxGroupForm {

    public CheckboxWidgetForm(String name,
			 SingleSelectionModel form,
			 SingleSelectionModel control) {
        super(name, form, control);
    }

    /**
     * To produce a unique HTML name for the widgets we use their 
     * ACS object id
     */
    protected String getName(PageState pageState, FormData formData) {

        return getWidget(pageState).getID().toString();
    }

    protected boolean showName() {

        return false;
    }

    protected boolean showDescription() {

        return false;
    }

    protected String getLabelText() {
	return "Question Text:";
    }

    protected boolean wantLabelMultiline() {
	return true;
    }
}
