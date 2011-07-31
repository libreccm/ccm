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

import com.arsdigita.util.StringUtils;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.util.parameter.ErrorList;
import com.arsdigita.util.parameter.Parameter;
import com.arsdigita.util.parameter.ParameterError;
import com.arsdigita.util.parameter.ParameterInfo;
import com.arsdigita.util.parameter.ParameterReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * ParameterEditor
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #8 $ $Date: 2004/08/16 $
 * @version $Id: ParameterEditor.java 736 2005-09-01 10:46:05Z sskracic $
 */
class ParameterEditor {

    private ParameterMap m_map;
    private BufferedReader m_in;
    private PrintStream m_out;
    private boolean m_optional;
    private boolean m_done;
    private boolean m_valid;
    private Menu m_menu;
    private List m_topics;
    private Map m_help;

    /**
     * 
     * 
     * @param map
     * @param in
     * @param out 
     */
    ParameterEditor(ParameterMap map, InputStream in, PrintStream out) {
        m_map = map;
        m_in = new BufferedReader(new InputStreamReader(in));
        m_out = out;
        m_optional = false;
        m_done = false;
        m_valid = false;
        menu();
    }

    private void menu() {
        m_menu = new Menu(m_in, m_out);
        m_topics = new ArrayList();
        m_help = new HashMap();

        m_menu.add(Menu.GAP);

        int index = 1;
        for (Iterator it = m_map.getParameters().iterator(); it.hasNext(); ) {
            final Parameter param = (Parameter) it.next();
            if (!m_optional
                && (!param.isRequired() || param.getDefaultValue() != null)) {
                continue;
            }
            String key = "" + index++;
            m_menu.add(key, new Menu.Item() {
                public String getDescription() {
                    String value = "" + m_map.get(param);
                    if (value.length() > 20) {
                        value = "\n          " + value;
                    } else {
                        value = " " + value;
                    }
                    return "Set " + getTitle(param) + ":" + value;
                }

                public void act() {
                    read(param);
                }
            });
            m_topics.add(key);
            m_help.put(key, getHelp(param));
        }

        m_menu.add(Menu.GAP);

        m_menu.add("l", new Menu.Item() {
            public String getDescription() {
                return "List parameters";
            }
            public void act() {
                m_menu.display();
            }
        });
        m_menu.add("o", new Menu.Item() {
            public String getDescription() {
                if (m_optional) {
                    return "Hide optional parameters";
                } else {
                    return "Show optional parameters";
                }
            }
            public void act() {
                m_optional = !m_optional;
                menu();
                m_menu.display();
            }
        }, Menu.HORIZONTAL);
        m_menu.add("v", new Menu.Item() {
            public String getDescription() {
                return "Validate parameters";
            }
            public void act() {
                if (validate()) {
                    m_out.println("  -- valid --");
                }
            }
        }, Menu.HORIZONTAL);
        m_menu.add("e", new Menu.Item() {
            public String getDescription() { return "Exit"; }
            public void act() {
                if (validate()) {
                    m_done = true;
                    m_valid = true;
                } else {
                    while (true) {
                        m_out.print
                            ("Configuration is invalid, abort? (yes/no) ");
                        m_out.flush();
                        String line;
                        try {
                            line = m_in.readLine();
                        } catch (IOException e) {
                            throw new UncheckedWrapperException(e);
                        }
                        if (line == null) {
                            abort();
                            return;
                        }
                        line = line.trim();
                        if (line.equals("")) { continue; }
                        if (line.equals("yes")) {
                            abort();
                            return;
                        } else if (line.equals("no")) {
                            return;
                        }
                    }
                }
            }
        }, Menu.HORIZONTAL);
        m_menu.add("r", new Menu.Item() {
            public String getDescription() {
                return "Set required parameters";
            }
            public void act() {
                for (Iterator it = m_map.getParameters().iterator();
                     it.hasNext(); ) {
                    Parameter param = (Parameter) it.next();
                    if (param.isRequired() && m_map.get(param) == null) {
                        while (!read(param)) {};
                        if (m_done) { return; }
                    }
                }
                m_menu.display();
            }
        }, Menu.HORIZONTAL);
        m_menu.add("A", new Menu.Item() {
            public String getDescription() {
                return "Abort";
            }
            public void act() {
                abort();
            }
        }, Menu.HORIZONTAL);
        m_menu.add("?", new Menu.Item() {
            public String getDescription() { return "Help"; }
            public void act() {
                help();
            }
        }, Menu.HORIZONTAL);
        m_menu.add(Menu.GAP);

        loadHelp();
    }

    /**
     * 
     */
    private void loadHelp() {
        Properties props = new Properties();
        InputStream is =
            getClass().getResourceAsStream("ParameterEditor.help");
        if (is == null) {
            throw new IllegalStateException
                ("Can't find ParameterEditor.help");
        }
        try {
            props.load(is);
            is.close();
        } catch (IOException e) {
            throw new UncheckedWrapperException(e);
        }
        String[] topics = StringUtils.split
            (props.getProperty("topics", ""), ',');
        for (int i = 0; i < topics.length; i++) {
            String topic = topics[i].trim();
            m_topics.add(topic);
            m_help.put(topic, props.getProperty("topics." + topic));
        }
    }

    private String getTitle(Parameter param) {
        ParameterInfo pinfo = param.getInfo();
        if (pinfo == null || pinfo.getTitle() == null) {
            return param.getName();
        } else {
            return pinfo.getTitle();
        }
    }

    private String getHelp(Parameter param) {
        ParameterInfo info = param.getInfo();
        if (info == null) { return null; }
        StringWriter buf = new StringWriter();
        PrintWriter pw = new PrintWriter(buf);
        pw.println("    Title: " + info.getTitle());
        pw.println("  Purpose: " + info.getPurpose());
        pw.println("  Example: " + info.getExample());
        pw.print("   Format: " + info.getFormat());
        pw.flush();
        return buf.toString();
    }

    private boolean validate() {
        ErrorList errs = new ErrorList();
        m_map.validate(errs);
        if (errs.isEmpty()) {
            return true;
        } else {
            print(errs);
            return false;
        }
    }

    private void print(ErrorList errs) {
        for (Iterator it = errs.iterator(); it.hasNext(); ) {
            ParameterError err = (ParameterError) it.next();
            m_out.println("  * " + getTitle(err.getParameter()) + ": " +
                          err.getMessage());
        }
    }

    private boolean read(Parameter param) {
        ErrorList errs = new ErrorList();
        Object obj = read(param, errs);
        if (errs.isEmpty()) {
            m_map.set(param, obj);
            return true;
        } else {
            print(errs);
            return false;
        }
    }

    private Object read(Parameter param, ErrorList errs) {
        return param.read(new ParameterReader() {
            public String read(Parameter param, ErrorList errs) {
                m_out.print(getTitle(param) + ": ");
                m_out.flush();
                try {
                    String line = m_in.readLine();
                    if (line == null) {
                        abort();
                        return null;
                    }
                    return line;
                } catch (IOException e) {
                    throw new UncheckedWrapperException(e);
                }
            }
        }, errs);
    }

    private void abort() {
        m_valid = false;
        m_done = true;
        m_out.println("Aborting...");
    }

    private void topics() {
        m_out.print("  Help topics: ");
        for (Iterator it = m_topics.iterator(); it.hasNext(); ) {
            m_out.print(it.next());
            if (it.hasNext()) { m_out.print(", "); }
        }
        m_out.println();
    }

    private void help() {
        topics();
        m_out.print("Topic: ");
        m_out.flush();
        String line;
        try {
            line = m_in.readLine();
        } catch (IOException e) {
            throw new UncheckedWrapperException(e);
        }
        if (line == null) {
            abort();
            return;
        }
        line = line.trim();
        String help = (String) m_help.get(line);
        if (help == null) {
            m_out.println("  * No help for: " + line);
        } else {
            m_out.println(help);
        }
    }

    public boolean edit() {
        m_menu.display();
        while (!m_done) {
            Menu.Item item = m_menu.choose("Choose: ");
            if (item == null) { return false; }
            item.act();
        }
        return m_valid;
    }

    /**
     * Method main added for testing only!
     * @param args 
     */
    public static void main(String[] args) {
        ParameterMap map = new ParameterMap();
        map.addContext(    com.arsdigita.runtime.RuntimeConfig.getConfig());
        map.addContext(new com.arsdigita.web.WebConfig());
        map.addContext(new com.arsdigita.kernel.KernelConfig());
        ParameterEditor pe = new ParameterEditor(map, System.in, System.out);
        pe.edit();
    }

}
