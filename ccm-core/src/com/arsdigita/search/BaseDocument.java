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
package com.arsdigita.search;

import com.arsdigita.kernel.Party;
import com.arsdigita.persistence.OID;
import com.arsdigita.util.Assert;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.util.servlet.HttpHost;
import com.arsdigita.web.ParameterMap;
import com.arsdigita.web.Web;
import com.arsdigita.web.WebConfig;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.Locale;

/**
 * A BaseDocument provides a base implementation of the
 * Document interface. Query engine implementations should
 * use this class, or subclass it, rather than implementing
 * the Document interface directly, since this makes them
 * immune to future additions to the Document interface.
 *
 * @see com.arsdigita.search.Document
 */
public class BaseDocument implements Document {

    private URL m_url;
    private OID m_oid;
    private Locale m_locale;
    private String m_title;
    private String m_summary;
    private Date m_creationDate;
    private Party m_creationParty;
    private Date m_modifiedDate;
    private Party m_modifiedParty;

    private String m_contentSection;

    private BigDecimal m_score;

    /**
     * Creates a new document;
     *
     * @param oid the domain object's unique oid
     * @param locale the locale of the document
     * @param title the title of the document
     * @param summary the optional summary
     * @param creationDate the date on which the document was created
     * @param creationParty the party who created the document
     * @param creationDate the date on which the document was modified
     * @param creationParty the party who modified the document
     * @param score the score
     */
    public BaseDocument(OID oid,
                        Locale locale,
                        String title,
                        String summary,
                        Date creationDate,
                        Party creationParty,
                        Date modifiedDate,
                        Party modifiedParty,
                        BigDecimal score) {
        this(generateURL(oid),
             oid,
             locale,
             title,
             summary,
             creationDate,
             creationParty,
             modifiedDate,
             modifiedParty,
             score);
    }


    public BaseDocument(URL url,
                        OID oid,
                        Locale locale,
                        String title,
                        String summary,
                        Date creationDate,
                        Party creationParty,
                        Date modifiedDate,
                        Party modifiedParty,
                        BigDecimal score) {
        Assert.exists(oid, OID.class);
        Assert.exists(url, String.class);
        Assert.exists(locale,Locale.class);
        Assert.exists(title,String.class);
        //Assert.exists(creationDate,Date.class);
        //Assert.exists(creationParty,Party.class);
        //Assert.exists(modifiedDate,Date.class);
        //Assert.exists(modifiedParty,Party.class);

        m_oid = oid;
        m_url = url;
        m_locale = locale;
        m_title = title;
        m_summary = summary;
        m_creationDate = creationDate;
        m_creationParty = creationParty;
        m_modifiedDate = modifiedDate;
        m_modifiedParty = modifiedParty;
        m_score = score;
    }

    /**
     * New constructor which includes the content section of the Document.
     **/
    public BaseDocument(OID oid,
                        Locale locale,
                        String title,
                        String summary,
                        Date creationDate,
                        Party creationParty,
                        Date modifiedDate,
                        Party modifiedParty,
                        BigDecimal score,
			String contentSection) {
        this(generateURL(oid),
             oid,
             locale,
             title,
             summary,
             creationDate,
             creationParty,
             modifiedDate,
             modifiedParty,
             score,
	     contentSection);
    }

    public BaseDocument(URL url,
                        OID oid,
                        Locale locale,
                        String title,
                        String summary,
                        Date creationDate,
                        Party creationParty,
                        Date modifiedDate,
                        Party modifiedParty,
			BigDecimal score,
			String contentSection) {
        Assert.exists(oid, OID.class);
        Assert.exists(url, String.class);
        Assert.exists(locale,Locale.class);
        Assert.exists(title,String.class);
        //Assert.exists(creationDate,Date.class);
        //Assert.exists(creationParty,Party.class);
        //Assert.exists(modifiedDate,Date.class);
        //Assert.exists(modifiedParty,Party.class);

        m_oid = oid;
        m_url = url;
        m_locale = locale;
        m_title = title;
        m_summary = summary;
        m_creationDate = creationDate;
        m_creationParty = creationParty;
        m_modifiedDate = modifiedDate;
        m_modifiedParty = modifiedParty;
        m_score = score;
	m_contentSection = contentSection;
    }


    /**
     * Gets the unique OID for the domain object
     * referenced by this document
     * @return the unique OID
     */
    public OID getOID() {
        return m_oid;
    }

    /**
     * Gets the URL for this document
     * @return the document url
     */
    public URL getURL() {
        return m_url;
    }

    /**
     * Gets the locale to which this object belongs
     *
     * @return the locale of the object
     *
     * @pos $retval != null
     */
    public Locale getLocale() {
        return m_locale;
    }

    /**
     * Gets the Title property for the DomainObject
     *
     * @return title of the object
     *
     * @post $retval != null
     */
    public String getTitle() {
        return m_title;
    }

    /**
     * Gets the (optional) summary of the DomainObject
     *
     * @return the object summary, or null
     */
    public String getSummary() {
        return m_summary;
    }

    /**
     * Gets the (optional) creation date of the DomainObject
     *
     * @return the creation date, or null
     */
    public Date getCreationDate() {
        return m_creationDate;
    }

    /**
     * Gets the (optional) creating party of the DomainObject
     *
     * @return the creation party, or null
     */
    public Party getCreationParty() {
        return m_creationParty;
    }

    /**
     * Gets the (optional) last modification date of the DomainObject
     *
     * @return the modification date, or null
     */
    public Date getLastModifiedDate() {
        return m_modifiedDate;
    }

    /**
     * Gets the (optional) last modifying party of the DomainObject
     *
     * @return the modification party, or null
     */
    public Party getLastModifiedParty() {
        return m_modifiedParty;
    }

    /**
     * Gets the document score
     *
     * @return the score
     */
    public BigDecimal getScore() {
        return m_score;
    }

    private static URL generateURL(final OID oid) {
        final ParameterMap params = new ParameterMap();
        final WebConfig config = Web.getConfig();
        final HttpHost server = config.getServer();

        params.setParameter("oid", oid);

        com.arsdigita.web.URL url = new com.arsdigita.web.URL
            ("http",
             server.getName(),
             server.getPort(),
             config.getDispatcherContextPath(),
             "",
             "/redirect/",
             params);

        try {
            return new URL(url.getURL());
        } catch (MalformedURLException ex) {
            throw new UncheckedWrapperException
                ("Cannot parse url " + url.getURL(), ex);
        }
    }

    public String getContentSection() {
	return m_contentSection;
    }

}
