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

import com.arsdigita.cms.contenttypes.ui.SciProjectGlobalizationUtil;
import com.arsdigita.cms.contenttypes.ui.SciProjectInvolvedOrganizationsStep;
import com.arsdigita.cms.contenttypes.ui.SciProjectSubProjectsStep;
import com.arsdigita.cms.contenttypes.ui.SciProjectSuperProjectsStep;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.runtime.DomainInitEvent;

import org.apache.log4j.Logger;


/**
 * Executes at each system startup and initializes the SciProject content type,
 * part of the ScientificCMS extension.
 * 
 * Defines the content type specific properties and just uses the super class
 * methods to register the content type with the (transient) content type store
 * (map). This is done by runtimeRuntime startup method which runs the init()
 * methods of all initializers (this one just using the parent implementation).
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class SciProjectInitializer extends ContentTypeInitializer {

    /** Private Logger instance for debugging purpose.                        */
    private final Logger logger = Logger.getLogger(SciProjectInitializer.class);

    /**
     * Constructor, sets the PDL manifest file and object type string.
     */
    public SciProjectInitializer() {
        super("ccm-sci-types-project.pdl.mf",
              SciProject.BASE_DATA_OBJECT_TYPE);
    }

    /**
     * 
     * @param event 
     */
    @Override
    public void init(final DomainInitEvent event) {
        super.init(event);

        final SciProjectConfig config = SciProject.getConfig();

        if (config.getEnableSubProjectsStep()) {
            AuthoringKitWizard.registerAssetStep(
                    SciProject.BASE_DATA_OBJECT_TYPE,
                    SciProjectSubProjectsStep.class,
                    SciProjectGlobalizationUtil.globalize(
                    "sciproject.ui.subprojects.title"),
                    SciProjectGlobalizationUtil.globalize(
                    "sciproject.ui.subprojects.description"),
                    config.getSubProjectsStepSortKey());
        }

        if (config.getEnableSuperProjectsStep()) {
            AuthoringKitWizard.registerAssetStep(
                    SciProject.BASE_DATA_OBJECT_TYPE,
                    SciProjectSuperProjectsStep.class,
                    SciProjectGlobalizationUtil.globalize(
                    "sciproject.ui.superprojects.title"),
                    SciProjectGlobalizationUtil.globalize(
                    "sciproject.ui.superprojects.description"),
                    config.getSuperProjectsStepSortKey());
        }

        if (config.getEnableInvolvedOrgasStep()) {
            AuthoringKitWizard.registerAssetStep(
                    SciProject.BASE_DATA_OBJECT_TYPE,
                    SciProjectInvolvedOrganizationsStep.class,
                    SciProjectGlobalizationUtil.globalize(
                    "sciproject.ui.involved_orgas.title"),
                    SciProjectGlobalizationUtil.globalize(
                    "sciproject.ui.involved_orgas.description"),
                    config.getInvolvedOrgasStepSortKey());
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
                INTERNAL_THEME_TYPES_DIR + "sci/SciProject.xsl"
                };
    }

    /**
     * Retrieves fully qualified traversal adapter file name.
     * @return 
     */
    @Override
    public String getTraversalXML() {
        return 
        "/WEB-INF/traversal-adapters/com/arsdigita/cms/contenttypes/SciProject.xml";
    }
}
