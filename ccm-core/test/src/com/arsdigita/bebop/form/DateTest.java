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
package com.arsdigita.bebop.form;

import com.arsdigita.bebop.ComponentTestHarness;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import java.util.Calendar;

/**
 * Regression tests for the text field widget
 *
 * @version $Revision: #11 $ $Date: 2004/08/16 $
 */

public class DateTest extends Fixture {

    public DateTest (String id) {
        super(id);
    }

    /**
     *  Test a Date
     */
    public void testDate() {
        //testWidget(new Date("birth"), "one");
    }

    /**
     * Test to make sure date field works after we call setValue
     * on the pageState.  Corresponds to SDM #196277.
     */
    public void testDayField() throws Exception {
        final Date dt = new Date("dt");
        m_form.add(dt);
        // NOTE:
        // this is convoluted and uses a FormInitListener instead of a
        // PrintListener because I couldn't get dt.setValue() to have
        // any effect in a print listener.  Oddly, the print-listener
        // version that calls dt.setValue(ps, ...); works fine in a test
        // page when there's a real request around.
        m_form.addInitListener(new FormInitListener() {
                public void init(FormSectionEvent evt) {
                    Calendar cal = Calendar.getInstance();
                    cal.set(2004, Calendar.FEBRUARY, 29);
                    evt.getFormData().put(dt.getName(), cal.getTime());
                }
            });
        final ComponentTestHarness harness = new ComponentTestHarness(m_page);

        final String docXML = harness.generateXML().toString();
        assertTrue(docXML.indexOf("value=\"29\"") >= 0);
    }
}
