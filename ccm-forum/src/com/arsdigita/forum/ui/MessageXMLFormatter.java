/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.forum.ui;

import com.arsdigita.domain.SimpleDomainObjectXMLFormatter;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.messaging.Message;
import com.arsdigita.persistence.metadata.Property;


public class MessageXMLFormatter 
    extends SimpleDomainObjectXMLFormatter {
    
    public Object format(DomainObject obj,
                         String path,
                         Property prop,
                         Object value) {
        if (obj instanceof Message &&
            prop.getName().equals(Message.BODY)) {
            Message msg = (Message)obj;
            return msg.getBodyAsHTML();
        }
        return super.format(obj, path, prop, value);
    }
    
    public boolean isEmpty() {
    	return false;
    }

}
