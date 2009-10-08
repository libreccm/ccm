package com.arsdigita.cms.contenttypes.ui.project;

import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.form.Date;
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.DateParameter;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.Project;
import com.arsdigita.cms.contenttypes.ProjectGlobalizationUtil;
import com.arsdigita.cms.ui.authoring.BasicPageForm;
import org.apache.log4j.Logger;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 */
public class ProjectPropertyForm extends BasicPageForm
        implements FormProcessListener, FormInitListener, FormSubmissionListener {

    private final static Logger s_log = Logger.getLogger(ProjectPropertyForm.class);
    private ProjectPropertiesStep m_step;
    public final static String PROJECTNAME = Project.PROJECTNAME;
    public final static String PROJECT_DESCRIPTION = Project.PROJECT_DESCRIPTION;
    public final static String FUNDING = Project.FUNDING;
    public final static String BEGINDATE = Project.BEGINDATE;
    public final static String ENDDATE = Project.ENDDATE;
    //public final static String FINISHED = Project.FINISHED;
    public final static String ID = "Project_edit";

    public ProjectPropertyForm(ItemSelectionModel itemModel) {
        this(itemModel, null);
    }

    public ProjectPropertyForm(ItemSelectionModel itemModel,
            ProjectPropertiesStep step) {
        super(ID, itemModel);
        s_log.debug("Creating PropertyForm for project...");
        m_step = step;
        addSubmissionListener(this);
    }

    @Override
    public void addWidgets() {
        s_log.debug("addWidgets called...");
        s_log.debug("Adding widgets of super class...");
        super.addWidgets();

        s_log.debug("Adding own widgets...");
        add(new Label(ProjectGlobalizationUtil.globalize("cms.contenttypes.ui.project.projectname")));
        ParameterModel projectNameParam = new StringParameter(PROJECTNAME);
        TextField projectName = new TextField(projectNameParam);
        projectName.addValidationListener(new NotNullValidationListener());
        add(projectName);

        add(new Label(ProjectGlobalizationUtil.globalize("cms.contenttypes.ui.project.description")));
        TextArea description = new TextArea(PROJECT_DESCRIPTION);
        description.setRows(5);
        description.setCols(30);
        add(description);

        add(new Label(ProjectGlobalizationUtil.globalize("cms.contenttypes.ui.project.funding")));
        TextArea funding = new TextArea(FUNDING);
        funding.setRows(5);
        funding.setCols(30);
        add(funding);

        add(new Label(ProjectGlobalizationUtil.globalize("cms.contenttypes.ui.project.begin")));
        ParameterModel beginParam = new DateParameter(BEGINDATE);
        Date begin = new Date(beginParam);
        add(begin);

        add(new Label(ProjectGlobalizationUtil.globalize("cms.contenttypes.ui.project.end")));
        ParameterModel endParam = new DateParameter(ENDDATE);
        Date end = new Date(endParam);
        add(end);
    }

    @Override
    public void init(FormSectionEvent e) throws FormProcessException {
        FormData data = e.getFormData();
        Project project = (Project) super.initBasicWidgets(e);       

        data.put(PROJECTNAME, project.getProjectName());
        data.put(PROJECT_DESCRIPTION, project.getProjectDescription());
        data.put(FUNDING, project.getFunding());
        data.put(BEGINDATE, project.getBegin());
        data.put(ENDDATE, project.getEnd());
    }

    @Override
    public void process(FormSectionEvent e) throws FormProcessException {
        FormData data = e.getFormData();

        Project project = (Project) super.processBasicWidgets(e);

        if ((project != null) &&
                (getSaveCancelSection().getSaveButton().isSelected(e.getPageState()))) {
            project.setProjectName((String) data.get(PROJECTNAME));
            project.setProjectDescription((String) data.get(PROJECT_DESCRIPTION));
            project.setFunding((String) data.get(FUNDING));
            project.setBegin((java.util.Date) data.get(BEGINDATE));
            project.setEnd((java.util.Date) data.get(ENDDATE));

            project.save();
        }

        if (m_step != null) {
            m_step.maybeForwardToNextStep(e.getPageState());
        }
    }

    public void submitted(FormSectionEvent e) throws FormProcessException {
        if ((m_step != null) &&
                (getSaveCancelSection().getCancelButton().isSelected(e.getPageState()))) {
            m_step.cancelStreamlinedCreation(e.getPageState());
        }
    }
}
