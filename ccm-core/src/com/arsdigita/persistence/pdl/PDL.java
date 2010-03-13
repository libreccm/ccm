/*
 * Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
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


import com.arsdigita.db.DbHelper;
import com.arsdigita.persistence.Utilities;
import com.arsdigita.persistence.metadata.MetadataRoot;
import com.arsdigita.util.Assert;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.util.cmd.BooleanSwitch;
import com.arsdigita.util.cmd.CommandLine;
import com.arsdigita.util.cmd.FileSwitch;
import com.arsdigita.util.cmd.PathSwitch;
import com.arsdigita.util.cmd.StringSwitch;
import com.redhat.persistence.metadata.Table;
import com.redhat.persistence.pdl.DDLWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.zip.ZipFile;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * The main class that is used to process PDL files.  It takes any number of
 * PDL files as arguments on the command line, then processes them all into
 * a single XML file (the first command line argument).
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #33 $ $Date: 2004/08/16 $
 */

public class PDL {

    public final static String versionId = "$Id: PDL.java 738 2005-09-01 12:36:52Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    private static final Logger s_log = Logger.getLogger(PDL.class);
    private static boolean s_quiet = false;

    private PDLCompiler m_pdlc = new PDLCompiler();

    /**
     * Generates the metadata that corresponds to the AST generated from the
     * various PDL files, all beneath the given metadata root node.
     *
     * @param root the metadata root node to build the metadata beneath
     */
    public void generateMetadata(MetadataRoot root) {
        m_pdlc.emit(root);
    }

    /**
     * Parses a PDL file into an AST.
     *
     * @param r a Reader open to the PDL file
     * @param filename the name of the PDL file read by "r"
     * @throws PDLException thrown on a parsing error.
     */
    public void load(final Reader r, final String filename)
        throws PDLException {
        m_pdlc.parse(r, filename);
    }

    /**
     * Parse a PDL file into an AST.
     *
     * @param f a File object that references a PDL file to parse
     * @throws PDLException thrown when the file is not found or on a parse
     *                      error
     */
    public void load(File f) throws PDLException {
        try {
            load(new FileReader(f), f.toString());
        } catch (FileNotFoundException e) {
            throw new PDLException(e.getMessage());
        }
    }

    /**
     * Parse a PDL file into an AST.
     *
     * @param filename the name of the PDL file to parse
     * @throws PDLException on file not found or a parse error.
     */
    public void load(String filename) throws PDLException {
        load(new File(filename));
    }

    /**
     *
     * @param s
     * @pre s != null
     * @throws PDLException
     */
    public void loadResource(String s) throws PDLException {
        InputStream is = getClass().getClassLoader().getResourceAsStream(s);
        if (is == null) {
            throw new PDLException("No such resource: " + s);
        }
        load(new InputStreamReader(is), s);
    }

    protected static final CommandLine CMD =
        new CommandLine(PDL.class.getName(), null);

    static {
        CMD.addSwitch(new PathSwitch(
            "-library-path",
            "PDL files appearing in this path will be searched " +
            "for unresolved dependencies found in the files to be processed",
            new File[0]
            ));
        CMD.addSwitch(new PathSwitch(
            "-path",
            "PDL files appearing in this path will be processed",
            new File[0]
            ));
        CMD.addSwitch(new BooleanSwitch("-validate", "validate PDL",
                                        Boolean.FALSE));
        CMD.addSwitch(new StringSwitch("-generate-ddl",
                                       "generate ddl and write " +
                                       "it to the specified directory", null));
        CMD.addSwitch(new FileSwitch(
            "-generate-events",
            "if present PDL will be written to the specified directory " +
            "containing the MDSQL generated events",
            null
            ));
        CMD.addSwitch(new StringSwitch("-database", "target database", null));
        CMD.addSwitch(new FileSwitch("-sqldir", "sql directory", null));
        CMD.addSwitch(new BooleanSwitch("-debug", "sets logging to DEBUG",
                                        Boolean.FALSE));
        CMD.addSwitch(new BooleanSwitch("-verbose", "sets logging to INFO",
                                        Boolean.FALSE));
        CMD.addSwitch(new BooleanSwitch("-quiet", "sets logging to ERROR and does not complain if no PDL files are found",
                                        Boolean.FALSE));
        CMD.addSwitch(new StringSwitch("-testddl", "no clue", null));
    }

    /**
     * Compiles pdl files into one xml file. The target xml file is
     * the first argument. All other arguments refer to pdl files that
     * need to be loaded.
     *
     * @throws PDLException if we have too few input files or if we
     * detect an error while parsing an input file. The reason we use
     * an exception is for the build process within ant to fail on
     * error.
     **/
    public static final void main(String[] args) throws PDLException {

        Map options = new HashMap();
        args = CMD.parse(options, args);

        BasicConfigurator.configure();
        if (Boolean.TRUE.equals(options.get("-debug"))) {
            Logger.getRootLogger().setLevel(Level.DEBUG);
        } else if (Boolean.TRUE.equals(options.get("-verbose"))) {
            Logger.getRootLogger().setLevel(Level.INFO);
        } else if (Boolean.TRUE.equals(options.get("-quiet"))) {
            Logger.getRootLogger().setLevel(Level.ERROR);
            s_quiet = true;
        } else {
            Logger.getRootLogger().setLevel(Level.FATAL);
        }

        String database = (String) options.get("-database");
        if ("postgres".equalsIgnoreCase(database)) {
            DbHelper.setDatabase(DbHelper.DB_POSTGRES);
        } else {
            DbHelper.setDatabase(DbHelper.DB_ORACLE);
        }

        final PDLFilter filter =
            new NameFilter(DbHelper.getDatabaseSuffix(), "pdl");
        // accumulate the values returned by the filter
        final Collection[] output = new Collection[] { new HashSet() };
        PDLFilter tracker = new PDLFilter() {
            public Collection accept(Collection names) {
                Collection result = filter.accept(names);
                output[0].addAll(result);
                return result;
            }
        };

        PDLCompiler compiler = new PDLCompiler();
        parse(compiler, (File[]) options.get("-library-path"), filter);
        parse(compiler, (File[]) options.get("-path"), tracker);
        parse(compiler, args, tracker);
        if (output[0].size() < 1) {
            if (s_quiet) {
                return;
            }
            usage();
        }

        HashSet pdlFiles = new HashSet();
        for (Iterator it = output[0].iterator(); it.hasNext(); ) {
            File pdlFile = new File((String) it.next());
            pdlFiles.add(pdlFile);
        }

        File debugDir = (File) options.get("-generate-events");
        if (debugDir != null) {
            if (!debugDir.exists() || !debugDir.isDirectory()) {
                throw new PDLException("No such directory: " + debugDir);
            }
            setDebugDirectory(debugDir);
        }

        MetadataRoot root = MetadataRoot.getMetadataRoot();
        compiler.emit(root);

        String ddlDir = (String) options.get("-generate-ddl");
        if (ddlDir != null) {
            Set sqlFiles = new HashSet();
            File sqldir = (File) options.get("-sqldir");
            if (sqldir != null) {
                findSQLFiles(sqldir, sqlFiles);
            }

            DDLWriter writer = new DDLWriter(ddlDir, sqlFiles);

            if (Boolean.TRUE.equals(Boolean.valueOf((String) options.get("-testddl")))) {
                writer.setTestPDL(true);
            }

            List tables = new ArrayList(root.getRoot().getTables());

            for (Iterator it = tables.iterator(); it.hasNext(); ) {
                Table table = (Table) it.next();
                File tableFile = new File(root.getRoot().getFilename(table));
                if (!pdlFiles.contains(tableFile)) {
                    it.remove();
                }
            }

            try {
                writer.write(tables);
            } catch (IOException ioe) {
                throw new PDLException(ioe.getMessage());
            }
        }
    }

    private static void parse(PDLCompiler compiler, String[] path,
                              PDLFilter filter) {
        File[] fpath =new File[path.length];
        for (int i = 0; i < path.length; i++) {
            fpath[i] = new File(path[i]);
        }
        parse(compiler, fpath, filter);
    }

    private static void parse(PDLCompiler compiler, File[] path,
                              PDLFilter filter) {
        ArrayList filenames = new ArrayList();
        for (int i = 0; i < path.length; i++) {
            File f = path[i];
            if (!f.exists()) { continue; }
            if (f.isDirectory()) {
                new DirSource(f, filter).parse(compiler);
            } else if (f.isFile()
                       && (f.getName().endsWith(".jar")
                           || f.getName().endsWith(".zip"))) {
                try {
                    new ZipSource(new ZipFile(f), filter).parse(compiler);
                } catch (IOException e) {
                    throw new UncheckedWrapperException(e);
                }
            } else {
                filenames.add(f.getPath());
            }
        }

        Collection accepted = filter.accept(filenames);
        for (Iterator it = accepted.iterator(); it.hasNext(); ) {
            new FileSource((String) it.next()).parse(compiler);
        }
    }

    private static final void usage() throws PDLException {
        throw new PDLException(CMD.usage());
    }

    private static String s_debugDirectory = null;

    public static void setDebugDirectory(File directory) {
        s_debugDirectory = directory.getPath();
    }

    public static void setDebugDirectory(String directory) {
        s_debugDirectory = directory;
    }

    public static String getDebugDirectory() {
        return s_debugDirectory;
    }

    /**
     * Loads all the PDL files in a given directory
     */
    public static MetadataRoot loadDirectory(File dir) {
        List files = findPDLFiles(dir);
        s_log.warn("Found " + files.size() + " files in the " +
                   dir.toString() + " directory.");

        try {
            return compilePDLFiles(files);
        } catch (PDLException ex) {
            throw new UncheckedWrapperException
                ("error while trying to compile PDL files", ex);
        }
    }

    /**
     * Finds all the PDL files in a given path.
     **/

    public static List findPDLFiles(File[] path) {
        List result = new ArrayList();

        for (int i = 0; i < path.length; i++) {
            s_log.debug("Loading default PDL files from " + path[i]);
            findPDLFiles(path[i], result);
        }

        return result;
    }

    /**
     * Finds all PDL files in a given directory
     */
    public static List findPDLFiles(File dir) {
        List files = new ArrayList();
        findFiles(dir, files, "pdl", false);
        return files;
    }

    /**
     * Searches a directory for all PDL files
     */
    public static void findPDLFiles(File base, Collection files) {
        findFiles(base, files, "pdl", false);
    }

    public static void findSQLFiles(File base, Collection files) {
        findFiles(base, files, "sql", true);
    }

    private static final Set SUFFIXES = new HashSet();

    static {
        String[] sfxs = DbHelper.getDatabaseSuffixes();
        for (int i = 0; i < sfxs.length; i++) {
            SUFFIXES.add(sfxs[i]);
        }
    }

    private static void findFiles(File base, Collection files,
                                  final String extension,
                                  boolean trimPath) {
        if (!base.exists()) {
            s_log.warn("Skipping directory " + base +
                       " since it doesn't exist");
            return;
        }

        Assert.isTrue(base.isDirectory(), "directory " + base +
                          " is directory");

        final String suffix = DbHelper.getDatabaseSuffix();
        Stack dirs = new Stack();
        dirs.push(base);
        Set toAdd = new HashSet();

        while (dirs.size() > 0) {
            File dir = (File) dirs.pop();
            if ("upgrade".equalsIgnoreCase(dir.getName())) {
                s_log.debug("Skipping upgrade directory");
                continue;
            }

            File[] listing = dir.listFiles(new FileFilter() {
                    public boolean accept(File file) {
                        if (file.isDirectory()) {
                            return true;
                        }

                        String name = file.getName();
                        String base = base(name);
                        String sfx = suffix(name);
                        String ext = extension(name);

                        if (ext != null && ext.equals(extension)) {
                            if (sfx != null) {
                                return sfx.equals(suffix);
                            } else {
                                return true;
                            }
                        }

                        return false;
                    }
                });

            // sort the listing to combat the non-deterministic nature of the
            // listFiles method. Helps reproduce bugs that depend on the order
            // in which the files are loaded.
            Arrays.sort(listing, new Comparator() {
                    public int compare(Object o1, Object o2) {
                        File f1 = (File) o1;
                        File f2 = (File) o2;
                        return f1.getAbsolutePath().compareTo(f2.getAbsolutePath());
                    }
                });

            toAdd.clear();

            for (int i = 0; i < listing.length; i++) {
                if (listing[i].isDirectory()) {
                    if (s_log.isDebugEnabled()) {
                        s_log.debug("Found subdir " + listing[i]);
                    }
                    dirs.push(listing[i]);
                } else {
                    try {
                        String path = listing[i].getCanonicalPath();
                        if (trimPath) {
                            int index = path.lastIndexOf(File.separator);
                            if (index != -1) {
                                path = path.substring(index + 1);
                            }
                            path = base(path) + "." + extension;
                        }

                        toAdd.add(path);
                    } catch (IOException e) {
                        throw new UncheckedWrapperException
                            ("cannot get file path", e);
                    }
                }
            }

            if (suffix != null) {
                for (Iterator it = toAdd.iterator(); it.hasNext(); ) {
                    String path = (String) it.next();
                    String shadow = base(path) + "." + suffix + "." +
                        extension;
                    if (!path.equals(shadow) && toAdd.contains(shadow)) {
                        if (s_log.isDebugEnabled()) {
                            s_log.debug(
                                "Ignoring " + path +
                                " because it is shadowed by  " + shadow
                                );
                        }
                        it.remove();
                    } else if (s_log.isDebugEnabled()) {
                        s_log.debug("Found file " + path);
                    }
                }
            }

            files.addAll(toAdd);
        }
    }

    private static final String base(String path) {
        String suffix = suffix(path);
        if (suffix == null) {
            return basename(path);
        } else {
            return basename(basename(path));
        }
    }

    private static final String suffix(String path) {
        String result = extension(basename(path));
        if (SUFFIXES.contains(result)) {
            return result;
        } else {
            return null;
        }
    }

    private static final String extension(String path) {
        if (path == null) { return null; }

        int idx = path.lastIndexOf('.');
        if (idx > -1) {
            return path.substring(idx + 1);
        } else {
            return null;
        }
    }

    private static final String basename(String path) {
        int idx = path.lastIndexOf('.');
        if (idx > -1) {
            return path.substring(0, idx);
        } else {
            return path;
        }
    }


    /**
     * Compiles PDL to Persistence Metadata
     *
     * @param files array of PDL files to process
     */
    public static MetadataRoot compilePDLFiles(Collection files)
        throws PDLException {
        StringBuffer sb = new StringBuffer();
        PDL pdl = new PDL();

        for (Iterator it = files.iterator(); it.hasNext(); ) {
            String file = (String) it.next();
            try {
                pdl.load(file);
            } catch (PDLException e) {
                sb.append(file).append(": ");
                sb.append(e.getMessage()).append(Utilities.LINE_BREAK);
            }
        }

        if (sb.length() == 0) {
            // No  errors so far. Try generating the xml file.
            pdl.generateMetadata(MetadataRoot.getMetadataRoot());
            if (s_debugDirectory != null) {
                try {
                    PDLOutputter.writePDL(MetadataRoot.getMetadataRoot(),
                                          new java.io.File(s_debugDirectory));
                } catch (java.io.IOException ex) {
                    s_log.error
                        ("There was a problem generating debugging output",
                         ex);
                }
            }
        } else {
            throw new PDLException(sb.toString());
        }

        return MetadataRoot.getMetadataRoot();
    }
}
