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
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Link;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.table.DefaultTableCellRenderer;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.table.TableColumn;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.bebop.event.RequestListener;
import com.arsdigita.bebop.event.RequestEvent;
import com.arsdigita.bebop.RequestLocal;

import com.arsdigita.toolbox.ui.DataQueryBuilder;
import com.arsdigita.toolbox.ui.DataTable;
import com.arsdigita.web.URL;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.simplesurvey.Survey;

import com.arsdigita.simplesurvey.util.GlobalizationUtil;
import com.arsdigita.kernel.KernelHelper;
import com.arsdigita.kernel.User;
import java.math.BigDecimal;


 
public class ResultsPane extends SimpleContainer {
	
    private static final String USER_ANSWER = "userAnswer";
    private static final String CORRECT_ANSWER = "correctAnswer";
    private static final String QUESTION_NUMBER = "questionNumber";
    private static final String SCORE = "score";
    private static final String SURVEY_NUMBER = "surveyNumber";
    private static final String SURVEY_PERCENTAGE = "surveyPercentage";
    private static final String ANSWER_COUNT = "answerCount";
    private static final String ANSWER_VALUE = "answerValue";
    private static final String KNOWLEDGE_TEST = "knowledge_test";

    private TableColumn m_tabulatorColumn;
    private TableColumn m_scoreColumn;
    private TableColumn m_percentColumn;
    private DataTable m_resultsTable;
    private DataTable m_statisticsTable;
    private Link m_statisticsLink;
    private Label m_assessmentSummary;
    private Label m_scoreLabel;
    private RequestLocal m_survey;
    private RequestLocal m_user;
    private RequestLocal m_response;
    private SurveySelectionModel m_surveyModel;
    private BigDecimalParameter m_responseID;
    private BigDecimalParameter m_surveyID;
    private int m_rowCount;

    private static org.apache.log4j.Logger s_log = 
        org.apache.log4j.Logger.getLogger(ResultsPane.class.getName());

    public ResultsPane(SurveySelectionModel surveyModel, RequestLocal response) {	
        super();
        m_surveyModel = surveyModel;
        m_response = response;
	
        m_survey = new RequestLocal() {
                public Object initialValue(PageState ps) {
                    
                    Survey survey = m_surveyModel.getSelectedSurvey(ps);
                    return survey;
                }
            };
        
        m_user = new RequestLocal() {
                public Object initialValue(PageState ps) {
                    User user = KernelHelper.getCurrentUser(ps.getRequest());
                    return user;
                }
            };
        addAssessmentSummary(this);
        addResultsTable(this);
        addScoreLabel(this);
        addStatisticsTable(this);
        addStatisticsLink(this);
    }

    public void register (Page p) {
        p.setVisibleDefault(m_statisticsTable, false);
        m_responseID = new BigDecimalParameter("response_id");
        m_surveyID = new BigDecimalParameter("survey_id");
        p.addGlobalStateParam(m_surveyID);
        p.addGlobalStateParam(m_responseID);
        p.addRequestListener(new RequestListener() {
                public void pageRequested(RequestEvent e) {
                    PageState ps = e.getPageState();
                    Survey s = (Survey) m_survey.get(ps);
                    if ( s == null) {
                        return;
                    } 
                    
                    if ( s.getQuizType().equals(KNOWLEDGE_TEST) ) {
                        m_resultsTable.setVisible(ps,true);
                        m_scoreLabel.setVisible(ps, true);
                        m_assessmentSummary.setVisible(ps, false);
                    } else {
                        // We don't show the ResultsTable, just the summary
                        m_resultsTable.setVisible(ps,false);
                        m_scoreLabel.setVisible(ps, false);
                        m_assessmentSummary.setVisible(ps, true);
                    }
                    if ( !s.responsesArePublic()) {
                        m_statisticsLink.setVisible(ps,false);
                        
                    } else if ( s.responsesArePublic() && ( ps.getValue(m_responseID) != null )) {
                        
                        // We only show the statistics if the survey's stat results are public
                        m_statisticsTable.setVisible(ps, true);
                        m_statisticsLink.setVisible(ps,false);
                    }
                }
            });
        super.register(p);
    }

    public void addScoreLabel(SimpleContainer sc) {
        
        m_scoreLabel = new Label();
        m_scoreLabel.addPrintListener(new ScorePrintListener());
        sc.add(m_scoreLabel);
    }

    public void addStatisticsLink(SimpleContainer sc) {
	
	//m_statisticsLink = new Link(new Label(GlobalizationUtil.globalize("simplesurvey.ui.view_all_results")),"../simplesurvey");
	m_statisticsLink = new Link( new Label(GlobalizationUtil.globalize("simplesurvey.ui.view_all_results")), new PrintListener() {
		public void prepare(PrintEvent e) {
		    PageState ps = e.getPageState();
		    Link t = (Link) e.getTarget();
		    BigDecimal responseID = null;
		    
		    String uri = ps.getRequestURI();
		    String node = URL.getDispatcherPath();
		    if ( uri.startsWith(node)) {
                uri = uri.substring(node.length(), uri.length());
		    }
            t.setTarget(uri);
            
		    // If the form was just submitted than we get the response id from a RequestLocal
            t.setVar("response_id", getResponseID(ps).toString());
            t.setVar("survey_id", ((BigDecimal) ps.getValue(m_surveyID)).toString());
		}
		 public boolean isVisible(PageState ps) {
		     Survey s = (Survey) m_survey.get(ps);
		      if ( ! s.responsesArePublic() ||  m_statisticsTable.isVisible(ps) ) {
			    return false;
      		       }
		      return true;
		 }

	    });
	/*try {
	    m_statisticsLink.addPrintListener(new PrintListener() {
		public void prepare(PrintEvent e) {
		    PageState ps = e.getPageState();
		    Link t = (Link) e.getTarget();
		    BigDecimal responseID = null;
	
		    // If the form was just submitted than we get the response id from a RequestLocal
       		    t.setVar("response_id", getResponseID(ps).toString());
      		    t.setVar("survey_id", ((BigDecimal) ps.getValue(m_surveyID)).toString());
		}
		 public boolean isVisible(PageState ps) {
		     Survey s = (Survey) m_survey.get(ps);
		      if ( ! s.responsesArePublic() ||  m_statisticsTable.isVisible(ps) ) {
			    return false;
      		       }
		      return true;
		 }

		 });
	} catch (java.util.TooManyListenersException err) {
            throw new com.arsdigita.util.UncheckedWrapperException(err);
        }
	*/
	sc.add(m_statisticsLink);
    }
    public void addStatisticsTable(SimpleContainer sc) {

        m_statisticsTable = new DataTable(new StatisticsTableBuilder());
	m_statisticsTable.addColumn((String) GlobalizationUtil.globalize("simplesurvey.ui.score").localize(), SCORE);
	m_statisticsTable.addColumn((String) GlobalizationUtil.globalize("simplesurvey.ui.number_of_surveys").localize(), SURVEY_NUMBER);
	m_percentColumn = m_statisticsTable.addColumn(
               (String) GlobalizationUtil.globalize("simplesurvey.ui.percentage").localize(), SURVEY_PERCENTAGE, false, new PercentCellRenderer());
	sc.add(m_statisticsTable);

    }
    public void addAssessmentSummary(SimpleContainer sc) {
	
	// This is th esummary of points for the personal assessment type surveys
	m_assessmentSummary = new Label();
	m_assessmentSummary.addPrintListener(new PrintListener() {
            public void prepare(PrintEvent e) {
                PageState ps = e.getPageState();
                BigDecimal responseID = getResponseID(ps);
                final String query = "com.arsdigita.simplesurvey.getAssessmentResults";
                DataQuery dq = SessionManager.getSession().retrieveQuery(query);
                dq.setParameter("responseID", responseID);
                StringBuffer sb = new StringBuffer((String) GlobalizationUtil.globalize("simplesurvey.ui.you_have_scored").localize());
                int i = 0;
                
                while ( dq.next() ){			
                    BigDecimal answerCount = (BigDecimal) dq.get(ANSWER_COUNT);
                    BigDecimal answerValue = (BigDecimal) dq.get(ANSWER_VALUE);
                    
                    if ( i > 0 ) { 
                        sb.append(", "); 
                    }
                    sb.append(" ").append(answerCount.toString()).append(" ");
                    sb.append((String) GlobalizationUtil.globalize
                              ("simplesurvey.ui.answers_worth").localize()); 
                    sb.append(" ").append(answerValue.toString());
                    sb.append(" ").append((String) GlobalizationUtil.globalize
                                          ("simplesurvey.ui.points").localize());
                    i++;
                }
                sb.append(".");
                Label target = (Label) e.getTarget();
                target.setLabel(sb.toString());
            }
	    });
	sc.add(m_assessmentSummary);
    }
    public void addResultsTable(SimpleContainer sc) {

	m_resultsTable = new DataTable(new ResultsTableBuilder());
	m_resultsTable.addColumn((String) GlobalizationUtil.globalize("simplesurvey.ui.question_number").localize(), QUESTION_NUMBER);
	m_resultsTable.addColumn(
			(String) GlobalizationUtil.globalize("simplesurvey.ui.user_answer").localize(), USER_ANSWER);
       
	
	m_resultsTable.addColumn((String) GlobalizationUtil.globalize("simplesurvey.ui.correct_answer").localize(),CORRECT_ANSWER);
	
	sc.add(m_resultsTable);
    }
    private BigDecimal getResponseID(PageState ps) {
        //Figures out whether the response_id is in a RequestLocal or a GLobalParam
        BigDecimal responseID = null;
        if ( m_response.get(ps) !=  null ) {
			responseID = (BigDecimal) m_response.get(ps);
        } else {
			responseID = (BigDecimal) ps.getValue(m_responseID);
        }
        return responseID;
    } 

    private class StatisticsTableBuilder implements DataQueryBuilder {
        
        private final static String KEY_COLUMN = "surveyID";
        public boolean m_locked; 
        public StatisticsTableBuilder() {
            super();
        }
        
        public DataQuery makeDataQuery(DataTable t, PageState ps) {
	   	   
	   Survey s = (Survey) m_survey.get(ps);
	   String query = "com.arsdigita.simplesurvey.getSurveyStatistics";
	      
	   DataQuery dq = SessionManager.getSession().retrieveQuery(query);
       	   dq.setParameter("surveyID", s.getID());
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
    private class ResultsTableBuilder implements DataQueryBuilder {
	    
        private final static String KEY_COLUMN = "answerID";
	public boolean m_locked; 
	public ResultsTableBuilder() {
	    super();
	}

       public DataQuery makeDataQuery(DataTable t, PageState ps) {
	   
	   BigDecimal responseID = getResponseID(ps);
       	   final String query = "com.arsdigita.simplesurvey.getResponseResults";
	   DataQuery dq = SessionManager.getSession().retrieveQuery(query);
       	   dq.setParameter("responseID", responseID);
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
    
    private class ScorePrintListener implements PrintListener{

	private static final String USER_SCORE = "userScore";
	private static final String MAX_SCORE = "maxScore";
	public ScorePrintListener() {
	    super();
	}
	public void prepare(PrintEvent e) {

	    PageState ps = e.getPageState();
	    Label target = (Label) e.getTarget();
	    User user = (User) m_user.get(ps);
	    Survey survey = (Survey) m_survey.get(ps);
	    BigDecimal userScore = null;
	    BigDecimal maxScore = null;

	    BigDecimal responseID = getResponseID(ps);
	    DataQuery dq = SessionManager.getSession().retrieveQuery("com.arsdigita.simplesurvey.getUserScore");
	    dq.setParameter("responseID", responseID);
	    
	    while(dq.next()) {
		userScore = (BigDecimal) dq.get(USER_SCORE);
		maxScore = (BigDecimal) dq.get(MAX_SCORE);
	    }
	    dq.close();
	    Object [] results = { userScore, maxScore };
	    //s_log.warn("userScore=" + userScore + "max=" + maxScore);
	    target.setLabel(GlobalizationUtil.globalize("simplesurvey.ui.you_have_scored_out_of_a_possible", results));
	    	    
     	}	    

    }
    private class PercentCellRenderer extends DefaultTableCellRenderer {

	public PercentCellRenderer() {
	    super(true);
	}
	public Component getComponent (  Table table, PageState s, Object value,
                            boolean isSelected, Object key, int row, int column
                         ) {
	    return new Label(value.toString() + "%");
	}
    }
}

 
