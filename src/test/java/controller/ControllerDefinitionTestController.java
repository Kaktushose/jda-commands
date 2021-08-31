package controller;

import com.github.kaktushose.jda.commands.entities.CommandEvent;
import com.github.kaktushose.jda.commands.rewrite.annotations.Command;
import com.github.kaktushose.jda.commands.rewrite.annotations.CommandController;
import com.github.kaktushose.jda.commands.rewrite.annotations.Cooldown;
import com.github.kaktushose.jda.commands.rewrite.annotations.Permission;

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

    // this overloads with adopt
    @Command
    public void overloading(CommandEvent event) {

    }

    @Command(value = "super", isSuper = true)
    public void superCommand(CommandEvent event) {

    }

}
