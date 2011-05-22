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

/**
 * OLD INITIALIZATION SYSTEM - DEPRECATED
 * 
 * Any class implementing this interface may appear in an initialization
 * script read in by the Script class. A class that does this should construct
 * its own configuration object and initialize the parameters with the
 * appropriate types and default values. This object should be returned by the
 * getConfiguration() method. This configuration object will then be filled
 * out by the Script class with whatever values appear in the initialization
 * script.
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
 * The following syntax may then be used in an initialization script:
 *
 *  <blockquote><pre>
 *  init MyInitializer {
 *      stringParam = "foo";
 *      intParam = 3;
 *      listParam = { "foo", "bar", "baz" };
 *  }
 *  </pre></blockquote>
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Id: Initializer.java 287 2005-02-22 00:29:02Z sskracic $
 */

public interface Initializer {

    /**
     * Returns the configuration object used by this initializer.
     */
    Configuration getConfiguration();

    /**
     * Called on startup.
     */
    void startup() throws InitializationException;

    /**
     * Called on shutdown. It's probably not a good idea to depend on this
     * being called.
     */
    void shutdown()throws InitializationException;

}
