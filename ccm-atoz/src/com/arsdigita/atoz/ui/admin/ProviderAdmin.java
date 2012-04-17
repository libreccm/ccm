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

package com.arsdigita.atoz.ui.admin;

import com.arsdigita.atoz.AtoZ;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.ActionLink;
import com.arsdigita.kernel.ui.ACSObjectSelectionModel;
import com.arsdigita.xml.Element;


public class ProviderAdmin extends SimpleContainer {
    
    private ACSObjectSelectionModel m_provider;
    private String m_name;
    private ActionLink m_return;

    public ProviderAdmin(String name,
                         ACSObjectSelectionModel provider) {
        m_name = name;
        m_provider = provider;

        m_return = new ActionLink("Return to provider list");
        m_return.setIdAttr("return");
        m_return.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    fireCompletionEvent(e.getPageState());
                }
            });
        add(m_return);
    }

    public void generateXML(PageState state,
                            Element parent) {
        Element content = AtoZ.newElement(m_name);
        exportAttributes(content);
   
        generateChildrenXML(state, content);
        
        parent.addContent(content);
    }
}
