/*
 * Copyright (C) 2010 pboy (pboy@barkhof.uni-bremen.de) All Rights Reserved.
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

package com.arsdigita.globalization;

// import com.arsdigita.runtime.ContextInitEvent;
import com.arsdigita.runtime.DomainInitEvent;
// import com.arsdigita.runtime.ConfigError;

import org.apache.log4j.Logger;

/**
 *
 * @author pb
 */
public class Initializer extends com.arsdigita.runtime.GenericInitializer {

    /** Creates a s_logging category with name = to the full name of class */
    private static Logger s_log = Logger.getLogger(Initializer.class);

    /** Config object for the UI package   */
    private static GlobalizationConfig s_conf = GlobalizationConfig.getConfig();

    
    /**
     * Constructor
     */
    public Initializer() {
    }


    /**
     * Implementation of the {@link Initializer#init(DomainInitEvent)}
     * method.
     *
     * @param evt The domain init event.
     */
    @Override
    public void init(DomainInitEvent evt) {
        s_log.debug("Core globalization domain initialization started.");

        LocaleNegotiator.setApplicationLocaleProvider(
                                              new ApplicationLocaleProvider());
        LocaleNegotiator.setClientLocaleProvider(new ClientLocaleProvider());
        LocaleNegotiator.setSystemLocaleProvider(new SystemLocaleProvider());

        String defaultCharset = s_conf.getDefaultCharset();
        Globalization.setDefaultCharset(defaultCharset);


        s_log.debug("Core globalization domain initialization completed");
    }

}
