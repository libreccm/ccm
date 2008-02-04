package com.arsdigita.cms.contentassets;

import com.arsdigita.cms.contentassets.ui.FileDescriptionForm;
import com.arsdigita.runtime.AbstractConfig;
import com.arsdigita.util.parameter.ClassParameter;
import com.arsdigita.util.parameter.BooleanParameter;
import com.arsdigita.util.parameter.Parameter;

public class FileAttachmentConfig extends AbstractConfig {

    Parameter editFormClass;
    Parameter showAssetID;
    
    public FileAttachmentConfig() {
        editFormClass = new ClassParameter ("com.arsdigita.cms.contentassets.file_edit_form",
                Parameter.REQUIRED,
                FileDescriptionForm.class 
                // TODO move *private* class ContentSectionConfig.SpecificClassParameter to c.a.util.parameter
                // so we can use it here.
                // , FormSection.class
                ); 
        showAssetID = new BooleanParameter("com.arsdigita.cms.contentassets.file_show_asset_id",
        		Parameter.OPTIONAL,
        		Boolean.FALSE
        		);
                        
        register(editFormClass);
        register(showAssetID);
        loadInfo();
    }

    public Class getEditFormClass() {
        return (Class) get(editFormClass);
    }
    
    public boolean isShowAssetIDEnabled(){
    	return get(showAssetID).equals(Boolean.TRUE);
    }

}
