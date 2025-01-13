package com.github.kaktushose.jda.commands.dispatching.instantiation;

@FunctionalInterface
public interface Instantiator {
    <T> T instantiate(Class<T> clazz, InstantiationContext context);

    default Instantiator forRuntime(String id) {
        return this;
    }
}
