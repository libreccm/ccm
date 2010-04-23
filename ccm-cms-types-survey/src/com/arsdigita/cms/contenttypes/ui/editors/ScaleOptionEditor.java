/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.arsdigita.cms.contenttypes.ui.editors;

import com.arsdigita.bebop.SingleSelectionModel;
import com.arsdigita.cms.contenttypes.PersistentScale;
import com.arsdigita.formbuilder.PersistentOptionGroup;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.formbuilder.ui.editors.OptionEditor;
import java.math.BigDecimal;

/**
 *
 * @author quasi
 */
public class ScaleOptionEditor extends OptionEditor {

    public ScaleOptionEditor(SingleSelectionModel control) {

        super(control);

    }

    protected PersistentOptionGroup getOptionGroup(BigDecimal id)
            throws DataObjectNotFoundException {
        return new PersistentScale(id).getOptionList();
    }
}
