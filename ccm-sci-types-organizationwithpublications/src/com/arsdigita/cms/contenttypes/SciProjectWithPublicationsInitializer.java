/*
 * Copyright (c) 2011 Jens Pelzetter,
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
import com.arsdigita.cms.contenttypes.ui.PublicationSciProjectStep;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.runtime.DomainInitEvent;

/**
 *
 * @author Jens Pelzetter 
 */
public class SciProjectWithPublicationsInitializer
        extends ContentTypeInitializer {

    public SciProjectWithPublicationsInitializer() {
        super("empty.pdl.mf",
              SciProjectWithPublications.BASE_DATA_OBJECT_TYPE);
    }

    @Override
    public void init(final DomainInitEvent event) {
        super.init(event);

        ContentType projectType =
                    ContentType.findByAssociatedObjectType(SciProject.class.
                getName());
        if (!projectType.isInternal()) {
            projectType.setMode("internal");
        }

        AuthoringKitWizard.registerAssetStep(Publication.BASE_DATA_OBJECT_TYPE,
                                             PublicationSciProjectStep.class,
                                             new GlobalizedMessage(
                "sciorganizationpublication.ui.publication.projects.title",
                "com.arsdigita.cms.contenttypes.ui.SciOrganizationWithPublicationsResources"),
                                             new GlobalizedMessage(
                "sciorganizationpublication.ui.publication.projects.description",
                "com.arsdigita.cms.contenttypes.ui.SciOrganizationWithPublicationsResources"),
                                             1);
    }

    @Override
    public String[] getStylesheets() {
        return new String[]{
                    "/static/content-types/com/arsdigita/cms/contenttypes/SciProject.xsl"};
    }

    @Override
    public String getTraversalXML() {
        return "/WEB-INF/traversal-adapters/com/arsdigita/cms/contenttypes/SciProjectWithPublications.xml";

    }
}
