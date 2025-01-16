package com.github.kaktushose.jda.commands.dispatching.instance;

import com.github.kaktushose.jda.commands.annotations.interactions.Interaction;
import com.github.kaktushose.jda.commands.dispatching.Runtime;

/// An [InstanceProvider] is used get instances of classes annotated with [Interaction], if needed creating those.
///
/// At the time of [`Runtime`]({@docRoot}/index.html#runtime-concept-heading) creation [#forRuntime(String)] is called,
/// allowing the InstanceProvider to provide an instance that hasn't to be thread safe and is bound to one Runtime.
///
/// Please also note that per [`Runtime`]({@docRoot}/index.html#runtime-concept-heading) there can be multiple
/// classes annotated with [Interaction] but there can be only one instance per class of those per [`Runtime`]({@docRoot}/index.html#runtime-concept-heading).
/// Instances of interactions should be treated like runtime scoped singletons, so to speak.
@FunctionalInterface
public interface InstanceProvider {
    /// This method will be called each time an instance of a specific class is needed.
    /// Please note the points specified in the class docs.
    ///
    /// @param clazz the [Class] of needed instance
    /// @param context a context that gives additional useful information or provide some needed functionality
    <T> T instance(Class<T> clazz, Context context);

    /// Called each time a [`Runtime`]({@docRoot}/index.html#runtime-concept-heading) creation [#forRuntime(String)] is created.
    ///
    /// @param id the runtime id
    /// @return a specific instance of [InstanceProvider] belonging to provided runtime.
    default InstanceProvider forRuntime(String id) {
        return this;
    }

    class Context {
        private final Runtime runtime;

        public Context(Runtime runtime) {
            this.runtime = runtime;
        }

        /// the runtime id
        public String runtimeId() {
            return runtime.id();
        }
    }
}
