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
package com.arsdigita.cms.contenttypes.ui.authoring;

import com.arsdigita.cms.contenttypes.MOTDItem;
import com.arsdigita.cms.contenttypes.util.MOTDGlobalizationUtil;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.mimetypes.MimeType;
import com.arsdigita.cms.FileAsset;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.dispatcher.Utilities;
import com.arsdigita.cms.ui.FileUploadSection;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.toolbox.ui.DomainObjectPropertySheet;
import com.arsdigita.cms.ui.authoring.BasicPageForm;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.web.URL;

import java.io.IOException;
import java.lang.RuntimeException;

/*
 * Edit page to attach a file to MOTDItem.
 *
 * @see com.arsdigita.cms.contenttypes.MOTDItem
 * @author Aingaran Pillai
 * @version $Revision: #7 $
 */
public class MOTDItemFileEdit extends SimpleEditStep {
    
    public MOTDItemFileEdit(ItemSelectionModel itemModel,
                        AuthoringKitWizard parent) {

        super(itemModel, parent);

        FileEditForm form = new FileEditForm(itemModel);

        add("edit", 
            "Edit",
            new WorkflowLockedComponentAccess(form, itemModel),
            form.getSaveCancelSection().getCancelButton());

        DomainObjectPropertySheet sheet = new DomainObjectPropertySheet(itemModel);
        sheet.add(GlobalizationUtil
                  .globalize("cms.contenttypes.ui.file_upload.file"),
                  "file.name",
                  new DomainObjectPropertySheet.AttributeFormatter() {
                      public String format(DomainObject item,
                                           String attribute,
                                           PageState state) {
                          MOTDItem motd = (MOTDItem) item;
                          FileAsset asset = motd.getFile();
                          if(asset != null) {
                              return "<a href=\"" +
                              URL.there(state.getRequest(),
                                        Utilities.getAssetURL(asset)).getRequestURI() +
                              "\" target=\"_blank\">" +
                              asset.getDisplayName() + "</a>";
                          } else {
                              return (String)GlobalizationUtil.globalize(
                                      "cms.ui.authoring.file_upload.no_file")
                                      .localize();
                          }
                      }
                  });

        
        setDisplayComponent(sheet);
        
    }

    private class FileEditForm extends BasicPageForm
        implements FormProcessListener {

        private FileUploadSection m_fileUploadSection;

        public FileEditForm(ItemSelectionModel itemModel) {

            // construct a BasicPageForm with nothing on it
            super("FileEdit", new ColumnPanel(2), itemModel);

            addWidgets();

            addProcessListener(this);
        }

        protected void addWidgets() {
            
            add(new Label(GlobalizationUtil
                          .globalize("cms.ui.authoring.file_upload.file")));
            add(new Label(new PrintListener() {
                    
                    public void prepare(PrintEvent e) {
                        PageState s = e.getPageState();
                        Label l = (Label) e.getTarget();

                        MOTDItem item = (MOTDItem)
                            getItemSelectionModel().getSelectedObject(s);
                        FileAsset file = item.getFile();
                            
                        if (file != null) {
                            l.setLabel(file.getName());
                        } else {
                            l.setLabel(GlobalizationUtil
                                       .globalize("cms.ui.none"));
                        }
                    }
                }));

            m_fileUploadSection = new 
                    FileUploadSection(GlobalizationUtil
                    .globalize("cms.ui.authoring.file_upload.file_type"),"","");
            m_fileUploadSection.getFileUploadWidget()
                .addValidationListener(new NotNullValidationListener());

            add(m_fileUploadSection);

            super.addSaveCancelSection();

        }

        public void init(FormSectionEvent e) throws FormProcessException {
            // do nothing here
        }

        public void process(FormSectionEvent e) throws FormProcessException {

            FormData data = e.getFormData();
            PageState s = e.getPageState();
            MOTDItem item = (MOTDItem) this.getItemSelectionModel()
                .getSelectedObject(s);

            if (item != null) {
        
                try {
                    FileAsset file = new FileAsset();
                    String fileName = m_fileUploadSection.getFileName(e);
                    java.io.File uploadFile = m_fileUploadSection.getFile(e);
                    MimeType type = MimeType.guessMimeTypeFromFile(fileName);

                    String mimeType;
                    if( null == type )
                        mimeType = "text/plain";
                    else
                        mimeType = type.getMimeType();

                    file.loadFromFile(fileName, uploadFile, mimeType);
                    file.save();
                    
                    item.setFile(file);
                    item.save();
                } catch (IOException err) {
                    throw new RuntimeException(err.getMessage());
                }
            }
        }
    }

}









