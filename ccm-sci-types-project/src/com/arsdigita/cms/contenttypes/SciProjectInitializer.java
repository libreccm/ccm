package com.arsdigita.cms.contenttypes;

import com.arsdigita.cms.contenttypes.ui.SciProjectGlobalizationUtil;
import com.arsdigita.cms.contenttypes.ui.SciProjectSubProjectsStep;
import com.arsdigita.cms.contenttypes.ui.SciProjectSuperProjectsStep;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.runtime.DomainInitEvent;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class SciProjectInitializer extends ContentTypeInitializer {
    
    public SciProjectInitializer() {
        super("ccm-sci-types-project.pdl.mf",
              SciProject.BASE_DATA_OBJECT_TYPE);
    }
    
    @Override
    public void init(final DomainInitEvent event) {
        super.init(event);
        
        SciProjectConfig config = SciProject.getConfig();
        
        if (config.getEnableSubProjectsStep()) {
            AuthoringKitWizard.registerAssetStep(SciProject.BASE_DATA_OBJECT_TYPE, 
                                                 SciProjectSubProjectsStep.class, 
                                                 SciProjectGlobalizationUtil.globalize("sciproject.ui.subprojects.title"), 
                                                 SciProjectGlobalizationUtil.globalize("sciproject.ui.subprojects.description"), 
                                                 10);
        }
        
        if (config.getEnableSuperProjectsStep()) {
            AuthoringKitWizard.registerAssetStep(SciProject.BASE_DATA_OBJECT_TYPE, 
                                                 SciProjectSuperProjectsStep.class, 
                                                 SciProjectGlobalizationUtil.globalize("sciproject.ui.superprojects.title"), 
                                                 SciProjectGlobalizationUtil.globalize("sciproject.ui.superprojects.description"), 
                                                 10);
        }
    }
    
    @Override
    public String[] getStylesheets() {
        return new String[] {
          "/static/content-types/com/arsdigita/cms/contenttypes/SciProject.xsl"
        };
    }
    
    @Override
    public String getTraversalXML() {
        return "/WEB-INF/traversal-adapters/com/arsdigita/cms/contenttypes/SciProject.xml";
    }
}
