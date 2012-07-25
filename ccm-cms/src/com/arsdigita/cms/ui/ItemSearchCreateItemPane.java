/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.arsdigita.cms.ui;

import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Link;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.ParameterSingleSelectionModel;
import com.arsdigita.bebop.SegmentedPanel;
import com.arsdigita.bebop.SegmentedPanel.Segment;
import com.arsdigita.bebop.SingleSelectionModel;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.SingleSelect;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.CMS;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ContentPage;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.ui.authoring.CreationSelector;
import com.arsdigita.cms.ui.authoring.NewItemForm;
import com.arsdigita.cms.ui.folder.FlatFolderPicker;
import com.arsdigita.cms.ui.folder.FolderRequestLocal;
import com.arsdigita.cms.ui.folder.FolderSelectionModel;
import com.arsdigita.cms.ui.item.ContentItemRequestLocal;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.persistence.OID;
import java.math.BigDecimal;
import java.util.TooManyListenersException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Sören Bernstein (quasimodo) <sbernstein@zes.uni-bremen.de>
 * @author Jens Pelzetter <jens@jp-digital.de>
 */
class ItemSearchCreateItemPane extends CMSContainer implements FormInitListener,
                                                               FormProcessListener,
                                                               FormSubmissionListener {

    public static final String WIDGET_PARAM = "widget";
    public static final String SEARCHWIDGET_PARAM = "searchWidget";
    public static final String PUBLISHWIDGET_PARAM = "publishWidget";
    private static final String CONTENT_TYPE_ID = "ct";
    private static final String FOLDER_ID = "folder_id";
    private final NewItemForm m_newItem;
    private final SingleSelectionModel m_typeSel;
    private final FlatFolderPicker m_folderPicker;
    private final SingleSelectionModel m_model;
    private final FolderSelectionModel m_folderSel; // To support legacy UI code    
    private String defaultFolder;
    private boolean editAfterCreate = true;
    private final CreationSelector m_creator;
    private final ContentItemRequestLocal m_contentItem = new ContentItemRequestLocal() {
    };
    //private final Link m_selectCloseLink;
    //private final Link m_selectEditLink;
    private final Link m_fallBackLink;
    private final SegmentedPanel m_segPanel;
    private final Segment m_creationSeg;
    private final Segment m_newItemSeg;
    private final Segment m_linkSeg;

    public ItemSearchCreateItemPane(final ItemSearchPage parent) {
        super();

        m_segPanel = new SegmentedPanel("itemSearchCreate");
        m_creationSeg = new Segment();
        m_newItemSeg = new Segment();
        m_linkSeg = new Segment();

        m_newItem = new SectionNewItemForm("newItem");

        m_model = new ParameterSingleSelectionModel(new BigDecimalParameter(FOLDER_ID));
        m_folderSel = new FolderSelectionModel(m_model);

        this.setIdAttr("folder-new-item");

        m_typeSel = new ParameterSingleSelectionModel(new BigDecimalParameter(CONTENT_TYPE_ID));

        m_creator = new CreationSelector(m_typeSel, m_folderSel) {

            @Override
            public void editItem(final PageState state, final ContentItem item) {

                m_creationSeg.setVisible(state, false);
                m_linkSeg.setVisible(state, true);

                m_contentItem.set(state, item);
            }

        };
        m_creationSeg.add(m_creator);
        m_creationSeg.add(new Label("<br/>", false));

        final BoxPanel folderRow = new BoxPanel(BoxPanel.HORIZONTAL);
        folderRow.add(new Label(GlobalizationUtil.globalize("cms.ui.item_search.create.folder_select")));
        m_folderPicker = new FlatFolderPicker("flatFolder");
        folderRow.add(m_folderPicker);
        m_newItem.add(folderRow);
        m_newItemSeg.add(m_newItem);

        m_newItem.addInitListener(this);
        m_newItem.addProcessListener(this);
        m_newItem.addSubmissionListener(this);

        m_segPanel.add(m_newItemSeg);
        m_segPanel.add(m_creationSeg);
        add(m_segPanel);

        m_fallBackLink = new Link(
                (String) GlobalizationUtil.globalize("cms.ui.search.create.fallback").localize(),
                new PrintListener() {

                    public void prepare(final PrintEvent event) {
                        final Link target = (Link) event.getTarget();
                        final PageState state = event.getPageState();

                        final ContentItem item = m_contentItem.getContentItem(state);
                        final String title;
                        if (item instanceof ContentPage) {
                            title = ((ContentPage) item).getTitle();
                        } else {
                            title = item.getName();
                        }

                        final String widget = (String) state.getValue(new StringParameter(WIDGET_PARAM));
                        final String searchWidget = (String) state.getValue(new StringParameter(SEARCHWIDGET_PARAM));
                        final String publishWidget = (String) state.getValue(new StringParameter(PUBLISHWIDGET_PARAM));

                        final String scriptAction;
                        if (editAfterCreate) {
                            ((Label) target.getChild()).setLabel((String) GlobalizationUtil.globalize(
                                    "cms.ui.search.create.select_edit").localize());

                            final ContentSection section = item.getContentSection();
                            final String nodeURL = section.getPath() + "/";
                            final String linkTarget = ContentItemPage.getItemURL(nodeURL, item.getID(),
                                                                                 ContentItemPage.AUTHORING_TAB, true);
                            target.setTarget(linkTarget);
                            scriptAction = "";
                        } else {
                            ((Label) target.getChild()).setLabel((String) GlobalizationUtil.globalize(
                                    "cms.ui.search.create.select_close").localize());
                            scriptAction = "self.close();\n"
                                     + "return false;";
                        }

                        target.setOnClick(String.format("window.opener.document.%s.value=\"%s\";"
                                                        + "window.opener.document.%s.value=\"%s\";"
                                                        + "window.opener.document.%s.value=\"%s\";"
                                                        + "%s",
                                                        widget,
                                                        item.getID().toString(),
                                                        searchWidget,
                                                        title,
                                                        publishWidget,
                                                        Boolean.TRUE.toString(),
                                                        scriptAction));
                    }

                });

//        m_selectCloseLink = new Link(
//                (String) GlobalizationUtil.globalize("cms.ui.search.create.select_close").localize(),
//                new PrintListener() {
//
//                    public void prepare(final PrintEvent event) {
//                        final Link target = (Link) event.getTarget();
//                        final PageState state = event.getPageState();
//
//                        final ContentItem item = m_contentItem.getContentItem(state);
//                        final String title;
//                        if (item instanceof ContentPage) {
//                            title = ((ContentPage) item).getTitle();
//                        } else {
//                            title = item.getName();
//                        }
//
//                        final String widget = (String) state.getValue(new StringParameter(WIDGET_PARAM));
//                        final String searchWidget = (String) state.getValue(new StringParameter(SEARCHWIDGET_PARAM));
//                        final String publishWidget = (String) state.getValue(new StringParameter(PUBLISHWIDGET_PARAM));
//
//                        target.setOnClick(String.format("window.opener.document.%s.value=\"%s\";"
//                                                        + "window.opener.document.%s.value=\"%s\";"
//                                                        + "window.opener.document.%s.value=\"%s\";"
//                                                        + "self.close();"
//                                                        + "return false;",
//                                                        widget,
//                                                        item.getID().toString(),
//                                                        searchWidget,
//                                                        title,
//                                                        publishWidget,
//                                                        Boolean.TRUE.toString()));
//                    }
//
//                });
//
//        m_selectEditLink = new Link((String) GlobalizationUtil.globalize("cms.ui.search.create.select_edit").localize(),
//                                    new PrintListener() {
//
//            public void prepare(final PrintEvent event) {
//                final Link target = (Link) event.getTarget();
//                final PageState state = event.getPageState();
//
//                final ContentItem item = m_contentItem.getContentItem(state);
//                final String title;
//                if (item instanceof ContentPage) {
//                    title = ((ContentPage) item).getTitle();
//                } else {
//                    title = item.getName();
//                }
//
//
//                final String widget = (String) state.getValue(new StringParameter(WIDGET_PARAM));
//                final String searchWidget = (String) state.getValue(new StringParameter(SEARCHWIDGET_PARAM));
//                final String publishWidget = (String) state.getValue(new StringParameter(PUBLISHWIDGET_PARAM));
//
//                final ContentSection section = item.getContentSection();
//                final String nodeURL = section.getPath() + "/";
//                final String linkTarget = ContentItemPage.getItemURL(nodeURL, item.getID(),
//                                                                     ContentItemPage.AUTHORING_TAB, true);
//                target.setTarget(linkTarget);
//                target.setOnClick(String.format("window.opener.document.%s.value=\"%s\";"
//                                                + "window.opener.document.%s.value=\"%s\";"
//                                                + "window.opener.document.%s.value=\"%s\";",
//                                                widget,
//                                                item.getID().toString(),
//                                                searchWidget,
//                                                title,
//                                                publishWidget,
//                                                Boolean.TRUE.toString()));
//
//            }
//
//        });

        final BoxPanel linkPanel = new BoxPanel(BoxPanel.VERTICAL);
        final Label jsLabel = new Label("", false);
        jsLabel.addPrintListener(new PrintListener() {

            public void prepare(final PrintEvent event) {
                final Label target = (Label) event.getTarget();
                final PageState state = event.getPageState();

                final ContentItem item = m_contentItem.getContentItem(state);
                final String title;
                if (item instanceof ContentPage) {
                    title = ((ContentPage) item).getTitle();
                } else {
                    title = item.getName();
                }

                final String widget = (String) state.getValue(new StringParameter(WIDGET_PARAM));
                final String searchWidget = (String) state.getValue(new StringParameter(SEARCHWIDGET_PARAM));
                final String publishWidget = (String) state.getValue(new StringParameter(PUBLISHWIDGET_PARAM));

                final String scriptAction;
                if (editAfterCreate) {
                    final ContentSection section = item.getContentSection();
                    final String nodeURL = section.getPath() + "/";
                    final String linkTarget = ContentItemPage.getItemURL(nodeURL, item.getID(),
                                                                         ContentItemPage.AUTHORING_TAB, true);
                    scriptAction = String.format("window.location.href = \"/ccm/%s\";", linkTarget);
                } else {
                    scriptAction = "self.close();";
                }

                target.setLabel(String.format("<script type=\"text/javascript\">"
                                              + "<!--\n"
                                              + "window.opener.document.%s.value=\"%s\";\n"
                                              + "window.opener.document.%s.value=\"%s\";\n"
                                              + "window.opener.document.%s.value=\"%s\";\n"
                                              + "%s\n"
                                              + "-->\n"
                                              + "</script>",
                                              widget,
                                              item.getID().toString(),
                                              searchWidget,
                                              title,
                                              publishWidget,
                                              Boolean.TRUE.toString(),
                                              scriptAction));

            }

        });
        linkPanel.add(jsLabel);
        //linkPanel.add(m_selectCloseLink);
        //linkPanel.add(m_selectEditLink);
        linkPanel.add(m_fallBackLink);
        m_linkSeg.add(linkPanel);

        m_segPanel.add(m_linkSeg);

    }

    @Override
    public void register(final Page page) {
        super.register(page);

        page.setVisibleDefault(m_newItemSeg, true);
        page.setVisibleDefault(m_creationSeg, false);
        page.setVisibleDefault(m_linkSeg, false);

        page.addComponentStateParam(this, m_typeSel.getStateParameter());
        page.addComponentStateParam(this, m_folderSel.getStateParameter());
    }

    public void init(final FormSectionEvent fse) throws FormProcessException {
        final PageState state = fse.getPageState();
        final FormData data = fse.getFormData();
        if (((data.get("flatFolder") == null) || "".equals(data.get("flatFolder")))
            && (defaultFolder != null)) {
            data.put("flatFolder", defaultFolder.toString());
        }


    }

    public void submitted(final FormSectionEvent fse) {
        final PageState state = fse.getPageState();

        final BigDecimal typeID = m_newItem.getTypeID(state);
        m_typeSel.setSelectedKey(state, typeID);
        final OID folderOID = OID.valueOf((String) m_folderPicker.getValue(state));
        m_folderSel.setSelectedKey(state, folderOID.get("id"));
        m_newItemSeg.setVisible(state, false);
        m_creationSeg.setVisible(state, true);
    }

    public void process(final FormSectionEvent fse) throws FormProcessException {
        //Nothing
    }

    private static class SectionNewItemForm extends NewItemForm {

        public SectionNewItemForm(String name) {
            super(name);
        }

        public ContentSection getContentSection(PageState s) {
            return CMS.getContext().getContentSection();
        }

    }

    protected String getDefaultFolder() {
        return defaultFolder;
    }

    protected void setDefaultFolder(final String defaultFolder) {
        this.defaultFolder = defaultFolder;
    }

    protected boolean getEditAfterCreate() {
        return editAfterCreate;
    }

    protected void setEditAfterCreate(final boolean editAfterCreate) {
        this.editAfterCreate = editAfterCreate;
    }

}
