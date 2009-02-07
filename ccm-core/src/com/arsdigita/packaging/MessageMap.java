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
package com.arsdigita.packaging;

import com.arsdigita.util.Assert;
import com.arsdigita.util.UncheckedWrapperException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import org.apache.log4j.Logger;

/**
 * Helper class which can be used by the packaging classes and others. Manages 
 * messages to be printed and makes it easier to handle multi line messages.
 * 
 * Subject to change.
 *
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: MessageMap.java 287 2005-02-22 00:29:02Z sskracic $
 */
public final class MessageMap {
    public static final String versionId =
        "$Id: MessageMap.java 287 2005-02-22 00:29:02Z sskracic $" +
        "$Author: sskracic $" +
        "$DateTime: 2004/08/16 18:10:38 $";

    private static final Logger s_log = Logger.getLogger(MessageMap.class);

    private final HashMap m_messages;

    public MessageMap() {
        m_messages = new HashMap();
    }

    public String get(final String key) {
        Assert.exists(key, String.class);

        return (String) m_messages.get(key);
    }

    public void load(final Reader reader) {
        try {
            internalLoad(reader);
        } catch (IOException ioe) {
            throw new UncheckedWrapperException(ioe);
        }
    }

    private void internalLoad(final Reader reader) throws IOException {
        Assert.exists(reader, Reader.class);

        final BufferedReader breader = new BufferedReader(reader);

        String line = null;
        String key = null;
        StringBuffer message = new StringBuffer();

        while ((line = breader.readLine()) != null) {
            if (line.startsWith("[") && line.endsWith("]")) {
                if (key != null) {
                    m_messages.put(key, message.toString().trim());
                }

                message = new StringBuffer();

                key = line.substring(1, line.length() - 1);

                continue;
            }

            message.append(line + "\n");
        }

        m_messages.put(key, message.toString().trim());
    }
}
