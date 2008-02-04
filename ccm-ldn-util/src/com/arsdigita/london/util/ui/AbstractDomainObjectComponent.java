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


package com.arsdigita.london.util.ui;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.event.EventListenerList;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
import com.arsdigita.london.util.ui.event.DomainObjectActionAbortedException;
import com.arsdigita.london.util.ui.event.DomainObjectActionEvent;
import com.arsdigita.london.util.ui.event.DomainObjectActionListener;
import com.arsdigita.persistence.OID;
import com.arsdigita.util.Assert;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.web.RedirectSignal;

public abstract class AbstractDomainObjectComponent extends SimpleContainer {

    private static final Logger s_log =
        Logger.getLogger( AbstractDomainObjectComponent.class );
    
    private Map m_actions = new HashMap();
	private Map m_actionPrivileges = new HashMap();
    private boolean m_redirecting;

    public AbstractDomainObjectComponent(String cname,
                                         String xmlns) {
        super(cname, xmlns);
    }
    
    public void setRedirecting(boolean redirecting) {
        m_redirecting = redirecting;
    }

    public boolean isRedirecting() {
        return m_redirecting;
    }

    public void respond(PageState state) {
        Assert.locked(this);

        String name = state.getControlEventName();
        String value = state.getControlEventValue();
        state.clearControlEvent();

        if (!m_actions.containsKey(name)) {
            throw new RuntimeException("Action " + name + " not registered");
        }

        OID oid = OID.valueOf(value);
        DomainObject dobj = DomainObjectFactory.newInstance(oid);
        
        boolean aborted = false;
        try {
            fireDomainObjectActionEvent(
                new DomainObjectActionEvent(this, state, dobj, name));
        } catch( DomainObjectActionAbortedException ex ) {
            aborted = true;
            if( s_log.isInfoEnabled() ) {
                if( s_log.isDebugEnabled() ) {
                    s_log.debug( "Action aborted", ex );
                } else {
                    s_log.info( "Action aborted" );
                }
            }
        }

        if (!aborted && m_redirecting) {
            state.clearControlEvent();

            throw new RedirectSignal(state.toURL(), true);
        }
    }
    
    protected void fireDomainObjectActionEvent(DomainObjectActionEvent ev) {
        Assert.locked(this);
        
        EventListenerList listeners = 
            (EventListenerList)m_actions.get(ev.getAction());

        Iterator i = listeners
            .getListenerIterator(DomainObjectActionListener.class);
        while (i.hasNext()) {
            ((DomainObjectActionListener)i.next()).actionPerformed(ev);
        }
    }

    protected void registerDomainObjectAction(String action) {
        Assert.unlocked(this);

        if (m_actions.containsKey(action)) {
            throw new RuntimeException("Action " + action + " already registered");
        }
        m_actions.put(action,
                      new EventListenerList());
    }

	protected void registerDomainObjectAction(String action, PrivilegeDescriptor privilege) {
		registerDomainObjectAction(action);
		m_actionPrivileges.put(action, privilege);
	}


    protected Iterator getDomainObjectActions() {
        return m_actions.keySet().iterator();
    }
    
	/**
	 * return privilegeDescripter registered against this action, or null if action was registered without
	 * a privilege being specified
	 * @param action
	 * @return
	 */
	protected PrivilegeDescriptor getDomainObjectActionPrivilege (String action) {
		return (PrivilegeDescriptor)m_actionPrivileges.get(action);
	}
    public void addDomainObjectActionListener(String action,
                                              DomainObjectActionListener listener) {
        Assert.unlocked(this);

        if (!m_actions.containsKey(action)) {
            throw new RuntimeException("Action " + action + " not registered");
        }
        
        EventListenerList listeners = (EventListenerList)m_actions.get(action);
        listeners.add(DomainObjectActionListener.class, listener);
    }
    
    protected String getDomainObjectActionLink(PageState state,
                                               DomainObject dobj,
                                               String action) {
        Assert.locked(this);

        String url = null;
        state.setControlEvent(this, action, dobj.getOID().toString());
        try {
            url = state.stateAsURL();
        } catch (IOException ex) {
            throw new UncheckedWrapperException("cannot get state url", ex);
        }
        state.clearControlEvent();
        return url;
    }
}
