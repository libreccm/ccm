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
package com.arsdigita.util.parameter;

import com.arsdigita.templating.XSLTemplate;
import com.arsdigita.util.Classes;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.apache.log4j.Logger;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * Subject to change.
 *
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: ParameterPrinter.java 287 2005-02-22 00:29:02Z sskracic $
 */
final class ParameterPrinter {

    private static final Logger s_log = Logger.getLogger
        (ParameterPrinter.class);

    private static final ArrayList s_records = new ArrayList();

    private static final Options OPTIONS = new Options();

    static {
        s_log.debug("Static initalizer starting...");
        OPTIONS.addOption
            (OptionBuilder
             .hasArg(false)
             .withLongOpt("usage")
             .withDescription("Print this message")
             .create());
        OPTIONS.addOption
            (OptionBuilder
             .hasArg(false)
             .withLongOpt("html")
             .withDescription("Generate HTML")
             .create());
        OPTIONS.addOption
            (OptionBuilder
             .hasArg()
             .withLongOpt("file")
             .withArgName("FILE")
             .withDescription("Use list of additional Config classes from FILE")
             .create());
        s_log.debug("Static initalizer starting...");
    }

    private static void writeXML(final PrintWriter out) {
        out.write("<?xml version=\"1.0\"?>");
        out.write("<records>");

        final Iterator records = s_records.iterator();

        while (records.hasNext()) {
            writeRecord(((ParameterContext) records.next()), out);
        }

        out.write("</records>");
        out.close();
    }

    private static void writeRecord(final ParameterContext record,
                                    final PrintWriter out) {
        out.write("<record>");

        final Parameter[] params = record.getParameters();

        for (int i = 0; i < params.length; i++) {
            writeParameter(params[i], out);
        }

        out.write("</record>");
    }

    private static void writeParameter(final Parameter param,
                                       final PrintWriter out) {
        out.write("<parameter>");

        field(out, "name", param.getName());

        if (param.isRequired()) {
            out.write("<required/>");
        }

        final ParameterInfo info = param.getInfo();

        if (info != null) {
            field(out, "title", info.getTitle());
            field(out, "purpose", info.getPurpose());
            field(out, "example", info.getExample());
            field(out, "format", info.getFormat());
        }

        out.write("</parameter>");
    }

    private static void field(final PrintWriter out,
                              final String name,
                              final String value) {
        if (value != null) {
            out.write("<");
            out.write(name);
            out.write("><![CDATA[");
            out.write(value);
            out.write("]]></");
            out.write(name);
            out.write(">");
        }
    }

    private static void register(final String classname) {
        s_records.add((ParameterContext) Classes.newInstance(classname));
    }

    public static final void main(final String[] args) throws IOException {

        CommandLine line = null;
        try {
            line = new PosixParser().parse(OPTIONS, args);
        } catch (ParseException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }

        String[] outFile = line.getArgs();
        if (outFile.length != 1) {
            System.out.println("Usage: ParameterPrinter [--html] [--file config-list-file] output-file");
            System.exit(1);
        }
        if (line.hasOption("usage")) {
            System.out.println("Usage: ParameterPrinter [--html] [--file config-list-file] output-file");
            System.exit(0);
        }

        if (line.hasOption("file")) {
            String file = line.getOptionValue("file");
            try {
                BufferedReader reader =  new BufferedReader(new FileReader(file));
                String configClass;
                while ((configClass = reader.readLine()) != null) {
                    register(configClass);
                }
            } catch (IOException e) {
                System.err.println(e.getMessage());
                System.exit(1);
            }
        } else {
            register("com.arsdigita.runtime.RuntimeConfig");
            register("com.arsdigita.web.WebConfig");
            register("com.arsdigita.templating.TemplatingConfig");
            register("com.arsdigita.kernel.KernelConfig");
            register("com.arsdigita.kernel.security.SecurityConfig");
            register("com.arsdigita.mail.MailConfig");
            register("com.arsdigita.versioning.VersioningConfig");
            register("com.arsdigita.search.SearchConfig");
            register("com.arsdigita.search.lucene.LuceneConfig");
            register("com.arsdigita.kernel.security.SecurityConfig");
            register("com.arsdigita.bebop.BebopConfig");
            register("com.arsdigita.dispatcher.DispatcherConfig");
            register("com.arsdigita.workflow.simple.WorkflowConfig");
            register("com.arsdigita.cms.ContentSectionConfig");
        }


        if (line.hasOption("html")) {
            final StringWriter sout = new StringWriter();
            final PrintWriter out = new PrintWriter(sout);

            writeXML(out);

            final XSLTemplate template = new XSLTemplate
                (ParameterPrinter.class.getResource
                     ("ParameterPrinter_html.xsl"));

            final Source source = new StreamSource
                (new StringReader(sout.toString()));
            final Result result = new StreamResult(new File(outFile[0]));

            template.transform(source, result);
        } else {
            final PrintWriter out = new PrintWriter
                (new FileWriter(outFile[0]));

            writeXML(out);
        }
    }
}
