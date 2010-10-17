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

package com.arsdigita.docmgr.ui;

import java.util.ArrayList;

import com.arsdigita.bebop.ActionLink;
import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SegmentedPanel;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;

/**
 * This component shows all the properties of a file with links
 * to administrative actions to change those.
 *
 * @author <mailto href="StefanDeusch@computer.org">Stefan Deusch</a>
 */
class FileInfoPropertiesPane extends SimpleContainer
    implements DMConstants
{

    private ArrayList m_componentList;

    private Component  m_properties;
    private Component  m_upload;
    private Component  m_sendColleague;
    private Component  m_edit;
    private Component  m_action;

    private DocmgrBasePage m_page;

    public FileInfoPropertiesPane(DocmgrBasePage p) {
        m_page = p;

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

        container.add(new FilePropertiesPanel());
        ActionLink link = new ActionLink(new Label(FILE_EDIT_LINK));
        link.setClassAttr("actionLink");
        link.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    PageState state = e.getPageState();
                    displayEditForm(state);
                }
            });
        container.add(link);
        return main.addSegment(FILE_PROPERTIES_HEADER, container);
    }

    private Component makeEditPane(SegmentedPanel main) {
        return main.addSegment(FILE_EDIT_HEADER,
                               new FileEditForm(this));
    }

    private Component makeActionPane(SegmentedPanel main) {
        return  main.addSegment(FILE_ACTION_HEADER,
                                new FileActionPane(this));
    }

    private Component makeUploadPane(SegmentedPanel main) {
        return  main.addSegment(FILE_UPLOAD_HEADER,
                                new VersionUploadForm(this));
    }

    private Component makeSendColleaguePane(SegmentedPanel main) {
        return main.addSegment(FILE_SEND_COLLEAGUE_HEADER,
                               new FileSendColleagueForm(this));
    }

    public void register(Page p) {
        for (int i = 0; i < m_componentList.size(); i++) {
            p.setVisibleDefault((Component) m_componentList.get(i), false);
        }
        p.setVisibleDefault( m_properties, true);
        p.setVisibleDefault( m_action, true);

        p.addGlobalStateParam(FILE_ID_PARAM);

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
    }

}
