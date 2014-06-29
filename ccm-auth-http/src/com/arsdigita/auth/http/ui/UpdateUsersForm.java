 /*
 * Copyright (C) 2001 ArsDigita Corporation. All Rights Reserved.
 *
 * The contents of this file are subject to the ArsDigita Public
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of
 * the License at http://www.arsdigita.com/ADPL.txt
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */

package com.arsdigita.auth.http.ui;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.List;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.form.FileUpload;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.list.ListCellRenderer;
import com.arsdigita.bebop.list.ListModel;
import com.arsdigita.bebop.list.ListModelBuilder;
import com.arsdigita.dispatcher.MultipartHttpServletRequest;
import com.arsdigita.ui.admin.GlobalizationUtil;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.xml.Element;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.Vector;
import org.apache.log4j.Logger;

public class UpdateUsersForm extends Form
    implements FormProcessListener, ListCellRenderer
{
    private static final Logger s_log =
        Logger.getLogger( UpdateUsersForm.class );

    private SimpleContainer m_uploadForm = new SimpleContainer();
    private SimpleContainer m_displayAction = new SimpleContainer();

    private FileUpload m_uploadFile = new FileUpload( "fileUpload" );
    private Submit m_uploadSubmit = new Submit( "Upload" );

    private RequestLocal m_invalidUsers = new RequestLocal();
    private RequestLocal m_failedUsers = new RequestLocal();
    private RequestLocal m_processed = new RequestLocal();
    private List m_invalidUserList;
    private List m_failedUserList;

    /**
     * 
     */
    public UpdateUsersForm() {
        super( "updateUsers" );

        setMethod( Form.POST );
        setEncType("multipart/form-data");

        m_uploadForm.add( new Label(GlobalizationUtil
                                    .globalize("auth.http.users_file")));
        m_uploadForm.add( m_uploadFile );
        m_uploadForm.add( m_uploadSubmit );

        add( m_uploadForm );

        m_invalidUserList = new List( new UserCSVListModel( m_invalidUsers ) );
        m_failedUserList = new List( new UserCSVListModel( m_failedUsers ) );
        m_invalidUserList.setCellRenderer( this );
        m_failedUserList.setCellRenderer( this );

        m_displayAction.add( new Label(GlobalizationUtil
                                       .globalize("auth.http.users_processed"),
                                       false));
     // m_displayAction.add( new Label( "<p>", false));
     // m_displayAction.add( new Label( "Invalid users:" ) );
        m_displayAction.add( new Label(GlobalizationUtil
                                       .globalize("auth.http.users_invalid"),
                                       false));
        m_displayAction.add( m_invalidUserList );
     // m_displayAction.add( new Label( "<p>", false ) );
     // m_displayAction.add( new Label( "Failed users:" ) );
        m_displayAction.add( new Label(GlobalizationUtil
                                       .globalize("auth.http.users_failed"),
                                       false));
        m_displayAction.add( m_failedUserList );

        add( m_displayAction );

        addProcessListener( this );
    }

    @Override
    public Component getComponent( List list, PageState state, Object value,
                                   String key, int index, boolean isSelected ) {
        String[] values = new String[1];
        values[0] = value.toString();
        return new Label( GlobalizationUtil
                          .globalize("auth.http.value_dummy", values) );
    }

    private class UserCSVListModel implements ListModelBuilder {
        private boolean m_locked = false;
        private RequestLocal m_users;

        public UserCSVListModel( RequestLocal users ) {
            m_users = users;
        }

        public ListModel makeModel( List l, PageState ps ) {
            Vector userList = (Vector)m_users.get(ps);
            final Iterator users = userList == null ? null : userList.iterator();

            return new ListModel() {
                    private String m_entry;

                    public Object getElement() {
                        return m_entry;
                    }

                    public String getKey() {
                        return m_entry;
                    }

                    public boolean next() {
                        if (users == null || !users.hasNext() ) {
                            m_entry = null;
                            return false;
                        }

                        m_entry = users.next().toString();
                        return true;
                    }
                };
        }

        public void lock() {
            m_locked = true;
        }

        public boolean isLocked() {
            return m_locked;
        }
    }

    public void process( FormSectionEvent ev ) {
        PageState ps = ev.getPageState();
        FormData data = ev.getFormData();

        if ( data.get( m_uploadSubmit.getName() ) != null ) {
            m_processed.set(ps, Boolean.TRUE);

            MultipartHttpServletRequest req =
                (MultipartHttpServletRequest) ps.getRequest();

            try {
                File f = req.getFile( m_uploadFile.getName() );
                UserCSVEntry.init( new FileReader( f ) );

                // Skip the first line (contains headers)
                UserCSVEntry.skipEntry();

                Vector failedUsers = new Vector();
                Vector invalidUsers = new Vector();
                m_failedUsers.set( ps, failedUsers );
                m_invalidUsers.set( ps, invalidUsers );

                while (UserCSVEntry.hasMore()) {
                    UserCSVEntry entry = UserCSVEntry.nextEntry();
                    if (entry == null) {
                        break;
                    }

                    if ( entry.isValid() ) {
                        try {
                            entry.createUser();
                            s_log.info( "Created User: " + entry );
                        } catch( Throwable ex ) {
                            ex.printStackTrace();
                            failedUsers.add( entry.toString() );
                            s_log.info( "Failed to create User: " + entry );
                        }
                    } else {
                        invalidUsers.add( entry.toString() );
                        s_log.info( "Invalid Entry: " + entry );
                    }
                }
            } catch( FileNotFoundException ex ) {
                throw new UncheckedWrapperException( ex );
            } catch( IOException ex ) {
                throw new UncheckedWrapperException( ex );
            }
        }
    }

    public void generateXML(PageState state,
                            Element parent) {
        m_displayAction.setVisible(state, m_processed.get(state) != null);

        super.generateXML(state, parent);
    }

    public void register( Page p ) {
        super.register( p );

        p.setVisibleDefault( m_displayAction, false );
    }


}
