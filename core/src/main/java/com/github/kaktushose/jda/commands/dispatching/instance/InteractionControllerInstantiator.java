package com.github.kaktushose.jda.commands.dispatching.instance;

import com.github.kaktushose.jda.commands.annotations.interactions.Interaction;
import com.github.kaktushose.jda.commands.dispatching.Runtime;
import com.github.kaktushose.jda.commands.extension.Implementation;
import net.dv8tion.jda.api.JDA;
import org.jetbrains.annotations.NotNull;

/// An [InteractionControllerInstantiator] is used get instances of classes annotated with [Interaction], if needed creating those.
///
/// At the time of [`Runtime`]({@docRoot}/index.html#runtime-concept-heading) creation, [#forRuntime(String, JDA)] is called,
/// allowing the InteractionControllerInstantiator to provide an instance that has not to be thread safe and is bound to one Runtime.
///
/// Please also note that per [`Runtime`]({@docRoot}/index.html#runtime-concept-heading) there can be multiple
/// classes annotated with [Interaction] but there can be only one instance per class of those per [`Runtime`]({@docRoot}/index.html#runtime-concept-heading).
/// Instances of interactions should be treated like runtime scoped singletons, so to speak.
@FunctionalInterface
public non-sealed interface InteractionControllerInstantiator extends Implementation.ExtensionProvidable {

    /// This method will be called each time an instance of a class annotated with [Interaction] is needed.
    ///
    /// @param clazz   the [Class] of needed instance
    /// @param context a context that gives additional useful information or provide some needed functionality
    <T> T instance(Class<T> clazz, Context context);

    /// Called each time a new [`Runtime`]({@docRoot}/index.html#runtime-concept-heading) is created.
    ///
    /// @param id  the runtime id
    /// @param jda the [JDA] instance associated with the event responsible for creating the new Runtime instance
    /// @return a specific instance of [InteractionControllerInstantiator] belonging to provided runtime.
    default InteractionControllerInstantiator forRuntime(String id, JDA jda) {
        return this;
    }

    class Context {
        private final Runtime runtime;

        public Context(@NotNull Runtime runtime) {
            this.runtime = runtime;
        }

        /// the runtime id
        @NotNull
        public String runtimeId() {
            return runtime.id();
        }
    }
}
