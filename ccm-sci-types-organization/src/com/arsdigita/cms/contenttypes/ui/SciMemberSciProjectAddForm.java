package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.SingleSelect;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.ContentType;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.RelationAttribute;
import com.arsdigita.cms.RelationAttributeCollection;
import com.arsdigita.cms.contenttypes.SciProject;
import com.arsdigita.cms.contenttypes.SciMember;
import com.arsdigita.cms.contenttypes.SciMemberSciProjectsCollection;
import com.arsdigita.cms.contenttypes.util.ContenttypesGlobalizationUtil;
import com.arsdigita.cms.ui.ItemSearchWidget;
import com.arsdigita.cms.ui.authoring.BasicItemForm;
import com.arsdigita.dispatcher.DispatcherHelper;

/**
 *
 * @author Jens Pelzetter
 * @version $Id$
 */
public class SciMemberSciProjectAddForm
        extends BasicItemForm
        implements FormProcessListener,
                   FormInitListener,
                   FormSubmissionListener {

    private ItemSearchWidget itemSearch;
    private final String ITEM_SEARCH = "sciMemberProject";
    private SciMemberSciProjectsStep step;
    private Label selectedProjectNameLabel;

    public SciMemberSciProjectAddForm(ItemSelectionModel itemModel,
                                         SciMemberSciProjectsStep step) {
        super("sciMemberProjectAddForm", itemModel);
        this.step = step;
    }

    @Override
    public void addWidgets() {
        add(new Label(SciOrganizationGlobalizationUtil.globalize(
                "scimember.ui.project.select_project")));
        itemSearch = new ItemSearchWidget(ITEM_SEARCH, ContentType.
                findByAssociatedObjectType(SciProject.class.getName()));
        add(itemSearch);

        selectedProjectNameLabel = new Label("");
        add(selectedProjectNameLabel);

        add(new Label(ContenttypesGlobalizationUtil.globalize(
                "cms.contenttypes.ui.genericorgaunit.person.role")));
        ParameterModel roleParam =
                       new StringParameter(
                SciMemberSciProjectsCollection.MEMBER_ROLE);
        SingleSelect roleSelect = new SingleSelect(roleParam);
        roleSelect.addValidationListener(new NotNullValidationListener());
        roleSelect.addOption(
                new Option("",
                           new Label((String) ContenttypesGlobalizationUtil.
                globalize("cms.ui.select_one").localize())));
        RelationAttributeCollection roles = new RelationAttributeCollection(
                "SciProjectRole");
        roles.addLanguageFilter(DispatcherHelper.getNegotiatedLocale().
                getLanguage());
        while (roles.next()) {
            RelationAttribute role;
            role = roles.getRelationAttribute();
            roleSelect.addOption(new Option(role.getKey(), role.getName()));
        }
        add(roleSelect);

        add(new Label(ContenttypesGlobalizationUtil.globalize(
                "cms.contenttypes.ui.genericorgaunit.person.status")));
        ParameterModel statusModel =
                       new StringParameter(
                SciMemberSciProjectsCollection.STATUS);
        SingleSelect statusSelect = new SingleSelect(statusModel);
        statusSelect.addValidationListener(new NotNullValidationListener());
        statusSelect.addOption(new Option("",
                                          new Label((String) ContenttypesGlobalizationUtil.
                globalize("cms.ui.select_one").localize())));
        RelationAttributeCollection statusColl = new RelationAttributeCollection(
                "GenericOrganizationalUnitMemberStatus");
        statusColl.addLanguageFilter(DispatcherHelper.getNegotiatedLocale().
                getLanguage());
        while (statusColl.next()) {
            RelationAttribute status;
            status = statusColl.getRelationAttribute();
            statusSelect.addOption(new Option(status.getKey(), status.getName()));
        }
        add(statusSelect);
    }

    @Override
    public void init(FormSectionEvent fse) throws FormProcessException {
        FormData data = fse.getFormData();
        PageState state = fse.getPageState();

        SciProject project;
        String role;
        String status;

        project = step.getSelectedProject();
        role = step.getSelectedProjectRole();
        status = step.getSelectedProjectStatus();

        if (project == null) {
            itemSearch.setVisible(state, true);
            selectedProjectNameLabel.setVisible(state, false);
        } else {
            data.put(ITEM_SEARCH, project);
            data.put(SciMemberSciProjectsCollection.MEMBER_ROLE, role);
            data.put(SciMemberSciProjectsCollection.STATUS, status);

            itemSearch.setVisible(state, false);
            selectedProjectNameLabel.setVisible(state, true);
            selectedProjectNameLabel.setLabel(project.getTitle(), state);
        }

        setVisible(state, true);
    }

    @Override
    public void process(FormSectionEvent fse) throws FormProcessException {
        FormData data = fse.getFormData();
        PageState state = fse.getPageState();
        SciMember member = (SciMember) getItemSelectionModel().getSelectedObject(
                state);

        if (this.getSaveCancelSection().getSaveButton().isSelected(state)) {
            SciProject project;
            project = step.getSelectedProject();

            if (project == null) {
                member.addProject((SciProject) data.get(ITEM_SEARCH),
                                     (String) data.get(
                        SciMemberSciProjectsCollection.MEMBER_ROLE),
                                     (String) data.get(
                        SciMemberSciProjectsCollection.STATUS));
            } else {
                SciMemberSciProjectsCollection projects;

                projects = member.getProjects();

                while (projects.next()) {
                    if (projects.getProject().equals(project)) {
                        break;
                    }
                }

                projects.setRoleName((String) data.get(
                        SciMemberSciProjectsCollection.MEMBER_ROLE));
                projects.setStatus((String) data.get(
                        SciMemberSciProjectsCollection.STATUS));

                step.setSelectedProject(null);
                step.setSelectedProjectRole(null);
                step.setSelectedProjectStatus(null);

                projects.close();
            }

            init(fse);
        }
    }

    @Override
    public void submitted(FormSectionEvent fse) throws FormProcessException {
        if (getSaveCancelSection().getCancelButton().isSelected(fse.getPageState())) {
            step.setSelectedProject(null);
            step.setSelectedProjectRole(null);
            step.setSelectedProjectStatus(null);

            init(fse);
        }
    }
}
