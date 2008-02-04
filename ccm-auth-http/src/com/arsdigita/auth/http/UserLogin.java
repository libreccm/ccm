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

package com.arsdigita.auth.http;

import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.kernel.User;
import com.arsdigita.kernel.ACSObject;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.SessionManager;


public class UserLogin extends ACSObject {

    public static final String BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.auth.http.UserLogin";

    public static final String LOGIN = "login";
    public static final String USER = "user";

    public UserLogin() {
        super( BASE_DATA_OBJECT_TYPE );
    }

    public UserLogin( DataObject dobj ) {
        super( dobj );
    }
    
    
    public static UserLogin create(User user, 
                                   String login) {
        UserLogin map = new UserLogin();
        map.setUser(user);
        map.setLogin(login.toLowerCase());
        return map;
    }
    
    public static UserLogin findByLogin(String login) {
        DataCollection users = SessionManager.getSession()
            .retrieve(BASE_DATA_OBJECT_TYPE);
        
        users.addEqualsFilter(LOGIN, login.toLowerCase());
        
        if (users.next()) {
            DataObject dobj = users.getDataObject();
            users.close();
            return (UserLogin)DomainObjectFactory
                .newInstance(dobj);
        }
        return null;
    }

    public static UserLogin findByUser(User user) {
        DataCollection users = SessionManager.getSession()
            .retrieve(BASE_DATA_OBJECT_TYPE);
        
        users.addEqualsFilter(USER + "." + ACSObject.ID, 
                              user.getID());
        
        if (users.next()) {
            DataObject dobj = users.getDataObject();
            users.close();
            return (UserLogin)DomainObjectFactory
                .newInstance(dobj);
        }
        return null;
    }
    
    public User getUser() {
        DataObject dobj = (DataObject)get(USER);
        return (User)DomainObjectFactory.newInstance(dobj);
    }

    public void setUser(User user) {
        setAssociation(USER, user);
    }

    public String getLogin() {
        return get( LOGIN ).toString();
    }
    
    public void setLogin(String login) {
        set( LOGIN, login.toLowerCase() );
    }
}
