package com.arsdigita.cms.contentassets.util;

import com.arsdigita.globalization.GlobalizedMessage;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 */
public class FileAttachmentGlobalizationUtil {
    
    public static final String BUNDLE_NAME = "com.arsdigita.cms.contentassets.FileAttachmentResources";
    
    public static GlobalizedMessage globalize(final String key) {
        return new GlobalizedMessage(key, BUNDLE_NAME);
    }
    
    public static GlobalizedMessage globalize(final String key, final Object[] args) {
        return new GlobalizedMessage(key, BUNDLE_NAME, args);
    }
    
}
