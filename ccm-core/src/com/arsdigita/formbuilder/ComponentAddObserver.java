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

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.FormSection;

import com.arsdigita.formbuilder.PersistentComponent;


/**
 * Implement this interface to add modifications to the persistent form
 * before each component gets added to the form. This does not work recursively
 * so that the observer is only executed in the top level PersistentFormSection.
 */
public interface ComponentAddObserver {

    public void beforeAddingComponent(FormSection formSection,
                                      PersistentComponent component,
                                      int componentPosition);

    public void addingComponent(PersistentComponent persistentComponent,
                                int componentPosition,
                                Component component);

    public void afterAddingComponent(FormSection formSection,
                                     PersistentComponent component,
                                     int componentPosition);
}
