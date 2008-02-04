/*
 * Copyright (C) 2001, 2002 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the CCM Public
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of
 * the License at http://www.redhat.com/licenses/ccmpl.html
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */

package com.arsdigita.cms.docmgr.ui;

import java.math.BigDecimal;
import java.util.ArrayList;

import com.arsdigita.bebop.ActionLink;
import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.bebop.SegmentedPanel;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.event.RequestEvent;
import com.arsdigita.bebop.event.RequestListener;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.ContentSectionCollection;
import com.arsdigita.cms.docmgr.DocMgr;
import com.arsdigita.cms.docmgr.Document;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.kernel.User;
import com.arsdigita.kernel.permissions.PermissionDescriptor;
import com.arsdigita.kernel.permissions.PermissionService;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.web.Web;

/**
 * This component shows all the properties of a file with links
 * to administrative actions to change those.
 *
 * @author <mailto href="StefanDeusch@computer.org">Stefan Deusch</a>
 */
class FileInfoPropertiesPane extends SimpleContainer
    implements DMConstants, RequestListener
{

    private ArrayList m_componentList;
    private RequestLocal m_fileData;

    private Component  m_properties;
    private Component  m_upload;
    private Component  m_sendColleague;
    private Component  m_edit;
    private Component  m_action;

    // unfortunately need to declare here so can call
    // PageState related methods
    private FileSendColleaguePane m_sendColleaguePane;
    private FileActionPane m_fileActionPane;
    private ActionLink m_editLink;

    private DocmgrBasePage m_page;

    private ContentSection m_docsContentSection;

    public FileInfoPropertiesPane(DocmgrBasePage p) {
        m_page = p;

        // set component's content section
        ContentSectionCollection csl = ContentSection.getAllSections();
        csl.addEqualsFilter("label",DocMgr.getConfig().getContentSection());
        if (!csl.next()) {
                csl.close(); return;
        }
        m_docsContentSection = csl.getContentSection();
        csl.close();

        m_fileData = new RequestLocal() {
                protected Object initialValue(PageState state) {
                    BigDecimal id = (BigDecimal) 
                        state.getValue(getFileIDParam());
                    Document doc = null;
                    try {
                        doc = new Document(id);
                    } catch(DataObjectNotFoundException nfe) {
                        throw new UncheckedWrapperException(nfe);
                    }
                    return doc;
                }
            };

        SegmentedPanel main = new SegmentedPanel();
        main.setClassAttr("main");

        m_componentList = new ArrayList();

        m_properties = makePropertiesPane(main);
        m_componentList.add(m_properties);

        m_edit = makeEditPane(main);
        m_componentList.add(m_edit);

        m_action = makeActionPane(main);
        m_componentList.add(m_action);

        m_upload = makeUploadPane(main);
        m_componentList.add(m_upload);

        m_sendColleague = makeSendColleaguePane(main);
        m_componentList.add(m_sendColleague);

        add(main);
    }

    private Component makePropertiesPane(SegmentedPanel main) {
        SimpleContainer container= new SimpleContainer();

        container.add(new FilePropertiesPanel(this));
        m_editLink = new ActionLink(new Label(FILE_EDIT_LINK));
        m_editLink.setClassAttr("actionLink");
        m_editLink.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    PageState state = e.getPageState();
                    displayEditForm(state);
                }
            });
        container.add(m_editLink);
        return main.addSegment(FILE_PROPERTIES_HEADER, container);
    }

    private Component makeEditPane(SegmentedPanel main) {
        return main.addSegment(FILE_EDIT_HEADER,
                               new FileEditForm(this));
    }

    private Component makeActionPane(SegmentedPanel main) {
        m_fileActionPane = new FileActionPane(this);
        return  main.addSegment(FILE_ACTION_HEADER,
                                m_fileActionPane);
    }

    private Component makeUploadPane(SegmentedPanel main) {
        return  main.addSegment(FILE_UPLOAD_HEADER,
                                new VersionUploadForm(this));
    }

    private Component makeSendColleaguePane(SegmentedPanel main) {
        m_sendColleaguePane = new FileSendColleaguePane(this) ;
        return main.addSegment(FILE_SEND_COLLEAGUE_HEADER,
                               m_sendColleaguePane);
    }

    public void register(Page p) {
        for (int i = 0; i < m_componentList.size(); i++) {
            p.setVisibleDefault((Component) m_componentList.get(i), false);
        }
        p.setVisibleDefault( m_properties, true);
        p.setVisibleDefault( m_action, true);

        p.addRequestListener(m_fileActionPane);
        p.addRequestListener(this);

        super.register(p);
    }

    /**
     * Visibility of components management methods
     */
    private void hideAll(PageState state) {
        for (int i = 0; i < m_componentList.size(); i++) {
            ((Component) m_componentList.get(i)).setVisible(state, false);
        }
    }

    public void displayPropertiesAndActions(PageState state) {
        m_page.goUnmodal(state);
        hideAll(state);
        m_properties.setVisible(state, true);
        m_action.setVisible(state, true);
    }

    public void displayEditForm(PageState state) {
        m_page.goModal(state, m_edit);
    }

    public void displayUploadForm(PageState state) {
        m_page.goModal(state, m_upload);
    }

     public void displaySendColleagueForm(PageState state) {
         m_page.goModal(state, m_sendColleague);
         m_sendColleaguePane.initState(state);
     }

    public BigDecimalParameter getFileIDParam() {
        return m_page.getFileIDParam();
    }

    public ContentSection getContentSection() {
        return m_docsContentSection;
    }

    /**
     * return Document initialized in RequestLocal
     */
    public Document getDocument(PageState s) {
        return (Document)m_fileData.get(s);
    }

    public void pageRequested(RequestEvent event) {
        PageState state = event.getPageState();
        Document doc = getDocument(state);
        User user = Web.getContext().getUser();

        if(!PermissionService.checkPermission
            (new PermissionDescriptor
             (PrivilegeDescriptor.WRITE, doc, user))) {
            m_editLink.setVisible(state,false);
        }
        doc.assertPrivilege(PrivilegeDescriptor.READ);
    }
}
