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
package com.arsdigita.simplesurvey.ui.admin;
import com.arsdigita.simplesurvey.util.GlobalizationUtil ; 

import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.TableActionAdapter;
import com.arsdigita.bebop.event.TableActionEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.event.RequestListener;
import com.arsdigita.bebop.event.RequestEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.ActionEvent;
import 	com.arsdigita.bebop.FormData;	
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.Link;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.ActionLink;
import com.arsdigita.bebop.ControlLink;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.form.SingleSelect;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.table.TableCellRenderer;
import com.arsdigita.bebop.table.TableColumn;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.bebop.RequestLocal;

import com.arsdigita.toolbox.ui.DataQueryBuilder;
import com.arsdigita.toolbox.ui.DataTable;

import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.DataOperation;
import com.arsdigita.persistence.SessionManager;

import com.arsdigita.simplesurvey.Survey;
import com.arsdigita.simplesurvey.ui.SurveySelectionModel;

import java.math.BigDecimal;
import java.math.BigDecimal;

public class AnswerValuesPanel extends SimpleContainer  {

    // Displays a list of the multiple choice answers grouped by question
    // Each of the answers contains a link that leads to a drop-down value selection widget

    private SurveySelectionModel m_survey;
    private static final String QUESTION_TEXT = "questionText";
    private static final String ANSWER_TEXT = "answerText";
    private static final String ANSWER_VALUE = "answerValue";
    //protected Label m_answerText;
    //protected Label m_questionText;
    //protected SingleSelect m_select;
    private RequestLocal m_answerText;
    private RequestLocal m_questionText;
    private RequestLocal m_answerValue;

    private Form m_answerValuesForm;
    private SingleSelect m_select;
    private BigDecimalParameter m_optionID;
    private BigDecimalParameter m_surveyID;
    private ActionLink m_complete;
    private Link m_back;

    private static org.apache.log4j.Logger s_log = 
        org.apache.log4j.Logger.getLogger(AnswerValuesPanel.class.getName());

    private DataTable m_answerTable;

    public AnswerValuesPanel(SurveySelectionModel survey) {
	super();
	m_survey = survey;
	
	m_answerText = new RequestLocal();
	m_questionText = new RequestLocal();
	m_answerValue = new RequestLocal();
	
	m_complete = new ActionLink( (String) GlobalizationUtil.globalize("simplesurvey.ui.admin.back_to_question_list").localize());
	m_complete.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		     PageState ps = e.getPageState();
       			 m_answerTable.setVisible(ps, true);
			 m_answerValuesForm.setVisible(ps, false);
			 m_complete.setVisible(ps,false);
			 m_back.setVisible(ps, true);
     		}
	    });
	m_back = new Link(new Label(GlobalizationUtil.globalize("simplesurvey.ui.admin.back_to_survey_list")), "../admin");

	add(m_complete);
	add(m_back);
       	setupAnswerTable();
	setupAnswerValuesForm();
    }
    public void register (Page p) {
	p.setVisibleDefault(m_answerValuesForm,false);
	m_optionID = new BigDecimalParameter("option_id");
	m_surveyID = new BigDecimalParameter("survey_id");
	p.addGlobalStateParam(m_optionID);
	p.addGlobalStateParam(m_surveyID);
	p.addRequestListener( new RequestListener() {
		public void pageRequested(RequestEvent e) {
		    PageState ps = e.getPageState();
		    
		    if ( ps.getValue(m_optionID) == null || 
			 m_answerValuesForm.getFormData(ps).isSubmission()) {
			m_answerTable.setVisible(ps, true);
			m_answerValuesForm.setVisible(ps, false);
			m_complete.setVisible(ps,false);
			m_back.setVisible(ps, true);
		    } /*else {
			m_answerTable.setVisible(ps, false);
			m_answerValuesForm.setVisible(ps, true);
			   
			BigDecimal optionID = (BigDecimal) ps.getValue(m_optionID);
			final String query = "com.arsdigita.simplesurvey.getAnswerOption";
			DataQuery dq = SessionManager.getSession().retrieveQuery(query);
			dq.setParameter("optionID", optionID);
			
			if ( dq.next()) {
      			    m_questionText.set(ps, (String) dq.get(QUESTION_TEXT));
       			    m_answerText.set(ps, (String) dq.get(ANSWER_TEXT));
			    m_answerValue.set(ps, (BigDecimal) dq.get(ANSWER_VALUE));
			    m_select.setValue(ps,getAnswerValue(ps));
			}
			dq.close();
			}*/
		}
	    });
    }
    
    private String getQuestionText(PageState ps) {
	String s = (String) m_questionText.get(ps);
	return s.substring(6, s.length());
    }
    private String getAnswerText(PageState ps) {
	String s = (String) m_answerText.get(ps);
	return s;
    }
    private BigDecimal getAnswerValue(PageState ps) {
	BigDecimal bi = (BigDecimal) m_answerValue.get(ps);
	return bi;
    }

    
    private void setupAnswerTable() {

	// This is the table that holds the list of all answer options in the survey
	m_answerTable = new DataTable(new AnswerListingBuilder());
       
	TableColumn c1 = m_answerTable.addColumn("simplesurvey.ui.admin.question", QUESTION_TEXT,
						 false, new QuestionTextRenderer());
	TableColumn c2 = m_answerTable.addColumn("simplesurvey.ui.admin.answer",  ANSWER_TEXT);
	TableColumn c3 = m_answerTable.addColumn("simplesurvey.ui.admin.current_value",  ANSWER_VALUE);
	c1.setHeaderRenderer(new GlobalizedHeaderRenderer());
	c2.setHeaderRenderer(new GlobalizedHeaderRenderer());
	c3.setHeaderRenderer(new GlobalizedHeaderRenderer());
	

	// Add a single select box widget to each possible answer
	// The admin can select a point value for each answer
	TableColumn editValueColumn = new TableColumn();
	editValueColumn.setCellRenderer(new EditLinkRenderer());
       	m_answerTable.getColumnModel().add(editValueColumn);
	m_answerTable.addTableActionListener(new AnswerValueActionListener());
       	add(m_answerTable);

    }
     private BigDecimal getSurveyID(PageState ps) {
	    BigDecimal surveyID = null;
	    if ( ps.getValue(m_surveyID) == null ) {
		 Survey s =  m_survey.getSelectedSurvey(ps);
		 surveyID = s.getID();
	     } else {
		 surveyID = (BigDecimal) ps.getValue(m_surveyID);
	     }
	    return surveyID;
     }

    private void setupAnswerValuesForm() {
	m_answerValuesForm = new AnswerValuesForm();
	add(m_answerValuesForm);
     }
    private class AnswerValueActionListener extends TableActionAdapter{
	
	public void cellSelected(TableActionEvent e) {
	    PageState ps = e.getPageState();
	    ps.setValue(m_optionID, new BigDecimal(e.getRowKey().toString()));
	    m_answerTable.setVisible(ps, false);
	    m_back.setVisible(ps, false);
	    m_complete.setVisible(ps, true);
			m_answerValuesForm.setVisible(ps, true);
			   
			BigDecimal optionID = (BigDecimal) ps.getValue(m_optionID);
			final String query = "com.arsdigita.simplesurvey.getAnswerOption";
			DataQuery dq = SessionManager.getSession().retrieveQuery(query);
			dq.setParameter("optionID", optionID);
			
			if ( dq.next()) {
      			    m_questionText.set(ps, (String) dq.get(QUESTION_TEXT));
       			    m_answerText.set(ps, (String) dq.get(ANSWER_TEXT));
			    m_answerValue.set(ps, (BigDecimal) dq.get(ANSWER_VALUE));
			    m_select.setValue(ps,getAnswerValue(ps));
			}
			dq.close();

	}

    }
    private class AnswerValuesForm extends Form {
	
       	private AnswerValuesForm() {

	    super("answerValuesForm");
       	    Label questionText = new Label(new PrintListener() {
		    public void prepare(PrintEvent e) {
			PageState ps = e.getPageState();
			Label target = (Label) e.getTarget();
			target.setLabel(getQuestionText(ps));
		    }
		});
	    
       	    Label answerText = new Label(new PrintListener() {
		    public void prepare(PrintEvent e) {
			PageState ps = e.getPageState();
			Label target = (Label) e.getTarget();
			target.setLabel(getAnswerText(ps));
		    }
		});
	    add(questionText);
	    add(answerText);
	    m_select = new SingleSelect(ANSWER_VALUE);
	    m_select.addOption(new Option("0", new Label("0")));
	    m_select.addOption(new Option("1", new Label("1")));
	    m_select.addOption(new Option("2", new Label("2")));
	    m_select.addOption(new Option("3", new Label("3")));
	    m_select.addOption(new Option("4", new Label("4")));
	    m_select.addOption(new Option("5", new Label("5")));
	    m_select.addOption(new Option("6", new Label("6")));
	    m_select.addOption(new Option("7", new Label("7")));
	    m_select.addOption(new Option("8", new Label("8")));
	    m_select.addOption(new Option("9", new Label("9")));
	    m_select.addOption(new Option("10", new Label("10")));
	    add(m_select);
	    add(new Submit(GlobalizationUtil.globalize("simplesurvey.ui.admin.submit")));
	    //add(new Submit(GlobalizationUtil.globalize("simplesurvey.ui.admin.done_editing")));
	    addProcessListener(new AnswerValuesProcessListener());
	    addInitListener(new AnswerValuesInitListener());
	}
    }
    private class AnswerValuesProcessListener implements FormProcessListener {
	public void process(FormSectionEvent e) throws FormProcessException {
	    PageState ps = e.getPageState();
	    FormData fd = e.getFormData();
	    BigDecimal optionID = (BigDecimal) ps.getValue(m_optionID);
	    BigDecimal answerValue = new BigDecimal((String) fd.get(ANSWER_VALUE));
	    
	    DataOperation dao = SessionManager.getSession().retrieveDataOperation("com.arsdigita.simplesurvey.updateAnswerValue");
	    dao.setParameter("optionID", optionID);
	    dao.setParameter("answerValue", answerValue);
	    dao.execute();
	    ps.setValue(m_optionID, null);
	}
    }
    private class AnswerValuesInitListener implements FormInitListener {
	public void init(FormSectionEvent e) throws FormProcessException {
	    PageState ps = e.getPageState();
	    s_log.warn("Setting value of select to " + getAnswerValue(ps)); 
	    m_select.setValue(ps, getAnswerValue(ps));
     	}
    }
    private class GlobalizedHeaderRenderer implements TableCellRenderer {
	public Component getComponent(Table table, PageState state, Object value,
				    boolean isSelected, Object key, int row, 
				    int column) {
	
	Label label = new Label(GlobalizationUtil.globalize((String) value));
	label.setVerticalAlignment(CENTER);
	return label;
      }
    }
    private class QuestionTextRenderer implements TableCellRenderer {
	 public Component getComponent(Table table, PageState state, Object value,
				    boolean isSelected, Object key, int row, 
				    int column) {
	  
       	     if ( ! isLocked() && table != null && table.isLocked() ) {
		 lock();
	     }
	     if ( value == null ) {
       		 return new Label("");
	     }
	     String question = (String) value;
	     // Cut off the 6 chars "label=" stored in the db
	     return new Label(question.substring(6,question.length()));
	}
    }
    private class EditLinkRenderer implements TableCellRenderer {

      public Component getComponent(Table table, PageState ps, Object value,
				    boolean isSelected, Object key, int row, 
				    int column) {
	  
       	     if ( ! isLocked() && table != null && table.isLocked() ) {
		 lock();
	     }
	     if ( value == null ) {
       		 return new Label("");
	     }
	     /*Link l = new Link(new Label(GlobalizationUtil.globalize("simplesurvey.ui.admin.edit_value")),"../admin"); 
	       BigDecimal surveyID = getSurveyID(ps); 
	     
	     //Sometimes we can get the surveyID from the global parameter, sometimes from the selection
	      l.setVar("survey_id", surveyID.toString());
	      l.setVar("option_id", ((BigDecimal) key).toString()); 
	      
	      return l; */
	     ControlLink cl = new ControlLink(new Label (GlobalizationUtil.globalize("simplesurvey.ui.admin.edit_value")));
	     return cl;
	}
    }
   
    private class AnswerListingBuilder implements DataQueryBuilder {
	private final static String KEY_COLUMN = "optionID";
	public boolean m_locked; 
	public AnswerListingBuilder() {
	    super();
	}
	public DataQuery makeDataQuery(DataTable t, PageState ps) {
	    final String query = "com.arsdigita.simplesurvey.getAnswerOptions";
	     DataQuery dq = SessionManager.getSession().retrieveQuery(query);
	    
	     dq.setParameter("surveyID", getSurveyID(ps));
	     return dq;
	}
    
	public String getKeyColumn() {
	    return KEY_COLUMN;
	}
        public void lock() {
	   m_locked = true;
	}
	public boolean isLocked() {
	   return m_locked;
	}

    }

}
