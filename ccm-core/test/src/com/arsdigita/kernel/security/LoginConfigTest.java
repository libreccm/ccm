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
import java.util.Arrays;
import java.util.List;
import javax.security.auth.login.AppConfigurationEntry;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class LoginConfigTest extends TestCase {

    public LoginConfigTest(String name) {
        super(name);
    }
    public static Test suite() {
        try {
            return new TestSuite(LoginConfigTest.class);
        } catch (final Throwable t) {
            // handles NoClassDefFoundError
            // and ExceptionInInitializerError
            return new TestCase("Create CredentialTest") {
                    public void runTest() throws Throwable {
                        throw t;
                    }
                };
        }
    }
    private List m_list;
    protected void setUp() {
        List mod1 = Arrays.asList(new Object[] {
            "mod1", "required" });
        List mod2 = Arrays.asList(new Object[] {
            "mod2", "requisite", "opt1=1" });
        List mod3 = Arrays.asList(new Object[] {
            "mod3", "sufficient", "opt2=2", "opt2=3" });
        List mod4 = Arrays.asList(new Object[] {
            "mod4", "optional", "opt3=3", "opt4=", "=5" });
        List app1Mods = Arrays.asList(new Object[] { });
        List app2Mods = Arrays.asList(new Object[] {
            mod1, mod2, mod3, mod4
        });
        m_list = Arrays.asList(new Object[] {
            "app1", app1Mods, "app2", app2Mods
        });
    }
    protected void tearDown() {
        m_list = null;
    }

    public void testDefault() {
        LoginConfig conf = null;
        try {
            conf = new LoginConfig(m_list);
        } catch (InitializationException e) {
            fail(e.toString());
        }
        assertNull("app0", conf.getAppConfigurationEntry("app0"));

        AppConfigurationEntry[] app1 =
            conf.getAppConfigurationEntry("app1");
        assertNotNull("app1", app1);
        assertEquals("number of entries for app1", 0, app1.length);

        AppConfigurationEntry[] app2 =
            conf.getAppConfigurationEntry("app2");
        assertNotNull("app2", app2);
        assertEquals("number of entries for app2", 4, app2.length);

        AppConfigurationEntry mod1 = app2[0];
        assertEquals("mod1 name", "mod1", mod1.getLoginModuleName());
        assertEquals("mod1 flag",
                     AppConfigurationEntry.LoginModuleControlFlag.REQUIRED,
                     mod1.getControlFlag());
        assertEquals("mod1 num options", 0,
                     mod1.getOptions().keySet().size());

        AppConfigurationEntry mod2 = app2[1];
        assertEquals("mod2 name", "mod2", mod2.getLoginModuleName());
        assertEquals("mod2 flag",
                     AppConfigurationEntry.LoginModuleControlFlag.REQUISITE,
                     mod2.getControlFlag());
        assertEquals("mod2 num options", 1,
                     mod2.getOptions().keySet().size());
        assertEquals("mod2 option opt1", "1",
                     mod2.getOptions().get("opt1"));

        AppConfigurationEntry mod3 = app2[2];
        assertEquals("mod3 name", "mod3", mod3.getLoginModuleName());
        assertEquals("mod3 flag",
                     AppConfigurationEntry.LoginModuleControlFlag.SUFFICIENT,
                     mod3.getControlFlag());
        assertEquals("mod3 num options", 1,
                     mod3.getOptions().keySet().size());
        assertEquals("mod3 option opt2", "3",
                     mod3.getOptions().get("opt2"));

        AppConfigurationEntry mod4 = app2[3];
        assertEquals("mod4 name", "mod4", mod4.getLoginModuleName());
        assertEquals("mod4 flag",
                     AppConfigurationEntry.LoginModuleControlFlag.OPTIONAL,
                     mod4.getControlFlag());
        assertEquals("mod4 num options", 3,
                     mod4.getOptions().keySet().size());
        assertEquals("mod4 option opt3", "3",
                     mod4.getOptions().get("opt3"));
        assertEquals("mod4 option opt4", "",
                     mod4.getOptions().get("opt4"));
        assertEquals("mod4 option \"\"", "5",
                     mod4.getOptions().get(""));
    }
}
