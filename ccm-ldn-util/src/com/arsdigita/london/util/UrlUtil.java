/*
 * Copyright (C) 2005-2006 UNDP. All Rights Reserved.
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

package com.arsdigita.london.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.web.ParameterMap;
import com.arsdigita.web.URL;

public class UrlUtil {
	
	/**
	 * Processes the URL for location.
	 */
	public static String prepareURL(final PageState state, String location, ParameterMap params) {
		return prepareURL(state, location, params, true);
	}

	/**
	 * Processes the URL for location.
	 */
	public static String prepareURL(PageState state, String location, ParameterMap params, boolean includeDispatcherPath) {
		return prepareURL(state, location, params, includeDispatcherPath, (List) null, false);
	}

	/**
	 * Processes the URL for location.
	 */
	public static String prepareURL(final PageState state, String location, ParameterMap params, boolean includeDispatcherPath, String ignoreParam) {
		ArrayList ignoreParams = new ArrayList();
		ignoreParams.add(ignoreParam);
		return prepareURL(state, location, params, includeDispatcherPath, ignoreParams, false);
	}

	/**
	 * Processes the URL for location.
	 */
	public static String prepareURL(final PageState state, String location,
            ParameterMap params, boolean includeDispatcherPath,
            List ignoreParams, boolean addPageStateParams) {
		final HttpServletRequest req = state.getRequest();
		final HttpServletResponse resp = state.getResponse();

		if (params == null) {
			params = new ParameterMap();
		}
		//add global state parameters
		if (addPageStateParams) {
		Iterator stateParams = state.getPage().getParameters();
		while (stateParams.hasNext()) {
			ParameterModel param = (ParameterModel) stateParams.next();
			Object value = state.getValue(param);
			if (value != null) {
				String paramName = param.getName();
				//don't replace param
				if (params.getParameter(paramName) == null) {
					params.setParameter(paramName, value);
					}
				}
			}
		}
		params.runListeners(req);

		if (includeDispatcherPath && location.startsWith("/")) {
			location = URL.getDispatcherPath() + location;
		}

		String url;
		if (location.indexOf("?") == -1) {
			// m_params adds the "?" as needed.
			url = resp.encodeURL(location + params);
		}
		else {
			// The location already includes a query string, so
			// append to it without including a "?".
			if (location.endsWith("&")) {
				url = resp.encodeURL(location + params.getQueryString());
			}
			else {
				url = resp.encodeURL(location + "&" + params.getQueryString());
			}
		}

		//remove ignored params
		if (ignoreParams != null) {
			Iterator iParsIter = ignoreParams.iterator();
			while (iParsIter.hasNext()) {
				String ignoreParam = (String) iParsIter.next();
				url = removeParameter(url, ignoreParam);
			}
		}

		return url;
	}

	/**
	 * Removes the URL's paremeter
	 */
	public static String removeParameter(String url, String parameter) {
		Pattern p = Pattern.compile("[?&]" + parameter + "=[^&]*");
		Matcher m = p.matcher(url);
		int index = 0;
		int lastIndex = 0;
		int length = url.length();
		StringBuffer out = new StringBuffer();
		while (m.find(index)) {
			index = m.start();
			int end = m.end();
			if (url.charAt(index) == '&') {
				out.append(url.substring(lastIndex, index));
			}
			else {
				//start with ?...
				out.append(url.substring(lastIndex, index + 1));
				if (length > end && url.charAt(end) == '&') {
					end++;
				}
			}
			index = end;
			lastIndex = index;
		}
		if (url.length() > lastIndex) {
			out.append(url.substring(lastIndex));
		}
		//remove '?' as last char 
		index = out.length() - 1;
		if (index >= 0 && out.charAt(index) == '?') {
			out.deleteCharAt(index);
		}
		return out.toString();
	}
}
