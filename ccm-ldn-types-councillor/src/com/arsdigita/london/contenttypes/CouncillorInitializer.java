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

package com.arsdigita.coventry.cms.contenttypes;

import com.arsdigita.cms.contenttypes.*;import com.arsdigita.cms.ContentType;

import com.arsdigita.db.*;
import com.arsdigita.persistence.pdl.*;
import com.arsdigita.runtime.*;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.domain.DomainObjectInstantiator;
import com.arsdigita.persistence.DataObject;

import org.apache.log4j.Logger;

/**
 * The CMS initializer.
 *
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: CouncillorInitializer.java 1489 2007-03-19 11:39:58Z apevec $
 */
public class CouncillorInitializer extends ContentTypeInitializer {
    public final static String versionId =
        "$Id: CouncillorInitializer.java 1489 2007-03-19 11:39:58Z apevec $" +
        "$Author: apevec $" +
        "$DateTime: 2004/03/03 22:35:47 $";

    private static final Logger s_log = Logger.getLogger(CouncillorInitializer.class);

    public CouncillorInitializer() {
        super("ccm-ldn-coventry-councillor.pdl.mf", Councillor.BASE_DATA_OBJECT_TYPE);
    }

    public String getTraversalXML() {
        return "WEB-INF/traversal-adapters/com/arsdigita/coventry/cms/contenttypes/Councillor.xml";
    }

    public String[] getStylesheets() {
        return new String[] { "/__ccm__/themes/coventry/types/Councillor.xsl" };
    }
}
