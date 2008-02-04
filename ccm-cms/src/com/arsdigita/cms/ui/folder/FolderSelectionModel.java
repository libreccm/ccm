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
 *
 */
package com.arsdigita.cms.ui.folder;


import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SingleSelectionModel;
import com.arsdigita.cms.CMS;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.Folder;
import com.arsdigita.cms.ItemSelectionModel;

import java.math.BigDecimal;

/**
 * Keeps track of the selection of an item in a folder. The objects that
 * are selected by this model are all subclasses of {@link
 * com.arsdigita.cms.Folder}.
 *
 * @author <a href="mailto:lutter@arsdigita.com">David Lutterkort</a>
 * @version $Id: FolderSelectionModel.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class FolderSelectionModel
    extends ItemSelectionModel {

    public static final String versionId = "$Id: FolderSelectionModel.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/17 23:15:09 $";

    public FolderSelectionModel(String name) {
        super(Folder.BASE_DATA_OBJECT_TYPE, Folder.BASE_DATA_OBJECT_TYPE, name);
    }

    public FolderSelectionModel(final SingleSelectionModel model) {
        super(Folder.BASE_DATA_OBJECT_TYPE, Folder.BASE_DATA_OBJECT_TYPE, model);
    }

    public Object getSelectedKey(PageState s) {
        // FIXME: this code will go away once parameter models support init listeners
        Object result = super.getSelectedKey(s);
        if ( result == null ) {
            result = getRootFolderID(s);
            setSelectedKey(s, result);
        }
        return result;
    }

    /**
     * Clear the selection by resetting it to the root folder id.
     *
     * @param s represents the curent request.
     */
    public void clearSelection(PageState s) {
        setSelectedKey(s, getRootFolderID(s));
    }

    /**
     * Return the ID of the root folder. By default, this is the root folder
     * of the content section in which the current request is made. If this
     * model is to be used outside a content section, this method has to be
     * overriden appropriately.
     *
     * @param s represents the current request
     * @return the ID of the root folder
     *
     * @pre s != null
     * @post return != null
     */
    protected BigDecimal getRootFolderID(PageState s) {
        ContentSection sec = CMS.getContext().getContentSection();
        return sec.getRootFolder().getID();
    }

    /**
     * Return true, since this selection model will always have
     * a folder selected in it
     */
    public boolean isSelected(PageState s) {
        return true;
    }

}
