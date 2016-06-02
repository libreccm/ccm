/* -*- mode: java; c-basic-offset: 4; indent-tabs-mode: nil -*-
 *
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

import java.io.IOException;
import java.net.URLEncoder;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.ShortBufferException;

import sun.misc.BASE64Encoder;

/**
 * <p>
 * Attempt to authenticate a user against an IIS server.
 * </p>
 *
 * <p>
 * This servlet runs on an IIS server and is used as part of
 * the mechanism for automatically authenticating IE users on
 * CCM.
 * </p>
 *
 * <p><strong>Managing the public and private keys</strong></p>
 *
 * <p>
 * The IIS server needs to send a signed cookie to the CCM server.
 * To do this, the IIS server needs a private key, and the CCM
 * server needs the corresponding public key. This section
 * describes how to generate and distribute the keys.
 * </p>
 *
 * <p>
 * On the IIS server, you need to generate the private/public key
 * pair using the Java <code>keytool</code> utility. Run
 * <code>keytool</code> like this:
 * </p>
 *
 * <pre>
 * keytool -genkey -keyalg rsa -validity 365 -keypass 123456 \
 *         -dname 'cn=jones' -alias ccmkey
 * </pre>
 *
 * <p>
 * The <code>-dname</code> field does not matter. Make sure the
 * key store has password <code>123456</code> (or change the
 * <code>HTTPAuthServlet</code> to use a different password).
 * </p>
 *
 * <p>
 * The resultant <code>keystore</code> (or <code>.keystore</code>)
 * file must be copied to <code>$webappdir/WEB-INF/keystore</code>
 * (on the IIS server).
 * </p>
 *
 * <p>
 * Now on the IIS server export the public key into a separate
 * file like this:
 * </p>
 *
 * <pre>
 * keytool -export -alias ccmkey -file c:\temp\public_cert
 * </pre>
 *
 * <p>
 * You will need to type the keystore password in (<code>123456</code>).
 *
 * <p>
 * Copy the public key to the CCM server, and import it into the
 * keystore file on the CCM server like this:
 * </p>
 *
 * <p>
 * keytool -import -alias ccmkey -file /tmp/public_cert
 * </p>
 *
 * <p>
 * Make sure the <code>keystore</code> (or <code>.keystore</code>)
 * file on the CCM file is copied to <code>$webappdir/WEB-INF/keystore</code>.
 * </p>
 *
 * <p>
 * Ensure that the public key has alias <code>ccmkey</code>
 * and password <code>123456</code>.
 * </p>
 *
 * @author Matt Booth, documentation by Richard W.M. Jones.
 * @see com.arsdigita.auth.ntlm.NTLMLoginModule
 */
public class HTTPAuthServlet extends HttpServlet {
    private Cipher m_encrypt;

    // Leave same for backwards compatability
    public static final String AUTH = "__camden_auth";
    public static final String MAGIC = "camden";

    /**
     * Open the keystore and look for a key with the given alias. Create
     * the encryption (ie. signing) cipher object.
     */
    public void init() throws ServletException
    {
        // Create the encryption cypher.
        PrivateKey privateKey = HTTPAuth.getConfig().getPrivateKey();

        try {
            m_encrypt = Cipher.getInstance(HTTPAuth.getConfig().getKeyCypher());
            m_encrypt.init (Cipher.ENCRYPT_MODE, privateKey);
        }
        catch (GeneralSecurityException ex) {
            throw new ServletException (ex);
        }
    }


    public void doGet( HttpServletRequest req, HttpServletResponse res )
        throws IOException, ServletException {
        String nonce = req.getParameter ("nonce");

        // If no nonce, just return the public key
        if ( nonce == null ) {
            throw new ServletException ("nonce parameter missing");
        }

        String returnURL = req.getParameter ("returnURL");
        if ( returnURL == null ) {
            throw new ServletException( "returnURL was not specified" );
        }

        // Generate an authorisation string of the form MAGICnonce|USERIDENT
        String authString = MAGIC + nonce +
                            "|" + getRemoteUser (req);

        try {
            byte[] encBytes =
                new byte[ m_encrypt.getOutputSize( authString.length() ) ];
            m_encrypt.doFinal( authString.getBytes(), 0, authString.length(), encBytes );

            authString = new BASE64Encoder().encode( encBytes );
            authString = URLEncoder.encode( authString );
        } catch( IllegalBlockSizeException e ) {
            throw new ServletException( e );
        } catch( BadPaddingException e ) {
            throw new ServletException( e );
        } catch( ShortBufferException e ) {
            throw new ServletException( e );
        }

        if ( returnURL.indexOf( "?" ) == -1 ) {
            returnURL += "?";
        } else {
            returnURL += "&";
        }
        
        returnURL += AUTH + "=" + authString;

        res.sendRedirect( returnURL );
    }

    /**
     * Note by RWMJ: I separated this out into a separate function just
     * to make it simpler to test by hacking in hard-coded values.
     */
    private String getRemoteUser (HttpServletRequest req) {
        if (HTTPAuth.getConfig().isDebugMode()) {
            return "example\\\\administrator";
        } else {
            return req.getRemoteUser ();
        }
    }
}
