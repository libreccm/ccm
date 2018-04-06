/*
 * Copyright (C) 2015 LibreCCM Foundation.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package com.arsdigita.cms.portation.conversion;

import com.arsdigita.cms.portation.modules.assets.BinaryAsset;
import com.arsdigita.cms.portation.modules.assets.LegalMetadata;
import com.arsdigita.cms.portation.modules.contentsection.Asset;
import com.arsdigita.cms.portation.modules.contentsection.AttachmentList;
import com.arsdigita.cms.portation.modules.contentsection.ContentItem;
import com.arsdigita.cms.portation.modules.contentsection.ContentSection;
import com.arsdigita.cms.portation.modules.contentsection.ContentType;
import com.arsdigita.cms.portation.modules.contentsection.ItemAttachment;
import com.arsdigita.cms.portation.modules.lifecycle.Lifecycle;
import com.arsdigita.cms.portation.modules.lifecycle.LifecycleDefinition;
import com.arsdigita.cms.portation.modules.lifecycle.Phase;
import com.arsdigita.cms.portation.modules.lifecycle.PhaseDefinition;

import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:tosmers@uni-bremen.de>Tobias Osmers<\a>
 * @version created the 2/12/18
 */
public class NgCmsCollection {
    public static Map<Long, Asset> assets = new HashMap<>();
    public static Map<Long, BinaryAsset> binaryAssets = new HashMap<>();
    public static Map<Long, LegalMetadata> legalMetadatas = new HashMap<>();
    public static Map<Long, ItemAttachment> itemAttachments = new HashMap<>();
    public static Map<Long, AttachmentList> attachmentLists = new HashMap<>();
    public static Map<Long, ContentItem> contentItems = new HashMap<>();

    public static Map<Long, ContentType> contentTypes = new HashMap<>();
    public static Map<Long, ContentSection> contentSections = new HashMap<>();

    public static Map<Long, Phase> phases = new HashMap<>();

    public static Map<Long, Lifecycle> lifecycles = new HashMap<>();
    public static Map<Long, LifecycleDefinition> lifecycleDefinitions = new
            HashMap<>();
    public static Map<Long, PhaseDefinition> phaseDefinitions = new HashMap<>();


    /**
     * Private constructor to prevent the instantiation of this class.
     */
    private NgCmsCollection() {}
}
