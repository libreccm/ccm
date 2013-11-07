/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.arsdigita.cms.ui.folder;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.form.SingleSelect;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.Folder;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.persistence.OID;
import java.util.TooManyListenersException;

/**
 *
 * @author Sören Bernstein <quasi@quasiweb.de>
 */
public abstract class AbstractFolderPicker extends SingleSelect {

    public AbstractFolderPicker(String name) {        
        super(new StringParameter(name));

        try {
            addPrintListener(new PrintListener() {

                @Override
                public void prepare(PrintEvent ev) {
                    addOptions(ev.getPageState(),
                               (SingleSelect) ev.getTarget());
                }

            });
        } catch (TooManyListenersException ex) {
            throw new RuntimeException("this cannot happen");
        }
    }

    public Folder getFolder(PageState state) {
        OID oid = OID.valueOf((String) getValue(state));//(OID) getValue(state);

        if (oid == null) {
            return null;
        } else {
            return (Folder) DomainObjectFactory.newInstance(oid);
        }
    }

    public void setFolder(PageState state,
                          Folder folder) {
        if (folder == null) {
            setValue(state, null);
        } else {
            setValue(state, folder.getOID().toString());
        }
    }

    protected abstract void addOptions(PageState state,
                                       SingleSelect target);

}
