/*
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
 * Revision:
 * Method : generate()
 * Description : Method added to class Placeholders$UniqueIDGenerator to generate a sequence unique id.
 *
 * Method : setFormVars()
 * Description : Data related submit button not appended to email body.
 *				 Checkbox value appended to the email body.
 */

package com.arsdigita.formbuilder.util;

import com.arsdigita.formbuilder.CompoundComponent;
import com.arsdigita.formbuilder.PersistentComponent;
import com.arsdigita.formbuilder.PersistentFormSection;
import com.arsdigita.formbuilder.PersistentWidget;

import com.arsdigita.bebop.FormData;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.PersonName;
import com.arsdigita.kernel.User;
import com.arsdigita.persistence.DataAssociationCursor;
import com.arsdigita.util.StringUtils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;

public class Placeholders {
    private static final Logger s_log = Logger.getLogger( Placeholders.class );

    private HashMap m_vars;

    public Placeholders() {
        User user = (User)Kernel.getContext().getParty();

        m_vars = new HashMap();
        if (user != null) {
            setUserVars(user);
        }
    }

    public Placeholders(PersistentFormSection form, FormData data) {
        this();

        DataAssociationCursor components = form.getComponents();
        while( components.next() ) {
            PersistentComponent c = (PersistentComponent)
                DomainObjectFactory.newInstance( components.getDataObject() );

            handleComponent(data, c);
        }
    }

    private void handleComponent(FormData data, PersistentComponent c) {
        if( c instanceof PersistentWidget ) {
            PersistentWidget w = (PersistentWidget) c;

            String paramName = w.getParameterName();
            String varName;
            if( paramName.startsWith( "::user." ) && paramName.endsWith( "::" ) ) {
                varName = paramName.substring( 2, paramName.length() - 2 );
            } else {
                varName = "form." + paramName;
            }

            Object value = w.getValue(data);
            String valueStr = null;
            if( null != value ) {
                if( value.getClass().isArray() ) {
                    Object[] values = (Object[]) value;

                    StringBuffer buf = new StringBuffer();
                    for( int i = 0; i < values.length; i++ ) {
                        buf.append( values[i].toString() );
                        if( values.length - 1 != i )
                            buf.append( ", " );
                    }

                    valueStr = buf.toString();
                } else {
                    valueStr = value.toString();
                }
            }

            m_vars.put(varName, (valueStr == null ? "(null)" : valueStr));
        } else if (c instanceof CompoundComponent) {
            Iterator i = ((CompoundComponent) c).getComponentsIter();
            while (i.hasNext()) {
                handleComponent(data, (PersistentComponent) i.next());
            }
        } else if (s_log.isDebugEnabled()) {
            s_log.debug("Ignoring component: " + c.getClass().getName());
        }
    }

    public String interpolate(String text) {
        if( s_log.isDebugEnabled() ) {
            StringBuffer buf = new StringBuffer();

            buf.append( "Interpolating: " ).append( text ).append( '\n' );

            Iterator vars = m_vars.entrySet().iterator();
            while( vars.hasNext() ) {
                Map.Entry var = (Map.Entry) vars.next();

                buf.append( var.getKey() ).append( ": " );
                buf.append( var.getValue() ).append( '\n' );
            }

            s_log.debug( buf.toString() );
        }
        return StringUtils.interpolate(text, m_vars);
    }

    public void setVariable(String key,
                            String value) {
        m_vars.put(key, value);
    }

    protected void setUserVars(User user) {
        PersonName name = user.getPersonName();

        m_vars.put("user.givenname", name.getGivenName());
        m_vars.put("user.familyname", name.getFamilyName());
        m_vars.put("user.screenname", user.getScreenName());
        m_vars.put("user.email", user.getPrimaryEmail().getEmailAddress());
    }
}
