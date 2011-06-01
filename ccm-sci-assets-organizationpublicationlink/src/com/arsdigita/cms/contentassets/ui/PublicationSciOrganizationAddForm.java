/*
 * Copyright (c) 2011 Jens Pelzetter,
 * for the Center of Social Politics of the University of Bremen
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

import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.cms.ContentType;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.GenericOrganizationalUnit;
import com.arsdigita.cms.contenttypes.Publication;
import com.arsdigita.cms.contenttypes.PublicationWithOrganization;
import com.arsdigita.cms.ui.ItemSearchWidget;
import com.arsdigita.cms.ui.authoring.BasicItemForm;
import java.math.BigDecimal;

/**
 *
 * @author Jens Pelzetter 
 */
public class PublicationSciOrganizationAddForm
        extends BasicItemForm
        implements FormProcessListener,
                   FormInitListener {
    
    private ItemSearchWidget itemSearch;
    private final String ITEM_SEARCH = "organizations";
    private String orgaClassName;
    
    public PublicationSciOrganizationAddForm(ItemSelectionModel itemModel,
                                             String orgaClassName) {
        super("OrganizationsAddForm", itemModel);
        this.orgaClassName = orgaClassName;
    }
    
    @Override
    public void addWidgets() {
        add(new Label((String) SciOrganizationPublicationGlobalizationUtil.
                globalize("sciorganization.ui.selectOrganization").localize()));
        itemSearch = new ItemSearchWidget(ITEM_SEARCH, ContentType.
                findByAssociatedObjectType(orgaClassName));
        add(itemSearch);
    }
    
    @Override
    public void init(FormSectionEvent fse) throws FormProcessException {
        PageState state = fse.getPageState();
        
        setVisible(state, true);
    }
    
    @Override
    public void process(FormSectionEvent fse) throws FormProcessException {
        FormData data = fse.getFormData();
        PageState state = fse.getPageState();
        Publication pub = (Publication) getItemSelectionModel().
                getSelectedObject(state);
        PublicationWithOrganization publication =
                                    new PublicationWithOrganization(
                pub.getOID());
        
        if (this.getSaveCancelSection().getSaveButton().isSelected(state)) {
            publication.addOrganization((GenericOrganizationalUnit) data.get(
                    ITEM_SEARCH));            
        }
        
        init(fse);
    }
}
