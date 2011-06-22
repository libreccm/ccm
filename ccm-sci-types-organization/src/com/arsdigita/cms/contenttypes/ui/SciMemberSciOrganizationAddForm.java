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
import com.arsdigita.cms.contenttypes.SciMember;
import com.arsdigita.cms.contenttypes.SciMemberSciOrganizationsCollection;
import com.arsdigita.cms.contenttypes.SciOrganization;
import com.arsdigita.cms.contenttypes.util.ContenttypesGlobalizationUtil;
import com.arsdigita.cms.ui.ItemSearchWidget;
import com.arsdigita.cms.ui.authoring.BasicItemForm;
import com.arsdigita.dispatcher.DispatcherHelper;

/**
 *
 * @author Jens Pelzetter
 */
public class SciMemberSciOrganizationAddForm
        extends BasicItemForm
        implements FormProcessListener,
                   FormInitListener,
                   FormSubmissionListener {

    private ItemSearchWidget itemSearch;
    private final String ITEM_SEARCH = "sciMemberOrganization";
    private SciMemberSciOrganizationsStep step;
    private Label selectedOrganizationNameLabel;

    public SciMemberSciOrganizationAddForm(ItemSelectionModel itemModel,
                                           SciMemberSciOrganizationsStep step) {
        super("sciMemberOrganizationAddForm", itemModel);
        this.step = step;

    }

    @Override
    protected void addWidgets() {
        add(new Label(SciOrganizationGlobalizationUtil.globalize(
                "scimember.ui.organization.select_organization")));
        itemSearch = new ItemSearchWidget(ITEM_SEARCH, ContentType.
                findByAssociatedObjectType(SciOrganization.class.getName()));
        add(itemSearch);

        selectedOrganizationNameLabel = new Label("");
        add(selectedOrganizationNameLabel);

        add(new Label(ContenttypesGlobalizationUtil.globalize(
                "cms.contenttypes.ui.genericorgaunit.person.role")));
        ParameterModel roleParam =
                       new StringParameter(
                SciMemberSciOrganizationsCollection.MEMBER_ROLE);
        SingleSelect roleSelect = new SingleSelect(roleParam);
        roleSelect.addValidationListener(new NotNullValidationListener());
        roleSelect.addOption(
                new Option("",
                           new Label((String) ContenttypesGlobalizationUtil.
                globalize("cms.ui.select_one").localize())));
        RelationAttributeCollection roles = new RelationAttributeCollection(
                "SciOrganizationRole");
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
                SciMemberSciOrganizationsCollection.STATUS);
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

        SciOrganization orga;
        String role;
        String status;

        orga = step.getSelectedOrganization();
        role = step.getSelectedOrganizationRole();
        status = step.getSelectedOrganizationStatus();

        if (orga == null) {
            itemSearch.setVisible(state, true);
            selectedOrganizationNameLabel.setVisible(state, false);
        } else {
            data.put(ITEM_SEARCH, orga);
            data.put(SciMemberSciOrganizationsCollection.MEMBER_ROLE, role);
            data.put(SciMemberSciOrganizationsCollection.STATUS, status);

            itemSearch.setVisible(state, false);
            selectedOrganizationNameLabel.setVisible(state, true);
            selectedOrganizationNameLabel.setLabel(orga.getTitle(), state);
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

            SciOrganization orga;
            orga = step.getSelectedOrganization();

            if (orga == null) {
                SciOrganization orgaToAdd = (SciOrganization) data.get(
                        ITEM_SEARCH);
                orgaToAdd = (SciOrganization) orgaToAdd.getContentBundle().
                        getInstance(member.getLanguage());

                member.addOrganization(orga,
                                       (String) data.get(
                        SciMemberSciOrganizationsCollection.MEMBER_ROLE),
                                       (String) data.get(
                        SciMemberSciOrganizationsCollection.STATUS));
            } else {
                SciMemberSciOrganizationsCollection orgas;

                orgas = member.getOrganizations();

                while (orgas.next()) {
                    if (orgas.getOrganization().equals(orga)) {
                        break;
                    }
                }

                orgas.setRoleName((String) data.get(
                        SciMemberSciOrganizationsCollection.MEMBER_ROLE));
                orgas.setStatus((String) data.get(
                        SciMemberSciOrganizationsCollection.STATUS));

                step.setSelectedOrganization(null);
                step.setSelectedOrganizationRole(null);
                step.setSelectedOrganizationStatus(null);

                orgas.close();
            }

            init(fse);
        }
    }

    @Override
    public void submitted(FormSectionEvent fse) throws FormProcessException {
        if (getSaveCancelSection().getCancelButton().isSelected(
                fse.getPageState())) {
            step.setSelectedOrganization(null);
            step.setSelectedOrganizationRole(null);
            step.setSelectedOrganizationStatus(null);

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
                    "scimember.ui.organization.select_organization.no_orga_selected"));
            return;
        }

        SciMember member = (SciMember) getItemSelectionModel().getSelectedObject(
                state);
        SciOrganization orga = (SciOrganization) data.get(
                ITEM_SEARCH);
        if (!(orga.getContentBundle().hasInstance(member.getLanguage()))) {
            data.addError(
                    SciOrganizationGlobalizationUtil.globalize(
                    "scimember.ui.organization.select_organization.no_suitable_language_variant"));
            return;
        }

        orga = (SciOrganization) orga.getContentBundle().getInstance(member.
                getLanguage());
        SciMemberSciOrganizationsCollection organizations = member.
                getOrganizations();
        organizations.addFilter(String.format("id = %s",
                                              orga.getID().toString()));
        if (organizations.size() > 0) {
            data.addError(
                    SciOrganizationGlobalizationUtil.globalize(
                    "scimember.ui.organization.select_organization.already_added"));
        }
        
        organizations.close();
    }
}
