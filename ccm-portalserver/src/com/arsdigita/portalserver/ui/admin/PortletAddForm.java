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
import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.CompoundComponent;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.form.RadioGroup;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.KernelExcursion;
import com.arsdigita.kernel.Party;
import com.arsdigita.kernel.permissions.PermissionDescriptor;
import com.arsdigita.kernel.permissions.PermissionService;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
import com.arsdigita.kernel.Resource;
import com.arsdigita.kernel.ui.ResourceConfigFormSection;
import com.arsdigita.kernel.ResourceType;
import com.arsdigita.web.Application;
import com.arsdigita.web.ApplicationCollection;
import com.arsdigita.web.ApplicationType;
import com.arsdigita.portalserver.PortalSite;
import com.arsdigita.portalserver.PortalPage;
import com.arsdigita.portalserver.PortalTab;
import com.arsdigita.portal.Portlet;
import com.arsdigita.portal.PortletType;
import com.arsdigita.xml.Element;
import java.math.BigDecimal;
import org.apache.log4j.Logger;

class PortletAddForm extends CompoundComponent {
    private static final Logger s_log = Logger.getLogger(PortletAddForm.class);

    private static final String LEFT_COLUMN = "left";
    private static final String RIGHT_COLUMN = "right";

    private PortalPage m_portalPage;

    private RequestLocal m_portalsiteRL;
    private RequestLocal m_portalIDRL;

    private RequestLocal m_portletTypeRL;

    private RequestLocal m_appTypeRL = new RequestLocal() {
            protected Object initialValue(PageState ps) {
                PortletType portletType = (PortletType) m_portletTypeRL.get(ps);
                if (portletType == null) { return null; }
                else {
                    return portletType.getProviderApplicationType();
                }
            }
        };

    private final BigDecimalParameter m_parentAppParam =
        new BigDecimalParameter("appID");

    private RequestLocal m_parentAppRL = new RequestLocal() {
            protected Object initialValue(PageState ps) {
                BigDecimal id = (BigDecimal) ps.getValue(m_parentAppParam);
                if (id == null) { return null; }
                return Application.retrieveApplication(id);
            }
        };

    private final StringParameter m_columnParam = new StringParameter("colID");

    private Form m_portletTypeForm;
    private Form m_appSelect;
    private Form m_newExistingSelect;
    private Form m_existingSelect;
    private ApplicationCreateComponent m_appCreate;
    private ApplicationCreateComponent m_portletCreate;

    public PortletAddForm(RequestLocal portalsiteRL,
                          RequestLocal portalIDRL,
                          RequestLocal portletTypeRL) {
        super();
        m_portalsiteRL = portalsiteRL;
        m_portalIDRL = portalIDRL;
        m_portletTypeRL = portletTypeRL;

        final RequestLocal createNewRL = new RequestLocal();
        m_newExistingSelect = buildNewExistingSelect(
           m_appTypeRL, m_portalsiteRL, createNewRL,new ActionListener() {
               public void actionPerformed(ActionEvent ev) {
                  PageState ps = ev.getPageState();
                  if (createNewRL.get(ps).equals(Boolean.TRUE)) {
                     m_portalPage.goModal(ps, m_appCreate);
                  } else {
                     m_portalPage.goModal(ps, m_appSelect);
                  }
               }
           },
           new ActionListener() {
              public void actionPerformed(ActionEvent ev) {
                PageState ps = ev.getPageState();
                PortletAddForm.this.fireCompletionEvent(ps);
              }
           });
        add(m_newExistingSelect);

        final RequestLocal makeLocalRL = new RequestLocal();
        m_existingSelect = buildExistingSelect(
          m_appTypeRL, m_portalsiteRL, makeLocalRL, new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
              PageState ps = ev.getPageState();
              if (makeLocalRL.get(ps).equals(Boolean.TRUE)) {
              //Assign new portlet to this
              PortletType ptype = (PortletType) m_portletTypeRL.get(ps);
              PortalSite psite = (PortalSite) m_portalsiteRL.get(ps);
              ApplicationCollection apps = 
                   psite.getFullPagePortalSiteApplications();
              ApplicationType appType = (ApplicationType) m_appTypeRL.get(ps);
              ApplicationType atp;
              Application app = null;
              boolean wasFound = false;
              while (apps.next()) {
                app = apps.getApplication();
                atp = app.getApplicationType();
                if(atp.getID().equals(appType.getID()))
                {
                  apps.close();
                  wasFound = true;
                  break;
                }
              }
              if(wasFound) {
                Portlet portlet= Portlet.createPortlet(ptype, app);
                addToTab(ps,portlet);
                fireCompletionEvent(ps);
              }
             //throw an exception here for 'else'
              } else {
                m_portalPage.goModal(ps, m_appSelect);
              }
           }
        }, new ActionListener() {
             public void actionPerformed(ActionEvent ev) {
               PageState ps = ev.getPageState();
               PortletAddForm.this.fireCompletionEvent(ps);
             }
        });

        add(m_existingSelect);

        final RequestLocal selectedAppRL = new RequestLocal();
        m_appSelect = 
           buildAppSelect(m_appTypeRL, m_portalsiteRL, selectedAppRL,
             new ActionListener() {
               public void actionPerformed(ActionEvent ev) {
                 PageState ps = ev.getPageState();
                 Application app = (Application) selectedAppRL.get(ps);
                 if (app == null) {
                    m_portalPage.goModal(ps, m_appCreate);
                 } else {
                    PortletType ptype = (PortletType)m_portletTypeRL.get(ps);
                    if (m_portletCreate.canCreate(ptype)) {
                        ps.setValue(m_parentAppParam, app.getID());
                        m_portalPage.goModal(ps, m_portletCreate);
                     } else {
                         Portlet portlet = Portlet.createPortlet(ptype, app);
                         addToTab(ps, portlet);
                         PortletAddForm.this.fireCompletionEvent(ps);
                     }
                }
           }
        }, new ActionListener() {
             public void actionPerformed(ActionEvent ev) {
               PageState ps = ev.getPageState();
               m_portalPage.goModal(ps, m_newExistingSelect);
             }
        }, new ActionListener() {
             public void actionPerformed(ActionEvent ev) {
               PageState ps = ev.getPageState();
               PortletAddForm.this.fireCompletionEvent(ps);
             }
        });
        add(m_appSelect);

        // holds the application from app creation
        final RequestLocal createdAppRL = new RequestLocal();
        m_appCreate = 
          buildAppCreate(m_appTypeRL, m_portalsiteRL, createdAppRL,
            new ActionListener() {
              public void actionPerformed(ActionEvent ev) {
                PageState ps = ev.getPageState();
                Application app = (Application) createdAppRL.get(ps);
                app.save();
                PortletType ptype = (PortletType) m_portletTypeRL.get(ps);
               if (m_portletCreate.canCreate(ptype)) {
                   ps.setValue(m_parentAppParam, app.getID());
                   m_portalPage.goModal(ps, m_portletCreate);
               } else {
               Portlet portlet = Portlet.createPortlet(ptype, app);
               addToTab(ps, portlet);
                 PortletAddForm.this.fireCompletionEvent(ps);
               }
             }
         }, new ActionListener() { 
             public void actionPerformed(ActionEvent ev) {
               PageState ps = ev.getPageState();
               // XXX: implement back
               m_portalPage.goModal(ps, m_newExistingSelect);
             }
         }, new ActionListener() {
               public void actionPerformed(ActionEvent ev) {
                 PageState ps = ev.getPageState();
                 PortletAddForm.this.fireCompletionEvent(ps);
               }
            }
       );

        add(m_appCreate);

        final RequestLocal portletRL = new RequestLocal();

        m_portletCreate = buildPortletCreate
            (m_portletTypeRL, m_parentAppRL, portletRL,
             new ActionListener() {
                 public void actionPerformed(ActionEvent ev) {
                     PageState ps = ev.getPageState();
                     Portlet portlet = (Portlet) portletRL.get(ps);
                     addToTab(ps, portlet);
                     PortletAddForm.this.fireCompletionEvent(ps);
                 }
             }, new ActionListener() {
                     public void actionPerformed(ActionEvent ev) {
                         PageState ps = ev.getPageState();
                         PortletAddForm.this.fireCompletionEvent(ps);
                     }
                 }
             );

        add(m_portletCreate);
    }

    protected void fireCompletionEvent(PageState ps) {
        ps.reset(this);
        m_portalPage.goUnmodal(ps);
        super.fireCompletionEvent(ps);
    }

    private static final ApplicationCollection getApplicationChoices
            (ApplicationType type) {
        ApplicationCollection ac =
            Application.retrieveAllApplications();
        ac.filterToApplicationType(type.getApplicationObjectType());
        ac.filterToHasFullPageView();
        ac.filterToWorkspaceApplications();
        return ac;
    }

    public void activate(PageState ps) {
        ApplicationType appType = (ApplicationType) m_appTypeRL.get(ps);
        PortalSite psite = (PortalSite) m_portalsiteRL.get(ps);

        if (appType == null) {
            PortletType ptype = (PortletType) m_portletTypeRL.get(ps);

            ApplicationType atp = ptype.getProviderApplicationType();
            ApplicationType applicationType;

            if ( atp != null && atp.isSingleton()) {
                //1 - get collection of apps for portal
                ApplicationCollection apps = psite.getFullPagePortalSiteApplications();
                boolean wasFound = false;

                //2 - iterate through collection and compare with atp
                //If in collection, just create and add portlet.
                //If not in collection, create app then create portlet
                while(apps.next()) {
                    applicationType = apps.getApplication().getApplicationType();
                    if(applicationType.getID().equals(atp.getID())) {
                        wasFound = true;
                        Portlet portlet = Portlet.createPortlet(ptype, apps.getApplication());
                        addToTab(ps, portlet);
                        fireCompletionEvent(ps);
                        break;
                    }
                }
                apps.close();

                if (!wasFound) {
                    Application newApp = Application.createApplication
                        (atp, atp.getID().toString(), atp.getTitle(), psite);
                    Portlet portlet = Portlet.createPortlet(ptype, newApp);
                    addToTab(ps, portlet);
                    fireCompletionEvent(ps);
                }
            } else if (m_portletCreate.canCreate(ptype)) {
                ps.setValue(m_parentAppParam, psite.getID());
                m_portalPage.goModal(ps, m_portletCreate);
            } else {
                Portlet portlet = Portlet.createPortlet(ptype, psite);
                addToTab(ps, portlet);
                fireCompletionEvent(ps);
            }

            return;
        }

        //The line below retrieves all app choices in the system
        //of type appType. If an app is not a singleton, then
        //we can let the if/else code below run. If the app
        //*is* a singleton, we need to check  if it is
        //already installed in this portal site. If so, then
        //offer user a choice of existing selections only
        //if not, then offer them the typical newExisting select.
        if(appType.isSingleton())
            {
                if (psite.isAppTypeInPortalSite(appType))
                    {
                        m_portalPage.goModal(ps,m_existingSelect);
                    }
                else
                    {
                        m_portalPage.goModal(ps,m_appCreate);
                    }
            }
        else
            {
                ApplicationCollection ac = getApplicationChoices(appType);
                if (ac.size() == 0) {
                    m_portalPage.goModal(ps, m_appCreate);
                    // XXX: need to handle configuration less applications
                    // throw new IllegalStateException("can't create");
                } else {
                    m_portalPage.goModal(ps, m_newExistingSelect);
                }
            }
    }

    public void register(Page p) {
        super.register(p);
        m_portalPage = (PortalPage) p;
        p.addComponentStateParam(this, m_columnParam);
        p.addComponentStateParam(this, m_parentAppParam);
    }

    private static Form buildNewExistingSelect(final RequestLocal appTypeRL,
                                               final RequestLocal portalsiteRL,
                                               final RequestLocal createNew,
                                               final ActionListener onNext,
                                               final ActionListener onCancel) {
        final Form f = new Form("newexist", new BoxPanel(BoxPanel.VERTICAL)) {
                public void generateXML(PageState ps, Element parent) {
                    // force init listeners to run
                    getFormData(ps);
                    super.generateXML(ps, parent);
                }
            };

        final Label info = new Label(GlobalizationUtil.globalize("cw.workspace.ui.admin.choose_new_or_existing"));
        f.add(info);

        final RadioGroup rg = new RadioGroup("select");
        rg.setLayout(RadioGroup.VERTICAL);
        final Label newLabel = new Label(GlobalizationUtil.globalize("cw.workspace.ui.admin.create_new"),  false);
        final Label existingLabel = new Label(GlobalizationUtil.globalize("cw.workspace.ui.admin.pick_existing"),  false);
        rg.addOption(new Option("create", newLabel));
        rg.addOption(new Option("choose", existingLabel));
        rg.setOptionSelected("create");
        f.add(rg);

        BoxPanel buttons = new BoxPanel(BoxPanel.HORIZONTAL);
        final Submit next = new Submit("Next >>");
        final Submit cancel = new Submit("Cancel");
        buttons.add(next);
        buttons.add(cancel);
        f.add(buttons);

        f.addInitListener(new FormInitListener() {
                public void init(FormSectionEvent ev) {
                    PageState ps = ev.getPageState();
                    ApplicationType type = (ApplicationType) appTypeRL.get(ps);
                    String title = type.getTitle();
                    title = title.toLowerCase();

                    info.setLabel("You can choose which " + title
                                  + " to display in the new portlet.", ps);

                    newLabel.setLabel("I want to <b>create a new " + title
                                      + "</b> to display in the portlet.", ps);
                    existingLabel.setLabel(
                                           "I want to <b>choose an existing " + title
                                           + "</b> to display in the portlet.", ps);
                }
            });

        f.addProcessListener(new FormProcessListener() {
                public void process(FormSectionEvent ev) {
                    PageState ps = ev.getPageState();
                    if (cancel.isSelected(ps)) {
                        onCancel.actionPerformed(new ActionEvent(f, ps));
                        return;
                    }

                    String value = (String) rg.getValue(ps);
                    if ("create".equals(value)) {
                        createNew.set(ps, Boolean.TRUE);
                    } else if ("choose".equals(value)) {
                        createNew.set(ps, Boolean.FALSE);
                    } else {
                        throw new IllegalStateException("invalid selection");
                    }
                    onNext.actionPerformed(new ActionEvent(f, ps));
                }
            });

        f.addSubmissionListener(new FormSubmissionListener() {
                public void submitted(FormSectionEvent ev) {
                   PageState ps = ev.getPageState();
                   if (cancel.isSelected(ps)) {
                       onCancel.actionPerformed(new ActionEvent(f, ps));
                       return;
                   }
                }
            });

        return f;
    }

    private static Form buildExistingSelect(final RequestLocal appTypeRL,
                                            final RequestLocal portalsiteRL,
                                            final RequestLocal makeLocal,
                                            final ActionListener onNext,
                                            final ActionListener onCancel) {
        final Form f = new Form("newexist", new BoxPanel(BoxPanel.VERTICAL)) {
                public void generateXML(PageState ps, Element parent) {
                    // force init listeners to run
                    getFormData(ps);
                    super.generateXML(ps, parent);
                }
            };

        final Label info = new Label(GlobalizationUtil.globalize("cw.workspace.ui.admin.choose_new_or_existing"));
        f.add(info);

        final RadioGroup rg = new RadioGroup("select");
        rg.setLayout(RadioGroup.VERTICAL);
        final Label newLabel = new Label(GlobalizationUtil.globalize("cw.workspace.ui.admin.create_new"),  false);
        final Label existingLabel = new Label(GlobalizationUtil.globalize("cw.workspace.ui.admin.pick_existing"),  false);
        rg.addOption(new Option("local", newLabel));
        rg.addOption(new Option("external", existingLabel));
        rg.setOptionSelected("local");
        f.add(rg);

        BoxPanel buttons = new BoxPanel(BoxPanel.HORIZONTAL);
        final Submit next = new Submit("Next >>");
        final Submit cancel = new Submit("Cancel");
        buttons.add(next);
        buttons.add(cancel);
        f.add(buttons);

        f.addInitListener(new FormInitListener() {
                public void init(FormSectionEvent ev) {
                    PageState ps = ev.getPageState();
                    ApplicationType type = (ApplicationType) appTypeRL.get(ps);
                    String title = type.getTitle();
                    title = title.toLowerCase();

                    info.setLabel("You can choose which " + title
                                  + " to display in the new portlet.", ps);

                    newLabel.setLabel("I want to display the "+ title
                                      + " for this portal in the portlet.", ps);
                    existingLabel.setLabel(
                                           "I want to choose a " + title
                                           + " from another portal to display in the portlet.", ps);
                }
            });

        f.addProcessListener(new FormProcessListener() {
                public void process(FormSectionEvent ev) {
                    PageState ps = ev.getPageState();
                    if (cancel.isSelected(ps)) {
                        onCancel.actionPerformed(new ActionEvent(f, ps));
                        return;
                    }

                    String value = (String) rg.getValue(ps);
                    if ("local".equals(value)) {
                        makeLocal.set(ps, Boolean.TRUE);
                    } else if ("external".equals(value)) {
                        makeLocal.set(ps, Boolean.FALSE);
                    } else {
                        throw new IllegalStateException("invalid selection");
                    }
                    onNext.actionPerformed(new ActionEvent(f, ps));
                }
            });

        f.addSubmissionListener(new FormSubmissionListener() {
                public void submitted(FormSectionEvent ev) {
                   PageState ps = ev.getPageState();
                   if (cancel.isSelected(ps)) {
                       onCancel.actionPerformed(new ActionEvent(f, ps));
                       return;
                   }
                }
            });

        return f;
    }

    private static Form buildAppSelect(final RequestLocal appType,
                                       final RequestLocal portalsiteRL,
                                       final RequestLocal selectedApp,
                                       final ActionListener onNext,
                                       final ActionListener onBack,
                                       final ActionListener onCancel) {
        final Form f = new Form("appselect", new BoxPanel(BoxPanel.VERTICAL)) {
                public void generateXML(PageState ps, Element parent) {
                    // force init listeners to run
                    getFormData(ps);
                    super.generateXML(ps, parent);
                }
            };

        SimpleContainer header = new SimpleContainer();
        Label header1 = new Label(GlobalizationUtil.globalize("cw.workspace.ui.admin.you_have_opted_to_display_an_existing"));
        final Label header2 = new Label(GlobalizationUtil.globalize("cw.workspace.ui.admin.apptype"));
        Label header3 = new Label(GlobalizationUtil.globalize("cw.workspace.ui.admin.in_the_new_portlet"));
        header.add(header1);
        header.add(header2);
        header.add(header3);
        f.add(header);

        SimpleContainer choose = new SimpleContainer();
        Label choose1 = new Label(GlobalizationUtil.globalize("cw.workspace.ui.admin.choose_one_of_the"));
        final Label choose2 = new Label(GlobalizationUtil.globalize("cw.workspace.ui.admin.apptype"));
        Label choose3 = new Label(GlobalizationUtil.globalize("cw.workspace.ui.admin.already_in_this_workspace"));
        choose.add(choose1);
        choose.add(choose2);
        choose.add(choose3);
        f.add(choose);

        final RadioGroup rg = new RadioGroup("appselect");
        rg.setLayout(RadioGroup.VERTICAL);
        f.add(rg);

        BoxPanel buttons = new BoxPanel(BoxPanel.HORIZONTAL);
        final Submit next = new Submit("Next >>");
        buttons.add(next);
        final Submit back = new Submit("Back <<");
        buttons.add(back);
        final Submit cancel = new Submit("Cancel");
        buttons.add(cancel);
        f.add(buttons);

        f.addInitListener(new FormInitListener() {
                public void init(FormSectionEvent ev) {
                    PageState ps = ev.getPageState();

                    ApplicationType type = (ApplicationType) appType.get(ps);

                    String title = type.getTitle();
                    title = title.toLowerCase();

                    header2.setLabel(title, ps);
                    // XXX: need plural
                    choose2.setLabel(title, ps);

                    ApplicationCollection ac = getApplicationChoices(type);
                    ac.orderByParentTitle();
                    ac.orderByTitle();
                    while (ac.next()) {
                        Label l = new Label(ac.getTitle() + " ( " +
                                            ac.getParentTitle() + " ) ");
                        l.setFontWeight(Label.BOLD);
                        rg.addOption(new Option(ac.getID().toString(), l), ps);
                    }
                }
            });

        f.addProcessListener(new FormProcessListener() {
                public void process(FormSectionEvent ev) {
                    PageState ps = ev.getPageState();
                    String idStr = (String) rg.getValue(ps);
                    if (cancel.isSelected(ps)) {
                        onCancel.actionPerformed(new ActionEvent(f, ps));
                        return;
                    } else if (back.isSelected(ps)) {
                        onBack.actionPerformed(new ActionEvent(f, ps));
                        return;
                    }

                    BigDecimal id;
                    try {
                        id = new BigDecimal(idStr);
                        selectedApp.set(
                                        ps, Application.retrieveApplication(id));
                        onNext.actionPerformed(new ActionEvent(f, ps));
                    } catch (NumberFormatException nfe) {
                        throw new IllegalStateException("bad id");
                    }
                }
            });

        f.addSubmissionListener(new FormSubmissionListener() {
                public void submitted(FormSectionEvent ev) {
                   PageState ps = ev.getPageState();
                   if (cancel.isSelected(ps)) {
                       onCancel.actionPerformed(new ActionEvent(f, ps));
                       return;
                   }
                }
            });

        return f;
    }

    private static ApplicationCreateComponent buildAppCreate
        (final RequestLocal appTypeRL, final RequestLocal portalsiteRL,
         final RequestLocal createdAppRL, final ActionListener onNext,
         final ActionListener onBack, final ActionListener onCancel) {

        return new ApplicationCreateComponent
            (appTypeRL, portalsiteRL, false,
             new ApplicationCreateComponent.Builder() {
                 public Component build
                     (final ResourceConfigFormSection acfs) {
                     final Form f = new Form
                         ("appcreate", new BoxPanel(BoxPanel.VERTICAL)) {
                             public void generateXML
                                 (PageState ps, Element parent) {
                                 // force init listeners to run
                                 getFormData(ps);
                                 super.generateXML(ps, parent);
                             }
                         };

                     SimpleContainer header = new SimpleContainer();
                     Label header1 = new Label(GlobalizationUtil.globalize("cw.workspace.ui.admin.you_chose_to_create_a_new"));
                     final Label header2 = new Label(GlobalizationUtil.globalize("cw.workspace.ui.admin.apptype"));
                     Label header3 = new Label(GlobalizationUtil.globalize("cw.workspace.ui.admin.in_the_new_portlet"));
                     header.add(header1);
                     header.add(header2);
                     header.add(header3);
                     f.add(header);

                     f.add(acfs);

                     BoxPanel buttons = new BoxPanel(BoxPanel.HORIZONTAL);
                     final Submit next = new Submit("Next >>");
                     buttons.add(next);
                     final Submit back = new Submit("Back <<");
                     buttons.add(back);
                     final Submit cancel = new Submit("Cancel");
                     buttons.add(cancel);
                     f.add(buttons);

                     f.addInitListener(new FormInitListener() {
                             public void init(FormSectionEvent ev) {
                                 PageState ps = ev.getPageState();
                                 ApplicationType type = (ApplicationType)
                                     appTypeRL.get(ps);
                                 String title = type.getTitle();
                                 title = title.toLowerCase();
                                 header2.setLabel(title, ps);
                             }
                         });

                     f.addProcessListener(new FormProcessListener() {
                             public void process(FormSectionEvent ev)
                                     throws FormProcessException {
                                 PageState ps = ev.getPageState();

                                 if (next.isSelected(ps)) {


                                     if (s_log.isDebugEnabled()) {
                                         s_log.debug
                                                 ("Using application config " +
                                                 "form section " + acfs +
                                                 " to create an application");
                                     }

                                     final Application app =(Application) acfs.createResource(ps);
                                     createdAppRL.set(ps, app);
                                     onNext.actionPerformed(new ActionEvent(f, ps));
                                     grantWrite(app);
                                 } else if (back.isSelected(ps)) {
                                     onBack.actionPerformed(new ActionEvent(f, ps));
                                 } else if (cancel.isSelected(ps)) {
                                     onCancel.actionPerformed(new ActionEvent(f, ps));
                                 }
                             }
                         });

        f.addSubmissionListener(new FormSubmissionListener() {
                public void submitted(FormSectionEvent ev) {
                   PageState ps = ev.getPageState();
                   if (cancel.isSelected(ps)) {
                       onCancel.actionPerformed(new ActionEvent(f, ps));
                       return;
                   }
                }
            });
                     return f;
                 }
             });
    }

    private static ApplicationCreateComponent buildPortletCreate
        (final RequestLocal portletTypeRL, final RequestLocal parentAppRL,
         final RequestLocal portletRL, final ActionListener onNext,
         final ActionListener onCancel) {
        return new ApplicationCreateComponent
            (portletTypeRL, parentAppRL, true,
             new ApplicationCreateComponent.Builder() {
                 public Component build
                         (final ResourceConfigFormSection acfs) {
                     final Form f = new Form
                         ("portcreat", new BoxPanel(BoxPanel.VERTICAL)) {
                             public void generateXML(PageState ps, Element p) {
                                 // force init listeners to run
                                 getFormData(ps);
                                 super.generateXML(ps, p);
                             }
                         };

                     SimpleContainer header = new SimpleContainer();
                     Label header1 = new Label
                         ("Choose display options for the new ");
                     final Label header2 = new Label(GlobalizationUtil.globalize("cw.workspace.ui.admin.apptype"));
                     final Label header2a = new Label(" "); //sep char
                     Label header3 = new Label(GlobalizationUtil.globalize("cw.workspace.ui.admin.portlet"));
                     header.add(header1);
                     header.add(header2);
                     header.add(header2a);
                     header.add(header3);
                     f.add(header);

                     f.add(acfs);

                     BoxPanel buttons = new BoxPanel(BoxPanel.HORIZONTAL);
                     final Submit next = new Submit("Next >>");
                     buttons.add(next);
                     final Submit cancel = new Submit("Cancel");
                     buttons.add(cancel);
                     f.add(buttons);

                     f.addInitListener(new FormInitListener() {
                             public void init(FormSectionEvent ev) {
                                 PageState ps = ev.getPageState();
                                 ResourceType type = (ResourceType)
                                     portletTypeRL.get(ps);
                                 String title = type.getTitle();
                                 title = title.toLowerCase();
                                 header2.setLabel(title, ps);
                             }
                         });

                     f.addProcessListener(new FormProcessListener() {
                             public void process(FormSectionEvent ev) {
                                 PageState ps = ev.getPageState();

                                 if (s_log.isDebugEnabled()) {
                                     s_log.debug("Using application config " +
                                                 "form section " + acfs +
                                                 " to create a new portlet");
                                 }

                                 if (next.isSelected(ps)) {
                                     Resource app = (Resource) acfs.createResource(ps);

                                     portletRL.set(ps, app);

                                     onNext.actionPerformed(new ActionEvent(f, ps));

                                     grantWrite(app);
                                 } else if (cancel.isSelected(ps)) {
                                     onCancel.actionPerformed
                                         (new ActionEvent(f, ps));
                                 }
                             }
                         });

        f.addSubmissionListener(new FormSubmissionListener() {
                public void submitted(FormSectionEvent ev) {
                   PageState ps = ev.getPageState();
                   if (cancel.isSelected(ps)) {
                       onCancel.actionPerformed(new ActionEvent(f, ps));
                       return;
                   }
                }
            });

                     return f;
                 }
             });
    }

    private void addToTab(PageState ps, Portlet newPortlet) {
        BigDecimal ptabID = (BigDecimal)m_portalIDRL.get(ps);
        PortalTab ptab = PortalTab.retrieveTab(ptabID);

        String column = (String)ps.getValue(m_columnParam);
        if (column == null) {
            if (newPortlet.getProfile().equals(PortletType.WIDE_PROFILE)) {
                ptab.addPortlet(newPortlet, 2);
            } else {
                ptab.addPortlet(newPortlet, 1);
            }
        } else {
            if (LEFT_COLUMN.equals(ps.getValue(m_columnParam))) {
                ptab.addPortlet(newPortlet, 1);
            } else {
                ptab.addPortlet(newPortlet, 2);
            }
        }
        ptab.save();
        newPortlet.save();
    }

    private static final void grantWrite(final Resource app) {
        final Party current = Kernel.getContext().getParty();

        if (current == null || current.equals(Kernel.getSystemParty())) {
            return;
        }

        new KernelExcursion() {
            public void excurse() {
                setParty(Kernel.getSystemParty());

                app.save();

                PermissionService.grantPermission
                    (new PermissionDescriptor
                         (PrivilegeDescriptor.WRITE, app, current));
            }
        }.run();
    }
}
