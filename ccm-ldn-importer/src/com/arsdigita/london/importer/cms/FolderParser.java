package com.arsdigita.london.importer.cms;

import com.arsdigita.cms.CMS;
import com.arsdigita.cms.ContentBundle;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ContentPage;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.Folder;
import com.arsdigita.kernel.EmailAddress;
import com.arsdigita.kernel.PersonName;
import com.arsdigita.kernel.User;
import com.arsdigita.kernel.UserAuthentication;
import com.arsdigita.london.importer.AbstractTagParser;
import com.arsdigita.london.importer.TagParser;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.workflow.simple.TaskCollection;
import com.arsdigita.workflow.simple.UserTask;
import com.arsdigita.workflow.simple.Workflow;
import com.arsdigita.workflow.simple.WorkflowTemplate;
import java.util.Iterator;
import java.util.Stack;
import org.apache.log4j.Logger;
import org.xml.sax.Attributes;


/**
 *  Parser handling &lt;folder&gt; XML subblock.
 *
 * &lt;folder&gt; elements can be nested to arbitrary depth.
 * <tt>name</tt> is mandatory XML attribute for the &lt;folder&gt;
 * XML element, while <tt>label</tt> is optional.  They are used
 * to set URL and the title of created folder, respectively.
 * FolderParser will create new folder only if folder with specified
 * does not exist under the current parent folder.
 *
 *  @see com.arsdigita.london.importer
 */
public class FolderParser extends AbstractTagParser {
    private static Logger s_log =
        Logger.getLogger(FolderParser.class);

    public static final String FOLDER_NAME = "name";
    public static final String FOLDER_LABEL = "label";

    private Stack m_folders;
    private ContentSection m_section;
    private WorkflowTemplate m_workflowTemplate;

    /**
     *  Preferred constructor, which accepts the <tt>ContentSection</tt>
     * where imported folders and items will be stored.
     */
    public FolderParser(ContentSection section) {
        this("folder", CMS.CMS_XML_NS, section);
    }

    public FolderParser(String tagName,
                        String tagURI,
                        ContentSection section) {
        super(tagName, tagURI);

        m_folders = new Stack();
        m_section = section;
        TaskCollection tc = section.getWorkflowTemplates();
        if (tc.next()) {
            m_workflowTemplate = (WorkflowTemplate) tc.getTask();
            tc.close();
        }
    }

    protected void startTag(String tagName,
                            String uri,
                            Attributes atts) {
        if (!"folder".equals(tagName)) {
            s_log.warn("Unexpected tag " + tagName + " " + uri);
            return;
        }

        String name = atts.getValue(FOLDER_NAME);
        String label = atts.getValue(FOLDER_LABEL);
        if (label == null) {
            label = name;
        }

        Folder folder = getFolderByName(getFolder(),
                                        name,
                                        label);
        m_folders.push(folder);
    }

    protected void endTag(String name, String uri) {
        if (!"folder".equals(name)) {
            s_log.warn("Unexpected tag " + name + " " + uri);
            return;
        }
    }

    public void endBlock() {
        m_folders.pop();
    }

    /**
     *  If parser handling the subblock happens to be instance of {@link ItemParser},
     * here we create {@link com.arsdigita.cms.ContentBundle} instance and attach
     * newly imported content item to bundle.  Additional magic performed here is
     * placing the newly created bundle in currently processed folder and starting
     * a workflow on the imported item, if specified by XML source.
     */
    public void endSubBlock(TagParser parser) {
        // The subblock might be another instance of FolderParser, if
        // import XML file contains nested folder structure!
        if (parser instanceof ItemParser) {
            ItemParser itemParser = (ItemParser) parser;
            ContentItem item = (ContentItem) itemParser.getDomainObject();
            // returning the null item indicates an item that
            // has already been imported
            s_log.info("Item is " + item);
            if (item != null) {
                // We have to place this item into language bundle
                ContentBundle bundle = new ContentBundle(item);
                Folder folder = getFolder();
                bundle.setParent(folder);
                if (itemParser.isIndexItem()) {
                    folder.setIndexItem(bundle);
                }
                if (itemParser.relabelFolder()  &&  item instanceof ContentPage) {
                    folder.setLabel( ((ContentPage) item).getTitle());
                }
                s_log.info("Set bundle " + bundle);
                if (itemParser.getAuthor() != null  &&  m_workflowTemplate != null) {
                    // This means this item wants to have its
                    // workflow started.  First find the author,
                    // if he's not in database, create it.
                    String email = itemParser.getAuthor().trim();
                    User author = retrieveUserByEmail(email);
                    Workflow workflow = m_workflowTemplate.instantiateNewWorkflow();
                    workflow.setObject(item);
                    workflow.start(author);
                    s_log.info("Starting workflow on item by user: " + author.getPrimaryEmail().getEmailAddress());
                    Iterator tasks = workflow.getTasks();
                    while (tasks.hasNext()) {
                        UserTask task = (UserTask) tasks.next();
                        if (task.isActive()) {
                            task.lock(author);
                        }
                    }
                }
                // TODO: End Of Lifecycle
            }
            //} else if (parser instanceof BundleParser) {
            //    ContentBundle bundle = (ContentBundle)
            //      ((BundleParser)parser).getDomainObject();
            // returning the null item indicates an item that
            // has already been imported
            //    if (item != null) {
            //        bundle.setParent(getFolder());
            //    }
        }
    }

    /**
     * Gets the currently active folder.  It might be newly created folder
     * or one which already exists.
     */
    protected Folder getFolder() {
        if (m_folders.empty()) {
            return m_section.getRootFolder();
        } else {
            return (Folder)m_folders.peek();
        }
    }


    // Retrieves the folder with passed name under the designated parent.
    // If none exists, create it.  If name is "/" and parent is root,
    // simply return root folder.
    private Folder getFolderByName(Folder parent, String name, String label) {
        if ("/".equals(name)  &&  parent.getParent() == null) {
            return parent;
        }
        Folder.ItemCollection items = parent.getItems();
        items.addFolderFilter(true);
        items.addVersionFilter(false);
        items.addNameFilter(name);
        if (items.next()) {
            Folder f = (Folder) items.getContentItem();
            items.close();
            return f;
        }
        // Create new folder
        Folder f = new Folder();
        f.setName(name);
        f.setLabel(label);
        // Place new folder under the current parent,
        f.setParent(parent);
        return f;
    }

    /**
     *  Creates new CCM user if email not found in database.
     *
     *  @return either existing or newly created user
     */
    private User retrieveUserByEmail(String email) {
        email = email.toLowerCase().trim();
        DataCollection dc = SessionManager.getSession()
                                .retrieve(User.BASE_DATA_OBJECT_TYPE);
        dc.addEqualsFilter("lower(primaryEmail)", email);
        if (dc.next()) {
            User user = User.retrieve(dc.getDataObject());
            dc.close();
            return user;
        }
        User newUser = new User();
        PersonName name = newUser.getPersonName();
        name.setGivenName(email);
        name.setFamilyName("CCM user");
        newUser.setPrimaryEmail(new EmailAddress(email));

        UserAuthentication ua = UserAuthentication.createForUser(newUser);
        ua.setPasswordQuestion("Please type your email again (all in lowercase, with no whitespace)");
        ua.setPasswordAnswer(email);
        // Is this random enough?
        ua.setPassword("" + newUser.hashCode() + ":" + name.hashCode());
        s_log.info("New user created, email: " + email + ", ID: " + newUser.getID());
        return newUser;
    }
}

