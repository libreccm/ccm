/*
 * Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.web;

import com.arsdigita.domain.DomainCollection;
import com.arsdigita.runtime.AbstractConfig;
import com.arsdigita.util.parameter.BooleanParameter;
import com.arsdigita.util.parameter.EnumerationParameter;
import com.arsdigita.util.parameter.ErrorList;
import com.arsdigita.util.parameter.Parameter;
import com.arsdigita.util.parameter.ParameterError;
import com.arsdigita.util.parameter.SingletonParameter;
import com.arsdigita.util.parameter.StringArrayParameter;
import com.arsdigita.util.parameter.StringParameter;
import com.arsdigita.util.servlet.HttpHost;
import com.arsdigita.util.servlet.HttpHostParameter;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * A record containing server-session scoped configuration properties.
 *
 * Accessors of this class may return null.  Developers should take
 * care to trap null return values in their code.
 *
 * @see com.arsdigita.web.Web
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: WebConfig.java 1548 2007-03-29 14:49:49Z chrisgilbert23 $
 */
public final class WebConfig extends AbstractConfig {
    public static final String versionId =
        "$Id: WebConfig.java 1548 2007-03-29 14:49:49Z chrisgilbert23 $" +
        "$Author: chrisgilbert23 $" +
        "$DateTime: 2004/08/16 18:10:38 $";

    private static final Logger s_log = Logger.getLogger(WebConfig.class);

    private final Parameter m_scheme;
    private final Parameter m_server;
    private final Parameter m_secureServer;
    private final Parameter m_host;
    private final Parameter m_site;
    private final Parameter m_context;
    private final Parameter m_servlet;
    private final Parameter m_policy;
    private final Parameter m_resolver;
    private final Parameter m_dynamic_host_provider;
    private final Parameter m_deactivate_cache_host_notifications;
    private final Parameter m_secureRequired;

    public WebConfig() {
        m_scheme = new DefaultSchemeParameter
            ("waf.web.default_scheme", Parameter.REQUIRED, "http");

        m_server = new HttpHostParameter("waf.web.server");

        m_secureServer = new HttpHostParameter
        	("waf.web.secure_server", Parameter.OPTIONAL, null);

        m_host = new HttpHostParameter
            ("waf.web.host", Parameter.OPTIONAL, null) {
                public final Object getDefaultValue() {
                    return getServer();
                }
            };

        m_site = new StringParameter
            ("waf.web.site_name", Parameter.OPTIONAL, null) {
                public final Object getDefaultValue() {
                    final HttpHost host = getServer();

                    if (host == null) {
                        return null;
                    } else {
                        return host.toString();
                    }
                }
            };

        m_context = new StringParameter
            ("waf.web.dispatcher_context_path", Parameter.REQUIRED, "");

        m_servlet = new StringParameter
            ("waf.web.dispatcher_servlet_path", Parameter.REQUIRED, "/ccm");

        m_policy = new CachePolicyParameter
            ("waf.web.cache_policy", Parameter.OPTIONAL, null);

        m_resolver = new SingletonParameter
            ("waf.web.application_file_resolver",
             Parameter.OPTIONAL,
             new DefaultApplicationFileResolver());

        m_secureRequired = new StringArrayParameter(
                "waf.web.secure_required", Parameter.OPTIONAL, null);

        m_dynamic_host_provider = new StringParameter
            ("waf.web.dynamic_host_provider", Parameter.OPTIONAL, "");

        m_deactivate_cache_host_notifications = new BooleanParameter
            ("waf.web.deactivate_cache_host_notifications", Parameter.OPTIONAL, Boolean.FALSE);

        register(m_scheme);
        register(m_server);
        register(m_secureServer);
        register(m_host);
        register(m_site);
        register(m_context);
        register(m_servlet);
        register(m_policy);
        register(m_resolver);
        register(m_dynamic_host_provider);
        register(m_deactivate_cache_host_notifications);
        register(m_secureRequired);

        loadInfo();
    }

    public final String getDefaultScheme() {
        return (String) get(m_scheme);
    }

    public final HttpHost getServer() {
        return (HttpHost) get(m_server);
    }

    public final HttpHost getSecureServer() {
        return (HttpHost) get(m_secureServer);
    }
    
    public final boolean isSecureRequired(String uri) {
        String[] secured = (String[])get(m_secureRequired);
        if (secured != null) {
            for (int i=0, n=secured.length; i<n; i++) {
                if (uri.startsWith(secured[i])) {
                    return true;
                }
            }
        }
        return false;
    }

    public final HttpHost getHost() {
        return (HttpHost) get(m_host);
    }

    final void setHost(final HttpHost host) {
        set(m_host, host);
    }

    public final String getSiteName() {
        return (String) get(m_site);
    }

    public final String getDispatcherContextPath() {
        return (String) get(m_context);
    }

    public final String getDispatcherServletPath() {
        return (String) get(m_servlet);
    }

    public final ApplicationFileResolver getApplicationFileResolver() {
        return (ApplicationFileResolver) get(m_resolver);
    }

    /**
     * Gets the system default cache policy.  This value is set via
     * the <code>com.arsdigita.web.cache_policy</code> system property
     * using one fo the following values: <code>user</code> for
     * per-user caching, <code>world</code> for globally enabled
     * caching, and <code>disable</code> to always prevent caching.
     */
    public final CachePolicy getCachePolicy() {
        return (CachePolicy) get(m_policy);
    }

    private static class DispatcherServletPathParameter
            extends StringParameter {
        DispatcherServletPathParameter(final String name) {
            super(name);
        }

        protected void doValidate(final Object value, final ErrorList errors) {
            final String string = (String) value;

            if (string.endsWith("/")) {
                final ParameterError error = new ParameterError
                    (this, "The value must not end in a '/'");
                errors.add(error);
            }
        }
    }

    private static class DefaultSchemeParameter extends EnumerationParameter {
        DefaultSchemeParameter(final String name,
                               final int multiplicity,
                               final Object defaalt) {
            super(name, multiplicity, defaalt);

            put("http", "http");
            put("https", "https");
        }
    }

    private static class CachePolicyParameter extends EnumerationParameter {
        CachePolicyParameter(final String name,
                             final int multiplicity,
                             final Object defaalt) {
            super(name, multiplicity, defaalt);

            put("none", null);
            put("disable", CachePolicy.DISABLE);
            put("user", CachePolicy.USER);
            put("world", CachePolicy.WORLD);
        }
    }

    protected DynamicHostProvider dhProvider = null;
    protected boolean dhProviderInited = false;

    public final DynamicHostProvider getDynamicHostProvider() {
        if (dhProviderInited == false) {
            String classname = (String) get(m_dynamic_host_provider);
            if (classname != null) {
                try {
                    Class klass = Class.forName(classname);
                    dhProvider = (DynamicHostProvider) klass.newInstance();
                } catch (Exception e) {
                    s_log.error("Could not instantiate DynamicHostProvider using classname : "+classname, e);
                }
            }
            dhProviderInited = true;
        }
        return dhProvider;
    }

    public final boolean getDeactivateCacheHostNotifications() {
        return ((Boolean) get(m_deactivate_cache_host_notifications)).booleanValue();
    }

    //
    // Deprecated classes and methods
    //

    /**
     * @deprecated Use <code>getServer().getName()</code> instead.
     */
    public final String getServerName() {
        return getServer().getName();
    }

    /**
     * @deprecated Use <code>getServer().getPort()</code> instead.
     */
    public final int getServerPort() {
        return getServer().getPort();
    }

    /**
     * @deprecated Use
     * <code>Host.retrieve(Web.getConfig().getHost())</code> instead.
     */
    public final Host getCurrentHost() {
        return Host.retrieve(Web.getConfig().getHost());
    }

    /**
     * @deprecated Use <code>Host.retrieveAll()</code> instead.
     */
    public final Host[] getHosts() {
        final List hosts = new ArrayList();
        final DomainCollection coll = Host.retrieveAll();

        while (coll.next()) {
            hosts.add((Host) coll.getDomainObject());
        }

        coll.close();

        return (Host[]) hosts.toArray(new Host[hosts.size()]);
    }
}
