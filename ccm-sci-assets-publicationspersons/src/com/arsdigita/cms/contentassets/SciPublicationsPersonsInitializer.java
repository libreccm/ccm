/*
 * Copyright (c) 2014 Jens Pelzetter
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

import com.arsdigita.cms.contentassets.ui.SciPublicationsPersonsExtraXMLGenerator;
import com.arsdigita.cms.contentassets.ui.SciPublicationsPersonsGlobalisationUtil;
import com.arsdigita.cms.contentassets.ui.SciPublicationsPersonsStep;
import com.arsdigita.cms.contenttypes.ContentItemTraversalAdapter;
import com.arsdigita.cms.contenttypes.Publication;
import com.arsdigita.cms.contenttypes.ui.PublicationExtraXmlGenerator;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.db.DbHelper;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.domain.DomainObjectInstantiator;
import com.arsdigita.domain.DomainObjectTraversalAdapter;
import com.arsdigita.domain.xml.TraversalHandler;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.pdl.ManifestSource;
import com.arsdigita.persistence.pdl.NameFilter;
import com.arsdigita.runtime.CompoundInitializer;
import com.arsdigita.runtime.DomainInitEvent;
import com.arsdigita.runtime.PDLInitializer;
import com.arsdigita.runtime.RuntimeConfig;
import com.arsdigita.xml.XML;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class SciPublicationsPersonsInitializer extends CompoundInitializer {

    public SciPublicationsPersonsInitializer() {
        final String url = RuntimeConfig.getConfig().getJDBCURL();
        final int database = DbHelper.getDatabaseFromURL(url);

        add(new PDLInitializer(new ManifestSource(
            "ccm-sci-assets-publicationspersons.pdl.mf",
            new NameFilter(DbHelper.getDatabaseSuffix(database), "pdl"))));
    }

    @Override
    public void init(final DomainInitEvent event) {

        super.init(event);

        DomainObjectFactory.registerInstantiator(
            SciPublicationsPersonsPublicationsPersons.BASE_DATA_OBJECT_TYPE,
            new DomainObjectInstantiator() {

                @Override
                protected DomainObject doNewInstance(
                    final DataObject dataObject) {
                        return new SciPublicationsPersonsPublicationsPersons(
                            dataObject);
                    }

            });

        DomainObjectFactory.registerInstantiator(
            SciPublicationsPersonsPersonsPublications.BASE_DATA_OBJECT_TYPE,
            new DomainObjectInstantiator() {

                @Override
                protected DomainObject doNewInstance(
                    final DataObject dataObject) {
                        return new SciPublicationsPersonsPersonsPublications(
                            dataObject);
                    }

            });

        final String traversal = "/WEB-INF/traversal-adapters/com/arsdigita/cms/contentassets/"
                                     + "SciPublicationsPersons.xml";
        XML.parseResource(traversal, new TraversalHandler() {

            @Override
            protected void registerAdapter(final String objectType,
                                           final DomainObjectTraversalAdapter adapter,
                                           final String context) {
                ContentItemTraversalAdapter.registerAssetAdapter("publications", adapter, context);
                ContentItemTraversalAdapter.registerAssetAdapter("persons", adapter, context);
            }

        });

        final SciPublicationsPersonsGlobalisationUtil globalisationUtil
                                                          = new SciPublicationsPersonsGlobalisationUtil();
        AuthoringKitWizard.registerAssetStep(
            Publication.BASE_DATA_OBJECT_TYPE,
            SciPublicationsPersonsStep.class,
            globalisationUtil.globalize(
                "com.arsdigita.cms.contentassets.publications_persons.related_persons.title"),
            globalisationUtil.globalize(
                "com.arsdigita.cms.contentassets.publications_persons.related_persons.desc"),
            50);

        PublicationExtraXmlGenerator.addExtendingGenerator(
            new SciPublicationsPersonsExtraXMLGenerator());
    }

}
