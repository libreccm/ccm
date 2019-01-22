package org.libreccm.pagemodel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class ComponentRendererManager {

    private static final Logger LOGGER = LogManager
        .getLogger(ComponentRendererManager.class);

    private static final ComponentRendererManager INSTANCE
                                                      = new ComponentRendererManager();

    private final Map<Class<?>, ComponentRenderer<?>> componentRenderers;

    public ComponentRendererManager() {

        componentRenderers = new HashMap<>();
    }

    public static ComponentRendererManager getInstance() {
        return INSTANCE;
    }

    /**
     * Find an implementation of the {@link ComponentRenderer} interface for a
     * specific {@link ComponentModel}.
     *
     * @param <M>                 Generic variable for the subtype of
     *                            {@link ComponentModel} which is produced by
     *                            the {@link ComponentRenderer} implementation.
     * @param componentModelClass The sub class of the {@link ComponentModel}
     *                            for which is processed by the
     *                            {@link ComponentRenderer}.
     *
     * @return An {@link Optional} containing the implementation of the
     *         {@link ComponentRenderer} interface for the specified parameters.
     *         If there is no implementation for the specified parameters an
     *         empty {@link Optional} is returned.
     */
    @SuppressWarnings({"unchecked"})
    public <M extends ComponentModel> Optional<ComponentRenderer<M>> findComponentRenderer(
        final Class<M> componentModelClass) {

        if (componentRenderers.containsKey(componentModelClass)) {
            return Optional.of((ComponentRenderer<M>) componentRenderers
                .get(componentModelClass));
        } else {
            return Optional.empty();
        }
    }

    public <M extends ComponentModel> void registerComponentRenderer(
        final Class<M> componentClass,
        final ComponentRenderer<M> componentRenderer) {

        componentRenderers.put(componentClass, componentRenderer);
    }

}
