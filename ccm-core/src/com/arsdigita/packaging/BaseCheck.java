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

// import com.arsdigita.packaging.Check;
import com.arsdigita.util.Assert;
import com.arsdigita.util.UncheckedWrapperException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Logger;

/**
 * BaseCheck: Extension of the Check abstract class, which can be used as a
 * starting point by packages which have to perform additional checks during
 * the installation step. (@see Checklist machinery)
 *
 * Provides some methods to print out classes and classnames, which can be
 * used in the run() method of derived classes.
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #4 $ $Date: 2004/08/16 $
 * @version $Id: BaseCheck.java 736 2005-09-01 10:46:05Z sskracic 
 */
abstract public class BaseCheck extends Check {

    private static final Logger logger = Logger.getLogger(BaseCheck.class);

    public static final MessageMap s_messages = new MessageMap();

    static {
        logger.debug("Static initalizer starting...");
        final InputStream in = BaseCheck.class.getResourceAsStream
            ("basecheck.messages_linux");

        Assert.exists(in, InputStream.class);

        s_messages.load(new InputStreamReader(in));
        logger.debug("Static initalizer finished.");
    }

    public static String message(final String key) {
        Assert.exists(key, String.class);

        return s_messages.get(key);
    }

    public PrintStream m_out = System.out;
    ClassLoader m_loader =
        Thread.currentThread().getContextClassLoader();
    boolean m_verbose = false;

    /**
     * Prints the ...
     * 
     * @param classname
     */
    void printClassLocations(final String classname) {
        final Iterator urls = getClassURLs(classname).iterator();

        while (urls.hasNext()) {
            final String url = ((URL) urls.next()).toString();

            final int end = url.indexOf("!");

            if (end != -1) {
                if (url.startsWith("jar:file:")) {
                    m_out.println("    " + url.substring(9, end));
                } else {
                    m_out.println("    " + url.substring(0, end));
                }
            } else {
                m_out.println("    " + url);
            }
        }
    }

    /**
     * 
     * @param classname
     * @return
     */
    List getClassURLs(final String classname) {
        return getResourceURLs(classname.replace('.', '/') + ".class");
    }

    /**
     * 
     * @param resource
     * @return
     */
    List getResourceURLs(final String resource) {
        final ArrayList list = new ArrayList();

        try {
            final Enumeration resources = m_loader.getResources(resource);

            while (resources.hasMoreElements()) {
                list.add(resources.nextElement());
            }
        } catch (IOException ioe) {
            throw new UncheckedWrapperException(ioe);
        }

        return list;
    }

    /**
     * 
     * @param classname
     * @return
     */
    public boolean isClassFound(final String classname) {
        if (m_verbose) {
            m_out.println("Looking for class " + classname);
        }

        try {
            final Class clacc = m_loader.loadClass(classname);

            if (m_verbose) {
                m_out.println
                    ("Found class at " + getClassURLs(classname).get(0));
            }

            return true;
        } catch (ClassNotFoundException nfe) {
            m_out.println("Class not found");

            return false;
        }
    }

    /**
     * 
     * @param classname
     */
    public void checkDuplicates(final String classname) {
        if (m_verbose) {
            m_out.println("Checking for duplicates of " + classname);
        }

        if (getClassURLs(classname).size() > 1) {
            if (m_verbose) {
                m_out.println("Duplicates found");
            }

            m_out.println(message("duplicate_classes"));
            m_out.println();
            printClassLocations(classname);
        } else {
            if (m_verbose) {
                m_out.println("No duplicates");
            }
        }
    }

}
