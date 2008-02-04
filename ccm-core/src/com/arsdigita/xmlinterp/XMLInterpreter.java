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

import org.w3c.dom.Node;

/**
 * <p>The XMLInterpreter interface allows an arbitrary class to obtain tokens to
 * interpret at part of a simple language framework. There are three events:
 * shift, reduce, and push. The semantics of the interpreter are described in
 * the documentation for XMLWalker.</p>
 *
 * <p>No exceptions are thrown as part of the interface, and no consistent way
 * to get errors is provided. The implementor is expected to cache error
 * information locally and provide it with a getError method.</p>
 *
 * <p>To use this interface, create a DOM with Xerces, using the
 * DocumentBuilderFactory to construct a DocumentBuilder. Use parse to return
 * the new DOM.</p>
 *
 * <p>Instantiate an object that implements XMLInterpreter, then pass is to
 * XMLWalker.walk().  Use additional methods in the imterpreter class to pass
 * state and error information.</p>
 *
 * @author Bryan Che (bche@redhat.com)
 * @version $Revision: #8 $ $Date: 2004/08/16 $
 * @since CCM Core 5.2
 *
 */

public interface XMLInterpreter {
    void shift(String s, String v);

    void reduce(String s);

    void push(Node n);
}
