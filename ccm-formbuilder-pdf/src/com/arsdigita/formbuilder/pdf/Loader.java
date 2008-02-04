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
package com.arsdigita.formbuilder.pdf;

import com.arsdigita.formbuilder.BebopObjectType;
import com.arsdigita.formbuilder.MetaObject;
import com.arsdigita.formbuilder.PersistentProcessListener;

import com.arsdigita.loader.PackageLoader;
import com.arsdigita.runtime.ScriptContext;

import org.apache.log4j.Logger;

/**
 * Loader.
 *
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: //apps/forum/dev/src/com/arsdigita/forum/Loader.java#7 $
 */
public class Loader extends PackageLoader {
    public final static String versionId =
        "$Id: //apps/forum/dev/src/com/arsdigita/forum/Loader.java#7 $" +
        "$Author: dennis $" +
        "$DateTime: 2004/08/17 23:26:27 $";

    private static final Logger s_log = Logger.getLogger(Loader.class);

    public void run(final ScriptContext ctx) {
        BebopObjectType type = BebopObjectType.findByClass
            ("forms-cms", PersistentProcessListener.class);

        MetaObject.create(type,
                          "PDF Generator",
                          "PDF Generators",
                          "com.arsdigita.formbuilder.pdf.PDFListener",
                          "com.arsdigita.formbuilder.pdf.PDFDocumentForm");
    }
}
