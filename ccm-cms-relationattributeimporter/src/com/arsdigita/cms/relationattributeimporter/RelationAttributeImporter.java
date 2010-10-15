/*
 * Copyright (c) 2010 Jens Pelzetter,
 * for the Center of Social Politics of the University of Bremen
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
package com.arsdigita.cms.relationattributeimporter;

import com.arsdigita.cms.RelationAttribute;
import com.arsdigita.cms.RelationAttributeInterface;
import com.arsdigita.london.util.Transaction;
import com.arsdigita.packaging.Program;
import java.io.File;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.apache.commons.cli.CommandLine;
import org.xml.sax.SAXException;

/**
 * <p>
 * A simple import tool for {@link RelationAttribute}s. This tool imports the
 * relation attributes from a simple XML file, which looks like this:
 * </p>
 * <pre>
 * &lt;?xml version="1.0"?&gt;
 * &lt;relationAttributes&gt;
 *     &lt;relationAttribute&gt;
 *         &lt;attribute&gt;...&lt;/attribute&gt;
 *         &lt;key&gt;...&lt;/key&gt;
 *         &lt;lang&gt;...&lt;/lang&gt;
 *         &lt;name&gt;...&lt;/name&gt;
 *         &lt;description&gt;...&lt;/description&gt;
 *     &lt;/relationAttribute&gt;
 * &lt;/relationAttributes&gt;
 * </pre>
 * <p>
 * The tool can be invoked using the ccm-run command. One parameter, the file
 * to import is needed. With tools-ng and ECDC, the line for calling the
 * <code>RelationAttributeImporter</code> would like the following:
 * </p>
 * <pre>
 * ant -Dccm.classname="com.arsdigita.cms.relationattributeimporter.RelationAttributeImporter" -Dccm.parameters="/path/to/relation/attribute/file.xml" ccm-run
 * </pre>
 * <p>
 * You have to add the <code>RelationAttributeImporter</code> to add to your
 * environment, of course.
 * </p>
 *
 * @author Jens Pelzetter
 * @see RelationAttribute
 * @see RelationAttributeInterface
 */
public class RelationAttributeImporter extends Program {

    public RelationAttributeImporter() {
        super("Relation Attribute Importer", "0.1.0", "FILE");
    }

    public RelationAttributeImporter(boolean startup) {
        super("Relation Attribute Importer", "0.1.0", "FILE", startup);
    }

    @Override
    protected void doRun(CommandLine cmdLine) {

        final String[] args;
        File relAttrFile;
        SAXParserFactory saxFactory;
        SAXParser saxParser = null;
        RelationAttributeParser parser;
        args = cmdLine.getArgs();
        if (args.length != 1) {
            help(System.err);
            System.exit(-1);
        }

        System.out.printf("Using file %s.", args[0]);
        relAttrFile = new File(args[0]);
        if (!relAttrFile.exists()) {
            System.err.printf("ERROR: File %s does not exist.", args[0]);
            System.exit(-1);
        }
        if (!relAttrFile.isFile()) {
            System.err.printf("ERROR: Path %s does not point to a file.",
                              args[0]);
            System.exit(-1);
        }
        saxFactory = SAXParserFactory.newInstance();
        try {
            saxParser = saxFactory.newSAXParser();
        } catch (ParserConfigurationException ex) {
            System.err.printf("Error creating SAXParser: %s", ex.getMessage());
            ex.printStackTrace();
            System.exit(-1);
        } catch (SAXException ex) {
            System.err.printf("Error creating SAXParser: %s", ex.getMessage());
            ex.printStackTrace();
            System.exit(-1);
        }
        parser = new RelationAttributeParser();
        try {
            saxParser.parse(relAttrFile, parser);
        } catch (SAXException ex) {
            System.err.printf("Error parsing file %s: %s",
                              args[0],
                              ex.getMessage());
            ex.printStackTrace();
            System.exit(-1);
        } catch (IOException ex) {
            System.err.printf("Error parsing file %s: %s",
                              args[0], ex.getMessage());
            ex.printStackTrace();
            System.exit(-1);
        }

        System.out.println(
                "Parsed XML file successfully. Creating ACSObjects...");
        for (int i = 0; i < parser.getRelAttrs().size(); i++) {
            System.out.printf("%d of %d...", (i + 1), parser.getRelAttrs().size());
            createACSObject(parser.getRelAttrs().get(i), i);
        }
    }

    /**
     * The method creates the {@link RelationAttribute} object from the data
     * retrieved from the XML file and stores the object in the database.
     *
     * @param relAttr
     * @param index
     */
    private void createACSObject(final RelAttrBean relAttr, int index) {

        /*System.out.printf("\tattribute   = %s\n", relAttr.getAttribute());
        System.out.printf("\tkey         = %s\n", relAttr.getKey());
        System.out.printf("\tlang        = %s\n", relAttr.getLang());
        System.out.printf("\tname        = %s\n", relAttr.getName());
        System.out.printf("\tdescription = %s\n", relAttr.getDescription());*/

        Transaction transaction = new Transaction() {
            @Override
            public void doRun() {
                RelationAttribute attr;
                attr = new RelationAttribute();
                attr.setAttribute(relAttr.getAttribute());
                attr.setKey(relAttr.getKey());
                attr.setLanguage(relAttr.getLang());
                attr.setName(relAttr.getName());
                attr.setDescription(relAttr.getDescription());
                System.out.print("Saving...");
                attr.save();
            }
        };
        transaction.run();
        System.out.println("Done\n");
    }

    public final static void main(String[] args) {
        new RelationAttributeImporter().run(args);
    }
}
