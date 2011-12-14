/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.arsdigita.cms.ui;

import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.ParameterSingleSelectionModel;
import com.arsdigita.bebop.SegmentedPanel;
import com.arsdigita.bebop.SegmentedPanel.Segment;
import com.arsdigita.bebop.SingleSelectionModel;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.cms.CMS;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.ui.authoring.CreationSelector;
import com.arsdigita.cms.ui.authoring.NewItemForm;
import com.arsdigita.cms.ui.folder.FolderRequestLocal;
import com.arsdigita.cms.ui.folder.FolderSelectionModel;
import com.arsdigita.cms.ui.folder.FolderTreeModelBuilder;
import com.arsdigita.cms.util.GlobalizationUtil;
import java.math.BigDecimal;

/**
 *
 * @author Sören Bernstein (quasimodo) <sbernstein@zes.uni-bremen.de>
 */
class ItemSearchCreateItemPane extends CMSContainer implements FormProcessListener {

    private static final String CONTENT_TYPE_ID = "ct";
    private NewItemForm m_newItem;
    private SingleSelectionModel m_typeSel;
    private final BaseTree m_tree;
    private final SingleSelectionModel m_model;
    private final FolderSelectionModel m_folderSel; // To support legacy UI code
    private final FolderRequestLocal m_folder;
    private CreationSelector m_selector;
    private Segment m_newItemSeg;

    public ItemSearchCreateItemPane() {

//        m_newItem = new SectionNewItemForm("newItem");
//        m_newItem.addProcessListener(this);

        m_tree = new BaseTree(new FolderTreeModelBuilder());
        m_model = m_tree.getSelectionModel();
        m_folderSel = new FolderSelectionModel(m_model);
        m_folder = new FolderRequestLocal(m_folderSel);

//        m_newItemSeg = addSegment();
        this.setIdAttr("folder-new-item");

//        m_newItemSeg.addHeader(new Label(GlobalizationUtil.globalize("cms.ui.new_item")));
        m_typeSel = new ParameterSingleSelectionModel(new BigDecimalParameter(CONTENT_TYPE_ID));
//      m_typeSel.addChangeListener(this);

        m_selector = new CreationSelector(m_typeSel, m_folderSel);
        this.add(m_selector);
        this.add(new Label("<br/>", false));

//        add(m_newItem);


    }

    public void process(FormSectionEvent e) {
        PageState s = e.getPageState();
        final Object source = e.getSource();
        if (source == m_newItem) {
            BigDecimal typeID = m_newItem.getTypeID(s);
            m_typeSel.setSelectedKey(s, typeID);
            //newItemMode(s);
        }
    }

    private static class SectionNewItemForm extends NewItemForm {

        public SectionNewItemForm(String name) {
            super(name);
        }

        public ContentSection getContentSection(PageState s) {
            return CMS.getContext().getContentSection();
        }
    }
}
