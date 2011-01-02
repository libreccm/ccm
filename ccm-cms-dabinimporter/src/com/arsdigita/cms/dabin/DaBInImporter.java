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
    private ContentSection section;
    private LifecycleDefinition lifecycle;
    private Connection connection = null;
    private Folder root;
    private Folder authors;
    private Map<Character, Folder> authorsAlpha;
    private Folder contacts;
    private Map<Character, Folder> contactsAlpha;
    private Folder departments;
    private Folder members;
    private Map<Character, Folder> membersAlpha;
    private Folder organization;
    private Folder persons;
    private Map<Character, Folder> personsAlpha;
    private Folder projects;
    private Map<Character, Folder> projectsAlpha;
    private Folder publications;
    private Map<Character, Folder> publicationsAlpha;
    private Folder publishers;
    private Map<Character, Folder> publishersAlpha;
    private Folder files;
    private Map<Character, Folder> filesAlpha;
    private Map<String, ContentBundle> departmentsMap;
    private Map<String, ContentBundle> personsMap;
    private Map<String, ContentBundle> projectsMap;
    private Map<PublisherData, ContentBundle> publishersMap;
    private Map<String, ContentBundle> publicationMap;
    private Map<String, ContentBundle> workingPaperMap;
    private SciOrganization orgaDe;
    private SciOrganization orgaEn;
    private ContentBundle orga;
    private Address postalAddress;
    private Address officeAddress;
    private Domain termsDomain;
    private Term publicationsTerm;
    private Map<String, Term> publicationTerms;
    private Term workingPapersTerm;
    private Map<String, Term> workingPaperTerms;
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
        authorsAlpha = new HashMap<Character, Folder>(27);
        membersAlpha = new HashMap<Character, Folder>(27);
        personsAlpha = new HashMap<Character, Folder>(27);
        contactsAlpha = new HashMap<Character, Folder>(27);
        projectsAlpha = new HashMap<Character, Folder>(27);
        publicationsAlpha = new HashMap<Character, Folder>(27);
        publishersAlpha = new HashMap<Character, Folder>(27);
        filesAlpha = new HashMap<Character, Folder>(27);
        departmentsMap = new HashMap<String, ContentBundle>();
        personsMap = new HashMap<String, ContentBundle>();
        projectsMap = new HashMap<String, ContentBundle>();
        publishersMap = new HashMap<PublisherData, ContentBundle>();
        publicationMap = new HashMap<String, ContentBundle>();
        workingPaperMap = new HashMap<String, ContentBundle>();
        publicationTerms = new HashMap<String, Term>(30);
        workingPaperTerms = new HashMap<String, Term>(30);
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

        section = getContentSection(config.getProperty("ccm.contentsection"));
        LifecycleDefinitionCollection lifecycles =
                                      section.getLifecycleDefinitions();
        while (lifecycles.next()) {
            lifecycle = lifecycles.getLifecycleDefinition();
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

        authors = createFolder(root, "autoren", "Autoren");
        folder = createFolder(authors, "09", "0-9");
        authorsAlpha.put('0', folder);
        folder = createFolder(authors, "ab", "A-B");
        authorsAlpha.put('a', folder);
        authorsAlpha.put('b', folder);
        folder = createFolder(authors, "cd", "C-D");
        authorsAlpha.put('c', folder);
        authorsAlpha.put('d', folder);
        folder = createFolder(authors, "ef", "E-F");
        authorsAlpha.put('e', folder);
        authorsAlpha.put('f', folder);
        folder = createFolder(authors, "gh", "G-H");
        authorsAlpha.put('g', folder);
        authorsAlpha.put('h', folder);
        folder = createFolder(authors, "ij", "I-J");
        authorsAlpha.put('i', folder);
        authorsAlpha.put('j', folder);
        folder = createFolder(authors, "kl", "K-L");
        authorsAlpha.put('k', folder);
        authorsAlpha.put('l', folder);
        folder = createFolder(authors, "mn", "M-N");
        authorsAlpha.put('m', folder);
        authorsAlpha.put('n', folder);
        folder = createFolder(authors, "op", "O-P");
        authorsAlpha.put('o', folder);
        authorsAlpha.put('p', folder);
        folder = createFolder(authors, "qr", "Q-R");
        authorsAlpha.put('q', folder);
        authorsAlpha.put('r', folder);
        folder = createFolder(authors, "st", "S-T");
        authorsAlpha.put('s', folder);
        authorsAlpha.put('t', folder);
        folder = createFolder(authors, "uv", "U-V");
        authorsAlpha.put('u', folder);
        authorsAlpha.put('v', folder);
        folder = createFolder(authors, "wxzy", "W-Z");
        authorsAlpha.put('w', folder);
        authorsAlpha.put('x', folder);
        authorsAlpha.put('y', folder);
        authorsAlpha.put('z', folder);

        contacts = createFolder(root, "kontaktdaten", "Kontaktdaten");
        folder = createFolder(contacts, "09", "0-9");
        contactsAlpha.put('0', folder);
        folder = createFolder(contacts, "ab", "A-B");
        contactsAlpha.put('a', folder);
        contactsAlpha.put('b', folder);
        folder = createFolder(contacts, "cd", "C-D");
        contactsAlpha.put('c', folder);
        contactsAlpha.put('d', folder);
        folder = createFolder(contacts, "ef", "E-F");
        contactsAlpha.put('e', folder);
        contactsAlpha.put('f', folder);
        folder = createFolder(contacts, "gh", "G-H");
        contactsAlpha.put('g', folder);
        contactsAlpha.put('h', folder);
        folder = createFolder(contacts, "ij", "I-J");
        contactsAlpha.put('i', folder);
        contactsAlpha.put('j', folder);
        folder = createFolder(contacts, "kl", "K-L");
        contactsAlpha.put('k', folder);
        contactsAlpha.put('l', folder);
        folder = createFolder(contacts, "mn", "M-N");
        contactsAlpha.put('m', folder);
        contactsAlpha.put('n', folder);
        folder = createFolder(contacts, "op", "O-P");
        contactsAlpha.put('o', folder);
        contactsAlpha.put('p', folder);
        folder = createFolder(contacts, "qr", "Q-R");
        contactsAlpha.put('q', folder);
        contactsAlpha.put('r', folder);
        folder = createFolder(contacts, "st", "S-T");
        contactsAlpha.put('s', folder);
        contactsAlpha.put('t', folder);
        folder = createFolder(contacts, "uv", "U-V");
        contactsAlpha.put('u', folder);
        contactsAlpha.put('v', folder);
        folder = createFolder(contacts, "wxzy", "W-Z");
        contactsAlpha.put('w', folder);
        contactsAlpha.put('x', folder);
        contactsAlpha.put('y', folder);
        contactsAlpha.put('z', folder);

        departments = createFolder(root, "abteilungen", "Abteilungen");

        members = createFolder(root, "mitglieder", "Mitglieder");
        folder = createFolder(members, "09", "0-9");
        membersAlpha.put('0', folder);
        folder = createFolder(members, "ab", "A-B");
        membersAlpha.put('a', folder);
        membersAlpha.put('b', folder);
        folder = createFolder(members, "cd", "C-D");
        membersAlpha.put('c', folder);
        membersAlpha.put('d', folder);
        folder = createFolder(members, "ef", "E-F");
        membersAlpha.put('e', folder);
        membersAlpha.put('f', folder);
        folder = createFolder(members, "gh", "G-H");
        membersAlpha.put('g', folder);
        membersAlpha.put('h', folder);
        folder = createFolder(members, "ij", "I-J");
        membersAlpha.put('i', folder);
        membersAlpha.put('j', folder);
        folder = createFolder(members, "kl", "K-L");
        membersAlpha.put('k', folder);
        membersAlpha.put('l', folder);
        folder = createFolder(members, "mn", "M-N");
        membersAlpha.put('m', folder);
        membersAlpha.put('n', folder);
        folder = createFolder(members, "op", "O-P");
        membersAlpha.put('o', folder);
        membersAlpha.put('p', folder);
        folder = createFolder(members, "qr", "Q-R");
        membersAlpha.put('q', folder);
        membersAlpha.put('r', folder);
        folder = createFolder(members, "st", "S-T");
        membersAlpha.put('s', folder);
        membersAlpha.put('t', folder);
        folder = createFolder(members, "uv", "U-V");
        membersAlpha.put('u', folder);
        membersAlpha.put('v', folder);
        folder = createFolder(members, "wxzy", "W-Z");
        membersAlpha.put('w', folder);
        membersAlpha.put('x', folder);
        membersAlpha.put('y', folder);
        membersAlpha.put('z', folder);

        organization = createFolder(root, "organisationen", "Organisation(en)");

        persons = createFolder(root, "personen", "Personen");
        folder = createFolder(persons, "09", "0-9");
        personsAlpha.put('0', folder);
        folder = createFolder(persons, "ab", "A-B");
        personsAlpha.put('a', folder);
        personsAlpha.put('b', folder);
        folder = createFolder(persons, "cd", "C-D");
        personsAlpha.put('c', folder);
        personsAlpha.put('d', folder);
        folder = createFolder(persons, "ef", "E-F");
        personsAlpha.put('e', folder);
        personsAlpha.put('f', folder);
        folder = createFolder(persons, "gh", "G-H");
        personsAlpha.put('g', folder);
        personsAlpha.put('h', folder);
        folder = createFolder(persons, "ij", "I-J");
        personsAlpha.put('i', folder);
        personsAlpha.put('j', folder);
        folder = createFolder(persons, "kl", "K-L");
        personsAlpha.put('k', folder);
        personsAlpha.put('l', folder);
        folder = createFolder(persons, "mn", "M-N");
        personsAlpha.put('m', folder);
        personsAlpha.put('n', folder);
        folder = createFolder(persons, "op", "O-P");
        personsAlpha.put('o', folder);
        personsAlpha.put('p', folder);
        folder = createFolder(persons, "qr", "Q-R");
        personsAlpha.put('q', folder);
        personsAlpha.put('r', folder);
        folder = createFolder(persons, "st", "S-T");
        personsAlpha.put('s', folder);
        personsAlpha.put('t', folder);
        folder = createFolder(persons, "uv", "U-V");
        personsAlpha.put('u', folder);
        personsAlpha.put('v', folder);
        folder = createFolder(persons, "wxzy", "W-Z");
        personsAlpha.put('w', folder);
        personsAlpha.put('x', folder);
        personsAlpha.put('y', folder);
        personsAlpha.put('z', folder);

        projects = createFolder(root, "projekte", "Projekte");
        folder = createFolder(projects, "09", "0-9");
        projectsAlpha.put('0', folder);
        folder = createFolder(projects, "a", "A");
        projectsAlpha.put('a', folder);
        folder = createFolder(projects, "b", "B");
        projectsAlpha.put('b', folder);
        folder = createFolder(projects, "c", "C");
        projectsAlpha.put('c', folder);
        folder = createFolder(projects, "d", "D");
        projectsAlpha.put('d', folder);
        folder = createFolder(projects, "e", "E");
        projectsAlpha.put('e', folder);
        folder = createFolder(projects, "f", "F");
        projectsAlpha.put('f', folder);
        folder = createFolder(projects, "g", "G");
        projectsAlpha.put('g', folder);
        folder = createFolder(projects, "h", "H");
        projectsAlpha.put('h', folder);
        folder = createFolder(projects, "i", "I");
        projectsAlpha.put('i', folder);
        folder = createFolder(projects, "j", "J");
        projectsAlpha.put('j', folder);
        folder = createFolder(projects, "k", "K");
        projectsAlpha.put('k', folder);
        folder = createFolder(projects, "l", "L");
        projectsAlpha.put('l', folder);
        folder = createFolder(projects, "m", "M");
        projectsAlpha.put('m', folder);
        folder = createFolder(projects, "n", "N");
        projectsAlpha.put('n', folder);
        folder = createFolder(projects, "o", "O");
        projectsAlpha.put('o', folder);
        folder = createFolder(projects, "p", "P");
        projectsAlpha.put('p', folder);
        folder = createFolder(projects, "q", "Q");
        projectsAlpha.put('q', folder);
        folder = createFolder(projects, "r", "R");
        projectsAlpha.put('r', folder);
        folder = createFolder(projects, "s", "S");
        projectsAlpha.put('s', folder);
        folder = createFolder(projects, "t", "T");
        projectsAlpha.put('t', folder);
        folder = createFolder(projects, "u", "U");
        projectsAlpha.put('u', folder);
        folder = createFolder(projects, "v", "V");
        projectsAlpha.put('v', folder);
        folder = createFolder(projects, "w", "W");
        projectsAlpha.put('w', folder);
        folder = createFolder(projects, "x", "X");
        projectsAlpha.put('x', folder);
        folder = createFolder(projects, "y", "Y");
        projectsAlpha.put('y', folder);
        folder = createFolder(projects, "z", "Z");
        projectsAlpha.put('z', folder);

        publishers = createFolder(root, "verlage", "Verlage");
        folder = createFolder(publishers, "09", "0-9");
        publishersAlpha.put('0', folder);
        folder = createFolder(publishers, "a", "A");
        publishersAlpha.put('a', folder);
        folder = createFolder(publishers, "b", "B");
        publishersAlpha.put('b', folder);
        folder = createFolder(publishers, "c", "C");
        publishersAlpha.put('c', folder);
        folder = createFolder(publishers, "d", "D");
        publishersAlpha.put('d', folder);
        folder = createFolder(publishers, "e", "E");
        publishersAlpha.put('e', folder);
        folder = createFolder(publishers, "f", "F");
        publishersAlpha.put('f', folder);
        folder = createFolder(publishers, "g", "G");
        publishersAlpha.put('g', folder);
        folder = createFolder(publishers, "h", "H");
        publishersAlpha.put('h', folder);
        folder = createFolder(publishers, "i", "I");
        publishersAlpha.put('i', folder);
        folder = createFolder(publishers, "j", "J");
        publishersAlpha.put('j', folder);
        folder = createFolder(publishers, "k", "K");
        publishersAlpha.put('k', folder);
        folder = createFolder(publishers, "l", "L");
        publishersAlpha.put('l', folder);
        folder = createFolder(publishers, "m", "M");
        publishersAlpha.put('m', folder);
        folder = createFolder(publishers, "n", "N");
        publishersAlpha.put('n', folder);
        folder = createFolder(publishers, "o", "O");
        publishersAlpha.put('o', folder);
        folder = createFolder(publishers, "p", "P");
        publishersAlpha.put('p', folder);
        folder = createFolder(publishers, "q", "Q");
        publishersAlpha.put('q', folder);
        folder = createFolder(publishers, "r", "R");
        publishersAlpha.put('r', folder);
        folder = createFolder(publishers, "s", "S");
        publishersAlpha.put('s', folder);
        folder = createFolder(publishers, "t", "T");
        publishersAlpha.put('t', folder);
        folder = createFolder(publishers, "u", "U");
        publishersAlpha.put('u', folder);
        folder = createFolder(publishers, "v", "V");
        publishersAlpha.put('v', folder);
        folder = createFolder(publishers, "w", "W");
        publishersAlpha.put('w', folder);
        folder = createFolder(publishers, "x", "X");
        publishersAlpha.put('x', folder);
        folder = createFolder(publishers, "y", "Y");
        publishersAlpha.put('y', folder);
        folder = createFolder(publishers, "z", "Z");
        publishersAlpha.put('z', folder);

        publications = createFolder(root, "publikationen", "Publikationen");
        folder = createFolder(publications, "09", "0-9");
        publicationsAlpha.put('0', folder);
        folder = createFolder(publications, "a", "A");
        publicationsAlpha.put('a', folder);
        folder = createFolder(publications, "b", "B");
        publicationsAlpha.put('b', folder);
        folder = createFolder(publications, "c", "C");
        publicationsAlpha.put('c', folder);
        folder = createFolder(publications, "d", "D");
        publicationsAlpha.put('d', folder);
        folder = createFolder(publications, "e", "E");
        publicationsAlpha.put('e', folder);
        folder = createFolder(publications, "f", "F");
        publicationsAlpha.put('f', folder);
        folder = createFolder(publications, "g", "G");
        publicationsAlpha.put('g', folder);
        folder = createFolder(publications, "h", "H");
        publicationsAlpha.put('h', folder);
        folder = createFolder(publications, "i", "I");
        publicationsAlpha.put('i', folder);
        folder = createFolder(publications, "j", "J");
        publicationsAlpha.put('j', folder);
        folder = createFolder(publications, "k", "K");
        publicationsAlpha.put('k', folder);
        folder = createFolder(publications, "l", "L");
        publicationsAlpha.put('l', folder);
        folder = createFolder(publications, "m", "M");
        publicationsAlpha.put('m', folder);
        folder = createFolder(publications, "n", "N");
        publicationsAlpha.put('n', folder);
        folder = createFolder(publications, "o", "O");
        publicationsAlpha.put('o', folder);
        folder = createFolder(publications, "p", "P");
        publicationsAlpha.put('p', folder);
        folder = createFolder(publications, "q", "Q");
        publicationsAlpha.put('q', folder);
        folder = createFolder(publications, "r", "R");
        publicationsAlpha.put('r', folder);
        folder = createFolder(publications, "s", "S");
        publicationsAlpha.put('s', folder);
        folder = createFolder(publications, "t", "T");
        publicationsAlpha.put('t', folder);
        folder = createFolder(publications, "u", "U");
        publicationsAlpha.put('u', folder);
        folder = createFolder(publications, "v", "V");
        publicationsAlpha.put('v', folder);
        folder = createFolder(publications, "w", "W");
        publicationsAlpha.put('w', folder);
        folder = createFolder(publications, "x", "X");
        publicationsAlpha.put('x', folder);
        folder = createFolder(publications, "y", "Y");
        publicationsAlpha.put('y', folder);
        folder = createFolder(publications, "z", "Z");
        publicationsAlpha.put('z', folder);

        files = createFolder(root, "dateien", "Dateien");
        folder = createFolder(files, "09", "0-9");
        filesAlpha.put('0', folder);
        folder = createFolder(files, "a", "A");
        filesAlpha.put('a', folder);
        folder = createFolder(files, "b", "B");
        filesAlpha.put('b', folder);
        folder = createFolder(files, "c", "C");
        filesAlpha.put('c', folder);
        folder = createFolder(files, "d", "D");
        filesAlpha.put('d', folder);
        folder = createFolder(files, "e", "E");
        filesAlpha.put('e', folder);
        folder = createFolder(files, "f", "F");
        filesAlpha.put('f', folder);
        folder = createFolder(files, "g", "G");
        filesAlpha.put('g', folder);
        folder = createFolder(files, "h", "H");
        filesAlpha.put('h', folder);
        folder = createFolder(files, "i", "I");
        filesAlpha.put('i', folder);
        folder = createFolder(files, "j", "J");
        filesAlpha.put('j', folder);
        folder = createFolder(files, "k", "K");
        filesAlpha.put('k', folder);
        folder = createFolder(files, "l", "L");
        filesAlpha.put('l', folder);
        folder = createFolder(files, "m", "M");
        filesAlpha.put('m', folder);
        folder = createFolder(files, "n", "N");
        filesAlpha.put('n', folder);
        folder = createFolder(files, "o", "O");
        filesAlpha.put('o', folder);
        folder = createFolder(files, "p", "P");
        filesAlpha.put('p', folder);
        folder = createFolder(files, "q", "Q");
        filesAlpha.put('q', folder);
        folder = createFolder(files, "r", "R");
        filesAlpha.put('r', folder);
        folder = createFolder(files, "s", "S");
        filesAlpha.put('s', folder);
        folder = createFolder(files, "t", "T");
        filesAlpha.put('t', folder);
        folder = createFolder(files, "u", "U");
        filesAlpha.put('u', folder);
        folder = createFolder(files, "v", "V");
        filesAlpha.put('v', folder);
        folder = createFolder(files, "w", "W");
        filesAlpha.put('w', folder);
        folder = createFolder(files, "x", "X");
        filesAlpha.put('x', folder);
        folder = createFolder(files, "y", "Y");
        filesAlpha.put('y', folder);
        folder = createFolder(files, "z", "Z");
        filesAlpha.put('z', folder);

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
            publicationTerms = checkYearTerms(publicationsTerm,
                                              getPublicationYears());

            System.out.println("Terms for working papers...");
            String workingPapersTermPath =
                   (String) config.get("terms.workingpapers");
            workingPapersTerm = checkTermPath(workingPapersTermPath);
            workingPaperTerms = checkYearTerms(workingPapersTerm,
                                               getWorkingPaperYears());

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

            result =
            stmt.executeQuery(
                    "SELECT person.Person_Id, Anrede, Vorname, Name, Angaben "
                    + "FROM person "
                    + "WHERE Eigenschaft  = 'Aktiv' OR Eigenschaft = 'Ehemalig' "
                    + "ORDER BY Name, Vorname");
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

            result = stmt.executeQuery(
                    "SELECT DISTINCT person.Person_Id, Anrede, Vorname, Name, Angaben "
                    + "FROM person JOIN projektlink on person.Person_Id = projektlink.Person_Id "
                    + "WHERE Eigenschaft = 'Autor' OR Eigenschaft = 'Sonstiges' "
                    + "ORDER BY Name, Vorname");
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

            result = stmt.executeQuery(
                    "SELECT DISTINCT person.Person_Id, Anrede, Vorname, Name, Angaben "
                    + "FROM person "
                    + "WHERE Eigenschaft  = 'Autor' AND NOT EXISTS (SELECT * FROM projektlink where projektlink.Person_Id = person.Person_Id)"
                    + "ORDER BY Name, Vorname");
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

            result =
            stmt.executeQuery(
                    "SELECT DISTINCT abteilunglink.Auftrag, person.Person_Id, person.Eigenschaft, abteilunglink.Auftrag "
                    + "FROM abteilunglink JOIN person ON abteilunglink.Person_Id = person.Person_Id "
                    + "WHERE abteilunglink.Abteilung_Id = 11 AND person.Eigenschaft = 'Aktiv'");
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

            result =
            stmt.executeQuery(
                    "SELECT DISTINCT abteilunglink.Auftrag, person.Person_Id, person.Eigenschaft, abteilunglink.Auftrag "
                    + "FROM abteilunglink JOIN person ON abteilunglink.Person_Id = person.Person_Id "
                    + "WHERE abteilunglink.Abteilung_Id = 11 AND person.Eigenschaft = 'Ehemalig'");
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
                        "SELECT Name, Beschreibung, Finanzierung, Abteilung_Id, Beginn, Ende "
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
                }

                result = stmt.executeQuery(String.format(
                        "SELECT Name, Beschreibung, Finanzierung, Abteilung_Id, Beginn, Ende "
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

            result = stmt.executeQuery("SELECT Verlag FROM publikation "
                                       + "WHERE Typ = 'Monographie' OR Typ = 'Sammelband' OR Typ = 'Artikel / Aufsatz' "
                                       + "GROUP BY Verlag");
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

            result = stmt.executeQuery(
                    "SELECT Publikation_Id, Name, Verlag, Jahr, Link, Beschreibung, Abteilung_Id, Sichtbarkeit, ErschienenIn "
                    + "FROM publikation "
                    + "WHERE Typ = 'Monographie' "
                    + "ORDER BY Name");
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

            result = stmt.executeQuery(
                    "SELECT Publikation_Id, Name, Verlag, Jahr, Link, Beschreibung, Abteilung_Id, Sichtbarkeit "
                    + "FROM publikation "
                    + "WHERE (Typ = 'Sammelband' AND (ErschienenIn IS NULL OR CHAR_LENGTH(ErschienenIn) = 0)) "
                    + "ORDER BY Name");
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

            result = stmt.executeQuery(
                    "SELECT Publikation_Id, Name, Verlag, Jahr, Link, Beschreibung, Abteilung_Id, Sichtbarkeit, ErschienenIn "
                    + "FROM publikation "
                    + "WHERE (Typ = 'Sammelband' AND ErschienenIn IS NOT NULL AND CHAR_LENGTH(ErschienenIn) > 0) "
                    + "OR (Typ = 'Monograph' AND ErschienenIn IS NOT NULL AND CHAR_LENGTH(ErschienenIn) > 0) "
                    + "OR (Typ = 'Artikel / Aufsatz' AND SUBSTRING(ErschienenIn, 1, 2) = 'in') "
                    + "ORDER BY Name");
            result.last();
            number = result.getRow();
            result.beforeFirst();

            while (result.next()) {
                System.out.printf("%4d of %4d: %s...\n", counter, number, result.
                        getString("name"));
                PublicationData data = new PublicationData();
                data.setType(PublicationType.ARTICLE_IN_COLLECTED_VOLUME);
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

            result = stmt.executeQuery(
                    "SELECT Publikation_Id, Name, Verlag, Jahr, Link, Beschreibung, Abteilung_Id, Sichtbarkeit, ErschienenIn "
                    + "FROM publikation "
                    + "WHERE (Typ = 'Artikel / Aufsatz' AND SUBSTRING(ErschienenIn, 1, 2) <> 'in') "
                    + "ORDER BY Name");
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

            result = stmt.executeQuery(
                    "SELECT Publikation_Id, Name, Verlag, Jahr, Link, Beschreibung, Abteilung_Id, Sichtbarkeit, ErschienenIn "
                    + "FROM publikation "
                    + "WHERE Typ = 'Sonstiges' "
                    + "ORDER BY Name");
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

            result = stmt.executeQuery("SELECT DISTINCT Arbeitspapier_Id FROM arbeitspapier "
                                       + "ORDER BY Jahr, Name");
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
                personDe.setContentSection(section);
                personDe.setLifecycle(createLifecycle());
                personDe.save();
                personDe.setLanguage("de");

                personEn.setSurname(personData.getSurname());
                personEn.setTitlePre(personData.getTitlePre());
                personEn.setGivenName(personData.getGivenname());
                personEn.setContentSection(section);
                personEn.setLifecycle(createLifecycle());
                personEn.save();
                personEn.setLanguage("en");

                ContentBundle person;
                person = new ContentBundle(personDe);
                person.addInstance(personEn);
                person.setDefaultLanguage("de");
                person.setContentSection(section);
                person.setLifecycle(createLifecycle());
                person.save();

                personDe.setContentSection(section);
                personEn.setContentSection(section);

                //folder.addItem(person);
                char letter;
                letter = personData.getSurname().toLowerCase().charAt(0);
                Map<Character, Folder> folders = null;
                switch (type) {
                    case MEMBER:
                        folders = membersAlpha;
                        break;
                    case AUTHOR:
                        folders = authorsAlpha;
                        break;
                    case OTHER:
                        folders = personsAlpha;
                        break;
                }

                insertIntoAZFolder(person, letter, folders);

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
                contactEn.setPerson(personDe, "commonContact");
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

                    contactDe.setContentSection(section);
                    contactDe.setLifecycle(createLifecycle());
                    contactDe.save();
                    contactEn.setContentSection(section);
                    contactEn.setLifecycle(createLifecycle());
                    contactEn.save();
                    ContentBundle contactBundle = new ContentBundle(contactDe);
                    contactBundle.addInstance(contactEn);
                    contactBundle.setContentSection(section);
                    //contacts.addItem(contactBundle);
                    insertIntoAZFolder(contactBundle,
                                       personDe.getSurname().charAt(0),
                                       contactsAlpha);

                    contactDe.setContentSection(section);
                    contactEn.setContentSection(section);


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
                departmentDe.setName(departmentData.getNameDe().
                        replace(",", "").
                        replace("/", "").
                        replaceAll("\\s\\s+", " ").
                        replace(' ', '-').toLowerCase());
                departmentDe.setLanguage("de");
                departmentDe.setLifecycle(createLifecycle());
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
                departmentEn.setLifecycle(createLifecycle());
                departmentEn.setContentSection(section);
                departmentEn.save();
                System.out.println("OK");

                department = new ContentBundle(departmentDe);
                department.addInstance(departmentEn);
                department.setContentSection(section);
                department.setDefaultLanguage("de");
                department.setLifecycle(createLifecycle());
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
                    if (projectData.getBegin() != null) {
                        projectDe.setBegin(projectData.getBegin().getTime());
                    }
                    if (projectData.getEnd() != null) {
                        projectDe.setEnd(projectData.getEnd().getTime());
                    }
                    projectDe.setLanguage("de");
                    projectDe.setLifecycle(createLifecycle());
                    projectDe.setContentSection(section);
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
                    if (projectData.getBegin() != null) {
                        projectEn.setBegin(projectData.getBegin().getTime());
                    }
                    if (projectData.getEnd() != null) {
                        projectEn.setEnd(projectData.getEnd().getTime());
                    }
                    projectEn.setLanguage("en");
                    projectEn.setLifecycle(createLifecycle());
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
                project.setLifecycle(createLifecycle());
                project.setContentSection(section);
                project.setDefaultLanguage("de");

                if (projectDe != null) {
                    projectDe.setContentSection(section);
                }

                if (projectEn != null) {
                    projectEn.setContentSection(section);
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

                insertIntoAZFolder(project, projectsAlpha);

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
                            link.setTitle(publicationData.getLink());
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
                            link.setTitle(publicationData.getLink());
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
                            link.setTitle(publicationData.getLink());
                            link.setTargetType(Link.EXTERNAL_LINK);
                            link.setTargetURI(publicationData.getLink());
                            link.setLinkOwner(collectedVolumeDe);
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
                            link.setTitle(publicationData.getLink());
                            link.setTargetType(Link.EXTERNAL_LINK);
                            link.setTargetURI(publicationData.getLink());
                            link.setLinkOwner(collectedVolumeEn);
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
                            link.setTitle(publicationData.getLink());
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
                            link.setTitle(publicationData.getLink());
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
                            if (publicationData.getLink().length() < 200) {
                                link.setTitle(publicationData.getLink());
                            } else {
                                System.out.println(
                                        "\t***WARNING: Link in publication is too long for title. Truncating.");
                                link.setTitle(publicationData.getLink().
                                        substring(0, 200));
                            }
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
                            if (publicationData.getLink().length() < 200) {
                                link.setTitle(publicationData.getLink());
                            } else {
                                System.out.println(
                                        "\t***WARNING: Link in publication is too long for title. Truncating.");
                                link.setTitle(publicationData.getLink().
                                        substring(0, 200));
                            }
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
                            link.setTitle(publicationData.getLink());
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
                            link.setTitle(publicationData.getLink());
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

                publicationDe.setLifecycle(createLifecycle());
                publicationDe.setContentSection(section);
                publicationDe.setLanguage("de");
                publicationEn.setLanguage("en");
                publicationEn.setLifecycle(createLifecycle());
                publicationEn.setContentSection(section);

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
                                "\t***WARNING: Publication title is too long for link title. Trancating.");
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
                }

                if (publicationDe == null) {
                    publication = new ContentBundle(publicationEn);
                    publication.setDefaultLanguage("en");
                } else {
                    publication = new ContentBundle(publicationDe);
                    publication.setDefaultLanguage("de");
                }
                publication.setLifecycle(createLifecycle());
                publication.setContentSection(section);

                publicationDe.setContentSection(section);
                publicationEn.setContentSection(section);

                if ((publicationData.getAbteilungId() != null)
                    && !publicationData.getAbteilungId().isEmpty()
                    && departmentsMap.containsKey(
                        publicationData.getAbteilungId())) {
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

                insertIntoAZFolder(publication, publicationsAlpha);
                Term term = publicationTerms.get(Integer.toString(((Publication) publication.
                                                                   getPrimaryInstance()).
                        getYearOfPublication()));
                if (term == null) {
                    term = publicationsTerm;
                }
                term = termsDomain.getTerm(term.getUniqueID());
                System.out.printf("\tAdding publication to term '%s:%s'...\n", term.
                        getUniqueID(), term.getName());
                term.addObject(publication);
                term.save();
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
                    String workingPaperNameDe = workingPaperData.getTitleDe().
                            replace(",", "").
                            replace("/", "").
                            replaceAll("\\s\\s+", " ").
                            replace(' ', '-').toLowerCase();
                    if (workingPaperNameDe.length() > 200) {
                        workingPaperNameDe =
                        workingPaperNameDe.substring(0, 200);
                    }
                    workingPaperDe.setName(workingPaperNameDe);
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
                    workingPaperDe.setLifecycle(createLifecycle());
                    workingPaperDe.setContentSection(section);
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
                    String workingPaperNameEn = workingPaperData.getTitleEn().
                            replace(",", "").
                            replace("/", "").
                            replaceAll("\\s\\s+", " ").
                            replace(' ', '-').toLowerCase();
                    if (workingPaperNameEn.length() > 200) {
                        workingPaperNameEn =
                        workingPaperNameEn.substring(0, 200);
                    }
                    workingPaperEn.setName(workingPaperNameEn);
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
                    workingPaperEn.setLanguage("En");
                    workingPaperEn.setLifecycle(createLifecycle());
                    workingPaperEn.setContentSection(section);
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
                workingPaper.setLifecycle(createLifecycle());
                workingPaper.setContentSection(section);

                workingPaperDe.setContentSection(section);
                workingPaperEn.setContentSection(section);

                workingPaperMap.put(workingPaperData.getDabinId(), workingPaper);
                insertIntoAZFolder(workingPaper, publicationsAlpha);
                WorkingPaper primary = (WorkingPaper) workingPaper.
                        getPrimaryInstance();
                String yearStr =
                       Integer.toString(primary.getYearOfPublication());
                Term term = workingPaperTerms.get(yearStr);
                if (term == null) {
                    System.out.printf(
                            "***WARNING: Term for year '%s' not found. Using basic term.",
                            yearStr);
                    term = workingPapersTerm;
                }
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
                            String title = String.format("Datei %s",
                                                         ((WorkingPaper) workingPaper.
                                                          getPrimaryInstance()).
                                    getTitle());
                            if (title.length() > 200) {
                                fsi.setTitle(title.substring(0, 200));
                            } else {
                                fsi.setTitle(title);
                            }

                            String name = String.format("datei_%s.pdf",
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
                            file.setContentSection(section);
                            file.setLifecycle(createLifecycle());
                            fsi.setLifecycle(createLifecycle());
                            fsi.setContentSection(section);
                            fsi.save();

                            fsi.setLanguage("de");
                            ContentBundle bundle = new ContentBundle(fsi);
                            bundle.setLifecycle(createLifecycle());
                            bundle.setContentSection(section);
                            bundle.setDefaultLanguage("de");
                            //bundle.save();

                            RelatedLink download = new RelatedLink();
                            download.setTitle("Download");
                            download.setTargetType(Link.INTERNAL_LINK);
                            download.setTargetItem(fsi);
                            download.setLinkOwner(workingPaperDe);

                            download = new RelatedLink();
                            download.setTitle("Download");
                            download.setTargetType(Link.INTERNAL_LINK);
                            download.setTargetItem(fsi);
                            download.setLinkOwner(workingPaperEn);

                            char letter = workingPaperDe.getName().toLowerCase().
                                    charAt(0);
                            insertIntoAZFolder(bundle, letter, filesAlpha);
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
                    "Publisher '%s: %s' was already exists. Skiping.\n",
                    publisherData.getPlace(),
                    publisherData.getName());
            return;
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
                publisherDe.setTitle(publisherData.getName());
                publisherDe.setName(publisherData.getName().toLowerCase());
                publisherDe.setPlace(publisherData.getPlace());
                publisherDe.setLanguage("de");
                publisherDe.setLifecycle(createLifecycle());
                publisherDe.setContentSection(section);
                publisherDe.save();
                System.out.println("OK");

                System.out.printf("\tEn: %s, %s...", publisherData.getName(), publisherData.
                        getPlace());
                publisherEn = new Publisher();
                publisherEn.setTitle(publisherData.getName());
                publisherEn.setName(publisherData.getName().toLowerCase());
                publisherEn.setPlace(publisherData.getPlace());
                publisherEn.setLanguage("en");
                publisherEn.setLifecycle(createLifecycle());
                publisherEn.setContentSection(section);
                publisherEn.save();
                System.out.println("OK");

                publisher = new ContentBundle(publisherDe);
                publisher.addInstance(publisherEn);
                publisher.setDefaultLanguage("de");
                publisher.setLifecycle(createLifecycle());
                publisher.setContentSection(section);

                publisherDe.setContentSection(section);
                publisherEn.setContentSection(section);

                insertIntoAZFolder(publisher, publishersAlpha);
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
            publisher.setName(normalizedData);
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

            publisher.setName(name.trim());
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

    private void insertIntoAZFolder(final ContentBundle bundle,
                                    final Map<Character, Folder> folders) {
        Character letter;

        letter = bundle.getPrimaryInstance().getName().toLowerCase().charAt(0);

        insertIntoAZFolder(bundle, letter, folders);
    }

    private void insertIntoAZFolder(final ContentBundle bundle,
                                    final Character letter,
                                    final Map<Character, Folder> folders) {
        Folder folder;

        folder = folders.get(letter);
        if (folder == null) {
            folder = folders.get('0');
        }

        folder.addItem(bundle);
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

    private Map<String, Term> checkYearTerms(final Term parent,
                                             final List<String> years) {
        String parentId = parent.getUniqueID();
        Map<String, Term> yearTerms = new HashMap<String, Term>();

        for (String year : years) {
            String yearTermId = String.format("%s%s", parentId, year);
            Term term;
            try {
                term = termsDomain.getTerm(yearTermId);
            } catch (DataObjectNotFoundException ex) {
                System.out.printf(
                        "Term for year '%s' in term '%s' does not exist. Creating.\n",
                        year,
                        parent.getName());
                createTerm(yearTermId, year, termsDomain, parent);
                term = termsDomain.getTerm(yearTermId);
            }

            yearTerms.put(year, term);
        }

        return yearTerms;
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

    private List<String> getPublicationYears() {
        List<String> years = new ArrayList<String>();

        try {
            Statement stmt = connection.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);

            ResultSet result = stmt.executeQuery(
                    "SELECT Jahr FROM publikation GROUP BY Jahr");

            while (result.next()) {
                String year = result.getString(1);
                if (year.length() > 4) {
                    years.add(year.substring(0, 4));
                } else {
                    years.add(year);
                }
            }
        } catch (SQLException ex) {
            System.err.println("Query for publication years failed.");
            ex.printStackTrace(System.err);
        }

        return years;
    }

    private List<String> getWorkingPaperYears() {
        List<String> years = new ArrayList<String>();

        try {
            Statement stmt = connection.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);

            ResultSet result = stmt.executeQuery(
                    "SELECT Jahr FROM arbeitspapier GROUP BY Jahr");

            while (result.next()) {
                while (result.next()) {
                    String year = result.getString(1);
                    if (year.length() > 4) {
                        years.add(year.substring(0, 4));
                    } else {
                        years.add(year);
                    }
                }
            }
        } catch (SQLException ex) {
            System.err.println(
                    "Query for publication years of working papers failed.");
            ex.printStackTrace(System.err);
        }

        return years;
    }

    private Lifecycle createLifecycle() {
        Lifecycle lifecycle;
        Calendar calendarNow = new GregorianCalendar();
        Date now = calendarNow.getTime();
        lifecycle = this.lifecycle.createLifecycle();
        lifecycle.setStartDate(now);

        return lifecycle;
    }

    public static void main(String[] args) {
        new DaBInImporter().run(args);
    }
}
