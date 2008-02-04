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
package com.arsdigita.initializer;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * This class is used by the initialization system to pass configuration
 * parameters to an initializer. Every initializer should contain a member
 * variable with exactly one instance of this class. The initializer should
 * instantiate this class in the constructor and set the types and optionally
 * set default values for each parameter the initializer requires.
 *
 *  <blockquote><pre>
 *  public class MyInitializer implements Initializer {
 *
 *      Configuration m_config = new Configuration();
 *
 *      public MyInitializer() throws InitializationException {
 *          m_config.initParameter("stringParam", "This is a usage string.",
 *                                 String.class,"This is a string.");
 *          m_config.initParameter("intParam",
 *                                 "Please enter a value for the intParam.")
 *          m_config.initParameter("listParam", "Should be a list.",
 *                                 java.util.List.class, new ArrayList());
 *      }
 *
 *      public Configuration getConfiguration() {
 *          return m_config;
 *      }
 *
 *      public void startup() {
 *          // Run startup code here.
 *      }
 *
 *      public void shutdown() {
 *          // Run shutdown code here.
 *      }
 *
 *  }
 *  </pre></blockquote>
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #10 $ $Date: 2004/08/16 $
 */

public class Configuration {

    public final static String versionId = "$Id: Configuration.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    Map m_parameters = new HashMap();
    Map m_types = new HashMap();
    Map m_usage = new HashMap();

    /**
     * Initializes the parameter to the default value.
     *
     * @param name The parameter name.
     * @param usage Documentation on the parameter usage.
     * @param type The parameter type.
     * @param defaultValue The parameter default value.
     **/

    public void initParameter(String name, String usage,
                              Class type, Object defaultValue)
        throws InitializationException {
        if (m_parameters.containsKey(name))
            throw new InitializationException("Parameter " + name + " already defined.");
        m_types.put(name, type);
        typeCheck(name, defaultValue);
        m_parameters.put(name, defaultValue);
        m_usage.put(name, usage);
    }

    /**
     * Initializes the parameter, passing in null as the default value.
     *
     * @param name The parameter name.
     * @param usage Documentation on the parameter usage.
     * @param type The parameter type.
     **/

    public void initParameter(String name, String usage, Class type)
        throws InitializationException {
        initParameter(name, usage, type, null);
    }

    private void paramCheck(String name) throws InitializationException {
        if (!m_parameters.containsKey(name))
            throw new InitializationException(
                                              "No such parameter: " + name + ", legal parameters are: " +
                                              getParameterNames()
                                              );
    }

    private void typeCheck(String name, Object value)
        throws InitializationException {
        if (value == null)
            return;
        Class cls = (Class) m_types.get(name);
        if (!cls.isInstance(value))
            throw new InitializationException(
                                              "Parameter " + name + " must be of type " + cls.getName()
                                              );
    }

    /**
     * Sets the parameter specified by <i>name</i> to the <i>value</i>
     *
     * @param name The parameter name.
     * @param value The parameter value.
     **/

    public void setParameter(String name, Object value)
        throws InitializationException {
        //paramCheck(name);
        //typeCheck(name, value);
        m_parameters.put(name, value);
    }

    /**
     * Returns the parameter value for the parameter specified by <i>name</i>.
     *
     * @param name The parameter name.
     *
     * @return The parameter value.
     **/

    public Object getParameter(String name) throws InitializationException {
        //paramCheck(name);
        return m_parameters.get(name);
    }

    /**
     * Returns true if this configuration has the specified parameter.
     *
     * @param name The parameter.
     *
     * @return True if the configuration contains the parameter,
     *         false otherwise.
     **/

    public boolean hasParameter(String name) {
        return m_parameters.containsKey(name);
    }

    /**
     * Returns the legal parameter names for this Configuration object.
     **/

    public Set getParameterNames() {
        return m_parameters.keySet();
    }

    public String toString() {
        return "<configuration parameters: " + m_parameters + ">";
    }

}
