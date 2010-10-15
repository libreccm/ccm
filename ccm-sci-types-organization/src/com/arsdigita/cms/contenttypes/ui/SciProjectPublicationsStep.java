/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.FormSection;
import com.arsdigita.cms.ContentType;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contentassets.ui.RelatedLinkPropertiesStep;
import com.arsdigita.cms.contentassets.ui.RelatedLinkPropertyForm;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;

/**
 *
 * @author jensp
 */
public class SciProjectPublicationsStep extends RelatedLinkPropertiesStep {

    public SciProjectPublicationsStep(ItemSelectionModel itemModel,
                                      AuthoringKitWizard parent) {
        super(itemModel, parent);
    }

    @Override
    protected FormSection getEditSheet() {
        return new RelatedLinkPropertyForm(getItemSelectionModel(),
                                           getLinkSelectionModel(),
                                           ContentType.
                findByAssociatedObjectType(
                "com.arsdigita.cms.contenttypes.Publication"));
    }
}
