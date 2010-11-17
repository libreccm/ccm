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
import com.arsdigita.cms.ContentTypeLifecycleDefinition;
import com.arsdigita.cms.ContentTypeWorkflowTemplate;
import com.arsdigita.cms.Template;
import com.arsdigita.cms.TemplateManager;
import com.arsdigita.cms.TemplateManagerFactory;
import com.arsdigita.cms.installer.xml.XMLContentTypeHandler;
import com.arsdigita.cms.lifecycle.LifecycleDefinition;
import com.arsdigita.cms.lifecycle.LifecycleDefinitionCollection;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.KernelExcursion;
import com.arsdigita.loader.PackageLoader;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.Session;
import com.arsdigita.runtime.ScriptContext;
import com.arsdigita.util.Assert;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.workflow.simple.TaskCollection;
import com.arsdigita.workflow.simple.WorkflowTemplate;
import com.arsdigita.xml.XML;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;

import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Logger;

/**
 * This is the base loader that can be used by individual content types.
 * Specifically, it provides type loading functionality in the "run" method
 * that can be used by content types to reduce code duplication.
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #754 $ $Date: 2005/09/02 $ $Author: sskracic $
 **/
public abstract class AbstractContentTypeLoader extends PackageLoader {

    private static final Logger s_log = Logger.getLogger(
            AbstractContentTypeLoader.class);

    public void run(final ScriptContext ctx) {
        new KernelExcursion() {

            protected void excurse() {
                setEffectiveParty(Kernel.getSystemParty());
                createTypes(ctx);
            }
        }.run();
    }

    private void createTypes(ScriptContext ctx) {
        XMLContentTypeHandler handler = new XMLContentTypeHandler();
        String[] contentTypes = getTypes();
        for (int i = 0; i < contentTypes.length; i++) {
            XML.parseResource(contentTypes[i], handler);
        }

        List types = handler.getContentTypes();
        Session ssn = ctx.getSession();
        DataCollection sections = ssn.retrieve(
                ContentSection.BASE_DATA_OBJECT_TYPE);

        while (sections.next()) {
            ContentSection section = (ContentSection) DomainObjectFactory.
                    newInstance(sections.getDataObject());
            if (!isLoadableInto(section)) {
                continue;
            }

            LifecycleDefinitionCollection ldc =
                                          section.getLifecycleDefinitions();
            LifecycleDefinition ld = null;
            if (ldc.next()) {
                ld = ldc.getLifecycleDefinition();
                ldc.close();
            }

            TaskCollection tc = section.getWorkflowTemplates();
            WorkflowTemplate wf = null;
            if (tc.next()) {
                wf = (WorkflowTemplate) tc.getTask();
                tc.close();
            }

            for (Iterator it = types.iterator(); it.hasNext();) {
                final ContentType type = (ContentType) it.next();

                section.addContentType(type);

                prepareSection(section, type, ld, wf);
            }
        }
    }

    protected void prepareSection(final ContentSection section,
                                  final ContentType type,
                                  final LifecycleDefinition ld,
                                  final WorkflowTemplate wf) {
        ContentTypeLifecycleDefinition.updateLifecycleDefinition(section, type,
                                                                 ld);

        ContentTypeWorkflowTemplate.updateWorkflowTemplate(section, type, wf);
    }

    protected abstract String[] getTypes();

    private boolean isLoadableInto(ContentSection section) {
        if (section == null) {
            throw new NullPointerException("section");
        }

        if (getContentSections().size() > 0) {
            return getContentSections().contains(section.getName());
        } else {
            return ContentSection.getConfig().getDefaultContentSection().
                    equals(section.getName());
        }
    }

    /**
     * Returns a list of content sections into which the content type should be
     * installed.
     *
     * <p>If this returns an empty list, then the content type will be loaded
     * into the section specified by {@link
     * com.arsdigita.cms.ContentSectionConfig#getDefaultContentSection()}.</p>
     *
     * <p>The default implementation returns an empty list.</p>
     *
     * @post return != null
     **/
    protected List getContentSections() {
        return java.util.Collections.EMPTY_LIST;
    }

    /**
     *  This provides an easy way to subtypes to register default
     *  templates during the loading.  When this is used, it should
     *  be called by the loader class by overriding prepareSection
     */
    protected Template setDefaultTemplate(final String name,
                                          final String label,
                                          final InputStream templateIs,
                                          final ContentSection section,
                                          final ContentType type,
                                          final LifecycleDefinition ld,
                                          final WorkflowTemplate wf) {
        final Template template = new Template();
        template.setName(name);
        template.setLabel(label);
        template.setContentSection(section);
        template.setParent(section.getTemplatesFolder());

        Assert.isTrue(templateIs != null, "Template not found");

        final BufferedReader input = new BufferedReader(new InputStreamReader(
                templateIs));

        final StringBuffer body = new StringBuffer();

        try {
            String line;

            while ((line = input.readLine()) != null) {
                body.append(line);
                body.append("\n");
            }
        } catch (IOException ioe) {
            throw new UncheckedWrapperException("Template cannot be read", ioe);
        }

        template.setText(body.toString());

        TemplateManagerFactory.getInstance().addTemplate(section, type, template,
                                                         TemplateManager.PUBLIC_CONTEXT);

        template.publish(ld, new Date());
        return template;
    }
}
