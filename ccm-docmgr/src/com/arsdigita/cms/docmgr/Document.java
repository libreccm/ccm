package com.arsdigita.cms.docmgr;

import java.math.BigDecimal;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;

import com.arsdigita.categorization.Category;
import com.arsdigita.categorization.CategoryCollection;
import com.arsdigita.cms.ContentBundle;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ContentPage;
import com.arsdigita.cms.ContentType;
import com.arsdigita.cms.FileAsset;
import com.arsdigita.cms.Folder;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.domain.DomainObjectXMLRenderer;
import com.arsdigita.kernel.ACSObject;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.KernelExcursion;
import com.arsdigita.kernel.User;
import com.arsdigita.kernel.permissions.PermissionService;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.PersistenceException;
import com.arsdigita.persistence.SessionManager;
//import com.arsdigita.search.Searchable;
import com.arsdigita.util.StringUtils;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.xml.Element;

/**
 * This content type represents a document.
 *
 * @author Crag Wolfe
 **/

public class Document extends ContentPage implements Resource {
//, Searchable

    private final static org.apache.log4j.Logger s_log =
        org.apache.log4j.Logger.getLogger(Document.class);

    /** PDL properties */
    public static final String DESCRIPTION = "description";
    public static final String FILE = "file";
    public static final String AUTHOR = "author";
    // redundant fields, for efficient retreival
    public static final String CREATOR = "creator";
    public static final String LAST_MOD_LOCAL = "lastModifiedTimeCached";
    public static final String AUTHOR_LAST_NAME = "authorLastName";
    public static final String REPOSITORY = "repository";

    // added in to make it compile 
    //public static final String SEARCHABLE_ADAPTER_CONTEXT = "com.arsdigita.search.Searchable";
    // FR: this has changed to
    //public static final String SEARCHABLE_ADAPTER_CONTEXT = "com.arsdigita.cms.search.ContentPageMetadataProvider";

    // FR: actually I comment out the search customisations: getContent and getTitle
    // no need for these

    /** Data object type for this domain object */
    public static final String BASE_DATA_OBJECT_TYPE
        = "com.arsdigita.cms.docmgr.Document";
    /** Data object type for this domain object (for CMS compatibility) */
    public static final String TYPE = BASE_DATA_OBJECT_TYPE;



    private boolean m_wasNew;

    public Document() {
        this(BASE_DATA_OBJECT_TYPE);
        try {
            setContentType(ContentType.findByAssociatedObjectType(BASE_DATA_OBJECT_TYPE));
        } catch(DataObjectNotFoundException e) {
            throw new UncheckedWrapperException( (String) GlobalizationUtil.globalize("cms.contenttypes.event_type_not_registered").localize(),  e);
        }
    }

    public Document(BigDecimal id) throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    public Document(OID id) throws DataObjectNotFoundException {
        super(id);
    }

    public Document(DataObject obj) {
        super(obj);
    }

    public Document(String type) {
        super(type);
    }

    /**
     * Returns a document for the given data object.  Use this method instead
     * of the new Document(DataObject) method.  This method uses the domain
     * object factory to produce the appropriate document class.
     *
     * @param documentData the document <code>DataObject</code>
     *
     * @return  the document for the given data object.
     **/
    public static Document retrieveDocument(DataObject documentData) {
        Document document = (Document) 
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

    public User getCreator() {
        if ( get(CREATOR) == null ) {
            return null;
        }
        return new User((DataObject)get(CREATOR));
    }

    public void setCreator(User creator) {
        set(CREATOR, creator);
    }

    public Repository getRepository() {
        if ( get(REPOSITORY) == null ) {
            return null;
        }
        return new Repository((DataObject)get(REPOSITORY));
    }

    public void setRepository(Repository repository) {
        set(REPOSITORY, repository);
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

    /** Accessor. Get the file associated with this item. */
    public FileAsset getFile () {
        if ( get(FILE) == null ) {
            return null;
        }
        return new FileAsset((DataObject)get(FILE));
    }

    /** Mutator. Set the file associated with this item. */
    public void setFile ( FileAsset file ) {
        setAssociation(FILE, file);
        // don't set the master if we're removing the file
        if (file != null) {
            file.setMaster(getMaster());
        }
    }

    public boolean isFolder() {
        return false;
    }
    public boolean isFile() {
        return true;
    }

    /**
     * Copies the resource into another location.  Preserves the
     * original name of the resource but places the copy inside a new
     * parent resource.
     *
     * @param parent the parent of the copy
     * @return a copy of the original resource
     */
    public Resource copyTo(Resource parent) throws ResourceExistsException {
        return copyTo(getTitle(), parent);
    }

    /**
     * Copies the resource into another location with a new name.
     *
     * @param name the name of the copy
     * @param parent the parent of the copy
     * @return a copy of the original resource.
     */
    public Resource copyTo(String name, final Resource parent) 
        throws ResourceExistsException {
        //TODO
        
        Folder.ItemCollection ic = 
            ((Folder) parent).getItems();
        ic.addEqualsFilter("name",URLEncoder.encode(name));
        boolean resourceExists = ic.next();
        ic.close();
        if(resourceExists) {
            throw new ResourceExistsException
                ("Copying document would result in duplicate: "+name);
        }

        ContentItem item = this;
        if (item.getParent() instanceof ContentBundle) {
            item = (ContentBundle) item.getParent();
        }
        
        if (s_log.isDebugEnabled()) {
            s_log.debug("Copying item " + item);
        }

        final ContentItem newItem = item.copy();
        newItem.copyServicesFrom(item);
        newItem.setParent((Folder) parent);
        //((Document) newItem).setTitle(name);
        // Doesn't seem like I should have to do this, but what the hell
        newItem.setContentSection(item.getContentSection());
        newItem.save();

        new KernelExcursion() {
            protected void excurse() {
                setParty(Kernel.getSystemParty());
                PermissionService.setContext(newItem,(ACSObject) parent);
            }}.run();

        return (Resource) ((ContentBundle) newItem)
            .getPrimaryInstance();
        //throw new ResourceExistsException("need code");
    }

    /**
     * Copies the resource into the same location (same parent) with a
     * new name.
     *
     * @param name the name of the copy
     * @return a copy of the original resource.
     */
    public Resource copyTo(String name) throws ResourceExistsException {
        final ACSObject parent = getParent();

        return copyTo(name , (Resource) parent);
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
    /**
     * Returns the size of the file in bytes.
     *
     * @return the size of the file in bytes, or 0 (should be -1?) if
     * the size cannot be computed.
     */
    public BigDecimal getSize() {
        FileAsset fa = getFile();
        if (fa == null) {
            return BigDecimal.valueOf(0);
        }
        return new BigDecimal(fa.getSize());
    }

    public boolean isRoot() {
        return false;
    }

    public String getPrettyMimeType() {
        FileAsset fa = getFile();
        if (fa == null) {
            return "";
        }
        if (fa.getMimeType() == null) {
            return "unknown";
        }
        return fa.getMimeType().getLabel();
    }

    public Resource getParentResource() {
        DocFolder parent = (DocFolder) 
            ((ContentBundle) getParent()).getParent();
        return parent;
    }

    public void setParentResource(final Resource r) {
        final ContentBundle cb = (ContentBundle) getParent();
        cb.setParent((ACSObject) r);
        cb.save();

        new KernelExcursion() {
            protected void excurse() {
                setParty(Kernel.getSystemParty());
                PermissionService.setContext(cb,(ACSObject) r);
            }}.run();
    }

    protected void beforeSave() {
        super.beforeSave();
        
        if (isNew()) {
            s_log.debug(this + "newly created Document");
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
        // Iterator old = getCategories();
	CategoryCollection cats = getCategoryCollection();
	Category cat;
	if (cats.next()) {
	    cat = cats.getCategory();
	    String catID = cat.getID().toString();
            if (newCategories.contains(catID)) {
                newCategories.remove(catID);
            } else {
                removeCategory(cat);
            }

        }

//         while(old.hasNext()) {
//             Category cat = (Category) old.next();
//             String catID = cat.getID().toString();
//             if (newCategories.contains(catID)) {
//                 newCategories.remove(catID);
//             } else {
//                 removeCategory(cat);
//             }
//         }
        Iterator additions = newCategories.iterator();
        while (additions.hasNext()) {
            addCategory(new Category(new BigDecimal
                                     ((String) additions.next())));
        }
    }


    //protected void afterSave() {
    //    super.afterSave();
    //}

    public Folder.ItemCollection getDocumentsWithName
        (Folder parentFolder, String name) {
        Folder.ItemCollection ic = parentFolder.getItems();
        ic.addEqualsFilter("name", name);
        return ic;
    }

//     public static final int SUMMARY_LENGTH = 200;
//     public String getSearchSummary() {
//         String descrip = "";
//         if (getDescription() != null) {
//             descrip = getDescription();
//         }
//         return com.arsdigita.util.StringUtils.truncateString
//             (descrip, SUMMARY_LENGTH, true);
//     }

    //public byte[] getSearchRawContent() {
    //    FileAsset fa = getFile();
    //    if (fa != null) {
    //        return fa.getContent();
    //    }
    //    return null;
    //}

//     public String getSearchXMLContent() {
//         Element root = new Element
//             ("cms:item", com.arsdigita.cms.CMS.CMS_XML_NS);
//         DomainObjectXMLRenderer renderer =
//             new DomainObjectXMLRenderer(root);

//         renderer.setWrapAttributes(true);
//         renderer.walk(this, SEARCHABLE_ADAPTER_CONTEXT);

//         com.arsdigita.xml.Document doc = null;
//         try {
//             doc = new com.arsdigita.xml.Document(root);
//         } catch (javax.xml.parsers.ParserConfigurationException ex) {
//             final String message =
//                 (String) GlobalizationUtil.globalize
//                     ("cms.cannot_create_xml_document").localize();
//             throw new UncheckedWrapperException(message,  ex);
//         }
//         if (s_log.isDebugEnabled()) {
//             s_log.debug("XML is " + doc.toString(true));
//         }

//         // add custom searchable fields
//         Element mimeType = new Element("mimeType");
//         mimeType.setText(getPrettyMimeType());
//         Element author = new Element("author");
//         author.setText(getImpliedAuthor());
//         Element workspace = new Element("workspace");
//         workspace.setText(getRepository().getParentApplication()
//                           .getID().toString());

//         doc.getRootElement().addContent(mimeType);
//         doc.getRootElement().addContent(author);
//         doc.getRootElement().addContent(workspace);

//         // Hmm, why on earth doesn't this method return
//         // Element directly ?!?!
//         return doc.toString(true);
//     }
    
    /**
     * Deletes the Parent ContentBundle of this Document.
     * @see com.arsdigita.domain.DomainObject#delete()
     */
    public void delete() throws PersistenceException {
        //Need to Remove all the links that point to this Document.
        deleteReferringLinks();
        ((ContentBundle)getParent()).delete();
    }
     
    /**Helper method to loop over all the links that point to this document and
     * delete them.
     */      
    private void deleteReferringLinks(){
        DataQuery dq = SessionManager.getSession().
        retrieveQuery("com.arsdigita.cms.docmgr.getReferringLinks");
        dq.setParameter("docID" , getID());
        DataObject dobj;
        while(dq.next()){
            dobj = (DataObject) dq.get("docLink");
            DocLink docLink = new DocLink(dobj);
            docLink.delete();
        }
        dq.close();
    }

    public String getSearchLanguage() {
        // Returns language type of document.  "eng" is english, (ISO 639-2)
        // If not English, should be overridden.
        return "eng";
    }

    public byte[] getSearchRawContent() {
        return new byte[0];
    }

    public String getSearchUrlStub() {
	return "";
    }

    public String getSearchLinkText() {
        return getTitle();
    }
}
