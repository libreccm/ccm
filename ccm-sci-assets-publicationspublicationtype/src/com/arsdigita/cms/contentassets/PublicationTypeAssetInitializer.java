/*
 * Copyright (c) 2013 Jens Pelzetter
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

import com.arsdigita.cms.RelationAttributeImportTool;
import com.arsdigita.cms.contentassets.ui.PublicationTypeAssetStep;
import com.arsdigita.cms.contenttypes.ContentAssetInitializer;
import com.arsdigita.cms.contenttypes.Publication;
import com.arsdigita.cms.dispatcher.SimpleXMLGenerator;
import com.arsdigita.domain.DomainObjectTraversal;
import com.arsdigita.domain.SimpleDomainObjectTraversalAdapter;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.runtime.DomainInitEvent;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class PublicationTypeAssetInitializer extends ContentAssetInitializer {

    public PublicationTypeAssetInitializer() {
        super("ccm-sci-assets-publicationspublicationtype.pdl.mf");
    }

    @Override
    public void init(final DomainInitEvent event) {
        super.init(event);

        DomainObjectTraversal.registerAdapter(PublicationTypeAsset.BASE_DATA_OBJECT_TYPE,
                                              new SimpleDomainObjectTraversalAdapter(),
                                              SimpleXMLGenerator.ADAPTER_CONTEXT);
        
        final RelationAttributeImportTool importTool = new RelationAttributeImportTool();
        importTool.loadData("WEB-INF/resources/publication_types.xml");
    }

    @Override
    public String getBaseType() {
        return Publication.BASE_DATA_OBJECT_TYPE;
    }

    @Override
    public String getTraversalXML() {
        return TRAVERSAL_ADAPTER_BASE_DIR + "PublicationTypeAsset.xml";
    }

    @Override
    public String getProperty() {
        return "publicationtypes";
    }

    @Override
    public Class getAuthoringStep() {
        return PublicationTypeAssetStep.class;
    }

    @Override
    public GlobalizedMessage getAuthoringStepLabel() {
        return new GlobalizedMessage("com.arsdigita.cms.contentassets.publicationtypes.label",
                                     "com.arsdigita.cms.contentassets.PublicationTypeAssetResources");
    }

    @Override
    public GlobalizedMessage getAuthoringStepDescription() {
        return new GlobalizedMessage("com.arsdigita.cms.contentassets.publicationtypes.description",
                                     "com.arsdigita.cms.contentassets.PublicationTypeAssetResources");
    }

    @Override
    public int getAuthoringStepSortKey() {
        return 10;
    }

}
