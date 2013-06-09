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
package com.arsdigita.cms.contenttypes.ui;


import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.DateParameter;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.Job;
import com.arsdigita.cms.contenttypes.util.JobGlobalizationUtil;
import com.arsdigita.cms.ui.authoring.BasicPageForm;
import com.arsdigita.cms.ui.CMSDHTMLEditor;
import com.arsdigita.cms.util.GlobalizationUtil;


/**
 * Form to edit the basic properties of an job. This form can be extended to
 * create forms for Job subclasses.
 **/
public class JobPropertyForm extends BasicPageForm
    implements FormProcessListener, FormInitListener, FormSubmissionListener {

    private JobPropertiesStep m_step;

    /**  body parameter name */
    public static final String BODY = "body";
    /** Grade parameter name */
    public static final String GRADE = "grade";
    /** closing date parameter name */
    public static final String CLOSING_DATE = "closingDate";
    /** salary parameter name */
    public static final String SALARY = "salary";
    /** Ref number parameter name */
    public static final String REF_NUMBER = "ref_number";
    /** department parameter name */
    public static final String DEPARTMENT = "department";
    /**  job description parameter name */
    public static final String JOB_DESCRIPTION = "jobDescription";
    /**  person specification parameter name */
    public static final String PERSON_SPECIFICATION = "personSpecification";
    /**  contact details parameter name */
    public static final String CONTACT_DETAILS = "contactDetails";

    /** Name of this form */
    public static final String ID = "job_edit";

    /**
     * Creates a new form to edit the Job object specified
     * by the item selection model passed in.
     * @param itemModel The ItemSelectionModel to use to obtain the
     *    Job to work on
     */
    public JobPropertyForm( ItemSelectionModel itemModel ) {
        this( itemModel, null );
    }

    /**
     * Creates a new form to edit the Job object specified
     * by the item selection model passed in.
     * @param itemModel The ItemSelectionModel to use to obtain the
     *    Job to work on
     * @param step The JobPropertiesStep which controls this form.
     */
    public JobPropertyForm( ItemSelectionModel itemModel, JobPropertiesStep step ) {
        super( ID, itemModel );
        m_step = step;
        addSubmissionListener(this);
    }

    /**
     * Adds widgets to the form.
     **/
    protected void addWidgets() {
        super.addWidgets();

        // Job content type currently does not use the default 
        // basic descriuption properties (as persisted in cms-pages and by
        // default part of the object list). Would be convenient to move the
        // ct specific overview property to basic description.
        add(new Label(JobGlobalizationUtil
                      .globalize("cms.contenttypes.ui.job.overview")));
        ParameterModel bodyParam = new StringParameter(BODY);
        CMSDHTMLEditor body = new CMSDHTMLEditor(bodyParam);
        body.setCols(40);
        body.setRows(10);
        add(body);

        add(new Label(JobGlobalizationUtil
                      .globalize("cms.contenttypes.ui.job.grade")));
        ParameterModel gradeParam
            = new StringParameter(GRADE);
        TextField grade = new TextField(gradeParam);
        grade.setSize(30);
        grade.setMaxLength(30);
        add(grade);

        add(new Label(JobGlobalizationUtil
                      .globalize("cms.contenttypes.ui.job.closing_date")));
        ParameterModel closingDateParam
            = new DateParameter(CLOSING_DATE);
        com.arsdigita.bebop.form.Date closingDate
            = new com.arsdigita.bebop.form.Date(closingDateParam );
        add(closingDate);

        add(new Label(JobGlobalizationUtil
                      .globalize("cms.contenttypes.ui.job.salary")));
        ParameterModel salaryParam = new StringParameter(SALARY);
        CMSDHTMLEditor salary = new CMSDHTMLEditor(salaryParam);
        salary.setCols(40);
        salary.setRows(10);
        add(salary);


        add(new Label(JobGlobalizationUtil
                      .globalize("cms.contenttypes.ui.job.ref_number")));
        ParameterModel refNumberParam = new StringParameter(REF_NUMBER);
        TextField refNumber = new TextField(refNumberParam);
        refNumber.setSize(30);
        refNumber.setMaxLength(30);
        add(refNumber);

        add(new Label(JobGlobalizationUtil
                      .globalize("cms.contenttypes.ui.job.department")));
        ParameterModel departmentParam = new StringParameter(DEPARTMENT);
        TextField department = new TextField(departmentParam);
        department.setSize(30);
        department.setMaxLength(30);
        add(department);

        add(new Label(JobGlobalizationUtil
                      .globalize("cms.contenttypes.ui.job.job_description")));
        ParameterModel jobDescriptionParam =
            new StringParameter(JOB_DESCRIPTION);
        CMSDHTMLEditor jobDescription = new CMSDHTMLEditor(jobDescriptionParam);
        jobDescription.setCols(40);
        jobDescription.setRows(10);
        add(jobDescription);

        add(new Label(JobGlobalizationUtil
                      .globalize("cms.contenttypes.ui.job.person_specification")));
        ParameterModel personSpecificationParam =
            new StringParameter(PERSON_SPECIFICATION);
        CMSDHTMLEditor personSpecification = new CMSDHTMLEditor(personSpecificationParam);
        personSpecification.setCols(40);
        personSpecification.setRows(10);
        add(personSpecification);

        add(new Label(JobGlobalizationUtil
                      .globalize("cms.contenttypes.ui.job.contact_details")));
        ParameterModel contactDetailsParam
            = new StringParameter(CONTACT_DETAILS);
        CMSDHTMLEditor contactDetails = new CMSDHTMLEditor(contactDetailsParam);
        contactDetails.setCols(40);
        contactDetails.setRows(10);
        add(contactDetails);
    }

    /** Form initialisation hook. Fills widgets with data. */
    public void init(FormSectionEvent fse) {
        FormData data = fse.getFormData();
        Job job = (Job) super.initBasicWidgets(fse);

        data.put(CLOSING_DATE,         job.getClosingDate());
        data.put(BODY,                 job.getBody());
        data.put(JOB_DESCRIPTION,      job.getJobDescription());
        data.put(PERSON_SPECIFICATION, job.getPersonSpecification());
        data.put(GRADE,                job.getGrade());
        data.put(REF_NUMBER,           job.getRefNumber());
        data.put(DEPARTMENT,           job.getDepartment());
        data.put(SALARY,               job.getSalary());
        data.put(CONTACT_DETAILS,      job.getContactDetails());
    }


    /** Cancels streamlined editing. */
    public void submitted( FormSectionEvent fse ) {
        if (m_step != null &&
            getSaveCancelSection().getCancelButton()
            .isSelected( fse.getPageState())) {
            m_step.cancelStreamlinedCreation(fse.getPageState());
        }
    }

    /** Form processing hook. Saves Job object. */
    public void process(FormSectionEvent fse) {
        FormData data = fse.getFormData();

        Job job
            = (Job) super.processBasicWidgets(fse);

        // save only if save button was pressed
        if (job != null
            && getSaveCancelSection().getSaveButton()
            .isSelected(fse.getPageState())) {

            job.setGrade((String) data.get(GRADE));
            job.setRefNumber((String) data.get(REF_NUMBER));
            job.setDepartment((String) data.get(DEPARTMENT));
            job.setClosingDate((java.util.Date) data.get(CLOSING_DATE));
            job.setBody((String) data.get(BODY));
            job.setJobDescription((String) data.get(JOB_DESCRIPTION));
            job.setPersonSpecification((String) data.get(PERSON_SPECIFICATION));
            job.setSalary((String) data.get(SALARY));
            job.setContactDetails((String) data.get(CONTACT_DETAILS));
            job.save();
        }
        if (m_step != null) {
            m_step.maybeForwardToNextStep(fse.getPageState());
        }
    }
}
