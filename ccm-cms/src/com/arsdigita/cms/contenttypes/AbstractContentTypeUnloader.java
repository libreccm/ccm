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

import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.ContentType;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.KernelExcursion;
import com.arsdigita.loader.PackageLoader;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.Session;
import com.arsdigita.runtime.ScriptContext;
import com.arsdigita.xml.XML;
import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Logger;

/**
 * 
 * 
 * @author Tobias Osmers <tosmers@uni-bremen.de>
 * @version $Revision: #1 $ $Date: 2015/05/18 $
 */
public abstract class AbstractContentTypeUnloader extends PackageLoader {
    /** 
     * Internal logger instance to faciliate debugging. Enable logging output
     * by editing /WEB-INF/conf/log4j.properties int hte runtime environment
     * and set com.arsdigita.cms.contenttypes.AbstractContentTypeLoader=DEBUG
     * by uncommenting or adding the line.
     */
    private static final Logger s_log = Logger.getLogger(
            AbstractContentTypeLoader.class);
    
    /**
     * The run method is invoked to execute the unloader step. Before calling
     * this method any required parameters registered by the noargs
     * constructer should be set.
     * 
     * Overwrites the parent's class abstract method adding the task specific
     * sweep() method.
     * 
     * @param ctx The context to the unload-script
     */
    @Override
    public void run(final ScriptContext ctx) {
        new KernelExcursion() {

            @Override
            protected void excurse() {
                setEffectiveParty(Kernel.getSystemParty());
                
                sweepTypes(ctx);
                
            }
        }.run();
    }
    
    /**
     * Parses the content-types specified in the "contentType".xml-file and
     * stores them into a list. Then retrieves all contentitems from the db into 
     * a dataCollection and unpulishes and deletes all the contentitems/instances
     * to the content-types stored in the list. Then the content-types are
     * removed form all sections.
     * 
     * @param ctx The context to the unload-script
     */
    private void sweepTypes(ScriptContext ctx) {
        XMLContentTypeHandler handler = new XMLContentTypeHandler(false);
        // Retrieve the content type definition file(s)
        String[] contentTypes = getTypes();
        for (String contentType : contentTypes) {
            XML.parseResource(contentType, handler);
        }

        List types = handler.getContentTypes();
        Session ssn = ctx.getSession();
        
        // Removes all contentitems/instances of the specified
        // contenttype
        DataCollection contentItems = ssn.retrieve(
                ContentItem.BASE_DATA_OBJECT_TYPE);
        while (contentItems.next()) {
            ContentItem contentItem;
            try {
                contentItem = (ContentItem)
                    DomainObjectFactory.newInstance(contentItems.getDataObject());
            } catch (Exception ex) {
                continue;
            }
            if (contentItem == null || !contentItem.isPublished()) { 
                continue; 
            }
            for (Iterator it = types.iterator(); it.hasNext(); ) {
                final ContentType type = (ContentType) it.next();
                if (contentItem.getContentType().equals(type)) {
                    contentItem.unpublish();
                    contentItem.delete();
                }
            }
        }
        
        // Removes the types from the sections.
        DataCollection sections = ssn.retrieve(
                ContentSection.BASE_DATA_OBJECT_TYPE);
        while (sections.next()) {
            ContentSection section = (ContentSection) 
                    DomainObjectFactory.newInstance(sections.getDataObject());
            if (!isLoadableInto(section)) {
                continue;
            }

            for (Iterator it = types.iterator(); it.hasNext();) {
                final ContentType type = (ContentType) it.next();
                section.removeContentType(type);
            }
        }
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
     * 
     * @return This content type's property definitions through the ".xml"-file
     */
    protected abstract String[] getTypes();
    
    /**
     * Checks, if its possible to load into the given section.
     * 
     * @param The section to be checked
     * @return true, if its possible to load into the section, otherwise false
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
}
