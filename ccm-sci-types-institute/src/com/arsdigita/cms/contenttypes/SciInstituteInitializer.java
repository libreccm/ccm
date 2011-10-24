package com.arsdigita.cms.contenttypes;

import com.arsdigita.cms.ContentType;
import com.arsdigita.cms.ContentTypeCollection;
import com.arsdigita.cms.contenttypes.ui.SciDepartmentInstitutesStep;
import com.arsdigita.cms.contenttypes.ui.SciInstituteDepartmentsStep;
import com.arsdigita.cms.contenttypes.ui.SciInstituteGlobalizationUtil;
import com.arsdigita.cms.contenttypes.ui.SciInstituteProjectsStep;
import com.arsdigita.cms.contenttypes.ui.SciProjectInstitutesStep;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.runtime.DomainInitEvent;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class SciInstituteInitializer extends ContentTypeInitializer {

    public SciInstituteInitializer() {
        super("ccm-sci-types-institute.pdl.mf",
              SciInstitute.BASE_DATA_OBJECT_TYPE);
    }

    @Override
    public void init(final DomainInitEvent event) {
        super.init(event);

        final SciInstituteConfig config = SciInstitute.getConfig();

        final ContentTypeCollection contentTypes = ContentType.
                getAllContentTypes();
        contentTypes.addFilter(
                "associatedObjectType = 'com.arsdigita.cms.contenttypes.SciDepartment'");

        if (contentTypes.size() > 0) {
            if (config.getEnableDepartmentsStep()) {
                AuthoringKitWizard.registerAssetStep(
                        SciInstitute.BASE_DATA_OBJECT_TYPE,
                        SciInstituteDepartmentsStep.class,
                        SciInstituteGlobalizationUtil.globalize(
                        "sciinstitute.ui.departments.title"),
                        SciInstituteGlobalizationUtil.globalize(
                        "sciinstitute.ui.departments.description"),
                        10);
            }

            if (config.getEnableDepartmentInstitutesStep()) {
                AuthoringKitWizard.registerAssetStep(
                        "com.arsdigita.cms.contenttypes.SciDepartment",
                        SciDepartmentInstitutesStep.class,
                        SciInstituteGlobalizationUtil.globalize(
                        "scidepartment.ui.institutes.title"),
                        SciInstituteGlobalizationUtil.globalize(
                        "scidepartment.ui.institutes.description"),
                        20);
            }
        }

        contentTypes.reset();
        contentTypes.addFilter(
                "associatedObjectType = 'com.arsdigita.cms.contenttypes.SciProject'");

        if (contentTypes.size() > 0) {
            if (config.getEnableProjectsStep()) {
                AuthoringKitWizard.registerAssetStep(
                        SciInstitute.BASE_DATA_OBJECT_TYPE,
                        SciInstituteProjectsStep.class,
                        SciInstituteGlobalizationUtil.globalize(
                        "sciinstitute.ui.projects.title"),
                        SciInstituteGlobalizationUtil.globalize(
                        "sciinstitute.ui.projects.description"),
                        30);
            }

            if (config.getEnableProjectInstitutesStep()) {
                if (config.getEnableDepartmentInstitutesStep()) {
                    AuthoringKitWizard.registerAssetStep(
                            "com.arsdigita.cms.contenttypes.SciProject",
                            SciProjectInstitutesStep.class,
                            SciInstituteGlobalizationUtil.globalize(
                            "sciproject.ui.institutes.title"),
                            SciInstituteGlobalizationUtil.globalize(
                            "sciproject.ui.institutes.description"),
                            40);
                }
            }
        }
    }

    @Override
    public String[] getStylesheets() {
        return new String[]{
                    "/static/content-types/com/arsdigita/cms/contenttypes/SciInstitute.xsl"
                };
    }

    @Override
    public String getTraversalXML() {
        return "/WEB-INF/traversal-adapters/com/arsdigita/cms/contenttypes/SciInstitute.xml";
    }
}
