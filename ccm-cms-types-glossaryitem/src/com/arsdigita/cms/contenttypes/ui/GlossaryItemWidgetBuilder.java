/*
 * Copyright (C) 2009 Permeance Technologies Ptd Ltd. All Rights Reserved.
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
 */

package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.Bebop;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.form.DHTMLEditor;
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.bebop.util.BebopConstants;
import com.arsdigita.cms.contenttypes.GlossaryItem;
import com.arsdigita.cms.contenttypes.ui.authoring.GlossaryItemCreate;
import com.arsdigita.cms.contenttypes.util.GlossaryGlobalizationUtil;
import com.arsdigita.cms.ui.CMSDHTMLEditor;

import static com.arsdigita.cms.contenttypes.ui.GlossaryItemPropertyForm.DEFINITION;

/**
 * Helps to build some of the widgets for {@link GlossaryItemPropertyForm}
 * and {@link GlossaryItemCreate}
 * @author <a href="http://sourceforge.net/users/timcarpenter/">timcarpenter</a>
 */
public class GlossaryItemWidgetBuilder {

    public TextArea makeDefinitionArea() {

        TextArea definition;

        switch (GlossaryItem.getConfig().getDefinitionEditorType()) {
        case WYSIWYG:
            definition = new CMSDHTMLEditor(DEFINITION);
            ((CMSDHTMLEditor) definition).setWrap(DHTMLEditor.SOFT);
            if (Bebop.getConfig().getDHTMLEditor()
                                 .equals(BebopConstants.BEBOP_FCKEDITOR)) 
            {
                ((CMSDHTMLEditor) definition).setConfig(
                    new DHTMLEditor.Config("glossaryitem", 
                       		    GlossaryItem.getConfig().getFckEditorConfig()));
            } else {
                // remove this so end users cannot browse through back end 
                // folder system
                ((CMSDHTMLEditor) definition).hideButton("insertlink");
            }
            break;
        default:
            definition = new TextArea(DEFINITION);
            definition.setWrap(TextArea.SOFT);
            break;
        }

        definition.setLabel(GlossaryGlobalizationUtil
                        .globalize("cms.contenttypes.ui.glossary.definition"));
        definition.addValidationListener(new NotNullValidationListener());
        definition.setCols(40);
        definition.setRows(5);
        return definition;
    }
    
 // public Label makeDefinitionLabel() {
     // return new Label(GlossaryGlobalizationUtil
     //                 .globalize("cms.contenttypes.ui.glossary.definition"));
 // }
}
