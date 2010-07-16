package com.arsdigita.cms.contenttypes.ui.project;

import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.FormSection;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SaveCancelSection;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.event.FormValidationListener;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.cms.ContentType;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.Member;
import com.arsdigita.cms.contenttypes.Project;
import com.arsdigita.cms.contenttypes.Project2Person;
import com.arsdigita.cms.contenttypes.ProjectGlobalizationUtil;
import com.arsdigita.cms.ui.ItemSearchWidget;
import com.arsdigita.util.Assert;
import com.arsdigita.util.UncheckedWrapperException;
import org.apache.log4j.Logger;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 */
public class Project2PersonPropertyForm
        extends FormSection
        implements FormInitListener,
        FormProcessListener,
        FormValidationListener,
        FormSubmissionListener {

    private final static Logger s_log =
            Logger.getLogger(Project2PersonPropertyForm.class);
    private ItemSelectionModel m_itemModel;
    private Project2PersonSelectionModel m_project2PersonModel;
    private ItemSearchWidget m_itemSearch;
    private SaveCancelSection m_saveCancelSection;
    private final String ITEM_SEARCH = "project2Person";

    public Project2PersonPropertyForm(
            ItemSelectionModel itemModel,
            Project2PersonSelectionModel project2PersonModel) {
        super(new ColumnPanel(2));
        m_itemModel = itemModel;
        m_project2PersonModel = project2PersonModel;

        addWidgets();
        addSaveCancelSection();

        addInitListener(this);
        addValidationListener(this);
        addProcessListener(this);
        addSubmissionListener(this);
    }

    protected void addWidgets() {
        add(new Label(ProjectGlobalizationUtil.globalize(
                "cms.contenttypes.ui.project.person")));
        m_itemSearch = new ItemSearchWidget(ITEM_SEARCH,
                ContentType.findByAssociatedObjectType(
                "com.arsdigita.cms.contenttypes.Person"));
        add(m_itemSearch);
    }

    public void addSaveCancelSection() {
        m_saveCancelSection = new SaveCancelSection();
        try {
            m_saveCancelSection.getCancelButton().addPrintListener(new PrintListener() {

                public void prepare(PrintEvent e) {
                    Submit target = (Submit) e.getTarget();
                    if (m_project2PersonModel.isSelected(e.getPageState())) {
                        target.setButtonLabel("Cancel");
                    } else {
                        target.setButtonLabel("Reset");
                    }
                }
            });

            m_saveCancelSection.getSaveButton().addPrintListener(new PrintListener() {

                public void prepare(PrintEvent e) {
                    Submit target = (Submit) e.getTarget();
                    if (m_project2PersonModel.isSelected(e.getPageState())) {
                        target.setButtonLabel("Save");
                    } else {
                        target.setButtonLabel("Create");
                    }
                }
            });
        } catch (Exception ex) {
            throw new UncheckedWrapperException("this cannot happen", ex);
        }
        add(m_saveCancelSection, ColumnPanel.FULL_WIDTH);
    }

    public SaveCancelSection getSaveCancelSection() {
        return m_saveCancelSection;
    }

    protected Project2PersonSelectionModel getProject2PersonSelectionModel() {
        return m_project2PersonModel;
    }

    protected Project getProject(PageState s) {
        return (Project) m_itemModel.getSelectedItem(s);
    }

    protected Project2Person createProject2Person(PageState s) {
        Project project = getProject(s);
        Assert.exists(project);
        Project2Person project2Person = new Project2Person();
        project2Person.setProject(project);
        return project2Person;
    }

    protected void setProject2PersonProperties(
            Project2Person project2Person,
            FormSectionEvent e) {
        PageState state = e.getPageState();
        FormData data = e.getFormData();

        project2Person.setTargetItem((Member) data.get(ITEM_SEARCH));

        project2Person.save();
    }

    public void init(FormSectionEvent e) throws FormProcessException {
        FormData data = e.getFormData();
        PageState state = e.getPageState();

        setVisible(state, true);

        Project2Person project2Person;
        if (m_project2PersonModel.isSelected(state)) {
            project2Person = m_project2PersonModel.getSelectedP2P(state);
            try {
                data.put(ITEM_SEARCH, project2Person.getTargetItem());
            } catch(IllegalStateException ex) {
                throw ex;
            }
        } else {
            data.put(ITEM_SEARCH, null);
        }
    }

    public void process(FormSectionEvent e) throws FormProcessException {
        PageState state = e.getPageState();
        Project2Person project2Person;

        if (getSaveCancelSection().getCancelButton().isSelected(state)) {
            m_project2PersonModel.clearSelection(state);
        } else {
            if (m_project2PersonModel.isSelected(state)) {
                project2Person = m_project2PersonModel.getSelectedP2P(state);
            } else {
                project2Person = createProject2Person(state);
            }
            setProject2PersonProperties(project2Person, e);
        }

        m_project2PersonModel.clearSelection(state);
        init(e);
    }

    public void validate(FormSectionEvent e) throws FormProcessException {
        if (e.getFormData().get(ITEM_SEARCH) == null) {
            throw new FormProcessException("Person selection is required");
        }
    }

    public void submitted(FormSectionEvent e) throws FormProcessException {
        if (m_saveCancelSection.getCancelButton().isSelected(
                e.getPageState())) {
            m_project2PersonModel.clearSelection(e.getPageState());
            init(e);
            throw new FormProcessException("cancelled");
        }
    }
}
