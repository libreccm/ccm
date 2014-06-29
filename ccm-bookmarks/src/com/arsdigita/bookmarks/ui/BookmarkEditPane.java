/*
 * Copyright (C) 2001, 2002 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the CCM Public
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of
 * the License at http://www.redhat.com/licenses/ccmpl.html
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */

package com.arsdigita.bookmarks.ui;


import com.arsdigita.bookmarks.util.GlobalizationUtil; 
import com.arsdigita.bookmarks.BookmarkCollection; 
import com.arsdigita.bookmarks.Bookmarks; 
import com.arsdigita.bookmarks.Bookmark; 

import com.arsdigita.web.Application;
import com.arsdigita.web.Web;
import com.arsdigita.bebop.ActionLink;
import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.ControlLink;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.GridPanel;
import com.arsdigita.bebop.DynamicListWizard;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.List;
import com.arsdigita.bebop.ModalContainer;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.RadioGroup;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.event.ChangeEvent;
import com.arsdigita.bebop.event.ChangeListener;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormValidationListener;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.bebop.parameters.NotEmptyValidationListener;
import com.arsdigita.bebop.list.ListCellRenderer;
import com.arsdigita.bebop.list.ListModel;
import com.arsdigita.bebop.list.ListModelBuilder;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.KernelExcursion;
import com.arsdigita.kernel.permissions.PermissionService;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;

import java.math.BigDecimal;

import javax.servlet.ServletException;

/**
 * <b><strong>Experimental</strong></b>
 *
 * @author <a href="mailto:jparsons@arsdigita.com">Jim Parsons</a>
 */
public class BookmarkEditPane extends DynamicListWizard {

    public static final String EVENT_SWAP_UP = "swapUp";
    public static final String EVENT_SWAP_DOWN = "swapDown";

    final ModalContainer m_editContainer = new ModalContainer();

    SimpleContainer m_mainDisplay;
    EditBookmarkForm m_editBmrkForm;
    DeleteForm m_deleteForm;
    RequestLocal m_prtlRL;



    static class BmrkListModel implements ListModel {
        BookmarkCollection m_bmrks;
        Bookmark m_bmrk;
        public BmrkListModel(PageState s) {
            Bookmarks bmrkapp = (Bookmarks)Application.getCurrentApplication(s.getRequest());
            m_bmrks = bmrkapp.getBookmarks();
        }
        public boolean next() {
            if (!m_bmrks.next()) {
                return false;
            }
            m_bmrk = m_bmrks.getBookmark();
            return true;
        }
        public Object getElement() {
            return m_bmrk.getName();
        }
        public String getKey() {
            return m_bmrk.getID().toString();
        }
    }



    /**
     * Constructor
     * 
     */
    public BookmarkEditPane() {
        super("Current Bookmarks", new ListModelBuilder() {
            public ListModel makeModel(List l, PageState ps) {
                return new BmrkListModel(ps);
            }
            public void lock() {}
            public boolean isLocked() { return true; }
        },
        "Add a new Bookmark",
        new Label(GlobalizationUtil.globalize(
                                    "bookmarks.ui.select_a_bookmark_for_editing")));

        final DynamicListWizard dlw = this;

        m_prtlRL = new RequestLocal() {
            protected Object initialValue(PageState ps) {
                return (Bookmarks)Application.getCurrentApplication(
                                                        ps.getRequest());
            }
        };



        // FORM FOR ADDING NEW Bookmarks
        Form addForm = new Form("addBookmark", new GridPanel(2));

        addForm.add(new Label(GlobalizationUtil.globalize(
                              "bookmarks.ui.name_of_new_bookmark")));

        final TextField newBmrkName = new TextField("name");
        newBmrkName.getParameterModel().addParameterListener
            (new NotNullValidationListener("Choose a name for the new Bookmark"));
        addForm.add(newBmrkName);

        addForm.add(new Label(GlobalizationUtil.globalize("bookmarks.ui.new_bookmark_url")));

        final TextField newBmrkURL = new TextField("url");
        newBmrkURL.getParameterModel().addParameterListener
            (new NotEmptyValidationListener("White space is not allowed in URLs"));
        addForm.add(newBmrkURL);

        addForm.add(new Label(GlobalizationUtil.globalize("bookmarks.ui.bookmark_description")));

        final TextArea bmrkDesc = new TextArea("BmrkDesc");
        bmrkDesc.getParameterModel().addParameterListener
            (new NotNullValidationListener("Enter a description for the new Bookmark"));
        addForm.add(bmrkDesc);

        addForm.add(new Label(GlobalizationUtil.globalize("bookmarks.ui.bookmark_in_new_window")));

        final RadioGroup bmrkNewWin = new RadioGroup("newWin");
        bmrkNewWin.addOption(new Option("true", "Yes"));
        bmrkNewWin.addOption(new Option("false", "No"));
        addForm.add(bmrkNewWin);
        
        addForm.addInitListener(new FormInitListener() {
        	public void init(FormSectionEvent e) {
        		FormData data = e.getFormData();
        		data.put("newWin", "false"); 
        	}
        });

        addForm.add(new Submit("Create Bookmark"));

        addForm.addValidationListener(new FormValidationListener() {
        	public void validate(FormSectionEvent ev) {
        		// check that the user has permission to create bookmarks.
        		PageState ps = ev.getPageState();
        		Bookmarks ba = (Bookmarks)m_prtlRL.get(ps);
        		ba.assertPrivilege(PrivilegeDescriptor.CREATE);
        	}
        });


        addForm.addProcessListener(new FormProcessListener() {
                public void process(FormSectionEvent ev) {
                    PageState ps = ev.getPageState();
                    final Bookmarks ba = (Bookmarks)m_prtlRL.get(ps);
                    final Bookmark newBmrk =
                        new Bookmark(newBmrkName.getValue(ps).toString(),
                                     newBmrkURL.getValue(ps).toString());
                    newBmrk.setDescription(bmrkDesc.getValue(ps).toString());
                    newBmrk.setBookmarkApplication(ba);
                    newBmrk.setNewWindow("true".equals(bmrkNewWin.getValue(ps)));
                    newBmrk.setSortKey(Integer.MAX_VALUE);
                    newBmrk.save();
                    ba.normalizeBookmarkSortKeys();
                    // Give the new bookmark a permissions context
                    new KernelExcursion() {
                    	protected void excurse() {
                    		setParty(Kernel.getSystemParty());
                    		PermissionService.setContext(newBmrk,ba);
                    		newBmrk.save();
                    	}}.run();

                    dlw.getListOfComponents()
                        .getSelectionModel()
                        .setSelectedKey(ps, newBmrk.getID().toString());
                }
            });

        setAddPane(addForm);

        // CONSTRUCT EDIT COMPONENT


        m_mainDisplay = new BoxPanel(BoxPanel.VERTICAL);

        m_editBmrkForm = new EditBookmarkForm(new GridPanel(2));

        m_mainDisplay.add(m_editBmrkForm);

        ActionLink deleteLink = new ActionLink( 
                                (String) GlobalizationUtil.globalize(
                                "bookmarks.ui.delete_this_bookmark").localize());

        deleteLink.setClassAttr("actionLink");
        deleteLink.addActionListener(new DeleteLinkListener());

        m_mainDisplay.add(deleteLink);



        m_deleteForm = new DeleteForm();

        m_editContainer.add(m_mainDisplay);
        m_editContainer.add(m_deleteForm);
        setEditPane(m_editContainer);

        ((List) getListingComponent()).addChangeListener(new ChangeListener() {
                public void stateChanged(ChangeEvent ev) {
                    ev.getPageState().reset(m_editContainer);
                }
            });

        ((List) getListingComponent()).setCellRenderer(new ListCellRenderer() {
            public Component getComponent(List list, PageState state,
                    Object value, String key, int index, boolean isSelected) {

                Bookmarks app = (Bookmarks) Web.getWebContext().getApplication();
                BookmarkCollection bColl = app.getBookmarks();
                final long size = bColl.size();
                bColl.close();

                SimpleContainer container = new SimpleContainer();
                
                Label name = new Label(value.toString(), false);
                if (isSelected) {
                    final String fKey = key;
                    if (index > 0) {
                        ControlLink upLink = new ControlLink("up") {
                            public void setControlEvent(PageState s) {
                                s.setControlEvent(dlw, EVENT_SWAP_UP, fKey);
                            }
                        };
                        container.add(upLink);
                        upLink.setClassAttr("shiftUp");
                        container.add(new Label("&nbsp;&nbsp;", false));
                    }
                    
                	name.setFontWeight(Label.BOLD);
                	container.add(name);
                    
                    if (index < size - 1) {
                    	container.add(new Label("&nbsp;&nbsp;", false));
                        ControlLink downLink = new ControlLink("down") {
                            public void setControlEvent(PageState s) {
                                s.setControlEvent(dlw, EVENT_SWAP_DOWN, fKey);
                            }
                        };
                        downLink.setClassAttr("shiftDown");
                        container.add(downLink);
                    }
                }
                else {
                    ControlLink l = new ControlLink(name);
                    container.add(l);
                }
                return container;
            }
        });
    } //end of constructor

    public void respond(PageState state) throws ServletException {
        String name = state.getControlEventName();
        String bIDstr = state.getControlEventValue();
        if (EVENT_SWAP_UP.equals(name)) {
            BigDecimal bID = new BigDecimal(bIDstr);
            Bookmark b = Bookmark.retrieveBookmark(bID);
            Bookmarks bApp = b.getBookmarkApplication();
            bApp.swapBookmarkWithPrevious(b);
        }
        else if (EVENT_SWAP_DOWN.equals(name)) {
            BigDecimal bID = new BigDecimal(bIDstr);
            Bookmark b = Bookmark.retrieveBookmark(bID);
            Bookmarks bApp = b.getBookmarkApplication();
            bApp.swapBookmarkWithNext(b);
        }
        else {
            super.respond(state);
        }
    }


    public class EditBookmarkForm extends Form implements FormProcessListener
    {
        private TextField bookmarkName;
        private TextField bookmarkURL;
        private TextArea bookmarkDescription;
        private Label instruction;
        private Label blank;
        private Label instruction1;
        private Label instruction2;
        private Label instruction3;
        private Label creationDateLabel;
        private Label creationDate;
        private Label modDateLabel;
        private Label modDate;
        private Label authorLabel;
        private Label author;
        private Label visitsLabel;
        private Label visits;
        private Label blank1,blank2,blank3,blank4;
        private Submit button;
        private Label newWindowLabel;
        private RadioGroup newWindow;


        public EditBookmarkForm(GridPanel gp)
        {
            super("editbookmarkform",gp);
            instruction = new Label(GlobalizationUtil.globalize(
                              "bookmarks.ui.edit_fields_and_click_save_button"));
            blank = new Label(" ");
            instruction1 = new Label(GlobalizationUtil.globalize(
                               "bookmarks.ui.bookmark_name"));
            instruction2 = new Label(GlobalizationUtil.globalize(
                               "bookmarks.ui.bookmark_url"));
            instruction3 = new Label(GlobalizationUtil.globalize(
                               "bookmarks.ui.bookmark_description"));
            blank1 = new Label("");
            blank2 = new Label("");
            blank3 = new Label("");
            blank4 = new Label("");
            creationDateLabel = new Label(GlobalizationUtil.globalize(
                                    "bookmarks.ui.creation_date"));
            modDateLabel = new Label(GlobalizationUtil.globalize(
                               "bookmarks.ui.last_modified_date"));
            authorLabel = new Label(GlobalizationUtil.globalize(
                              "bookmarks.ui.created_by"));
            visitsLabel = new Label(GlobalizationUtil.globalize(
                              "bookmarks.ui.number_of_visits"));
            bookmarkName = new TextField("BookmarkName");
            bookmarkName.setDefaultValue("");
            bookmarkName.addValidationListener(new NotNullValidationListener(
                                               "Every Bookmark must have a name!"));

            newWindowLabel = new Label(GlobalizationUtil.globalize(
                                 "bookmarks.ui.bookmark_in_new_window"));
            newWindow = new RadioGroup("newWin");
            newWindow.addOption(new Option("true", "Yes"));
            newWindow.addOption(new Option("false", "No"));
            try {
            	newWindow.addPrintListener( new PrintListener() {
                    public void prepare(PrintEvent e) {
                        PageState s = e.getPageState();
                        if(getSelectionModel().isSelected(s)) {
                            BigDecimal bd = new BigDecimal((String)
                                       getSelectionModel().getSelectedKey(s));
                            Bookmark bmrk = Bookmark.retrieveBookmark(bd);
                            RadioGroup group = (RadioGroup)e.getTarget();
                            group.setValue(s,String.valueOf(bmrk.getNewWindow()));
                        }
                    }
                });
            } catch(java.util.TooManyListenersException e) { }


            try {
                bookmarkName.addPrintListener( new PrintListener() {
                    public void prepare(PrintEvent e) {
                        PageState s = e.getPageState();
                        if(getSelectionModel().isSelected(s)) {
                            BigDecimal bd = new BigDecimal((String)
                                            getSelectionModel().getSelectedKey(s));
                            Bookmark bmrk = Bookmark.retrieveBookmark(bd);
                            TextField tf = (TextField)e.getTarget();
                            tf.setValue(s,bmrk.getName());
                        }
                    }
                });
            } catch(java.util.TooManyListenersException e) { }

            bookmarkURL = new TextField("BookmarkURL");
            bookmarkURL.setDefaultValue("");
            bookmarkURL.addValidationListener(new NotEmptyValidationListener(
                                              "White space is not allowed in URLs!"));


            try {
                bookmarkURL.addPrintListener( new PrintListener()
                    {
                        public void prepare(PrintEvent e)
                        {
                            PageState s = e.getPageState();
                            if(getSelectionModel().isSelected(s))
                                {
                                    BigDecimal bd =
                                        new BigDecimal((String) getSelectionModel().getSelectedKey(s));
                                    Bookmark bmrk = Bookmark.retrieveBookmark(bd);
                                    TextField tf = (TextField)e.getTarget();
                                    tf.setValue(s,bmrk.getURL());
                                }
                        }
                    });
            } catch(java.util.TooManyListenersException e) { }

            bookmarkDescription = new TextArea("Description");
            bookmarkDescription.setDefaultValue("");

            try {
                bookmarkDescription.addPrintListener( new PrintListener()
                    {
                        public void prepare(PrintEvent e)
                        {
                            PageState s = e.getPageState();
                            if(getSelectionModel().isSelected(s))
                                {
                                    BigDecimal bd =
                                        new BigDecimal((String) getSelectionModel().getSelectedKey(s));
                                    Bookmark bmrk = Bookmark.retrieveBookmark(bd);
                                    TextArea ta = (TextArea)e.getTarget();
                                    ta.setValue(s,bmrk.getDescription());
                                }
                        }
                    });
            } catch(java.util.TooManyListenersException e) { }

            creationDate = new Label(GlobalizationUtil.globalize("bookmarks.ui.creation_date"));
            creationDate.addPrintListener(new PrintListener() {
                    public void prepare(PrintEvent e) {
                        PageState s = e.getPageState();
                        BigDecimal bd =
                            new BigDecimal((String) getSelectionModel().getSelectedKey(s));
                        Bookmark bmrk = Bookmark.retrieveBookmark(bd);
                        Label t = (Label)e.getTarget();
                        t.setLabel(bmrk.getCreateDate());
                    }
                });

            modDate = new Label(GlobalizationUtil.globalize("bookmarks.ui.modification_date"));
            modDate.addPrintListener(new PrintListener() {
                    public void prepare(PrintEvent e) {
                        PageState s = e.getPageState();
                        BigDecimal bd =
                            new BigDecimal((String) getSelectionModel().getSelectedKey(s));
                        Bookmark bmrk = Bookmark.retrieveBookmark(bd);
                        Label t = (Label)e.getTarget();
                        t.setLabel(bmrk.getModDate());
                    }
                });

            author = new Label(GlobalizationUtil.globalize("bookmarks.ui.creator"));
            author.addPrintListener(new PrintListener() {
                    public void prepare(PrintEvent e) {
                        PageState s = e.getPageState();
                        BigDecimal bd =
                            new BigDecimal((String) getSelectionModel().getSelectedKey(s));
                        Bookmark bmrk = Bookmark.retrieveBookmark(bd);
                        Label t = (Label)e.getTarget();
                        t.setLabel(bmrk.getAuthor());
                    }
                });

            visits = new Label(GlobalizationUtil.globalize("bookmarks.ui.number_of_visits"));
            visits.addPrintListener(new PrintListener() {
                    public void prepare(PrintEvent e) {
                        PageState s = e.getPageState();
                        BigDecimal bd =
                            new BigDecimal((String) getSelectionModel().getSelectedKey(s));
                        Bookmark bmrk = Bookmark.retrieveBookmark(bd);
                        Label t = (Label)e.getTarget();
                        t.setLabel(bmrk.getNumVisits());
                    }
                });

            button = new Submit("Save");
            button.setButtonLabel("Update Properties");
            add(instruction);
            add(blank);
            add(instruction1);
            add(bookmarkName);
            add(instruction2);
            add(bookmarkURL);
            add(instruction3);
            add(bookmarkDescription);
            add(newWindowLabel);
            add(newWindow);
            add(blank1);
            add(blank2);
            add(creationDateLabel);
            add(creationDate);
            add(authorLabel);
            add(author);
            add(modDateLabel);
            add(modDate);
            //visits is commented out here until impl strategy is determined
            //add(visitsLabel);
            //add(visits);
            add(blank3);
            add(blank4);
            add(button);
            addProcessListener(this);

        }

        public void process(FormSectionEvent e)
        {
            PageState s = e.getPageState();
            BigDecimal bd =
                new BigDecimal((String) getSelectionModel().getSelectedKey(s));
            Bookmark bmrk = Bookmark.retrieveBookmark(bd);
            Bookmarks ba = (Bookmarks)Application.getCurrentApplication(s.getRequest());
            bmrk.setName(bookmarkName.getValue(s).toString());
            bmrk.setURL(bookmarkURL.getValue(s).toString());
            bmrk.setDescription(bookmarkDescription.getValue(s).toString());
            bmrk.setModDate();
            bmrk.setBookmarkApplication(ba);
            bmrk.setNewWindow("true".equals(newWindow.getValue(s)));
            bmrk.save();
            //This needs to be wrapped in an exception clause incase the save fails
        }

    }//end Edit Bookmark form



    public class DeleteForm extends Form implements FormProcessListener {
        private TextField currenttabName;
        private Label instruction;
        private Submit button;
        private Submit cancelbutton;


        public DeleteForm() {
            super("deletetabform");

            instruction = new Label(GlobalizationUtil.globalize("bookmarks.ui.are_you_sure_you_want_to_delete_this_bookmark"));
            instruction.addPrintListener(new PrintListener() {
                    public void prepare(PrintEvent e) {
                        PageState s = e.getPageState();
                        String prefixstr = "Are you sure you want to delete the ";
                        BigDecimal bmrkID = new BigDecimal
                            ((String)getSelectionModel().getSelectedKey(s));
                        Bookmark bmrk = Bookmark.retrieveBookmark(bmrkID);
                        Label t = (Label)e.getTarget();
                        t.setLabel(prefixstr + bmrk.getName() + " Bookmark?");
                    }
                });

            button = new Submit("Delete this Bookmark");
            button.setButtonLabel("Delete this Bookmark");
            cancelbutton = new Submit("Cancel");
            cancelbutton.setButtonLabel("Cancel");
            Label spacer = new Label(" ");
            this.add(instruction);
            this.add(spacer);
            this.add(button);
            this.add(cancelbutton);
            this.addProcessListener(this);
        }

        public void process(FormSectionEvent e) {
            PageState s = e.getPageState();

            if(button.isSelected(s)) {
                BigDecimal bmrkID = new BigDecimal((String)getSelectionModel().getSelectedKey(s));
                Bookmark bmrk = Bookmark.retrieveBookmark(bmrkID);
                Bookmarks bmrkapp = (Bookmarks)Application.getCurrentApplication(s.getRequest());
                bmrkapp.removeBookmark(bmrk);
                getSelectionModel().clearSelection(s);
                reset(s);
            }
            s.reset(m_editContainer);
        }
    } //end delete form

    private class DeleteLinkListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            PageState ps = event.getPageState();
            m_editContainer.setVisibleComponent(ps, m_deleteForm);
        }
    }


}
