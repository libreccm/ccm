/*
 * Copyright (c) 2010 Jens Pelzetter
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
package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class GenericPersonOrgaUnitsStep extends SimpleEditStep {

    public GenericPersonOrgaUnitsStep(final ItemSelectionModel itemModel, 
                                      final AuthoringKitWizard parent) {
        this(itemModel, parent, null);
    }

    public GenericPersonOrgaUnitsStep(final ItemSelectionModel itemModel,
                                      final AuthoringKitWizard parent,
                                      final String prefix) {
        super(itemModel, parent, prefix);

        final GenericPersonOrgaUnitsTable table = new 
                                          GenericPersonOrgaUnitsTable(itemModel);
        setDisplayComponent(table);
    }

}
