/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.arsdigita.cms.contenttypes;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.form.Widget;
import com.arsdigita.bebop.util.Traversal;
import com.arsdigita.cms.CMS;
import com.arsdigita.cms.contenttypes.ui.SurveyProcessListener;
import com.arsdigita.cms.dispatcher.SimpleXMLGenerator;
import com.arsdigita.cms.formbuilder.FormUnavailableException;
import com.arsdigita.cms.formbuilder.NoParametersHttpServletRequest;
import com.arsdigita.formbuilder.PersistentForm;
import com.arsdigita.formbuilder.ui.BaseAddObserver;
import com.arsdigita.formbuilder.ui.FormBuilderXMLRenderer;
import com.arsdigita.formbuilder.ui.PlaceholdersInitListener;
import com.arsdigita.formbuilder.util.FormBuilderUtil;

import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.web.URL;
import com.arsdigita.web.Web;
import com.arsdigita.xml.Element;

/**
 *
 * @author quasi
 */
public class SurveyXMLGenerator extends SimpleXMLGenerator {

    public static final String ADAPTER_CONTEXT = SurveyXMLGenerator.class.getName();

    @Override
    public void generateXML(PageState state, Element parent, String useContext) {

        Survey survey = (Survey) getContentItem(state);
        PersistentForm pForm = survey.getForm();
        Component c = instantiateForm(pForm, "itemAdminSummary".equals(useContext));
//        String url = DispatcherHelper.getRequestContext().getRemainingURLPart();

        // Fake the page context for the item, since we
        // have no access to the real page context.
        Page p = new Page("dummy");
        p.add(c);
        p.lock();

        PageState fake;
        try {
            if ("itemAdminSummary".equals(useContext)) {
                // Chop off all the parameters to stop bebop stategetting confused
                fake = p.process(new NoParametersHttpServletRequest(
                        state.getRequest()), state.getResponse());
            } else {
                // Really serving the user page, so need the params when
                // processing the pForm
                fake = p.process(state.getRequest(), state.getResponse());
            }
        } catch (Exception e) {
            throw new UncheckedWrapperException(e);
        }

//        Traversal t = new VisibleTraverse(fake);
//        t.preorder(c);

        // Simply embed the bebop xml as a child of the cms:item tag
        Element element = parent.newChildElement("cms:item", CMS.CMS_XML_NS);

        String action = pForm.getAction();
        if (action == null) {
            final URL requestURL = Web.getWebContext().getRequestURL();

            if (requestURL == null) {
                action = state.getRequest().getRequestURI();
            } else {
                action = requestURL.getRequestURI();
            }
        }

        element.addAttribute(FormBuilderUtil.FORM_ACTION, action);

        if (!survey.hasStarted()) {
            // This survey has not started yet, so show info text instead of survey form
            element.newChildElement(new Element("info").setText("not started yet"));
        } else if (survey.getResponsesPublic().booleanValue() && survey.hasResponses() &&
                (survey.getResultsDuringSurvey() || survey.hasEnded()))  {
            element.newChildElement(new Element("results").setText("go to results"));
        }

        FormBuilderXMLRenderer renderer = new FormBuilderXMLRenderer(element);
        renderer.setWrapAttributes(true);
        renderer.setWrapRoot(false);
        renderer.setRevisitFullObject(true);
        renderer.setWrapObjects(false);

        renderer.walk(survey, SurveyXMLGenerator.ADAPTER_CONTEXT);

        // then, if the component is actually a pForm, we need
        // to print out any possible errors
        // Ideally we could do this as part of the "walk" but for now
        // that does not work because we don't pass in the page state
        // although that can always we updated.
        //        if (c instanceof Form) {
        //            Element infoElement =
        //                    element.newChildElement(FormBuilderUtil.FORMBUILDER_FORM_INFO,
        //                    FormBuilderUtil.FORMBUILDER_XML_NS);
        //            Form f = (Form) c;

        //            Traversal infoTraversal =
        //                    new ComponentTraverse(state, ((Form) c).getFormData(state),
        //                    infoElement);
        //            infoTraversal.preorder(f);
        //        }

        // we need to generate the state so that it can be part of the pForm
        // and correctly included when the pForm is submitted.  We could
        // do this by iterating through the pForm data but it does not
        // seem like a good idea to just cut and paste the code out
        // of the PageState class
        fake.setControlEvent(c);

        // Generate the hidden PageState info for this page
        fake.generateXML(element.newChildElement(FormBuilderUtil.FORMBUILDER_PAGE_STATE, FormBuilderUtil.FORMBUILDER_XML_NS));
    }

    protected Component instantiateForm(PersistentForm persistentForm, boolean readOnly) {

        try {

            persistentForm.setComponentAddObserver(new BaseAddObserver());
            Form form = (Form) persistentForm.createComponent();
            form.addInitListener(new PlaceholdersInitListener());
            form.addProcessListener(new SurveyProcessListener());

            if (readOnly) {
                Traversal t = new Traversal() {

                    public void act(Component c) {
                        try {
                            Widget widget = (Widget) c;
                            widget.setDisabled();
                            widget.setReadOnly();
                        } catch (ClassCastException ex) {
                            // Nada
                        }
                    }
                };
                t.preorder(form);
            }

            return form;
        } catch (FormUnavailableException ex) {
            return new Label("This form is temporarily unavailable");
        }
    }
}
