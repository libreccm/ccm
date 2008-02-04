/*
 * Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.web;

import com.arsdigita.domain.DomainObject;
import com.arsdigita.kernel.ACSObjectInstantiator;
import com.arsdigita.kernel.security.UserContext;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.util.DummyServletConfig;
import com.arsdigita.web.Application;
import com.arsdigita.web.ApplicationSetup;
import java.io.IOException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;

/**
 * Tests Application dispatching
 *
 */
public class ApplicationDispatchTest extends WebTestCase {
   public ApplicationDispatchTest(String name) {
      super(name);
   }

   private static Logger s_log = Logger.getLogger(ApplicationDispatchTest.class);
   private TestApplication m_app;
   private BaseApplicationServlet m_appServlet;

   protected void setUp() throws Exception {
      super.setUp();

      setupApplication();

      m_app = TestApplication.createTestApp("TestApplication");

      setupApplicationServlet();
   }

   private void setupApplicationServlet() throws ServletException {
      m_appServlet = new BaseApplicationServlet() {
         protected void doService(HttpServletRequest sreq,
                                  HttpServletResponse sresp,
                                  Application app)
               throws ServletException, IOException {

            s_log.info("Dispatching to application!");
         }

         protected UserContext getUserContext(HttpServletRequest sreq,
                                    HttpServletResponse sresp) {
            UserContext ctx =  super.getUserContext(sreq, sresp);
/*
            try {
                User user = new User() {
                    public EmailAddress getPrimaryEmail() {
                        EmailAddress address = Kernel.getSystemParty().getPrimaryEmail();
                        return address;
                    }
                };
                ctx.login(user);
            } catch (LoginException e) {
               throw new UncheckedWrapperException("Login failure", e);
            }
            */
            return ctx;
         }
      };
      ServletConfig config = new DummyServletConfig("TestApplicationServlet");
      m_appServlet.init(config);

      this.m_container.addServletMapping(m_app.getServletPath(), m_appServlet);
   }

   static void setupApplication() {
      ApplicationSetup setup = new ApplicationSetup(s_log);

      setup.setApplicationObjectType(TestApplication.BASE_DATA_OBJECT_TYPE);
      setup.setTitle("TestApplication");
      setup.setDescription
            ("This is a test application.");
      setup.setKey("testapp");
      setup.setDispatcherClass("com.arsdigita.is.this.really.Needed");
      setup.setStylesheet("/packages/webtest/xsl/test.xsl");

      setup.setInstantiator(new ACSObjectInstantiator() {
         public DomainObject doNewInstance(DataObject dataObject) {
            return new TestApplication(dataObject);
         }
      });

      setup.run();
   }

   public void testApplicationCache() throws Exception {
      m_container.getRequest().getServletURL().setPathInfo("/testapp/");
      m_container.dispatch(m_dispatcher);
   }

}
