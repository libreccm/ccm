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
package com.arsdigita.cms.ui;

import com.arsdigita.bebop.form.DHTMLEditor;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.ContentSection;

/**
 *
 *
 */
public class CMSDHTMLEditor extends DHTMLEditor {

    public CMSDHTMLEditor(String name) {
        super(new StringParameter(name),
              ContentSection.getConfig().getDHTMLEditorConfig());
        addPlugins();
        hideButtons();

    }

    public CMSDHTMLEditor(ParameterModel model) {
        super(model,
              ContentSection.getConfig().getDHTMLEditorConfig());

        addPlugins();
        hideButtons();
    }

    private void addPlugins() {
        String[] plugins = ContentSection.getConfig().getDHTMLEditorPlugins();
        if (plugins != null) {
            for (int i = 0; i < plugins.length; i++) {
                addPlugin(plugins[i]);
            }
        }
    }

    private void hideButtons() {
        String[] hiddenButtons = ContentSection.getConfig().getDHTMLEditorHiddenButtons();
        if (hiddenButtons != null) {
            for (int i = 0; i < hiddenButtons.length; i++) {
                hideButton(hiddenButtons[i]);
            }
        }
    }

}
