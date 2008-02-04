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
package com.arsdigita.cms.ui.authoring;

import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.SingleSelect;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.util.LanguageUtil;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.util.Pair;

import java.util.Collection;
import java.util.Iterator;


/**
 * Language picker for the multilingual content items.
 */
public class LanguageWidget extends SingleSelect {

    public LanguageWidget(String name) {
        this(new StringParameter(name));
    }
    
    public LanguageWidget(ParameterModel model) {
        super(model);
        setupOptions();
    }
    
    /**
     * Adds list of languages.
     * Default version shows all supported languages for this CMS installation, as defined
     * in the enterprise.init: com.arsdigita.cms.installer.Initializer: languages
     */
    protected void setupOptions() {
        Collection languages = LanguageUtil.convertToG11N(LanguageUtil.getSupportedLanguages2LA());
        for (Iterator iter=languages.iterator(); iter.hasNext(); ) {
            Pair pair = (Pair) iter.next();
            String langCode = (String) pair.getKey();
            GlobalizedMessage langName = (GlobalizedMessage) pair.getValue();
            addOption(new Option(langCode, new Label(langName)));
        }
    }
    
}
