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

import com.arsdigita.london.util.Util;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.xml.Element;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * A widget which will output messages added to it in the current request
 */

public class ErrorMessage extends SimpleContainer {
    private RequestLocal m_messages = new RequestLocal() {
        protected Object initialValue( PageState ps ) {
            return new LinkedList();
        }
    };

    public ErrorMessage() {
        super( "util:errorMessage", Util.XML_NS );
    }

    public void addMessage( PageState ps, String msg ) {
        LinkedList messages = getMessages( ps );
        messages.add( msg );
    }

    private LinkedList getMessages( PageState ps ) {
        return (LinkedList) m_messages.get( ps );
    }

    public void generateChildrenXML( PageState ps, Element parent ) {
        Iterator messages = getMessages( ps ).iterator();
        while( messages.hasNext() ) {
            String msg = (String) messages.next();

            Element msgXML = parent.newChildElement( "util:message", Util.XML_NS );
            msgXML.setText( msg );
        }
    }
}
