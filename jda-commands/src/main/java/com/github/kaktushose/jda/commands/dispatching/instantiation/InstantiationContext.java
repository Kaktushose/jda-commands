package com.github.kaktushose.jda.commands.dispatching.instantiation;

import com.github.kaktushose.jda.commands.dispatching.Runtime;

public class InstantiationContext {
    private final com.github.kaktushose.jda.commands.dispatching.Runtime runtime;

    public InstantiationContext(Runtime runtime) {
        this.runtime = runtime;
    }

    public <T> T interactionClass(Class<T> clazz) {
        return runtime.interactionClass(clazz);
    }
}
