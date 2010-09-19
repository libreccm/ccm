package com.arsdigita.cms.contentassets;

import com.arsdigita.cms.contentassets.ui.FileDescriptionForm;
import com.arsdigita.bebop.FormSection;
import com.arsdigita.runtime.AbstractConfig;
import com.arsdigita.util.parameter.SpecificClassParameter;
import com.arsdigita.util.parameter.BooleanParameter;
import com.arsdigita.util.parameter.Parameter;

public class FileAttachmentConfig extends AbstractConfig {

    /**
     * A form which should be used for editing file asset properties.
     * Default implementation edits Assets.description property.
     */
    Parameter editFormClass;
    /**
     * Optional parameter if set to TRUE will disply the asset URL instead of
     * the description on AttachFile Authroing step. Default: FALSE
     */
    Parameter showAssetID;
    
    /**
     * Constructor, don't use it directly!
     */
    public FileAttachmentConfig() {

        editFormClass = new SpecificClassParameter(
                            "com.arsdigita.cms.contentassets.file_edit_form",
                            Parameter.REQUIRED,
                            FileDescriptionForm.class,
                            FormSection.class
                            );
        showAssetID = new BooleanParameter(
                          "com.arsdigita.cms.contentassets.file_show_asset_id",
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
