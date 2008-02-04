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

import com.arsdigita.initializer.Configuration;
import com.arsdigita.initializer.InitializationException;
import com.arsdigita.initializer.Initializer;
import com.arsdigita.util.URLRewriter;
import org.apache.log4j.Logger;

/**
 * <p>
 * Add the character set encoding provider.
 * </p>
 *
 * @version $Revision: #10 $ $Date: 2004/08/16 $
 */
public class CharsetEncodingProviderInitializer implements Initializer {

    public final static String versionId = "$Id: CharsetEncodingProviderInitializer.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    private static final Logger s_log =
        Logger.getLogger(CharsetEncodingProviderInitializer.class);

    private Configuration m_conf = new Configuration();

    public CharsetEncodingProviderInitializer() throws InitializationException {
    }

    public Configuration getConfiguration() {
        return m_conf;
    }

    public void startup() {
        setCharsetEncodingProvider();
    }

    public void shutdown() {
    }

    private void setCharsetEncodingProvider() {
        URLRewriter.addParameterProvider(new CharsetEncodingProvider());
    }
}
