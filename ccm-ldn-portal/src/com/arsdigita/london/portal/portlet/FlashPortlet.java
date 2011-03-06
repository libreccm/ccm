/*
 * Copyright (C) 2008 Permeance Technologies Ptd Ltd. All Rights Reserved.
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

package com.arsdigita.london.portal.portlet;

import java.io.IOException;
import java.io.StringBufferInputStream;
import java.util.Map;
import java.util.Properties;

import com.arsdigita.bebop.portal.AbstractPortletRenderer;
import com.arsdigita.london.portal.ui.portlet.FlashPortletRenderer;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.portal.Portlet;

/**
 * This portlet displays a Flash movie using
 * <a href="http://blog.deconcept.com/swfobject/">SWFObject 1.5</a>.
 * 
 * @author <a href="https://sourceforge.net/users/terry_permeance/">terry_permeance</a>
 */
public class FlashPortlet extends Portlet
{
    public static final String BASE_DATA_OBJECT_TYPE =
                            "com.arsdigita.london.portal.portlet.FlashPortlet";

    public static final String BACKGROUND_COLOUR = "backgroundColour";

    public static final String DETECT_KEY = "detectKey";

    public static final String SWF_FILE = "swfFile";

    public static final String HEIGHT = "height";

    public static final String PARAMETERS = "parameters";

    public static final String QUALITY = "quality";

    public static final String REDIRECT_URL = "redirectUrl";

    public static final String VARIABLES = "variables";

    public static final String VERSION = "version";

    public static final String WIDTH = "width";

    public static final String XI_REDIRECT_URL = "xiRedirectUrl";

    protected FlashPortlet(DataObject dataObject)
    {
        super(dataObject);
    }

    protected String getBaseDataObjectType()
    {
        return BASE_DATA_OBJECT_TYPE;
    }

    protected AbstractPortletRenderer doGetPortletRenderer()
    {
        return new FlashPortletRenderer(this);
    }

    /**
     * Get the background colour.
     * 
     * @return the background colour..
     */
    public String getBackgroundColour()
    {
        return (String) get(BACKGROUND_COLOUR);
    }

    /**
     * Set the background colour as a hex string, <i>e.g.</i> <code>#336699</code>.
     * 
     * @param backgroundColour
     *            the background colour to set
     */
    public void setBackgroundColour(String backgroundColour)
    {
        set(BACKGROUND_COLOUR, backgroundColour);
    }

    /**
     * Get the URL parameter name that the SWFObject script will look for when
     * bypassing the detection (optional).
     * Default is "detectflash".
     * 
     * @return the detect key
     */
    public String getDetectKey()
    {
        return (String) get(DETECT_KEY);
    }

    /**
     * Set the URL parameter name that the SWFObject script will look for when
     * bypassing the detection (optional).
     * Default is "detectflash".
     * 
     * @param detectKey
     *            the detect key to set
     */
    public void setDetectKey(String detectKey)
    {
        set(DETECT_KEY, detectKey);
    }

    /**
     * Get the SWF file.
     * 
     * @return the file.
     */
    public String getFile()
    {
        return (String) get(SWF_FILE);
    }

    /**
     * Set the SWF file
     * 
     * @param file
     *            the height to set
     */
    public void setFile(String file)
    {
        set(SWF_FILE, file);
    }

    /**
     * Get the height.
     * 
     * @return the height as pixels or percentage.
     */
    public String getHeight()
    {
        return (String) get(HEIGHT);
    }

    /**
     * @param height
     *            the height to set as pixels or percentage
     */
    public void setHeight(String height)
    {
        set(HEIGHT, height);
    }

    /**
     * Get the parameters.
     * 
     * @return the parameters in {@link Properties} file format.
     */
    public String getParameters()
    {
        return (String) get(PARAMETERS);
    }

    /**
     * Get the parameters.
     * 
     * @return a map the the parameters, possibly empty but not null.
     */
    public Map getParametersMap()
    {
        try
        {
            Properties parameters = new Properties();
            if (this.getParameters() != null && this.getParameters()
                                                    .trim().length() > 1)
            {
                parameters.load(new StringBufferInputStream(this.getParameters()));
            }
            return parameters;
        }
        catch (IOException e)
        {
            throw new IllegalStateException(e);
        }
    }

    /**
     * @param parameters
     *            the parameters to set
     */
    public void setParameters(String parameters)
    {
        set(PARAMETERS, parameters);
    }

    /**
     * Get the quality for the Flash movie to play at (optional).
     * 
     * @return the quality
     */
    public String getQuality()
    {
        return (String) get(QUALITY);
    }

    /**
     * Set the quality for the Flash movie to play at. If no quality is
     * specified, the default is "high". (optional)
     * 
     * @param quality
     *            the quality to set
     * 
     * @see <a href=
     * "http://kb.adobe.com/selfservice/viewContent.do?externalId=tn_12701&sliceId=2">
     * Flash OBJECT and
     *      EMBED tag attributes< /a> documents the legal values for quality
     */
    public void setQuality(String quality)
    {
        set(QUALITY, quality);
    }

    /**
     * Get the redirect URL for users who don't have the correct plug-in version
     * (optional).
     * 
     * @return the redirect URL
     */
    public String getRedirectUrl()
    {
        return (String) get(REDIRECT_URL);
    }

    /**
     * Set the redirect URL for users who don't have the correct plug-in version.
     * (optional).
     * 
     * @param redirectUrl
     *            redirect URL to set
     */
    public void setRedirectUrl(String redirectUrl)
    {
        set(REDIRECT_URL, redirectUrl);
    }

    /**
     * Set the variables.
     * 
     * @param variables
     *            variables to set
     */
    public void setVariables(String variables)
    {
        set(VARIABLES, variables);
    }

    /**
     * Get the variables.
     * 
     * @return the variables in {@link Properties} file format.
     */
    public String getVariables()
    {
        return (String) get(VARIABLES);
    }

    /**
     * Get the variables.
     * 
     * @return a map the the variables, possibly empty but not null.
     */
    public Map getVariablesMap()
    {
        try
        {
            Properties variables = new Properties();
            if (this.getVariables() != null && this.getVariables().trim().length() > 1)
            {
            	variables.load(new StringBufferInputStream(this.getVariables()));
            }
            return variables;
        }
        catch (IOException e)
        {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Set the minimum version of Flash required, e.g. "6.0.65".
     * 
     * @param version
     *            the version to set
     */
    public void setVersion(String version)
    {
        set(VERSION, version);
    }

    /**
     * Get the minimum version of Flash required.
     * 
     * @return the version.
     */
    public String getVersion()
    {
        return (String) get(VERSION);
    }

    /**
     * Get the width.
     * 
     * @return the width as pixels or percentage.
     */
    public String getWidth()
    {
        return (String) get(WIDTH);
    }

    /**
     * @param width
     *            the width to set as pixels or percentage
     */
    public void setWidth(String width)
    {
        set(WIDTH, width);
    }

    /**
     * Get the URL to redirect users who complete the ExpressInstall upgrade
     * (optional).
     * 
     * @return the express install redirect URL
     */
    public String getXiRedirectUrl()
    {
        return (String) get(XI_REDIRECT_URL);
    }

    /**
     * Set the URL to redirect users who complete the ExpressInstall upgrade
     * (optional).
     * 
     * @param xiRedirectUrl
     *            express install redirect URL to set
     */
    public void setXiRedirectUrl(String xiRedirectUrl)
    {
        set(XI_REDIRECT_URL, xiRedirectUrl);
    }
}
