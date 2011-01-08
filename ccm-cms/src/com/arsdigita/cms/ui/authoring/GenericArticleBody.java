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
package com.arsdigita.cms.ui.authoring;


import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.SingleSelect;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.TextAsset;
import com.arsdigita.cms.contenttypes.GenericArticle;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.util.Assert;


/**
 * Displays the current text body of the article and allows the user
 * to edit it, by uploading a file or entering text in a textbox.
 *
 * The {@link com.arsdigita.bebop.PropertySheet} class is often used
 * as the display component in the default authoring kit steps of
 * this class.
 *
 * @author Stanislav Freidin (sfreidin@arsdigita.com)
 * @version $Id: GenericArticleBody.java 1949 2009-06-25 08:30:50Z terry $
 */
public class GenericArticleBody extends TextAssetBody {

    private AuthoringKitWizard m_parent;
    private ItemSelectionModel m_itemModel;

    /**
     * Construct a new GenericArticleBody component
     *
     * @param itemModel The {@link ItemSelectionModel} which will
     *   be responsible for loading the current item
     *
     * @param parent The parent wizard which contains the form. The form
     *   may use the wizard's methods, such as stepForward and stepBack,
     *   in its process listener.
     */
    public GenericArticleBody(ItemSelectionModel itemModel, AuthoringKitWizard parent) {
        super(new ItemAssetModel(itemModel));
        m_itemModel = itemModel;
        m_parent = parent;

        // Rest the component when it is hidden
        parent.getList().addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    PageState state = e.getPageState();
                    reset(state);
                }
            });

        // Set the right component access on the forms
        Component f = getComponent(FILE_UPLOAD);
        if (f != null) {
        	setComponentAccess(FILE_UPLOAD,
        			new WorkflowLockedComponentAccess(f, itemModel));
        }
        Component t = getComponent(TEXT_ENTRY);
        setComponentAccess(TEXT_ENTRY,
                           new WorkflowLockedComponentAccess(t, itemModel));
    }

    /**
     * Adds the options for the mime type select widget of
     * <code>GenericArticleForm</code> and sets the default mime type.
     **/
    @Override
    protected void setMimeTypeOptions(SingleSelect mimeSelect) {
        mimeSelect.addOption(new Option("text/html", "HTML Text"));
        mimeSelect.setOptionSelected("text/html");
    }

    /**
     * Create a new text asset and associate it with the current item
     *
     * @param s the current page state
     * @return a valid TextAsset
     */
    protected TextAsset createTextAsset(PageState s) {
        GenericArticle item = getGenericArticle(s);
        TextAsset t = new TextAsset();
        t.setName(item.getName() + "_text_" + item.getID());
        // no need - cg. Text doesn't need a security context,
        // and ownership of text is recorded in text_pages
        // t.setParent(item);
        return t;
    }

    /**
     * Set additional parameters of a brand new text asset, such as the
     * parent ID, after the asset has been successfully uploaded
     *
     * @param s the current page state
     * @param a the new <code>TextAsset</code>
     */
    protected void updateTextAsset(PageState s, TextAsset a) {
        GenericArticle t = getGenericArticle(s);
        Assert.exists(t);
        // no need - cg. Text doesn't need a security context,
        // and ownership of text is recorded in text_pages
        
        //  a.setParent(t);
        t.setTextAsset(a);
        a.save();
        t.save();
    }

    /**
     * Get the current GenericArticle
     */
    protected GenericArticle getGenericArticle(PageState s) {
        return (GenericArticle)m_itemModel.getSelectedObject(s);
    }

    /**
     * An ACSObjectSelectionModel that selects the current text asset for
     * the text page
     */
    private static class ItemAssetModel extends ItemSelectionModel {

        private RequestLocal m_asset;

        public ItemAssetModel(ItemSelectionModel m) {
            super(m);

            m_asset = new RequestLocal() {
                @Override
                    protected Object initialValue(PageState s) {
                        GenericArticle t = (GenericArticle)
                            ((ItemSelectionModel)getSingleSelectionModel())
                            .getSelectedObject(s);
                        Assert.exists(t);
                        return t.getTextAsset();
                    }
                };
        }

        @Override
        public Object getSelectedKey(PageState s) {
            TextAsset a = (TextAsset)getSelectedObject(s);
            return (a == null) ? null : a.getID();
        }

        @Override
        public DomainObject getSelectedObject(PageState s) {
            return (DomainObject)m_asset.get(s);
        }

        @Override
        public void setSelectedObject(PageState s, DomainObject o) {
            m_asset.set(s, o);
        }

        @Override
        public void setSelectedKey(PageState s, Object key) {
            throw new UnsupportedOperationException( (String) GlobalizationUtil.globalize("cms.ui.authoring.not_implemented").localize());
        }

        @Override
        public boolean isSelected(PageState s) {
            return (getSelectedObject(s) != null);
        }
    }

}
