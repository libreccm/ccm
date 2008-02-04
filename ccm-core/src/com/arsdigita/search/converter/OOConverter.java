/*
 * Copyright (C) 2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.search.converter;

import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import com.arsdigita.xml.Document;
import com.arsdigita.xml.Element;
import com.arsdigita.util.StringUtils;
import java.util.LinkedList;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipEntry;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

/**
 * This class provides the mechanism to perform a conversion from
 * OpenOffice files to standard text.
 */
public class OOConverter extends BaseConverter {
    
    public final static String CONTENT_XML = "content.xml";

    /**
     *  Returns a string array representing all of the files types
     *  used by the given converter
     */
    protected String[] getFileExtensions() {
        String[] extensions = {"sxw", "stw", "sxi", "sti", "sxc", "stc", "sxd", "sxm", "sxg", "std"};
        return extensions;
    }


    /**
     *  This takes in a document represented as an InputStream and returns
     *  a text representation of that document.
     */
    public String convertDocument(InputStream stream) throws ConversionException {
        try {
            // unzip the actual file
            ZipInputStream zipStream = new ZipInputStream(stream);
            ZipEntry entry = zipStream.getNextEntry();
            while (entry != null && !CONTENT_XML.equals(entry.getName())) {
                entry = zipStream.getNextEntry();
            }
            
            if (entry != null) {
                ByteArrayOutputStream output = new ByteArrayOutputStream();
                // we have the content document
                byte[] buf = new byte[1024];
                int position;
                
                while ((position = zipStream.read(buf)) != -1) {
                    output.write(buf, 0, position);
                }
                
                // we have to remove the DOCTYPE definition because if we don't
                // then the parser tries to validate against the "office.dtd"
                // which is not present which causes an error to be thrown.
                Document doc = new Document(StringUtils.replace(new String(output.toByteArray()), "<!DOCTYPE office:document-content PUBLIC \"-//OpenOffice.org//DTD OfficeDocument 1.0//EN\" \"office.dtd\">", ""));
                
                Element element = doc.getRootElement();
                // we don't care about the order so we just go through breadth 
                // first
                StringBuffer outBuf = new StringBuffer();
                LinkedList list = new LinkedList();
                
                list.addAll(element.getChildren());
                while (list.size() > 0) {
                    outBuf.append(element.getText()).append(" ");
                    if (list.size() > 0) {
                        element = (Element)list.removeFirst();
                        list.addAll(element.getChildren());
                    }
                }
                
                return outBuf.toString();
            } else {
                // there is no "content.xml" file so we just give the
                // empty string since we don't know where else to look
                // for content
                return "";
            }
        } catch (IOException ioe) {
            throw new ConversionException(ioe);
        } catch (ParserConfigurationException pce) {
            throw new ConversionException(pce);
        } catch (SAXException se) {
            throw new ConversionException(se);
        } catch (Exception ex) {
            throw new ConversionException(ex);
        }
    }
} 
