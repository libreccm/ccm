/*
 * Copyright (C) 2001, 2002, 2003 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the CCM Public
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of
 * the License at http://www.redhat.com/licenses/ccmpl.html
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */

package com.arsdigita.cms.contenttypes;

import com.arsdigita.cms.contenttypes.AbstractContentTypeLoader;

/**
 * Loader.
 *
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: //ps/apps/london/htmlform/dev/src/com/arsdigita/cms/contenttypes/HTMLFormLoader.java#1 $
 */
public class HTMLFormLoader extends AbstractContentTypeLoader {
    public final static String versionId =
        "$Id: //ps/apps/london/htmlform/dev/src/com/arsdigita/cms/contenttypes/HTMLFormLoader.java#1 $" +
        "$Author: mbooth $" +
        "$DateTime: 2004/03/05 09:47:41 $";

    private static final String[] TYPES = {
        "/WEB-INF/content-types/com/arsdigita/cms/contenttypes/HTMLForm.xml"
    };

    public String[] getTypes() {
        return TYPES;
    }
}
