/*
 * Copyright (C) 2013 Jens Pelzetter
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
 *
 */
package com.arsdigita.cms;

import com.arsdigita.xml.XML;
import java.math.BigDecimal;
import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

/**
 * This is a helper tool for loading data for the RelationAttributes (Database Driven Enums) 
 * into the database in a loader of a module. This helper class uses a XML format for loading 
 * the enum data, which looks like this:
 * 
 * <pre>
 * &lt;ddenums&gt;
 *     &lt;ddenum name="..."&gt;
 *         &lt;entry key="..." lang="..." id="..."&gt;
 *            &lt;value&gt;...&lt;/value&gt;
 *            &lt;description&gt;...&lt;/description&gt;
 *         &lt;entry&gt;
 *     &lt;/ddenum&gt;
 * &lt;/ddenums&gt;
 * </pre>
 * 
 * The root element is {@code <ddenums>} which can appear only once per file. The {@code <ddenums} can have multiple
 * {@code <ddenum>} elements as child elements. The {@code <ddenum> element has one attribute, {@code name} which contains
 * the name of the enumeration. Each {@code <ddenum>} may have multiple {@code<entry>} child elements. The 
 * {@code <entry>} element has three attributes.
 * 
 * <dl>
 *   <dt>{@code key}</dt><dd>The key of the entry. This attribute is mandatory.</dd>
 *   <dt>{@code lang}</dt><dd>The language of the entry. This attribute is mandatory. The combination of {@code key}
 *   and {@code lang} should be unique.</dd>
 *   <dt>{@code id}</dt><dd>This attribute is optional and contains the database id of the entry if necessary. The
 *   value is maybe ignored by this import tool.</dd>
 * </dl>
 * 
 * Each entry has at least a {@code <value>} element as child. The {@code <value>} element contains the value of the
 * enum entry. Optionally there can also be can description element containing a description of the entry.
 * 
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class RelationAttributeImportTool {

    private final static Logger LOGGER = Logger.getLogger(RelationAttributeImportTool.class);

    public void loadData(final String fileName) {

        XML.parseResource(fileName, new RelationAttributeXmlHandler());

    }

    //Suppressing this warnings because they are false positives here.
    @SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.AvoidStringBufferField"})
    private class RelationAttributeXmlHandler extends DefaultHandler {

        private String currentEnum;
        private String currentKey;
        private String currentLang;
        private String currentId;
        private String currentValue;
        private String currentDesc;
        private StringBuffer charBuffer;

        public RelationAttributeXmlHandler() {
            super();
            //Nothing
        }

        @Override
        public void startElement(final String uri,
                                 final String localName,
                                 final String qName,
                                 final Attributes attributes) {
            if ("ddenum".equals(qName)) {
                currentEnum = attributes.getValue("name");
            } else if ("entry".equals(qName)) {
                currentKey = attributes.getValue("key");
                currentLang = attributes.getValue("lang");
                currentId = attributes.getValue("id");
            } else if ("value".equals(qName)) {
                //Create new StringBuffer of creating a string from the content of the element
                charBuffer = new StringBuffer();
            } else if ("description".equals(qName)) {
                //Create new StringBuffer of creating a string from the content of the element
                charBuffer = new StringBuffer();
            }
        }

        @Override
        public void endElement(final String uri,
                               final String localName,
                               final String qName) {
            if ("ddenum".equals(qName)) {
                currentEnum = "";
            } else if ("entry".equals(qName)) {
                createEntry();
                currentKey = "";
                currentLang = "";
                currentId = "";
                currentValue = "";
                currentDesc = "";
            } else if ("value".equals(qName)) {
                //Copy the value of the StringBuffer charBuffer to currentValue
                currentValue = charBuffer.toString().trim();
                charBuffer = new StringBuffer();
            } else if ("description".equals(qName)) {
                //Copy the value of the StringBuffer charBuffer to currentValue
                currentDesc = charBuffer.toString().trim();
                charBuffer = new StringBuffer();
            }
        }

        @Override
        public void characters(final char[] chars, final int start, final int length) {
            if (charBuffer != null) {
                for(int i = start; i < start + length; i++) {
                charBuffer.append(chars[i]);
                }
            }
        }

        //Supressing this warning since there is no better way yet.
        @SuppressWarnings("PMD.NPathComplexity")
        private void createEntry() {
            if ((currentEnum == null) || currentEnum.isEmpty()) {
                LOGGER.warn(String.format(
                        "Value for current enum is empty. Ignorning entry with key '%s' and lang '%s'.",
                        currentKey,
                        currentLang));
                return;
            }

            if ((currentKey == null) || currentKey.isEmpty()) {
                LOGGER.warn("No key. Ignorning entry");
                return;
            }

            if ((currentLang == null) || currentLang.isEmpty()) {
                LOGGER.warn(String.format("No lang for entry with key '%s'. Ignoring entry",
                                          currentKey));
                return;
            }
            
            LOGGER.warn("Creating RelationAttribute entry with this values:");
            LOGGER.warn(String.format("\tcurrentEnum  = '%s'", currentEnum));
            LOGGER.warn(String.format("\tcurrentKey   = '%s'", currentKey));
            LOGGER.warn(String.format("\tcurrentLang  = '%s'", currentLang));
            LOGGER.warn(String.format("\tcurrentValue = '%s'", currentValue));
            
            final RelationAttribute entry = new RelationAttribute();
            if ((currentId != null) && !currentId.isEmpty()) {
                entry.setID(new BigDecimal(currentId));
            }                                    
            
            entry.setAttribute(currentEnum);
            entry.setKey(currentKey);
            entry.setLanguage(currentLang);
            entry.setName(currentValue);

            if ((currentDesc != null) && !currentValue.isEmpty()) {
                entry.setDescription(currentDesc);
            }

            entry.save();
        }

    }
}
