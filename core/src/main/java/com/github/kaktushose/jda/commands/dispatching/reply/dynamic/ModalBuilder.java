package com.github.kaktushose.jda.commands.dispatching.reply.dynamic;

import com.github.kaktushose.jda.commands.definitions.interactions.CustomId;
import com.github.kaktushose.jda.commands.definitions.interactions.ModalDefinition;
import com.github.kaktushose.jda.commands.definitions.interactions.ModalDefinition.TextInputDefinition;
import com.github.kaktushose.jda.commands.dispatching.events.ReplyableEvent;
import com.github.kaktushose.jda.commands.i18n.I18n;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.interactions.components.LayoutComponent;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.modals.Modal;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static net.dv8tion.jda.api.interactions.modals.Modal.MAX_COMPONENTS;

/// Builder for [Modal]s. Acts as a bridge between [ModalDefinition] and [Modal] for dynamic modifications.
public class ModalBuilder {

    private final ReplyableEvent<?> event;
    private final CustomId customId;
    private final List<TextInputDefinition> components = new ArrayList<>(MAX_COMPONENTS);
    private final ModalDefinition modalDefinition;
    private @Nullable String title;
    private Function<Modal.Builder, Modal.Builder> callback = Function.identity();
    private final Collection<I18n.Entry> placeholder = new ArrayList<>();

    /// Constructs a new [ModalBuilder].
    ///
    /// @param customId        the [CustomId] the [Modal] should have in the end
    /// @param modalDefinition the [ModalDefinition] to modify
    public ModalBuilder(ReplyableEvent<?> event, CustomId customId, ModalDefinition modalDefinition) {
        this.event = event;
        this.customId = customId;
        this.modalDefinition = modalDefinition;
    }

    /// @see Modal.Builder#setTitle(String)
    public ModalBuilder title(String title) {
        this.title = title;
        return this;
    }

    /// @param placeholder the placeholders to be used for localization
    public ModalBuilder placeholder(I18n.Entry... placeholder) {
        this.placeholder.addAll(Arrays.asList(placeholder));
        return this;
    }


    /// Allows modification of a text input with the given callback.
    ///
    /// @param textInput the name of the method parameter the text input is assigned to
    /// @param callback the [Function] to modify the text input
    /// @see TextInput.Builder
    /// @return this instance for fluent interface
    public ModalBuilder textInput(String textInput, Function<TextInput.Builder, TextInput.Builder> callback) {
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
    ///                 The passed function will be called after all modifications except localization are made by jda-commands,
    ///                 shortly before the component is localized and then registered in the message
    public ModalBuilder modify(Function<Modal.Builder, Modal.Builder> callback) {
        this.callback = callback;
        return this;
    }

    /// Builds the [Modal].
    public Modal build() {
        var definition = modalDefinition.with(title, components);

        Modal.Builder builder = definition.toJDAEntity(customId).createCopy();
        callback.apply(builder);

        localize(builder);

        return builder.build();
    }

    private void localize(Modal.Builder builder) {
        I18n.Entry[] entries = placeholder.toArray(I18n.Entry[]::new);
        I18n i18n = event.i18n();
        Locale locale = event.getUserLocale().toLocale();

        builder.setTitle(i18n.localize(locale, builder.getTitle(), entries));
        for (LayoutComponent layout : builder.getComponents()) {
            for (ItemComponent component : layout.getComponents()) {
                if (component instanceof TextInput text) {
                    layout.updateComponent(component, copyWith(text,
                            i18n.localize(locale, text.getLabel(), entries),
                            localize(text.getPlaceHolder(), locale, entries),
                            localize(text.getValue(), locale, entries))
                    );
                }
            }
        }
    }

    private TextInput copyWith(TextInput input, String label, @Nullable String placeholder, @Nullable String value) {
        return TextInput.create(input.getId(), label, input.getStyle())
                .setPlaceholder(placeholder)
                .setMaxLength(input.getMaxLength())
                .setMinLength(input.getMinLength())
                .setRequired(input.isRequired())
                .setValue(value)
                .build();
    }

    @Nullable
    private String localize(@Nullable String val, Locale locale, I18n.Entry... entries) {
        if (val == null) return null;
        return event.i18n().localize(locale, val, entries);
    }

}
