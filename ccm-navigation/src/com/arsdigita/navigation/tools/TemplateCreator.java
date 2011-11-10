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
 */

package com.arsdigita.navigation.tools;

import com.arsdigita.navigation.Template;
import com.arsdigita.london.util.Program;
import com.arsdigita.london.util.Transaction;

import org.apache.commons.cli.CommandLine;

public class TemplateCreator extends Program {
    public TemplateCreator() {
        super( "Navigation Create Category Template",
               "1.0.0",
               "TITLE DESCRIPTION PATH" );
    }

    protected void doRun( CommandLine cmdLine ) {
        String[] args = cmdLine.getArgs();

        if( args.length != 3 ) {
            help( System.err );
            System.exit( 1 );
        }

        final String title = args[0];
        final String desc = args[1];
        final String path = args[2];

        Transaction txn = new Transaction() {
            protected void doRun() {
                Template.create( title, desc, path );
            }
        };
        txn.run();
    }

    public static void main( String[] args ) {
        new TemplateCreator().run( args );
    }
}