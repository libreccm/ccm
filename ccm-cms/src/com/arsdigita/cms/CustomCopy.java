/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.cms;

import com.arsdigita.persistence.metadata.Property;

/**
 * This interface represents an item which may be a component of a
 * ContentItem but may not itself extend ContentItem. Implementing
 * CustomCopy is not necessary for a component to be
 * published. However, only CustomCopy objects can override default
 * attribute/role copying behavior via its copyProperty method. Only
 * DomainObjects should implement this interface.
 *
 */
public interface CustomCopy {
    /**
     * Copy the specified property (attribute or association) from the
     * specified source item. This method almost completely overrides
     * the metadata-driven methods in <code>ObjectCopier</code>. If
     * the property in question is an association to
     * <code>ContentItem</code>(s), this method should <b>only</b>
     * call <code>FooContentItem newChild = copier.copy(srcItem, this,
     * riginalChild, property);</code> An attempt to call any other
     * method in order to copy the child will most likely have
     * disastrous consequences. In fact, this copier method should
     * generally be called for any DomainObject copies, later making
     * custom changes, unless the copying behavior itself is different
     * from the default (or the item should not be copied at all at
     * this point).
     *
     *
     * If a subclass of a class which implements CustomCopy overrides
     * this method, it should return <code>super.copyProperty</code>
     * for properties which do not need custom behavior in order to
     * indicate that it is not interested in handling the property in
     * any special way.
     *
     * For example, the {@link Article} class extends
     * <code>ContentItem</code>.  It defines an association to 0..n
     * {@link ImageAsset}. Unfortunately, the association has
     * "order_n" and "caption" link attributes, which cannot be copied
     * automatically, since the persistence system doesn't know enough
     * about them. The following sample code from the {@link Article}
     * class ensures that images are copied correctly (note that this
     * example no longer applies to the Article case, so it remains as
     * a general example):
     *
     * <blockquote><pre><code>
     * protected boolean copyProperty(CustomCopy srcItem, Property property, ItemCopier copier) {
     *    String attrName = property.getName();
     *   // We only care about copying images; all other properties should
     *   // be handled in a default manner
     *   if (!attrName.equals(IMAGES))
     *     return super.copyProperty(srcItem, property, copier);
     *
     *   // The source item is guaranteed to be of the correct type
     *   Article src = (Article)srcItem;
     *
     *   // Retrieve images from the source
     *   ImageAssetCollection srcImages = src.getImages();
     *
     *   // Copy each image using the passed-in copier
     *   while(srcImages.next()) {
     *     ImageAsset srcImage = srcImages.getImage();
     *
     *     // Images may be shared between items, and so they are not
     *     // composite. Thus, we are going to pass false to the object
     *     // copier in the second parameter
     *     ImageAsset newImage = 
     *         (ImageAsset)copier.copy(srcItem, this, srcImage, property);
     *
     *     // Add the new image to the new item
     *     addImage(newImage, src.getCaption(srcImage));
     *   }
     *
     *   // Tell the automated copying service that we have handled this
     *   // property
     *   return true;
     * }
     * </code></pre></blockquote>
     *
     * @param source the source CustomCopy item
     * @param property the property to copy
     * @param copier a temporary class that is able to copy a child item
     *   correctly.
     * @return true if the property was copied; false to indicate
     *   that regular metadata-driven methods should be used
     *   to copy the property.
     */
    boolean copyProperty(final CustomCopy source,
			 final Property property,
			 final ItemCopier copier);
}
