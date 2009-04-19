/*
 * Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
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
 */

package com.arsdigita.london.exporter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;

import com.arsdigita.categorization.Category;
import com.arsdigita.categorization.CategoryNotFoundException;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ContentPage;
import com.arsdigita.domain.DomainCollection;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.xml.Document;
import com.arsdigita.xml.Element;

public class CategoryExporter {
    
    private static final Logger s_log = Logger.getLogger(CategoryExporter.class);

    public static final String TERMS_XML_NS
        = "http://xmlns.redhat.com/london/terms/1.0";

    private File m_expDir;

    private Map m_idMaps;

    public CategoryExporter(File expDir) {
        m_expDir = expDir;
        m_idMaps = new HashMap();
    }
    
    public void export(Category root,
            String key,
            URI url,
            String title,
            String description,
            String version,
            Date released) {
        
        export(root,
               key,
               url,
               title,
               description,
               version,
               released,
               true);
    }
    
    public void export(Category root,
                       String key,
                       URI url,
                       String title,
                       String description,
                       String version,
                       Date released,
                       boolean exportItems) {
        exportDomain(root, key, url, title, description, version, released);
        exportHierarchy(root, key, url);
        if (exportItems) {
            exportItems(root, key, url);  
        }
    }
    
    
    private void exportDomain(Category root,
                              String key,
                              URI url,
                              String title,
                              String description,
                              String version,
                              Date released) {
        Map idMap = getIDMap(url);
        
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        Element domain = new Element("terms:domain",
                                   TERMS_XML_NS);
        domain.addAttribute("about", url.toString());
        domain.addAttribute("key", key);
        domain.addAttribute("title", title);
        domain.addAttribute("version", version);
        domain.addAttribute("released", format.format(released));

        Element desc = domain.newChildElement("terms:description",
                                            TERMS_XML_NS);
        desc.setText(description);

        DomainCollection cats = root.getDescendants();
        while (cats.next()) {
            Category cat = (Category)cats.getDomainObject();
            int id = idMap.size() + 1;
            idMap.put(cat, new Integer(id));
            if (!cat.equals(root)) {
                Element term = domain.newChildElement("terms:term",
                        TERMS_XML_NS);
                term.addAttribute("id", Integer.valueOf(id).toString());
                term.addAttribute("name", cat.getName());
                term.addAttribute("inAtoZ", "false");
            }
        }
        
        save(domain, 
             key + "-domain.xml");
    }
    
    private void exportHierarchy(Category root,
                                 String key,
                                 URI url) {
        Map idMap = getIDMap(url);

        Element hier = new Element("terms:hierarchy",
                                     TERMS_XML_NS);
        
        Element domain = hier.newChildElement("terms:domain",
                                              TERMS_XML_NS);
        domain.addAttribute("resource", url.toString());

        DomainCollection children = root.getChildren();
        while (children.next()) {
            Category child = (Category)children.getDomainObject();
            
            Element term = hier.newChildElement("terms:term",
                                                TERMS_XML_NS);
            term.addAttribute("id", idMap.get(child).toString());
        }
        
        children = root.getChildren();
        while (children.next()) {
            Category child = (Category)children.getDomainObject();
            exportChildren(idMap, child, hier);
        }
        
        save(hier, key + "-hierarchy.xml");
    }
    
    
    private void exportChildren(Map idMap,
                                Category cat,
                                Element root) {
        DomainCollection children = cat.getChildren();
        while (children.next()) {
            Category child = (Category)children.getDomainObject();
            exportPair(idMap, cat, child, root);
        }

        children = cat.getChildren();
        while (children.next()) {
            Category child = (Category)children.getDomainObject();
            exportChildren(idMap, child, root);
        }
    }
    
    private void exportPair(Map idMap,
                            Category srcCat,
                            Category dstCat,
                            Element root) {                
        Element pair = root.newChildElement("terms:orderedPair",
                                            TERMS_XML_NS);
        Element src = pair.newChildElement("terms:source",
                                           TERMS_XML_NS);
        Element srcTerm = src.newChildElement("terms:term",
                                              TERMS_XML_NS);
        srcTerm.addAttribute("id", idMap.get(srcCat).toString());
        
        Element dst = pair.newChildElement("terms:destination",
                                           TERMS_XML_NS);
        Element dstTerm = dst.newChildElement("terms:term",
                                              TERMS_XML_NS);
        dstTerm.addAttribute("id", idMap.get(dstCat).toString());
            
        try {
            if (dstCat.getDefaultParentCategory().equals(srcCat)) {
                dst.addAttribute("isDefault", "true");
            } else {
                dst.addAttribute("isDefault", "false");
            }
        } catch (CategoryNotFoundException e) {
            dst.addAttribute("isDefault", "false");
        }
        dst.addAttribute("isPreferred", "true");
    }
    
    private void exportItems(Category root,
                             String key,
                             URI url) {
        Map idMap = getIDMap(url);
        
        Element items = new Element("terms:itemMapping",
                                    TERMS_XML_NS);
        Element domain = items.newChildElement("terms:domain",
                                               TERMS_XML_NS);
        domain.addAttribute("resource", url.toString());

        DomainCollection children = root.getDescendants();
        while (children.next()) {
            Category cat = (Category)children.getDomainObject();
            
            DomainCollection objs = cat.getObjects(ContentPage.BASE_DATA_OBJECT_TYPE,
                                                   "parents");
            while (objs.next()) {
                ContentPage obj = (ContentPage)objs.getDomainObject();

                Element mapping = items.newChildElement("terms:mapping",
                                                        TERMS_XML_NS);
                
                Element term = mapping.newChildElement("terms:term",
                                                       TERMS_XML_NS);
                term.addAttribute("id", idMap.get(cat).toString());
                
                Element item = mapping.newChildElement("terms:item",
                                                       TERMS_XML_NS);
                item.addAttribute("path", 
                                  obj.getContentSection().getPath() + "/" + 
                                  ((ContentItem)obj).getPathNoJsp());
            }
        }
        
        save(items, key + "-items.xml");
    }
    
    private Map getIDMap(URI url) {
        if (!m_idMaps.containsKey(url)) {
            m_idMaps.put(url, new HashMap());
        }
        return (Map)m_idMaps.get(url);
    }

    private void save(Element root,
                      String filename) {
        Document doc = null;
        try {
            s_log.debug("Got root " + root);
            doc = new Document(root);
        } catch (ParserConfigurationException ex) {
            throw new UncheckedWrapperException(ex);
        }
        
        try {
            File dst = new File(m_expDir, filename);
            FileOutputStream os = new FileOutputStream(dst);
            
            os.write(doc.toString(true).getBytes("UTF-8"));
            os.flush();
            os.close();
        } catch (IOException ex) {
            throw new UncheckedWrapperException("cannot write file", ex);
        }
    }
}
