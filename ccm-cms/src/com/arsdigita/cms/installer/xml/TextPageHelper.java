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
package com.arsdigita.cms.installer.xml;

import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.TextAsset;
import com.arsdigita.cms.TextPage;
import org.apache.log4j.Logger;

/**
 * TextPageHelper
 *
 * @author <a href="mailto:jorris@redhat.com">Jon Orris</a>
 *
 */
public class TextPageHelper extends ContentPageHelper {

    private static final Logger s_log = Logger.getLogger(TextPageHelper.class);
    public TextPageHelper(ContentSection section) {
        super(section);
    }

    public void setBodyText(String body) {
        s_log.warn("Aha. Setting body text to: " + body);
        TextAsset asset = new TextAsset();
        asset.setText(body);

        TextPage page = (TextPage) m_item;
        page.setTextAsset(asset);
        asset.setParent(page);
        asset.setName(page.getName() + "_text_" + page.getID());
        //asset.save();
        //page.save();

        //s_log.warn("Saved the page");

    }
}
