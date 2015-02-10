package com.arsdigita.aplaws.ui;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import com.arsdigita.aplaws.AutoCategorisation;
import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SaveCancelSection;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.form.Hidden;
import com.arsdigita.bebop.form.MultipleSelect;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.parameters.ArrayParameter;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.categorization.Category;
import com.arsdigita.cms.CMS;
import com.arsdigita.cms.ContentBundle;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.london.terms.Term;
import com.arsdigita.persistence.OID;

public class AutoCategoryForm extends Form {

    private MultipleSelect autoCatNames = new MultipleSelect("autoCatNames");
    private Hidden autoCatIDs = new Hidden(new ArrayParameter(new BigDecimalParameter("autoCatIDs")));
    private SaveCancelSection buttons = new SaveCancelSection();
    
    public AutoCategoryForm() {
        super("autocat", new BoxPanel(BoxPanel.VERTICAL));

        add(new Label(new GlobalizedMessage("autocat.title","com.arsdigita.aplaws.AplawsResources")));
        autoCatNames.setReadOnly();
        autoCatNames.setDisabled();
        add(autoCatNames);
        add(autoCatIDs);
        add(buttons);
        
        addInitListener(new FormInitListener() {
            public void init(FormSectionEvent e) 
                throws FormProcessException {
                
                PageState state = e.getPageState();
                try {
                    ContentItem item = CMS.getContext().getContentItem();
                    Collection terms = AutoCategorisation.getAutoTerms(item);    
                    Collection ids = new LinkedList();
                    for (Iterator i=terms.iterator(); i.hasNext(); ) {
                        Term t = (Term) i.next();
                        Category c = t.getModel();
                        ids.add(c.getID());
                        autoCatNames.addOption( new Option( c.getID().toString(),
                                c.getQualifiedName(">", true)), state);
                    }
                    autoCatIDs.setValue(state,
                                        ids.toArray(new BigDecimal[ids.size()]));
                } catch (AutoCategorisation.ServiceFailed sf) {
                    // XXX limit error msg length
                    autoCatNames.addOption( new Option( "0",
                            "FAILED: "+sf.getCause().getMessage()),state);
                }
            }
        });

        addProcessListener(new FormProcessListener() {
            public void process(FormSectionEvent ev)
                throws FormProcessException {

                PageState state = ev.getPageState();

                ContentItem item = CMS.getContext().getContentItem();
                ContentBundle bundle = (ContentBundle)item.getParent();
                
                BigDecimal[] ids = (BigDecimal[])autoCatIDs.getValue(state);
                for (int i = 0 ; i < ids.length ; i++) {
                    Category cat = (Category)
                        DomainObjectFactory.newInstance(
                            new OID(Category.BASE_DATA_OBJECT_TYPE,
                                    ids[i]));
                    // XXX mark it as auto
                    cat.addChild(bundle);
                }

                fireCompletionEvent(state);
            }
        });

        addSubmissionListener(new FormSubmissionListener() {
            public void submitted(FormSectionEvent ev) 
                throws FormProcessException {

                PageState state = ev.getPageState();

                if (buttons.getCancelButton().isSelected(state)) {
                    fireCompletionEvent(state);
                    throw new FormProcessException(GlobalizationUtil.globalize("cms.ui.cancelled"));
                }
            }
        });
    }

}
