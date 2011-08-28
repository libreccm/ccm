/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.arsdigita.globalization;

import com.arsdigita.dispatcher.DispatcherHelper;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.KernelConfig;
import java.util.Enumeration;
import javax.servlet.ServletRequest;
import java.util.Locale;
import java.util.StringTokenizer;

/**
 *
 * @author Sören Bernstein
 */
public class GlobalizationHelper {

    // Don't instantiate
    private GlobalizationHelper() {
    }

    /**
     * This method returns the best matching locate for the request. In contrast to
     * the other methods available this one will also respect the supported_languages
     * config entry.
     *
     * @return The negotiated locale
     */
    public static java.util.Locale getNegotiatedLocale() {
        KernelConfig kernelConfig = Kernel.getConfig();

        // Set the preferedLocale to the default locale (first entry in the config parameter list)
        java.util.Locale preferedLocale = getPrefferedLocale();

        // The ACCEPTED_LANGUAGES from the client
        Enumeration locales = null;

        // Try to get the RequestContext
        try {

            // Get the SerrvletRequest
            ServletRequest request = ((ServletRequest) DispatcherHelper.getRequest());

            // Get the selected locale from the request, if any
            java.util.Locale selectedLocale = getSelectedLocale(request);
            if (selectedLocale != null && kernelConfig.hasLanguage(selectedLocale.getLanguage())) {
                preferedLocale = selectedLocale;
            } else {

                // 
                String lang = request.getParameter("lang");

                if (lang != null && kernelConfig.hasLanguage(lang)) {

                    preferedLocale = new Locale(lang);
                } else {

                    locales = request.getLocales();

                    // For everey element in the enumerator
                    while (locales.hasMoreElements()) {

                        // Test if the current locale is listed in the supported locales list
                        java.util.Locale curLocale = (Locale) locales.nextElement();
                        if (kernelConfig.hasLanguage(curLocale.getLanguage())) {
                            preferedLocale = curLocale;
                            break;
                        }
                    }
                }
            }
        } catch (NullPointerException ex) {
            // Don't have to do anything because I want to fall back to default language anyway
            // This case should only appear during setup
        } finally {

            return preferedLocale;

        }
    }

//    public static java.util.Locale getSystemLocale() {
//        
//    }
    private static Locale getPrefferedLocale() {
        KernelConfig kernelConfig = Kernel.getConfig();
        java.util.Locale preferedLocale = new java.util.Locale(kernelConfig.getDefaultLanguage(), "", "");
        return preferedLocale;
    }

    /**
     * Get the selected (as in fixed) locale from the ServletRequest
     * 
     * @return the selected locale as java.util.Locale or null if not defined
     */
    public static Locale getSelectedLocale(ServletRequest request) {
//        ServletRequest request = ((ServletRequest) DispatcherHelper.getRequest());
        String paramValue = request.getParameter("selLang");
        java.util.Locale selectedLocale = null;

        if (paramValue != null) {
            StringTokenizer paramValues = new StringTokenizer(paramValue, "_");
            if (paramValues.countTokens() > 1) {
                selectedLocale = new java.util.Locale(paramValues.nextToken(), paramValues.nextToken());
            } else {
                selectedLocale = new java.util.Locale(paramValues.nextToken());
            }
        }

        return selectedLocale;
    }
}
