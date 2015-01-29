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
package com.arsdigita.cms.dispatcher;

import com.arsdigita.bebop.PageState;
import com.arsdigita.cms.contenttypes.SiteProxy;
import com.arsdigita.cms.contenttypes.util.SiteProxyGlobalizationUtil;
import com.arsdigita.bebop.Label;
import com.arsdigita.cms.CMS;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.contenttypes.SiteProxyConfig;
import com.arsdigita.util.url.URLFetcher;
import com.arsdigita.util.url.URLPool;
import com.arsdigita.util.url.URLCache;
import com.arsdigita.util.url.URLData;
import com.arsdigita.xml.Element;
import com.arsdigita.xml.Document;

import org.apache.log4j.Logger;

import javax.servlet.http.*;

import java.util.*;
import java.net.*;
import java.io.*;

/**
 * <p>
 * This <code>SiteProxyPanel</code> component fetches the
 * {@link com.arsdigita.cms.dispatcher.XMLGenerator} for the content section. It also uses the url
 * from the SiteProxy object to retrieve XML or HTML from the specified location. This XML/HTML is
 * integrated in to the resulting DOM
 *
 * @author Michael Pih (pihman@arsdigita.com)
 * @version $Revision: #6 $ $Date: 2004/08/17 $
 * @version $Id: SiteProxyPanel.java 1048 2005-12-11 20:46:12Z apevec $
 */
public class SiteProxyPanel extends ContentPanel {

    private static Logger s_log = Logger.getLogger(SiteProxyPanel.class);

    private static String SITE_PROXY_PANEL_NAME = "cms:siteProxyPanel";
    private static String DATA_TYPE = "dataType";
    private static String C_DATA_DATA_TYPE = "cdata";
    private static String XML_DATA_TYPE = "xml";

    private static String s_cacheServiceKey = "SiteProxyPanel";
    private static final SiteProxyConfig config = new SiteProxyConfig();
//    private static URLCache s_cache = new URLCache(1000000, 15*60*1000);
//    private static URLPool s_pool = new URLPool();
    private static final URLCache s_cache = new URLCache(
        config.getUrlCacheSize(), config.getUrlCacheExpiryTime());
    private static final URLPool s_pool = new URLPool(
        config.getUrlPoolSize(), config.getUrlPoolTimeout());

    static {
        config.load();
        
        s_log.debug("Static initalizer starting...");
        URLFetcher.registerService(s_cacheServiceKey, s_pool, s_cache);
        s_log.debug("Static initalizer finished.");
    }

    ;

    public SiteProxyPanel() {
        super();
    }

    /**
     * Fetches an XML Generator. This method can be overidden to fetch any
     * {@link com.arsdigita.cms.dispatcher.XMLGenerator}, but by default, it fetches the
     * <code>XMLGenerator</code> registered to the current {@link com.arsdigita.cms.ContentSection}.
     *
     * @param state The page state
     */
    protected XMLGenerator getXMLGenerator(PageState state) {
        return new SiteProxyXMLGenerator();
    }

    /**
     * Retrieve remote XML for SiteProxy item.
     *
     * @param child com.arsdigita.xml.Element where remote XML is placed
     * @param url   remote XML URL (text/xml)
     */
    public static URLData internalGetRemoteXML(Element child, String url) {
        URLData data = URLFetcher.fetchURLData(url, s_cacheServiceKey);
        if (data == null || data.getException() != null || data.getContent().length == 0) {
            return data;
        }

        String contentType = data.getContentType();

        boolean success = false;
        if (contentType != null && contentType.toLowerCase().indexOf("/xml") > -1) {
            // we use the /xml intead of text/xml because
            // things like application/xml are also valid
            Document document = null;
            try {
                document = new Document(data.getContent());
                success = true;
            } catch (Exception ex) {
                s_log.info("The document is not proper XML, trying to "
                           + "add the property xml headers to the file " + "retrieved from " + url,
                           ex);
                try {
                    String xmlString = data.getContentAsString();
                    xmlString = "<?xml version=\"1.0\"?> \n" + xmlString;
                    document = new Document(xmlString);
                    success = true;
                    s_log
                        .info("Adding the headers to " + url + " allowed it to be properly parsed.");
                } catch (Exception exception) {
                    s_log.info("The document found at " + url + " is not correctly formed XML",
                               exception);
                }
            }
            if (success) {
                child.addContent(document.getRootElement());
                child.addAttribute(DATA_TYPE, XML_DATA_TYPE);
            }
        }
        if (!success) {
            // just add the item as CDATA
            child.setCDATASection(data.getContentAsString());
            child.addAttribute(DATA_TYPE, C_DATA_DATA_TYPE);
        }
        return data;
    }

    class SiteProxyXMLGenerator extends SimpleXMLGenerator {

        @Override
        public void generateXML(PageState state, Element parent, String useContext) {

            ContentSection section = CMS.getContext().getContentSection();
            SiteProxy item = (SiteProxy) getContentItem(state);
            String url = passParameters(state.getRequest(), item.getURL());

            Element child = parent.newChildElement(SITE_PROXY_PANEL_NAME,
                                                   CMS.CMS_XML_NS);
            URLData data = internalGetRemoteXML(child, url);

            if (data == null) {
                String[] urlArray = {url};
                (new Label(SiteProxyGlobalizationUtil.globalize(
                 "cms.contenttypes.dispatcher.siteproxy.error_fetching_url",
                 urlArray))).generateXML(state, parent);
            } else if (data.getException() != null) {
                String[] urlArray = {url, data.getException().getClass().getName(),
                                     data.getException().getMessage()};
                (new Label(SiteProxyGlobalizationUtil.globalize(
                 "cms.contenttypes.siteproxy.dispatcher.exception_fetching_url",
                 urlArray))).generateXML(state, parent);
            } else if (data.getContent().length == 0) {
                String[] urlArray = {url};
                (new Label(SiteProxyGlobalizationUtil.globalize(
                 "cms.contenttypes.siteproxy.dispatcher.empty_page_returned",
                 urlArray))).generateXML(state, parent);
            }
        }

    }

    private String passParameters(HttpServletRequest request, String url) {
        StringBuffer sb = new StringBuffer(url);
        String enc = request.getCharacterEncoding();
        if (enc == null) {
            enc = "UTF-8";
        }
        Enumeration en = request.getParameterNames();
        while (en.hasMoreElements()) {
            String paramName = (String) en.nextElement();
            String[] paramValues = request.getParameterValues(paramName);
            for (int i = 0; i < paramValues.length; ++i) {
                if (sb.indexOf("?") < 0) {
                    sb.append("?");
                } else {
                    sb.append("&");
                }
                sb.append(paramName).append("=");
                try {
                    sb.append(URLEncoder.encode(paramValues[i], enc));
                } catch (UnsupportedEncodingException ex) {
                    s_log.error("Unable to encode SiteProxy request", ex);
                    return url;
                }
            }
        }
        return sb.toString();
    }

}
