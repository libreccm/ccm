/*
 * Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
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

package com.arsdigita.rssfeed;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Date;
import java.text.SimpleDateFormat;

import com.arsdigita.categorization.Category;
import com.arsdigita.categorization.CategorizedCollection;
import com.arsdigita.categorization.ui.CategorizationTree;
import com.arsdigita.cms.ContentBundle;
import com.arsdigita.cms.ContentPage;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.kernel.URLService;
import com.arsdigita.web.URL;
import com.arsdigita.web.ParameterMap;

import com.arsdigita.london.terms.Domain;
import com.arsdigita.london.terms.Term;
import com.arsdigita.domain.DomainCollection;
import com.arsdigita.domain.DomainCollectionIterator;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.output.XMLOutputter;
import org.jdom.ProcessingInstruction;
import com.arsdigita.kernel.User;


/**
 * Methods for generating RSS Channels &amp; Items.
 *
 * This class has been extended to also generate the LAWs syndication standard
 * extensions to RSS 1.0. The implementation is very basic.
 *
 * @author Scott Seago (sseago@redhat.com)
 * @author Daniel Berrange (berrange@redhat.com)
 * @author Matthew Booth (mbooth@redhat.com)
 * @author Oli Sharpe (oli@gometa.co.uk)
 * @version $Revision: #17 $, $Date: 2004/03/29 $
 */
public class RSSService {
    
    private static org.apache.log4j.Logger s_log =
            org.apache.log4j.Logger.getLogger(RSSService.class);
    
    private static final RSSFeedConfig s_config = RSSFeedConfig.getConfig();
        
    public static RSSFeedConfig getConfig() {
        return s_config;
    }
    
    /**
     * Generates an RSS channel for a specified category and and all of its Articles.
     */
    public static void generateChannel(
            BigDecimal categoryId,
            HttpServletRequest request,
            HttpServletResponse response)
            throws Exception {
        Category cat = new Category(categoryId);
        
        boolean useLAWs = "laws-1.0".equals(request.getParameter("extension"));
        
        boolean useESD = "laws-esd".equals(request.getParameter("extension"));
        if (useESD) {
            useLAWs = true;
        }
        // The two namespaces used for basic rdf. rssNS is the default namespace
        // for all elements.
        Namespace rdfNS =
                Namespace.getNamespace(
                "rdf",
                "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
        Namespace rssNS = Namespace.getNamespace("http://purl.org/rss/1.0/");
        
        // The following namespaces are declared for the possible
        // use of the LAWS extension
        Namespace dcNS =
                Namespace.getNamespace("dc", "http://purl.org/dc/elements/1.1/");
        Namespace egmsNS = null;
        if (useESD) {
            egmsNS =
                    Namespace.getNamespace(
                    "esd",
                    "http://www.esd.org.uk/standards/esd/3.0/esd.rdfs");
        } else {
            egmsNS =
                    Namespace.getNamespace(
                    "egms",
                    "http://www.esd.org.uk/standards/egms/3.0/egms.rdfs");
        }
        Namespace lgclNS =
                Namespace.getNamespace(
                "lgcl",
                "http://www.esd.org.uk/standards/lgcl/1.03/lgcl.rdfs");
        
        // rdf is the root element
        Element rdf = new Element("RDF", "rdf", rdfNS.getURI());
        rdf.addNamespaceDeclaration(rssNS);
        
        if (useLAWs) {
            rdf.addNamespaceDeclaration(dcNS);
            rdf.addNamespaceDeclaration(egmsNS);
            rdf.addNamespaceDeclaration(lgclNS);
        }
        // Channel info
        Element channel = new Element("channel", rssNS);
        channel.setAttribute(
                "about",
                URL.here(request, "/rss/").getURL(),
                rdfNS);
        rdf.addContent(channel);
        
        Element channelTitle = new Element("title", rssNS);
        channelTitle.setText(cat.getName());
        channel.addContent(channelTitle);
        
        if (useLAWs) {
            Element channelDCTitle = new Element("title", dcNS);
            channelDCTitle.setText(cat.getName());
            channel.addContent(channelDCTitle);
        }
        
        Element channelLink = new Element("link", rssNS);
        channelLink.setText((URL.there(request,null).getServerURI()).concat(URLService.locate(cat.getOID())));
        channel.addContent(channelLink);
        
        Element channelDescription = new Element("description", rssNS);
        channelDescription.setText(cat.getDescription());
        channel.addContent(channelDescription);
        
        Element channelItems = new Element("items", rssNS);
        channel.addContent(channelItems);
        
        Element itemsSeq = new Element("Seq", rdfNS);
        channelItems.addContent(itemsSeq);
        
        // Get and store a list of items. Items urls are added to the list in
        // the channel info, and a complete entry is added at the top level
        // (below rdf)
        SortedSet items = new TreeSet();
        CategorizedCollection objects =
                cat.getObjects(ContentItem.BASE_DATA_OBJECT_TYPE);
        while (objects.next()) {
            ContentItem item = (ContentItem) objects.getACSObject();
            
            s_log.debug("item: " + item.getDisplayName());
            if (ContentItem.LIVE.equals(item.getVersion())) {
                items.add(new NewestFirstItem(item));
            }
        }
        
        Iterator iter = items.iterator();
        int max = 10;
        int current = 0;
        while (iter.hasNext()) {
            current++;
            if (current > max) {
                break;
            }
            NewestFirstItem itemWrapper = (NewestFirstItem) iter.next();
            ContentItem item = itemWrapper.getContentItem();
            
            String title;
            String description = "";
            try {
                // In Aplaws+, only content bundles get categorised
                ContentItem primary = ((ContentBundle) item).getPrimaryInstance();
                try {
                    ContentPage page = (ContentPage) primary;
                    title = page.getTitle();
                    description = page.getSearchSummary();
                } catch (ClassCastException e) {
                    title = primary.getDisplayName();
                }
            } catch (ClassCastException e) {
                title = item.getDisplayName();
            }
            
            String itemURL = (URL.there(request,null).getServerURI()).concat(URLService.locate(item.getOID()));
            
            s_log.debug("item is live");
            
            // Add the element to the channel list
            Element seqEl = new Element("li", rdfNS);
            seqEl.setAttribute("resource", itemURL, rdfNS);
            itemsSeq.addContent(seqEl);
            
            // Add the element to the top level
            Element itemEl = new Element("item", rssNS);
            itemEl.setAttribute("about", itemURL, rdfNS);
            rdf.addContent(itemEl);
            
            Element titleEl = new Element("title", rssNS);
            titleEl.setText(title);
            itemEl.addContent(titleEl);
            
            Element linkEl = new Element("link", rssNS);
            linkEl.setText(itemURL);
            itemEl.addContent(linkEl);
            
            if (description != null) {
                Element descEl = new Element("description", rssNS);
                descEl.setText(
                        com.arsdigita.util.StringUtils.truncateString(
                        description,
                        100,
                        true)
                        + "...");
                itemEl.addContent(descEl);
            }
            
            if (useLAWs) {
                
                Element dcTitleEl = new Element("title", dcNS);
                dcTitleEl.setText(title);
                itemEl.addContent(dcTitleEl);
                
                User creatorUser = item.getCreationUser();
                String creator = "Not specified";
                if (creatorUser != null) {
                    creator = creatorUser.getName();
                }
                Element dcCreatorEl = new Element("creator", dcNS);
                dcCreatorEl.setText(creator);
                itemEl.addContent(dcCreatorEl);
                
                Date dcDate = item.getCreationDate();
                String dcDateString = "Not specified";
                if (dcDate != null) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    dcDateString = sdf.format(dcDate);
                }
                
                Element dcDateEl = new Element("date", dcNS);
                dcDateEl.setText(dcDateString);
                itemEl.addContent(dcDateEl);
                
                Element subjectCategoryEl =
                        new Element("subjectCategory", egmsNS);
                itemEl.addContent(subjectCategoryEl);
                
                Element subjectBagEl = new Element("Bag", rdfNS);
                subjectCategoryEl.addContent(subjectBagEl);
                
                Element liEl;
                Element categoryEl;
                Element rdfValueEl;
                
                // OK now we are going to see if we can find any
                // LGCL categories for this item:
                
                Domain lgclDomain = Domain.retrieve("LGCL");
                
                DomainCollection terms = lgclDomain.getTerms();
                terms.addEqualsFilter("model.childObjects.id", item.getID());
                
                if (terms != null) {
                    DomainCollectionIterator it =
                            new DomainCollectionIterator(terms);
                    
                    while (it.hasNext()) {
                        
                        Term term = (Term) it.next();
                        String name = term.getName();
                        String urlName = toUpperCamel(name);
                        
                        liEl = new Element("li", rdfNS);
                        subjectBagEl.addContent(liEl);
                        
                        categoryEl = new Element(urlName, lgclNS);
                        liEl.addContent(categoryEl);
                        
                        rdfValueEl = new Element("value", rdfNS);
                        rdfValueEl.setText(name);
                        categoryEl.addContent(rdfValueEl);
                    }
                }
                
            }
        }
        
        // Write XML to the output stream
        Document doc = new Document();
        
        if (getConfig().getPIxslt()!= null) {
            doc.addContent(new ProcessingInstruction("xml-stylesheet","type=\"text/xsl\" href=\"" + getConfig().getPIxslt() + "\""));
        }
        doc.setRootElement(rdf);
        
        response.setContentType("text/xml; charset=UTF-8");
        
        XMLOutputter xmlOutput = new XMLOutputter("UTF-8");
        xmlOutput.setNewlines(true);
        xmlOutput.setIndent(true);
        xmlOutput.output(doc, response.getWriter());
    }
    
    private static class NewestFirstItem implements Comparable {
        private ContentItem m_item;
        private BigDecimal m_liveID;
        
        public NewestFirstItem(ContentItem item) {
            m_item = item;
            
            m_liveID = item.getID();
        }
        
        public ContentItem getContentItem() {
            return m_item;
        }
        
        public BigDecimal getLiveID() {
            return m_liveID;
        }
        
        public int compareTo(Object o) {
            if ((o instanceof NewestFirstItem)) {
                return
                        - 1 * (m_liveID.compareTo(((NewestFirstItem) o).getLiveID()));
            } else {
                throw new ClassCastException("Must compare to NewestFirstItem");
            }
        }
        
        public boolean equals(Object o) {
            if ((o instanceof NewestFirstItem)) {
                return m_item.equals(((NewestFirstItem) o).getContentItem());
            } else {
                return false;
            }
        }
    }
    
    /**
     * Generates an RSS channel for a specified category purpose
     */
    public static void generateChannelList(
            Category root,
            HttpServletRequest request,
            HttpServletResponse response)
            throws Exception {
        
        // The two namespaces used for basic rdf. rssNS is the default namespace
        // for all elements.
        Namespace rdfNS =
                Namespace.getNamespace(
                "rdf",
                "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
        Namespace rssNS = Namespace.getNamespace("http://purl.org/rss/1.0/");
        
        // rdf is the root element
        Element rdf = new Element("RDF", "rdf", rdfNS.getURI());
        rdf.addNamespaceDeclaration(rssNS);
        
        // Channel info
        Element channel = new Element("channel", rssNS);
        channel.setAttribute(
                "about",
                URL.here(request, "/rss/").getURL(),
                rdfNS);
        rdf.addContent(channel);
        
        Element channelTitle = new Element("title", rssNS);
        channelTitle.setText("Channel Index");
        channel.addContent(channelTitle);
        
        Element channelLink = new Element("link", rssNS);
        channelLink.setText(URL.here(request, "/rss/").getURL());
        channel.addContent(channelLink);
        
        Element channelDescription = new Element("description", rssNS);
        channelDescription.setText("The list of content feeds");
        channel.addContent(channelDescription);
        
        Element channelItems = new Element("items", rssNS);
        channel.addContent(channelItems);
        
        Element itemsSeq = new Element("Seq", rdfNS);
        channelItems.addContent(itemsSeq);
        
        Map cats = CategorizationTree.getSubtreePath(root);
        s_log.debug("Get categories");
        Iterator i = cats.keySet().iterator();
        s_log.debug("About to iterate");
        while (i.hasNext()) {
            String path = (String) i.next();
            Category cat = (Category) cats.get(path);
            
            if (cat.getID().equals(root.getID())) {
                continue;
            }
            
            s_log.debug("GOt sub cat " + path + " id " + cat.getID());
            ParameterMap params = new ParameterMap();
            params.setParameter("id", cat.getID());
            URL url = URL.here(request, "/rss/channel.rss", params);
            
            // Add the element to the channel list
            Element seqEl = new Element("li", rdfNS);
            seqEl.setAttribute("resource", url.getURL(), rdfNS);
            itemsSeq.addContent(seqEl);
            
            // Add the element to the top level
            Element itemEl = new Element("item", rssNS);
            itemEl.setAttribute("about", url.getURL(), rdfNS);
            rdf.addContent(itemEl);
            
            Element titleEl = new Element("title", rssNS);
            titleEl.setText(path);
            itemEl.addContent(titleEl);
            
            Element linkEl = new Element("link", rssNS);
            linkEl.setText(url.getURL());
            itemEl.addContent(linkEl);
            
            if (cat.getDescription() != null) {
                Element descEl = new Element("description", rssNS);
                descEl.setText(cat.getDescription());
                itemEl.addContent(descEl);
            }
        }
        s_log.debug("All done");
        
        // Write XML to the output stream
        Document doc = new Document(rdf);
        
        response.setContentType("text/xml; charset=UTF-8");
        
        XMLOutputter xmlOutput = new XMLOutputter("UTF-8");
        xmlOutput.setNewlines(true);
        xmlOutput.setIndent(true);
        xmlOutput.output(doc, response.getWriter());
    }
    
    /**
     * Generates an RSS channel for a specified category and and all of its Articles.
     */
    public static void generateFeedList(
            boolean acsj,
            HttpServletRequest request,
            HttpServletResponse response)
            throws Exception {
        // The two namespaces used for basic rdf. rssNS is the default namespace
        // for all elements.
        Namespace rdfNS =
                Namespace.getNamespace(
                "rdf",
                "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
        Namespace rssNS = Namespace.getNamespace("http://purl.org/rss/1.0/");
        
        // rdf is the root element
        Element rdf = new Element("RDF", "rdf", rdfNS.getURI());
        rdf.addNamespaceDeclaration(rssNS);
        
        // Channel info
        Element channel = new Element("channel", rssNS);
        channel.setAttribute(
                "about",
                URL.here(request, "/rss/").getURL(),
                rdfNS);
        rdf.addContent(channel);
        
        Element channelTitle = new Element("title", rssNS);
        channelTitle.setText("Channel Index");
        channel.addContent(channelTitle);
        
        Element channelLink = new Element("link", rssNS);
        channelLink.setText(URL.here(request, "/rss/").getURL());
        channel.addContent(channelLink);
        
        Element channelDescription = new Element("description", rssNS);
        channelDescription.setText("The list of server feeds");
        channel.addContent(channelDescription);
        
        Element channelItems = new Element("items", rssNS);
        channel.addContent(channelItems);
        
        Element itemsSeq = new Element("Seq", rdfNS);
        channelItems.addContent(itemsSeq);
        
        FeedCollection feeds = Feed.retrieveAll();
        feeds.filterACSJFeeds(acsj);
        
        while (feeds.next()) {
            Feed feed = feeds.getFeed();
            
            // Add the element to the channel list
            Element seqEl = new Element("li", rdfNS);
            seqEl.setAttribute("resource", feed.getURL(), rdfNS);
            itemsSeq.addContent(seqEl);
            
            // Add the element to the top level
            Element itemEl = new Element("item", rssNS);
            itemEl.setAttribute("about", feed.getURL(), rdfNS);
            rdf.addContent(itemEl);
            
            Element titleEl = new Element("title", rssNS);
            titleEl.setText(feed.getTitle());
            itemEl.addContent(titleEl);
            
            Element linkEl = new Element("link", rssNS);
            linkEl.setText(feed.getURL());
            itemEl.addContent(linkEl);
            
            String desc = feed.getDescription();
            if (desc != null) {
                Element descEl = new Element("description", rssNS);
                descEl.setText(desc);
                itemEl.addContent(descEl);
            }
            
        }
        
        // Write XML to the output stream
        Document doc = new Document(rdf);
        
        response.setContentType("text/xml; charset=UTF-8");
        
        XMLOutputter xmlOutput = new XMLOutputter("UTF-8");
        xmlOutput.setNewlines(true);
        xmlOutput.setIndent(true);
        xmlOutput.output(doc, response.getWriter());
    }
    
    public static String toUpperCamel(String termName) {
        
        String upperCamel = "";
        
        if (termName != null) {
            
            StringTokenizer tokens = new StringTokenizer(termName);
            while (tokens.hasMoreTokens()) {
                
                String word = tokens.nextToken();
                if (word.length() <= 1) {
                    upperCamel += word.toUpperCase();
                } else {
                    upperCamel += word.substring(0, 1).toUpperCase()
                    + word.substring(1, word.length());
                }
                
            }
        }
        
        return upperCamel;
    }
}
