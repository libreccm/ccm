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
package com.arsdigita.cms.portation.modules.assets;

import com.arsdigita.cms.portation.conversion.NgCmsCollection;
import com.arsdigita.cms.portation.modules.contentsection.Asset;
import com.arsdigita.portation.modules.core.l10n.LocalizedString;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;
import java.util.Locale;

/**
 * @author <a href="mailto:tosmers@uni-bremen.de>Tobias Osmers<\a>
 * @version created the 4/6/18
 */
public class BinaryAsset extends Asset {
    private LocalizedString description;
    private String fileName;
    private MimeType mimeType;
    private byte[] data;
    private long size;

    /**
     * Constructor for the ng-object.
     *
     * @param trunkBinaryAsset the old trunk object
     */
    public BinaryAsset(final com.arsdigita.cms.BinaryAsset trunkBinaryAsset) {
        super(trunkBinaryAsset);

        this.description = new LocalizedString();
        final Locale language = trunkBinaryAsset.getLanguage() != null
                ? new Locale(trunkBinaryAsset.getLanguage())
                : Locale.getDefault();
        this.description.addValue(language, trunkBinaryAsset.getDescription());

        this.fileName = trunkBinaryAsset.getName();
        try {
            this.mimeType = new MimeType(trunkBinaryAsset.getMimeType()
                    .getMimeType());
        } catch (MimeTypeParseException e) {
            e.printStackTrace();
        }

        this.data = trunkBinaryAsset.getContent();
        this.size = trunkBinaryAsset.getSize();

        NgCmsCollection.binaryAssets.put(this.getObjectId(), this);
    }

    public LocalizedString getDescription() {
        return description;
    }

    public void setDescription(final LocalizedString description) {
        this.description = description;
    }

    public void addDescription(final Locale language, final String description) {
        this.description.addValue(language, description);
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(final String fileName) {
        this.fileName = fileName;
    }

    public MimeType getMimeType() {
        return mimeType;
    }

    public void setMimeType(final MimeType mimeType) {
        this.mimeType = mimeType;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(final byte[] data) {
        this.data = data;
    }

    public long getSize() {
        return size;
    }

    public void setSize(final long size) {
        this.size = size;
    }
}
