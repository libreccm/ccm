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
package com.arsdigita.cms.portation.modules.contentsection;

import com.arsdigita.cms.portation.conversion.NgCmsCollection;
import com.arsdigita.kernel.ACSObject;
import com.arsdigita.portation.Portable;
import com.fasterxml.jackson.annotation.JsonIdentityReference;

import java.util.UUID;

/**
 * @author <a href="mailto:tosmers@uni-bremen.de>Tobias Osmers<\a>
 * @version created the 2/21/18
 */
public class ItemAttachment<T extends Asset> implements Portable {
    private long attachmentId;
    private String uuid;
    @JsonIdentityReference(alwaysAsId = true)
    private T asset;
    @JsonIdentityReference(alwaysAsId = true)
    private AttachmentList attachmentList;
    private long sortkey;

    /**
     * Constructor for the ng-object.
     *
     * @param asset a ng-asset
     * @param attachmentList the corresponding ng-attachmentList
     */
    public ItemAttachment(final T asset, final AttachmentList attachmentList) {
        this.attachmentId = ACSObject.generateID().longValue();;
        this.uuid = UUID.randomUUID().toString();

        this.asset = asset;
        this.attachmentList = attachmentList;

        this.sortkey = 0;

        NgCmsCollection.itemAttachments.put(this.attachmentId, this);
    }


    public long getAttachmentId() {
        return attachmentId;
    }

    public void setAttachmentId(final long attachmentId) {
        this.attachmentId = attachmentId;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(final String uuid) {
        this.uuid = uuid;
    }

    public T getAsset() {
        return asset;
    }

    public void setAsset(final T asset) {
        this.asset = asset;
    }

    public AttachmentList getAttachmentList() {
        return attachmentList;
    }

    public void setAttachmentList(final AttachmentList attachmentList) {
        this.attachmentList = attachmentList;
    }

    public long getSortkey() {
        return sortkey;
    }

    public void setSortkey(final long sortkey) {
        this.sortkey = sortkey;
    }
}
