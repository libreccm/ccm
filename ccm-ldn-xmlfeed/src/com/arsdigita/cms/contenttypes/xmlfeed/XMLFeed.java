/* This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * Copyright (C) 2003 Runtime Collective
 */

package com.arsdigita.cms.contenttypes.xmlfeed;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.PageState;
import com.arsdigita.xml.Element;

import com.arsdigita.cms.contenttypes.xmlfeed.listener.RetrieveListener;

import com.arsdigita.cms.FileAsset;
import com.arsdigita.cms.formbuilder.FormItem;

import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.domain.DataObjectNotFoundException;

import com.arsdigita.formbuilder.PersistentComponent;
import com.arsdigita.formbuilder.PersistentForm;
import com.arsdigita.formbuilder.PersistentSubmit;
import com.arsdigita.formbuilder.PersistentWidget;

import com.arsdigita.persistence.DataAssociationCursor;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;


import java.math.BigDecimal;

import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import org.apache.log4j.Logger;

/**
 * <p>
 * This domain object represents the XML feed content type in the system. The
 * XML feed content type allows external XML feeds to act as data sources in
 * the APLAWS system.
 * </p>
 *
 * <p>
 * The feeds also allow a query string to be formed to customize the XML feed
 * at the external server's end. e.g. upmystreet.com returns information
 * about businesses and services based upon a postal code. To achieve this
 * functionality you would create a form that contains:
 * </p>
 *
 * <ul>
 *   <li>postal code
 *   <li>type
 *   <li>cobrand id
 *   <li>view
 * </ul>
 *
 * <p>
 * In the case of upmystreet.com you would also have to specify the particular
 * url for that type of information, e.g. school related data needs to go
 * through http://www.upmystreet.com/xml/education.php3
 * </p>
 *
 * <p>
 * For installation instructions, please the documentation for
 * {@link XMLFeedInitializer XMLFeedInitializer}.
 * </p>
 *
 * @author <a href="mailto:miles@runtime-collective.com">Miles Barr</a>
 * @version $Id: XMLFeed.java 755 2005-09-02 13:42:47Z sskracic $
 * @since 26-02-2003
 */
public final class XMLFeed extends FormItem {

    /** The logging object for this class. */
    private static Logger s_log = Logger.getLogger(XMLFeed.class);

    // ===== Constants ======================================================= //
    public static final String versionID = "$Id: XMLFeed.java 755 2005-09-02 13:42:47Z sskracic $";

    /**
     * The fully qualified model name of the underlying data object, which in
     * this case is the same as the Java type.
     */
    public static final String BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.cms.contenttypes.xmlfeed.XMLFeed";

    /** The name of the url attribute. **/
    public static final String URL = "url";

    /** The name of the xsl file attribute. */
    public static final String XSL_FILE = "xslFile";


    // ===== Constructors ==================================================== //

    /** Default constructor. This creates a new XML feed. */
    public XMLFeed() {
        this(BASE_DATA_OBJECT_TYPE);
    }


    /**
     * Load a specific XMLFeed object from it's id.
     *
     * @param id The id of the data object.
     * @throws DataObjectNotFoundException if there is no data object of type
     *         XMLFeed with that id.
     */
    public XMLFeed(BigDecimal id) throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }


    /**
     * Load a specific XMLFeed object from it's id.
     *
     * @param oid The id of the data object.
     * @throws DataObjectNotFoundException if there is no data object of type
     *         XMLFeed with that id.
     */
    public XMLFeed(OID oid) throws DataObjectNotFoundException {
        super(oid);
    }


    /**
     * Construct a new XML feed object based upon the supplied
     * <code>DataObject</code>.
     *
     * @param xmlFeedDataObject The <code>DataObject</code> to use for this XML
     *                          feed.
     */
    public XMLFeed(DataObject xmlFeedDataObject) {
        super(xmlFeedDataObject);
    }


    /**
     * Creates a new <code>XMLFeed</code> instance based up the supplied type
     * name.
     *
     * @param typeName The type name
     */
    public XMLFeed(String typeName) {
        super(typeName);
    }


    // ===== Data Access Method ============================================== //

    /**
     * Gets the url of the XML feed for this content item.
     *
     * @return The url as a <code>String</code>.
     */
    public String getURL() {
        return (String) get(URL);
    }


    /**
     * Set the url of the XML feed.
     *
     * @param url A valid url that points to a valid feed.
     */
    public void setURL(String url) {
        set(URL, url);
    }

    /**
     * Get the file asset that represents the XSL file that can transform this
     * feed into HTML.
     *
     * @return The current <code>FileAsset</code>. It will return
     *         <code>null</code> if none currently exists.
     */
    public FileAsset getXSLFile() {
        DataObject dobj = (DataObject) get(XSL_FILE);
        if (null == dobj) {
            return null;
        }
        
        return (FileAsset)DomainObjectFactory.newInstance(dobj);
    }


    /**
     * Set the XSL file for this XML feed.
     *
     * @param xsl The <code>FileAsset</code> object containing the XSL file.
     */
    public void setXSLFile(FileAsset xsl) {
        setAssociation(XSL_FILE, xsl);
    }


    /**
     * Returns a collection of all the user defined fields in this form except
     * for the submit button. It is useful when you want to determine which
     * fields in the <code>FormData</code> object were specified by the user.
     *
     * @throws UncheckedWrapperException If there is not form associated with
     *   this XMLFeed object.
     */
    public Iterator getFormFieldNames() {
        Collection names = new Vector();

        DataAssociationCursor allFields = getForm().getComponents();

        while (allFields.next()) {
            PersistentComponent c = (PersistentComponent) DomainObjectFactory.newInstance(allFields.getDataObject());
            if (c instanceof PersistentWidget && !(c instanceof PersistentSubmit)) {
                names.add(((PersistentWidget) c).getParameterName());
            }
        }

        return names.iterator();
    }

    
    protected Form instantiateForm(PersistentForm pform,
                                   boolean readOnly) {
        Form form = super.instantiateForm(pform, readOnly);
        
        if (!readOnly) {
            form.addProcessListener
                (new RetrieveListener(this));
        }
        
        return form;
    }

    protected void generateXMLBody(PageState state,
                                   Element parent,
                                   Component c) {
        super.generateXMLBody(state, parent, c);
        
        String data = (String)state.getRequest()
            .getAttribute(RetrieveListener.REQUEST_RESULTS_KEY);
        
        Element label = parent.newChildElement(
            "bebop:label", Component.BEBOP_XML_NS);
        label.addAttribute("escape", "yes");
        
        label.setText(data);
    }
}
