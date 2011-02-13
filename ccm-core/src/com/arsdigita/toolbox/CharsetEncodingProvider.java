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
package com.arsdigita.toolbox;

import com.arsdigita.bebop.parameters.ParameterData;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.globalization.Globalization;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.util.ParameterProvider;
import com.arsdigita.util.Assert;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;

/**
 * <p>
 * Provides a character set encoding parameter to add to each URL or Form in a
 * request.
 * </p>
 *
 * @version $Id: CharsetEncodingProvider.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class CharsetEncodingProvider implements ParameterProvider {

    private static final Logger logger = Logger.getLogger(CharsetEncodingProvider.class);
    private static StringParameter s_encodingParam =
        new StringParameter(Globalization.ENCODING_PARAM_NAME);

    private static Set s_models = new HashSet();

    static {
        logger.debug("Static initalizer starting...");
        s_encodingParam.setDefaultValue(Globalization.DEFAULT_ENCODING);
        s_encodingParam.setDefaultOverridesNull(true);
        s_models.add(s_encodingParam);
        logger.debug("Static initalizer finished...");
    }

    public Set getModels() {
        return Collections.unmodifiableSet(s_models);
    }

    public Set getParams(HttpServletRequest request) {
        Set params = new HashSet();

        Locale locale = Kernel.getContext().getLocale();
        
        Assert.exists(locale, "Locale locale");

        ParameterData pd = new ParameterData
            (s_encodingParam, Globalization.getDefaultCharset(locale));

        params.add(pd);

        return Collections.unmodifiableSet(params);
    }
}
