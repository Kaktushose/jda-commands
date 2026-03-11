package io.github.kaktushose.jdac.dispatching.instance;

import io.github.kaktushose.jdac.annotations.IntrospectionAccess;
import io.github.kaktushose.jdac.annotations.interactions.Interaction;
import io.github.kaktushose.jdac.introspection.Introspection;
import io.github.kaktushose.jdac.introspection.Stage;
import io.github.kaktushose.jdac.message.resolver.MessageResolver;

/// An [Instantiator] is used get instances of classes, if needed creating those.
///
/// For classes annotated with [Interaction], there can be only one instance per class of those per
/// [`Runtime`]({@docRoot}/index.html#runtime-concept-heading). Instances of interactions should be treated like
/// runtime scoped singletons, so to speak.
@FunctionalInterface
public interface Instantiator {

    /// This method will be called each time an instance of a class is needed.
    ///
    /// The provided [Introspection] instance or [Introspection#accessScoped()] can be used to retrieve other
    /// components/parts of this framework, e.g. [MessageResolver].
    ///
    /// @param clazz         the [Class] of needed instance
    /// @param introspection the [Introspection] instance of this runtime (stage = [Stage#RUNTIME]).
    @IntrospectionAccess(Stage.RUNTIME)
    <T> T instance(Class<T> clazz, Introspection introspection);
}
