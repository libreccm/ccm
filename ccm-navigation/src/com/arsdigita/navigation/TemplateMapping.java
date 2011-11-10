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

package com.arsdigita.navigation;

import com.arsdigita.categorization.Category;
import com.arsdigita.db.Sequences;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.util.UncheckedWrapperException;

import java.math.BigDecimal;
import java.sql.SQLException;

public class TemplateMapping extends DomainObject {
    public static final String BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.navigation.TemplateMapping";

    public static final String ID = "id";
    public static final String TEMPLATE = "template";
    public static final String CATEGORY = "category";
    public static final String DISPATCHER_CONTEXT = "dispatcherContext";
    public static final String USE_CONTEXT = "useContext";

    public TemplateMapping( Template template,
                            Category category,
                            String dispatcherContext,
                            String useContext ) {
        super( BASE_DATA_OBJECT_TYPE );

        setAssociation( TEMPLATE, template );
        setAssociation( CATEGORY, category );
        set( DISPATCHER_CONTEXT, dispatcherContext );
        set( USE_CONTEXT, useContext );
    }

    public TemplateMapping( DataObject obj ) {
        super( obj );
    }

    public static TemplateMapping retrieve( BigDecimal id ) {
        DataCollection templates = SessionManager.getSession().retrieve
            ( BASE_DATA_OBJECT_TYPE );
        templates.addEqualsFilter( ID, id );

        if( templates.next() ) {
            DataObject obj = templates.getDataObject();
            templates.close();

            return new TemplateMapping( obj );
        }

        return null;
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

    public static TemplateMapping retrieve( Category category, String dispatcherContext,
                                            String useContext ) {
        DataCollection mappings = SessionManager.getSession().retrieve( BASE_DATA_OBJECT_TYPE );
        mappings.addEqualsFilter( "category.id", category.getID() );
        mappings.addEqualsFilter( DISPATCHER_CONTEXT, dispatcherContext );
        mappings.addEqualsFilter( USE_CONTEXT, useContext );

        if( mappings.next() ) {
            DataObject obj = mappings.getDataObject();
            mappings.close();

            return (TemplateMapping) DomainObjectFactory.newInstance( obj );
        }

        return null;
    }

    public void setTemplate( Template template ) {
        setAssociation( TEMPLATE, template );
    }

    public void setUseContext( String useContext ) {
        set( USE_CONTEXT, useContext );
    }
}
