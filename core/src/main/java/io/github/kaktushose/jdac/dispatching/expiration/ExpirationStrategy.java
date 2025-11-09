package io.github.kaktushose.jdac.dispatching.expiration;

import io.github.kaktushose.jdac.dispatching.events.Event;

/// Classes implementing [ExpirationStrategy] define a strategy when a [`Runtime`]({@docRoot}/index.html#runtime-concept-heading) should be closed.
///
/// There are two strategies available:
/// - [Inactivity]: closes a [`Runtime`]({@docRoot}/index.html#runtime-concept-heading) after the passed amount of time
/// - [Explicit]: **Only** closes a runtime when [Event#closeRuntime()] is called.
///
/// @implNote The [Inactivity] strategy will check all active [`Runtimes`]({@docRoot}/index.html#runtime-concept-heading) at every incoming jda event. If a
/// [`Runtime`]({@docRoot}/index.html#runtime-concept-heading) didn't handle any events in the last *x* minutes, it will be closed.
public sealed interface ExpirationStrategy {

    /// Default [ExpirationStrategy] of [Inactivity], that closes [`Runtimes`]({@docRoot}/index.html#runtime-concept-heading) after *15* minutes of inactivity.
    ExpirationStrategy AFTER_15_MINUTES = new ExpirationStrategy.Inactivity(15);

    /// Default [ExpirationStrategy] of [Explicit].
    ExpirationStrategy EXPLICIT = new ExpirationStrategy.Explicit();

    /// [ExpirationStrategy] that closes a [`Runtime`]({@docRoot}/index.html#runtime-concept-heading) after the passed amount of time.
    ///
    /// @param afterMinutes the amount of time after a [`Runtime`]({@docRoot}/index.html#runtime-concept-heading) should be closed, in minutes.
    record Inactivity(long afterMinutes) implements ExpirationStrategy {}

    /// [ExpirationStrategy] that closes a [`Runtime`]({@docRoot}/index.html#runtime-concept-heading) **only** when [Event#closeRuntime()] has been called.
    record Explicit() implements ExpirationStrategy {}
}
