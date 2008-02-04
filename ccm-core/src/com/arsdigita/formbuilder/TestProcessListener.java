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
package com.arsdigita.formbuilder;


import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.FormData;

// logging
import org.apache.log4j.Logger;


/**
 * This process listener is used to test submission of persistent
 * forms created with the Form Builder administration UI. The listener
 * simply logs all parameters submitted by the form.
 *
 * @author Peter Marklund
 * @version $Id: TestProcessListener.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class TestProcessListener implements FormProcessListener {

    public static final String versionId = "$Id: TestProcessListener.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    // Logging
    private static final Logger s_log =
        Logger.getLogger(TestProcessListener.class);

    public void process(FormSectionEvent formEvent) {
        FormData formData = formEvent.getFormData();

        java.util.Iterator parameterIter = formData.keySet().iterator();
        while (parameterIter.hasNext()) {
            String parameter = (String)parameterIter.next();
            Object value = formData.get(parameter);

            value = value == null ? "" : value;

            s_log.debug("Form submitted parameter " + parameter +
                        " with value " + value.toString() +
                        " and class " + value.getClass().getName());
        }
    }
}
