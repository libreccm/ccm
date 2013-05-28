package com.arsdigita.cms;

import com.arsdigita.caching.CacheTable;
import com.arsdigita.persistence.OID;
import com.arsdigita.xml.Element;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * A cache for generated XML. This class is a singleton. Use {@link #getInstance()} to obtain the only instance of this
 * class.
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public final class XMLDeliveryCache {

    private static final String DEFAULT_CONTEXT = "{{DEFAULT_CONTEXT}}";
    private static final String LIST_MODE_CONTEXT = "{{LIST_MODE_CONTEXT}}";
    private static final XMLDeliveryCache INSTANCE = new XMLDeliveryCache();
    /**
     * The real cache.
     */
    private CacheTable cache = new CacheTable(XMLDeliveryCache.class.getName(),
                                              CMSConfig.getInstance().getXmlCacheSize(),
                                              CMSConfig.getInstance().getXmlCacheAge(),
                                              true);
    /**
     * Maps from the OID of the master version of an item in the cache to the OID of the item in the cache.
     */
    private Map<String, String> cachedItems = new HashMap<String, String>();

    /**
     * Private constructor
     */
    private XMLDeliveryCache() {
        //Nothing here at the moment
    }

    /**
     * Retrieves the instance of the XMLDeliveryCache
     *
     * @return
     */
    public static XMLDeliveryCache getInstance() {
        return INSTANCE;
    }

    /**
     * Checks if the XML representation of an item is cached.
     *
     * @param oid The OID of the item.
     * @param context The context of the XML output.
     * @param listMode Is the XML cached for list mode or normal mode.
     * @return
     */
    public boolean isCached(final OID oid, final String context, final boolean listMode) {
        final CachedItem cachedItem = (CachedItem) cache.get(oid.toString());
        if (cachedItem == null) {
            return false;
        }

        final CachedXml cachedXml = cachedItem.get(context);
        if (cachedXml == null) {
            return false;
        }

        if (listMode) {
            return (cachedXml.getCachedXml(Mode.LIST_MODE) != null);
        } else {
            return (cachedXml.getCachedXml(Mode.DEFAULT) != null);
        }
    }

    /**
     * Convenient method for calling {@link #isCached(OID, java.lang.String, boolean)} with {@code listMode = false}.
     *
     * @param oid
     * @param context
     * @return
     */
    public boolean isCached(final OID oid, final String context) {
        return isCached(oid, context, false);
    }

    /**
     * Convenient method for calling {@link #isCached(OID, java.lang.String, boolean)} with {@code context = ""} and
     * {@code listMode = false}.
     *
     * @param oid
     * @return
     */
    public boolean isCached(final OID oid) {
        return isCached(oid, "", false);
    }

    /**
     * Retrieves the cached XML from the cache and copies the cached XML to the provided parent element. Important:
     * Before calling this method check if the XML for the item is cached using
     * {@link #isCached(OID, java.lang.String, boolean)}. If this method is called for an item not in the cache this
     * method will throw an {@link IllegalArgumentException}!
     *
     * @param parent The element to which the cached XML is copied.
     * @param oid The OID of the item to retrieve.
     * @param context The context of the cached XML.
     * @param listMode Is the cached XML for the list mode or the normal mode.
     *   
     */
    public void retrieveFromCache(final Element parent,
                                  final OID oid,
                                  final String context,
                                  final boolean listMode) {
        final CachedItem cachedItem = (CachedItem) cache.get(oid.toString());
        if (cachedItem == null) {
            throw new IllegalArgumentException(String.format("The item with the OID '%s' is not cached.",
                                                             oid.toString()));
        }

        final CachedXml cachedXml = cachedItem.get(context);
        if (cachedXml == null) {
            throw new IllegalArgumentException(String.
                    format(
                    "The item with the OID '%s' is not cached for context '%s'",
                    oid.toString(),
                    context));
        }

        //Element cacheElem;
        String cached;
        if (listMode) {
            //cacheElem = cachedXml.getCachedXml(Mode.LIST_MODE);
            cached = cachedXml.getCachedXml(Mode.LIST_MODE);
        } else {
            //cacheElem = cachedXml.getCachedXml(Mode.DEFAULT);
            cached = cachedXml.getCachedXml(Mode.LIST_MODE);
        }

        //if (cacheElem == null) {
        if (cached == null) {
            if (listMode) {
                throw new IllegalArgumentException(String.format(
                        "The item with the OID '%s' is not cached for context '%s' in list mode",
                        oid.toString(),
                        context));
            } else {
                if (listMode) {
                    throw new IllegalArgumentException(String.format(
                            "The item with the OID '%s' is not cached for context '%s'.",
                            oid.toString(),
                            context));
                }
            }
        }

        //cacheElem.syncDocs();

        Map<String, String> cachedAttributes;
        if (listMode) {
            cachedAttributes = cachedXml.getCachedAttributes(Mode.LIST_MODE);
        } else {
            cachedAttributes = cachedXml.getCachedAttributes(Mode.DEFAULT);
        }

        if (cachedAttributes != null) {
            for (Map.Entry<String, String> attribute : cachedAttributes.entrySet()) {
                parent.addAttribute(attribute.getKey(), attribute.getValue());
            }
        }

        parent.setText(cached);

//        final Iterator<Map.Entry<String, String>> attrs = cacheElem.getAttributes().entrySet().iterator();
//        Map.Entry<String, String> attr;
//        while (attrs.hasNext()) {
//            attr = attrs.next();
//            parent.addAttribute(attr.getKey(), attr.getValue());
//        }
//        final Iterator<Element> childs = cacheElem.getChildren().iterator();
//        while (childs.hasNext()) {
//            copyElement(parent, childs.next());
//        }
    }

    /**
     * Convenient method for calling
     * {@link #retrieveFromCache(com.arsdigita.xml.Element, OID, java.lang.String, boolean)} with
     * {@code listMode = false}.
     *
     * @param parent
     * @param oid
     * @param context
     *
     * @see #retrieveFromCache(Element, OID, String, boolean)
     */
    public void retrieveFromCache(final Element parent, final OID oid, final String context) {
        retrieveFromCache(parent, oid, context, false);
    }

    /**
     * Convenient method for calling
     * {@link #retrieveFromCache(com.arsdigita.xml.Element, OID, java.lang.String, boolean)} with {@code context = ""}
     * and {@code listMode = false}.
     *
     * @param parent
     * @param oid
     *
     * @see #retrieveFromCache(Element, OID, String, boolean)
     */
    public void retrieveFromCache(final Element parent, final OID oid) {
        retrieveFromCache(parent, oid, "", false);
    }

    /**
     * Cache the XML output of an item for the given context and list mode.
     *
     * @param oid The OID of the item to cache.
     * @param item The item to cache.
     * @param parent The root element of the items XML output.
     * @param context The context of the XML output.
     * @param listMode If the XML is for the list mode.
     */
    public void cache(final OID oid,
                      final ContentItem item,
                      final Element parent,
                      final String context,
                      final boolean listMode) {
        CachedItem cachedItem = (CachedItem) cache.get(oid.toString());
        if (cachedItem == null) {
            cachedItem = new CachedItem();
            cache.put(oid.toString(), cachedItem);
        }

        CachedXml cachedXml = cachedItem.get(context);
        if (cachedXml == null) {
            cachedXml = new CachedXml();
            cachedItem.put(context, cachedXml);
        }

//        final Element cacheElem = new Element("cachedItem");
//        final Iterator<Map.Entry<String, String>> attrs = parent.getAttributes().entrySet().iterator();
//        Map.Entry<String, String> attr;
//        while (attrs.hasNext()) {
//            attr = attrs.next();
//            cacheElem.addAttribute(attr.getKey(), attr.getValue());
//        }
//        final Iterator<Element> childs = parent.getChildren().iterator();
//        while (childs.hasNext()) {
//            copyElement(cacheElem, childs.next());
//        }

        final Mode mode;
        if (listMode) {
            mode = Mode.LIST_MODE;
        } else {
            mode = Mode.DEFAULT;
        }

        final Iterator<Map.Entry<String, String>> attrs = parent.getAttributes().entrySet().iterator();
        Map.Entry<String, String> attr;
        final Map<String, String> attributes = new HashMap<String, String>();
        while (attrs.hasNext()) {
            attr = attrs.next();
            attributes.put(attr.getKey(), attr.getValue());
        }

        cachedXml.putCachedXml(mode, parent.toString());
        cachedXml.pubCachedAttributes(mode, attributes);

        if (item.getDraftVersion() != null) {
            cachedItems.put(item.getDraftVersion().getOID().toString(), oid.toString());
        }

        if ((item instanceof ContentPage) && (((ContentPage) item).getContentBundle() != null)) {
            cachedItems.put(((ContentPage) item).getContentBundle().getOID().toString(), oid.toString());
        }
    }

    /**
     * Removes an object from the cache.
     *
     * @param oid The OID of the item to remove from the cache or the master id of the item to remove.
     */
    public void removeFromCache(final OID oid) {
        cache.remove(oid.toString());

        final String cachedId = cachedItems.get(oid.toString());
        if (cachedId != null) {
            cache.remove(cachedId);
            cachedItems.remove(oid.toString());
        }
    }

//    private void copyElement(final Element parent, final Element element) {
//        final Element copy = parent.newChildElement(element.getName());
//        final Iterator attrs = element.getAttributes().entrySet().iterator();
//        Map.Entry attr;
//        while (attrs.hasNext()) {
//            attr = (Map.Entry) attrs.next();
//            copy.addAttribute((String) attr.getKey(), (String) attr.getValue());
//        }
//
//        final Iterator childs = element.getChildren().iterator();
//        while (childs.hasNext()) {
//            copyElement(copy, (Element) childs.next());
//        }
//
//        if (element.getText() != null) {
//            copy.setText(element.getText());
//        }
//
//        if (element.getCDATASection() != null) {
//            copy.setCDATASection(element.getCDATASection());
//        }
//
//    }

    private class CachedItem {

        private final Map<String, CachedXml> cachedXml = new HashMap<String, CachedXml>();

        public CachedItem() {
            //Nothing
        }

        public CachedXml get(final String context) {
            return cachedXml.get(context);
        }

        public void put(final String context, final CachedXml cachedXml) {
            this.cachedXml.put(context, cachedXml);
        }

    }

    private class CachedXml {

        //private final Map<Mode, Element> cachedXml = new EnumMap<Mode, Element>(Mode.class);
        private final Map<Mode, String> cachedXml = new EnumMap<Mode, String>(Mode.class);
        private final Map<Mode, Map<String, String>> cachedAttributes = new EnumMap<Mode, Map<String, String>>(
                Mode.class);

        public CachedXml() {
            //Nothing
        }

//        public Element getCachedXml(final Mode mode) {
//            return cachedXml.get(mode);
//        }
        public String getCachedXml(final Mode mode) {
            return cachedXml.get(mode);
        }

        public Map<String, String> getCachedAttributes(final Mode mode) {
            return cachedAttributes.get(mode);
        }

//        public void putCachedXml(final Mode mode, final Element element) {
//            cachedXml.put(mode, element);
//        }
        public void pubCachedAttributes(final Mode mode, final Map<String, String> attributes) {
            cachedAttributes.put(mode, attributes);
        }

        public void putCachedXml(final Mode mode, final String element) {
            cachedXml.put(mode, element);
        }

    }

    private enum Mode {

        DEFAULT,
        LIST_MODE,
    }
}
