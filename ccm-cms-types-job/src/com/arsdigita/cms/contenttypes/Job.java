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
package com.arsdigita.cms.contenttypes;


import com.arsdigita.cms.ContentPage;
import com.arsdigita.cms.ContentType;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.util.Assert;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.util.Date;

/**
 * This content type represents a job.
 *
 * @version $Revision: #6 $ $Date: 2004/08/17 $
 **/
public class Job extends ContentPage {

    /** PDL property name for grade */
    public static final String GRADE = "grade";
    /** PDL property name for closing date */
    public static final String CLOSING_DATE = "closingDate";
    /** PDL property name for salary */
    public static final String SALARY = "salary";
    /** PDL property name for body */
    public static final String BODY = "body";
    /** PDL property name for reference number */
    public static final String REF_NUMBER = "refNumber";
    /** PDL property name for department */
    public static final String DEPARTMENT = "department";
    /** PDL property name for job description */
    public static final String JOB_DESCRIPTION = "jobDescription";
    /** PDL property name for person specification */
    public static final String PERSON_SPECIFICATION = "personSpecification";
    /** PDL property name for contact details */
    public static final String CONTACT_DETAILS = "contactDetails";

    /** Data object type for this domain object */
    public static final String BASE_DATA_OBJECT_TYPE
        = "com.arsdigita.cms.contenttypes.Job";

    public Job() {
        this(BASE_DATA_OBJECT_TYPE);
    }

    public Job(BigDecimal id) throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    public Job(OID id) throws DataObjectNotFoundException {
        super(id);
    }

    public Job(DataObject obj) {
        super(obj);
    }

    public Job(String type) {
        super(type);
    }


    public void beforeSave() {
        super.beforeSave();
        
        Assert.exists(getContentType(), ContentType.class);
    }

    /* accessors *****************************************************/
    public String getGrade() {
        return (String) get(GRADE);
    }

    public void setGrade(String grade) {
        set(GRADE, grade);
    }

    public Date getClosingDate() {
        return (Date) get(CLOSING_DATE);
    }

    public String getDisplayClosingDate() {
        Date d = getClosingDate();
        return (d != null) ? DateFormat.getDateInstance(DateFormat.LONG)
            .format(d) : null;
    }


    public void setClosingDate(Date closingDate) {
        set(CLOSING_DATE, closingDate);
    }

    public String getSalary() {
        return (String) get(SALARY);
    }

    public void setSalary(String salary) {
        set(SALARY, salary);
    }

    public String getBody() {
        return (String) get(BODY);
    }

    public void setBody(String body) {
        set(BODY, body);
    }

    public String getRefNumber() {
        return (String) get(REF_NUMBER);
    }

    public void setRefNumber(String refNumber) {
        set(REF_NUMBER, refNumber);
    }

    public String getDepartment() {
        return (String) get(DEPARTMENT);
    }

    public void setDepartment(String department) {
        set(DEPARTMENT, department);
    }

    public String getJobDescription() {
        return (String) get(JOB_DESCRIPTION);
    }

    public void setJobDescription(String jobDescription) {
        set(JOB_DESCRIPTION, jobDescription);
    }

    public String getPersonSpecification() {
        return (String) get(PERSON_SPECIFICATION);
    }

    public void setPersonSpecification(String personSpecification) {
        set(PERSON_SPECIFICATION, personSpecification);
    }

    public String getContactDetails() {
        return (String) get(CONTACT_DETAILS);
    }

    public void setContactDetails(String contactDetails) {
        set(CONTACT_DETAILS, contactDetails);
    }


    public static final int SUMMARY_LENGTH = 200;
    public String getSearchSummary() {
        return com.arsdigita.util.StringUtils.truncateString(getJobDescription(),
                                                             SUMMARY_LENGTH,
                                                             true);
    }
}
