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
import com.arsdigita.web.Application;
import com.arsdigita.web.ApplicationCollection;
import com.arsdigita.web.ApplicationType;
import com.arsdigita.bebop.AbstractSingleSelectionModel;
import com.arsdigita.bebop.Container;
import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.ComponentSelectionModel;
import com.arsdigita.bebop.ControlLink;
import com.arsdigita.bebop.GridPanel;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Link;
import com.arsdigita.bebop.ActionLink;
import com.arsdigita.bebop.List;
import com.arsdigita.bebop.list.ListModel;
import com.arsdigita.bebop.list.ListModelBuilder;
import com.arsdigita.bebop.list.ListCellRenderer;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.form.RadioGroup;
import com.arsdigita.bebop.form.CheckboxGroup;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.bebop.parameters.NotEmptyValidationListener;
import com.arsdigita.bebop.parameters.ArrayParameter;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.SimpleComponent;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.SplitWizard;
import com.arsdigita.bebop.SplitPanel;
import com.arsdigita.bebop.Tree;
import com.arsdigita.bebop.RequestLocal;
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
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.KernelExcursion;
import com.arsdigita.xml.Element;

import org.apache.log4j.Category;


/**
 * This Class aggregates common admin utilities in one page.
 * @author <a href="mailto:jparsons@arsdigita.com">Jim Parsons</a>
 */

public final class PortalSiteMapPanel extends SplitPanel {


    Tree m_portalTree;
    PortalTreeModelBuilder m_portalTreeModelBuilder;
    Label m_noPortalSelected;
    ArchiveForm archiveForm;
    UnarchiveForm unarchiveForm;
    PropertiesForm propForm;
    EditPropertiesForm editpropForm;
    DeleteForm deleteform;
    Component createform;
    Component createchildform;
    GridPanel vmdPanel;  //view, modify, delete
    GridPanel linkPanel;
    RequestLocal m_selectedPortalRL;
    RequestLocal portalsiteRL;
    RequestLocal childportalsiteRL;



    public PortalSiteMapPanel(Page p) {

        p.addRequestListener(new RequestListener () {
            public void pageRequested(RequestEvent e) {
                PageState s = e.getPageState();
                if(!m_portalTree.isSelected(s)) {
                    //Site map Tree does not have a selection...
                    //set all forms invisible
                    clearAllVisibility(s);
                    linkPanel.setVisible(s,true);
                    m_noPortalSelected.setVisible(s, true);
                }
            }
        });

        portalsiteRL = new RequestLocal();
        childportalsiteRL = new RequestLocal();

        m_selectedPortalRL = new RequestLocal() {
           protected Object initialValue(PageState s) {
             if(m_portalTree.isSelected(s)) {
               String portalIDstr = 
                  (String)m_portalTree.getSelectionModel()
                                          .getSelectedKey(s);
               return 
                    PortalSite.retrievePortalSite(new BigDecimal(portalIDstr));
              } else {
                return null; 
              }
           }
         };
 
               
        setClassAttr("portalsitemap");
        setDivider(40);
 
        GridPanel headerPanel = new GridPanel(1);
        headerPanel.setClassAttr("portalsitemap_header");
        headerPanel.add(new Label(GlobalizationUtil.globalize("cw.workspace.ui.admin.portal_archive_header")));
        setHeader(headerPanel);

        GridPanel portalPanel = new GridPanel(1);
        portalPanel.setClassAttr("portalsitemap_panel");
        setLeftComponent(portalPanel);

        GridPanel RHSPanel = new GridPanel(1);
        GridPanel formPanel = new GridPanel(1);
        GridPanel actionPanel = new GridPanel(1);
        linkPanel = new GridPanel(4);
        linkPanel.setClassAttr("portalsitemaplinkpanel");        
        setRightComponent(RHSPanel);
        RHSPanel.add(formPanel);
        RHSPanel.add(actionPanel);

        archiveForm = new ArchiveForm();
        actionPanel.add(archiveForm);

        unarchiveForm = new UnarchiveForm();
        actionPanel.add(unarchiveForm);

        GridPanel gp0 = new GridPanel(1);
        gp0.setClassAttr("deleteform");
        deleteform = new DeleteForm(gp0);
        actionPanel.add(deleteform);

        actionPanel.add(linkPanel);

        GridPanel gp1 = new GridPanel(1);
        gp1.setClassAttr("propertiesform");
        propForm = new PropertiesForm(gp1);
        formPanel.add(propForm);

        GridPanel gp2 = new GridPanel(1);
        gp2.setClassAttr("editpropscontainer");
        editpropForm = new EditPropertiesForm(gp2);
        formPanel.add(editpropForm);

        
        //this panel exists so view, modify, and delete can be
        //optionally visible
        vmdPanel = new GridPanel(3) {
          public boolean isVisible(PageState s) {
            if(m_portalTree.isSelected(s))
              return(true);
            else
              return(false);
          }
        };
        vmdPanel.setClassAttr("portalsitemaplinkpanelvmd");
        linkPanel.add(vmdPanel);

        createform = PortalCreateForm.create(new RequestLocal(),
                        portalsiteRL, new ActionListener() {
                           public void actionPerformed(ActionEvent e) {
                             PageState s = e.getPageState();
                             //turn off this form and turn on completion form
                           }
                        });
        formPanel.add(createform); 

        createchildform = PortalCreateForm.create(m_selectedPortalRL,
                        childportalsiteRL, new ActionListener() {
                           public void actionPerformed(ActionEvent e) {
                             PageState s = e.getPageState();
                             //turn off this form and turn on completion form
                           }
                        });

        formPanel.add(createchildform); 

        m_portalTreeModelBuilder = new PortalTreeModelBuilder(false);

        m_portalTree = new Tree( m_portalTreeModelBuilder); 

        m_portalTree.addChangeListener(new ChangeListener () {
            public void stateChanged(ChangeEvent e) {
                PageState s = e.getPageState();
                if(m_portalTree.isSelected(s)) {
                  clearAllVisibility(s);
                  propForm.setVisible(s, true);
                  linkPanel.setVisible(s, true);
                  vmdPanel.setVisible(s, true);
                } else {
                  clearAllVisibility(s);
                  m_noPortalSelected.setVisible(s, true);
                }
              
            }
        });
        m_portalTree.setCellRenderer
              (new PortalTreeModelBuilder.DefaultRenderer());
        m_portalTree.setClassAttr("portalsitemap_tree");

         Label sitemaplabel = 
            new Label(GlobalizationUtil.globalize(
                     "cw.workspace.ui.admin.portals_currently_mounted"));
         sitemaplabel.setFontWeight(Label.BOLD);
         portalPanel.add(sitemaplabel);
         portalPanel.add(m_portalTree);

         
         ////////////////////////////////
         //Set up links in link panel.
         ///////////////////////////////

         ////////////////////////////////
         //Create panel links

         //Link for creating top level workplaces  
         ActionLink createtoplevel = 
            new ActionLink(new Label(GlobalizationUtil.
               globalize("cw.workspace.ui.admin.create_top_level")));
         createtoplevel.setClassAttr("createtoplevel");
         createtoplevel.addActionListener(new ActionListener() {
           public void actionPerformed(ActionEvent e) {
             PageState s = e.getPageState();
             if(m_portalTree.isSelected(s)) {
               PortalSite psite = (PortalSite)m_selectedPortalRL.get(s);
               if(psite != null) {
                   clearAllVisibility(s);
                   createform.setVisible(s,true);
               }
             }
           }
         });

         //Link for creating child portals, only visible when
         //a portal is selected in tree widget.
         ActionLink createchild = 
            new ActionLink(new Label(GlobalizationUtil.
               globalize("cw.workspace.ui.admin.create_child"))) {
          public boolean isVisible(PageState s) {
            if(m_portalTree.isSelected(s))
              return(true);
            else
              return(false);
          }
        };
         createchild.setClassAttr("createchild");
         createchild.addActionListener(new ActionListener() {
           public void actionPerformed(ActionEvent e) {
             PageState s = e.getPageState();
             if(m_portalTree.isSelected(s)) {
               PortalSite psite = (PortalSite)m_selectedPortalRL.get(s);
               if(psite != null) {
                   clearAllVisibility(s);
                   createchildform.setVisible(s,true);
               }
             }
           }
         });
         Label createPanelLabel = 
              new Label(GlobalizationUtil.
                 globalize("cw.workspace.ui.admin.create"));
         createPanelLabel.setIdAttr("createpanelheader");
         linkPanel.add(createPanelLabel);
         linkPanel.add(createtoplevel);
         linkPanel.add(createchild);

         //////////////////////////
         //View panel links

         //Visit in new browser window 
         Link visitbutton = new Link("Visit selected portal","");
         visitbutton.setClassAttr("portalvisitlink");
         try {
         visitbutton.addPrintListener(new PrintListener() {
           public void prepare(PrintEvent e) {
             PageState s = e.getPageState();
             //Check if portal is selected
             if(m_portalTree.isSelected(s)) {
               PortalSite psite = (PortalSite)m_selectedPortalRL.get(s);
               Link l = (Link)e.getTarget();
               if(!(psite == null)) {
                 l.setTarget(psite.getPath());
               } else {
                 l.setTarget("");
               }
             }   
           }
         });
         } catch(java.util.TooManyListenersException e) { }

         //Visit local admin in new browser window
         Link visitadminbutton = 
                      new Link("Visit selected portal admin page","");
         visitadminbutton.setClassAttr("portaladminvisitlink");
         try {
         visitadminbutton.addPrintListener(new PrintListener() {
           public void prepare(PrintEvent e) {
             PageState s = e.getPageState();
             //Check if portal is selected
             if(m_portalTree.isSelected(s)) {
               PortalSite psite = (PortalSite)m_selectedPortalRL.get(s);
               Link l = (Link)e.getTarget();
               if(!(psite == null)) {
                 l.setTarget(psite.getPath() + "/admin/");
               } else {
                 l.setTarget("");
               }
             }   
           }
         });

         } catch(java.util.TooManyListenersException e) { }

         Label viewLabel = 
                     new Label(GlobalizationUtil.
                         globalize("cw.workspace.ui.admin.viewpanel_header"));
         viewLabel.setIdAttr("viewpanelheader");
         vmdPanel.add(viewLabel);
         vmdPanel.add(visitbutton);
         vmdPanel.add(visitadminbutton);

         ////////////////////////
         //Modify Panel links

         Label archivelinklabel = new Label("Archive/Unarchive");
         archivelinklabel.addPrintListener(new PrintListener() {
           public void prepare(PrintEvent e) {
             PageState s = e.getPageState();
             Label t = (Label)e.getTarget();
             if(m_portalTree.isSelected(s)) {
               PortalSite psite = (PortalSite)m_selectedPortalRL.get(s);
               if(!(psite == null)) {
                 if(psite.isArchived()) 
                   t.setLabel(GlobalizationUtil.
                      globalize("cw.workspace.ui.admin.unarchive"));
                 else
                   t.setLabel(GlobalizationUtil.
                      globalize("cw.workspace.ui.admin.archive"));
                }
             }
          }
        });   



         ActionLink archivebutton = new ActionLink(archivelinklabel); 
         archivebutton.setClassAttr("portalarchivelink");
         archivebutton.addActionListener(new ActionListener() {
           public void actionPerformed(ActionEvent e) {
             PageState s = e.getPageState();
             if(m_portalTree.isSelected(s)) {
               PortalSite psite = (PortalSite)m_selectedPortalRL.get(s);
               if(psite != null) {
                 if(psite.isArchived()) {
                   clearAllVisibility(s);
                   unarchiveForm.setVisible(s, true);
                   propForm.setVisible(s, true);
                 } else {
                   clearAllVisibility(s);
                   archiveForm.setVisible(s, true);
                   propForm.setVisible(s, true);
                }                 
               }
             }
           }
         });

         Label modifyLabel = 
                     new Label(GlobalizationUtil.
                         globalize("cw.workspace.ui.admin.modifypanel_header"));
         modifyLabel.setIdAttr("modifypanelheader");
         vmdPanel.add(modifyLabel);
         vmdPanel.add(archivebutton);

         //////////////////////
         //Delete links
         ActionLink deletebutton = new ActionLink("Delete");
         deletebutton.setClassAttr("portaldeletelink");
         deletebutton.addActionListener(new ActionListener() {
           public void actionPerformed(ActionEvent e) {
             PageState s = e.getPageState();
             //Set delete form visible
             clearAllVisibility(s);
             deleteform.setVisible(s, true);
             propForm.setVisible(s, true);
           }
         });

         Label deleteLabel = 
                     new Label(GlobalizationUtil.
                         globalize("cw.workspace.ui.admin.deletepanel_header"));
         deleteLabel.setIdAttr("deletepanelheader");
         vmdPanel.add(deleteLabel);
         vmdPanel.add(deletebutton); 

         //set Default message in right pane when nothing selected
         m_noPortalSelected = 
                 new Label(GlobalizationUtil
                     .globalize("cw.workspace.ui.admin.no_portal_selected"));
         m_noPortalSelected.setFontWeight(Label.ITALIC);
         formPanel.add(m_noPortalSelected);

        
    }
  
    /**
      * Set's all relevant panels and forms to no visibility.
      *
      */ 
    public void clearAllVisibility(PageState s) {
       propForm.setVisible(s, false);
       editpropForm.setVisible(s, false);
       createform.setVisible(s,false);
       createchildform.setVisible(s,false);
       linkPanel.setVisible(s, false);
       vmdPanel.setVisible(s, false);
       archiveForm.setVisible(s,false);
       unarchiveForm.setVisible(s, false);
       deleteform.setVisible(s, false);
       m_noPortalSelected.setVisible(s, false);
    }


    public class ArchiveForm extends Form implements FormProcessListener {
        private Label instruction;
        private Submit button;
        private Submit cancelbutton;
        private RadioGroup radio;
        private String recurse;

        public ArchiveForm() {
            super("archiveform");
            instruction = new Label(GlobalizationUtil
                   .globalize("cw.workspace.ui.admin.archive_this_portal"));
            button = new Submit(GlobalizationUtil
                    .globalize("cw.workspace.ui.admin.archive"));
            cancelbutton = new Submit(GlobalizationUtil
                    .globalize("cw.workspace.ui.admin.cancel"));
            radio = new RadioGroup("child_portals");
            radio.addOption(new Option("recurse",new Label(GlobalizationUtil
                    .globalize("cw.workspace.ui.admin.recurse_archive"))));
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
              String id = (String)m_portalTree.getSelectedKey(s);
              BigDecimal bd = new BigDecimal(id);
              PortalSite p = PortalSite.retrievePortalSite(bd);
              if(recurse == null)
                p.archive();
              else
                p.archiveRecurse();
              p.save();
            }
            m_portalTree.clearSelection(s); 
            this.setVisible(s, false);
            m_noPortalSelected.setVisible(s, true);
        }

    }


    public class UnarchiveForm extends Form implements FormProcessListener {
        private Label instruction;
        private Submit button;
        private Submit cancelbutton;

        public UnarchiveForm() {
            super("unarchiveform");
            instruction = new Label(GlobalizationUtil
                   .globalize("cw.workspace.ui.admin.online_this_portal"));
            button = new Submit(GlobalizationUtil
                   .globalize("cw.workspace.ui.admin.unarchive"));
            cancelbutton = new Submit(GlobalizationUtil
                   .globalize("cw.workspace.ui.admin.cancel"));
            this.add(instruction);
            this.add(button);
            this.add(cancelbutton);
            this.addProcessListener(this);
        }
        
        public void process(FormSectionEvent e) {
            PageState s = e.getPageState();

            if(button.isSelected(s)) {
              String id = (String)m_portalTree.getSelectedKey(s);
              BigDecimal bd = new BigDecimal(id);
              PortalSite p = PortalSite.retrievePortalSite(bd);
              p.unarchive();
              p.save();
            }
            m_portalTree.clearSelection(s); 
            this.setVisible(s, false);
            m_noPortalSelected.setVisible(s, true);
        }

    }


    public class PropertiesForm extends Form {
        private Label nameSelected;

        private Label nameLabel;
        private Label name;

        private Label urlLabel;
        private Label url;

        private Label descriptionLabel;
        private Label description;

        private Label creationDateLabel;
        private Label creationDate;

        private Label statusLabel;
        private Label status;

        private Label membersLabel;
        private List members;

        private Label applications;
        private List apps;

        private ActionLink editlink;

        


        public PropertiesForm(GridPanel gp) {
            super("propsform", gp);

             nameSelected = new Label("Selected Portal");
             nameSelected.setIdAttr("propnameselected"); 
             nameSelected.addPrintListener(new PrintListener() {
               public void prepare(PrintEvent e) {
                 PageState s = e.getPageState();
                 if(m_portalTree.isSelected(s)) {
                   PortalSite psite = (PortalSite)m_selectedPortalRL.get(s);
                   if(!(psite == null)) {
                     String n = psite.getDisplayName();
                     Label t = (Label)e.getTarget();
                     t.setLabel(GlobalizationUtil
                             .globalize("cw.workspace.ui.admin.props")
                             .localize(s.getRequest()) + " " + n);
                   }
                 }
               }
             });
 
            this.add(nameSelected);
            this.add(new Label(""));
 
            nameLabel = new Label(GlobalizationUtil.globalize("cw.workspace.ui.admin.portal_name"));
            nameLabel.setIdAttr("propnamelabel");

             name = new Label("portalname");
             name.setIdAttr("propname");

             name.addPrintListener(new PrintListener() {
               public void prepare(PrintEvent e) {
                 PageState s = e.getPageState();
                 if(m_portalTree.isSelected(s)) {
                   PortalSite psite = (PortalSite)m_selectedPortalRL.get(s);
                   if(!(psite == null)) {
                     String n = psite.getDisplayName();
                     Label t = (Label)e.getTarget();
                     t.setLabel(n);
                   }
                 }
               }
             });
 
            this.add(nameLabel);
            this.add(name);

            urlLabel = new Label(GlobalizationUtil
                         .globalize("cw.workspace.ui.admin.portal_url"));
            urlLabel.setIdAttr("propurllabel"); 

             url = new Label("portalurl");
             url.setIdAttr("propurl");
             url.addPrintListener(new PrintListener() {
               public void prepare(PrintEvent e) {
                 PageState s = e.getPageState();
                 if(m_portalTree.isSelected(s)) {
                   PortalSite psite = (PortalSite)m_selectedPortalRL.get(s);
                   if(!(psite == null)) {
                     String n = psite.getPath();
                     Label t = (Label)e.getTarget();
                     t.setLabel(n);
                   }
                 }
               }
             });
 
            this.add(urlLabel);
            this.add(url);

            descriptionLabel = new Label(GlobalizationUtil
                        .globalize("cw.workspace.ui.admin.portal_desc"));
            descriptionLabel.setIdAttr("propdescriptionlabel");

             description = new Label("portaldesc");
             description.setIdAttr("propdescription");
             description.addPrintListener(new PrintListener() {
               public void prepare(PrintEvent e) {
                 PageState s = e.getPageState();
                 if(m_portalTree.isSelected(s)) {
                   PortalSite psite = (PortalSite)m_selectedPortalRL.get(s);
                   if(!(psite == null)) {
                     String n = psite.getMission();
                     Label t = (Label)e.getTarget();
                     t.setLabel(n);
                   }
                 }
               }
             });
 
            this.add(descriptionLabel);
            this.add(description);

            creationDateLabel = new Label(GlobalizationUtil
                     .globalize("cw.workspace.ui.admin.portal_creation_date"));
            creationDateLabel.setIdAttr("propcreationdatelabel");

             creationDate = new Label("portalcreation");
             creationDate.setIdAttr("propcreationdate");
             creationDate.addPrintListener(new PrintListener() {
               public void prepare(PrintEvent e) {
                 PageState s = e.getPageState();
                 if(m_portalTree.isSelected(s)) {
                   PortalSite psite = (PortalSite)m_selectedPortalRL.get(s);
                   if(!(psite == null)) {
                     Date d = psite.getCreationDate();
                     SimpleDateFormat dft = new SimpleDateFormat();
                     String date;
                     //This null trap is for legacy portals
                     if(d == null)
                      date = "----";
                     else
                      date = dft.format(d);
                     Label t = (Label)e.getTarget();
                     t.setLabel(date);
                   }
                 }
               }
             });
 
            this.add(creationDateLabel);
            this.add(creationDate);

            statusLabel = new Label(GlobalizationUtil
                   .globalize("cw.workspace.ui.admin.portal_status"));
            statusLabel.setIdAttr("propstatuslabel");

             status = new Label("portalstatus");
             status.setIdAttr("propstatus");
             status.addPrintListener(new PrintListener() {
               public void prepare(PrintEvent e) {
                 PageState s = e.getPageState();
                   if(m_portalTree.isSelected(s)) {
                     PortalSite psite = (PortalSite)m_selectedPortalRL.get(s);
                     if(!(psite == null)) {
                       Label t = (Label)e.getTarget();
                       if(psite.isDraft()) {
                         t.setLabel(GlobalizationUtil
                           .globalize("cw.workspace.ui.admin.is_draft"));
                       } else {
                         if(psite.isArchived()) {
                           Date d = psite.getArchiveDate();
                           SimpleDateFormat dft = new SimpleDateFormat();
                           t.setLabel(GlobalizationUtil
                             .globalize("cw.workspace.ui.admin.is_archived")
                             .localize(s.getRequest())  + 
                             " " + dft.format(d));
                         } else {
                           t.setLabel(GlobalizationUtil
                             .globalize("cw.workspace.ui.admin.is_online"));
                         }
                       }
                       
                   }
                 }
               }
             });
 
            this.add(statusLabel);
            this.add(status);

            editlink = new ActionLink("Edit These Properties");
            editlink.setIdAttr("propedittheseprops");
            editlink.addActionListener( new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                  PageState s = e.getPageState();
                  editpropForm.setVisible(s, true); 
                  propForm.setVisible(s, false);
                  linkPanel.setVisible(s, false);
               }
            });

            this.add(editlink);  

        }
        
    }

    public class EditPropertiesForm extends Form implements FormProcessListener {
   
        private Label heading;

        private Label nameLabel;
        private TextField name;

        private Label descriptionLabel;
        private TextArea description;

        private Submit update; 
        private Submit cancel;

        EditPropertiesForm(GridPanel gp) {
            super("editpropsform", gp);

        heading = new Label("Editable Properties");
        heading.setIdAttr("editpropnameselected");
        heading.addPrintListener(new PrintListener() {
          public void prepare(PrintEvent e) {
            PageState s = e.getPageState();
            if(m_portalTree.isSelected(s)) {
              PortalSite psite = (PortalSite)m_selectedPortalRL.get(s);
              if(!(psite == null)) {
                String n = psite.getDisplayName();
                Label t = (Label)e.getTarget();
                t.setLabel(GlobalizationUtil
                             .globalize("cw.workspace.ui.admin.edit_props")
                             .localize(s.getRequest()) + " " + n);
              }
            }
          }
        });

        this.add(heading);

            nameLabel = 
                new Label(GlobalizationUtil.
                      globalize("cw.workspace.ui.admin.portal_name"));
            nameLabel.setIdAttr("editpropnamelabel");
        
             name = new TextField("portalname");
             name.setIdAttr("editpropname");
             name.addValidationListener(
               new NotNullValidationListener(GlobalizationUtil
                      .globalize("cw.workspace.ui.admin.need_portal_name")));

             try {
             name.addPrintListener(new PrintListener() {
               public void prepare(PrintEvent e) {
                 PageState s = e.getPageState();
                 if(m_portalTree.isSelected(s)) {
                   PortalSite psite = (PortalSite)m_selectedPortalRL.get(s);
                   if(!(psite == null)) {
                     String n = psite.getDisplayName();
                     TextField tf = (TextField)e.getTarget();
                     tf.setValue(s, n);
                   }
                 }
               }
             });
             } catch(java.util.TooManyListenersException e) { }

            this.add(nameLabel);
            this.add(name);

            descriptionLabel = new Label(GlobalizationUtil
                          .globalize("cw.workspace.ui.admin.portal_desc"));
            descriptionLabel.setIdAttr("editpropdescriptionlabel"); 

             description = new TextArea("portaldesc");
             description.setIdAttr("editpropdescription"); 
             try {
             description.addPrintListener(new PrintListener() {
               public void prepare(PrintEvent e) {
                 PageState s = e.getPageState();
                 if(m_portalTree.isSelected(s)) {
                   PortalSite psite = (PortalSite)m_selectedPortalRL.get(s);
                   if(!(psite == null)) {
                     String n = psite.getMission();
                     TextArea ta = (TextArea)e.getTarget();
                     ta.setValue(s, n);
                   }
                 }
               }
             });
             } catch(java.util.TooManyListenersException e) { }

            this.add(descriptionLabel);
            this.add(description);

            update = new Submit(GlobalizationUtil
                  .globalize("cw.workspace.ui.admin.update"));
            update.setIdAttr("editproptextinput");

            cancel = new Submit(GlobalizationUtil
                  .globalize("cw.workspace.ui.admin.cancel"));
            cancel.setIdAttr("editproptextcancel");

            add(update);
            add(cancel);
            addProcessListener(this);


    }

            public void process(FormSectionEvent e) {
            PageState s = e.getPageState();

            if(update.isSelected(s)) {
              PortalSite psite = (PortalSite)m_selectedPortalRL.get(s);
              if(!(psite == null)) {
                //Set props, then save
                String pname = (String)name.getValue(s);
                psite.setTitle(pname);
                String pdesc = (String)description.getValue(s);
                psite.setMission(pdesc);
                psite.save();
              }
            } 
            clearAllVisibility(s);
            propForm.setVisible(s, true);
            linkPanel.setVisible(s, true);
            vmdPanel.setVisible(s, true);
        }
    }
  

    public class DeleteForm extends Form implements FormProcessListener {
        private Label instruction3;
        private Label instruction1;
        private Label deleteTableHeader1;
        private Label deleteTableHeader2;
        private Label deleteTableHeader3;
        private Submit button;
        private Submit cancelbutton;
        PortalSite psite;
        PageState s;
        CheckboxGroup checkGroup;
        List list;
        ArrayParameter params;
        private final static String APP_LIST = "delete-form-app-choices";
    
        public DeleteForm(GridPanel gp) {
            super("deleteform", gp);

            params = new ArrayParameter(new BigDecimalParameter(APP_LIST));
            checkGroup = new CheckboxGroup(APP_LIST);

            list = new List(new ListModelBuilder() {
                   public ListModel makeModel(List l, PageState s) {
                       PortalSite psite = (PortalSite)m_selectedPortalRL.get(s);
                       return new AppListModel(psite);
                   }
                   public void lock() {}
                   public boolean isLocked() {return true; }
            }); 
            list.setIdAttr("deleteappslist");
            list.setCellRenderer(new ListCellRenderer() {
                public Component getComponent(List l, PageState s, Object value,
                    String key, int index, boolean isSelected) {
                     
                     SimpleContainer container = new SimpleContainer();
                     container.setIdAttr("deleteapplist");
                     Label type = null;
                     Option newOption = new Option(key, "");
                     newOption.setGroup(checkGroup);
                     Label name = new Label((String)value);
                     name.setIdAttr("deleteappname");
                     Application app = Application.retrieveApplication(new
                                                             BigDecimal(key));
                     if(!(app == null)) {
                       type = new Label(app.getApplicationType().getTitle());
                       type.setIdAttr("deleteapptype");
                     }
                     container.add(newOption); 
                     container.add(name);
                     if(type != null)
                        container.add(type);
            
                     return container;
                }
             }); 

            deleteTableHeader1 = 
              new Label(GlobalizationUtil.globalize
                     ("cw.workspace.ui.admin.delete_table_header1"));
            deleteTableHeader1.setIdAttr("deletetableheader1");

            deleteTableHeader2 = 
              new Label(GlobalizationUtil.globalize
                     ("cw.workspace.ui.admin.delete_table_header2"));
            deleteTableHeader2.setIdAttr("deletetableheader2");

            deleteTableHeader3 = 
              new Label(GlobalizationUtil.globalize
                     ("cw.workspace.ui.admin.delete_table_header3"));
            deleteTableHeader3.setIdAttr("deletetableheader3");

            instruction1 = 
              new Label(GlobalizationUtil.globalize
                     ("cw.workspace.ui.admin.delete_instruction1"));
            instruction1.setIdAttr("delete_instruction1");

            instruction3 = 
              new Label(GlobalizationUtil.globalize
                     ("cw.workspace.ui.admin.delete_this_portal"));
            instruction3.setIdAttr("delete_instruction3");

            button = 
              new Submit(GlobalizationUtil.globalize
                     ("cw.workspace.ui.admin.delete"));
            button.setIdAttr("deleteformbutton");  
            cancelbutton = 
              new Submit(GlobalizationUtil
                             .globalize("cw.workspace.ui.admin.cancel"));
            cancelbutton.setIdAttr("deleteformcancelbutton");

            this.add(instruction1);
            this.add(instruction3);
            this.add(deleteTableHeader1);
            this.add(deleteTableHeader2);
            this.add(deleteTableHeader3);
            this.add(new Label(""));
            this.add(button);
            this.add(cancelbutton);
            this.add(checkGroup);
            this.add(list);
            this.addProcessListener(this);
        }

        public void register(Page p) {
            super.register(p);
            p.addComponentStateParam(this, params);
        }

        public void process(FormSectionEvent e) {
            s = e.getPageState();

            if(button.isSelected(s)) {
              KernelExcursion ex = new KernelExcursion() {
                  protected void excurse() {
                    Application deadApp;
                    //This string array contains IDs of apps user wishes deleted
                    String[] selectedApps = 
                      (String[])checkGroup.getValue(s);
                        if(selectedApps != null) {
                          for( int i = 0; i < selectedApps.length; i++) {
                            deadApp = 
                              Application.retrieveApplication(new BigDecimal
                                  (selectedApps[i]));
                            deadApp.delete(); 
                          }
                        }
                      psite = (PortalSite)m_selectedPortalRL.get(s);
                      setEffectiveParty(Kernel.getSystemParty());
                      psite.delete();
                  }
              };
              ex.run();

              clearAllVisibility(s);
              m_portalTree.clearSelection(s);
              this.setVisible(s, false);
              m_noPortalSelected.setVisible(s, true);
              linkPanel.setVisible(s, true);
            } else {
              clearAllVisibility(s);
              this.setVisible(s, false);
              propForm.setVisible(s, true);
              linkPanel.setVisible(s, true);
              vmdPanel.setVisible(s, true);
            }
        }
        //------------------------------------------------------------
        public class AppListModel implements ListModel {
           ApplicationCollection m_apps;
           Application m_app;
           public AppListModel(PortalSite psite) {
               m_apps = psite.getFullPagePortalSiteApplications();
           }
           public boolean next() {
               if (!m_apps.next()) {
                 return false;
               }
               m_app = m_apps.getApplication();
               return true;
           }
           public Object getElement() {
               return m_app.getTitle();
           }
           public String getKey() {
               return m_app.getID().toString();
           }
         }
      }
}
