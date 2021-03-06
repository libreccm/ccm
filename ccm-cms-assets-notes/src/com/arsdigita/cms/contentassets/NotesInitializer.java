/*
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
package com.arsdigita.cms.contentassets;

import com.arsdigita.cms.ContentPage;
import com.arsdigita.cms.ContentType;
import com.arsdigita.cms.contentassets.ui.NotesStep;
import com.arsdigita.cms.contentassets.ui.NotesSummary;
import com.arsdigita.cms.contenttypes.ContentAssetInitializer;
import com.arsdigita.cms.dispatcher.SimpleXMLGenerator;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.domain.DomainObjectTraversal;
import com.arsdigita.domain.SimpleDomainObjectTraversalAdapter;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.runtime.DomainInitEvent;

/**
 * Initializes the Notes content item asset at each system startup.
 * 
 * The class just implements all abstract methods of the super class and
 * provides some additions to the init(DomainInitEvent) part of the super
 * initializer.
 */
public class NotesInitializer extends ContentAssetInitializer {

    /**
     * Default constructor, sets its specific manifest file and delegates to 
     * super class.
     */
    public NotesInitializer() {
        super("ccm-cms-assets-notes.pdl.mf");
    }

    /**
     * 
     * @param ev 
     */
    @Override
    public void init(DomainInitEvent ev) {
        super.init(ev);

        ContentType.registerXSLFile(
                null,
                "/themes/heirloom/contentassets/notes/xsl/index.xsl");
        DomainObjectTraversal.registerAdapter(
                Note.BASE_DATA_OBJECT_TYPE,
                new SimpleDomainObjectTraversalAdapter(),
                SimpleXMLGenerator.ADAPTER_CONTEXT);
        SimpleEditStep.addAdditionalDisplayComponent(new NotesSummary());
    }

    /**
     * 
     * @return The base type against which the asset is defined,
     * typically com.arsdigita.cms.ContentPage
     */
    @Override
    public String getBaseType() {
        return ContentPage.BASE_DATA_OBJECT_TYPE;
    }

    /**     
     * @return the path to the XML file defintions for the asset, eg:
     * /WEB-INF/traversal-adapters/com/arsdigita/cms/contentassets/FileAttachments.xml
     */
    @Override
    public String getTraversalXML() {
        return TRAVERSAL_ADAPTER_BASE_DIR + "Notes.xml";
    }

    /**
     * @return The name of the association between the item
     * and the asset, eg 'fileAttachments'.
     */
    @Override
    public String getProperty() {
        return Note.NOTES;
    }

    /**
     * @return The class of the authoring kit step
     */
    @Override
    public Class getAuthoringStep() {
        return NotesStep.class;
    }

    /**
     * @return The label for the authoring step
     */
    @Override
    public GlobalizedMessage getAuthoringStepLabel() {
        return new GlobalizedMessage(
                "com.arsdigita.cms.contentassets.notes_authoring_step_label",
                "com.arsdigita.cms.contentassets.NotesResources");
    }

    /**
     * @return The description for the authoring step
     */
    @Override
    public GlobalizedMessage getAuthoringStepDescription() {
        return new GlobalizedMessage(
                "com.arsdigita.cms.contentassets.notes_authoring_step_description",
                "com.arsdigita.cms.contentassets.NotesResources");
    }

    /**
     * @return The sort key for the authoring step
     */
    @Override
    public int getAuthoringStepSortKey() {
        return NotesConfig.getInstance().getAssetStepSortKey();
    }

}
