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

package com.arsdigita.london.contenttypes;

import com.arsdigita.cms.contenttypes.*;

import org.apache.log4j.Logger;

/**
 * The CMS initializer.
 *
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: CouncillorInitializer.java 1489 2007-03-19 11:39:58Z apevec $
 */
public class CouncillorInitializer extends ContentTypeInitializer {

    private static final Logger s_log = Logger.getLogger(CouncillorInitializer.class);

    public CouncillorInitializer() {
        super("ccm-ldn-types-councillor.pdl.mf", Councillor.BASE_DATA_OBJECT_TYPE);
    }

    @Override
    public String getTraversalXML() {
        return "WEB-INF/traversal-adapters/com/arsdigita/london/contenttypes/Councillor.xml";
    }

    @Override
    public String[] getStylesheets() {
        return new String[] { "/__ccm__/themes/coventry/types/Councillor.xsl" };
    }
}
