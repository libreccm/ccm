/*
 * Copyright (c) 2013 Jens Pelzetter
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
package com.arsdigita.cms.contentassets.ui;

import com.arsdigita.bebop.PageState;
import com.arsdigita.cms.contentassets.GenericOrgaUnitTextAsset;
import com.arsdigita.cms.contenttypes.GenericOrganizationalUnit;
import com.arsdigita.cms.contenttypes.ui.GenericOrgaUnitTab;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.xml.Element;

/**
 * A tab for {@link GenericOrgaunizationalUnit}s which displays additional texts. Can be used more
 * than once in the configuration for the tabs, which different keys. The key is used to find the
 * correct text to display.
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class GenericOrgaUnitTextTab implements GenericOrgaUnitTab {

    private String key;

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public void setKey(final String key) {
        this.key = key;
    }

    @Override
    public boolean hasData(final GenericOrganizationalUnit orgaunit,
                           final PageState state) {
        final DataCollection texts = GenericOrgaUnitTextAsset.getTextAssets(orgaunit);
        texts.addEqualsFilter(GenericOrgaUnitTextAsset.TEXT_ASSET_NAME, key);
        final boolean empty = texts.isEmpty();
        texts.close();
        return !empty;
    }

    @Override
    public void generateXml(GenericOrganizationalUnit orgaunit,
                            Element parent,
                            PageState state) {
        final DataCollection texts = GenericOrgaUnitTextAsset.getTextAssets(orgaunit);
        texts.addEqualsFilter(GenericOrgaUnitTextAsset.TEXT_ASSET_NAME, key);
        if (texts.isEmpty()) {
            parent.newChildElement("noText");
        } else {
            texts.next();
            final GenericOrgaUnitTextAsset text = new GenericOrgaUnitTextAsset(texts.getDataObject());
            final Element textElem = parent.newChildElement("text");
            textElem.addAttribute("key", key);
            textElem.setText(text.getContent());
        }

        texts.close();
    }

}
