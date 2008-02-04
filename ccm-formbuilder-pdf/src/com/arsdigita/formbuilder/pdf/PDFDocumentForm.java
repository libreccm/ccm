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
 * New Class Created For PDF Document
 *
 *  Date          Author
 *  23 Nec 2004   CS Gupta
 */

package com.arsdigita.formbuilder.pdf;

import com.arsdigita.formbuilder.util.GlobalizationUtil ;
import com.arsdigita.formbuilder.pdf.PDFListener;

import com.arsdigita.bebop.FormData;
import com.arsdigita.formbuilder.ui.editors.ProcessListenerForm;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.FormSection;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.form.TextField;
import java.math.BigDecimal;
import com.arsdigita.bebop.SingleSelectionModel;
import com.arsdigita.formbuilder.PersistentProcessListener;

import org.apache.log4j.Logger;

public class PDFDocumentForm extends ProcessListenerForm {
    private TextField m_to;
    private TextField m_subject;
    private SingleSelectionModel m_form;
    public static final Logger s_log = Logger.getLogger(PDFDocumentForm.class);

    public PDFDocumentForm(String name,
                           SingleSelectionModel form,
                           SingleSelectionModel action) {
        super(name, form, action);

        m_form=form;

    }

    protected void addWidgets(FormSection section) {
        super.addWidgets(section);
     

    }

    protected PersistentProcessListener getProcessListener() {
        return new PDFListener();
    }

    protected PersistentProcessListener getProcessListener(BigDecimal id) {
        return new PDFListener(id);
    }

    protected void initWidgets(FormSectionEvent e,
                               PersistentProcessListener listener)
        throws FormProcessException {
        super.initWidgets(e, listener);
        PDFListener l = (PDFListener)listener;
        PageState state = e.getPageState();

   }

    protected void processWidgets(FormSectionEvent e,
                                  PersistentProcessListener listener)
        throws FormProcessException {
        super.processWidgets(e, listener);
        PDFListener l = (PDFListener)listener;
        FormData data = e.getFormData();


    }
}



