/*
 * Copyright (C) 2004 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the Open Software License v2.1
 * (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * http://rhea.redhat.com/licenses/osl2.1.html.
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */
package com.arsdigita.cms.contentassets;

import com.arsdigita.cms.contenttypes.ContentAssetInitializer;
import com.arsdigita.cms.contentassets.ui.FileAttachmentsStep;
import com.arsdigita.cms.ContentPage;
import com.arsdigita.domain.xml.TraversalHandler;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.kernel.URLService;
import com.arsdigita.runtime.DomainInitEvent;
import com.arsdigita.search.MetadataProviderRegistry;
import com.arsdigita.util.StringUtils;
import com.arsdigita.xml.XML;

import org.libreccm.export.ExportManager;
import org.librecms.assets.FileAssetsExporter;
import org.librecms.assets.FileAttachmentListsExporter;
import org.librecms.assets.FileAttachmentsExporter;

/**
 * Initializer
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: 1.1 $ $Date: 2004/12/15 15:37:51 $
 * @version $Id: FileAttachmentInitializer.java 1262 2006-07-17 08:15:45Z
 * cgyg9330 $
 *
 */
public class FileAttachmentInitializer extends ContentAssetInitializer {

    /**
     * Constructor
     */
    public FileAttachmentInitializer() {
        super("ccm-cms-assets-fileattachment.pdl.mf");
    }

    /**
     * Initializes content asset by parsing traversal xml file and registering
     * the specified steps in a transient storage which may be modified during
     * operation and has to be re-initialized each system startup). Essential
     * part of initializing the systems domain coupling machinery.
     *
     * @param evt
     */
    @Override
    public void init(DomainInitEvent evt) {
        super.init(evt);

        /*
         * Register with kernel's URLservice so for an instance of it the
         * complete path to its location in the sites URL tree, based on its OID,
         * can be build.
         */
        URLService.registerFinder(
            FileAttachment.BASE_DATA_OBJECT_TYPE,
            new FileAttachmentURLFinder());

        /*
         * cms registers AssetMetadataProvider for type FileAsset and provides
         * adapter for that context. We register a more specific metadataprovider
         * for FileAttachment that provides useful information about the owner.
         * Because we are using a new metadataprovider, we need to register
         * adapter for that context. Note this is not the same as the adapters
         * registered by the ContentAssetInitializer, because those are used
         * specifically when traversing a content item that delegates assets to
         * their specific adapters
         *
         * chris.gilbert@westsussex.gov.uk
         */
        MetadataProviderRegistry.registerAdapter(
            FileAttachment.BASE_DATA_OBJECT_TYPE,
            new FileAttachmentMetadataProvider());

        XML.parseResource(
            "/WEB-INF/traversal-adapters/com/arsdigita/cms/contentassets/"
                + "FileAttachment-search.xml",
            new TraversalHandler());

        final String traversal = getTraversalXML();
        if (!StringUtils.emptyString(traversal)) {
            XML.parseResource(traversal, new TraversalHandler());
        }
        
        ExportManager.getInstance().registerExporter(new FileAssetsExporter());
        ExportManager
            .getInstance()
            .registerExporter(new FileAttachmentListsExporter());
        ExportManager
            .getInstance()
            .registerExporter(new FileAttachmentsExporter());
    }

    /**
     * The base type against which the asset is defined, typically
     * com.arsdigita.cms.ContentPage
     */
    public String getBaseType() {
        return ContentPage.BASE_DATA_OBJECT_TYPE;
    }

    /**
     * Returns the path to the XML file defintions for the asset, eg:
     * /WEB-INF/traversal-adapters/com/arsdigita/cms/contentassets/FileAttachments.xml
     */
    public String getTraversalXML() {
        return "/WEB-INF/traversal-adapters/com/arsdigita/"
                   + "cms/contentassets/FileAttachment.xml";
    }

    /**
     * The name of the association between the item and the asset, eg
     * 'fileAttachments'.
     */
    public String getProperty() {
        return "fileAttachments";
    }

    /**
     * The class of the authoring kit step
     */
    public Class getAuthoringStep() {
        return FileAttachmentsStep.class;
    }

    /**
     * The label for the authoring step
     */
    public GlobalizedMessage getAuthoringStepLabel() {
        return FileAttachmentGlobalize.AuthoringStepLabel();
    }

    /**
     * The description for the authoring step
     */
    public GlobalizedMessage getAuthoringStepDescription() {
        return FileAttachmentGlobalize.AuthoringStepDescription();
    }

    /**
     * The sort key for the authoring step
     */
    public int getAuthoringStepSortKey() {
        return FileAttachmentConfig.instanceOf().getFileAttachmentStepSortKey();
    }

}
