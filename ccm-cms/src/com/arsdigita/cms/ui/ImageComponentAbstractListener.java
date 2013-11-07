/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.arsdigita.cms.ui;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.MapComponentSelectionModel;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.cms.ReusableImageAsset;
import java.util.Iterator;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 * An abstract listener for {@link ImageComponent}.
 *
 * This listener provides the base implementation which is shared between all
 * listeners of this kind.
 *
 * This listerner is used by {@link ImageSelectPage}.
 *
 * @author Sören Bernstein <quasi@quasiweb.de>
 */
public abstract class ImageComponentAbstractListener implements FormInitListener,
	FormProcessListener,
	FormSubmissionListener {

	private static final Logger s_log = Logger.getLogger(
		ImageComponentSelectListener.class);
	MapComponentSelectionModel m_imageComponent;

	public ImageComponentAbstractListener(MapComponentSelectionModel imageComponent) {
		super();
		m_imageComponent = imageComponent;
	}

	@Override
	public void init(FormSectionEvent event)
		throws FormProcessException {
		PageState ps = event.getPageState();
		if (!m_imageComponent.isSelected(ps)) {
			setImageComponent(ps, ImageComponent.LIBRARY);
		}
	}

	/**
	 * Call {@link #cancelled(com.arsdigita.bebop.PageState)} if the cancel
	 * button was pressed.
	 *
	 * @param event the {@link FormSectionEvent}
	 * @throws FormProcessException
	 */
	@Override
	public void submitted(FormSectionEvent event) throws FormProcessException {
		PageState ps = event.getPageState();
		ImageComponent component = getImageComponent(ps);

		if (component.getSaveCancelSection().getCancelButton().isSelected(ps)) {
			cancelled(ps);
		}
	}

	/**
	 * Call {@link #processImage(com.arsdigita.bebop.event.FormSectionEvent,
	 * com.arsdigita.bebop.PageState, com.arsdigita.cms.ui.ImageComponent,
	 * com.arsdigita.cms.ReusableImageAsset) }
	 * if the save button was pressed.
	 *
	 * @param event the {@link FormSectionEvent}
	 * @throws FormProcessException
	 */
	@Override
	public void process(FormSectionEvent event) throws FormProcessException {
		PageState ps = event.getPageState();
		ImageComponent component = getImageComponent(ps);

		if (!component.getSaveCancelSection().getSaveButton().isSelected(ps)) {
			return;
		}

		ReusableImageAsset image = component.getImage(event);

		processImage(event, ps, component, image);
	}

	/**
	 * To be overridden by child if neccessary.
	 *
	 * @param ps
	 */
	protected void cancelled(PageState ps) {
	}

	;
    
    /**
     * Process the input.
     * 
     * @param event the {@link FormSectionEvent}
     * @param ps {@link PageState}
     * @param component an {@link ImageComponent}
     * @param image the {@link ReusableImageAsset}
     */
    protected abstract void processImage(FormSectionEvent event, PageState ps, ImageComponent component, ReusableImageAsset image);

	protected ImageComponent getImageComponent(PageState ps) {
		if (!m_imageComponent.isSelected(ps)) {
			if (s_log.isDebugEnabled()) {
				s_log.debug("No component selected");
				s_log.debug("Selected: " + m_imageComponent.getComponent(ps));
			}

			m_imageComponent.setSelectedKey(ps, ImageComponent.UPLOAD);
		}

		return (ImageComponent) m_imageComponent.getComponent(ps);

	}

	/**
	 * Sets the active component
	 *
	 * @param ps Page state
	 * @param activeKey the key of the active component
	 */
	protected void setImageComponent(PageState ps, final String activeKey) {

		if (s_log.isDebugEnabled()) {
			s_log.debug("Selected component: " + activeKey);
		}

		Map componentsMap = m_imageComponent.getComponentsMap();
		Iterator i = componentsMap.keySet().iterator();
		while (i.hasNext()) {
			Object key = i.next();
			Component component = (Component) componentsMap.get(key);

			boolean isVisible = activeKey.equals(key);

			if (s_log.isDebugEnabled()) {
				s_log.debug("Key: " + key + "; Visibility: " + isVisible);
			}

			ps.setVisible(component, isVisible);
		}
	}
}
