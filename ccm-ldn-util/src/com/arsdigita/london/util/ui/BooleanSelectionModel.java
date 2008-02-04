/*
 * Copyright (C) 2001 ArsDigita Corporation. All Rights Reserved.
 *
 * The contents of this file are subject to the ArsDigita Public 
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of
 * the License at http://www.arsdigita.com/ADPL.txt
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */

package com.arsdigita.london.util.ui;

import com.arsdigita.bebop.parameters.BooleanParameter;
import com.arsdigita.bebop.ParameterSingleSelectionModel;
import com.arsdigita.bebop.PageState;




public class BooleanSelectionModel extends ParameterSingleSelectionModel {
    
    public BooleanSelectionModel(BooleanParameter p) {
	super(p);
    }

    public boolean getValue(PageState state) {
	Boolean value = (Boolean)getSelectedKey(state);
	return (value != null ? value.booleanValue() : false);
    }
    
    public void setValue(PageState state,
			 boolean value) {
	setSelectedKey(state, new Boolean(value));
    }
}
