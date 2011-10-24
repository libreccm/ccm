package com.arsdigita.cms.contenttypes;

import com.arsdigita.cms.ContentType;
import com.arsdigita.cms.ContentTypeCollection;
import com.arsdigita.cms.contenttypes.ui.SciDepartmentGlobalizationUtil;
import com.arsdigita.cms.contenttypes.ui.SciDepartmentProjectsStep;
import com.arsdigita.cms.contenttypes.ui.SciDepartmentSubDepartmentsStep;
import com.arsdigita.cms.contenttypes.ui.SciDepartmentSuperDepartmentsStep;
import com.arsdigita.cms.contenttypes.ui.SciProjectDepartmentsStep;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.runtime.DomainInitEvent;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class SciDepartmentInitializer extends ContentTypeInitializer {

    public SciDepartmentInitializer() {
        super("ccm-sci-types-department.pdl.mf",
              SciDepartment.BASE_DATA_OBJECT_TYPE);
    }

    @Override
    public void init(final DomainInitEvent event) {
        super.init(event);

        final SciDepartmentConfig config = SciDepartment.getConfig();

        if (config.getEnableSubDepartmentsStep()) {
            AuthoringKitWizard.registerAssetStep(
                    SciDepartment.BASE_DATA_OBJECT_TYPE,
                    SciDepartmentSubDepartmentsStep.class,
                    SciDepartmentGlobalizationUtil.globalize(
                    "scidepartment.ui.subdepartments.title"),
                    SciDepartmentGlobalizationUtil.globalize(
                    "scidepartment.ui.subdepartments.description"),
                    10);
        }

        if (config.getEnableSuperDepartmentsStep()) {
            AuthoringKitWizard.registerAssetStep(
                    SciDepartment.BASE_DATA_OBJECT_TYPE,
                    SciDepartmentSuperDepartmentsStep.class,
                    SciDepartmentGlobalizationUtil.globalize(
                    "scidepartment.ui.superdepartments.title"),
                    SciDepartmentGlobalizationUtil.globalize(
                    "scidepartment.ui.superdepartments.description"),
                    20);
        }

        final ContentTypeCollection contentTypes = ContentType.
                getAllContentTypes();
        contentTypes.addFilter(
                "associatedObjectType = 'com.arsdigita.cms.contenttypes.SciProject'");

        if (contentTypes.size() > 0) {
            if (config.getEnableProjectsStep()) {

                AuthoringKitWizard.registerAssetStep(
                        SciDepartment.BASE_DATA_OBJECT_TYPE,
                        SciDepartmentProjectsStep.class,
                        SciDepartmentGlobalizationUtil.globalize(
                        "scidepartment.ui.projects.title"),
                        SciDepartmentGlobalizationUtil.globalize(
                        "scidepartment.ui.projects.description"),
                        30);
            }

            if (config.getEnableProjectDepartmentsStep()) {
                AuthoringKitWizard.registerAssetStep(
                        "com.arsdigita.cms.contenttypes.SciProject",
                        SciProjectDepartmentsStep.class,
                        SciDepartmentGlobalizationUtil.globalize(
                        "sciproject.ui.departments.title"),
                        SciDepartmentGlobalizationUtil.globalize(
                        "sciproject.ui.departments.description"),
                        40);
            }
        }
    }

    @Override
    public String[] getStylesheets() {
        return new String[]{
                    "/static/content-types/com/arsdigita/cms/contenttypes/SciDepartment.xsl"
                };
    }

    @Override
    public String getTraversalXML() {
        return "/WEB-INF/traversal-adapters/com/arsdigita/cms/contenttypes/SciDepartment.xml";
    }
}
