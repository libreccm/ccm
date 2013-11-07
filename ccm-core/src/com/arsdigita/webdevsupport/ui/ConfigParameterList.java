/*
 * Copyright (C) 2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.webdevsupport.ui;

import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.PageState;
import com.arsdigita.xml.Element;
import com.arsdigita.runtime.ConfigRegistry;
import com.arsdigita.xml.XML;
import com.arsdigita.util.Classes;
import com.arsdigita.util.StringUtils;
import com.arsdigita.util.parameter.Parameter;
import com.arsdigita.util.parameter.ParameterWriter;
import com.arsdigita.util.parameter.ParameterInfo;
import com.arsdigita.util.parameter.ParameterContext;
import com.arsdigita.util.parameter.ErrorList;

import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.Attributes;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import org.apache.log4j.Logger;


/**
 * 
 *
 */
public class ConfigParameterList extends SimpleContainer {

    private static final Logger s_log = Logger.getLogger(ConfigParameterList.class);

    
    public static final String XML_NS = "http://xmlns.redhat.com/waf/webdevsupport/1.0";

    public ConfigParameterList() {
        super("devsup:configList",
              XML_NS);
    }
    
	@Override
    public void generateXML(PageState state,
                            Element parent) {
        Element content = generateParent(parent);
        
        ConfigRegistry reg = new ConfigRegistry();
        
        Iterator packages = reg.getPackages().iterator();
        while (packages.hasNext()) {
            content.addContent(
                generateApplication(reg, (String)packages.next()));
        }
    }


    public Element generateApplication(ConfigRegistry reg,
                                       String key) {
        List ctxs = new ArrayList();
        XML.parseResource(key + ".config",
                          new ConfigRegistryParser(ctxs));

        Element app = new Element("application");
        app.addAttribute("key", key);

        Iterator i = ctxs.iterator();
        while (i.hasNext()) {
            ParameterContext context = (ParameterContext)i.next();

            // XXX I want the existing in memory
            // values, not load them from disk. Argh
            // XXX Actually I want both so we can 
            // highlight what's out of sync with active
            // server instance
            reg.load(context, new ErrorList());
            
            app.addContent(generateContext(context));
        }

        return app;
    }

    public Element generateContext(ParameterContext context) {
        Element ctx = new Element("context");
        ctx.addAttribute("class", context.getClass().getName());
        
        Parameter[] params = context.getParameters();
        
        for (int i = 0 ; i < params.length ; i++) {
            ctx.addContent(generateParameter(context, params[i]));
        }
        
        return ctx;
    }
    
    public Element generateParameter(ParameterContext context,
                                     Parameter param) {
        final Element p = new Element("param");
        p.addAttribute("name", param.getName());
        p.addAttribute("class", param.getClass().getName());
        p.addAttribute("isRequired", XML.format(new Boolean(param.isRequired())));
        
        param.write(new ParameterWriter() {
		@Override
                public void write(Parameter param, String value) {
                    if (value != null) {
                        p.addAttribute("value", value);
                    }
                }
            }, context.get(param));
        
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
    
    private class ConfigRegistryParser extends DefaultHandler {
        private List m_contexts;

        public ConfigRegistryParser(List contexts) {
            m_contexts = contexts;
        }

	    @Override
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
