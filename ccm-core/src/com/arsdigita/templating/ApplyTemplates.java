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
package com.arsdigita.templating;

import com.arsdigita.util.cmd.CommandLine;
import com.arsdigita.util.cmd.StringSwitch;
import com.arsdigita.util.cmd.BooleanSwitch;
import com.arsdigita.util.UncheckedWrapperException;

import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;

import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.stream.StreamResult;

import java.util.Date;
import java.util.Map;
import java.util.HashMap;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.PatternLayout;

public class ApplyTemplates {
    
    private static final String OPT_LOOP = "-loop";
    private static final String OPT_LOG = "-log";
    private static final String OPT_WARMUP = "-warmup";
    private static final String OPT_VERBOSE = "-verbose";
    
    private static final Logger s_log = Logger.getLogger(ApplyTemplates.class);

    private static CommandLine s_cmd = new CommandLine(
        "apply-templates",
        "java com.arsdigita.templating.ApplyTemplates " +
        "-loop [count] -log [loglevel] -verbose -warmup [count] Stylesheet Input Output "
    );
    static {
        s_log.debug("Static initalizer starting...");
        s_cmd.addSwitch(new StringSwitch(OPT_LOG, 
                                         "Log4j debug level", 
                                         "warn"));
        s_cmd.addSwitch(new StringSwitch(OPT_LOOP, 
                                         "Number of iterations to apply xsl",
                                         "1"));
        s_cmd.addSwitch(new StringSwitch(OPT_WARMUP, 
                                         "Number of iterations to warm up on",
                                         "0"));
        s_cmd.addSwitch(new BooleanSwitch(OPT_VERBOSE, 
                                          "Display progress",
                                          Boolean.FALSE));
        s_log.debug("Static initalizer finished.");
    }
    
    public final static void main(String[] args) {
        ConsoleAppender log =
            new ConsoleAppender(new PatternLayout("%d{ISO8601} [%5.5t] %-5p %c{2} - %m%n"));
        
        log.setThreshold(Level.toLevel("warn"));
        BasicConfigurator.configure(log);


        Map options = new HashMap();
        args = s_cmd.parse(options, args);
        
        String stylesheet = args[0];
        String input = args[1];
        String output = args[2];
        
        log.setThreshold(Level.toLevel((String)options.get(OPT_LOG)));
        
        s_log.debug("Build xml source " + new Date());        
        StreamSource xml = new StreamSource(input);

        s_log.debug("Build xsl source " + new Date());
        StreamSource xsl = new StreamSource(stylesheet);

        s_log.debug("Build html dest " + new Date());
        StreamResult html = new StreamResult(output);

        s_log.debug("Build transformer factory " + new Date());
        TransformerFactory fact = TransformerFactory.newInstance();

        s_log.debug("Build templates " + new Date());
        Templates templates = null;
        try {
            templates = fact.newTemplates(xsl);
        } catch (TransformerConfigurationException tce) {
            throw new UncheckedWrapperException(tce);
        }


        Transformer xf = null;
        try {
            xf = templates.newTransformer();
        } catch (TransformerConfigurationException tce) {
            throw new UncheckedWrapperException(tce);
        }

        boolean verbose = Boolean.TRUE.equals(options.get(OPT_VERBOSE));

        try {
            int warmup = (new Integer((String)options.get(OPT_WARMUP))).intValue();
            Date start = new Date();
            s_log.debug("Warming up " + start);
            for (int i = 0 ; i < warmup ; i++) {
                xf.setOutputProperty("encoding", "UTF-8");
                xf.transform(xml,
                             html);
                if (verbose) {
                    System.out.print(".");
                    System.out.flush();
                }
            }
            if (warmup > 0 && verbose) {
                System.out.println();
            }

            int loop = (new Integer((String)options.get(OPT_LOOP))).intValue();
            start = new Date();
            s_log.debug("Start " + start);
            for (int i = 0 ; i < loop ; i++) {
                xf.setOutputProperty("encoding", "UTF-8");
                xf.transform(xml,
                             html);
                if (verbose) {
                    System.out.print(".");
                    System.out.flush();
                }
            }
            if (verbose) {
                System.out.println();
            }
            Date end = new Date();
            s_log.debug("End " + end);
            
            long duration = end.getTime() - start.getTime();
            s_log.info("Duration for " + loop + " iterations with " + 
                       " is " + duration + " milliseconds");
        } catch (TransformerException ex) {
            throw new UncheckedWrapperException("cannot transform document", ex);
        }

    }
}
