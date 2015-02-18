/*
 * Copyright (C) 2007 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.event.ParameterEvent;
import com.arsdigita.bebop.parameters.GlobalizedParameterListener;
import com.arsdigita.bebop.parameters.ParameterData;
import com.arsdigita.cms.contenttypes.util.DecisionTreeGlobalizationUtil;
import com.arsdigita.globalization.GlobalizedMessage;

/**
 * Verifies that the parameter's value contains only letters, digits, "-" and "_".
 *
 * @author Carsten Clasohm
 * @version $Id$
 */
public class DecisionTreeParameterNameValidationListener 
             extends GlobalizedParameterListener {

    public DecisionTreeParameterNameValidationListener() {
        setError(DecisionTreeGlobalizationUtil.globalize(
                "cms.contenttypes.ui.decisiontree.error.parameter_name_characters"));
    }

    public DecisionTreeParameterNameValidationListener(GlobalizedMessage error) {
        setError(error);
    }

    public void validate (ParameterEvent e) throws FormProcessException {
        ParameterData data = e.getParameterData();
        Object obj = data.getValue();

        if (obj == null) {
            return;
        }

        String value;
        try {
            value = (String) obj;
        } catch (ClassCastException cce) {
            throw new FormProcessException(cce);
        }

        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            if (!isPrintableChar(c)) {
                data.addError(getError());
                return;
            }
        }
    }

      private boolean isPrintableChar( char c ) {
          Character.UnicodeBlock block = Character.UnicodeBlock.of( c );
          return (!Character.isISOControl(c)) && 
                  block != null &&
                  block != Character.UnicodeBlock.SPECIALS;
      }

}
