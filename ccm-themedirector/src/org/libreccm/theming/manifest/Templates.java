/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.libreccm.theming.manifest;


import static org.libreccm.theming.ThemeConstants.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;


/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@XmlRootElement(name = "templates", namespace = THEMES_XML_NS)
@XmlAccessorType(XmlAccessType.FIELD)
public class Templates {
    
    @XmlElementWrapper(name = "applications", namespace = THEMES_XML_NS)
    @XmlElement(name = "applications", namespace = THEMES_XML_NS)
    private List<ApplicationTemplate> applications;
    
    @XmlElement(name = "default-application-template", 
                namespace = THEMES_XML_NS)
    private String defaultApplicationTemplate;
    
    @XmlElementWrapper(name = "contentitems", namespace = THEMES_XML_NS)
    @XmlElement(name = "contentitems", namespace = THEMES_XML_NS)
    private List<ContentItemTemplate> contentItems;
    
    @XmlElement(name = "default-contentitem-template", 
                namespace = THEMES_XML_NS)
    private String defaultContentItemsTemplate;
    
    public Templates() {
        
        applications = new ArrayList<>();
        contentItems= new ArrayList<>();
    }
    
    public List<ApplicationTemplate> getApplications() {
        
        return Collections.unmodifiableList(applications);
    }
    
    public void addApplication(final ApplicationTemplate template) {
        
        applications.add(template);
    }
    
    public void removeApplication(final ApplicationTemplate template) {
        
        applications.remove(template);
    }
    
    public void setApplications(final List<ApplicationTemplate> applications) {
        
        this.applications = new ArrayList<>(applications);
    }
    
    public String getDefaultApplicationTemplate() {
        return defaultApplicationTemplate;
    }
    
    public void setDefaultApplicationTemplate(
        final String defaultApplicationTemplate) {
        this.defaultApplicationTemplate = defaultApplicationTemplate;
    }
    
    public List<ContentItemTemplate> getContentItems() {
        
        return Collections.unmodifiableList(contentItems);
    }
    
    public void addContentItem(final ContentItemTemplate template) {
        
        contentItems.add(template);
    }
    
    public void removeContentItem(final ContentItemTemplate template) {
        
        contentItems.remove(template);
    }
    
    public void setContentItems(final List<ContentItemTemplate> contentItems) {
        
        this.contentItems = new ArrayList<>(contentItems);
    }
    
    public String getDefaultContentItemsTemplate() {
        return defaultContentItemsTemplate;
    }
    
    public void setDefaultContentItemsTemplate(
        final String defaultContentItemsTemplate) {
        this.defaultContentItemsTemplate = defaultContentItemsTemplate;
    }
}
