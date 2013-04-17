/*
 * Copyright (C) 2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.globalization;

import java.util.Enumeration;

/**
 *  This is an interface that make sure that the implementing class
 *  provides access to the handleGetObject and the getKeys method
 *  of ResourceBundle
 */
public interface ChainableResourceBundle {

    /**
     *  This is the public version of ResourceBundle.handleGetObject(String key)
     *  which normally has protected access.  If you have a PropertyResourceBundle
     *  or a ListResourceBundle, this will simply delegate to the
     *  handleGetObject(String key) method.
     * 
     * @param key
     * @return  
     */
    public Object handleGetObject(String key);

    /**
     *  This is the public version of ResourceBundle.getKeys()
     *  which normally has protected access.  If you have a PropertyResourceBundle
     *  or a ListResourceBundle, this will simply delegate to the
     *  getKeys() method.
     * 
     * @return 
     */
    public Enumeration<String> getKeys();
}
