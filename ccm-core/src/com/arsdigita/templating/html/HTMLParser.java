/*
 * Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.templating.html;


/**
 * This interface is intended to provide limited support for templating in the
 * context of CMS and similar applications, where users are expected to submit
 * HTML that may have custom tags that have to be processed programmaticaly.
 *
 * <p>For example, suppose you allow a content item body to have the
 * <code>&lt;footnote></code> tag. Suppose the user submits the following HTML
 * fragment, </p>
 *
 * <blockquote><pre style="border: 1px solid black; padding-left: 1ex; padding-right: 1ex;">
 *  &lt;h2>What is Truth?&lt;/h2>
 *
 *  &lt;p>We've bandied about the term truth,&lt;footnote>Strictly speaking,
 *  this is not true.&lt;/footnote> and we've mentioned that certain operators
 *  return a true or false value. Before we go any further, we really ought to
 *  explain exactly what we mean by that.  &lt;/p>
 * </pre></blockquote>
 *
 * <p>To render this content correctly, we have to transform this fragment to
 * look like so: </p>
 *
 * <blockquote><pre style="border: 1px solid black; padding-left: 1ex; padding-right: 1ex;">
 *  &lt;h2>What is Truth?&lt;/h2>
 *
 *  &lt;p>We've bandied about the term truth&lt;sup>&lt;a
 *  href="#fn1">1&lt;/a>&lt;/sup>, and we've mentioned that certain operators
 *  return a true or false value. Before we go any further, we really ought to
 *  explain exactly what we mean by that. &lt;/p>
 *
 *  &lt;hr>
 *  &lt;p id="fn1">&lt;sup>1&lt;/sup>Strictly speaking, this is not true.&lt;/p>
 * </pre></blockquote>
 *
 * <p>So that the rendered output looks like so: </p>
 *
 * <blockquote style="border: 1px solid black; padding-left: 1ex; padding-right: 1ex;">
 *  <h2>What is Truth?</h2>
 *
 *  <p>We've bandied about the term truth<sup><a
 *  href="#fn1">1</a></sup>, and we've mentioned that certain operators
 *  return a true or false value. Before we go any further, we really ought to
 *  explain exactly what we mean by that. </p>
 *
 *  <hr>
 *  <p id="fn1"><sup>1</sup>Strictly speaking, this is not true.</p>
 *  </blockquote>
 *
 * <p>This interface is similar to the <a
 * href="http://java.sun.com/xml/jaxp/dist/1.1/docs/api/javax/xml/parsers/SAXParser.html">SAXParser</a>
 * interface. One major difference is that this parser normally treats most of
 * the markup as text whose structure is of no interest to the designated {@link
 * ContentHandler}. See {@link #parse(String, ContentHandler)} for more details.</p>
 * 
 * @author Vadim Nasardinov (vadimn@redhat.com)
 * @since 2002-08-21
 * @version $Id: HTMLParser.java 287 2005-02-22 00:29:02Z sskracic $
 **/
public interface HTMLParser {

    /**
     * Registers the tag <code>qName</code>.
     **/
    void registerTag(String qName);

    /**
     * <p>The interface is designed around the following assumptions. </p>
     *
     * <ol>
     *   <li>Clients of the interface are only interested in special tags
     *   registered via {@link #registerTag(String)}. All other markup is treated
     *   as text. </li>
     *
     *   <li>The HTML fragment <code>html</code> is well-formed with regards to
     *   the special tags.  It does not have to be well-formed with regards to
     *   the "normal" markup that has not been registered with the parser via
     *   {@link #registerTag(String)}. (Implementations of this interface may, of
     *   course, impose this additional restriction. )</li>
     *   </ol>
     **/
    void parse(String html, ContentHandler handler)
        throws HTMLParserException;

    /**
     * Checks if the tag is registered with this parser.
     **/
    boolean isRegistered(String qName);
}
