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
 * NOTE: Implementing classes may need to define and use configuration parameters
 * to adjust things at load time. These MUST be part of Loader class 
 * implementation and itself and can not be delegated to a Config object 
 * (derived from AbstractConfig). They will (and can) not be persisted into an 
 * registry object (file).
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @author Sören Bernstein <quasi@quasiweb.de>
 * @version $Revision: #754 $ $Date: 2005/09/02 $ $Author: pboy $
 */
public abstract class AbstractContentTypeLoader extends PackageLoader {

    /** Internal logger instance to faciliate debugging. Enable logging output
     *  by editing /WEB-INF/conf/log4j.properties int hte runtime environment
     *  and set com.arsdigita.cms.contenttypes.AbstractContentTypeLoader=DEBUG
     *  by uncommenting or adding the line.                                                   */
    private static final Logger s_log = Logger.getLogger(
                                               AbstractContentTypeLoader.class);

    /**
     * The run method is invoked to execute the loader step. Before calling
     * this method any required parameters registered by the noargs
     * constructer should be set.
     * 
     * Overwrites the parent's class abstract method adding the tast specific
     * createTypes() method.
     * 
     * @param ctx 
     */
    @Override
    public void run(final ScriptContext ctx) {
        new KernelExcursion() {

            @Override
            protected void excurse() {
                setEffectiveParty(Kernel.getSystemParty());
                
                createTypes(ctx);
                
            }
        }.run();
    }

    /**
     * 
     * @param ctx 
     */
    private void createTypes(ScriptContext ctx) {

        XMLContentTypeHandler handler = new XMLContentTypeHandler();
        // Retrieve the content type definition file(s)
        String[] contentTypes = getTypes();
        for (String contentType : contentTypes) {
            XML.parseResource(contentType, handler);
        }

        List types = handler.getContentTypes();
        Session ssn = ctx.getSession();
        DataCollection sections = ssn.retrieve(ContentSection
                                               .BASE_DATA_OBJECT_TYPE);

        while (sections.next()) {
            ContentSection section = (ContentSection) 
                                     DomainObjectFactory.newInstance(
                                                         sections.getDataObject());
            if (!isLoadableInto(section)) {
                continue;
            }

            LifecycleDefinitionCollection ldc = section.getLifecycleDefinitions();
            LifecycleDefinition ld = null;
            if (ldc.next()) {
                ld = ldc.getLifecycleDefinition();
                ldc.close();
            }

            WorkflowTemplate wf = section.getDefaultWorkflowTemplate();

            for (Iterator it = types.iterator(); it.hasNext();) {
                final ContentType type = (ContentType) it.next();

                section.addContentType(type);

                prepareSection(section, type, ld, wf);
            }
        }
    }

    /**
     * 
     * @param section
     * @param type
     * @param ld
     * @param wf 
     */
    protected void prepareSection(final ContentSection section,
                                  final ContentType type,
                                  final LifecycleDefinition ld,
                                  final WorkflowTemplate wf) {
        ContentTypeLifecycleDefinition.updateLifecycleDefinition(section, 
                                                                 type,
                                                                 ld);

        ContentTypeWorkflowTemplate.updateWorkflowTemplate(section, type, wf);
    }

    /**
     * Provides a list of contenttype property definitions.
     * 
     * In the file there are definitions of the type's name as displayed in 
     * content center select box and the authoring steps. These are loaded into 
     * database.
     * 
     * It is a XML file and by convention named after the content type or the
     * module's base name which implements one or more content types. It is
     * usually something like
     * <pre>
     * "/WEB-INF/content-types/com/arsdigita/cms/contenttypes/Event.xml"
     * </pre>
     * The path is fixed by convention and the name is the same as the 
     * content types's.
     * Must be implemented by each content type loader to provide its 
     * specific definition files.
     * @return 
     */
    protected abstract String[] getTypes();

    /**
     * 
     */
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
     * com.arsdigita.cms.ContentSectionConfig#get
     * @return DefaultContentSection()}.</p>
     *
     * <p>The default implementation returns an empty list.</p>
     *
     * @post return != null
     */
    protected List getContentSections() {
        return java.util.Collections.EMPTY_LIST;
    }

    /**
     * This provides an easy way to subtypes to register default templates
     * during the loading.  When this is used, it should be called by the
     * loader class by overriding prepareSection.
     * 
     * @param name
     * @param label
     * @param templateIs
     * @param section
     * @param type
     * @param ld
     * @param wf
     * @return 
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
