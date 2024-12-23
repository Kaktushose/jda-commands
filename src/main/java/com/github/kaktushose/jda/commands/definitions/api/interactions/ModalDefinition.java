package com.github.kaktushose.jda.commands.definitions.api.interactions;

import com.github.kaktushose.jda.commands.definitions.api.Definition;
import com.github.kaktushose.jda.commands.definitions.api.features.JDAEntity;
import com.github.kaktushose.jda.commands.definitions.api.features.Replyable;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;

import java.util.List;

public non-sealed interface ModalDefinition extends JDAEntity<Modal>, Replyable, PermissionsInteraction, CustomIdInteraction {

    String title();

    List<TextInputDefinition> textInputs();

    interface TextInputDefinition extends Definition {
        String label();

        String placeholder();

        String defaultValue();

        int minValue();

        int maxValue();

        TextInputStyle style();

        boolean required();
    }

}
