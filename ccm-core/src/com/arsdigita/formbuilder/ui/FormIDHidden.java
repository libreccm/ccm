/*
 * Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.formbuilder.ui;

import com.arsdigita.bebop.form.Hidden;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.event.PrintEvent;


/**
 * This hidden will grab the form id from the URL and
 * put it in a Hidden widget to be added to forms so
 * that the form id is not lost from request to request.
 *
 * @author Peter Marklund
 * @version $Id: FormIDHidden.java 287 2005-02-22 00:29:02Z sskracic $
 *
 */
public class FormIDHidden extends Hidden {

    public static final String versionId = "$Id: FormIDHidden.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    public FormIDHidden() {
        super("form_id");

        try {
            addPrintListener(
                             new PrintListener() {
                                 public void prepare(PrintEvent printEvent) {
                                     Hidden hidden = (Hidden)printEvent.getTarget();

                                     // Get the form id from the URL if possible
                                     String urlFormID = printEvent.getPageState().getRequest().getParameter("form_id");

                                     // Set the form id if there was any in the URL
                                     if (urlFormID != null && !urlFormID.equals("")) {
                                         hidden.setValue(printEvent.getPageState(), urlFormID);
                                     }
                                 }
                             }

                             );
        } catch (java.util.TooManyListenersException e) {
            throw new com.arsdigita.util.UncheckedWrapperException(e);
        }
    }
}
