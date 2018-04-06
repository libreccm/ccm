/*
 * Copyright (C) 2015 LibreCCM Foundation.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package com.arsdigita.cms.portation.modules.assets;

import com.arsdigita.cms.contentassets.Note;
import com.arsdigita.cms.portation.convertion.NgCmsCollection;
import com.arsdigita.cms.portation.modules.contentsection.Asset;
import com.arsdigita.portation.Portable;
import com.arsdigita.portation.modules.core.l10n.LocalizedString;

import java.util.Locale;

/**
 * @author <a href="mailto:tosmers@uni-bremen.de>Tobias Osmers<\a>
 * @version created the 4/6/18
 */
public class SideNote extends Asset implements Portable {
    private LocalizedString text;

    /**
     * Constructor for the ng-object.
     *
     * @param trunkNote the trunk object
     */
    public SideNote(final Note trunkNote) {
        super("Title_" + trunkNote.getDisplayName(),
                            trunkNote.getDisplayName());

        this.text = new LocalizedString();
        final Locale language = Locale.getDefault();
        this.text.addValue(language, trunkNote.getContent());

        NgCmsCollection.sideNotes.put(this.getObjectId(), this);
    }

    public LocalizedString getText() {
        return text;
    }

    public void setText(final LocalizedString text) {
        this.text = text;
    }
}
