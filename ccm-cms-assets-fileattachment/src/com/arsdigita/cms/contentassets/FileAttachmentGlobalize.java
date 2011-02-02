/*
 * Copyright (C) 2010 Peter Boy (pb@zes.uni-remen.de)
 *
 * The contents of this file are subject to the Open Software License v2.1
 * (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * http://rhea.redhat.com/licenses/osl2.1.html.
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */

package com.arsdigita.cms.contentassets;

import com.arsdigita.globalization.GlobalizedMessage;

/**
 * Service routine tp provide localized versions of key strings for the
 * FileAttachment UI and messages.
 *
 * @author pb
 * @version $Id: $
 */
public class FileAttachmentGlobalize {


    /**
     * The label for the authoring step
     */
    public static GlobalizedMessage AuthoringStepLabel() {
        return new GlobalizedMessage(
            "cms.contentassets.file_attachment.label",
            "com.arsdigita.cms.contentassets.FileAttachmentResources");
    }

    /**
     * The description for the authoring step
     */
    public static GlobalizedMessage AuthoringStepDescription() {
        return new GlobalizedMessage(
            "cms.contentassets.file_attachment.description",
            "com.arsdigita.cms.contentassets.FileAttachmentResources");
    }

    /**
     * Label for File Type selection box.
     */
    public static GlobalizedMessage FileTypeLabel() {
        return new GlobalizedMessage(
            "cms.contentassets.file_attachment.type_label",
            "com.arsdigita.cms.contentassets.FileAttachmentResources");
    }

    /**
     * Label for File Type selection box.
     */
    public static GlobalizedMessage NoFilesAssociatedMsg() {
        return new GlobalizedMessage(
            "cms.contentassets.file_attachment.no_files_associated_msg",
            "com.arsdigita.cms.contentassets.FileAttachmentResources");
    }

    /**
     * Label for File Type selection box.
     */
    public static GlobalizedMessage UploadNewFileLabel() {
        return new GlobalizedMessage(
            "cms.contentassets.file_attachment.upload_new_file_label",
            "com.arsdigita.cms.contentassets.FileAttachmentResources");
    }



}
