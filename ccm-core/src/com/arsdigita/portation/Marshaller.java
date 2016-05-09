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

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Central class for exporting and importing objects of this system stored in
 * the database.
 *
 * <info>Exporting or importing object classes need to implement
 * interface identifiable.</info>
 *
 * @author <a href="mailto:tosmers@uni-bremen.de">Tobias Osmers</a>
 * @version created the 03.02.2016
 */
public class Marshaller {
    private static final Logger log = Logger.getLogger(Marshaller.class);

    // Assigns lists with objects of the same type as values to their typ as
    // key.
    private Map<Class<? extends Identifiable>, List<Identifiable>> classListMap;


    /**
     * Main export method. Organizes the objects into list of the same type
     * and calls a second export method for each list.
     *
     * @param objects All objects to be exported
     * @param format The export style/format e.g. CSV or JSON
     * @param filename The name of the file to be exported to
     */
    public void exportObjects(List<Identifiable> objects, Format format,
                               String filename) {
        putObjects(objects);

        for (Map.Entry<Class<? extends Identifiable>, List<Identifiable>>
            classListEntry : classListMap.entrySet()) {
            exportList(classListEntry.getValue(), classListEntry.getKey(),
                    format, filename);
        }
    }

    /**
     * Organizes a list of different {@link Identifiable} objects into a map
     * assigning lists of the same type to their type as values to a key. The
     * type which all objects of that list have in common is their key.
     * That opens the possibility of being certain of the objects types in
     * the list. Guarantied through this implementation.
     *
     * @param objects list of all objects being organized
     */
    private void putObjects(List<Identifiable> objects) {
        for (Identifiable object : objects) {
            Class<? extends Identifiable> type = object.getClass();

            if (classListMap.containsKey(type)) {
                classListMap.get(type).add(object);
            } else {
                List<Identifiable> values = new ArrayList<>();
                values.add(object);
                classListMap.put(type, values);
            }
        }
    }

    /**
     * Selects the right marshaller for the given type, initializes that
     * marshaller for the given export wishes and calls the export method of
     * that marshaller upon the given list of same typed objects.
     *
     * Naming convention for the export file name:
     *      <basic file name>__<type/class name>.<format>
     *
     * @param list List of objects to be exported of the same type
     * @param type The class of the type
     * @param format The export style
     * @param filename The filename
     * @param <I> The type of the current marshaller
     */
    private <I extends Identifiable> void exportList(List<I> list, Class<?
            extends I> type, Format format, String filename) {
        @SuppressWarnings("unchecked")
        AbstractMarshaller<I> marshaller = (AbstractMarshaller<I>) list.get
                (0).getMarshaller();

        marshaller.prepare(format, filename + "__" + type.toString(),
                false);
        marshaller.exportList(list);
    }

    /**
     * Selects the right marshaller for each file being imported depending on
     * the filename. Therefore the filename has to contain the name of the
     * class this file stores objects for. The marshaller will then be
     * initialized and be called for importing the objects contained in the
     * file being processed.
     *
     * Naming convention for the import file name:
     *      <basic file name>__<type/class name>.<format>
     *
     * @param filenames List of filenames for the files wishing to be imported
     * @param format The import style
     * @param <I> The type of the current marshaller
     */
    public <I extends Identifiable> void importObjects(
            List<String> filenames, Format format) {
        for (String filename : filenames) {
            String[] splitFilename = filename.split("__");
            String className =
                    splitFilename[splitFilename.length].split(".")[0];

            try {
                Class clazz = Class.forName(className);
                @SuppressWarnings("unchecked")
                Class<I> type = clazz.asSubclass(Identifiable.class);

                I instance = null;
                try {
                    instance = type.newInstance();
                } catch (InstantiationException | IllegalAccessException e) {
                    log.error(String.format("Error finding an instance for " +
                            "the given type %s.", type.getName()), e);
                }

                if (instance != null) {
                    @SuppressWarnings("unchecked")
                    AbstractMarshaller<I> marshaller = (AbstractMarshaller<I>)
                            instance.getMarshaller();

                    marshaller.prepare(format, filename, false);
                    marshaller.importFile();
                } else {
                    log.error(String.format("Class instance for type %s has " +
                            "has null value!", type.getName()));
                }
            } catch (ClassNotFoundException e) {
               log.error(String.format("Error finding class for given name: " +
                       "%s.", className), e);
            }
        }
    }


}

