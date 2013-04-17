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

import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListResourceBundle;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

/**
 * This is a ResourceBundle that allows the developer to add additional
 * ChainableResourceBundles to it.  Then, when the bundle is asked for
 * a key, it checks the ChainableResourceBundles in the order that they
 * were added.  A typical use would be something like this
 * <pre><code>
 * public class EventResourceBundle extends ChainedResourceBundle {
 *   public EventResourceBundle() {
 *       super();
 *       addBundle((PropertyResourceBundle)getBundle("EVENT_BUNDLE_NAME"));
 *       addBundle((PropertyResourceBundle)getBundle("DEFAULT_BUNDLE_NAME"));
 *   }
 * }
 * </code></pre>
 */
public class ChainedResourceBundle extends ResourceBundle {

    private final List<ChainableResourceBundle> bundles;
    private final List<String> keys;

    public ChainedResourceBundle() {
        super();
        bundles = new LinkedList<ChainableResourceBundle>();
        keys = new LinkedList<String>();
    }

    /**
     *  this wraps the PropertyResourceBundle in a ChainableResourceBundle
     *  and then delegates to addBundle(ChainableResourceBundle bundle);
     * 
     * @param bundle 
     */
    public void addBundle(final PropertyResourceBundle bundle) {
        addBundle(new ChainablePropertyResourceBundle(bundle));
    }

    /**
     *  this wraps the PropertyResourceBundle in a ChainableResourceBundle
     *  and then delegates to addBundle(ChainableResourceBundle bundle);
     * 
     * @param bundle 
     */
    public void addBundle(final ListResourceBundle bundle) {
        addBundle(new ChainableListResourceBundle(bundle));
    }

    /**
     *  This adds bundles to this chained resource.  The bundles
     *  are examined for the key in the order that they are added.
     */
    private void addBundle(final ChainableResourceBundle bundle) {
        bundles.add(bundle);
        final Enumeration<String> enu = bundle.getKeys();
        while (enu.hasMoreElements()) {
            keys.add(enu.nextElement());
        }
    }

    public void putBundle(final PropertyResourceBundle bundle) {
        putBundle(new ChainablePropertyResourceBundle(bundle));
    }

    public void putBundle(final ListResourceBundle bundle) {
        putBundle(new ChainableListResourceBundle(bundle));
    }

    private void putBundle(final ChainableResourceBundle bundle) {
        bundles.add(0, bundle);
        final Enumeration<String> enu = bundle.getKeys();
        final List<String> bundleKeys = new LinkedList<String>();
        while (enu.hasMoreElements()) {
            bundleKeys.add(enu.nextElement());
        }
        keys.addAll(0, bundleKeys);
    }

    /**
     *  Because this particular bundle is just a wrapper around other bundles,
     *  this method will return null so that the ResourceBundle can then
     *  examine the values returned by the chained parents
     * @param key
     * @return  
     */
    @Override
    public Object handleGetObject(final String key) {
        final Iterator<ChainableResourceBundle> iter = bundles.iterator();
        Object object = null;
        ChainableResourceBundle bundle;
        while (iter.hasNext() && object == null) {        
            bundle = iter.next();
            object = bundle.handleGetObject(key);
            //object = iter.next().handleGetObject(key);
        }        
        return object;
    }

    @Override
    public Enumeration<String> getKeys() {
        return Collections.enumeration(keys);
    }

    /**
     *  This is basically a way to allow us to set the parent.  The javadoc
     *  for these methods can be found by looking at the javadoc for
     *  PropertyResourceBundle since this delegate everything to 
     *  an internal PropertyResourceBundle.
     */
    private class ChainablePropertyResourceBundle
            implements ChainableResourceBundle {

        private final PropertyResourceBundle wrappedBundle;

        /**
         *  This creates a new Bundle that delegates everything to the
         *  passed in bundle and sets the parent of the passed in bundle
         *  to the passed in ResourceBundle
         */
        public ChainablePropertyResourceBundle(final PropertyResourceBundle bundle) {
            super();
            wrappedBundle = bundle;
        }

        @Override
        public Object handleGetObject(final String key) {
            return wrappedBundle.handleGetObject(key);
        }

        @Override
        public Enumeration<String> getKeys() {
            return wrappedBundle.getKeys();
        }

    }

    /**
     *  This is basically a way to allow us to set the parent.  The javadoc
     *  for these methods can be found by looking at the javadoc for
     *  PropertyResourceBundle since this delegate everything to 
     *  an internal PropertyResourceBundle.
     */
    private class ChainableListResourceBundle implements ChainableResourceBundle {

        private final ListResourceBundle m_wrappedBundle;

        /**
         *  This creates a new Bundle that delegates everything to the
         *  passed in bundle and sets the parent of the passed in bundle
         *  to the passed in ResourceBundle
         */
        public ChainableListResourceBundle(final ListResourceBundle bundle) {
            super();
            m_wrappedBundle = bundle;
        }

        public Object handleGetObject(final String key) {
            return m_wrappedBundle.handleGetObject(key);
        }

        public Enumeration<String> getKeys() {
            return m_wrappedBundle.getKeys();
        }

    }
}
