/*
 * Copyright (C) 2005 Runtime Collective Ltd. All Rights Reserved.
 */

package com.arsdigita.london.navigation.ui;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.cms.ContentBundle;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ContentSectionConfig;
import com.arsdigita.cms.ExtraXMLGenerator;
import com.arsdigita.dispatcher.DispatcherHelper;
import com.arsdigita.london.navigation.Navigation;
import com.arsdigita.xml.Element;

/**
 * GreetingItemExtraXML generates the extra XML which is not included in the standard
 * cms:item element.
 *
 * @author <a href="mailto:fabrice@runtime-collective.com">Fabrice Retkowsky</a>
 * @version $Id: GreetingItem.java 285 2005-02-22 00:29:02Z sskracic $
 */
public class GreetingItemExtraXML extends AbstractComponent {
    
    private static final Logger s_log = Logger.getLogger(GreetingItemExtraXML.class);
    
    public static final String extraTag = "greetingItemExtraXML";
    
    Map xmlGenerators = new HashMap();
    
    public void register(Page p) {
        super.register(p);
        
        for (Iterator i=ContentSectionConfig.getExtraXMLGeneratorsIterator(); i.hasNext(); ) {
            Map.Entry e = (Map.Entry) i.next();
            String type = (String) e.getKey();
            List genClasses = (List) e.getValue();
            List genInstances = new LinkedList();
            for (Iterator genClassesIterator=genClasses.iterator(); genClassesIterator.hasNext(); ) {
                try {
                    ExtraXMLGenerator gen =(ExtraXMLGenerator) ((Class)genClassesIterator.next()).newInstance();
                    if (s_log.isDebugEnabled()) {
                        s_log.debug("Adding parameters for generator : "+gen);
                    }
                    gen.addGlobalStateParams(p);
                    genInstances.add(gen);
                } catch (Exception exc) {
                    s_log.warn("getting ExtraXMLGenerator failed:", exc);
                }
            }
            xmlGenerators.put(type, genInstances);
        }
    }
    
    public Element generateXML(HttpServletRequest request,
            HttpServletResponse response) {
        
        ContentItem item = getItem();
        
        if (item == null) {
            return null;
        }
        
        List generators = (List) xmlGenerators.get(item.getClass().getName());
        
        if (s_log.isDebugEnabled()) {
            s_log.debug("Item : "+item.getName()+", type : "+item.BASE_DATA_OBJECT_TYPE+", no generators : "+(generators == null ? "null" : ""+generators.size()));
        }
        
        if (generators == null || generators.isEmpty()) {
            return null;
        }
        
        Element content = Navigation.newElement(extraTag);
        PageState state = PageState.getPageState(request);
        for (Iterator i=generators.iterator(); i.hasNext(); ) {
            ExtraXMLGenerator generator = (ExtraXMLGenerator) i.next();
            if (s_log.isDebugEnabled()) {
                s_log.debug("Calling generator : "+generator);
            }
            generator.generateXML(item, content, state);
        }
        
        return content;
    }
    
    public ContentItem getItem() {
        
        ContentItem item = (ContentItem) getObject();
        if (null == item || !item.isLive()) {
            return null;
        }
        
        if (!ContentItem.VERSION.equals(item.getVersion())) {
            item = item.getLiveVersion();
        }
        
        ContentBundle bundle = (ContentBundle) item;
        /*Fix by Quasimodo*/
        /* getPrimaryInstance doesn't negotiate the language of the content item */
        /* ContentItem baseItem = bundle.getPrimaryInstance(); */
        ContentItem baseItem = bundle.negotiate(DispatcherHelper.getRequest().getLocales());
        // If there is no matching language version for this content item
        if(baseItem == null) {
        // get the primary instance instead (fallback)
            baseItem = bundle.getPrimaryInstance();
        }
        
        return baseItem;
    }
}
