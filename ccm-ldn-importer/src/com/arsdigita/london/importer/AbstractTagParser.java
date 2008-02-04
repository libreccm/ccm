package com.arsdigita.london.importer;

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;


/**
 *  Base class for TagParser implementations.
 * Its main purpose is API simplification through
 * {@link #startTag(String,String,Attributes) startTag}
 * and {@link #endTag(String,String) endTag} methods, which
 * replace {@link #startElement(String,String,Attributes) startElement}
 * and {@link #endElement(String,String) endElement}, respectively.
 *
 * @see com.arsdigita.london.importer
 */
public abstract class AbstractTagParser implements TagParser {
    private static Logger s_log =
        Logger.getLogger(TagParser.class);

    private StringBuffer m_body;
    private String m_tagName;
    private String m_tagURI;

    /**
     *  Main constructor.
     * @param tagName the name of XML tag this parser will be registered to handle
     * @param tagURI URI of the XML tag namespace
     */
    public AbstractTagParser(String tagName,
                             String tagURI) {
        m_tagName = tagName;
        m_tagURI = tagURI;
    }

    public String getTagName() {
        return m_tagName;
    }
    public String getTagURI() {
        return m_tagURI;
    }

    public final void startElement(String name,
                                   String uri, 
                                   Attributes atts) {
        m_body = new StringBuffer();
        startTag(name, uri, atts);
    }

    public final void characters(char[] ch, int start, int length) {
        if (m_body != null) {
            m_body.append(ch, start, length);
        } else {
            if (s_log.isDebugEnabled()) {
                s_log.debug("Skipping chars from non-leaf tag");
            }
        }
    }

    public final void endElement(String name,
                                 String uri) {
        endTag(name, uri);
        m_body = null;
    }


    public void startBlock() {}

    public void startSubBlock(TagParser parser) {}

    protected abstract void startTag(String name,
                                     String uri,
                                     Attributes atts);

    protected abstract void endTag(String name,
                                   String uri);

    public void endBlock() {}
    public void endSubBlock(TagParser parser) {}

    /**
     *  Gets the body of the XML element being processed.
     */
    protected String getTagBody() {
        return m_body.toString();
    }
}
