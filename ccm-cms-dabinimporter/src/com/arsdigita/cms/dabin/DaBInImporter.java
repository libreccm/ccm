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
package com.arsdigita.cms.dabin;

import com.arsdigita.cms.ContentBundle;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.Folder;
import com.arsdigita.cms.contentassets.RelatedLink;
import com.arsdigita.cms.contenttypes.Address;
import com.arsdigita.cms.contenttypes.Contact;
import com.arsdigita.cms.contenttypes.GenericContactEntry;
import com.arsdigita.cms.contenttypes.Link;
import com.arsdigita.cms.contenttypes.SciAuthor;
import com.arsdigita.cms.contenttypes.SciDepartment;
import com.arsdigita.cms.contenttypes.SciMember;
import com.arsdigita.cms.contenttypes.SciOrganization;
import com.arsdigita.cms.contenttypes.SciProject;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.london.util.Transaction;
import com.arsdigita.packaging.Program;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import org.apache.commons.cli.CommandLine;
import org.apache.log4j.Logger;

/**
 *
 * @author Jens Pelzetter
 */
public class DaBInImporter extends Program {

    private static final Logger logger = Logger.getLogger(DaBInImporter.class);
    private Properties config;
    private ContentSection section;
    private Connection connection = null;
    private Folder root;
    private Folder authors;
    private Map<String, Folder> authorsAlpha;
    private Folder contacts;
    private Folder departments;
    private Folder members;
    private Map<String, Folder> membersAlpha;
    private Folder organization;
    private Folder projects;
    private Folder publications;
    private Map<String, SciAuthor> authorsMap;
    private Map<String, ContentBundle> departmentsMap;
    private Map<String, SciMember> membersMap;
    private Map<String, ContentBundle> projectsMap;
    private Map<String, ContentBundle> publicationMap;
    private Map<String, ContentBundle> workingPaperMap;
    private SciOrganization orgaDe;
    private SciOrganization orgaEn;
    private ContentBundle orga;
    private Address postalAddress;
    private Address officeAddress;

    public DaBInImporter() {
        this(true);
        /*super("DaBInImporter",
        "0.1.0",
        "MySQLHost MySQLUser MySQLPassword MySQLDB OrgaTitle OrgaName contentsection");
        authorsAlpha = new HashMap<String, Folder>(12);
        membersAlpha = new HashMap<String, Folder>(12);
        authorsMap = new HashMap<String, SciAuthor>();
        departmentsMap = new HashMap<String, SciDepartment>();
        membersMap = new HashMap<String, SciMember>();
        projectsMap = new HashMap<String, SciProject>();
        publicationMap = new HashMap<String, Publication>();*/
    }

    public DaBInImporter(boolean startup) {
        /*super("DaBInImporter",
        "0.1.0",
        "MySQLHost MySQLUser MySQLPassword MySQLDB OrgaTitle OrgaName contentSection",
        startup);*/
        super("DaBInImporter",
              "0.1.0",
              "configFile",
              startup);
        authorsAlpha = new HashMap<String, Folder>(12);
        membersAlpha = new HashMap<String, Folder>(12);
        authorsMap = new HashMap<String, SciAuthor>();
        departmentsMap = new HashMap<String, ContentBundle>();
        membersMap = new HashMap<String, SciMember>();
        projectsMap = new HashMap<String, ContentBundle>();
        publicationMap = new HashMap<String, ContentBundle>();
        workingPaperMap = new HashMap<String, ContentBundle>();
    }

    @Override
    protected void doRun(CommandLine cmdLine) {
        final String args[];
        String mySqlHost;
        String mySqlUser;
        String mySqlPassword;
        String mySqlDb;
        String orgaTitle;
        String orgaName;

        System.out.println("");
        System.out.println("");
        System.out.println("DaBInImporter starting...");

        //Get command line arguments...
        args = cmdLine.getArgs();

        /*if (args.length != 7) {
        logger.error("Invalid number of arguments.");
        //System.err.println();
        help(System.err);
        System.exit(-1);
        }

        mySqlHost = args[0];
        mySqlUser = args[1];
        mySqlPassword = args[2];
        mySqlDb = args[3];
        orgaTitle = args[4];
        orgaName = args[5];

        section = getContentSection(args[6]);*/

        if (args.length != 1) {
            System.out.println("Invalid number of arguments.");
            help(System.err);
            System.exit(-1);
        }

        config = new Properties();
        try {
            config.loadFromXML(new FileInputStream(args[0]));
        } catch (FileNotFoundException ex) {
            System.err.printf("Configuration file '%s' not found:\n", args[0]);
            ex.printStackTrace(System.err);
            System.exit(-1);
        } catch (IOException ex) {
            System.err.printf("Failed to read configuration file '%s' "
                              + "not found:\n",
                              args[0]);
            ex.printStackTrace(System.err);
            System.exit(-1);
        }

        mySqlHost = config.getProperty("mysql.host", "localhost");
        mySqlUser = config.getProperty("mysql.user");
        mySqlPassword = config.getProperty("mysql.password");
        mySqlDb = config.getProperty("mysql.db");

        section = getContentSection(config.getProperty("ccm.contentsection"));

        //Create connection to the DaBIn MySQL database
        System.out.println("Trying to connect to DaBIn MySQL database with these "
                           + "parameters:");
        System.out.printf("Host     = %s\n", mySqlHost);
        System.out.printf("User     = %s\n", mySqlUser);
        //logger.info(String.format("Password = %s", mySqlPassword));
        System.out.printf("Database = %s\n", mySqlDb);
        try {
            connection = DriverManager.getConnection(
                    String.format("jdbc:mysql://%s/%s", mySqlHost, mySqlDb),
                    mySqlUser,
                    mySqlPassword);
        } catch (SQLException ex) {
            System.err.println("Failed to connect to DaBIn MySQL database: ");
            ex.printStackTrace(System.err);
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException ex1) {
                System.err.println("Failed to close failed connection: ");
                ex1.printStackTrace(System.err);
            }
            System.exit(-1);
        }

        Folder folder;
        System.out.println(
                "\nCreating CCM folders (if they do not exist already)...");
        root = section.getRootFolder();

        authors = createFolder(root, "autoren", "Autoren");
        folder = createFolder(authors, "ab", "A - B");
        authorsAlpha.put("ab", folder);
        folder = createFolder(authors, "cd", "C - D");
        authorsAlpha.put("cd", folder);
        folder = createFolder(authors, "ef", "E - F");
        authorsAlpha.put("ef", folder);
        folder = createFolder(authors, "gh", "G - H");
        authorsAlpha.put("gh", folder);
        folder = createFolder(authors, "ij", "I - J");
        authorsAlpha.put("ij", folder);
        folder = createFolder(authors, "kl", "K - L");
        authorsAlpha.put("kl", folder);
        folder = createFolder(authors, "mn", "M - N");
        authorsAlpha.put("mn", folder);
        folder = createFolder(authors, "op", "P - P");
        authorsAlpha.put("op", folder);
        folder = createFolder(authors, "qr", "Q - R");
        authorsAlpha.put("qr", folder);
        folder = createFolder(authors, "st", "S - T");
        authorsAlpha.put("st", folder);
        folder = createFolder(authors, "uv", "U - V");
        authorsAlpha.put("uv", folder);
        folder = createFolder(authors, "wxzy", "W - Z");
        authorsAlpha.put("wxyz", folder);

        contacts = createFolder(root, "kontaktdaten", "Kontaktdaten");

        departments = createFolder(root, "abteilungen", "Abteilungen");

        members = createFolder(root, "mitglieder", "Mitglieder");
        folder = createFolder(members, "ab", "A - B");
        membersAlpha.put("ab", folder);
        folder = createFolder(members, "cd", "C - D");
        membersAlpha.put("cd", folder);
        folder = createFolder(members, "ef", "E - F");
        membersAlpha.put("ef", folder);
        folder = createFolder(members, "gh", "G - H");
        membersAlpha.put("gh", folder);
        folder = createFolder(members, "ij", "I - J");
        membersAlpha.put("ij", folder);
        folder = createFolder(members, "kl", "K - L");
        membersAlpha.put("kl", folder);
        folder = createFolder(members, "mn", "M - N");
        membersAlpha.put("mn", folder);
        folder = createFolder(members, "op", "P - P");
        membersAlpha.put("op", folder);
        folder = createFolder(members, "qr", "Q - R");
        membersAlpha.put("qr", folder);
        folder = createFolder(members, "st", "S - T");
        membersAlpha.put("st", folder);
        folder = createFolder(members, "uv", "U - V");
        membersAlpha.put("uv", folder);
        folder = createFolder(members, "wxzy", "W - Z");
        membersAlpha.put("wxyz", folder);

        organization = createFolder(root, "organisationen", "Organisation(en)");

        projects = createFolder(root, "projekte", "Projekte");

        publications = createFolder(root, "publikationen", "Publications");

        System.out.print("Creating organization item and "
                         + "postal and office address items...");
        Transaction transaction = new Transaction() {

            public void doRun() {
                orgaDe = new SciOrganization();
                orgaDe.setName(config.getProperty("orga.name.de"));
                orgaDe.setTitle(config.getProperty("orga.title.de"));
                orgaDe.setContentSection(section);
                orgaDe.setLanguage("de");
                orgaDe.save();
                orgaDe.setContentSection(section);

                orgaEn = new SciOrganization();
                orgaEn.setName(config.getProperty("orga.name.en"));
                orgaEn.setTitle(config.getProperty("orga.title.en"));
                orgaEn.setContentSection(section);
                orgaEn.setLanguage("en");
                orgaEn.save();
                orgaEn.setContentSection(section);

                ContentBundle orga = new ContentBundle(orgaDe);
                orga.addInstance(orgaEn);
                organization.addItem(orga);

                if (config.getProperty("orga.address.postal.name") != null) {
                    postalAddress = new Address();
                    postalAddress.setName(config.getProperty(
                            "orga.address.postal.name"));
                    postalAddress.setTitle(config.getProperty(
                            "orga.address.postal.title"));
                    postalAddress.setAddress(config.getProperty(
                            "orga.address.postal.data").
                            trim().
                            replace("\t", "").
                            replaceAll("  +", " ").
                            replace("\n ", "\n"));
                    postalAddress.setPostalCode(config.getProperty(
                            "orga.address.postal.code"));
                    postalAddress.setCity(config.getProperty(
                            "orga.address.postal.city"));
                    postalAddress.setState(config.getProperty(
                            "orga.address.postal.state"));
                    postalAddress.setIsoCountryCode(config.getProperty(
                            "orga.address.postal.country"));
                    postalAddress.setContentSection(section);
                    postalAddress.setLanguage("de");
                    postalAddress.save();

                    ContentBundle bundle = new ContentBundle(postalAddress);
                    organization.addItem(bundle);

                    Contact contact = new Contact();
                    contact.setName(config.getProperty(
                            "orga.address.postal.name"));
                    contact.setTitle(config.getProperty(
                            "orga.address.postal.title"));
                    contact.setAddress(postalAddress);
                    contact.setLanguage("de");
                    contact.save();
                    bundle = new ContentBundle(contact);
                    organization.addItem(bundle);

                    orgaDe.addContact(contact, "postalAddress");
                    orgaDe.save();
                    orgaEn.addContact(contact, "postalAddress");
                    orgaEn.save();
                }

                if (config.getProperty("orga.address.office.name") != null) {
                    officeAddress = new Address();
                    officeAddress.setName(config.getProperty(
                            "orga.address.office.name"));
                    officeAddress.setTitle(config.getProperty(
                            "orga.address.office.title"));
                    officeAddress.setAddress(config.getProperty(
                            "orga.address.office.data").
                            trim().
                            replace("\t", "").
                            replaceAll("  +", " ").
                            replace("\n ", "\n"));
                    //.replace("\n ", "\n"));
                    officeAddress.setPostalCode(config.getProperty(
                            "orga.address.office.code"));
                    officeAddress.setCity(config.getProperty(
                            "orga.address.office.city"));
                    officeAddress.setState(config.getProperty(
                            "orga.address.office.state"));
                    officeAddress.setIsoCountryCode(config.getProperty(
                            "orga.address.office.country"));
                    officeAddress.setContentSection(section);
                    officeAddress.setLanguage("de");
                    officeAddress.save();

                    ContentBundle bundle = new ContentBundle(officeAddress);
                    organization.addItem(bundle);

                    Contact contact = new Contact();
                    contact.setName(config.getProperty(
                            "orga.address.office.name"));
                    contact.setTitle(config.getProperty(
                            "orga.address.office.title"));
                    contact.setAddress(officeAddress);
                    contact.setLanguage("de");
                    contact.setContentSection(section);
                    contact.save();
                    bundle = new ContentBundle(contact);
                    organization.addItem(bundle);

                    orgaDe.addContact(contact, "officeAddress");
                    orgaDe.save();
                    orgaEn.addContact(contact, "officeAddress");
                    orgaEn.save();

                    orgaDe.setContentSection(section);
                    orgaEn.setContentSection(section);
                    orga.setContentSection(section);
                }
            }
        };
        transaction.run();
        System.out.println("OK");

        System.out.println("\nImporting members from DaBIn into CCM...");
        try {
            Statement stmt =
                      connection.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            ResultSet result;
            long counter = 1;
            long number;

            /*result =
            stmt.executeQuery(
            "SELECT person.Person_Id, Anrede, Vorname, Name, Angaben "
            + "FROM person "
            + "JOIN abteilunglink on person.Person_Id = abteilunglink.Person_Id "
            + "WHERE Eigenschaft  = 'Aktiv' AND Abteilung_Id <> 11 "
            + "GROUP BY person.Person_Id, Vorname, Name ORDER BY Name, Vorname");*/
            result =
            stmt.executeQuery(
                    "SELECT person.Person_Id, Anrede, Vorname, Name, Angaben "
                    + "FROM person "
                    + "JOIN abteilunglink ON person.Person_Id = abteilunglink.Person_Id "
                    + "WHERE Eigenschaft  = 'Aktiv' OR Eigenschaft = 'Ehemalig' "
                    + "GROUP BY person.Person_Id, Vorname, Name "
                    + "ORDER BY Name, Vorname");
            result.last();
            number = result.getRow();
            result.beforeFirst();

            while (result.next()) {
                System.out.printf("%4d of %4d:", counter, number);
                MemberData data = new MemberData();
                data.setDabinId(result.getString("person.Person_Id"));
                data.setTitlePre(result.getString("Anrede"));
                data.setGivenname(result.getString("Vorname"));
                data.setSurname(result.getString("Name"));
                data.setContactData(result.getString("Angaben"));
                createMember(data);
                counter++;
            }

        } catch (SQLException ex) {
            System.out.println("FAILED");
            ex.printStackTrace(System.err);
        } catch (Exception ex) {
            System.out.println("FAILED");
            ex.printStackTrace(System.err);
        }

        System.out.println("Adding associated members to organization...");
        try {
            Statement stmt = connection.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            ResultSet result;
            long counter = 1;
            long number;

            result =
            stmt.executeQuery(
                    "SELECT abteilunglink.Auftrag, person.Person_Id, person.Eigenschaft, abteilunglink.Auftrag "
                    + "FROM abteilunglink JOIN person ON abteilunglink.Person_Id = person.Person_Id "
                    + "WHERE abteilunglink.Abteilung_Id = 11 AND person.Eigenschaft = 'Aktiv'");
            result.last();
            number = result.getRow();
            result.beforeFirst();

            while (result.next()) {
                System.out.printf("\t%d of %d ", counter, number);
                if (membersMap.containsKey(result.getString("person.Person_Id"))) {
                    System.out.printf("%s...", membersMap.get(result.getString(
                            "person.Person_Id")).getTitle());
                    orgaDe.addPerson(membersMap.get(result.getString(
                            "person.Person_Id")),
                                     "member",
                                     "associated");
                    orgaEn.addPerson(membersMap.get(result.getString(
                            "person.Person_Id")),
                                     "member",
                                     "associated");
                    System.out.println("OK");
                } else {
                    System.out.printf("... No value of DaBIn person ID ' '\n",
                                      result.getString("person.PersonId"));
                }
            }
        } catch (SQLException ex) {
            System.out.println("FAILED");
            ex.printStackTrace(System.err);
        } catch (Exception ex) {
            System.out.println("FAILED");
            ex.printStackTrace(System.err);
        }
        System.out.println("FINSHED");


        System.out.println("Adding former associated members to organization...");
        try {
            Statement stmt = connection.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            ResultSet result;
            long counter = 1;
            long number;

            result =
            stmt.executeQuery(
                    "SELECT abteilunglink.Auftrag, person.Person_Id, person.Eigenschaft, abteilunglink.Auftrag "
                    + "FROM abteilunglink JOIN person ON abteilunglink.Person_Id = person.Person_Id "
                    + "WHERE abteilunglink.Abteilung_Id = 11 AND person.Eigenschaft = 'Ehemalig'");
            result.last();
            number = result.getRow();
            result.beforeFirst();

            while (result.next()) {
                System.out.printf("\t%d of %d ", counter, number);
                if (membersMap.containsKey(result.getString("person.Person_Id"))) {
                    System.out.printf("%s...", membersMap.get(result.getString(
                            "person.Person_Id")).getTitle());
                    orgaDe.addPerson(membersMap.get(result.getString(
                            "person.Person_Id")),
                                     "member",
                                     "associated");
                    orgaEn.addPerson(membersMap.get(result.getString(
                            "person.Person_Id")),
                                     "member",
                                     "associated");
                    System.out.println("OK");
                } else {
                    System.out.printf("... No value of DaBIn person ID ' '\n",
                                      result.getString("person.PersonId"));
                }
            }

        } catch (SQLException ex) {
            System.out.println("FAILED");
            ex.printStackTrace(System.err);
        } catch (Exception ex) {
            System.out.println("FAILED");
            ex.printStackTrace(System.err);
        }
        System.out.println("FINISHED");

        System.out.println("\nImporting departments from DaBIn into CCM...");
        try {
            Statement stmt = connection.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            ResultSet result;
            List<String> departmentIds = new ArrayList<String>();

            result = stmt.executeQuery("SELECT DISTINCT Abteilung_Id "
                                       + "FROM abteilung "
                                       + "WHERE Abteilung_Id <> 11 "
                                       + "ORDER BY Abteilung_Id");
            while (result.next()) {
                departmentIds.add(result.getString(1));
            }

            for (int i = 0; i < departmentIds.size(); i++) {
                DepartmentData data = new DepartmentData();

                System.out.printf("%2d of %2d:\n", i + 1, departmentIds.size());
                result = stmt.executeQuery(String.format(
                        "SELECT Name "
                        + "FROM abteilung "
                        + "WHERE Abteilung_Id = %s AND Sprache = 'DE'",
                        departmentIds.get(i)));
                if (result.next()) {
                    data.setNameDe(result.getString(1));
                }

                result = stmt.executeQuery(String.format(
                        "SELECT Name "
                        + "FROM abteilung "
                        + "WHERE Abteilung_Id = %s AND Sprache = 'EN'",
                        departmentIds.get(i)));
                if (result.next()) {
                    data.setNameEn(result.getString(1));
                }

                result = stmt.executeQuery(String.format(
                        "SELECT abteilunglink.Auftrag, person.Person_Id, person.Eigenschaft "
                        + "FROM abteilunglink JOIN person ON abteilunglink.Person_Id = person.Person_Id "
                        + "WHERE abteilunglink.Abteilung_Id = %s AND (person.Eigenschaft = 'Aktiv' OR person.Eigenschaft = 'Ehemalig')",
                        departmentIds.get(i)));

                while (result.next()) {
                    MembershipData membership;
                    membership = new MembershipData();

                    membership.setPersonDaBInId(result.getString(
                            "person.Person_Id"));
                    membership.setEigenschaft(result.getString(
                            "person.Eigenschaft"));
                    membership.setAuftrag(result.getString(
                            "abteilunglink.Auftrag"));

                    data.addMember(membership);
                }

                data.setDabinId(departmentIds.get(i));
                createDepartment(data);
            }
        } catch (SQLException ex) {
            System.out.println("FAILED");
            ex.printStackTrace(System.err);
        } catch (Exception ex) {
            System.out.println("FAILED");
            ex.printStackTrace(System.err);
        }

        System.out.println("Importing projects...");
        try {
            Statement stmt = connection.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            ResultSet result;
            List<String> projectsIds = new ArrayList<String>();

            result = stmt.executeQuery("SELECT DISTINCT Projekt_Id "
                                       + "FROM projekt "
                                       + "ORDER BY Projekt_Id");
            while (result.next()) {
                projectsIds.add(result.getString(1));
            }

            for (int i = 0; i < projectsIds.size(); i++) {
                ProjectData data = new ProjectData();

                System.out.printf("%3d of %3d:\n", i + 1, projectsIds.size());
                result = stmt.executeQuery(String.format(
                        "SELECT Name, Beschreibung, Finanzierung, Abteilung_Id "
                        + "FROM projekt "
                        + "WHERE Projekt_Id = %s AND Sprache = 'DE'",
                        projectsIds.get(i)));
                if (result.next()) {
                    data.setNameDe(result.getString("Name"));
                    data.setDescDe(result.getString("Beschreibung"));
                    data.setFundingDe(result.getString("Finanzierung"));
                    data.setDepartment(result.getString("Abteilung_Id"));
                }

                result = stmt.executeQuery(String.format(
                        "SELECT Name, Beschreibung, Finanzierung "
                        + "FROM projekt "
                        + "WHERE Projekt_Id = %s AND Sprache = 'EN'",
                        projectsIds.get(i)));
                if (result.next()) {
                    data.setNameEn(result.getString("Name"));
                    data.setDescEn(result.getString("Beschreibung"));
                    data.setFundingEn(result.getString("Finanzierung"));
                }

                result = stmt.executeQuery(String.format(
                        "SELECT Auftrag, Person_Id "
                        + "FROM projektlink "
                        + "WHERE Projekt_Id = %s",
                        projectsIds.get(i)));

                while (result.next()) {
                    MembershipData membership;
                    membership = new MembershipData();

                    membership.setPersonDaBInId(result.getString("Person_Id"));
                    membership.setAuftrag(result.getString("Auftrag"));

                    data.addMember(membership);
                }

                data.setDabinId(projectsIds.get(i));
                createProject(data);
            }

        } catch (SQLException ex) {
            System.out.println("FAILED");
            ex.printStackTrace(System.err);
        } catch (Exception ex) {
            System.out.println("FAILED");
            ex.printStackTrace(System.err);
        }

        System.out.println("Closing MySQL connection...");
        try {
            connection.close();
        } catch (SQLException ex) {
            System.err.println("Failed to close MySQL connection: ");
            ex.printStackTrace(System.err);
            System.exit(-1);
        }

        System.out.println("DaBIn importer finished. Check the output for "
                           + "error messages, then check the imported items.");
        System.exit(0);
    }

    protected void oldDoRun(CommandLine cmdLine) {
        final String args[];
        String mySqlHost;
        String mySqlUser;
        String mySqlPassword;
        String mySqlDb;
        String orgaTitle;
        String orgaName;
        ContentSection section;

        //Connection connection = null;

        System.out.println("");
        System.out.println("");
        System.out.println("DaBInImporter starting...");

        //Get command line arguments...
        args = cmdLine.getArgs();

        if (args.length != 7) {
            logger.error("Invalid number of arguments.");
            //System.err.println();
            help(System.err);
            System.exit(-1);
        }

        mySqlHost = args[0];
        mySqlUser = args[1];
        mySqlPassword = args[2];
        mySqlDb = args[3];
        orgaTitle = args[4];
        orgaName = args[5];

        section = getContentSection(args[6]);

        //Create connection to the DaBIn MySQL database
        System.out.println("Trying to connect to DaBIn MySQL database with these "
                           + "parameters:");
        System.out.printf("Host     = %s\n", mySqlHost);
        System.out.printf("User     = %s\n", mySqlUser);
        //logger.info(String.format("Password = %s", mySqlPassword));
        System.out.printf("Database = %s\n", mySqlDb);
        try {
            connection = DriverManager.getConnection(
                    String.format("jdbc:mysql://%s/%s", mySqlHost, mySqlDb),
                    mySqlUser,
                    mySqlPassword);
        } catch (SQLException ex) {
            System.err.println("Failed to connect to DaBIn MySQL database: ");
            ex.printStackTrace(System.err);
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException ex1) {
                System.err.println("Failed to close failed connection: ");
                ex1.printStackTrace(System.err);
            }
            System.exit(-1);
        }


        try {
            Statement stmt = connection.createStatement();
            long num = 1;

            ResultSet result =
                      stmt.executeQuery(
                    "SELECT Person_Id, Name, Vorname, Anrede, Eigenschaft "
                    + "FROM person "
                    + "WHERE Eigenschaft = 'Aktiv' OR Eigenschaft = 'Ehemalig"
                    + "ORDER BY Name, Vorname, Anrede");


            System.out.println("Creating members...");
            while (result.next()) {
                System.out.printf("%d: %s, %s, %s, %s, %s\n",
                                  num,
                                  result.getString("Person_Id"),
                                  result.getString("Name"),
                                  result.getString("Vorname"),
                                  result.getString("Anrede"),
                                  result.getString("Eigenschaft"));
                num++;
                MemberData memberData = new MemberData();
                memberData.setDabinId(result.getString("Person_Id"));
                memberData.setTitlePre(result.getString("Anrede"));
                memberData.setSurname(result.getString("Name"));
                memberData.setGivenname(result.getString("Vorname"));
                //createMember(memberData, section);
            }
        } catch (SQLException ex) {
            System.err.println("Failed to query 'person' table: ");
            ex.printStackTrace(System.err);
        } catch (Exception ex) {
            System.err.println("Failed to create member: ");
            ex.printStackTrace(System.err);
        }

        System.out.println("Closing MySQL connection...");
        try {
            connection.close();
        } catch (SQLException ex) {
            System.err.println("Failed to close MySQL connection: ");
            ex.printStackTrace(System.err);
            System.exit(-1);
        }

        System.out.println("DaBIn importer finished successfully.");
        System.exit(0);
    }

    private void createMember(final MemberData memberData) {

        StringBuilder memberTitleBuilder = new StringBuilder();
        if ((memberData.getTitlePre() != null)
            && (memberData.getTitlePre().length() > 0)) {
            memberTitleBuilder.append(memberData.getTitlePre());
            memberTitleBuilder.append(' ');
        }
        memberTitleBuilder.append(memberData.getGivenname());
        memberTitleBuilder.append(' ');
        memberTitleBuilder.append(memberData.getSurname());
        System.out.printf(" Creating new member '%s'...",
                          memberTitleBuilder.toString());

        final Folder folder;
        char letter;

        letter = memberData.getSurname().toLowerCase().charAt(0);

        switch (letter) {
            case 'a':
                folder = membersAlpha.get("ab");
                break;
            case 'b':
                folder = membersAlpha.get("ab");
                break;
            case 'c':
                folder = membersAlpha.get("cd");
                break;
            case 'd':
                folder = membersAlpha.get("cd");
                break;
            case 'e':
                folder = membersAlpha.get("ef");
                break;
            case 'f':
                folder = membersAlpha.get("ef");
                break;
            case 'g':
                folder = membersAlpha.get("gh");
                break;
            case 'h':
                folder = membersAlpha.get("gh");
                break;
            case 'i':
                folder = membersAlpha.get("ij");
                break;
            case 'j':
                folder = membersAlpha.get("ij");
                break;
            case 'k':
                folder = membersAlpha.get("kl");
                break;
            case 'l':
                folder = membersAlpha.get("kl");
                break;
            case 'm':
                folder = membersAlpha.get("mn");
                break;
            case 'n':
                folder = membersAlpha.get("mn");
                break;
            case 'o':
                folder = membersAlpha.get("op");
                break;
            case 'p':
                folder = membersAlpha.get("op");
                break;
            case 'q':
                folder = membersAlpha.get("qr");
                break;
            case 'r':
                folder = membersAlpha.get("qr");
                break;
            case 's':
                folder = membersAlpha.get("st");
                break;
            case 't':
                folder = membersAlpha.get("st");
                break;
            case 'u':
                folder = membersAlpha.get("uv");
                break;
            case 'v':
                folder = membersAlpha.get("uv");
                break;
            case 'w':
                folder = membersAlpha.get("wxyz");
                break;
            case 'x':
                folder = membersAlpha.get("wxyz");
                break;
            case 'y':
                folder = membersAlpha.get("wxyz");
                break;
            case 'z':
                folder = membersAlpha.get("wxyz");
                break;
            case 'ä':
                folder = membersAlpha.get("ab");
                break;
            case 'ö':
                folder = membersAlpha.get("op");
                break;
            case 'ü':
                folder = membersAlpha.get("uv");
                break;
            default:
                folder = members;
                break;
        }

        Transaction transaction = new Transaction() {

            @Override
            public void doRun() {
                SciMember member;

                member = new SciMember();
                member.setSurname(memberData.getSurname());
                member.setGivenName(memberData.getGivenname());
                member.setTitlePre(memberData.getTitlePre());

                member.save();
                member.setLanguage("de");

                ContentBundle bundle;
                bundle = new ContentBundle(member);

                folder.addItem(bundle);

                StringTokenizer contactData = new StringTokenizer(
                        memberData.getContactData(),
                        "\n");
                Contact contact = new Contact();
                //System.out.printf("\nmember.name = %s\n", member.getName());
                //System.out.printf("\nmember.title = %s\n", member.getTitle());
                contact.setLanguage("de");
                contact.setName(String.format("kontakt-%s", member.getName()));
                contact.setTitle(String.format("Kontakt %s",
                                               member.getTitle()));
                contact.setPerson(member, "commonContact");
                String homepage = null;
                while (contactData.hasMoreTokens()) {
                    String token;
                    String key;
                    String value;
                    token = contactData.nextToken();

                    if (token.indexOf("=") < 0) {
                        System.err.printf("Warning: Invalid contact entry: '%s'"
                                          + "Skiping.", token);
                        continue;
                    }
                    key = token.substring(0, token.indexOf('=')).trim();
                    value = token.substring(token.indexOf('=') + 1).trim();

                    if ("Raum_1".equals(key)) {
                        contact.addContactEntry(
                                new GenericContactEntry(contact,
                                                        "office",
                                                        value,
                                                        ""));
                    } else if ("Tel_1".equals(key)
                                   && value.startsWith("Fax: ")) {
                        contact.addContactEntry(new GenericContactEntry(
                                contact,
                                "fax",
                                value.substring(6),
                                ""));
                    } else if ("Tel_1".equals(key)) {
                        contact.addContactEntry(
                                new GenericContactEntry(contact,
                                                        "phoneOffice",
                                                        value,
                                                        ""));
                    } else if ("eMail_1".equals(key)) {
                        contact.addContactEntry(
                                new GenericContactEntry(contact,
                                                        "email",
                                                        value,
                                                        ""));
                    } else if ("WWW_1".equals(key)) {
                        contact.addContactEntry(
                                new GenericContactEntry(contact,
                                                        "homepage",
                                                        value,
                                                        ""));
                        homepage = value;
                    }

                    contact.setContentSection(section);
                    contact.save();
                    ContentBundle contactBundle = new ContentBundle(contact);
                    contacts.addItem(contactBundle);
                    member.save();

                    if (homepage != null) {
                        RelatedLink homepageLink;
                        homepageLink = new RelatedLink();
                        homepageLink.setTitle("Persönliche Homepage");
                        homepageLink.setTargetType(Link.EXTERNAL_LINK);
                        homepageLink.setTargetURI(homepage);
                        homepageLink.setLinkListName("");
                        homepageLink.setLinkOwner(member);
                        homepageLink.save();
                    }
                }


                membersMap.put(memberData.getDabinId(), member);
            }
        };
        transaction.run();

        System.out.println("OK");
    }

    public void createDepartment(final DepartmentData departmentData) {
        Transaction transaction = new Transaction() {

            @Override
            protected void doRun() {
                SciDepartment departmentDe;
                SciDepartment departmentEn;
                ContentBundle department;

                System.out.printf("\tde: %s...", departmentData.getNameDe());
                departmentDe = new SciDepartment();
                departmentDe.setTitle(departmentData.getNameDe());
                departmentDe.setName(departmentData.getNameDe().
                        replace(",", "").
                        replace("/", "").
                        replaceAll("\\s\\s+", " ").
                        replace(' ', '-').toLowerCase());
                departmentDe.setLanguage("de");
                departmentDe.setContentSection(section);
                departmentDe.save();
                System.out.println("OK");

                System.out.printf("\ten: %s...",
                                  departmentData.getNameEn());
                departmentEn = new SciDepartment();
                departmentEn.setTitle(departmentData.getNameEn());
                departmentEn.setName(departmentData.getNameEn().
                        replace(",", "").
                        replace("/", "").
                        replaceAll("\\s\\s+", " ").
                        replace(' ', '-').toLowerCase());
                departmentEn.setLanguage("en");
                departmentEn.setContentSection(section);
                departmentEn.save();
                System.out.println("OK");

                department = new ContentBundle(departmentDe);
                department.addInstance(departmentEn);
                department.setContentSection(section);
                department.setDefaultLanguage("de");
                department.setContentSection(section);
                department.save();
                departments.addItem(department);
                departmentsMap.put(departmentData.getDabinId(), department);
                //departmentDe.save();
                //departmentEn.save();
                //department.save();

                System.out.println("Assigning members...");
                int i = 1;
                for (MembershipData membership : departmentData.getMembers()) {
                    System.out.printf("\t\t%d of %d...", i, departmentData.
                            getMembers().size());
                    SciMember member = membersMap.get(membership.
                            getPersonDaBInId());
                    String status;
                    String role;

                    if (member == null) {
                        System.out.printf(
                                "No member for DaBIn person ID '%s' found. Skiping.\n", membership.
                                getPersonDaBInId());
                        continue;
                    }

                    if ("Aktiv".equals(membership.getEigenschaft())) {
                        status = "active";
                    } else if ("Ehemalig".equals(
                            membership.getEigenschaft())) {
                        status = "former";
                    } else {
                        status = "UNKNOWN";
                    }

                    if ("Direktor".equals(membership.getAuftrag())) {
                        role = "head";
                    } else if ("Mitarbeiter".equals(membership.getAuftrag())) {
                        role = "member";
                    } else if ("Sekretariat".equals(membership.getAuftrag())) {
                        role = "office";
                    } else if ("Sprecher".equals(membership.getAuftrag())) {
                        role = "speaker";
                    } else {
                        role = "member";
                    }

                    departmentDe.addPerson(member, role, status);
                    departmentEn.addPerson(member, role, status);
                    System.out.println("OK");
                    i++;
                }

                System.out.println("OK");

                orgaDe.addDepartment(departmentDe);
                orgaEn.addDepartment(departmentEn);
                departmentsMap.put(departmentData.getDabinId(), department);
            }
        };

        transaction.run();

        System.out.println("FINISHED");

    }

    public void createProject(final ProjectData projectData) {
        Transaction transaction = new Transaction() {

            @Override
            protected void doRun() {
                SciProject projectDe = null;
                SciProject projectEn = null;
                ContentBundle project;

                System.out.printf("\tde: %s...", projectData.getNameDe());
                if ((projectData.getNameDe() != null)
                    && (projectData.getNameDe().length() > 0)) {
                    projectDe = new SciProject();
                    projectDe.setTitle(projectData.getNameDe());
                    String projectNameDe = projectData.getNameDe().
                            replace(",", "").
                            replace("/", "").
                            replaceAll("\\s\\s+", " ").
                            replace(' ', '-').toLowerCase();
                    if (projectNameDe.length() > 200) {
                        projectNameDe = projectNameDe.substring(0, 200);
                    }
                    projectDe.setName(projectNameDe);
                    projectDe.setProjectDescription(projectData.getDescDe());
                    projectDe.setFunding(projectData.getFundingDe());
                    projectDe.setLanguage("de");
                    projectDe.setContentSection(section);
                    projectDe.save();
                    System.out.println("OK");
                } else {
                    System.out.println("No english version. Skiping.");
                }

                System.out.printf("\ten: %s...", projectData.getNameEn());
                if ((projectData.getNameEn() != null)
                    && (projectData.getNameEn().length() > 0)) {
                    projectEn = new SciProject();
                    projectEn.setTitle(projectData.getNameEn());
                    String projectNameEn = projectData.getNameEn().
                            replace(",", "").
                            replace("/", "").
                            replaceAll("\\s\\s+", " ").
                            replace(' ', '-').toLowerCase();
                    if (projectNameEn.length() > 200) {
                        projectNameEn = projectNameEn.substring(0, 200);
                    }
                    projectEn.setName(projectNameEn);
                    projectEn.setProjectDescription(projectData.getDescEn());
                    projectEn.setFunding(projectData.getFundingEn());
                    projectEn.setLanguage("en");
                    projectEn.setContentSection(section);
                    projectEn.save();
                    System.out.println("OK");
                } else {
                    System.out.println("No english version. Skiping.");
                }

                if (projectDe == null) {
                    project = new ContentBundle(projectEn);
                } else {
                    project = new ContentBundle(projectDe);
                    if (projectEn != null) {
                        project.addInstance(projectEn);
                    }
                }
                project.setContentSection(section);
                project.save();
                projects.addItem(project);
                projectsMap.put(projectData.getDabinId(), project);

                System.out.print("Assigning project to department... ");
                ContentBundle department = departmentsMap.get(projectData.
                        getDepartment());
                if (department == null) {
                    System.out.printf(
                            "No department found for DaBIn id '%s'. Assinging project to organization...");
                    orgaDe.addProject(projectDe);
                    if (projectEn != null) {
                        orgaEn.addProject(projectEn);
                    }
                } else {
                    SciDepartment departmentDe = (SciDepartment) department.
                            getInstance("de");
                    SciDepartment departmentEn = (SciDepartment) department.
                            getInstance("en");

                    departmentDe.addProject(projectDe);
                    if (projectEn != null) {
                        departmentEn.addProject(projectEn);
                    }
                }
                System.out.println("OK");

                System.out.println("Assigning members...");
                int i = 1;
                for (MembershipData membership : projectData.getMembers()) {
                    System.out.printf("\t\t%d of %d...", i, projectData.
                            getMembers().size());
                    SciMember member = membersMap.get(membership.
                            getPersonDaBInId());
                    String role;

                    if (member == null) {
                        System.out.printf(
                                "No member for DaBIn person ID '%s' found. Skiping.\n",
                                membership.getPersonDaBInId());
                        continue;
                    }

                    if ("Projektleitung".equals(membership.getAuftrag())) {
                        role = "head";
                    } else {
                        role = "member";
                    }

                    projectDe.addPerson(member, role, "active");
                    if (projectEn != null) {
                        projectEn.addPerson(member, role, "active");
                    }
                    System.out.println("OK");
                    i++;
                }

                System.out.println("OK");
            }
        };

        transaction.run();

        System.out.println("FINISHED");
    }

    /**
     * Get the content section for the entered path, adding "/" prefix and suffix if necessary.
     * (taken from london.importer)
     *
     * @param rawPath the raw path of the content section, e.g. "content".
     *
     * @return the content section
     */
    private ContentSection getContentSection(String rawPath) {
        final StringBuilder path = new StringBuilder();

        if (!rawPath.startsWith("/")) {
            path.append("/");
        }
        path.append(rawPath);

        if (!rawPath.endsWith("/")) {
            path.append("/");
        }

        final ContentSection _section = (ContentSection) ContentSection.
                retrieveApplicationForPath(path.toString());

        if (_section == null) {
            throw new DataObjectNotFoundException("Content section not found with path "
                                                  + path);
        }
        return _section;
    }

    private Folder createFolder(final Folder parent,
                                final String name,
                                final String label) {
        Folder folder;
        System.out.printf("Creating folder '%s/%s'...",
                          parent.getName(),
                          name);

        folder = (Folder) parent.getItem(name, true);

        if (folder == null) {
            Transaction transaction = new Transaction() {

                @Override
                protected void doRun() {
                    Folder newFolder;
                    newFolder = new Folder();
                    newFolder.setName(name);
                    newFolder.setLabel(label);
                    newFolder.setParent(parent);
                }
            };

            transaction.run();

            folder = (Folder) parent.getItem(name, true);




        }

        System.out.println("OK");

        return folder;
    }

    public static final void main(String[] args) {
        new DaBInImporter().run(args);
    }
}
