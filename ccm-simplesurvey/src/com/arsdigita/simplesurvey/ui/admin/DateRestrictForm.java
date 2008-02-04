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
package com.arsdigita.simplesurvey.ui.admin;


import com.arsdigita.simplesurvey.util.GlobalizationUtil ; 

import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.form.Date;
import com.arsdigita.bebop.form.Submit;

import java.util.GregorianCalendar;
import java.util.Calendar;


/**
 * A FormSection with a start and an end date intended for restricting
 * data queries.
 *
 * @author <a href="mailto:pmarklun@arsdigita.com">Peter Marklund</a>
 * @version $Id: DateRestrictForm.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class DateRestrictForm extends Form {

    private Date m_startDate = new Date("startDate");
    private Date m_endDate = new Date("endDate");

    public DateRestrictForm(String label) {
	super("dateRestrictForm", new ColumnPanel(5));

        Calendar startCalendar = new GregorianCalendar();        
        startCalendar.add(Calendar.DATE, -5);
        java.util.Date startDate = startCalendar.getTime();
	m_startDate.setDefaultValue(startDate);

        Calendar endCalendar = new GregorianCalendar();        
        endCalendar.add(Calendar.DATE, 1);
        java.util.Date endDate = endCalendar.getTime();
	m_endDate.setDefaultValue(endDate);

	add(new Label(label + " between"));
	add(m_startDate);
	add(new Label(GlobalizationUtil.globalize("simplesurvey.ui.admin.and")));
	add(m_endDate);
	add(new Submit("submit", "Show"));
    }

    public java.util.Date getStartDate(PageState pageState) {
	
	return (java.util.Date)m_startDate.getValue(pageState);
    }

    public java.util.Date getEndDate(PageState pageState) {

	return (java.util.Date)m_endDate.getValue(pageState);
    }
}
