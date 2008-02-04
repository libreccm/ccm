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
package com.arsdigita.bebop.demo;

import com.arsdigita.bebop.BebopMapDispatcher;
import com.arsdigita.bebop.Link;
import com.arsdigita.bebop.Page;
import com.arsdigita.util.UncheckedWrapperException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import org.apache.log4j.Logger;


/**
 * A dispatcher that looks for "buildPage*" methods, invokes them,
 * and constructs an index page with links automatically.
 *
 * */
public class AutoDispatcher extends BebopMapDispatcher {

    public static final String versionId = "$Id: AutoDispatcher.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    private static final Logger s_log =
        Logger.getLogger(AutoDispatcher.class);

    public AutoDispatcher() {
        setUp();                // set up map & index
    }

    /** Set up map by reflection and index page.
     *  For use from derived classes; 'this' indicates where builPage_* are.
     */
    protected void setUp()
    {
        setUp(this);
    }

    /**
     * The constructor Instantiates the subsite url->page mapping.
     * Unlike a typical Dispatcher constrctor, this one invokes any
     * method starting in "buildPage" in the class of the child and puts the
     * returned page in the map.  Also creates an index page with
     * links to all pages in the map.  This makes adding another page
     * as easy as writing its build method; the rest is automated.  */
    public void setUp(Object child) {
        Map m = new TreeMap();

        final String prefix = "buildPage";
        Method method[] = child.getClass().getDeclaredMethods();
        for (int i = 0; i < method.length; i++) {
            String name = method[i].getName();
            if (name.startsWith(prefix)) {
                try {
                    m.put(name.substring(prefix.length()),
                          method[i].invoke(null, new Object[0]));
                } catch (Exception ex) {
                    throw new UncheckedWrapperException(name, ex);
                }
            }
        }

        // pages added above will show up in the index
        Page index = buildIndexPage(m.entrySet().iterator(),
                                    "Index for " + child.getClass().getName());
        m.put("", index);
        m.put("index", index);
        // any pages added below won't be listed.

        setMap(m);
    }

    /**
     * @return a Bebop page for the index
     */
    private static Page buildIndexPage(Iterator entries, String title) {
        Page p = new Page(title);
        while (entries.hasNext()) {
            Map.Entry entry = (Map.Entry) entries.next();
            p.add(
                  new Link(
                           ((Page) entry.getValue()).getTitle(),
                           (String) entry.getKey()
                           )
                  );
        }

        p.lock();
        return p;
    }
}
