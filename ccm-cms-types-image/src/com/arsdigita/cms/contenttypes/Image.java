/*
 * Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.cms.contenttypes;

import com.arsdigita.cms.ContentPage;
import com.arsdigita.cms.ImageAsset;
import com.arsdigita.cms.TextAsset;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;

import java.math.BigDecimal;
import java.util.Date;

/**
 * This content type represents an article.
 *
 * @version $Revision: #6 $ $Date: 2004/08/17 $
 */
public class Image extends ContentPage {

    private final static org.apache.log4j.Logger s_log =
            org.apache.log4j.Logger.getLogger(Image.class);
    /** PDL property name for lead */
    public static final String IMAGE = "image";
    public static final String THUMBNAIL = "thumbnail";
    public static final String TEXT_ASSET = "textAsset";
    public static final String WIDTH = "width";
    public static final String HEIGHT = "height";
    public static final String CAPTION = "caption";
    public static final String DESCRIPTION = "description";
    public static final String ARTIST = "artist";
    public static final String SKIPDAY= "skipDay";
    public static final String SKIPMONTH= "skipMonth";
    public static final String PUBLISHDATE = "publishDate";
    public static final String SOURCE = "source";
    public static final String MEDIA = "media";
    public static final String COPYRIGHT = "copyright";
    public static final String SITE = "site";
    public static final String LICENSE = "license";
    public static final String MATERIAL = "material";
    public static final String TECHNIQUE = "technique";
    public static final String ORIGIN = "origin";
    public static final String ORIGSIZE = "origSize";

    protected static final int SUMMARY_SIZE = 1024;
    /** Data object type for this domain object */
    public static final String BASE_DATA_OBJECT_TYPE = "com.arsdigita.cms.contenttypes.Image";
    private static final ImageConfig s_config = new ImageConfig();

    static {
        s_log.debug("Static initalizer starting...");
        s_config.load();
        s_log.debug("Static initalizer finished.");
    }

    public static ImageConfig getConfig() {
        return s_config;
    }

    public Image() {
        this(BASE_DATA_OBJECT_TYPE);
    }

    public Image(BigDecimal id)
            throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    public Image(OID id)
            throws DataObjectNotFoundException {
        super(id);
    }

    public Image(DataObject obj) {
        super(obj);
    }

    public Image(String type) {
        super(type);
    }

    public ImageAsset getImage() {
        DataObject dobj = (DataObject) get(IMAGE);
        if (dobj != null) {
            return (ImageAsset) DomainObjectFactory.newInstance(dobj);
        } else {
            return null;
        }
    }

    public void setImage(ImageAsset image) {
        set(IMAGE, image);
        setWidth(image.getWidth());
        setHeight(image.getHeight());
        setThumbnail();
    }

    public String getCaption() {
        return (String) get(CAPTION);
    }

    public void setCaption(String caption) {
        set(CAPTION, caption);
    }

    public ImageAsset getThumbnail() {
        DataObject dobj = (DataObject) get(THUMBNAIL);
        if (dobj != null) {
            return (ImageAsset) DomainObjectFactory.newInstance(dobj);
        } else {
            return null;
        }
    }

    private void setThumbnail() {
        try {
            set(THUMBNAIL, getImage().proportionalResizeToWidth(Image.getConfig().getMaxThumbnailWidth()));
        } catch (NullPointerException ex) {
        }
    }

    public BigDecimal getWidth() {
        return (BigDecimal) get(WIDTH);
    }

    private void setWidth(BigDecimal width) {
        set(WIDTH, width);
    }

    public BigDecimal getHeight() {
        return (BigDecimal) get(HEIGHT);
    }

    private void setHeight(BigDecimal height) {
        set(HEIGHT, height);
    }

    /**
     * Return the text asset for this <code>Image</code>. Could return
     * null if there is no text body actually associated with the page
     */
    public TextAsset getTextAsset() {
        DataObject text = (DataObject) get(TEXT_ASSET);
        if (text == null) {
            return null;
        } else {
            return new TextAsset(text);
        }
    }

    /**
     * Pass in a null value to remove the text of this item.
     * Explicitly call text.delete() to remove the text from the database
     */
    public void setTextAsset(TextAsset text) {
        setAssociation(TEXT_ASSET, text);
    }

    /**
     * Return a short summary of the text body for search.
     * This method is WRONG, because the text body could actually
     * be extremely large, and doing substring on it is NOT safe
     */
    @Override
    public String getSearchSummary() {
        TextAsset textAsset = getTextAsset();

        if (textAsset == null) {
            return "";
        }
        return com.arsdigita.util.StringUtils.truncateString(textAsset.getText(),
                SUMMARY_SIZE,
                true);
    }

    @Override
    public String getDescription() {
        return (String) get(DESCRIPTION);
    }

    @Override
    public void setDescription(String description) {
        set(DESCRIPTION, description);
    }

    public String getArtist() {
        return (String) get(ARTIST);
    }

    public void setArtist(String artist) {
        set(ARTIST, artist);
    }

    public Boolean getSkipDay() {
        try {
            return ((Boolean) get(SKIPDAY)).booleanValue();
        } catch (NullPointerException ex) {
            return Boolean.FALSE;
        }
    }

    public void setSkipDay(Boolean skipDay) {
        set(SKIPDAY, skipDay);
    }

    public Boolean getSkipMonth() {
        try {
            return ((Boolean) get(SKIPMONTH)).booleanValue();
        } catch (NullPointerException ex) {
            return Boolean.FALSE;
        }
    }

    public void setSkipMonth(Boolean skipMonth) {
        set(SKIPMONTH, skipMonth);
    }

    public Date getPublishDate() {
        return (Date) get(PUBLISHDATE);
    }

    public void setPublishDate(Date publishDate) {
        set(PUBLISHDATE, publishDate);
    }

    public String getSource() {
        return (String) get(SOURCE);
    }

    public void setSource(String source) {
        set(SOURCE, source);
    }

    public String getMedia() {
        return (String) get(MEDIA);
    }

    public void setMedia(String media) {
        set(MEDIA, media);
    }

    public String getCopyright() {
        return (String) get(COPYRIGHT);
    }

    public void setCopyright(String copyright) {
        set(COPYRIGHT, copyright);
    }

    public String getSite() {
        return (String) get(SITE);
    }

    public void setSite(String site) {
        set(SITE, site);
    }

    public String getLicense() {
        return (String) get(LICENSE);
    }

    public void setLicense(String license) {
        set(LICENSE, license);
    }

    public String getMaterial() {
        return (String) get(MATERIAL);
    }

    public void setMaterial(String material) {
        set(MATERIAL, material);
    }
    public String getTechnique() {
        return (String) get(TECHNIQUE);
    }

    public void setTechnique(String technique) {
        set(TECHNIQUE, technique);
    }

    public String getOrigin() {
        return (String) get(ORIGIN);
    }

    public void setOrigin(String origin) {
        set(ORIGIN, origin);
    }

    public String getOriginalSize() {
        return (String) get(ORIGSIZE);
    }

    public void setOriginalSize(String origSize) {
        set(ORIGSIZE, origSize);
    }

}
