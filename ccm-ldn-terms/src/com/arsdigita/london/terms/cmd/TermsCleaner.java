package com.arsdigita.london.terms.cmd;

import com.arsdigita.domain.DomainCollection;
import com.arsdigita.london.terms.Domain;
import com.arsdigita.london.terms.Term;
import com.arsdigita.london.util.Transaction;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.util.cmd.Program;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionBuilder;
import org.apache.log4j.Logger;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class TermsCleaner extends Program {

//    public static final Logger LOGGER = Logger.getLogger(TermsCleaner.class);

    public TermsCleaner() {
        super("TermsCleaner",
              "1.0.0",
              "");

        getOptions()
            .addOption(
                OptionBuilder
                    .hasArg(false)
                    .withLongOpt("clean-domains")
                    .withDescription(
                        "Cleanup empty domains after removing orphaned terms")
                    .create('c'));

        getOptions()
            .addOption(
                OptionBuilder
                    .hasArg(false)
                    .withLongOpt("pretend")
                    .withDescription(
                        "Only print what would be done but don't remove any terms etc.")
                    .create('p'));
    }

    public static void main(final String[] args) {
        new TermsCleaner().run(args);
    }

    @Override
    protected void doRun(final CommandLine cmdLine) {

        final boolean cleanDomains = cmdLine.hasOption('c');
        final boolean pretend = cmdLine.hasOption('p');

        if (pretend) {
            System.err.println(
                "Pretend option is set. Not action will be taken.");
        }

        final DataCollection domainsData = SessionManager
            .getSession()
            .retrieve(Domain.BASE_DATA_OBJECT_TYPE);

        final DomainCollection domains = new DomainCollection(domainsData);

        System.out.printf(String.format("Found %d domains.%n", domains.size()));

        final Transaction txn = new Transaction() {

            @Override
            protected void doRun() {
                while (domains.next()) {
                    removeOrphanedTerms((Domain) domains.getDomainObject(),
                                        pretend);
                }

                if (cleanDomains) {
                    System.out.println("Removing domains with no terms...");
                    domains.reset();

                    while (domains.next()) {
                        removeEmptyDomain((Domain) domains.getDomainObject(),
                                          pretend);
                    }
                }
            }

        };

        try {
            txn.run();
        } catch (Exception ex) {
            System.err.println("Failed to clean orphaned terms:");
            ex.printStackTrace(System.err);
        }

    }

    private void removeOrphanedTerms(final Domain domain,
                                     final boolean pretend) {

        System.out.printf("Removing orphaned terms from Domain '%s'...%n",
                          domain.getKey());

        final DomainCollection orphanedTerms = domain.getOrphanedTerms();
        System.out.printf("Found %d orphaned terms.%n",
                          orphanedTerms.size());

        while (orphanedTerms.next()) {
            final Term orphanedTerm = (Term) orphanedTerms.getDomainObject();

            if (pretend) {
                System.out.printf("Would remove orphaned term '%s'...%n",
                                  orphanedTerm.getName());
            } else {
                System.out.printf("Removing orphaned term '%s'...%n",
                                  orphanedTerm.getName());
                orphanedTerm.delete();
            }
        }
    }

    private void removeEmptyDomain(final Domain domain,
                                   final boolean pretend) {

        final DomainCollection terms = domain.getTerms();

        if (terms.isEmpty()) {
            if (pretend) {
                System.out.printf(
                    "Domain '%s' has no terms and would be removed if pretend "
                        + "is not set.%n",
                    domain.getKey());
            } else {
                System.out.printf("Domain '%s' has not terms. "
                                      + "Removing domain...%n",
                                  domain.getKey());
                domain.delete();
            }
        }
    }

}
