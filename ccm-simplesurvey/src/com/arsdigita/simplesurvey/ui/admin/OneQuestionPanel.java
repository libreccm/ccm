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


import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.ActionLink;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.SingleSelectionModel;

// Classes related to DataObjects
import com.arsdigita.persistence.SessionManager;

import com.arsdigita.bebop.table.TableModel;
import com.arsdigita.bebop.table.TableModelBuilder;


import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.event.ActionEvent;

import com.arsdigita.toolbox.ui.DataTable;
import com.arsdigita.toolbox.ui.DataQueryBuilder;



import com.arsdigita.bebop.RequestLocal;

import com.arsdigita.bebop.Table;


import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.SessionManager;

import com.arsdigita.util.LockableImpl;
import com.arsdigita.xml.Element;


import com.arsdigita.formbuilder.PersistentLabel;

import java.math.BigDecimal;
import com.arsdigita.simplesurvey.ui.SurveySelectionModel;
import com.arsdigita.bebop.SimpleContainer;


/**
 * A page displaying the responses to a particular Question. If this is
 * a Multiple Choice with single answer question then numbers and percentages
 * for each answer alternative will be shown instead.
 *
 * @author <a href="mailto:pmarklun@arsdigita.com">Peter Marklund</a>
 * @version $Id: OneQuestionPanel.java 755 2005-09-02 13:42:47Z sskracic $
 */
public class OneQuestionPanel extends SimpleContainer {

    // Components of the page
    private Label m_questionText;
    private DateRestrictForm m_dateForm;
    private AnswerTable m_answerTable;
    private StatisticsTable m_statisticsTable;

    private SingleSelectionModel m_label;
    private SurveySelectionModel m_survey;

    // RequestLocal variables
    private RequestLocal m_persistentLabel =
	new RequestLocal() {
	    public Object initialValue(PageState state) {

                BigDecimal labelID = new BigDecimal(m_label.getSelectedKey(state).toString());
                PersistentLabel persistentLabel;
                try {
                    persistentLabel = new PersistentLabel(labelID);
                } catch (com.arsdigita.domain.DataObjectNotFoundException e) {
                    throw new com.arsdigita.util.UncheckedWrapperException(e);
                }

		return persistentLabel;
	    }
	};

    private RequestLocal m_widgetClass =
	new RequestLocal() {
	    public Object initialValue(PageState state) {

		BigDecimal labelID = new BigDecimal(m_label.getSelectedKey(state).toString());
		String queryName = "com.arsdigita.simplesurvey.GetWidgetClassAfterLabel";
		DataQuery dataQuery =
		    SessionManager.getSession().retrieveQuery(queryName);
		dataQuery.setParameter("labelID", labelID);		
		dataQuery.next();
		String widgetClass = (String)dataQuery.get("widgetClass");

		return widgetClass;
	    }
	};

    public OneQuestionPanel(SurveySelectionModel survey,
			    SingleSelectionModel label) {
	m_survey = survey;
	m_label = label;

	ActionLink m_complete = new ActionLink( (String) GlobalizationUtil.globalize("simplesurvey.ui.admin.back_to_question_list").localize());
	m_complete.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    m_label.clearSelection(e.getPageState());
		}
	    });
	add(m_complete);

	m_questionText =  new Label("");
        m_questionText.setFontWeight(Label.BOLD);
	add(m_questionText);

	m_dateForm = new DateRestrictForm("Show Answers");
	add(m_dateForm);

        m_answerTable = new AnswerTable(m_survey, m_label, m_dateForm);
        add(m_answerTable);

	m_statisticsTable = new StatisticsTable(m_label);
	add(m_statisticsTable);	
    }

    public void generateXML(PageState state,
			    Element parent) {
	// Set the question text heading
	PersistentLabel label = getPersistentLabel(state);
	m_questionText.setLabel( (String) GlobalizationUtil.globalize("simplesurvey.ui.admin.question").localize() + label.getLabel(), state);
	
	setComponentVisibility(state);
	
	super.generateXML(state, parent);
    }


    private void setComponentVisibility(PageState state) {

	if (!isSingleAnswerQuestion(state)) {
	    // This is a multiple choice or free text type question
	    // and we present the list of all answers

	    m_dateForm.setVisible(state, true);
	    m_answerTable.setVisible(state, true);
	    m_statisticsTable.setVisible(state, false);
	} else {
	    // This is a single answer multiple choice type question
	    // and we can present the statistics table
	    m_dateForm.setVisible(state, false);
	    m_answerTable.setVisible(state, false);
	    m_statisticsTable.setVisible(state, true);

	}
    }


    private class AnswerTable extends DataTable {

        public AnswerTable(SurveySelectionModel survey,
			   SingleSelectionModel label,
			   DateRestrictForm dateForm) {
            super(new AnswerQueryBuilder(survey, label, dateForm));

            addColumn("Answer", "answerValue", true);
        }
        
    }

    private static class AnswerQueryBuilder extends LockableImpl
        implements DataQueryBuilder {

	SingleSelectionModel m_label;
	DateRestrictForm m_dateForm;

	public AnswerQueryBuilder(SurveySelectionModel survey,
				  SingleSelectionModel label,
				  DateRestrictForm dateForm) {
	    m_label = label;
	    m_dateForm = dateForm;
	}

        public DataQuery makeDataQuery(DataTable t, PageState state) {

            String queryName = "com.arsdigita.simplesurvey.GetAnswersToQuestion";
            DataQuery dataQuery =
                SessionManager.getSession().retrieveQuery(queryName);
            dataQuery.setParameter("labelID", (new BigDecimal(m_label.getSelectedKey(state).toString())));

	    AnswerTable table = (AnswerTable)t;
	    dataQuery.setParameter("startDate", m_dateForm.getStartDate(state));
	    dataQuery.setParameter("endDate", m_dateForm.getEndDate(state));	    
                
            return dataQuery;
        }

        public String getKeyColumn() {
            return "answerID";
        }        
    }

    public PersistentLabel getPersistentLabel(PageState state) {
	return (PersistentLabel)m_persistentLabel.get(state);
    }

    public String getPersistentWidgetClass(PageState state) {
	return (String)m_widgetClass.get(state);
    }    

    public boolean isSingleAnswerQuestion(PageState state) {

	String widgetClass = getPersistentWidgetClass(state);

	if (widgetClass.equals("com.arsdigita.formbuilder.PersistentRadioGroup")) {
	    return true;
	} else {
	    return false;
	}
    }

    private class StatisticsTable extends Table {

	public StatisticsTable(SingleSelectionModel label) {
	    super(OneQuestionPanel.getTableModelBuilder(label), new String[] {"Answer Alternative",
									      "Number of Answers",
									      "Percentage of Total"});
	}
    }

    private static TableModelBuilder getTableModelBuilder(final SingleSelectionModel label) {

	return new TableModelBuilder() {
	    public TableModel makeModel(Table l, PageState state) {

		return new StatisticsTableModel(new BigDecimal(label.getSelectedKey(state).toString()));
	    }

	    public void lock() {
		// This TableModelBuilder has nothing to lock :-)
	    }

	    public boolean isLocked() {
		return true;
	    }
	};
    }

    private static class StatisticsTableModel implements TableModel {

	private DataQuery m_statisticsQuery;
	private int m_totalNumberOfAnswers;

	public StatisticsTableModel(BigDecimal labelID) {

	    String statQueryName = "com.arsdigita.simplesurvey.GetOneAnswerQuestionStatistics";
	    m_statisticsQuery = SessionManager.getSession().retrieveQuery(statQueryName);
	    m_statisticsQuery.setParameter("labelID", labelID);

	    // Get the total number of answers to calculate percentage
	    String numQueryName = "com.arsdigita.simplesurvey.GetTotalNumberOfAnswers";
	    DataQuery numberQuery = SessionManager.getSession().retrieveQuery(numQueryName);
	    numberQuery.setParameter("labelID", labelID);
	    numberQuery.next();
	    Integer totalNumberOfAnswers = new Integer(numberQuery.get("numberOfAnswers").toString());
	    m_totalNumberOfAnswers = totalNumberOfAnswers.intValue();
        numberQuery.close();
	}

	public int getColumnCount() {
	    return 3;
	}

	public boolean nextRow() {
	    
	    return m_statisticsQuery.next();
	}

	public Object getElementAt(int columnIndex) {

	    String numberOfAnswers = m_statisticsQuery.get("numberOfAnswers").toString();

	    Object returnValue = null;

	    if (columnIndex == 0) {

		returnValue = m_statisticsQuery.get("value");
	    } else if (columnIndex == 1) {

		returnValue = numberOfAnswers;
	    } else if (columnIndex == 2) {

		returnValue = getPercentageOfAnswers(numberOfAnswers);
	    }

	    return returnValue;
	}

	public Object getKeyAt(int columnIndex) {
	    return m_statisticsQuery.get("value");
	}

	private String getPercentageOfAnswers(String numberOfAnswers) {
	    
	    Integer numberOfAnswersInt = new Integer(numberOfAnswers);
	    
	    double ratio = 1.0;
	    if (m_totalNumberOfAnswers != 0) {
		ratio = (double)numberOfAnswersInt.intValue() / (double)m_totalNumberOfAnswers;
	    }

	    double promille = ratio * 1000.0;

	    long promilleRounded = Math.round(promille);

	    double percentage = (double)promilleRounded / 10.0;

	    return Double.toString(percentage);
	}
    }
}
