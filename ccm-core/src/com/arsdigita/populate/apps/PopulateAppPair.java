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
package com.arsdigita.populate.apps;

import java.util.List;

import com.arsdigita.util.Assert;

/**
 * @author bche
 */
public class PopulateAppPair {
    private PopulateApp m_popApp;
    private List m_args;
    
    public PopulateAppPair(List popAppPair) {
        Assert.assertTrue(popAppPair.size() == 2);
        m_popApp = (PopulateApp)popAppPair.get(0);
        m_args = (List)popAppPair.get(1);
    }
    
    public PopulateAppPair(PopulateApp popApp, List args) {
        m_popApp = popApp;
        m_args = args;
    }
    
    public PopulateApp getPopulateApp() {
        return m_popApp;
    }
    
    public List getArgs() {
        return m_args;
    }
}
