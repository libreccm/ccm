/*
 * Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Resettable;
import com.arsdigita.bebop.SimpleContainer;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * A container which implements the {@link Resettable} interface and
 * provides other useful methods.
 *
 * @author <a href="mailto:phong@arsdigita.com">Phong Nguyen</a>
 * @version $Revision: #7 $
 **/
public class ResettableContainer extends SimpleContainer implements Resettable {

    // $Source: /cvsroot/content-types/apps/content-types/src/ui/ResettableContainer.java,v $
    // $Revision: #7 $
    // $Date: 2004/08/17 $
    // $Author: sskracic $


    // A list of all resettable components in this container
    private ArrayList m_resettableComponents = new ArrayList();

    // A list of all components that are not visible by default
    private ArrayList m_componentsNotVisibleByDefault = new ArrayList();


    /**
     * Constructs a new, empty <code>RessetableContainer</code>.
     **/
    public ResettableContainer() {
        super();
    }

    /**
     * Constructs a new, empty <code>RessetableContainer</code>.
     *
     * @param key The key for this container.
     **/
    public ResettableContainer(String key) {
        super();
        setKey(key);
    }

    /**
     * Constructs a new, empty <code>RessetableContainer</code>. The
     * container will wrap its children in the specified tag.
     *
     * @param tag The name of the XML element that will be used to
     * wrap the children of this container.
     * @param ns The namespace for the tag.
     **/
    public ResettableContainer(String tag, String ns) {
        super(tag, ns);
    }

    /**
     * Adds a component to container.
     *
     * @param pc The component to be added.
     * @pre (pc != null)
     * @post (m_components.contains(pc))
     **/
    public void add(Component pc) {
        add(pc, true);
    }

    /**
     * Add a component to this container
     *
     * @param pc The component to be added.
     * @param constraints This parameter is ignored. Child classes
     * should override the add method if they wish to provide special
     * handling of constraints.
     **/
    public void add(Component pc, int constraints) {
        add(pc);
    }

    /**
     * Adds the component to this pane with the specified default
     * visibility.
     *
     * @param defaultVisibility The default visibility of this component
     * @pre (pc != null)
     **/
    public void add(Component pc, boolean defaultVisibility) {
        super.add(pc);
        if (pc instanceof Resettable) {
            m_resettableComponents.add(pc);
        }
        if (! defaultVisibility) {
            m_componentsNotVisibleByDefault.add(pc);
        }
    }

    /**
     * Sets the visibility of all child components to false, except
     * for the component with the specified key.
     *
     * @param state The state of the current request.
     * @param key The key of the component. There will be no
     * visibility changes if key is null.
     **/
    public void onlyShowComponent(PageState state, String key) {
        if (key == null) { return; }

        Component child;
        Iterator iter = children();
        while (iter.hasNext()) {
            child = (Component) iter.next();
            child.setVisible(state, key.equals(child.getKey()));
        }
    }

    /**
     * Sets the visibility of all child components to false, except
     * for the specified component.
     *
     * @param state The state of the current request.
     * @param c The key of the component. There will be no visibility
     * changes if <code>c</code> is null.
     **/
    public void onlyShowComponent(PageState state, Component c) {
        if (c == null) { return; }

        Component child;
        Iterator iter = children();
        while (iter.hasNext()) {
            child = (Component) iter.next();
            child.setVisible(state, child.equals(c));
        }
    }

    /**
     * Resets all resettable components added to this container.
     *
     * @param state The state of the current request.
     **/
    public void reset(PageState state) {
        // Reset all resettable components automatically
        Iterator iter = m_resettableComponents.iterator();
        while (iter.hasNext()) {
            ((Resettable) iter.next()).reset(state);
        }
    }

    /**
     * Registers with page that this container belongs to and sets the
     * default visibility of child components.
     *
     * @param p The page this container belongs to.
     **/
    public void register(Page p) {
        Iterator iter = m_componentsNotVisibleByDefault.iterator();
        while (iter.hasNext()) {
            p.setVisibleDefault((Component) iter.next(), false);
        }
    }

}
