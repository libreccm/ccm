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
package com.arsdigita.formbuilder;

import java.util.Iterator;

/**
 * Implemented by classes that contain persistent Bebop components. The
 * components are added via their corresponding PersistentComponent that
 * knows how to create the component. The relationship between the container
 * and the components is an aggregate association (in UML terminology) with
 * multiplicity 0..N on both ends of the association.
 *
 * @author Peter Marklund
 * @version $Id: PersistentContainer.java 317 2005-03-11 19:04:37Z mbooth $
 *
 */
public interface PersistentContainer {

    public static final String versionId = "$Id: PersistentContainer.java 317 2005-03-11 19:04:37Z mbooth $ by $Author: mbooth $, $DateTime: 2004/08/16 18:10:38 $";

    /**
     * Add a component last in the list of components in
     * this container.
     */
    public void addComponent(PersistentComponent component);


    /**
     * Add a component to the Container at the given position.
     *
     * @param position The position to add the component at. Positions start
     *                 with 1
     */
    public void addComponent(PersistentComponent component,
                             int position);

    /**
     * Remove a component from the Container
     */
    public void removeComponent(PersistentComponent component);

    /**
     * Move component to new position.
     *
     * @param toPosition The position to move the component to. Positions start with 1.
     */
    public void moveComponent(PersistentComponent component,
                              int toPosition);

    /**
     * Delete all component associations from this container
     */
    public void clearComponents();

    /**
     * Return an Iterator over all components in the container
     */
    public Iterator getComponentsIter();
}
