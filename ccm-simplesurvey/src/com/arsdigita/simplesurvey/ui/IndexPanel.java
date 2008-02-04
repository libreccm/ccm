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


import com.arsdigita.simplesurvey.util.GlobalizationUtil ; 



import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Link;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.event.RequestListener;
import com.arsdigita.bebop.event.RequestEvent;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.bebop.SegmentedPanel;
import com.arsdigita.simplesurvey.SimpleSurveyUtil;
import com.arsdigita.simplesurvey.Survey;
import java.math.BigDecimal;


/**
 * The index page of the Simple Survey admin UI.
 *
 * @author <a href="mailto:pmarklun@arsdigita.com">Peter Marklund</a>
 * @version $Id: IndexPanel.java 755 2005-09-02 13:42:47Z sskracic $
 */
public class IndexPanel extends SimpleContainer {
        
    private SegmentedPanel m_panel;
    private SurveyTable m_surveyTable;
    private SurveyTable m_pollsTable;
    
    private ViewPanel m_viewSurvey;
    private ViewPanel m_viewPoll;
    
    private Link m_adminLink;
    private BigDecimalParameter m_surveyID;
    private SurveySelectionModel m_survey;
    //private SurveySelectionModel m_poll;
    
    private static org.apache.log4j.Logger s_log = 
        org.apache.log4j.Logger.getLogger(IndexPanel.class.getName());

    public IndexPanel(SurveySelectionModel survey,
		      SurveySelectionModel poll) {
	m_survey = survey;
	//m_poll = poll;

	m_viewSurvey = new ViewPanel(m_survey);
	//m_viewPoll = new ViewPanel(m_poll);
	
	add(m_viewSurvey);
	//add(m_viewPoll);

	m_panel = new SegmentedPanel();

	
	m_adminLink = new Link( new Label(GlobalizationUtil.globalize("simplesurvey.ui.administer")), "admin/index.jsp");
	add(m_adminLink);

	Label surveyLabel = new Label(GlobalizationUtil.globalize("simplesurvey.ui.active_surveys"));
        m_surveyTable = new SurveyTable(Survey.class);
	m_surveyTable.setRowSelectionModel(m_survey);
	m_surveyTable.setClassAttr("dataTable");
	//m_surveyTable.addTableActionListener(new TableActionListener() {
	//public void cellSelected(TableActionEvent e) {
	//	    m_survey.setSelectedKey(e.getPageState(),
	//				    new BigDecimal((String)e.getRowKey()));
	//	    //s_log.warn("Cell selected!" + e.getRowKey());
	//	}
	//	public void headSelected(TableActionEvent e) {
	//	}
	//    });		

	m_panel.addSegment(surveyLabel, m_surveyTable);

	//Label pollLabel = new Label(GlobalizationUtil.globalize("simplesurvey.ui.active_polls"));
	//m_pollsTable = new SurveyTable(Poll.class);
	//m_pollsTable.setClassAttr("dataTable");
	//m_pollsTable.setRowSelectionModel(m_poll);
	//m_pollsTable.addTableActionListener(new TableActionListener() {
	//public void cellSelected(TableActionEvent e) {
	//m_poll.setSelectedKey(e.getPageState(),
	//new BigDecimal((String)e.getRowKey()));
	//}
	//public void headSelected(TableActionEvent e) {
	//}
	//});		
	
	//m_panel.addSegment(pollLabel, m_pollsTable);

	add(m_panel);
    }
    
    public void register(Page p) {
	super.register(p);
	
	//p.setVisibleDefault(m_adminLink, false);
	//p.setVisibleDefault(m_viewSurvey, false);
	//p.setVisibleDefault(m_viewPoll, false);
	m_surveyID = new BigDecimalParameter("survey_id");
	p.addGlobalStateParam(m_surveyID);
	p.addRequestListener(new RequestListener() {
		public void pageRequested(RequestEvent e) {
		    PageState ps = e.getPageState();
		    BigDecimal surveyID = (BigDecimal) ps.getValue(m_surveyID);
		    //s_log.warn("m_surveyID=" + surveyID);
		    m_survey.setSelectedKey(ps, surveyID);
		    //s_log.warn("Page Requested (IndexPanel)");
		    if (m_survey.isSelected(ps)) {
			//s_log.warn("A survey has been selected (IndexPanel)");
			m_adminLink.setVisible(ps, false);
			m_viewSurvey.setVisible(ps, true);
			//m_viewPoll.setVisible(ps, false);
			m_panel.setVisible(ps, false);
			//} else if (m_poll.isSelected(ps)) {
			//m_adminLink.setVisible(ps, false);
			//m_viewSurvey.setVisible(ps, false);
			//m_viewPoll.setVisible(ps, true);
			//m_panel.setVisible(ps, false);
		    } else {
			m_adminLink.setVisible(ps, SimpleSurveyUtil.isUserAdmin(ps));
			m_viewSurvey.setVisible(ps, false);
			//m_viewPoll.setVisible(ps, false);
			m_panel.setVisible(ps, true);
		    }
		}
	    });
    }
}
