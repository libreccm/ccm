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
import com.arsdigita.cms.scipublications.importer.report.ImportReport;
import com.arsdigita.util.cmd.Program;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class ImporterCli extends Program {

    //private static final Logger LOGGER = Logger.getLogger(ImporterCli.class);
    private static final String PRETEND = "pretend";
    private static final String PUBLISH = "publish";
    private static final String LIST = "list";

    public ImporterCli() {
        super("ImporterCli", "1.0.0", "ImporterCli [--pretend] [--publish] file | directory [parameters for importer] OR ImporterCLI --list");

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
    public void help(final OutputStream stream) {
        super.help(stream);
        
        final PrintWriter writer = new PrintWriter(stream);
        writer.append("parameters for importer: Optional parameters for the importer, provided in the following format:");
        writer.append("parameter1=value1;parameter2=value2;...");
    }

    @Override
    protected void doRun(final CommandLine cmdLine) {
        try {
            final PrintWriter writer = new PrintWriter(System.out);
            final PrintWriter errWriter = new PrintWriter(System.err);

            writer.printf("Publications Importer CLI tool.\n");
            writer.flush();
            if (cmdLine.hasOption(LIST)) {
                final List<PublicationFormat> formats = SciPublicationsImporters.getInstance().getSupportedFormats();
                writer.printf("Supported formats:\n");
                for (PublicationFormat format : formats) {
                    writer.printf("%s, MIME type: %s, file extension: %s\n", format.getName(),
                                  format.getMimeType().toString(),
                                  format.getFileExtension());
                }
                writer.flush();
                return;
            }

            final boolean pretend = cmdLine.hasOption(PRETEND);
            final boolean publish = cmdLine.hasOption(PUBLISH);

            if (cmdLine.getArgs().length < 1) {
                errWriter.printf("Missing file/directory to import.\n");
                errWriter.flush();
                help(System.err);
                return;
            }

            final String sourceName = cmdLine.getArgs()[0];
            final Map<String, String> importerParams = new HashMap<String, String>();
            if (cmdLine.getArgs().length >= 2) {
                final String importerParamsStr = cmdLine.getArgs()[1];
                final String[] tokens = importerParamsStr.split(";");
                for(String token : tokens) {
                    final String[] valueTokens = token.split("=");
                    if (valueTokens.length == 2) {
                        importerParams.put(valueTokens[0], valueTokens[1]);
                    }
                }
            }
            
            final File source = new File(sourceName);
            importFile(source, importerParams, pretend, publish);

            errWriter.flush();
            writer.flush();
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
        }
    }

    protected void importFile(final File file, 
                              final Map<String, String> importerParams, 
                              final boolean pretend, final boolean publish) {
        final PrintWriter writer = new PrintWriter(System.out);
        final PrintWriter errWriter = new PrintWriter(System.err);

        if (file == null) {
            throw new IllegalArgumentException("File object is null.");
        }

        if (file.isDirectory()) {
            final File[] files = file.listFiles();
            for (File f : files) {
                importFile(f, importerParams, pretend, publish);
            }
        } else if (file.isFile()) {
            final String fileName = file.getName();
            final String extension = fileName.substring(fileName.lastIndexOf('.') + 1);
            final PublicationFormat format = findFormatForExtension(extension);
            if (format == null) {
                errWriter.printf(String.format("No importer for publication format identified "
                                               + "by file extension '%s' available.\n",
                                               extension));
                errWriter.flush();
                return;
            }

            final SciPublicationsImporter importer = SciPublicationsImporters.getInstance().getImporterForFormat(
                    format.getName());
            final String data;
            try {
                data = FileUtils.readFileToString(file);
            } catch (IOException ex) {
                errWriter.printf(String.format("Failed to read file '%s'.\n", file.getAbsolutePath()), ex);
                errWriter.flush();
                return;
            }
            writer.printf("Importing publications from file '%s'...\n", file.getAbsolutePath());
            writer.flush();
            final ImportReport report;
            try {
                report = importer.importPublications(data, importerParams, pretend, publish);
            } catch (SciPublicationsImportException ex) {

                errWriter.printf("Import failed:\n");
                errWriter.printf("%s: %s\n", ex.getClass().getName(), ex.getMessage());
                ex.printStackTrace(errWriter);
                errWriter.flush();
                writer.flush();
                return;
            }

            writer.printf("Import finished. Report:\n\n");
            writer.print(report.toString());
            writer.flush();
        } else {
            errWriter.printf("File %s does not exist.\n", file.getAbsolutePath());
            errWriter.flush();
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
