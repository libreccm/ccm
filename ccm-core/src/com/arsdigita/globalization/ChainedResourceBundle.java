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

import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.PropertyResourceBundle;
import java.util.ListResourceBundle;
import java.util.Enumeration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

/**
 *  This is a ResourceBundle that allows the developer to add additional
 *  ChainableResourceBundles to it.  Then, when the bundle is asked for
 *  a key, it checks the ChainableResourceBundles in the order that they
 *  were added.  A typical use would be something like this
 *  <pre><code>
 *  public class EventResourceBundle extends ChainedResourceBundle {
 *    public EventResourceBundle() {
 *        super();
 *        addBundle((PropertyResourceBundle)getBundle("EVENT_BUNDLE_NAME"));
 *        addBundle((PropertyResourceBundle)getBundle("DEFAULT_BUNDLE_NAME"));
 *    }
 *  }
 *
 *  </code></pre>
 **/
public class ChainedResourceBundle extends ResourceBundle {
    
    private List m_bundles;
    private List m_keys;

    public ChainedResourceBundle() {
        super();
        m_bundles = new LinkedList();
        m_keys = new LinkedList();
    }

    /**
     *  this wraps the PropertyResourceBundle in a ChainableResourceBundle
     *  and then delegates to addBundle(ChainableResourceBundle bundle);
     */
    public void addBundle(PropertyResourceBundle bundle) {
        addBundle(new ChainablePropertyResourceBundle(bundle));
    }

    /**
     *  this wraps the PropertyResourceBundle in a ChainableResourceBundle
     *  and then delegates to addBundle(ChainableResourceBundle bundle);
     */
    public void addBundle(ListResourceBundle bundle) {
        addBundle(new ChainableListResourceBundle(bundle));
    }

    /**
     *  This adds bundles to this chained resource.  The bundles
     *  are examined for the key in the order that they are added.
     */
    private void addBundle(ChainableResourceBundle bundle) {
        m_bundles.add(bundle);
        Enumeration enu = bundle.getKeys();
        while (enu.hasMoreElements()) {
            m_keys.add(enu.nextElement());
        }
    }
        
    public void putBundle(PropertyResourceBundle bundle) {
    	putBundle(new ChainablePropertyResourceBundle(bundle));
    }
    
    public void putBundle(ListResourceBundle bundle) {
    	putBundle(new ChainableListResourceBundle(bundle));
    }

    private void putBundle(ChainableResourceBundle bundle) {
    	m_bundles.add(0,bundle);
        Enumeration enu = bundle.getKeys();
        List bundleKeys = new LinkedList();
        while (enu.hasMoreElements()) {
            bundleKeys.add(enu.nextElement());
        }
        m_keys.addAll(0, bundleKeys);
    }
    
    /**
     *  Because this particular bundle is just a wrapper around other bundles,
     *  this method will return null so that the ResourceBundle can then
     *  examine the values returned by the chained parents
     */
    public Object handleGetObject(String key) {
        Iterator iter = m_bundles.iterator();
        Object object = null;
        while (iter.hasNext() && object == null) {
            object = ((ChainableResourceBundle)iter.next()).handleGetObject(key);
        }
        return object;
    }

    public Enumeration getKeys() {
        return Collections.enumeration(m_keys);
    }


    /**
     *  This is basically a way to allow us to set the parent.  The javadoc
     *  for these methods can be found by looking at the javadoc for
     *  PropertyResourceBundle since this delegate everything to 
     *  an internal PropertyResourceBundle.
     */
    private class ChainablePropertyResourceBundle
        implements ChainableResourceBundle {
        private PropertyResourceBundle m_wrappedBundle = null;

        /**
         *  This creates a new Bundle that delegates everything to the
         *  passed in bundle and sets the parent of the passed in bundle
         *  to the passed in ResourceBundle
         */
        public ChainablePropertyResourceBundle(PropertyResourceBundle bundle) {
            super();
            m_wrappedBundle = bundle;
        }
        
        public Object handleGetObject(String key) {
            return m_wrappedBundle.handleGetObject(key);
        }

        public Enumeration getKeys() {
            return m_wrappedBundle.getKeys();
        }
    }


    /**
     *  This is basically a way to allow us to set the parent.  The javadoc
     *  for these methods can be found by looking at the javadoc for
     *  PropertyResourceBundle since this delegate everything to 
     *  an internal PropertyResourceBundle.
     */
    private class ChainableListResourceBundle
        implements ChainableResourceBundle {
        private ListResourceBundle m_wrappedBundle = null;

        /**
         *  This creates a new Bundle that delegates everything to the
         *  passed in bundle and sets the parent of the passed in bundle
         *  to the passed in ResourceBundle
         */
        public ChainableListResourceBundle(ListResourceBundle bundle) {
            super();
            m_wrappedBundle = bundle;
        }
        
        public Object handleGetObject(String key) {
            return m_wrappedBundle.handleGetObject(key);
        }

        public Enumeration getKeys() {
            return m_wrappedBundle.getKeys();
        }
    }
}
