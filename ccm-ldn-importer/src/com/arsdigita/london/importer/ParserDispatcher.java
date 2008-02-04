package com.arsdigita.london.importer;

import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.xml.XML;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *  The main entry point to importer facility.
 * ParserDispatcher is XML parser implementation which delegates the
 * control to one of the registered {@link TagParser} instances,
 * depending on the XML tag encountered.  When an XML tag with no
 * associated TagParser instance is encountered, the control is retained
 * by the currently active parser.
 *
 * @see com.arsdigita.london.importer
 */
public class ParserDispatcher extends DefaultHandler {
    private static Logger s_log =
        Logger.getLogger(ParserDispatcher.class);

    // Parsers for individual elements.
    private Map m_parsers;
    private Stack m_parserContext;

    public ParserDispatcher() {
        m_parsers = new HashMap();
    }

    /**
     *  Import objects specified by <tt>importFile</tt>.
     * <em>Important notice</em>: before this method can be called, the caller must
     * register one or more instances of {@link TagHandler} interface via
     * {@link #addParser(TagParser) addParser} method.
     *
     * @param importFile name of the file containing XML source for import.
     */
    public void execute(String importFile) {
        m_parserContext = new Stack();

        if (s_log.isInfoEnabled()) {
            s_log.info("Parsing from " + importFile);
        }

        InputStream is = null;
        try {
            is = new FileInputStream(new File(importFile));
        } catch (IOException ex) {
            throw new UncheckedWrapperException(
                "cannot load file " + importFile,
                ex);
        }

        XML.parse(is, this);
    }

    /**
     *  Import objects specified by <tt>source</tt>.
     * <em>Important notice</em>: before this method can be called, the caller must
     * register one or more instances of {@link TagHandler} interface via
     * {@link #addParser(TagParser) addParser} method.
     */
    public void execute(InputSource source) {
        m_parserContext = new Stack();

        if (s_log.isInfoEnabled()) {
            s_log.info("Parsing from InputSource");
        }

        try {
            SAXParserFactory spf = SAXParserFactory.newInstance();
            spf.setFeature("http://xml.org/sax/features/namespaces", true);
            SAXParser parser = spf.newSAXParser();
            parser.parse(source, this);
        } catch (ParserConfigurationException e) {
            throw new UncheckedWrapperException("error parsing stream", e);
        } catch (SAXException e) {
            if (e.getException() != null) {
                throw new UncheckedWrapperException("error parsing stream",
                                                    e.getException());
            } else {
                throw new UncheckedWrapperException("error parsing stream", e);
            }
        } catch (IOException e) {
            throw new UncheckedWrapperException("error parsing stream", e);
        }
    }

    public void addParser(TagParser parser) {
        m_parsers.put(getKey(parser), parser);
    }

    /**
     *  Based on the element name, switch to the parser handling that element.
     */
    public void startElement(String uri, String local,
                             String qname, Attributes atts) {
        if (s_log.isDebugEnabled()) {
            s_log.debug("Processing start element: " +
                        qname + " " + uri + " " + local);
        }

        if (m_parsers.containsKey(getKey(local, uri))) {
            TagParser current = getCurrentParser();
            TagParser next = (TagParser) m_parsers.get(getKey(local, uri));

            if (s_log.isInfoEnabled()) {
                s_log.info("Switching from " + current + " to " + next);
            }

            if (current != null) {
                current.startSubBlock(next);
            }
            next.startBlock();

            m_parserContext.push(next);
        }

        if (s_log.isDebugEnabled()) {
            s_log.debug("In start, current parser is " +
                        getCurrentParser());
        }
        TagParser current = getCurrentParser();
        if (current != null) {
            current.startElement(local, uri, atts);
        } else {
            s_log.warn("No current parser available for " + local + " " + uri);
        }
    }

    public void characters(char[] ch, int start, int length) {
        getCurrentParser().characters(ch, start, length);
    }

    public void endElement(String uri, String local, String qname ) {
        if (s_log.isDebugEnabled()) {
            s_log.debug("Processing end element: " +
                        qname + " " + uri + " " + local);
        }

        getCurrentParser().endElement(local, uri);
        if (s_log.isDebugEnabled()) {
            s_log.debug("In end, current parser is " +
                        getCurrentParser());
        }

        if (m_parsers.containsKey(getKey(local, uri))) {
            TagParser current = getCurrentParser();
            m_parserContext.pop();
            TagParser previous = m_parserContext.empty() ? null :
                (TagParser)m_parserContext.peek();

            current.endBlock();
            if (previous != null) {
                previous.endSubBlock(current);
            }

            if (s_log.isInfoEnabled()) {
                s_log.info("Reverting from " + current + " to " + previous);
            }
        }

    }

    protected String getKey(TagParser parser) {
        String name = parser.getTagName();
        String uri = parser.getTagURI();

        return getKey(name, uri);
    }

    protected String getKey(String name,
                            String uri) {
        if (uri == null) {
            return name;
        } else {
            return name + ":" + uri;
        }
    }

    protected TagParser getCurrentParser() {
        if (m_parserContext.empty()) {
            return null;
        } else {
            return (TagParser)m_parserContext.peek();
        }
    }
}


