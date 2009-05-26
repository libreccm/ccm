package com.redhat.ccm.tools.ant.taskdefs;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.FileScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Execute;
import org.apache.tools.ant.taskdefs.ExecuteJava;
import org.apache.tools.ant.taskdefs.LogStreamHandler;
import org.apache.tools.ant.taskdefs.MatchingTask;
import org.apache.tools.ant.types.Commandline;
import org.apache.tools.ant.types.CommandlineJava;
import org.apache.tools.ant.types.DirSet;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Mapper;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Reference;
import org.apache.tools.ant.util.FileNameMapper;
import org.apache.tools.ant.util.GlobPatternMapper;
import org.apache.tools.ant.util.IdentityMapper;
import org.apache.tools.ant.util.SourceFileScanner;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Enumeration;
import java.util.Vector;

/**
 * JDO enhancement task.
 *
 * @author Dennis Gregorovic <a href="mailto:dgregor@redhat.com">dgregor@redhat.com</a>
 */
public class JDOEnhance extends MatchingTask {

    private CommandlineJava m_cmdline = new CommandlineJava();
    private File m_destination = null;
    private File[] m_enhanceList = new File[0];
    private Mapper m_mapperElement = null;
    private Path m_source = null;
    //private Path m_classes = null;
    private Path m_jdo = null;
    private Vector m_classes = new Vector();
    private final static String DEFAULT_CLASSNAME = "com.sun.jdori.enhancer.Main";

    public JDOEnhance() {
        m_cmdline.setClassname(DEFAULT_CLASSNAME);
    }

    public File getDestination() {
        return m_destination;
    }

    public Path getSource() {
        return m_source;
    }

    public void setClasspath(Path s) {
        createClasspath().append(s);
    }
    public void setClasspathRef(Reference r) {
        createClasspath().setRefid(r);
    }
    public Path createClasspath() {
        return m_cmdline.createClasspath(project).createPath();
    }

    /**
     * The name of the class that does the enhancement
     */
    public void setClassname(String classname) {
        m_cmdline.setClassname(classname);
    }

    /**
     * The directory that the enhanced class files will be written to.
     */
    public void setDestination(File destination) {
        m_destination = destination;
    }

    /**
     * The source path for jdo and class files
     */
    public void setSource(Path source) {
        //m_source = source;
    }

    public Path createSrcpath() {
        if (m_source == null) {
            m_source = new Path(project);
        }
        return m_source.createPath();
    }

    public void addDirset(DirSet set) {
        m_classes.addElement(set);
    }

    //public Path createClasses() {
    //    if (m_classes == null) {
    //        m_classes = new Path(project);
    //    }
    //    return m_classes.createPath();
    //}

    public Path createJdo() {
        if (m_jdo == null) {
            m_jdo = new Path(project);
        }
        return m_jdo.createPath();
    }

    /**
     * Defines the mapper to map source to destination files.
     */
    public Mapper createMapper() throws BuildException {
        if (m_mapperElement != null) {
            throw new BuildException("Cannot define more than one mapper",
                                     location);
        }
        m_mapperElement = new Mapper(project);
        return m_mapperElement;
    }

    public void execute() throws BuildException {

        log("running jdoenhance target", Project.MSG_VERBOSE);
        // compare source files with target directory to figure out which files
        // need to be enhanced

        for (int i = 0; i < m_classes.size(); i++) {
            DirSet dirset = (DirSet) m_classes.elementAt(i);
            File fromDir = dirset.getDir(project);
            DirectoryScanner ds = dirset.getDirectoryScanner(project);
            String[] srcDirs = ds.getIncludedDirectories();
            log(fromDir.toString(), Project.MSG_VERBOSE);
            for (int j = 0; j < srcDirs.length; j++) {
                log(srcDirs[j].toString(), Project.MSG_VERBOSE);
            }
            log(m_destination.toString(), Project.MSG_VERBOSE);
            scanDir(fromDir, m_destination, srcDirs);
        }

        if (m_enhanceList.length > 0) {
            log("Enhancing " + m_enhanceList.length +
                " class file"
                + (m_enhanceList.length == 1 ? "" : "s"));

            Path newPath = new Path(project);
            newPath.addExisting(m_source);
            m_cmdline.createArgument().setValue("-s");
            m_cmdline.createArgument().setValue(newPath.toString());

            m_cmdline.createArgument().setValue("-d");
            m_cmdline.createArgument().setValue(getDestination().getAbsolutePath());

            for (int i = 0; i < m_enhanceList.length; i++) {
                String arg = m_enhanceList[i].getAbsolutePath();
                m_cmdline.createArgument().setValue(arg);
            }

            String[] jdo_list = m_jdo.list();
            String[] includes = { "**/*.jdo" };
            for (int i = 0; i < jdo_list.length; i++) {
                File jdoDir = project.resolveFile(jdo_list[i]);
                DirectoryScanner ds = getDirectoryScanner(jdoDir);
                ds.setIncludes(includes);
                ds.scan();
                String[] jdo_files = ds.getIncludedFiles();
                for (int j = 0; j < jdo_files.length; j++) {
                    File arg = new File(ds.getBasedir(), jdo_files[j]);
                    m_cmdline.createArgument().setValue(arg.getAbsolutePath());
                }
            }
            log(m_cmdline.describeCommand(), Project.MSG_VERBOSE);
            run(m_cmdline.getCommandline());
        }
    }

    /**
     * Scans the directory looking for class files to be enhanced.
     */
    protected void scanDir(File srcDir, File destDir, String[] dirs) {
        log(srcDir.toString(), Project.MSG_VERBOSE);
        log(destDir.toString(), Project.MSG_VERBOSE);
        FileNameMapper mapper = null;
        if (m_mapperElement != null) {
            mapper = m_mapperElement.getImplementation();
        } else {
            mapper = new IdentityMapper();
        }
        FilenameFilter classFileFilter = new FilenameFilter() {
                public boolean accept( File dir, String name ) {
                    if ( name.endsWith(".class") &&
                         name.indexOf("$") == -1 ) {
                        return true;
                    }
                    return false;
                }
            };
        SourceFileScanner sfs = new SourceFileScanner(this);
        for (int i = 0; i < dirs.length; i++) {
            File dir = new File(srcDir, dirs[i]);
            log(dir.toString(), Project.MSG_VERBOSE);
            String[] files = dir.list(classFileFilter);
            for (int j = 0; j < files.length; j++) {
                files[j] = (new File(dirs[i], files[j])).toString();
            }
            File[] newFiles = sfs.restrictAsFiles(files, srcDir, destDir, mapper);
            if (newFiles.length > 0) {
                File[] newEnhanceList = new File[m_enhanceList.length + newFiles.length];
                System.arraycopy(m_enhanceList, 0,
                                 newEnhanceList, 0,
                                 m_enhanceList.length);
                System.arraycopy(newFiles, 0,
                                 newEnhanceList, m_enhanceList.length,
                                 newFiles.length);
                m_enhanceList = newEnhanceList;
            }
        }
    }

    private void run(CommandlineJava command) throws BuildException {
        Path newPath = new Path(project);
        newPath.addExisting(command.getClasspath());
        ExecuteJava exe = new ExecuteJava();
        exe.setJavaCommand(command.getJavaCommand());
        exe.setClasspath(newPath);
        exe.setSystemProperties(command.getSystemProperties());
        exe.execute(project);
    }

    /**
     * Executes the given classname with the given arguments in a separate VM.
     */
    private int run(String[] command) throws BuildException {
        Execute exe = null;
        exe = new Execute(new LogStreamHandler(this, Project.MSG_INFO,
                                               Project.MSG_WARN));
        exe.setAntRun(project);
        exe.setWorkingDirectory(project.getBaseDir());
        exe.setCommandline(command);
        try {
            int rc = exe.execute();
            if (exe.killedProcess()) {
                log("Timeout: killed the sub-process", Project.MSG_WARN); 
            }
            return rc;
        } catch (IOException e) {
            throw new BuildException(e, location);
        }
    }
}
