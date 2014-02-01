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
package com.arsdigita.bebop;

/**
 *  An implementation of ComponentXMLComparator for regression testing
 *  compound Components. The XML name is provided by the user, and should
 *  be a good description of the compound document.
 *
 * @version $Revision: #8 $ $Date: 2004/08/16 $
 */
public class CompoundComponentXMLComparator extends ComponentXMLComparator
{

    /**
     *  Constucts a comparator for a Component.
     *
     *  @param c The component to perform XML comparison on.
     *  @param xmlFileName Some descriptive name for the xml file.
     *
     */
    public CompoundComponentXMLComparator(Component c, String testName)
    {
        super(c, testName, c.getClass().getName() + ".xml");
    }

    /**
     *  Constucts a comparator for a Component.
     *
     *  @param c The component to perform XML comparison on.
     *  @param xmlFileName Some descriptive name for the xml file.
     *
     */
    public CompoundComponentXMLComparator(Page p, String testName) throws Exception
    {
        super(p, testName, p.getClass().getName() + ".xml");
    }


}
