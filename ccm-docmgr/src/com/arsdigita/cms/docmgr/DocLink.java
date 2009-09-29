/*
 * Copyright (C) 2001 - 2003 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the CCM Public
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of
 * the License at http://www.redhat.com/licenses/ccmpl.html
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 * Created on Dec 9, 2003
 *
 */

package com.arsdigita.cms.docmgr;

import com.arsdigita.categorization.Category;
import com.arsdigita.categorization.CategoryCollection;
import com.arsdigita.cms.ContentBundle;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ContentPage;
import com.arsdigita.cms.ContentType;
import com.arsdigita.cms.Folder;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.kernel.ACSObject;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.KernelExcursion;
import com.arsdigita.kernel.permissions.PermissionService;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.PersistenceException;
import com.arsdigita.search.Searchable;
import com.arsdigita.util.Assert;
import com.arsdigita.util.UncheckedWrapperException;

import java.math.BigDecimal;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;

import org.apache.log4j.Logger;

/**
 * DomainObject class which represents <code>DocLink<code> ContentType objects.
 *
 * A DocLink will either have an internal reference to another Document
 * _or_ contain an external (http) URL.
 *
 * @author <a href="mailto:sshinde@redhat.com">Shashin Shinde</a>
 * @author Crag Wolfe
 *
 * $Id: DocLink.java,v 1.1 2004/12/15 16:06:37 pkopunec Exp $
 *
 */

public class DocLink extends ContentPage implements Resource, Searchable {
    
    private static final Logger s_log = Logger.getLogger(DocLink.class.getName());
    
    private static final String NAME_SUFFIX = "-LinkTo";
    
    /** Data object type for this domain object */
    public static final String BASE_DATA_OBJECT_TYPE =
            "com.arsdigita.cms.docmgr.DocLink";
    /** Data object type for this domain object (for CMS compatibility) */
    public static final String TYPE = BASE_DATA_OBJECT_TYPE;
    
    public static final String DESCRIPTION = "description";
    private static final String REPOSITORY = "repository";
    private static final String TARGET = "target";
    private static final String EXTERNAL_URL = "externalURL";
    public static final String LAST_MOD_LOCAL = "lastModifiedTimeCached";
    
    public DocLink() {
        this(BASE_DATA_OBJECT_TYPE);
        try {
            setContentType(
                    ContentType.findByAssociatedObjectType(BASE_DATA_OBJECT_TYPE));
        } catch (DataObjectNotFoundException e) {
            throw new UncheckedWrapperException(
                    (String) GlobalizationUtil
                    .globalize("cms.contenttypes.event_type_not_registered")
                    .localize(),
                    e);
        }
    }
    
    public DocLink(BigDecimal id) throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }
    
    public DocLink(OID id) throws DataObjectNotFoundException {
        super(id);
    }
    
    public DocLink(DataObject obj) {
        super(obj);
    }
    
    public DocLink(String type) {
        super(type);
    }
    
    public void setTitle(String title) {
        setName(URLEncoder.encode(title));
        super.setTitle(title);
    }
    
    /**
     * @see com.arsdigita.cms.docmgr.Resource#getDescription()
     */
    public String getDescription() {
        return (String) get(DESCRIPTION);
    }
    
    public void setDescription(String desc){
        set(DESCRIPTION,desc);
    }
    
    public boolean isExternal() {
        String s = getExternalURL();
        return s != null && s.length() > 0;
    }
    
    public String getExternalURL() {
        return (String) get(EXTERNAL_URL);
    }
    
    public void setExternalURL(String externalURL){
        set(EXTERNAL_URL,externalURL);
    }
    
    public Repository getRepository() {
        if (get(REPOSITORY) == null) {
            return null;
        }
        return new Repository((DataObject) get(REPOSITORY));
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
    
    /**
     * Set Target Document of this Link.Also set's the name of the Link.
     * i.e. target.getName()-LinkToxxxx
     */
    public void setTarget(Document target) {
        setName(generateLinkName(target));
        set(TARGET, target);
    }
    
    /**
     * Helper method to generate the name of the Link using the
     * NAME_SUFFIX,target Document id,and target document name.
     * Also truncates the name if it exceeds 200 characters.
     */
    private String generateLinkName(Document targetDoc){
        StringBuffer buf = new StringBuffer(targetDoc.getName());
        buf.append(NAME_SUFFIX).append(targetDoc.getID());
        if(buf.length() > 200){
            String docName = targetDoc.getName();
            String suffix = NAME_SUFFIX + targetDoc.getID();
            int suffixLen = suffix.length();
            docName = docName.substring(0,(200 - suffixLen));
            docName = docName + suffix;
            Assert.truth(docName.length() < 201 , "Actual Length is: " + docName.length());
            return docName;
        } else{
            return buf.toString();
        }
    }
    
    public Document getTarget() {
        if (get(TARGET) == null) {
            return null;
        }
        return new Document((DataObject) get(TARGET));
    }
    
    /**
     * @see com.arsdigita.cms.docmgr.Resource#getParentResource().
     * Returns the parent <code>DocFolder</code>
     */
    public Resource getParentResource() {
        DocFolder parent =
                (DocFolder) ((ContentBundle) getParent()).getParent();
        return parent;
    }
    
    /**
     * @see com.arsdigita.cms.docmgr.Resource#isFolder().
     * returns false to indicate that it's not a folder.
     */
    public boolean isFolder() {
        return false;
    }
    
    /**
     * @see com.arsdigita.cms.docmgr.Resource#isFile().
     * returns true to indicate that it's a file.
     */
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
    public Resource copyTo(String name, final Resource parent) throws ResourceExistsException {
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
        newItem.setContentSection(item.getContentSection());
        newItem.save();
        
        new KernelExcursion() {
            protected void excurse() {
                setParty(Kernel.getSystemParty());
                PermissionService.setContext(newItem,(ACSObject) parent);
            }}.run();
            
            return (Resource) ((ContentBundle) newItem).getPrimaryInstance();
    }
    
    /**
     * Copies the resource into the same location (same parent) with passed
     * in name as new name.
     *
     * @param name the name of the copy
     * @return a copy of the original resource.
     */
    public Resource copyTo(String name) throws ResourceExistsException {
        final ACSObject parent = getParent();
        return copyTo(name , (Resource) parent);
    }
    
    /**
     * @see com.arsdigita.cms.docmgr.Resource#toURL().
     * Not Supported.throws <code>UnsupportedOperationException</code>
     */
    public URL toURL() {
        throw new UnsupportedOperationException();
    }
    
    /**
     * @see com.arsdigita.cms.docmgr.Resource#setParentResource(com.arsdigita.cms.docmgr.Resource)
     */
    public void setParentResource(final Resource parent) {
        final ContentBundle cb = (ContentBundle) getParent();
        cb.setParent((ACSObject) parent);
        cb.save();
        
        new KernelExcursion() {
            protected void excurse() {
                setParty(Kernel.getSystemParty());
                PermissionService.setContext(cb, (ACSObject) parent);
            }
        }
        .run();
    }
    
    /**
     * @see com.arsdigita.cms.docmgr.Resource#isRoot()
     * returns false to indicate that it's not the root.
     */
    public boolean isRoot() {
        return false;
    }
    
    /**
     * Delete this Link along with it's parent ContentBundle
     * @see com.arsdigita.domain.DomainObject#delete()
     */
    public void delete() throws PersistenceException {
        ((ContentBundle) getParent()).delete();
    }
    
    /**
     * Over-ride to avoid any indexing of data for this ContentType.
     * returns empty string;
     */
    public String getSearchXMLContent() {
        return "";
    }
    
    /**
     * Over-ride to avoid any indexing of data for this ContentType.
     * returns empty byte array;
     */
    public byte[] getSearchRawContent() {
        return new byte[0];
    }
    
    public void setCategories(String[] catIDs) {
        HashSet newCategories = new HashSet();
        if (catIDs != null) {
            for(int i =0; i < catIDs.length; i++) {
                newCategories.add(catIDs[i]);
                s_log.debug("newCategories: "+catIDs[i]);
            }
        }
        
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
        Iterator additions = newCategories.iterator();
        while (additions.hasNext()) {
            addCategory(new Category(new BigDecimal
                    ((String) additions.next())));
        }
    }
    
    protected void beforeSave() {
        super.beforeSave();
        setLastModifiedLocal(new Date());
    }
    
    public String getSearchLanguage() {
        // Returns language type of document.  "eng" is english, (ISO 639-2)
        // If not English, should be overridden.
        return "eng";
    }
    
    public String getSearchLinkText() {
        return generateLinkName(getTarget());
    }
    
    public String getSearchUrlStub() {
        return "";
    }
    
}
