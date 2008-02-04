package com.arsdigita.profiler;

import com.arsdigita.runtime.AbstractConfig;
import com.arsdigita.util.parameter.BooleanParameter;
import com.arsdigita.util.parameter.Parameter;

/**
 * Configuration for simple profiler.
 *   
 * @author Alan Pevec
 */
public class ProfilerConfig extends AbstractConfig {

    private Parameter enabled;
    
    public ProfilerConfig() {
        enabled = new BooleanParameter(
                "waf.profiler.enabled",
                Parameter.REQUIRED,
                new Boolean(false)
        );
        register(enabled);
        loadInfo();
    }

    public boolean isEnabled() {
        return Boolean.TRUE.equals(get(enabled));
    }
}
