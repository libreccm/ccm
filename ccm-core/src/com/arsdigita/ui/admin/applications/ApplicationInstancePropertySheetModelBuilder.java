/*
 * Copyright (c) 2013 Jens Pelzetter
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
package com.arsdigita.ui.admin.applications;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.PropertySheet;
import com.arsdigita.bebop.PropertySheetModel;
import com.arsdigita.bebop.PropertySheetModelBuilder;
import com.arsdigita.util.LockableImpl;
import com.arsdigita.web.Application;

/**
 * {@link PropertySheetModelBuilder} implementation for the {@link ApplicationInstancePropertySheetModel}.
 * 
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class ApplicationInstancePropertySheetModelBuilder extends LockableImpl implements PropertySheetModelBuilder {

    private Application application;
    
    public ApplicationInstancePropertySheetModelBuilder() {
        super();                
    }
    
    public PropertySheetModel makeModel(final PropertySheet sheet, final PageState state) {
        return new ApplicationInstancePropertySheetModel(application);
    }
    
    public void setApplication(final Application application) {
        this.application = application;
    }
    
}
