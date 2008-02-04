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



import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.ActionLink;
import com.arsdigita.bebop.SegmentedPanel;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.event.TableActionEvent;
import com.arsdigita.bebop.event.TableActionListener;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.RequestEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.event.RequestListener;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.bebop.SegmentedPanel;

import com.arsdigita.simplesurvey.Survey;

import com.arsdigita.xml.Element;

import com.arsdigita.simplesurvey.ui.SurveySelectionModel;

import java.math.BigDecimal;

/**
 * The index page of the Simple Survey admin UI.
 *
 * @author <a href="mailto:pmarklun@arsdigita.com">Peter Marklund</a>
 * @version $Id: IndexPanel.java 755 2005-09-02 13:42:47Z sskracic $
 */
public class IndexPanel extends SimpleContainer {

    // Components of the page
    private SegmentedPanel m_panel;
    private AdminSurveyTable m_surveyTable;
    //private AdminSurveyTable m_pollsTable;
    
    private AdminPanel m_adminSurvey;
    //private AdminPanel m_adminPoll;

    private SurveySelectionModel m_survey;
    //private SurveySelectionModel m_poll;
    private BigDecimalParameter m_surveyID;
    private ActionLink m_newSurvey;
    //private ActionLink m_newPoll;
    
    
    private static org.apache.log4j.Logger s_log = 
        org.apache.log4j.Logger.getLogger(IndexPanel.class.getName());

    public IndexPanel(SurveySelectionModel survey, SurveySelectionModel poll) {
	m_survey = survey;
        //m_poll = poll;

        m_adminSurvey = new AdminPanel(m_survey,
				       Survey.class);
        //m_adminPoll = new AdminPanel(m_poll,	     Poll.class);

	m_adminSurvey.addCompletionListener(new ResetVisibilityListener(m_survey));
       //m_adminPoll.addCompletionListener(new ResetVisibilityListener(m_poll));

        add(m_adminSurvey);
       // add(m_adminPoll);

	
	m_newSurvey = new ActionLink( (String) GlobalizationUtil.globalize("simplesurvey.ui.admin.add_new_survey").localize());
	m_newSurvey.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    PageState state = e.getPageState();
		    m_survey.clearSelection(state);
		    m_adminSurvey.setDisplayMode(state,
						 AdminPanel.MODE_PROPERTIES);

		    m_adminSurvey.setVisible(state, true);
		    // m_adminPoll.setVisible(state, false);
		    m_panel.setVisible(state, false);
		}
	    });
    //m_newPoll = new ActionLink( (String) GlobalizationUtil.globalize("simplesurvey.ui.admin.add_new_poll").localize());
    /*	m_newPoll.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    PageState state = e.getPageState();
		    m_poll.clearSelection(state);
		    m_adminPoll.setDisplayMode(state,
					       AdminPanel.MODE_PROPERTIES);

		    m_adminSurvey.setVisible(state, false);
		    m_adminPoll.setVisible(state, true);
		    m_panel.setVisible(state, false);
		}
	    });
    */
	m_panel = new SegmentedPanel();
	
	Label surveyLabel = new Label(GlobalizationUtil.globalize("simplesurvey.ui.admin.surveys"));
        m_surveyTable = new AdminSurveyTable(Survey.class);
	m_surveyTable.setClassAttr("dataTable");
	m_surveyTable.addTableActionListener(new SurveyActionListener(m_survey,
								      m_adminSurvey));
	
	SegmentedPanel.Segment surveySegment = m_panel.addSegment(surveyLabel, m_newSurvey);
	surveySegment.add(m_surveyTable);

    /*Label pollLabel = new Label(GlobalizationUtil.globalize("simplesurvey.ui.admin.polls"));
	m_pollsTable = new AdminSurveyTable(Poll.class);
	m_pollsTable.setClassAttr("dataTable");
	m_pollsTable.addTableActionListener(new SurveyActionListener(m_poll,
								     m_adminPoll));

	SegmentedPanel.Segment pollSegment = m_panel.addSegment(pollLabel, m_newPoll);
	pollSegment.add(m_pollsTable);
    */
	add(m_panel);
    }

    public void register(Page p) {
	super.register(p);
	m_surveyID = new BigDecimalParameter("survey_id");
	p.setVisibleDefault(m_adminSurvey, false);
	//page.setVisibleDefault(m_adminPoll, false);
	p.setVisibleDefault(m_panel, true);
	p.addGlobalStateParam(m_surveyID);
	p.addRequestListener(new RequestListener() {
		public void pageRequested(RequestEvent e) {
		    
		    PageState ps = e.getPageState();
		    // Sometimes the page is called only with the surveyID
		    if ( ps.getValue(m_surveyID) != null ) {
			BigDecimal surveyID = (BigDecimal) ps.getValue(m_surveyID);
	  			m_survey.setSelectedKey(ps, surveyID);
				
		    }
		  		      
		}
	    });
    }
    public void generateXML(PageState state,
			    Element parent) {
	
	 if (m_survey.isSelected(state)) {
	    m_adminSurvey.setVisible(state, true);
	    // m_adminPoll.setVisible(state, false);
	    m_panel.setVisible(state, false);
	} /*else if (m_poll.isSelected(state)) {
	    m_adminSurvey.setVisible(state, false);
	    m_adminPoll.setVisible(state, true);
	    m_panel.setVisible(state, false);	    
	    }*/
	
	super.generateXML(state, parent);
    }

    private class SurveyActionListener implements TableActionListener {
	private SurveySelectionModel m_survey;
	private AdminPanel m_admin;
	
	public SurveyActionListener(SurveySelectionModel survey,
				    AdminPanel admin) {
	    m_survey = survey;
	    m_admin = admin;
	}

	public void cellSelected(TableActionEvent e) {
	    PageState state = e.getPageState();
	    int column = e.getColumn().intValue();
	    
	    String key = (String)e.getRowKey();
	    BigDecimal id = new BigDecimal(key);
	    m_survey.setSelectedKey(state, id);
	    
	    if (column == AdminSurveyTable.COL_VIEW) {
		m_admin.setDisplayMode(state, AdminPanel.MODE_VIEW);
	    } else if (column == AdminSurveyTable.COL_RESPONSES) {
		m_admin.setDisplayMode(state, AdminPanel.MODE_RESPONSES);
	    } else if (column == AdminSurveyTable.COL_CORRECT_ANSWERS) {
		m_admin.setDisplayMode(state, AdminPanel.MODE_CORRECT_ANSWERS);
       	    } else if (column == AdminSurveyTable.COL_WIDGETS) {
		m_admin.setDisplayMode(state, AdminPanel.MODE_WIDGETS);
	    } else if (column == AdminSurveyTable.COL_PROPERTIES) {
		m_admin.setDisplayMode(state, AdminPanel.MODE_PROPERTIES);
	    } else if (column == AdminSurveyTable.COL_ANSWER_VALUES) {
		m_admin.setDisplayMode(state, AdminPanel.MODE_ANSWER_VALUES);
	    } else if (column == AdminSurveyTable.COL_DELETE) {
		Survey survey = m_survey.getSelectedSurvey(state);
		survey.delete();
		m_survey.clearSelection(state);
		
	    }
	}
	public void headSelected(TableActionEvent e) {}
    }

    private class ResetVisibilityListener implements ActionListener {
	SurveySelectionModel m_survey;

	public ResetVisibilityListener(SurveySelectionModel survey) {
	    m_survey = survey;
	}
	
	public void actionPerformed(ActionEvent e) {
	    PageState state = e.getPageState();

	    m_adminSurvey.setVisible(state, false);
	    //m_adminPoll.setVisible(state, false);
	    m_panel.setVisible(state, true);	    
	    
	    m_survey.clearSelection(state);
	}
    }
}



