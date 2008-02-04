/*
 * Copyright (C) 2004 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the Open Software License v2.1
 * (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * http://rhea.redhat.com/licenses/osl2.1.html.
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */
package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;

/**
 * @deprecated use {@link
 * com.arsdigita.cms.contentassets.ui.FileAttachmentsStep} instead
 */
public class FileAttachmentsStep
        extends com.arsdigita.cms.contentassets.ui.FileAttachmentsStep {
    public FileAttachmentsStep(ItemSelectionModel itemModel,
                               AuthoringKitWizard parent) {
        super(itemModel, parent);
    }
}
