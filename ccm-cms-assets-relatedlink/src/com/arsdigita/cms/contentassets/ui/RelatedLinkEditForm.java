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
package com.arsdigita.cms.contentassets.ui;

import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.form.Hidden;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.SingleSelect;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ContentType;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contentassets.RelatedLink;
import com.arsdigita.cms.contentassets.RelatedLinkConfig;
import com.arsdigita.cms.contentassets.util.RelatedLinkGlobalizationUtil;
import com.arsdigita.cms.contenttypes.Link;
import com.arsdigita.cms.contenttypes.ui.LinkPropertyForm;
import com.arsdigita.cms.contenttypes.ui.LinkSelectionModel;
import com.arsdigita.mimetypes.MimeType;
import com.arsdigita.mimetypes.MimeTypeCollection;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.util.Assert;

import org.apache.log4j.Logger;

/**
 * Form to edit the basic properties of a RelatedLink. This form is a copy&paste
 * of relatedlinkform
 *
 * @version $Revision: #3 $ $Date: 2004/03/30 $
 * @author Scott Seago (sseago@redhat.com)
 */
public class RelatedLinkEditForm extends RelatedLinkPropertyForm {

    private static final Logger logger = Logger.getLogger(
            RelatedLinkEditForm.class);
    private static boolean isHideNewTargetWindow = RelatedLinkConfig.getInstance()
            .isHideNewTargetWindow();
    private static boolean isHideAdditionalResourceFields = RelatedLinkConfig.getInstance()
            .isHideAdditionalResourceFields();
    private String m_linkListName;

    /**
     * Creates a new form to edit the RelatedLink object specified by the item
     * selection model passed in.
     *
     * @param itemModel The ItemSelectionModel to use to obtain the ContentItem
     * to which this link is (or will be) attached
     * @param link The LinkSelectionModel to use to obtain the Link to work on
     */
    public RelatedLinkEditForm(ItemSelectionModel itemModel,
            LinkSelectionModel link, String linkListName) {

        super(itemModel, link, linkListName);
    }

    /**
     * Over-ride super class method to initialize addtional fields specific to
     * <code>RelatedLink</code> content asset.
     */
    @Override
    public void init(FormSectionEvent fse) throws FormProcessException {
        super.init(fse);
        FormData data = fse.getFormData();
        PageState ps = fse.getPageState();
        RelatedLink rl;
        setVisible(ps, false);
        if (isHideAdditionalResourceFields) {
            // Do nothing except protect the poor users from themselves.
        } else {
            if (getLinkSelectionModel().isSelected(ps)) {

                rl = (RelatedLink) getLinkSelectionModel().getSelectedLink(ps);
                if (!rl.getTargetURI().equals("caption")) {
                    //make this form visible because we are editing and its not a caption              
                    setVisible(ps, true);
                }
                //We are editing the link , populate our addtional fields.
                data.put(RelatedLink.RESOURCE_SIZE, rl.getResourceSize());
                if (rl.getResourceType() != null) {
                    data.put(RelatedLink.RESOURCE_TYPE,
                            rl.getResourceType().getMimeType());
                }
                data.put(RelatedLink.LINK_LIST_NAME, rl.getLinkListName());
            }
        }
    }

}
