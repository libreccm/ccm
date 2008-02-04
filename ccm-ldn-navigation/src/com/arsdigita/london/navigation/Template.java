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

package com.arsdigita.london.navigation;


import com.arsdigita.categorization.Category;
import com.arsdigita.db.Sequences;
import com.arsdigita.domain.DomainServiceInterfaceExposer;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.Filter;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;

import com.arsdigita.util.UncheckedWrapperException;
import org.apache.log4j.Logger;

import java.sql.SQLException;
import java.math.BigDecimal;

public class Template extends DomainObject {
    private static final Logger s_log = Logger.getLogger(Template.class);

    public static final String ID = "id";
    public static final String TITLE = "title";
    public static final String DESCRIPTION = "description";
    public static final String URL = "url";

    public static final String TEMPLATE_CATEGORIES = "templateCategories";
    public static final String TEMPLATES = "templates";
    public static final String DISPATCHER_CONTEXT = "dispatcherContext";
    public static final String DEFAULT_DISPATCHER_CONTEXT = "public";
    public static final String DEFAULT_USE_CONTEXT = "default";

    public static final String BASE_DATA_OBJECT_TYPE = 
        "com.arsdigita.london.navigation.Template";

    // used by findBestForCategory
    private static final String TEMPLATES_FOR_CATEGORY = 
        "com.arsdigita.london.navigation.getTemplatesForCategory";

    protected Template() {
        this(BASE_DATA_OBJECT_TYPE);
    }

    protected Template(String type) {
        super(type);
    }

    public Template(DataObject obj) {
        super(obj);
    }


    public void initialize() {
        super.initialize();
        
        if (get(ID) == null) {
            try {
                set(ID, Sequences.getNextValue());
            } catch (SQLException ex) {
                throw new UncheckedWrapperException("cannot assign id", ex);
            }
        }
    }
    
    public static Template create(String title,
                                  String description,
                                  String url) {
        Template temp = new Template();
        temp.setTitle(title);
        temp.setDescription(description);
        temp.setURL(url);
        return temp;
    }

    public static Template retrieveByURL(String url) 
        throws DataObjectNotFoundException {

        Session session = SessionManager.getSession();
        DataCollection temp = session.retrieve(BASE_DATA_OBJECT_TYPE);
        temp.addEqualsFilter(URL, url);
        
        if (temp.next()) {
            DataObject obj = temp.getDataObject();
            temp.close();
            return (Template)DomainObjectFactory.newInstance(obj);
        }
        
        throw new DataObjectNotFoundException("cannot find template for url " + url);
    }

    public static Template retrieve(BigDecimal id)
        throws DataObjectNotFoundException {

        Session session = SessionManager.getSession();
        DataCollection temp = session.retrieve(BASE_DATA_OBJECT_TYPE);
        temp.addEqualsFilter(ID, id);
        
        if (temp.next()) {
            DataObject obj = temp.getDataObject();
            temp.close();
            return (Template)DomainObjectFactory.newInstance(obj);
        }
        
        throw new DataObjectNotFoundException("cannot find template for id " + id);
    }

    public static TemplateCollection retrieveAll() {
        Session session = SessionManager.getSession();
        DataCollection temp = session.retrieve(BASE_DATA_OBJECT_TYPE);
        return new TemplateCollection(temp);
    }



    /**
     * Retrieve templates across all contexts for a given Category.
     */
    public static DataCollection retrieveForCategory(Category category) {
        DataCollection templates = SessionManager.getSession().retrieve
            ( Template.BASE_DATA_OBJECT_TYPE );

        templates.addEqualsFilter( "templateMappings.category.id", category.getID() );

        return templates;
    }

    
    public static Template matchExact(Category category, String dispatcherContext, String useContext) {
        DataCollection templates = SessionManager.getSession().retrieve
            ( Template.BASE_DATA_OBJECT_TYPE );
        templates.addEqualsFilter( "templateMappings.category.id", category.getID() );
        templates.addEqualsFilter( "templateMappings.dispatcherContext", dispatcherContext );
        templates.addEqualsFilter( "templateMappings.useContext", useContext );
        
        if (templates.next()) {
            DataObject obj = templates.getDataObject();
            templates.close();

            return (Template)DomainObjectFactory.newInstance(obj);
        }

        return null;
    }
    

    public static Template matchBest(Category cat, String dispatcherContext, String useContext) {

        String path = (String)DomainServiceInterfaceExposer.get(cat, "defaultAncestors");

        DataCollection templates = SessionManager.getSession().retrieve
            ( Template.BASE_DATA_OBJECT_TYPE );
        templates.addPath( "templateMappings.category.defaultAncestors" );

        // Filter on ancestors, getting longest match first
        Filter f = templates.addFilter( ":path like templateMappings.category.defaultAncestors || '%'" );
        f.set( "path", path );
        templates.addOrder( "templateMappings.category.defaultAncestors desc" );

        templates.addEqualsFilter("templateMappings.dispatcherContext", dispatcherContext);
        templates.addEqualsFilter("templateMappings.useContext", useContext);

        templates.setRange( new Integer( 1 ), new Integer( 2 ) );
        
        if (templates.next()) {
            if (s_log.isDebugEnabled()) {
                s_log.debug("Got template matching " + templates.get("templateMappings.category.defaultAncestors"));
            }

            Template template = (Template)DomainObjectFactory.newInstance(templates.getDataObject());
            templates.close();

            return template;
        }

        return null;
    }

    public BigDecimal getID() {
        return (BigDecimal)get(ID);
    }

    public String getTitle() {
        return (String)get(TITLE);
    }

    public void setTitle(String title) {
        set(TITLE, title);
    }

    public String getDescription() {
        return (String)get(DESCRIPTION);
    }

    public void setDescription(String description) {
        set(DESCRIPTION, description);
    }

    public String getURL() {
        return (String)get(URL);
    }

    public void setURL(String url) {
        set(URL, url);
    }
}
