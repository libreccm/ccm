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
package com.arsdigita.portation.cmd;

import com.arsdigita.util.cmd.Program;
import org.apache.commons.cli.CommandLine;
import org.apache.log4j.Logger;

/**
 * Commandline tool to export all the objects of a specified class to a xml-file.
 *
 * @author <a href="mailto:tosmers@uni-bremen.de>Tobias Osmers<\a>
 * @version created the 25.05.16
 */
public class ExportCliTool extends Program {

    private final static Logger logger = Logger.getLogger(ExportCliTool.class);

    private ExportCliTool() {
        super("Export Commandline Tool",
              "1.0.0",
              "Exportation of POJOs...");
    }

    public static void main(String[] args) {
        new ExportCliTool().run(args);
    }

    @Override
    protected void doRun(CommandLine cmdLine) {
        final String[] args = cmdLine.getArgs();

        if (args.length < 1) {
            printUsage();
            System.exit(-1);
        }

        final String command = args[0];
        System.out.printf("Command ist %s\n", command);

        switch (command) {
            case "help":
                printUsage();
                break;
            case "export":
                createTestFolder();
                export(args);
                break;
            default:
                printUsage();
                break;
        }
    }

    private void printUsage() {
        System.err.printf(
                "\t\t\t--- ExportCliTool ---\n" +
                "usage:\t<command> [<category>]\n" +
                "\n" +
                "Available commands:\n" +
                "\tlist               \t\t Shows information on how to use this tool.\n" +
                "\texport <category>  \t\t Exports the chosen category to xml file.\n" +
                "\n" +
                "Available categories for export:\n" +
                "   \t\t users          \t all users of the system\n" +
                "   \t\t groups         \t all groups of the system\n" +
                "Use for exporting java objects of a specified class to a xml-file.\n"
        );
    }

    private void export(String[] args) {
        if (args.length < 2) {
            printUsage();
            System.exit(-1);
        }

        final String category = args[1];

        switch (category) {
            case "users":
                try {
                    System.out.printf("\nStarting export of users...\n\n");
                    UserExport userExport = new UserExport();
                    userExport.export();
                    System.out.printf("\n...done!\n\n");
                } catch (Exception ex) {
                    logger.error("ERROR", ex);
                }
                break;
            case "groups":
                break;
            default:
                printUsage();
                break;
        }
    }

    private void createTestFolder() {

    }
}
