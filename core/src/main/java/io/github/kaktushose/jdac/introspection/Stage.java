package io.github.kaktushose.jdac.introspection;

import io.github.kaktushose.jdac.JDACBuilder;
import io.github.kaktushose.jdac.annotations.IntrospectionAccess;
import io.github.kaktushose.jdac.configuration.Property;
import io.github.kaktushose.jdac.dispatching.context.InvocationContext;
import io.github.kaktushose.jdac.dispatching.instance.InteractionControllerInstantiator;
import io.github.kaktushose.jdac.dispatching.middleware.Middleware;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;

/// The stage or "level" inside the framework at which a properties value is set.
///
/// JDA-Commands has the following stages in this order:
/// 1. [#CONFIGURATION] -> setting of builder properties, extension loading, construction of framework components
/// 2. [#INITIALIZED] -> after starting the framework (basically after [JDACBuilder#start()] completed), e.g. all
/// definitions are indexed etc.
/// 3. [#RUNTIME] -> inside a runtime but outside of processing an [GenericInteractionCreateEvent], e.g. when
///  [InteractionControllerInstantiator#instance(Class, Introspection)] is called
/// 4. [#PREPARATION] -> during the preparation of an [GenericInteractionCreateEvent] for the [#INTERACTION] stage, e
/// .g. where type adapters get called
/// 5. [#INTERACTION] -> when processing an [GenericInteractionCreateEvent], e.g. in
///  [Middleware#accept(InvocationContext)] or inside a user defined interaction controller method
///
/// Generally, a stage includes all properties that were set in a former stage:
/// - [#INTERACTION] includes [#CONFIGURATION], [#INITIALIZED], [#RUNTIME] and [#PREPARATION]
/// - [#PREPARATION] includes [#CONFIGURATION], [#INITIALIZED] and [#RUNTIME]
/// - [#RUNTIME] includes [#CONFIGURATION] and [#INITIALIZED]
/// - [#INITIALIZED] includes [#CONFIGURATION]
///
/// To know in which stage a [Property] is available take a look at [Property#stage()].
///
/// More technical, a [Stage] with a certain [ordinal][Stage#ordinal()] contains all stages with a lower ordinal value:
///
/// `stageA ⊆ stageB if stageA.ordinal < stageB.ordinal`
///
/// Many user implementable methods are annotated with [IntrospectionAccess]. To get a hint in which stage this method
/// will be called by JDA-Commands you can take a look at [IntrospectionAccess#value()].
// don't change order!
public enum Stage {
    CONFIGURATION,
    INITIALIZED,
    RUNTIME,
    PREPARATION,
    INTERACTION
}
