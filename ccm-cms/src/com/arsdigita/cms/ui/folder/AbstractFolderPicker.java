/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.arsdigita.cms.ui.folder;

import com.arsdigita.bebop.form.SingleSelect;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.PageState;
import com.arsdigita.cms.Folder;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.persistence.OID;
import com.arsdigita.toolbox.ui.OIDParameter;
import java.util.TooManyListenersException;

/**
 *
 * @author Sören Bernstein (quasimodo) <sbernstein@zes.uni-bremen.de>
 */
public abstract class AbstractFolderPicker extends SingleSelect {
    
    public AbstractFolderPicker(String name) {
        super(new OIDParameter(name));
        
        try {
            addPrintListener(new PrintListener() {
                @Override
                    public void prepare(PrintEvent ev) {
                        addOptions(ev.getPageState(),
                                   (SingleSelect)ev.getTarget());
                    }
                });
        } catch (TooManyListenersException ex) {
            throw new RuntimeException("this cannot happen");
        }        
    }
    
    public Folder getFolder(PageState state) {
        OID oid = (OID)getValue(state);
        
        if (oid == null) {
            return null;
        }
        return (Folder)DomainObjectFactory.newInstance(oid);
    }
    
    public void setCategory(PageState state,
                            Folder folder) {
        if (folder == null) {
            setValue(state, null);
        } else {
            setValue(state, folder.getOID());
        }
    }
    
    protected abstract void addOptions(PageState state,
                                       SingleSelect target);
    
}
