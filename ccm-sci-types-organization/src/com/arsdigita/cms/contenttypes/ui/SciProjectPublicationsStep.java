/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.cms.ContentType;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contentassets.ui.RelatedLinkPropertiesStep;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;

/**
 *
 * @author jensp
 */
public class SciProjectPublicationsStep extends RelatedLinkPropertiesStep {

    protected String m_linkListName = "SciProjectPublications";
    protected ContentType m_contentType = ContentType.findByAssociatedObjectType(
            "com.arsdigita.cms.contenttypes.Publication");

    public SciProjectPublicationsStep(ItemSelectionModel itemModel,
            AuthoringKitWizard parent) {
        super(itemModel, parent);
    }
}
