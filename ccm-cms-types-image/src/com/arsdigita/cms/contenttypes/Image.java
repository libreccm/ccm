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
            org.apache.log4j.Logger.getLogger(GenericArticle.class);
    /** PDL property name for lead */
    public static final String IMAGE = "image";
//    public static final String WIDTH = "width";
//    public static final String HEIGHT = "height";
    public static final String CAPTION = "caption";
    public static final String DESCRIPTION = "description";
    public static final String ARTIST = "artist";
    public static final String PUBLISHDATE = "publishDate";
    public static final String SOURCE = "source";
    public static final String MEDIA = "media";
    public static final String COPYRIGHT = "copyright";
    public static final String SITE = "site";
    /** Data object type for this domain object */
    public static final String BASE_DATA_OBJECT_TYPE = "com.arsdigita.cms.contenttypes.Image";
    private static final ImageConfig s_config = new ImageConfig();

    static {
        s_log.debug("Static initalizer starting...");
        s_config.load();
        s_log.debug("Static initalizer finished.");
    }

    public static final ImageConfig getConfig() {
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
    }

    public String getCaption() {
        return (String) get(CAPTION);
    }

    public void setCaption(String caption) {
        set(CAPTION, caption);
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
}
