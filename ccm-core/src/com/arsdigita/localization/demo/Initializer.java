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
package com.arsdigita.localization.demo;

import com.arsdigita.initializer.Configuration;
import com.arsdigita.initializer.InitializationException;

import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.TransactionContext;

import com.arsdigita.domain.DataObjectNotFoundException;

import com.arsdigita.kernel.PackageType;
import com.arsdigita.kernel.Stylesheet;

import java.util.Locale;
import org.apache.log4j.Logger;

/**
 * Demonstrate how to add a localization of the Core without having to
 * modify the Core source code.
 *
 * @author Michael Yoon
 * @version $Revision: #10 $
 * @since ACS 5.0
 */
public class Initializer
    implements com.arsdigita.initializer.Initializer {

    private Configuration m_conf = new Configuration();

    public final static String versionId = "$Id: Initializer.java 287 2005-02-22 00:29:02Z sskracic $";

    private static final Logger s_log =
        Logger.getLogger(Initializer.class);

    // TODO: Move this constant to a more appropriate location.
    private final static String SUBSITE_PACKAGE_KEY = "acs-subsite";

    public Configuration getConfiguration() {
        return m_conf;
    }

    public void startup() {

        String stylesheetPath =
            "/packages/localization-demo/xsl/login_de.xsl";

        TransactionContext txn =
            SessionManager.getSession().getTransactionContext();
        txn.beginTxn();

        PackageType subsitePackage = null;

        try {
            subsitePackage =
                PackageType.findByKey(SUBSITE_PACKAGE_KEY);

        } catch (DataObjectNotFoundException e) {
            s_log.warn("Error registering German-localized stylesheet " +
                       "for the ACS login/registration pages: the \"" +
                       SUBSITE_PACKAGE_KEY + "\" PackageType cannot be " +
                       "found.");
            return;
        }

        // Check to see if this stylesheet has already been
        // registered.

        boolean isRegistered = false;
        Stylesheet stylesheet = null;
        Stylesheet[] stylesheets =
            subsitePackage.getStylesheets(Locale.GERMAN,
                                          "text/html");

        for (int i = 0; i < stylesheets.length; i++) {
            stylesheet = stylesheets[i];
            if (stylesheet.getPath().equals(stylesheetPath)) {
                isRegistered = true;
                break;
            }
        }

        if (!isRegistered) {

            s_log.warn
                ("Localization Demo: registering a German-" +
                 "localized stylesheet for ACS login/registration " +
                 "pages...");

            // The stylesheet's outputType property is set to
            // "text/html" by default.

            stylesheet =
                Stylesheet.createStylesheet(stylesheetPath,
                                            Locale.GERMAN);

            subsitePackage.addStylesheet(stylesheet);

            subsitePackage.save();

            s_log.info
                ("Localization Demo: registration of German-" +
                 "localized stylesheet for ACS login/registration " +
                 "pages completed successfully.");
        }

        txn.commitTxn();

    }

    public void shutdown() throws InitializationException {}
}
