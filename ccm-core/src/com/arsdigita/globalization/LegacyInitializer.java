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

import com.arsdigita.initializer.Configuration;
import com.arsdigita.initializer.InitializationException;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.TransactionContext;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * Initialize the Globalization package
 *
 * Old initializer system. Needs LegacyInitializer as glue to invoke.
 *
 * @version $Revision: #15 $ $Date: 2004/08/16 $
 */
public class LegacyInitializer implements com.arsdigita.initializer.Initializer {

    private static final String DEFAULT_CHARSET = "defaultCharset";
    private static final String CHARSETS = "charsets";
    private static final String LOCALES  = "locales";

    private static final Logger s_log = Logger.getLogger(LegacyInitializer.class);

    private Configuration m_conf = new Configuration();

    public LegacyInitializer() throws InitializationException {
        m_conf.initParameter
            (CHARSETS,
             "Recognized character sets",
             List.class);

        m_conf.initParameter
            (LOCALES,
             "Recognized locales",
             List.class);
        
        m_conf.initParameter
            (DEFAULT_CHARSET,
             "Default character set for locales not listed",
             String.class);
    }

    public Configuration getConfiguration() {
        return m_conf;
    }

    public void startup() throws InitializationException {
        TransactionContext txn = SessionManager.getSession().
            getTransactionContext();
        txn.beginTxn();

        if ( ! isLoaded() ) {
            s_log.info("Globalization Initializer is loading.");
            load();
        }
        LocaleNegotiator.setApplicationLocaleProvider
            (new ApplicationLocaleProvider());
        LocaleNegotiator.setClientLocaleProvider(new ClientLocaleProvider());
        LocaleNegotiator.setSystemLocaleProvider(new SystemLocaleProvider());

        String defaultCharset = (String)m_conf.getParameter(DEFAULT_CHARSET);
        
        Globalization.setDefaultCharset(defaultCharset);

        txn.commitTxn();        
    }

    public void shutdown() {}

    private static boolean isLoaded() {
        DataCollection charsets = SessionManager.getSession().
            retrieve(Charset.BASE_DATA_OBJECT_TYPE);
        boolean result = (charsets.size() > 0);
        charsets.close();
        return result;
    }

    private void load() throws InitializationException {
        List charsets = (List) m_conf.getParameter(CHARSETS);
        if ( charsets == null ) {
            throw new InitializationException
                ("You must specify at least one charset in the \"" + CHARSETS +
                 "\" parameter of the globalization initaliazer in the init " +
                 " file. ISO-8859-1 would be a good first choice.");
        }

        Map charsetMap = new HashMap();

        for (Iterator i=charsets.iterator(); i.hasNext(); ) {
            String charsetName = (String) i.next();

            // Check if this is a valid charset.  Is there a better way to do
            // this? - vadimn@redhat.com, Mon 2002-07-29 14:47:41 -0400
            try {
                new OutputStreamWriter(new ByteArrayOutputStream(), charsetName);
            } catch (UnsupportedEncodingException ex) {
                throw new InitializationException
                    (charsetName + " is not a supported charset", ex);
            }
            Charset charset = new Charset();
            charset.setCharset(charsetName);
            charset.save();
            charsetMap.put(charsetName, charset);
        }

        List locales = (List) m_conf.getParameter(LOCALES);

        if ( locales == null ) {
            throw new InitializationException
                ("You must specify at least one locale in the \"" + LOCALES +
                 "\" parameter of the globalization initializer section of " +
                 "the init file. The \"en\" locale is probably required.");
        }

        for (Iterator i=locales.iterator(); i.hasNext(); ) {
            List localeData = (List) i.next();
            String language    = (String) localeData.get(0);
            String country     = (String) localeData.get(1);
            String variant     = (String) localeData.get(2);
            String charsetName = (String) localeData.get(3);
            Locale locale = new Locale(language, country, variant);

            Charset defaultCharset = (Charset) charsetMap.get(charsetName);
            if ( defaultCharset == null ) {
                throw new InitializationException
                    ("You must list " + charsetName + " in the \"" + CHARSETS +
                     "\" parameter before using it in the \"" + LOCALES +
                     "\" parameter.");
            }
            locale.setDefaultCharset(defaultCharset);
            locale.save();
        }
    }
}
