/*
 * Copyright (C) 2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.cms.ui.authoring;

import com.arsdigita.cms.TextAsset;

import com.arsdigita.bebop.PageState;



import com.arsdigita.mimetypes.MimeType;

import com.arsdigita.bebop.table.TableCellRenderer;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Component;

// Renders strings as labels
public class TextAssetBodyLabelCellRenderer implements TableCellRenderer {

    public final static String MIME_TYPE_KEY = 
        TextAsset.MIME_TYPE + "." + MimeType.LABEL;

    public Component getComponent(Table table, PageState state, Object value,
                                  boolean isSelected, Object key,
                                  int row, int column) {
        Label label = null;
        if (TextAsset.CONTENT.equals(key) && value instanceof TextAsset) {
            // We have different styles for different mime types
            TextAsset asset = (TextAsset)value;
            String type = asset.getMimeType().getMimeType()
                .toLowerCase();
            if (type.indexOf("text/xml") > -1 || 
                type.indexOf("text/xsl") > -1 ||
                type.indexOf("text/x-jsp") > -1) {
                label = new Label(asset.getText());
                label.setClassAttr("preformatted");
                label.setOutputEscaping(true);
            } else {
                label = new Label(asset.getText());
                label.setOutputEscaping(false);
            }
        } else if (MIME_TYPE_KEY.equals(key) && value instanceof TextAsset) {
            label = new Label(((TextAsset)value).getMimeType().getLabel(),
                              false);
        } else {
            label = new Label(value.toString());
            label.setOutputEscaping(false);
        }

        return label;
    }
}
