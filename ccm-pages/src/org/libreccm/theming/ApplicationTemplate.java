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
@XmlRootElement(name = "application-template", namespace = THEMES_XML_NS)
@XmlAccessorType(XmlAccessType.FIELD)
public class ApplicationTemplate {

    @XmlElement(name = "application-name", namespace = THEMES_XML_NS)
    private String applicationName;

    @XmlElement(name = "application-class", namespace = THEMES_XML_NS)
    private String applicationClass;

    @XmlElement(name = "template", namespace = THEMES_XML_NS)
    private String template;

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(final String applicationName) {
        this.applicationName = applicationName;
    }

    public String getApplicationClass() {
        return applicationClass;
    }

    public void setApplicationClass(final String applicationClass) {
        this.applicationClass = applicationClass;
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
        hash = 79 * hash + Objects.hashCode(applicationName);
        hash = 79 * hash + Objects.hashCode(applicationClass);
        hash = 79 * hash + Objects.hashCode(template);
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
        if (!(obj instanceof ApplicationTemplate)) {
            return false;
        }
        final ApplicationTemplate other = (ApplicationTemplate) obj;
        if (!other.canEqual(this)) {
            return false;
        }
        if (!Objects.equals(applicationName, other.getApplicationName())) {
            return false;
        }
        if (!Objects.equals(applicationClass, other.getApplicationClass())) {
            return false;
        }

        return Objects.equals(template, other.getTemplate());
    }

    public boolean canEqual(final Object obj) {

        return obj instanceof ApplicationTemplate;
    }

    @Override
    public final String toString() {
        return toString("");
    }

    public String toString(final String data) {

        return String.format("%s{ "
                                 + "applicationName = \"%s\", "
                                 + "applicationClass = \"%s\", "
                                 + "template = \"%s\"%s"
                                 + " }",
                             super.toString(),
                             applicationName,
                             applicationClass,
                             template,
                             data
        );
    }

}
