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
package com.arsdigita.persistence.pdl;

import com.arsdigita.util.UncheckedWrapperException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

/**
 * An implementation of PDLSource that loads an individual file.
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #6 $ $Date: 2004/08/16 $
 **/

public class FileSource implements PDLSource {

    

    private final File m_file;

    /**
     * Constructs a PDLSource with the contents of the given file.
     *
     * @param file the PDL file
     **/

    public FileSource(File file) {
        m_file = file;
    }

    /**
     * Constructs a PDLSource with the contents of the given file.
     *
     * @param filename the name of the PDL file
     **/

    public FileSource(String filename) {
        this(new File(filename));
    }

    /**
     * Parses the contents of this PDLSource using the given compiler.
     *
     * @param compiler the PDLCompiler used to parse this PDLSource
     **/

    public void parse(PDLCompiler compiler) {
        try {
            compiler.parse(new FileReader(m_file), m_file.getAbsolutePath());
        } catch (FileNotFoundException e) {
            throw new UncheckedWrapperException(e);
        }
    }

}
