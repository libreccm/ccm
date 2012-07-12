/*
 * Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
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
 */
package com.arsdigita.cms.contentassets.ui;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.MapComponentSelectionModel;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.ParameterSingleSelectionModel;
import com.arsdigita.bebop.Resettable;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.ParameterEvent;
import com.arsdigita.bebop.event.ParameterListener;
import com.arsdigita.bebop.parameters.ParameterData;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ReusableImageAsset;
import com.arsdigita.cms.contentassets.ItemImageAttachment;
import com.arsdigita.cms.ui.ImageComponent;
import com.arsdigita.cms.ui.ImageLibraryComponent;
import com.arsdigita.cms.ui.ImageUploadComponent;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.util.Assert;
import com.arsdigita.util.StringUtils;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;

public class ImageStepEdit extends SimpleContainer
        implements Resettable, FormProcessListener, FormInitListener {

    private static final Logger s_log = Logger.getLogger(ImageStepEdit.class);
    private final ImageStep m_imageStep;
    private final StringParameter m_imageComponentKey;
    private final MapComponentSelectionModel m_imageComponent;

    public ImageStepEdit(ImageStep step) {
        m_imageStep = step;

        m_imageComponentKey = new StringParameter("imageComponent");

        ParameterSingleSelectionModel componentModel =
                new ParameterSingleSelectionModel(m_imageComponentKey);
        m_imageComponent =
                new MapComponentSelectionModel(componentModel, new HashMap());

        Map selectors = m_imageComponent.getComponentsMap();

        ImageLibraryComponent library = new ImageLibraryComponent();
        library.getForm().addInitListener(this);
        library.getForm().addProcessListener(this);
        library.addUploadLink(new ActionListener() {

            public void actionPerformed(ActionEvent ev) {
                setImageComponent(ev.getPageState(), ImageComponent.UPLOAD);
            }
        });
        selectors.put(ImageComponent.LIBRARY, library);
        add(library);
        
        ImageUploadComponent upload = new ImageUploadComponent();
        upload.getForm().addInitListener(this);
        upload.getForm().addProcessListener(this);
        selectors.put(ImageComponent.UPLOAD, upload);
        add(upload);

    }

    @Override
    public void register(Page p) {
        super.register(p);

        Map componentsMap = m_imageComponent.getComponentsMap();
        Iterator i = componentsMap.keySet().iterator();
        while (i.hasNext()) {
            Object key = i.next();
            Component component = (Component) componentsMap.get(key);

            p.setVisibleDefault(component, ImageComponent.LIBRARY.equals(key));
        }

        p.addComponentStateParam(this, m_imageComponentKey);
    }

    Iterator getImageComponents() {
        return m_imageComponent.getComponentsMap().values().iterator();
    }

    private ImageComponent getImageComponent(PageState ps) {
        if (!m_imageComponent.isSelected(ps)) {
            if (s_log.isDebugEnabled()) {
                s_log.debug("No component selected");
                s_log.debug("Selected: " + m_imageComponent.getComponent(ps));
            }

            m_imageComponent.setSelectedKey(ps, ImageComponent.UPLOAD);
        }

        return (ImageComponent) m_imageComponent.getComponent(ps);

    }

    private void setImageComponent(PageState ps, final String activeKey) {
        m_imageComponent.setSelectedKey(ps, activeKey);

        if (s_log.isDebugEnabled()) {
            s_log.debug("Selected component: " + activeKey);
        }

        Map componentsMap = m_imageComponent.getComponentsMap();
        Iterator i = componentsMap.keySet().iterator();
        while (i.hasNext()) {
            Object key = i.next();
            Component component = (Component) componentsMap.get(key);

            boolean isVisible = activeKey.equals(key);

            if (s_log.isDebugEnabled()) {
                s_log.debug("Key: " + key + "; Visibility: " + isVisible);
            }

            ps.setVisible(component, isVisible);
        }
    }

    public void init(FormSectionEvent event)
            throws FormProcessException {
        PageState ps = event.getPageState();

        ItemImageAttachment attachment = m_imageStep.getAttachment(ps);
        if (null == attachment) {
            // XXX: Do something
        }
    }

    public void process(FormSectionEvent event) throws FormProcessException {
        PageState ps = event.getPageState();
        ImageComponent component = getImageComponent(ps);

        if (!component.getSaveCancelSection().getSaveButton().isSelected(ps)) {
            return;
        }

        ContentItem item = m_imageStep.getItem(ps);
        if (null == item) {
            s_log.error("No item selected in ImageStepEdit",
                    new RuntimeException());
            return;
        }

        ReusableImageAsset image = component.getImage(event);

        ItemImageAttachment attachment = m_imageStep.getAttachment(ps);
        if (null == attachment) {
            attachment = new ItemImageAttachment(item, image);
        }
        attachment.setCaption(component.getCaption(event));

        // We only set the description and title based on the UI in
        // the case where getIsImageStepDescriptionAndTitleShown is true.
        // Otherwise, we leave this as the default value.  This means 
        // existing values are not overwritten if the image is edited when
        // isImageStepDescriptionAndTitleShown is false.
        if (ItemImageAttachment.getConfig().getIsImageStepDescriptionAndTitleShown()) {
            attachment.setDescription(component.getDescription(event));
            attachment.setTitle(component.getTitle(event));
        }
        attachment.setUseContext(component.getUseContext(event));
    }

    public void reset(PageState state) {
        Page p = state.getPage();
        
        Map componentsMap = m_imageComponent.getComponentsMap();
        Iterator i = componentsMap.keySet().iterator();
        while (i.hasNext()) {
            Object key = i.next();
            Component component = (Component) componentsMap.get(key);

            p.setVisibleDefault(component, ImageComponent.LIBRARY.equals(key));
        }
    }

    private class UniqueUseContextListener implements ParameterListener {

        public void validate(ParameterEvent ev)
                throws FormProcessException {
            PageState ps = ev.getPageState();
            ParameterData data = ev.getParameterData();

            ContentItem item = m_imageStep.getItem(ps);
            Assert.exists(item, ContentItem.class);

            String value = data.getValue().toString();
            value = StringUtils.trimleft(value);
            if (StringUtils.emptyString(value)) {
                value = null;
            }
            data.setValue(value);

            DataCollection attachments =
                    ItemImageAttachment.getImageAttachments(item);
            attachments.addEqualsFilter(ItemImageAttachment.USE_CONTEXT, value);

            try {
                if (attachments.next()) {
                    ItemImageAttachment attachment = m_imageStep.getAttachment(ps);
                    BigDecimal dupAttachmentID = (BigDecimal) attachments.get(ItemImageAttachment.ID);

                    if (null == attachment
                            || !attachment.getID().equals(dupAttachmentID)) {
                        data.addError("There is already an image for this "
                                + "item with this context");
                    }
                }
            } finally {
                attachments.close();
            }
        }
    }
}
