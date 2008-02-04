/*
 * Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.persistence.pdl;

import com.arsdigita.persistence.metadata.MetadataRoot;
import com.arsdigita.persistence.metadata.Model;
import com.arsdigita.persistence.metadata.ObjectType;
import com.redhat.persistence.metadata.Root;
import com.redhat.persistence.pdl.PDLWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;


/**
 * An class that outputs the PDL associated with a particular
 * metadata set to files.  Each file will contain the metadata for a single
 * model.  The output will go to the a given directory, with each PDL
 * file being named after the fully qualified model name.
 *
 * @author Patrick McNeill
 * @version $Id: PDLOutputter.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class PDLOutputter {
    /**
     * Output the contents of an entire metadata hierarchy as PDL files.
     *
     * @param root the metadata root of the hierarchy to output
     * @param directory the directory to output to
     */
    public static void writePDL(MetadataRoot root, File directory)
        throws IOException {
        Iterator models = root.getModels();

        Root rt = root.getRoot();

        while (models.hasNext()) {
            Model model = (Model) models.next();
            FileWriter writer =
                new FileWriter(new File(directory, model.getName() + ".pdl"));
            PDLWriter out = new PDLWriter(writer);
            for (Iterator it = model.getObjectTypes().iterator();
                 it.hasNext(); ) {
                ObjectType ot = (ObjectType) it.next();
                out.write(rt.getObjectType(ot.getQualifiedName()));
                writer.write("\n\n");
            }
            writer.close();
        }
    }

}
