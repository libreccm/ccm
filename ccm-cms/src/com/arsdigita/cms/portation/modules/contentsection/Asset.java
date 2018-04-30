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
import com.arsdigita.portation.modules.core.core.CcmObject;
import com.arsdigita.portation.modules.core.l10n.LocalizedString;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Base class for all assets providing common fields. This class is
 * <strong>not</strong> indented for direct use. Only to sub classes should be
 * used.
 *
 * @author <a href="mailto:tosmers@uni-bremen.de>Tobias Osmers<\a>
 * @version created the 2/21/18
 */
public class Asset extends CcmObject {
    @JsonIgnore
    private List<ItemAttachment<?>> itemAttachments;
    private LocalizedString title;

    /**
     * Constructor for the ng-object.
     *
     * @param trunkAsset the old trunk object
     */
    public Asset(final com.arsdigita.cms.Asset trunkAsset) {
        super(trunkAsset);

        this.itemAttachments = new ArrayList<>();

        this.title = new LocalizedString();
        final Locale language = new Locale(trunkAsset.getLanguage());
        this.title.addValue(language, trunkAsset.getName());

        NgCmsCollection.assets.put(this.getObjectId(), this);
    }

    /**
     * Specific constructor for subclass SideNews.
     *
     * @param objectId The id of this asset
     * @param displayName The display name as the {@link CcmObject}
     */
    public Asset(final BigDecimal objectId, final String displayName) {
        super(objectId, displayName);

        this.itemAttachments = new ArrayList<>();

        this.title = new LocalizedString();
        final Locale language = Locale.getDefault();
        this.title.addValue(language, displayName + "_title");

        NgCmsCollection.assets.put(this.getObjectId(), this);
    }

    public List<ItemAttachment<?>> getItemAttachments() {
        return itemAttachments;
    }

    public void setItemAttachments(final List<ItemAttachment<?>>
                                           itemAttachments) {
        this.itemAttachments = itemAttachments;
    }

    public void addItemAttachment(final ItemAttachment itemAttachment) {
        this.itemAttachments.add(itemAttachment);
    }

    public void removeItemAttachment(final ItemAttachment itemAttachment) {
        this.itemAttachments.remove(itemAttachment);
    }

    public LocalizedString getTitle() {
        return title;
    }

    public void setTitle(final LocalizedString title) {
        this.title = title;
    }

    public void addTitle(final Locale language, final String title) {
        this.title.addValue(language, title);
    }
}
