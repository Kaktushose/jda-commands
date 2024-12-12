package commands;

import com.github.kaktushose.jda.commands.annotations.interactions.*;
import com.github.kaktushose.jda.commands.dispatching.events.interactions.CommandEvent;

import java.util.concurrent.TimeUnit;

@Interaction
public class CommandDefinitionTestController {

    public void noAnnotation() {
    }

    @SlashCommand("a")
    public void noArgs() {
    }

    @SlashCommand("b")
    public void noCommandEvent(int i) {
    }

    @SlashCommand("c")
    public void wrongCommandEvent(int i, CommandEvent event) {
    }

    @SlashCommand("d")
    public void commandEvent(CommandEvent event) {
    }

    @SlashCommand("e")
    public void unsupported(CommandEvent event, UnsupportedType type) {
    }

    @SlashCommand("f")
    public void arrayArgument(CommandEvent event, String[] args) {

    }

    @SlashCommand("g")
    public void argsAfterArray(CommandEvent event, String[] args, int i) {

    }

    @SlashCommand("h")
    public void argsAfterOptional(CommandEvent event, @Optional String s, int i) {

    }

    @SlashCommand("i")
    public void optionalAfterOptional(CommandEvent event, @Optional String s, @Optional int i) {

    }

    @SlashCommand("m")
    @Cooldown(value = 0, timeUnit = TimeUnit.MILLISECONDS)
    public void zeroCooldown(CommandEvent event) {

    }

    @SlashCommand("n")
    @Cooldown(value = 10, timeUnit = TimeUnit.MILLISECONDS)
    public void cooldown(CommandEvent event) {

    }

    @SlashCommand("o")
    @Permissions("permission")
    public void permission(CommandEvent event) {

    }
}
