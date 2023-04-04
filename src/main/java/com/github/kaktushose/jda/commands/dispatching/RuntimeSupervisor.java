package com.github.kaktushose.jda.commands.dispatching;

import com.github.kaktushose.jda.commands.reflect.interactions.GenericInteraction;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class RuntimeSupervisor {

    private final Map<String, InteractionRuntime> runtimes;
    private final ScheduledExecutorService executor;

    public RuntimeSupervisor() {
        runtimes = new HashMap<>();
        executor = new ScheduledThreadPoolExecutor(4);
    }

    public InteractionRuntime newRuntime(GenericCommandInteractionEvent event, GenericInteraction interaction)
            throws InvocationTargetException, InstantiationException, IllegalAccessException {

        Object instance = interaction.newInstance();
        String id = event.getId();
        InteractionRuntime runtime = new InteractionRuntime(id, instance);

        runtimes.put(id, runtime);
        executor.schedule(() ->  runtimes.remove(id), 15, TimeUnit.MINUTES);

        return runtime;
    }

    public Optional<InteractionRuntime> getRuntime(GenericComponentInteractionCreateEvent event) {
        String[] split = event.getComponentId().split("\\.");
        if (split.length != 3) {
            return Optional.empty();
        }
        return Optional.ofNullable(runtimes.get(split[2]));
    }

    public static class InteractionRuntime {
        private final String instanceId;
        private final Object instance;

        public InteractionRuntime(String instanceId, Object instance) {
            this.instanceId = instanceId;
            this.instance = instance;
        }

        public String getInstanceId() {
            return instanceId;
        }

        public Object getInstance() {
            return instance;
        }
    }
}
