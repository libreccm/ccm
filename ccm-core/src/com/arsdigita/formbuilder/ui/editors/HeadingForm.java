/*
 * Copyright (C) 2004 Red Hat Inc. All Rights Reserved.
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
 * Revision:
 * Method : getTextWidget()
 * Description : New htmlarea component is created inside a textarea.
 */

package com.arsdigita.formbuilder.ui.editors;

import com.arsdigita.formbuilder.PersistentHeading;
import com.arsdigita.formbuilder.PersistentText;
import com.arsdigita.formbuilder.util.GlobalizationUtil ;

import com.arsdigita.bebop.SingleSelectionModel;
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.bebop.form.DHTMLEditor;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.bebop.form.Widget;
import com.arsdigita.globalization.GlobalizedMessage;

import java.math.BigDecimal;

/**
 *  This is the form used to create a persistent heading element
 */
public class HeadingForm extends TextForm {

    public static final String TEXT_ENTRY = "text_entry";

    public HeadingForm(String name,
                      SingleSelectionModel form,
                      SingleSelectionModel control) {
        super(name, form, control);
    }

    /**
     *  this returns the widget that is used in the "addWidgets" method
     *  can provides subclasses with the ability to use any size desired
     */
    protected Widget getTextWidget(StringParameter parameter) {
        //Create htmlarea component inside textarea
        TextArea m_textWidget = new DHTMLEditor(TEXT_ENTRY);
        m_textWidget.setRows(25);
        m_textWidget.setCols(70);
        m_textWidget.setWrap(DHTMLEditor.SOFT);

        return m_textWidget;
    }

    /**
     *  Subclasses can override this so that it will give the correct
     *  name to the text.
     */
    protected GlobalizedMessage getTextTitle() {
        return GlobalizationUtil.globalize
            ("formbuilder.ui.editors.heading_form_title");
    }

    protected PersistentText getPersistentText() {
        return new PersistentHeading();
    }

    protected PersistentText getPersistentText(BigDecimal id) {
        return new PersistentHeading(id);
    }
}
