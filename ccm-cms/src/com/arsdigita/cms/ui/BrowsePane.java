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
package com.arsdigita.cms.ui;

import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Resettable;
import com.arsdigita.bebop.SegmentedPanel;
import com.arsdigita.bebop.SingleSelectionModel;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.cms.CMS;
import com.arsdigita.cms.Folder;
import com.arsdigita.cms.ui.folder.FolderRequestLocal;
import com.arsdigita.cms.ui.folder.FolderSelectionModel;
import com.arsdigita.cms.ui.folder.FolderTreeModelBuilder;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.kernel.ACSObject;
import com.arsdigita.kernel.User;
import com.arsdigita.toolbox.ui.LayoutPanel;
import com.arsdigita.util.Assert;
import com.arsdigita.web.Web;

import java.math.BigDecimal;
import org.apache.log4j.Logger;

/**
 * A pane that contains a folder tree on the left and a folder manipulator on
 * the right. It is a part of the content section main page and is displayed
 * as the "Browse" tab.
 *
 * @author David LutterKort &lt;dlutter@redhat.com&gt;
 * @version $Id: BrowsePane.java 1325 2006-09-22 08:11:33Z sskracic $
 */
public class BrowsePane extends LayoutPanel implements Resettable {

    private static final Logger s_log = Logger.getLogger(BrowsePane.class);

    private final BaseTree m_tree;
    private final SingleSelectionModel m_model;
    private final FolderSelectionModel m_folderModel; // To support legacy UI code
    private final FolderRequestLocal m_folder;
    private final FlatItemList m_fil;

    public BrowsePane() {
        
        /* The folder tree displayed on the left side / left column           */
        m_tree = new BaseTree(new FolderTreeModelBuilder());
        m_model = m_tree.getSelectionModel();
        m_folderModel = new FolderSelectionModel(m_model);
        m_folder = new FolderRequestLocal(m_folderModel);

        final SegmentedPanel left = new SegmentedPanel();
        setLeft(left);

        final Label heading = new Label(GlobalizationUtil
                                        .globalize("cms.ui.folder_browser"));
        left.addSegment(heading, m_tree);

        m_fil = new FlatItemList(m_folder, m_folderModel);
        setBody(m_fil);

        m_fil.getManipulator().getItemView().addProcessListener
            (new ProcessListener());
        m_fil.getManipulator().getTargetSelector().addProcessListener
            (new ProcessListener());
        m_fil.getManipulator().getTargetSelector().addSubmissionListener
            (new SubmissionListener());
    }

    @Override
    public final void register(Page page) {
        super.register(page);

        page.addActionListener(new FolderListener());
        page.addActionListener(new TreeListener());
    }

    @Override
    public final void reset(PageState state) {
        super.reset(state);

        m_fil.reset(state);
    }

    // Private classes and methods

    /**
     * 
     */
    private final class ProcessListener implements FormProcessListener {
        /**
         * 
         * @param e 
         */
        public final void process(final FormSectionEvent e) {
            final PageState state = e.getPageState();

            if (e.getSource() == m_fil.getManipulator().getItemView()) {
                // Hide everything except for the flat item list
                m_tree.setVisible(state, false);
            } else if (e.getSource() == m_fil.getManipulator
                           ().getTargetSelector()) {
                m_tree.setVisible(state, true);
            }
        }
    }

    private final class SubmissionListener implements FormSubmissionListener {
        public final void submitted(final FormSectionEvent e) {
            final PageState s = e.getPageState();

            if (e.getSource() == m_fil.getManipulator().getTargetSelector()) {
                if (!m_fil.getManipulator().getTargetSelector().isVisible(s)) {
                    m_tree.setVisible(s, true);
                }
            }
        }
    }

    private final class FolderListener implements ActionListener {
        public final void actionPerformed(final ActionEvent e) {
            final PageState state = e.getPageState();

            if (!m_model.isSelected(state)) {
                final String folder = state.getRequest().getParameter
                    (ContentSectionPage.SET_FOLDER);

                if (folder == null) {
                    final Folder root = CMS.getContext().getContentSection
                        ().getRootFolder();
		    BigDecimal folderID = root.getID();

		    User user = Web.getWebContext().getUser();
		    if ( user != null ) {
			Folder homeFolder = Folder.getUserHomeFolder(
                                     user,CMS.getContext().getContentSection());
			if ( homeFolder != null ) {
			    folderID = homeFolder.getID();
			}
			
		    }

                    m_model.setSelectedKey(state, folderID);
                } else {
                    m_model.setSelectedKey(state, folder);
                }
            }
        }
    }

    private final class TreeListener implements ActionListener {
        public final void actionPerformed(final ActionEvent e) {
            final PageState state = e.getPageState();

            if (Assert.isEnabled()) {
                Assert.isTrue(m_model.isSelected(state));
            }

            final Folder root = CMS.getContext().getContentSection
                ().getRootFolder();

            if (!root.equals(m_folder.getFolder(state))) {
                // Expand the ancestor nodes of the currently
                // selected node.

                ACSObject object = m_folder.getFolder(state);

                while (object != null) {
                    if (object instanceof Folder) {
                        final ACSObject result =
                            ((Folder) object).getParent();

                        if (result instanceof Folder) {
                            object = result;
                            m_tree.expand(object.getID().toString(), state);
                        } else {
                            object = null;
                        }
                    } else {
                        object = null;
                    }
                }
            }
        }
    }
}
