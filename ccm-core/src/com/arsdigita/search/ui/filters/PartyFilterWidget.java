/*
 * Copyright (C) 2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.search.ui.filters;

import com.arsdigita.search.ui.FilterWidget;
import com.arsdigita.search.Search;
import com.arsdigita.search.FilterSpecification;
import com.arsdigita.search.FilterType;
import com.arsdigita.kernel.Party;
import com.arsdigita.kernel.PartyCollection;
import com.arsdigita.xml.Element;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.bebop.PageState;
import com.arsdigita.search.filters.PartyFilterSpecification;
import com.arsdigita.util.StringUtils;


/**
 *  This is a base widget that can be used to restrict a search
 *  to a certain group of users.
 */
public class PartyFilterWidget extends FilterWidget {

    public final static String PARTY_TEXT = "partyText";

    private FilterType m_type;
    private String m_paramName;

    /**
     *  This creates a text field that can be used to restrict
     *  the results by user.  The passed in is used for the parameter
     *  that holds the user input party informaiton
     */
    public PartyFilterWidget(FilterType type, String parameterName) {
        super(type, new StringParameter(parameterName));
        m_type = type;
        m_paramName = parameterName;
    }
    
    public FilterSpecification getFilter(PageState state) {
        String searchTerm = (String)getValue(state);
        if (searchTerm == null || searchTerm.trim().length() == 0) {
            return new PartyFilterSpecification(null,
                                                m_type);
        } else {
            return new PartyFilterSpecification(getParties(searchTerm),
                                                m_type);
        }
    }

    private PartyCollection getParties(String searchTerm) {
        searchTerm = StringUtils.stripWhiteSpace(searchTerm);
        PartyCollection parties = Party.retrieveAllParties();
        parties.filter(searchTerm);
        return parties;
    }

    public void generateBodyXML(PageState state,
                                Element parent) {
        super.generateBodyXML(state, parent);
        
        Element partyElement = Search.newElement(PARTY_TEXT);
        partyElement.addAttribute("name", m_paramName);
        partyElement.addAttribute("value", (String)getValue(state));
        parent.addContent(partyElement);
        
    }
}
