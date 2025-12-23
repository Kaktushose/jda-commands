package io.github.kaktushose.jdac.dispatching.instance;

import io.github.kaktushose.jdac.annotations.interactions.Interaction;
import io.github.kaktushose.jdac.introspection.Introspection;
import net.dv8tion.jda.api.JDA;

/// An [InteractionControllerInstantiator] is used get instances of classes annotated with [Interaction], if needed creating those.
///
/// At the time of [`Runtime`]({@docRoot}/index.html#runtime-concept-heading) creation, [#forRuntime(String, JDA)] is called,
/// allowing the InteractionControllerInstantiator to provide an instance that has not to be thread safe and is bound to one Runtime.
///
/// Please also note that per [`Runtime`]({@docRoot}/index.html#runtime-concept-heading) there can be multiple
/// classes annotated with [Interaction] but there can be only one instance per class of those per [`Runtime`]({@docRoot}/index.html#runtime-concept-heading).
/// Instances of interactions should be treated like runtime scoped singletons, so to speak.
@FunctionalInterface
public interface InteractionControllerInstantiator {

    /// This method will be called each time an instance of a class annotated with [Interaction] is needed.
    ///
    /// @param clazz   the [Class] of needed instance
    <T> T instance(Class<T> clazz, Introspection introspection);
}
