/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.arsdigita.subsite.ui;

import com.arsdigita.globalization.GlobalizedMessage;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class SubsiteGlobalizationUtil {

    public static final String BUNDLE_NAME = "com.arsdigita.subsite.ui.SubsiteResources";

    public static GlobalizedMessage globalize(final String key) {
        return new GlobalizedMessage(key, BUNDLE_NAME);
    }

    public static GlobalizedMessage globalize(final String key,
                                              final Object[] args) {
        return new GlobalizedMessage(key, BUNDLE_NAME, args);
    }

}
