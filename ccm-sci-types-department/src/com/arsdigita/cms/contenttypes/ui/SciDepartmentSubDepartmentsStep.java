package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.SciDepartment;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.BasicItemForm;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class SciDepartmentSubDepartmentsStep extends SimpleEditStep {

    private final String ADD_SUBDEPARTMENT_SHEET_NAME =
                         "SciDepartmentAddSubDepartment";
    public final static String ASSOC_TYPE = "DepartmentOf";

    public SciDepartmentSubDepartmentsStep(final ItemSelectionModel itemModel,
                                           final AuthoringKitWizard parent) {
        this(itemModel, parent, null);
    }

    public SciDepartmentSubDepartmentsStep(final ItemSelectionModel itemModel,
                                           final AuthoringKitWizard parent,
                                           final String prefix) {
        super(itemModel, parent, prefix);

        final BasicItemForm addSubDepartmentSheet =
                            new GenericOrganizationalUnitSubordinateOrgaUnitAddForm(
                itemModel,
                new GenericOrgaUnitSubordinateOrgaUnitAddFormCustomizer() {

                    public String getSelectSubordinateOrgaUnitLabel() {
                        return (String) SciDepartmentGlobalizationUtil.globalize(
                                "scidepartment.ui.subdepartment.select").
                                localize();
                    }

                    public String getSubordinateOrgaUnitType() {
                        return SciDepartment.class.getName();
                    }

                    public String getAssocType() {
                        return ASSOC_TYPE;
                    }

                    public String getNothingSelectedMessage() {
                        return (String) SciDepartmentGlobalizationUtil.globalize(
                                "scidepartment.ui.subdepartment.select.nothing").
                                localize();
                    }

                    public String getNoSuitableLanguageVariantMessage() {
                        return (String) SciDepartmentGlobalizationUtil.globalize(
                                "scidepartment.ui.subdepartment.no_suitable_languge_variant").
                                localize();
                    }

                    public String getAddingToItselfMessage() {
                        return (String) SciDepartmentGlobalizationUtil.globalize(
                                "scidepartment.ui.subdepartment.adding_to_itself").
                                localize();
                    }

                    public String getAlreadyAddedMessage() {
                        return (String) SciDepartmentGlobalizationUtil.globalize(
                                "scidepartment.ui.subdepartment.already_added").
                                localize();
                    }
                });
        add(ADD_SUBDEPARTMENT_SHEET_NAME,
            (String) SciDepartmentGlobalizationUtil.globalize(
                "scidepartment.ui.subdepartment.add").localize(),
            new WorkflowLockedComponentAccess(addSubDepartmentSheet, itemModel),
            addSubDepartmentSheet.getSaveCancelSection().getCancelButton());

        final GenericOrganizationalUnitSubordinateOrgaUnitsTable subDepartmentTable =
                                                                 new GenericOrganizationalUnitSubordinateOrgaUnitsTable(
                itemModel,
                new GenericOrgaUnitSubordinateOrgaUnitsTableCustomizer() {

                    public String getEmptyViewLabel() {
                        return (String) SciDepartmentGlobalizationUtil.globalize(
                                "scidepartment.ui.subdepartments.empty_view").
                                localize();
                    }

                    public String getNameColumnLabel() {
                        return (String) SciDepartmentGlobalizationUtil.globalize(
                                "scidepartment.ui.subdepartments.columns.name").
                                localize();
                    }

                    public String getDeleteColumnLabel() {
                        return (String) SciDepartmentGlobalizationUtil.globalize(
                                "scidepartment.ui.subdepartments.columns.delete").
                                localize();
                    }

                    public String getUpColumnLabel() {
                        return (String) SciDepartmentGlobalizationUtil.globalize(
                                "scidepartment.ui.subdepartments.columns.up").
                                localize();
                    }

                    public String getDownColumnLabel() {
                        return (String) SciDepartmentGlobalizationUtil.globalize(
                                "scidepartment.ui.subdepartments.columns.down").
                                localize();
                    }

                    public String getDeleteLabel() {
                        return (String) SciDepartmentGlobalizationUtil.globalize(
                                "scidepartment.ui.subdepartments.delete").
                                localize();
                    }

                    public String getUpLabel() {
                        return (String) SciDepartmentGlobalizationUtil.globalize(
                                "scidepartment.ui.subdepartments.up").localize();
                    }

                    public String getDownLabel() {
                        return (String) SciDepartmentGlobalizationUtil.globalize(
                                "scidepartment.ui.subdepartments.down").localize();
                    }

                    public String getConfirmRemoveLabel() {
                        return (String) SciDepartmentGlobalizationUtil.globalize(
                                "scidepartment.ui.subdepartments.delete.confirm").
                                localize();
                    }

                    public String getAssocType() {
                        return ASSOC_TYPE;
                    }
                });

        setDisplayComponent(subDepartmentTable);

    }
}
