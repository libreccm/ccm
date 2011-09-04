/*
 * Copyright (C) 2011 Peter Boy (pb@zes.uni-bremen.de) All Rights Reserved.
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

package com.arsdigita.ant;


import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.DocumentException;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.xml.SAXReaderUtil;
import com.liferay.portal.xml.DocumentImpl;
import com.liferay.portal.xml.SAXReaderImpl;
import com.liferay.util.xml.XMLMerger;
import com.liferay.util.xml.descriptor.WebXML23Descriptor;
import com.liferay.util.xml.descriptor.WebXML24Descriptor;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.IOException;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
// import org.apache.tools.ant.Project;



/**
 *
 * @author pb
 */
public class WebXMLMergeTask extends Task {
    
    // Instance Variables ------------------------------------------------------
    
    /** Location of the original <code>web.xml</code>.                       */
    private String originalFile;  

    /** Location of the file to merge in <code>web.xml</code>.               */
    private String mergeFile;  

    /** Location of the resulting (destination) <code>web.xml</code>.        */
    private String destFile;

    // Public Methods ----------------------------------------------------------
    
    /**
     * {@inheritDoc}
     * @see Task#execute()
     */
    @Override
    public void execute() throws BuildException {
        
//      /* Check parameters                                                   */
//      if ((this.originalFile == null) || !this.originalFile.isFile())
//      {
//          throw new BuildException("The [originalfile] attribute is required");
//      }
//      if (this.mergeFile == null)
//      {
//          throw new BuildException("The [mergefile] attribute is required");
//      }
//      if (this.destFile == null)
//      {
//          throw new BuildException("The [destfile] attribute is required");
//      }
        
        BuildWebXML(originalFile, mergeFile, destFile);

        
    }

	private void BuildWebXML(String originalWebXML, String customWebXML, 
                             String mergedWebXML) {
        
        try {
            
            String customContent = readFileIntoString(customWebXML);
              
			int x = customContent.indexOf("<web-app");
			x = customContent.indexOf(">", x) + 1;
			int y = customContent.indexOf("</web-app>");
			customContent = customContent.substring(x, y);

            
            String originalContent = readFileIntoString(originalWebXML);

			int z = originalContent.indexOf("<web-app");
			z = originalContent.indexOf(">", z) + 1;


			String mergedContent =
				originalContent.substring(0, z) + customContent +
					originalContent.substring(z, originalContent.length());

			mergedContent = processContent(mergedContent);

        
            writeFileFromString(mergedWebXML, mergedContent);
            
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String processContent(String webXML)
		throws DocumentException, IOException {

        webXML = stripHtmlComments(webXML);
        new SAXReaderUtil().setSAXReader(new SAXReaderImpl());

		double version = 2.3;

		Document doc = SAXReaderUtil.read(webXML);

		Element root = doc.getRootElement();

		version = GetterUtil.getDouble(root.attributeValue("version"), version);

		XMLMerger merger = null;

		if (version == 2.3) {
			merger = new XMLMerger(new WebXML23Descriptor());
		}
		else {
			merger = new XMLMerger(new WebXML24Descriptor());
		}

		DocumentImpl docImpl = (DocumentImpl)doc;

		merger.organizeXML(docImpl.getWrappedDocument());

		webXML = doc.formattedString();

		return webXML;
        
    }

	private String readFileIntoString(String filename)
		throws  IOException {
        
        BufferedReader cbr = new BufferedReader(
                   new InputStreamReader(
                   new FileInputStream(filename))); 
        StringBuilder contentOfFile = new StringBuilder();
        String line; 
        while ((line = cbr.readLine()) != null) {
            contentOfFile.append(line);
            contentOfFile.append('\n');
        }
        
        return(contentOfFile.toString());
    }
    
    private void writeFileFromString(String fileName, String content) 
                 throws  IOException{
        
        FileOutputStream fos = new FileOutputStream(fileName);
        for (int i=0; i < content.length(); i++){
            fos.write((byte)content.charAt(i));
        }
        fos.close();
        
    }

    private String stripHtmlComments(String xmlContent) {
        
        String strippedContent = StringUtil.stripBetween(xmlContent, 
                                                         "<!--", "-->");
        return(strippedContent);
    }

    /**
     * The original web deployment descriptor into which the new elements will
     * be merged.
     * 
     * @param theSrcFile the original <code>web.xml</code>
     */
    public final void setOriginalFile(String originalFile)
    {
        this.originalFile = originalFile;
    }

    /**
     * The descriptor to merge into the original file.
     * 
     * @param theMergeFile the <code>web.xml</code> to merge
     */
    public final void setMergeFile(String mergeFile)
    {
        this.mergeFile = mergeFile;
    }

    /**
     * The destination file where the result of the merge are stored.
     * 
     * @param theDestFile the resulting <code>web.xml</code>
     */
    public final void setDestFile(String destFile)
    {
        this.destFile = destFile;
    }
    
    
    
}
