/*
 * Copyright (c) 2010 Jens Pelzetter
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
import com.arsdigita.cms.contenttypes.ui.SciDepartmentGlobalizationUtil;
import com.arsdigita.cms.contenttypes.ui.SciDepartmentProjectsStep;
import com.arsdigita.cms.contenttypes.ui.SciDepartmentSubDepartmentsStep;
import com.arsdigita.cms.contenttypes.ui.SciDepartmentSuperDepartmentsStep;
import com.arsdigita.cms.contenttypes.ui.SciProjectDepartmentsStep;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.runtime.DomainInitEvent;

/**
 * Executes at each system startup and initializes the SciDepartment 
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
public class SciDepartmentInitializer extends ContentTypeInitializer {

    /**
     * Constructor, sets the PDL manifest file and object type string.
     */
    public SciDepartmentInitializer() {
        super("ccm-sci-types-department.pdl.mf",
              SciDepartment.BASE_DATA_OBJECT_TYPE);
    }

    /**
     * Initializes the domain coupling machinery
     * 
     * @param event 
     */
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
                    config.getSubDepartmentsStepSortKey());
        }

        if (config.getEnableSuperDepartmentsStep()) {
            AuthoringKitWizard.registerAssetStep(
                    SciDepartment.BASE_DATA_OBJECT_TYPE,
                    SciDepartmentSuperDepartmentsStep.class,
                    SciDepartmentGlobalizationUtil.globalize(
                    "scidepartment.ui.superdepartments.title"),
                    SciDepartmentGlobalizationUtil.globalize(
                    "scidepartment.ui.superdepartments.description"),
                    config.getSuperDepartmentsStepSortKey());
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
                        config.getProjectsStepSortKey());
            }

            if (config.getEnableProjectDepartmentsStep()) {
                AuthoringKitWizard.registerAssetStep(
                        "com.arsdigita.cms.contenttypes.SciProject",
                        SciProjectDepartmentsStep.class,
                        SciDepartmentGlobalizationUtil.globalize(
                        "sciproject.ui.departments.title"),
                        SciDepartmentGlobalizationUtil.globalize(
                        "sciproject.ui.departments.description"),
                        config.getProjectDepartmentsStepSortKey());
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
                    INTERNAL_THEME_TYPES_DIR + "sci/SciDepartment.xsl"
                };
    }

    /**
     * Retrieves fully qualified traversal adapter file name.
     * @return 
     */
    @Override
    public String getTraversalXML() {
        return 
        "/WEB-INF/traversal-adapters/com/arsdigita/cms/contenttypes/SciDepartment.xml";
    }
}
