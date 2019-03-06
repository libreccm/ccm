/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.libreccm.theming;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import javax.servlet.ServletContext;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class ThemeResourceBundleControl extends ResourceBundle.Control {

    private final ServletContext servletContext;

    public ThemeResourceBundleControl(final ServletContext servletContext) {

        super();

        this.servletContext = servletContext;
    }

    @Override
    public ResourceBundle newBundle(final String baseName,
                                    final Locale locale,
                                    final String format,
                                    final ClassLoader loader,
                                    final boolean reload)
        throws IllegalAccessException, InstantiationException, IOException {

        final String bundleName = toBundleName(baseName, locale);
        final String fileName = String.format("%s.properties", bundleName);

        final InputStream stream = servletContext
            .getResourceAsStream(fileName);

        if (stream == null) {
            return null;
        } else {
            final InputStreamReader reader = new InputStreamReader(
                stream,
                StandardCharsets.UTF_8);
            return new PropertyResourceBundle(reader);
        }

    }

}
