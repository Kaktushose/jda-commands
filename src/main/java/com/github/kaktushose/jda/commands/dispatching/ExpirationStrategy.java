package com.github.kaktushose.jda.commands.dispatching;

import com.github.kaktushose.jda.commands.dispatching.events.Event;
import com.github.kaktushose.jda.commands.dispatching.internal.Runtime;

/// Classes implementing [ExpirationStrategy] define a strategy when a [Runtime] should be closed.
///
/// There are two strategies available:
/// - [Inactivity]: closes a [Runtime] after the passed amount of time
/// - [Explicit]: **Only** closes a runtime when [Event#closeRuntime()] is called.
///
/// @implNote The [Inactivity] strategy will check all active [Runtime]s at every incoming jda event. If a
/// [Runtime] didn't handle any events in the last *x* minutes, it will be closed.
/// @see Runtime
/// @since 4.0.0
public sealed interface ExpirationStrategy {

    /// Default [ExpirationStrategy] of [Inactivity], that closes [Runtime]s after *15* minutes of inactivity.
    ExpirationStrategy AFTER_15_MINUTES = new ExpirationStrategy.Inactivity(15);

    /// Default [ExpirationStrategy] of [Explicit].
    ExpirationStrategy EXPLICIT = new ExpirationStrategy.Explicit();

    /// [ExpirationStrategy] that closes a [Runtime] after the passed amount of time.
    ///
    /// @param afterMinutes the amount of time after a [Runtime] should be closed, in minutes.
    record Inactivity(long afterMinutes) implements ExpirationStrategy {}

    /// [ExpirationStrategy] that closes a [Runtime] **only** when [Event#closeRuntime()] has been called.
    record Explicit() implements ExpirationStrategy {}
}
