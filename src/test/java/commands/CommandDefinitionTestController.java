package commands;

import com.github.kaktushose.jda.commands.dispatching.CommandEvent;
import com.github.kaktushose.jda.commands.annotations.*;

import java.util.concurrent.TimeUnit;

@CommandController
public class CommandDefinitionTestController {

    public void noAnnotation() {
    }

    @Command
    public void noArgs() {
    }

    @Command
    public void noCommandEvent(int i) {
    }

    @Command
    public void wrongCommandEvent(int i, CommandEvent event) {
    }

    @Command
    public void commandEvent(CommandEvent event) {
    }

    @Command
    public void unsupported(CommandEvent event, UnsupportedType type) {
    }

    @Command
    public void arrayArgument(CommandEvent event, String[] args) {

    }

    @Command
    public void argsAfterArray(CommandEvent event, String[] args, int i) {

    }

    @Command
    public void argsAfterOptional(CommandEvent event, @Optional String s, int i) {

    }

    @Command
    public void optionalAfterOptional(CommandEvent event, @Optional String s, @Optional int i) {

    }

    @Command
    public void argsAfterConcat(CommandEvent event, @Concat String s, int i) {

    }

    @Command(isActive = false)
    public void inactive() {

    }

    @Command(isSuper = true, isDM = false)
    public void superAndDM(CommandEvent event) {

    }

    @Command(value = {"sub", "subAlias"})
    public void label(CommandEvent event) {

    }

    @Command
    @Cooldown(value = 0, timeUnit = TimeUnit.MILLISECONDS)
    public void zeroCooldown(CommandEvent event) {

    }

    @Command
    @Cooldown(value = 10, timeUnit = TimeUnit.MILLISECONDS)
    public void cooldown(CommandEvent event) {

    }

    @Command
    @Permission("permission")
    public void permission(CommandEvent event) {

    }
}
