/*
 * Copyright (C) 2011 Peter boy (pboy@barkhof.uni-bremen.de
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

package com.arsdigita.faq;

import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.page.BebopApplicationServlet;
import com.arsdigita.faq.ui.FaqAdminView;
import com.arsdigita.faq.ui.FaqPage;
import com.arsdigita.faq.ui.FaqUserView;
import javax.servlet.ServletException;

import org.apache.log4j.Logger;

/**
 *
 * @author pb
 */
public class FaqServlet  extends BebopApplicationServlet {

    /** Private logger instance to faciliate debugging procedures         */
    private static final Logger s_log = Logger.getLogger(FaqServlet.class);


    @Override
    public void init() throws ServletException {
        super.init();
        s_log.debug("creating FAQ page");

        Page index = buildIndexPage();
        Page admin = buildAdminIndexPage();

        put("/", index);
        put("/index.jsp", index);
        put("/one.jsp", index);

  //    put("admin", admin);
  //    put("admin/index.jsp", admin);

    }

    /**
     *
     * @return
     */
    private FaqPage buildIndexPage() {
        FaqPage p = new FaqPage();

        p.add(new FaqUserView());

        /*CommentsService commentsService =
         *  new CommentsService(req, FaqHelper.getFaqID(req));
         *  p.add(commentsService.buildCommentsComponent(
         *  "comments/one-object"));
         */

        p.lock();
        return p;
    }


    /**
     *
     * @return
     */
    private FaqPage buildAdminIndexPage() {

        FaqPage p = new FaqPage("admin");

        FaqAdminView faqAdminTabs = new FaqAdminView();
        faqAdminTabs.setKey("FaqAdminTabs");
        p.add(faqAdminTabs);

        p.lock();
        return p;
    }

}
