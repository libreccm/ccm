package com.arsdigita.aplaws;

import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.StringTokenizer;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.rpc.ParameterMode;

import org.apache.axis.Constants;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionBuilder;
import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.arsdigita.aplaws.ui.ItemCategoryPicker;
import com.arsdigita.categorization.Category;
import com.arsdigita.cms.ContentBundle;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.Folder;
import com.arsdigita.cms.dispatcher.ItemResolver;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainCollection;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.domain.DomainServiceInterfaceExposer;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.KernelExcursion;
import com.arsdigita.london.cms.dublin.DublinCoreItem;
import com.arsdigita.london.navigation.Navigation;
import com.arsdigita.london.navigation.NavigationFileResolver;
import com.arsdigita.london.terms.Domain;
import com.arsdigita.london.terms.Term;
import com.arsdigita.london.util.Transaction;
import com.arsdigita.persistence.DataAssociation;
import com.arsdigita.persistence.DataAssociationCursor;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.Filter;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.search.ContentProvider;
import com.arsdigita.search.ContentType;
import com.arsdigita.search.MetadataProvider;
import com.arsdigita.search.MetadataProviderRegistry;
import com.arsdigita.web.Application;
/**
 * Command line tool to automatically assign terms.
 * 
 * Input is a MASmedia Searchlight Indexer report XML file.
 * This service is available for registered users
 * at http://demo.masprovider.com/searchLight/
 * 
 * @author apevec@redhat.com
 */
public class AutoCategorisation extends com.arsdigita.packaging.Program {

    private static final Logger LOG = Logger.getLogger(AutoCategorisation.class);
    private static final String CCM_PREFIX = "/ccm/";
    
    private SAXParser parser;

    public AutoCategorisation() {
        super("AutoCategorisation", "1.0", "FILENAME(Searchlight XML report)");
        getOptions().addOption
        (OptionBuilder
         .hasArg(false)
         .withLongOpt("test")
         .withDescription("Test only")
         .create('t'));

        try {
            parser = SAXParserFactory.newInstance().newSAXParser();
        } catch (ParserConfigurationException pce) {
            throw new RuntimeException("SAX parser configuration error", pce);
        } catch (SAXException se) {
            throw new RuntimeException("SAX parser error", se);
        }
    }

    protected void doRun(final CommandLine cmdLine) {
        new Transaction() {
            public void doRun() {
                new KernelExcursion() {
                    public void excurse() {
                        setEffectiveParty(Kernel.getSystemParty());
                        String[] args = cmdLine.getArgs();
                        if (args.length == 1) {
                            String filename = args[0];
                            try {
                                boolean persistChanges = ! cmdLine.hasOption('t');
                                parser.parse(new InputSource(new FileReader(filename)),
                                        new SearchlightHandler(isDebug(), isVerbose(), persistChanges));
                            } catch (SAXException ex) {
                                throw new RuntimeException(ex);
                            } catch (IOException ex) {
                                throw new RuntimeException(ex);
                            }
                        } else {
                            help(System.err);
                            System.exit(1);
                        }
                    }
                }.run();
            }
        }.run();
    }

    public static void main(String[] args) {
        new AutoCategorisation().run(args);
    }

    /**
     * Parse the XML site report from MASmedia Searchlight Indexer.
     */
    private static class SearchlightHandler extends DefaultHandler {

        boolean isDebug;
        boolean isVerbose;
        boolean persistChanges;
        StringBuffer buffer;
        String urlid;
        String url;
        ContentItem item;
        String scheme;
        Collection keywords = new HashSet();
        Collection terms = new HashSet();
        Domain ipsv, lgcl, gcl, lgsl, lgdl;

        public SearchlightHandler(boolean isDebug, boolean isVerbose, boolean persistChanges) {
            this.isDebug = isDebug;
            this.isVerbose = isVerbose;
            this.persistChanges = persistChanges;
            ipsv = Domain.retrieve("IPSV");
            lgcl = Domain.retrieve("LGCL");
            gcl = Domain.retrieve("GCL");
            lgsl = Domain.retrieve("LGSL");
            lgdl = Domain.retrieve("LGDL");
        }

        public void startDocument() throws SAXException {
            if (isDebug) {
                out("startDoc");
            }
        }

        public void characters(char[] ch, int start, int len)
                throws SAXException {
            for (int i = 0; i < len; i++) {
                buffer.append(ch[start + i]);
            }
        }

        public void startElement(String uri, String localName, String qName,
                Attributes attributes) throws SAXException {
            if (isDebug) {
                out("startElement " + qName);
            }
            buffer = new StringBuffer();
            if ("url".equals(qName)) {
                urlid = null;
                url = null;
                item = null;
            } else if ("unformated_data".equals(qName)) {
                keywords.clear();
                terms.clear();
                scheme = null;
            } else if ("keyword_data".equals(qName)) {
                scheme = attributes.getValue("scheme");
            } else if ("category_data".equals(qName)) {
                scheme = attributes.getValue("scheme");
            } // if qName
        }
        // XXX term@id is *NOT* Term.uniqueID, use unformated_data/category_data
        // and retrieve terms by name
/* example from CAMDEN.xml
      <tags>
        <terms>
          <term score="40" thesarus="LGTL" id="9087">A to Z</term>
          <term score="6" thesarus="LGCS" id="8030">Development control</term>
          <term score="6" thesarus="LGSL" id="10213">Development control</term>
          <term score="6" thesarus="IPSV" id="12738">Development control</term>
          <term score="6" thesarus="IPSV" id="11440">Domestic violence</term>
          <term score="6" thesarus="IPSV" id="11695">Planning (town and country)</term>
        </terms>
        <category><![CDATA[<meta name="eGMS.subject.category" scheme="LGCS" content="Development Control" />]]><![CDATA[<meta name="eGMS.subject.category" scheme="LGSL" content="Development Control" />]]><![CDATA[<meta name="eGMS.subject.category" scheme="IPSV" content="Domestic violence; Planning (town and country)" />]]></category>
        <keyword><![CDATA[<meta name="eGMS.subject.keyword" scheme="LGTL" content="A to Z" />]]><![CDATA[<meta name="eGMS.subject.keyword" scheme="IPSV" content="Development Control" />]]></keyword>
        <unformated_data>
          <keyword_data scheme="LGTL">A to Z</keyword_data>
          <keyword_data scheme="IPSV">Development Control</keyword_data>
          <category_data scheme="LGCS">Development Control</category_data>
          <category_data scheme="LGSL">Development Control</category_data>
          <category_data scheme="IPSV">Domestic violence; Planning (town and country)</category_data>
        </unformated_data>
      </tags>
 */
        public void endElement(String uri, String localName, String qName)
                throws SAXException {
            if (isDebug) {
                out("endElement " + qName);
            }
            if ("urlid".equals(qName)) {
                urlid = buffer.toString();
                if (isVerbose) {
                    out("urlid "+urlid);
                }
            } else if ("urladdress".equals(qName)) {
                findItem();
                if (item != null && isVerbose) {
                    out("item " + item + " at " + url);
                }
            } else if ("keyword_data".equals(qName)) {
                // merge all keywords
                StringTokenizer tok = new StringTokenizer(buffer.toString(),
                        ";");
                while (tok.hasMoreTokens()) {
                    keywords.add(tok.nextToken().trim());
                }
            } else if ("category_data".equals(qName)) {
                // use only IPSV terms
                if ("IPSV".equals(scheme)) {
                    StringTokenizer tok = new StringTokenizer(
                            buffer.toString(), ";");
                    while (tok.hasMoreTokens()) {
                        String name = tok.nextToken().trim();
                        Term term = findTerm(scheme, name);
                        if (term != null) {
                            terms.add(term);
                        } else if (isVerbose) {
                            out("term not found " + scheme + '/' + name);
                        }
                    }
                }
            } else if ("unformated_data".equals(qName)) {
                assignKeywords();
                assignTerms();
            } // if qName
        }

        public void endDocument() throws SAXException {
            if (isDebug) {
                out("endDoc");
            }
        }
        
        private void findItem() {
            url = buffer.toString();
            // resolve url to the item
            // supported are Navigation and ContentSection URLs
            int ccmPrefix = url.indexOf(CCM_PREFIX);
            if (ccmPrefix > -1) {
                int appBegin = ccmPrefix + 5;
                int appEnd = url.indexOf('/', appBegin);
                if (appEnd > appBegin) {
                    String appURL = url.substring(appBegin, appEnd);
                    Application app = Application
                            .retrieveApplicationForPath('/' + appURL + '/');
                    if (app != null) {
                        String appType = app.getApplicationType()
                                .getApplicationObjectType();
                        if (ContentSection.BASE_DATA_OBJECT_TYPE
                                .equals(appType)) {
                            // a Content Section URL detected, resolving
                            // item path
                            ContentSection cs = (ContentSection) app;
                            ItemResolver resolver = cs.getItemResolver();
                            if (resolver != null) {
                                int queryBegin = url.indexOf('?', appEnd);
                                if (queryBegin > appEnd) {
                                    // MPA URLs can have ?page=N which confuses
                                    // c.a.cms.d.MLIR
                                    url = url.substring(0, queryBegin);
                                }
                                item = resolver.getItem(cs, url
                                        .substring(appEnd), ContentItem.LIVE);
                                if (item != null) {
                                    item = item.getDraftVersion();
                                }
                                // make sure we return real content item,
                                // and not structure (folder or bundle)
                                if (item != null && item instanceof Folder) {
                                    item = ((Folder) item).getIndexItem();
                                }
                                if (item != null
                                        && item instanceof ContentBundle) {
                                    item = ((ContentBundle) item)
                                            .getPrimaryInstance();
                                }
                                if (item == null) {
                                    out("item not found for " + url);
                                }
                            } else {
                                if (isVerbose) {
                                    out("ContentSection without resolver");
                                }
                            }
                        } else if (Navigation.BASE_DATA_OBJECT_TYPE
                                .equals(appType)) {
                            // a Navigation URL detected, resolving category
                            Navigation nav = (Navigation) app;
                            Category cat = null;
                            // categoryID=
                            int catBegin = url.indexOf("categoryID=",
                                    appEnd);
                            if (catBegin > appEnd) {
                                BigDecimal catID = new BigDecimal(url
                                        .substring(catBegin + 11));
                                cat = new Category(catID);
                            } else {
                                // named cat path, resolve using
                                // default context XXX subsites?
                                Category root = Category.getRootForObject(
                                        nav, null);
                                Category[] cats = NavigationFileResolver
                                        .resolveCategory(root, url
                                                .substring(appEnd));
                                if (cats != null && cats.length != 0) {
                                    cat = cats[cats.length - 1];
                                } else {
                                    if (isVerbose) {
                                        out("category path not found " + url);
                                    }
                                }
                            }
                            // category index item
                            if (cat != null) {
                                ContentBundle bundle = (ContentBundle) cat.getIndexObject();
                                if (bundle != null) {
                                    item = bundle.getPrimaryInstance();
                                }
                            }
                        } else {
                            if (isVerbose) {
                                out("unsupported application " + appType + " at " + url);
                            }
                        }
                    } else {
                        if (isVerbose) {
                            out("application not found " + url);
                        }
                    }
                } else {
                    if (isVerbose) {
                        out("unsupported CCM url " + url);
                    }
                }
            } else {
                if (isVerbose) {
                    out("unsupported url " + url);
                }
            }
        }
        
        private void assignTerms() {
            if (item != null) {
                ContentBundle bundle = (ContentBundle) item.getParent();
                Collection manualCategories = new HashSet();
                Collection oldAutoCategories = new HashSet();
                Collection newAutoCategories = new LinkedList();
                DataAssociationCursor cursor = ((DataAssociation)DomainServiceInterfaceExposer
                        .get(bundle, "categories")).cursor();
                // cat_object_category_map.auto_p
                // cursor.addEqualsFilter("link.isAuto", Boolean.FALSE);
                while (cursor.next()) {
                    Object categoryID = cursor.get("id");
                    Boolean isAuto = (Boolean) cursor.getLinkProperty("isAuto");
                    if (isAuto.booleanValue()) {
                        oldAutoCategories.add(categoryID);
                    } else {
                        manualCategories.add(categoryID);
                    }
                }
                // assign all new auto-derived terms, unless it's already assigned
                // prefer manual IPSV/LGCL over auto IPSV
                // check existing manual terms
                Collection manualIPSV = new LinkedList();
                Collection manualLGCL = new LinkedList();
                if (!manualCategories.isEmpty()) {
                    DataCollection dc = SessionManager.getSession().retrieve(Term.BASE_DATA_OBJECT_TYPE);
                    Filter f = dc.addFilter("model.id IN :manualCats");
                    f.set("manualCats", manualCategories);
                    dc.addFilter("domain.key IN ('IPSV','LGCL')");
                    dc.addPath("domain.key");
                    dc.addPath("model.id");
                    while (dc.next()) {
                        String domainKey = (String)dc.get("domain.key");
                        if ("IPSV".equals(domainKey)) {
                            manualIPSV.add(dc.get("model.id"));
                        } else if ("LGCL".equals(domainKey)) {
                            manualLGCL.add(dc.get("model.id"));
                        }
                    }
                }
                boolean canAutoIPSV = true;
                boolean canAutoLGCL = true;
                if (manualLGCL.isEmpty() && !manualIPSV.isEmpty()) {
                    // auto-assign related LGCL from manual IPSV
                    if (isVerbose) {
                        out("derive from manual IPSV");
                    }
                    // TODO move getRelatedTerms etc. out of UI code
                    Collection relatedLGCL = ItemCategoryPicker
                            .getRelatedTerms(manualIPSV, lgcl);
                    if (!relatedLGCL.isEmpty()) {
                        ItemCategoryPicker.assignTerms(relatedLGCL, bundle);
                        canAutoLGCL = false;
                    }
                }
                if (manualIPSV.isEmpty() && !manualLGCL.isEmpty()) {
                    // auto-assign related IPSV from manual LGCL
                    if (isVerbose) {
                        out("derive from manual LGCL");
                    }
                    Collection relatedIPSV = ItemCategoryPicker
                            .getRelatedTerms(manualLGCL, ipsv);
                    if (!relatedIPSV.isEmpty()) {
                        ItemCategoryPicker.assignTerms(relatedIPSV, bundle);
                        canAutoIPSV = false;
                    }
                }

                // auto assign IPSV/LGCL
                for (Iterator iter = terms.iterator(); iter.hasNext();) {
                    Term t = (Term) iter.next();
                    Domain d = t.getDomain();
                    if (canAutoIPSV && ipsv.equals(d) || canAutoLGCL
                            && lgcl.equals(d)) {

                        BigDecimal categoryID = t.getModel().getID();
                        if ( !manualCategories.contains(categoryID)) {
                            if (!oldAutoCategories.contains(categoryID)) {
                                if (persistChanges) {
                                    t.addObject(bundle);
                                }
                                newAutoCategories.add(categoryID); // to be marked isAuto
                                if (isVerbose) {
                                    out("autoASSIGN " + t + " to " + bundle);
                                }
                            } else {
                                oldAutoCategories.remove(categoryID);
                                if (isVerbose) {
                                    out("already auto assigned " + t);
                                }
                            }
                        } else if (isVerbose) {
                            out("already manually assigned " + t);
                        }
                    }
                }
                // cleanup old auto-assigned terms
                for (Iterator iter = oldAutoCategories.iterator(); iter
                        .hasNext();) {
                    Category category = new Category((BigDecimal) iter.next());
                    if (persistChanges) {
                        category.removeChild(bundle);
                    }
                    if (isVerbose) {
                        out("removing oldAuto " + category + " from " + bundle);
                    }
                }
                if (!newAutoCategories.isEmpty()) {
                    cursor = ((DataAssociation) DomainServiceInterfaceExposer
                            .get(bundle, "categories")).cursor();
                    Filter f = cursor.addFilter("id IN :newAutoCats");
                    f.set("newAutoCats", newAutoCategories);
                    while (cursor.next()) {
                        Object categoryID = cursor.get("id");
                        if (persistChanges) {
                            DataObject link = cursor.getLink();
                            link.set("isAuto", Boolean.TRUE);
                        }
                        if (isVerbose) {
                            out("isAuto=TRUE for new categoryID=" + categoryID
                                    + "/" + bundle);
                        }
                    }
                }
                // TODO assign related GCL, LGSL, LGDL - move that code out of
                // UI (ItemCategoryPicker)
            }
        }

        private void assignKeywords() {
            if (item != null && !keywords.isEmpty()) {
                DublinCoreItem dcItem = DublinCoreItem.findByOwner(item);
                if (dcItem != null) {
                    // preserve existing dcItem.getKeywords()
                    // NOTE: "DC keywords" metadata is stored as a string, cannot tell which keywords are auto.
                    String dcKeywords = dcItem.getKeywords();
                    if (dcKeywords != null) {
                        StringTokenizer tok = new StringTokenizer(dcKeywords, ";");
                        while (tok.hasMoreTokens()) {
                            keywords.add(tok.nextToken().trim());
                        }
                    }
                }
                StringBuffer buf = new StringBuffer();
                // reconstruct "DC keywords" and store them
                Iterator i=keywords.iterator();
                if (i.hasNext()) {
                    buf.append(i.next());
                }
                for (; i.hasNext();) {
                    buf.append(';').append(' ').append(i.next());
                }
                String dcKeywords = buf.toString();
                if (isVerbose) {
                    out("ASSIGN DC keywords \""+dcKeywords+"\"");
                }
                if (persistChanges) {
                    if (dcItem == null) {
                        dcItem = DublinCoreItem.create(item);
                    }
                    dcItem.setKeywords(dcKeywords);
                }
            }
        }
        
    }

    private static void out(String line) {
        LOG.info(line);
    }
    
    private static Term findTerm(String domainKey, String name) {
        try {
            Domain domain = Domain.retrieve(domainKey);
            if (domain != null) {
                DomainCollection terms = domain.getTerms();
                Filter f = terms.addFilter("upper("+Term.NAME+") = :name");
                f.set("name", name.toUpperCase());
                if (terms.next()) {
                    Term term = (Term) terms.getDomainObject();
                    terms.close();
                    return term;
                }
            }
        } catch (DataObjectNotFoundException donfe) {
            // domain not found 
        }
        return null;
    }

    /**
     * Call Searchlight web service.
     * Process service response and try to map detected categories to TermS.
     * 
     * @param item content item
     * @return a list of suggested TermS derived from the text extracted
     *  from the given content item
     */
    public static Collection getAutoTerms(ContentItem item) throws ServiceFailed {
        Collection terms = new HashSet();
        try {
            // text extraction, see c.a.search.lucene.DocumentObserver
            MetadataProvider adapter = MetadataProviderRegistry
                    .findAdapter(item.getObjectType());
            if (adapter != null) {
                ContentProvider[] content = adapter.getContent(item,
                        ContentType.TEXT);
                StringBuffer buf = new StringBuffer();
                for (int i = 0, n = content.length; i < n; i++) {
                    if (content[i].getType().equals(ContentType.TEXT)) {
                        buf.append(new String(content[i].getBytes()));
                    }
                }
                String endpoint = Aplaws.getAplawsConfig().getAutocatServiceURL();
                String version = "1.0";
                Service service = new Service();
                Call call = (Call) service.createCall();
                call.setTargetEndpointAddress(new java.net.URL(endpoint));
                call.setOperationName("getCategoriesAndKeywordsFromText");
                call.addParameter("text", Constants.XSD_STRING,
                        ParameterMode.IN);
                call.addParameter("version", Constants.XSD_STRING,
                        ParameterMode.IN);
                call.setUsername(Aplaws.getAplawsConfig()
                        .getAutocatServiceUsername());
                call.setPassword(Aplaws.getAplawsConfig()
                        .getAutocatServicePassword());
                call.setReturnType(Constants.XSD_STRING);
                LOG.debug("username/password="+call.getUsername()+"/"+call.getPassword());
                String result = (String) call.invoke(new Object[] {
                        buf.toString(), version });
                // parse the response from autocat service
                SAXParser parser;
                parser = SAXParserFactory.newInstance().newSAXParser();
                parser.parse(new InputSource(new StringReader(result)),
                        new SearchlightServiceHandler(terms));
            } else {
                LOG.info("no metadata adapter for " + item);
            }
        } catch (Exception e) {
            LOG.info("autocat service call failed", e);
            throw new ServiceFailed(e);
        }
        return terms;
    }

    // example autocat service response:
    /*<searchlight:textminer
     * xmlns:searchlight="http://www.cintra.com/MASmedia/Searchlight">
     * <searchlight:results>
     *  <searchlight:category-matches>
     *   <searchlight:category-match score="11">
     *    <searchlight:category>Community Safety</searchlight:category>
     *    <searchlight:thesaurus-ref thesaurus="LGCS" version="0.01" id="182" />
     *    <searchlight:thesaurus-ref thesaurus="LGSL" version="2.02" id="870" />
     *    <searchlight:thesaurus-ref thesaurus="IPSV" version="2.00" id="6280" />
     *   </searchlight:category-match>
     *   ...
     */
    private static class SearchlightServiceHandler extends DefaultHandler {

        StringBuffer buffer;
        String category = null;
        String thesaurus = null;
        String score = null;
        Collection terms;

        public SearchlightServiceHandler(Collection terms) {
            this.terms = terms;
        }

        public void characters(char[] ch, int start, int len)
                throws SAXException {
           for (int i = 0; i < len; i++) {
               buffer.append(ch[start + i]);
           }
        }

        public void startElement(String uri, String localName, String qName,
                Attributes attributes) throws SAXException {
            buffer = new StringBuffer();
            if ("searchlight:category-match".equals(qName)) {
                score = attributes.getValue("score");
            } else if (category != null && score != null
                && "searchlight:thesaurus-ref".equals(qName)) {

                thesaurus = attributes.getValue("thesaurus");
                if ("IPSV".equals(thesaurus)) {
                    LOG.debug("IPSV "+category);
                    Term t = findTerm(thesaurus, category);
                    if (t != null) {
                        terms.add(t);
                        LOG.debug("term = "+t);
                    } else {
                        LOG.debug("term not found");
                    }
                }
            }
        }

        public void endElement(String uri, String localName, String qName)
                throws SAXException {
            if ("searchlight:category".equals(qName)) {
                category = buffer.toString();
            } else if ("searchlight:category-match".equals(qName)) {
                category = null; score = null; thesaurus = null;
            } else if ("searchlight:category-matches".equals(qName)) {
                LOG.debug("terms autodetected "+terms);
            }
        }
    }
    
    public static class ServiceFailed extends RuntimeException {
        public ServiceFailed(Throwable cause) {
            super(cause);
        }
    }
}
