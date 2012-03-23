package com.arsdigita.cms.docmgr;

import java.math.BigDecimal;
import java.net.URL;
import java.net.URLEncoder;

import com.arsdigita.cms.ContentBundle;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ContentType;
import com.arsdigita.cms.Folder;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.kernel.ACSObject;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.KernelExcursion;
import com.arsdigita.kernel.permissions.PermissionService;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.util.UncheckedWrapperException;

/**
 * This class needs its own BASE_DATA_OBJECT_TYPE so we can have
 * Description field.  Otherwise, it would just be cms.Folder.
 *
 * @author Crag Wolfe
 **/

public class DocFolder extends Folder implements Resource {

    /** Private Logger instance for debugging purpose.                        */
    private final static org.apache.log4j.Logger s_log =
        org.apache.log4j.Logger.getLogger(DocFolder.class);
    
    public static final String DESCRIPTION = "description";

    /** Data object type for this domain object */
    public static final String BASE_DATA_OBJECT_TYPE
                               = "com.arsdigita.cms.docmgr.DocFolder";
    /** Data object type for this domain object (for CMS compatibility) */
    public static final String TYPE = BASE_DATA_OBJECT_TYPE;


    public DocFolder() {
        this(BASE_DATA_OBJECT_TYPE);
        try {
            setContentType(ContentType.findByAssociatedObjectType(BASE_DATA_OBJECT_TYPE));
        } catch(DataObjectNotFoundException e) {
            throw new UncheckedWrapperException( (String) GlobalizationUtil.globalize(
                      "cms.contenttypes.event_type_not_registered").localize(),  e);
        }
    }

    public DocFolder(BigDecimal id) throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    public DocFolder(OID id) throws DataObjectNotFoundException {
        super(id);
    }

    public DocFolder(DataObject obj) {
        super(obj);
    }

    public DocFolder(String type) {
        super(type);
    }

    public DocFolder(String title, String descrip, DocFolder parent) {
        this(TYPE);
        setTitle(title);
        setDescription(descrip);
        setParent(parent);
        setContentSection(parent.getContentSection());
        s_log.debug("creating docfolder: " + title);
    }

    /**
     * Returns a DocFolder for the given data object. Use this method instead
     * of the new Docfolder(DataObject) method.  This method uses the domain
     * object factory to produce the appropriate docfolder class.
     *
     * @param docfolderData the docfolder <code>DataObject</code>
     *
     * @return  the docfolder for the given data object.
     **/
    public static DocFolder retrieveDocFolder(DataObject docfolderData) {
        DocFolder docfolder = (DocFolder) 
            DomainObjectFactory.newInstance(docfolderData);
        if (docfolder==null) {
            throw new RuntimeException
                ("Domain object factory produced " +
                 "null docfolder for data object " +
                 docfolderData);
        }
        return docfolder;
    }

    public DocFolder retrieveSubFolder(String folderTitle) throws
        DataObjectNotFoundException {
        Folder.ItemCollection items = getItems();
        items.addFolderFilter(true);
        items.addEqualsFilter("name",
                              URLEncoder.encode(folderTitle));
        
        DocFolder df = null;
        if (items.next()) {
            df = (DocFolder) items.getContentItem();
        }
        items.close();
        if (df == null) {
            throw new DataObjectNotFoundException
                ("No child, "+folderTitle+", found for folder "+
                 getTitle()+" (id is "+getID().toString());
        }
        return df;
    }

    public Resource retrieveSubResource(String resourceTitle) throws
        DataObjectNotFoundException {
        Folder.ItemCollection items = getItems();
        items.addEqualsFilter("name",
                              URLEncoder.encode(resourceTitle));
        
        Resource r = null;
        if (items.next()) {
            if (items.isFolder()) {
                r = (DocFolder) items.getContentItem();
            } else {
                r = (Document) 
                    ((ContentBundle) items.getContentItem())
                    .getPrimaryInstance();
            }
        }
        items.close();
        if (r == null) {
            throw new DataObjectNotFoundException
                ("No child, "+resourceTitle+", found for folder "+
                 getTitle()+" (id is "+getID().toString());
        }
        return r;
    }


    public boolean isFolder() {
        return true;
    }
    public boolean isFile() {
        return false;
    }

    public String getTitle() {
        return getLabel();
    }

    public void setTitle(String title) {
        setName(URLEncoder.encode(title));
        super.setLabel(title);
    }

    public String getDescription() {
        return (String) get(DESCRIPTION);
    }

    public void setDescription(String description) {
        set(DESCRIPTION, description);
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
    public Resource copyTo(String name, Resource parent) throws ResourceExistsException {
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

        DocFolder df = new DocFolder(getTitle(), getDescription(), 
                                     (DocFolder) parent);
        df.save();
        return df;
    }

    /**
     * Copies the resource into the same location (same parent) with a
     * new name.
     *
     * @param name the name of the copy
     * @return a copy of the original resource.
     */
    public Resource copyTo(String name) throws ResourceExistsException {
        return copyTo(name, getParentResource());
    }

    public URL toURL() {
        //TODO
        //return new URL("http://tod.example.net");
        try {
            return new URL("http://tod.example.net");
        } catch ( java.net.MalformedURLException ex ) {
            ex.printStackTrace();
            s_log.error(ex.getMessage());
            return null;
        }
    }

    public boolean isRoot() {
        // all root repositories folders are one folder below
        // a content section root
        // OR
        // have a parent Folder rather than DocFolder
        return ((ContentItem) getParent()).getParent() == null ||
            //((ACSObject) ((ContentItem) getParent()).getParent()).
            ((ACSObject) getParent()).
            getSpecificObjectType().equals("com.arsdigita.cms.Folder");
    }

    public Resource getParentResource() {
        DocFolder parent = (DocFolder) getParent();
        return parent;
    }

    public void setParentResource(final Resource r) {
        setParent((ACSObject) r);
        save();
        final DocFolder thisResource = this;
        new KernelExcursion() {
            protected void excurse() {
                setParty(Kernel.getSystemParty());
                PermissionService.setContext(thisResource,(ACSObject) r);
            }}.run();
    }

    public static DocFolder getRootFolder(DocFolder f) {
        
        if (f.isRoot()) {
            return f;
        }
        return f.getRootFolder((DocFolder) f.getParentResource());
    }

    public static Repository getRepository(Resource r) {
        DocFolder f = null;
        if (r.isFolder()) {
            f = (DocFolder) r;
        } else {
            f = (DocFolder) r.getParentResource();
        }
        DocFolder rootFolder = DocFolder.getRootFolder(f);
        Repository rep = null;

        DataCollection dataCollection =
            SessionManager.getSession().retrieve(Repository.BASE_DATA_OBJECT_TYPE);
        dataCollection.addEqualsFilter(Repository.ROOT,rootFolder.getID().toString());
        long size = dataCollection.size();
        if (size > 1) {
            s_log.error("should not have more than one repository per folder");
        } if (size == 0) {
            s_log.error("root folder does not have a repository");
        } else {
            dataCollection.next();
            rep = new Repository(dataCollection.getDataObject());
        }
        dataCollection.close();
        return rep;
    }
}
