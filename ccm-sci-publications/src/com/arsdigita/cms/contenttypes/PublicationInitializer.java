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
import com.arsdigita.cms.contenttypes.ui.PublicationGenericOrganizationalUnitsStep;
import com.arsdigita.cms.contenttypes.ui.PublicationGlobalizationUtil;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.runtime.DomainInitEvent;
import org.apache.log4j.Logger;

/**
 *
 * @author Jens Pelzetter
 * @version $Id$
 */
public class PublicationInitializer extends ContentTypeInitializer {

    private final Logger logger = Logger.getLogger(PublicationInitializer.class);

    public PublicationInitializer() {
        super("ccm-sci-publications.pdl.mf", Publication.BASE_DATA_OBJECT_TYPE);
    }

    @Override
    public void init(final DomainInitEvent event) {
        super.init(event);

        final PublicationsConfig config = Publication.getConfig();

        if (config.getAttachOrgaUnitsStep()) {
            AuthoringKitWizard.registerAssetStep(
                    Publication.BASE_DATA_OBJECT_TYPE,
                    PublicationGenericOrganizationalUnitsStep.class,
                    PublicationGlobalizationUtil.globalize(
                    "publications.ui.orgaunits.title"),
                    PublicationGlobalizationUtil.globalize(
                    "publications.ui.orgaunits.description"),
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
    }

    @Override
    public String[] getStylesheets() {
        return new String[]{
                    "/static/content-types/com/arsdigita/cms/contenttypes/Publication.xsl"};
    }

    @Override
    public String getTraversalXML() {
        return "/WEB-INF/traversal-adapters/com/arsdigita/cms/contenttypes/Publication.xml";
    }
}
