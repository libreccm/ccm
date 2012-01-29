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
import com.arsdigita.cms.contenttypes.ContentAssetInitializer;
import com.arsdigita.cms.dispatcher.SimpleXMLGenerator;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.domain.DomainObjectTraversal;
import com.arsdigita.domain.SimpleDomainObjectTraversalAdapter;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.cms.contentassets.ui.NotesStep;
import com.arsdigita.cms.contentassets.ui.NotesSummary;
import com.arsdigita.runtime.DomainInitEvent;

public class NotesInitializer extends ContentAssetInitializer {
    public NotesInitializer() {
        super( "ccm-cms-assets-notes.pdl.mf" );
    }

    public String getTraversalXML() {
        return "/WEB-INF/traversal-adapters/com/arsdigita/cms/contentassets/Notes.xml";
    }

    public String getProperty() {
        return Note.NOTES;
    }

    public String getBaseType() {
        return ContentPage.BASE_DATA_OBJECT_TYPE;
    }

    public Class getAuthoringStep() {
        return NotesStep.class;
    }

    public GlobalizedMessage getAuthoringStepLabel() {
        return new GlobalizedMessage(
            "com.arsdigita.cms.contentassets.notes_authoring_step_label",
            "com.arsdigita.cms.contentassets.NotesResources"
        );
    }

    public GlobalizedMessage getAuthoringStepDescription() {
        return new GlobalizedMessage(
            "com.arsdigita.cms.contentassets.notes_authoring_step_description",
            "com.arsdigita.cms.contentassets.NotesResources"
        );
    }

    public int getAuthoringStepSortKey() {
        return 3;
    }

    // public void init( LegacyInitEvent ev ) {
    @Override
    public void init( DomainInitEvent ev ) {
        super.init( ev );

        ContentType.registerXSLFile( 
                        null, 
                        "/themes/heirloom/contentassets/notes/xsl/index.xsl" );
        DomainObjectTraversal.registerAdapter( 
                        Note.BASE_DATA_OBJECT_TYPE,
                        new SimpleDomainObjectTraversalAdapter(),
                        SimpleXMLGenerator.ADAPTER_CONTEXT );
        SimpleEditStep.addAdditionalDisplayComponent(new NotesSummary());
    }
}
