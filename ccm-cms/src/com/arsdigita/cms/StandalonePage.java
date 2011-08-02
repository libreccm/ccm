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
package com.arsdigita.cms;

import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.mimetypes.MimeType;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.util.Assert;
import org.apache.log4j.Logger;

import java.math.BigDecimal;

/**
 * Represents a top-level page on the site. The page will usually represent
 * a piece of JSP code, represented as an internal, one-off template.
 *
 * @author Stanislav Freidin (sfreidin@arsdigita.com)
 * @version $Id: StandalonePage.java 2090 2010-04-17 08:04:14Z pboy $
 */
public class StandalonePage extends ContentPage {

    public static final String BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.cms.StandalonePage";

    public static final String TEMPLATE = "template";

    private static Logger s_log = Logger.getLogger(StandalonePage.class);
    /**
     * Default constructor. This creates a new content item.
     */
    public StandalonePage()  {
        this(BASE_DATA_OBJECT_TYPE);
    }

    /**
     * Constructor. The contained <code>DataObject</code> is retrieved
     * from the persistent storage mechanism with an <code>OID</code>
     * specified by <i>oid</i>.
     *
     * @param oid The <code>OID</code> for the retrieved
     * <code>DataObject</code>.
     **/
    public StandalonePage(OID oid) throws DataObjectNotFoundException {
        super(oid);
    }

    /**
     * Load a StandalonePage with the given ID
     *
     * @param id The <code>id</code> for the retrieved
     * <code>DataObject</code>.
     */
    public StandalonePage(BigDecimal id) throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    /**
     * Load a StandalonePage by encapsulating the given data object
     */
    public StandalonePage(DataObject obj)  {
        super(obj);
    }

    /**
     * Create a new StandalonePage with the given type
     */
    public StandalonePage(String type) {
        super(type);
    }

    /**
     * Return the internal template used by this page. This method
     * will be called by the authoring kit UI.
     */
    public final Template getTemplate() {
        return (Template) DomainObjectFactory.newInstance
            ((DataObject) get(TEMPLATE));
    }

    /**
     * Set the internal template used by this page. This method
     * will be called by the authoring kit UI.
     */
    public final void setTemplate(Template t) {
        setAssociation(TEMPLATE, t);
    }

    /**
     * Return the body of this page; the body will probably
     * contain some JSP code
     */
    public final String getBody() {
        Template t = getTemplate();
        if(t == null) return null;

        String text = t.getText();
        return text;
    }

    /**
     * Return the body of this page; the body will probably
     * contain some JSP code.
     *
     * @param text the text for the body
     * @param mime the mime type for the body
     */
    public final void setBody(String text, MimeType mime) {
        Template t = getTemplate();
        boolean isNew = false;

        if(t == null) {
            isNew = true;
            t = new Template();
            t.setName(getName() + "_body");
            t.setMimeType(mime);
            t.save();
        }


        t.setText(text);

        if(isNew) {
            save();
            t.setParent(this);
            t.save();
        }
    }

    /**
     * Return the JSP body of this page
     *
     * @param text the text for the body
     */
    public final void setBody(String text) {
        setBody(text, MimeType.loadMimeType(Template.JSP_MIME_TYPE));
    }

    /**
     * Publish/unpublish this page and its associated template
     */
    public void setLive(ContentItem version) {
        Template t = getTemplate();
        Assert.exists(t);
        if(version != null)
            t.createLiveVersion();
        super.setLive(version);
    }

    /**
     * Save: associate the built-in template with this page in the
     * public context. Thus, all requests to this page will return
     * the built-in template.
     */
    protected void afterSave() {
        super.afterSave();

        TemplateManager m = TemplateManagerFactory.getInstance();
        Template oldTemplate =
            m.getTemplate(this, TemplateManager.PUBLIC_CONTEXT);
        Template newTemplate = getTemplate();

        if(! newTemplate.equals(oldTemplate))
            m.addTemplate(this, newTemplate, TemplateManager.PUBLIC_CONTEXT);
    }

}
