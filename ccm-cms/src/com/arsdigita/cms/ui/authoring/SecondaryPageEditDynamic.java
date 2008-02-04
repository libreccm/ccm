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
package com.arsdigita.cms.ui.authoring;

import com.arsdigita.cms.ContentType;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.toolbox.ui.DomainObjectPropertySheet;

/**
 * The editing component for user defined items. Consists of a display
 * component which displays the form metadata, and a form which edits the
 * metadata as well as text.
 *
 * @author Xixi D'Moon (xdmoon@arsdigita.com)
 * @version $Revision: #6 $ $DateTime: 2004/08/17 23:15:09 $
 */
public class SecondaryPageEditDynamic extends PageEditDynamic {

    public static final String versionId = "$Id: SecondaryPageEditDynamic.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/17 23:15:09 $";


    /**
     * Construct a new SecondaryPageEditDynamic component
     *
     * @param itemModel The {@link ItemSelectionModel} which will
     *   be responsible for loading the current item
     *
     * @param parent The parent wizard which contains the form. The form
     *   may use the wizard's methods, such as stepForward and stepBack,
     *   in its process listener.
     */
    public SecondaryPageEditDynamic(ItemSelectionModel itemModel,
                                    AuthoringKitWizard parent) {
        super (itemModel, parent, null, false);
    }
    /**
     * Construct a new SecondaryPageEditDynamic component
     *
     * @param itemModel The {@link ItemSelectionModel} which will
     *   be responsible for loading the current item
     *
     * @param parent The parent wizard which contains the form. The form
     *   may use the wizard's methods, such as stepForward and stepBack,
     *   in its process listener.
     *
     * @param originatingType The content type to use for choosing dynamic
     *   components (if one UDCT inherits from another, type-specific attributes
     *   will be in separate steps).
     */
    public SecondaryPageEditDynamic(ItemSelectionModel itemModel,
                                    AuthoringKitWizard parent,
                                    ContentType originatingType) {

        super (itemModel, parent, originatingType, false);
    }

    protected void addNameTitleFields(DomainObjectPropertySheet sheet) {
        // do nothing for secondary steps
    }

}
