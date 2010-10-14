/*
 * Copyright (c) 2010 Jens Pelzetter,
 * for the Center of Social Politics of the University of Bremen
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
package com.arsdigita.cms.relationattributeimporter;

import java.util.ArrayList;
import java.util.List;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Parser for the XML file. This parser uses the SAX-Parser from the Java
 * Standard API.
 *
 * @author Jens Pelzetter
 */
public class RelationAttributeParser extends DefaultHandler {

    private RelAttrBean currentRelAttr;
    /**
     * A buffer
     */
    private StringBuilder buffer;
    private boolean relAttrOpen;
    private boolean attrOpen;
    private boolean keyOpen;
    private boolean langOpen;
    private boolean nameOpen;
    private boolean descOpen;
    private List<RelAttrBean> relAttrs;

    public RelationAttributeParser() {
        relAttrs = new ArrayList<RelAttrBean>();
        relAttrOpen = false;
        attrOpen = false;
        keyOpen = false;
        langOpen = false;
        nameOpen = false;
        descOpen = false;
    }

    @Override
    public void startDocument() throws SAXException {
        System.out.println("Relation attribute document begin...");
    }

    @Override
    public void endDocument() throws SAXException {
        System.out.println("Relation attribute document end.");
    }

    @Override
    public void startElement(String uri,
                             String localName,
                             String qName,
                             Attributes attributes) throws SAXException {
        System.out.printf("DEBUG: uri       = %s\n", uri);
        System.out.printf("DEBUG: localName = %s\n", localName);
        System.out.printf("DEBUG: qName     = %s\n", qName);
        buffer = new StringBuilder();
        if ("relationAttributes".equals(qName)) {
            //Nothing to do
        } else if ("relationAttribute".equals(qName)) {
            relAttrOpen = true;
            currentRelAttr = new RelAttrBean();
        } else if ("attribute".equals(qName)) {
            attrOpen = true;
        } else if ("key".equals(qName)) {
            keyOpen = true;
        } else if ("lang".equals(qName)) {
            langOpen = true;
        } else if ("name".equals(qName)) {
            nameOpen = true;
        } else if ("description".equals(qName)) {
            descOpen = true;
        } else {
            System.err.printf(
                    "Warning: Encountered unexpected element %s. Ignoring.\n",
                    qName);
        }
    }

    @Override
    public void endElement(String uri,
                           String localName,
                           String qName) throws SAXException {
        if ("relationAttributes".equals(qName)) {
            //Nothing to do
        } else if ("relationAttribute".equals(qName)) {
            relAttrs.add(currentRelAttr);
            currentRelAttr = null;
            relAttrOpen = false;
        } else if ("attribute".equals(qName)) {
            System.out.printf("buffer = %s\n", buffer.toString());
            currentRelAttr.setAttribute(buffer.toString());
            attrOpen = false;
        } else if ("key".equals(qName)) {
            currentRelAttr.setKey(buffer.toString());
            keyOpen = false;
        } else if ("lang".equals(qName)) {
            currentRelAttr.setLang(buffer.toString());
            langOpen = false;
        } else if ("name".equals(qName)) {
            currentRelAttr.setName(buffer.toString());
            nameOpen = false;
        } else if ("description".equals(qName)) {
            currentRelAttr.setDescription(buffer.toString());
            descOpen = false;
        } else {
            System.err.printf(
                    "Warning: Encountered unexpected element %s. Ignoring.\n",
                    qName);
        }
    }

    @Override
    public void characters(char[] ch,
                           int start,
                           int length) throws SAXException {
        System.out.printf("Parsing characters %d to %d...\n", start, (start + length));
        for (int i = start; i < (start + length); i++) {
            char character;
            character = ch[i];
            buffer.append(character);
        }
    }

    @Override
    public void warning(SAXParseException ex) {
        System.err.println(saxMsg(ex));
    }

    @Override
    public void error(SAXParseException ex) {
        System.err.println(saxMsg(ex));
    }

    @Override
    public void fatalError(SAXParseException ex) {
        System.err.println(saxMsg(ex));
    }

    protected List<RelAttrBean> getRelAttrs() {
        return relAttrs;
    }

    private String saxMsg(SAXParseException ex) {
        String msg;

        msg = String.format(
                "SAX parser reported an error at line %d, column %d: %s",
                ex.getLineNumber(),
                ex.getColumnNumber(),
                ex.getMessage());

        return msg;
    }
}
