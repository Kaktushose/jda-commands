package com.github.kaktushose.jda.commands.dispatching.events.interactions;

import com.github.kaktushose.jda.commands.definitions.interactions.InteractionRegistry;
import com.github.kaktushose.jda.commands.dispatching.Runtime;
import com.github.kaktushose.jda.commands.dispatching.events.Event;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.CommandAutoCompleteInteraction;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

/// This class is a subclass of [Event].
/// It provides additional features for replying to a [CommandAutoCompleteInteractionEvent].
///
/// @see Event
public final class AutoCompleteEvent extends Event<CommandAutoCompleteInteractionEvent> {

    /// Constructs a new AutoCompleteEvent.
    ///
    /// @param event    the [CommandAutoCompleteInteractionEvent] this event holds
    /// @param registry the corresponding [InteractionRegistry]
    /// @param runtime  the corresponding [Runtime]
    public AutoCompleteEvent(CommandAutoCompleteInteractionEvent event,
                             InteractionRegistry registry,
                             Runtime runtime) {
        super(event, registry, runtime);
    }

    /**
     * Reply with up to {@value OptionData#MAX_CHOICES} choices which can be picked from by the user.
     * <br>The user may continue writing inputs instead of using one of your choices.
     *
     * @param choices The choice suggestions to present to the user, 0-{@value OptionData#MAX_CHOICES} choices
     * @throws IllegalArgumentException <ul>
     *                                  <li>If {@code null} is provided</li>
     *                                  <li>If more than {@value OptionData#MAX_CHOICES} choices are added</li>
     *                                  <li>If any of the choice names are empty or longer than {@value OptionData#MAX_CHOICE_NAME_LENGTH}</li>
     *                                  <li>If the option type is incompatible with the choice type</li>
     *                                  <li>If the numeric value of any of the choices is not between {@value OptionData#MIN_NEGATIVE_NUMBER} and {@value OptionData#MAX_POSITIVE_NUMBER}</li>
     *                                  <li>If the string value of any of the choices is empty or longer than {@value OptionData#MAX_CHOICE_VALUE_LENGTH}</li>
     *
     *                                  </ul>
     */
    public void replyChoices(Collection<Command.Choice> choices) {
        event.replyChoices(choices).queue();
    }

    /**
     * Reply with up to {@value OptionData#MAX_CHOICES} choices which can be picked from by the user.
     * <br>The user may continue writing inputs instead of using one of your choices.
     *
     * @param choices The choice suggestions to present to the user, 0-{@value OptionData#MAX_CHOICES} choices
     * @throws IllegalArgumentException <ul>
     *                                  <li>If {@code null} is provided</li>
     *                                  <li>If more than {@value OptionData#MAX_CHOICES} choices are added</li>
     *                                  <li>If any of the choice names are empty or longer than {@value OptionData#MAX_CHOICE_NAME_LENGTH}</li>
     *                                  <li>If the option type is incompatible with the choice type</li>
     *                                  <li>If the numeric value of any of the choices is not between {@value OptionData#MIN_NEGATIVE_NUMBER} and {@value OptionData#MAX_POSITIVE_NUMBER}</li>
     *                                  <li>If the string value of any of the choices is empty or longer than {@value OptionData#MAX_CHOICE_VALUE_LENGTH}</li>
     *                                  </ul>
     */
    public void replyChoices(Command.Choice... choices) {
        replyChoices(Arrays.asList(choices));
    }

    /**
     * Reply with up to {@value OptionData#MAX_CHOICES} choices which can be picked from by the user.
     * <br>The user may continue writing inputs instead of using one of your choices.
     *
     * @param name  The choice name to show to the user, 1-{@value OptionData#MAX_CHOICE_NAME_LENGTH} characters
     * @param value The choice value, 1-{@value OptionData#MAX_CHOICE_VALUE_LENGTH} characters
     * @throws IllegalArgumentException <ul>
     *                                  <li>If {@code null} is provided</li>
     *                                  <li>If more than {@value OptionData#MAX_CHOICES} choices are added</li>
     *                                  <li>If the choice name is empty or longer than {@value OptionData#MAX_CHOICE_NAME_LENGTH}</li>
     *                                  <li>If the option type is not {@link OptionType#STRING}</li>
     *                                  <li>If the string value of any of the choices is empty or longer than {@value OptionData#MAX_CHOICE_VALUE_LENGTH}</li>
     *                                  </ul>
     */
    public void replyChoice(String name, String value) {
        replyChoices(new Command.Choice(name, value));
    }

    /**
     * Reply with up to {@value OptionData#MAX_CHOICES} choices which can be picked from by the user.
     * <br>The user may continue writing inputs instead of using one of your choices.
     *
     * @param name  The choice name to show to the user, 1-{@value OptionData#MAX_CHOICE_NAME_LENGTH} characters
     * @param value The choice value, must be between {@value OptionData#MIN_NEGATIVE_NUMBER} and {@value OptionData#MAX_POSITIVE_NUMBER}
     * @throws IllegalArgumentException <ul>
     *                                  <li>If {@code null} is provided</li>
     *                                  <li>If more than {@value OptionData#MAX_CHOICES} choices are added</li>
     *                                  <li>If the choice name is empty or longer than {@value OptionData#MAX_CHOICE_NAME_LENGTH}</li>
     *                                  <li>If the option type is incompatible with the choice type</li>
     *                                  <li>If the value of is not between {@value OptionData#MIN_NEGATIVE_NUMBER} and {@value OptionData#MAX_POSITIVE_NUMBER}</li>
     *                                  </ul>
     */
    public void replyChoice(String name, long value) {
        replyChoices(new Command.Choice(name, value));
    }

    /**
     * Reply with up to {@value OptionData#MAX_CHOICES} choices which can be picked from by the user.
     * <br>The user may continue writing inputs instead of using one of your choices.
     *
     * @param name  The choice name to show to the user, 1-{@value OptionData#MAX_CHOICE_NAME_LENGTH} characters
     * @param value The choice value, must be between {@value OptionData#MIN_NEGATIVE_NUMBER} and {@value OptionData#MAX_POSITIVE_NUMBER}
     * @throws IllegalArgumentException <ul>
     *                                  <li>If {@code null} is provided</li>
     *                                  <li>If more than {@value OptionData#MAX_CHOICES} choices are added</li>
     *                                  <li>If the choice name is empty or longer than {@value OptionData#MAX_CHOICE_NAME_LENGTH}</li>
     *                                  <li>If the option type is incompatible with the choice type</li>
     *                                  <li>If the value of is not between {@value OptionData#MIN_NEGATIVE_NUMBER} and {@value OptionData#MAX_POSITIVE_NUMBER}</li>
     *                                  </ul>
     */
    public void replyChoice(String name, double value) {
        replyChoices(new Command.Choice(name, value));
    }

    /**
     * Reply with up to {@value OptionData#MAX_CHOICES} choices which can be picked from by the user.
     * <br>The user may continue writing inputs instead of using one of your choices.
     *
     * <p>The provided strings will be used as value and name for the {@link net.dv8tion.jda.api.interactions.commands.Command.Choice Choices}.
     *
     * @param choices The choice suggestions to present to the user, each limited to {@value OptionData#MAX_CHOICE_NAME_LENGTH} characters
     * @throws IllegalArgumentException <ul>
     *                                  <li>If {@code null} is provided</li>
     *                                  <li>If more than {@value OptionData#MAX_CHOICES} choices are added</li>
     *                                  <li>If any of the choice names are empty or longer than {@value OptionData#MAX_CHOICE_NAME_LENGTH}</li>
     *                                  <li>If the string value of any of the choices is empty or longer than {@value OptionData#MAX_CHOICE_VALUE_LENGTH}</li>
     *                                  </ul>
     */
    public void replyChoiceStrings(String... choices) {
        replyChoices(Arrays.stream(choices).map(it -> new Command.Choice(it, it)).collect(Collectors.toList()));
    }

    /**
     * Reply with up to {@value OptionData#MAX_CHOICES} choices which can be picked from by the user.
     * <br>The user may continue writing inputs instead of using one of your choices.
     *
     * <p>The provided strings will be used as value and name for the {@link net.dv8tion.jda.api.interactions.commands.Command.Choice Choices}.
     *
     * @param choices The choice suggestions to present to the user, each limited to {@value OptionData#MAX_CHOICE_NAME_LENGTH} characters
     * @throws IllegalArgumentException <ul>
     *                                  <li>If {@code null} is provided</li>
     *                                  <li>If more than {@value OptionData#MAX_CHOICES} choices are added</li>
     *                                  <li>If any of the choice names are empty or longer than {@value OptionData#MAX_CHOICE_NAME_LENGTH}</li>
     *                                  <li>If the string value of any of the choices is empty or longer than {@value OptionData#MAX_CHOICE_VALUE_LENGTH}</li>
     *                                  </ul>
     */
    public void replyChoiceStrings(Collection<String> choices) {
        replyChoices(choices.stream().map(it -> new Command.Choice(it, it)).collect(Collectors.toList()));
    }

    /**
     * Reply with up to {@value OptionData#MAX_CHOICES} choices which can be picked from by the user.
     * <br>The user may continue writing inputs instead of using one of your choices.
     *
     * <p>The string values of the provided longs will be used as value and name for the {@link net.dv8tion.jda.api.interactions.commands.Command.Choice Choices}.
     *
     * @param choices The choice suggestions to present to the user
     * @throws IllegalArgumentException <ul>
     *                                  <li>If {@code null} is provided</li>
     *                                  <li>If more than {@value OptionData#MAX_CHOICES} choices are added</li>
     *                                  <li>If the option type is incompatible with the choice type</li>
     *                                  <li>If the numeric value of any of the choices is not between {@value OptionData#MIN_NEGATIVE_NUMBER} and {@value OptionData#MAX_POSITIVE_NUMBER}</li>
     *                                  </ul>
     */
    public void replyChoiceLongs(long... choices) {
        replyChoices(Arrays.stream(choices).mapToObj(it -> new Command.Choice(String.valueOf(it), it)).collect(Collectors.toList()));
    }

    /**
     * Reply with up to {@value OptionData#MAX_CHOICES} choices which can be picked from by the user.
     * <br>The user may continue writing inputs instead of using one of your choices.
     *
     * <p>The string values of the provided longs will be used as value and name for the {@link net.dv8tion.jda.api.interactions.commands.Command.Choice Choices}.
     *
     * @param choices The choice suggestions to present to the user
     * @throws IllegalArgumentException <ul>
     *                                  <li>If {@code null} is provided</li>
     *                                  <li>If more than {@value OptionData#MAX_CHOICES} choices are added</li>
     *                                  <li>If the option type is incompatible with the choice type</li>
     *                                  <li>If the numeric value of any of the choices is not between {@value OptionData#MIN_NEGATIVE_NUMBER} and {@value OptionData#MAX_POSITIVE_NUMBER}</li>
     *                                  </ul>
     */
    public void replyChoiceLongs(Collection<Long> choices) {
        replyChoices(choices.stream().map(it -> new Command.Choice(String.valueOf(it), it)).collect(Collectors.toList()));
    }

    /**
     * Reply with up to {@value OptionData#MAX_CHOICES} choices which can be picked from by the user.
     * <br>The user may continue writing inputs instead of using one of your choices.
     *
     * <p>The string values of the provided doubles will be used as value and name for the {@link net.dv8tion.jda.api.interactions.commands.Command.Choice Choices}.
     *
     * @param choices The choice suggestions to present to the user
     * @throws IllegalArgumentException <ul>
     *                                  <li>If {@code null} is provided</li>
     *                                  <li>If more than {@value OptionData#MAX_CHOICES} choices are added</li>
     *                                  <li>If the option type is incompatible with the choice type</li>
     *                                  <li>If the numeric value of any of the choices is not between {@value OptionData#MIN_NEGATIVE_NUMBER} and {@value OptionData#MAX_POSITIVE_NUMBER}</li>
     *                                  </ul>
     */

    public void replyChoiceDoubles(double... choices) {
        replyChoices(Arrays.stream(choices).mapToObj(it -> new Command.Choice(String.valueOf(it), it)).collect(Collectors.toList()));
    }

    /**
     * Reply with up to {@value OptionData#MAX_CHOICES} choices which can be picked from by the user.
     * <br>The user may continue writing inputs instead of using one of your choices.
     *
     * <p>The string values of the provided doubles will be used as value and name for the {@link net.dv8tion.jda.api.interactions.commands.Command.Choice Choices}.
     *
     * @param choices The choice suggestions to present to the user
     * @throws IllegalArgumentException <ul>
     *                                  <li>If {@code null} is provided</li>
     *                                  <li>If more than {@value OptionData#MAX_CHOICES} choices are added</li>
     *                                  <li>If the option type is incompatible with the choice type</li>
     *                                  <li>If the numeric value of any of the choices is not between {@value OptionData#MIN_NEGATIVE_NUMBER} and {@value OptionData#MAX_POSITIVE_NUMBER}</li>
     *                                  </ul>
     */

    public void replyChoiceDoubles(Collection<Double> choices) {
        replyChoices(choices.stream().map(it -> new Command.Choice(String.valueOf(it), it)).collect(Collectors.toList()));
    }

    /// The name of the input field, usually an option name in [CommandAutoCompleteInteraction].
    ///
    /// @return The option name
    
    public String getName() {
        return event.getFocusedOption().getName();
    }

    /// The query value that the user is currently typing.
    ///
    /// This is not validated and may not be a valid value for an actual command.
    /// For instance, a user may input invalid numbers for [OptionType#NUMBER].
    ///
    /// @return The current auto-completable query value
    
    public String getValue() {
        return event.getFocusedOption().getValue();
    }

    /// The expected option type for this query.
    ///
    /// @return The option type expected from this auto-complete response
    
    public OptionType getOptionType() {
        return event.getFocusedOption().getType();
    }
}
