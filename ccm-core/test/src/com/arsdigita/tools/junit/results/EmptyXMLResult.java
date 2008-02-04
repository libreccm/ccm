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
package com.arsdigita.tools.junit.results;


/**
 *  EmptyXMLResult
 *
 *  @author <a href="mailto:jorris@redhat.com">Jon Orris</a>
 *  @version $Revision: #6 $ $Date Nov 6, 2002 $
 */
public class EmptyXMLResult extends XMLResult {
    public EmptyXMLResult(String filename) {
        super();

        String testName = filename.substring(filename.indexOf("-") + 1, filename.indexOf(".xml"));
        setAttribute("name", testName);
        setAttribute("tests", "0");
        setAttribute("failures", "0");
        setAttribute("errors", "0");
    }
}
