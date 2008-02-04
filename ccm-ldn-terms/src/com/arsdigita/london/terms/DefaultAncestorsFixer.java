/*
 * Copyright (C) 2005 Runtime Collective Ltd. All Rights Reserved.
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

package com.arsdigita.london.terms;

import com.arsdigita.categorization.Category;
import com.arsdigita.categorization.CategoryCollection;
import com.arsdigita.london.util.Program;
import com.arsdigita.london.util.Transaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class DefaultAncestorsFixer extends Program {
    
    private static final Logger s_log = Logger.getLogger(DefaultAncestorsFixer.class);
    
    public static void main(String[] args) {
        new DefaultAncestorsFixer().run(args);
    }

    public DefaultAncestorsFixer() {

        super("DefaultAncestorsFixer",
              "1.0.1",
              "");

        Options options = getOptions();
        
        options.addOption(OptionBuilder.hasArgs()
                          .withLongOpt("domain" )
                          .withDescription("Domain to fix." )
                          .create("d"));
    }

    public void doRun(CommandLine cmdLine) {

        s_log.info("Running.");

        final String domainName;
        if (cmdLine.hasOption("d")) {
            domainName = cmdLine.getOptionValues("d")[0];
        } else {
            domainName = "LGCL";
        }

        Transaction trans = new Transaction() {
                public void doRun() {
                    Domain dom = Domain.retrieve(domainName);
                    Category cat = dom.getModel();
                    fixChildren(cat);
                }
            };
        trans.run();

        s_log.info("All done.");
    }

    /** Recursive method to fix a category tree. */
    public void fixChildren(Category parent) {

        CategoryCollection children = parent.getChildren();
        Category cat;
        Category par;
        s_log.info("Processing parent category : "+parent.getName()+" ("+parent.getID()+").");

        while (children.next()) {
            cat = children.getCategory();
            s_log.info("Looking at child category : "+cat.getName()+" ("+cat.getID()+").");
            try {
                // only fix default children
                par = cat.getDefaultParentCategory();
                if (par != null && par.getID() == parent.getID()) {
                    s_log.info("Setting default ancestor.");
                    cat.setDefaultAncestors(parent);
                    fixChildren(cat);
                }
            } catch (Exception e) {
                // no default parent, or something else
                s_log.info("Could not process!", e);
            }
        }

        children.close();
    }
}

