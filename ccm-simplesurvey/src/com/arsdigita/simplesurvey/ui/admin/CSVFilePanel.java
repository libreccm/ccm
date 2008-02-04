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
import com.arsdigita.bebop.Label;


import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.event.PrintEvent;





import com.arsdigita.simplesurvey.Survey;

import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.SessionManager;





import java.util.HashMap;

import java.math.BigDecimal;
import com.arsdigita.simplesurvey.ui.SurveySelectionModel;
import com.arsdigita.bebop.SimpleContainer;

import org.apache.log4j.Logger;

/**
 * A page displaying the responses to a particular Survey
 *
 * @author <a href="mailto:pmarklun@arsdigita.com">Peter Marklund</a>
 * @version $Id: CSVFilePanel.java 755 2005-09-02 13:42:47Z sskracic $
 */
public class CSVFilePanel extends SimpleContainer {

    private final static Logger s_log =
        Logger.getLogger(CSVFilePanel.class.getName());        

    private SurveySelectionModel m_survey;

    public CSVFilePanel(SurveySelectionModel survey) {
	m_survey = survey;
	
	setIdAttr("csvPage");

        Label csvLabel = new Label("");
	csvLabel.setIdAttr("csvFile");
        csvLabel.addPrintListener(new PrintListener() {
            public void prepare(PrintEvent event) {
                PageState pageState = event.getPageState();
                Label label = (Label)event.getTarget();

                label.setLabel(getCSVContent(pageState), pageState);
            }
        });

        add(csvLabel);
    }

    private String getCSVContent(PageState state) {

        StringBuffer buffer = new StringBuffer();

        HashMap labelColumnMap = new HashMap();

        // Add the headings (the question texts)
        Survey survey = m_survey.getSelectedSurvey(state);
        DataQuery labelQuery = survey.getLabelDataQuery();
        boolean hasNext = labelQuery.next();
        int columnNumber = 0;
        while (hasNext) {
            String attributeString = (String)labelQuery.get("attributeString");
            String question = ReportPanel.getLabelAttribute(attributeString);
            BigDecimal labelID = (BigDecimal)labelQuery.get("labelID");

            buffer.append(question);
            
            hasNext = labelQuery.next();
            if (hasNext) {
                buffer.append(", ");
            }

            s_log.debug("adding labelID " + labelID + " with column number " +
                        columnNumber);

            labelColumnMap.put(labelID, new Integer(columnNumber));

            ++columnNumber;
        }
        buffer.append("\n");

        // Add all answers
        String queryName = "com.arsdigita.simplesurvey.GetAllAnswers";
        DataQuery answerQuery = SessionManager.getSession().retrieveQuery(queryName);
        answerQuery.setParameter("surveyID", survey.getID());
        BigDecimal previousResponseID = null;
        String[] rowAnswers = new String[labelColumnMap.keySet().size()];
        // initialize the answer columns
        for (int i = 0; i < rowAnswers.length; ++i) {
            rowAnswers[i] = "";
        }                
        while (answerQuery.next()) {
            String answer = (String)answerQuery.get("answerValue");
            if (answer == null || answer.equals("null")) {
                answer = "";
            }
            BigDecimal responseID = (BigDecimal)answerQuery.get("responseID");
            BigDecimal labelID = (BigDecimal)answerQuery.get("labelID");

            if (previousResponseID != null && !responseID.equals(previousResponseID)) {
                // This is the first entry on a line

                // Flush the previous line
                buffer.append(rowAnswers[0]);
                for (int i = 1; i < rowAnswers.length; ++i) {
                    buffer.append(", ");

                    buffer.append(rowAnswers[i]);
                }
                // Add a new line charachter
                buffer.append("\n");

                // initialize the answer columns for the new row
                for (int i = 0; i < rowAnswers.length; ++i) {
                    rowAnswers[i] = "";
                }                

                // Add the answer to the answers for this row
                columnNumber = ((Integer)labelColumnMap.get(labelID)).intValue();
                s_log.debug("adding labelID " + labelID + " to column " + columnNumber);
                rowAnswers[columnNumber] = answer;

            } else if (previousResponseID == null || responseID.equals(previousResponseID)) {
                // This is the first entry on the first line or it 
                // is not the first entry on a line

                // Add the answer to the answers for this row
                columnNumber = ((Integer)labelColumnMap.get(labelID)).intValue();                
                s_log.debug("adding labelID " + labelID + " to column " + columnNumber);

		if (rowAnswers[columnNumber].equals("")) {
		    rowAnswers[columnNumber] = answer;
		} else {
		    // For multiple answer questions we separate answers with the pipe charachter
		    rowAnswers[columnNumber] = rowAnswers[columnNumber] + " | " + answer;
		}
            }

            previousResponseID = responseID;
        }

        // Flush the previous line
	if (rowAnswers.length > 0) {
	    buffer.append(rowAnswers[0]);
	    for (int i = 1; i < rowAnswers.length; ++i) {
		buffer.append(", ");

		buffer.append(rowAnswers[i]);
	    }
	}

        return buffer.toString();
    }
}


