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
package com.arsdigita.xmlutil;

import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.output.XMLOutputter;

public class XMLException extends JDOMException {
    public XMLException() {
        super();
    }

    public XMLException(String s) {
        super(s);
    }

    public XMLException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public XMLException(String s, Element e) {
        super(makeMessage(s, e));
    }

    public XMLException(String s, Throwable throwable, Element e) {
        super(makeMessage(s,e), throwable);
    }

    private static String makeMessage(String msg, Element e) {
        XMLOutputter out = new XMLOutputter();
        final String elementText = out.outputString(e);
        final String newMessage  = msg + System.getProperty("line.separator") + elementText;
        return newMessage;
    }

}
