/*
 * Copyright (C) 2001 ArsDigita Corporation. All Rights Reserved.
 *
 * The contents of this file are subject to the ArsDigita Public
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of
 * the License at http://www.arsdigita.com/ADPL.txt
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */

package com.arsdigita.london.cms.dublin.ui;

import java.util.StringTokenizer;
import com.arsdigita.bebop.parameters.ParameterData;
import com.arsdigita.bebop.event.ParameterListener;
import com.arsdigita.bebop.event.ParameterEvent;

/**
 *    Verifies that the parameter contains only comma separated
 *    alpha-numeric words (allowing hypens and underscores).
 *
 *    Note: An empty string will pass the validation tests.
 *
 *    @author <a href="mailto:dturner@arsdigita.com">Dave Turner</a>
 *    @version $Id: KeywordsValidationListener.java 652 2005-07-22 13:15:41Z sskracic $
 **/
public class KeywordsValidationListener implements ParameterListener {


    private String m_label;

    // allow tokens to contain only alpha-numerics, hyphens and underscores
    // TODO: configuration parameter maybe?
    private static String TOKEN_PATTERN = "(\\w|-)*";
    private static String TOKEN_DELIMITERS = ", \t\n\r\f";

    public KeywordsValidationListener(String label) {
        m_label = label;
    }

    public KeywordsValidationListener() {
        this("This parameter");
    }

    public void validate(ParameterEvent event) {
        ParameterData data = event.getParameterData();
        String value = data.getValue().toString();

        StringTokenizer st = new StringTokenizer(value, TOKEN_DELIMITERS);
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            if (!token.matches(TOKEN_PATTERN)) {
                // The error message
                StringBuffer msg = new StringBuffer(128);
                msg
                    .append(m_label)
                    .append(" must contain only comma separated keywords");
                data.addError(msg.toString());
                return;
            }
        }
    }
}
