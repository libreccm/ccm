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
package com.arsdigita.core;

import com.arsdigita.db.DbHelper;
import com.arsdigita.initializer.Configuration;
import com.arsdigita.initializer.Initializer;
import com.arsdigita.initializer.Script;
import com.arsdigita.packaging.Config;
import com.arsdigita.packaging.ConfigRegistry;
import com.arsdigita.packaging.RegistryConfig;
import com.arsdigita.util.StringUtils;
import com.arsdigita.util.parameter.Parameter;
import com.arsdigita.util.parameter.ParameterContext;
import com.arsdigita.util.servlet.HttpHost;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.log4j.Logger;

/**
 * Subject to change.
 *
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: TroikaRickshawUpgrade.java 736 2005-09-01 10:46:05Z sskracic $
 */
public class TroikaRickshawUpgrade {
    private static final Logger s_log = Logger.getLogger
        (TroikaRickshawUpgrade.class);
    private static final String s_key = "ccm-core";
    private static final String s_defaultDir = "/var/www/ccm-core/dist";

    public static final void main(final String[] args) throws IOException {
        final RegistryConfig rconfig = new RegistryConfig();
        rconfig.load();

        final List loaded = Arrays.asList(rconfig.getPackages());

        if (loaded.contains(s_key)) {
            System.out.println("The ccm-core package is already upgraded");
            System.exit(1);
        }

        final ConfigRegistry reg = new ConfigRegistry();
        reg.initialize(s_key);

        final Config config = new Config(reg);
        config.load(System.err);

        Parameter param = config.getParameter("waf.config.packages");
        ParameterContext ctx = config.getContainer(param);

        final List pkgs = new ArrayList
            (Arrays.asList((String[]) ctx.get(param)));
        pkgs.add(s_key);
        ctx.set(param, (String[]) pkgs.toArray(new String[pkgs.size()]));

        String webappBase = null;

        int count = 0;
        while (true) {
            if (count == 0) {
                count++;
                for (int i = 0; i < args.length; i++) {
                    String[] split = StringUtils.split(args[i],'=');
                    if (split.length == 2 && "webapp-dir".equals(split[0])) {
                        webappBase = split[1];
                        break;
                    }
                }
            }
            if (webappBase == null) {
                webappBase = promptWebappBase();
            }
            if (webappBase.equals("")) {
                webappBase = s_defaultDir;
            }

            final File file = new File(webappBase);

            if (file.exists() && file.isDirectory()) {
                break;
            } else {
                System.out.println
                    (webappBase + " does not exist or is not a directory. " +
                     "Please check the path and try again.");
            }
        }

        final File initFile = new File
            (webappBase + "/WEB-INF/resources/enterprise.init");

        System.out.println("Loading old configuration from " + initFile);

        final Script script = Script.readConfig(new FileReader(initFile));

        final Initializer dinit = script.getInitializer
            ("com.arsdigita.db.Initializer");

        final Configuration dconfig = dinit.getConfiguration();
        final String url = (String) dconfig.getParameter("jdbcUrl");
        final String user = (String) dconfig.getParameter("dbUsername");
        final String password = (String) dconfig.getParameter("dbPassword");

        final int db_type = DbHelper.getDatabaseFromURL(url);
        String jdbc = "";
        switch (db_type) {
            case DbHelper.DB_ORACLE:
                jdbc = url;
                int index = jdbc.indexOf(':');
                index = jdbc.indexOf(':',index+1);
                index = jdbc.indexOf(':',index+1);
                final String hostinfo = jdbc.substring(index + 1);
                jdbc = jdbc.substring(0,index) + ":" + user + "/";
                if (password != null && !password.equals("")) {
                    jdbc = jdbc + password;
                }
                jdbc = jdbc + hostinfo;
                break;
            case DbHelper.DB_POSTGRES:
                jdbc = url + "?user=" + user;
                if (password != null && !password.equals("")) {
                    jdbc = jdbc + "&password=" + password;
                }
                break;
        default: System.out.println ("jdbc url '" + url + "' is not recognized as Oracle or PostgreSQL");
        }

        System.out.println("Translating JDBC URL as " + jdbc);

        set(config, "waf.runtime.jdbc_url", jdbc);

        final File pdl = new File(webappBase + "/WEB-INF/pdl");

        System.out.println("Loading PDL from " + pdl);

        final Initializer winit = script.getInitializer
            ("com.arsdigita.web.Initializer");

        final Configuration wconfig = winit.getConfiguration();
        final String server = (String) wconfig.getParameter("serverName");
        final Integer port = (Integer) wconfig.getParameter("serverPort");
        final HttpHost hserver = new HttpHost(server, port.intValue());

        System.out.println("Translating server config as " + hserver);

        set(config, "waf.web.server", hserver);

        config.save();
    }

    private static String promptWebappBase() throws IOException {
        System.out.println
            ("Enter the fully qualified path of the ccm-core webapp " +
             "directory or press enter to accept the default.");

        System.out.print("[" + s_defaultDir + "]: ");

        final BufferedReader reader = new BufferedReader
            (new InputStreamReader(System.in));

        final String result = reader.readLine();

        return result.trim();
    }

    private static void set(final Config config,
                            final String key,
                            final Object value) {
        final Parameter param = config.getParameter(key);
        final ParameterContext ctx = config.getContainer(param);
        ctx.set(param, value);

        System.out.println("Parameter " + key + " set to " + value);
    }
}
