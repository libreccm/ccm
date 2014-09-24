package com.arsdigita.cms.contenttypes.utils;

import com.arsdigita.cms.RelationAttributeCollection;
import com.arsdigita.cms.contenttypes.GenericOrganizationalUnitPersonCollection;
import com.arsdigita.cms.contenttypes.SciProject;
import com.arsdigita.cms.contenttypes.SciProjectSponsorCollection;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.util.cmd.Program;
import java.io.File;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionBuilder;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 */
public class SciProjectCSVExporter extends Program {

    private static final String END_AFTER = "endAfter";
    private static final String END_BEFORE = "endBefore";
    private static final String START_AFTER = "startAfter";
    private static final String START_BEFORE = "startBefore";
    private static final String OMIT_FUNDING_VOLUME = "omitFundingVolume";
    private static final String OMIT_FUNDING_CODE = "omitFundingCode";
    private static final String OMIT_ROLES = "omitRoles";
    private static final String WITH_DESC = "withDesc";
    private static final String COLUMN_SEPARATOR = "\t";
    private static final String LINE_SEPARATOR = "\n";

    private boolean withRoles = true;
    private boolean withFundingCode = true;
    private boolean withFundingVolume = true;
    private boolean withDesc = false;

    public SciProjectCSVExporter() {
        super("SciProjectCSVExporter", "1.0.0", "");

        getOptions().addOption(OptionBuilder
                .hasArg(true)
                .withLongOpt(START_BEFORE)
                .withDescription(
                        "Include only projects started before a data. Date is in ISO format (yyyy-mm-dd)").
                create());

        getOptions().addOption(OptionBuilder
                .hasArg(true)
                .withLongOpt(START_AFTER)
                .withDescription(
                        "Include only projects started after a data. Date is in ISO format (yyyy-mm-dd)").
                create());

        getOptions().addOption(OptionBuilder
                .hasArg(true)
                .withLongOpt(END_BEFORE)
                .withDescription(
                        "Include only projects finished before a data. Date is in ISO format (yyyy-mm-dd)").
                create());

        getOptions().addOption(OptionBuilder
                .hasArg(true)
                .withLongOpt(END_AFTER)
                .withDescription(
                        "Include only projects finished after a data. Date is in ISO format (yyyy-mm-dd)").
                create());

        getOptions().addOption(OptionBuilder
                .hasArg(false)
                .withLongOpt(OMIT_FUNDING_VOLUME)
                .withDescription("Omit funding volume")
                .create());

        getOptions().addOption(OptionBuilder
                .hasArg(false)
                .withLongOpt(OMIT_FUNDING_CODE)
                .withDescription("Omit funding code")
                .create());

        getOptions().addOption(OptionBuilder
                .hasArg(false)
                .withLongOpt(OMIT_ROLES)
                .withDescription("Omit roles")
                .create());

        getOptions().addOption(OptionBuilder
                .hasArg(false)
                .withLongOpt(WITH_DESC)
                .withDescription("Add description to CSV")
                .create());

    }

    public static void main(final String[] args) {
        new SciProjectCSVExporter().run(args);
    }

    @Override
    public void doRun(final CommandLine cmdLine) {

        final StringBuffer buffer = new StringBuffer();

        final Session session = SessionManager.getSession();
        final DataCollection projects = session.retrieve(SciProject.BASE_DATA_OBJECT_TYPE);

        if (cmdLine.hasOption(START_BEFORE)) {
            projects.addFilter(String.format("projectBegin <= '%s'",
                                             cmdLine.getOptionValue(START_BEFORE)));
        } else if (cmdLine.hasOption(START_AFTER)) {
            projects.addFilter(String.format("projectBegin >= '%s'",
                                             cmdLine.getOptionValue(START_AFTER)));
        }

        if (cmdLine.hasOption(END_BEFORE)) {
            projects.addFilter(String.format("projectEnd <= '%s'",
                                             cmdLine.getOptionValue(END_BEFORE)));
        } else if (cmdLine.hasOption(END_AFTER)) {
            projects.addFilter(String.format("projectEnd >= '%s'",
                                             cmdLine.getOptionValue(END_AFTER)));
        }

        if (cmdLine.hasOption(OMIT_ROLES)) {
            withRoles = false;
        }

        if (cmdLine.hasOption(OMIT_FUNDING_CODE)) {
            withFundingCode = false;
        }

        if (cmdLine.hasOption(OMIT_FUNDING_VOLUME)) {
            withFundingVolume = false;
        }

        if (cmdLine.hasOption(WITH_DESC)) {
            withDesc = true;
        }

        //First line with column labels
        createColumn("Name", buffer);
        createColumn("Begin", buffer);
        createColumn("End", buffer);
        createColumn("Members", buffer);
        createColumn("Short description", buffer);
        if (withDesc) {
            createColumn("Description", buffer);
        }

        createColumn("Sponsors", buffer);

        if (withFundingVolume) {
            createColumn("Funding volme", buffer);
        }
        
        buffer.append(LINE_SEPARATOR);

        while (projects.next()) {

            final SciProject project = (SciProject) DomainObjectFactory.newInstance(projects
                    .getDataObject());

            createLine(project, buffer);

        }

        projects.close();

        System.out.print(buffer.toString());
        try {
            if (cmdLine.getArgs().length > 0) {
                final File out = new File(cmdLine.getArgs()[0]);
                final FileOutputStream outStream = new FileOutputStream(out);
                outStream.write(buffer.toString().getBytes());
                outStream.flush();
                outStream.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
        }
    }

    private void createLine(final SciProject project, final StringBuffer buffer) {
        createColumn(project.getTitle(), buffer);

        final DateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ROOT);

        if (project.getBegin() == null) {
            createColumn("", buffer);
        } else {
            createColumn(isoFormat.format(project.getBegin()), buffer);
        }
        if (project.getEnd() == null) {
            createColumn("", buffer);
        } else {
            createColumn(isoFormat.format(project.getEnd()), buffer);
        }

        createMemberColumn(project, buffer);

        createColumn(String.format("\"%s\"", project.getProjectShortDescription()), buffer);
        if (withDesc) {
            createColumn(String.format("\"%s\"", project.getProjectDescription()), buffer);
        }

        createSponsorColumn(project, buffer);

        if (withFundingVolume && project.getFundingVolume() != null) {
            createColumn(project.getFundingVolume(), buffer);
        }

        buffer.append(LINE_SEPARATOR);
    }

    private void createColumn(final String value, final StringBuffer buffer) {
        buffer.append(value);
        buffer.append(COLUMN_SEPARATOR);
    }

    private void createMemberColumn(final SciProject project, final StringBuffer buffer) {
        final GenericOrganizationalUnitPersonCollection members = project.getPersons();

        while (members.next()) {
            buffer
                    .append(members.getSurname())
                    .append(", ")
                    .append(members.getGivenName());

            if (withRoles) {
                final String roleName = members.getRoleName();
                final String roleLabel;

                final RelationAttributeCollection roles = new RelationAttributeCollection(
                        SciProject.ROLE_ENUM_NAME, roleName);
                roles.addLanguageFilter(Kernel.getConfig().getDefaultLanguage());

                if (roles.isEmpty()) {
                    roleLabel = roleName;
                } else {
                    roles.next();
                    roleLabel = roles.getName();
                    roles.close();
                }

                buffer
                        .append(" (")
                        .append(roleLabel)
                        .append(')');
            }

            buffer.append("; ");
        }

        buffer.append(COLUMN_SEPARATOR);
    }

    private void createSponsorColumn(final SciProject project, final StringBuffer buffer) {
        final SciProjectSponsorCollection sponsors = project.getSponsors();

        while (sponsors.next()) {
            if (withFundingCode && sponsors.getFundingCode() != null) {
                buffer
                        .append(sponsors.getSponsor().getTitle())
                        .append(" (")
                        .append(sponsors.getFundingCode())
                        .append(");");
            } else {
                buffer
                        .append(sponsors.getSponsor().getTitle())
                        .append("; ");
            }
        }
    }

}
