package controller;

import com.github.kaktushose.jda.commands.annotations.interactions.Interaction;
import com.github.kaktushose.jda.commands.annotations.interactions.SlashCommand;
import com.github.kaktushose.jda.commands.annotations.interactions.Cooldown;
import com.github.kaktushose.jda.commands.annotations.interactions.Permissions;
import com.github.kaktushose.jda.commands.dispatching.events.CommandEvent;

import java.util.concurrent.TimeUnit;

@Interaction(value = "super")
@Permissions("superPermission")
@Cooldown(value = 10, timeUnit = TimeUnit.MILLISECONDS)
public class ControllerDefinitionTestController {

    @SlashCommand
    public void adopt(CommandEvent event) {

    }

    @SlashCommand(value = "sub")
    @Permissions("subPermission")
    @Cooldown(value = 5, timeUnit = TimeUnit.DAYS)
    public void combine(CommandEvent event) {

    }

    @SlashCommand("overload")
    public void overloading(CommandEvent event) {

    }

    @SlashCommand("overload")
    public void overloadingTwo(CommandEvent event) {

    }

}
