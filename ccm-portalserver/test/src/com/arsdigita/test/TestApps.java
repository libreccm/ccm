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
package com.arsdigita.portalserver.test;

import com.arsdigita.kernel.EmailAddress;

public final class TestApps {
    public static final String KM_MANAGER =  "Knowledge Manager";
    public static final String DOC_MANAGER =  "Document Manager";
    public static final String FORUM = "Forum";
    public static final String PORTAL = "test";

    public static final String MANAGERS = "Managers";
    public static final String EDITORS = "Editors";
    public static final String MEMBERS = "Members";

    public static final String DEFAULT_DOMAIN = "redhat.com";

    public static final EmailAddress SWA = new EmailAddress("swa@" + DEFAULT_DOMAIN);
    public static final EmailAddress USER = new EmailAddress("joeuser@" + DEFAULT_DOMAIN);
    public static final EmailAddress MANAGER = new EmailAddress("joemanager@" + DEFAULT_DOMAIN);
    public static final EmailAddress EDITOR = new EmailAddress("joeeditor@" + DEFAULT_DOMAIN);

    private TestApps() {
    }
}
