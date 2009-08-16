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

package com.arsdigita.london.terms.ui.admin;


import java.util.Set;

import org.apache.log4j.Logger;

import com.arsdigita.bebop.PageState;
import com.arsdigita.categorization.Category;
import com.arsdigita.domain.DeleteException;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.london.terms.Domain;
import com.arsdigita.london.terms.Terms;
import com.arsdigita.london.terms.indexing.Indexer;
import com.arsdigita.london.util.ui.AbstractDomainObjectDetails;
import com.arsdigita.london.util.ui.ErrorMessage;
import com.arsdigita.london.util.ui.event.DomainObjectActionAbortedException;
import com.arsdigita.london.util.ui.event.DomainObjectActionEvent;
import com.arsdigita.london.util.ui.event.DomainObjectActionListener;
import com.arsdigita.london.util.ui.parameters.DomainObjectParameter;
import com.arsdigita.xml.Element;

public class DomainDetails extends AbstractDomainObjectDetails {
    private static final Logger s_log =
        Logger.getLogger( DomainDetails.class );

    private DomainObjectParameter m_domain;
    private ErrorMessage m_errorMessage = new ErrorMessage();

    public static final String ACTION_EDIT = "edit";
    public static final String ACTION_DELETE = "delete";
    public static final String ACTION_TRAIN = "train";
    public static final String ACTION_UNTRAIN = "untrain";

    public DomainDetails(DomainObjectParameter domain) {
        super("domainDetails", 
              Terms.XML_PREFIX, 
              Terms.XML_NS);
        setRedirecting(true);

        m_domain = domain;

        registerDomainObjectAction(ACTION_EDIT);
        registerDomainObjectAction(ACTION_DELETE);
        registerDomainObjectAction(ACTION_TRAIN);
        registerDomainObjectAction(ACTION_UNTRAIN);

        addDomainObjectActionListener(
            ACTION_DELETE,
            new DomainObjectActionListener() {
                public void actionPerformed(DomainObjectActionEvent e) {
                    PageState ps = e.getPageState();

                    DomainObject dobj = e.getObject();

                    if( s_log.isDebugEnabled() ) {
                        s_log.debug( "Deleting " + dobj.getOID() );
                    }

                    try {
                        dobj.delete();
                    } catch( DeleteException ex ) {
                        Set properties = ex.getDependencyProperties();

                        if( properties.contains( Category.OWNER_USE_CONTEXT ) ) {
                            m_errorMessage.addMessage( ps, "Unable to delete this domain because it still contains one or more mappings below. You can manually remove these by clicking on the 'remove' links under 'Domain Usage'." );
                            properties.remove( Category.OWNER_USE_CONTEXT );

                            s_log.debug( "Use context mapping would cause failure" );
                        }

                        // A catchall. If a specific message isn't caught above,
                        // dump the raw error message. It's reasonably useful.
                        if( !properties.isEmpty() ) {
                            m_errorMessage.addMessage( ps, ex.getMessage() );

                            s_log.debug( "Other error would cause failure" );
                        }

                        throw new DomainObjectActionAbortedException
                            ( "Error deleting object" + dobj.getOID() );
                    }

                    ps.setValue(m_domain, null);
                    s_log.debug( "Delete succeeded" );
                }
            });
        
        addDomainObjectActionListener(ACTION_TRAIN, new DomainObjectActionListener() {
            public void actionPerformed(DomainObjectActionEvent e) {
                Domain domain = (Domain) e.getObject();
                Indexer indexer = Indexer.retrieve(domain);
                if (indexer == null) {
                    indexer = Indexer.create(domain);
                }
                indexer.train();
            }
        });
        
        addDomainObjectActionListener(ACTION_UNTRAIN, new DomainObjectActionListener() {
            public void actionPerformed(DomainObjectActionEvent e) {
                Domain domain = (Domain) e.getObject();
                Indexer indexer = Indexer.retrieve(domain);
                if (indexer != null) {
                    indexer.delete();
                }
            }
        });
    }
    
    protected DomainObject getDomainObject(PageState state) {
        return (DomainObject)state.getValue(m_domain);
    }

    public void generateActionXML( PageState ps, Element parent,
                                   DomainObject dobj ) {
        s_log.debug( "In generateActionXML" );
        m_errorMessage.generateXML( ps, parent );
        super.generateActionXML( ps, parent, dobj );
    }
}
