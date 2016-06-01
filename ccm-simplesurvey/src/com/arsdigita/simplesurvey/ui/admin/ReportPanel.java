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

import com.arsdigita.simplesurvey.Survey;
import com.arsdigita.simplesurvey.ui.SurveySelectionModel;

import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Link;
import com.arsdigita.bebop.ActionLink;
import com.arsdigita.bebop.ControlLink;
import com.arsdigita.bebop.Label;

import com.arsdigita.bebop.List;
import com.arsdigita.bebop.list.ListCellRenderer;
import com.arsdigita.bebop.list.ListModel;
import com.arsdigita.bebop.list.ListModelBuilder;

import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.event.PrintEvent;




import com.arsdigita.bebop.Component;

import com.arsdigita.simplesurvey.Survey;
import com.arsdigita.simplesurvey.Response;
import com.arsdigita.simplesurvey.ResponseCollection;

import com.arsdigita.persistence.DataQuery;


import com.arsdigita.persistence.Filter;

import com.arsdigita.formbuilder.util.AttributeHelper;

import com.arsdigita.bebop.util.Attributes;
import com.arsdigita.util.UncheckedWrapperException;


import com.arsdigita.bebop.SegmentedPanel;

import com.arsdigita.bebop.event.ChangeEvent;
import com.arsdigita.bebop.event.ChangeListener;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;

/**
 * A page displaying the responses to a particular Survey
 *
 * @author <a href="mailto:pmarklun@arsdigita.com">Peter Marklund</a>
 * @version $Id: ReportPanel.java 755 2005-09-02 13:42:47Z sskracic $
 */
public class ReportPanel extends SimpleContainer {

    private SurveySelectionModel m_survey;

    private ActionLink m_complete;

    // Components of the page
    private SegmentedPanel m_segmentedPanel;
    private QuestionList m_questionList;
    private ResponseList m_responseList;
    private DateRestrictForm m_dateForm;

    private OneResponsePanel m_response;
    private OneQuestionPanel m_question;

    public ReportPanel(SurveySelectionModel survey) {
	m_survey = survey;


	m_complete = new ActionLink( (String) GlobalizationUtil.globalize("simplesurvey.ui.admin.back_to_survey_list").localize());
	m_complete.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    fireCompletionEvent(e.getPageState());
		}
	    });
	add(m_complete);

        m_segmentedPanel = new SegmentedPanel();

        // List the questions
        Label questionLabel = new Label(GlobalizationUtil.globalize("simplesurvey.ui.admin.questions_of_the_survey"));
        m_questionList = new QuestionList(m_survey);
        m_segmentedPanel.addSegment(questionLabel, m_questionList);

        // List all responses
        Label responseLabel = new Label(GlobalizationUtil.globalize("simplesurvey.ui.admin.responses_to_the_survey"));
	m_dateForm = new DateRestrictForm("Show responses");
        m_responseList = new ResponseList(m_survey, m_dateForm);
        SegmentedPanel.Segment responseSegment = 
	    m_segmentedPanel.addSegment(responseLabel, m_dateForm);
	responseSegment.add(m_responseList);

        // Export to CSV
        Label exportLabel = new Label(GlobalizationUtil.globalize("simplesurvey.ui.admin.export_response_data"));
        Link exportLink = new Link("Export to CSV file",
				   "exportUsers.jsp");
        try {
	    exportLink.addPrintListener(new PrintListener() {
		    public void prepare(PrintEvent event) {
			PageState state = event.getPageState();
			Link link = (Link)event.getTarget();
			
			link.setVar("survey",
				    m_survey.getSelectedKey(state).toString());
		    }
		});
        } catch (java.util.TooManyListenersException e) {
            throw new com.arsdigita.util.UncheckedWrapperException(e);
        }
        m_segmentedPanel.addSegment(exportLabel, exportLink);

	m_response = new OneResponsePanel(m_survey, m_responseList.getSelectionModel());
	m_question = new OneQuestionPanel(m_survey, m_questionList.getSelectionModel());

	m_responseList.getSelectionModel().addChangeListener(new ChangeListener() {
		public void stateChanged(ChangeEvent e) {
		    PageState state = e.getPageState();
		    
		    if (m_responseList.getSelectionModel().isSelected(state)) {
			m_segmentedPanel.setVisible(state, false);
			m_complete.setVisible(state, false);
			m_question.setVisible(state, false);
			m_response.setVisible(state, true);
		    } else {
 			m_complete.setVisible(state, true);
			m_segmentedPanel.setVisible(state, true);
			m_question.setVisible(state, false);
			m_response.setVisible(state, false);
		    }
		}
	    });
	m_questionList.getSelectionModel().addChangeListener(new ChangeListener() {
		public void stateChanged(ChangeEvent e) {
		    PageState state = e.getPageState();
		    
		    if (m_questionList.getSelectionModel().isSelected(state)) {
			m_segmentedPanel.setVisible(state, false);
			m_complete.setVisible(state, false);
			m_question.setVisible(state, true);
			m_response.setVisible(state, false);
		    } else {
 			m_complete.setVisible(state, true);
			m_segmentedPanel.setVisible(state, true);
			m_question.setVisible(state, false);
			m_response.setVisible(state, false);
		    }
		}
	    });

        add(m_segmentedPanel);
	add(m_response);
	add(m_question);
    }

    public void register(Page p) {
	super.register(p);
	
	
	p.setVisibleDefault(m_response, false);
	p.setVisibleDefault(m_question, false);
    }

    private class ResponseList extends List {
	
        public ResponseList(SurveySelectionModel survey,
			    DateRestrictForm dateForm) {
            super(new ResponseListModelBuilder(survey, dateForm));

            setCellRenderer(new ResponseListCellRenderer());
        }
    }

    private static class ResponseListModelBuilder 
        implements ListModelBuilder {
    
	private SurveySelectionModel m_survey;
	private DateRestrictForm m_dateForm;

	public ResponseListModelBuilder(SurveySelectionModel survey,
					DateRestrictForm dateForm) {
	    m_survey = survey;
	    m_dateForm = dateForm;
	}

        public ListModel makeModel(List l, PageState state) {
	    Survey survey = m_survey.getSelectedSurvey(state);

	    ResponseCollection responses = survey.getResponses();

	    Filter startFilter = responses.addFilter("entryDate > :startDate");
	    startFilter.set("startDate", m_dateForm.getStartDate(state));
	    Filter endFilter = responses.addFilter("entryDate < :endDate");
	    endFilter.set("endDate", m_dateForm.getEndDate(state));
	    
            return new ResponseListModel(responses);
        }
                
        public void lock() {

        }

        public boolean isLocked() {
            return true;
        }
    }

    private static class ResponseListModel implements ListModel {

        private ResponseCollection m_responses;
	private Response m_response;

        public ResponseListModel(ResponseCollection responses) {
	    m_responses = responses;

        }

        public boolean next() {
            if (m_responses.next()) {
		m_response = m_responses.getResponse();
		return true;
	    }
	    m_response = null;
	    return false;
        }
    
        public Object getElement() {

            return m_response.getEntryDate();
        }
    
        public String getKey() {
            return m_response.getID().toString();
        }

    }

    private class ResponseListCellRenderer
        implements ListCellRenderer {

        public Component getComponent(List list, PageState state, Object value, 
                                      String key, int index, boolean isSelected) {
            return new ControlLink(value.toString());
        }
    }

    private class QuestionList extends List {

        public QuestionList(SurveySelectionModel survey) {
            super(new QuestionListModelBuilder(survey));

            setCellRenderer(new QuestionListCellRenderer());
        }
    }

    private static class QuestionListModelBuilder 
        implements ListModelBuilder {

	private SurveySelectionModel m_survey;

	public QuestionListModelBuilder(SurveySelectionModel survey) {
	    m_survey = survey;
	}

        public ListModel makeModel(List l, PageState state) {
	    Survey survey = m_survey.getSelectedSurvey(state);

            return new QuestionListModel(survey.getLabelDataQuery());
        }
                
        public void lock() {

        }

        public boolean isLocked() {
            return true;
        }
    }

    private static class QuestionListModel implements ListModel {

        private DataQuery m_labels;

        public QuestionListModel(DataQuery labels) {
	    m_labels = labels;
        }

        public boolean next() {
            return m_labels.next();
        }
    
        public Object getElement() {

            return m_labels.get("attributeString");
        }
    
        public String getKey() {
            return m_labels.get("labelID").toString();
        }

    }

    private class QuestionListCellRenderer
        implements ListCellRenderer {

        public Component getComponent(List list, PageState state, Object value, 
                                      String key, int index, boolean isSelected) {
            return new ControlLink(ReportPanel.getLabelAttribute((String)value));
        }
    }

    public static String getLabelAttribute(String attributeString) {

            AttributeHelper attributeHelper = new AttributeHelper();
            Attributes attributes = 
                attributeHelper.getAttributesMap(attributeString);
            String labelText = attributes.getAttribute("label");

            return labelText;
    }
}
