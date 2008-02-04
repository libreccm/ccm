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
 * HostInit
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #16 $ $Date: 2004/08/16 $
 **/

public class HostInit {

    public final static String versionId = "$Id: HostInit.java 736 2005-09-01 10:46:05Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    private static final Logger s_log =
        Logger.getLogger(HostInit.class);

    private static final Options OPTIONS = new Options();

    static {
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

        com.arsdigita.runtime.Startup.startup();

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

        File dest = new File(destination);
        File inf = new File(dest, "WEB-INF");
        File lib = new File(inf, "lib");
        File system = new File(inf, "system");

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

        system.mkdirs();
        if (!(system.exists() && system.isDirectory())) {
            err("unable to create system: " + system);
        }

        ConfigRegistry reg = new ConfigRegistry();
        List packages = reg.getPackages();
        try {
            copy(classpath, packages, lib);
            copySystem(classpath, packages, system);
            copy(webapps, packages, dest);
        } catch (IOException e) {
            err(e.getMessage());
        }
    }

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
                    s_log.info("Entry found in file that does not correspond to an installed package: " + line);
                }
            }
        }
        reader.close();
    }

    private static void copySystem(String pathfile, List packages, File dest)
        throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(pathfile));
        String line;
        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (contains(line, packages) && line.endsWith(".jar")) {
                String newline = line.substring(0, line.lastIndexOf(".jar")) + "-system.jar";
                File file = new File(newline);
                if (file.isFile()) {
                    if (s_log.isInfoEnabled()) {
                        s_log.info("Copying System JAR " + file.toString());
                    }
                    Files.copy(file, dest, Files.IGNORE_EXISTING);
                }
            }
        }
        reader.close();
    }

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

    private static boolean contains(String line, List packages) {
        for (Iterator it = packages.iterator(); it.hasNext(); ) {
            String key = (String) it.next();
            if (line.indexOf(key) >= 0) {
                return true;
            }
        }
        return false;
    }

}
