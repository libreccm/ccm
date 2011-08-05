package com.redhat.ccm.documentation;

import java.io.*;
import java.util.*;

/**
 * Splitter
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Id: Splitter.java 2122 2011-01-13 17:31:49Z pboy $
 **/

public class Splitter {

    private static final String BEGIN = "@rhdoc.begin";
    private static final String END = "@rhdoc.end";

    private File m_dir;
    private HashMap m_examples = new HashMap();

    public Splitter(File dir) {
        m_dir = dir;
    }

    public Splitter(String dir) {
        this(new File(dir));
    }

    private FileWriter getExample(String example) {
        return (FileWriter) m_examples.get(example);
    }

    private Collection getExamples() {
        return m_examples.values();
    }

    private void beginExample(String example) throws IOException {
        File file = new File(m_dir, example);
        File parent = file.getParentFile();
        if (parent != null) { parent.mkdirs(); }
        FileWriter fw = new FileWriter(file);
        fw.write("<![CDATA[");
        m_examples.put(example, fw);
    }

    private void endExample(String example) throws IOException {
        FileWriter fw = getExample(example);
        fw.write("]]>\n");
        fw.close();
        m_examples.remove(example);
    }

    private boolean hasExample(String example) {
        return m_examples.containsKey(example);
    }

    private void endExamples() throws IOException {
        for (Iterator it = m_examples.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry me = (Map.Entry) it.next();
            FileWriter fw = (FileWriter) me.getValue();
            fw.close();
            it.remove();
        }
    }

    private void writeln(String line) throws IOException {
        for (Iterator it = getExamples().iterator(); it.hasNext(); ) {
            FileWriter fw = (FileWriter) it.next();
            fw.write(line);
            fw.write("\n");
        }
    }

    public void split(String file) throws IOException {
        split(new File(file));
    }

    public void split(File file) throws IOException {
        FileReader fr = new FileReader(file);
        LineNumberReader lines = new LineNumberReader(fr);

        while (true) {
            String line = lines.readLine();
            if (line == null) { break; }

            int index = line.indexOf(END);
            if (index >= 0) {
                String example = line.substring(index + END.length()).trim();
                if (hasExample(example)) {
                    endExample(example);
                    continue;
                } else {
                    System.err.println
                        (file + ":" + lines.getLineNumber() +
                         ": no matching begin, ignoring directive");
                }
            }

            index = line.indexOf(BEGIN);
            if (index >= 0) {
                String example = line.substring(index + BEGIN.length()).trim();
                beginExample(example);
                continue;
            }

            writeln(line);
        }

        endExamples();

        fr.close();
        lines.close();
    }

    private static final String USAGE =
        "Usage: splitter <directory> <file_1> ... <file_n>";

    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println(USAGE);
            return;
        }

        try {
            Splitter splitter = new Splitter(args[0]);
            for (int i = 1; i < args.length; i++) {
                splitter.split(args[i]);
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

}
