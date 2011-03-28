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
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.ContentType;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.Link;
import com.arsdigita.cms.contenttypes.ui.LinkPropertyForm;
import com.arsdigita.cms.contenttypes.ui.LinkSelectionModel;
import com.arsdigita.cms.contentassets.RelatedLink;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.mimetypes.MimeType;
import com.arsdigita.mimetypes.MimeTypeCollection;
import com.arsdigita.util.Assert;
import org.apache.log4j.Logger;

/**
 * Form to edit the basic properties of a RelatedLink. This form
 * extends LinkPropertyForm in order to create items of the correct
 * subclass and set the linkOwner property. Users have found the additional
 * fields confusing at authoring time (resourceSize and resourceType) so we
 * have added a configuration parameter that allows us to hide them on a
 * site wide basis.
 * @version $Revision: #3 $ $Date: 2004/03/30 $
 * @author Scott Seago (sseago@redhat.com)
 */
public class RelatedLinkPropertyForm extends LinkPropertyForm {

    private static final Logger logger = Logger.getLogger(RelatedLinkPropertyForm.class);
    private static boolean isHideAdditionalResourceFields = ContentSection.getConfig().isHideAdditionalResourceFields();
    private String m_linkListName;
    
    /**
     * Creates a new form to edit the RelatedLink object specified
     * by the item selection model passed in.
     * @param itemModel The ItemSelectionModel to use to obtain the 
     *    ContentItem to which this link is (or will be) attached
     * @param link The LinkSelectionModel to use to obtain the 
     *    Link to work on
     */
    public RelatedLinkPropertyForm(ItemSelectionModel itemModel,
            LinkSelectionModel link, String linkListName) {

        this(itemModel, link, linkListName, null);
    }

    public RelatedLinkPropertyForm(ItemSelectionModel itemModel,
            LinkSelectionModel link, String linkListName, ContentType contentType) {

        super(itemModel, link, contentType);
        logger.debug(String.format("linkListName = %s", linkListName));
        m_linkListName = linkListName;
    }

    @Override
    protected void addWidgets() {

        super.addWidgets();

        if (isHideAdditionalResourceFields) {
            // Do nothing except protect the poor users from themselves.
        } else {
            //Hack to get the form layout right.
            add(new Label(""));
            add(new Label(
                    new GlobalizedMessage("com.arsdigita.cms.contentassets.related_link_resourceSize",
                    "com.arsdigita.cms.contentassets.RelatedLinkResources")));
            TextField resSize = new TextField(new StringParameter(RelatedLink.RESOURCE_SIZE));
            add(resSize);

            add(new Label(
                    new GlobalizedMessage("com.arsdigita.cms.contentassets.related_link_resourceType",
                    "com.arsdigita.cms.contentassets.RelatedLinkResources")));
            SingleSelect resType = new SingleSelect(new StringParameter(RelatedLink.RESOURCE_TYPE));
            addMimeOptions(resType);
            add(resType);           
        }

        Hidden linkListName = new Hidden(new StringParameter(RelatedLink.LINK_LIST_NAME));
        add(linkListName);
    }

    /**
     * Add mime-type options to the option group by loading all mime
     * types which match a certain prefix from the database
     *
     * @param w The mime type widget to which options should be added
     *
     */
    public static void addMimeOptions(SingleSelect w) {
        MimeTypeCollection types;
        types = MimeType.getAllMimeTypes();
        while (types.next()) {
            MimeType type = types.getMimeType();
            w.addOption(new Option(type.getMimeType(), type.getLabel()));
        }
    }

    /** 
     * Take care of basic RelatedLink creation steps. Creates the
     * RelatedLink and sets the linkOwner property.
     *
     * @param s the PageState
     * @return the newly-created RelatedLink
     */
    @Override
    protected Link createLink(PageState s) {
        ContentItem item = getContentItem(s);
        Assert.exists(item, ContentItem.class);
        RelatedLink link = new RelatedLink();

        // remove the following line if we make Link extend ACSObject
        //link.setName(item.getName() + "_link_" + item.getID());
        // set the owner of the link
        link.setLinkOwner(item);

        return link;
    }

    /**
     * Over-ride super class method to initialize addtional fields specific
     * to <code>RelatedLink</code> content asset.
     */
    @Override
    public void init(FormSectionEvent fse) throws FormProcessException {
        super.init(fse);
        FormData data = fse.getFormData();
        PageState ps = fse.getPageState();
        RelatedLink rl;
        if (isHideAdditionalResourceFields) {
            // Do nothing except protect the poor users from themselves.
        } else {
            if (getLinkSelectionModel().isSelected(ps)) {
                //We are editing the link , populate our addtional fields.
                rl = (RelatedLink) getLinkSelectionModel().getSelectedLink(ps);
                data.put(RelatedLink.RESOURCE_SIZE, rl.getResourceSize());
                if (rl.getResourceType() != null) {
                    data.put(RelatedLink.RESOURCE_TYPE, rl.getResourceType().getMimeType());
                }
                data.put(RelatedLink.LINK_LIST_NAME, rl.getLinkListName());
            } else {
                // New Link creation , clear the fields.
                data.put(RelatedLink.RESOURCE_SIZE, null);
                data.put(RelatedLink.RESOURCE_TYPE, null);
                data.put(RelatedLink.LINK_LIST_NAME, m_linkListName);
            }
        }
    }

    /** 
     * over-ride super class method to set extended properties for
     * <code>RelatedLink</code>.
     */
    @Override
    protected void setLinkProperties(Link link, FormSectionEvent fse) {
        RelatedLink rl = (RelatedLink) (link);
        FormData data = fse.getFormData();
        if (isHideAdditionalResourceFields) {
            // We are not using these but let's try to set some reasonable defaults.
            rl.setResourceSize("");
            rl.setResourceType(MimeType.loadMimeType("text/html"));
        } else {
            rl.setResourceSize((String) data.get(RelatedLink.RESOURCE_SIZE));
            String typeName = (String) data.get(RelatedLink.RESOURCE_TYPE);
            MimeType mType = MimeType.loadMimeType(typeName);
            rl.setResourceType(mType);
        }
        rl.setLinkListName((String) data.get(RelatedLink.LINK_LIST_NAME));
        super.setLinkProperties(link, fse);
    }
}
