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
package com.arsdigita.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.OutputStream;
import java.io.FileNotFoundException;
import java.util.zip.ZipOutputStream;
import java.util.zip.ZipEntry;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;
import java.text.NumberFormat;
import com.arsdigita.util.Assert;
import com.arsdigita.kernel.Kernel;

import org.apache.log4j.Logger;


/**
 * Commonly used file utilities.
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @author Randy Graebner &lt;randyg@alum.mit.edu&gt;
 * @version $Revision: #10 $ $Date: 2004/08/16 $
 **/

public final class Files {

    public final static String versionId = "$Id: Files.java 738 2005-09-01 12:36:52Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    private static final Logger s_log =
        Logger.getLogger(Files.class);

    public final static int OVERWRITE = 0;
    public final static int UPDATE = 1;
    public final static int IGNORE_EXISTING = 2;

    private Files() {}

    public static void delete(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                delete(files[i]);
            }
        }

        if (s_log.isDebugEnabled()) {
            s_log.debug("Deleting " + file.toString());
        }
        file.delete();
    }

    public static void copy(File from, File to) throws IOException {
        copy(from,to,OVERWRITE);
    }

    public static void copy(File from, File to, int mode) throws IOException {
        if (to.isDirectory()) {
            to = new File(to, from.getName());
        }

        if (from.isDirectory()) {
            to.mkdir();
            if (!(to.exists() && to.isDirectory())) {
                throw new IOException("couldn't make directory: " + to);
            }
            File[] files = from.listFiles();
            if (files == null) {
                throw new IOException("could not read directory: " + from);
            }
            for (int i = 0; i < files.length; i++) {
                copy(files[i], to, mode);
            }
        } else {
            if (to.exists()) {
                if ( mode == IGNORE_EXISTING ) {
                    if (s_log.isDebugEnabled()) {
                        s_log.debug("Skipping copy of '" + from.toString() + "' because target exists.");
                    }
                    return;
                }
                if ( mode == UPDATE &&
                     to.lastModified() > from.lastModified() ) {
                    if (s_log.isDebugEnabled()) {
                        s_log.debug("Skipping copy of '" + from.toString() + "' because target is newer.");
                    }
                    return;
                }
                if (!to.canWrite()) {
                    throw new IOException("can not write file: " + to);
                }
            }
            if (s_log.isDebugEnabled()) {
                s_log.debug("Copying '" + from.toString() + "' to '" + to.toString() + "'");
            }
            File parent = to.getParentFile();
            if ( parent != null &&
                 !parent.canWrite() ) {
                throw new IOException("can not write to directory: " + parent);
            }
            if (!from.canRead()) {
                throw new IOException("can not read file: " + from);
            }
            InputStream is = new FileInputStream(from);
            OutputStream os = new FileOutputStream(to);
            byte[] buf = new byte[64*1024];
            int bytes;
            while ((bytes = is.read(buf)) >= 0) {
                os.write(buf, 0, bytes);
            }
            os.close();
            is.close();
        }
    }


    /**
     *  A convenience method that will create a FileOutputStream
     *  so that the zip file can be written to outputFileName
     */
    public static void writeZipFile(String outputFileName,
                                    String[] includedFiles,
                                    File baseFile) {
        try {
            writeZipFile(new FileOutputStream(outputFileName), includedFiles,
                          baseFile);
        } catch (FileNotFoundException e) {
            s_log.error("Error creating the file output stream for file " +
                        outputFileName, e);
        }
    }


    /**
     *  This writes a zip file to the given output stream and has
     *  several options for how the file should be written.
     *
     *  @param outputStream The output stream to write the file.
     *                      This is typically the response output stream
     *                      if the file is download or a FileOutputStream if
     *                      it is written to disk.
     *  @param fileList A string list of the files to be included in the
     *                  zip file.  The strings are relative to the
     *                  baseFile or are absolute if the baseFile is null.
     *  @param baseFile The file that is used as the base of the zip.
     *                  This basically allows us to "cd" to the directory
     *                  and then perform the "zip" from that directory
     *                  as opposed to doing everything from "/".
     *  @pre outputStream != null && fileList != null
     */
    public static void writeZipFile(OutputStream outputStream,
                                     String[] fileList,
                                     File baseFile) {
        // Create a buffer for reading the files
        byte[] buffer = new byte[1024];

        try {
            ZipOutputStream out = new ZipOutputStream(outputStream);

            for (int i=0; i<fileList.length; i++) {
                File inputFile = new File(baseFile, fileList[i]);
                if (inputFile.isDirectory()) {
                    // we don't need to put the empty directory in the zip
                    // the other option would be to create the ZipEntry
                    // for the directory but that seems useless since it
                    // could lead to empty directories when the file is
                    // unzipped.
                    continue;
                }

                FileInputStream in = new FileInputStream(inputFile);

                ZipEntry zipEntry = new ZipEntry(fileList[i]);
                out.putNextEntry(zipEntry);

                if (s_log.isDebugEnabled()) {
                    long compressed = zipEntry.getCompressedSize();
                    long original = zipEntry.getSize();
                    long ratio = ((original-compressed)*100) / original;
                    s_log.debug("Compressing file " + fileList[i] +
                                "; original size = " + original + "; compressed = " +
                                compressed + "; ratio = " + ratio);
                }

                // Transfer bytes from the file to the ZIP file
                int len = 0;
                while ((len=in.read(buffer)) > -1) {
                    out.write(buffer, 0, len);
                }

                // Close the streams
                out.closeEntry();
                in.close();
            }

            // Complete the ZIP file
            out.close();
        } catch (IOException e) {
            throw new UncheckedWrapperException("Error Creating the Zip File", e);
        }
    }

    /**
     *  This recursively builds a list of file paths, including all files
     *  that meet the requirements of the file filter, if specified.  This
     *  does not include the base directory but it does include all other
     *  directories
     *
     *  @param FilenameFilter This is applied to the list.  If it is null,
     *                        all files in the directory tree are returned.
     *  @pre baseDirectory != null && baseDirectory.isDirectory() == true
     */
    public static String[] listFilesInTree(File baseDirectory,
                                           FilenameFilter filter) {
        return listFilesInTree(baseDirectory, filter, new ArrayList(), null);
    }

    /**
     *  This provides the same funcationality as listFilesInTree but
     *  also takes in the collection that is used to build the array
     *  and can call itself recursively.
     *  @param prefix this is the string that will go at the beginning of
     *  the name in the returned array.  This is needed so tha the code
     *  can recursively call itself.  Pass in "null" if there is no
     *  prefix that is desired.
     */
    private static String[] listFilesInTree(File baseDirectory,
                                            FilenameFilter filter,
                                            Collection files,
                                            String prefix) {
        // TODO: there has to be a better way to do this...
        Assert.isTrue(baseDirectory.isDirectory(),
                     "Base Directory must be a directory but is actually a file.");
        if (prefix != null && prefix.trim().length() == 0) {
            prefix = null;
        }
        File tempFile = null;
        String[] list = baseDirectory.list(filter);
        ArrayList directories = new ArrayList();
        for (int i = 0; i < list.length; i++) {
            tempFile = new File(baseDirectory, list[i]);
            if (tempFile.isDirectory()) {
                directories.add(tempFile);
            } else {
                if (prefix == null) {
                    files.add(list[i]);
                } else {
                    files.add(prefix + File.separator + list[i]);
                }
            }
        }

        Iterator iter = directories.iterator();
        while (iter.hasNext()) {
            tempFile = (File)iter.next();
            if (prefix == null) {
                files.add(tempFile.getName());
                listFilesInTree(tempFile, filter, files, tempFile.getName());
            } else {
                String name = prefix + File.separator + tempFile.getName();
                files.add(name);
                listFilesInTree(tempFile, filter, files, name);
            }
        }

        return (String[]) files.toArray(new String[0]);
    }


    /**
     *  This will take in a file and return the pretty size in human
     *  readable terms.  Specifically, it will return things like
     *  100kb or 220M instead of just the number of bytes.
     *
     *  The formatting is retrieved from the Locale provided by the kernel
     */
    public static String getPrettySize(File file) {
        if (file == null || !file.exists()) {
            return "0";
        } else {
            return getPrettySize(file.length());
        }
    }

    /**
     *  This will take in a file and return the pretty size in human
     *  readable terms.  Specifically, it will return things like
     *  100kb or 220M instead of just the number of bytes.
     *
     *  The formatting is retrieved from the Locale provided by the kernel
     */
    public static String getPrettySize(long longSize) {
        Locale locale = Kernel.getContext().getLocale();

        NumberFormat nf = NumberFormat.getNumberInstance(locale);
        nf.setMaximumFractionDigits(0);

        // Try finding a good matching going from small to large
        // files, assuming that most files will be small.

        double size = (double) longSize;

        if (size < 1000) {
            return nf.format(size) + " b";
        } else if ((size = byteToKilo(longSize)) < 1000) {
            return nf.format(size) + " kb";
        }

        // Must be one MB or larger.  Change pricision and keep
        // checking.

        nf.setMaximumFractionDigits(2);

        if ((size = byteToMega(longSize)) < 1000) {
            return nf.format(size) + " MB";
        } else {
            return nf.format(size) + " GB";
        }
    }

    /*
     * Converstion methods
     */
    private static double byteToKilo(long size) {
        return ((double) size) / 1024.;
    }

    private static double byteToMega(long size) {
        return ((double) size) / 1048576.;
    }
}
