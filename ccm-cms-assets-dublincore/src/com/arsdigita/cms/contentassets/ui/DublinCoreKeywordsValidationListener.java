/*
 * Copyright (C) 2004 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the Open Software License v2.1
 * (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * http://rhea.redhat.com/licenses/osl2.1.html.
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */

package com.arsdigita.cms.contentassets.ui;

import com.arsdigita.bebop.event.ParameterListener;
import com.arsdigita.bebop.event.ParameterEvent;
import com.arsdigita.bebop.parameters.ParameterData;

import java.util.StringTokenizer;

/**
 *    Verifies that the parameter contains only comma separated
 *    alpha-numeric words (allowing hypens and underscores).
 *
 *    Note: An empty string will pass the validation tests.
 *
 *    @author <a href="mailto:dturner@arsdigita.com">Dave Turner</a>
 *    @version $Id: DublinCoreKeywordsValidationListener.java 652 2005-07-22 13:15:41Z sskracic $
 **/
public class DublinCoreKeywordsValidationListener implements ParameterListener {

    private String m_label;

    // allow tokens to contain only alpha-numerics, hyphens and underscores
    // TODO: configuration parameter maybe?
    private static String TOKEN_PATTERN = "(\\w|-)*";
    private static String TOKEN_DELIMITERS = ", \t\n\r\f";

    /**
     * 
     * @param label 
     */
    public DublinCoreKeywordsValidationListener(String label) {
        m_label = label;
    }

    /**
     * 
     */
    public DublinCoreKeywordsValidationListener() {
        this("This parameter");
    }

    /**
     * 
     * @param event 
     */
    public void validate(ParameterEvent event) {
        ParameterData data = event.getParameterData();
        String value = data.getValue().toString();

        StringTokenizer st = new StringTokenizer(value, TOKEN_DELIMITERS);
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            if (!token.matches(TOKEN_PATTERN)) {
                // The error message
                StringBuilder msg = new StringBuilder(128);
                msg.append(m_label)
                   .append(" must contain only comma separated keywords");
                data.addError(msg.toString());
                return;
            }
        }
    }
}
