package com.github.kaktushose.jda.commands.definitions.api.interactions;

import com.github.kaktushose.jda.commands.definitions.api.Definition;
import com.github.kaktushose.jda.commands.definitions.api.features.JDAEntity;
import com.github.kaktushose.jda.commands.dispatching.validation.Validator;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.Collection;
import java.util.SequencedCollection;
import java.util.concurrent.TimeUnit;

public interface SlashCommandDefinition extends CommandDefinition {

    String description();

    SequencedCollection<ParameterDefinition> commandParameters();

    CooldownDefinition cooldown();

    boolean isAutoComplete();

    non-sealed interface ParameterDefinition extends Definition, JDAEntity<OptionData> {
        Class<?> type();

        boolean optional();

        String defaultValue();

        boolean primitive();

        String name();

        String description();

        SequencedCollection<Command.Choice> choices();

        Collection<ConstraintDefinition> constraints();

        interface ConstraintDefinition extends Definition {
            Validator validator();

            String message();

            Object annotation();
        }
    }

    interface CooldownDefinition extends Definition {
        long delay();

        TimeUnit timeUnit();
    }

}
