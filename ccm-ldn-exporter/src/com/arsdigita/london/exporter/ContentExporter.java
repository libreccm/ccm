
package com.arsdigita.london.exporter;


import com.arsdigita.cms.CMS;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.Folder;
import com.arsdigita.cms.ContentPage;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.kernel.ACSObject;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.TransactionContext;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.Filter;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.domain.DataObjectNotFoundException;
   
import com.arsdigita.xml.Document;
import com.arsdigita.xml.Element;

import com.arsdigita.util.UncheckedWrapperException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;

public class ContentExporter {
    
    private static final Logger s_log = Logger.getLogger(ContentExporter.class);

    private Map m_folders;
    private Map m_elements;
    private Map m_paths;

    private List m_items;

    private File m_itemDir;
    private File m_assetDir;

    public ContentExporter(File itemDir,
                           File assetDir) {
        m_itemDir = itemDir;
        m_assetDir = assetDir;
        m_folders = new HashMap();
        m_elements = new HashMap();
        m_paths = new HashMap();
        m_items = new ArrayList();
    }
    
    
    public void exportManifest(ContentSection section,
                               String version,
                               String systemID) {
        Session session = SessionManager.getSession();

        DataCollection folders = session.retrieve(Folder.BASE_DATA_OBJECT_TYPE);
        Filter f = folders.addInSubqueryFilter(ACSObject.ID,
                                               "com.arsdigita.london.exporter.itemIDsInSection");
        f.set("sectionID", section.getID());
        folders.addEqualsFilter("isDeleted", Boolean.FALSE);
        folders.addEqualsFilter("version", version);
        folders.addOrder("ancestors");

        Element root = null;

        while (folders.next()) {
            Folder folder = (Folder)DomainObjectFactory
                .newInstance(folders.getDataObject());
            
            Folder parent = getParent(folder);
            if (s_log.isDebugEnabled()) {
                s_log.debug("Folder" + folder.getOID() + " parent " + (parent == null ? null : parent.getOID()));
            }
            
            if (parent != null || folder.getName().equals("/")) {
                Element pel = null;
                if (parent != null) {
                    pel = (Element)m_elements.get(parent);
                }
                s_log.debug("Processing " + folder + " " + pel);
                
                if (pel == null && parent != null) {
                    s_log.debug("Oh damn " + folder.getOID());
                    throw new RuntimeException("No elemnent for " + parent.getOID());
                }
                Element el = new Element("cms:folder", CMS.CMS_XML_NS);
                el.addAttribute("name", folder.getName());
                el.addAttribute("label", folder.getLabel());
                el.addAttribute("oid", folder.getOID().toString());
                
                m_elements.put(folder, el);
                String path = (parent == null ? "" : m_paths.get(parent) + "/" + folder.getName());
                m_paths.put(folder, path);
                File dir = new File(m_itemDir, path);
                dir.mkdir();

                if (pel != null) {
                    s_log.debug("Adding " + el + " to " + pel);
                    pel.addContent(el);
                } else if (root == null) {
                    s_log.debug("Is root folder: " + el);
                    root = el;
                }
            } else {
                s_log.warn("Skip " + folder.getOID() + " " + folder.getName());
            }
        }
        
        DataCollection items = session.retrieve(ContentPage.BASE_DATA_OBJECT_TYPE);
        items.addEqualsFilter("isDeleted", Boolean.FALSE);
        items.addEqualsFilter("version", version);
        items.addOrder("ancestors");

        while (items.next()) {
            ContentPage item = (ContentPage)DomainObjectFactory
                .newInstance(items.getDataObject());

            if (item.getObjectType().getQualifiedName().equals("com.arsdigita.london.cms.freeform.FreeformContentItem")) {
                continue;
            }
            
            Folder parent = getParent(item);
            if (s_log.isDebugEnabled()) {
                s_log.debug("Item" + item.getOID() + " parent " + (parent == null ? null : parent.getOID()));
            }
            if (parent == null) {
                s_log.warn("Skipping item " + item.getOID() + " has no parent");
                continue;
            }

            Element pel = (Element)m_elements.get(parent);
            if (pel == null) {
                throw new RuntimeException("No elemnent for " + parent.getOID());
            }

            String path = m_paths.get(parent) + "/" + item.getName() + ".xml";

            m_paths.put(item, path);

            Element el = new Element("cms:external", CMS.CMS_XML_NS);
            el.addAttribute("source", path);

            pel.addContent(el);
            s_log.debug("Adding " + el + " to " + pel + " path");
            
            m_items.add(item.getOID());
        }
        
        Document doc = null;
        try {
            s_log.debug("Got root " + root);
            
            Element imp = new Element("imp:import", "http://xmlns.redhat.com/waf/london/importer/1.0");
            imp.addAttribute("source", systemID);
            imp.addContent(root);
            doc = new Document(imp);
        } catch (ParserConfigurationException ex) {
            throw new UncheckedWrapperException(ex);
        }
        
        try {
            File dst = new File(m_itemDir, "index.xml");
            FileOutputStream os = new FileOutputStream(dst);
            
            os.write(doc.toString(true).getBytes("UTF-8"));
            os.flush();
            os.close();
        } catch (IOException ex) {
            throw new UncheckedWrapperException("cannot write file", ex);
        }
    }
    
    public void exportItems() {
        Iterator oids = m_items.iterator();
        while (oids.hasNext()) {
            TransactionContext txn = SessionManager.getSession()
                .getTransactionContext();
            txn.beginTxn();

            OID oid = (OID)oids.next();
            
            if (s_log.isDebugEnabled()) {
                s_log.info("Exporting item " + oid);
            }
            
            ContentPage item = null;
            try {
                item = (ContentPage)DomainObjectFactory.newInstance(oid);
            } catch (DataObjectNotFoundException ex) {
                throw new UncheckedWrapperException("cannot find item " + oid, ex);
            }
            
            
            Element el = new Element("cms:item", CMS.CMS_XML_NS);                
            DomainObjectExporter exporter = new DomainObjectExporter(el, m_assetDir);
            exporter.setNamespace("cms", CMS.CMS_XML_NS);
            exporter.setWrapAttributes(true);
            exporter.setWrapRoot(false);
            exporter.setWrapObjects(false);
            exporter.walk(item,
                          getClass().getName());
            
            Document doc = null;
            try {
                doc = new Document(el);
            } catch (ParserConfigurationException ex) {
                throw new UncheckedWrapperException(ex);
            }
        
                
            try {
                String path = (String)m_paths.get(item);
                File dst = new File(m_itemDir, "." + path);
                FileOutputStream os = new FileOutputStream(dst);
            
                os.write(doc.toString(true).getBytes("UTF-8"));
                os.flush();
                os.close();
            } catch (IOException ex) {
                throw new UncheckedWrapperException("cannot write file", ex);
            }
            txn.commitTxn();
        }
    }
    
    protected Folder getParent(ContentItem item) {
        if (item instanceof Folder) {
            m_folders.put(item.get(ContentItem.ANCESTORS),
                          item);
        }

        String ancestors = (String)item.get(ContentItem.ANCESTORS);
        
        if (s_log.isDebugEnabled()) {
            s_log.debug("Ancestors " + ancestors);
        }
        
        String base = null;

        if (item instanceof Folder) {
            base = getParentPath(ancestors);
        } else {
            String bundle = getParentPath(ancestors);
            base = bundle == null ? null : getParentPath(bundle);
        }
        if (base == null) {
            return null;
        }
        
        Folder parent = (Folder)m_folders.get(base);
        
        if (s_log.isDebugEnabled()) {
            s_log.warn("Item " + item.getOID() + " with " + ancestors + " parent " + base + " obj " + parent);
        }
        
        return parent;
    }
    
    protected String getParentPath(String path) {
        int offset = path.lastIndexOf("/", path.length() - 2);
        if (offset == -1) {
            s_log.warn("No ancestors parent for " + path);
            return null;
        }
        return path.substring(0, offset+1);
    }
}
