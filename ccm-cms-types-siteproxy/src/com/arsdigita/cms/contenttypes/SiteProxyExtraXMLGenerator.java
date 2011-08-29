package com.arsdigita.cms.contenttypes;

import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.cms.CMS;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ExtraXMLGenerator;
import com.arsdigita.cms.contenttypes.util.SiteProxyGlobalizationUtil;
import com.arsdigita.util.url.URLCache;
import com.arsdigita.util.url.URLData;
import com.arsdigita.util.url.URLFetcher;
import com.arsdigita.util.url.URLPool;
import com.arsdigita.xml.Document;
import com.arsdigita.xml.Element;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Enumeration;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class SiteProxyExtraXMLGenerator implements ExtraXMLGenerator {

    private final static Logger logger =
                                Logger.getLogger(
            SiteProxyExtraXMLGenerator.class);
    private static final String SITE_PROXY_PANEL_NAME = "cms:siteProxyPanel";
    private static final String DATA_TYPE = "dataType";
    private static final String C_DATA_DATA_TYPE = "cdata";
    private static final String XML_DATA_TYPE = "xml";
    private static final String s_cacheServiceKey = "SiteProxyPanel";
    private static final URLCache s_cache = new URLCache(1000000, 1 * 60 * 1000);
    private static final URLPool s_pool = new URLPool();

    static {
        logger.debug("Static initalizer starting...");
        URLFetcher.registerService(s_cacheServiceKey, s_pool, s_cache);
        logger.debug("Static initalizer finished.");
    }

    public void generateXML(final ContentItem item,
                            final Element element,
                            final PageState state) {
        if (!(item instanceof SiteProxy)) {
            throw new IllegalArgumentException(
                    "Only SiteProxy items are supported");
        }

        final SiteProxy siteProxy = (SiteProxy) item;
        final String url = passParameters(state.getRequest(),
                                          siteProxy.getURL());
        final Element child = element.newChildElement(SITE_PROXY_PANEL_NAME,
                                                      CMS.CMS_XML_NS);
        final URLData data = getRemoteXML(child, url);

        if (data == null) {
            final String[] urlArray = {url};
            final Element error = child.newChildElement("siteProxyError");
            error.setText((String) SiteProxyGlobalizationUtil.globalize(
                    "cms.contenttypes.siteproxy.error_fetching_url",
                    urlArray).
                    localize());
        } else if (data.getException() != null) {
            final String[] urlArray = {url,
                                       data.getException().getClass().getName(),
                                       data.getException().getMessage()};
            final Element error = child.newChildElement("siteProxyError");
            error.setText((String) SiteProxyGlobalizationUtil.globalize(
                    "cms.contenttypes.siteproxy.exception_fetching_url",
                    urlArray).
                    localize());
        } else if (data.getContent().length == 0) {
            final String[] urlArray = {url};
            final Element error = child.newChildElement("siteProxyError");
            error.setText((String) SiteProxyGlobalizationUtil.globalize(
                    "cms.contenttypes.siteproxy.empty_page_returned",
                    urlArray).
                    localize());
        }
    }

    public void addGlobalStateParams(final Page page) {
    }

    private String passParameters(final HttpServletRequest request,
                                  final String url) {
        StringBuilder sb = new StringBuilder(url);
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
                    logger.error("Unable to encode SiteProxy request", ex);
                    return url;
                }
            }
        }
        return sb.toString();
    }

    /**
     * Retrieve remote XML for SiteProxy item.
     * 
     * @param child com.arsdigita.xml.Element where remote XML is placed
     * @param url remote XML URL (text/xml)
     * @return  
     */
    public URLData getRemoteXML(Element child, String url) {
        URLData data = URLFetcher.fetchURLData(url, s_cacheServiceKey);
        if (data == null || data.getException() != null || data.getContent().length
                                                           == 0) {
            return data;
        }

        String contentType = data.getContentType();

        boolean success = false;
        if (contentType != null && contentType.toLowerCase().indexOf("/xml")
                                   > -1) {
            // we use the /xml intead of text/xml because
            // things like application/xml are also valid
            Document document = null;
            try {
                document = new Document(data.getContent());
                success = true;
            } catch (Exception ex) {
                logger.info("The document is not proper XML, trying to "
                            + "add the property xml headers to the file "
                            + "retrieved from " + url, ex);
                try {
                    //String xmlString = data.getContentAsString();
                    final byte[] xml = data.getContent();
                    final String xmlString = String.format(
                            "<?xml version=\"1.0\"?> \n%s",
                            new String(xml,
                                       Charset.forName("UTF-8")));
                    document = new Document(xmlString);
                    success = true;
                    logger.info("Adding the headers to " + url
                                + " allowed it to be properly parsed.");
                } catch (Exception exception) {
                    logger.info("The document found at " + url
                                + " is not correctly formed XML", exception);
                }
            }
            if (success) {
                child.addContent(document.getRootElement());
                child.addAttribute(DATA_TYPE, XML_DATA_TYPE);
            }
        }
        if (!success) {
            // just add the item as CDATA
            child.setCDATASection(new String(data.getContent(),
                                             Charset.forName("UTF-8")));
            child.addAttribute(DATA_TYPE, C_DATA_DATA_TYPE);
        }
        return data;
    }
}
