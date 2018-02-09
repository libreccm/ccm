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
package com.arsdigita.portation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Abstract class responsible for ex- and importing entity-objects to several
 * file-formats. Every entity-class (e.g. DocRepo.File) needs to have its own
 * extension of this class to override the abstract methods, making it
 * possible to ex- or import that extending entity-class (e.g. DocRepo
 * .FileMarshal).
 *
 * @author <a href="mailto:tosmers@uni-bremen.de>Tobias Osmers</a>
 * @version created on 2/10/16
 */
public abstract class AbstractMarshaller<P extends Portable> {
    private Format format;
    private String pathName;
    private String fileName;
    private boolean indentation;

    /**
     * Passes the parameters for the file to which the ng objects shall be
     * exported to down to its corresponding {@link AbstractMarshaller<P>}
     * and then requests this {@link AbstractMarshaller<P>} to start the
     * export of all its ng objects.
     *
     * @param format The format of the file to which will be exported to
     * @param pathName The name for the file
     * @param indentation Whether to use indentation in the file
     */
    public abstract void marshallAll(final Format format,
                                     final String pathName,
                                     final boolean indentation);

    /**
     * Prepares parameters for the export:
     *
     * @param format format of the exported file (e.g. xml)
     * @param pathName path for the exported files
     * @param fileName filenames
     * @param indentation whether to use indentation or not
     */
    protected void prepare(final Format format,
                           final String pathName,
                           final String fileName,
                           final boolean indentation) {
        this.format = format;
        this.pathName = pathName;
        this.fileName = fileName;
        this.indentation = indentation;
    }

    /**
     * Exports list of the same objects.
     *
     * @param exportList List of same objects
     */
    protected void exportList(final List<P> exportList) {
        ObjectMapper objectMapper = null;
        switch (format) {
            case XML:
                // xml extension to filename
                fileName += ".xml";
                // xml mapper configuration
                final JacksonXmlModule module = new JacksonXmlModule();
                module.setDefaultUseWrapper(false);
                objectMapper = new XmlMapper(module);
                break;
        }

        FileWriter fileWriter = null;
        try {
            final Path filePath = Paths.get(pathName, fileName);
            Files.createFile(filePath);

            final File file = new File(filePath.toString());
            fileWriter = new FileWriter(file);
        } catch (FileAlreadyExistsException e) {
            // destination file already exists
        } catch (IOException ex) {
            System.err.printf("ERROR Unable to open a fileWriter for the file" +
                    " with the name %s.\n %s\n", fileName, ex);
        }

        if (objectMapper != null && fileWriter != null) {
            if (indentation) {
                objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
            }
            objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);

            String line = null;
            for (P object : exportList) {
                try {
                    line = objectMapper.writeValueAsString(object);
                } catch (JsonProcessingException ex) {
                    System.err.printf("ERROR Unable to write object of class " +
                                    "%s as XML string with name %s " +
                                    "in file %s.\n %s\n", object.getClass(),
                                    object.toString(), fileName, ex);
                }
                if (line != null) {
                    try {
                        fileWriter.write(line);
                        fileWriter.write(System.getProperty("line.separator"));
                    } catch (IOException ex) {
                        System.err.printf("ERROR Unable to write to file with" +
                                " the name %s.\n %s\n", fileName, ex);
                    }
                }
            }

            try {
                fileWriter.close();
            } catch (IOException ex) {
                System.err.printf("ERROR Unable to close a fileWriter for" +
                        " a file with the name %s.\n %s\n", fileName, ex);
            }
        }
    }
}
