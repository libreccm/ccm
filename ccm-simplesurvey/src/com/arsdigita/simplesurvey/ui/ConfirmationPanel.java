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
package com.arsdigita.simplesurvey.ui;

import com.arsdigita.bebop.Label;
import com.arsdigita.ui.UI;


/**
 * A page confirming that a survey response has been submitted.
 *
 * @author <a href="mailto:pmarklun@arsdigita.com">Peter Marklund</a>
 * @version $Id: ConfirmationPanel.java 2164 2011-06-19 20:31:22Z pboy $
 */
public class ConfirmationPanel extends SimpleSurveyPanel {

    private static ConfirmationPanel s_instance;

    private ConfirmationPanel() {
        super("Survey Submission Confirmation");
    }

    protected void addComponentsToPage() {	

	String workspaceURL = UI.getWorkspaceURL() ;
	//  LegacyInitializer.getURL(LegacyInitializer.WORKSPACE_PAGE_KEY);

	Label bodyText = new Label("Your survey response has been submitted. " +
                         "You may return to your <a href=\"/" + workspaceURL +
				         "\">workspace</a>");

	bodyText.setOutputEscaping(false);
	add(bodyText);    
    }

    /**
     * Provide access to the single instance of this page.
     */
    public static ConfirmationPanel instance() {

        if (s_instance == null) {
            s_instance = new ConfirmationPanel();
        }

        return s_instance;
    }

    public String getRelativeURL() {
        
        return "confirm.jsp";
    }
}
