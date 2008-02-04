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

package com.arsdigita.london.navigation;

import com.arsdigita.kernel.ACSObject;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.persistence.OID;
import com.arsdigita.util.Assert;
import java.net.URL;
import java.net.MalformedURLException;


public class QuickLink extends ACSObject {

    public static final String BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.london.navigation.QuickLink";

    public static final String TITLE = "title";
    public static final String DESCRIPTION = "description";
    public static final String URL = "url";
    public static final String ICON = "icon";
    public static final String CASCADE = "cascade";

    private QuickLink() {
        this(BASE_DATA_OBJECT_TYPE);
    }

    protected QuickLink(String type) {
        super(type);
    }

    public QuickLink(DataObject obj) {
        super(obj);
    }

    public QuickLink(OID oid) {
        super(oid);
    }

    public static QuickLink create(String title,
                                   String description,
                                   URL url,
                                   URL icon,
                                   boolean cascade) {
        QuickLink link = new QuickLink();
        link.setup(title, description, url, icon, cascade);
        return link;
    }

    protected void setup(String title,
                         String description,
                         URL url,
                         URL icon,
                         boolean cascade) {
        setTitle(title);
        setDescription(description);
        setURL(url);
        setIcon(icon);
        setCascade(cascade);
    }

    public void setTitle(String title) {
        Assert.exists(title, String.class);
        set(TITLE, title);
    }

    public String getTitle() {
        return (String)get(TITLE);
    }

    public void setDescription(String description) {
        set(DESCRIPTION, description);
    }

    public String getDescription() {
        return (String)get(DESCRIPTION);
    }

    public void setURL(URL url) {
        Assert.exists(url, URL.class);
        set(URL, url.toString());
    }

    public URL getURL() {
        try {
            return new URL((String)get(URL));
        } catch (MalformedURLException ex) {
            throw new UncheckedWrapperException("cannot parse URL" + get(URL), ex);
        }
    }

    public void setIcon(URL icon) {
        set(ICON, icon == null ? null : icon.toString());
    }

    public URL getIcon() {
        try {
            Object icon = get(ICON);
            if (icon == null) {
                return null;
            }
            return new URL((String)icon);
        } catch (MalformedURLException ex) {
            throw new UncheckedWrapperException("cannot parse URL" + get(ICON), ex);
        }
    }

    public boolean cascade() {
        return ((Boolean)get(CASCADE)).booleanValue();
    }

    public void setCascade(boolean cascade) {
        set(CASCADE, new Boolean(cascade));
    }

}
