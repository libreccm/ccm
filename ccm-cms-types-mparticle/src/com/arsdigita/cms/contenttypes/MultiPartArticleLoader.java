/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
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

import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.ContentType;
import com.arsdigita.cms.lifecycle.LifecycleDefinition;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.util.parameter.Parameter;
import com.arsdigita.util.parameter.URLParameter;
import com.arsdigita.workflow.simple.WorkflowTemplate;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Loader.
 *
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: MultiPartArticleLoader.java 1561 2007-04-16 15:37:21Z apevec $
 */
public class MultiPartArticleLoader extends AbstractContentTypeLoader {
    public final static String versionId =
        "$Id: MultiPartArticleLoader.java 1561 2007-04-16 15:37:21Z apevec $" +
        "$Author: apevec $" +
        "$DateTime: 2004/08/17 23:15:09 $";

    private static final String[] TYPES = {
        "/WEB-INF/content-types/com/arsdigita/cms/contenttypes/MultiPartArticle.xml"
    };

    public String[] getTypes() {
        return TYPES;
    }

    private URLParameter m_template;

    public MultiPartArticleLoader() {
        try {
            m_template = new URLParameter
                ("com.arsdigita.cms.contenttypes.mparticle.template",
                 Parameter.REQUIRED,
                 new URL(null,
                         "resource:WEB-INF/content-types/com/arsdigita/cms/contenttypes" +
                         "/mparticle-item.jsp"));
        } catch (MalformedURLException ex) {
            throw new UncheckedWrapperException("cannot parse url", ex);
        }

        register(m_template);
    }

    protected void prepareSection(final ContentSection section,
                                  final ContentType type,
                                  final LifecycleDefinition ld,
                                  final WorkflowTemplate wf) {
        super.prepareSection(section, type, ld, wf);

        setDefaultTemplate("MultiPartArticle-mparticle-item",
                           "mparticle-item",
                           (URL)get(m_template),
                           section, type,ld, wf);

    }
}
