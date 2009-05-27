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
package com.arsdigita.logging.examples;

import com.arsdigita.logging.Log;
import com.arsdigita.logging.SecureLogFilter;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;

/**
 * <p>
 * This is an example class that uses the secure logging api.
 * </p>
 *
 * @version $Revision: #9 $ $Date: 2004/08/16 $
 */
public class LogExample {
    public final static String versionId = 
            "$Id: LogExample.java 287 2005-02-22 00:29:02Z sskracic $" +
            " by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    // get the logger named the same as this class
    static final Logger cat = Logger.getLogger(LogExample.class);

    private static void configureNewAppender(String filename) {
        // try to add an appender that sends log output to
        // the file passed in as the first argument to this program
        // but only output that is not marked as secure
        try {
            // create a rolling file appender with standard layout
            RollingFileAppender rfa = new RollingFileAppender(
                                      new PatternLayout(
                                          PatternLayout.TTCC_CONVERSION_PATTERN
                                          ), filename
                                      );

            // add our secure log filter to this appender so that it
            // won't get any of the secure log messages
            rfa.addFilter(new SecureLogFilter()) ;

            // add this appender to the root category.
            BasicConfigurator.configure(rfa);
        } catch (java.io.IOException e) {
            // we should error out here really but who cares for now
        }
    }

    public static void main(String[] argv) {
        // lets just use the basic configuration
        BasicConfigurator.configure();

        int iterations = 3;
        switch(argv.length) {
        case 0:
            break;
        case 1:
            configureNewAppender(argv[0]);
            break;
        case 2:
            configureNewAppender(argv[0]);
            iterations = Integer.parseInt(argv[1]);
            break;
        default:
            String s = System.getProperty("line.separator");
            System.out.println(
                               "Usage:" + s +
                               "java " + LogExample.class.getName() + s +
                               "java " + LogExample.class.getName() +
                               " <insecure-log-file>" + s +
                               "java " + LogExample.class.getName() +
                               " <insecure-log-file>" + " <number-of-log-messages>"
                               );
            return;
        }

        cat.info("Entering " + LogExample.class.getName());

        for(int i = 0; i < iterations; i++) {
            if(Log.isSecure())
                cat.debug("Logging securely");
            else
                cat.debug("Logging insecurely");

            LogExampleThread t = new LogExampleThread();
            t.start();

            if(Log.isSecure())
                cat.debug("Logging securely");
            else
                cat.debug("Logging insecurely");
        }

        cat.info("Exiting " + LogExample.class.getName());
    }
}
