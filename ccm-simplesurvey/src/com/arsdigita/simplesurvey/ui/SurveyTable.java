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


import com.arsdigita.simplesurvey.ui.admin.AdminSurveyTableModel;

// Every item in the Table will have links
import com.arsdigita.bebop.Link;

import java.util.Date;

import com.arsdigita.persistence.Filter;
import com.arsdigita.kernel.ACSObject;
import com.arsdigita.kernel.PackageInstance;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.table.TableModelBuilder;
import com.arsdigita.bebop.table.TableModel;
import com.arsdigita.bebop.table.TableCellRenderer;

import com.arsdigita.util.LockableImpl;
import com.arsdigita.formbuilder.PersistentForm;

import com.arsdigita.simplesurvey.SimpleSurveyUtil;

import com.arsdigita.bebop.Component;

import com.arsdigita.simplesurvey.Survey;
import com.arsdigita.simplesurvey.SurveyCollection;

import org.apache.log4j.Logger;
import com.arsdigita.web.URL;
import com.arsdigita.bebop.PageState;


/**
 * Tables all Simple Surveys in the system.
 *
 * @author <a href="mailto:pmarklun@arsdigita.com">Peter Marklund</a>
 * @version $Id: SurveyTable.java 755 2005-09-02 13:42:47Z sskracic $
 */
public class SurveyTable extends Table {

    private static final Logger s_log =
        Logger.getLogger(SurveyTable.class.getName());
 
    public SurveyTable(Class surveyClass) {
        super(new SurveyTableModelBuilder(surveyClass), new String[] {""});

        setDefaultCellRenderer(new SurveyCellRenderer());
     }

    /**
     *
     * @param surveyClass The Survey class name must be identical to the corresponding
     *        base data object type
     */
    private static class SurveyTableModelBuilder extends LockableImpl 
	implements TableModelBuilder {

	private Class m_surveyClass;

	public SurveyTableModelBuilder(Class surveyClass) {
	    m_surveyClass = surveyClass;
	}
	
	public TableModel makeModel(Table l, PageState pageState) {
	    PackageInstance pack = SimpleSurveyUtil.getPackageInstance(pageState);
	    SurveyCollection surveys = 
		Survey.retrieveByPackage(pack);
	    
	    surveys.addEqualsFilter(ACSObject.DEFAULT_DOMAIN_CLASS, 
				    m_surveyClass.getName());
	    
	    Date currentDate = new Date();
	    Filter startFilter = surveys.addFilter("startDate < :startDate");
	    startFilter.set("startDate", currentDate);
	    Filter endFilter = surveys.addFilter("endDate > :endDate");
	    endFilter.set("endDate", currentDate);
	    
	    return new AdminSurveyTableModel(surveys);
	}
    }

    private class SurveyCellRenderer implements TableCellRenderer {
	public Component getComponent(Table table, PageState ps, Object value,
				      boolean isSelected, Object key, 
				      int row, int column) {
	    Survey survey = (Survey)value;
	    PersistentForm form = survey.getForm();
	    String adminName = form.getAdminName();
	    //s_log.warn("AdminName=" + adminName);
	    //s_log.warn("URI=" + ps.getRequestURI());
	    if (column == 0) {
		
       		
		String uri = ps.getRequestURI();
		String node = URL.getDispatcherPath();
		if ( uri.startsWith(node)) {
		    uri = uri.substring(node.length(), uri.length());
		}
		Link l =  new Link(adminName, uri);
		l.setVar("survey_id", survey.getID().toString());
		return l;
	    } 
	    
	    throw new RuntimeException("column out of bounds " + column);
	}
    }    
}
