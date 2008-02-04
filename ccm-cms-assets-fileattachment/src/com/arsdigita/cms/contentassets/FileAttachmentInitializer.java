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
import com.arsdigita.runtime.LegacyInitEvent;
import com.arsdigita.search.MetadataProviderRegistry;
import com.arsdigita.xml.XML;

/**
 * Initializer
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: 1.1 $ $Date: 2004/12/15 15:37:51 $
 **/

public class FileAttachmentInitializer extends ContentAssetInitializer {

	public final static String versionId =
		"$Id: FileAttachmentInitializer.java 1262 2006-07-17 08:15:45Z cgyg9330 $ by $Author: cgyg9330 $, $DateTime: 2004/03/30 18:21:14 $";

    public FileAttachmentInitializer() {
        super("ccm-cms-assets-fileattachment.pdl.mf");
    }

    public String getTraversalXML() {
		return "/WEB-INF/traversal-adapters/com/arsdigita/"
			+ "cms/contentassets/FileAttachment.xml";
    }

    public String getProperty() {
        return "fileAttachments";
    }

    public String getBaseType() {
        return ContentPage.BASE_DATA_OBJECT_TYPE;
    }

    public Class getAuthoringStep() {
        return FileAttachmentsStep.class;
    }

    public GlobalizedMessage getAuthoringStepLabel() {
        return new GlobalizedMessage(
            "com.arsdigita.cms.contentassets.file_attachment_label",
            "com.arsdigita.cms.contentassets.FileAttachmentResources");
    }

    public GlobalizedMessage getAuthoringStepDescription() {
        return new GlobalizedMessage(
            "com.arsdigita.cms.contentassets.file_attachment_description",
            "com.arsdigita.cms.contentassets.FileAttachmentResources");
    }

    public int getAuthoringStepSortKey() {
        return 2; // XXX config param please
    }

    public void init(LegacyInitEvent evt) {
        super.init(evt);
                                                                                
        URLService.registerFinder(
            FileAttachment.BASE_DATA_OBJECT_TYPE,
            new FileAttachmentURLFinder());
        
		/*
			 * cms registers AssetMetadataProvider for type FileAsset and provides adapter for that 
			 * context. We register a more specific metadataprovider for FileAttachment that provides useful information
			 * about the owner. Because we are using a new metadataprovider, we need to register
			 * adapter for that context. Note this is not the same as the adapters registered by the 
			 * ContentAssetInitializer, because those are used specifically when traversing a content item
			 * that delegates assets to their specific adapters
			 * 
			 * chris.gilbert@westsussex.gov.uk 
			 */
		MetadataProviderRegistry.registerAdapter(
			FileAttachment.BASE_DATA_OBJECT_TYPE,
			new FileAttachmentMetadataProvider());
		XML.parseResource(
			"/WEB-INF/traversal-adapters/com/arsdigita/cms/contentassets/FileAttachment-search.xml",
                new TraversalHandler());
    }

}
