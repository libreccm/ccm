/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.libreccm.theming;

import static org.libreccm.theming.ThemeConstants.*;

import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@XmlRootElement(name = "contentitem-template", namespace = THEMES_XML_NS)
@XmlAccessorType(XmlAccessType.FIELD)
public class ContentItemTemplate {

    @XmlElement(name = "view", namespace = THEMES_XML_NS)
    private ContentItemViews view;
    
    @XmlElement(name = "contenttype", namespace = THEMES_XML_NS)
    private String contentType;
    
    @XmlElement(name = "style", namespace = THEMES_XML_NS)
    private String style;
    
    @XmlElement(name = "contentsection", namespace = THEMES_XML_NS)
    private String contentSection;
    
    @XmlElement(name = "category", namespace = THEMES_XML_NS)
    private String category;
    
    @XmlElement(name = "template", namespace = THEMES_XML_NS)
    private String template;

    public ContentItemViews getView() {
        return view;
    }

    public void setView(final ContentItemViews view) {
        this.view = view;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(final String contentType) {
        this.contentType = contentType;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(final String style) {
        this.style = style;
    }

    public String getContentSection() {
        return contentSection;
    }

    public void setContentSection(final String contentSection) {
        this.contentSection = contentSection;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(final String category) {
        this.category = category;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(final String template) {
        this.template = template;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 73 * hash + Objects.hashCode(view);
        hash = 73 * hash + Objects.hashCode(contentType);
        hash = 73 * hash + Objects.hashCode(style);
        hash = 73 * hash + Objects.hashCode(contentSection);
        hash = 73 * hash + Objects.hashCode(category);
        hash = 73 * hash + Objects.hashCode(template);
        return hash;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof ContentItemTemplate)) {
            return false;
        }
        final ContentItemTemplate other = (ContentItemTemplate) obj;
        if (!other.canEqual(this)) {
            return false;
        }
        if (!Objects.equals(contentType, other.getContentType())) {
            return false;
        }
        if (!Objects.equals(style, other.getStyle())) {
            return false;
        }
        if (!Objects.equals(contentSection, other.getContentSection())) {
            return false;
        }
        if (!Objects.equals(category, other.getCategory())) {
            return false;
        }
        if (view != other.getView()) {
            return false;
        }
        return Objects.equals(template, other.getTemplate());
    }

    public boolean canEqual(final Object obj) {

        return obj instanceof ContentItemTemplate;
    }

    @Override
    public final String toString() {

        return toString("");
    }

    public String toString(final String data) {

        return String.format("%s{ "
                                 + "contentType = \"%s\", "
                                 + "style = \"%s\", "
                                 + "contentSection = \"%s\", "
                                 + "category = \"%s\""
                                 + "template = \"%s\"%s"
                                 + " }",
                             super.toString(),
                             contentType,
                             style,
                             contentSection,
                             category,
                             template,
                             data);
    }

}
