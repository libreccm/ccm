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
import com.arsdigita.cms.contenttypes.SciDepartment;
import com.arsdigita.cms.contenttypes.SciMember;
import com.arsdigita.cms.contenttypes.SciMemberSciDepartmentsCollection;
import com.arsdigita.cms.contenttypes.util.ContenttypesGlobalizationUtil;
import com.arsdigita.cms.ui.ItemSearchWidget;
import com.arsdigita.cms.ui.authoring.BasicItemForm;
import com.arsdigita.dispatcher.DispatcherHelper;

/**
 *
 * @author Jens Pelzetter
 * @version $Id$
 */
public class SciMemberSciDepartmentAddForm
        extends BasicItemForm
        implements FormProcessListener,
                   FormInitListener,
                   FormSubmissionListener {

    private ItemSearchWidget itemSearch;
    private final String ITEM_SEARCH = "sciMemberDepartment";
    private SciMemberSciDepartmentsStep step;
    private Label selectedDepartmentNameLabel;

    public SciMemberSciDepartmentAddForm(ItemSelectionModel itemModel,
                                         SciMemberSciDepartmentsStep step) {
        super("sciMemberDepartmentAddForm", itemModel);
        this.step = step;
    }

    @Override
    public void addWidgets() {
        add(new Label(SciOrganizationGlobalizationUtil.globalize(
                "scimember.ui.department.select_department")));
        itemSearch = new ItemSearchWidget(ITEM_SEARCH, ContentType.
                findByAssociatedObjectType(SciDepartment.class.getName()));
        add(itemSearch);

        selectedDepartmentNameLabel = new Label("");
        add(selectedDepartmentNameLabel);

        add(new Label(ContenttypesGlobalizationUtil.globalize(
                "cms.contenttypes.ui.genericorgaunit.person.role")));
        ParameterModel roleParam =
                       new StringParameter(
                SciMemberSciDepartmentsCollection.MEMBER_ROLE);
        SingleSelect roleSelect = new SingleSelect(roleParam);
        roleSelect.addValidationListener(new NotNullValidationListener());
        roleSelect.addOption(
                new Option("",
                           new Label((String) ContenttypesGlobalizationUtil.
                globalize("cms.ui.select_one").localize())));
        RelationAttributeCollection roles = new RelationAttributeCollection(
                "SciDepartmentRole");
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
                SciMemberSciDepartmentsCollection.STATUS);
        SingleSelect statusSelect = new SingleSelect(statusModel);
        statusSelect.addValidationListener(new NotNullValidationListener());
        statusSelect.addOption(new Option("",
                                          new Label((String) ContenttypesGlobalizationUtil.
                globalize("cms.ui.select_one").localize())));
        RelationAttributeCollection statusColl =
                                    new RelationAttributeCollection(
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

        SciDepartment department;
        String role;
        String status;

        department = step.getSelectedDepartment();
        role = step.getSelectedDepartmentRole();
        status = step.getSelectedDepartmentStatus();

        if (department == null) {
            itemSearch.setVisible(state, true);
            selectedDepartmentNameLabel.setVisible(state, false);
        } else {
            data.put(ITEM_SEARCH, department);
            data.put(SciMemberSciDepartmentsCollection.MEMBER_ROLE, role);
            data.put(SciMemberSciDepartmentsCollection.STATUS, status);

            itemSearch.setVisible(state, false);
            selectedDepartmentNameLabel.setVisible(state, true);
            selectedDepartmentNameLabel.setLabel(department.getTitle(), state);
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
            SciDepartment department;
            department = step.getSelectedDepartment();

            if (department == null) {
                SciDepartment departmentToAdd = (SciDepartment) data.get(
                        ITEM_SEARCH);
                departmentToAdd = (SciDepartment) departmentToAdd.
                        getContentBundle().getInstance(
                        member.getLanguage());

                member.addDepartment(departmentToAdd,
                                     (String) data.get(
                        SciMemberSciDepartmentsCollection.MEMBER_ROLE),
                                     (String) data.get(
                        SciMemberSciDepartmentsCollection.STATUS));
            } else {
                SciMemberSciDepartmentsCollection departments;

                departments = member.getDepartments();

                while (departments.next()) {
                    if (departments.getDepartment().equals(department)) {
                        break;
                    }
                }

                departments.setRoleName((String) data.get(
                        SciMemberSciDepartmentsCollection.MEMBER_ROLE));
                departments.setStatus((String) data.get(
                        SciMemberSciDepartmentsCollection.STATUS));

                step.setSelectedDepartment(null);
                step.setSelectedDepartmentRole(null);
                step.setSelectedDepartmentStatus(null);

                departments.close();
            }

            init(fse);
        }
    }

    @Override
    public void submitted(FormSectionEvent fse) throws FormProcessException {
        if (getSaveCancelSection().getCancelButton().isSelected(
                fse.getPageState())) {
            step.setSelectedDepartment(null);
            step.setSelectedDepartmentRole(null);
            step.setSelectedDepartmentStatus(null);

            init(fse);
        }
    }

    @Override
    public void validate(FormSectionEvent fse) throws FormProcessException {
        final PageState state = fse.getPageState();
        final FormData data = fse.getFormData();

        if (data.get(ITEM_SEARCH) == null) {
            data.addError(
                    SciOrganizationGlobalizationUtil.globalize(
                    "scimember.ui.department.select_department.no_department_selected"));
            return;
        }

        SciMember member = (SciMember) getItemSelectionModel().getSelectedObject(
                state);
        SciDepartment department = (SciDepartment) data.get(ITEM_SEARCH);
        if (!(department.getContentBundle().hasInstance(member.getLanguage()))) {
            data.addError(
                    SciOrganizationGlobalizationUtil.globalize(
                    "scimember.ui.department.select_department.no_suitable_language_variant"));
            return;
        }

        department = (SciDepartment) department.getContentBundle().getInstance(member.
                getLanguage());
        SciMemberSciDepartmentsCollection departments = member.getDepartments();
        departments.addFilter(String.format("id = %s", department.getID().toString()));
        if (departments.size() > 0) {
            data.addError(
                    SciOrganizationGlobalizationUtil.globalize(
                    "scimember.ui.department.select_department.already_added"));
        }
        
        departments.close();
    }
}
