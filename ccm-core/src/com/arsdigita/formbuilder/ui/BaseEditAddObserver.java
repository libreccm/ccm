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

import com.arsdigita.bebop.BaseLink;
import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.FormSection;
import com.arsdigita.bebop.Label;
import com.arsdigita.formbuilder.CompoundComponent;
import com.arsdigita.formbuilder.PersistentComponent;
import com.arsdigita.formbuilder.PersistentHidden;
import com.arsdigita.formbuilder.PersistentLabel;
import com.arsdigita.formbuilder.util.GlobalizationUtil ; 
import com.arsdigita.globalization.GlobalizedMessage;

import org.apache.log4j.Logger;

/**
 * This is an extension of the BaseAddObserver which handles the editing of
 * controls on a form by adding additional links on each row.
 */
public abstract class BaseEditAddObserver extends BaseAddObserver {

    private static final Logger s_log =
        Logger.getLogger(BaseEditAddObserver.class);

    /**
     * 
     * @param formSection
     * @param component
     * @param componentPosition 
     */
    public void beforeAddingComponent(FormSection formSection,
                                      PersistentComponent component,
                                      int componentPosition) {
        super.beforeAddingComponent(formSection,
                                    component,
                                    componentPosition);
    }

    /**
     * 
     * @param persistentComponent
     * @param componentPosition
     * @param component 
     */
    public void addingComponent(PersistentComponent persistentComponent,
                                int componentPosition,
                                Component component) {
        super.addingComponent(persistentComponent,
                              componentPosition,
                              component);
    }

    /**
     * 
     * @param formSection
     * @param component
     * @param componentPosition 
     */
    public void afterAddingComponent(FormSection formSection,
                                     PersistentComponent component,
                                     int componentPosition) {

        super.afterAddingComponent(formSection,
                                   component,
                                   componentPosition);
        if (component instanceof PersistentHidden) {
            PersistentHidden hidden = (PersistentHidden)component;
            formSection.add(new Label(GlobalizationUtil.globalize(
                                      "formbuilder.ui.hidden_field")));
            formSection.add(new Label(hidden.getParameterName()));
        } 

        if(!(component instanceof PersistentLabel)) {
            BoxPanel b = new BoxPanel(BoxPanel.HORIZONTAL);

            if (s_log.isDebugEnabled())
                s_log.debug("Adding widget " + component.getClass().getName());

            if (component.isEditable()) {
                // createLink implementation creates the name and the GlobMsg
                BaseLink edit = createLink("edit", component);
                b.add(edit);
            }

            // createLink implementation creates the name and the GlobMsg
            BaseLink move = createLink("move", component);
            b.add(move);

            // createLink implementation creates the name and the GlobMsg
            BaseLink delete = createLink("delete", component);
            delete.setConfirmation(GlobalizationUtil.globalize(
                                   "formbuilder.ui.delete_confirm"));
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
     * It is the respnsibility of the implementation to provide a
     * corresponding GlobalizedMessage for the label of the component name.
     */
    protected abstract BaseLink createLink(String name,
                                           PersistentComponent component);

}
