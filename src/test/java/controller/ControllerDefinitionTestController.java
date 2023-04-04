package controller;

import com.github.kaktushose.jda.commands.annotations.interactions.SlashCommand;
import com.github.kaktushose.jda.commands.annotations.CommandController;
import com.github.kaktushose.jda.commands.annotations.Cooldown;
import com.github.kaktushose.jda.commands.annotations.Permission;
import com.github.kaktushose.jda.commands.dispatching.commands.CommandEvent;

import java.util.concurrent.TimeUnit;

@CommandController(value = {"super", "superAlias"})
@Permission("superPermission")
@Cooldown(value = 10, timeUnit = TimeUnit.MILLISECONDS)
public class ControllerDefinitionTestController {

    @SlashCommand
    public void adopt(CommandEvent event) {

    }

    @SlashCommand(value = {"sub", "subAlias"})
    @Permission("subPermission")
    @Cooldown(value = 5, timeUnit = TimeUnit.DAYS)
    public void combine(CommandEvent event) {

    }

    @SlashCommand("overload")
    public void overloading(CommandEvent event) {

    }

    @SlashCommand("overload")
    public void overloadingTwo(CommandEvent event) {

    }

    @SlashCommand(value = "super", isSuper = true)
    public void superCommand(CommandEvent event) {

    }

}
