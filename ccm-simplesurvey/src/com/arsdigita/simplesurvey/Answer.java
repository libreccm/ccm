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
package com.arsdigita.simplesurvey;

import com.arsdigita.db.Sequences;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.formbuilder.PersistentLabel;
import com.arsdigita.formbuilder.PersistentWidget;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.metadata.ObjectType;
import java.math.BigDecimal;

/**
 * A SurveyAnswer object represents a response by a user to
 * a survey at a certain point in time. A survey response consists
 * of a collection of answers to questions in the survey.
 * 
 * @author <a href="mailto:pmarklun@arsdigita.com">Peter Marklund</a>
 * @version $Id: Answer.java 759 2005-09-02 15:25:32Z sskracic $
 */
public class Answer extends DomainObject {

    public static final String BASE_DATA_OBJECT_TYPE = 
        "com.arsdigita.simplesurvey.Answer";

    public static final String ID = "id";
    public static final String LABEL = "label";
    public static final String WIDGET = "widget";
    public static final String VALUE = "value";
    public static final String RESPONSE = "response";

    protected String getBaseDataObjectType() {
        return BASE_DATA_OBJECT_TYPE;
    }

    public Answer(DataObject dataObject) {
        super(dataObject);
    }

    protected Answer(String typeName) {
        super(typeName);
    }

    public Answer() {
        this(BASE_DATA_OBJECT_TYPE);
    }

    protected Answer(ObjectType type) throws DataObjectNotFoundException {
        super(type);
    }

    protected Answer(OID oid) throws DataObjectNotFoundException {
        super(oid);
    }

    public Answer(BigDecimal id) throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    public static Answer create(PersistentLabel label,
				PersistentWidget widget,
				String value) {
	Answer answer = new Answer();
	answer.setup(label, widget, value);
	return answer;
    }
    
    protected void setup(PersistentLabel label,
			 PersistentWidget widget,
			 String value) {
	try {
	    set(ID, Sequences.getNextValue("ss_answers_seq"));
	} catch (java.sql.SQLException e) {
            throw new com.arsdigita.util.UncheckedWrapperException(e);
        }
        set(LABEL, label);
        set(WIDGET, widget);
        set(VALUE, value);
    }
    
    public BigDecimal getID() {
	return (BigDecimal)get(ID);
    }
}
