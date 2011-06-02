package com.arsdigita.london.util.cmd;

import com.arsdigita.cms.installer.xml.XMLContentItemHandler;
import com.arsdigita.cms.installer.xml.XMLContentTypeHandler;
import com.arsdigita.london.util.Transaction;
import com.arsdigita.packaging.Program;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.TransactionContext;
import com.arsdigita.xml.XML;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;

/**
 *
 * @author Jens Pelzetter 
 */
public class ReloadAuthoringSteps extends Program {

    public ReloadAuthoringSteps() {
        super("ReloadAuthoringSteps",
              "1.0.0",
              "");



    }

    @Override
    protected void doRun(CommandLine cmdLine) {

        String[] args = cmdLine.getArgs();

        if (args.length < 1) {
            System.out.println(
                    "Usage ReloadAuthoringSteps pathToAuthoringStepDefinition");
            System.out.println("");
            System.out.println(
                    "The path to the definition file has usally the form");
            System.out.println("");
            System.out.println(
                    "/WEB-INF/content-types/com/arsdigita/contenttypes/ContentType.xml");
            System.out.println("");
            System.out.println(
                    "Replace 'ContentType.xml' with the name of the ContentType which AuthoringSteps should be reloaded.");
            System.exit(-1);
        }

        final String defToReload = args[0];

        System.out.printf("Reloading AuthoringSteps from '%s'...", defToReload);
        new Transaction() {

            @Override
            protected void doRun() {
                XMLContentTypeHandler handler = new XMLContentTypeHandler();
                XML.parseResource(defToReload, handler);
            }
        }.run();
        System.out.printf("Reloaded AuthoringSteps from '%s'.", defToReload);

    }
}
