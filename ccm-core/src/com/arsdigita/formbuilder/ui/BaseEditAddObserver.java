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

import com.arsdigita.formbuilder.CompoundComponent;
import com.arsdigita.formbuilder.PersistentComponent;
import com.arsdigita.formbuilder.PersistentHidden;
import com.arsdigita.formbuilder.PersistentLabel;
import com.arsdigita.formbuilder.util.GlobalizationUtil ; 

import com.arsdigita.bebop.BaseLink;
import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.FormSection;
import com.arsdigita.bebop.Label;

import org.apache.log4j.Logger;

/**
 * This is an extension of the BaseAddObserver which
 * handles the editing of controls on a form by
 * adding additional links on each row.
 */
public abstract class BaseEditAddObserver extends BaseAddObserver {
    private static final Logger s_log =
        Logger.getLogger(BaseEditAddObserver.class);

    public void beforeAddingComponent(FormSection formSection,
                                      PersistentComponent component,
                                      int componentPosition) {
        super.beforeAddingComponent(formSection,
                                    component,
                                    componentPosition);

        // Propagate the 3 column layout to form sections
        /*
          try {
          CompoundComponent fs = (CompoundComponent)componentFactory;
          fs.setContainer(new ColumnPanel(3, true));
          } catch (ClassCastException ex) {
          // Nada
          }
        */
    }

    public void addingComponent(PersistentComponent persistentComponent,
                                int componentPosition,
                                Component component) {
        super.addingComponent(persistentComponent,
                              componentPosition,
                              component);
    }

    public void afterAddingComponent(FormSection formSection,
                                     PersistentComponent component,
                                     int componentPosition) {
        super.afterAddingComponent(formSection,
                                   component,
                                   componentPosition);
        if (component instanceof PersistentHidden) {
            PersistentHidden hidden = (PersistentHidden)component;
            formSection.add(new Label(GlobalizationUtil.globalize("formbuilder.ui.hidden_field")));
            formSection.add(new Label(hidden.getParameterName()));
        } 

        if(!(component instanceof PersistentLabel)) {
            BoxPanel b = new BoxPanel(BoxPanel.HORIZONTAL);

            if (s_log.isDebugEnabled())
                s_log.debug("Adding widget " + component.getClass().getName());

            if (component.isEditable()) {
                BaseLink edit = createLink("edit", component);
                b.add(edit);
            }

            BaseLink move = createLink("move", component);
            b.add(move);

            BaseLink delete = createLink("delete", component);
            delete.setConfirmation("Are you sure you wish to delete this widget?");
            b.add(delete);

            if (component instanceof CompoundComponent) {
                formSection.add(new Label(""));
                formSection.add(new Label(""));
            }
            formSection.add(b);
        }
    }

    /**
     * This method is use to create a link for editing
     * a component.
     */
    protected abstract BaseLink createLink(String label,
                                           PersistentComponent component);
}
