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
import com.arsdigita.bebop.event.ActionListener;

import com.arsdigita.bebop.event.ActionEvent;


import com.arsdigita.toolbox.ui.DataTable;
import com.arsdigita.toolbox.ui.DataQueryBuilder;


import com.arsdigita.bebop.table.TableCellRenderer;

import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.Component;


import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.SessionManager;

import com.arsdigita.util.LockableImpl;



import java.math.BigDecimal;

import com.arsdigita.bebop.SingleSelectionModel;
import com.arsdigita.simplesurvey.ui.SurveySelectionModel;
import com.arsdigita.bebop.SimpleContainer;


/**
 * A page displaying the responses to a particular Survey
 *
 * @author <a href="mailto:pmarklun@arsdigita.com">Peter Marklund</a>
 * @version $Id: OneResponsePanel.java 755 2005-09-02 13:42:47Z sskracic $
 */
public class OneResponsePanel extends SimpleContainer {


    private SingleSelectionModel m_response;
    private SurveySelectionModel m_survey;

    // Components of the page
    private ResponseTable m_responseTable;

    public OneResponsePanel(SurveySelectionModel survey,
			    SingleSelectionModel response) {
	m_survey = survey;
	m_response = response;

	ActionLink m_complete = new ActionLink( (String) GlobalizationUtil.globalize("simplesurvey.ui.admin.back_to_response_list").localize());
	m_complete.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    m_response.clearSelection(e.getPageState());
		}
	    });
	add(m_complete);

        // Add the table with the response
        m_responseTable = new ResponseTable(m_response);
        add(m_responseTable);
    }


    private class ResponseTable extends DataTable {

        public ResponseTable(SingleSelectionModel response) {
            super(new ResponseQueryBuilder(response));

            addColumn("Question", "attributeString", true, new QuestionTableCellRenderer());
            addColumn("Answer", "answerValue", false);
        }
    }

    private static class ResponseQueryBuilder extends LockableImpl
        implements DataQueryBuilder {

	private SingleSelectionModel m_response;

        public ResponseQueryBuilder(SingleSelectionModel response) {
	    m_response = response;
	}

        public DataQuery makeDataQuery(DataTable t, PageState s) {

            String queryName = "com.arsdigita.simplesurvey.GetAnswersForResponse";
            DataQuery dataQuery =
                SessionManager.getSession().retrieveQuery(queryName);
	    BigDecimal responseID = new BigDecimal(m_response.getSelectedKey(s).toString());
	    dataQuery.setParameter("responseID", responseID);
                
            return dataQuery;
        }

        public String getKeyColumn() {
            return "answerID";
        }        
    }

    private class QuestionTableCellRenderer
        implements TableCellRenderer {

        public Component getComponent(Table table, PageState state, Object value,
                                      boolean isSelected, Object key, 
                                      int row, int column) {

            return new Label(ReportPanel.getLabelAttribute((String)value));
        }
    }

}
