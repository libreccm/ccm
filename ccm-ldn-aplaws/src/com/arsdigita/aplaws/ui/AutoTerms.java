package com.arsdigita.aplaws.ui;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.log4j.Logger;

import com.arsdigita.aplaws.AutoCategorisation;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleComponent;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.london.terms.Term;
import com.arsdigita.london.terms.ui.TermWidget;
import com.arsdigita.persistence.OID;
import com.arsdigita.xml.Element;

/**
 * 
 * @author pb
 */
public class AutoTerms extends SimpleComponent {
    
    private static final Logger LOG = Logger.getLogger(AutoTerms.class);
    BigDecimalParameter itemIDparam = new BigDecimalParameter("itemID");

    /**
     * 
     * @param state
     * @param p
     */
    @Override
    public void generateXML(PageState state, Element p) {
        try {
            BigDecimal itemID = (BigDecimal) state.getValue(itemIDparam);
            ContentItem item = (ContentItem) DomainObjectFactory
            .newInstance(new OID(ContentItem.BASE_DATA_OBJECT_TYPE,
                    itemID));

            Collection terms = AutoCategorisation.getAutoTerms(item);
            Set selected = new HashSet();
            BigDecimal sortKey = BigDecimal.valueOf(0);
            final BigDecimal ONE = BigDecimal.valueOf(1);
            for (Iterator i=terms.iterator(); i.hasNext(); ) {
                Term term = (Term) i.next();
                TermWidget.generateTerm(p, term, selected, sortKey);
                sortKey.add(ONE);
            }
        } catch (Exception e) {
            LOG.info(e);
        }
    }
    
    /**
     * 
     * @param p
     */
    @Override
    public void register(Page p) {
        super.register(p);
        p.addGlobalStateParam(itemIDparam);
    }

}
