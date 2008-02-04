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
package com.arsdigita.globalization;

import com.arsdigita.dispatcher.DispatcherHelper;
import com.arsdigita.dispatcher.RequestValue;
import java.util.Locale;

/**
 * <p>
 * Provides the Locale of an application.
 * </p>
 *
 * @version $Revision: #8 $ $Date: 2004/08/16 $
 */
public class ApplicationLocaleProvider implements LocaleProvider {
    public final static String versionId = "$Id: ApplicationLocaleProvider.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    private RequestValue m_locale = new RequestValue();

    public Locale getLocale() {
        return (Locale) m_locale.get(DispatcherHelper.getRequest());
    }

    /**
     * <p>
     * Set the Locale you wish to provide.
     * </p>
     *
     * @param locale, The locale to provide.
     */
    public void setLocale(Locale locale) {
        m_locale.set(DispatcherHelper.getRequest(), locale);
    }
}
