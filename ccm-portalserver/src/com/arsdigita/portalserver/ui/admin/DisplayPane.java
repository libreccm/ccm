/*
 * Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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

import com.arsdigita.web.ApplicationType;
import com.arsdigita.portalserver.PortalSite;
import com.arsdigita.portalserver.PortalTab;
import com.arsdigita.portalserver.PortalTabCollection;
import com.arsdigita.portal.Portlet;
import com.arsdigita.portal.PortletType;
import com.arsdigita.portal.PortletTypeCollection;

import com.arsdigita.bebop.ActionLink;
import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.ControlLink;
import com.arsdigita.bebop.DynamicListWizard;
import com.arsdigita.bebop.Image;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.GridPanel;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.List;
import com.arsdigita.bebop.ModalContainer;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.SingleSelect;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.event.ChangeEvent;
import com.arsdigita.bebop.event.ChangeListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.bebop.parameters.NotEmptyValidationListener;
import com.arsdigita.bebop.list.ListCellRenderer;
import com.arsdigita.bebop.list.ListModel;
import com.arsdigita.bebop.list.ListModelBuilder;
import org.apache.log4j.Logger;
import java.util.TooManyListenersException;
import java.util.Vector;
import java.util.ListIterator;
import java.math.BigDecimal;

/**
 * <b><strong>Experimental</strong></b>
 *
 * @author <a href="mailto:jparsons@arsdigita.com">Jim Parsons</a>
 * @author <a href="mailto:elorenzo@arsdigita.com">Eric Lorenzo</a>
 * @author <a href="mailto:justin@arsdigita.com">Justin Ross</a>
 * @version $Id: //portalserver/dev/src/com/arsdigita/portalserver/ui/admin/DisplayPane.java#9 $
 */
public class DisplayPane extends DynamicListWizard {

    public static final String versionId = "$Id: //portalserver/dev/src/com/arsdigita/portalserver/ui/admin/DisplayPane.java#9 $ by $Author: dennis $, $DateTime: 2004/08/17 23:19:25 $";

    private static final Logger s_cat
                                = Logger.getLogger(ApplicationsPane.class.getName());

    final ModalContainer m_editContainer = new ModalContainer();

    SimpleContainer m_mainDisplay;
    ApplicationModifyComponent m_portletModify;
    PortletAddForm m_portletAddForm;
    RenameTabForm m_renameTabForm;
    DeleteTabForm m_deleteTabForm;
    List m_layouts;
    RequestLocal selectedTabIDRL;
    static final String m_layoutNames[] = {"W", "NW", "WN", "NWN", "NNN"};

    BigDecimalParameter m_selectedPortletParam;

    static final String FOUR_SPACE_INDENT_STRING = "&nbsp;&nbsp;&nbsp;&nbsp;";

    static class TabListModel implements ListModel {

        PortalTabCollection m_tabs;
        PortalTab m_currTab;

        public TabListModel(PortalSite psite) {
            m_tabs = psite.getTabsForPortalSite();
        }

        public boolean next() {
            if (!m_tabs.next()) {
                return false;
            }
            m_currTab = m_tabs.getPortalTab();
            return true;
        }

        public Object getElement() {
            return m_currTab.getTitle();
        }

        public String getKey() {
            return m_currTab.getID().toString();
        }
    }

    public DisplayPane(final RequestLocal portalsiteRL) {
        super("Current Tabs. Use the arrows to shift tab position.",
              new ListModelBuilder() {
                  public ListModel makeModel(List l, PageState ps) {
                      PortalSite psite = (PortalSite) portalsiteRL.get(ps);
                      return new TabListModel(psite);
                  }

                  public void lock() {
                  }

                  public boolean isLocked() {
                      return true;
                  }
              },
              "Add a tab",
              new Label(GlobalizationUtil.globalize(
                              "cw.workspace.ui.admin.there_currently_is_no_tab_selected_please_select_a_tab")));

        final DynamicListWizard dlw = this;

        // FORM FOR ADDING NEW TABS
        Form addForm = new Form("addTab");

        addForm.add(new Label(GlobalizationUtil.globalize("cw.workspace.ui.admin.new_tab_name")));
        final TextField newTabName = new TextField("name");
        newTabName.getParameterModel().addParameterListener(new NotEmptyValidationListener());
        newTabName.setSize(40);
        addForm.add(newTabName);
        addForm.add(new Label());
        addForm.add(new Submit("Add tab"));

        addForm.addProcessListener(new FormProcessListener() {
            public void process(FormSectionEvent ev) {
                PageState ps = ev.getPageState();
                PortalSite psite = (PortalSite) portalsiteRL.get(ps);
                PortalTab newTab
                          = PortalTab.createTab((String) newTabName.getValue(ps), psite);
                psite.addPortalTab(newTab);
                newTab.save();
                psite.save();

                List list = (List) dlw.getListingComponent();

                list.getSelectionModel()
                        .setSelectedKey(ps, newTab.getID().toString());
            }
        });

        setAddPane(addForm);

        // CONSTRUCT EDIT COMPONENT
        selectedTabIDRL = new RequestLocal() {
            protected Object initialValue(PageState ps) {
                String tabIDstr = (String) dlw.getSelectionModel()
                        .getSelectedKey(ps);
                return new BigDecimal(tabIDstr);
            }
        };

        m_selectedPortletParam = new BigDecimalParameter("selectedPortlet");

        m_mainDisplay = new BoxPanel(BoxPanel.VERTICAL);
        PortletLayoutComponent plc
                               = new PortletLayoutComponent(portalsiteRL, selectedTabIDRL) {
                    protected void handleConfigure(PageState ps, BigDecimal id) {
                        ps.setValue(m_selectedPortletParam, id);
                        m_editContainer.setVisibleComponent(ps, m_portletModify);
                    }
                };
        LockableLinks locklinks
                      = new LockableLinks(portalsiteRL, selectedTabIDRL);
        m_mainDisplay.add(locklinks);
        m_mainDisplay.add(plc);

        BoxPanel links = new BoxPanel(BoxPanel.HORIZONTAL, true);

        ActionLink renameLink = new ActionLink((String) GlobalizationUtil.globalize(
                "cw.workspace.ui.admin.rename_tab").localize());
        renameLink.setClassAttr("actionLink");
        renameLink.addActionListener(new RenameLinkListener());
        links.add(renameLink);

        ActionLink deleteLink = new ActionLink((String) GlobalizationUtil.globalize(
                "cw.workspace.ui.admin.delete_tab").localize()) {
                    public boolean isVisible(PageState ps) {
                        PortalSite psite = (PortalSite) portalsiteRL.get(ps);
                        PortalTabCollection pstc = psite.getTabsForPortalSite();
                        return (pstc.size() > 1);
                    }
                };
        deleteLink.setClassAttr("actionLink");
        deleteLink.addActionListener(new DeleteLinkListener());
        links.add(deleteLink);

        m_mainDisplay.add(links);

        final BigDecimalParameter portletTypeParam
                                  = new BigDecimalParameter("typeID");
        Form addPortlet = new Form("ap1", new GridPanel(1)) {
            public void register(Page p) {
                super.register(p);
                p.addComponentStateParam(this, portletTypeParam);
            }
        };
        Image image = new Image("/assets/cw/general/add.gif");
        image.setBorder("0");

        SimpleContainer imageLabelSC = new SimpleContainer();
        imageLabelSC.add(image);
        imageLabelSC.add(new Label("To add a portlet to this page, first choose a type: "));

        addPortlet.add(imageLabelSC);

        final SingleSelect portletTypeSelect = new SingleSelect("ptype");
        portletTypeSelect.addValidationListener(
                new NotNullValidationListener());
        PortletTypeCollection ptc = PortletType.retrieveAllPortletTypes();
        //Alphabetize by title
        ptc.addOrder("title");
        while (ptc.next()) {
            ApplicationType providerAppType = ptc.getPortletType().getProviderApplicationType();
            if (providerAppType != null) {
                portletTypeSelect.addOption(new Option(ptc.getID().toString(), ptc.getTitle() + " ("
                                                                               + providerAppType.
                                                       getTitle() + ") "));
            } else {
                portletTypeSelect.addOption(new Option(ptc.getID().toString(), ptc.getTitle()));
            }
        }

        SimpleContainer selectorSC = new SimpleContainer();
        selectorSC.add(portletTypeSelect);
        selectorSC.add(new Submit("Go"));
        addPortlet.add(selectorSC);

        addPortlet.addProcessListener(new FormProcessListener() {
            public void process(FormSectionEvent ev) {
                PageState ps = ev.getPageState();
                m_editContainer.setVisibleComponent(ps, m_portletAddForm);
                ps.setValue(portletTypeParam, new BigDecimal(
                            (String) portletTypeSelect.getValue(ps)));
                m_portletAddForm.activate(ps);
            }
        });
        m_mainDisplay.add(addPortlet);

        BoxPanel tabLayouts = new BoxPanel(BoxPanel.HORIZONTAL, true);

        ListModelBuilder lmb = new ListModelBuilder() {
            boolean m_isLocked;

            public ListModel makeModel(List l, PageState pageState) {
                return new LayoutListModel(pageState);
            }

            public void lock() {
                m_isLocked = true;
            }

            public boolean isLocked() {
                return m_isLocked;
            }
        };

        m_layouts = new List(lmb);

        m_layouts.setClassAttr("tabLayouts");

        m_layouts.setCellRenderer(new LayoutListCellRenderer());

        m_layouts.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                PageState ps = e.getPageState();
                String key = (String) ((List) dlw.getListingComponent())
                        .getSelectedKey(ps);
                BigDecimal bd = new BigDecimal(key);
                PortalTab ptab = PortalTab.retrieveTab(bd);
                String laykey = (String) m_layouts.getSelectedKey(ps);
                Integer lk = new Integer(laykey);
                ptab.setLayout(m_layoutNames[lk.intValue()]);
                ptab.save();
            }
        });

        tabLayouts.add(m_layouts);

        m_mainDisplay.add(tabLayouts);

        m_portletAddForm
        = new PortletAddForm(portalsiteRL, selectedTabIDRL, new RequestLocal() {
                    protected Object initialValue(PageState ps) {
                        BigDecimal id = (BigDecimal) ps.getValue(portletTypeParam);
                        if (id == null) {
                            return null;
                        }
                        return PortletType.retrievePortletType(id);
                    }
                });
        m_portletAddForm.addCompletionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                PageState ps = ev.getPageState();
                ps.reset(m_editContainer);
            }
        });

        RequestLocal selectedPortletRL = new RequestLocal() {
            public Object initialValue(PageState ps) {
                BigDecimal portletID
                           = (BigDecimal) ps.getValue(m_selectedPortletParam);
                if (portletID == null) {
                    return null;
                }
                return Portlet.retrievePortlet(portletID);
            }
        };
        ActionListener reset = new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                PageState ps = ev.getPageState();
                ps.reset(m_editContainer);
                ps.setValue(m_selectedPortletParam, null);
            }
        };
        m_portletModify = new ApplicationModifyComponent(selectedPortletRL, true, reset, reset);

        m_renameTabForm = new RenameTabForm(selectedTabIDRL);
        m_deleteTabForm = new DeleteTabForm(selectedTabIDRL);

        m_editContainer.add(m_mainDisplay);
        m_editContainer.add(m_portletModify);
        m_editContainer.add(m_portletAddForm);
        m_editContainer.add(m_renameTabForm);
        m_editContainer.add(m_deleteTabForm);
        setEditPane(m_editContainer);

        ((List) getListingComponent()).addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent ev) {
                ev.getPageState().reset(m_editContainer);
            }
        });

        //
        //This cell renderer attempts to do the following:
        //1) If tab name is selected, returns a label
        //2) If not selected, returns a link
        //3) If the tab name is selected, and:
        //  a) It is the first tab, a move_right arrow link is rendered
        //  b) It is the last tab, a move_left arrow link is rendered
        //  c) If there are more than two tabs, and it isn't first
        //     or last, both move_left and move_right arrows are shown.
        //The arrows are implemented as control links with attr values...
        //One other thing, if there is only one tab in the list,
        //this code preselects it, unless the add tab link has been
        //clicked.
        ((List) getListingComponent()).setCellRenderer(new ListCellRenderer() {
            public Component getComponent(List list, PageState state,
                                          Object value, String key, int index, boolean isSelected) {

                Label tabName;
                int indent_ctr = 0;
                boolean isFirst = false;
                boolean isLast = false;

                SimpleContainer container = new SimpleContainer();

                    //get the collection of tabs, its size, and use this info
                //to determine where in the collection this tab name resides...
                BigDecimal bd = new BigDecimal(key);
                PortalTab ptab = PortalTab.retrieveTab(bd);
                PortalSite psite = ptab.getPortalSite();
                PortalTabCollection ptcoll = psite.getTabsForPortalSite();
                long size = ptcoll.size();

                    //If only one tab, select it ONLY if addtablink not selected
                // and then get outa here...
                if (size == 1) {
                    if ((dlw.getAddLink().isSelected(state)) == false) {
                        list.setSelectedKey(state, key);
                        tabName = new Label(value.toString());
                        container.add(tabName);
                        //Update tab request local
                        selectedTabIDRL.set(state, bd);
                        return container;
                    }
                } else //more than one tab...
                {
                    indent_ctr = 0;
                    while (ptcoll.next()) {
                        indent_ctr++;
                        if (bd.compareTo(ptcoll.getID()) == 0) //we found tab...
                        {
                            if (indent_ctr == 1) {
                                isFirst = true;
                            }
                            if (indent_ctr == size) {
                                isLast = true;
                            }
                            break;
                        }
                    }
                }
                ptcoll.close();

                //Code below constructs proper indent for tab
                StringBuffer buf = new StringBuffer(200);
                for (int i = 0; i < (indent_ctr - 1); i++) {
                    buf.append(FOUR_SPACE_INDENT_STRING);
                }

                    //Here we construct the control links for arrows
                //This could be done below in the particular cases for
                //a slight performance benefit, but putting them
                //here, together, is easier to understand...
                if (isSelected) //This tab name is currently selected...
                {
                    tabName = new Label(value.toString());
                    tabName.setFontWeight(Label.BOLD);
                    Label labelLeft = new Label(GlobalizationUtil.globalize(
                            "cw.workspace.ui.admin.shift_left"));
                    Label labelRight = new Label(GlobalizationUtil.globalize(
                            "cw.workspace.ui.admin.shift_right"));

                    ControlLink linkLeft = new ControlLink(labelLeft) {
                        public void setControlEvent(PageState s) {
                            s.setControlEvent(dlw, "left", "1");
                        }
                    };
                    ControlLink linkRight = new ControlLink(labelRight) {
                        public void setControlEvent(PageState s) {
                            s.setControlEvent(dlw, "right", "1");
                        }
                    };

                    linkLeft.setClassAttr("shiftleft");
                    linkRight.setClassAttr("shiftright");
                    if (isFirst) {
                        container.add(tabName);
                        container.add(linkRight);
                    } else if (isLast) {
                        container.add(linkLeft);
                        container.add(tabName);
                    } else //This tab is somewhere in the middle...
                    {
                        container.add(linkLeft);
                        container.add(tabName);
                        container.add(linkRight);
                    }

                } else //this tab is NOT selected...return a link
                {
                    String ttab = buf.toString() + value.toString();
                    tabName = new Label(ttab, false);
                    ControlLink l = new ControlLink(tabName);
                    container.add(l);
                }
                return container;
            }
        });

    } //end of constructor

    public void register(Page p) {
        super.register(p);
        p.addComponentStateParam(this, m_selectedPortletParam);
    }

    public void respond(PageState state) throws javax.servlet.ServletException {
        String tabIDstr
               = (String) this.getSelectionModel()
                .getSelectedKey(state);

        BigDecimal tabID = new BigDecimal(tabIDstr);

        String name = state.getControlEventName();
        if (name.compareTo("left") == 0) {
            PortalTab ptab = PortalTab.retrieveTab(tabID);
            PortalSite psite = ptab.getPortalSite();
            psite.swapTabWithPrevious(ptab);
        } else if (name.compareTo("right") == 0) {
            PortalTab ptab = PortalTab.retrieveTab(tabID);
            PortalSite psite = ptab.getPortalSite();
            psite.swapTabWithNext(ptab);
        }
    }

    public class RenameTabForm extends Form implements FormProcessListener {

        private TextField currenttabName;
        private Label instruction;
        private Submit button, cancelbutton;
        private RequestLocal m_tabIDRL;

        public RenameTabForm(RequestLocal SelectedTabIDRL) {
            super("renametabform");
            m_tabIDRL = SelectedTabIDRL;

            instruction = new Label(GlobalizationUtil.globalize(
                    "cw.workspace.ui.admin.enter_new_name_for_this_tab_in_text_field"));
            currenttabName = new TextField("CurrentTabName");
            currenttabName.setDefaultValue("");
            currenttabName.setSize(40);
            currenttabName.addValidationListener(new NotNullValidationListener(
                    "Every Tab must have a name!"));
            try {
                currenttabName.addPrintListener(new PrintListener() {
                    public void prepare(PrintEvent e) {
                        PageState s = e.getPageState();
                        BigDecimal tabID = (BigDecimal) m_tabIDRL.get(s);
                        PortalTab ptab = PortalTab.retrieveTab(tabID);
                        TextField tf = (TextField) e.getTarget();
                        tf.setValue(s, ptab.getTitle());
                    }
                });
            } catch (java.util.TooManyListenersException e) {
            }
            button = new Submit("Rename tab");
            button.setButtonLabel("Rename tab");
            cancelbutton = new Submit("Cancel");
            cancelbutton.setButtonLabel("Cancel");
            this.add(instruction);
            this.add(currenttabName);
            this.add(button);
            this.add(cancelbutton);
            this.addProcessListener(this);
        }

        public void process(FormSectionEvent e) {
            PageState s = e.getPageState();

            if (button.isSelected(s)) {
                BigDecimal tabID = (BigDecimal) m_tabIDRL.get(s);
                PortalTab ptab = PortalTab.retrieveTab(tabID);
                ptab.setTitle(currenttabName.getValue(s).toString());
                ptab.save();
            }

            s.reset(m_editContainer);
        }
    } //end rename form

    public class DeleteTabForm extends Form implements FormProcessListener {

        private TextField currenttabName;
        private Label instruction;
        private Submit button;
        private Submit cancelbutton;
        private RequestLocal m_tabIDRL;

        public DeleteTabForm(RequestLocal SelectedTabIDRL) {
            super("deletetabform");
            m_tabIDRL = SelectedTabIDRL;

            instruction = new Label(GlobalizationUtil.globalize(
                    "cw.workspace.ui.admin.are_you_sure_you_want_to_delete_this_tab"));
            instruction.addPrintListener(new PrintListener() {
                public void prepare(PrintEvent e) {
                    PageState s = e.getPageState();
                    String prefixstr = "Are you sure you want to delete the ";
                    BigDecimal tabID = (BigDecimal) m_tabIDRL.get(s);
                    PortalTab ptab = PortalTab.retrieveTab(tabID);
                    Label t = (Label) e.getTarget();
                    t.setLabel(prefixstr + ptab.getTitle() + " tab?");
                }
            });

            button = new Submit("Delete this tab");
            button.setButtonLabel("Delete this tab");
            cancelbutton = new Submit("Cancel");
            cancelbutton.setButtonLabel("Cancel");
            this.add(instruction);
            this.add(button);
            this.add(cancelbutton);
            this.addProcessListener(this);
        }

        public void process(FormSectionEvent e) {
            PageState s = e.getPageState();

            if (button.isSelected(s)) {
                BigDecimal tabID = (BigDecimal) m_tabIDRL.get(s);
                PortalTab ptab = PortalTab.retrieveTab(tabID);
                ptab.delete();
                getSelectionModel().clearSelection(s);
                reset(s);
            }
            s.reset(m_editContainer);
        }
    } //end delete form

    private class RenameLinkListener implements ActionListener {

        public void actionPerformed(ActionEvent event) {
            PageState ps = event.getPageState();
            m_editContainer.setVisibleComponent(ps, m_renameTabForm);
        }
    }

    private class DeleteLinkListener implements ActionListener {

        public void actionPerformed(ActionEvent event) {
            PageState ps = event.getPageState();
            m_editContainer.setVisibleComponent(ps, m_deleteTabForm);
        }
    }

    private class LayoutListModel implements ListModel {

        Vector vec;
        ListIterator vl;

        LayoutListModel(PageState pageState) {
            vec = new Vector();
            vec.add("W");
            vec.add("NW");
            vec.add("WN");
            vec.add("NWN");
            vec.add("NNN");

            vl = vec.listIterator();
        }

        public boolean next() {
            return vl.hasNext();
        }

        public Object getElement() {
            return vl.next();
        }

        public String getKey() {
            int dex = vl.previousIndex();
            dex = dex + 1;
            //Integer it = new Integer(vl.previousIndex());
            Integer it = new Integer(dex);
            return it.toString();
        }
    }

    class LayoutListCellRenderer implements ListCellRenderer {

        public Component getComponent(List list, PageState pageState, Object value, String key,
                                      int index, boolean isSelected) {
            String val = (String) value;
            //check if selected...if not selected
            //find out what index we are...
            //then set an attribute on the link so that it is replaced
            //with an image link in the stylesheet.
            //if selected, return a label with a selected attr and value...
            if (isSelected) {
                Label label = new Label(val);
                label.setClassAttr(val);
                return label;
            } else {
                ControlLink link = new ControlLink(val);
                link.setClassAttr(val);
                link.setStyleAttr("HooHaH");
                return link;
            }

        }
    }

}
