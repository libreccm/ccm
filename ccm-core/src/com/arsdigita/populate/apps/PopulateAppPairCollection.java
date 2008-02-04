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

import java.util.ArrayList;

/**
 * @author bche
 */
public class PopulateAppPairCollection {
    
    private ArrayList m_PopAppPairs = new ArrayList();
    
    public PopulateAppPairCollection() {;
    }
        
    public void addPopulateApp(PopulateAppPair popAppPair) {
        m_PopAppPairs.add(popAppPair);
    }
    
    public int getSize() {
        return m_PopAppPairs.size();
    }
    
    public PopulateAppPair getPopulateApp(int iIndex) throws IndexOutOfBoundsException {
        return (PopulateAppPair)m_PopAppPairs.get(iIndex);
    }

}
