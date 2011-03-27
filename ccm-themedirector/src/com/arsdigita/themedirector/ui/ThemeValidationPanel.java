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
 */

package com.arsdigita.themedirector.ui;

import com.arsdigita.bebop.ActionLink;
import com.arsdigita.bebop.GridPanel;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.bebop.SimpleComponent;
import com.arsdigita.themedirector.ThemeDirectorConstants;
import com.arsdigita.themedirector.Theme;
import com.arsdigita.themedirector.ui.listeners.LoggingErrorListener;
import com.arsdigita.themedirector.util.GlobalizationUtil;
import com.arsdigita.themedirector.util.TransformerExceptionContainer;
import com.arsdigita.web.Web;
import com.arsdigita.xml.Element;
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.net.URL;
import java.util.Collection;
import java.util.Iterator;
import com.arsdigita.templating.XSLTemplate;
import com.arsdigita.templating.WrappedTransformerException;
import javax.xml.transform.ErrorListener;

import org.apache.log4j.Logger;
import javax.xml.transform.TransformerException;

import org.apache.log4j.Logger;

/**
 *  This displays information about the results of running a validation
 *  test on all of the stylesheets for a given theme.  It also includes
 *  links to "revalidate" and to return the viewing the theme.
 *
 *  @author Randy Graebner &lt;randyg@redhat.com&gt;
 */
class ThemeValidationPanel extends GridPanel implements ThemeDirectorConstants {
    
    private static final Logger s_log = 
        Logger.getLogger(ThemeValidationPanel.class);

    ThemeSelectionModel m_model;

    RequestLocal m_listener;

    /**
     *  This creates a new validation panel
     *  @param model This is the selection model so that the panel knows
     *  which theme to operation on
     *  @param container This is the parent container that holds this theme.
     *  When the user wants to quit validation and return to where they were,
     *  the visibility of the passed in container inverted (if it is visible
     *  then it becomes invisible, if it is invisible, it becomes visible) and
     *  this item becomes invisible.
     */
    // TODO: passing in the container is a hackish way to do the visibility...
    // is there a better way to do this?
    ThemeValidationPanel(ThemeSelectionModel model, 
                         SimpleComponent parentContainer) {
        super(1);
        m_model = model;
        Label results = 
            new Label(GlobalizationUtil.globalize("theme.validation_results"));
        results.setFontWeight(Label.BOLD);
        add(results);
        m_listener = new RequestLocal();

        add(new ValidationResults());

        ActionLink revalidateLink = 
            new ActionLink(new Label(GlobalizationUtil.globalize
                                     ("theme.revalidate_theme")));
        revalidateLink.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    validateStylesheets(e.getPageState());
                }
            });
        add(revalidateLink);
           
        ToggleActionLink returnLink = new ToggleActionLink
            (new Label(GlobalizationUtil.globalize("theme.return_to_previous")));
        returnLink.addToggleComponent(this);
        returnLink.addToggleComponent(parentContainer);
        add(returnLink);
    }


    /**
     *  This is an ActionLink that, when clicked, toggles the visibility
     *  of the components.  This could be done all as an anonymous inner
     *  class but this is more clear as to what is being done.
     */
    private class ToggleActionLink extends ActionLink implements ActionListener {
        private ArrayList m_components;
        
        ToggleActionLink(Label name) {
            super(name);
            m_components = new ArrayList();
            addActionListener(this);
        }

        public void actionPerformed(ActionEvent e) {
            Iterator iter = m_components.iterator();
            PageState state = e.getPageState();
            while(iter.hasNext()) {
                SimpleComponent comp = (SimpleComponent)iter.next();
                comp.setVisible(state, !comp.isVisible(state));
            }
        }
        
        protected void addToggleComponent(SimpleComponent component) {
            m_components.add(component);
        }
    }


    /**
     *  This validates the stylesheets for the selected theme (designated
     *  by the ThemeSelectionModel passed in to the constructor of this
     *  class).  This should be called if the user wants the system
     *  @return true if the stylesheets are all valid and false otherwise
     */
    public boolean validateStylesheets(PageState state) {
        LoggingErrorListener listener = new LoggingErrorListener();
        m_listener.set(state, listener);
        return validateStylesheets(state, listener);
    }


    /**
     *  This method sets up the validation by finding the correct base
     *  directory and setting up other necessary initialization variables
     */ 
    private boolean validateStylesheets(PageState state, 
                                        LoggingErrorListener listener) {
        // this should verify the stylesheets and then present
        // any error messages that are found
        Theme theme = m_model.getSelectedTheme(state);

        // The call to resolve returns a url similar to this:
        // http://localhost:9008/resource/ccm-ldn-theme,ROOT/__ccm__/apps/theme/xsl/index.xs
        //
        // TODO: This is VERY UGLY! Bad style to code a path into source code!
        // String base = "http://" + Web.getConfig().getHost().toString() +
        //               "/resource/ccm-ldn-theme/";
        String base = "http://" + Web.getConfig().getHost().toString() +
                      "/resource/ROOT/";
        File currentRoot = new File(Web.getServletContext().getRealPath("/"));
        File devDir = new File(currentRoot, DEV_THEMES_BASE_DIR + 
                               theme.getURL());

        // TODO: There has to be a better way to do this
        String stylesheetPath = base + 
            DEV_THEMES_BASE_DIR + theme.getURL();

        if (s_log.isDebugEnabled()) {
            s_log.debug("Path is " + stylesheetPath);
        }

        checkFiles(devDir, stylesheetPath, listener);

        return !listener.hasErrors();
    }

    /**
     *   This method does the actual validation of the stylesheets.
     *   It starts witht he base directory and then iterates through
     *   the entire folder subtree, loading every single available xsl
     *   file.
     */
    private void checkFiles(File baseDirectory, String basePath,
                            ErrorListener listener) {
        File[] list = baseDirectory.listFiles(new XSLFileFilter());
        if (list == null) {
            return;
        }

        // if the listener is not used then we need to find a different
        // way to log the errors.  We do this by catching the 
        // WrappedTransformerException and if the listener does
        // not already have an error we then log the error and all
        // future caught errors.  This is sort of a hack but I am not
        // sure of a better way around it
        boolean transformUsesListener = true;

        for (int i = 0; i < list.length; i ++) {
            if (list[i].isDirectory()) {
                // Only check top level XSL - the rest are xsl:import'd
                //checkFiles(list[i], basePath + "/" + list[i].getName(),
                //           listener);
            } else {
                String filePath = basePath + "/" + list[i].getName();
                try {
                    URL stylesheetURL = new URL(filePath);
                    if (s_log.isDebugEnabled()) {
                        s_log.debug("Validating " + stylesheetURL);
                    }
                    XSLTemplate template = new XSLTemplate(stylesheetURL,
                                                           listener);
                } catch (WrappedTransformerException we) {
                    if (transformUsesListener && 
                        listener instanceof LoggingErrorListener &&
                        !((LoggingErrorListener)listener).hasErrors()) {
                        transformUsesListener = false;
                    } 
                    if (!transformUsesListener) {
                        TransformerException transEx = 
                            (TransformerException)we.getRootCause();
                        try {
                            listener.error(transEx);
                        } catch (TransformerException transformerEx) {
                            s_log.error("Error logging the exception " + 
                                        transEx.getMessage(),
                                        transformerEx);
                            transEx.printStackTrace();
                        }
                    } 
                    s_log.debug("Wrapper excpetion thrown");
                } catch (Exception exp) {
                    s_log.warn("Error creating template that was not a " +
                               " standard wrapper transformer exception.",
                               exp);
                }
            }
        }
    }


    /**
     *  This is simply used so that only directories and xsl files are
     *  examined
     */
    private static class XSLFileFilter implements FileFilter {
        public boolean accept(File pathname) {
            return pathname.isDirectory() || 
                pathname.getName().endsWith(".xsl");
        }
    }

    
    private class ValidationResults extends SimpleContainer {
        private Label m_noErrorsLabel;
        private Label m_errorsLabel;

        ValidationResults() {
            super();
            m_noErrorsLabel = new Label
                (GlobalizationUtil.globalize("themes.no_validation_errors"));
            m_errorsLabel = new Label
                (GlobalizationUtil.globalize("themes.validation_errors"));
        }

        public void generateXML(PageState state, Element p) {
            if ( isVisible(state) ) {

                LoggingErrorListener listener = 
                    (LoggingErrorListener)m_listener.get(state);
                if (listener == null) {
                    // the validation was not run so this does not display
                    // anything.
                } else {
                    if (!listener.hasErrors()) {
                        m_noErrorsLabel.generateXML(state, p);
                    } else {
                        m_errorsLabel.generateXML(state, p);

                        printMessages(XSL_VALIDATION_WARNINGS, p,
                                      listener.getWarnings());
                        printMessages(XSL_VALIDATION_ERRORS, p,
                                      listener.getErrors());
                        printMessages(XSL_VALIDATION_FATALS, p,
                                      listener.getFatals());
                    }
                }
            }
        }


        /**
         *  if the collection size > 0 then it prints out the xml
         *  to display the messages
         */
        private void printMessages(String name, Element parent, 
                                   Collection messages) {
            if (messages.size() > 0) {
                Element element = parent.newChildElement(name, XML_NS);
                Iterator iter = messages.iterator();
                while (iter.hasNext()) {
                    Element error = element.newChildElement(XSL_ERROR_INFO, XML_NS);
                    TransformerExceptionContainer ex = 
                        (TransformerExceptionContainer)iter.next();
                    
                    String location = ex.getOriginalLocation();

                    if (ex.getOriginalMessageAndLocation() != null) {
                        error.addAttribute("messageAndLocation", 
                                           ex.getOriginalMessageAndLocation());
                    }

                    if (ex.getMessage() != null) {
                        error.addAttribute("message", ex.getMessage());
                    }

                    if (ex.getCauseMessage() != null) {
                        error.addAttribute("causeMessage", ex.getCauseMessage());
                    }

                    int columnNumber = ex.getOriginalColumnNumber();
                    int lineNumber = ex.getOriginalLineNumber();

                    if (location != null) {
                        error.addAttribute("location", location);
                    }
                    error.addAttribute("column", Integer.toString
                                       (columnNumber));
                    error.addAttribute("line", Integer.toString(lineNumber));
                }
            }
        }
    }
}
