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
    

import com.arsdigita.xml.Element;
import com.arsdigita.util.UncheckedWrapperException;


/**
 * Highly experimental. This API will definitely
 * change, possibly beyond all recognition.
 */
public class RSSRenderer {
    
    public final static org.jdom.Namespace s_rdfNS = org.jdom.Namespace.getNamespace
        ( "rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#" );
    public final static org.jdom.Namespace s_rssNS = org.jdom.Namespace.getNamespace
        ( "http://purl.org/rss/1.0/" );
    
    /**
     * 
     * @param channel
     * @return 
     */
    public static org.jdom.Element generateJDOM(RSSChannel channel) {
        // rdf is the root element
        org.jdom.Element rdfEl = new org.jdom.Element( "RDF", "rdf", s_rdfNS.getURI() );
        rdfEl.addNamespaceDeclaration( s_rssNS );

        // Channel info
        org.jdom.Element channelEl = new org.jdom.Element( "channel", s_rssNS );
        channelEl.setAttribute( "about", channel.getLink(), s_rdfNS );
        rdfEl.addContent( channelEl );

        org.jdom.Element channelTitleEl = new org.jdom.Element( "title", s_rssNS );
        channelTitleEl.setText( channel.getTitle() );
        channelEl.addContent( channelTitleEl );

        org.jdom.Element channelLinkEl = new org.jdom.Element( "link", s_rssNS );
        channelLinkEl.setText( channel.getLink() );
        channelEl.addContent( channelLinkEl );

        org.jdom.Element channelDescriptionEl = new org.jdom.Element( "description", s_rssNS );
        channelDescriptionEl.setText( channel.getDescription() );
        channelEl.addContent( channelDescriptionEl );

        org.jdom.Element channelItemsEl = new org.jdom.Element( "items", s_rssNS );
        channelEl.addContent( channelItemsEl );

        org.jdom.Element itemsSeqEl = new org.jdom.Element( "Seq", s_rdfNS );
        channelItemsEl.addContent( itemsSeqEl );


        RSSItemCollection items = channel.getItems();
        while (items.next()) {
            RSSItem item = items.getItem();

            // Add the element to the channel list
            org.jdom.Element seqEl = new org.jdom.Element( "li", s_rdfNS );
            seqEl.setAttribute( "resource", item.getLink(), s_rdfNS );
            itemsSeqEl.addContent( seqEl );

            // Add the element to the top level
            org.jdom.Element itemEl = new org.jdom.Element( "item", s_rssNS );
            itemEl.setAttribute( "about", item.getLink(), s_rdfNS );
            rdfEl.addContent( itemEl );

            org.jdom.Element titleEl = new org.jdom.Element( "title", s_rssNS );
            titleEl.setText( item.getTitle());
            itemEl.addContent( titleEl );

            org.jdom.Element linkEl = new org.jdom.Element( "link", s_rssNS );
            linkEl.setText( item.getLink() );
            itemEl.addContent( linkEl );

            if (item.getDescription() != null) {
                org.jdom.Element descEl = new org.jdom.Element( "description", s_rssNS );
                descEl.setText( item.getDescription());
                itemEl.addContent( descEl );
            }
        }
        
        
        RSSImage image = channel.getImage();
        if (image != null) {
            // Add the element to the channel list
            org.jdom.Element seqEl = new org.jdom.Element( "image", s_rdfNS );
            seqEl.setAttribute( "resource", image.getURL(), s_rdfNS );
            channelEl.addContent( seqEl );

            // Add the element to the top level
            org.jdom.Element imageEl = new org.jdom.Element( "image", s_rssNS );
            imageEl.setAttribute( "about", image.getURL(), s_rdfNS );
            rdfEl.addContent( imageEl );

            org.jdom.Element titleEl = new org.jdom.Element( "title", s_rssNS );
            titleEl.setText( image.getTitle());
            imageEl.addContent( titleEl );

            org.jdom.Element linkEl = new org.jdom.Element( "link", s_rssNS );
            linkEl.setText( image.getLink() );
            imageEl.addContent( linkEl );

            org.jdom.Element urlEl = new org.jdom.Element( "url", s_rssNS );
            urlEl.setText( image.getURL());
            imageEl.addContent( urlEl );
        }

        return rdfEl;
    }

    /**
     * 
     * @param channel
     * @return 
     */
    public static org.w3c.dom.Element generateDOM(RSSChannel channel) {
        org.jdom.Element jdomContent = RSSRenderer.generateJDOM(channel);
        org.jdom.output.DOMOutputter convertor = new org.jdom.output.DOMOutputter();
        org.w3c.dom.Element domContent = null;
        try {
            domContent = convertor.output(jdomContent);
        } catch (org.jdom.JDOMException e) {
            throw new UncheckedWrapperException(
                "cannot convert JDOM element to DOM", e
            );
        }
        
        return domContent;
    }
    
    /**
     * 
     * @param channel
     * @return 
     */
    public static Element generateXML(RSSChannel channel) {
        org.w3c.dom.Element domContent = generateDOM(channel);
        
        return new WrapperElement(domContent);
    }

    /**
     * 
     */
    private static class WrapperElement extends Element {
        
        /**
         * 
         * @param element 
         */
        public WrapperElement(org.w3c.dom.Element element) {
            m_element = element;
        }
        
    }

}
