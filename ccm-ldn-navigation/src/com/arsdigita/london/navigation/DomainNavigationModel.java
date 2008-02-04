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
import com.arsdigita.kernel.ACSObject;

import com.arsdigita.london.terms.Domain;

import org.apache.log4j.Logger;

/**
 * The Domain navigation model returns the
 * root category of a specified Domain
 */
public class DomainNavigationModel extends AbstractNavigationModel {
    private static final Logger s_log =
        Logger.getLogger( DomainNavigationModel.class );

    private String m_domainKey;
    private NavigationModel m_model = null;

    public DomainNavigationModel( String domainKey ) {
        m_domainKey = domainKey;
    }

    public DomainNavigationModel( String domainKey, NavigationModel model ) {
        m_domainKey = domainKey;
        m_model = model;
    }

    private Category getDomainRoot() {
        Domain domain = Domain.retrieve( m_domainKey );
        Category root = domain.getModel();

        if( s_log.isDebugEnabled() ) {
            s_log.debug( "Got root category " + root.getOID().toString() +
                         " for domain " + m_domainKey );
        }

        return root;
    }

    protected ACSObject loadObject() {
        return ( null == m_model ) ? null : m_model.getObject();
    }
    
    protected Category loadCategory() {
        return ( null == m_model ) ? null : m_model.getCategory();
    }
    
    protected Category[] loadCategoryPath() {
        Category[] path = null;
        if( null != m_model ) {
            Category[] modelPath = m_model.getCategoryPath();

            Category root = getRootCategory();

            for( int i = 0; i < path.length; i++ ) {
                if( modelPath[i].equals( root ) ) {
                    path = modelPath;
                    break;
                }
            }
        }

        if( null == path ) return new Category[]{ getRootCategory() };
        return path;
    }
    
    protected Category loadRootCategory() {
        return getDomainRoot();
    }
}
