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
 *
 */
package com.arsdigita.xmlinterp;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *  <p>This class walks through a DOM created by parsing an XML file, passing
 *  events to an XMLInterpreter.</p>
 *
 *  <p>There are three events: shift, push, and reduce. A <b>shift</b> is
 *  executed when a node contains a single #text child, and no attributes. This
 *  node is not pushed or reduced. A <b>push</b> is executed when a node has
 *  more than one child. The effect of a push is to put a part of the
 *  interpreter in scope, and may cause a dispatch to another XMLInterpreter. A
 *  <b>reduce</b> is executed at the end of a pushed node.</p>
 *
 * @author Bryan Che (bche@redhat.com)
 * @version $Revision: #9 $ $Date: 2004/08/16 $
 * @since CCM Core 5.2
 *
 */

public class XMLWalker {
    public final static String versionId = "$Id: XMLWalker.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    private final String TM = "#";
    private boolean trace = false;

    private XMLInterpreter xi;

    private void shift (String s1, String s2) {
        if (trace) {
            System.out.println("Tracing: shift " + s1 + " " + s2);
        }
        xi.shift(s1, s2);
        return;
    }

    private void push (Node n) {
        if (trace) {
            System.out.println("Tracing: push " + n.getNodeName());
        }
        xi.push(n);
        return;
    }

    private void reduce (String s) {
        if (trace) {
            System.out.println("Tracing: reduce " + s);
        }
        xi.reduce(s);
        return;
    }

    // FIXME: the nn parameter is passed in but never used. -- 2002-11-26
    private void shiftAttributes (NamedNodeMap nm, String nn) {
        int il = nm.getLength();
        /* loop through all attributes here */
        /* shift attribute name, and value */
        for (int i = 0; i < il; i++) {
            Node n = nm.item(i);
            try {
                shift(n.getNodeName(), n.getNodeValue());
            } catch (DOMException e) {
                System.out.println("Exception in attribute parser " + e.toString());
            }
        }
        return;
    }

    // if a node has more than one sibling, or has attributes, push and recurse
    // if a node is empty, push and reduce , but do not recurse. degenerate case of 1.

    // in a pure shift situation, do not push and reduce

    // this is pretty complicated.

    private void step (Node n) {
        NodeList nl = n.getChildNodes();
        String nn = n.getNodeName();
        int il = nl.getLength();
        boolean pureshift = ((1 == il) && (! n.hasAttributes()));

        // if this is not a node we can push, shift and return, no reduce
        if (n.hasAttributes()) {
            push(n);
            shiftAttributes(n.getAttributes(), nn);
        }

        // handle pure shift condition
        // because the child item is a #text, there is no duplicate node handling,
        // see loop below.

        if (pureshift) {
            Node fc = nl.item(0);
            String v;
            try {
                v = fc.getNodeValue();
            }
            catch (DOMException e) {
                System.out.println("Caught exception; returning");
                return;
            }
            shift(nn, v);
            if (n.hasAttributes()) {
                reduce(nn);
            }
            return;
        }

        // we pushed earlier if n has attributes
        if (! n.hasAttributes()) {
            push(n);
        }

        // now loop through the children, calling step on each, ignoring whitespace
        int i = 0;
        while (i < il) {
            Node pn = nl.item(i);
            if (!pn.getNodeName().startsWith(TM)) {
                step(pn);
            }
            i++;
        }

        reduce(nn);
        return;
    }

    public void setTracing() {
        trace = true;
        return;
    }

    public void clearTracing() {
        trace = false;
        return;
    }

    public void walk (Document d, XMLInterpreter x) {
        Element elem = d.getDocumentElement();

        xi = x;

        step(elem);
        return;
    }
}
