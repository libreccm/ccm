/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.cms.installer.xml;

import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.initializer.Configuration;
import com.arsdigita.initializer.InitializationException;
import com.arsdigita.kernel.BaseInitializer;
import com.arsdigita.kernel.SiteNode;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.TransactionContext;
import com.arsdigita.util.ResourceManager;
import org.apache.log4j.Logger;
import org.apache.oro.text.perl.Perl5Util;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.math.BigDecimal;

/** 
 *  An initializer to load content items for load testing Will take an
 *  XML file with one content item definition, and create items whose
 *  title is "title + number".
 * 
 *  There's an example configuration file in
 *  /WEB-INF/content-items/ContentItemLoader.xml It looks like this:
 * 
 * &lt;ccm:content-items&gt;
 *   &lt;ccm:folder 
 *     clone="3"
 *     depth="2"
 *     label="Folder" &gt;
 *       
 *   &lt;ccm:content-item clone="4"&gt;
 *     &lt;ccm:content-type 
 *       classname="com.arsdigita.cms.dublin.types.DublinArticle"
 *       objectType="com.arsdigita.cms.dublin.types.DublinArticle"/&gt;
 *     
 *     &lt;ccm:item-properties title="item" &gt;
 *       &lt;body-text&gt;
 *       body text
 *       &lt;/body-text&gt;
 *     &lt;/ccm:item-properties&gt;
 *   &lt;/ccm:content-item&gt;
 *   &lt;ccm:content-item clone="1"&gt;
 *    &lt;ccm:content-type 
 *      classname="com.arsdigita.cms.contenttypes.Address"
 *      objectType="com.arsdigita.cms.contenttypes.Address"
 *      helperClass="com.arsdigita.util.cms.ContentPageHelper"/&gt;
 *    
 *    &lt;ccm:item-properties title="address" &gt;
 *        &lt;ccm:item-property method="setAddress" 
 *          argClass="java.lang.String" argValue="338 EustonRoad"/&gt;
 * 
 *        &lt;body-text&gt;
 *          there's no body text
 *        &lt;/body-text&gt;
 *    &lt;/ccm:item-properties&gt;
 *  &lt;/ccm:content-item&gt;
 * 
 *
 * &lt;/ccm:folder&gt;
 * &lt;/ccm:content-items&gt;
 * 
 * This will create a folder tree with 2 levels of folders, each folder
 * will have 3 subfolders and 4 content items.
 * 
 * The XMLContentItemHandler will create any content type. However, it
 * will only process the values for the basic attributes (name, title,
 * body text if it exists).
 * 
 * @author Nobuko Asakai (nasakai@redhat.com)
 * @see XMLContentItemHandler
 * @see ContentItemHelper
*/


public class ContentItemLoader extends BaseInitializer {

    private Configuration m_conf = new Configuration();

    private static Logger s_log =
        Logger.getLogger(ContentItemLoader.class);


    private static final String CONTENT_DEFINITION = "contentItemDef";
    private static final String SECTION = "section";

    public ContentItemLoader() {
        // To be changed to List {{xmlfile} {numbertobeduplicated}}
        m_conf.initParameter(
            CONTENT_DEFINITION,
            "Path to XML definition file for the contents of the content item",
            String.class
        );

        m_conf.initParameter(
            SECTION,
            "Content section where the content items should be created",
            String.class
        );

    }

    // Accessors

    public String getContentSectionParam() {
        return (String)m_conf.getParameter(SECTION);
    }

    public String getContentDefinition() {
        return (String)m_conf.getParameter(CONTENT_DEFINITION);
    }

    public Configuration getConfiguration() {
        return m_conf;
    }
    
    private String getXMLFilePath(String xmlFile) {
        return ResourceManager.getInstance().getServletContext().
                    getRealPath(xmlFile);
    }


    public void doStartup() {
        s_log.warn("Starting ContentItemLoader");
        TransactionContext txn = SessionManager.getSession()
            .getTransactionContext();
        txn.beginTxn();
        String xmlFile = getXMLFilePath(getContentDefinition());

        ContentSection section = getContentSection();

        s_log.debug("Loading XMLFile: " + xmlFile);
        loadContentItems(xmlFile, section);
        txn.commitTxn();
    }

    public void doShutdown() {
        // Do nothing
    }

    public void validateNameParameter(String value)
        throws InitializationException {

        final String pattern = "/[^A-Za-z_0-9\\-]+/";
        Perl5Util util = new Perl5Util();
        if ( util.match(pattern, value) ) {
            throw new InitializationException("The \"" + value + "\" name parameter must contain only alpha-numeric " +
                                              "characters, underscores, and/or hyphens.");
        } 
    }

    public ContentSection getContentSection() throws InitializationException {
        String name = getContentSectionParam();

        validateNameParameter(name);

        BigDecimal rootNodeID = SiteNode.getRootSiteNode().getID();
        SiteNode node = null;
        try {
            node = SiteNode.getSiteNode("/" + name);
        } catch (DataObjectNotFoundException ex) {
            throw new InitializationException( 
                (String) GlobalizationUtil.globalize("cms.installer.root_site_node_missing")
                .localize(),  ex);
        }

        if ( rootNodeID.equals(node.getID()) ) {
            // This instance does not exist yet, abort
            throw new InitializationException("The section does not exist");
        }

        ContentSection section = null;
        try {
            section = ContentSection.getSectionFromNode(node);
        } catch (DataObjectNotFoundException e) {
            throw new InitializationException( 
                (String) GlobalizationUtil.globalize(
                    "cms.installer.failed_to_update_the_default_content_section").localize());
        }

        return section;
    }

    public void loadContentItems(String path, 
                                 ContentSection section) {
        // Make duplicates of the content item
        XMLContentItemHandler handler 
            = new XMLContentItemHandler(section);
            
        try {
            SAXParserFactory spf = SAXParserFactory.newInstance();
            SAXParser parser = spf.newSAXParser();
            parser.parse(path, handler);
        } catch (ParserConfigurationException e) { 
            s_log.error("error parsing content item config", e);
        } catch (SAXException e) { 
            s_log.error("error parsing content item config", e);
        } catch (IOException e) { 
            s_log.error("error parsing content item config", e);
        }
    }


}
