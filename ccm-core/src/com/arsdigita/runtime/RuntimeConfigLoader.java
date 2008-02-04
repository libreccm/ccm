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
package com.arsdigita.runtime;

import com.arsdigita.util.Assert;
import com.arsdigita.util.config.JavaPropertyLoader;
import java.io.InputStream;
import org.apache.log4j.Logger;

/**
 * @deprecated Use {@link AbstractConfig#load()} instead.
 *
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: RuntimeConfigLoader.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class RuntimeConfigLoader extends JavaPropertyLoader {
    public final static String versionId =
        "$Id: RuntimeConfigLoader.java 287 2005-02-22 00:29:02Z sskracic $" +
        "$Author: sskracic $" +
        "$DateTime: 2004/08/16 18:10:38 $";

    private static final Logger s_log = Logger.getLogger
        (RuntimeConfigLoader.class);

    private final RuntimeClassLoader m_loader;

    public RuntimeConfigLoader() {
        super(System.getProperties());

        m_loader = new RuntimeClassLoader
            (Thread.currentThread().getContextClassLoader());
    }

    public RuntimeConfigLoader(final String resource, final boolean required) {
        this();
        load(resource, required);
    }

    public final void load(final String resource, final boolean required) {
        Assert.exists(resource, String.class);

        final InputStream in = m_loader.getResourceAsStream(resource);

        if (in == null) {
            if (required) {
                throw new ConfigError
                    ("Resource " + resource + " not found; it is required");
            } else {
                if (s_log.isInfoEnabled()) {
                    s_log.info(resource + " was not found; proceeding anyhow");
                }
            }
        } else {
            if (s_log.isDebugEnabled()) {
                s_log.debug("Loading configuration values from " +
                            resource + " on the class path");
            }

            load(in);
        }
    }
}
