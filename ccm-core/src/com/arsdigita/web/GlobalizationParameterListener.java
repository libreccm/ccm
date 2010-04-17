/*
 * Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.web;

import com.arsdigita.kernel.Kernel;
import com.arsdigita.globalization.Globalization;
import com.arsdigita.util.Assert;
import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;

/**
 * <p>Preserves the <code>g11n.enc</code> parameter.  This is a
 * temporary solution.</p>
 *
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: GlobalizationParameterListener.java 287 2005-02-22 00:29:02Z sskracic $
 */
class GlobalizationParameterListener implements ParameterListener {

    private static final Logger s_log = Logger.getLogger
        (GlobalizationParameterListener.class);

    public final void run(final HttpServletRequest sreq,
                          final ParameterMap map) {
        final String value = sreq.getParameter
            (Globalization.ENCODING_PARAM_NAME);

        if (value == null) {
            final Locale locale = Kernel.getContext().getLocale();

            Assert.exists(locale, "Locale locale");

            map.setParameter(Globalization.ENCODING_PARAM_NAME,
                             Globalization.getDefaultCharset(locale));

            //if (s_log.isDebugEnabled()) {
            //    s_log.debug("The parameter was null; I set it to '" +
            //                map.getParameter
            //                    (Globalization.ENCODING_PARAM_NAME) + "'");
            //}
        } else {
            //if (s_log.isDebugEnabled()) {
            //    s_log.debug("The parameter was set to '" + value + "'; " +
            //                "preserving it");
            //}

            map.setParameter(Globalization.ENCODING_PARAM_NAME, value);
        }
    }
}
