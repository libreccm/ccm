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

package com.arsdigita.cms.contenttypes.ldn;

/**
 * Loader.
 *
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: CouncillorLoader.java 1489 2007-03-19 11:39:58Z apevec $
 */
public class CouncillorLoader extends AbstractContentTypeLoader {
    
    public final static String versionId =
        "$Id: CouncillorLoader.java 1489 2007-03-19 11:39:58Z apevec $" +
        "$Author: apevec $" +
        "$DateTime: 2003/12/19 16:20:50 $";

    private static final String[] TYPES = {
        "/WEB-INF/content-types/com/arsdigita/coventry/cms/contenttypes/Councillor.xml"
    };

    public String[] getTypes() {
        return TYPES;
    }
}
