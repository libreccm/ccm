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
package com.arsdigita.bebop;

import org.apache.log4j.Logger;

import com.arsdigita.bebop.util.Traversal;
import java.util.LinkedList;
import java.util.List;
import java.util.HashMap;
import java.util.Collections;
import java.util.Map;

/**
 * 
 * Component pool for recycling component instances.  This is useful
 * for performance optimization, to limit object creation and garbage
 * collection for frequently used objects (Pages, Portlets, etc.)
 *
 * <p>A ComponentPool is a really set of individual pools for each
 * different class of component.
 *
 * @author Bob Donald
 * @author Bill Schneider
 * @version $Id: ComponentPool.java 287 2005-02-22 00:29:02Z sskracic $ $DateTime: 2004/08/16 18:10:38 $
 * @since
 *  */

public class ComponentPool {

    private static final Logger s_cat = Logger.getLogger(ComponentPool.class.getName());

    private static class _pool {

        List m_usedComponents = Collections.synchronizedList(new LinkedList());
        List m_availComponents = Collections.synchronizedList(new LinkedList());

        private Class    m_class;
        private int      m_maxSize = -1;

        public _pool() {}

        public void setPoolSize(Class c, int num) {
            m_class = c;
            m_maxSize = num;

            // expand pool to size
            while (m_availComponents.size() < m_maxSize) {
                try {
                    Component p = (Component)m_class.newInstance();

                    // At this point, the OnBuildPage method is called on the
                    // page to give it a chance to build its own specific page
                    // layout before putting it into the pool

                    //p.OnBuildPage();
                    m_availComponents.add(p);
                } catch ( InstantiationException e) {
                    s_cat.error("setPoolSize", e);
                    break;
                } catch ( IllegalAccessException e) {
                    s_cat.error("setPoolSize", e);
                    break;
                }
            }
        }

        public Component getComponent()
        {
            Component p = null;
            try {
                p = (Component) m_availComponents.remove(0);
                m_usedComponents.add(p);

                // Call OnActivate method in order to inform page that
                // it is about to be used. This gives the page a chance
                // to make any last minute changes before being used
                // in a request

                //p.OnActivate();

            } catch ( java.lang.IndexOutOfBoundsException e ) {
                // only happens if avail list is empty
                s_cat.error("getComponent: free list was empty");
            }

            return p;
        }

        public void returnToPool( Component p ) {
            if (m_usedComponents.remove(p))
                {
                    // Call OnDeactivate method in order to inform page that
                    // it is done being used in the current request and being
                    // put back into the pool. This allows the page to remove
                    // any necessary request specific changes

                    //p.OnDeactivate();
                    // unlock component and all its children
                    Traversal tr = new Traversal() {
                            public void act(Component c) {
                                if (c instanceof SimpleComponent) {
                                    ((SimpleComponent)c).unlock();
                                }
                            }
                        };
                    tr.preorder(p);
                    m_availComponents.add(p);
                }
        }

    };

    private Map m_pools = Collections.synchronizedMap(new HashMap());
    private static int s_retryLimit = 3;
    private static int s_retrySleep = 50;
    private static ComponentPool s_instance = new ComponentPool();

    public static ComponentPool getInstance() {
        return s_instance;
    }

    /**
     * Sets the pool size for components of class <code>c</code>.
     * Instantiates enough objects to ensure that there will be
     * <code>num</code> instances of that component available.
     *
     * @param c the component class to set the  pool size for
     * @param num the minimum pool size for that class
     */
    public void setPoolSize( Class c, int num ) {
        _pool p = (_pool) m_pools.get(c.getName());
        if ( p == null ) {
            p = new _pool();
            m_pools.put(c.getName(), p);
        }
        p.setPoolSize(c, num);
    }

    /**
     * Returns a component of class <code>componentClass</code>
     * from the pool.
     * @param componentClass the class of the component to return
     * @return an instatnce of componentClass
     */
    public Component getComponent( Class componentClass ) {
        int retries = 0;

        _pool pool = (_pool) m_pools.get(componentClass.getName());
        if (pool == null) {
            setPoolSize(componentClass, 10);
            pool = (_pool) m_pools.get(componentClass.getName());
        }

        do {
            Component c = pool.getComponent();

            // none available? try again.
            if (c == null) {
                retries++;
                try {
                    Thread.sleep(s_retrySleep);
                } catch (InterruptedException e) {
                    // ignore
                }
            } else {
                return c;
            }
        } while (retries <= s_retryLimit);
        s_cat.error("could not retrieve component of type " + componentClass);
        // need a better exception to throw
        throw new RuntimeException(componentClass.getName());
    }

    /**
     * Return the component to the pool of free components so it can
     * be reused.  If the component was not pooled then this becomes a
     * no-op; it is harmless to return an unpooled component to the
     * pool.
     */
    public void returnToPool( Component c ) {
        _pool pool = (_pool) m_pools.get(c.getClass().getName());
        if (pool != null ) {
            pool.returnToPool(c);
        }
    }

}
