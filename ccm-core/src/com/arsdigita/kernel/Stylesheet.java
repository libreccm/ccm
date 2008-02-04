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
 *
 */
package com.arsdigita.kernel;

import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.globalization.GlobalizationException;
import com.arsdigita.globalization.Locale;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.util.ResourceManager;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import javax.servlet.ServletContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import org.apache.log4j.Logger;
import org.apache.oro.text.perl.Perl5Util;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Stylesheet extends ACSObject implements Templates {

    public static final String versionId = "$Id: Stylesheet.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    /** Composite of a String and a boolean.  The boolean defaults to false.
     *  We use it to keep track of all XSL Files and which of them are valid.
     */
    public static class FileSource {
        private String  m_name;
        private boolean m_valid = false;

        /** construct from a String and a boolean
         */
        public FileSource(String name) { m_name  = name; }

        /** @return the String member
         */
        public String  getName() { return m_name; }

        /** change the boolean member to true
         */
        public void makeValid() { m_valid = true;}

        /** @return the boolean member
         */
        public boolean isValid() { return m_valid;}
    }

    // XXX: this is a kludge for persistence's lack of domain
    // object caching.
    private String m_path;

    // TODO: transformers are not threadsafe.  Fix this.
    private ThreadLocal m_stylesheet = new ThreadLocal();

    private Templates m_trax_templates;

    private synchronized void setTemplates(Templates t) {
        m_trax_templates = t;
    }

    private synchronized Templates getTemplates() {
        return m_trax_templates;
    }

    private Document m_stylesheetDOM;
    private static final Logger s_cat =
        Logger.getLogger(Stylesheet.class.getName());
    private static DocumentBuilderFactory s_dbFactory;
    private final static String XSL_NAMESPACE_URI =
        "http://www.w3.org/1999/XSL/Transform";
    private static final String SAXON = "SAXON";
    private static final String XSLTC = "XSLTC";

    private static ThreadLocal s_transformerFactory = new ThreadLocal();

    private long m_lastModified = 0;
    // timeout in milliseconds
    private static long s_ttl = 30000;
    private boolean m_valid = true;
    private List m_fileSources = new ArrayList(); // of FileSource
    private List m_imports = new ArrayList(); // for imports

    // TODO: keep a list of sources used to build the stylesheet
    // so we can improve up-to-date checking.

    private static final String s_typeName = "com.arsdigita.kernel.Stylesheet";

    // Define defaults for new stylesheets
    private static final String s_defaultOutputType = "text/html";

    protected String getBaseDataObjectType() {
        return s_typeName;
    }

    // Constructors

    /**
     * Creates an instance of a stylesheet.
     *
     * @see com.arsdigita.domain.DomainObject#DomainObject(String)
     **/
    public Stylesheet() {
        super(s_typeName);
    }

    public Stylesheet(DataObject dataObject) {
        super(dataObject);
    }

    public Stylesheet(OID oid) throws DataObjectNotFoundException {
        super(oid);
    }

    public Stylesheet(BigDecimal id) throws DataObjectNotFoundException {
        super(new OID(s_typeName, id));
    }

    public void initialize() {
        super.initialize();
        m_lastModified = System.currentTimeMillis();
        if (isNew()) {
            set("outputType", s_defaultOutputType);
        }
    }

    // for stylesheets in the file system
    // we propagate exceptions upwards, because it's not very helpful
    // to log an error here if we don't know what file is associated with.
    private Stylesheet loadStylesheet(InputStream is)
        throws ParserConfigurationException, SAXException, IOException {
        // The file is modified
        m_lastModified = System.currentTimeMillis();
        createFactories();
        DocumentBuilder db = null;

        db = s_dbFactory.newDocumentBuilder();
        m_stylesheetDOM = db.parse(is);
        return this;
    }

    /**
     * Determines whether this stylesheet is newer than the specified stylesheet.
     * @param s the stylesheet to compare against
     *
     * @return <code>true</code> if this stylesheet's last modified time is after
     * s's or if s is null; <code>false</code> otherwise.
     */
    public boolean isNewerThan(Stylesheet s) {
        if (s == null) {
            // trivial: this is always newer than undefined.
            return true;
        }
        return (m_lastModified >= s.m_lastModified ) ;
    }

    /**
     * Gets the last modified time of this stylesheet, in Java system
     * time (ms since 1970).
     * @return the last modified time of this stylesheet.
     */
    public long getLastModified() {
        return m_lastModified;
    }

    /**
     * Marks this stylesheet as invalid. Subsequent calls to isValid()
     * will return false.
     */
    public void invalidate() {
        m_valid = false;
    }

    /**
     * Indicates whether this stylesheet is still "fresh" and valid.
     *
     * @return <code>true</code> if this stylesheet has not been invalidated and
     * the file sources on disk are all up-to-date.  We only check the files
     * on disk every (s_ttl) milliseconds because we don't want to
     * constantly check the filesystem for file modifications.
     */
    public boolean isValid() {
        return (m_valid &&
                (m_lastModified + s_ttl > System.currentTimeMillis() ||
                 sourcesUpToDate()));
    }

    /**
     * returns true if all files in m_fileSources have last modified
     * times <= m_lastModified.
     */
    private boolean sourcesUpToDate() {
        ResourceManager rm = ResourceManager.getInstance();
        for (Iterator i = m_fileSources.iterator(); i.hasNext(); ) {
            // check all FileSources, regardless of whether they
            // parsed ok last time
            String sourceName = ((FileSource)i.next()).getName();
            long lastMod = rm.getLastModified(sourceName);
            final boolean isOutOfDate = lastMod > m_lastModified;
            if (isOutOfDate) {
                return false;
            }
        }
        for (Iterator i = m_imports.iterator(); i.hasNext(); ) {
            // check all Imports, regardless of whether they
            // parsed ok last time
            File source = (File)i.next();
            long lastMod = source.lastModified();
            final boolean isOutOfDate = lastMod > m_lastModified;
            if (isOutOfDate) {
                return false;
            }
        }
        m_lastModified = System.currentTimeMillis();
        return true;
    }

    /**
     * Sets the global lifetime for stylesheets before they are re-read.
     * Default is 30 seconds.  Production systems should bump this up to
     * a longer duration.
     */
    public static void setTimeout(long ttl) {
        if (ttl < 0 ) {
            throw new IllegalArgumentException("Timeout must be positive");
        }
        s_ttl = ttl;
    }

    // public methods
    /**
     * Loads a set of stylesheets and combines them into a single stylesheet.
     *
     * @param ssList the list of stylesheets to combine
     * @return the combined stylesheet.
     */
    public static Stylesheet combineStylesheets(Stylesheet[] ssList) {
        // WRS 5/29: the stylesheet for a packageType might actually be
        // several stylesheets. Glom them all together and  return the
        // composed stylesheet.

        Stylesheet ss = null;
        List fileSources = new ArrayList(); // of FileSource
        ResourceManager rm = ResourceManager.getInstance();
        for (int i = 0; i < ssList.length; i++) {
            Stylesheet tmpSS = ssList[i];
            String path = tmpSS.getPath();
            FileSource src = new FileSource(path); // born invalid
            InputStream is = rm.getResourceAsStream(path);
            if (is == null) {
                s_cat.warn("Could not find stylesheet " + path);
            } else {
                try {
                    tmpSS.loadStylesheet(is);
                    src.makeValid();                // no Exception => approve
                    if (ss == null) {
                        ss = tmpSS;
                    } else {
                        ss = ss.composeStylesheet(tmpSS);
                    }
                    is.close();
                } catch (Exception ex) {
                    s_cat.error("error parsing stylesheet " + path, ex);
                }
            }
            fileSources.add(src);                // accumulate in list
        }
        if (ss != null) {
            ss.m_lastModified = System.currentTimeMillis();
            ss.m_fileSources = fileSources;
        }
        return ss;
    }

    public void setPath(String path) {
        m_path = path;
        set("pathName", path);
    }

    /**
     * Returns the pathname that was originally associated
     * with this stylesheet object in the database.  Will not
     * fully reflect the DOM/Transformer contents of the stylesheet
     * if it is composed with other stylesheets.
     *
     * @return the pathname originally associated with this stylesheet.
     */
    public String getPath() {
        // this is a kludge for persistence's lack of domain
        // object caching.
        if (m_path == null) {
            m_path = (String)get("pathName");
        }
        return m_path;
    }

    /**
     * Returns a display name for this stylesheet.
     *
     * @see ACSObject#getDisplayName()
     */
    public String getDisplayName() {
        return getPath();
    }

    /**
     * Returns the list of all Filesources (with filenames relative to
     * the context root) that should have contributed some part to
     * this stylesheet. Attempt to keep them in priority order, but
     * no guarantees.
     *
     * @see FileSource
     * @see FileSource#isValid()
     * @return the list of filesources that should have contributed to
     * this stylesheet.
     */
    public List getSources() {
        return m_fileSources;
    }

    public void setOutputType(String type) {
        set("outputType", type);
    }

    public String getOutputType(String type) {
        return (String)get("outputType");
    }

    public Locale getLocale() {
        return (Locale)get("locale");
    }

    public void setLocale(Locale locale) {
        setAssociation("locale", locale);
    }

    public void setLocale(java.util.Locale locale) {
        try {
            setLocale(Locale.fromJavaLocale(locale));
        } catch (GlobalizationException ge) {
            s_cat.error(
                        "Locale " + locale.toString() + " is not supported.", ge
                        );
        }
    }

    /**
     * Producer method that adds the rules of a specified stylesheet
     * to the existing stylesheet object and returns the composed
     * moby-stylesheet.  Gives precedence to the rules in the existing
     * stylesheet object (this) over the input object (s).
     *
     * @param s an XSL stylesheet object
     * @return a moby-stylesheet with the template rules from this and
     * the template rules from s.  */
    public Stylesheet composeStylesheet(Stylesheet s) {
        // create a new stylesheet object
        Stylesheet newSS = new Stylesheet();

        // maintain the list of file sources for a stylesheet
        // the things I'll do to avoid cut-and-paste programming.  Sigh.
        Stylesheet[] tmp = {this, s};
        for (int i = 0; i < tmp.length; i++) {
            if (tmp[i] != null) {
                Iterator it = tmp[i].m_fileSources.iterator();
                while (it.hasNext()) {
                    newSS.m_fileSources.add(it.next());
                }
                if (tmp[i].m_stylesheetDOM == null) {
                    // read it in, in case it was a single-file SS before
                    // (now we need the DOM for composing)
                    String path = tmp[i].getPath();
                    ResourceManager rm = ResourceManager.getInstance();
                    try {
                        tmp[i].loadStylesheet(rm.getResourceAsStream(path));
                    } catch (Exception pce) {
                        s_cat.error("error in compose", pce);
                    }
                }
            }
        }

        newSS.setTemplates(null);
        newSS.m_stylesheet.set(null);

        if (s != null && s.m_stylesheetDOM != null
            && this.m_stylesheetDOM != null) {
            newSS.m_stylesheetDOM = composeStylesheet(new Document[] {
                s.m_stylesheetDOM, this.m_stylesheetDOM
            });
        } else if (s != null && s.m_stylesheetDOM != null) {
            // our current SS is empty and we're composing with
            // a non-empty SS
            newSS.m_stylesheetDOM = s.m_stylesheetDOM;
        } else {
            newSS.m_stylesheetDOM = this.m_stylesheetDOM;
        }
        newSS.m_lastModified = System.currentTimeMillis();
        return newSS;
    }

    private static boolean usingSaxon() {
        final String trans = System.getProperty
            ("javax.xml.transform.TransformerFactory");

        if (trans == null) {
            return false;
        } else {
            return trans.indexOf("saxon") != -1;
        }
    }

    private static boolean usingXSLTC() {
        final String trans = System.getProperty
            ("javax.xml.transform.TransformerFactory");

        if (trans == null) {
            return false;
        } else {
            return trans.indexOf("xsltc") != -1;
        }
    }

    public synchronized Transformer newTransformer()
        throws TransformerConfigurationException {

        Source ssSource = null;

        if ( getTemplates() == null && m_stylesheetDOM != null) {
            // no template; need to rebuild from composite DOM.
            ssSource = new DOMSource(m_stylesheetDOM);
            // Saxon requires full path info
            if(usingSaxon())
                {
                    ResourceManager rm = ResourceManager.getInstance();
                    String pathn = rm.getResourceAsFile(getPath()).getAbsolutePath();
                    ssSource.setSystemId(pathn);
                }
            else
                {
                    // dummy value to keep non-Saxon xmfr's happy
                    //Eventually, as xmfr's besides Saxon approach the spec closer,
                    //This if/else will probably go away, but right now
                    //we must explicitly set via cases...
                    ssSource.setSystemId("DOMSource");
                }
        } else if (m_stylesheetDOM == null) {
            // we have no DOM, so we're dealing with a one-file
            // stylesheet.  Use a StreamSource instead of a DOM
            // so relative URLs will work.
            if (!isValid() || getTemplates() == null) {
                if (m_fileSources.size() == 0) {
                    FileSource src = new FileSource(getPath());
                    src.makeValid();
                    m_fileSources.add(src);
                }
                ResourceManager rm = ResourceManager.getInstance();
                ssSource = new StreamSource(rm.getResourceAsFile(getPath()));
                // Saxon requires full path info
                if(usingSaxon())
                    {
                        String pathname = rm.getResourceAsFile(getPath()).getAbsolutePath();
                        ssSource.setSystemId(pathname);
                    }
                m_lastModified = System.currentTimeMillis();
                // read in imports
                m_imports = getImports();
            }
        }
        if (ssSource != null) {
            // one way or the other, we have to re-set the Templates
            // object that we cached previously.  maybe even build
            // a factory first.

            // TODO:
            // instead of synchronized(this.getClass()),
            // we could call createFactories, which is
            // static synchronized
            TransformerFactory tfact = null;
            synchronized(this.getClass()) {
                tfact = (TransformerFactory)s_transformerFactory.get();
                if (tfact == null) {
                    tfact = TransformerFactory.newInstance();
                    if (usingXSLTC()) {
                        //disable template inlining, otherwise xsltc might generate methods
                        //that are too long, or contain jump offsets that are too large for
                        //the JVM to handle for more details see "Known problems for XSLTC
                        //Translets - http://xml.apache.org/xalan-j/xsltc_constraints.html#xsltcknownproblems
                        tfact.setAttribute("disable-inlining", new Boolean(true));
                        s_cat.info("set disable-inlining to true for XSLSTC");
                        tfact.setErrorListener(new ErrorListener() {
                            public void warning(TransformerException transformerException) throws TransformerException {
                                s_cat.warn("Transformer warning: " + transformerException.getMessage(), transformerException);
                            }

                            public void error(TransformerException transformerException) throws TransformerException {
                                s_cat.error("Transformer error: " + transformerException.getMessage(), transformerException);

                            }

                            public void fatalError(TransformerException transformerException) throws TransformerException {
                                s_cat.fatal("Transformer FATAL: " + transformerException.getMessage(), transformerException);

                            }
                        });

                    }

                    s_transformerFactory.set(tfact);
                }
            }
            setTemplates(tfact.newTemplates(ssSource));
            // also have to clear the transformer to force rebuild
            // below
            m_stylesheet.set(null);
        }

        Object transformer = m_stylesheet.get();
        if (transformer == null) {
            transformer = getTemplates().newTransformer();
            m_stylesheet.set(transformer);
        }
        return (Transformer)transformer;
    }

    public java.util.Properties getOutputProperties() {
        // return m_stylesheet.getOutputProperties();
        return getTemplates().getOutputProperties();
    }

    // private helper methods below

    private synchronized static void createFactories() {
        TransformerFactory tfact =
            (TransformerFactory)s_transformerFactory.get();
        if (tfact == null) {
            tfact = TransformerFactory.newInstance();
            if (usingXSLTC()) {
                //disable template inlining, otherwise xsltc might generate methods
                //that are too long, or contain jump offsets that are too large for
                //the JVM to handle for more details see "Known problems for XSLTC
                //Translets - http://xml.apache.org/xalan-j/xsltc_constraints.html#xsltcknownproblems
                tfact.setAttribute("disable-inlining", new Boolean(true));
                s_cat.info("set disable-inlining to true for XSLSTC");
            }

            s_transformerFactory.set(tfact);
        }

        if ( s_dbFactory == null ) {
            s_dbFactory = DocumentBuilderFactory.newInstance();
            s_dbFactory.setNamespaceAware(true);
        }
    }

    /**
     * Creates a new Stylesheet document using all of the transformation
     * rules that comprise the list of input stylesheets.
     *
     * @param stylesheetList a list of input stylesheets.
     * @return a Document that is an XSL styleshet containing all of
     * the template rules in the list of input stylesheets.  The
     * template rules in the returned Document will be in the same
     * order they were supplied, with the ones in the lower-indexed
     * stylesheets appearing first; this means that input[1] will take
     * precedence over input[0] if two identical rules match, etc.
     */
    private static Document composeStylesheet(Document[] stylesheetList) {

        createFactories();
        Document result = null;

        try {
            result = s_dbFactory.newDocumentBuilder().newDocument();
        } catch (ParserConfigurationException e) {
            s_cat.error("error in Stylesheet", e);
        }

        Element stylesheetElement = result.createElementNS
            (XSL_NAMESPACE_URI, "xsl:stylesheet");
        // Element stylesheetElement = result.createElement("xsl:stylesheet");

        result.appendChild(stylesheetElement);

        for (int j = 0; j < stylesheetList.length; j++) {
            Document thisSS = stylesheetList[j];

            // iterate over all the nodes under d1's <xsl:stylesheet>

            NodeList nl = thisSS.getElementsByTagNameNS
                (XSL_NAMESPACE_URI, "stylesheet");
            if (nl == null) {
                s_cat.error("badly formed stylesheet, missing xsl:stylesheet");
                return result;
            }

            // NodeList nl = thisSS.getElementsByTagName("xsl:stylesheet");
            Node n = nl.item(0);
            if (n == null) {
                s_cat.error("badly formed stylesheet, missing xsl:stylesheet");
                return result;
            }

            // n is <xsl:stylesheet>
            // iterate over all attributes and set them in stylesheetElement
            NamedNodeMap ssAttributes = n.getAttributes();
            for (int i = 0; i < ssAttributes.getLength(); i++) {
                Attr attr = (Attr)ssAttributes.item(i);
                String name = attr.getName(), value = attr.getValue();

                // normal attributes from n override those in stylesheetElement
                // but exclude-result-prefixes must be joined with spaces
                if (name.equals("exclude-result-prefixes")) {
                    String oldValue = stylesheetElement.getAttribute(name);
                    StringTokenizer st = new StringTokenizer(oldValue);
                    boolean duplicate = false;
                    while (st.hasMoreTokens()) {
                        if (st.nextToken().equals(value)) {
                            duplicate = true;
                            break;
                        }
                    }
                    if (!duplicate) {
                        // no duplicate, so we need to add.
                        if (oldValue != null && oldValue.length() > 0) {
                            // something already there; add to it
                            value += " " + oldValue;
                        } // else nothing to add to.  set value = newValue
                    } else {
                        // we have a duplicate.. so set value to
                        // ever it was before.
                        value = oldValue;
                    }
                }

                stylesheetElement.setAttribute(name, value);
            }

            // now we want all child nodes (<xsl:template>) for the stylesheet
            nl = n.getChildNodes();
            for (int i = 0; i < nl.getLength(); i++) {
                Node thisNode = nl.item(i);

                // iterate over existing rules.
                // if we find a duplicate rule, remove the old one
                // to make room for the new one (thisNode has precedence
                // over existingRules).
                NodeList existingRules = stylesheetElement.getChildNodes();
                if (existingRules != null) {
                    for (int z = 0; z < existingRules.getLength(); z++) {
                        Node testNode = existingRules.item(z);
                        if (duplicateTemplateRule(thisNode, testNode)) {
                            // we have a match, remove rule!
                            stylesheetElement.removeChild(testNode);
                        }
                    }
                }

                Node tmp = result.importNode(thisNode, true);

                // need to special-case xsl:import, because they
                // must all come before the first xsl:template
                if (tmp.getNodeName().equals("xsl:import")) {
                    NodeList tmpNodeList = stylesheetElement.getChildNodes();
                    for (int ii = 0; ii < tmpNodeList.getLength(); ii++) {
                        Node testForTemplate = tmpNodeList.item(ii);
                        if (testForTemplate.getNodeName()
                            .equals("xsl:template")) {
                            stylesheetElement.insertBefore
                                (tmp, testForTemplate);
                            break;
                        }
                    }
                } else {
                    stylesheetElement.appendChild(tmp);
                }

            }
        }
        return result;
    }

    /**
     * @return true if we should consider the template rules denoted
     * by node1 and node2 a duplicate.  Note this isn't perfect, because
     *  we don't try to understand XPath and XSLT here exactly.
     * We consider a rule to be duplicate iff
     * node1.name == node2.name == "xsl:template"
     * AND names empty AND node1[@match] == node2[@match]
     *     OR names non-empty AND node1[@name] == node2[@name]
     */
    private static boolean duplicateTemplateRule(Node node1, Node node2) {
        String nodeName1 = node1.getNodeName();
        String nodeName2 = node2.getNodeName();
        if ("xsl:template".equals(nodeName1)
            && "xsl:template".equals(nodeName2)
            && (node1 instanceof Element)
            && (node2 instanceof Element)) {
            Element e1 = (Element) node1;
            Element e2 = (Element) node2;
            String match1 = e1.getAttribute("match");
            String match2 = e2.getAttribute("match");
            String name1 = e1.getAttribute("name");
            String name2 = e2.getAttribute("name");
            if (name1 != null && name1.length() > 0) {
                // at least one @name not empty.
                // consider duplicate if names match.
                // (allow duplicate @match attrs. with different names)
                if (name1.equals(name2)) {
                    return true;
                }
            } else {
                // at least one @name is empty.
                // if the other @name is empty, then the names don't match
                // so here we check the match rule.
                if (match1.equals(match2)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static Stylesheet createStylesheet(String path) {
        Stylesheet sheet = new Stylesheet();
        sheet.setPath(path);
        sheet.save();
        return sheet;
    }

    public static Stylesheet createStylesheet(
                                              String path, java.util.Locale locale
                                              ) {
        Stylesheet sheet = new Stylesheet();
        sheet.setPath(path);
        sheet.setLocale(locale);
        sheet.save();
        return sheet;
    }

    public String toString() {
        if (m_stylesheetDOM == null) {
            // see if we read from a file
            if (m_fileSources != null && m_fileSources.size() > 0) {
                String path = ((FileSource)m_fileSources.get(0)).getName();
                return "[contents of " + path + "]";
            } else {
                return "null Stylesheet";
            }
        }
        return com.arsdigita.xml.Document.toString(m_stylesheetDOM);
    }

    /**
     * Gets the default output type for new stylesheets.
     * @return the default output type for new stylesheets
     **/
    public static String getDefaultOutputType() {
        return s_defaultOutputType;
    }

    /**
     * Returns a string representation of the primary stylesheet.
     * @param ctx the current servlet context
     * @return a string representation of the primary stylesheet.
     */
    public String getPrimaryStylesheetContents(ServletContext ctx) throws IOException
    {
        File f;
        FileReader in = null;
        String path = getPath();
        String realPath = ctx.getRealPath(path);
        f = new File(realPath);
        in = new FileReader(f);
        int size = (int) f.length();
        char[] data = new char[size];
        int chars_read = 0;

        while (chars_read < size)
            chars_read += in.read(data, chars_read, size - chars_read);

        String returnstring = new String(data);
        return (returnstring);
    }

    /**
     * Returns a byte array that is the contents of a JAR file
     * containing all the XSL stylesheets imported by this stylesheet
     * plus the primary stylesheet (the root of the import
     * tree).
     * @param ctx the current servlet context
     * @return a byte[] consisting of a JAR file contents
     * that contains all the XSL stylesheets imported by this stylesheet.
     */
    public byte[] getAllStylesheetContents(ServletContext ctx)
        throws IOException {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        JarOutputStream jos = new JarOutputStream(baos);

        List l = new ArrayList();
        String path = getPath();
        String realPath = ctx.getRealPath(path);

        File file = new File(realPath);
        l.add(file);

        importRecurseHelper(file, l, jos, true);
        jos.finish();
        return(baos.toByteArray());
    }

    /**
     * Returns a list of all stylesheet files that this stylesheet
     * uses, including imported stylesheets (through xsl:import and
     * xsl:include).
     * @param ctx the current servlet context
     * @return a list of file objects.
     */
    public List getStylesheetList(ServletContext ctx) throws IOException {
        List l = new ArrayList();
        String path = getPath();
        String realPath = ctx.getRealPath(path);

        File file = new File(realPath);
        l.add(file);
        importRecurseHelper(file, l, null, true);

        return(l);

    }

    /**
     *
     * Recurses the list of all stylesheets imported from this stylesheet,
     * and returns a list of files.
     * @return the list of stylesheet files (File objects) that are
     *  imported by this <code>Stylesheet</code>.
     */
    public List getImports() {
        FileSource fs = (FileSource)m_fileSources.get(0);
        ResourceManager rm = ResourceManager.getInstance();
        ServletContext sctx = rm.getServletContext();
        File file = new File(sctx.getRealPath(fs.getName()));
        List result = new ArrayList();
        try {
            importRecurseHelper(file, result, null, true);
        } catch (IOException ioe) {
            s_cat.error("getImports", ioe);
        }
        return result;
    }

    /**
     *
     * Helper function for findImports()
     * @param file the stylesheet file to recurse imports for; must
     * correspond to physical location on disk
     * @param l the list to append imported files to
     * @param buf a string buffer to write the stylesheet
     * with all imports unrolled / expanded
     * @param isFirst indicates whether importRecurseHelper has been
     * called already
     */

    // FIXME: the parameter isFirst is passed in but never used. -- 2002-11-26
    private void importRecurseHelper(File file,
                                     List l,
                                     JarOutputStream jos,
                                     boolean isFirst)
        throws IOException
    {
        String line;
        Perl5Util re = new Perl5Util();

        ResourceManager rm = ResourceManager.getInstance();
        String root = rm.getWebappRoot().getCanonicalPath();
        if (jos != null) {
            String entryName = file.getCanonicalPath()
                .substring(root.length() + 1);
            JarEntry jent = new JarEntry(entryName);
            jos.putNextEntry(jent);
        }

        BufferedReader bufRead = new BufferedReader(new FileReader(file));
        ArrayList recurselist = new ArrayList();

        while ((line = bufRead.readLine()) != null) {
            if (re.match("m$<xsl:import\\s+href=\"([^\"]+)\"[^>]*>$", line)) {
                String href = re.group(1);

                // take the import href and treat it as a path
                // relative to the current file's directory
                //
                // note, we need to compensate for the fact that
                // we're not really dealing with relative PATHS
                // here but relative URLs.
                File newFile = new File(file.getParent(), href);
                // definition of "file equality" means we can't use
                // file.equals(), since we want to define files as equal
                // if their *canonical* paths are the same
                String canonical = newFile.getCanonicalPath();
                boolean dup = false;
                for (int i = 0; i < l.size(); i++) {
                    File testFile = (File)l.get(i);
                    if (canonical.equals(testFile.getCanonicalPath())) {
                        dup = true;
                        break;
                    }
                }
                if (!dup) {
                    l.add(newFile);
                    recurselist.add(newFile);
                }
            }
            if (jos != null) {
                jos.write(line.getBytes());
                jos.write("\n".getBytes());
            }
        } // end (for each line)
        bufRead.close();
        if (jos != null) {
            jos.closeEntry();
        }
        for (Iterator i = recurselist.iterator(); i.hasNext(); ) {
            File f = (File)i.next();
            importRecurseHelper(f, l, jos, false);
        }
    }
}
