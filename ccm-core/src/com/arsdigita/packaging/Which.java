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

import com.arsdigita.util.UncheckedWrapperException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.log4j.Logger;

/**
 * Subject to change.
 *
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: Which.java 736 2005-09-01 10:46:05Z sskracic $
 */
class Which extends Command {
    public static final String versionId =
        "$Id: Which.java 736 2005-09-01 10:46:05Z sskracic $" +
        "$Author: sskracic $" +
        "$DateTime: 2004/08/16 18:10:38 $";

    private static final Logger s_log = Logger.getLogger(Which.class);

    private static final Options OPTIONS = getOptions();

    public Which() {
        super("which", "Find a resource or class in the CCM classpath");
    }

    // XXX The following two methods are duplicated from Checklist.
    // Need to find a good place for this functionality.

    private static List getClassURLs(final String classname) {
        return getResourceURLs(classname.replace('.', '/') + ".class");
    }

    private static List getResourceURLs(final String resource) {
        final ArrayList list = new ArrayList();

        try {
            final Enumeration resources = Thread.currentThread
                ().getContextClassLoader().getResources(resource);

            while (resources.hasMoreElements()) {
                list.add(resources.nextElement());
            }
        } catch (IOException ioe) {
            throw new UncheckedWrapperException(ioe);
        }

        return list;
    }

    public boolean run(final String[] args) {
        CommandLine line;
        try {
            line = new PosixParser().parse(OPTIONS, args);
        } catch (ParseException e) {
            System.err.println(e.getMessage());
            return false;
        }

        if (line.hasOption("usage")) {
            usage(OPTIONS, System.out, "RESOURCES");
            return true;
        }

        String[] names = line.getArgs();

        if (names.length == 0) {
            usage(OPTIONS, System.err, "RESOURCES");
            return false;
        }

        for (int i = 0; i < names.length; i++) {
            String name = names[i];
            final Iterator classes = getClassURLs(name).iterator();

            while (classes.hasNext()) {
                System.out.println(classes.next());
            }

            final Iterator resources = getResourceURLs(name).iterator();

            while (resources.hasNext()) {
                System.out.println(resources.next());
            }
        }

        return true;
    }

}
