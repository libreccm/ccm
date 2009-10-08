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
import com.arsdigita.cms.contenttypes.OrganizationalUnit;
import com.arsdigita.cms.contenttypes.Project;
import com.arsdigita.cms.contenttypes.Project2OrgaUnit;
import com.arsdigita.cms.contenttypes.ProjectGlobalizationUtil;
import com.arsdigita.cms.ui.ItemSearchWidget;
import com.arsdigita.util.Assert;
import com.arsdigita.util.UncheckedWrapperException;
import org.apache.log4j.Logger;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 */
public class Project2OrgaUnitPropertyForm
        extends FormSection
        implements FormInitListener,
        FormProcessListener,
        FormValidationListener,
        FormSubmissionListener {

    private final static Logger s_log =
            Logger.getLogger(Project2OrgaUnitPropertyForm.class);
    private ItemSelectionModel m_itemModel;
    private Project2OrgaUnitSelectionModel m_p2ouModel;
    private ItemSearchWidget m_itemSearch;
    private SaveCancelSection m_saveCancelSection;
    private final String ITEM_SEARCH = "project2OrgaUnit";

    public Project2OrgaUnitPropertyForm(
            ItemSelectionModel itemModel,
            Project2OrgaUnitSelectionModel p2ouModel) {
        super(new ColumnPanel(2));
        m_itemModel = itemModel;
        m_p2ouModel = p2ouModel;

        addWidgets();
        addSaveCancelSection();

        addInitListener(this);
        addValidationListener(this);
        addProcessListener(this);
        addSubmissionListener(this);
    }

    protected void addWidgets() {
        add(new Label(ProjectGlobalizationUtil.globalize(
                "cms.contenttypes.ui.project.orgaunit")));
        m_itemSearch = new ItemSearchWidget(ITEM_SEARCH,
                ContentType.findByAssociatedObjectType(
                "com.arsdigita.cms.contenttypes.OrganizationalUnit"));
        add(m_itemSearch);
    }

    public void addSaveCancelSection() {
        m_saveCancelSection = new SaveCancelSection();
        try {
            m_saveCancelSection.getCancelButton().addPrintListener(new PrintListener() {

                public void prepare(PrintEvent e) {
                    Submit target = (Submit) e.getTarget();
                    if (m_p2ouModel.isSelected(e.getPageState())) {
                        target.setButtonLabel("Cancel");
                    } else {
                        target.setButtonLabel("Reset");
                    }
                }
            });

            m_saveCancelSection.getSaveButton().addPrintListener(new PrintListener() {

                public void prepare(PrintEvent e) {
                    Submit target = (Submit) e.getTarget();
                    if (m_p2ouModel.isSelected(e.getPageState())) {
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

    protected Project2OrgaUnitSelectionModel getP2OUSelectionModel() {
        return m_p2ouModel;
    }

    protected Project getProject(PageState s) {
        return (Project) m_itemModel.getSelectedItem(s);
    }

    protected Project2OrgaUnit createProject2OrgaUnit(PageState s) {
        Project project = getProject(s);
        Assert.exists(project);
        Project2OrgaUnit p2ou = new Project2OrgaUnit();
        p2ou.setProject(project);
        return p2ou;
    }

    protected void setProject2OrgaUnitProperties(
            Project2OrgaUnit p2ou,
            FormSectionEvent e) {
        PageState state = e.getPageState();
        FormData data = e.getFormData();

        p2ou.setTargetItem((OrganizationalUnit) data.get(ITEM_SEARCH));

        p2ou.save();
    }

    public void init(FormSectionEvent e) throws FormProcessException {
        FormData data = e.getFormData();
        PageState state = e.getPageState();

        setVisible(state, true);

        Project2OrgaUnit p2ou;
        if (m_p2ouModel.isSelected(state)) {
            p2ou = m_p2ouModel.getSelectedP2OU(state);
            try {
                data.put(ITEM_SEARCH, p2ou.getTargetItem());
            } catch (IllegalStateException ex) {
                throw ex;
            }
        } else {
            data.put(ITEM_SEARCH, null);
        }
    }

    public void process(FormSectionEvent e) throws FormProcessException {
        PageState state = e.getPageState();
        Project2OrgaUnit p2ou;

        if (this.getSaveCancelSection().getCancelButton().isSelected(state)) {
            this.m_p2ouModel.clearSelection(state);
        } else {
            if (this.m_p2ouModel.isSelected(state)) {
                p2ou = m_p2ouModel.getSelectedP2OU(state);
            } else {
                p2ou = createProject2OrgaUnit(state);
            }
            setProject2OrgaUnitProperties(p2ou, e);
        }

        m_p2ouModel.clearSelection(state);
        init(e);
    }

    public void validate(FormSectionEvent e) throws FormProcessException {
        if (e.getFormData().get(ITEM_SEARCH) == null) {
            throw new FormProcessException(
                    "OrganiztionalUnit selection is required");
        }
    }

    public void submitted(FormSectionEvent e) throws FormProcessException {
        if (this.m_saveCancelSection.getCancelButton().isSelected(
                e.getPageState())) {
            m_p2ouModel.clearSelection(e.getPageState());
            init(e);
            throw new FormProcessException("cancelled");
        }
    }
}
