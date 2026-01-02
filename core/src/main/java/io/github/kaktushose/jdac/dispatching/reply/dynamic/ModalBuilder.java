package io.github.kaktushose.jdac.dispatching.reply.dynamic;

import io.github.kaktushose.jdac.definitions.interactions.CustomId;
import io.github.kaktushose.jdac.definitions.interactions.ModalDefinition;
import io.github.kaktushose.jdac.dispatching.events.ReplyableEvent;
import io.github.kaktushose.jdac.message.placeholder.Entry;
import net.dv8tion.jda.api.components.ModalTopLevelComponentUnion;
import net.dv8tion.jda.api.components.label.Label;
import net.dv8tion.jda.api.components.replacer.ComponentReplacer;
import net.dv8tion.jda.api.components.textinput.TextInput;
import net.dv8tion.jda.api.modals.Modal;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;
import java.util.function.Function;

/// Builder for [Modal]s. Acts as a bridge between [ModalDefinition] and [Modal] for dynamic modifications.
public class ModalBuilder {

    private final ReplyableEvent<?> event;
    private final CustomId customId;
    private final ModalDefinition modalDefinition;
    private final Collection<Entry> placeholder = new ArrayList<>();
    private @Nullable String title;
    private Function<Modal.Builder, Modal.Builder> callback = Function.identity();

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
    public ModalBuilder placeholder(Entry... placeholder) {
        this.placeholder.addAll(Arrays.asList(placeholder));
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
        var definition = modalDefinition.with(title);

        Modal.Builder builder = definition.toJDAEntity(customId).createCopy();
        callback.apply(builder);

        resolve(builder);

        return builder.build();
    }

    private void resolve(Modal.Builder builder) {
        Entry[] entries = placeholder.toArray(Entry[]::new);
        Locale locale = event.getUserLocale().toLocale();

        builder.setTitle(resolve(builder.getTitle(), locale, entries));
        for (ModalTopLevelComponentUnion component : builder.getComponents()) {
            if (component instanceof Label label) {
                ComponentReplacer.byUniqueId(label, copyWith(label.getChild().asTextInput(),
                        resolve(label.getLabel(), locale, entries),
                        resolve(label.getChild().asTextInput().getPlaceHolder(), locale, entries),
                        resolve(label.getChild().asTextInput().getValue(), locale, entries))).apply(component);
            }
        }
    }

    private Label copyWith(TextInput input, String label, @Nullable String placeholder, @Nullable String value) {
        return Label.of(label,
                TextInput.create(input.getCustomId(), input.getStyle())
                        .setPlaceholder(placeholder)
                        .setMaxLength(input.getMaxLength())
                        .setMinLength(input.getMinLength())
                        .setRequired(input.isRequired())
                        .setValue(value)
                        .build());
    }

    @Nullable
    private String resolve(@Nullable String val, Locale locale, Entry... entries) {
        if (val == null) return null;
        return event.messageResolver().resolve(val, locale, Entry.toMap(entries));
    }

}
