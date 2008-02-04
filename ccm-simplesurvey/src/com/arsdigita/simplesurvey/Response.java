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

import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.formbuilder.PersistentLabel;
import com.arsdigita.formbuilder.PersistentWidget;
import com.arsdigita.kernel.ACSObject;
import com.arsdigita.kernel.User;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.metadata.ObjectType;
import java.math.BigDecimal;
import java.util.Date;

/**
 * A SurveyResponse object represents a response by a user to
 * a survey at a certain point in time. A survey response consists
 * of a collection of answers to questions in the survey.
 * 
 * @author <a href="mailto:pmarklun@arsdigita.com">Peter Marklund</a>
 * @version $Id: Response.java 759 2005-09-02 15:25:32Z sskracic $
 */
public class Response extends ACSObject {

    public static final String BASE_DATA_OBJECT_TYPE = 
        "com.arsdigita.simplesurvey.Response";

    public static final String SURVEY = "survey";
    public static final String USER = "user";
    public static final String SCORE = "score";
    public static final String ENTRY_DATE = "entryDate";
    public static final String ANSWERS = "answers";
    public static final String QUESTIONS_ANSWERED = "questionsAnswered";

    protected String getBaseDataObjectType() {
        return BASE_DATA_OBJECT_TYPE;
    }

    public Response(DataObject dataObject) {
        super(dataObject);
    }

    protected Response(String typeName) {
        super(typeName);
    }
 
    public Response() {
        this(BASE_DATA_OBJECT_TYPE);
    }

    protected Response(ObjectType type) throws DataObjectNotFoundException {
        super(type);
    }

    protected Response(OID oid) throws DataObjectNotFoundException {
        super(oid);
    }

    public Response(BigDecimal id) throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    public static Response create(Survey survey,
				  User user) {
	Response response = new Response();
	response.setup(survey, user);
	response.setScore(new BigDecimal(0));
	return response;
    }
    
    protected void setup(Survey survey,
			 User user) {
        set(ENTRY_DATE, new Date());
	
	// XXX hack - see pdl file
	set(USER + "ID", user.getID());
	set(SURVEY + "ID", survey.getID());
        //set(SURVEY, survey);
        //set(USER, user);
    }

    public static Response retrieve(DataObject obj) {
	return new Response(obj);
    }

    public static ResponseCollection retrieveBySurvey(Survey survey) {
	DataCollection responses = 
            SessionManager.getSession().retrieve(BASE_DATA_OBJECT_TYPE);
	

	responses.addEqualsFilter(SURVEY + "ID",
				  survey.getID());
	
	return new ResponseCollection(responses);
    }


    public static ResponseCollection retrieveBySurvey(Survey survey,
						      User user) {
	ResponseCollection responses = retrieveBySurvey(survey);
	
	responses.addEqualsFilter(USER + "ID",
				  user.getID());
	
	return responses;
    }
    public boolean questionsAnswered() {
	
	// Returns true of questions have been answered on this response
	BigDecimal responseID = this.getID();
	DataQuery dq = SessionManager.getSession().retrieveQuery("com.arsdigita.simplesurvey.questionsAnswered");
	dq.setParameter("responseID", responseID);
	dq.next();
	Boolean questionsAnswered = (Boolean) dq.get(QUESTIONS_ANSWERED);
       	dq.close();
	return questionsAnswered.booleanValue();

    }

    //*** Attribute Methods
    public void addAnswer(PersistentLabel label, 
			  PersistentWidget widget,
			  String value) {
	Answer answer = Answer.create(label, 
				      widget, 
				      value);
	add(ANSWERS, answer);
    }
    
    public Date getEntryDate() {
	return (Date)get(ENTRY_DATE);
    }

    public BigDecimal getScore() {
	return (BigDecimal) get(SCORE);
    }
    public void setScore(BigDecimal score) {
	set(SCORE, score);
    }
}
