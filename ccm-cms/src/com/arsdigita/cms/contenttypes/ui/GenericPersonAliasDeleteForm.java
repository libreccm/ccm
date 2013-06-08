/*
 * Copyright (c) 2010 Jens Pelzetter
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
 *
 */
package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.GenericPerson;
import com.arsdigita.cms.contenttypes.util.ContenttypesGlobalizationUtil;
import com.arsdigita.cms.ui.authoring.BasicPageForm;
import com.arsdigita.util.UncheckedWrapperException;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class GenericPersonAliasDeleteForm
        extends BasicPageForm
        implements FormProcessListener {

    public static final String ID = "GenericPersonAliasDeleteForm";

    public GenericPersonAliasDeleteForm(
            final ItemSelectionModel itemModel,
            final GenericPersonAliasPropertiesStep step) {
        super(ID, itemModel);
        addSaveCancelSection();
        addProcessListener(this);        
    }

    @Override
    public void addWidgets() {
        add(new Label(ContenttypesGlobalizationUtil.globalize(
                "cms.contenttypes.ui.person.alias.delete.confirm")));
    }

    @Override
    public void init(final FormSectionEvent fse) throws FormProcessException {
        //Nothing
    }

    @Override
    public void process(final FormSectionEvent fse) throws FormProcessException {
        final PageState state = fse.getPageState();
        final GenericPerson person = (GenericPerson) getItemSelectionModel()
                                                     .getSelectedObject(state);
        
        if ((person != null) && (person.getAlias() != null)) {
            person.unsetAlias();
        }
    }
    
    @Override
    public void validate(final FormSectionEvent event) throws FormProcessException {
        //Nothing
    }

    @Override
    public void addSaveCancelSection() {
        try {
            getSaveCancelSection().getSaveButton().addPrintListener(new PrintListener() {

                public void prepare(final PrintEvent event) {
                    GenericPerson person =
                                   (GenericPerson) getItemSelectionModel().
                            getSelectedObject(event.getPageState());
                    Submit target = (Submit) event.getTarget();
                    target.setButtonLabel(ContenttypesGlobalizationUtil.
                            globalize(
                            "cms.contenttypes.ui.person.alias.delete.label"));
                }
            });
        } catch (Exception ex) {
            throw new UncheckedWrapperException("this cannot happen", ex);
        }
    }
}
