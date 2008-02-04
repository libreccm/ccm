/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.simplesurvey.ui;

import com.arsdigita.formbuilder.PersistentForm;

import com.arsdigita.simplesurvey.Poll;
import com.arsdigita.simplesurvey.ui.SurveyProcessListener;
import com.arsdigita.bebop.BlockStylable;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.MetaForm;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.form.Hidden;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.parameters.BigDecimalParameter;


/**
 * Generates a random event
 *
 * @author <a href="mailto:scott@arsdigita.com">Scott Seago</a>
 * @version $Id: RecentPollForm.java 759 2005-09-02 15:25:32Z sskracic $
 *
 */

public class RecentPollForm extends MetaForm {

    
    public RecentPollForm() {
	super("RecentPollForm");
    }
    

    public Form buildForm(PageState pageState) {

	Form form = new Form("RecentPollForm");

	String categoryID = pageState.getRequest().getParameter("categoryID");
	
	if ( categoryID == null ) {	
	    Poll poll = Poll.getMostRecentPoll();
	    
	    // Create the form
	    if ( poll != null ) {  
		PersistentForm persistentForm = poll.getForm();
		form = (Form)persistentForm.createComponent();
		form.setIdAttr("recentPoll");
		
		// Add a submit button
		form.add(new Submit("submit"), BlockStylable.CENTER);
		// Add a hidden input with the poll id
		Hidden hiddenID = new Hidden(new BigDecimalParameter(SurveyProcessListener.SURVEY_ID_NAME));
		hiddenID.setDefaultValue(poll.getID());
		form.add(hiddenID);
		// Add the Simple Poll process listener
		form.addProcessListener(new SurveyProcessListener());
	    }
	}
	return form;
    }
}
