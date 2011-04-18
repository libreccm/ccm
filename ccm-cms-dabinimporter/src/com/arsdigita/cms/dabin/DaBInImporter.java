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
import com.arsdigita.cms.FileAsset;
import com.arsdigita.cms.Folder;
import com.arsdigita.cms.ItemCollection;
import com.arsdigita.cms.contentassets.RelatedLink;
import com.arsdigita.cms.contenttypes.Address;
import com.arsdigita.cms.contenttypes.ArticleInCollectedVolume;
import com.arsdigita.cms.contenttypes.ArticleInJournal;
import com.arsdigita.cms.contenttypes.CollectedVolume;
import com.arsdigita.cms.contenttypes.Contact;
import com.arsdigita.cms.contenttypes.FileStorageItem;
import com.arsdigita.cms.contenttypes.GenericContactEntry;
import com.arsdigita.cms.contenttypes.GenericPerson;
import com.arsdigita.cms.contenttypes.GreyLiterature;
import com.arsdigita.cms.contenttypes.Link;
import com.arsdigita.cms.contenttypes.Monograph;
import com.arsdigita.cms.contenttypes.Person;
import com.arsdigita.cms.contenttypes.Publication;
import com.arsdigita.cms.contenttypes.Publisher;
import com.arsdigita.cms.contenttypes.SciAuthor;
import com.arsdigita.cms.contenttypes.SciDepartment;
import com.arsdigita.cms.contenttypes.SciMember;
import com.arsdigita.cms.contenttypes.SciOrganization;
import com.arsdigita.cms.contenttypes.SciProject;
import com.arsdigita.cms.contenttypes.WorkingPaper;
import com.arsdigita.cms.lifecycle.Lifecycle;
import com.arsdigita.cms.lifecycle.LifecycleDefinition;
import com.arsdigita.cms.lifecycle.LifecycleDefinitionCollection;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.london.terms.Domain;
import com.arsdigita.london.terms.Term;
import com.arsdigita.london.util.Transaction;
import com.arsdigita.packaging.Program;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;

/**
 * This CLI program for CCM is used to import data from the DaBIn application
 * used by the ZeS and other institutes into CCM. The applications works 
 * directly on the MySQL database of DaBIn, and creates new objects for CCM
 * with this informations. Some aspects of the importer can be controlled by
 * the configurations file passed as command line argument. For a explanations
 * of the available options please read the comments in the example
 * configuration file.
 *
 * @author Jens Pelzetter
 */
public class DaBInImporter extends Program {

    /*Many variables to store objects, especially objects which are needed
     * for associations.
     */
    private static final Logger logger = Logger.getLogger(DaBInImporter.class);
    private Properties config;
    private String timestamp = null;
    private ContentSection section;
    private ContentSection personsSection;
    private ContentSection projectsSection;
    private ContentSection publicationsSection;
    private LifecycleDefinition lifecycle;
    private LifecycleDefinition personsLifecycle;
    private LifecycleDefinition projectsLifecycle;
    private LifecycleDefinition publicationsLifecycle;
    private Connection connection = null;
    private Folder root;
    private Folder personsRootFolder;
    private Folder membersRootFolder;
    private Folder projectsRootFolder;
    private Folder publicationsRootFolder;
    private Folder authors;
    private Folder contacts;
    private Folder departments;
    private Folder members;
    private Folder organization;
    private Folder persons;
    private Folder projects;
    private Folder publications;
    private Folder publishers;
    private Folder files;
    private Map<String, ContentBundle> departmentsMap;
    private Map<String, ContentBundle> personsMap;
    private Map<String, ContentBundle> projectsMap;
    private Map<PublisherData, ContentBundle> publishersMap;
    private Map<String, ContentBundle> publicationMap;
    private Map<String, ContentBundle> workingPaperMap;
    private SciOrganization orgaDe;
    private SciOrganization orgaEn;
    private ContentBundle orga;
    private ContentBundle postalAddress;
    private Address postalAddressDe;
    private Address postalAddressEn;
    private ContentBundle officeAddress;
    private Address officeAddressDe;
    private Address officeAddressEn;
    private Domain termsDomain;
    private Term publicationsTerm;
    private Term workingPapersTerm;
    private Term currentProjectsTerm;
    private Term finishedProjectsTerm;

    public DaBInImporter() {
        this(true);
    }

    public DaBInImporter(boolean startup) {
        super("DaBInImporter",
              "0.1.0",
              "configFile",
              startup);
        departmentsMap = new HashMap<String, ContentBundle>();
        personsMap = new HashMap<String, ContentBundle>();
        projectsMap = new HashMap<String, ContentBundle>();
        publishersMap = new HashMap<PublisherData, ContentBundle>();
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

        System.out.println("");
        System.out.println("");
        System.out.println("DaBInImporter is starting...");

        //Get command line arguments...
        args = cmdLine.getArgs();

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

        timestamp = config.getProperty("data.timestamp");

        section = getContentSection(config.getProperty("ccm.contentsection"));
        personsSection = getContentSection(config.getProperty(
                "ccm.personsContentSection"));

        projectsSection = getContentSection(config.getProperty(
                "ccm.projectsContentSection"));
        publicationsSection = getContentSection(config.getProperty(
                "ccm.publicationsContentSection"));
        LifecycleDefinitionCollection lifecycles =
                                      section.getLifecycleDefinitions();
        while (lifecycles.next()) {
            lifecycle = lifecycles.getLifecycleDefinition();
        }
        lifecycles = personsSection.getLifecycleDefinitions();
        while (lifecycles.next()) {
            personsLifecycle = lifecycles.getLifecycleDefinition();
        }
        lifecycles = projectsSection.getLifecycleDefinitions();
        while (lifecycles.next()) {
            projectsLifecycle = lifecycles.getLifecycleDefinition();
        }
        lifecycles = publicationsSection.getLifecycleDefinitions();
        while (lifecycles.next()) {
            publicationsLifecycle = lifecycles.getLifecycleDefinition();
        }

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

        //Create folders for storing the created objects, if they exist already.
        Folder folder;
        System.out.println(
                "\nCreating CCM folders (if they do not exist already)...");
        root = section.getRootFolder();
        personsRootFolder = personsSection.getRootFolder();
        membersRootFolder = section.getRootFolder();
        projectsRootFolder = projectsSection.getRootFolder();
        publicationsRootFolder = publicationsSection.getRootFolder();

        authors = createFolder(personsRootFolder, "externe", "Externe");

        contacts = createFolder(personsRootFolder, "kontaktdaten",
                                "Kontaktdaten");

        departments = createFolder(root, "abteilungen", "Abteilungen");

        members = createFolder(membersRootFolder, "mitglieder", "Mitglieder");

        organization = createFolder(root, "organisationen", "Organisation(en)");

        //Personen sollen beim ZeS in den gleichen Ordner wie Autoren.
        //persons = createFolder(personsRootFolder, "persons", "persons");
        persons = authors;

        projects = createFolder(projectsRootFolder, "projekte", "Projekte");

        publishers = createFolder(publicationsRootFolder, "verlage", "Verlage");

        publications = createFolder(publicationsRootFolder, "publikationen",
                                    "Publikationen");

        files = createFolder(publicationsRootFolder, "dateien", "Dateien");

        /*
         * Create the catgories/terms for publications and projects.
         */
        System.out.println(
                "\nRetrieving terms/categories and creating them if necsseary...");
        try {
            termsDomain = Domain.retrieve(
                    (String) config.get("terms.domain.key"));

            System.out.println("Terms for publications...");
            String publicationsTermPath = (String) config.get(
                    "terms.publications");
            publicationsTerm = checkTermPath(publicationsTermPath);

            System.out.println("Terms for working papers...");
            String workingPapersTermPath =
                   (String) config.get("terms.workingpapers");
            workingPapersTerm = checkTermPath(workingPapersTermPath);

            System.out.println("Term for current projects...");
            String currentProjectsTermPath = (String) config.get(
                    "terms.projects.current");
            currentProjectsTerm = checkTermPath(currentProjectsTermPath);

            System.out.println("Term for finished projects...");
            String finishedProjectsTermPath = (String) config.get(
                    "terms.projects.finished");
            finishedProjectsTerm = checkTermPath(finishedProjectsTermPath);
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
        }

        /*
         * Create the item for the organization. This item can be configured in
         * the configuration file, since this informations does not exist in the
         * DaBIn configuration file.
         */
        System.out.print("Creating organization item and "
                         + "postal and office address items...");
        Transaction transaction = new Transaction() {

            public void doRun() {
                orgaDe = new SciOrganization();
                orgaDe.setName(config.getProperty("orga.name.de"));
                orgaDe.setTitle(config.getProperty("orga.title.de"));
                orgaDe.setContentSection(section);
                orgaDe.setLanguage("de");
                orgaDe.setLifecycle(createLifecycle(lifecycle));
                orgaDe.setContentSection(section);
                orgaDe.save();

                orgaEn = new SciOrganization();
                orgaEn.setName(config.getProperty("orga.name.en"));
                orgaEn.setTitle(config.getProperty("orga.title.en"));
                orgaEn.setContentSection(section);
                orgaEn.setLanguage("en");
                orgaEn.setLifecycle(createLifecycle(lifecycle));
                orgaEn.setContentSection(section);
                orgaEn.save();

                ContentBundle orga = new ContentBundle(orgaDe);
                orga.addInstance(orgaEn);
                orga.setLifecycle(createLifecycle(lifecycle));
                orga.setContentSection(section);
                organization.addItem(orga);

                orgaDe.setContentSection(section);
                orgaEn.setContentSection(section);

                if (config.getProperty("orga.address.postal.name") != null) {
                    postalAddressDe = new Address();
                    postalAddressDe.setName(config.getProperty(
                            "orga.address.postal.name"));
                    postalAddressDe.setTitle(config.getProperty(
                            "orga.address.postal.title"));
                    postalAddressDe.setAddress(config.getProperty(
                            "orga.address.postal.data").
                            trim().
                            replace("\t", "").
                            replaceAll("  +", " ").
                            replace("\n ", "\n"));
                    postalAddressDe.setPostalCode(config.getProperty(
                            "orga.address.postal.code"));
                    postalAddressDe.setCity(config.getProperty(
                            "orga.address.postal.city"));
                    postalAddressDe.setState(config.getProperty(
                            "orga.address.postal.state"));
                    postalAddressDe.setIsoCountryCode(config.getProperty(
                            "orga.address.postal.country"));
                    postalAddressDe.setLanguage("de");
                    postalAddressDe.setLifecycle(createLifecycle(lifecycle));
                    postalAddressDe.setContentSection(section);
                    postalAddressDe.save();

                    postalAddressEn = new Address();
                    postalAddressEn.setName(config.getProperty(
                            "orga.address.postal.name"));
                    postalAddressEn.setTitle(config.getProperty(
                            "orga.address.postal.title"));
                    postalAddressEn.setAddress(config.getProperty(
                            "orga.address.postal.data").
                            trim().
                            replace("\t", "").
                            replaceAll("  +", " ").
                            replace("\n ", "\n"));
                    postalAddressEn.setPostalCode(config.getProperty(
                            "orga.address.postal.code"));
                    postalAddressEn.setCity(config.getProperty(
                            "orga.address.postal.city"));
                    postalAddressEn.setState(config.getProperty(
                            "orga.address.postal.state"));
                    postalAddressEn.setIsoCountryCode(config.getProperty(
                            "orga.address.postal.country"));
                    postalAddressEn.setLanguage("en");
                    postalAddressEn.setLifecycle(createLifecycle(lifecycle));
                    postalAddressEn.setContentSection(section);
                    postalAddressEn.save();

                    postalAddress = new ContentBundle(postalAddressDe);
                    postalAddress.addInstance(postalAddressEn);
                    postalAddress.setLifecycle(createLifecycle(lifecycle));
                    postalAddress.setContentSection(section);
                    organization.addItem(postalAddress);

                    postalAddressDe.setContentSection(section);
                    postalAddressEn.setContentSection(section);

                    Contact contactDe = new Contact();
                    contactDe.setName(config.getProperty(
                            "orga.address.postal.name"));
                    contactDe.setTitle(config.getProperty(
                            "orga.address.postal.title"));
                    contactDe.setAddress((Address) postalAddress.
                            getPrimaryInstance());
                    contactDe.setLanguage("de");
                    contactDe.setLifecycle(createLifecycle(lifecycle));
                    contactDe.setContentSection(section);
                    contactDe.save();

                    Contact contactEn = new Contact();
                    contactEn.setName(config.getProperty(
                            "orga.address.postal.name"));
                    contactEn.setTitle(config.getProperty(
                            "orga.address.postal.title"));
                    contactEn.setAddress((Address) postalAddress.
                            getPrimaryInstance());
                    contactEn.setLanguage("en");
                    contactEn.setLifecycle(createLifecycle(lifecycle));
                    contactEn.setContentSection(section);
                    contactEn.save();

                    ContentBundle contact = new ContentBundle(contactDe);
                    contact.addInstance(contactEn);
                    contact.setLifecycle(createLifecycle(lifecycle));
                    contact.setContentSection(section);
                    organization.addItem(contact);

                    contactDe.setContentSection(section);
                    contactEn.setContentSection(section);

                    orgaDe.addContact((Contact) contact.getPrimaryInstance(),
                                      "postalAddress");
                    orgaDe.save();
                    orgaEn.addContact((Contact) contact.getPrimaryInstance(),
                                      "postalAddress");
                    orgaEn.save();
                }

                if (config.getProperty("orga.address.office.name") != null) {
                    officeAddressDe = new Address();
                    officeAddressDe.setName(config.getProperty(
                            "orga.address.office.name"));
                    officeAddressDe.setTitle(config.getProperty(
                            "orga.address.office.title"));
                    officeAddressDe.setAddress(config.getProperty(
                            "orga.address.office.data").
                            trim().
                            replace("\t", "").
                            replaceAll("  +", " ").
                            replace("\n ", "\n"));
                    //.replace("\n ", "\n"));
                    officeAddressDe.setPostalCode(config.getProperty(
                            "orga.address.office.code"));
                    officeAddressDe.setCity(config.getProperty(
                            "orga.address.office.city"));
                    officeAddressDe.setState(config.getProperty(
                            "orga.address.office.state"));
                    officeAddressDe.setIsoCountryCode(config.getProperty(
                            "orga.address.office.country"));
                    officeAddressDe.setLanguage("de");
                    officeAddressDe.setLifecycle(createLifecycle(lifecycle));
                    officeAddressDe.setContentSection(section);
                    officeAddressDe.save();

                    officeAddressEn = new Address();
                    officeAddressEn.setName(config.getProperty(
                            "orga.address.office.name"));
                    officeAddressEn.setTitle(config.getProperty(
                            "orga.address.office.title"));
                    officeAddressEn.setAddress(config.getProperty(
                            "orga.address.office.data").
                            trim().
                            replace("\t", "").
                            replaceAll("  +", " ").
                            replace("\n ", "\n"));
                    //.replace("\n ", "\n"));
                    officeAddressEn.setPostalCode(config.getProperty(
                            "orga.address.office.code"));
                    officeAddressEn.setCity(config.getProperty(
                            "orga.address.office.city"));
                    officeAddressEn.setState(config.getProperty(
                            "orga.address.office.state"));
                    officeAddressEn.setIsoCountryCode(config.getProperty(
                            "orga.address.office.country"));
                    officeAddressEn.setLanguage("en");
                    officeAddressEn.setLifecycle(createLifecycle(lifecycle));
                    officeAddressEn.setContentSection(section);
                    officeAddressEn.save();

                    officeAddress = new ContentBundle(officeAddressDe);
                    officeAddress.setLifecycle(createLifecycle(lifecycle));
                    officeAddress.setContentSection(section);
                    officeAddress.addInstance(officeAddressEn);
                    organization.addItem(officeAddress);

                    officeAddressDe.setContentSection(section);
                    officeAddressEn.setContentSection(section);

                    Contact contactDe = new Contact();
                    contactDe.setName(config.getProperty(
                            "orga.address.office.name"));
                    contactDe.setTitle(config.getProperty(
                            "orga.address.office.title"));
                    contactDe.setAddress((Address) officeAddress.
                            getPrimaryInstance());
                    contactDe.setLanguage("de");
                    contactDe.setLifecycle(createLifecycle(lifecycle));
                    contactDe.setContentSection(section);
                    contactDe.save();

                    Contact contactEn = new Contact();
                    contactEn.setName(config.getProperty(
                            "orga.address.office.name"));
                    contactEn.setTitle(config.getProperty(
                            "orga.address.office.title"));
                    contactEn.setAddress((Address) officeAddress.
                            getPrimaryInstance());
                    contactEn.setLanguage("en");
                    contactEn.setLifecycle(createLifecycle(lifecycle));
                    contactEn.setContentSection(section);
                    contactEn.save();

                    ContentBundle contact = new ContentBundle(contactDe);
                    contact.addInstance(contactEn);
                    contact.setLifecycle(createLifecycle(lifecycle));
                    contact.setContentSection(section);
                    organization.addItem(contact);

                    contactDe.setContentSection(section);
                    contactEn.setContentSection(section);

                    orgaDe.addContact((Contact) contact.getPrimaryInstance(),
                                      "officeAddress");
                    orgaDe.save();
                    orgaEn.addContact((Contact) contact.getPrimaryInstance(),
                                      "officeAddress");
                    orgaEn.save();
                    // orga.setContentSection(section);

                }
            }
        };
        transaction.run();
        System.out.println("OK");

        // Import the persons.
        System.out.println(
                "\nImporting persons (members) from DaBIn into CCM...");
        try {
            Statement stmt =
                      connection.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            ResultSet result;
            long counter = 1;
            long number;

            if (timestamp == null) {
                result =
                stmt.executeQuery(
                        "SELECT person.Person_Id, Anrede, Vorname, Name, Angaben "
                        + "FROM person "
                        + "WHERE Eigenschaft  = 'Aktiv' OR Eigenschaft = 'Ehemalig' "
                        + "ORDER BY Name, Vorname");
            } else {
                result = stmt.executeQuery(String.format(
                        "SELECT person.Person_Id, Anrede, Vorname, Name, Angaben "
                        + "FROM person "
                        + "WHERE (Eigenschaft  = 'Aktiv' OR Eigenschaft = 'Ehemalig') AND Timestamp > '%s' "
                        + "ORDER BY Name, Vorname", timestamp));
            }
            result.last();
            number = result.getRow();
            result.beforeFirst();

            while (result.next()) {
                System.out.printf("%4d of %4d:", counter, number);
                PersonData data = new PersonData();
                data.setDabinId(result.getString("person.Person_Id"));
                data.setTitlePre(result.getString("Anrede"));
                data.setGivenname(result.getString("Vorname"));
                data.setSurname(result.getString("Name"));
                data.setContactData(result.getString("Angaben"));
                createPerson(data, PersonType.MEMBER);
                counter++;
            }

        } catch (SQLException ex) {
            System.out.println("FAILED");
            ex.printStackTrace(System.err);
        } catch (Exception ex) {
            System.out.println("FAILED");
            ex.printStackTrace(System.err);
        }

        System.out.printf(
                "Importing persons (cooperatives) from DaBIn into CCM...\n");
        try {
            Statement stmt = connection.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            ResultSet result;
            long counter = 1;
            long number;

            if (timestamp == null) {
                result = stmt.executeQuery(
                        "SELECT DISTINCT person.Person_Id, Anrede, Vorname, Name, Angaben "
                        + "FROM person JOIN projektlink on person.Person_Id = projektlink.Person_Id "
                        + "WHERE Eigenschaft = 'Autor' OR Eigenschaft = 'Sonstiges' "
                        + "ORDER BY Name, Vorname");
            } else {
                result = stmt.executeQuery(String.format(
                        "SELECT DISTINCT person.Person_Id, Anrede, Vorname, Name, Angaben "
                        + "FROM person JOIN projektlink on person.Person_Id = projektlink.Person_Id "
                        + "WHERE (Eigenschaft = 'Autor' OR Eigenschaft = 'Sonstiges') AND Timestamp > '%s' "
                        + "ORDER BY Name, Vorname", timestamp));

            }
            result.last();
            number = result.getRow();
            result.beforeFirst();

            while (result.next()) {
                System.out.printf("%4d of %4d:", counter, number);
                PersonData data = new PersonData();
                data.setDabinId(result.getString("person.Person_Id"));
                data.setTitlePre(result.getString("Anrede"));
                data.setGivenname(result.getString("Vorname"));
                data.setSurname(result.getString("Name"));
                data.setContactData(result.getString("Angaben"));
                createPerson(data, PersonType.OTHER);
                counter++;
            }
        } catch (SQLException ex) {
            System.out.println("FAILED");
            ex.printStackTrace(System.err);
        } catch (Exception ex) {
            System.out.println("FAILED");
            ex.printStackTrace(System.err);
        }
        System.out.println("FINSHED");

        System.out.printf("Importing persons (authors) from DaBIn into CCM...\n");
        try {
            Statement stmt = connection.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            ResultSet result;
            long counter = 1;
            long number;

            if (timestamp == null) {
                result = stmt.executeQuery(
                        "SELECT DISTINCT person.Person_Id, Anrede, Vorname, Name, Angaben "
                        + "FROM person "
                        + "WHERE Eigenschaft  = 'Autor' AND NOT EXISTS (SELECT * FROM projektlink where projektlink.Person_Id = person.Person_Id)"
                        + "ORDER BY Name, Vorname");
            } else {
                result = stmt.executeQuery(String.format(
                        "SELECT DISTINCT person.Person_Id, Anrede, Vorname, Name, Angaben "
                        + "FROM person "
                        + "WHERE Eigenschaft  = 'Autor' AND NOT EXISTS (SELECT * FROM projektlink where projektlink.Person_Id = person.Person_Id) AND Timestamp > '%s' "
                        + "ORDER BY Name, Vorname", timestamp));
            }
            result.last();
            number = result.getRow();
            result.beforeFirst();

            while (result.next()) {
                System.out.printf("%4d of %4d:", counter, number);
                PersonData data = new PersonData();
                data.setDabinId(result.getString("person.Person_Id"));
                data.setTitlePre(result.getString("Anrede"));
                data.setGivenname(result.getString("Vorname"));
                data.setSurname(result.getString("Name"));
                data.setContactData(result.getString("Angaben"));
                createPerson(data, PersonType.AUTHOR);
                counter++;
            }

        } catch (SQLException ex) {
            System.out.println("FAILED");
            ex.printStackTrace(System.err);
        } catch (Exception ex) {
            System.out.println("FAILED");
            ex.printStackTrace(System.err);
        }

        System.out.println("Adding active associated members to organization...");
        try {
            Statement stmt = connection.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            ResultSet result;
            long counter = 1;
            long number;

            if (timestamp == null) {
                result =
                stmt.executeQuery(
                        "SELECT DISTINCT abteilunglink.Auftrag, person.Person_Id, person.Eigenschaft, abteilunglink.Auftrag "
                        + "FROM abteilunglink JOIN person ON abteilunglink.Person_Id = person.Person_Id "
                        + "WHERE abteilunglink.Abteilung_Id = 11 AND person.Eigenschaft = 'Aktiv'");
            } else {
                result =
                stmt.executeQuery(String.format(
                        "SELECT DISTINCT abteilunglink.Auftrag, person.Person_Id, person.Eigenschaft, abteilunglink.Auftrag "
                        + "FROM abteilunglink JOIN person ON abteilunglink.Person_Id = person.Person_Id "
                        + "WHERE abteilunglink.Abteilung_Id = 11 AND person.Eigenschaft = 'Aktiv' AND Timestamp > '%s'",
                        timestamp));
            }
            result.last();
            number = result.getRow();
            result.beforeFirst();

            while (result.next()) {
                System.out.printf("\t%d of %d ", counter, number);
                if (personsMap.containsKey(result.getString("person.Person_Id"))) {
                    System.out.printf("%s...", ((GenericPerson) personsMap.get(result.
                                                getString(
                                                "person.Person_Id")).
                                                getPrimaryInstance()).getTitle());
                    orgaDe.addPerson((GenericPerson) personsMap.get(result.
                            getString(
                            "person.Person_Id")).getInstance("de"),
                                     "member",
                                     "associated");
                    orgaEn.addPerson((GenericPerson) personsMap.get(result.
                            getString(
                            "person.Person_Id")).getInstance("en"),
                                     "member",
                                     "associated");
                    System.out.println("OK");
                    counter++;
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

        System.out.println("Adding former associated members to organization...");
        try {
            Statement stmt = connection.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            ResultSet result;
            long counter = 1;
            long number;

            if (timestamp == null) {
                result =
                stmt.executeQuery(
                        "SELECT DISTINCT abteilunglink.Auftrag, person.Person_Id, person.Eigenschaft, abteilunglink.Auftrag "
                        + "FROM abteilunglink JOIN person ON abteilunglink.Person_Id = person.Person_Id "
                        + "WHERE abteilunglink.Abteilung_Id = 11 AND person.Eigenschaft = 'Ehemalig'");
            } else {
                result =
                stmt.executeQuery(String.format(
                        "SELECT DISTINCT abteilunglink.Auftrag, person.Person_Id, person.Eigenschaft, abteilunglink.Auftrag "
                        + "FROM abteilunglink JOIN person ON abteilunglink.Person_Id = person.Person_Id "
                        + "WHERE abteilunglink.Abteilung_Id = 11 AND person.Eigenschaft = 'Ehemalig' AND Timestamp > '%s'",
                        timestamp));
            }
            result.last();
            number = result.getRow();
            result.beforeFirst();

            while (result.next()) {
                System.out.printf("\t%d of %d ", counter, number);
                if (personsMap.containsKey(result.getString("person.Person_Id"))) {
                    System.out.printf("%s...", ((GenericPerson) personsMap.get(result.
                                                getString(
                                                "person.Person_Id")).
                                                getPrimaryInstance()).getTitle());
                    orgaDe.addPerson((GenericPerson) personsMap.get(result.
                            getString(
                            "person.Person_Id")).getInstance("de"),
                                     "member",
                                     "associatedFormer");
                    orgaEn.addPerson((GenericPerson) personsMap.get(result.
                            getString(
                            "person.Person_Id")).getInstance("en"),
                                     "member",
                                     "associatedFormer");
                    System.out.println("OK");
                } else {
                    System.out.printf("... No value of DaBIn person ID ' '\n",
                                      result.getString("person.PersonId"));
                }
                counter++;
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

                if (timestamp == null) {
                    result = stmt.executeQuery(String.format(
                            "SELECT abteilunglink.Auftrag, person.Person_Id, person.Eigenschaft "
                            + "FROM abteilunglink JOIN person ON abteilunglink.Person_Id = person.Person_Id "
                            + "WHERE abteilunglink.Abteilung_Id = %s AND (person.Eigenschaft = 'Aktiv' OR person.Eigenschaft = 'Ehemalig')",
                            departmentIds.get(i)));
                } else {
                    result = stmt.executeQuery(String.format(
                            "SELECT abteilunglink.Auftrag, person.Person_Id, person.Eigenschaft "
                            + "FROM abteilunglink JOIN person ON abteilunglink.Person_Id = person.Person_Id "
                            + "WHERE abteilunglink.Abteilung_Id = %s AND (person.Eigenschaft = 'Aktiv' OR person.Eigenschaft = 'Ehemalig') AND Timestamp > '%s'",
                            departmentIds.get(i), timestamp));
                }

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

            if (timestamp == null) {
                result = stmt.executeQuery("SELECT DISTINCT Projekt_Id "
                                           + "FROM projekt "
                                           + "ORDER BY Projekt_Id");
            } else {
                result = stmt.executeQuery(String.format("SELECT DISTINCT Projekt_Id "
                                                         + "FROM projekt "
                                                         + "WHERE Timestamp > '%s'"
                                                         + "ORDER BY Projekt_Id",
                                                         timestamp));
            }
            while (result.next()) {
                projectsIds.add(result.getString(1));
            }

            for (int i = 0; i < projectsIds.size(); i++) {
                ProjectData data = new ProjectData();

                System.out.printf("%3d of %3d:\n", i + 1, projectsIds.size());
                result = stmt.executeQuery(String.format(
                        "SELECT Name, Beschreibung, Finanzierung, Abteilung_Id, Beginn, Ende, Link "
                        + "FROM projekt "
                        + "WHERE Projekt_Id = %s AND Sprache = 'DE'",
                        projectsIds.get(i)));
                if (result.next()) {
                    data.setNameDe(result.getString("Name"));
                    data.setDescDe(result.getString("Beschreibung"));
                    data.setFundingDe(result.getString("Finanzierung"));
                    data.setDepartment(result.getString("Abteilung_Id"));
                    if (result.getInt("Beginn") != 0) {
                        data.setBegin(new GregorianCalendar(result.getInt(
                                "Beginn"), 0, 1, 0, 0));
                    }
                    if (result.getInt("Ende") != 0) {
                        data.setEnd(new GregorianCalendar(result.getInt("Ende"),
                                                          11, 31, 23, 59));
                    }
                    data.setLink(result.getString("Link"));
                }

                result = stmt.executeQuery(String.format(
                        "SELECT Name, Beschreibung, Finanzierung, Abteilung_Id, Beginn, Ende, Link "
                        + "FROM projekt "
                        + "WHERE Projekt_Id = %s AND Sprache = 'EN'",
                        projectsIds.get(i)));
                if (result.next()) {
                    data.setNameEn(result.getString("Name"));
                    data.setDescEn(result.getString("Beschreibung"));
                    data.setFundingEn(result.getString("Finanzierung"));
                    data.setDepartment(result.getString("Abteilung_Id"));
                    if (result.getInt("Beginn") != 0) {
                        data.setBegin(new GregorianCalendar(result.getInt(
                                "Beginn"), 0, 1, 0, 0));
                    }
                    if (result.getInt("Ende") != 0) {
                        data.setEnd(new GregorianCalendar(result.getInt("Ende"),
                                                          11, 31, 23, 59));
                    }
                    data.setLink(result.getString("Link"));
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

        System.out.println("\nImporting publications from DaBIn into CCM...");
        System.out.println("Publishers...");
        try {
            Statement stmt = connection.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            ResultSet result;
            long counter = 1;
            long number;

            if (timestamp == null) {
                result = stmt.executeQuery("SELECT Verlag FROM publikation "
                                           + "WHERE Typ = 'Monographie' OR Typ = 'Sammelband' OR Typ = 'Artikel / Aufsatz' "
                                           + "GROUP BY Verlag");
            } else {
                result = stmt.executeQuery(String.format("SELECT Verlag FROM publikation "
                                                         + "WHERE (Typ = 'Monographie' OR Typ = 'Sammelband' OR Typ = 'Artikel / Aufsatz') AND Timestamp > '%s' "
                                                         + "GROUP BY Verlag",
                                                         timestamp));
            }
            result.last();
            number = result.getRow();
            result.beforeFirst();

            while (result.next()) {
                System.out.printf("\t%d of %d... ", counter, number);
                if ((result.getString(1) == null)
                    || (result.getString(1).isEmpty())) {
                    System.out.println("Publisher field is empty. Skiping.");
                } else {
                    PublisherData publisherData = extractPublisher(result.
                            getString(
                            1));
                    createPublisher(publisherData);
                }
                counter++;
            }

            System.out.printf("Found %d unique publishers.\n", publishersMap.
                    size());

        } catch (SQLException ex) {
            System.out.println("FAILED");
            ex.printStackTrace(System.err);
        } catch (Exception ex) {
            System.out.println("FAILED");
            ex.printStackTrace(System.err);
        }

        System.out.println("Monographies...");
        try {
            Statement stmt = connection.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            Statement stmt2 = connection.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);

            ResultSet result;
            long counter = 1;
            long number;

            if (timestamp == null) {
                result = stmt.executeQuery(
                        "SELECT Publikation_Id, Name, Verlag, Jahr, Link, Beschreibung, Abteilung_Id, Sichtbarkeit, ErschienenIn "
                        + "FROM publikation "
                        + "WHERE Typ = 'Monographie' "
                        + "ORDER BY Name");
            } else {
                result = stmt.executeQuery(String.format(
                        "SELECT Publikation_Id, Name, Verlag, Jahr, Link, Beschreibung, Abteilung_Id, Sichtbarkeit, ErschienenIn "
                        + "FROM publikation "
                        + "WHERE Typ = 'Monographie' AND Timestamp > '%s' "
                        + "ORDER BY Name",
                        timestamp));
            }
            result.last();
            number = result.getRow();
            result.beforeFirst();

            while (result.next()) {
                System.out.printf("%4d of %4d: %s...\n", counter, number, result.
                        getString("Name"));
                PublicationData data = new PublicationData();
                data.setType(PublicationType.MONOGRAPH);
                data.setPublicationDaBInId(result.getString("Publikation_Id"));
                data.setName(result.getString("Name"));
                data.setVerlag(result.getString("Verlag"));
                data.setJahr(result.getString("Jahr"));
                data.setLink(result.getString("Link"));
                data.setBeschreibung(result.getString("Beschreibung"));
                data.setErschienenIn(result.getString("ErschienenIn"));
                data.setAbteilungId(result.getString("Abteilung_Id"));
                if ("Abteilung".equals(result.getString(
                        "Sichtbarkeit"))) {
                    data.setVisiblity(PublicationVisibility.DEPARTMENT);
                } else if ("Persönlich".equals(result.getString(
                        "Sichtbarkeit"))) {
                    data.setVisiblity(PublicationVisibility.PRIVATE);
                } else {
                    data.setVisiblity(PublicationVisibility.GLOBAL);
                }

                ResultSet authorResult = stmt2.executeQuery(String.format(
                        "SELECT Beteiligung, Person_Id "
                        + "FROM publikationlink "
                        + "WHERE Publikation_Id = %s "
                        + "ORDER BY Reihenfolge",
                        data.getPublicationDaBInId()));
                while (authorResult.next()) {
                    Authorship authorship;
                    authorship = new Authorship();

                    authorship.setPersonDaBInId(authorResult.getString(
                            "Person_Id"));
                    authorship.setBeteiligung(authorResult.getString(
                            "Beteiligung"));

                    data.addAuthor(authorship);
                }

                createPublication(data);

                counter++;
            }

        } catch (SQLException ex) {
            System.err.println("FAILED");
            ex.printStackTrace(System.err);
        } catch (Exception ex) {
            System.err.println("FAILED");
            ex.printStackTrace(System.err);
        }

        System.out.println("Collected volumes...");
        try {
            Statement stmt = connection.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            Statement stmt2 = connection.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);

            ResultSet result;
            long counter = 1;
            long number;

            if (timestamp == null) {
                result = stmt.executeQuery(
                        "SELECT Publikation_Id, Name, Verlag, Jahr, Link, Beschreibung, Abteilung_Id, Sichtbarkeit "
                        + "FROM publikation "
                        + "WHERE (Typ = 'Sammelband' AND (ErschienenIn IS NULL OR CHAR_LENGTH(ErschienenIn) = 0)) "
                        + "ORDER BY Name");
            } else {
                result = stmt.executeQuery(String.format(
                        "SELECT Publikation_Id, Name, Verlag, Jahr, Link, Beschreibung, Abteilung_Id, Sichtbarkeit "
                        + "FROM publikation "
                        + "WHERE (Typ = 'Sammelband' AND (ErschienenIn IS NULL OR CHAR_LENGTH(ErschienenIn) = 0)) AND Timestamp > '%s' "
                        + "ORDER BY Name", timestamp));
            }
            result.last();
            number = result.getRow();
            result.beforeFirst();

            while (result.next()) {
                System.out.printf("%4d of %4d: %s...\n", counter, number, result.
                        getString("Name"));
                PublicationData data = new PublicationData();
                data.setType(PublicationType.COLLECTED_VOLUME);
                data.setPublicationDaBInId(result.getString("Publikation_Id"));
                data.setName(result.getString("Name"));
                data.setVerlag(result.getString("Verlag"));
                data.setJahr(result.getString("Jahr"));
                data.setLink(result.getString("Link"));
                data.setBeschreibung(result.getString("Beschreibung"));
                data.setAbteilungId(result.getString("Abteilung_Id"));
                if ("Abteilung".equals(result.getString(
                        "Sichtbarkeit"))) {
                    data.setVisiblity(PublicationVisibility.DEPARTMENT);
                } else if ("Persönlich".equals(result.getString(
                        "Sichtbarkeit"))) {
                    data.setVisiblity(PublicationVisibility.PRIVATE);
                } else {
                    data.setVisiblity(PublicationVisibility.GLOBAL);
                }

                ResultSet authorResult = stmt2.executeQuery(String.format(
                        "SELECT Beteiligung, Person_Id "
                        + "FROM publikationlink "
                        + "WHERE Publikation_Id = %s "
                        + "ORDER BY Reihenfolge",
                        data.getPublicationDaBInId()));
                while (authorResult.next()) {
                    Authorship authorship;
                    authorship = new Authorship();

                    authorship.setPersonDaBInId(authorResult.getString(
                            "Person_Id"));
                    authorship.setBeteiligung(authorResult.getString(
                            "Beteiligung"));

                    data.addAuthor(authorship);
                }

                createPublication(data);

                counter++;
            }
        } catch (SQLException ex) {
            System.out.println("FAILED");
            ex.printStackTrace(System.err);
        } catch (Exception ex) {
            System.out.println("FAILED");
            ex.printStackTrace(System.err);
        }

        System.out.println("Articles in collected volumes...");
        try {
            Statement stmt = connection.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            Statement stmt2 = connection.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);

            ResultSet result;
            long counter = 1;
            long number;

            if (timestamp == null) {
                result = stmt.executeQuery(
                        "SELECT Publikation_Id, Name, Verlag, Jahr, Link, Beschreibung, Abteilung_Id, Sichtbarkeit, ErschienenIn "
                        + "FROM publikation "
                        + "WHERE (Typ = 'Sammelband' AND ErschienenIn IS NOT NULL AND CHAR_LENGTH(ErschienenIn) > 0) "
                        + "OR (Typ = 'Monograph' AND ErschienenIn IS NOT NULL AND CHAR_LENGTH(ErschienenIn) > 0) "
                        + "OR (Typ = 'Artikel / Aufsatz' AND SUBSTRING(ErschienenIn, 1, 2) = 'in') "
                        + "ORDER BY Name");
            } else {
                result = stmt.executeQuery(String.format(
                        "SELECT Publikation_Id, Name, Verlag, Jahr, Link, Beschreibung, Abteilung_Id, Sichtbarkeit, ErschienenIn "
                        + "FROM publikation "
                        + "WHERE ((Typ = 'Sammelband' AND ErschienenIn IS NOT NULL AND CHAR_LENGTH(ErschienenIn) > 0) "
                        + "OR (Typ = 'Monograph' AND ErschienenIn IS NOT NULL AND CHAR_LENGTH(ErschienenIn) > 0) "
                        + "OR (Typ = 'Artikel / Aufsatz' AND SUBSTRING(ErschienenIn, 1, 2) = 'in')) "
                        + "AND Timestamp > '%s' "
                        + "ORDER BY Name", timestamp));
            }
            result.last();
            number = result.getRow();
            result.beforeFirst();

            while (result.next()) {
                System.out.printf("%4d of %4d: %s...\n", counter, number, result.
                        getString("name"));
                PublicationData data = new PublicationData();
                //All publications of the DaBIn type "Artikel/Aufsatz" are now
                //imported as ArticleInJournal since ArticleInJournal and ArticleInCollectedVolume
                //can't be seperated from the information in DaBIn
                //data.setType(PublicationType.ARTICLE_IN_COLLECTED_VOLUME);
                data.setType(PublicationType.ARTICLE_IN_JOURNAL);
                data.setPublicationDaBInId(result.getString("Publikation_Id"));
                data.setName(result.getString("Name"));
                data.setVerlag(result.getString("Verlag"));
                data.setJahr(result.getString("Jahr"));
                data.setLink(result.getString("Link"));
                data.setBeschreibung(result.getString("Beschreibung"));
                data.setErschienenIn(result.getString("ErschienenIn"));
                data.setAbteilungId(result.getString("Abteilung_Id"));
                if ("Abteilung".equals(result.getString(
                        "Sichtbarkeit"))) {
                    data.setVisiblity(PublicationVisibility.DEPARTMENT);
                } else if ("Persönlich".equals(result.getString(
                        "Sichtbarkeit"))) {
                    data.setVisiblity(PublicationVisibility.PRIVATE);
                } else {
                    data.setVisiblity(PublicationVisibility.GLOBAL);
                }
                extractPages(result.getString("Verlag"), data);

                ResultSet authorResult = stmt2.executeQuery(String.format(
                        "SELECT Beteiligung, Person_Id "
                        + "FROM publikationlink "
                        + "WHERE Publikation_Id = %s "
                        + "ORDER BY Reihenfolge",
                        data.getPublicationDaBInId()));
                while (authorResult.next()) {
                    Authorship authorship;
                    authorship = new Authorship();

                    authorship.setPersonDaBInId(authorResult.getString(
                            "Person_Id"));
                    authorship.setBeteiligung(authorResult.getString(
                            "Beteiligung"));

                    data.addAuthor(authorship);
                }

                createPublication(data);

                counter++;
            }
        } catch (SQLException ex) {
            System.out.println("FAILED");
            ex.printStackTrace(System.err);
        } catch (Exception ex) {
            System.out.println("FAILED");
            ex.printStackTrace(System.err);
        }

        System.out.println("Articles in journals...");
        try {
            Statement stmt = connection.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            Statement stmt2 = connection.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);

            ResultSet result;
            long counter = 1;
            long number;

            if (timestamp == null) {
                result = stmt.executeQuery(
                        "SELECT Publikation_Id, Name, Verlag, Jahr, Link, Beschreibung, Abteilung_Id, Sichtbarkeit, ErschienenIn "
                        + "FROM publikation "
                        + "WHERE (Typ = 'Artikel / Aufsatz' AND SUBSTRING(ErschienenIn, 1, 2) <> 'in') "
                        + "ORDER BY Name");
            } else {
                result = stmt.executeQuery(String.format(
                        "SELECT Publikation_Id, Name, Verlag, Jahr, Link, Beschreibung, Abteilung_Id, Sichtbarkeit, ErschienenIn "
                        + "FROM publikation "
                        + "WHERE (Typ = 'Artikel / Aufsatz' AND SUBSTRING(ErschienenIn, 1, 2) <> 'in') AND Timestamp > '%s' "
                        + "ORDER BY Name", timestamp));
            }
            result.last();
            number = result.getRow();
            result.beforeFirst();

            while (result.next()) {
                System.out.printf("%4d of %4d: %s...\n", counter, number, result.
                        getString("name"));
                PublicationData data = new PublicationData();
                data.setType(PublicationType.ARTICLE_IN_JOURNAL);
                data.setPublicationDaBInId(result.getString("Publikation_Id"));
                data.setName(result.getString("Name"));
                data.setVerlag(result.getString("Verlag"));
                data.setJahr(result.getString("Jahr"));
                data.setLink(result.getString("Link"));
                data.setBeschreibung(result.getString("Beschreibung"));
                data.setErschienenIn(result.getString("ErschienenIn"));
                data.setAbteilungId(result.getString("Abteilung_Id"));
                if ("Abteilung".equals(result.getString(
                        "Sichtbarkeit"))) {
                    data.setVisiblity(PublicationVisibility.DEPARTMENT);
                } else if ("Persönlich".equals(result.getString(
                        "Sichtbarkeit"))) {
                    data.setVisiblity(PublicationVisibility.PRIVATE);
                } else {
                    data.setVisiblity(PublicationVisibility.GLOBAL);
                }
                extractPages(result.getString("Verlag"), data);

                ResultSet authorResult = stmt2.executeQuery(String.format(
                        "SELECT Beteiligung, Person_Id "
                        + "FROM publikationlink "
                        + "WHERE Publikation_Id = %s "
                        + "ORDER BY Reihenfolge",
                        data.getPublicationDaBInId()));
                while (authorResult.next()) {
                    Authorship authorship;
                    authorship = new Authorship();

                    authorship.setPersonDaBInId(authorResult.getString(
                            "Person_Id"));
                    authorship.setBeteiligung(authorResult.getString(
                            "Beteiligung"));

                    data.addAuthor(authorship);
                }

                createPublication(data);

                counter++;
            }
        } catch (SQLException ex) {
            System.out.println("FAILED");
            ex.printStackTrace(System.err);
        } catch (Exception ex) {
            System.out.println("FAILED");
            ex.printStackTrace(System.err);
        }

        System.out.println("Anything else (grey literature)...");
        try {
            Statement stmt = connection.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            Statement stmt2 = connection.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);


            ResultSet result;
            long counter = 1;
            long number;

            if (timestamp == null) {
                result = stmt.executeQuery(
                        "SELECT Publikation_Id, Name, Verlag, Jahr, Link, Beschreibung, Abteilung_Id, Sichtbarkeit, ErschienenIn "
                        + "FROM publikation "
                        + "WHERE Typ = 'Sonstiges' "
                        + "ORDER BY Name");
            } else {
                result = stmt.executeQuery(String.format(
                        "SELECT Publikation_Id, Name, Verlag, Jahr, Link, Beschreibung, Abteilung_Id, Sichtbarkeit, ErschienenIn "
                        + "FROM publikation "
                        + "WHERE Typ = 'Sonstiges' AND Timestamp > '%s' "
                        + "ORDER BY Name", timestamp));
            }
            result.last();
            number = result.getRow();
            result.beforeFirst();

            while (result.next()) {
                System.out.printf("%4d of %4d: %s...\n", counter, number, result.
                        getString("name"));
                PublicationData data = new PublicationData();
                data.setType(PublicationType.GREY_LITERATURE);
                data.setPublicationDaBInId(result.getString("Publikation_Id"));
                data.setName(result.getString("Name"));
                data.setVerlag(result.getString("Verlag"));
                data.setJahr(result.getString("Jahr"));
                data.setLink(result.getString("Link"));
                data.setBeschreibung(result.getString("Beschreibung"));
                data.setErschienenIn(result.getString("ErschienenIn"));
                data.setAbteilungId(result.getString("Abteilung_Id"));
                if ("Abteilung".equals(result.getString(
                        "Sichtbarkeit"))) {
                    data.setVisiblity(PublicationVisibility.DEPARTMENT);
                } else if ("Persönlich".equals(result.getString(
                        "Sichtbarkeit"))) {
                    data.setVisiblity(PublicationVisibility.PRIVATE);
                } else {
                    data.setVisiblity(PublicationVisibility.GLOBAL);
                }

                ResultSet authorResult = stmt2.executeQuery(String.format(
                        "SELECT Beteiligung, Person_Id "
                        + "FROM publikationlink "
                        + "WHERE Publikation_Id = %s "
                        + "ORDER BY Reihenfolge",
                        data.getPublicationDaBInId()));
                while (authorResult.next()) {
                    Authorship authorship;
                    authorship = new Authorship();

                    authorship.setPersonDaBInId(authorResult.getString(
                            "Person_Id"));
                    authorship.setBeteiligung(authorResult.getString(
                            "Beteiligung"));

                    data.addAuthor(authorship);
                }

                createPublication(data);

                counter++;
            }
        } catch (SQLException ex) {
            System.out.println("FAILED");
            ex.printStackTrace(System.err);
        } catch (Exception ex) {
            System.out.println("FAILED");
            ex.printStackTrace(System.err);
        }


        System.out.println("Working papers...");
        try {
            Statement stmt = connection.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);

            ResultSet result;
            List<String> workingPaperIds = new ArrayList<String>();

            if (timestamp == null) {
                result = stmt.executeQuery("SELECT DISTINCT Arbeitspapier_Id FROM arbeitspapier "
                                           + "ORDER BY Jahr, Name");
            } else {
                result = stmt.executeQuery(String.format("SELECT DISTINCT Arbeitspapier_Id FROM arbeitspapier "
                                                         + "WHERE Timestamp > '%s'"
                                                         + "ORDER BY Jahr, Name",
                                                         timestamp));
            }
            while (result.next()) {
                workingPaperIds.add(result.getString(1));
            }

            for (int i = 0; i < workingPaperIds.size(); i++) {
                WorkingPaperData data = new WorkingPaperData();

                result = stmt.executeQuery(String.format(
                        "SELECT Name, Jahr, Beschreibung, Abkürzung, Datei "
                        + "FROM arbeitspapier "
                        + "WHERE Arbeitspapier_Id = %s AND Sprache = 'DE'",
                        workingPaperIds.get(i)));
                if (result.next()) {
                    System.out.printf("%3d of %3d: %s...\n",
                                      i + 1,
                                      workingPaperIds.size(),
                                      result.getString("Name"));
                    data.setTitleDe(result.getString("Name"));
                    data.setDescDe(result.getString("Beschreibung"));
                    data.setYear(result.getString("Jahr"));
                    data.setNumber(result.getString("Abkürzung"));
                    if ((result.getBlob("Datei") != null)
                        && (result.getBlob("Datei").length() > 0)) {
                        data.setFile(result.getBlob("Datei").getBytes(
                                1,
                                (int) result.getBlob("Datei").length()));
                    }
                }

                result = stmt.executeQuery(String.format(
                        "SELECT Name, Jahr, Beschreibung, Abkürzung "
                        + "FROM arbeitspapier "
                        + "WHERE Arbeitspapier_Id = %s AND Sprache = 'EN'",
                        workingPaperIds.get(i)));
                if (result.next()) {
                    data.setTitleEn(result.getString("Name"));
                    data.setDescEn(result.getString("Beschreibung"));
                    data.setYear(result.getString("Jahr"));
                }

                result = stmt.executeQuery(String.format(
                        "SELECT Beteiligung, Person_Id "
                        + "FROM arbeitspapierlink "
                        + "WHERE Arbeitspapier_Id = %s "
                        + "ORDER BY Reihenfolge",
                        workingPaperIds.get(i)));
                while (result.next()) {
                    Authorship authorship;
                    authorship = new Authorship();

                    authorship.setPersonDaBInId(result.getString("Person_Id"));
                    authorship.setBeteiligung(result.getString("Beteiligung"));

                    data.addAuthor(authorship);
                }

                data.setDabinId(workingPaperIds.get(i));
                createWorkingPaper(data);
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

    private void createPerson(final PersonData personData,
                              final PersonType type) {
        StringBuilder personTitleBuilder = new StringBuilder();
        if ((personData.getTitlePre() != null)
            && (personData.getTitlePre().length() > 0)) {
            personTitleBuilder.append(personData.getTitlePre());
            personTitleBuilder.append(' ');
        }
        personTitleBuilder.append(personData.getGivenname());
        personTitleBuilder.append(' ');
        personTitleBuilder.append(personData.getSurname());
        System.out.printf(" Creating new person '%s'...",
                          personTitleBuilder.toString());

        Transaction transaction = new Transaction() {

            @Override
            public void doRun() {
                GenericPerson personDe = null;
                GenericPerson personEn = null;

                switch (type) {
                    case MEMBER:
                        personDe = new SciMember();
                        personEn = new SciMember();
                        break;
                    case AUTHOR:
                        personDe = new SciAuthor();
                        personEn = new SciAuthor();
                        break;
                    case OTHER:
                        personDe = new Person();
                        personEn = new Person();
                        break;
                }
                personDe.setSurname(personData.getSurname());
                personDe.setGivenName(personData.getGivenname());
                personDe.setTitlePre(personData.getTitlePre());
                personDe.setDescription(String.format("DaBInId={%s}",
                                                      personData.getDabinId()));
                if (type == PersonType.AUTHOR) {
                    personDe.setContentSection(publicationsSection);
                    personDe.setLifecycle(createLifecycle(publicationsLifecycle));
                } else {
                    personDe.setContentSection(personsSection);
                    personDe.setLifecycle(createLifecycle(personsLifecycle));
                }
                personDe.save();
                personDe.setLanguage("de");

                personEn.setSurname(personData.getSurname());
                personEn.setTitlePre(personData.getTitlePre());
                personEn.setGivenName(personData.getGivenname());
                personEn.setDescription(String.format("DaBInId={%s}",
                                                      personData.getDabinId()));
                if (type == PersonType.AUTHOR) {
                    personEn.setContentSection(publicationsSection);
                    personEn.setLifecycle(createLifecycle(publicationsLifecycle));
                } else {
                    personEn.setContentSection(personsSection);
                    personEn.setLifecycle(createLifecycle(personsLifecycle));
                }
                personEn.save();
                personEn.setLanguage("en");

                ContentBundle person;
                person = new ContentBundle(personDe);
                person.addInstance(personEn);
                person.setDefaultLanguage("de");
                person.setContentSection(section);
                person.setLifecycle(createLifecycle(personsLifecycle));
                person.save();

                personDe.setContentSection(section);
                personEn.setContentSection(section);

                Folder folder = null;
                switch (type) {
                    case MEMBER:
                        folder = members;
                        break;
                    case AUTHOR:
                        folder = authors;
                        break;
                    case OTHER:
                        folder = persons;
                        break;
                }

                folder.addItem(person);

                StringTokenizer contactData = new StringTokenizer(
                        personData.getContactData(),
                        "\n");
                Contact contactDe = new Contact();
                Contact contactEn = new Contact();
                //System.out.printf("\nmember.name = %s\n", member.getName());
                //System.out.printf("\nmember.title = %s\n", member.getTitle());
                contactDe.setLanguage("de");
                contactDe.setName(
                        String.format("kontakt-%s", personDe.getName()));
                contactDe.setTitle(String.format("Kontakt %s",
                                                 personDe.getTitle()));
                contactDe.setPerson(personDe, "commonContact");
                contactEn.setLanguage("en");
                contactEn.setName(
                        String.format("kontakt-%s", personEn.getName()));
                contactEn.setTitle(String.format("Kontakt %s",
                                                 personEn.getTitle()));
                contactEn.setPerson(personEn, "commonContact");
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
                        contactDe.addContactEntry(
                                new GenericContactEntry(contactDe,
                                                        "office",
                                                        value,
                                                        ""));
                        contactEn.addContactEntry(
                                new GenericContactEntry(contactEn,
                                                        "office",
                                                        value,
                                                        ""));
                    } else if ("Tel_1".equals(key)
                                   && value.startsWith("Fax: ")) {
                        contactDe.addContactEntry(new GenericContactEntry(
                                contactDe,
                                "fax",
                                value.substring(6),
                                ""));
                        contactEn.addContactEntry(new GenericContactEntry(
                                contactEn,
                                "fax",
                                value.substring(6),
                                ""));
                    } else if ("Tel_1".equals(key)) {
                        contactDe.addContactEntry(
                                new GenericContactEntry(contactDe,
                                                        "phoneOffice",
                                                        value,
                                                        ""));
                        contactEn.addContactEntry(
                                new GenericContactEntry(contactEn,
                                                        "phoneOffice",
                                                        value,
                                                        ""));
                    } else if ("eMail_1".equals(key)) {
                        contactDe.addContactEntry(
                                new GenericContactEntry(contactDe,
                                                        "email",
                                                        value,
                                                        ""));
                        contactEn.addContactEntry(
                                new GenericContactEntry(contactEn,
                                                        "email",
                                                        value,
                                                        ""));
                    } else if ("WWW_1".equals(key)) {
                        contactDe.addContactEntry(
                                new GenericContactEntry(contactDe,
                                                        "homepage",
                                                        value,
                                                        ""));
                        contactEn.addContactEntry(
                                new GenericContactEntry(contactEn,
                                                        "homepage",
                                                        value,
                                                        ""));
                        homepage = value;
                        homepage = value;
                    }

                    contactDe.setContentSection(personsSection);
                    contactDe.setLifecycle(createLifecycle(personsLifecycle));
                    contactDe.save();
                    contactEn.setContentSection(personsSection);
                    contactEn.setLifecycle(createLifecycle(personsLifecycle));
                    contactEn.save();
                    ContentBundle contactBundle = new ContentBundle(contactDe);
                    contactBundle.addInstance(contactEn);
                    contactBundle.setContentSection(personsSection);
                    contacts.addItem(contactBundle);

                    contactDe.setContentSection(personsSection);
                    contactEn.setContentSection(personsSection);

                    if (homepage != null) {
                        RelatedLink homepageLinkDe;
                        homepageLinkDe = new RelatedLink();
                        homepageLinkDe.setTitle("Persönliche Homepage");
                        homepageLinkDe.setTargetType(Link.EXTERNAL_LINK);
                        homepageLinkDe.setTargetURI(homepage);
                        homepageLinkDe.setLinkListName("");
                        homepageLinkDe.setLinkOwner(personDe);
                        homepageLinkDe.save();

                        RelatedLink homepageLinkEn;
                        homepageLinkEn = new RelatedLink();
                        homepageLinkEn.setTitle("Personal homepage");
                        homepageLinkEn.setTargetType(Link.EXTERNAL_LINK);
                        homepageLinkEn.setTargetURI(homepage);
                        homepageLinkEn.setLinkListName("");
                        homepageLinkEn.setLinkOwner(personDe);
                        homepageLinkEn.save();
                    }
                }

                personsMap.put(personData.getDabinId(), person);
            }
        };
        transaction.run();

        System.out.println("OK");
    }

    private void createDepartment(final DepartmentData departmentData) {
        Transaction transaction = new Transaction() {

            @Override
            protected void doRun() {
                SciDepartment departmentDe;
                SciDepartment departmentEn;
                ContentBundle department;

                System.out.printf("\tde: %s...", departmentData.getNameDe());
                departmentDe = new SciDepartment();
                departmentDe.setTitle(departmentData.getNameDe());
                departmentDe.setName(DaBInImporter.normalizeString(departmentData.
                        getNameDe()));
                departmentDe.setLanguage("de");
                departmentDe.setLifecycle(createLifecycle(lifecycle));
                departmentDe.setContentSection(section);
                departmentDe.save();
                System.out.println("OK");

                System.out.printf("\ten: %s...",
                                  departmentData.getNameEn());
                departmentEn = new SciDepartment();
                departmentEn.setTitle(departmentData.getNameEn());
                departmentEn.setName(DaBInImporter.normalizeString(departmentData.
                        getNameDe()));
                departmentEn.setLanguage("en");
                departmentEn.setLifecycle(createLifecycle(lifecycle));
                departmentEn.setContentSection(section);
                departmentEn.save();
                System.out.println("OK");

                department = new ContentBundle(departmentDe);
                department.addInstance(departmentEn);
                department.setContentSection(section);
                department.setDefaultLanguage("de");
                department.setLifecycle(createLifecycle(lifecycle));
                department.setContentSection(section);

                departmentDe.setContentSection(section);
                departmentEn.setContentSection(section);

                //department.save();
                departments.addItem(department);
                departmentsMap.put(departmentData.getDabinId(), department);
                //departmentDe.save();
                //departmentEn.save();
                //department.save();

                System.out.println("\tAssigning members...");
                int i = 1;
                for (MembershipData membership : departmentData.getMembers()) {
                    System.out.printf("\t\t%d of %d: (DaBIn member id: %s)...",
                                      i,
                                      departmentData.getMembers().size(),
                                      membership.getPersonDaBInId());
                    if (!personsMap.containsKey(
                            membership.getPersonDaBInId())) {
                        System.out.printf("No person for DaBIn id '%s'. "
                                          + "Skiping.\n",
                                          membership.getPersonDaBInId());
                        continue;
                    }
                    SciMember member = (SciMember) personsMap.get(membership.
                            getPersonDaBInId()).getPrimaryInstance();
                    String status;
                    String role;

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

                System.out.println("\tOK");

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
                    String projectName = DaBInImporter.normalizeString(projectData.
                            getNameDe());
                    if (projectName.length() > 200) {
                        projectName = projectName.substring(0, 200);
                    }
                    projectDe.setName(projectName);
                    projectDe.setProjectDescription(projectData.getDescDe());
                    projectDe.setFunding(projectData.getFundingDe());
                    if (projectData.getBegin() != null) {
                        projectDe.setBegin(projectData.getBegin().getTime());
                    }
                    if (projectData.getEnd() != null) {
                        projectDe.setEnd(projectData.getEnd().getTime());
                    }
                    if ((projectData.getLink() != null)
                        && !projectData.getLink().isEmpty()) {
                        RelatedLink link = new RelatedLink();
                        link.setTitle(config.getProperty(
                                "projects.furtherInfoLink.de",
                                projectData.getLink()));
                        link.setTargetType(Link.EXTERNAL_LINK);
                        link.setTargetURI(projectData.getLink());
                        link.setLinkOwner(projectDe);
                    }

                    projectDe.setLanguage("de");
                    projectDe.setLifecycle(createLifecycle(projectsLifecycle));
                    projectDe.setContentSection(projectsSection);
                    projectDe.save();
                    System.out.println("OK");
                } else {
                    System.out.println("No german version. Skiping.");
                }

                System.out.printf("\ten: %s...", projectData.getNameEn());
                if ((projectData.getNameEn() != null)
                    && (projectData.getNameEn().length() > 0)) {
                    projectEn = new SciProject();
                    projectEn.setTitle(projectData.getNameEn());
                    String projectName;
                    if (projectData.getNameDe() == null) {
                        projectName = DaBInImporter.normalizeString(projectData.
                                getNameEn());
                    } else {
                        projectName = DaBInImporter.normalizeString(projectData.
                                getNameDe());
                    }
                    if (projectName.length() > 200) {
                        projectName = projectName.substring(0, 200);
                    }
                    projectEn.setName(projectName);
                    projectEn.setProjectDescription(projectData.getDescEn());
                    projectEn.setFunding(projectData.getFundingEn());
                    if (projectData.getBegin() != null) {
                        projectEn.setBegin(projectData.getBegin().getTime());
                    }
                    if (projectData.getEnd() != null) {
                        projectEn.setEnd(projectData.getEnd().getTime());
                    }
                    if ((projectData.getLink() != null)
                        && !projectData.getLink().isEmpty()) {
                        RelatedLink link = new RelatedLink();
                        link.setTitle(config.getProperty(
                                "projects.furtherInfoLink.en",
                                projectData.getLink()));
                        link.setTargetType(Link.EXTERNAL_LINK);
                        link.setTargetURI(projectData.getLink());
                        link.setLinkOwner(projectEn);
                    }
                    projectEn.setLanguage("en");
                    projectEn.setLifecycle(createLifecycle(projectsLifecycle));
                    projectEn.setContentSection(projectsSection);
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
                project.setLifecycle(createLifecycle(projectsLifecycle));
                project.setContentSection(projectsSection);
                project.setDefaultLanguage("de");

                if (projectDe != null) {
                    projectDe.setContentSection(projectsSection);
                }

                if (projectEn != null) {
                    projectEn.setContentSection(projectsSection);
                }

                projectsMap.put(projectData.getDabinId(), project);

                System.out.print("\tAssigning project to department... ");
                ContentBundle department = departmentsMap.get(projectData.
                        getDepartment());
                if (department == null) {
                    System.out.printf("No department found for DaBIn id '%s'. "
                                      + "Assinging project to organization...",
                                      projectData.getDabinId());
                    if (orgaDe != null) {
                        orgaDe.addProject(projectDe);
                    }
                    if (projectEn != null) {
                        orgaEn.addProject(projectEn);
                    }
                } else {
                    SciDepartment departmentDe = (SciDepartment) department.
                            getInstance("de");
                    SciDepartment departmentEn = (SciDepartment) department.
                            getInstance("en");

                    if (projectDe != null) {
                        departmentDe.addProject(projectDe);
                    }
                    if (projectEn != null) {
                        departmentEn.addProject(projectEn);
                    }
                }
                System.out.println("OK");

                System.out.println("\tAssigning members...");
                int i = 1;
                for (MembershipData membership : projectData.getMembers()) {
                    System.out.printf("\t\t%d of %d (dabin member id: %s)...",
                                      i,
                                      projectData.getMembers().size(),
                                      membership.getPersonDaBInId());
                    if (!personsMap.containsKey(
                            membership.getPersonDaBInId())) {
                        System.out.printf("No person for DaBIn id '%s'. "
                                          + "Skiping.\n",
                                          membership.getPersonDaBInId());
                        continue;
                    }
                    GenericPerson member = (GenericPerson) personsMap.get(membership.
                            getPersonDaBInId()).getPrimaryInstance();
                    String role;

                    if ("Projektleitung".equals(membership.getAuftrag())) {
                        role = "head";
                    } else {
                        role = "member";
                    }

                    if (projectDe != null) {
                        projectDe.addPerson(member, role, "active");
                    }
                    if (projectEn != null) {
                        projectEn.addPerson(member, role, "active");
                    }
                    System.out.println("OK");
                    i++;
                }

                projects.addItem(project);

                //Assign to term/category
                Calendar today = new GregorianCalendar();
                SciProject sciProject =
                           (SciProject) project.getPrimaryInstance();
                if ((sciProject.getEnd() != null)
                    && today.getTime().after(sciProject.getEnd())) {
                    Term term;
                    term = termsDomain.getTerm(
                            finishedProjectsTerm.getUniqueID());
                    System.out.printf("\tAdding project to term '%s:%s'...\n",
                                      term.getUniqueID(),
                                      term.getName());
                    term.addObject(project);
                    term.save();
                    project.save();
                } else {
                    Term term;
                    term =
                    termsDomain.getTerm(currentProjectsTerm.getUniqueID());
                    System.out.printf("\tAdding project to term '%s:%s'...\n",
                                      term.getUniqueID(),
                                      term.getName());
                    term.addObject(project);
                    term.save();
                    project.save();
                }

                System.out.println("\tOK");
            }
        };

        transaction.run();

        System.out.println("FINISHED");
    }

    private void createPublication(final PublicationData publicationData) {
        Transaction transaction = new Transaction() {

            @Override
            protected void doRun() {
                Publication publicationDe = null;
                Publication publicationEn = null;
                ContentBundle publication;

                switch (publicationData.getType()) {
                    case MONOGRAPH: {
                        Monograph monographDe = null;
                        Monograph monographEn = null;
                        PublisherData publisherData;

                        monographDe = new Monograph();
                        monographDe.setTitle(publicationData.getName());
                        monographDe.setName(publicationData.getUrl());
                        extractYearOfPublication(publicationData, monographDe);
                        publisherData = extractPublisher(publicationData.
                                getVerlag());
                        if (publishersMap.containsKey(publisherData)) {
                            monographDe.setPublisher((Publisher) publishersMap.
                                    get(
                                    publisherData).getPrimaryInstance());
                        } else {
                            System.out.println(
                                    "***WARNING: Invalid publisher. Ignoring.");
                        }

                        if ((publicationData.getLink() != null)
                            && !publicationData.getLink().isEmpty()) {
                            RelatedLink link = new RelatedLink();
                            link.setTitle(config.getProperty(
                                    "publications.furtherInfoLink.de",
                                    publicationData.getLink()));
                            link.setTargetType(Link.EXTERNAL_LINK);
                            link.setTargetURI(publicationData.getLink());
                            link.setLinkOwner(monographDe);
                        }

                        if (publicationData.getBeschreibung() != null) {
                            monographDe.setAbstract(publicationData.
                                    getBeschreibung());
                        }

                        if ((publicationData.getErschienenIn() != null)
                            && !publicationData.getErschienenIn().isEmpty()) {
                            System.out.println(
                                    "***WARNING: DaBIn field 'ErschienenIn' contains a value. For publications of type monograph, this is not supported.");
                        }
                        monographDe.save();

                        monographEn = new Monograph();
                        monographEn.setTitle(publicationData.getName());
                        monographEn.setName(publicationData.getUrl());
                        extractYearOfPublication(publicationData, monographEn);
                        publisherData = extractPublisher(publicationData.
                                getVerlag());
                        if (publishersMap.containsKey(publisherData)) {
                            monographEn.setPublisher((Publisher) publishersMap.
                                    get(
                                    publisherData).getPrimaryInstance());
                        } else {
                            System.out.println(
                                    "***WARNING: Invalid publisher. Ignoring.");
                        }

                        if ((publicationData.getLink() != null)
                            && !publicationData.getLink().isEmpty()) {
                            RelatedLink link = new RelatedLink();
                            link.setTitle(config.getProperty(
                                    "publications.furtherInfoLink.en",
                                    publicationData.getLink()));
                            link.setTargetType(Link.EXTERNAL_LINK);
                            link.setTargetURI(publicationData.getLink());
                            link.setLinkOwner(monographEn);
                        }

                        if (publicationData.getBeschreibung() != null) {
                            monographEn.setAbstract(publicationData.
                                    getBeschreibung());
                        }

                        if ((publicationData.getErschienenIn() != null)
                            && !publicationData.getErschienenIn().isEmpty()) {
                            System.out.println(
                                    "***WARNING: DaBIn field 'ErschienenIn' contains a value. For publications of type monograph, this is not supported.");
                        }
                        monographEn.save();

                        publicationDe = monographDe;
                        publicationEn = monographEn;

                        break;
                    }
                    case COLLECTED_VOLUME: {
                        CollectedVolume collectedVolumeDe;
                        CollectedVolume collectedVolumeEn;
                        PublisherData publisherData;

                        collectedVolumeDe = new CollectedVolume();
                        collectedVolumeDe.setTitle(publicationData.getName());
                        collectedVolumeDe.setName(publicationData.getUrl());
                        extractYearOfPublication(publicationData,
                                                 collectedVolumeDe);
                        publisherData = extractPublisher(publicationData.
                                getVerlag());
                        if (publishersMap.containsKey(publisherData)) {
                            collectedVolumeDe.setPublisher((Publisher) publishersMap.
                                    get(publisherData).getPrimaryInstance());
                        } else {
                            System.out.println(
                                    "***WARNING: Invalid publisher. Ignoring.");
                        }

                        if ((publicationData.getLink() != null)
                            && !publicationData.getLink().isEmpty()) {
                            RelatedLink link = new RelatedLink();
                            link.setTitle(config.getProperty(
                                    "publications.furtherInfoLink.de",
                                    publicationData.getLink()));
                            link.setTargetType(Link.EXTERNAL_LINK);
                            link.setTargetURI(publicationData.getLink());
                            link.setLinkOwner(collectedVolumeDe);
                        }

                        if (publicationData.getBeschreibung() != null) {
                            collectedVolumeDe.setAbstract(publicationData.
                                    getBeschreibung());
                        }

                        collectedVolumeDe.save();

                        collectedVolumeEn = new CollectedVolume();
                        collectedVolumeEn.setTitle(publicationData.getName());
                        collectedVolumeEn.setName(publicationData.getUrl());
                        extractYearOfPublication(publicationData,
                                                 collectedVolumeEn);
                        publisherData = extractPublisher(publicationData.
                                getVerlag());
                        if (publishersMap.containsKey(publisherData)) {
                            collectedVolumeEn.setPublisher((Publisher) publishersMap.
                                    get(publisherData).getPrimaryInstance());
                        } else {
                            System.out.println(
                                    "***WARNING: Invalid publisher. Ignoring.");
                        }

                        if ((publicationData.getLink() != null)
                            && !publicationData.getLink().isEmpty()) {
                            RelatedLink link = new RelatedLink();
                            link.setTitle(config.getProperty(
                                    "publications.furtherInfoLink.en",
                                    publicationData.getLink()));
                            link.setTargetType(Link.EXTERNAL_LINK);
                            link.setTargetURI(publicationData.getLink());
                            link.setLinkOwner(collectedVolumeEn);
                        }

                        if (publicationData.getBeschreibung() != null) {
                            collectedVolumeEn.setAbstract(publicationData.
                                    getBeschreibung());
                        }

                        collectedVolumeEn.save();
                        publicationDe = collectedVolumeDe;
                        publicationEn = collectedVolumeEn;

                        break;
                    }
                    case ARTICLE_IN_COLLECTED_VOLUME: {
                        ArticleInCollectedVolume articleDe;
                        ArticleInCollectedVolume articleEn;

                        articleDe = new ArticleInCollectedVolume();
                        articleDe.setTitle(publicationData.getName());
                        articleDe.setName(publicationData.getUrl());
                        extractYearOfPublication(publicationData, articleDe);
                        if ((publicationData.getLink() != null)
                            && !publicationData.getLink().isEmpty()) {
                            RelatedLink link = new RelatedLink();
                            link.setTitle(config.getProperty(
                                    "publications.furtherInfoLink.de",
                                    publicationData.getLink()));
                            link.setTargetType(Link.EXTERNAL_LINK);
                            link.setTargetURI(publicationData.getLink());
                            link.setLinkOwner(articleDe);
                        }

                        if (publicationData.getBeschreibung() != null) {
                            articleDe.setAbstract(publicationData.
                                    getBeschreibung());
                        }

                        if ((publicationData.getErschienenIn() != null)
                            && !publicationData.getErschienenIn().isEmpty()) {
                            articleDe.setMisc(publicationData.getErschienenIn());
                        }
                        if (publicationData.getPagesFrom() != 0) {
                            articleDe.setPagesFrom(
                                    publicationData.getPagesFrom());
                            articleDe.setPagesTo(publicationData.getPagesTo());
                        }
                        articleDe.save();

                        articleEn = new ArticleInCollectedVolume();
                        articleEn.setTitle(publicationData.getName());
                        articleEn.setName(publicationData.getUrl());
                        extractYearOfPublication(publicationData, articleEn);
                        if ((publicationData.getLink() != null)
                            && !publicationData.getLink().isEmpty()) {
                            RelatedLink link = new RelatedLink();
                            link.setTitle(config.getProperty(
                                    "publications.furtherInfoLink.en",
                                    publicationData.getLink()));
                            link.setTargetType(Link.EXTERNAL_LINK);
                            link.setTargetURI(publicationData.getLink());
                            link.setLinkOwner(articleEn);
                        }

                        if (publicationData.getBeschreibung() != null) {
                            articleEn.setAbstract(publicationData.
                                    getBeschreibung());
                        }

                        if ((publicationData.getErschienenIn() != null)
                            && !publicationData.getErschienenIn().isEmpty()) {
                            articleEn.setMisc(publicationData.getErschienenIn());
                        }
                        if (publicationData.getPagesFrom() != 0) {
                            articleEn.setPagesFrom(
                                    publicationData.getPagesFrom());
                            articleEn.setPagesTo(publicationData.getPagesTo());
                        }

                        articleEn.save();
                        publicationDe = articleDe;
                        publicationEn = articleEn;

                        break;
                    }
                    case ARTICLE_IN_JOURNAL: {
                        ArticleInJournal articleDe;
                        ArticleInJournal articleEn;

                        articleDe = new ArticleInJournal();
                        articleDe.setTitle(publicationData.getName());
                        articleDe.setName(publicationData.getUrl());
                        extractYearOfPublication(publicationData, articleDe);
                        if ((publicationData.getLink() != null)
                            && !publicationData.getLink().isEmpty()) {
                            RelatedLink link = new RelatedLink();
                            String titleUrl;
                            if (publicationData.getLink().length() < 200) {
                                titleUrl = publicationData.getLink();
                            } else {
                                System.out.println(
                                        "\t***WARNING: Link in publication is too long for title. Truncating.");
                                titleUrl = publicationData.getLink().
                                        substring(0, 200);
                            }
                            link.setTitle(config.getProperty(
                                    "publications.furtherInfoLink.de",
                                    titleUrl));
                            link.setTargetType(Link.EXTERNAL_LINK);
                            link.setTargetURI(publicationData.getLink());
                            link.setLinkOwner(articleDe);
                        }

                        if (publicationData.getBeschreibung() != null) {
                            articleDe.setAbstract(publicationData.
                                    getBeschreibung());
                        }

                        if ((publicationData.getErschienenIn() != null)
                            && !publicationData.getErschienenIn().isEmpty()) {
                            articleDe.setMisc(publicationData.getErschienenIn());
                        }
                        if (publicationData.getPagesFrom() != 0) {
                            articleDe.setPagesFrom(
                                    publicationData.getPagesFrom());
                            articleDe.setPagesTo(publicationData.getPagesTo());
                        }
                        articleDe.save();

                        articleEn = new ArticleInJournal();
                        articleEn.setTitle(publicationData.getName());
                        articleEn.setName(publicationData.getUrl());
                        extractYearOfPublication(publicationData, articleEn);
                        if ((publicationData.getLink() != null)
                            && !publicationData.getLink().isEmpty()) {
                            RelatedLink link = new RelatedLink();
                            String titleUrl;
                            if (publicationData.getLink().length() < 200) {
                                titleUrl = publicationData.getLink();
                            } else {
                                System.out.println(
                                        "\t***WARNING: Link in publication is too long for title. Truncating.");
                                titleUrl = publicationData.getLink().
                                        substring(0, 200);
                            }
                            link.setTitle(config.getProperty(
                                    "publications.furtherInfoLink.en",
                                    titleUrl));
                            link.setTargetType(Link.EXTERNAL_LINK);
                            link.setTargetURI(publicationData.getLink());
                            link.setLinkOwner(articleEn);
                        }

                        if (publicationData.getBeschreibung() != null) {
                            articleEn.setAbstract(publicationData.
                                    getBeschreibung());
                        }

                        if ((publicationData.getErschienenIn() != null)
                            && !publicationData.getErschienenIn().isEmpty()) {
                            articleEn.setMisc(publicationData.getErschienenIn());
                        }
                        if (publicationData.getPagesFrom() != 0) {
                            articleEn.setPagesFrom(
                                    publicationData.getPagesFrom());
                            articleEn.setPagesTo(publicationData.getPagesTo());
                        }

                        articleEn.save();
                        publicationDe = articleDe;
                        publicationEn = articleEn;

                        break;
                    }
                    case GREY_LITERATURE: {
                        GreyLiterature greyDe;
                        GreyLiterature greyEn;

                        greyDe = new GreyLiterature();
                        greyDe.setTitle(publicationData.getName());
                        greyDe.setName(publicationData.getUrl());
                        extractYearOfPublication(publicationData, greyDe);
                        if ((publicationData.getLink() != null)
                            && !publicationData.getLink().isEmpty()) {
                            RelatedLink link = new RelatedLink();
                            link.setTitle(config.getProperty(
                                    "publications.furtherInfoLink.de",
                                    publicationData.getLink()));
                            link.setTargetType(Link.EXTERNAL_LINK);
                            link.setTargetURI(publicationData.getLink());
                            link.setLinkOwner(greyDe);
                        }

                        if (publicationData.getBeschreibung() != null) {
                            greyDe.setAbstract(publicationData.getBeschreibung());
                        }
                        if ((publicationData.getErschienenIn() != null)
                            && !publicationData.getErschienenIn().isEmpty()) {
                            greyDe.setMisc(publicationData.getErschienenIn());
                        }
                        if (publicationData.getPagesFrom() != 0) {
                            greyDe.setPagesFrom(
                                    publicationData.getPagesFrom());
                            greyDe.setPagesTo(publicationData.getPagesTo());
                        }
                        greyDe.save();

                        greyEn = new GreyLiterature();
                        greyEn.setTitle(publicationData.getName());
                        greyEn.setName(publicationData.getUrl());
                        extractYearOfPublication(publicationData, greyEn);
                        if ((publicationData.getLink() != null)
                            && !publicationData.getLink().isEmpty()) {
                            RelatedLink link = new RelatedLink();
                            link.setTitle(config.getProperty(
                                    "publications.furtherInfoLink.en",
                                    publicationData.getLink()));
                            link.setTargetType(Link.EXTERNAL_LINK);
                            link.setTargetURI(publicationData.getLink());
                            link.setLinkOwner(greyEn);
                        }

                        if (publicationData.getBeschreibung() != null) {
                            greyEn.setAbstract(publicationData.getBeschreibung());
                        }
                        if ((publicationData.getErschienenIn() != null)
                            && !publicationData.getErschienenIn().isEmpty()) {
                            greyEn.setMisc(publicationData.getErschienenIn());
                        }
                        if (publicationData.getPagesFrom() != 0) {
                            greyEn.setPagesFrom(
                                    publicationData.getPagesFrom());
                            greyEn.setPagesTo(publicationData.getPagesTo());
                        }

                        greyEn.save();
                        publicationDe = greyDe;
                        publicationEn = greyEn;

                        break;
                    }
                }

                publicationDe.setLifecycle(
                        createLifecycle(publicationsLifecycle));
                publicationDe.setContentSection(publicationsSection);
                publicationDe.setLanguage("de");
                publicationEn.setLanguage("en");
                publicationEn.setLifecycle(
                        createLifecycle(publicationsLifecycle));
                publicationEn.setContentSection(publicationsSection);

                System.out.println("\tAssigning authors...\n");
                int i = 1;
                for (Authorship authorship : publicationData.getAuthors()) {
                    System.out.printf("\t\t%d of %d (dabin person id: %s)...",
                                      i,
                                      publicationData.getAuthors().size(),
                                      authorship.getPersonDaBInId());
                    if (!personsMap.containsKey(authorship.getPersonDaBInId())) {
                        System.out.printf("No person for DaBIn id '%s'. "
                                          + "Skiping.\n",
                                          authorship.getPersonDaBInId());
                        continue;
                    }
                    GenericPerson author = (GenericPerson) personsMap.get(authorship.
                            getPersonDaBInId()).getPrimaryInstance();
                    if ("Herausgeber".equals(authorship.getBeteiligung())) {
                        publicationDe.addAuthor(author, true);
                        publicationEn.addAuthor(author, true);
                    } else {
                        publicationDe.addAuthor(author, false);
                        publicationEn.addAuthor(author, true);
                    }

                    if ("et. al.".equals(author.getSurname())
                        || "et. al.".equals(author.getGivenName())) {
                        System.out.printf(
                                "\t***WARNING: The publication %s has a author 'et. al.'. It is strongly recommened to name ALL authors of a publication. ");
                    }

                    RelatedLink myPublication;
                    myPublication = new RelatedLink();
                    myPublication.setLinkOwner((GenericPerson) personsMap.get(authorship.
                            getPersonDaBInId()).getInstance("de"));
                    myPublication.setTargetType(Link.INTERNAL_LINK);
                    myPublication.setTargetItem(publicationDe);
                    if (publicationDe.getTitle().length() < 200) {
                        myPublication.setTitle(publicationDe.getTitle());
                    } else {
                        System.out.println(
                                "\t***WARNING: Publication title is too long for link title. Truncating.");
                        myPublication.setTitle(publicationDe.getTitle().
                                substring(0, 200));
                    }
                    myPublication.setLinkListName("MyPublications");
                    myPublication.save();

                    myPublication = new RelatedLink();
                    myPublication.setLinkOwner((GenericPerson) personsMap.get(authorship.
                            getPersonDaBInId()).getInstance("en"));
                    myPublication.setTargetType(Link.INTERNAL_LINK);
                    myPublication.setTargetItem(publicationEn);
                    if (publicationEn.getTitle().length() < 180) {
                        myPublication.setTitle(publicationEn.getTitle());
                    } else {
                        System.out.println(
                                "\t***WARNING: Publication title is too long for link title. Truncating.");
                        myPublication.setTitle(publicationEn.getTitle().
                                substring(0, 180));
                    }
                    myPublication.setLinkListName("MyPublications");
                    myPublication.save();

                    System.out.println("OK");
                    i++;
                }

                if (publicationDe == null) {
                    publication = new ContentBundle(publicationEn);
                    publication.setDefaultLanguage("en");
                } else {
                    publication = new ContentBundle(publicationDe);
                    publication.setDefaultLanguage("de");
                }
                publication.setLifecycle(createLifecycle(publicationsLifecycle));
                publication.setContentSection(publicationsSection);

                publicationDe.setContentSection(publicationsSection);
                publicationEn.setContentSection(publicationsSection);

                if ((publicationData.getAbteilungId() != null)
                    && !publicationData.getAbteilungId().isEmpty()
                    && departmentsMap.containsKey(
                        publicationData.getAbteilungId())
                    && (publicationData.getVisiblity()
                        != PublicationVisibility.PRIVATE)) {
                    System.out.println(
                            "\tAssigning publication to department...\n");
                    ContentBundle department = departmentsMap.get(publicationData.
                            getAbteilungId());
                    ItemCollection instances = department.getInstances();
                    while (instances.next()) {
                        RelatedLink pubLink;
                        pubLink = new RelatedLink();
                        pubLink.setLinkListName("SciDepartmentPublications");
                        if (((Publication) publication.getPrimaryInstance()).
                                getTitle().length() < 180) {
                            pubLink.setTitle(((Publication) publication.
                                              getPrimaryInstance()).getTitle());
                        } else {
                            System.out.println(
                                    "\t***WARNING: Title of publication too long for link title. Truncating.");
                            pubLink.setTitle(((Publication) publication.
                                              getPrimaryInstance()).getTitle().
                                    substring(0, 180));
                        }
                        pubLink.setTargetType(Link.INTERNAL_LINK);
                        pubLink.setTargetItem(publication);
                        pubLink.setLinkOwner(instances.getContentItem());
                        pubLink.save();
                    }
                }

                publications.addItem(publication);
                if (publicationData.getVisiblity()
                    == PublicationVisibility.GLOBAL) {
                    Term term = publicationsTerm;
                    term = termsDomain.getTerm(term.getUniqueID());
                    System.out.printf(
                            "\tAdding publication to term '%s:%s'...\n", term.
                            getUniqueID(), term.getName());
                    term.addObject(publication);
                    term.save();
                }
            }
        };

        transaction.run();

        System.out.println("FINISHED");
    }

    private void createWorkingPaper(final WorkingPaperData workingPaperData) {
        Transaction transaction = new Transaction() {

            @Override
            protected void doRun() {
                WorkingPaper workingPaperDe = null;
                WorkingPaper workingPaperEn = null;
                ContentBundle workingPaper;

                if ((workingPaperData.getTitleDe() != null)
                    && !(workingPaperData.getTitleDe().isEmpty())) {
                    System.out.printf("\tde: %s (%s)...",
                                      workingPaperData.getTitleDe(),
                                      workingPaperData.getYear());
                    workingPaperDe = new WorkingPaper();
                    workingPaperDe.setTitle(workingPaperData.getTitleDe());
                    String workingPaperName = DaBInImporter.normalizeString(
                            workingPaperData.getTitleDe());
                    if (workingPaperName.length() > 200) {
                        workingPaperName =
                        workingPaperName.substring(0, 200);
                    }
                    workingPaperDe.setName(workingPaperName);
                    if (workingPaperData.getDescDe().length() > 8000) {
                        workingPaperDe.setAbstract(workingPaperData.getDescDe().
                                substring(0, 8000));
                    } else {
                        workingPaperDe.setAbstract(workingPaperData.getDescDe());
                    }
                    workingPaperDe.setNumber(workingPaperData.getNumber());
                    workingPaperDe.setOrganization(orgaDe);
                    workingPaperDe.setPlace("Bremen");
                    extractYearOfPublication(workingPaperData, workingPaperDe);
                    workingPaperDe.setLanguage("de");
                    workingPaperDe.setLifecycle(createLifecycle(
                            publicationsLifecycle));
                    workingPaperDe.setContentSection(publicationsSection);
                    workingPaperDe.save();
                    System.out.println("OK");
                } else {
                    System.out.println("No german version. Skiping.");
                }

                if ((workingPaperData.getTitleEn() != null)
                    && !(workingPaperData.getTitleEn().isEmpty())) {
                    System.out.printf("\tEn: %s (%s)...",
                                      workingPaperData.getTitleEn(),
                                      workingPaperData.getYear());
                    workingPaperEn = new WorkingPaper();
                    workingPaperEn.setTitle(workingPaperData.getTitleEn());
                    String workingPaperName = DaBInImporter.normalizeString(
                            workingPaperData.getTitleDe());
                    if (workingPaperName.length() > 200) {
                        workingPaperName =
                        workingPaperName.substring(0, 200);
                    }
                    workingPaperEn.setName(workingPaperName);
                    if (workingPaperData.getDescEn().length() > 4096) {
                        System.out.println(
                                "***Warning: Value of DaBIn field abstract too long for abstracts (max: 4096 characters). Truncating.");
                        workingPaperEn.setAbstract(workingPaperData.getDescEn().
                                substring(0, 4095));
                    } else {
                        workingPaperEn.setAbstract(workingPaperData.getDescEn());
                    }
                    workingPaperEn.setNumber(workingPaperData.getNumber());
                    workingPaperEn.setOrganization(orgaEn);
                    workingPaperEn.setPlace("Bremen");
                    extractYearOfPublication(workingPaperData, workingPaperEn);
                    workingPaperEn.setLanguage("en");
                    workingPaperEn.setLifecycle(createLifecycle(
                            publicationsLifecycle));
                    workingPaperEn.setContentSection(publicationsSection);
                    workingPaperEn.save();
                    System.out.println("OK");
                } else {
                    System.out.println("No english version. Skiping.");
                }

                if (workingPaperDe == null) {
                    workingPaper = new ContentBundle(workingPaperEn);
                } else {
                    workingPaper = new ContentBundle(workingPaperDe);
                    if (workingPaperEn != null) {
                        workingPaper.addInstance(workingPaperEn);
                    }
                }
                workingPaper.setLifecycle(createLifecycle(publicationsLifecycle));
                workingPaper.setContentSection(publicationsSection);

                if (workingPaperDe != null) {
                    workingPaperDe.setContentSection(publicationsSection);
                }
                if (workingPaperEn != null) {
                    workingPaperEn.setContentSection(publicationsSection);
                }

                workingPaperMap.put(workingPaperData.getDabinId(), workingPaper);
                publications.addItem(workingPaper);
                WorkingPaper primary = (WorkingPaper) workingPaper.
                        getPrimaryInstance();
                Term term = workingPapersTerm;//s.get(yearStr);                
                term = termsDomain.getTerm(term.getUniqueID());
                System.out.printf("\tAdding project to term '%s:%s'...\n", term.
                        getUniqueID(), term.getName());
                term.addObject(workingPaper);
                term.save();

                System.out.println("\tOK");

                System.out.print("\tAssigning file...\n ");
                if (workingPaperData.getFile() == null) {
                    System.out.println("No file found.");
                } else {
                    try {
                        File tmpFile = File.createTempFile(
                                "ccm_workingpaperCompressed", ".zip");

                        FileOutputStream tmpFileStream =
                                         new FileOutputStream(tmpFile);
                        byte buf[] = new Base64().decode(workingPaperData.
                                getFile());
                        tmpFileStream.write(buf);
                        tmpFileStream.close();

                        ZipFile zipFile = new ZipFile(tmpFile);
                        Enumeration<? extends ZipEntry> entries = zipFile.
                                entries();
                        if (entries.hasMoreElements()) {
                            InputStream unzip = zipFile.getInputStream(entries.
                                    nextElement());

                            File pdf = File.createTempFile("ccm_workingPaper",
                                                           ".pdf");
                            FileOutputStream pdfFileStream = new FileOutputStream(
                                    pdf);
                            int b;
                            while ((b = unzip.read()) != -1) {
                                pdfFileStream.write(b);
                            }

                            unzip.close();
                            pdfFileStream.close();

                            FileStorageItem fsi = new FileStorageItem();
                            String title = String.format("Datei %s [pdf]",
                                                         ((WorkingPaper) workingPaper.
                                                          getPrimaryInstance()).
                                    getTitle());
                            if (title.length() > 200) {
                                fsi.setTitle(title.substring(0, 200));
                            } else {
                                fsi.setTitle(title);
                            }

                            String name = String.format("datei_%s-pdf",
                                                        ((WorkingPaper) workingPaper.
                                                         getPrimaryInstance()).
                                    getName());
                            if (name.length() > 200) {
                                name = name.substring(0, 200);
                            }
                            fsi.setName(name);
                            FileAsset file = new FileAsset();
                            file.loadFromFile(name,
                                              pdf,
                                              "application/pdf");
                            fsi.setFile(file);
                            file.setContentSection(publicationsSection);
                            file.setLifecycle(createLifecycle(
                                    publicationsLifecycle));
                            fsi.setLifecycle(createLifecycle(
                                    publicationsLifecycle));
                            fsi.setContentSection(publicationsSection);
                            fsi.save();

                            fsi.setLanguage("de");
                            ContentBundle bundle = new ContentBundle(fsi);
                            bundle.setLifecycle(createLifecycle(
                                    publicationsLifecycle));
                            bundle.setContentSection(publicationsSection);
                            bundle.setDefaultLanguage("de");
                            //bundle.save();

                            RelatedLink download = new RelatedLink();
                            download.setTitle(config.getProperty(
                                    "workingpaper.download.de"));
                            download.setTargetType(Link.INTERNAL_LINK);
                            download.setTargetItem(fsi);
                            download.setLinkOwner(workingPaperDe);

                            download = new RelatedLink();
                            download.setTitle(config.getProperty(
                                    "workingpaper.download.en"));
                            download.setTargetType(Link.INTERNAL_LINK);
                            download.setTargetItem(fsi);
                            download.setLinkOwner(workingPaperEn);

                            files.addItem(bundle);
                        }
                    } catch (IOException ex) {
                        System.out.println(
                                "\n***ERROR: Failed to copy file from DaBIn to CCM: ");
                        ex.printStackTrace(System.out);
                    }

                }

                System.out.print("\tAssigning authors to working paper...\n");
                int i = 1;
                for (Authorship authorship : workingPaperData.getAuthors()) {
                    System.out.printf("\t\t%d of %d (dabin person id: %s)...",
                                      i,
                                      workingPaperData.getAuthors().size(),
                                      authorship.getPersonDaBInId());
                    if (!personsMap.containsKey(authorship.getPersonDaBInId())) {
                        System.out.printf("No person for DaBIn id '%s'. "
                                          + "Skiping.\n",
                                          authorship.getPersonDaBInId());
                        continue;
                    }
                    GenericPerson author = (GenericPerson) personsMap.get(authorship.
                            getPersonDaBInId()).getPrimaryInstance();
                    if (workingPaperDe != null) {
                        if ("Herausgeber".equals(authorship.getBeteiligung())) {
                            workingPaperDe.addAuthor(author, true);
                        } else {
                            workingPaperDe.addAuthor(author, false);
                        }


                        RelatedLink myPublication;
                        myPublication = new RelatedLink();
                        myPublication.setLinkOwner((GenericPerson) personsMap.
                                get(authorship.getPersonDaBInId()).getInstance(
                                "de"));
                        myPublication.setTargetType(Link.INTERNAL_LINK);
                        myPublication.setTargetItem(workingPaperDe);
                        if (workingPaperDe.getTitle().length() > 180) {
                            myPublication.setTitle(workingPaperDe.getTitle().
                                    substring(0, 180));
                        } else {
                            myPublication.setTitle(workingPaperDe.getTitle());
                        }
                        myPublication.setLinkListName("MyPublications");
                        myPublication.save();
                    }
                    if (workingPaperEn != null) {
                        if ("Herausgeber".equals(authorship.getBeteiligung())) {
                            workingPaperEn.addAuthor(author, true);
                        } else {
                            workingPaperEn.addAuthor(author, false);
                        }

                        RelatedLink myPublication;
                        myPublication = new RelatedLink();
                        myPublication.setLinkOwner((GenericPerson) personsMap.
                                get(authorship.getPersonDaBInId()).getInstance(
                                "en"));
                        myPublication.setTargetType(Link.INTERNAL_LINK);
                        myPublication.setTargetItem(workingPaperEn);
                        if (workingPaperEn.getTitle().length() > 180) {
                            myPublication.setTitle(workingPaperEn.getTitle().
                                    substring(0, 180));
                        } else {
                            myPublication.setTitle(workingPaperEn.getTitle());
                        }
                        myPublication.setLinkListName("MyPublications");
                        myPublication.save();
                    }

                    System.out.println("\tOK");
                    i++;
                }

                System.out.println("OK");
            }
        };

        transaction.run();

        System.out.println("FINISHED");
    }

    private void createPublisher(final PublisherData publisherData) {
        if (publishersMap.containsKey(publisherData)) {
            System.out.printf(
                    "Publisher '%s: %s' already exists. Skiping.\n",
                    publisherData.getPlace(),
                    publisherData.getName());
            return;
        }

        if ((publisherData.getName().length() == 0)
            || DaBInImporter.normalizeString(publisherData.getName()).length()
               == 0) {
            System.out.println("Publisher has no name. Skiping.");
            return;
        }

        if (publisherData.getName().length() < 3) {
            System.out.printf(
                    "WARNING: The name of the publisher '%s' is very short.",
                    publisherData.getName());
        }

        if ((publisherData.getPlace() == null)
            || publisherData.getPlace().isEmpty()) {
            System.out.printf(
                    "WARNING: The publisher '%s' has no place.",
                    publisherData.getName());
        }

        Transaction transaction = new Transaction() {

            @Override
            protected void doRun() {
                Publisher publisherDe;
                Publisher publisherEn;
                ContentBundle publisher;

                System.out.printf("\tde: %s, %s...", publisherData.getName(), publisherData.
                        getPlace());
                publisherDe = new Publisher();
                if ((publisherData.getPlace() == null) || publisherData.getPlace().
                        isEmpty()) {
                    publisherDe.setTitle(publisherData.getName());
                    publisherDe.setName(DaBInImporter.normalizeString(publisherData.
                            getName()));
                } else {
                    /*publisherDe.setTitle(String.format("%s, %s", publisherData.
                    getName(), publisherData.getPlace()));*/
                    publisherDe.setTitle(String.format("%s %s",
                                                       publisherData.getName(),
                                                       publisherData.getPlace()));
                    publisherDe.setName(DaBInImporter.normalizeString(String.
                            format(
                            "%s %s", publisherData.getName(), publisherData.
                            getPlace())));
                }
                publisherDe.setPublisherName(publisherData.getName());
                publisherDe.setPlace(publisherData.getPlace());
                publisherDe.setLanguage("de");
                publisherDe.setLifecycle(createLifecycle(publicationsLifecycle));
                publisherDe.setContentSection(publicationsSection);
                publisherDe.save();
                System.out.println("OK");

                System.out.printf("\tEn: %s, %s...", publisherData.getName(), publisherData.
                        getPlace());
                publisherEn = new Publisher();
                if ((publisherData.getPlace() == null) || publisherData.getPlace().
                        isEmpty()) {
                    publisherEn.setTitle(publisherData.getName());
                    publisherEn.setName(DaBInImporter.normalizeString(publisherData.
                            getName()));
                } else {
                    publisherEn.setTitle(String.format("%s %s", publisherData.
                            getName(), publisherData.getPlace()));
                    publisherEn.setName(DaBInImporter.normalizeString(String.
                            format(
                            "%s %s", publisherData.getName(), publisherData.
                            getPlace())));
                }
                publisherEn.setPublisherName(publisherData.getName());
                publisherEn.setPlace(publisherData.getPlace());
                publisherEn.setLanguage("en");
                publisherEn.setLifecycle(createLifecycle(publicationsLifecycle));
                publisherEn.setContentSection(publicationsSection);
                publisherEn.save();
                System.out.println("OK");

                publisher = new ContentBundle(publisherDe);
                publisher.addInstance(publisherEn);
                publisher.setDefaultLanguage("de");
                publisher.setLifecycle(createLifecycle(publicationsLifecycle));
                publisher.setContentSection(publicationsSection);

                publisherDe.setContentSection(publicationsSection);
                publisherEn.setContentSection(publicationsSection);

                publishers.addItem(publisher);
                System.out.printf(
                        "Putting publisher into publishers map. HashCode: %d",
                        publisherData.hashCode());
                publishersMap.put(publisherData, publisher);
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

    protected PublisherData extractPublisher(final String data) {
        PublisherData publisher;
        String normalizedData;

        normalizedData = data.replace(',', '.').replace(')', '.').replace('(',
                                                                          '.');
        System.out.printf("\tExtracting publisher name and place from: %s...\n",
                          normalizedData);

        publisher = new PublisherData();

        int colonIndex = normalizedData.indexOf(':');
        if (colonIndex < 0) {
            while ((normalizedData.length() > 1)
                   && !Character.isLetter(normalizedData.charAt(0))) {
                normalizedData = normalizedData.substring(1);
            }

            publisher.setName(normalizedData.trim());/*.replace(",", "").
            replace("/", "").
            replaceAll("\\s\\s+", " ").
            replace(' ', '-').toLowerCase());*/
        } else {
            String name;
            String place;
            int prevDelimIndex;
            int nextDelimIndex;

            nextDelimIndex = normalizedData.indexOf('.', colonIndex);
            System.out.printf("\tcolonIndex = %d\n", colonIndex);
            if (nextDelimIndex < 0) {
                nextDelimIndex = normalizedData.indexOf(',', colonIndex);
            }
            if (nextDelimIndex < 0) {
                nextDelimIndex = normalizedData.indexOf(':', colonIndex + 1);
            }

            System.out.printf("\tnextDelimIndex = %d\n", nextDelimIndex);
            if (nextDelimIndex < 0) {
                System.out.println("\tNext delim smaller than 0...");
                name = normalizedData.substring(colonIndex);
            } else {
                System.out.println("\tNext delim greater than 0...");
                name = normalizedData.substring(colonIndex + 1, nextDelimIndex);
            }

            prevDelimIndex = normalizedData.lastIndexOf('.', colonIndex);
            if (prevDelimIndex < 0) {
                prevDelimIndex = normalizedData.lastIndexOf(')', colonIndex);
            }
            if (prevDelimIndex < 0) {
                prevDelimIndex = 0;
            } else {
                prevDelimIndex++;
            }
            System.out.printf("\tprevDelimIndex = %d\n", prevDelimIndex);
            place = normalizedData.substring(prevDelimIndex, colonIndex);

            while ((name.length() > 1)
                   && !Character.isLetter(name.charAt(0))) {
                name = name.substring(1);
            }
            publisher.setName(name.trim());//.
            //replace(",", "").
            //replace("/", "").
            //replaceAll("\\s\\s+", " ").
            //replace(' ', '-').toLowerCase());
            while ((place.length() > 1)
                   && !Character.isLetter(place.charAt(0))) {
                place = place.substring(1);
            }
            publisher.setPlace(place.trim());
        }

        System.out.printf("\tExtracted:\n\tName: %s\n\tPlace: %s\n",
                          publisher.getName(),
                          publisher.getPlace());
        return publisher;
    }

    private void extractYearOfPublication(final PublicationData data,
                                          final Publication publication) {
        try {
            if ((data.getJahr() != null)
                && (data.getJahr().length() <= 4)) {
                publication.setYearOfPublication(
                        Integer.parseInt(data.getJahr()));
            } else if ((data.getJahr() != null)
                           && (data.getJahr().length() > 4)) {
                publication.setYearOfPublication(Integer.parseInt(data.getJahr().
                        substring(0, 4)));
            }
        } catch (NumberFormatException ex) {
            System.out.println(
                    "***WARNING: Invalid year of publication: Not a number. Ignoring.");
        }
    }

    private void extractYearOfPublication(final WorkingPaperData data,
                                          final Publication publication) {
        try {
            if ((data.getYear() != null)
                && (data.getYear().length() <= 4)) {
                publication.setYearOfPublication(
                        Integer.parseInt(data.getYear()));
            } else if ((data.getYear() != null)
                           && (data.getYear().length() > 4)) {
                publication.setYearOfPublication(Integer.parseInt(data.getYear().
                        substring(0, 4)));
            }
        } catch (NumberFormatException ex) {
            System.out.println(
                    "***WARNING: Invalid year of publication: Not a number. Ignoring.");
        }
    }

    private void extractPages(final String data,
                              final PublicationData publicationData) {
        int index;
        int leftLimit;
        int rightLimit;
        String tmp;
        int pagesFrom;
        int pagesTo;

        System.out.printf("Trying to extract pages from '%s'...\n", data);

        index = data.lastIndexOf('-');
        leftLimit = data.lastIndexOf(' ', index);
        rightLimit = data.indexOf(' ', index);

        if (leftLimit < 0) {
            System.out.println(
                    "*** WARNING: Malformed pages. Left limit less than 0. Ignoring.");

        }
        if (rightLimit > data.length()) {
            System.out.println(
                    "*** WARNING: Malformed pages. Right limit greater than length of string. Ignoring.");
        }
        if (rightLimit < 0) {
            System.out.println(
                    "*** WARNING: Malformed pages. Right limit is smaller than 0. Ignoring.");
        }

        try {
            tmp = data.substring(leftLimit + 1, index);
            pagesFrom = Integer.parseInt(tmp);

            tmp = data.substring(index + 1, rightLimit);
            pagesTo = Integer.parseInt(tmp);
        } catch (NumberFormatException ex) {
            System.out.println("*** WARNING: Malformed pages. Ignoring.");
            return;
        } catch (StringIndexOutOfBoundsException ex) {
            System.out.println("*** WARNING: Malformed pages. Ignoring.");
            return;
        }

        publicationData.setPagesFrom(pagesFrom);
        publicationData.setPagesTo(pagesTo);
    }

    private Term checkTermPath(final String path) {
        StringTokenizer pathTokenizer = new StringTokenizer(path, "/");

        Term prevTerm = null;
        Term term = null;
        while (pathTokenizer.hasMoreTokens()) {
            String token = pathTokenizer.nextToken();
            String[] split;
            String uniqueId;
            String name;

            split = token.split(":");
            if (split.length < 2) {
                System.err.printf(
                        "***ERROR: Malformed path token '%s'. Abborting path check.\n",
                        token);
                return null;
            }

            uniqueId = split[0];
            name = split[1];

            prevTerm = term;
            try {
                term = termsDomain.getTerm(uniqueId);
            } catch (DataObjectNotFoundException ex) {
                System.out.printf("Term '%s' does not exist. Creating...\n",
                                  token);
                createTerm(uniqueId, name, termsDomain, prevTerm);
                term = termsDomain.getTerm(uniqueId);
            }
        }

        return term;
    }

    private void createTerm(final String uniqueId,
                            final String name,
                            final Domain domain,
                            final Term parent) {
        Transaction transaction = new Transaction() {

            @Override
            protected void doRun() {
                try {
                    Term term = Term.create(uniqueId, name, false, "", domain);
                    term.save();
                    Term parentTerm = termsDomain.getTerm(parent.getUniqueID());
                    term = termsDomain.getTerm(term.getUniqueID());
                    parentTerm.addNarrowerTerm(term, true, true);
                    parentTerm.save();
                } catch (Exception ex) {
                    ex.printStackTrace(System.err);
                }
            }
        };

        transaction.run();
    }

    private Lifecycle createLifecycle(final LifecycleDefinition def) {
        Lifecycle lifecycle;
        Calendar calendarNow = new GregorianCalendar();
        Date now = calendarNow.getTime();
        lifecycle = def.createLifecycle();
        lifecycle.setStartDate(now);

        return lifecycle;
    }

    public static String normalizeString(final String str) {
        if (str == null) {
            return "null";
        }
        return str.replace("ä", "ae").replace("ö", "oe").replace("ü", "ue").
                replace(
                "Ä", "Ae").replace("Ü", "Ue").replace("Ö", "Oe").replace("ß",
                                                                         "ss").
                replace(" ", "-").
                replaceAll("[^a-zA-Z0-9\\-]", "").toLowerCase().trim();
    }

    public static void main(String[] args) {
        new DaBInImporter().run(args);
    }
}
