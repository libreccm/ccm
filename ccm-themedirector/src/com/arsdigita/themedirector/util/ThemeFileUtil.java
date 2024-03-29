/*
* Copyright (C) 2001, 2003 ArsDigita Corporation. All Rights Reserved.
*
* The contents of this file are subject to the ArsDigita Public
* License (the "License"); you may not use this file except in
* compliance with the License. You may obtain a copy of
* the License at http://www.arsdigita.com/ADPL.txt
*
* Software distributed under the License is distributed on an "AS
* IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
* implied. See the License for the specific language governing
* rights and limitations under the License.
*
 */
package com.arsdigita.themedirector.util;

import com.arsdigita.themedirector.Theme;
import com.arsdigita.themedirector.ThemeDirectorConfig;
import com.arsdigita.themedirector.ThemeFile;
import com.arsdigita.themedirector.ThemeFileCollection;
import com.arsdigita.util.Assert;
import com.arsdigita.util.UncheckedWrapperException;

import com.inet.lib.less.Less;
import com.inet.lib.less.LessException;
import org.apache.log4j.Logger;

import java.util.Date;
import java.util.Map;
import java.util.HashMap;
import java.io.File;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.ArrayList;
import java.util.List;

/**
 * This is a utility class that is able to take a theme and, when necessary,
 * insert the files for the theme in to the database.
 *
 * @author <a href="mailto:randyg@redhat.com">Randy Graebner</a>
 *
 * @version $Revision: #1 $ $DateTime: 2004/01/29 09:49:40 $
 */
public class ThemeFileUtil {

    private static final Logger s_log = Logger.getLogger(ThemeFileUtil.class);

    /**
     * this copies the files from the file system to the database.
     *
     * @param currentFile         The directory to search recursively for files
     *                            to put in the database or the single file to
     *                            add to the db.
     * @param currentTheme        The current theme that is being operated on
     * @param serverSpecificPath  The absolute path of the root file. This
     *                            string is removed from the absolute path of
     *                            the current file to get the filePath property
     *                            of the ThemeFile
     * @param themeFiles          A Map of ThemeFiles with the key being the
     *                            filePath. This is used to look up files that
     *                            have already been pulled from the database so
     *                            that the code does not have to check the db
     *                            once for every file.
     * @param overwriteNewerFiles If this is true then it insert everything in
     *                            to the database. If this is false, it only
     *                            writes the file to the database if the file on
     *                            the file system is newer than the one in the
     *                            database.
     * @param fileType            The type of file this is. ThemeFile.LIVE and
     *                            ThemeFile.DRAFT are the two allowed values.
     *
     */
    public static void updateDatabaseFiles(File currentFile, 
                                           Theme currentTheme,
                                           String serverSpecificPath,
                                           boolean overwriteNewerFiles,
                                           String fileType) {
        
        Assert.isTrue(ThemeFile.LIVE.equals(fileType) || ThemeFile.DRAFT.equals(
            fileType));

        prepareThemeDirectory(serverSpecificPath);

        Map<String, ThemeFile> themeFiles = new HashMap<>();
        ThemeFileCollection collection;
        if (ThemeFile.LIVE.equals(fileType)) {
            collection = currentTheme.getPublishedThemeFiles();
        } else {
            collection = currentTheme.getDraftThemeFiles();
        }

        if (collection != null) {
            while (collection.next()) {
                ThemeFile file = collection.getThemeFile();
                themeFiles.put(file.getFilePath(), file);
            }
        }

        updateDatabaseFiles(currentFile, 
                            currentTheme, 
                            serverSpecificPath,
                            themeFiles, 
                            overwriteNewerFiles, 
                            fileType);
    }

    /**
     * Prepares the theme directory. At the moment only compiles LESS CSS files.
     * All files which do end with {@code *.less}. Also files ending with
     * {@code *.inc.less}. are ignored. These files are interpreted as include
     * files that are included into another LESS file.
     *
     * @param themePath
     */
    public static void prepareThemeDirectory(final String themePath) {

        final Path root = Paths.get(themePath);

        if (!Files.isDirectory(root)) {
            return;
        }

        final List<Path> eligiblePaths = new ArrayList<>();
        for (final String lessFileName : ThemeDirectorConfig.getInstance()
            .getLessFiles()) {
            if (lessFileName.startsWith("/")) {
                eligiblePaths.add(root.resolve(lessFileName.substring(1)));
            } else {
                eligiblePaths.add(root.resolve(lessFileName));
            }
        }

        try (final DirectoryStream<Path> directoryStream
                                             = Files.newDirectoryStream(root)) {

            directoryStream.forEach(path -> prepareThemeFile(path,
                                                             eligiblePaths));
        } catch (IOException ex) {
            throw new UncheckedWrapperException(ex);
        }
    }

    private static void prepareThemeFile(final Path path,
                                         final List<Path> eligiablePaths) {

        if (Files.isDirectory(path)) {
            try (DirectoryStream<Path> directoryStream = Files
                .newDirectoryStream(path)) {

                directoryStream
                    .forEach(current -> prepareThemeFile(current,
                                                         eligiablePaths));
                
                directoryStream.close();
            } catch (IOException ex) {
                throw new UncheckedWrapperException(ex);
            }
        } else {
            final String fileName = path.toString();
            if (fileName.toLowerCase().endsWith(".less")
                    && !fileName.toLowerCase().endsWith(".inc.less")
                    && !path.getFileName().toString().startsWith(".")
                    && eligiablePaths.contains(path)) {

                try {
                    final String result = Less.compile(path.toFile(), false);
                    final String outputPath = String.format(
                        "%s.css",
                        fileName.substring(0,
                                           fileName.length() - ".less".length()));
                    final Path output = Paths.get(outputPath);
                    Files.write(output, result.getBytes());
                } catch (IOException | LessException ex) {
                    throw new UncheckedWrapperException(
                        String.format("Error while compiling file \"%s\".",
                                      fileName),
                        ex);
                }
            }
        }
    }

    /**
     * this copies the files to the database
     *
     * @param currentFile         The directory to search recursively for files
     *                            to put in the database or the single file to
     *                            add to the db.
     * @param currentTheme        The current theme that is being operated on
     * @param serverSpecificPath  The absolute path of the root file. This
     *                            string is removed from the absolute path of
     *                            the current file to get the filePath property
     *                            of the ThemeFile
     * @param themeFiles          A Map of ThemeFiles with the key being the
     *                            filePath. This is used to look up files that
     *                            have already been pulled from the database so
     *                            that the code does not have to check the db
     *                            once for every file.
     * @param overwriteNewerFiles If this is true then it insert everything in
     *                            to the database. If this is false, it only
     *                            writes the file to the database if the file on
     *                            the file system is newer than the one in the
     *                            database.
     * @param fileType            The type of file this is. ThemeFile.LIVE and
     *                            ThemeFile.DRAFT are the two allowed values.
     *
     */
    private static void updateDatabaseFiles(File currentFile,
                                            Theme currentTheme,
                                            String serverSpecificPath,
                                            Map themeFiles,
                                            boolean overwriteNewerFiles,
                                            String fileType) {
        if (currentFile.isDirectory()) {

            File[] files = currentFile.listFiles(new WhiteListFilenameFilter());
            if (files != null) {
                for (int i = 0; i < files.length; i++) {

                    updateDatabaseFiles(files[i],
                                        currentTheme,
                                        serverSpecificPath,
                                        themeFiles,
                                        overwriteNewerFiles,
                                        fileType);
                }
            }
        } else {
            if (currentFile.exists()) {

                String fullFilePath = currentFile.getAbsolutePath();
                String filePath = null;
                int beginIndex = fullFilePath.indexOf(serverSpecificPath);
                if (beginIndex > -1
                        && fullFilePath.length() > serverSpecificPath.length()) {
                    filePath = fullFilePath
                        .substring(beginIndex + serverSpecificPath.length() + 1);
                } else {
                    filePath = fullFilePath;
                }

                ThemeFile themeFile = (ThemeFile) themeFiles.get(filePath);
                if (!overwriteNewerFiles && themeFile != null) {
                    // make sure the currentFile is newer than the
                    // file in the db.
                    if (!(new Date(currentFile.lastModified()))
                        .after(themeFile.getLastModifiedDate())) {
                        // the file on the file system is older than
                        // the one in the db so we just return
                        return;
                    }
                }
                // Undelete the file if it reappeared
                if (themeFile != null
                        && themeFile.isDeleted()
                        && themeFile
                        .getLastModifiedDate()
                        .before(new Date(currentFile.lastModified()))) {
                    themeFile.setDeleted(false);
                    s_log.info("Undeleting the file: " + currentFile);
                }
                try (FileInputStream in = new FileInputStream(currentFile);
                     ByteArrayOutputStream os = new ByteArrayOutputStream()) {

                    byte[] buffer = new byte[8];
                    int length = -1;
                    while ((length = in.read(buffer)) != -1) {
                        os.write(buffer, 0, length);
                    }

                    byte[] content = os.toByteArray();

                    if (themeFile == null) {
                        themeFile = new ThemeFile(currentTheme, filePath);
                        themeFiles.put(filePath, themeFile);
                    }
                    themeFile.setVersion(fileType);
                    themeFile.setContent(content);
                    themeFile.setLastModifiedDate(new Date(currentFile
                        .lastModified()));

                    // we save to help with stack trace issues and so that
                    // we don't have too many build up.
                    themeFile.save();
                } catch (FileNotFoundException fnfe) {
                    s_log.error("Error opening file reader for " + currentFile
                        .getAbsolutePath(), fnfe);
                } catch (IOException ex) {
                    s_log.error("Error working with either the input or "
                                    + " output stream for " + currentFile
                            .getAbsolutePath(), ex);
                }
            }
        }
    }

}
