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

import com.arsdigita.bebop.page.BebopMapDispatcher;
import com.arsdigita.bebop.Page;
import java.util.HashMap;

/**
 * Minimal dispatcher class.  Demonstrates building a page using Bebop.
 *
 */
public class MinimalDispatcher extends BebopMapDispatcher {


    /** Constructor.  Instantiates the subsite url/page mapping.  */
    public MinimalDispatcher() {
        HashMap m = new HashMap();
        m.put("hello", buildPagePage());
        setMap(m);
    }

    /** build a page demonstrating bebop.Page */
    private static Page buildPagePage() {
        Page page = new Page("Hello, World !");
        page.lock();
        return page;
    }
}
