/*
 * Copyright (c) 2012 Jens Pelzetter, ScientificCMS.org team
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
package com.arsdigita.cms.scipublications.importer;

import com.arsdigita.cms.scipublications.imexporter.PublicationFormat;
import com.arsdigita.util.cmd.Program;
import java.io.File;
import java.io.IOException;
import java.util.List;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class ImporterCli extends Program {

    private static final Logger LOGGER = Logger.getLogger(ImporterCli.class);
    private static final String PRETEND = "pretend";
    private static final String PUBLISH = "publish";
    private static final String LIST = "list";

    public ImporterCli() {
        super("ImporterCli", "1.0.0", "ImporterCli [--pretend] [--publish] file | dir OR ImporterCLI --list");

        final Options options = getOptions();

        options.addOption(OptionBuilder
                .withLongOpt(PRETEND)
                .withDescription("Do not perform import, only print results.")
                .create());
        options.addOption(OptionBuilder
                .withLongOpt(PUBLISH)
                .withDescription("Publish created publications")
                .create());
        options.addOption(OptionBuilder
                .withLongOpt(LIST)
                .withDescription("List all available importers and exit")
                .create());        
    }

    public static void main(final String args[]) {
        new ImporterCli().run(args);
    }

    @Override
    protected void doRun(final CommandLine cmdLine) {
        LOGGER.info("Publications Importer CLI tool.");
        if (cmdLine.hasOption(LIST)) {
            final List<PublicationFormat> formats = SciPublicationsImporters.getInstance().getSupportedFormats();
            LOGGER.info("Supported formats:");            
            for(PublicationFormat format : formats) {
                LOGGER.info(String.format("%s, MIME type: %s, file extension: %s", format.getName(), 
                                                                     format.getMimeType().toString(),
                                                                     format.getFileExtension()));
            }
            return;
        }
        
        final boolean pretend = cmdLine.hasOption(PRETEND);
        final boolean publish = cmdLine.hasOption(PUBLISH);

        if (cmdLine.getArgs().length != 1) {
            LOGGER.error("Missing file/directory to import.");
            help(System.err);
            return;
        }

        final String sourceName = cmdLine.getArgs()[0];
        final File source = new File(sourceName);
        importFile(source, pretend, publish);
    }

    protected void importFile(final File file, final boolean pretend, final boolean publish) {
        if (file == null) {
            throw new IllegalArgumentException("File object is null.");
        }

        if (file.isDirectory()) {
            final File[] files = file.listFiles();
            for (File f : files) {
                importFile(f, pretend, publish);
            }
        } else if (file.isFile()) {
            final String fileName = file.getName();
            final String extension = fileName.substring(fileName.lastIndexOf('.'));
            final PublicationFormat format = findFormatForExtension(extension);
            if (format == null) {
                LOGGER.error(String.format("No importer for publication format identified "
                        + "by file extension '%s' available.",
                        extension));                
                return;
            }
            
            final SciPublicationsImporter importer = SciPublicationsImporters.getInstance().getImporterForFormat(
                    format.getName());    
            final String data;
            try {
                data = FileUtils.readFileToString(file);
            } catch (IOException ex) {
                LOGGER.error(String.format("Failed to read file '%s'.", file.getAbsolutePath()), ex);
                return;
            }
            LOGGER.info(String.format("Importing publications from file '%s'...", file.getAbsolutePath()));
            importer.importPublications(data, pretend, publish);
        } else {
            LOGGER.info(String.format("File %s does not exist.", file.getAbsolutePath()));
        }
    }

    protected PublicationFormat findFormatForExtension(final String extension) {
        final List<PublicationFormat> formats = SciPublicationsImporters.getInstance().getSupportedFormats();

        for (PublicationFormat format : formats) {
            if (format.getFileExtension().equals(extension)) {
                return format;
            }
        }

        return null;
    }

}
