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

import com.arsdigita.portation.conversion.MainConverter;
import com.arsdigita.util.cmd.Program;
import org.apache.commons.cli.CommandLine;
import org.apache.log4j.Logger;

/**
 * A Commandline tool for exporting all the objects of specified classes to
 * one or many specified file types.
 *
 * @author <a href="mailto:tosmers@uni-bremen.de>Tobias Osmers<\a>
 * @version created on 25.05.16
 */
public class ExportCliTool extends Program {

    private final static Logger logger = Logger.getLogger(ExportCliTool.class);

    /**
     * Constructor for the command line tool.
     */
    private ExportCliTool() {
        super("Export Commandline Tool",
              "1.0.0",
              "Exportation of POJOs...");
    }

    /**
     * Main method, which calls the {@code doRun}-method and hands the given
     * arguments over to that method.
     *
     * @param args The command line arguments
     */
    public static void main(String[] args) {
        new ExportCliTool().run(args);
    }

    /**
     * This method differentiates between multiple commands. Through the
     * parameter the command line arguments will be matched to predefined
     * commands which will then depending on the case define what will be
     * called or executed.
     *
     * The commands are:
     *      {@code help} which prints just the usage of this tool
     *      {@code export} which executes the process of exporting whatever
     *      is required to be exported
     *
     * @param cmdLine The parsed command line arguments
     */
    @Override
    protected void doRun(CommandLine cmdLine) {
        final String[] args = cmdLine.getArgs();

        if (args.length < 1) {
            printUsage();
            System.exit(-1);
        }
        
        final String command = args[0];
        System.out.printf("command: %s\n", command);

        switch (command) {
            case "help":
                printUsage();
                break;

            case "export":
                export(args);
                break;

            default:
                printUsage();
                break;
        }
    }

    /**
     * Method defining the process of exporting after its command has been
     * triggered.
     *
     * @param args The secondary command line arguments
     */
    private void export(String[] args) {
        if (args.length != 3) {
            printUsage();
            System.exit(-1);
        }

        final String moduleClass = args[1];
        System.err.printf("module-class: %s\n", moduleClass);
        final String pathName = args[2];
        System.err.printf("path: %s\n", pathName);
        ExportHelper.setPath(pathName);
        System.err.printf("\n");


        try {
            switch (moduleClass) {
                case "categories":
                    convert();
                    ExportHelper.exportCategories();
                    break;

                case "categorizations":
                    convert();
                    ExportHelper.exportCategorizations();
                    break;

                case "users":
                    convert();
                    ExportHelper.exportUsers();
                    break;

                case "groups":
                    convert();
                    ExportHelper.exportGroups();
                    break;

                case "groupMemberships":
                    convert();
                    ExportHelper.exportGroupMemberships();
                    break;

                case "roles":
                    convert();
                    ExportHelper.exportRoles();
                    break;

                case "roleMemberships":
                    convert();
                    ExportHelper.exportRoleMemberships();
                    break;

                case "workflowTemplates":
                    convert();
                    ExportHelper.exportWorkflowTemplates();
                    break;

                case "workflows":
                    convert();
                    ExportHelper.exportWorkflows();
                    break;

                case "assignableTasks":
                    convert();
                    ExportHelper.exportAssignableTasks();
                    break;

                case "taskAssignments":
                    convert();
                    ExportHelper.exportTaskAssignments();
                    break;

                case "permissions":
                    convert();
                    ExportHelper.exportPermissions();
                    break;

                case "all_core":
                    convert();
                    System.out.println("Started exporting all ng-objects...");
                    ExportHelper.exportCategories();
                    ExportHelper.exportCategorizations();
                    ExportHelper.exportUsers();
                    ExportHelper.exportGroups();
                    ExportHelper.exportGroupMemberships();
                    ExportHelper.exportRoles();
                    ExportHelper.exportRoleMemberships();
                    ExportHelper.exportWorkflowTemplates();
                    ExportHelper.exportWorkflows();
                    ExportHelper.exportAssignableTasks();
                    ExportHelper.exportTaskAssignments();
                    ExportHelper.exportPermissions();
                    System.out.println("Finished exports.");
                    System.out.printf("\n");
                    break;

                default:
                    printUsage();
                    break;
            }
        } catch (Exception ex) {
            logger.error("ERROR while exporting", ex);
        }
    }

    /**
     * Method for converting all trunk objects into ng objects at once.
     */
    private void convert() {
        try {
            System.err.println("Started conversions of systems objects to " +
                    "ng-objects...");
            MainConverter.startConversionToNg();
            System.err.println("Finished conversions.");
            System.out.printf("\n");
        } catch (Exception e) {
            logger.error("ERROR while converting trunk-objects to " +
                    "ng-objects", e);
        }
    }



    /**
     * Prints the usage of this command line tool.
     */
    private void printUsage() {
        System.err.printf(
        "\n" +
        "\t\t\t    --- ExportCliTool ---\n" +
        "\n" +
        "usage:\t<command> <module-class> <path>\n" +
        "\n" +
        "Available commands:\n" +
        "\thelp" +
                "\t\t\t\t\t Shows information on how to use this tool.\n" +
        "\texport <module-class> <path>" +
                "\t\t Exports the chosen module class to a file\n" +
                "\t\t\t\t" +
                "\t\t at the location specified by the given path." +
        "\n" +
        "\n" +
        "Available module-classes for export:\n" +
        "   \t\t categories        \t\t all categories of the system\n" +
        "   \t\t categorizations   \t\t all categorizations of the system\n" +
        "   \t\t users             \t\t all users of the system\n" +
        "   \t\t groups            \t\t all groups of the system\n" +
        "   \t\t groupMemberships  \t\t all groupsMemberships of the system\n" +
        "   \t\t roles             \t\t all roles of the system\n" +
        "   \t\t roleMemberships   \t\t all roleMemberships of the system\n" +
        "   \t\t workflowTemplates \t\t all workflowTemplates of the system\n" +
        "   \t\t workflows         \t\t all workflows of the system\n" +
        "   \t\t assignableTasks   \t\t all assignableTasks of the system\n" +
        "   \t\t taskAssignments   \t\t all taskAssignments of the system\n" +
        "   \t\t permissions       \t\t all permissions of the system\n" +
        "   \n" +
        "   \t\t all_core          \t\t all objects of the entire core module" +
        "\n" +
        "\n" +
        "" +
        "Do use for exporting java objects of a specified class.\n" +
        "\n"
        );
    }
}
