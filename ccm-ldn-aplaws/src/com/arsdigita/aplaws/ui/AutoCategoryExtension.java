package com.arsdigita.aplaws.ui;

import com.arsdigita.bebop.ActionLink;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleComponent;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.ui.authoring.ItemCategoryExtension;
import com.arsdigita.globalization.GlobalizedMessage;

public class AutoCategoryExtension extends ItemCategoryExtension {

    public SimpleComponent[] getForm() {
        SimpleComponent[] autocatForm = new SimpleComponent[1];
        autocatForm[0] = new AutoCategoryForm();
        return autocatForm;
    }

    public SimpleComponent[] getSummary() {
        SimpleComponent[] autocatLink = new SimpleComponent[1];
        autocatLink[0] = new AutoCatActionLink();
        return autocatLink;
    }

    private class AutoCatActionLink extends ActionLink {
        public AutoCatActionLink() {
            super(new Label(new GlobalizedMessage("autocat.call.service","com.arsdigita.aplaws.AplawsResources")));
            addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    PageState state = e.getPageState();
                    fireCompletionEvent(state);
                }
            });
        }
    }
}
