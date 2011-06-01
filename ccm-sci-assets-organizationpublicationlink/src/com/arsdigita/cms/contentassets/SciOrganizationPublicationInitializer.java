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
package com.arsdigita.cms.contentassets;

import com.arsdigita.cms.contentassets.ui.PublicationSciOrganizationStep;
import com.arsdigita.cms.contentassets.ui.SciOrganizationPublicationStep;
import com.arsdigita.cms.contenttypes.ContentAssetTraversalHandler;
import com.arsdigita.cms.contenttypes.Publication;
import com.arsdigita.cms.contenttypes.SciDepartment;
import com.arsdigita.cms.contenttypes.SciOrganization;
import com.arsdigita.cms.contenttypes.SciProject;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.db.DbHelper;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.persistence.pdl.ManifestSource;
import com.arsdigita.persistence.pdl.NameFilter;
import com.arsdigita.runtime.CompoundInitializer;
import com.arsdigita.runtime.DomainInitEvent;
import com.arsdigita.runtime.PDLInitializer;
import com.arsdigita.runtime.RuntimeConfig;
import com.arsdigita.xml.XML;

/**
 *
 * @author Jens Pelzetter 
 */
public class SciOrganizationPublicationInitializer extends CompoundInitializer {

    public SciOrganizationPublicationInitializer() {
        final String url = RuntimeConfig.getConfig().getJDBCURL();
        final int database = DbHelper.getDatabaseFromURL(url);

        add(new PDLInitializer(
                new ManifestSource(
                "ccm-sci-assets-sciorganiationpublication.pdl.mf",
                new NameFilter(DbHelper.getDatabaseSuffix(database), "pdl"))));
    }

    @Override
    public void init(DomainInitEvent event) {
        super.init(event);

        final String traversal = getTraversalXML();
        XML.parseResource(traversal,
                          new ContentAssetTraversalHandler(getProperty()));

        AuthoringKitWizard.registerAssetStep(
                SciOrganization.BASE_DATA_OBJECT_TYPE,
                SciOrganizationPublicationStep.class,
                new GlobalizedMessage("sciorganization.ui.publications",
                                      "com.arsdigita.cms.contentassets.ui.SciOrganizationPublication"),
                new GlobalizedMessage("sciorganization.ui.publications",
                                      "com.arsdigita.cms.contentassets.ui.SciOrganizationPublication"),
                1);
        AuthoringKitWizard.registerAssetStep(
                SciDepartment.BASE_DATA_OBJECT_TYPE,
                SciOrganizationPublicationStep.class,
                new GlobalizedMessage("sciorganization.ui.publications",
                                      "com.arsdigita.cms.contentassets.ui.SciOrganizationPublication"),
                new GlobalizedMessage("sciorganization.ui.publications",
                                      "com.arsdigita.cms.contentassets.ui.SciOrganizationPublication"),
                1);
        AuthoringKitWizard.registerAssetStep(
                SciProject.BASE_DATA_OBJECT_TYPE,
                SciOrganizationPublicationStep.class,
                new GlobalizedMessage("sciorganization.ui.publications",
                                      "com.arsdigita.cms.contentassets.ui.SciOrganizationPublication"),
                new GlobalizedMessage("sciorganization.ui.publications",
                                      "com.arsdigita.cms.contentassets.ui.SciOrganizationPublication"),
                1);

        AuthoringKitWizard.registerAssetStep(
                Publication.BASE_DATA_OBJECT_TYPE,
                PublicationSciOrganizationStep.class,
                new GlobalizedMessage("sciorganization.ui.organizations",
                                      "com.arsdigita.cms.contentassets.ui.SciOrganizationPublication"),
                new GlobalizedMessage("sciorganization.ui.organizations",
                                      "com.arsdigita.cms.contentassets.ui.SciOrganizationPublication"),
                1);
    }

    /**
     * 
     * @return
     */
    public String getTraversalXML() {
        return "/WEB-INF/traversal-adapters/com/arsdigita/"
               + "cms/contentassets/SciOrganizationPublication.xml";
    }

    /**
     * 
     * @return
     */
    public String getProperty() {
        return "publications";
    }
}
