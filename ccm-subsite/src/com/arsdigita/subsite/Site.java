/*
 * Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
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
 */

package com.arsdigita.subsite;

import com.arsdigita.categorization.Category;
import com.arsdigita.cms.TemplateContext;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.kernel.ACSObject;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.util.Assert;
import com.arsdigita.web.Application;

import java.math.BigDecimal;

/**
 * Represents a single subsite (as created by the admin interface)
 *
 */
public class Site extends ACSObject {
    
    public static final String BASE_DATA_OBJECT_TYPE = 
                               "com.arsdigita.subsite.Site";

    /** Title for a subsite, text field. */
    public static final String TITLE = "title";
    /** Description for a subsite, text field. */
    public static final String DESCRIPTION = "description";
    /** Subsite host name, text field, must be unique. */
    public static final String HOSTNAME = "hostname";
    /** Directory containin the theme to be used for the subsite. */
    public static final String STYLE_DIRECTORY = "styleDirectory";
    public static final String FRONT_PAGE = "frontPage";
    public static final String TEMPLATE_CONTEXT = "templateContext";

    /**
     * 
     */
    public Site() {
        this(BASE_DATA_OBJECT_TYPE);
    }
    
    /** 
     * Constructor
     * @param type
     */
    public Site(String type) {
        super(type);
    }

    public Site(DataObject obj) {
        super(obj);
    }

    public static Site create(String title,
                              String description,
                              String hostname,
                              String styleDir,
                              Category root,
                              Application frontPage) {
        Site site = new Site();
        site.setup(title, 
                   description,
                   hostname,
                   styleDir,
                   root,
                   frontPage);
        return site;
    }

    /** 
     * 
     * @param title
     * @param description
     * @param hostname
     * @param styleDir
     * @param root
     * @param frontPage
     */
    protected void setup(String title,
                         String description,
                         String hostname,
                         String styleDir,
                         Category root,
                         Application frontPage) {
        set(TITLE, title);
        set(DESCRIPTION, description);
        set(HOSTNAME, hostname);
        set(STYLE_DIRECTORY, styleDir);
        set(FRONT_PAGE, frontPage);

        TemplateContext context = TemplateContext.create("subsite-" + getID(),
                                                         title,
                                                         description);
        set(TEMPLATE_CONTEXT, context);

        Category.setRootForObject(this, root);
    }

    @Override
    public void beforeSave() {
        super.beforeSave();

        TemplateContext ctx = getTemplateContext();
        if (ctx.isModified()) {
            ctx.save();
        }
    }

    @Override
    public void beforeDelete() {
        super.beforeDelete();
        
        TemplateContext ctx = getTemplateContext();
        ctx.delete();
    }

    public static Site retrieve(BigDecimal id)
        throws DataObjectNotFoundException {

        return (Site)DomainObjectFactory.newInstance(
            new OID(BASE_DATA_OBJECT_TYPE, id)
        );
    }

    public static Site retrieve(DataObject obj) {
        return (Site)DomainObjectFactory.newInstance(obj);
    }
    
    /** 
     * 
     * @param hostname
     * @return
     * @throws DataObjectNotFoundException
     */
    public static Site findByHostname(String hostname)
        throws DataObjectNotFoundException {
        DataCollection sites = SessionManager.getSession()
            .retrieve(BASE_DATA_OBJECT_TYPE);
        
        sites.addEqualsFilter(HOSTNAME, hostname);
        
        if (sites.next()) {
            DataObject obj = sites.getDataObject();
            if (sites.next()) {
                sites.close();
                Assert.isTrue(false, "hostname is unique");
            }
            return retrieve(obj);
        }
        
        throw new DataObjectNotFoundException(
            "cannot find site for hostname" + hostname
        );
    }

    public TemplateContext getTemplateContext() {
        return (TemplateContext)DomainObjectFactory
            .newInstance((DataObject)get(TEMPLATE_CONTEXT));
    }

    public void setTitle(String title) {
        set(TITLE, title);
        getTemplateContext().setLabel(title);
    }

    public String getTitle() {
        return(String)get(TITLE);
    }

    public void setDescription(String description) {
        set(DESCRIPTION, description);
        getTemplateContext().setDescription(description);
    }

    public String getDescription() {
        return(String)get(DESCRIPTION);
    }

    public void setHostname(String hostname) {
        set(HOSTNAME, hostname);
    }

    public String getHostname() {
        return(String)get(HOSTNAME);
    }

    public void setStyleDirectory(String styleDirectory) {
        set(STYLE_DIRECTORY, styleDirectory);
    }

    public String getStyleDirectory() {
        return(String)get(STYLE_DIRECTORY);
    }

    public void setRootCategory(Category rootCategory) {
        Category.setRootForObject(this, rootCategory);
    }

    public Category getRootCategory() {
        return Category.getRootForObject(this);
    }

    public void setFrontPage(Application frontPage) {
        set(FRONT_PAGE, frontPage);
    }

    public Application getFrontPage() {
        return (Application)DomainObjectFactory
            .newInstance((DataObject)get(FRONT_PAGE));
    }

}
