/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.cms.contentassets;

import com.arsdigita.runtime.AbstractConfig;
import com.arsdigita.util.parameter.BooleanParameter;
import com.arsdigita.util.parameter.IntegerParameter;
import com.arsdigita.util.parameter.Parameter;

import org.apache.log4j.Logger;

/**
 * A record containing server-session scoped configuration properties.
 *
 * Accessors of this class may return null.  Developers should take
 * care to trap null return values in their code.
 * 
 * @see ContentSection#getConfig()
 *
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: $
 */
public final class ItemImageAttachmentConfig extends AbstractConfig {

    /** A logger instance to assist debugging.                                */
    private static final Logger s_log = Logger.getLogger(ItemImageAttachmentConfig.class);
    /** Singelton config object.  */
    private static ItemImageAttachmentConfig s_conf;

    /**
     * Gain a DublinCoreConfig object.
     *
     * Singelton pattern, don't instantiate a config object using the
     * constructor directly!
     * @return
     */
    public static synchronized ItemImageAttachmentConfig instanceOf() {
        if (s_conf == null) {
            s_conf = new ItemImageAttachmentConfig();
            s_conf.load();
        }

        return s_conf;
    }

    // ///////////////////////////////////////////////////////////////////////
    //
    // set of configuration parameters
    // Are the description and title properties available for
    // display/editing.  These properties are used by the 
    // ImageGallery content type.
    private final Parameter m_isImageStepDescriptionAndTitleShown;
    private final Parameter m_imageStepSortKey;

    /**
     * Do not instantiate this class directly.
     *
     * @see ContentSection#getConfig()
     **/
    public ItemImageAttachmentConfig() {

        m_isImageStepDescriptionAndTitleShown =
        new BooleanParameter(
                "com.arsdigita.cms.m_is_image_step_description_and_title_shown",
                Parameter.REQUIRED,
                Boolean.FALSE);

        m_imageStepSortKey = new IntegerParameter(
                "com.arsdigita.cms.image_step_sortkey",
                Parameter.REQUIRED,
                1);

        register(m_isImageStepDescriptionAndTitleShown);
        register(m_imageStepSortKey);

        loadInfo();
    }

    public final boolean getIsImageStepDescriptionAndTitleShown() {
        return ((Boolean) get(m_isImageStepDescriptionAndTitleShown)).booleanValue();
    }
    
    public final Integer getImageStepSortKey() {
        return (Integer) get(m_imageStepSortKey);
    }

}
