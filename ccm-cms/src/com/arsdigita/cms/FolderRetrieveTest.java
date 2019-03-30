package com.arsdigita.cms;

import com.arsdigita.util.cmd.Program;
import java.io.PrintWriter;
import org.apache.commons.cli.CommandLine;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 */
public class FolderRetrieveTest extends Program {

    public FolderRetrieveTest() {
        super("FolderRetrieveTest", "1.0.0", "");
    }

    public static final void main(final String[] args) {
        new FolderRetrieveTest().run(args);
    }

    public void doRun(final CommandLine cmdLine) {

        final PrintWriter writer = new PrintWriter(System.out);

        final ContentSectionCollection sections = ContentSection.getAllSections();
        sections.addEqualsFilter("label", "content");
        sections.next();
        final ContentSection section = sections.getContentSection();

        final Folder folder = Folder.retrieveFolder(section, "/personen");

        if (folder == null) {
            writer.println("Failed to get folder.");
        } else {
            writer.printf("Got folder '%s'\n", folder.getPath());
        }

        writer.flush();

    }

}
