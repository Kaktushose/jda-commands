package com.github.kaktushose.jda.commands.definitions.interactions.impl;

import com.github.kaktushose.jda.commands.definitions.Definition;
import com.github.kaktushose.jda.commands.definitions.features.JDAEntity;
import com.github.kaktushose.jda.commands.definitions.features.Replyable;
import com.github.kaktushose.jda.commands.definitions.interactions.CustomIdInteraction;
import com.github.kaktushose.jda.commands.definitions.interactions.PermissionsInteraction;
import com.github.kaktushose.jda.commands.definitions.reflect.interactions.ReplyConfig;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.SequencedCollection;

public record ModalDefinition(
        String displayName,
        Method method,
        SequencedCollection<Class<?>> parameters,
        Collection<String> permissions,
        ReplyConfig replyConfig,
        String title,
        SequencedCollection<TextInputDefinition> textInputs
) implements JDAEntity<Modal>, Replyable, PermissionsInteraction, CustomIdInteraction {

    @Override
    public Modal toJDAEntity() {
        return null;
    }

    public record TextInputDefinition(
            String label,
            String placeholder,
            String defaultValue,
            int minValue,
            int maxValue,
            TextInputStyle style,
            boolean required
    ) implements Definition {
        @Override
        public String displayName() {
            return label;
        }
    }

}
