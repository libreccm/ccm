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
 */

package com.arsdigita.london.util;

import com.arsdigita.xml.Element;
import com.arsdigita.xml.XML;
import com.arsdigita.xml.Document;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.util.Classes;
import com.arsdigita.util.StringUtils;
import com.arsdigita.util.parameter.Parameter;
import com.arsdigita.util.parameter.ParameterInfo;
import com.arsdigita.util.parameter.ParameterContext;


import java.io.IOException;
import java.io.File;
import java.io.FileOutputStream;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.Attributes;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionBuilder;
import org.apache.log4j.Logger;

public class ConfigPrinter extends Program {

    private static final Logger s_log = Logger.getLogger(ConfigPrinter.class);

    ConfigPrinter() {
        super("Config Printer",
              "1.0.0",
              "APPLICATION...",
              false);
        
        getOptions().addOption(
            OptionBuilder
            .hasArg(true)
            .withArgName("directory")
            .withLongOpt("output")
            .withDescription("XML output directory")
            .create("o"));
    }

    
    public void doRun(CommandLine cmdLine) {
        String[] args = cmdLine.getArgs();
        
        String dir = cmdLine.getOptionValue("o");

        for (int i = 0 ; i < args.length ; i++) {
            Element el = generateApplication(args[i]);

            Document doc = null;
            try {
                doc = new Document(el);
            } catch (ParserConfigurationException ex) {
                throw new UncheckedWrapperException(ex);
            }
            try {
                File dst = dir == null ?
                    new File(args[i] + ".xml") :
                    new File(dir, args[i] + ".xml");
                FileOutputStream os = new FileOutputStream(dst);
                os.write(doc.toString(true).getBytes("UTF-8"));
                os.flush();
                os.close();
            } catch (IOException ex) {
                throw new UncheckedWrapperException("cannot write file", ex);
            }
        }
    }
    
    public Element generateApplication(String key) {
        List reg = new ArrayList();
        XML.parseResource(key + ".config",
                          new ConfigRegistryParser(reg));

        Element app = new Element("application");
        app.addAttribute("key", key);

        Iterator i = reg.iterator();
        while (i.hasNext()) {
            ParameterContext context = (ParameterContext)i.next();
            
            app.addContent(generateContext(context));
        }

        return app;
    }

    public Element generateContext(ParameterContext context) {
        Element ctx = new Element("context");
        ctx.addAttribute("class", context.getClass().getName());
        
        Parameter[] params = context.getParameters();
        
        for (int i = 0 ; i < params.length ; i++) {
            ctx.addContent(generateParameter(params[i]));
        }
        
        return ctx;
    }
    
    public Element generateParameter(Parameter param) {
        Element p = new Element("param");
        p.addAttribute("name", param.getName());
        p.addAttribute("class", param.getClass().getName());
        p.addAttribute("isRequired", XML.format(new Boolean(param.isRequired())));
        
        
        ParameterInfo info = param.getInfo();
        if (info != null) {
            if (StringUtils.emptyString(info.getTitle()) ||
                StringUtils.emptyString(info.getPurpose()) ||
                StringUtils.emptyString(info.getExample()) ||
                StringUtils.emptyString(info.getFormat())) {
                s_log.warn("Info for parameter " + 
                           param.getName() + " is incomplete");
            }

            p.addAttribute("title", info.getTitle());
            p.addAttribute("purpose", info.getPurpose());
            p.addAttribute("example", info.getExample());
            p.addAttribute("format", info.getFormat());
        } else {
            s_log.warn("Parameter " + param.getName() + " has no info");
        }
        
        return p;
    }
    
    public static void main(String[] args) {
        new ConfigPrinter().run(args);
    }

    private class ConfigRegistryParser extends DefaultHandler {
        private List m_contexts;

        public ConfigRegistryParser(List contexts) {
            m_contexts = contexts;
        }

        public void startElement(String uri, String localName, String qn,
                                 Attributes attrs) {
            if (localName.equals("config")) {
                String klass = attrs.getValue(uri, "class");
                // XXX: Is there a better way to handle errors that
                // includes line number information?
                if (klass == null) {
                    throw new IllegalArgumentException
                        ("class and storage attributes are required");
                }

                ParameterContext context = 
                    (ParameterContext)Classes.newInstance(klass);
                m_contexts.add(context);
            }
        }
    }

}
