/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.arsdigita.cms.ui;

import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.ParameterSingleSelectionModel;
import com.arsdigita.bebop.SegmentedPanel;
import com.arsdigita.bebop.SegmentedPanel.Segment;
import com.arsdigita.bebop.SingleSelectionModel;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.cms.CMS;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.ui.authoring.CreationSelector;
import com.arsdigita.cms.ui.authoring.NewItemForm;
import com.arsdigita.cms.ui.folder.FlatFolderPicker;
import com.arsdigita.cms.ui.folder.FolderItemPane;
import com.arsdigita.cms.ui.folder.FolderRequestLocal;
import com.arsdigita.cms.ui.folder.FolderSelectionModel;
import com.arsdigita.cms.ui.folder.FolderTreeModelBuilder;
import com.arsdigita.cms.util.GlobalizationUtil;
import java.math.BigDecimal;

/**
 *
 * @author Sören Bernstein (quasimodo) <sbernstein@zes.uni-bremen.de>
 * @author Jens Pelzetter <jens@jp-digital.de>
 */
class ItemSearchCreateItemPane extends CMSContainer implements FormProcessListener, FormSubmissionListener {

    private static final String CONTENT_TYPE_ID = "ct";
    private NewItemForm m_newItem;
    private SingleSelectionModel m_typeSel;
    private FlatFolderPicker m_folderPicker;
    private final BaseTree m_tree;
    private final SingleSelectionModel m_model;
    private final FolderSelectionModel m_folderSel; // To support legacy UI code
    private final FolderRequestLocal m_folder;
    private final CreationSelector m_creator;
    private final ItemSearchPage parent;
    private SegmentedPanel m_segPanel;
    private Segment m_creationSeg;
    private Segment m_newItemSeg;

    public ItemSearchCreateItemPane(final ItemSearchPage parent) {
        super();
               
        this.parent = parent;

        m_segPanel = new SegmentedPanel("itemSearchCreate");
        m_creationSeg = new Segment();
        m_newItemSeg = new Segment();
        
        m_newItem = new SectionNewItemForm("newItem");
        m_newItem.addProcessListener(this);

        m_tree = new BaseTree(new FolderTreeModelBuilder());
        m_model = m_tree.getSelectionModel();
        m_folderSel = new FolderSelectionModel(m_model);
        m_folder = new FolderRequestLocal(m_folderSel);

//        m_newItemSeg = addSegment();
        this.setIdAttr("folder-new-item");

//        m_newItemSeg.addHeader(new Label(GlobalizationUtil.globalize("cms.ui.new_item")));        
        m_typeSel = new ParameterSingleSelectionModel(new BigDecimalParameter(CONTENT_TYPE_ID));
//      m_typeSel.addChangeListener(this);

        m_creator = new CreationSelector(m_typeSel, m_folderSel);
        m_creationSeg.add(m_creator);
        m_creationSeg.add(new Label("<br/>", false));

        final BoxPanel folderRow = new BoxPanel(BoxPanel.HORIZONTAL);
        folderRow.add(new Label(GlobalizationUtil.globalize("cms.ui.item_search.create.folder_select")));
        m_folderPicker = new FlatFolderPicker("flatFolder");
        //m_newItem.add(m_folderPicker);
        folderRow.add(m_folderPicker);
        m_newItem.add(folderRow);

        m_newItemSeg.add(m_newItem);

        m_newItem.addProcessListener(this);
        m_newItem.addSubmissionListener(this);

        m_segPanel.add(m_newItemSeg);
        m_segPanel.add(m_creationSeg);
        
        add(m_segPanel);

    }

    @Override
    public void register(final Page page) {
        super.register(page);

        page.setVisibleDefault(m_newItemSeg, true);
        page.setVisibleDefault(m_creationSeg, false);
                        
        page.addComponentStateParam(this, m_typeSel.getStateParameter());
        page.addComponentStateParam(this, m_folderSel.getStateParameter());                   
    }

    public void submitted(final FormSectionEvent fse) {
        
    }

    public void process(FormSectionEvent fse) throws FormProcessException {      
        final PageState state = fse.getPageState();
        final Object source = fse.getSource();
        //if (source == m_newItem) {
            final BigDecimal typeID = m_newItem.getTypeID(state);
            m_typeSel.setSelectedKey(state, typeID);
            final BigDecimal folderId = (BigDecimal) m_folderPicker.getValue(state);
            m_folderSel.setSelectedKey(state, folderId);            

            //m_newItem.setVisible(state, false);
            //m_creator.setVisible(state, true);
            //m_newItemSeg.setVisible(state, false);
            m_creationSeg.setVisible(state, true);
            //parent.setTabActive(state, this, true);           
            //newItemMode(state);
        //}
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
