package com.arsdigita.cms.contentassets;

import com.arsdigita.cms.contentassets.ui.FileDescriptionForm;
import com.arsdigita.bebop.FormSection;
import com.arsdigita.runtime.AbstractConfig;
import com.arsdigita.util.parameter.SpecificClassParameter;
import com.arsdigita.util.parameter.BooleanParameter;
import com.arsdigita.util.parameter.IntegerParameter;
import com.arsdigita.util.parameter.Parameter;

import org.apache.log4j.Logger;

public class FileAttachmentConfig extends AbstractConfig {

    /** A logger instance to assist debugging.  */
    private static final Logger s_log = Logger.getLogger(FileAttachmentConfig.class);
    /** Singelton config object.  */
    private static FileAttachmentConfig s_conf;

    /**
     * Singleton pattern, don't instantiate a config object using the
     * constructor directly!
     * @return
     */
    public static synchronized FileAttachmentConfig instanceOf() {
        if (s_conf == null) {
            s_conf = new FileAttachmentConfig();
            s_conf.load();
        }

        return s_conf;
    }

    // ///////////////////////////////////////////////////////////////////////
    //
    // set of configuration parameters
    /**
     * A form which should be used for editing file asset properties.
     * Default implementation edits Assets.description property.
     */
    private final Parameter editFormClass =
                            new SpecificClassParameter(
            "com.arsdigita.cms.contentassets.file_edit_form",
            Parameter.REQUIRED,
            FileDescriptionForm.class,
            FormSection.class);
    /**
     * Optional parameter if set to TRUE will disply the asset URL instead of
     * the description on AttachFile Authroing step. Default: FALSE
     */
    private final Parameter showAssetID =
                            new BooleanParameter(
            "com.arsdigita.cms.contentassets.file_show_asset_id",
            Parameter.OPTIONAL,
            Boolean.FALSE);
    private final Parameter fileAttachmentStepSortKey =
                            new IntegerParameter(
            "com.arsdigita.cms.contentassets.file_attchment_step_sort_key",
            Parameter.REQUIRED,
            2);

    /**
     * Constructor, don't use it directly!
     */
    public FileAttachmentConfig() {

        super();
        
        register(editFormClass);
        register(showAssetID);
        register(fileAttachmentStepSortKey);
        
        loadInfo();
    }

    public Class getEditFormClass() {
        return (Class) get(editFormClass);
    }

    public boolean isShowAssetIDEnabled() {
        return get(showAssetID).equals(Boolean.TRUE);
    }
    
    public Integer getFileAttachmentStepSortKey() {
        return (Integer) get(fileAttachmentStepSortKey);
    }

}
