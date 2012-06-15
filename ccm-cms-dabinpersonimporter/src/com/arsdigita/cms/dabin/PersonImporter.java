package com.arsdigita.cms.dabin;

import com.arsdigita.categorization.Category;
import com.arsdigita.cms.ContentBundle;
import com.arsdigita.cms.ContentPage;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.Folder;
import com.arsdigita.cms.contentassets.RelatedLink;
import com.arsdigita.cms.contenttypes.Address;
import com.arsdigita.cms.contenttypes.Contact;
import com.arsdigita.cms.contenttypes.GenericContactBundle;
import com.arsdigita.cms.contenttypes.GenericContactEntry;
import com.arsdigita.cms.contenttypes.GenericPerson;
import com.arsdigita.cms.contenttypes.GenericPersonBundle;
import com.arsdigita.cms.contenttypes.Link;
import com.arsdigita.cms.contenttypes.SciMember;
import com.arsdigita.cms.lifecycle.Lifecycle;
import com.arsdigita.cms.lifecycle.LifecycleDefinition;
import com.arsdigita.cms.lifecycle.LifecycleDefinitionCollection;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.TransactionContext;
import com.arsdigita.util.cmd.Program;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import org.apache.commons.cli.CommandLine;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class PersonImporter extends Program {

    private Properties config;
    private Connection connection = null;
    private ContentSection membersSection;
    private ContentSection membersContactsSection;
    private ContentSection authorsSection;
    private ContentSection authorsContactsSection;
    private ContentSection personsSection;
    private ContentSection personsContactsSection;
    private ContentSection addressSection;
    private LifecycleDefinition membersLifecycle;
    private LifecycleDefinition membersContactsLifecycle;
    private LifecycleDefinition authorsLifecycle;
    private LifecycleDefinition authorsContactsLifecycle;
    private LifecycleDefinition personsLifecycle;
    private LifecycleDefinition personsContactsLifecycle;
    private LifecycleDefinition addressLifecycle;
    private Folder membersFolder;
    private Folder membersContactsFolder;
    private Folder authorsFolder;
    private Folder authorsContactsFolder;
    private Folder personsFolder;
    private Folder personsContactsFolder;
    private Folder addressFolder;
    private Category membersActiveCategory;
    private Category membersFormerCategory;
    //private Category membersAssociatedCategory;
    //Department key -> category
    private final Map<String, Category> membersActiveDepartmentCategories =
                                        new TreeMap<String, Category>();
    private final Map<String, Category> membersFormerDepartmentCategories =
                                        new TreeMap<String, Category>();
    private final Map<String, Category> membersAssociatedDepartmentCategories =
                                        new TreeMap<String, Category>();

    public PersonImporter() {
        this(true);
    }

    public PersonImporter(final boolean startup) {
        super("PersonImporter",
              "0.1.0",
              "configFile",
              startup);

    }

    public static void main(final String[] args) {
        new PersonImporter().run(args);
    }

    @Override
    protected void doRun(final CommandLine cmdLine) {
        final String args[];
        String mySqlHost;
        String mySqlUser;
        String mySqlPassword;
        String mySqlDb;

        System.out.println("");
        System.out.println("");
        System.out.println("PersonImporter is starting...");

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

        mySqlHost = config.getProperty("mysql.host", "localhost").trim();
        mySqlUser = config.getProperty("mysql.user").trim();
        mySqlPassword = config.getProperty("mysql.password").trim();
        mySqlDb = config.getProperty("mysql.db").trim();

        membersSection = getContentSection(config.getProperty(
                "members.contentsection"));
        membersContactsSection = getContentSection(config.getProperty(
                "members.contacts.contentsection"));
        authorsSection = getContentSection(config.getProperty(
                "members.contentsection"));
        authorsContactsSection = getContentSection(config.getProperty(
                "members.contacts.contentsection"));
        personsSection = getContentSection(config.getProperty(
                "members.contentsection"));
        personsContactsSection = getContentSection(config.getProperty(
                "members.contacts.contentsection"));
        addressSection = getContentSection((config.getProperty(
                                            "address.contentsection")));

        LifecycleDefinitionCollection lifecycles = membersSection.getLifecycleDefinitions();
        while (lifecycles.next()) {
            membersLifecycle = lifecycles.getLifecycleDefinition();
        }
        lifecycles = membersContactsSection.getLifecycleDefinitions();
        while (lifecycles.next()) {
            membersContactsLifecycle = lifecycles.getLifecycleDefinition();
        }

        lifecycles = authorsSection.getLifecycleDefinitions();
        while (lifecycles.next()) {
            authorsLifecycle = lifecycles.getLifecycleDefinition();
        }
        lifecycles = authorsContactsSection.getLifecycleDefinitions();
        while (lifecycles.next()) {
            authorsContactsLifecycle = lifecycles.getLifecycleDefinition();
        }

        lifecycles = personsSection.getLifecycleDefinitions();
        while (lifecycles.next()) {
            personsLifecycle = lifecycles.getLifecycleDefinition();
        }
        lifecycles = personsContactsSection.getLifecycleDefinitions();
        while (lifecycles.next()) {
            personsContactsLifecycle = lifecycles.getLifecycleDefinition();
        }

        final String membersFolderPath = config.getProperty("members.folder");
        final String membersContactsFolderPath = config.getProperty(
                "members.contacts.folder");
        final String authorsFolderPath = config.getProperty("authors.folder");
        final String authorsContactsFolderPath = config.getProperty(
                "authors.contacts.folder");
        final String personsFolderPath = config.getProperty("persons.folder");
        final String personsContactsFolderPath = config.getProperty(
                "persons.contacts.folder");
        final String addressFolderPath = config.getProperty("address.folder");

        membersFolder = getOrCreateFolder(membersSection, membersFolderPath);
        membersContactsFolder = getOrCreateFolder(membersContactsSection,
                                                  membersContactsFolderPath);
        authorsFolder = getOrCreateFolder(authorsSection, authorsFolderPath);
        authorsContactsFolder = getOrCreateFolder(authorsContactsSection,
                                                  authorsContactsFolderPath);
        personsFolder = getOrCreateFolder(personsSection, personsFolderPath);
        personsContactsFolder = getOrCreateFolder(personsContactsSection,
                                                  personsContactsFolderPath);
        addressFolder = getOrCreateFolder(addressSection, addressFolderPath);

        membersActiveCategory = new Category(new BigDecimal(config.getProperty(
                "members.active.category")));
        membersFormerCategory = new Category(new BigDecimal(config.getProperty(
                "members.former.category")));
//        membersAssociatedCategory = new Category(new BigDecimal(config.
//                getProperty(
//                "members.associated.category")));

        //Get department ids from DaBIn DB
        //Create connection to the DaBIn MySQL database
        System.out.println("Trying to connect to DaBIn MySQL database with these "
                           + "parameters:");
        System.out.printf("Host     = '%s'\n", mySqlHost);
        System.out.printf("User     = '%s'\n", mySqlUser);
        System.out.printf("Password = '%s'\n", mySqlPassword);
        System.out.printf("Database = '%s'\n", mySqlDb);
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

        System.out.println("Loading departments from DaBIn database...");
        try {
            final Statement stmt = connection.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);

            final ResultSet result =
                            stmt.executeQuery("SELECT Abteilung_Id, Name "
                                              + "FROM abteilung "
                                              + "GROUP BY Abteilung_Id "
                                              + "ORDER BY Abteilung_Id ");
            result.last();
            final long number = result.getRow();
            result.beforeFirst();

            System.out.printf("Found %d departments.", number);
            String departmentId;
            String departmentName;
            String membersActiveCatId;
            String membersFormerCatId;
            String membersAssociatedCatId;
            Category membersDepartmentActive;
            Category membersDepartmentFormer;
            Category membersDepartmentAssociated;
            while (result.next()) {
                departmentId = result.getString("abteilung.Abteilung_Id");
                departmentName = result.getString("abteilung.Name");

                membersActiveCatId = config.getProperty(String.format(
                        "members.%s.active.category", departmentId));
                if (membersActiveCatId == null) {
                    membersDepartmentActive = null;
                } else {
                    membersDepartmentActive = new Category(new BigDecimal(
                            membersActiveCatId));
                }

                membersFormerCatId = config.getProperty(String.format(
                        "members.%s.active.category", departmentId));
                if (membersFormerCatId == null) {
                    membersDepartmentFormer = null;
                } else {
                    membersDepartmentFormer = new Category(new BigDecimal(
                            membersFormerCatId));
                }

                membersAssociatedCatId = config.getProperty(String.format(
                        "members.%s.active.category", departmentId));
                if (membersAssociatedCatId == null) {
                    membersDepartmentAssociated = null;
                } else {
                    membersDepartmentAssociated = new Category(new BigDecimal(
                            membersAssociatedCatId));
                }

                membersActiveDepartmentCategories.put(
                        departmentId, membersDepartmentActive);
                membersFormerDepartmentCategories.put(
                        departmentId, membersDepartmentFormer);
                membersAssociatedDepartmentCategories.put(
                        departmentId, membersDepartmentAssociated);
            }
        } catch (SQLException ex) {
            System.err.println("Failed to load departments.");
            ex.printStackTrace(System.err);
            return;
        }

        System.out.println("Configuration values:");
        System.out.println("---------------------");
        System.out.printf("membersSection         = %s\n", membersSection.getName());
        System.out.printf("membersContactsSection = %s\n",
                          membersContactsSection.getName());
        System.out.printf("authorsSection         = %s\n", authorsSection.getName());
        System.out.printf("authorsContactsSection = %s\n",
                          authorsContactsSection.getName());
        System.out.printf("personsSection         = %s\n", personsSection.getName());
        System.out.printf("personsContactsSection = %s\n",
                          personsContactsSection.getName());
        System.out.println("");
        System.out.printf("membersFolder         = %s\n",
                          membersFolder.getPath());
        System.out.printf("membersContactsFolder = %s\n",
                          membersFolder.getPath());
        System.out.printf("authorsFolder         = %s\n",
                          authorsFolder.getPath());
        System.out.printf("authorsContactsFolder = %s\n",
                          authorsFolder.getPath());
        System.out.printf("personsFolder         = %s\n",
                          personsFolder.getPath());
        System.out.printf("personsContactsFolder = %s\n",
                          personsFolder.getPath());
        System.out.printf("addressFolder         = %s\n",
                          addressFolder.getPath());
        System.out.println("");
        System.out.printf("membersActiveCategory = %s\n",
                          membersActiveCategory.getName());
        System.out.printf("membersFormerCategory = %s\n",
                          membersFormerCategory.getName());
//        System.out.printf("membersAssociatedCategory = %s\n",
//                          membersAssociatedCategory.getName());

        System.out.println(
                "Categories for active members in the departments "
                + "(departmentId -> Category):");
        for (Map.Entry<String, Category> entry :
             membersActiveDepartmentCategories.entrySet()) {
            if (entry.getValue() == null) {
                System.out.printf("%s -> null\n",
                                  entry.getKey());
            } else {
                System.out.printf("%s -> %s\n",
                                  entry.getKey(),
                                  entry.getValue().getName());
            }
        }

        System.out.println(
                "Categories for former members in the departments "
                + "(departmentId -> Category):");
        for (Map.Entry<String, Category> entry :
             membersFormerDepartmentCategories.entrySet()) {
            if (entry.getValue() == null) {
                System.out.printf("%s -> null\n",
                                  entry.getKey());
            } else {
                System.out.printf("%s -> %s\n",
                                  entry.getKey(),
                                  entry.getValue().getName());
            }
        }

        System.out.println(
                "Categories for associated members in the departments "
                + "(departmentId -> Category):");
        for (Map.Entry<String, Category> entry :
             membersAssociatedDepartmentCategories.entrySet()) {
            if (entry.getValue() == null) {
                System.out.printf("%s -> null\n",
                                  entry.getKey());
            } else {
                System.out.printf("%s -> %s\n",
                                  entry.getKey(),
                                  entry.getValue().getName());
            }
        }

        try {
            Statement stmt = connection.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);

            final ResultSet result =
                            stmt.executeQuery(
                    "SELECT person.Person_Id, Anrede, Name, Vorname, "
                    + "Anstellung, Eigenschaft, Angaben, Abteilung_Id "
                    + "FROM person "
                    + "JOIN abteilunglink "
                    + "ON person.Person_Id = abteilunglink.Person_Id "
                    + "WHERE Eigenschaft = 'Aktiv' OR Eigenschaft = 'Ehemalig' "
                    + "ORDER BY person.Name");

            result.last();
            final long number = result.getRow();
            result.beforeFirst();

            System.out.printf("Found %d persons in the DaBIn database.\n",
                              number);

            int i = 1;

            final TransactionContext tctx = SessionManager.getSession().
                    getTransactionContext();

            tctx.beginTxn();

            System.out.println("Creating address...");
            final Address address = new Address();
            address.setName(config.getProperty("address.name"));
            address.setTitle(config.getProperty("address.title"));
            address.setAddress(config.getProperty("address.data"));
            address.setPostalCode(config.getProperty("address.code"));
            address.setCity(config.getProperty("address.city"));
            address.setIsoCountryCode(config.getProperty("address.country"));
            address.setLanguage("de");
            address.save();

            final ContentBundle addressBundle = new ContentBundle(address);
            addressBundle.setDefaultLanguage("de");

            address.setContentSection(addressSection);
            addressBundle.setContentSection(addressSection);
            addressBundle.save();
            addressFolder.addItem(addressBundle);            

            try {
                while (result.next()) {
                    System.out.printf("Processing person '%d' of '%d':\n", i,
                                      number);
                    System.out.printf("\tPerson_Id = %s\n",
                                      result.getString("person.Person_Id"));
                    System.out.printf("\tAnrede = %s\n",
                                      result.getString("person.Anrede"));
                    System.out.printf("\tName = %s\n",
                                      result.getString("person.Name"));
                    System.out.printf("\tVorname = %s\n",
                                      result.getString("person.Vorname"));
                    System.out.printf("\tAnstellung = %s\n",
                                      result.getString("person.Anstellung"));
                    System.out.printf("\tEigenschaft = %s\n",
                                      result.getString("person.Eigenschaft"));
                    System.out.printf("\tAngaben = %s\n",
                                      result.getString("person.Angaben"));
                    System.out.printf("\tAbteilung_Id = %s\n",
                                      result.getString(
                            "abteilunglink.Abteilung_Id"));
                    System.out.println("");

                    GenericPerson person = null;
                    ContentSection section = null;
                    ContentSection contactsSection = null;
                    Folder folder = null;
                    Folder contactsFolder = null;
                    LifecycleDefinition lifecycleDefinition = null;
                    LifecycleDefinition contactLifecycleDefinition = null;
                    Category category = null;
                    System.out.println("Eigenschaft...");
                    if ("Aktiv".equals(result.getString("person.Eigenschaft"))) {
                        person = new SciMember();
                        section = membersSection;
                        folder = membersFolder;
                        contactsSection = membersContactsSection;
                        contactsFolder = membersContactsFolder;
                        category = membersActiveCategory;
                        lifecycleDefinition = membersLifecycle;
                        contactLifecycleDefinition = membersContactsLifecycle;
                        category = membersActiveCategory;
                    } else if ("Ehemalig".equals(result.getString(
                            "person.Eigenschaft"))) {
                        person = new SciMember();
                        section = membersSection;
                        folder = membersFolder;
                        contactsSection = membersContactsSection;
                        contactsFolder = membersContactsFolder;
                        category = membersFormerCategory;
                        lifecycleDefinition = membersLifecycle;
                        contactLifecycleDefinition = membersContactsLifecycle;
                        category = membersFormerCategory;
                    }
//                    } else if ("Assoziert".equals(result.getString(
//                            "person.Eigenschaft"))) {
//                        person = new SciMember();
//                        section = membersSection;
//                        folder = membersFolder;
//                        contactsSection = membersContactsSection;
//                        contactsFolder = membersContactsFolder;
//                        category = membersAssociatedCategory;
//                        lifecycleDefinition = membersLifecycle;
//                        category = membersAssociatedCategory;
//                        contactLifecycleDefinition = membersContactsLifecycle;
//                    } 

                    System.out.println("Person basic data...");
                    person.setSurname(result.getString("person.Name"));
                    person.setGivenName(result.getString("person.Vorname"));
                    person.setTitlePre(result.getString("person.Anrede"));
                    //person.setDabinId(result.getInt("person.Person_Id"));
                    person.setLanguage("de");
                    person.setContentSection(section);
                    person.setLifecycle(createLifecycle(lifecycleDefinition));
                    person.save();

                    resolveDuplicateNameAndTitle(person, folder);

                    GenericPersonBundle personBundle = new GenericPersonBundle(person);
                    personBundle.setDefaultLanguage("de");
                    personBundle.setContentSection(section);
                    folder.addItem(personBundle);
                    personBundle.setLifecycle(createLifecycle(
                            lifecycleDefinition));
                    personBundle.save();


                    if (category != null) {
                        category.addChild(personBundle);
                    }

                    System.out.println("Department assoc...");
                    if ("Aktiv".equals(result.getString("person.Eigenschaft"))
                        || "Ehemalig".equals(result.getString(
                            "person.Eigenschaft"))
                        || "Assoziert".equals(result.getString(
                            "person.Eigenschaft"))) {
                        if (!result.getString("abteilunglink.Abteilung_Id").
                                isEmpty()) {
                            Category depCat = null;

                            if ("Aktiv".equals(result.getString(
                                    "person.Eigenschaft"))) {
                                depCat =
                                membersActiveDepartmentCategories.get(result.getString("abteilunglink.Abteilung_Id"));
                            } else if ("Ehemalig".equals(result.getString(
                                    "person.Eigenschaft"))) {
                                depCat =
                                membersFormerDepartmentCategories.get(result.getString("abteilunglink.Abteilung_Id"));
                            } else if ("Assoziert".equals(result.getString(
                                    "person.Eigenschaft"))) {
                                depCat = membersAssociatedDepartmentCategories.get(result.getString(
                                        "abteilunglink.Abteilung_Id"));
                            }

                            if (depCat != null) {
                                depCat.addChild(personBundle);
                            }
                        }
                    }
                    
                    System.out.println("Contact...");                    
                    if (!result.getString("person.Angaben").isEmpty()) {
                        System.out.println("Creating contact...");
                        Contact contact = new Contact();                        
                        System.out.println("Setting language...");
                        contact.setLanguage("de");
                        System.out.println("Setting name...");                        
                        contact.setName(String.format("%s-kontakt", person.getName()));                                                                        
                        System.out.println("setting title...");
                        contact.setTitle(String.format("%s Kontakt", person.getTitle()));
                        
                        System.out.println("Creating contact bundle...");
                        contact.setContentSection(contactsSection);
                        contact.setLifecycle(createLifecycle(
                                contactLifecycleDefinition));
                        GenericContactBundle contactBundle = new GenericContactBundle(contact);
                        
                        contactBundle.setDefaultLanguage("de");
                        contactBundle.setContentSection(contactsSection);
                        contactsFolder.addItem(contactBundle);
                        contactBundle.save();
                        
                        System.out.printf("Setting person to %s...\n", person.getContentBundle().getName());                        
                        contact.setPerson(person, "commonContact");
                        System.err.println("Done with contact base data.");

                        System.err.println("Spliting 'Angaben'...");
                        final String[] contactData =
                                       result.getString("person.Angaben").split(
                                "\n");
                        String homepage = null;
                        for (String token : contactData) {
                            String key;
                            String value;

                            if (token.indexOf("=") < 0) {
                                System.err.printf("Warning: Invalid contact entry: '%s'"
                                                  + "Skiping.", token);
                                continue;
                            }
                            key = token.substring(0, token.indexOf('=')).trim();
                            value =
                            token.substring(token.indexOf('=') + 1).trim();
                            
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
                        }

                        contact.setAddress(address);                        

                        if (homepage != null) {
                            RelatedLink homepageLink;
                            homepageLink = new RelatedLink();
                            homepageLink.setTitle("Persönliche Homepage");
                            homepageLink.setTargetType(Link.EXTERNAL_LINK);
                            homepageLink.setTargetURI(homepage);
                            homepageLink.setLinkListName("NONE");
                            homepageLink.setLinkOwner(person);
                            homepageLink.save();
                        }
                    }

                    i++;
                }

                tctx.commitTxn();
            } finally {
                if (tctx.inTxn()) {
                    tctx.abortTxn();
                }
            }
        } catch (SQLException ex) {
            System.err.println("Failed to load departments.");
            ex.printStackTrace(System.err);
            return;
        }
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

        final ContentSection _section = (ContentSection) ContentSection.retrieveApplicationForPath(path.toString());

        if (_section == null) {
            throw new DataObjectNotFoundException("Content section not found with path "
                                                  + path);
        }
        return _section;
    }

    private Folder getOrCreateFolder(final ContentSection section,
                                     final String path) {
        final Folder root = section.getRootFolder();
        Folder prev = root;
        Folder current = root;

        final String[] pathTokens = path.split("/");

        for (String token : pathTokens) {
            current = (Folder) current.getItem(token, true);

            if (current == null) {
                current = createFolder(prev, token);
            }

            prev = current;
        }

        return current;
    }

    private Folder createFolder(final Folder parent,
                                final String name) {
        Folder folder;
        System.out.printf("Creating folder '%s/%s'...",
                          parent.getName(),
                          name);

        folder = (Folder) parent.getItem(name, true);

        if (folder == null) {
            final TransactionContext tctx = SessionManager.getSession().
                    getTransactionContext();

            try {
                tctx.beginTxn();

                final Folder newFolder = new Folder();
                newFolder.setName(name);
                newFolder.setLabel(name);
                newFolder.setParent(parent);

                tctx.commitTxn();
            } finally {
                if (tctx.inTxn()) {
                    tctx.abortTxn();
                    System.out.println("FAILED");
                    throw new IllegalStateException("Something is wrong.");
                }
            }

            folder = (Folder) parent.getItem(name, true);
        }

        System.out.println("OK");

        return folder;
    }

    private Lifecycle createLifecycle(final LifecycleDefinition def) {
        Lifecycle lifecycle;
        Calendar calendarNow = new GregorianCalendar();
        Date now = calendarNow.getTime();
        lifecycle = def.createLifecycle();
        lifecycle.setStartDate(now);

        return lifecycle;
    }

    private String normalizeString(final String str) {
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

    private void resolveDuplicateNameAndTitle(final ContentPage page,
                                              final Folder folder) {

        String resolvedName = page.getName();
        String resolvedTitle = page.getTitle();
        int i = 0;

        while (folder.getItem(resolvedName, false) != null) {
            i++;
            resolvedName = String.format("%s-%d", page.getName(), i);
            resolvedTitle = String.format("%s (%d)", page.getTitle(), i);
        }

        page.setName(resolvedName);
        page.setTitle(resolvedTitle);
    }

}
