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
package com.arsdigita.kernel.security;

import com.arsdigita.initializer.InitializationException;
import com.arsdigita.util.StringUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.Configuration;
import org.apache.log4j.Logger;

/**
 * <p>Parses a login <code>Configuration</code> from an
 * <code>Initializer</code> entry.</p>
 *
 * @author Sameer Ajmani
 * @since ACS 4.5
 * @version $Id: LoginConfig.java 287 2005-02-22 00:29:02Z sskracic $
 **/
public class LoginConfig extends Configuration {

    /** Maps application names to <code>AppConfigurationEntry[]</code>. **/
    private Map m_appConfigs = new HashMap();

    private static final Logger s_log = Logger.getLogger(LoginConfig.class);

    /**
     * <p>Creates a new login configuration.  <tt>Request</tt> and <tt>Register</tt>
     * are mandatory contexts, WAF refuses to start if they are not configured.
     * Each login context can span multiple modules.  </p>
     *
     *   <p> The input <tt>List</tt> comprises of <tt>StringS</tt> adhering to the
     * following format: </p>
     *
     * <pre>
     *    context:moduleName:controlFlag[:option1[:option2[:...]]]
     * </pre>
     *
     * <dl>
     *
     * <dt><i>context</i></dt>
     * <dd>String</dd>
     *
     * <dt><i>moduleName</i></dt>
     * <dd>String</dd>
     *
     * <dt><i>controlFlag</i></dt>
     * <dd>"required"</dd>
     * <dd>"requisite"</dd>
     * <dd>"sufficient"</dd>
     * <dd>"optional"</dd>
     *
     * <dt><i>option</i></dt>
     * <dd>"key=value"</dd>
     * </dl>
     *
     * <p>Example:</p>
     *
     * <pre>
     *     Request:com.arsdigita.kernel.security.CredentialLoginModule:requisite:debug=true
     *     Register:com.arsdigita.kernel.security.LocalLoginModule:requisite
     *     Register:com.arsdigita.kernel.security.UserIDLoginModule:requisite
     *     Register:com.arsdigita.kernel.security.CredentialLoginModule:optional
     * </pre>
     *
     * @throws InitializationException if there is a parsing error.
     **/
    public LoginConfig(List config) throws InitializationException {
        Map contextConfigs = new HashMap();
        for (int i = 0; i < config.size(); i++) {
            String tuple = (String) config.get(i);
            int pos = tuple.indexOf(':');
            String context = tuple.substring(0, pos);
            String moduleConf = tuple.substring(pos+1);
            List contextConfig = (List) contextConfigs.get(context);
            if (contextConfig == null) {
                contextConfig = new ArrayList();
                contextConfigs.put(context, contextConfig);
            }
            contextConfig.add(moduleConf);
        }
        for (Iterator it = contextConfigs.keySet().iterator(); it.hasNext(); ) {
            String context = (String) it.next();
            addAppConfig(context, (List) contextConfigs.get(context));
        }
    }

    private void addAppConfig(String name, List entries)
        throws InitializationException {
        AppConfigurationEntry[] array =
            new AppConfigurationEntry[entries.size()];
        for (int i = 0; i < array.length; i++) {
            List entry = Arrays.asList( StringUtils.split( (String) entries.get(i), ':'));
            array[i] = loadAppConfigEntry(entry);
        }
        m_appConfigs.put(name, array);
    }

    private AppConfigurationEntry loadAppConfigEntry(List list)
        throws InitializationException {
        Iterator iter = list.iterator();
        String name = getString(iter, "module class name");
        AppConfigurationEntry.LoginModuleControlFlag flag
            = getFlag(getString(iter, "control flag"));
        Map options = new HashMap();
        while (iter.hasNext()) {
            addOption(iter, options);
        }
        return new AppConfigurationEntry(name, flag, options);
    }

    private AppConfigurationEntry.LoginModuleControlFlag
        getFlag(String flag) throws InitializationException {
        if (flag.equalsIgnoreCase("requisite")) {
            return AppConfigurationEntry.LoginModuleControlFlag.REQUISITE;
        }
        if (flag.equalsIgnoreCase("required")) {
            return AppConfigurationEntry.LoginModuleControlFlag.REQUIRED;
        }
        if (flag.equalsIgnoreCase("sufficient")) {
            return AppConfigurationEntry.LoginModuleControlFlag.SUFFICIENT;
        }
        if (flag.equalsIgnoreCase("optional")) {
            return AppConfigurationEntry.LoginModuleControlFlag.OPTIONAL;
        }
        throw new InitializationException
            ("Control flag must be one of \"required\", "
             +"\"requisite\", \"sufficient\", or \"optional\", "
             +"but got: \""+flag+"\"");
    }

    private void addOption(Iterator iter, Map map)
        throws InitializationException {
        String option = getString(iter, "option");
        int index = option.indexOf('=');
        if (index == -1) {
            throw new InitializationException
                ("Option must be \"key=value\", but got: \""
                 +option+"\"");
        }
        String key = option.substring(0, index);
        String value = option.substring(index+1);
        map.put(key, value);
    }

    private String getString(Iterator iter, String name)
        throws InitializationException {
        Object temp = getObject(iter, name);
        if (!(temp instanceof String)) {
            throw new InitializationException
                ("Expected String "+name
                 +", but got: \""+temp+"\"");
        }
        return (String)temp;
    }

    private Object getObject(Iterator iter, String name)
        throws InitializationException {
        if (!iter.hasNext()) {
            throw new InitializationException
                ("Missing "+name);
        }
        return iter.next();
    }

    // overrides Configuration
    public AppConfigurationEntry[] getAppConfigurationEntry(String name) {
        return (AppConfigurationEntry[])m_appConfigs.get(name);
    }

    // overrides Configuration
    public void refresh() {
        // do nothing
    }

    /**
     * Add an application configuration to this Configuration.
     * Package-private.
     **/
    void addAppConfig(String name, AppConfigurationEntry[] entries) {
        m_appConfigs.put(name, entries);
    }
}
