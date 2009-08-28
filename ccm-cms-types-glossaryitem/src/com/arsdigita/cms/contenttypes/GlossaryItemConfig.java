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

package com.arsdigita.cms.contenttypes;

import com.arsdigita.runtime.AbstractConfig;
import com.arsdigita.util.parameter.EnumerationParameter;
import com.arsdigita.util.parameter.Parameter;
import com.arsdigita.util.parameter.StringParameter;

/**
 * Config options for GlossaryItem.
 * @author <a href="http://sourceforge.net/users/timcarpenter/">timcarpenter</a>
 */
public class GlossaryItemConfig extends AbstractConfig {
    private final EnumerationParameter definitionEditorType;
    private final Parameter fckEditorConfig;

    /**
     * Enum of the types of editor that can be used.
     */
    public enum EDITOR_TYPE {
        WYSIWYG,
        TEXT;
    }

    public GlossaryItemConfig() {
    	definitionEditorType = new EnumerationParameter(
                "com.arsdigita.cms.contenttypes.glossaryitem.definition.editor",
                Parameter.REQUIRED,
                EDITOR_TYPE.TEXT);
        definitionEditorType.put(EDITOR_TYPE.WYSIWYG.name().toLowerCase(), 
                                 EDITOR_TYPE.WYSIWYG);
        definitionEditorType.put(EDITOR_TYPE.TEXT.name().toLowerCase(), 
                                 EDITOR_TYPE.TEXT);

        fckEditorConfig = new StringParameter(
            "com.arsdigita.cms.contenttypes.glossaryitem.fck_editor_config",
            Parameter.REQUIRED,
            "/assets/fckeditor/config/fckconfig_glossaryitem.js");

        register(definitionEditorType);
        register(fckEditorConfig);

        loadInfo();
    }
    
    public final EDITOR_TYPE getDefinitionEditorType() {
    	return ((EDITOR_TYPE) get(definitionEditorType));
    }
    
    public final String getFckEditorConfig() {
        return (String) get(fckEditorConfig);
    }
}
