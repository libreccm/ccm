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
package com.arsdigita.formbuilder.ui;


import com.arsdigita.formbuilder.util.GlobalizationUtil ; 

import com.arsdigita.formbuilder.ComponentAddObserver;
import com.arsdigita.bebop.FormSection;
import com.arsdigita.formbuilder.PersistentComponent;
import com.arsdigita.formbuilder.CompoundComponent;
import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.Label;


/**
 * This class provides a basic implementation of the
 * {@link com.arsdigita.formbuilder.ComponentAddObserver}
 * class which will organise a sensible layout for
 * controls on a form
 */
public class BaseAddObserver implements ComponentAddObserver {

    public void beforeAddingComponent(FormSection formSection,
                                      PersistentComponent component,
                                      int componentPosition) {
        // Padding
        if (!component.hasLabel()) {
            formSection.add(new Label(""));
        }

        // Make sure we process nested form sections
        if (component instanceof CompoundComponent) {
            CompoundComponent fs = (CompoundComponent)component;
            fs.setComponentAddObserver(new BaseAddObserver());
        }
    }

    public void addingComponent(PersistentComponent persistentComponent,
                                int componentPosition,
                                Component component) {
        // Nada
    }

    public void afterAddingComponent(FormSection formSection,
                                     PersistentComponent component,
                                     int componentPosition) {

        if (component instanceof CompoundComponent) {
            try {
                ColumnPanel panel = (ColumnPanel)formSection.getPanel();

                FormSection fs = (FormSection)formSection.get(formSection.size()-1);
                panel.setConstraint(fs, ColumnPanel.FULL_WIDTH);
            } catch (ClassCastException ex) {
                // Nada
            }
        }
    }
}
