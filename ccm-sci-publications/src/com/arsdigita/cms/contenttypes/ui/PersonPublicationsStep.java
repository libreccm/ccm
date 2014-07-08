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

import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SegmentedPanel;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.GenericPerson;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;

/**
 *
 * @author Jens Pelzetter
 * @version $Id$
 */
public class PersonPublicationsStep extends SimpleEditStep {

    //private String ADD_PUBLICATION_TO_PERSON_SHEET_NAME = "PersonPublicationStep";
    
    public PersonPublicationsStep(final ItemSelectionModel itemModel, final AuthoringKitWizard parent) {
        this(itemModel, parent, null);
    }
    
    public PersonPublicationsStep(final ItemSelectionModel itemModel, 
                                 final AuthoringKitWizard parent, 
                                 final String prefix) {
        super(itemModel, parent, prefix);                
        
        final SegmentedPanel panel = new SegmentedPanel();
        final Label personPubsHeader = new Label(new PrintListener() {

            @Override
            public void prepare(final PrintEvent event) {
                final PageState state = event.getPageState();
                final Label target = (Label) event.getTarget();
                
                final GenericPerson person = (GenericPerson) itemModel.getSelectedItem(state);                
                
                target.setLabel(PublicationGlobalizationUtil.globalize("person.ui.publications.header", 
                                                       new String[]{person.getFullName()}));
            }
        });        
        final PersonPublicationsTable publicationsTable = new PersonPublicationsTable(itemModel);        
        panel.addSegment(personPubsHeader, publicationsTable);
        
        final SimpleContainer aliasHeader = new SimpleContainer();
        final Label personAliasPubsHeader = new Label(new PrintListener() {

            @Override
            public void prepare(final PrintEvent event) {
                final PageState state = event.getPageState();
                final Label target = (Label) event.getTarget();
                
                final GenericPerson person = (GenericPerson) itemModel.getSelectedItem(state);
                final GenericPerson alias = person.getAlias();
                
                if (alias == null) {
                    target.setLabel("");
                } else {
                    target.setLabel(PublicationGlobalizationUtil.globalize(
                                            "person.ui.publications.header", 
                                    new String[]{alias.getFullName()}));
                }
            }
        });
        final Label personAliasOfHeader = new Label(new PrintListener() {

            @Override
            public void prepare(final PrintEvent event) {
                final PageState state = event.getPageState();
                final Label target = (Label) event.getTarget();
                
                final GenericPerson person = (GenericPerson) itemModel.getSelectedItem(state);
                final GenericPerson alias = person.getAlias();
                
                if (alias == null) {
                    target.setLabel("");
                } else {
                    target.setLabel(PublicationGlobalizationUtil.globalize(
                                        "person.ui.publications.header.alias_of", 
                                    new String[]{person.getFullName()}));
                }
                
            }
        });
        
        
        aliasHeader.add(personAliasPubsHeader);
        aliasHeader.add(personAliasOfHeader);
        
        final PersonPublicationsTable aliasPublicationsTable = new 
                                      PersonPublicationsTable(itemModel, true);
        
        panel.addSegment(aliasHeader, aliasPublicationsTable);
        
        //setDisplayComponent(publicationsTable);
        setDisplayComponent(panel);
    }
    
    
    
}
