package com.arsdigita.cms.webpage;

import com.arsdigita.cms.util.GlobalizationUtil;

import com.arsdigita.categorization.Category;
import com.arsdigita.categorization.CategoryCollection;
// import com.arsdigita.cms.ContentBundle;
// import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ContentPage;
import com.arsdigita.cms.ContentType;
import com.arsdigita.cms.Folder;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.domain.DomainObjectXMLRenderer;
import com.arsdigita.kernel.ACSObject;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.KernelExcursion;
import com.arsdigita.kernel.permissions.PermissionService;
import com.arsdigita.kernel.User;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.PersistenceException;
import com.arsdigita.persistence.SessionManager;
//import com.arsdigita.search.Searchable;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.util.StringUtils;
import com.arsdigita.xml.Element;

import java.math.BigDecimal;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;

/**
 * This content type represents a webpage.
 *
 * @author Tzu-Mainn Chen
 **/

public class Webpage extends ContentPage /*implements Searchable*/ {

    private final static org.apache.log4j.Logger s_log =
        org.apache.log4j.Logger.getLogger(Webpage.class);

    /** PDL properties */
    public static final String DESCRIPTION = "description";
    public static final String BODY = "body";
    public static final String AUTHOR = "author";

    // redundant fields, for efficient retreival
    public static final String CREATOR = "creator";
    public static final String LAST_MOD_LOCAL = "lastModifiedTimeCached";
    public static final String AUTHOR_LAST_NAME = "authorLastName";

    /** Data object type for this domain object */
    public static final String BASE_DATA_OBJECT_TYPE
        = "com.arsdigita.cms.webpage.Webpage";
    /** Data object type for this domain object (for CMS compatibility) */
    public static final String TYPE = BASE_DATA_OBJECT_TYPE;

    private boolean m_wasNew;

    public Webpage() {
        this(BASE_DATA_OBJECT_TYPE);
        try {
            setContentType(ContentType.findByAssociatedObjectType(BASE_DATA_OBJECT_TYPE));
        } catch(DataObjectNotFoundException e) {
            throw new UncheckedWrapperException( (String) GlobalizationUtil.globalize("cms.contenttypes.event_type_not_registered").localize(),  e);
        }
    }

    public Webpage(BigDecimal id) throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    public Webpage(OID id) throws DataObjectNotFoundException {
        super(id);
    }

    public Webpage(DataObject obj) {
        super(obj);
    }

    public Webpage(String type) {
        super(type);
    }

    /**
     * Returns a document for the given data object.  Use this method instead
     * of the new Webpage(DataObject) method.  This method uses the domain
     * object factory to produce the appropriate document class.
     *
     * @param documentData the document <code>DataObject</code>
     *
     * @return  the document for the given data object.
     **/
    public static Webpage retrieveWebpage(DataObject documentData) {
        Webpage document = (Webpage) 
            DomainObjectFactory.newInstance(documentData);
        if (document==null) {
            throw new RuntimeException
                ("Domain object factory produced " +
                 "null document for data object " +
                 documentData);
        }
        return document;
    }

    public String getDescription() {
        return (String) get(DESCRIPTION);
    }

    public void setDescription(String description) {
        set(DESCRIPTION, description);
    }

    public String getBody() {
        return (String) get(BODY);
    }

    public void setBody(String body) {
        set(BODY, body);
    }

    public User getCreator() {
        if ( get(CREATOR) == null ) {
            return null;
        }
        return new User((DataObject)get(CREATOR));
    }

    public void setCreator(User creator) {
        set(CREATOR, creator);
    }

    /* redundant to versioning, only for performance */
    public Date getLastModifiedLocal() {
        return (Date) get(LAST_MOD_LOCAL);
    }
    /* redundant to versioning, only for performance */
    public void setLastModifiedLocal(Date last) {
        set(LAST_MOD_LOCAL, last);
    }

    public String getImpliedAuthor() {
        if(getAuthor() == null) {
            if (getCreator() == null) {
                return "";
            } 
            return getCreator().getName();
        }
        return getAuthor();
    }

    public String getAuthor() {
        return (String) get(AUTHOR);
    }

    public void setAuthor(String author) {
        if (author != null &&
            getCreationUser() != null &&
            author.equals(getCreationUser().getName())) {
            set(AUTHOR, null);
            setAuthorLastName(null);
        }
        set(AUTHOR, author);
        String[] chunks = StringUtils.split(author, ' ');
        if(chunks.length > 0) {
            setAuthorLastName(chunks[chunks.length-1]);
        } else {
            setAuthorLastName(null);
        }
    }

    public String getAuthorLastName() {
        return (String) get(AUTHOR_LAST_NAME);
    }

    private void setAuthorLastName(String authorLastName) {
        set(AUTHOR_LAST_NAME, authorLastName);
    }

    public void setTitle(String title) {
        setName(URLEncoder.encode(title));
        super.setTitle(title);
    }

    public boolean isFolder() {
        return false;
    }
    public boolean isFile() {
        return true;
    }

    public URL toURL() {
        //TODO
        throw new UnsupportedOperationException();
        //try {
        //    return new URL("http://tod.example.net");
        //} catch ( java.net.MalformedURLException ex ) {
        //    ex.printStackTrace();
        //    s_log.error(ex.getMessage());
        //    return null;
        //}
    }

    public boolean isRoot() {
        return false;
    }

    protected void beforeSave() {
        super.beforeSave();
        
        if (isNew()) {
            s_log.debug(this + "newly created Webpage");
            m_wasNew = true;
        }
        if (m_wasNew) {
            setCreator(getCreationUser());
        }
    }

    public void setCategories(String[] catIDs) {
        HashSet newCategories = new HashSet();
        if (catIDs != null) {
            for(int i =0; i < catIDs.length; i++) {
                newCategories.add(catIDs[i]);
                s_log.debug("newCategories: "+catIDs[i]);
            }
        }
        CategoryCollection old = getCategoryCollection();
        while(old.next()) {
            Category cat = (Category) old.getCategory();
            String catID = cat.getID().toString();
            if (newCategories.contains(catID)) {
                newCategories.remove(catID);
            } else {
                removeCategory(cat);
            }
        }
        Iterator additions = newCategories.iterator();
        while (additions.hasNext()) {
            addCategory(new Category(new BigDecimal
                                     ((String) additions.next())));
        }
    }


    //protected void afterSave() {
    //    super.afterSave();
    //}

    public Folder.ItemCollection getWebpagesWithName
        (Folder parentFolder, String name) {
        Folder.ItemCollection ic = parentFolder.getItems();
        ic.addEqualsFilter("name", name);
        return ic;
    }

    /*
    // EE 20051220 - commented out this bit
    // as we no longer implement Searchable

    public static final int SUMMARY_LENGTH = 200;
    public String getSearchSummary() {
        String descrip = "";
        if (getDescription() != null) {
            descrip = getDescription();
        }
        return com.arsdigita.util.StringUtils.truncateString
            (descrip, SUMMARY_LENGTH, true);
    }
    */

    //public byte[] getSearchRawContent() {
    //    FileAsset fa = getFile();
    //    if (fa != null) {
    //        return fa.getContent();
    //    }
    //    return null;
    //}

    /*
    // EE 20051220 - commented out this bit
    // as we no longer implement Searchable

    public String getSearchXMLContent() {
        Element root = new Element
            ("cms:item", com.arsdigita.cms.CMS.CMS_XML_NS);
        DomainObjectXMLRenderer renderer =
            new DomainObjectXMLRenderer(root);

        renderer.setWrapAttributes(true);
        // EE 20051125 - could not find SEARCHABLE_ADAPTER_CONTEXT
        //renderer.walk(this, SEARCHABLE_ADAPTER_CONTEXT);

        com.arsdigita.xml.Document doc = null;
        try {
            doc = new com.arsdigita.xml.Document(root);
        } catch (javax.xml.parsers.ParserConfigurationException ex) {
            final String message =
                (String) GlobalizationUtil.globalize
                    ("cms.cannot_create_xml_document").localize();
            throw new UncheckedWrapperException(message,  ex);
        }
        if (s_log.isDebugEnabled()) {
            s_log.debug("XML is " + doc.toString(true));
        }

        // add custom searchable fields
        Element author = new Element("author");
        author.setText(getImpliedAuthor());

        doc.getRootElement().addContent(author);

        // Hmm, why on earth doesn't this method return
        // Element directly ?!?!
        return doc.toString(true);
    }
    */

}
