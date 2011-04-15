/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.portalserver.ui.admin;

import com.arsdigita.portalserver.util.GlobalizationUtil;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Date;
import java.text.SimpleDateFormat;
import com.arsdigita.portalserver.PortalSite;
import com.arsdigita.portalserver.PortalSiteCollection;
import com.arsdigita.portalserver.PortalPage;
import com.arsdigita.portalserver.personal.PersonalPortal;
import com.arsdigita.portal.Portlet;
import com.arsdigita.bebop.AbstractSingleSelectionModel;
import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.ComponentSelectionModel;
import com.arsdigita.bebop.ControlLink;
import com.arsdigita.bebop.GridPanel;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Link;
import com.arsdigita.bebop.List;
import com.arsdigita.bebop.list.ListModel;
import com.arsdigita.bebop.list.ListModelBuilder;
import com.arsdigita.bebop.list.ListCellRenderer;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.form.RadioGroup;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.SimpleComponent;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.SplitWizard;
import com.arsdigita.bebop.SplitPanel;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.event.ChangeListener;
import com.arsdigita.bebop.event.ChangeEvent;
import com.arsdigita.bebop.event.RequestListener;
import com.arsdigita.bebop.event.RequestEvent;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.portalserver.util.GlobalizationUtil;
import com.arsdigita.kernel.User;

import org.apache.log4j.Category;


/**
 * <b><strong>Experimental</strong></b>
 *
 * @author <a href="mailto:jparsons@arsdigita.com">Jim Parsons</a>
 */

public final class PortalArchivePanel extends SplitPanel {

    List archiveList;
    List onlineList;

    Label m_noPortalSelected;

    ArchiveForm archiveForm;
    UnarchiveForm unarchiveForm;


    static class ArchiveListModel implements ListModel {
        PortalSiteCollection m_psites;
        PortalSite m_currSite;
        public ArchiveListModel() {
            m_psites = PortalSite.retrieveAllPortalSites();
            m_psites.filterForArchived();
        }
        public boolean next() {
            if (!m_psites.next()) {
                return false;
            }
            m_currSite = m_psites.getPortalSite();
            return true;
        }
        public Object getElement() {
            return m_currSite;
        }
        public String getKey() {
            return m_currSite.getID().toString();
        }
    }

    static class OnlineListModel implements ListModel {
        PortalSiteCollection m_psites;
        PortalSite m_currSite;
        public OnlineListModel() {
            m_psites = PortalSite.retrieveAllPortalSites();
            m_psites.filterForUnarchived();
        }
        public boolean next() {
            if (!m_psites.next()) {
                return false;
            }
            m_currSite = m_psites.getPortalSite();
            return true;
        }
        public Object getElement() {
            return m_currSite;
        }
        public String getKey() {
            return m_currSite.getID().toString();
        }
    }
    

    public PortalArchivePanel(Page p) {

        p.addRequestListener(new RequestListener () {
            public void pageRequested(RequestEvent e) {
                PageState ps = e.getPageState();
                if((!onlineList.isSelected(ps)) && 
                    (!archiveList.isSelected(ps))) {
                    //neither list has a selection...
                    archiveForm.setVisible(ps,false);
                    unarchiveForm.setVisible(ps,false);
                    m_noPortalSelected.setVisible(ps, true);
                }
            }
        });
               
        setClassAttr("archiver");
        setDivider(40);
 
        GridPanel headerPanel = new GridPanel(1);
        headerPanel.setClassAttr("archive_header");
        headerPanel.add(new Label(GlobalizationUtil.globalize("cw.workspace.ui.admin.portal_archive_header")));
        setHeader(headerPanel);

        GridPanel portalPanel = new GridPanel(1);
        portalPanel.setClassAttr("archive_panel");
        setLeftComponent(portalPanel);

        GridPanel formPanel = new GridPanel(1);
        setRightComponent(formPanel);

        archiveList = new List( new ListModelBuilder() {
                             public ListModel makeModel(List l, PageState ps) {
                                 return new ArchiveListModel();
                             } 
                             public void lock() {}
                             public boolean isLocked() { return true; }
                           });

        archiveList.addChangeListener(new ChangeListener () {
            public void stateChanged(ChangeEvent e) {
                PageState ps = e.getPageState();
                archiveForm.setVisible(ps,false);
                unarchiveForm.setVisible(ps, true);
                m_noPortalSelected.setVisible(ps, false);
                onlineList.clearSelection(ps);
            }
        });
        archiveList.setCellRenderer(new ArchiveListCellRenderer());
        archiveList.setClassAttr("archivelist");

        Label emptyarchive = new Label(GlobalizationUtil.globalize("cw.workspace.ui.admin.empty_archive"));
        emptyarchive.setFontWeight(Label.ITALIC);
        archiveList.setEmptyView(emptyarchive);

        onlineList = new List( new ListModelBuilder() {
                             public ListModel makeModel(List l, PageState ps) {
                                 return new OnlineListModel();
                             } 
                             public void lock() {}
                             public boolean isLocked() { return true; }
                           });

        onlineList.addChangeListener(new ChangeListener () {
            public void stateChanged(ChangeEvent e) {
                PageState ps = e.getPageState();
                archiveForm.setVisible(ps,true);
                unarchiveForm.setVisible(ps, false);
                m_noPortalSelected.setVisible(ps, false);
                archiveList.clearSelection(ps);
            }
        });
        onlineList.setCellRenderer(new OnlineListCellRenderer());
        onlineList.setClassAttr("onlinelist");

        Label emptyonline = new Label(GlobalizationUtil.globalize("cw.workspace.ui.admin.empty_online"));
        emptyonline.setFontWeight(Label.ITALIC);
        onlineList.setEmptyView(emptyonline);

         Label onlinelabel = 
            new Label(GlobalizationUtil.globalize(
                     "cw.workspace.ui.admin.portals_currently_online"));
         onlinelabel.setFontWeight(Label.BOLD);
         portalPanel.add(onlinelabel);
         portalPanel.add(onlineList);


        Label archivelabel = 
             new Label(GlobalizationUtil.globalize(
                      "cw.workspace.ui.admin.portals_currently_archived"));
         archivelabel.setFontWeight(Label.BOLD);
         portalPanel.add(archivelabel);
         portalPanel.add(archiveList);

         archiveForm = new ArchiveForm();
         formPanel.add(archiveForm);


         unarchiveForm = new UnarchiveForm();
         formPanel.add(unarchiveForm);

         //set Default message in right pane when nothing selected
         m_noPortalSelected = new Label(GlobalizationUtil.globalize("cw.workspace.ui.admin.no_portal_selected"));
         m_noPortalSelected.setFontWeight(Label.ITALIC);
         formPanel.add(m_noPortalSelected);
        
    }

    public class ArchiveForm extends Form implements FormProcessListener {
        private Label instruction;
        private Submit button;
        private Submit cancelbutton;
        private RadioGroup radio;
        private String recurse;

        public ArchiveForm() {
            super("archiveform");
            instruction = new Label(GlobalizationUtil.globalize("cw.workspace.ui.admin.archive_this_portal"));
            button = new Submit(GlobalizationUtil.globalize("cw.workspace.ui.admin.archive"));
            cancelbutton = new Submit(GlobalizationUtil.globalize("cw.workspace.ui.admin.cancel"));
            radio = new RadioGroup("child_portals");
            radio.addOption(new Option("recurse",new Label(GlobalizationUtil.globalize("cw.workspace.ui.admin.recurse_archive"))));
            this.add(instruction);
            this.add(button);
            this.add(cancelbutton);
            this.add(radio);
            this.addProcessListener(this);
        }
        
        public void process(FormSectionEvent e) {
            PageState s = e.getPageState();

            if(button.isSelected(s)) {
              recurse = (String)radio.getValue(s);
              String id = (String)onlineList.getSelectedKey(s);
              BigDecimal bd = new BigDecimal(id);
              PortalSite p = PortalSite.retrievePortalSite(bd);
              if(recurse == null)
                p.archive();
              else
                p.archiveRecurse();
              p.save();
            }
            onlineList.clearSelection(s); 
            this.setVisible(s, false);
            m_noPortalSelected.setVisible(s, true);
        }

        public void init(FormSectionEvent e) throws FormProcessException {
            PageState ps = e.getPageState();
            if((!onlineList.isSelected(ps)) && (!archiveList.isSelected(ps))) {
                //neither list has a selection...
                this.setVisible(ps,false);
                m_noPortalSelected.setVisible(ps, true);
            }
        }
    }

    public class UnarchiveForm extends Form implements FormProcessListener {
        private Label instruction;
        private Submit button;
        private Submit cancelbutton;

        public UnarchiveForm() {
            super("unarchiveform");
            instruction = new Label(GlobalizationUtil.globalize("cw.workspace.ui.admin.online_this_portal"));
            button = new Submit(GlobalizationUtil.globalize("cw.workspace.ui.admin.unarchive"));
            cancelbutton = new Submit(GlobalizationUtil.globalize("cw.workspace.ui.admin.cancel"));
            this.add(instruction);
            this.add(button);
            this.add(cancelbutton);
            this.addProcessListener(this);
        }
        
        public void process(FormSectionEvent e) {
            PageState s = e.getPageState();

            if(button.isSelected(s)) {
              String id = (String)archiveList.getSelectedKey(s);
              BigDecimal bd = new BigDecimal(id);
              PortalSite p = PortalSite.retrievePortalSite(bd);
              p.unarchive();
              p.save();
            }
            archiveList.clearSelection(s); 
            this.setVisible(s, false);
            m_noPortalSelected.setVisible(s, true);
        }

        public void init(FormSectionEvent e) throws FormProcessException {
            PageState ps = e.getPageState();
            if((!onlineList.isSelected(ps)) && (!archiveList.isSelected(ps))) {
                //neither list has a selection...
                this.setVisible(ps,false);
                m_noPortalSelected.setVisible(ps, true);
            }
        }
    }

    class OnlineListCellRenderer implements ListCellRenderer {
        public Component getComponent(List list, PageState pageState,
                                      Object value, String key,
                                      int index, boolean isSelected) {

            String date;
            String title;
            PortalSite ps = (PortalSite)value;
            if(ps instanceof PersonalPortal) {
              PersonalPortal pvt = (PersonalPortal)ps;
              User user = pvt.getOwningUser();
              title = user.getDisplayName() + " (personal)";
            } else {
              title = ps.getTitle();
            }
            SimpleDateFormat dft = new SimpleDateFormat();
            Date cd = ps.getCreationDate();
            //This null trap is for legacy portals
            if(cd == null)
                date = "----";
            else
                date = dft.format(ps.getCreationDate());

            if(isSelected) {
                Label label = new Label(title);
                label.setStyleAttr(date);
                return label;
            }
            else {
                ControlLink link = new ControlLink(title);
                link.setStyleAttr(date);
                return link;
            }
         }
    }

    class ArchiveListCellRenderer implements ListCellRenderer {
        public Component getComponent(List list, PageState pageState,
                                      Object value, String key,
                                      int index, boolean isSelected) {

            String title;

            PortalSite ps = (PortalSite)value;
            if(ps instanceof PersonalPortal) {
              PersonalPortal pvt = (PersonalPortal)ps;
              User user = pvt.getOwningUser();
              title = user.getDisplayName() + " (personal)";
            } else {
              title = ps.getTitle();
            }
            SimpleDateFormat dft = new SimpleDateFormat();
            String d = dft.format(ps.getArchiveDate());
            if(isSelected) {
                Label label = new Label(title);
                label.setStyleAttr(d);
                return label;
            }
            else {
                ControlLink link = new ControlLink(title);
                link.setStyleAttr(d);
                return link;
            }
         }
    }
}
