package com.arsdigita.london.importer.cms;

import com.arsdigita.london.importer.DomainObjectParser;
import com.arsdigita.london.importer.DomainObjectMapper;

import com.arsdigita.cms.BinaryAsset;
import com.arsdigita.cms.CMS;
import com.arsdigita.cms.ContentBundle;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ContentType;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.kernel.ACSObject;
import com.arsdigita.mimetypes.MimeType;
import com.arsdigita.persistence.DataAssociation;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.metadata.ObjectType;
import com.arsdigita.persistence.metadata.Property;
import com.arsdigita.util.Assert;
import com.arsdigita.util.UncheckedWrapperException;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import sun.misc.BASE64Decoder;

/**
 *   Core importer class, handling the &lt;cms:item&gt; XML subblock.
 *
 *  @see com.arsdigita.london.importer
 */
public class ItemParser extends DomainObjectParser {

    private static Logger s_log =
                          Logger.getLogger(ItemParser.class);
    public static final String DEFAULT_DOMAIN_CLASS = "defaultDomainClass";
    public static final String ID = "id";
    public static final String IS_DELETED = "isDeleted";
    public static final String VERSION = "version";
    public static final String ANCESTORS = "ancestors";
    public static final String NAME = "name";
    public static final String OID_ATTR = "oid";
    public static final String IMAGE_ID = "imageId";
    public static final String ARTICLE_ID = "articleId";
    public static final String DEFAULT_ANCESTORS = "defaultAncestors";
    public static final String DISPLAY_NAME = "displayName";
    public static final String FILE = "file";
    public static final String ENCODING_ATTR = "encoding";
    public static final String ENCODING_BASE64 = "base64";
    public static final String INDEX_ITEM_ATTR = "indexItem";
    public static final String RELABEL_FOLDER_ATTR = "relabelFolder";
    // Contains email of the author.  If present, a worfklow is started
    // for the imported item on behalf of that user.  CCM user is created
    // if not found in database.
    public static final String AUTHOR_ATTR = "author";
    public static final String END_OF_LIFE_ATTR = "eol";
    /**
     *  Object types holding the "global" OIDs, i.e. those which don't
     * change from one CCM instance to another.  Importer will not try
     * to create any of those.
     */
    static final String[] GLOBAL_OID_TYPES = new String[]{
        "com.arsdigita.cms.MimeType",
        "com.arsdigita.cms.contenttypes.IsoCountry"
    };
    // Some sequence of charactes that cannot be part of property name
    private static final String PLAIN_ATTRIBUTE =
                                ":..just the plain attribute!..:";
    private static final String DO_NOT_SAVE =
                                ":..please persistence don't save!..:";
    private static final String COUNT_SUFFIX = "////";
    // As we're traversing the XML tree, we keep the stack of parsed objects here
    private Stack m_objectStack;
    // And we keep track of attributes here - you never know when you gonna need then
    private Stack m_attributeStack;
    private Map m_currentDataObject;
    private int m_recursionLevel;
    private boolean m_write;
    private static List s_ignoredProps;
    // <launchDate>Wed Nov 12 00:00:00 CET 2003</launchDate>
    private static SimpleDateFormat s_formatter = new SimpleDateFormat(
            "EEE MMM dd HH:mm:ss zzz yyyy");
    private static byte[] s_byteArray = new byte[0];
    private boolean m_begin;
    private boolean m_isIndexItem;
    private boolean m_relabelFolder;
    private String m_author;
    private Date m_archiveDate;

    static {
        s_log.debug("Static initalizer starting...");
        s_ignoredProps = new ArrayList();
        s_ignoredProps.add(ID);
        s_ignoredProps.add(OID_ATTR);
        s_ignoredProps.add(IS_DELETED);
        s_ignoredProps.add(VERSION);
        s_ignoredProps.add(ANCESTORS);
        s_ignoredProps.add(IMAGE_ID);
        s_ignoredProps.add(ARTICLE_ID);
        s_ignoredProps.add(DEFAULT_ANCESTORS);
        s_ignoredProps.add(DO_NOT_SAVE);
        s_log.debug("Static initalizer finished.");
    }

    public ItemParser(File lobDir, DomainObjectMapper mapper) {
        this("item", CMS.CMS_XML_NS, ContentItem.BASE_DATA_OBJECT_TYPE, lobDir,
             mapper);
    }

    public ItemParser(String tagName,
                      String tagURI,
                      String objectType,
                      File lobDir,
                      DomainObjectMapper mapper) {
        super(tagName, tagURI, objectType, lobDir, mapper);

        m_recursionLevel = 0;
        m_objectStack = new Stack();
        m_attributeStack = new Stack();
        m_write = true;
    }

    protected ContentItem importItem() {
        debugLog("Ending content item, write: " + m_write);
        if (m_write) {
            debugLog(
                    "--------------- About to persist ContentItem -------------------");
            System.out.println(
                    "--------------- About to persist ContentItem -------------------");
            return persistContentItem(m_currentDataObject);
        }
        return null;
    }

    public void startBlock() {
        m_begin = true;
    }

    protected void startTag(String name,
                            String uri,
                            Attributes atts) {
        m_attributeStack.push(copyAttributes(atts));
        if (m_begin) {
            m_begin = false;
            OID oid = OID.valueOf(atts.getValue(OID_ATTR));
            m_write = !getObjectMapper().objectExists(oid);
            beginContentItem(oid, atts);
        } else {
            // Ignore everything if DB writing is disabled
            if (!m_write) {
                return;
            }
            Assert.exists(m_currentDataObject, DataObject.class);
            OID currentOID = (OID) m_currentDataObject.get(OID_ATTR);
            ObjectType ot = currentOID.getObjectType();
            if (ot == null) {
                throw new UncheckedWrapperException("no object type "
                                                    + currentOID);
            }
            Property prop = ot.getProperty(name);
            if (prop == null) {
                throw new UncheckedWrapperException("no property " + name
                                                    + " for " + ot.
                        getQualifiedName());
            }
            if (prop.isAttribute()) {
                beginAttribute(m_currentDataObject, prop);
            } else {
                OID oid = OID.valueOf(atts.getValue(OID_ATTR));
                if (prop.isCollection()) {
                    beginAssociation(m_currentDataObject, oid, prop);
                } else if (prop.isRole()) {
                    beginRole(m_currentDataObject, oid, prop);
                } else {
                    s_log.error("Unknown property type: " + prop);
                }
            }
        }
    }

    public void endBlock() {
        ContentItem item = importItem();
        if (s_log.isDebugEnabled()) {
            s_log.debug("End block " + item);
        }
        if (item != null) {
            setDomainObject(item);
        }
    }

    /**
     *  Called whenever a start of block which represents the association
     * is encountered.
     *
     *   @param parentObject (String,Object) map of properties that
     *                       have been collected so far from the import file
     *   @param oid source OID of the associated object
     *   @param property persistence property describing the association
     */
    protected void beginAssociation(Map parentObject,
                                    OID oid,
                                    Property property) {
        debugLog("Starting association: " + property.getName());
        m_objectStack.push(parentObject);
        m_currentDataObject = getDataObject(oid);
    }

    /**
     *  Called whenever a start of block which represents the role
     * is encountered.
     *
     *   @param parentObject (String,Object) map of properties that
     *                       have been collected so far from the import file
     *   @param oid source OID of the role object
     *   @param property persistence property describing the role
     */
    protected void beginRole(Map parentObject,
                             OID oid,
                             Property property) {
        debugLog("Starting role: " + property.getName());
        m_objectStack.push(parentObject);
        m_currentDataObject = getDataObject(oid);
    }

    /**
     *   Called whenever CMS content item block is started with
     *  &lt;cms:item&gt; element.
     *
     * @param oid the source OID of the item
     * @param atts attributes of opening &lt;cms:item&gt; tag
     */
    protected void beginContentItem(OID oid, Attributes atts) {
        m_objectStack.clear();
        m_recursionLevel = 0;
        m_isIndexItem = false;
        m_relabelFolder = false;
        m_author = null;
        m_archiveDate = null;
        if (m_write) {
            m_currentDataObject = getDataObject(oid);
            m_isIndexItem = "true".equals(atts.getValue(INDEX_ITEM_ATTR));
            m_relabelFolder = "true".equals(atts.getValue(RELABEL_FOLDER_ATTR));
            m_author = atts.getValue(AUTHOR_ATTR);
            String eol = atts.getValue(END_OF_LIFE_ATTR);
            if (eol != null && eol.trim().length() > 0) {
                ParsePosition pos = new ParsePosition(0);
                m_archiveDate = s_formatter.parse(eol.trim(), pos);
            }
            m_objectStack.push(m_currentDataObject);
        }
        debugLog("Start of content item " + oid + " encountered, write="
                 + m_write);
    }

    /**
     *   Called whenever a plain attribute (neither association nor role)
     * element is encountered.
     *
     *  @param objectMap map of properties imported so far for the current object
     *  @param prop the current property we've just encountered
     */
    protected void beginAttribute(Map objectMap, Property prop) {
        m_objectStack.push(PLAIN_ATTRIBUTE);
    }

    protected void endTag(String name,
                          String uri) {
        if (getTagName().equals(name)) {
            s_log.debug("All done here");
            return;
        }

        // Ignore everything if DB writing is disabled
        if (m_write) {
            if (PLAIN_ATTRIBUTE.equals(m_objectStack.peek())) {
                Property prop = ((OID) m_currentDataObject.get(OID_ATTR)).
                        getObjectType().getProperty(name);
                endAttribute(m_currentDataObject, prop, getTagBody());
            } else {
                // The current attribute is either role or association (collection)
                Map parentObject = (Map) m_objectStack.peek();
                Property prop = ((OID) parentObject.get(OID_ATTR)).getObjectType().
                        getProperty(name);
                if (prop.isCollection()) {
                    endAssociation(parentObject, m_currentDataObject, prop);
                } else if (prop.isRole()) {
                    endRole(parentObject, m_currentDataObject, prop);
                } else {
                    s_log.error("Unknown property type: " + prop);
                }
            }
        }
        if (!m_attributeStack.empty()) {
            m_attributeStack.pop();
        }
    }

    /**
     * @return true if newly imported item is index item.
     */
    public boolean isIndexItem() {
        return m_isIndexItem;
    }

    /**
     * @return true if item is index item and its title should propagate to folder
     */
    public boolean relabelFolder() {
        return m_isIndexItem && m_relabelFolder;
    }

    /**
     * @return the email address of the item's author (if provided)
     */
    public String getAuthor() {
        return m_author;
    }

    /**
     * @return the end of lifecycle date, might be null
     */
    public Date getArchiveDate() {
        return (Date) m_archiveDate.clone();
    }

    private ContentItem persistContentItem(Map itemMap) {
        OID srcOid = (OID) itemMap.get(OID_ATTR);
        String type = srcOid.getObjectType().getQualifiedName();
        DataObject itemDataObject = SessionManager.getSession().create(type);
        ContentItem item =
                    (ContentItem) createACSObject(itemDataObject, itemMap);
        if (item != null && item.getLanguage() == null) {
            item.setLanguage("en");
        }

        getObjectMapper().setObject(srcOid, item);

        setAllProperties(item, itemDataObject, itemMap, 0);                

        s_log.info("Created item " + item.getOID() + " from " + srcOid);
        return item;
    }

    /**
     *   Called whenever a plain attribute (neither association nor role) element is ended.
     *
     *  @param objectMap properties of current object
     *  @param prop persistence property corresponding to the visited attribute
     *  @param value attribute value as parsed from XML file
     */
    protected void endAttribute(Map dataObject, Property prop, String value) {
        // First remove PLAIN_ATTRIBUTE dummy from stack.
        m_objectStack.pop();
        debugLog("Ending attribute: " + prop.getName() + ": " + getTagBody());
        setProperty(dataObject, prop, value);
    }

    /**
     *  Called whenever an association property block is ended.
     *
     *   @param parentObject properties of the object we're attaching associated object to,
     *                       not necessarily complete
     *   @param childObject properties of the associated object, completely parsed
     *   @param property persistence property describing this association
     */
    protected void endAssociation(Map parentObject,
                                  Map childObject,
                                  Property property) {
        String propName = property.getName();
        debugLog("Ending association: " + propName + ": " + childObject);
        // Count how many we already have
        Integer assocCount = (Integer) parentObject.get(propName + COUNT_SUFFIX);
        int count = 0;
        if (assocCount != null) {
            count = assocCount.intValue();
        }
        count++;
        parentObject.put(propName + COUNT_SUFFIX, Integer.valueOf(count));
        parentObject.put(propName + COUNT_SUFFIX + count, childObject);
        m_currentDataObject = parentObject;
        m_objectStack.pop();
    }

    /**
     *  Called whenever a role property block is ended.
     *
     *   @param parentObject properties of the object we're attaching role to,
     *                       not necessarily complete
     *   @param childObject properties of the role, completely parsed
     *   @param property persistence property describing this role
     */
    protected void endRole(Map parentObject,
                           Map childObject,
                           Property property) {
        String propName = property.getName();
        debugLog("Ending role: " + propName + ": " + childObject);
        // don't set ContentType
        if (!((OID) childObject.get(OID_ATTR)).getObjectType().getQualifiedName().
                equals(ContentType.BASE_DATA_OBJECT_TYPE)) {
            parentObject.put(propName + COUNT_SUFFIX + "1", childObject);
        }
        m_currentDataObject = parentObject;
        m_objectStack.pop();
    }

    /**
     *  Process the item recursively until we get all attributes set.
     */
    private void setAllProperties(ACSObject acs, DataObject dataObject,
                                  Map objectMap, int recursionLevel) {

        m_recursionLevel = recursionLevel;
        debugLog("setAll on " + dataObject.getOID() + ": " + objectMap);

        // Loop over all properties recorded in Map and set them appropriately
        for (Iterator it = objectMap.keySet().iterator(); it.hasNext();) {

            String key = (String) it.next();
            if (s_ignoredProps.contains(key)) {
                debugLog("Ignoring property: " + key);
                continue;
            }
            // First deal with roles and associations:
            // Beware:  Association properties are named "categories////1", "categories////2" etc
            //          Role property is always with multiplicity 1, so "type////1"
            // Beware 2:  There is also a property called "categories////", which just holds count
            //            of how many objects that association holds.  For this purpose, just
            //            ignore the property with simple count
            int pos = key.indexOf(COUNT_SUFFIX);
            if (pos > -1 && key.length() > pos + COUNT_SUFFIX.length()) {
                String propName = key.substring(0, pos);
                debugLog("Compound property: " + propName);
                // We're in role/association, get the child object
                Map childObjectMap = (Map) objectMap.get(key);
                DataObject childObject = null;
                boolean recurse = true;
                OID childOid = (OID) childObjectMap.get(OID_ATTR);
                String type = childOid.getObjectType().getQualifiedName();
                ACSObject childAcs = null;
                // Having flag DO_NOT_SAVE means that childOid exists in database already
                if (childObjectMap.containsKey(DO_NOT_SAVE)) {
                    // Do not recurse down into child object, simply save association to it.
                    // This also means we have DataObject already in database, so retrieve it.
                    childObject = SessionManager.getSession().retrieve(childOid);
                    recurse = false;
                } else {
                    // The DataObject must be created anew, together with corresponding ACSObject
                    childObject = SessionManager.getSession().create(type);
                    childAcs = createACSObject(childObject, childObjectMap);
                    getObjectMapper().setObject(childOid, childAcs);
                }
                Property prop = dataObject.getObjectType().getProperty(propName);
                if (prop.isCollection()) {
                    DataAssociation assoc = (DataAssociation) dataObject.get(
                            propName);
                    assoc.add(childObject);
                } else {
                    dataObject.set(propName, childObject);
                }
                if (recurse) {
                    setAllProperties(childAcs, childObject, childObjectMap, recursionLevel
                                                                            + 1);
                    m_recursionLevel = recursionLevel;
                }
            } else if (pos == -1) { // We're dealing with simple property
                dataObject.set(key, objectMap.get(key));
                debugLog("Simple property: " + key + ": " + objectMap.get(key));
            }
        }
        finalizeACSObject(acs, dataObject, objectMap);
    }

    /**
     *  This methods guesses required properties not present in the import file.
     */
    private void finalizeACSObject(ACSObject acs, DataObject dobj, Map objMap) {

        debugLog("finalizing ACS: " + acs.getClass() + ": " + acs);
        if (acs instanceof BinaryAsset
            && dobj.get(BinaryAsset.MIME_TYPE) == null) {
            // First get default
            String mimeString = "application/octet-stream";
            String filename = (String) dobj.get(NAME);
            debugLog("Guessing mime type for file: " + filename);
            if (filename != null) {
                MimeType mime = MimeType.guessMimeTypeFromFile(filename);
                if (mime != null) {
                    mimeString = mime.getMimeType();
                }
            }
            debugLog("Guessed mime type: " + mimeString);
            // We have MimeType domain object, but we actually need data object:
            OID mimeOid = new OID(MimeType.BASE_DATA_OBJECT_TYPE);
            mimeOid.set(MimeType.MIME_TYPE, mimeString);
            DataObject mimeObj = SessionManager.getSession().retrieve(mimeOid);
            debugLog("Guessing mimetype from filename: " + filename + ", mime="
                     + mimeOid);
            dobj.set(BinaryAsset.MIME_TYPE, mimeObj);
        }
        if (dobj.getObjectType().getProperty(DISPLAY_NAME) != null
            && dobj.get(DISPLAY_NAME) == null) {
            dobj.set(DISPLAY_NAME, dobj.getObjectType().getQualifiedName()
                                   + " " + dobj.getOID().get("id").toString());
            debugLog("finalizing DISPLAY_NAME: " + dobj.get(DISPLAY_NAME));
        }
        if (dobj.getObjectType().getProperty(NAME) != null
            && dobj.get(NAME) == null) {
            dobj.set(NAME, dobj.getObjectType().getName()
                           + "-" + dobj.getOID().get("id").toString());
            debugLog("finalizing NAME: " + dobj.get(NAME));
        }
    }

    /**
     *  Create new Map holding the peristence attributes for the object with
     * passed source OID.  For "global" (MimeType) and "semi-global" (ContentType)
     * object types, activate the appropriate flag (an entry with key==DO_NOT_SAVE)
     * to indicate that the returned Map won't later be used for creating new
     * ACSObject.
     * <p>
     *  In addition to that, the RemoteOidMapping facility is used to determine
     * whether the object represented by its source OID has already been imported
     * in destination database.  If that is the case, the destination OID is being
     * retrieved and stored in the returned Map, and DO_NOT_SAVE flag set to TRUE.
     */
    private Map getDataObject(OID srcOid) {
        String type = srcOid.getObjectType().getQualifiedName();
        Map newDataObject = new HashMap();
        newDataObject.put(OID_ATTR, srcOid);
        // For MimeType, OIDs are "global"
        // which means we just to make sure that OID exists in database.
        if (Arrays.asList(GLOBAL_OID_TYPES).contains(type)) {
            DataObject dobj = SessionManager.getSession().retrieve(srcOid);
            if (dobj == null) {
                // TODO: better error handling.
                s_log.error("Global OID not found in db: " + srcOid);
                return null;
            }
            debugLog("Data Object retrieved from db: " + srcOid);
            newDataObject.put(DO_NOT_SAVE, Boolean.TRUE);
            return newDataObject;
        }
        // For content type objects, we will only create a placeholder.
        // setProperty() will later ignore any operation on ContentType data object.
        if (ContentType.BASE_DATA_OBJECT_TYPE.equals(type)) {
            newDataObject.put(DO_NOT_SAVE, Boolean.TRUE);
            return newDataObject;
        }
        // For all other objects, check first whether we have this object
        // already in database (it might have already been imported).
        DomainObject destination = getObjectMapper().getObject(srcOid);
        debugLog("finding mapped destination oid: " + destination);
        if (destination != null) {
            // Replace the (source) OID of this object with its real (destination) OID
            // in the target database.  This object won't be saved later.
            newDataObject.put(OID_ATTR, destination.getOID());
            newDataObject.put(DO_NOT_SAVE, Boolean.TRUE);
        }
        return newDataObject;
    }

    private ACSObject createACSObject(DataObject dobj, Map properties) {
        String domainClass = (String) properties.get(DEFAULT_DOMAIN_CLASS);
        if (domainClass == null) {
            // Default to persistence ObjectType -- possibly dangerous.
            domainClass = dobj.getOID().getObjectType().getQualifiedName();
        }
        debugLog("Creating " + domainClass + " ...");
        try {
            Class classDef = Class.forName(domainClass);
            Constructor constructor = classDef.getConstructor(new Class[]{
                        DataObject.class});
            ACSObject object = (ACSObject) constructor.newInstance(new Object[]{
                        dobj});
            debugLog("Successfully created " + object.getOID());
            return object;
        } catch (InvocationTargetException e) {
            throw new UncheckedWrapperException(e);
        } catch (InstantiationException e) {
            throw new UncheckedWrapperException(e);
        } catch (IllegalAccessException e) {
            throw new UncheckedWrapperException(e);
        } catch (ClassNotFoundException e) {
            throw new UncheckedWrapperException(e);
        } catch (Exception e) {
            throw new UncheckedWrapperException(e);
        }
    }

    private void setProperty(Map dobj, Property prop, String value) {
        String key = prop.getName();
        Class propertyClass = prop.getJavaClass();
        if (s_ignoredProps.contains(key)) {
            debugLog("Ignoring property: " + key);
            return;
        }
        if (propertyClass.equals(String.class)) {
            dobj.put(key, value);
        } else if (propertyClass.equals(Date.class)) {
            ParsePosition pos = new ParsePosition(0);
            Date date = s_formatter.parse(value.trim(), pos);
            dobj.put(key, date);
        } else if (propertyClass.equals(Integer.class)) {
            dobj.put(key, Integer.valueOf(value));
        } else if (propertyClass.equals(BigDecimal.class)) {
            dobj.put(key, new BigDecimal(value));
        } else if (propertyClass.equals(Boolean.class)) {
            dobj.put(key, Boolean.valueOf(value));
        } else if (propertyClass.equals(s_byteArray.getClass())) {
            // The OID stored in Map is source OID for objects that have
            // not already been imported.  For others, we should never
            // be at this point anyway!
            OID srcOid = (OID) dobj.get(OID_ATTR);
            String filename = srcOid.get("id").toString() + "-" + key + ".raw";
            // Override filename generation pattern if filename is
            // explicitly provided as attr of <content> element.
            Map atts = (Map) m_attributeStack.peek();
            if (atts != null && atts.get(FILE) != null) {
                filename = (String) atts.get(FILE);
                // We will also use this attribute to populate "name" property
                // (needed for mime-type guessing if none explicitly provided),
                // but only if such has not been set so far.  If this property
                // has been explicitly provided in XML file, the value from
                // XML file will take over.
                if (!dobj.containsKey(NAME)) {
                    dobj.put(NAME, filename);
                }
            }
            if (atts != null && atts.get(ENCODING_ATTR) != null) {
                String encoding = (String) atts.get(ENCODING_ATTR);
                if (ENCODING_BASE64.equals(encoding)) {
                    BASE64Decoder decoder = new BASE64Decoder();
                    try {
                        byte[] content = decoder.decodeBuffer(value);
                        dobj.put(key, content);
                        debugLog("Successfully loaded base64 encoded BLOB"
                                 + ", length: " + content.length + " bytes");
                        return;
                    } catch (IOException ioe) {
                        throw new UncheckedWrapperException(ioe);
                    }
                } else {
                    s_log.error("Don't know how to handle encoding: " + encoding);
                    return;
                }
            }

            File lobFile = new File(getLobDirectory(), filename);
            FileInputStream in = null;
            try {
                in = new FileInputStream(lobFile);
                ByteArrayOutputStream os = new ByteArrayOutputStream();

                byte[] buffer = new byte[1024];
                int length = -1;
                try {
                    while ((length = in.read(buffer)) != -1) {
                        os.write(buffer, 0, length);
                    }
                } catch (IOException ioe) {
                    throw new UncheckedWrapperException(ioe);
                }
                byte[] content = os.toByteArray();
                dobj.put(key, content);
                debugLog("Successfully loaded file " + lobFile
                         + ", length: " + content.length + " bytes");
            } catch (FileNotFoundException e) {
                s_log.error("Lob file: " + lobFile + " does not exist!", e);
            } finally {
                try {
                    in.close();
                } catch (IOException e) {
                    s_log.warn("Failed to close " + lobFile, e);
                }
            }
        } else {
            s_log.warn("Don't know how to handle property " + key + ", type: " + propertyClass.
                    getName());
        }
    }

    private void debugLog(String logString) {
        s_log.debug(getPadding() + logString);
    }

    /**
     *  To ease debugging, create a string padded with whitespace, with length being
     * multiple of current association traversal level.
     */
    private String getPadding() {
        int level = m_recursionLevel;
        if (level == 0) {
            level = m_objectStack.size();
        }
        String padString = "   ";
        // I can't believe this has to be that lame
        StringBuffer padding = new StringBuffer(level * padString.length());
        for (int i = 0; i < level; i++) {
            padding.append(padString);
        }
        return padding.toString();
    }

    protected Map copyAttributes(Attributes atts) {
        Map map = new HashMap();

        for (int i = 0; i < atts.getLength(); i++) {
            map.put(atts.getQName(i), atts.getValue(i));
        }
        return map;
    }
}
