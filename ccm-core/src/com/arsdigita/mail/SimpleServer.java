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
package com.arsdigita.mail;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import javax.mail.internet.MimeUtility;
import org.apache.log4j.Logger;

/**
 * <p>A simple SMTP server for testing the ACS Mail service. This
 * class provides one method, SimpleServer.startup(), that starts SMTP
 * servers running on a range of ports on the local host.  By default
 * it redirects all mail traffic to the server running in NORMAL mode.

 * <p>Using the SimpleServer.setMode() method, clients can
 * force connections to any of the available servers running in that
 * particular testing mode.  The server threads are maintained
 * statically, so additional calls to startup() are simply ignored.
 *
 * <p>Only the following SMTP operating modes are supported.  Use
 * these constants as the argument to setMode() to redirect SMTP
 * traffic to the appropriate server.
 *
 * <ul>
 *     <li>NORMAL
 *     <li>INTERRUPT
 *     <li>HANGING
 *     <li>UNAVAILABLE
 *     <li>TRANSACTIONABORT
 *     <li>INSUFFICIENTMEM
 *     <li>UNRECOGNIZEDCMD
 *     <li>SYNTAXERROR
 *     <li>UNSUPPORTEDMETH
 *     <li>TRANSACTIONFAILED
 * </ul>
 *
 *
 * @author Ron Henderson 
 * @author Joseph Bank 
 * @author Stefan Deusch 
 *
 * @version $Id: SimpleServer.java 287 2005-02-22 00:29:02Z sskracic $
 */

public class SimpleServer extends Thread implements ServerModes {

    private static final Logger s_log =
        Logger.getLogger(SimpleServer.class);

    /**
     * Socket to recieve client data from
     */

    private ServerSocket m_server;


    /**
     * Server operational mode.
     */

    private int m_mode;

    /**
     * Tracks the number of sucessfully processed connections to any
     * SimpleServer.
     */

    private static int s_success_count = 0;

    /**
     * Gets the number of succesfully processed connections to any
     * SimpleServer.
     * @return the number of succesfully processed connections to any
     * SimpleServer.
     */

    public static synchronized int getSuccessCount() {
        return s_success_count;
    }

    /**
     * Increment the number of successfully processed connections to
     * any SimpleServer (package-level access).
     */

    static synchronized void incrSuccessCount() {
        s_success_count++;
    }

    /**
     * Tracks the number of messages received.
     */

    private static int s_received_count = 0;

    /**
     * Gets the number of messages received.
     * @return the number of messages.
     */

    public static synchronized int getReceivedCount() {
        return s_received_count;
    }

    /**
     * Increment the number of messages received.
     */

    static synchronized void incrReceivedCount() {
        s_received_count++;
    }

    /**
     * Resets the counters of received and successful messages.
     */

    public static void reset() {
        s_received_count = s_success_count = 0;
    }

    /**
     * Server host. Not really useful for anything.
     */

    private static String s_soHost = "localhost";

    static synchronized void setSoHost (String host) {
        s_soHost = host;
    }

    static synchronized String getSoHost () {
        return s_soHost;
    }

    /**
     * Server listening port.  Note that ports below 1024 are only
     * accessible by privileged users (root).  This actually defines
     * the beginning of the range of ports need to start all possible
     * servers.
     */

    private static int s_soPort = 5000;

    static synchronized void setSoPort (int port) {
        s_soPort = port;
    }

    static synchronized int getSoPort () {
        return s_soPort;
    }

    /**
     * Socket timeout [ms].  A value of zero will disable timeouts on
     * the socket.  A non-zero value is currently required on Linux,
     * since there appears to be a bug in the socket library that
     * prevents the socket from properly closing.
     */

    private static int s_soTimeout =
        System.getProperty("os.name").equals("Linux") ? 1000 : 0;

    static synchronized void setSoTimeout (int msec) {
        s_soTimeout = msec;
    }

    static synchronized int getSoTimeout () {
        return s_soTimeout;
    }

    /**
     * How long to sleep [ms] before trying to open another
     * connection.  This part of SimpleServer's internal error
     * handling in the case of a BindException (address already in
     * use).
     */

    private static int s_soRetryDelay = 200;

    static synchronized void setSoRetryDelay (int msec) {
        s_soRetryDelay = msec;
    }

    static synchronized int getSoRetryDelay () {
        return s_soRetryDelay;
    }

    // Number of SimpleServers to create for use in testing.  Each
    // server is responsible for handling a different operating mode
    // (NORMAL, HANGING, etc.).

    private static final int MAX_SERVERS = 10;

    // Array for storing each SimpleServer thread created by the
    // SimpleServer.startup() method.

    private static SimpleServer server[] = new SimpleServer[MAX_SERVERS];

    // Error message for display when system fails to start

    private static final String help =
        "Try changing the value of soPort in the mail initializer.\n" +
        "Note that SimpleServer requires " + MAX_SERVERS + " contiguous free ports\n" +
        "starting from the value of soPort.";

    // A flag to make sure that we only start the servers running
    // once.

    private static boolean s_init = false;

    /**
     * Initializes the SimpleServer system.  Starts a listener
     * for each type of server operating mode on a specific port.  If
     * any servers fail to initialize (usually because the port is
     * busy), the entire system will fail and report the error.
     */

    public synchronized static void startup () {

        if (!s_init) {

            for (int i = 0; i < MAX_SERVERS; i++) {

                int mode = i;
                int port = i + s_soPort;

                try {
                    server[i] = new SimpleServer(port, mode);
                    server[i].setDaemon(true);
                    server[i].start();
                } catch (java.net.BindException ex) {
                    s_log.error("Unable to bind to port " + port, ex);
                    s_log.error(help);
                    return;
                }
            }

            s_init = true;
        }

        setMode(NORMAL);
    }

    /**
     * Configures the mail system to communicate with the
     * correct server.  After calling this method, the next message
     * sent using Mail.send() will connect to the appropriate server
     * for requested responsee mode.
     */

    public static void setMode (int mode) {
        int port = s_soPort + mode;
        Mail.setSmtpServer(s_soHost,Integer.toString(port));
    }

    /**
     * Default constructor. Changes Mail setting to redirect all
     * traffic through this server. Note that the server will block
     * until you call accept() to specify the operational mode.
     *
     * This can only be invoked from the SimpleServer.startup()
     * method.
     */

    SimpleServer (int port, int mode) throws BindException
    {
        // Operating mode for this server

        m_mode = mode;

        // Try to open a server socket on the service port.
        // Note that you can't choose a port less than 1024 unless
        // you're a privileged user (root)

        try {
            m_server = new ServerSocket(port);
            m_server.setSoTimeout(s_soTimeout);
        } catch (java.io.IOException ex) {
            throw new BindException("port: " + port);
        }

        s_log.debug ("SimpleServer: ready on port " + port + " in mode " + mode);
    }

    /**
     * The run method for SimpleServer.  This accepts a connection on
     * the specified port and hands the Socket to a SocketHandler for
     * processing.
     */

    public void run() {
        while (true) {
            try {

                // Accept the next connection
                Socket socket = m_server.accept();

                // Create handler to process it
                SocketHandler handler =
                    new SocketHandler(socket,m_mode);
                handler.start();

            } catch (SocketException e) {
                // occurs when Mail client closes connection before
                // waiting to get a response to the QUIT command.
                // This is a known bug in JavaMail 1.2.
            } catch (InterruptedIOException iex) {
                // expected on Linux, which appears to have a bug in
                // the socket library (you cannot shut down a socket,
                // you have to wait for it to timeout)
            } catch (IOException iex) {
                iex.printStackTrace();
            }
        }
    }
}


/**
 * Class for respresenting expected SMTP response messages
 * and the correct reply codes.
 */

final class Response {

    private String  m_expect;
    private String  m_send;
    private boolean m_allow_closed;

    Response (String expect, String send, boolean allow_closed) {
        m_expect       = expect;
        m_send         = send;
        m_allow_closed = allow_closed;
    }

    Response (String expect, String send) {
        this(expect, send, false);
    }

    String getSend() {
        return m_send;
    }

    boolean allowClosed() {
        return m_allow_closed;
    }

    boolean isValid (String response) {
        return response.startsWith(m_expect);
    }
}

/**
 *   Class for handling socket I/O.
 */
final class SocketHandler extends Thread implements ServerModes {

    /**
     * Response mode for this handler (NORMAL, INTERRUPT, etc.)
     */

    private int m_mode;

    /**
     * Socket used to communicate with the client
     */

    private Socket m_sock;

    /**
     * Input stream associated with the socket
     */

    private BufferedReader m_is;

    /**
     * Output stream associated with the socket
     */

    private DataOutputStream m_os;

    /**
     * Flag for whether the handler is currently serving
     * requests.
     */

    private boolean m_serving = true;

    /**
     * Amount of time to sleep [ms] when simulating a timeout.
     */

    private static final int HANGING_TIMEOUT = 30000;

    /**
     * Default decoding to use for the input stream (fits most western
     * European and US encodings)
     */

    private static final String ENCODING = "iso-8859-1";

    /**
     * Default greeting to send on startup.
     */

    private static final String GREETING = "220 Welcome, server waiting";

    /**
     * Command and response codes for the SMTP protocol
     */

    private static Response[] responses = {
        new Response("HELO",      "250"),
        new Response("EHLO",      "250"),
        new Response("MAIL FROM", "250"),
        new Response("RCPT TO",   "250"),
        new Response("DATA",      "354"),
        new Response(".",         "250"),
        new Response("QUIT",      "221", true),
    };

    private static String[] error = {
        "421 Service not available, shutdown transmission line",
        "450 mailbox unavailable",
        "451 requested action aborted",
        "452 requested action not taken, insufficient system storage",
        "500 syntax error, command unrecognized",
        "501 syntax error in parameters or arguments",
        "502 command not implemented",
        "550 no such user",
        "554 transaction failed"
    };

    /**
     * Default constructor.
     *
     * @param sock the Socket to use when communicating
     * @param mode the response mode to simulate
     */

    public SocketHandler (Socket sock, int mode) {

        // Save a copy of the socket and response mode for this
        // handler to use.

        m_sock = sock;
        m_mode = mode;

        // Open I/O streams for communicating with client

        try {
            m_os = new DataOutputStream(m_sock.getOutputStream());
            m_is = new BufferedReader
                (new InputStreamReader
                 (MimeUtility.decode(m_sock.getInputStream(),
                                     ENCODING)));
        } catch (javax.mail.MessagingException me) {

            // go w/o any decoding in this case

            try {
                m_is = new BufferedReader
                    (new InputStreamReader(m_sock.getInputStream()));
            } catch (IOException ioe){
                ioe.printStackTrace();
            }

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        // I/O streams are opened, so we're ready to start
        // processing.
    }

    /**
     * Main processing thread.
     */

    public void run() {

        try {
            writeln(GREETING);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        // Process input from the client

        try {
            while (m_serving) {
                if (!respond()) {
                    break;
                }
            }
        } catch (IOException e) {
            // ignore
        }

        // Update statistics

        SimpleServer.incrReceivedCount();
        SimpleServer.incrSuccessCount();

        // Close I/O streams (we're done)

        try {
            m_os.close();
            m_is.close();
            m_sock.close();
        } catch (IOException ic) {
            ic.printStackTrace();
        }
    }

    /**
     * Close the connection and stop serving the client
     */

    private void close() {
        m_serving = false;
    }

    /**
     * Process one line from the input stream.
     */

    public boolean respond()
        throws IOException, SocketException
    {
        String line = null;

        try {
            line = m_is.readLine();
        } catch (Exception e){
            // could not read from InputStream
            return false;
        }

        // Respond by either simulating a failure mode or responding
        // correctly.

        if (m_mode == NORMAL) {

            // process the input from the client

            for (int i = 0; i < responses.length; i++) {
                if (responses[i].isValid(line)) {
                    try {
                        writeln(responses[i].getSend());
                    } catch (IOException ioe) {
                        if (!responses[i].allowClosed()) {
                            throw ioe;
                        }
                    }
                    break;
                }

                if (line.startsWith("QUIT")) {
                    return false;
                }
            }
        } else {
            fail(); // Respond with the correct failure mode
        }

        return true;
    }

    /**
     * Utility method to write data to the client socket.
     */

    private void writeln (String s)
        throws IOException
    {
        m_os.writeBytes(s);
        m_os.writeChar('\n');
        m_os.flush();
    }

    /**
     * Respond with the appropriate error code to the client.
     */

    public void fail()
        throws IOException
    {
        switch (m_mode) {

        case INTERRUPT:
            close();
            break;

        case HANGING:
            try {
                sleep(HANGING_TIMEOUT);
            } catch (InterruptedException e) {
                // ignore
            }
            break;

            // The following just require sending back the appropriate
            // response to the client.

        case UNAVAILABLE:
            writeln(error[0]);
            break;
        case TRANSACTIONABORT:
            writeln(error[2]);
            break;
        case INSUFFICIENTMEM:
            writeln(error[3]);
            break;
        case UNRECOGNIZEDCMD:
            writeln(error[4]);
            break;
        case SYNTAXERROR:
            writeln(error[5]);
            break;
        case UNSUPPORTEDMETH:
            writeln(error[6]);
            break;
        case TRANSACTIONFAILED:
            writeln(error[8]);
            break;
        }
    }
}

/**
 * Constants used to define the various SMTP operating modes.
 */

interface ServerModes {
    public final static int NORMAL            = 0;
    public final static int INTERRUPT         = 1;
    public final static int HANGING           = 2;
    public final static int UNAVAILABLE       = 3;
    public final static int TRANSACTIONABORT  = 4;
    public final static int INSUFFICIENTMEM   = 5;
    public final static int UNRECOGNIZEDCMD   = 6;
    public final static int SYNTAXERROR       = 7;
    public final static int UNSUPPORTEDMETH   = 8;
    public final static int TRANSACTIONFAILED = 9;
}
