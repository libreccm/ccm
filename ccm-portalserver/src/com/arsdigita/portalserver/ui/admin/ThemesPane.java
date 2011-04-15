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

import com.arsdigita.bebop.ActionLink;
import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.SimpleComponent;
import com.arsdigita.bebop.Container;
import com.arsdigita.bebop.DynamicListWizard;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormSection;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Link;
import com.arsdigita.bebop.ModalContainer;
import com.arsdigita.bebop.List;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.PropertySheet;
import com.arsdigita.bebop.PropertySheetModel;
import com.arsdigita.bebop.PropertySheetModelBuilder;
import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.Tree;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.event.ChangeEvent;
import com.arsdigita.bebop.event.ChangeListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.list.ListModel;
import com.arsdigita.bebop.list.ListModelBuilder;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.form.RadioGroup;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.GridPanel;
import com.arsdigita.bebop.tree.TreeModel;
import com.arsdigita.persistence.OID;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.kernel.ui.ACSObjectSelectionModel;
import com.arsdigita.portalserver.PortalSite;
import com.arsdigita.portalserver.PortalSiteCollection;
import com.arsdigita.portalserver.Theme;
import com.arsdigita.portalserver.ThemeCollection;
import com.arsdigita.portalserver.ColorPicker;
import com.arsdigita.xml.Element;
import org.apache.log4j.Logger;
import java.math.BigDecimal;



public class ThemesPane extends SimpleContainer {

    private static final Logger s_log = Logger.getLogger(ThemesPane.class);

    private RadioGroup m_grp;
    private Submit createButton;
    private SelectThemeForm m_selectForm;
    private CreateThemeForm m_createForm;
    ActionLink m_createThemeLink;

    public ThemesPane(final RequestLocal portalsiteRL) {


        m_grp = new RadioGroup("themes");
        m_grp.setClassAttr("vertical");

        populateOptionGroup();

        m_selectForm = new SelectThemeForm(portalsiteRL);
        m_createForm = new CreateThemeForm(portalsiteRL);
     
        //m_createThemeLink = new ActionLink( (String) GlobalizationUtil.globalize("portalserver.ui.admin.create_theme").localize());
        //m_createThemeLink.addActionListener(new CreateThemeLinkListener());

        add(m_selectForm);
        add(m_createForm);
        //add(m_createThemeLink);

    }
    void populateOptionGroup() {
    
        Option opt;
        m_grp.clearOptions();
        ThemeCollection collection = Theme.retrieveAllThemes();

        while(collection.next())
        {
            m_grp.addOption( new Option(collection.getTheme().getID().toString(),
                                    collection.getTheme().getName()));
        }
    }


    public class SelectThemeForm extends Form implements FormProcessListener, 
                                                         FormInitListener
    {
        private Label instruction;
        private Submit button;
        RequestLocal prtlRL;

        public SelectThemeForm(RequestLocal portalsiteRL)
        {
            super("selectthemeform");
            prtlRL = portalsiteRL;
            instruction = 
             new Label(GlobalizationUtil.globalize("portalserver.ui.admin.select_theme_for_portal"));
            button = new Submit("selecttheme",
                   GlobalizationUtil.globalize("portalserver.ui.select_theme"));
            button.setButtonLabel("Select Theme");


            add(instruction);
            add(button);
            m_grp.addValidationListener(new NotNullValidationListener("Select a Theme"));
            add(m_grp);
            addProcessListener(this);
            addInitListener(this);
        }
        public void process(FormSectionEvent e)
        {
            String selectedkey;
            PageState s = e.getPageState();

            if(button.isSelected(s)) {
              selectedkey = (String)m_grp.getValue(s);
              BigDecimal bd = 
                new BigDecimal(selectedkey);

              Theme theme = Theme.retrieveTheme(bd);
            
              PortalSite p = (PortalSite)prtlRL.get(s);

              p.setTheme(theme);
              p.save();
            }
        }
        public void init(FormSectionEvent e) throws FormProcessException {
            PageState ps = e.getPageState();
            if(this.isVisible(ps)) {
              //m_createThemeLink.setVisible(ps, true);
              m_createForm.setVisible(ps,false);
            }
            FormData fd = e.getFormData();
            PortalSite p = (PortalSite)prtlRL.get(ps);
            Theme theme = p.getTheme();
            if(theme != null)
               fd.put("themes",theme.getID().toString());
            
        }
            
    }

    public class CreateThemeForm extends Form implements FormProcessListener,
                                                         FormInitListener
    {
      private Label instruction;
      private Submit savebutton;
      private Submit cancelbutton;
      private TextField themename;
      private ColorPicker globalheader;
      private ColorPicker globalheadertext;
      private ColorPicker background;
      private ColorPicker text;
      private ColorPicker activetab;
      private ColorPicker inactivetab;
      private ColorPicker activetabtext;
      private ColorPicker inactivetabtext;
      private ColorPicker toprule;
      private ColorPicker bottomrule;
      private ColorPicker portletheader;
      private ColorPicker portletborder;
      private ColorPicker portletheadertext;
      private ColorPicker portletbodynarrow;
      RequestLocal prtlRL;

      public CreateThemeForm(RequestLocal portalsiteRL)
      {
        super("createthemeform");
        prtlRL = portalsiteRL;
        setClassAttr("themecreator");
        savebutton = new Submit("savetheme");
        savebutton.setButtonLabel("Save Theme");
        instruction = 
          new Label(GlobalizationUtil.globalize("portalserver.ui.admin.create_theme_instruction"));

        globalheader = new ColorPicker("Page Header Color ", "#112233");
        globalheadertext = new ColorPicker("Page Header Text Color ", "#112233");
        background = new ColorPicker("Page Background Color ", "#112233");
        text = new ColorPicker("Page Text Color ", "#112233");
        activetab = new ColorPicker("Selected Tab Color ", "#1F22B3");
        inactivetab = new ColorPicker("Unselected Tab Color ", "#112233");
        activetabtext = new ColorPicker("Selected Tab Text Color", "#FFFFFF");
        inactivetabtext = new ColorPicker("Unselected Tab Text Color ", "#11CC33");
        toprule = new ColorPicker("Top Rule Color ", "#FFFFFF");
        bottomrule = new ColorPicker("Bottom Rule Color ", "#FFFFFF");
        portletheader = new ColorPicker("Portlet Header Color ", "#FFFFFF");
        portletborder = new ColorPicker("Portlet Border Color ", "#FFFFFF");
        portletheadertext = new ColorPicker("Portlet Header Text Color ", "#FFFFFF");
        portletbodynarrow = new ColorPicker("Narrow Column Portlet Body Color ", "#FFFFFF");


        themename = new TextField("themename");
        themename.addValidationListener(new NotNullValidationListener("Please provide a name for this theme."));

        add(instruction);
        add(themename);
        add(background);
        add(text);
        add(globalheader);
        add(globalheadertext);
        add(activetab);
        add(inactivetab);
        add(activetabtext);
        add(inactivetabtext);
        add(toprule);
        add(bottomrule);
        add(portletheader);
        add(portletheadertext);
        add(portletborder);
        add(portletbodynarrow);
        add(savebutton);

        addProcessListener(this);
        addInitListener(this);
      }
      public void process(FormSectionEvent e)
      { 
        PageState ps = e.getPageState();
        if(savebutton.isSelected(ps)) {
        Theme theme = new Theme((String)themename.getValue(ps));
        theme.setActiveTabColor(activetab.getValue(ps));
        theme.setInactiveTabColor(inactivetab.getValue(ps));
        theme.setActiveTabTextColor(activetabtext.getValue(ps));
        theme.setInactiveTabTextColor(inactivetabtext.getValue(ps));
        theme.setTopRuleColor(toprule.getValue(ps));
        theme.setBottomRuleColor(bottomrule.getValue(ps));
        theme.setPortletHeaderColor(portletheader.getValue(ps));
        theme.setPortletIconColor(portletheader.getValue(ps));
        theme.setPortletBorderColor(portletborder.getValue(ps));
        theme.setPortletHeaderTextColor(portletheadertext.getValue(ps));
        theme.setPageBGColor(background.getValue(ps));
        theme.setBodyTextColor(text.getValue(ps));
        theme.setNarrowBGColor(portletbodynarrow.getValue(ps));

        theme.save();
        }
        m_selectForm.setVisible(ps,true);
        m_createThemeLink.setVisible(ps, true);
        this.setVisible(ps, false);
      }

      public void init(FormSectionEvent e) throws FormProcessException {
      }
    }
      private class CreateThemeLinkListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
          PageState ps = event.getPageState();
          m_createForm.setVisible(ps,true);
          m_selectForm.setVisible(ps,false);
          m_createThemeLink.setVisible(ps, false);
        }
      }
}
