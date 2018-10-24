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
package org.libreccm.export.cmd;

import com.arsdigita.util.cmd.Program;

import org.apache.commons.cli.CommandLine;
import org.libreccm.export.ExportManager;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * A Commandline tool for exporting all the objects of specified classes to
 * one or many specified file types.
 *
 * @author <a href="mailto:tosmers@uni-bremen.de">Tobias Osmers</a>
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class ExportCliTool extends Program {
    /**
     * Constructor for the command line tool.
     */
    private ExportCliTool() {
        super("Export Commandline Tool",
              "1.0.0",
              "Export of all entities for reimport in LibreCCM 7");
    }

    /**
     * Main method, which calls the {@code doRun}-method and hands the given
     * arguments over to that method.
     *
     * @param args The command line arguments
     */
    public static void main(final String[] args) {
        new ExportCliTool().run(args);
    }

   
    @Override
    protected void doRun(final CommandLine cmdLine) {
        String[] args = cmdLine.getArgs();

        if (args.length < 1) {
            System.out.println("Usage ExportCliTool $targetDir");
            System.exit(-1);
        }
        
        final String targetDir = args[0];
        final Path targetDirPath = Paths.get(targetDir);
        
        ExportManager.getInstance().exportData(targetDirPath);
    }

}
