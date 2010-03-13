/*
 * Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
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
 */

package com.arsdigita.london.theme;

import com.arsdigita.util.Assert;
import com.arsdigita.kernel.ACSObject;
import com.arsdigita.kernel.Party;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.DataAssociation;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainObjectFactory;
import java.math.BigDecimal;
import java.util.Date;


/**
 * Represents a Theme within the system.  Specifically, it represents
 * a grouping of files that can be used to style a given subsite.
 *
 *  @author Randy Graebner %lt;randyg@alum.mit.edu&gt;
 */
public class Theme extends ACSObject {
    
    public static final String BASE_DATA_OBJECT_TYPE = 
        "com.arsdigita.london.theme.Theme";

    public static final String TITLE = "title";
    public static final String DESCRIPTION = "description";
    // this holds a reference to the directory holding the files.
    // This is rooted at /__ccm__/themes/.  Therefore, if you have
    // a "holiday" theme, the value of this column will be 'holiday'
    // so the files will be located at /__ccm__/themes/holiday
    public static final String URL = "url";

    //  Information about when the theme was last published.  This 
    //  allows the user to see that their files have been published
    public static final String LAST_PUBLISHED_USER = "lastPublishedUser";
    public static final String LAST_PUBLISHED_DATE = "lastPublishedDate";

    public static final String THEME_FILES = "themeFiles";

    public Theme() {
        this(BASE_DATA_OBJECT_TYPE);
    }
    
    public Theme(String type) {
        super(type);
    }

    public Theme(DataObject obj) {
        super(obj);
    }

    public Theme(String title, String description, String url) {
        this();
        setTitle(title);
        setDescription(description);
        setURL(url);
    }

    public Theme(BigDecimal id) throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    public Theme(OID oid) throws DataObjectNotFoundException {
        super(oid);
    }

    public static Theme retrieve(BigDecimal id)
        throws DataObjectNotFoundException {

        return (Theme)DomainObjectFactory.newInstance(
            new OID(BASE_DATA_OBJECT_TYPE, id)
        );
    }

    public static Theme retrieve(DataObject obj) {
        return (Theme)DomainObjectFactory.newInstance(obj);
    }

    public static Theme findByURL(String url) 
        throws DataObjectNotFoundException {
        DataCollection themes = SessionManager.getSession()
            .retrieve(BASE_DATA_OBJECT_TYPE);
        
        themes.addEqualsFilter(URL, url);
        
        if (themes.next()) {
            DataObject obj = themes.getDataObject();
            if (themes.next()) {
                themes.close();
                Assert.isTrue(false, "url is unique");
            }
            return retrieve(obj);
        }
        
        throw new DataObjectNotFoundException(
            "cannot find theme with the url " + url
        );
    }

    public void setTitle(String title) {
        set(TITLE, title);
    }

    public String getTitle() {
        return(String)get(TITLE);
    }

    public void setDescription(String description) {
        set(DESCRIPTION, description);
    }

    public String getDescription() {
        return(String)get(DESCRIPTION);
    }

    /**
     * The holds a reference to the directory holding the files.
     * This is rooted at /__ccm__/themes/.  Therefore, if you have
     * a "holiday" theme, the value of this parameter will be 'holiday'
     * so the files will be located at /__ccm__/themes/holiday
     */
    public void setURL(String url) {
        set(URL, url);
    }

    public String getURL() {
        return(String)get(URL);
    }

    public Party getLastPublishedUser() {
        DataObject object = (DataObject)get(LAST_PUBLISHED_USER);
        if (object != null) {
            return (Party)DomainObjectFactory.newInstance(object);
        } 
        return null;
    }

    public void setLastPublishedUser(Party party) {
        setAssociation(LAST_PUBLISHED_USER, party);
    }

    public Date getLastPublishedDate() {
        return (Date)get(LAST_PUBLISHED_DATE);
    }

    public void setLastPublishedDate(Date date) {
        set(LAST_PUBLISHED_DATE, date);
    }

    public ThemeFileCollection getAllThemeFiles() {
        DataAssociation files = (DataAssociation)get(THEME_FILES);
        if (files != null) {
            return new ThemeFileCollection(files);
        } else {
            return null;
        }
    }

    public ThemeFileCollection getPublishedThemeFiles() {
        DataAssociation files = (DataAssociation)get(THEME_FILES);
        if (files != null) {
            files.addEqualsFilter(ThemeFile.VERSION, ThemeFile.LIVE);
            return new ThemeFileCollection(files);
        } else {
            return null;
        }
    }

    public ThemeFileCollection getDraftThemeFiles() {
        DataAssociation files = (DataAssociation)get(THEME_FILES);
        if (files != null) {
            files.addEqualsFilter(ThemeFile.VERSION, ThemeFile.DRAFT);
            return new ThemeFileCollection(files);
        } else {
            return null;
        }
    }
}
