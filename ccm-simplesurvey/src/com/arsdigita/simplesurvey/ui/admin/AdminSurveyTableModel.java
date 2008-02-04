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


// The interface we are implementing
import com.arsdigita.bebop.table.TableModel;

import com.arsdigita.simplesurvey.Survey;
import com.arsdigita.simplesurvey.SurveyCollection;


import com.arsdigita.util.Assert;


/**
 * List of all SimpleSurvey objects. Those
 * are currently the only objects managed by the Form Builder admin UI.
 * 
 *
 * @author <a href="mailto:pmarklun@arsdigita.com">Peter Marklund</a>
 * @version $Id: AdminSurveyTableModel.java 755 2005-09-02 13:42:47Z sskracic $
 *
 */
public class AdminSurveyTableModel implements TableModel {

    public static final String versionId = "$Id: AdminSurveyTableModel.java 755 2005-09-02 13:42:47Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/17 23:26:27 $";

    private SurveyCollection m_surveys;
    private Survey m_survey;

    public AdminSurveyTableModel(SurveyCollection surveys) {
	m_surveys = surveys;
    }

    /**
     * Return the number of columns this table model has.
     *
     * @return the number of columns in the table model
     * @post return >= 0
     */
    public int getColumnCount() {
	return 9;
    }
    
    /**
     * Move to the next row and return true if the model is now positioned on
     * a valid row. Initially, the table model is positioned before the first
     * row. The table will call this method before it retrieves the data for
     * the row with calls to {@link #getElementAt getElementAt} and {@link
     * #getKeyAt getKeyAt}.
     *
     * <p> If this method returns <code>true</code>, subsequent calls to
     * {@link #getElementAt getElementAt} and {@link #getKeyAt getKeyAt} have
     * to succeed and return non-null objects. If this method returns
     * <code>false</code>, the table assumes that it has traversed all the
     * data contained in this model.
     *
     * @return <code>true</code> if the model is positioned on a valid row 
     */
    public boolean nextRow() {
        if (m_surveys.next()) {
	    m_survey = m_surveys.getSurvey();

	    Assert.exists(m_survey, Survey.class);
	    
	    return true;
        }
	
	m_survey = null;
        return false;
    }
    
    /**
     * Return the data element for the given column and the current row. The
     * returned object will be passed to the table cell renderer as the
     * <code>value</code> argument without modifications.
     *
     * @param columnIndex the number of the column for which to get data
     * @return the object to pass to the table cell renderer for display
     * @pre columnIndex >= 0 && columnIndex < getColumnCount()
     * @post return != null 
     * @see TableCellRenderer
     */
    public Object getElementAt(int columnIndex) {
	Assert.exists(m_survey, Survey.class);

        return m_survey;
    }
    
    /**
     * Return the key for the given column and the current row. The key has
     * to be unique for each <em>row</em> in the table model, but does not
     * need to be unique for each row <em>and</em> column, though it may.
     * The key is passed to the table cell renderer as the <code>key</code>
     * argument.
     *
     * @param columnIndex the number of the column for which to get data
     * @return the key for the given column and the current row.
     * @pre columnIndex >= 0 && columnIndex < getColumnCount()
     * @post return != null
     * @see TableCellRenderer
     */
    public Object getKeyAt(int columnIndex) {
        Assert.exists(m_survey, Survey.class);

        return m_survey.getID();
    }
}
