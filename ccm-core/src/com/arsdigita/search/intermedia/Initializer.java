/*
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
package com.arsdigita.search.intermedia;

import com.arsdigita.db.DbHelper;
import com.arsdigita.mimetypes.MimeTypeStatus;
import com.arsdigita.mimetypes.converters.ConvertFormat;
import com.arsdigita.runtime.ConfigError;
import com.arsdigita.runtime.ContextCloseEvent;
import com.arsdigita.runtime.ContextInitEvent;
import com.arsdigita.search.FilterType;
import com.arsdigita.search.IndexerType;
import com.arsdigita.search.QueryEngineRegistry;
import com.arsdigita.search.Search;
import com.arsdigita.search.filters.CategoryFilterType;
import com.arsdigita.search.filters.ObjectTypeFilterType;
import com.arsdigita.search.filters.PermissionFilterType;
import com.arsdigita.util.Assert;
import com.arsdigita.util.StringUtils;

import java.math.BigDecimal;

import org.apache.log4j.Logger;

/**
 * Initializes the Intermedia package.
 *
 * This initializer is a sub-initializer of the core initializer which adds it
 * to the list of initializers to be executed
 *
 * @author Peter Boy (pboy@barkhof.uni-bremen.de)
 * @version $Id: $
 *
 */
public class Initializer extends com.arsdigita.runtime.GenericInitializer {

    // Creates a s_logging category with name = to the full name of class
    public static final Logger s_log = Logger.getLogger(Initializer.class);

    private static final IntermediaConfig conf = IntermediaConfig.getConfig();

    /**
     * Constructor
     */
    public Initializer() {
    }


    /**
     * Implementation of the {@link Initializer#init(ContextInitEvent)}
     * method.
     *
     * @param evt The context init event.
     */
    @Override
    public void init(ContextInitEvent evt) {

        // In any case we have to check whether the INSO filter is set
        // correctly.
        // If it's not Oracle, INSO filters don't exist, so override whatever is
        // configured in registry.
        final boolean isNonOracleDB = DbHelper.getDatabase() != DbHelper.DB_ORACLE;
        if (conf.isINSOFilterEnabled() && isNonOracleDB) {
            s_log.debug("INSO filter is set to true using a non Oracle database. "
                       + "This is not allowed. Setting to false. Database=" +
                       DbHelper.getDatabaseName(DbHelper.getDatabase()));

            // silently disable filter
            MimeTypeStatus ms = MimeTypeStatus.getMimeTypeStatus();
            ms.setInsoFilterWorks(new BigDecimal(0));
            ms.save();

        }

        if (Search.getConfig().isIntermediaEnabled()) {

            if (DbHelper.getDatabase() != DbHelper.DB_ORACLE) {
                throw new ConfigError(
                    "Intermedia searching only works on Oracle, not " +
                    DbHelper.getDatabaseName(DbHelper.getDatabase()));
            }

            // update INSO filter status
            MimeTypeStatus ms = MimeTypeStatus.getMimeTypeStatus();
            if (conf.isINSOFilterEnabled() && testINSOFilter() ) {
                ms.setInsoFilterWorks(new BigDecimal(1));
            } else {
                ms.setInsoFilterWorks(new BigDecimal(0));
            }
            ms.save();
            
            // Multiply by 1000 to convert from seconds to milliseconds
            BuildIndex.setParameterValues( conf.getTimerDelay() * 1000,
                                           conf.getSyncDelay() * 1000,
                                           conf.getMaxSyncDelay() * 1000,
                                           conf.getMaxIndexingTime() * 1000,
                                           conf.getIndexingRetryDelay() * 1000 );

            BuildIndex.startTimer();

            s_log.debug("Registering query engines");
            QueryEngineRegistry.registerEngine(IndexerType.INTERMEDIA,
                                               new FilterType[] {
                                                   new PermissionFilterType(),
                                                   new ObjectTypeFilterType(),
                                                   new CategoryFilterType()
                                               },
                                               new BaseQueryEngine());
        } else {
            s_log.debug("Intermedia search engine not enabled. Initialization skipped.");
        }
     }

    /**
     * Implementation of the {@link Initializer#init(ContextCloseEvent)}
     * method which stops the background thread started by
     * {@link Initializer#init(ContextInitEvent)} initialization method
     */
    @Override
    public void close(ContextCloseEvent evt) {
            if (Search.getConfig().isIntermediaEnabled()) {
                BuildIndex.stopTimer();
        }
    }



    /**
     * Provides a routine test whether the INSO filter works correctly.
     *
     * @return true if INSO Filter test passed, false otherwise.
     */
    private boolean testINSOFilter() {
 
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
            return false;
        }

        // remove white space before doing matching.  In case version
        // of INSO filter changes.
        final String expected = StringUtils.
            stripWhiteSpace(rtfTestFileHTML).toLowerCase();

        actual = StringUtils.stripWhiteSpace(actual).toLowerCase();
        if (expected.equals(actual)) {
            s_log.info("INSO Filter test passed.");
            return true;
        } else if (actual.startsWith("<html><body> <p") &&
                   actual.indexOf("but lets see if it really works.") >= 0 &&
                   actual.indexOf("this app uses the intermedia inso " +
                                  "filtering to automatically convert " +
                                  "from an rtf format to html format.") >= 0 &&
                   actual.endsWith("</body></html>")) {
            s_log.info("INSO Filter test passed.");
            return true;
        } else {
            s_log.warn("INSO Filter test failed. " +
                       "(Will not be able to convert documents to "+
                       "html format by file uploading.)");
            s_log.warn("INSO Filter test Expected: '" + expected + "'");
            s_log.warn("Got: '" + actual + "'");
            return false ;
        }
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
