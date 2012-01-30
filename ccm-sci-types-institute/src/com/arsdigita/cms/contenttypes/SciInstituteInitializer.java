/*
 * Copyright (c) 2010 Jens Pelzetter,
 * for the Center of Social Politics of the University of Bremen
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
 * Executes at each system startup and initializes the SciInstitute 
 * content type, part of the ScientificCMS extension.
 * 
 * Defines the content type specific properties and just uses the super class
 * methods to register the content type with the (transient) content type store
 * (map). This is done by runtimeRuntime startup method which runs the init()
 * methods of all initializers (this one just using the parent implementation).
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class SciInstituteInitializer extends ContentTypeInitializer {

    /**
     * Constructor, sets the PDL manifest file and object type string.
     */
    public SciInstituteInitializer() {
        super("ccm-sci-types-institute.pdl.mf",
              SciInstitute.BASE_DATA_OBJECT_TYPE);
    }

    /**
     * Initializes the domain coupling machinery
     * 
     * @param event 
     */
    @Override
    public void init(final DomainInitEvent event) {
        super.init(event);

        final SciInstituteConfig config = SciInstitute.getConfig();

        //Add the authoring steps for departments if the department type is installed
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

        //Add the authoring steps for projects if the project type is installed
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

    /**
     * Retrieve location of this content type's internal default theme 
     * stylesheet(s) which concomitantly serve as a fallback if a custom theme 
     * is engaged. 
     * 
     * Custom themes usually will provide their own stylesheet(s) and their own
     * access method, but may not support every content type.
     * 
     * Overwrites parent method with AgendaItem specific value for use by the 
     * parent class worker methods.
     * 
     * @return String array of XSL stylesheet files of the internal default theme
     */
    @Override
    public String[] getStylesheets() {
        return new String[]{
                    INTERNAL_THEME_TYPES_DIR + "sci/SciInstitute.xsl"
                };
    }

    /**
     * Retrieves fully qualified traversal adapter file name.
     * @return 
     */
    @Override
    public String getTraversalXML() {
        return 
        "/WEB-INF/traversal-adapters/com/arsdigita/cms/contenttypes/SciInstitute.xml";
    }
}
