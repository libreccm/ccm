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

import com.arsdigita.bebop.ActionLink;
import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.Container;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.PropertySheet;
import com.arsdigita.bebop.PropertySheetModel;
import com.arsdigita.bebop.PropertySheetModelBuilder;
import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.NotEmptyValidationListener;
import com.arsdigita.portalserver.PortalSite;
import org.apache.log4j.Logger;

public class BasicPane {
    public static final String versionId =
        "$Id: //portalserver/dev/src/com/arsdigita/portalserver/ui/admin/BasicPane.java#5 $" +
        "$Author: dennis $" +
        "$DateTime: 2004/08/17 23:19:25 $";

    private static final Logger s_log = Logger.getLogger(BasicPane.class);

    private BasicPane() { 
        // No construction allowed
    }

    public static Component create(final RequestLocal portalsite) {
        final Container view = view(portalsite);

        final Component edit = edit(view, portalsite, new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    PageState ps = e.getPageState();
                    view.setVisible(ps, true);
                }
            });

        final ActionLink editLink = new ActionLink( (String) GlobalizationUtil.globalize("cw.workspace.ui.admin.edit").localize());
        editLink.setClassAttr("actionLink");
        editLink.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    PageState ps = e.getPageState();
                    view.setVisible(ps, false);
                    edit.setVisible(ps, true);
                }
            });
        view.add(editLink);

        final ActionLink personalizeLink = new ActionLink( (String) GlobalizationUtil.globalize("cw.workspace.ui.admin.personalize").localize()) {
            public boolean isVisible(PageState s) {
                PortalSite psite = (PortalSite)portalsite.get(s);
                if(!psite.isPersonalizable())
                  return true;
                else
                  return false;
            }
         };
        personalizeLink.setClassAttr("actionLink");
        personalizeLink.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    PageState ps = e.getPageState();
                    PortalSite psite = (PortalSite)portalsite.get(ps);
                    psite.setPersonalizable(true);
                }
            });
        view.add(personalizeLink);

        final ActionLink unpersonalizeLink = new ActionLink( (String) GlobalizationUtil.globalize("cw.workspace.ui.admin.unpersonalize").localize()) {
            public boolean isVisible(PageState s) {
                PortalSite psite = (PortalSite)portalsite.get(s);
                if(psite.isPersonalizable())
                  return true;
                else
                  return false;
            }
         };
        unpersonalizeLink.setClassAttr("actionLink");
        unpersonalizeLink.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    PageState ps = e.getPageState();
                    PortalSite psite = (PortalSite)portalsite.get(ps);
                    psite.setPersonalizable(false);
                }
            });
        view.add(unpersonalizeLink);

        SimpleContainer retval = new SimpleContainer();

        retval.add(view);
        retval.add(edit);

        return retval;
    }

    private static Component edit(final Component view,
                                  final RequestLocal portalsite,
                                  final ActionListener onDone) {
        final Form editForm = new Form("editws") {
                public void register(Page p) {
                    super.register(p);
                    p.setVisibleDefault(this, false);
                }
            };
        editForm.setMethod(Form.POST);

        editForm.add(new Label(GlobalizationUtil.globalize("cw.workspace.ui.admin.title")));
        final TextField title = new TextField("title");
        title.setSize(40);
        title.getParameterModel().addParameterListener
            (new NotEmptyValidationListener());
        editForm.add(title);

        editForm.add(new Label(GlobalizationUtil.globalize("cw.workspace.ui.admin.mission")));
        final TextArea mission = new TextArea("mission");
        mission.setRows(10);
        mission.setCols(40);
        editForm.add(mission);

        final Submit cancel = new Submit("cancel", "Cancel");
        editForm.add(cancel);

        editForm.add(new Submit("done", "Submit"));

        editForm.addInitListener(new FormInitListener() {
                public void init(FormSectionEvent e) {
                    PageState ps = e.getPageState();
                    PortalSite psite = (PortalSite) portalsite.get(ps);
                    title.setValue(ps, psite.getTitle());
                    mission.setValue(ps, psite.getMission());
                }
            });

        editForm.addSubmissionListener(new FormSubmissionListener() {
                public void submitted(FormSectionEvent e)
                    throws FormProcessException {
                    PageState ps = e.getPageState();

                    if (cancel.isSelected(ps)) {
                        view.setVisible(ps, true);
                        editForm.setVisible(ps, false);
                        throw new FormProcessException("");
                    }
                }
            });

        editForm.addProcessListener(new FormProcessListener() {
                public void process(FormSectionEvent e) {
                    PageState ps = e.getPageState();
                    PortalSite psite = (PortalSite) portalsite.get(ps);
                    psite.setTitle((String) title.getValue(ps));
                    psite.setMission((String) mission.getValue(ps));
                    psite.save();
                    editForm.setVisible(ps, false);
                    onDone.actionPerformed(new ActionEvent(editForm, ps));
                }
            });

        return editForm;
    }

    private static Container view(final RequestLocal portalsite) {
        BoxPanel retval = new BoxPanel(BoxPanel.VERTICAL);
        Label header = new Label(GlobalizationUtil.globalize("cw.workspace.ui.admin.workspace_basic_properties"));
        header.setFontWeight(Label.BOLD);
        retval.add(header);

        retval.add(new PropertySheet(new PropertySheetModelBuilder() {
                public void lock() { }
                public boolean isLocked() { return true; }
                public PropertySheetModel makeModel(PropertySheet sheet,
                                                    final PageState ps) {
                 return new RelatedPortalsPane.PortalModel((PortalSite)portalsite.get(ps));
                }
            }));

        return retval;
    }

}
