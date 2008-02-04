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
package com.arsdigita.cms.ui;

import com.arsdigita.bebop.PageState;
import com.arsdigita.cms.CMS;
import com.arsdigita.cms.ContentBundle;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.ContentType;
import com.arsdigita.cms.Folder;
import com.arsdigita.cms.PageLocations;
import com.arsdigita.cms.Template;
import com.arsdigita.kernel.ACSObject;
import com.arsdigita.web.ParameterMap;
import com.arsdigita.web.URL;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.util.List;
import java.util.Stack;

/**
 * <p>The context bar of the content section UI.</p>
 *
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: ContentSectionContextBar.java 287 2005-02-22 00:29:02Z sskracic $
 */
class ContentSectionContextBar extends WorkspaceContextBar {
    public static final String versionId =
        "$Id: ContentSectionContextBar.java 287 2005-02-22 00:29:02Z sskracic $" +
        "$Author: sskracic $" +
        "$DateTime: 2004/08/17 23:15:09 $";

    private static final Logger s_log = Logger.getLogger
        (ContentSectionContextBar.class);

    protected List entries(final PageState state) {
        final List entries = super.entries(state);
        final ContentSection section = CMS.getContext().getContentSection();
        final Stack folderEntryStack = new Stack();
        String currentFolderLabel = null;
        ParameterMap params = new ParameterMap();
        boolean isTemplate = false;
        BigDecimal templateID = null;

        if (CMS.getContext().hasContentItem()) {
            final ContentItem item = CMS.getContext().getContentItem();
            isTemplate = 
                item.getContentType().equals(ContentType.findByAssociatedObjectType(Template.BASE_DATA_OBJECT_TYPE));
            if (isTemplate) {
                templateID = item.getID();
            }
            ACSObject parent = item.getParent();

            while (!isTemplate && parent != null && parent instanceof ContentItem) {
                if (currentFolderLabel != null) {
                    final URL folderURL = URL.there
                        (state.getRequest(),
                         section.getPath() + "/" + PageLocations.SECTION_PAGE,
                         params);
                    folderEntryStack.push(new Entry(currentFolderLabel, folderURL));
                    currentFolderLabel = null;
                    params = new ParameterMap();
                }
                final ContentItem pitem = (ContentItem) parent;

                if (pitem instanceof Folder) {
                    final Folder folder = (Folder) pitem;
                    parent = folder.getParent();

                    currentFolderLabel = folder.getLabel();
                    if (parent != null || folder.equals(section.getRootFolder())) {
                        params.setParameter
                            (ContentSectionPage.SET_FOLDER, folder.getID());
                    }
                } else if (pitem instanceof ContentBundle) {
                    final ACSObject ppitem = pitem.getParent();

                    if (ppitem != null && ppitem instanceof Folder) {
                        final Folder folder = (Folder) ppitem;

                        parent = folder.getParent();
                        currentFolderLabel = folder.getLabel();
                        if (parent != null || folder.equals(section.getRootFolder())) {
                            params.setParameter
                                (ContentSectionPage.SET_FOLDER, folder.getID());
                        }
                    } else {
                        parent = null;
                    }
                } else {
                    parent = null;
                }
            }
            
        }

        if (isTemplate) {
            params.setParameter
                (ContentSectionPage.SET_TAB, new BigDecimal(ContentSectionPage.CONTENTTYPES_TAB));
            params.setParameter(ContentSectionPage.SET_TEMPLATE, templateID);
        }
        // add section-level entry. if this is for an item page, the URL will be for the root folder.
        final URL url = URL.there
            (state.getRequest(),
             section.getPath() + "/" + PageLocations.SECTION_PAGE,
             params);

            
        final String sectionTitle = lz("cms.ui.content_section");
        final String title = sectionTitle + ": " + section.getName();

        entries.add(new Entry(title, url));

        // add any folders to the path now
        while (!folderEntryStack.empty()) {
            entries.add(folderEntryStack.pop());
        }

        return entries;
    }

    private static String lz(final String key) {
        return (String) ContentSectionPage.globalize(key).localize();
    }
}
