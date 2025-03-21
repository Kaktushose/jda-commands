package com.github.kaktushose.jda.commands.dispatching.reply.dynamic;

import com.github.kaktushose.jda.commands.definitions.interactions.CustomId;
import com.github.kaktushose.jda.commands.definitions.interactions.ModalDefinition;
import com.github.kaktushose.jda.commands.definitions.interactions.ModalDefinition.TextInputDefinition;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.modals.Modal;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import static net.dv8tion.jda.api.interactions.modals.Modal.MAX_COMPONENTS;

/// Builder for [Modal]s. Acts as a bridge between [ModalDefinition] and [Modal] for dynamic modifications.
public class ModalBuilder {

    private final CustomId customId;
    private final List<TextInputDefinition> components = new ArrayList<>(MAX_COMPONENTS);
    private final ModalDefinition modalDefinition;
    private String title;
    private Function<Modal, Modal> callback = Function.identity();

    /// Constructs a new [ModalBuilder].
    ///
    /// @param customId        the [CustomId] the [Modal] should have in the end
    /// @param modalDefinition the [ModalDefinition] to modify
    public ModalBuilder(CustomId customId, ModalDefinition modalDefinition) {
        this.customId = customId;
        this.modalDefinition = modalDefinition;
    }

    /// @see Modal.Builder#setTitle(String)
    @NotNull
    public ModalBuilder title(@NotNull String title) {
        this.title = title;
        return this;
    }

    /// Allows modification of a text input with the given callback.
    ///
    /// @param textInput the name of the method parameter the text input is assigned to
    /// @param callback the [Function] to modify the text input
    /// @see TextInput.Builder
    /// @return this instance for fluent interface
    public ModalBuilder textInput(@NotNull String textInput, Function<TextInput.Builder, TextInput.Builder> callback) {
        List<TextInputDefinition> textInputs = (ArrayList<TextInputDefinition>) modalDefinition.textInputs();
        var optionalTextInput = textInputs.stream()
                .filter(it -> it.parameter().name().equals(textInput))
                .findFirst();
        if (optionalTextInput.isEmpty()) {
            throw new IllegalArgumentException(
                    """
                            No text input named %s found! Please check that the referenced text input parameter exists.
                            Available text inputs for this modal are: "%s\""""
                            .formatted(textInput, modalDefinition.textInputs().stream()
                                    .map(it -> it.parameter().name())
                                    .collect(Collectors.joining("\", \""))
                            )
            );
        }
        var definition = optionalTextInput.get();
        var index = textInputs.indexOf(definition);
        var updated = definition.with(callback.apply(definition.toBuilder()));
        textInputs.set(index, updated);
        return this;
    }

    /// @param callback a [Function] that allows to modify the resulting jda object.
    ///                 The passed function will be called after all modifications are made by jda-commands,
    ///                 shortly before the component is registered in the message
    public ModalBuilder modify(Function<Modal, Modal> callback) {
        this.callback = callback;
        return this;
    }

    /// Builds the [Modal].
    public Modal build() {
        var definition = modalDefinition.with(title, components);
        return callback.apply(definition.toJDAEntity(customId));
    }

}
