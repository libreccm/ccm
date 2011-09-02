package com.arsdigita.cms.dabin;

import com.arsdigita.categorization.Category;
import com.arsdigita.cms.ContentPage;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.Folder;
import com.arsdigita.cms.lifecycle.Lifecycle;
import com.arsdigita.cms.lifecycle.LifecycleDefinition;
import com.arsdigita.cms.lifecycle.LifecycleDefinitionCollection;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.packaging.Program;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.TransactionContext;
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
    private ContentSection authorsSection;
    private ContentSection personsSection;
    private LifecycleDefinition membersLifecycle;
    private LifecycleDefinition authorsLifecycle;
    private LifecycleDefinition personsLifecycle;
    private Folder membersFolder;
    private Folder authorsFolder;
    private Folder personsFolder;
    private Category membersActiveCategory;
    private Category membersFormerCategory;
    private Category membersAssociatedCategory;
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
        authorsSection = getContentSection(config.getProperty(
                "members.contentsection"));
        personsSection = getContentSection(config.getProperty(
                "members.contentsection"));

        LifecycleDefinitionCollection lifecycles = membersSection.
                getLifecycleDefinitions();
        while (lifecycles.next()) {
            membersLifecycle = lifecycles.getLifecycleDefinition();
        }

        lifecycles = authorsSection.getLifecycleDefinitions();
        while (lifecycles.next()) {
            authorsLifecycle = lifecycles.getLifecycleDefinition();
        }

        lifecycles = personsSection.getLifecycleDefinitions();
        while (lifecycles.next()) {
            personsLifecycle = lifecycles.getLifecycleDefinition();
        }

        final String membersFolderPath = config.getProperty("members.folder");
        final String authorsFolderPath = config.getProperty("authors.folder");
        final String personsFolderPath = config.getProperty("persons.folder");

        membersFolder = getOrCreateFolder(membersSection, membersFolderPath);
        authorsFolder = getOrCreateFolder(authorsSection, authorsFolderPath);
        personsFolder = getOrCreateFolder(personsSection, personsFolderPath);

        membersActiveCategory = new Category(new BigDecimal(config.getProperty(
                "members.active.category")));
        membersFormerCategory = new Category(new BigDecimal(config.getProperty(
                "members.former.category")));
        membersAssociatedCategory = new Category(new BigDecimal(config.
                getProperty(
                "members.associated.category")));

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
        System.out.printf("membersSection = %s\n", membersSection.getName());
        System.out.printf("authorsSection = %s\n", authorsSection.getName());
        System.out.printf("personsSection = %s\n", personsSection.getName());
        System.out.println("");
        System.out.printf("membersFolder = %s\n", membersFolder.getPath());
        System.out.printf("authorsFolder = %s\n", authorsFolder.getPath());
        System.out.printf("personsFolder = %s\n", personsFolder.getPath());
        System.out.println("");
        System.out.printf("membersActiveCategory = %s\n",
                          membersActiveCategory.getName());
        System.out.printf("membersFormerCategory = %s\n",
                          membersFormerCategory.getName());
        System.out.printf("membersAssociatedCategory = %s\n",
                          membersAssociatedCategory.getName());

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
                    + "ORDER BY person.Name");
            
            result.last();
            final long number = result.getRow();
            result.beforeFirst();

            System.out.printf("Found %d persons in the DaBIn database.", number);
            
            int i = 1;
            while (result.next()) {
                System.out.printf("Processing person '%d' of '%d':\n", i, number);                
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
                                  result.getString("abteilunglink.Abteilung_Id"));
                System.out.println("");
                
                i++;
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

        final ContentSection _section = (ContentSection) ContentSection.
                retrieveApplicationForPath(path.toString());

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
