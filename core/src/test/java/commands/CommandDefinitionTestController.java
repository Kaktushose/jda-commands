package commands;

import com.github.kaktushose.jda.commands.annotations.interactions.*;
import com.github.kaktushose.jda.commands.dispatching.events.interactions.CommandEvent;

import java.util.concurrent.TimeUnit;

@Interaction
public class CommandDefinitionTestController {

    public void noAnnotation() {
    }

    @Command("a")
    public void noArgs() {
    }

    @Command("b")
    public void noCommandEvent(int i) {
    }

    @Command("c")
    public void wrongCommandEvent(int i, CommandEvent event) {
    }

    @Command("d")
    public void commandEvent(CommandEvent event) {
    }

    @Command("e")
    public void unsupported(CommandEvent event, UnsupportedType type) {
    }

    @Command("f")
    public void arrayArgument(CommandEvent event, String[] args) {

    }

    @Command("g")
    public void argsAfterArray(CommandEvent event, String[] args, int i) {

    }

    @Command("h")
    public void argsAfterOptional(CommandEvent event, @Optional String s, int i) {

    }

    @Command("i")
    public void optionalAfterOptional(CommandEvent event, @Optional String s, @Optional int i) {

    }

    @Command("m")
    @Cooldown(value = 0, timeUnit = TimeUnit.MILLISECONDS)
    public void zeroCooldown(CommandEvent event) {

    }

    @Command("n")
    @Cooldown(value = 10, timeUnit = TimeUnit.MILLISECONDS)
    public void cooldown(CommandEvent event) {

    }

    @Command("o")
    @Permissions("permission")
    public void permission(CommandEvent event) {

    }
}
