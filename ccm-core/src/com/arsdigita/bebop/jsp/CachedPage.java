/*
 * Created on 20-Jan-06
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.arsdigita.bebop.jsp;

import com.arsdigita.bebop.Page;

/**
 * Wrapper around object array used by com.arsdigita.bebop.jsp.DefinePage
 * that implements hashCode in order that pages can be put in a CacheTable
 * without being removed unnecessarily by other nodes. This is required by 
 * stateful portlet framework.
 * 
 * Hashcode is based on ids of components on the page and should be equal for pages 
 * containing the same components. These ids are either specified when the component is added
 * or assigned an integer from a straightforward sequence. As a consequence, even if the components
 * are added in a different order, and hence individually may have different ids, the same ids should be 
 * present on the page.
 * 
 * 
 *  @author Chris Gilbert <a href="mailto:chris.gilbert@westsussex.gov.uk">chris.gilbert@westsussex.gov.uk</a>
 * @version $Id: CorrespondenceConfig.java,v 1.4 2006/01/18 08:39:11 cgyg9330 Exp $
 */
public class CachedPage {

	private Object[] pair;

	public CachedPage(Object[] pair) {
		this.pair = pair;
	}


	public Object[] getPageTimeStampPair () {
		return pair;
	}
	public int hashCode() {
		Page page = (Page) pair[0];
		return page.getComponentString().hashCode();
	}

	public boolean equals(Object other) {
		if (other == null) {
			return false;
		}
		if (!other.getClass().equals(CachedPage.class)) {
			return false;
		}
		CachedPage otherCachedPage = (CachedPage) other;
		Page otherPage = (Page)otherCachedPage.getPageTimeStampPair()[0];
		Page thisPage = (Page)getPageTimeStampPair()[0];
		return otherPage.getComponentString().equals(thisPage.getComponentString());
	}

}
