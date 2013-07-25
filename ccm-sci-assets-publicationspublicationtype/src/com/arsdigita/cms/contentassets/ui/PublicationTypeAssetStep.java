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
package com.arsdigita.cms.contentassets.ui;

import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;
import com.arsdigita.kernel.ui.ACSObjectSelectionModel;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class PublicationTypeAssetStep extends SimpleEditStep {
    
    protected static final String EDIT = "edit";    
    protected static final String PUB_TYPE_PARAM = "publication_type";
    
    private final static String FORM_KEY = "PublicationTypesAdd";
    
    private final BigDecimalParameter typeParameter;
    final PublicationTypeAssetAddForm addForm;
    
    public PublicationTypeAssetStep(final ItemSelectionModel itemModel,
                                    final AuthoringKitWizard parent) {
        this(itemModel, parent, null);
    }
    
    public PublicationTypeAssetStep(final ItemSelectionModel itemModel,
                                    final AuthoringKitWizard parent,
                                    final String prefix) {
        super(itemModel, parent, prefix);
        
        typeParameter = new BigDecimalParameter(PUB_TYPE_PARAM);
        final ACSObjectSelectionModel typeModel = new ACSObjectSelectionModel(typeParameter);
        
        addForm = new PublicationTypeAssetAddForm(itemModel, typeModel);
        add(FORM_KEY,
            PublicationTypeAssetGlobalizationUtil.globalize("scipublications.publicationtypeasset.add"),
            new WorkflowLockedComponentAccess(addForm, itemModel),
            addForm.getSaveCancelSection().getCancelButton());
        
        final PublicationTypeAssetTable table = new PublicationTypeAssetTable(itemModel, typeModel, this);
        setDisplayComponent(table);        
    }
    
    @Override
    public void register(final Page page) {
        super.register(page);
        
        page.addComponentStateParam(this, typeParameter);
    }
    
    protected void setAddVisible(final PageState state) {
        showComponent(state, FORM_KEY);
    }
    
}
