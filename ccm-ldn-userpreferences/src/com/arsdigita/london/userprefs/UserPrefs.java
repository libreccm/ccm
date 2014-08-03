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

package com.arsdigita.london.userprefs;

import com.arsdigita.caching.CacheTable;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainCollection;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.User;
import com.arsdigita.persistence.OID;
import com.arsdigita.util.UncheckedWrapperException;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

/**
 * <p>User Preferences. An object for storing user preferences in the current
 * session.</p>
 *
 * <p>This object is an abstraction of PersistentUserPreferences, which is a
 * <code>DomainObject</code> stored in the database. <code>UserPrefs</code> is
 * cached in the java Session and will not touch the database on every
 * request.</p>
 *
 * <p>If the current user is not logged in, <code>UserPrefs</code> will use its
 * own cookie. This means that preferences can be saved even when a user doesn't
 * log in.</p>
 *
 * @author Matthew Booth <mbooth@redhat.com>
 */

public class UserPrefs {
    private static final Logger s_log = Logger.getLogger( UserPrefs.class );

    private static final String COOKIE = "WAF_USER_PREFS";
    static final String SESSION_ATTRIBUTE = UserPrefs.class.getName();

    private OID m_user = null;
    private OID m_persistentPrefs = null;
    private Long m_cookie = null;

    private final HashMap m_prefs = new HashMap();

    private static final CacheTable s_prefsCache =
        new CacheTable( "user_preferences" );

    private UserPrefs() {};

    private UserPrefs( PersistentUserPrefs persistentPrefs ) {
        init( persistentPrefs );
    }

    private void init( PersistentUserPrefs persistentPrefs ) {
        m_persistentPrefs = persistentPrefs.getOID();
        m_cookie = persistentPrefs.getCookie();
        m_user = persistentPrefs.getUser() == null ?
            null : persistentPrefs.getUser().getOID();

        DomainCollection prefs = persistentPrefs.getAllValues();
        while( prefs.next() ) {
            Pref pref = (Pref) prefs.getDomainObject();
            m_prefs.put( pref.getKey(), pref.getValue() );
        }
    }

    /**
     * <p>Retrieve a UserPrefs object for the current request.</p>
     *
     * <p>In order, this will:
     * <ul>
     *   <li>Look for prefs in the http session</li>
     *   <li>Look for prefs for the currently logged in user</li>
     *   <li>Look for prefs for a supplied cookie (from DB)</li>
     *   <li>Create a new preferences object</li>
     * </ul>
     * </p>
     * 
     * @param req
     * @param res
     * @return 
     */
    public static UserPrefs retrieve( HttpServletRequest req,
                                      HttpServletResponse res ) {
        HttpSession session = req.getSession();

        // Yes, this seems like a silly use of an HttpSession, but they really
        // are broken in almost every useful way.
        UserPrefs prefs = (UserPrefs) s_prefsCache.get( session.getId() );

        if( null != prefs ) {
            if( s_log.isDebugEnabled() ) {
                s_log.debug( "Retrieved user prefs for session " +
                             session.getId() );
            }
            return prefs;
        }

        PersistentUserPrefs persistentPrefs = retrievePersistent( req, res );
        if( null != persistentPrefs ) {
            prefs = new UserPrefs( persistentPrefs );
        } else {
            prefs = new UserPrefs();
            
            User user = (User) Kernel.getContext().getParty();
            if( null != user ) {
                prefs.m_user = user.getOID();
            } else {
                Long cookie;
                try {
                    SecureRandom random = SecureRandom.getInstance( "SHA1PRNG" );
                    cookie = new Long( random.nextLong() );
                } catch( NoSuchAlgorithmException ex ) {
                    s_log.warn( "Unable to get SecureRandom for SHA1PRNG. " +
                                "Falling back to insecure random generator." );
                    cookie = new Long( new Random().nextLong() );
                }

                prefs.m_cookie = cookie;
                setCookie( res, cookie.toString() );
            }

            s_log.debug( "Created new prefs" );
        }

        s_prefsCache.put( session.getId(), prefs );
        return prefs;
    }

    private static PersistentUserPrefs retrievePersistent
        ( HttpServletRequest req, HttpServletResponse res )
    {
        PersistentUserPrefs persistentPrefs = null;

        User user = (User) Kernel.getContext().getParty();
        if( null != user ) {
            persistentPrefs = PersistentUserPrefs.retrieveForUser( user );
        }

        if( null != persistentPrefs ) {
            s_log.debug( "Got prefs for user" );
            return persistentPrefs;
        }

        Long cookie = getCookie( req );
        if( null != cookie ) {
            persistentPrefs = PersistentUserPrefs.retrieveForCookie( cookie );

            // Remove a bogus cookie
            if( null == persistentPrefs ) {
                removeCookie( res );
            }
        }

        if( null != persistentPrefs ) {
            s_log.debug( "Got prefs for cookie" );
            return persistentPrefs;
        }

        s_log.debug( "No existing prefs" );
        return null;
    }

    /**
     * Retrieve the value of a user preference entry.
     *
     * @param key The identifier of the preference to be retrieved
     * @return The value of the requsted preference, or null if it is not set
     */
    public String get( String key ) {
        return (String) m_prefs.get( key );
    }

    /**
     * Retrieve all stored user preferences.
     *
     * @return An Iterator of Map.Entry objects containing key/value pairs for
     *         the user's current preferences
     */
    public Iterator getAll() {
        return m_prefs.entrySet().iterator();
    }

    /**
     * Set a preference.
     *
     * @param key The identifier of the preference to be stored
     * @param value The value of the preference to be stored
     * @param req
     * @param res
     */
    public void set( String key, String value,
                     HttpServletRequest req, HttpServletResponse res ) {
        m_prefs.put( key, value );

        PersistentUserPrefs prefs = getPersistent();
        if( null == prefs ) prefs = createPersistent( req, res );
        prefs.setValue( key, value );
    }

    /**
     * Remove a user preference.
     *
     * @param key The identifier of the preference to be removed
     * @param req
     */
    public void remove( String key, HttpServletRequest req ) {
        m_prefs.remove( key );

        PersistentUserPrefs persistent = getPersistent();

        if( !m_prefs.isEmpty() ) {
            if( null != persistent ) persistent.removeValue( key );
        } else {
            if( null != persistent ) persistent.delete();

            req.getSession().setAttribute( SESSION_ATTRIBUTE, null );
            m_persistentPrefs = null;
        }
    }

    /**
     * Save user preferences to the database
     * @param req
     * @param res
     */
    public void persist( HttpServletRequest req,
                         HttpServletResponse res ) {
        s_log.info( "Persisting session" );

        PersistentUserPrefs prefs = getPersistent();
        if( null == prefs ) prefs = createPersistent( req, res );

        prefs.setAllValues( m_prefs );
        prefs.save();

        s_log.debug( "Session persisted" );
    }

    /**
     * Get a PersistentUserPrefs object. Create one if necessary.
     */
    private PersistentUserPrefs getPersistent() {
        PersistentUserPrefs prefs = null;

        if( null != m_persistentPrefs ) {
            try {
                prefs = new PersistentUserPrefs( m_persistentPrefs );
            } catch( DataObjectNotFoundException ex ) {
                s_log.warn( "User preferences object contained bogus " +
                            "persistent preferences OID" );
            }
        }

        // If we're saving something and we have a user object now, use that
        // in preference
        if( null == m_user ) {
            User user = (User) Kernel.getContext().getParty();
            if( null != user ) {
                prefs.setUser( user );
                prefs.setCookie( null );
            }
        }

        return prefs;
    }

    private PersistentUserPrefs createPersistent( HttpServletRequest req,
                                                  HttpServletResponse res ) {
        PersistentUserPrefs prefs = retrievePersistent( req, res );
        if( null == prefs ) {
            prefs = new PersistentUserPrefs();

            if( s_log.isDebugEnabled() ) {
                s_log.debug( "Initializing new user preferences: " +
                             prefs.getOID() );
            }

            if( null != m_user ) {
                prefs.setUser( new User( m_user ) );
            }
            
            else if( null != m_cookie ) {
                prefs.setCookie( m_cookie );
            }
            
            else {
                throw new UncheckedWrapperException ( "User preferences object doesn't contain either a user or a cookie object" );
            }

            m_persistentPrefs = prefs.getOID();
        } else {
            if( s_log.isDebugEnabled() ) {
                s_log.debug( "Reusing existing persistent preferences " +
                             prefs.getOID() );
            }

            init( prefs );
        }

        return prefs;
    }

    private static Long getCookie( HttpServletRequest req ) {
        Cookie[] cookieJar = req.getCookies();
        if( null == cookieJar ) return null;

        for( int i = 0; i < cookieJar.length; i++ ) {
            if( COOKIE.equals( cookieJar[i].getName() ) ) {
                try {
                    return Long.valueOf( cookieJar[i].getValue() );
                } catch( NumberFormatException ex ) {
                    s_log.warn( "Bogus cookie value: " +
                                cookieJar[i].getValue() );
                    // Might as well keep looking
                }
            }
        }

        return null;
    }

    private static void setCookie( HttpServletResponse res, String value ) {
        Cookie cookie = new Cookie( COOKIE, value );
        cookie.setMaxAge( Integer.MAX_VALUE );
        res.addCookie( cookie );
    }

    private static void removeCookie( HttpServletResponse res ) {
        Cookie cookie = new Cookie( COOKIE, "" );
        cookie.setMaxAge( 0 );
        res.addCookie( cookie );
    }
}
