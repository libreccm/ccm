package com.arsdigita.cms.scipublications;

import com.arsdigita.cms.contenttypes.Publication;
import com.arsdigita.cms.scipublications.exporter.SciPublicationsExporter;
import com.arsdigita.cms.scipublications.exporter.SciPublicationsExporters;
import com.arsdigita.globalization.GlobalizationHelper;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.Filter;
import com.arsdigita.persistence.FilterFactory;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.util.cmd.Program;

import org.apache.commons.cli.CommandLine;
import org.apache.log4j.Logger;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.logging.Level;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class ExportAllPublications extends Program {

    private static final Logger LOGGER = Logger
        .getLogger(ExportAllPublications.class);

    public ExportAllPublications() {
        super("Export all publications",
              "1.0.0",
              "FORMAT TARGETFILE");
    }

    @Override
    protected void doRun(final CommandLine cmdLine) {

        final String[] args = cmdLine.getArgs();

        if (args.length != 2) {
            help(System.err);
            System.exit(1);
        }

        System.out.printf("Exporting all publications to file %s as %s%n",
                          args[1],
                          args[0]);

        final DataCollection publications = SessionManager
            .getSession()
            .retrieve(Publication.BASE_DATA_OBJECT_TYPE);

        if (Kernel.getConfig().languageIndependentItems()) {
            final FilterFactory filterFactory = publications.getFilterFactory();
            final Filter filter = filterFactory.or().
                addFilter(filterFactory.equals("language", GlobalizationHelper.
                                               getNegotiatedLocale()
                                               .getLanguage())).
                addFilter(filterFactory.and().
                    addFilter(
                        filterFactory.equals("language",
                                             GlobalizationHelper.LANG_INDEPENDENT))
                    .addFilter(filterFactory.notIn("parent",
                                                   "com.arsdigita.navigation.getParentIDsOfMatchedItems")
                        .set("language", GlobalizationHelper.
                             getNegotiatedLocale().getLanguage())));
            publications.addFilter(filter);

        } else {
            publications.addEqualsFilter("language",
                                         GlobalizationHelper.
                                             getNegotiatedLocale().getLanguage());
        }

        publications.addOrder("yearOfPublication desc");
        publications.addOrder("authorsStr");
        publications.addOrder("title");

        System.out.printf("Found %d publications.", publications.size());

        final SciPublicationsExporter exporter = SciPublicationsExporters
            .getInstance()
            .getExporterForFormat(args[0]);

        if (exporter == null) {
            LOGGER.warn(String.format(
                "The requested export format '%s' is not supported yet.%n",
                args[0]));
            System.err.printf(
                "The requested export format '%s' is not supported yet.%n",
                args[0]);

            return;
        }

        final Path targetPath = Paths.get(args[1]);
        try (BufferedWriter writer = Files
            .newBufferedWriter(targetPath, StandardOpenOption.CREATE)) {

            writer.append(exporter.getPreamble());

            long index = 1;
            while (publications.next()) {

                System.out.printf("Exporting publication %d of %d...%n",
                                  index,
                                  publications.size());

                final Publication publication = new Publication(publications
                    .getDataObject());
                writer.append(exporter.exportPublication(publication));
                index++;
            }

        } catch (IOException ex) {
            System.err.printf("Failed to use target file %s%n", args[1]);
            ex.printStackTrace(System.out);
            System.exit(1);
            return;
        }

        System.out.printf("Exported %d publications successfully.%n",
                          publications.size());

        LOGGER.info(String.format(
            "Exporting all publications to file %s as %s.",
            args[1],
            args[0]));

    }

    public static final void main(final String[] args) {

        new ExportAllPublications().run(args);
    }

}
