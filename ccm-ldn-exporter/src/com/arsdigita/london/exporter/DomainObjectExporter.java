
package com.arsdigita.london.exporter;

import com.arsdigita.domain.DomainObjectXMLRenderer;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.domain.DomainServiceInterfaceExposer;
import com.arsdigita.persistence.metadata.Property;
import com.arsdigita.xml.Element;
import com.arsdigita.cms.Asset;
import com.arsdigita.util.UncheckedWrapperException;

import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import org.apache.log4j.Logger;

public class DomainObjectExporter extends DomainObjectXMLRenderer {

    public static final Logger s_log = Logger.getLogger(DomainObjectExporter.class);

    private File m_assetDir;

    public DomainObjectExporter(Element root,
                                File assetDir) {
        super(root);
        
        m_assetDir = assetDir;
    }
    
    public void handleAttribute(DomainObject obj,
                                String path,
                                Property property) {
        if ("Blob".equals(property.getType().getQualifiedName())) {
            String filename = getAssetFileName(obj, path, property);
            File file = new File(m_assetDir, filename);
            
            if (s_log.isDebugEnabled()) {
                s_log.debug("Creating asset " + file + " for " + obj.getOID() + 
                            " " + path + " " + property.getName());
            }

            FileOutputStream os = null;
            try {
                os = new FileOutputStream(file);
            } catch (FileNotFoundException ex) {
                throw new UncheckedWrapperException("cannot open file " + file, ex);
            }
            
            byte[] bytes = (byte[])DomainServiceInterfaceExposer.get(obj, property.getName());
            try {
                os.write(bytes);
                os.flush();
                os.close();
            } catch (IOException ex) {
                throw new UncheckedWrapperException("cannot write to file " + file, ex);
            }
            
            if (isWrappingAttributes()) {
                Element element = getCurrentElement().newChildElement(property.getName());
                element.addAttribute("file", filename);
            } else {
                getCurrentElement().addAttribute(property.getName(), 
                                                 filename);
            }
        } else {
            super.handleAttribute(obj, path, property);
        }
    }
    
    protected String getAssetFileName(DomainObject obj,
                                      String path,
                                      Property property) {
        Asset asset = (Asset)obj;
        return property.getName() + "-" + asset.getID() + "-" + asset.getName();
    }
}
