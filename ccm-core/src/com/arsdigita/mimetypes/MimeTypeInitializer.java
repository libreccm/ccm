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
package com.arsdigita.mimetypes;

import com.arsdigita.db.DbHelper;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.domain.DomainObjectInstantiator;
import com.arsdigita.initializer.Configuration;
import com.arsdigita.initializer.InitializationException;
import com.arsdigita.kernel.BaseInitializer;
import com.arsdigita.mimetypes.converters.ConvertFormat;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.TransactionContext;
import com.arsdigita.util.Assert;
import com.arsdigita.util.StringUtils;
import org.apache.log4j.Logger;

import java.math.BigDecimal;

/**
 * Initializes mime type tables.
 *
 * @author Jeff Teeters
 * @author Stanislav Freidin
 * @version $Revision: #15 $ $Date: 2004/08/16 $
 */
public class MimeTypeInitializer extends BaseInitializer {
    private static final Logger s_log = Logger.getLogger(MimeTypeInitializer.class);

    private final Configuration m_conf = new Configuration();
    public static final String INSO_FILTER_WORKS = "INSOFilterEnabled";

    /**
     * Update status table with new hash code
     **/
    private void updateStatus() {
        MimeTypeStatus ms = MimeTypeStatus.getMimeTypeStatus();
        if ( isFilterEnabled() ) {
            ms.setInsoFilterWorks(new BigDecimal(1));
        } else {
            ms.setInsoFilterWorks(new BigDecimal(0));
        }
        ms.save();
    }

    /**
     * Register a trivial instantiator for MimeType
     **/
    private static void registerInstantiator() {
        if (DomainObjectFactory.getInstantiator
                (MimeType.BASE_DATA_OBJECT_TYPE) == null) {
            DomainObjectInstantiator instMimeType = new DomainObjectInstantiator() {
                    public DomainObject doNewInstance(DataObject dataObject) {
                        return new MimeType(dataObject);
                    }
                };
            DomainObjectFactory.registerInstantiator
                (MimeType.BASE_DATA_OBJECT_TYPE, instMimeType);
        }
    }

    public MimeTypeInitializer() throws InitializationException {
        m_conf.initParameter
            (INSO_FILTER_WORKS,
             "Set to true if you have a working INSO filter",
             Boolean.class,
             Boolean.FALSE);
    }

    public Configuration getConfiguration() {
        return m_conf;
    }

    protected void doStartup() {
        TransactionContext txn =
            SessionManager.getSession().getTransactionContext();
        txn.beginTxn();

        // If it's not Oracle, INSO filters don't exist, so override whatever is
        // in enterprise.init.
        final boolean isNonOracleDB = DbHelper.getDatabase() != DbHelper.DB_ORACLE;
        if (isFilterEnabled() && isNonOracleDB) {
            s_log.warn("Had " + INSO_FILTER_WORKS +
                       " set to true when using a non Oracle database. " +
                       "This is not allowed. Setting to false. Database=" +
                       DbHelper.getDatabaseName(DbHelper.getDatabase()));
            disableFilter();
        }

        testINSOFilter();
        updateStatus();
        registerInstantiator();

        txn.commitTxn();
    }

    private void disableFilter() {
        m_conf.setParameter(INSO_FILTER_WORKS, Boolean.FALSE);
    }

    protected void doShutdown () { }

    private void testINSOFilter() {
        if ( !isFilterEnabled() ) {
            s_log.info("Not testing INSO filter.");
            return;
        }

        Assert.isTrue(DbHelper.getDatabase() == DbHelper.DB_ORACLE,
                     "Testing INSO filter on non Oracle DB! Shouldn't happen!");

        s_log.info("Starting INSO filter test.  If server hangs here,\n" +
                   "  kill the job, then change your enterprise.init setting;' in \n" +
                   "  enterprise.init com.arsdigita.cms.installer.Initializer.");

        String actual = ConvertFormat.toHTML(rtfTestFile.getBytes());

        if (actual == null) {
            s_log.warn("INSO Filter test failed. " +
                       "Unable to convert test rtf document to html.");
            s_log.warn("INSO Filter test Expected: '" + rtfTestFileHTML + "'");
            s_log.warn("INSO Filter test Got: '" + actual + "'");
            disableFilter();
            return;
        }

        // remove white space before doing matching.  In case version
        // of INSO filter changes.
        final String expected = StringUtils.
            stripWhiteSpace(rtfTestFileHTML).toLowerCase();

        actual = StringUtils.stripWhiteSpace(actual).toLowerCase();
        if (expected.equals(actual)) {
            s_log.info("INSO Filter test passed.");
            return;
        } else if (actual.startsWith("<html><body> <p") &&
                   actual.indexOf("but lets see if it really works.") >= 0 &&
                   actual.indexOf("this app uses the intermedia inso " +
                                  "filtering to automatically convert " +
                                  "from an rtf format to html format.") >= 0 &&
                   actual.endsWith("</body></html>")) {
            s_log.info("INSO Filter test passed.");
            return;
        } else {
            s_log.warn("INSO Filter test failed. " +
                       "(Will not be able to convert documents to "+
                       "html format by file uploading.)");
            s_log.warn("INSO Filter test Expected: '" + expected + "'");
            s_log.warn("Got: '" + actual + "'");
            disableFilter();
            return;
        }
    }

    private boolean isFilterEnabled() {
        final Boolean isEnabled = (Boolean) m_conf.getParameter(INSO_FILTER_WORKS);
        return isEnabled.booleanValue();
    }

    // rtf file used to test inso filter conversion to html
    private static final String rtfTestFile =
        "{\\rtf1\\ansi\\deff0\n" +
        "{\\fonttbl{\\f0\\froman\\fprq2\\fcharset0 Times;}}\n" +
        "{\\colortbl\\red0\\green0\\blue0;\\red255\\green255\\blue" +
        "255;\\red128\\green128\\blue128;}\n" +
        "{\\stylesheet{\\s1\\snext1 Standard;}\n}\n" +
        "{\\info{\\comment StarWriter}{\\vern5690}}\\deftab720\n" +
        "{\\*\\pgdsctbl\n" +
        "{\\pgdsc0\\pgdscuse195\\pgwsxn12240\\pghsxn15840\\marglsxn" +
        "1800\\margrsxn1800\\margtsxn1440\\margbsxn1440\\pgdscnxt0 Standard;}}\n" +
        "\\paperh15840\\paperw12240\\margl1800\\margr1800\\margt" +
        "1440\\margb1440\\sectd\\sbknone\\pgwsxn12240\\pghsxn" +
        "15840\\marglsxn1800\\margrsxn1800\\margtsxn1440\\margbsxn" +
        "1440\\ftnbj\\ftnstart1\\ftnrstcont\\ftnnar\\aenddoc\\aftnr" +
        "stcont\\aftnstart1\\aftnnrlc\n" +
        "\\pard\\plain \\s1\\fs40\\qc test rtf file.\n" +
        "\\par \\pard\\plain \\s1 \n" +
        "\\par This app uses the interMedia INSO filtering to " +
        "automatically convert from an RTF format to html format.\n" +
        "\\par \n" +
        "\\par But lets see if it really works.\n" +
        "\\par \n\\par }\n";


    // The following should be the result of converting rtfTestFile
    // to html using the inso filter.
    private static final String rtfTestFileHTML =
        "<HTML><BODY>\n" +
        "<P><A NAME=\"s1content\"></A>test rtf file.&nbsp;<BR></P>\n" +
        "<P>This app uses the interMedia INSO filtering to \n" +
        "automatically convert from an RTF format to html \n" +
        "format.&nbsp;<BR></P>\n" +
        "<P>But lets see if it really works.</P>\n" +
        "</BODY></HTML>\n";
}
