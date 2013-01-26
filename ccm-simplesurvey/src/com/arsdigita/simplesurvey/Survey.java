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


import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.metadata.ObjectType;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataQuery;
import java.math.BigDecimal;
import com.arsdigita.persistence.metadata.ObjectType;
import com.arsdigita.domain.DataObjectNotFoundException;

import com.arsdigita.formbuilder.PersistentForm;


import com.arsdigita.kernel.ACSObject;
import com.arsdigita.kernel.User;
// import com.arsdigita.kernel.PackageInstance;

import java.util.Date;

/**
 * A Survey domain object that represents a simple survey. This is
 * the main domain object of the Simple Survey application.
 *
 * @author <a href="mailto:pmarklun@arsdigita.com">Peter Marklund</a>
 * @version $Id: Survey.java 2286 2012-03-11 09:14:14Z pboy $
 */
public class Survey extends ACSObject {

    public static final String BASE_DATA_OBJECT_TYPE = 
        "com.arsdigita.simplesurvey.Survey";

    // Object type attribute names
    public static final String FORM_SECTION = "formSection";
    public static final String PACKAGE_INSTANCE = "packageInstance";
    public static final String START_DATE = "startDate";
    public static final String END_DATE = "endDate";    
    public static final String RESPONSES_PUBLIC = "responsesPublic";
    public static final String QUIZ_TYPE = "quizType";

    protected String getBaseDataObjectType() {
        return BASE_DATA_OBJECT_TYPE;
    }

    public Survey(DataObject dataObject) {
        super(dataObject);
    }

    public Survey(String typeName) {
        super(typeName);
    }

    public Survey() {
        this(BASE_DATA_OBJECT_TYPE);
    }

    public Survey(ObjectType type) throws DataObjectNotFoundException {
        super(type);
    }

    public Survey(OID oid) throws DataObjectNotFoundException {
        super(oid);	
    }

    public Survey(BigDecimal id) throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }


    public static Survey retrieve(BigDecimal id) 
	throws DataObjectNotFoundException {

        Survey survey = new Survey(id);

        return survey;
    }

    public static Survey retrieve(DataObject obj) {
        Survey survey = new Survey(obj);

        return survey;
    }
    
    /**
     * Retrieves a SurveyCollection of all Surveys ever created, sorted by
     * its start date.
     * 
     * @return 
     */
    public static SurveyCollection retrieveAll() {

        DataCollection surveys = SessionManager.getSession()
                                               .retrieve(BASE_DATA_OBJECT_TYPE);
	
    /*
        surveys.addEqualsFilter(PACKAGE_INSTANCE + "." + ACSObject.ID, 
                                pack.getID());
	*/
        surveys.addOrder(START_DATE);
        return new SurveyCollection(surveys);
    }

    /**
     * 
     * @return 
     */
    public ResponseCollection getResponses() {
        return Response.retrieveBySurvey(this);
    }

    public ResponseCollection getUserResponses(User user) {
	return Response.retrieveBySurvey(this, user);
    }

    public boolean hasUserResponded(User user) {
	ResponseCollection responses = getUserResponses(user);
	
	if (responses.next()) {
	    responses.close();
	    return true;
	}
	return false;
    }

    //*** Attribute Methods
    public void setForm(PersistentForm persistentForm) {        
        set(FORM_SECTION, persistentForm);
    }

    public PersistentForm getForm() {
	return new PersistentForm((DataObject)get(FORM_SECTION));
    }

    public void setStartDate(Date startDate) {
        set(START_DATE, startDate);
    }

    public Date getStartDate() {
        return (Date)get(START_DATE);
    }

    public void setEndDate(Date endDate) {
        set(END_DATE, endDate);
    }

    public Date getEndDate() {
        return (Date)get(END_DATE);
    }

    public void setQuizType(String quizType) {
	set(QUIZ_TYPE, quizType);
    }
    public String getQuizType() {
	return (String) get(QUIZ_TYPE);
    }
    public boolean responsesArePublic() {
	return ((Boolean) get(RESPONSES_PUBLIC)).booleanValue();
    }
    public void setResponsesPublic(Boolean responsesPublic) {
	set(RESPONSES_PUBLIC, responsesPublic);
    }

    public DataQuery getLabelDataQuery() {
        String queryName = "com.arsdigita.simplesurvey.GetFormLabels";
        DataQuery dataQuery =
            SessionManager.getSession().retrieveQuery(queryName);
        dataQuery.setParameter("surveyID", getID());

        return dataQuery;
    }

    public boolean isLive() {
	Date currentDate = new Date();

	return getStartDate().compareTo(currentDate) < 0 &&
	       getEndDate().compareTo(currentDate) > 0;
    }

//  Mo longer useful. PackageInstance is old style app no longer used.  
//  public void setPackageInstance(PackageInstance packageInstance) {
//      set(PACKAGE_INSTANCE, packageInstance);
//  }

    /*
     * Retrieves most recent survey that isn't completed
     */
    public static Survey getMostRecentSurvey() {
	DataCollection surveys = SessionManager.getSession().retrieve(BASE_DATA_OBJECT_TYPE);
	surveys.addFilter("startDate <= sysdate and endDate > sysdate");
	surveys.addOrder("startDate desc");
	
	Survey survey = null;
	if (surveys.next()) {
	    survey = new Survey(surveys.getDataObject());
	}
	surveys.close();
	
	return survey;
    }
}
