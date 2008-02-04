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


package com.arsdigita.london.search.ui.admin;

import com.arsdigita.bebop.PageState;
import com.arsdigita.london.search.Server;
import com.arsdigita.kernel.ui.ACSObjectSelectionModel;
import com.arsdigita.bebop.parameters.BigDecimalParameter;


public class ServerSelectionModel extends ACSObjectSelectionModel {
    
    public ServerSelectionModel(BigDecimalParameter param) {
	super(Server.class.getName(),
	      Server.BASE_DATA_OBJECT_TYPE,
	      param);
    }

    public Server getSelectedServer(PageState state) {
	return (Server)getSelectedObject(state);
    }
}
