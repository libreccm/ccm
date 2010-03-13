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
package com.arsdigita.developersupport;

import com.arsdigita.util.Assert;
import com.arsdigita.util.UncheckedWrapperException;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * This factory produces {@link Proxy dynamic proxies} that can log and delegate
 * all method calls to the object for which they are proxying.
 *
 * <p>After instantiating a factory instance, you can configure it via methods
 * like {@link #setLogger(String)} and {@link #setLevel(String)}. Once
 * configured, the factory can produce, via its {@link #newLoggingProxy(Object,
 * Class)} method, dynamic proxy objects implementing the required
 * interface.</p>
 *
 * @author  Vadim Nasardinov (vadimn@redhat.com)
 * @since   2003-06-17
 * @version $Revision: #6 $ $Date: 2004/08/16 $
 **/
public final class LoggingProxyFactory implements LoggerConfigurator {

    private Config m_config;

    public LoggingProxyFactory() {
        m_config = new Config();
    }

    /**
     * Sets the logger that will be used to log method calls.
     *
     * @see #setLevel(String)
     **/
    public void setLogger(String logger) {
        m_config.setLogger(logger);
    }

    /**
     * <p>Sets the logging level for the logger. If you don't ever call this
     * method to specify the logging level explicitly, it will default to
     * <code>"debug"</code>. If you specify an invalid logging level, the
     * default value of <code>"debug"</code> will be substituted silently. </p>
     *
     * @see #setLogger(String)
     **/
    public void setLevel(String level) {
        m_config.setLevel(level);
    }

    /**
     * Call this method to enable the logging of a stack trace for each
     * intercepted method call.
     **/
    public void enableStackTraces() {
        m_config.enableStackTraces();
    }

    /**
     * Returns an object that implements the <code>iface</code> and {@link
     * LoggingProxy} interfaces.
     *
     *
     * <p>The object works by delegating all method calls to
     * <code>proxiedObject</code>.  Method calls are logged on entry using the
     * logger specified via {@link #setLogger(String)}.  If no logger was
     * specified, <code>Logger.getLogger(LoggingProxyFactory.class)</code> is
     * used. Example: </p>
     *
     * <blockquote><pre>
     * protected void service(HttpServletRequest req, HttpServletResponse resp) {
     *     LoggingProxyFactory factory = new LoggingProxyFactory();
     *     factory.enableStackTraces();
     *     factory.setLevel("info");
     *     HttpServletRequest reqProxy = (HttpServletRequest)
     *         factory.newLoggingProxy(req, HttpServletRequest.class, true);
     *     doStuff((HttpServletRequest) reqProxy, resp)
     * }
     *
     * private void doStuff(HttpServletRequest req, HttpServletResponse resp) {
     *     // ...
     *     
     *     if ( Proxy.isProxyClass(req.getClass()) ) {
     *         LoggingProxy proxy = (LoggingProxy) req;
     *         HttpServletRequest realReq = (HttpServletRequest) req.getProxiedObject();
     *         // do stuff with the original, unwrapped request.
     *     }
     * }
     * </pre></blockquote>
     *
     * <p>This allows you to track all calls to the HTTP request object that are
     * made in your program during the execution of the <code>service(req,
     * resp)</code>. </p>
     *
     * @see #newLoggingProxy(Object, Class)
     *
     * @pre proxiedObject != null
     * @pre iface != null
     * @pre iface.isInstance(proxiedObject)
     * @post iface.isInstance(return)
     *
     * @param proxiedObject the proxied object
     * @param iface the interface to be implemented by the returned dynamic proxy
     * @param configurable if <code>true</code>, the returned object will also
     * implement the {@link LoggingProxy} interface. Note that requiring the
     * logging proxy to implement this additional interface may cause an {@link
     * IllegalArgumentException} due to the fact the specified interface is not
     * accessible to the classloader with which <code>proxiedObject</code> was
     * instantiated. See also {@link #newLoggingProxy(Object, Class)}.
     * 
     * @return an object that implements the interface <code>iface</code>. The
     * returned object will be created by the same classloader with which
     * <code>proxiedObject</code> was created.
     **/
    public Object newLoggingProxy(Object proxiedObject, Class iface,
                                  boolean configurable) {

        Assert.exists(iface, Class.class);
        Assert.isTrue(iface.isInstance(proxiedObject),
                     "proxiedObject is instance of iface");

        Class[] ifaces = configurable ?
            new Class[] {iface, LoggingProxy.class} : new Class[] {iface};

        return Proxy.newProxyInstance
            (getClass().getClassLoader(),
             ifaces,
             new Handler(m_config, proxiedObject));
             
    }


    /**
     * This is equivalent to <code>{@link #newLoggingProxy(Object, Class,
     * boolean) newLoggingProxy(proxiedObject, iface, false)}</code>. In other
     * words, the returned proxy does <em>not</em> implement the {@link
     * LoggingProxy} interface.
     *
     * @see #newLoggingProxy(Object, Class, boolean)
     **/
    public Object newLoggingProxy(Object proxiedObject, Class iface) {
        return newLoggingProxy(proxiedObject, iface, false);
    }

    private static class Config implements LoggerConfigurator {
        private Logger m_log;
        private Level m_level;
        private boolean m_logStacks;
        private Throwable m_stack;

        public Config() {
            m_log = Logger.getLogger(LoggingProxyFactory.class);
            m_level = Level.DEBUG;
            m_stack = new Throwable();
        }

        public void setLogger(String log) {
            Assert.exists(log, String.class);
            m_log = Logger.getLogger(log);
        }

        public void setLevel(String level) {
            Assert.exists(level, String.class);
            m_level = Level.toLevel(level);
        }

        public void enableStackTraces() {
            m_logStacks = true;
        }

        public Config copy() {
            Config copy = new Config();
            copy.m_log = m_log;
            copy.m_level = m_level;
            copy.m_logStacks = m_logStacks;
            copy.m_stack = new Throwable();
            return copy;
        }

        public void log(Method method, Object args[]) {
            StringBuffer sb = new StringBuffer(100);
            sb.append("invoked ").append(method.getDeclaringClass().getName());
            sb.append(".").append(method.getName());
            if ( m_logStacks ) {
                m_stack.fillInStackTrace();
                m_log.log(m_level, sb, m_stack);
            } else {
                m_log.log(m_level, sb);
            }
        }

        public void log(Object msg) {
            System.err.println(msg);
            if ( m_logStacks ) {
                m_stack.fillInStackTrace();
                m_log.log(m_level, msg, m_stack);
            } else {
                m_log.log(m_level, msg);
            }
        }
    }

    private static class Handler implements InvocationHandler {
        private static final Method s_getProxiedObject;

        static {
            try {
                s_getProxiedObject = 
                    LoggingProxy.class.getMethod("getProxiedObject",
                                                 new Class[] {});
            } catch (NoSuchMethodException ex) {
                throw new UncheckedWrapperException("failed", ex);
            }
        }

        private Config m_config;
        private Object m_proxiedObject;
        private boolean m_configCloned;

        public Handler(Config config, Object obj) {
            m_config = config;
            m_proxiedObject = obj;
        }

        public Object invoke(Object proxy, Method method, Object[] args)
            throws Throwable {

            if ( method.equals(s_getProxiedObject) ) {
                return m_proxiedObject;
            }

            if ( LoggerConfigurator.class.equals(method.getDeclaringClass()) ) {
                if ( !m_configCloned ) {
                    m_config = m_config.copy();
                    m_configCloned = true;
                }

                // m_config.log("returning proxied object");
                return method.invoke(m_config, args);
            }

            m_config.log(method, args);
            return method.invoke(m_proxiedObject, args);
        }
    }
}
