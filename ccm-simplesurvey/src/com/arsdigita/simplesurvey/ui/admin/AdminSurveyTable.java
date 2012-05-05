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


// Every item in the Table will have links
import com.arsdigita.bebop.ControlLink;
import com.arsdigita.bebop.Label;

import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.table.TableModelBuilder;
import com.arsdigita.bebop.table.TableModel;
import com.arsdigita.bebop.table.TableCellRenderer;

import com.arsdigita.simplesurvey.SimpleSurveyUtil;

import com.arsdigita.kernel.ACSObject;
import com.arsdigita.kernel.PackageInstance;

import com.arsdigita.bebop.Component;

import com.arsdigita.simplesurvey.Survey;
import com.arsdigita.simplesurvey.SurveyCollection;
import com.arsdigita.formbuilder.PersistentForm;

import org.apache.log4j.Logger;


import com.arsdigita.bebop.PageState;
import com.arsdigita.util.LockableImpl;

/**
 * Tables all Simple Surveys in the system.
 *
 * @author <a href="mailto:pmarklun@arsdigita.com">Peter Marklund</a>
 * @version $Id: AdminSurveyTable.java 2286 2012-03-11 09:14:14Z pboy $
 */
public class AdminSurveyTable extends Table {

    private static final Logger s_log =
        Logger.getLogger(AdminSurveyTable.class.getName());

    private Class m_surveyClass;

    public static final int COL_VIEW = 0;
    public static final int COL_STATUS = 1;
    public static final int COL_RESPONSES = 2;
    public static final int COL_CORRECT_ANSWERS = 3;
    public static final int COL_ANSWER_VALUES = 4;
    public static final int COL_WIDGETS = 5;
    public static final int COL_PROPERTIES = 6;
    public static final int COL_DELETE = 7;
  

    public AdminSurveyTable(Class surveyClass) {
        super(new AdminSurveyTableModelBuilder(surveyClass), 
	      new String[] {"", "", "", "", "", "","", ""});
        
	m_surveyClass = surveyClass;

        setDefaultCellRenderer(new AdminSurveyTableCellRenderer());
    }

    private static class AdminSurveyTableModelBuilder extends LockableImpl 
	implements TableModelBuilder {
	
	private Class m_surveyClass;
	
	public AdminSurveyTableModelBuilder(Class surveyClass) {
	    m_surveyClass = surveyClass; 
	}

	public TableModel makeModel(Table l, PageState pageState) {
	    PackageInstance pack = SimpleSurveyUtil.getPackageInstance(pageState);
	    SurveyCollection surveys = 
		Survey.retrieveAll();
	    
	    surveys.addEqualsFilter(ACSObject.DEFAULT_DOMAIN_CLASS, 
				    m_surveyClass.getName());
	    
	    return new AdminSurveyTableModel(surveys);
	}
    }

    private static class AdminSurveyTableCellRenderer implements TableCellRenderer {
	    public Component getComponent(Table table, PageState state, Object value,
				   boolean isSelected, Object key, 
				   int row, int column) {
		Survey survey = (Survey)value;

		PersistentForm form = survey.getForm();
		String adminName = form.getAdminName();

		if (column == COL_VIEW) {
		    return new ControlLink(adminName);
		} else if (column == COL_STATUS) {
		    Label liveLabel = null;
		    if (survey.isLive()) {
			liveLabel = new Label(GlobalizationUtil.globalize("simplesurvey.ui.admin.active"));
			liveLabel.setClassAttr("strong");
		    } else {
			liveLabel = new Label(GlobalizationUtil.globalize("simplesurvey.ui.admin.inactive"));
		    }

		    return liveLabel;

		} else if (column == COL_RESPONSES) {
		    return new ControlLink( (String) GlobalizationUtil.globalize("simplesurvey.ui.admin.view_responses").localize());
		} else if (column == COL_CORRECT_ANSWERS) {
		    return new ControlLink( (String) GlobalizationUtil.globalize("simplesurvey.ui.admin.set_correct_answers").localize());
		} else if (column == COL_ANSWER_VALUES) {
		    return new ControlLink( (String) GlobalizationUtil.globalize("simplesurvey.ui.admin.set_answer_values").localize());
		} else if (column == COL_WIDGETS) {
		    return new ControlLink( (String) GlobalizationUtil.globalize("simplesurvey.ui.admin.edit_controls").localize());
		} else if (column == COL_PROPERTIES) {
		    return new ControlLink( (String) GlobalizationUtil.globalize("simplesurvey.ui.admin.edit_properties").localize());
		} else if (column == COL_DELETE) {
		    ControlLink link = new ControlLink( (String) GlobalizationUtil.globalize("simplesurvey.ui.admin.delete").localize());
		    link.setConfirmation("Delete this survey");
		    return link;
		}  

		throw new RuntimeException("Unexpected column " + column);
            }
    }
}
