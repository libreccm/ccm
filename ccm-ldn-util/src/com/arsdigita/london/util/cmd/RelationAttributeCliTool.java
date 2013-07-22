/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.arsdigita.london.util.cmd;

import com.arsdigita.cms.RelationAttribute;
import com.arsdigita.cms.RelationAttributeCollection;
import com.arsdigita.london.util.Transaction;
import com.arsdigita.util.cmd.Program;
import org.apache.commons.cli.CommandLine;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 */
public class RelationAttributeCliTool extends Program {

    public RelationAttributeCliTool() {
        super("RelationAttributeCliTool", "1.0.0", "");
    }

    @Override
    protected void doRun(final CommandLine cmdLine) {

        final String[] args = cmdLine.getArgs();

        if (args.length == 0) {
            printUsage();
            System.exit(-1);
        }

        final String command = args[0];

        System.out.printf("command is %s", command);

        if ("list".equals(command)) {
            list(args);
        } else if ("add".equals(command)) {
            add(args);
        } else if ("alter".equals(command)) {
            alter(args);
        } else if ("remove".equals(command)) {
            remove(args);
        } else if ("help".equals(command)) {
            printUsage();
            System.exit(0);
        } else {
            printUsage();
            System.exit(-1);
        }


//        new Transaction() {
//            @Override
//            protected void doRun() {
//                final RelationAttribute attr = new RelationAttribute();
//                attr.setAttribute("test");
//                attr.setKey("test");
//                attr.setLanguage("de");
//                attr.setName("test");
//            }
//
//        }.run();

    }

    public static void main(String args[]) {
        new RelationAttributeCliTool().run(args);
    }

    private void list(final String[] args) {

        final RelationAttributeCollection enums;
        if (args.length >= 2) {
            enums = new RelationAttributeCollection(args[1]);
            System.out.print("\n");
            System.out.printf("All values of enum '%s':\n\n", args[1]);
        } else {
            enums = new RelationAttributeCollection();

            System.out.print("\n");
            System.out.print("All available enums and values:\n\n");
        }

        System.out.print("Enum Key Lang Value Description\n");

        while (enums.next()) {
            printEnumValue(enums.getRelationAttribute());
        }

    }

    private void printEnumValue(final RelationAttribute value) {
        System.out.printf("%s %s %s %s %s\n",
                          value.getAttribute(),
                          value.getKey(),
                          value.getLanguage(),
                          value.getName(),
                          value.getDescription());
    }

    private void add(final String[] args) {
        if (args.length < 5) {
            printUsage();
            System.exit(-1);
        }

        final String enumname = args[1];
        final String key = args[2];
        final String lang = args[3];
        final String value = args[4];

        new Transaction() {
            @Override
            protected void doRun() {
                final RelationAttribute enumvalue = new RelationAttribute();
                enumvalue.setAttribute(enumname);
                enumvalue.setKey(key);
                enumvalue.setLanguage(lang);
                enumvalue.setName(value);
                if (args.length >= 6) {
                    enumvalue.setDescription(args[5]);
                }

                enumvalue.save();
            }

        }.run();
        
        System.out.printf("Added %s %s %s %s\n", enumname, key, lang, value);
    }

    private void alter(final String[] args) {
        
    }

    private void remove(final String[] args) {
    }

    private void printUsage() {
        System.err.println("Usage ReleationAttributeCliTool command parameters");
        System.err.println("Available commands:");
        System.err.println("\tlist");
        System.err.println("\tadd");
        System.err.println("\talter");
        System.err.println("\tremove");

        System.err.println(" ");

        System.err.println("list\t Shows all avaiable enums and there values.");
        System.err.println("list $enum\t Shows all avaiable values for the enum $enum.");
        System.err.println(
                "add $enum $key $lang $value [$desc]\t Adds a value $value for the specified key $key and specficied language $lang to to enum $enum. If given the description is also set.");
        System.err.println(
                "alter $enum $key $lang $value [$desc]\t Alters a value $value for the specified key $key and specficied language $lang to to enum $enum. If given the description is also set.");
        System.err.println("remove $enum\t Removes the provided enum completly.");
        System.err.println("remove $enum $key\t Removes the all values for the provided key from the given enum.");
        System.err.println(
                "remove $enum $key $values\t Removes the value identified by $key and $lang from the given enum.");
    }

}
