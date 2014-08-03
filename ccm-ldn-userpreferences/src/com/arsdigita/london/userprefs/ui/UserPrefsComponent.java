/*
This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public License
as published by the Free Software Foundation; either version 2.1 of
the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
*/

package com.arsdigita.london.userprefs.ui;

import com.arsdigita.london.userprefs.UserPrefs;

import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleComponent;
import com.arsdigita.bebop.event.RequestEvent;
import com.arsdigita.bebop.event.RequestListener;
import com.arsdigita.bebop.parameters.BooleanParameter;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.web.RedirectSignal;
import com.arsdigita.xml.Element;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;

public class UserPrefsComponent extends SimpleComponent {
    private static final Logger s_log =
        Logger.getLogger( UserPrefsComponent.class );

    public static final String XMLNS = "http://xmlns.redhat.com/userprefs/1.0";

    public UserPrefsComponent() {
        super();
    }

    private final StringParameter m_setKey = new StringParameter( "pref.set.key" );
    private final StringParameter m_setValue = new StringParameter( "pref.set.value" );
    private final StringParameter m_remove = new StringParameter( "pref.remove" );
    private final BooleanParameter m_immediate = new BooleanParameter( "pref.immediate" );

    @Override
    public void generateXML( PageState ps, Element parent ) {
        UserPrefs prefs = UserPrefs.retrieve( ps.getRequest(),
                                              ps.getResponse() );
        
        Element rootE = parent.newChildElement( "up:userPreferences", XMLNS );

        Iterator values = prefs.getAll();
        while( values.hasNext() ) {
            Map.Entry pref = (Map.Entry) values.next();

            String key = (String) pref.getKey();
            String value = (String) pref.getValue();

            Element prefE = rootE.newChildElement( "up:preference", XMLNS );
            prefE.addAttribute( "key", key );

            if( null != value ) {
                prefE.addAttribute( "value", value );
            }
        }
    }

    @Override
    public void register( Page p ) {
        super.register( p );

        p.addGlobalStateParam( m_setKey );
        p.addGlobalStateParam( m_setValue );
        p.addGlobalStateParam( m_remove );
        p.addGlobalStateParam( m_immediate );

        p.addRequestListener( new RequestListener() {
            @Override
            public void pageRequested( RequestEvent ev ) {
                PageState ps = ev.getPageState();

                String setKey = (String) ps.getValue( m_setKey );
                String setValue = (String) ps.getValue( m_setValue );
                String remove = (String) ps.getValue( m_remove );

                UserPrefs prefs = UserPrefs.retrieve( ps.getRequest(),
                                                      ps.getResponse() );

                boolean acted = false;

                if( null != setKey ) {
                    if( s_log.isDebugEnabled() ) {
                        s_log.debug( "Set in " +
                                     ps.getRequest().getRequestURI() + '?' +
                                     ps.getRequest().getQueryString() );
                    }

                    prefs.set( setKey, setValue,
                               ps.getRequest(), ps.getResponse() );
                    acted = true;
                }

                if( null != remove ) {
                    if( s_log.isDebugEnabled() ) {
                        s_log.debug( "Remove in " +
                                     ps.getRequest().getRequestURI() + '?' +
                                     ps.getRequest().getQueryString() );
                    }

                    prefs.remove( remove, ps.getRequest() );
                    acted = true;
                }

                if( acted ) {
                    ps.setValue( m_setKey, null );
                    ps.setValue( m_setValue, null );
                    ps.setValue( m_remove, null );

                    try {
                        throw new RedirectSignal( ps.stateAsURL(), true );
                    } catch( IOException ex ) {
                        throw new UncheckedWrapperException( ex );
                    }
                }
            }
        } );
    }
}
