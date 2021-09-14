package controller;

import com.github.kaktushose.jda.commands.dispatching.CommandEvent;
import com.github.kaktushose.jda.commands.annotations.Command;
import com.github.kaktushose.jda.commands.annotations.CommandController;
import com.github.kaktushose.jda.commands.annotations.Cooldown;
import com.github.kaktushose.jda.commands.annotations.Permission;

import java.util.concurrent.TimeUnit;

@CommandController(value = {"super", "superAlias"})
@Permission("superPermission")
@Cooldown(value = 10, timeUnit = TimeUnit.MILLISECONDS)
public class ControllerDefinitionTestController {

    @Command
    public void adopt(CommandEvent event) {

    }

    @Command(value = {"sub", "subAlias"})
    @Permission("subPermission")
    @Cooldown(value = 5, timeUnit = TimeUnit.DAYS)
    public void combine(CommandEvent event) {

    }

    @Command("overload")
    public void overloading(CommandEvent event) {

    }

    @Command("overload")
    public void overloadingTwo(CommandEvent event) {

    }

    @Command(value = "super", isSuper = true)
    public void superCommand(CommandEvent event) {

    }

}
