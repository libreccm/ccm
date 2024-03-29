/*
 * Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.bebop.parameters;

import com.arsdigita.bebop.event.ParameterEvent;
import com.arsdigita.bebop.event.ParameterListener;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.parameters.ParameterData;

import org.apache.oro.text.perl.Perl5Util;

import org.apache.log4j.Logger;

public class WordValidationListener implements ParameterListener {
    private static final Logger s_log =
        Logger.getLogger( WordValidationListener.class );

    public void validate(ParameterEvent e)
        throws FormProcessException {

        ParameterData d = e.getParameterData();
        String value = (String)d.getValue();

        if( null == value ) return;

        if( s_log.isDebugEnabled() ) {
            s_log.debug( "Name: " + d.getName() + ", Value: " + value );
        }

        Perl5Util re = new Perl5Util();
        if (!re.match("/^\\s*\\w*\\s*$/", value)) {
            d.invalidate();
            d.addError("Please enter a single word");
        }
    }
}
