/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.cms.ui.type;

import com.arsdigita.bebop.ActionLink;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.form.SingleSelect;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.cms.CMS;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.ContentType;
import com.arsdigita.cms.RelationAttributeInterface;
import com.arsdigita.cms.SecurityManager;
import com.arsdigita.cms.dispatcher.Utilities;
import com.arsdigita.cms.ui.BaseItemPane;
import com.arsdigita.cms.ui.ContentSectionRequestLocal;
import com.arsdigita.cms.ui.templates.SectionTemplatesListing;
import com.arsdigita.cms.ui.templates.TemplateCreate;
import com.arsdigita.kernel.ui.ACSObjectSelectionModel;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.toolbox.ui.ActionGroup;
import com.arsdigita.toolbox.ui.Cancellable;
import com.arsdigita.toolbox.ui.Section;
import org.apache.log4j.Logger;

/**
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: ContentTypeItemPane.java 1942 2009-05-29 07:53:23Z terry $
 */
final class ContentTypeItemPane extends BaseItemPane {

    private static Logger s_log = Logger.getLogger(ContentTypeItemPane.class);
    private final ACSObjectSelectionModel m_model;
    private final ContentTypeRequestLocal m_type;
    private final SimpleContainer m_detailPane;
    private final TypeElements m_elements;
    private final AddElement m_elementAddForm;
    private final SectionTemplatesListing m_templates;
    private final TypePermissionsTable m_permissions;
//    private final RelationAttributeSection m_relationAttributes;

    ContentTypeItemPane(final ACSObjectSelectionModel model,
                        final ContentTypeRequestLocal type,
                        final ActionLink editLink,
                        final ActionLink deleteLink) {
        m_model = model;
        m_type = type;

        m_detailPane = new SimpleContainer();
        add(m_detailPane);
        setDefault(m_detailPane);

        m_elements = new TypeElements(m_model);
        m_elementAddForm = new AddElement();

        m_templates =
        new SectionTemplatesListing(new ContentSectionRequestLocal(), m_type);

        m_permissions = new TypePermissionsTable(
                new ContentSectionRequestLocal(), m_type);

        final ActionLink templateAddLink = new ActionLink(new Label(gz(
                "cms.ui.type.template.add")));
        final TemplateCreate templateFormSection = new TemplateCreate(m_model);
        final Form templateForm = new CancellableForm("AddTemplate",
                                                      templateFormSection.
                getSaveCancelSection().getCancelButton());
        templateForm.add(templateFormSection);
        add(templateForm);

        /*final ActionLink permissionAddLink = new ActionLink(new Label(gz(
                "cms.ui.type.permission.add")));
        final TypePermissions permissionsSection = new TypePermissions(m_model);
        final Form permissionsForm = new CancellableForm("AddPermission",
                                                         permissionsSection.
                getSaveCancelSection().getCancelButton());
        permissionsForm.add(permissionsSection);
        add(permissionsForm);*/

        final AddTextElement textForm = new AddTextElement(m_model);
        add(textForm);

        final AddNumberElement numberForm = new AddNumberElement(m_model);
        add(numberForm);

        final AddDateElement dateForm = new AddDateElement(m_model);
        add(dateForm);

        final AddTextAssetElement assetForm = new AddTextAssetElement(m_model);
        add(assetForm);

        final AddImageElement imageForm = new AddImageElement(m_model);
        add(imageForm);

        final AddFileElement fileForm = new AddFileElement(m_model);
        add(fileForm);

        final AddContentItemElement itemForm =
                                    new AddContentItemElement(m_model);
        add(itemForm);

        m_detailPane.add(new SummarySection(editLink, deleteLink));
        m_detailPane.add(new RelationAttributeSection());

        m_detailPane.add(new TypeSecurityContainer(new ElementSection()));
        m_detailPane.add(new TemplateSection(templateAddLink));

        //m_detailPane.add(new PermissionsSection(permissionAddLink));
        m_detailPane.add(new PermissionsSection());

        connect(templateAddLink, templateForm);
       // connect(permissionAddLink, permissionsForm);

        final SingleSelect elementSelect =
                           m_elementAddForm.getElementTypeSelect();

        connect(m_elementAddForm, elementSelect,
                AddElement.TEXT_ELEMENT, textForm);
        connect(textForm);

        connect(m_elementAddForm, elementSelect,
                AddElement.NUMBER_ELEMENT, numberForm);
        connect(numberForm);

        connect(m_elementAddForm, elementSelect,
                AddElement.DATE_ELEMENT, dateForm);
        connect(dateForm);

        connect(m_elementAddForm, elementSelect,
                AddElement.TEXT_ASSET_ELEMENT, assetForm);
        connect(assetForm);

        connect(m_elementAddForm, elementSelect,
                AddElement.IMAGE_ELEMENT, imageForm);
        connect(imageForm);

        connect(m_elementAddForm, elementSelect,
                AddElement.FILE_ELEMENT, fileForm);
        connect(fileForm);

        connect(m_elementAddForm, elementSelect,
                AddElement.CONTENT_ITEM_ELEMENT, itemForm);
        connect(itemForm);
    }

    // XXX A temporary, low-impact fix.
    private class CancellableForm extends Form implements Cancellable {

        private final Submit m_cancel;

        CancellableForm(final String name, final Submit cancel) {
            super(name);

            m_cancel = cancel;
        }

        public final boolean isCancelled(final PageState state) {
            return m_cancel.isSelected(state);
        }
    }

    private class SummarySection extends Section {

        SummarySection(final ActionLink editLink,
                       final ActionLink deleteLink) {
            setHeading(new Label(gz("cms.ui.type.details")));

            final ActionGroup group = new ActionGroup();
            setBody(group);

            group.setSubject(new ContentTypePropertyList(m_type));

            group.addAction(new TypeSecurityContainer(editLink));
            group.addAction(new TypeSecurityContainer(deleteLink));
        }
    }

    private class ElementSection extends Section {

        ElementSection() {
            setHeading(new Label(gz("cms.ui.type.elements")));

            final ActionGroup group = new ActionGroup();
            setBody(group);

            group.setSubject(m_elements);
            group.addAction(m_elementAddForm);
        }

        @Override
        public final boolean isVisible(final PageState state) {
            return m_model.isSelected(state) && isDynamicType(state)
                   && !ContentSection.getConfig().getHideUDCTUI();
        }
    }

    private class TemplateSection extends Section {

        TemplateSection(final ActionLink templateAddLink) {
            setHeading(new Label(gz("cms.ui.type.templates")));

            final ActionGroup group = new ActionGroup();
            setBody(group);

            group.setSubject(m_templates);
            group.addAction(new TypeSecurityContainer(templateAddLink));
        }

        @Override
        public final boolean isVisible(final PageState state) {
            return m_model.isSelected(state) && !isDynamicType(state);
        }
    }

    private class PermissionsSection extends Section {

        public PermissionsSection() {
            setHeading(new Label(gz("cms.ui.type.permissions")));

            final ActionGroup group = new ActionGroup();
            setBody(group);

            group.setSubject(m_permissions);
            //group.addAction(new TypeSecurityContainer(permissionsAddLink));
        }

        @Override
        public final boolean isVisible(final PageState state) {
            return m_model.isSelected(state) && !isDynamicType(state);
        }
    }

    private class RelationAttributeSection extends Section {

        RelationAttributeSection() {
            setHeading(new Label(gz("cms.ui.type.attributes")));

            setBody(new RelationAttributeList(m_type));
        }

        @Override
        public final boolean isVisible(final PageState state) {
// ISt es möglich, den folgenden Code nur einmal zu haben?? Kann man auf die isVisible von RelationAttributeList
            // zurückgreifen??
            boolean retVal = false;
            ContentType ct = (ContentType) m_type.getContentType(state);
            ContentItem ci = null;

            try {
                Class<? extends ContentItem> clazz = Class.forName(ct.
                        getClassName()).asSubclass(ContentItem.class);
                ci = clazz.newInstance();
                if (ci instanceof RelationAttributeInterface) {
                    RelationAttributeInterface rai =
                                               (RelationAttributeInterface) ci;
                    retVal = rai.hasRelationAttributes();
                }
                ci.delete();
            } catch (Exception ex) {
                //retVal = false;
            }

            return retVal;
        }
    }

    @Override
    public final void register(final Page page) {
        super.register(page);

        page.addActionListener(new ActionListener() {

            public final void actionPerformed(final ActionEvent e) {
                final PageState state = e.getPageState();

                if (state.isVisibleOnPage(ContentTypeItemPane.this)
                    && m_model.isSelected(state)
                    && !userCanEdit(state)) {
                    m_templates.getRemoveColumn().setVisible(state, false);
                    m_templates.getDefaultColumn().setVisible(state, false);
                    m_elements.getTable().getColumn(3).setVisible(state, false);
                }
            }
        });
    }

    /**
     * Unregister a content type from a content section.
     * XXX domlay
     */
    private void removeType(final PageState state) {
        final ContentSection section = CMS.getContext().getContentSection();

        section.removeContentType(m_type.getContentType(state));

        section.save();
    }

    /**
     * Determine if the current user has access to edit the content type
     * XXX domlay
     */
    protected static boolean userCanEdit(final PageState state) {
        SecurityManager sm = Utilities.getSecurityManager(state);
        return sm.canAccess(state.getRequest(),
                            SecurityManager.CONTENT_TYPE_ADMIN);
    }

    /**
     * utility method
     * get the DataObject from the DataBase, returns true if it
     * is a modifiable type, false otherwise.
     *
     * XXX domlay
     */
    protected final boolean isDynamicType(final PageState state) {
        final ContentType type = m_type.getContentType(state);

        final String objectTypeString =
                     "com.arsdigita.persistence.DynamicObjectType";
        final DataCollection collection = SessionManager.getSession().retrieve(
                objectTypeString);
        collection.addEqualsFilter("dynamicType", type.getAssociatedObjectType());

        final boolean b = collection.next();

        collection.close();

        return b;
    }
}
