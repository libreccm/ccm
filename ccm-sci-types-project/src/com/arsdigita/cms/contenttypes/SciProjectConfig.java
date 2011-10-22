package com.arsdigita.cms.contenttypes;

import com.arsdigita.runtime.AbstractConfig;
import com.arsdigita.util.parameter.BooleanParameter;
import com.arsdigita.util.parameter.IntegerParameter;
import com.arsdigita.util.parameter.Parameter;
import com.arsdigita.util.parameter.StringParameter;

/**
 * Configuration for {@link SciProject}.
 * 
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class SciProjectConfig extends AbstractConfig {

    private final Parameter enableSubProjectsStep;
    private final Parameter enableSuperProjectsStep;
    private final Parameter shortDescMaxLength;
    private final Parameter enableDescriptionDhtml;
    private final Parameter enableMembersAllInOne;
    private final Parameter enableMembersMerge;
    private final Parameter enableFunding;
    private final Parameter enableFundingDhtml;
    private final Parameter enableFundingVolume;
    private final Parameter fundingVolumeLength;
    private final Parameter permittedPersonType;
    private final Parameter tabs;

    public SciProjectConfig() {

        enableSubProjectsStep =
        new BooleanParameter(
                "com.arsdigita.cms.contenttypes.sciproject.enable_sub_projects_step",
                Parameter.REQUIRED,
                Boolean.TRUE);

        enableSuperProjectsStep =
        new BooleanParameter(
                "com.arsdigita.cms.contenttypes.sciproject.enable_super_projects_step",
                Parameter.REQUIRED,
                Boolean.TRUE);

        shortDescMaxLength = new IntegerParameter(
                "com.arsdigita.cms.contenttypes.sciproject.shortdesc.max_length",
                Parameter.REQUIRED,
                500);

        enableDescriptionDhtml =
        new BooleanParameter(
                "com.arsdigita.cms.contenttypes.sciproject.enable_description_dhtml",
                Parameter.REQUIRED,
                Boolean.TRUE);

        enableMembersAllInOne =
        new BooleanParameter(
                "com.arsdigita.cms.contenttypes.sciproject.enable_members_all_in_one",
                Parameter.REQUIRED,
                Boolean.FALSE);

        enableMembersMerge = new BooleanParameter(
                "com.arsdigita.cms.contenttypes.sciproject.enable_members_merge",
                Parameter.REQUIRED,
                Boolean.TRUE);

        enableFunding = new BooleanParameter(
                "com.arsdigita.cms.contenttypes.sciproject.enable_funding",
                Parameter.REQUIRED,
                Boolean.TRUE);
        
        enableFundingDhtml = new BooleanParameter(
                "com.arsdigita.cms.contenttypes.sciproject.enable_funding_dhtml",
                Parameter.REQUIRED,
                Boolean.TRUE);
        
        enableFundingVolume = new BooleanParameter(
                "com.arsdigita.cms.contenttypes.sciproject.enable_funding_volume", 
                Parameter.REQUIRED, 
                Boolean.TRUE);
        
        fundingVolumeLength = new IntegerParameter(
                "com.arsdigita.cms.contenttypes.sciproject.enable_funding_length", 
                Parameter.REQUIRED, 
                128);
        
        permittedPersonType = new StringParameter(
                "com.arsdigita.cms.contenttypes.sciproject.permitted_person_type",
                Parameter.REQUIRED,
                "com.arsdigita.cms.contenttypes.GenericPerson");
        
        tabs = new StringParameter(
                "com.arsdigita.cms.contenttypes.sciproject.tabs",
                Parameter.REQUIRED,
                "summary:com.arsdigita.cms.contenttypes.ui.SciProjectSummaryTab;desc:com.arsdigita.cms.contenttypes.ui.SciProjectDescTab");

        register(enableSubProjectsStep);
        register(enableSuperProjectsStep);
        register(shortDescMaxLength);
        register(enableDescriptionDhtml);
        register(enableMembersAllInOne);
        register(enableMembersMerge);
        register(enableFunding);
        register(enableFundingDhtml);
        register(enableFundingVolume);        
        register(fundingVolumeLength);
        register(permittedPersonType);
        register(tabs);

        loadInfo();
    }

    public final boolean getEnableSubProjectsStep() {
        return (Boolean) get(enableSubProjectsStep);
    }

    public final boolean getEnableSuperProjectsStep() {
        return (Boolean) get(enableSuperProjectsStep);
    }

    public final int getShortDescMaxLength() {
        return (Integer) get(shortDescMaxLength);
    }

    public final boolean getEnableDescriptionDhtml() {
        return (Boolean) get(enableDescriptionDhtml);
    }

    public final boolean getEnableMembersAllInOne() {
        return (Boolean) get(enableMembersAllInOne);
    }

    public final boolean getEnableMembersMerge() {
        return (Boolean) get(enableMembersMerge);
    }
    
    public final boolean getEnableFunding() {
        return (Boolean) get(enableFunding);        
    }
    
    public final boolean getEnableFundingDhtml() {
        return (Boolean) get(enableFundingDhtml);        
    }
    
    public final boolean getEnableFundingVolume() {
        Object value = get(enableFundingVolume);
        return (Boolean) value;
        //return (Boolean) get(enableFundingVolume);
    }
    
    public final int getFundingVolumeLength() {
        return (Integer) get(fundingVolumeLength);
    }
    
    public final String getPermittedPersonType() {
        return (String) get(permittedPersonType);
    }
    
    public final String getTabs() {
        return (String) get(tabs);
    }
}
