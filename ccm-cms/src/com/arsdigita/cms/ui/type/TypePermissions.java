package com.arsdigita.cms.ui.type;

import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.SingleSelect;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.CMS;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.ui.authoring.BasicItemForm;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.kernel.Group;
import com.arsdigita.kernel.RoleCollection;
import com.arsdigita.kernel.ui.ACSObjectSelectionModel;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class TypePermissions extends BasicItemForm {

    private static final String ROLE = "role";
    private final ACSObjectSelectionModel typeModel;
    private SingleSelect roleSelect;

    public TypePermissions(final ACSObjectSelectionModel typeModel) {
        super("typePermissions", null);

        this.typeModel = typeModel;
    }

    @Override
    public void addWidgets() {
        //super.addWidgets();
        
        add(new Label(GlobalizationUtil.globalize(
                "cms.ui.type.permissions.select_role")));
        ParameterModel roleModel = new StringParameter(ROLE);
        roleSelect = new SingleSelect(roleModel);
        roleSelect.addValidationListener(new NotNullValidationListener());
        add(roleSelect);
    }

    @Override
    public void init(FormSectionEvent e) throws FormProcessException {        
     
    }

    @Override
    public void process(FormSectionEvent e) throws FormProcessException {
    }
}
