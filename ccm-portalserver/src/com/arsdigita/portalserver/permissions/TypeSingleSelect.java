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
package com.arsdigita.portalserver.permissions;

import java.util.*;
import com.arsdigita.bebop.parameters.*;
import com.arsdigita.bebop.form.*;
import com.arsdigita.bebop.*;
import com.arsdigita.bebop.event.*;
import com.arsdigita.xml.Element;
import com.arsdigita.persistence.metadata.ObjectType;

class TypeSingleSelect extends SingleSelect {

    private RequestLocal m_typesRL;

    public TypeSingleSelect(ParameterModel param,
                            RequestLocal typesRL) {
        super(param);

        m_typesRL = typesRL;

        addOption(new Option(AddGrantForm.ALL_TYPES,
                             "All Contained Items"));
        setPrintListener(new PrintListener() {
            public void prepare(PrintEvent ev) {
                SingleSelect tgt = (SingleSelect) ev.getTarget();
                tgt.clearOptions();
                PageState ps = ev.getPageState();
                Iterator types
                         = ((Collection) m_typesRL.get(ps)).iterator();
                while (types.hasNext()) {
                    ObjectType type = (ObjectType) types.next();
                    tgt.addOption(new Option(type.getQualifiedName(),
                                             type.getName()));
                }
            }
        });
    }

    public void generateXML(PageState ps, Element parent) {
        Collection types = (Collection) m_typesRL.get(ps);
        if (types == null) {
            parent.newChildElement("bebop:label", BEBOP_XML_NS);
            return;
        } else {
            super.generateXML(ps, parent);
        }
    }
}
