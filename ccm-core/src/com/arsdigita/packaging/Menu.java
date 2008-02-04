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
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Menu
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #6 $ $Date: 2004/08/16 $
 **/

class Menu {

    public final static String versionId = "$Id: Menu.java 736 2005-09-01 10:46:05Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    static final class Layout {
        private Layout() {}
    }

    public static final Layout VERTICAL = new Layout();
    public static final Layout HORIZONTAL = new Layout();

    static interface Item {

        String getDescription();

        void act();

    }

    public static final Item GAP = new Item() {
        public String getDescription() {
            return null;
        }
        public void act() {}
    };


    private List m_entries;
    private Map m_keys;
    private BufferedReader m_in;
    private PrintStream m_out;
    private int m_padd;

    public Menu(BufferedReader in, PrintStream out) {
        m_entries = new ArrayList();
        m_keys = new HashMap();
        m_in = in;
        m_out = out;
        m_padd = 0;
    }

    public void add(Item item) {
        add(null, item);
    }

    public void add(String key, Item item) {
        add(key, item, VERTICAL);
    }

    public void add(String key, Item item, Layout layout) {
        Entry entry = new Entry(key, item, layout);
        m_entries.add(entry);
        if (key != null) {
            m_keys.put(key, entry);
            int length = key.length();
            if (length > m_padd) {
                m_padd = length;
            }
        }
    }

    public void display() {
        boolean first = true;
        int column = 0;

        for (Iterator it = m_entries.iterator(); it.hasNext(); ) {
            Entry entry = (Entry) it.next();
            Item item = entry.getItem();
            String key = entry.getKey();
            Layout layout = entry.getLayout();

            StringBuffer buf = new StringBuffer();

            if (key != null) {
                for (int i = 0; i < m_padd + 2 - key.length(); i++) {
                    buf.append(" ");
                }
                buf.append("[" + key + "]");
            }

            String description = item.getDescription();
            if (description != null) {
                buf.append(" " + description);
            }

            if ((!first && layout == VERTICAL)
                || column + buf.length() > 79) {
                m_out.println();
                column = 0;
            }

            m_out.print(buf.toString());
            column += buf.length();
            if (first) { first = false; }
        }

        m_out.println();
    }

    public Item choose(String prompt) {
        while (true) {
            m_out.print(prompt);
            m_out.flush();
            String line;
            try {
                line = m_in.readLine();
            } catch (IOException e) {
                throw new UncheckedWrapperException(e);
            }
            if (line == null) { return null; }
            line = line.trim();
            if (line.equals("")) { continue; }
            Entry entry = (Entry) m_keys.get(line);
            if (entry == null) {
                m_out.println("  * no such item: " + line);
            } else {
                return entry.getItem();
            }
        }
    }

    private class Entry {

        private String m_key;
        private Item m_item;
        private Layout m_layout;

        public Entry(String key, Item item, Layout layout) {
            m_key = key;
            m_item = item;
            m_layout = layout;
        }

        public String getKey() {
            return m_key;
        }

        public Item getItem() {
            return m_item;
        }

        public Layout getLayout() {
            return m_layout;
        }

    }

}
