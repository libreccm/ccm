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

import com.arsdigita.xml.Element;
import java.util.Iterator;

/**
 * Implementation of the Component interface. Used for testing.
 *
 * @version $Revision: #8 $ $Date: 2004/08/16 $
 */

public class ComponentImpl extends LockableImpl implements Component {

    public static final String versionId = "$Id: ComponentImpl.java 742 2005-09-02 10:29:31Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    public void generateXML(PageState state, Element parent) {}

    public void respond(PageState state)
        throws javax.servlet.ServletException {}

    public Iterator children() { return null; }

    public void register(Page p) {}

    public void register(Form f, FormModel m) {}

    public String getClassAttr() { return null; }

    public void setClassAttr(String theClass) {}

    public String getStyleAttr() { return null; }

    public void setStyleAttr(String style) {}

    public String getIdAttr() { return null; }

    public void setIdAttr(String id) {}

    public Component setKey(String key) { return null; }

    public String getKey() { return null; }

    public boolean isVisible(PageState state) { return true; }

    public void setVisible(PageState state, boolean v) {}

}
