package io.github.kaktushose.jdac.definitions.interactions;

import io.github.kaktushose.jdac.JDACommands;
import io.github.kaktushose.jdac.definitions.Definition;
import io.github.kaktushose.jdac.dispatching.events.ReplyableEvent;
import io.github.kaktushose.jdac.exceptions.internal.JDACException;

/// Representation of a custom id used in modals, buttons or select menus.
///
/// JDA-Commands uses the customid to store important information on how to process a component interaction.
/// This includes:
/// - the [#runtimeId()] to find the right "conversation"
/// - the [#definitionId()] to find the right interaction controller method
/// - a [#counter()] allowing us to reuse the same component multiple times in one single reply.
///   This isn't important when receiving component interactions.
///
/// ### counter
/// The counter just gets incremented for each component of a message. If you have to set it manually
/// (e.g. when using [JDACommands#getButton(Class, String, int)] or [ReplyableEvent#getSelectMenu(String, int)])
/// just set it to a value that isn't used already with the same component in this reply.
///
/// For example, you can't do following:
/// ```java
/// JDACommands jdac = ...
///
/// Button one = jdac.getButton(MyInteraction.class, "onButton", 0);
/// Button two = jdac.getButton(MyInteraction.class, "onButton", 0);
///
/// var data = new MessageCreateBuilder()
///         .addComponents(one, two)
///         .build();
///
/// channel.sendMessage(data);
/// ```
///
/// Instead, you have to give each button instance a unique counter, because you're sending the same button.
///
/// ```java
/// Button one = jdac.getButton(MyInteraction.class, "onButton", 0);
/// Button two = jdac.getButton(MyInteraction.class, "onButton", 1);
/// ```
///
/// But, you can do the following since you're having two different buttons:
/// ```java
/// Button one = jdac.getButton(MyInteraction.class, "aButton", 0);
/// Button two = jdac.getButton(MyInteraction.class, "otherButton", 0);
/// ```
///
/// @param runtimeId    the id of the [`Runtime`]({@docRoot}/index.html#runtime-concept-heading) this custom id is bound to
///                     or the literal `independent`.
/// @param definitionId the [Definition#definitionId()]
/// @param counter a counter that gets added as a suffix to the customid, allowing the reuse of components in one message.
///                For modals this should just be 0 because you can only reply with "one component" - the modal itself
/// @implNote the custom id has the following format: `jdac.runtimeId.definitionId.componentCounter`
public record CustomId(String runtimeId, String definitionId, String counter) {
    public static final String BOUND_CUSTOM_ID_REGEX = "^jdac\\.[0-9a-fA-F-]{36}\\.-?\\d+.\\d+$";
    private static final String PREFIX = "jdac";
    private static final String INDEPENDENT_ID = "independent";
    public static final String INDEPENDENT_CUSTOM_ID_REGEX = "^jdac\\.%s\\.-?\\d+.\\d+$".formatted(INDEPENDENT_ID);

    public CustomId {
        if (!runtimeId.matches("[0-9a-fA-F-]{36}") && !runtimeId.equals(INDEPENDENT_ID)) {
            throw new IllegalArgumentException(JDACException.errorMessage("invalid-runtime-id"));
        }
    }

    /// Constructs a new [CustomId] from the given String.
    ///
    /// @param customId the custom id String
    /// @return the [CustomId]
    public static CustomId fromMerged(String customId) {
        if (isInvalid(customId)) {
            throw new IllegalArgumentException(JDACException.errorMessage("invalid-custom-id"));
        }
        var split = customId.split("\\.");
        return new CustomId(split[1], split[2], split[3]);
    }

    /// Constructs a new runtime-independent [CustomId] from the given definition id.
    ///
    /// @param definitionId the definition id to construct the [CustomId] from
    /// @param counter the counter to add as a suffix, see class docs
    /// @return a new runtime-independent [CustomId]
    public static CustomId independent(String definitionId, int counter) {
        return new CustomId(INDEPENDENT_ID, definitionId, String.valueOf(counter));
    }

    /// Checks if the passed custom id *doesn't* conform to the defined format of jda-commands.
    ///
    /// @return `true` if the passed custom id *doesn't* conform to the jda-commands format
    public static boolean isInvalid(String customId) {
        return !(customId.matches(BOUND_CUSTOM_ID_REGEX) || customId.matches(INDEPENDENT_CUSTOM_ID_REGEX));
    }

    /// The String representation of this custom id.
    public String merged() {
        return "%s.%s.%s.%s".formatted(PREFIX, runtimeId, definitionId, counter);
    }

    /// Gets the runtime id of this custom id.
    ///
    /// @return the runtime id
    /// @throws IllegalStateException if this custom id is runtime-independent
    public String runtimeId() {
        if (isIndependent()) {
            throw new IllegalStateException(JDACException.errorMessage("independent-runtime-id"));
        }
        return runtimeId;
    }

    /// Checks if the passed custom id is runtime-independent.
    ///
    /// @return `true` if the custom id is runtime-independent
    public boolean isIndependent() {
        return runtimeId.equals(INDEPENDENT_ID);
    }

    /// Checks if the passed custom id is runtime-bound.
    ///
    /// @return `true` if the custom id is runtime-bound
    public boolean isBound() {
        return !isIndependent();
    }
}
