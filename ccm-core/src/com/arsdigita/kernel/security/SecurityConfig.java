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
package com.arsdigita.kernel.security;

import com.arsdigita.kernel.permissions.ObjectPermissionCollection;
import com.arsdigita.kernel.permissions.PermissionService;
import com.arsdigita.runtime.AbstractConfig;
import com.arsdigita.util.parameter.BooleanParameter;
import com.arsdigita.util.parameter.IntegerParameter;
import com.arsdigita.util.parameter.Parameter;
import com.arsdigita.util.parameter.StringArrayParameter;
import com.arsdigita.util.parameter.StringParameter;

/**
 * SecurityConfig
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #8 $ $Date: 2004/08/16 $
 **/

public class SecurityConfig extends AbstractConfig {

    private static String s_systemAdministratorEmailAddress = null;

    public final static String versionId = "$Id: SecurityConfig.java 1471 2007-03-12 11:27:55Z chrisgilbert23 $ by $Author: chrisgilbert23 $, $DateTime: 2004/08/16 18:10:38 $";

    private final Parameter m_rootPage       = new StringParameter
        ("waf.pagemap.root", Parameter.REQUIRED, "register/");
    private final Parameter m_loginPage      = new StringParameter
        ("waf.pagemap.login", Parameter.REQUIRED, "register/");
    private final Parameter m_newUserPage    = new StringParameter
        ("waf.pagemap.newuser", Parameter.REQUIRED, "register/new-user");
    private final Parameter m_logoutPage     = new StringParameter
        ("waf.pagemap.logout", Parameter.REQUIRED, "register/logout");
    private final Parameter m_cookiesPage    = new StringParameter
        ("waf.pagemap.cookies", Parameter.REQUIRED, "register/explain-persistent-cookies");
    private final Parameter m_changePage     = new StringParameter
        ("waf.pagemap.change", Parameter.REQUIRED, "register/change-password");
    private final Parameter m_recoverPage    = new StringParameter
        ("waf.pagemap.recover", Parameter.REQUIRED, "register/recover-password");
    private final Parameter m_expiredPage    = new StringParameter
        ("waf.pagemap.expired", Parameter.REQUIRED, "register/login-expired");
    private final Parameter m_workspacePage  = new StringParameter
        ("waf.pagemap.workspace", Parameter.REQUIRED, "pvt/");
    private final Parameter m_loginRedirectPage  = new StringParameter
        ("waf.pagemap.login_redirect", Parameter.REQUIRED, "pvt/");
    private final Parameter m_permissionPage = new StringParameter
        ("waf.pagemap.permission", Parameter.REQUIRED, "permissions/");
    private final Parameter m_permSinglePage = new StringParameter
        ("waf.pagemap.perm_single", Parameter.REQUIRED, "permissions/one");
    private final Parameter m_cookieDurationMinutes = new IntegerParameter
        ("waf.pagemap.cookies_duration_minutes", Parameter.OPTIONAL, null);
    private final Parameter m_cookieDomain = new StringParameter
        ("waf.cookie_domain", Parameter.OPTIONAL, null);
    private final Parameter m_loginConfig = new StringArrayParameter
        ("waf.login_config", Parameter.REQUIRED, new String[] {
                "Request:com.arsdigita.kernel.security.AdminLoginModule:sufficient",
                "Request:com.arsdigita.kernel.security.RecoveryLoginModule:sufficient",
                "Request:com.arsdigita.kernel.security.CookieLoginModule:requisite",
                "Register:com.arsdigita.kernel.security.LocalLoginModule:requisite",
                "Register:com.arsdigita.kernel.security.UserIDLoginModule:requisite",
                "Register:com.arsdigita.kernel.security.CookieLoginModule:optional",
                "RegisterSSO:com.arsdigita.kernel.security.SimpleSSOLoginModule:requisite",
                "RegisterSSO:com.arsdigita.kernel.security.CookieLoginModule:optional"
                }
        );
    private final Parameter m_adminEmail = new StringParameter
        ("waf.admin.contact_email", Parameter.OPTIONAL, null);
    private final Parameter m_autoRegistrationOn = new BooleanParameter
        ("waf.auto_registration_on", Parameter.REQUIRED, Boolean.TRUE);
    private final Parameter m_userBanOn = new BooleanParameter
        ("waf.user_ban_on", Parameter.REQUIRED, Boolean.FALSE);

    /** 
     * Constructs an empty SecurityConfig object
     */
    public SecurityConfig() {
        register(m_rootPage);
        register(m_loginPage);
        register(m_newUserPage);
        register(m_logoutPage);
        register(m_cookiesPage);
        register(m_changePage);
        register(m_recoverPage);
        register(m_expiredPage);
        register(m_workspacePage);
        register(m_loginRedirectPage);
        register(m_permissionPage);
        register(m_permSinglePage);
        register(m_cookieDomain);
        register(m_loginConfig);
        register(m_cookieDurationMinutes);
        register(m_adminEmail);
        register(m_autoRegistrationOn);
        register(m_userBanOn);

        loadInfo();
    }

    String getRootPage() {
        return (String) get(m_rootPage);
    }
    String getLoginPage() {
        return (String) get(m_loginPage);
    }
    String getNewUserPage() {
        return (String) get(m_newUserPage);
    }
    String getLogoutPage() {
        return (String) get(m_logoutPage);
    }
    String getCookiesPage() {
        return (String) get(m_cookiesPage);
    }
    String getChangePage() {
        return (String) get(m_changePage);
    }
    String getRecoverPage() {
        return (String) get(m_recoverPage);
    }
    String getExpiredPage() {
        return (String) get(m_expiredPage);
    }
    String getWorkspacePage() {
        return (String) get(m_workspacePage);
    }
    public String getLoginRedirectPage() {
        return (String) get(m_loginRedirectPage);
    }
    String getPermissionPage() {
        return (String) get(m_permissionPage);
    }
    String getPermSinglePage() {
        return (String) get(m_permSinglePage);
    }
    public String getCookieDomain() {
        return (String) get(m_cookieDomain);
    }
    String[] getLoginConfig() {
        return (String[]) get(m_loginConfig);
    }
    Integer getCookieDurationMinutes() {
        return (Integer) get(m_cookieDurationMinutes);
    }
    boolean isUserBanOn() {
        return ((Boolean) get(m_userBanOn)).booleanValue();
    }
    
    public String getAdminContactEmail() {
        String email = (String) get(m_adminEmail);
        if (email == null  ||  email.trim().length() == 0) {
            email = getSystemAdministratorEmailAddress();
        }
        return email;
    }

    private static synchronized String getSystemAdministratorEmailAddress() {
        if (s_systemAdministratorEmailAddress == null) {
            ObjectPermissionCollection perms = PermissionService.getGrantedUniversalPermissions();
            perms.addEqualsFilter("granteeIsUser", Boolean.TRUE);
            perms.clearOrder();
            perms.addOrder("granteeID");
            if (perms.next()) {
                s_systemAdministratorEmailAddress = perms.getGranteeEmail().toString();
                perms.close();
            } else {
                // Haven't found anything.  We don't want to repeat this query
                // over and over again.
                s_systemAdministratorEmailAddress = "";
            }
        }
        return s_systemAdministratorEmailAddress;
    }

    public final boolean isAutoRegistrationOn() {
        return ((Boolean) get(m_autoRegistrationOn)).booleanValue();
    }

}
