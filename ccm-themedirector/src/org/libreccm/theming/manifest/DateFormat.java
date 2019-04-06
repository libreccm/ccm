/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.libreccm.theming.manifest;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import static org.libreccm.theming.ThemeConstants.*;

import java.util.Objects;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@XmlRootElement(name = "dateformat", namespace = THEMES_XML_NS)
@XmlAccessorType(XmlAccessType.FIELD)
public class DateFormat {

    @XmlElement(name = "style", namespace = THEMES_XML_NS)
    private String style;

    @XmlElement(name = "lang", namespace = THEMES_XML_NS)
    private String lang;

    @XmlElement(name = "format", namespace = THEMES_XML_NS)
    private String format;

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + Objects.hashCode(lang);
        hash = 17 * hash + Objects.hashCode(style);
        hash = 17 * hash + Objects.hashCode(format);
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
        if (!(obj instanceof DateFormat)) {
            return false;
        }
        final DateFormat other = (DateFormat) obj;
        if (!other.canEqual(this)) {
            return false;
        }
        if (!Objects.equals(lang, other.getLang())) {
            return false;
        }
        if (!Objects.equals(style, other.getStyle())) {
            return false;
        }
        return Objects.equals(format, other.getFormat());
    }

    public boolean canEqual(final Object obj) {

        return obj instanceof DateFormat;
    }

    @Override
    public final String toString() {

        return toString("");
    }

    public String toString(final String data) {

        return String.format("%s{ "
                                 + "style = \"%s\", "
                                 + "lang = \"%s\", "
                                 + "format = \"%s\"%s"
                                 + " }",
                             super.toString(),
                             style,
                             lang,
                             format,
                             data);
    }

}
