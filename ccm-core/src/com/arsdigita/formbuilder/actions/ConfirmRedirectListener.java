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
package com.arsdigita.formbuilder.actions;

import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.metadata.ObjectType;
import com.arsdigita.web.RedirectSignal;

import com.arsdigita.formbuilder.PersistentProcessListener;

import java.math.BigDecimal;

public class ConfirmRedirectListener extends PersistentProcessListener {
    public static final String BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.formbuilder.actions.ConfirmRedirectListener";

    public static final String URL = "url";

    public ConfirmRedirectListener() {
        this(BASE_DATA_OBJECT_TYPE);
    }

    public ConfirmRedirectListener(String typeName) {
        super(typeName);
    }

    public ConfirmRedirectListener(ObjectType type) {
        super(type);
    }

    public ConfirmRedirectListener(DataObject obj) {
        super(obj);
    }

    public ConfirmRedirectListener(BigDecimal id) {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    public ConfirmRedirectListener(OID oid) {
        super(oid);
    }

    public static ConfirmRedirectListener create(String name,
                                                 String description,
                                                 String url) {
        ConfirmRedirectListener l = new ConfirmRedirectListener();

        l.setup(name, description, url);

        return l;
    }

    protected void setup(String name,
                         String description,
                         String url) {
        super.setup(name, description);
        set(URL, url);
    }

    // XXX hack to get around some wierd issues
    // with mdsql associations where the object
    // type in question is a subtype of the
    // one named in the association definition
    public boolean isContainerModified() {
        return false;
    }


    public String getUrl() {
        return (String)get(URL);
    }

    public void setUrl(String url) {
        set(URL, url);
    }

    public FormProcessListener createProcessListener() {
        return new ConfirmRedirectProcessListener(getUrl());
    }

    private class ConfirmRedirectProcessListener implements FormProcessListener {
        String m_url;

        public ConfirmRedirectProcessListener(String url) {
            m_url = url;
        }

        public void process(FormSectionEvent e) {
            throw new RedirectSignal(m_url, true);
        }
    }
}
