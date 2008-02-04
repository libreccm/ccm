/*
 * Copyright (C) 2004 Red Hat Inc. All Rights Reserved.
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

import com.arsdigita.cms.contenttypes.AbstractContentTypeLoader;
import com.arsdigita.cms.ContentType;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.lifecycle.LifecycleDefinition;
import com.arsdigita.workflow.simple.WorkflowTemplate;
import com.arsdigita.util.UncheckedWrapperException;
import java.net.URL;
import java.net.MalformedURLException;
import com.arsdigita.util.parameter.Parameter;
import com.arsdigita.util.parameter.URLParameter;


/**
 * Loader.
 *
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: SiteProxyLoader.java 1561 2007-04-16 15:37:21Z apevec $
 */
public class SiteProxyLoader extends AbstractContentTypeLoader {
    public final static String versionId =
        "$Id: SiteProxyLoader.java 1561 2007-04-16 15:37:21Z apevec $" +
        "$Author: apevec $" +
        "$DateTime: 2004/08/17 23:15:09 $";

    private static final String[] TYPES = {
        "/WEB-INF/content-types/com/arsdigita/cms/contenttypes/SiteProxy.xml"
    };

    private URLParameter m_template;

    public SiteProxyLoader() {
        try {
            m_template = new URLParameter
                ("com.arsdigita.cms.contenttypes.siteproxy.defaulttemplate", 
                 Parameter.REQUIRED,
                 new URL(null,
                         "resource:WEB-INF/content-types/com/arsdigita/cms/contenttypes" +
                         "/siteproxy-item.jsp"));
        } catch (MalformedURLException ex) {
            throw new UncheckedWrapperException("cannot parse url", ex);
        }

        register(m_template);
    }

    public String[] getTypes() {
        return TYPES;
    }

    protected void prepareSection(final ContentSection section,
                                  final ContentType type,
                                  final LifecycleDefinition ld,
                                  final WorkflowTemplate wf) {
        super.prepareSection(section, type, ld, wf);

        setDefaultTemplate("SiteProxyDefaultTemplate",
                           "siteproxy-item",
                           (URL)get(m_template),
                           section, type,ld, wf);

    }
}
