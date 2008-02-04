/*
 * Copyright (C) 2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.test;

import com.arsdigita.util.UncheckedWrapperException;
import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.SubmitButton;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebForm;
import com.meterware.httpunit.WebLink;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;
import java.io.IOException;
import org.xml.sax.SAXException;

/**
 * Class CoreHttpUnit
 * 
 * @author jorris@redhat.com
 * @version $Revision $1 $ $Date: 2004/08/16 $
 */
public class CoreHttpUnit {
    private final String m_swaEmail;
    private final String m_swaPasswd;
    private final String m_rootURL;
    private final WebConversation m_wc  = new WebConversation();

    public CoreHttpUnit(final String swaEmail,
                        final String swaPasswd,
                        final String rootURL) {
        m_swaEmail = swaEmail;
        m_swaPasswd = swaPasswd;
        m_rootURL = rootURL;
    }

    public String getRootURL() {
        return m_rootURL;
    }

    public String getSwaEmail() {
        return m_swaEmail;
    }

    public void loginSWA() throws HttpUnitException {
        login(m_swaEmail, m_swaPasswd);
    }

    public void login(final String email, final String password) throws HttpUnitException {
        WebRequest     req = new GetMethodWebRequest( m_rootURL + "/register" );
        try {
            WebResponse   resp = m_wc.getResponse( req );
            WebForm login = resp.getFormWithName("user-login");
            login.setParameter("email", email);
            login.setParameter("password", password);
            login.submit();
        } catch (IOException e) {
            throw new HttpUnitException(e.getMessage(), e);
        } catch (SAXException e) {
            throw new HttpUnitException(e.getMessage(), e);
        }

    }

    public WebResponse gotoURL(final String urlPart) {
        final WebRequest     req = new GetMethodWebRequest( m_rootURL + urlPart );
        return getResponse( req );
    }

    public void createUser(final String firstName,
                           final String lastName,
                           final String password,
                           final String email,
                           final String screenName) {

        final WebRequest     req = new GetMethodWebRequest( m_rootURL + "/admin" );
        getResponse( req );
        clickLink("Create new user");
        final WebForm form = getFormWithName("user-add-form");


        form.setParameter("firstname", firstName);
        form.setParameter("lastname", lastName);
        form.setParameter("password", password);
        form.setParameter("password_confirmation", password);
        form.setParameter("question", password);
        form.setParameter("answer", password);
        form.setParameter("email", email);
        form.setParameter("screenname", screenName);
        submitForm(form);

    }


    public WebResponse clickLink(final String linkText) {
        WebResponse resp = m_wc.getCurrentPage();
        WebLink link;
        try {
            link = resp.getLinkWith(linkText);
        } catch (SAXException e) {
            throw new HttpUnitException("SAX error getting link: " +
                    linkText + " Message: " +
                    e.getMessage(), e);
        }
        if (link == null) {
            throw new NoSuchLinkException(linkText, resp);
        }
        try {
            resp = link.click();
        } catch (IOException e) {
            throw new UncheckedWrapperException(e.getMessage(), e);
        } catch (SAXException e) {
            throw new UncheckedWrapperException(e.getMessage(), e);
        }
        return resp;
    }

    public WebResponse getCurrentPage() {
        WebResponse resp = m_wc.getCurrentPage();
        return resp;
    }

    public WebResponse getResponse(final WebRequest req) {
        try {
            final WebResponse resp = m_wc.getResponse( req );
            return resp;
        } catch (IOException e) {
            throw new HttpUnitException(e.getMessage(), e);
        } catch (SAXException e) {
            throw new HttpUnitException(e.getMessage(), e);
        }
    }

    public WebForm getFormWithName(final String formName) {
        final WebResponse resp = m_wc.getCurrentPage();
        try {
            return resp.getFormWithName(formName);
        } catch (SAXException e) {
            throw new HttpUnitException(e.getMessage(), e);
        }
    }

    public WebResponse submitForm(final WebForm form) {
        try {
            return form.submit();
        } catch (IOException e) {
            throw new HttpUnitException(e.getMessage(), e);
        } catch (SAXException e) {
            throw new HttpUnitException(e.getMessage(), e);
        }
    }

    public WebResponse submitForm(final WebForm form, final String buttonName) {
        try {
            final SubmitButton button = form.getSubmitButton(buttonName);
            return form.submit(button);
        } catch (IOException e) {
            throw new HttpUnitException(e.getMessage(), e);
        } catch (SAXException e) {
            throw new HttpUnitException(e.getMessage(), e);
        }
    }

}
