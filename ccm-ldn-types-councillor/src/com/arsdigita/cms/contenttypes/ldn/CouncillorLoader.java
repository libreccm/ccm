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

import com.arsdigita.cms.contenttypes.AbstractContentTypeLoader;

/**
 * Loader.
 *
 * @author Justin Ross &lt;jross@redhat.com&gt;
 */
public class CouncillorLoader extends AbstractContentTypeLoader {
    
    private static final String[] TYPES = {
        "/WEB-INF/content-types/com/arsdigita/cms/contenttypes/ldn/Councillor.xml"
    };

    @Override
    public String[] getTypes() {
        return TYPES;
    }
}
