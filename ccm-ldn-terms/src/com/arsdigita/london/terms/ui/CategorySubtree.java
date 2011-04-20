package com.arsdigita.london.terms.ui;

import com.arsdigita.bebop.FormData;
import java.math.BigDecimal;
// import java.util.Iterator;
// import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleComponent;
import com.arsdigita.bebop.parameters.ParameterData;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.categorization.Category;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.persistence.OID;
import com.arsdigita.util.Assert;
import com.arsdigita.xml.Element;
import java.util.Arrays;
import java.util.HashSet;
import java.util.StringTokenizer;
import javax.servlet.http.HttpSession;

/**
 * Generate part of the category tree. Used by Assign Category authoring step.
 *
 * Class is directlyx used by JSP page(s), eg. load-cat.jsp
 * (currently in ~/packages/content-section/www/admin, source in ccm-ldn-aplaws
 * or corresponding integration module).
 * 
 * @author Alan Pevec
 */
public class CategorySubtree extends SimpleComponent {

    StringParameter selectedCatsparam = new StringParameter("selectedCats");
    StringParameter nodeIDparam = new StringParameter("nodeID");
    private static Logger s_log = Logger.getLogger(CategorySubtree.class);

    /**
     *
     * @param p
     */
    @Override
    public void register(Page p) {
        super.register(p);
        p.addGlobalStateParam(nodeIDparam);
        p.addGlobalStateParam(selectedCatsparam);
    }

    /**
     * 
     * @param state
     * @param p
     */
    @Override
    public void generateXML(PageState state, Element p) {

        String node = (String) state.getValue(nodeIDparam);
        HashSet ids = new HashSet();
        if (((String) state.getValue(selectedCatsparam)) != null) {
            StringTokenizer values = new StringTokenizer((String) state.getValue(selectedCatsparam), ",");
            while(values.hasMoreTokens()) {
                ids.add(new BigDecimal(values.nextToken().trim()));
            }
        }

        s_log.debug("selected node = " + node);
        String[] pathElements = StringUtils.split(node, "-");

        Category root = (Category) DomainObjectFactory.newInstance(new OID(
                Category.BASE_DATA_OBJECT_TYPE,
                new BigDecimal(pathElements[pathElements.length - 1])));
        s_log.debug("generating subtree for cat " + root.getID());
        TermWidget.generateSubtree(p, root, ids);
    }

}
