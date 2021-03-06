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
package com.arsdigita.search;

import com.arsdigita.auditing.AuditedACSObject;
import com.arsdigita.persistence.OID;
import java.math.BigDecimal;

/**
 * Note
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Id: Note.java 287 2005-02-22 00:29:02Z sskracic $
 **/

class Note extends AuditedACSObject {

    public static final String BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.search.Note";

    public Note(BigDecimal id) {
        super(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    public Note() {
        super(BASE_DATA_OBJECT_TYPE);
    }

    public static Note create(String title,
                              String text) {
        Note note = new Note();
        note.setTitle(title);
        note.setText(text);
        return note;
    }

    public String getTitle() {
        return (String) get("title");
    }

    public void setTitle(String title) {
        set("title", title);
    }

    public String getText() {
        return (String) get("text");
    }

    public void setText(String text) {
        set("text", text);
    }
}
