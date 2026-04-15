package io.github.kaktushose.jdac.property;

import io.github.kaktushose.jdac.JDACBuilder;
import io.github.kaktushose.jdac.annotations.IntrospectionAccess;
import io.github.kaktushose.jdac.dispatching.context.InvocationContext;
import io.github.kaktushose.jdac.dispatching.instance.Instantiator;
import io.github.kaktushose.jdac.dispatching.middleware.Middleware;
import dev.goldmensch.propane.Scope;
import dev.goldmensch.propane.property.Property;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;

/// The scope or "scope" inside the framework at which a [properties][Property] value is set.
///
/// JDA-Commands has the following scopes in this order:
/// 1. [#CONFIGURATION] -> setting of builder properties, extension loading, construction of framework components
/// 2. [#INITIALIZED] -> after starting the framework (basically after [JDACBuilder#start()] completed), e.g. all definitions are indexed etc.
/// 3. [#RUNTIME] -> inside a runtime but outside of processing an [GenericInteractionCreateEvent], e.g. when [Instantiator#instance(Class, JDACIntrospection)] is called
/// 4. [#PREPARATION] -> during the preparation of an [GenericInteractionCreateEvent] for the [#INTERACTION] scope, e.g. where type adapters get called
/// 5. [#INTERACTION] -> when processing an [GenericInteractionCreateEvent], e.g. in [Middleware#accept(InvocationContext)] or inside a user defined interaction controller method
///
/// Generally, a scope includes all properties that were set in a former scope:
/// - [#INTERACTION] includes [#CONFIGURATION], [#INITIALIZED], [#RUNTIME] and [#PREPARATION]
/// - [#PREPARATION] includes [#CONFIGURATION], [#INITIALIZED] and [#RUNTIME]
/// - [#RUNTIME] includes [#CONFIGURATION] and [#INITIALIZED]
/// - [#INITIALIZED] includes [#CONFIGURATION]
///
/// To know in which scope a [Property] is available take a look at [Property#scope()].
///
/// More technical, a [Scope] with a certain [priority][Scope#priority()] contains all scopes with a lower ordinal value:
///
/// `scopeA ⊆ scopeB if scopeA.ordinal < scopeB.ordinal`
///
/// Many user implementable methods are annotated with [IntrospectionAccess]. To get a hint in which scope this method
/// will be called by JDA-Commands you can take a look at [IntrospectionAccess#value()].
// don't change order!
public enum JDACScope implements Scope {
    CONFIGURATION,

    INITIALIZED,

    RUNTIME,

    PREPARATION,

    INTERACTION;

    @Override
    public int priority() {
        return this.ordinal();
    }
}
