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
package com.arsdigita.packaging;

import com.arsdigita.runtime.ConfigRegistry;
import com.arsdigita.util.Files;
import com.arsdigita.util.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

import org.apache.log4j.Logger;

/**
 * Populates the CCM application directory in webapps.
 *
 * Implements the "hostinit" command and is called by the 
 * ccm hostinit / ccm hostinit-bundle command of the ccm PERL based
 * script system (up to APLAWS 1.0.5 / CCM 6.5).
 * 
 * In addition to populating the webapp directory the ccm command
 * used to configure the servlet container (hence to information
 * about servlet container and http port below).
 * Does not create the database nor the config registry.
 *
 * Options:
 *  --help           Display help
 *  --usage          Print this message
 *  --container      Specify the servlet container to initialize
 *  --clean          REQUIRED: Delete the existing webapp directories
 *                   before performing file copy.
 *
 * Command line call of hostinit via ccm scripts (shell, perl) 
 * root@localhost# ccm hostinit­bundle   \
 *                     ­­clean \
 *                     ­­name aplaws­plus­standard ­­container=tomcat \
 *                     http­port=8080 shutdown­port=8081 ajp­port=8009
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #16 $ $Date: 2004/08/16 $
 **/

public class HostInit {

    public final static String versionId = 
            "$Id: HostInit.java 736 2005-09-01 10:46:05Z sskracic $" +
            "by $Author: sskracic $, " + 
            "$DateTime: 2004/08/16 18:10:38 $";

    private static final Logger s_log =
        Logger.getLogger(HostInit.class);

    private static final Options OPTIONS = new Options();

    static {
        s_log.debug("Static initalizer starting...");
        OPTIONS.addOption
            (OptionBuilder
             .hasArg()
             .isRequired()
             .withLongOpt("classpath")
             .withArgName("FILE")
             .create());
        OPTIONS.addOption
            (OptionBuilder
             .hasArg()
             .isRequired()
             .withLongOpt("webapps")
             .withArgName("FILE")
             .create());
        OPTIONS.addOption
            (OptionBuilder
             .hasArg()
             .isRequired()
             .withLongOpt("destination")
             .withArgName("DIRECTORY")
             .create());
        OPTIONS.addOption
            (OptionBuilder
             .hasArg(false)
             .withLongOpt("clean")
             .withDescription("Remove the destination directory before copying files")
             .create());
        s_log.debug("Static initalizer finsished.");
    }

    private static final void err(String msg) {
        System.err.println(msg);
        System.exit(1);
    }

    public static final void main(String[] args) {

        if (args.length == 0) {
            Command.usage(OPTIONS, System.err,
                          "com.arsdigita.packaging.HostInit", null);
            System.exit(1);
        }

        // Register custom protocol handlers
        // No longer needed as of CCM 6.6
        // com.arsdigita.runtime.Startup.startup();

        CommandLine line;
        try {
            line = new PosixParser().parse(OPTIONS, args);
        } catch (ParseException e) {
            err(e.getMessage());
            return;
        }

        String classpath = line.getOptionValue("classpath");
        String webapps = line.getOptionValue("webapps");
        String destination = line.getOptionValue("destination");
        boolean clean = line.hasOption("clean");

        // base dir for all webapps, same as CATALINA_HOME
        // same as CCM_HOME
        File dest = new File(destination);

        // Currently: non-standard location of lib and classes
        File inf = new File(dest, "WEB-INF");

        // Currentliy: non-standard location of lib
        File lib = new File(inf, "lib");

        // currently (<=6.5): non-standard, special URL:resource handler, to be discarded
        // Removed on >= 6.6
        // File system = new File(inf, "system");

        if (!dest.exists()) {
            dest.mkdir();
            if (!dest.exists()) {
                err("unable to create destination: " + dest);
            }
        }

        if (clean) {
            File[] contents = dest.listFiles();
            if (contents == null) {
                err("unable to get directory listing: " + dest);
            }
            for (int i = 0; i < contents.length; i++) {
                Files.delete(contents[i]);
            }
        }

        lib.mkdirs();
        if (!(lib.exists() && lib.isDirectory())) {
            err("unable to create lib: " + lib);
        }

        // >= 6.6 system jar removed
        // system.mkdirs();
        // if (!(system.exists() && system.isDirectory())) {
        //     err("unable to create system: " + system);
        // }

        // check the configuration database (registry) for packages (modules)
        // retrieve a list of packages to deal with 
        ConfigRegistry reg = new ConfigRegistry();
        List packages = reg.getPackages();
        // Do the real work now.
        try {
            copy(classpath, packages, lib);
            // no longer used >= 6.6
            // copySystem(classpath, packages, system);
            copy(webapps, packages, dest);
        } catch (IOException e) {
            err(e.getMessage());
        }
    }

    /**
     * Internal general helper method to copy files from one location to a
     * destination dir using a list of files (packages) to copy.
     *
     * @param pathfile Path where to look for files specified in packages
     * @param packages List of files to copy
     * @param dest     Destination directory
     * @throws java.io.IOException
     */
    private static void copy(String pathfile, List packages, File dest)
        throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(pathfile));
        String line;
        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (line.startsWith("#")) {
                continue;
            }
            if (line.equals("")) {
                continue;
            }
            if (contains(line, packages)) {
                File file = new File(line);
                if (line.endsWith(File.separator)) {
                    copyDirectory(file, dest);
                } else {
                    if (file.isFile() && line.endsWith(".jar")) {
                        copyJar(file, dest);
                    } else {
                    if (s_log.isInfoEnabled()) {
                        s_log.info("Copying " + file.toString());
                    }
                    Files.copy(file, dest, Files.IGNORE_EXISTING);
                    }
                }
            } else {
                if (s_log.isInfoEnabled()) {
                    s_log.info("Entry found in file that does not correspond " +
                               "to an installed package: " + line);
                }
            }
        }
        reader.close();
    }

    /**
     * Internal helper method to copy all files in a given directory (dir) 
     * to a destination directory (dest).
     * 
     * @param dir  source directory path
     * @param dest destination direcotry path
     * @throws java.io.IOException
     */
    private static void copyDirectory(File dir, File dest) throws IOException {
        if (s_log.isInfoEnabled()) {
            s_log.info("Copying directory " + dir.toString());
        }
        if (!dir.isDirectory()) {
            err("directory does not exist: " + dir);
        }
        File[] files = dir.listFiles();
        if (files == null) {
            err("unable to get directory listing: " + dir);
        }
        for (int i = 0; i < files.length; i++) {
            Files.copy(files[i], dest, Files.IGNORE_EXISTING);
        }
    }

    /**
     * Copies .....   (currently not used.)
     * 
     * @param file
     * @param dest
     * @throws java.io.IOException
     */
    private static void copyJar(File file, File dest) throws IOException {
        if (s_log.isInfoEnabled()) {
            s_log.info("Copying JAR " + file.toString());
        }
        Files.copy(file, dest, Files.IGNORE_EXISTING);
        File dir = file.getParentFile();
        JarFile jar = new JarFile(file);

        Manifest manifest = jar.getManifest();
        if (manifest == null) { return; }

        Attributes attrs = manifest.getMainAttributes();
        if (attrs == null) { return; }

        String path = attrs.getValue("Class-Path");
        if (path == null) { return; }

        String[] jars = StringUtils.split(path, ' ');
        for (int i = 0; i < jars.length; i++) {
            String rel = jars[i].trim();
            if (rel.length() == 0) { continue; }
            File sub = new File(dir, rel);
            if (sub.exists()) {
                if (s_log.isInfoEnabled()) {
                    s_log.info("Copying referenced JAR " + sub.toString());
                }
                Files.copy(sub, dest, Files.IGNORE_EXISTING);
            }
        }
    }

    /**
     *
     */
    private static boolean contains(String line, List packages) {
        for (Iterator it = packages.iterator(); it.hasNext(); ) {
            String key = (String) it.next();
            if (line.indexOf(key) >= 0) {
                return true;
            }
        }
        return false;
    }

//  As of version 6.6 system.jar is no longer used. Code kept here as
//  an example just in case the mechanism is required for another purpose.
//  /**
//   * Copies the ccm-core-6.y.z-system.jar (java extension for URL:resource
//   * protocol handler) to its special location. 
//   * 
//   * Will not be used as soon as the resource protocol is replaced by a 
//   * standard compliant mechanism.
//   * 
//   * @param pathfile
//   * @param packages
//   * @param dest
//   * @throws java.io.IOException
//   * @deprecated 
//   */
//  private static void copySystem(String pathfile, List packages, File dest)
//      throws IOException {
//      BufferedReader reader = new BufferedReader(new FileReader(pathfile));
//      String line;
//      while ((line = reader.readLine()) != null) {
//          line = line.trim();
//          if (contains(line, packages) && line.endsWith(".jar")) {
//              String newline = line.substring(0, line.lastIndexOf(".jar")) +
//                                                                  "-system.jar";
//              File file = new File(newline);
//              if (file.isFile()) {
//                  if (s_log.isInfoEnabled()) {
//                      s_log.info("Copying System JAR " + file.toString());
//                  }
//                  Files.copy(file, dest, Files.IGNORE_EXISTING);
//              }
//          }
//      }
//      reader.close();
//  }

}
