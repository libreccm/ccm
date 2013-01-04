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

import com.arsdigita.cms.contenttypes.ui.GenericOrganizationalUnitPublicationsStep;
import com.arsdigita.cms.contenttypes.ui.OrganizationPublicationsStep;
import com.arsdigita.cms.contenttypes.ui.PersonPublicationsStep;
import com.arsdigita.cms.contenttypes.ui.PublicationGenericOrganizationalUnitsStep;
import com.arsdigita.cms.contenttypes.ui.PublicationGlobalizationUtil;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.runtime.DomainInitEvent;
import org.apache.log4j.Logger;

/**
 * Executes at each system startup and initializes the Publication content type, part of the ScientificCMS extension.
 * 
 * Defines the content type specific properties and just uses the super class methods to register the content type with
 * the (transient) content type store (map). This is done by runtimeRuntime startup method which runs the init() methods
 * of all initializers (this one just using the parent implementation).
 *
 * @author Jens Pelzetter
 * @version $Id$
 */
public class PublicationInitializer extends ContentTypeInitializer {

    /**
     * Private Logger instance for debugging purpose.
     */
    private final Logger logger = Logger.getLogger(PublicationInitializer.class);

    /**
     * Constructor, sets the PDL manifest file and object type string.
     */
    public PublicationInitializer() {
        super("ccm-sci-publications.pdl.mf", Publication.BASE_DATA_OBJECT_TYPE);
    }

    /**
     * Initializes the domain coupling machinery
     * 
     * @param event 
     */
    @Override
    public void init(final DomainInitEvent event) {
        super.init(event);

        final PublicationsConfig config = Publication.getConfig();

        if (config.getAttachOrgaUnitsStep()) {
            AuthoringKitWizard.registerAssetStep(
                    Publication.BASE_DATA_OBJECT_TYPE,
                    PublicationGenericOrganizationalUnitsStep.class,
                    PublicationGlobalizationUtil.globalize("publications.ui.orgaunits.title"),
                    PublicationGlobalizationUtil.globalize("publications.ui.orgaunits.description"),
                    10);
        }

        final String attachToStr = config.getAttachPublicationsStepTo();
        final String[] attachToCts = attachToStr.split(";");
        if (logger.isInfoEnabled()) {
            logger.info(String.format("Attaching publications step to: %s",
                                      attachToStr));
        }
        for (String attachTo : attachToCts) {
            if (logger.isInfoEnabled()) {
                logger.info(String.format("Attaching publications step to: '%s'",
                                          attachTo));
            }

            AuthoringKitWizard.registerAssetStep(
                    attachTo,
                    GenericOrganizationalUnitPublicationsStep.class,
                    PublicationGlobalizationUtil.globalize(
                    "genericorganizationalunit.ui.publications.title"),
                    PublicationGlobalizationUtil.globalize(
                    "genericorganizationalunit.ui.publications.description"),
                    10);
        }

        if (config.getAttachPersonPublicationsStep()) {
            AuthoringKitWizard.registerAssetStep(
                    GenericPerson.BASE_DATA_OBJECT_TYPE,
                    PersonPublicationsStep.class,
                    PublicationGlobalizationUtil.globalize("person.ui.publications.title"),
                    PublicationGlobalizationUtil.globalize("person.ui.publications.description"),
                    10);
        }

        final String attachOrgaPubStepTo = config.getAttachOrganizationPublicationsStepTo();
        final String[] attachOrgaPubStepToCts = attachOrgaPubStepTo.split(";");
        if (logger.isInfoEnabled()) {
            logger.info(String.format("Attaching organization publications step to: %s",
                                      attachOrgaPubStepTo));
        }
        for (String attachTo : attachOrgaPubStepToCts) {
            if (logger.isInfoEnabled()) {
                logger.info(String.format("Attaching publications step to: '%s'",
                                          attachTo));
            }

            AuthoringKitWizard.registerAssetStep(
                    attachTo,
                    OrganizationPublicationsStep.class,
                    PublicationGlobalizationUtil.globalize("organization.ui.publications.title"),
                    PublicationGlobalizationUtil.globalize("organization.ui.publications.description"),
                    11);
        }        
    }

    /**
     * Retrieve location of this content type's internal default theme stylesheet(s) which concomitantly serve as a
     * fallback if a custom theme is engaged.
     * 
     * Custom themes usually will provide their own stylesheet(s) and their own access method, but may not support every
     * content type.
     * 
     * Overwrites parent method with AgendaItem specific value for use by the parent class worker methods.
     * 
     * @return String array of XSL stylesheet files of the internal default theme
     */
    @Override
    public String[] getStylesheets() {
        return new String[]{
                    INTERNAL_THEME_TYPES_DIR + "sci/Publication.xsl"};
    }

    /**
     * Retrieves fully qualified traversal adapter file name.
     *
     * @return 
     */
    @Override
    public String getTraversalXML() {
        return "/WEB-INF/traversal-adapters/com/arsdigita/cms/contenttypes/Publication.xml";
    }

}
